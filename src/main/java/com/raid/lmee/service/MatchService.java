package com.raid.lmee.service;

import com.raid.lmee.domain.*;
import com.raid.lmee.domain.aggregate.MatchAggregate;
import com.raid.lmee.domain.event.MatchEvent;
import com.raid.lmee.exception.ClubNotFoundException;
import com.raid.lmee.exception.MatchNotFoundException;
import com.raid.lmee.exception.PlayerNotFoundException;
import com.raid.lmee.exception.PlayerNotInClubException;
import com.raid.lmee.model.MatchEventType;
import com.raid.lmee.model.MatchResponse;
import com.raid.lmee.model.command.MatchCommand;
import com.raid.lmee.projection.MatchProjection;
import com.raid.lmee.repos.ClubMatchRepository;
import com.raid.lmee.repos.ClubRepository;
import com.raid.lmee.repos.MatchEventStoreRepository;
import com.raid.lmee.repos.MatchRepository;
import com.raid.lmee.repos.PlayerRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchService {

    private final MatchEventStoreRepository matchEventStoreRepository;

    private final MatchRepository matchRepository;

    private final ClubRepository clubRepository;

    private final ClubMatchRepository clubMatchRepository;

    private final PlayerRepository playerRepository;

    private final MatchProjection matchProjection;

    private final ObjectMapper objectMapper;

    private final EntityManager entityManager;

    public List<MatchResponse> findAll() {
        return matchRepository.findAllWithClubsAndState()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MatchResponse mapToResponse(Match match) {
        MatchState matchState = match.getMatchState();
        return new MatchResponse(
                match.getId(),
                match.getScheduledKickoff(),
                matchState == null ? null : matchState.getStatus(),
                clubName(match, true),
                clubName(match, false)
        );
    }

    private String clubName(Match match, boolean isHome) {
        return match.getMatchClubs().stream()
                .filter(clubMatch -> Boolean.valueOf(isHome).equals(clubMatch.getIsHome()))
                .map(clubMatch -> clubMatch.getClub().getName())
                .findFirst()
                .orElse(null);
    }

    public UUID scheduleMatch(
            UUID homeClubId,
            UUID awayClubId,
            OffsetDateTime scheduledKickoff,
            String venue
    ) {
        Match registeredMatch = registerMatch(homeClubId, awayClubId, scheduledKickoff, venue);
        UUID registeredMatchId = registeredMatch.getId();

        MatchAggregate aggregate = MatchAggregate.scheduleMatch(
                registeredMatchId,
                homeClubId,
                awayClubId,
                scheduledKickoff,
                venue
        );


        dispatchEvents(aggregate);

        return aggregate.getMatchId();
    }

    public void handle(UUID matchId, @Valid MatchCommand command) {
        if (!matchRepository.existsById(matchId)) {
            throw new MatchNotFoundException(matchId);
        }
        requireReferencedRowsExist(command);

        List<MatchEvent> history = loadHistoryFor(matchId);
        MatchAggregate aggregate = MatchAggregate.reconstitute(history);

        switch (command) {
            case MatchCommand.StartMatch _ -> aggregate.startMatch();
            case MatchCommand.EndFirstHalf _ -> aggregate.endFirstHalf();
            case MatchCommand.StartSecondHalf _ -> aggregate.startSecondHalf();
            case MatchCommand.BlowFullTime _ -> aggregate.endMatch();
            case MatchCommand.AbandonMatch c -> aggregate.abandonMatch(c.reason(), c.minute());
            case MatchCommand.PostponeMatch c -> aggregate.postponeMatch(c.reason());
            case MatchCommand.ScoreGoal c -> aggregate.scoreGoal(c.clubId(), c.playerId(), c.minute());
            case MatchCommand.ScoreOwnGoal c -> aggregate.scoreOwnGoal(c.clubId(), c.playerId(), c.minute());
            case MatchCommand.CancelGoal c -> aggregate.cancelGoal(c.clubId(), c.minute());
            case MatchCommand.AwardPenalty c -> aggregate.awardPenalty(c.clubId(), c.minute());
            case MatchCommand.ScorePenalty c -> aggregate.scorePenalty(c.clubId(), c.playerId(), c.minute());
            case MatchCommand.MissPenalty c -> aggregate.missPenalty(c.clubId(), c.playerId(), c.minute());
            case MatchCommand.GiveYellowCard c -> aggregate.bookPlayer(c.clubId(), c.playerId(), c.minute());
            case MatchCommand.GiveRedCard c -> aggregate.sendOffPlayer(c.clubId(), c.playerId(), c.minute());
            case MatchCommand.Substitute c ->
                    aggregate.substitute(c.clubId(), c.playerOutId(), c.playerInId(), c.minute());
            case MatchCommand.StartVarCheck c -> aggregate.startVarCheck(c.reason(), c.minute());
            case MatchCommand.RecordVarDecision c -> aggregate.recordVarDecision(c.decision(), c.minute());
            case MatchCommand.AnnounceAddedTime c -> aggregate.announceAddedTime(c.addedMinutes());
        }

        dispatchEvents(aggregate);
    }

    private void requireReferencedRowsExist(MatchCommand command) {
        switch (command) {
            case MatchCommand.ScoreGoal c -> requirePlaysFor(c.clubId(), c.playerId());
            case MatchCommand.ScoreOwnGoal c -> requirePlaysFor(c.clubId(), c.playerId());
            case MatchCommand.ScorePenalty c -> requirePlaysFor(c.clubId(), c.playerId());
            case MatchCommand.MissPenalty c -> requirePlaysFor(c.clubId(), c.playerId());
            case MatchCommand.GiveYellowCard c -> requirePlaysFor(c.clubId(), c.playerId());
            case MatchCommand.GiveRedCard c -> requirePlaysFor(c.clubId(), c.playerId());
            case MatchCommand.Substitute c -> requirePlaysFor(c.clubId(), c.playerOutId(), c.playerInId());
            case MatchCommand.CancelGoal c -> requireClubExists(c.clubId());
            case MatchCommand.AwardPenalty c -> requireClubExists(c.clubId());
            default -> {
                // the remaining commands name neither a club nor a player
            }
        }
    }

    private void requirePlaysFor(UUID clubId, UUID... playerIds) {
        requireClubExists(clubId);

        for (UUID playerId : playerIds) {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new PlayerNotFoundException(playerId));

            if (!player.getClub().getId().equals(clubId)) {
                throw new PlayerNotInClubException(playerId, clubId);
            }
        }
    }

    private void requireClubExists(UUID clubId) {
        if (!clubRepository.existsById(clubId)) {
            throw new ClubNotFoundException(clubId);
        }
    }

    private List<MatchEvent> loadHistoryFor(UUID matchId) {
        Sort sort = Sort.by("occurredAt").ascending();
        List<MatchEventStore> events = matchEventStoreRepository.findByMatchId(matchId, sort);

        return events.stream()
                .map(matchEvent -> deserialize(matchEvent.getEventType(), matchEvent.getPayload()))
                .toList();
    }

    private MatchEvent deserialize(MatchEventType eventType, String payload) {
        return switch (eventType) {
            case MATCH_SCHEDULED -> objectMapper.readValue(payload, MatchEvent.MatchScheduled.class);
            case MATCH_STARTED -> objectMapper.readValue(payload, MatchEvent.MatchStarted.class);
            case FIRST_HALF_ENDED -> objectMapper.readValue(payload, MatchEvent.FirstHalfEnded.class);
            case SECOND_HALF_STARTED -> objectMapper.readValue(payload, MatchEvent.SecondHalfStarted.class);
            case FULL_TIME -> objectMapper.readValue(payload, MatchEvent.FullTime.class);
            case MATCH_ABANDONED -> objectMapper.readValue(payload, MatchEvent.MatchAbandoned.class);
            case MATCH_POSTPONED -> objectMapper.readValue(payload, MatchEvent.MatchPostponed.class);
            case GOAL_SCORED -> objectMapper.readValue(payload, MatchEvent.GoalScored.class);
            case OWN_GOAL -> objectMapper.readValue(payload, MatchEvent.OwnGoal.class);
            case GOAL_CANCELED -> objectMapper.readValue(payload, MatchEvent.GoalCanceled.class);
            case YELLOW_CARD_GIVEN -> objectMapper.readValue(payload, MatchEvent.YellowCardGiven.class);
            case RED_CARD_GIVEN -> objectMapper.readValue(payload, MatchEvent.RedCardGiven.class);
            case SECOND_YELLOW_CARD -> objectMapper.readValue(payload, MatchEvent.SecondYellowCard.class);
            case SUBSTITUTION -> objectMapper.readValue(payload, MatchEvent.Substitution.class);
            case PENALTY_AWARDED -> objectMapper.readValue(payload, MatchEvent.PenaltyAwarded.class);
            case PENALTY_SCORED -> objectMapper.readValue(payload, MatchEvent.PenaltyScored.class);
            case PENALTY_MISSED -> objectMapper.readValue(payload, MatchEvent.PenaltyMissed.class);
            case VAR_CHECK_STARTED -> objectMapper.readValue(payload, MatchEvent.VarCheckStarted.class);
            case VAR_DECISION -> objectMapper.readValue(payload, MatchEvent.VarDecision.class);
            case ADDED_TIME_ANNOUNCED -> objectMapper.readValue(payload, MatchEvent.AddedTimeAnnounced.class);
        };
    }

    private Match registerMatch(
            UUID homeClubId,
            UUID awayClubId,
            OffsetDateTime scheduledKickoff,
            String venue
    ) {
        Match match = new Match();
        match.setScheduledKickoff(scheduledKickoff);
        match.setVenue(venue);
        Match savedMatch = matchRepository.save(match);

        clubMatchRepository.save(buildClubMatch(savedMatch, homeClubId, true));
        clubMatchRepository.save(buildClubMatch(savedMatch, awayClubId, false));

        return savedMatch;
    }

    private ClubMatch buildClubMatch(Match match, UUID clubId, boolean isHome) {
        ClubMatch clubMatch = new ClubMatch();
        clubMatch.setId(new ClubMatchId(match.getId(), clubId));
        clubMatch.setMatch(match);
        clubMatch.setClub(entityManager.getReference(Club.class, clubId));
        clubMatch.setIsHome(isHome);
        return clubMatch;
    }

    private void dispatchEvents(MatchAggregate aggregate) {
        List<MatchEvent> events = aggregate.getUncommittedEvents();
        appendToEventStore(events, aggregate.getMatchId());
        events.forEach(this::routeToProjections);
        aggregate.clearUncommittedEvents();
    }

    private void appendToEventStore(List<MatchEvent> events, UUID matchId) {
        Match match = entityManager.getReference(Match.class, matchId);
        AtomicInteger baseSequenceNumber = new AtomicInteger(matchEventStoreRepository.countByMatchId(matchId));
        events.forEach(event -> {
            String payload = objectMapper.writeValueAsString(event);
            MatchEventType eventType = event.eventType();
            int sequenceNumber = baseSequenceNumber.incrementAndGet();
            OffsetDateTime occurredAt = event.occurredAt();

            MatchEventStore newEvent = new MatchEventStore();
            newEvent.setMatch(match);
            newEvent.setPayload(payload);
            newEvent.setSequenceNumber(sequenceNumber);
            newEvent.setEventType(eventType);
            newEvent.setOccurredAt(occurredAt);

            matchEventStoreRepository.save(newEvent);
        });

    }

    private void routeToProjections(MatchEvent event) {
        switch (event) {
            case MatchEvent.MatchScheduled e -> matchProjection.on(e);
            case MatchEvent.MatchStarted e -> matchProjection.on(e);
            case MatchEvent.FirstHalfEnded e -> matchProjection.on(e);
            case MatchEvent.SecondHalfStarted e -> matchProjection.on(e);
            case MatchEvent.FullTime e -> matchProjection.on(e);
            case MatchEvent.MatchAbandoned e -> matchProjection.on(e);
            case MatchEvent.MatchPostponed e -> matchProjection.on(e);
            case MatchEvent.GoalScored e -> matchProjection.on(e);
            case MatchEvent.OwnGoal e -> matchProjection.on(e);
            case MatchEvent.GoalCanceled e -> matchProjection.on(e);
            case MatchEvent.PenaltyAwarded e -> matchProjection.on(e);
            case MatchEvent.PenaltyScored e -> matchProjection.on(e);
            case MatchEvent.PenaltyMissed e -> matchProjection.on(e);
            case MatchEvent.YellowCardGiven e -> matchProjection.on(e);
            case MatchEvent.SecondYellowCard e -> matchProjection.on(e);
            case MatchEvent.RedCardGiven e -> matchProjection.on(e);
            case MatchEvent.Substitution e -> matchProjection.on(e);
            case MatchEvent.VarCheckStarted e -> matchProjection.on(e);
            case MatchEvent.VarDecision e -> matchProjection.on(e);
            case MatchEvent.AddedTimeAnnounced e -> matchProjection.on(e);
        }
    }


}
