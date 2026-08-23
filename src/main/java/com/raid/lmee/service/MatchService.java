package com.raid.lmee.service;

import com.raid.lmee.domain.*;
import com.raid.lmee.domain.aggregate.MatchAggregate;
import com.raid.lmee.domain.event.MatchEvent;
import com.raid.lmee.model.MatchEventType;
import com.raid.lmee.model.MatchResponse;
import com.raid.lmee.projection.MatchProjection;
import com.raid.lmee.repos.ClubMatchRepository;
import com.raid.lmee.repos.ClubRepository;
import com.raid.lmee.repos.MatchEventStoreRepository;
import com.raid.lmee.repos.MatchRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
        System.out.println("###### CLUB ID PASSED: " + clubId + " ######");
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
            default -> {}
        }
    }
}
