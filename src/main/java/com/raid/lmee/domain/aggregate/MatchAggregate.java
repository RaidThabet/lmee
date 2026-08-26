package com.raid.lmee.domain.aggregate;

import com.raid.lmee.domain.event.MatchEvent;
import com.raid.lmee.model.MatchStatus;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.*;

@Data
public class MatchAggregate {

    private UUID matchId;

    private UUID homeClubId;

    private UUID awayClubId;

    private MatchStatus status;

    private Integer half;

    private Integer homeScore;

    private Integer awayScore;

    private TeamTally homeTally;

    private TeamTally awayTally;

    private final Map<UUID, Integer> yellowsByPlayer = new HashMap<>();

    private final Set<UUID> sentOffPlayers = new HashSet<>();

    private final Set<UUID> subbedOutPlayers = new HashSet<>();

    private final Set<UUID> subbedInPlayers = new HashSet<>();

    private UUID pendingPenaltyClubId;

    private final List<MatchEvent> uncommittedEvents = new ArrayList<>();

    // ### Factories ###

    public static MatchAggregate reconstitute(List<MatchEvent> history) {
        var matchAggregate = new MatchAggregate();
        history.forEach(matchAggregate::apply);

        return matchAggregate;
    }

    public static MatchAggregate scheduleMatch(
            UUID matchId,
            UUID homeClubId,
            UUID awayClubId,
            OffsetDateTime scheduledKickoff,
            String venue
    ) {
        MatchEvent event = new MatchEvent.MatchScheduled(
                matchId,
                OffsetDateTime.now(),
                homeClubId,
                awayClubId,
                scheduledKickoff,
                venue
        );
        MatchAggregate aggregate = new MatchAggregate();
        aggregate.apply(event);
        aggregate.uncommittedEvents.add(event);

        return aggregate;
    }

    // ### Match lifecycle ###

    public void startMatch() {
        if (status != MatchStatus.SCHEDULED || half != null) {
            throw new IllegalStateException("Match must be in scheduled state");
        }
        MatchEvent event = new MatchEvent.MatchStarted(matchId, OffsetDateTime.now());
        apply(event);
        uncommittedEvents.add(event);
    }

    public void endFirstHalf() {
        if (status != MatchStatus.IN_PROGRESS || half != 1) {
            throw new IllegalStateException("match is not in progress or in first half");
        }
        MatchEvent event = new MatchEvent.FirstHalfEnded(matchId, OffsetDateTime.now());
        apply(event);
        uncommittedEvents.add(event);
    }

    public void startSecondHalf() {
        if (status != MatchStatus.HALF_TIME) {
            throw new IllegalStateException("match is not in half time");
        }
        MatchEvent event = new MatchEvent.SecondHalfStarted(matchId, OffsetDateTime.now());
        apply(event);
        uncommittedEvents.add(event);
    }

    public void endMatch() {
        if (status != MatchStatus.IN_PROGRESS || half != 2) {
            throw new IllegalStateException("match is not in progress or in second half");
        }
        MatchEvent event = new MatchEvent.FullTime(matchId, OffsetDateTime.now());
        apply(event);
        uncommittedEvents.add(event);
    }

    public void abandonMatch(String reason, int minute) {
        if (status != MatchStatus.IN_PROGRESS) {
            throw new IllegalStateException("match is not in progress");
        }
        MatchEvent event = new MatchEvent.MatchAbandoned(matchId, reason, minute, OffsetDateTime.now());
        apply(event);
        uncommittedEvents.add(event);
    }

    public void postponeMatch(String reason) {
        if (status != MatchStatus.SCHEDULED) {
            throw new IllegalStateException("match must be scheduled");
        }
        MatchEvent event = new MatchEvent.MatchPostponed(matchId, reason, OffsetDateTime.now());
        apply(event);
        uncommittedEvents.add(event);
    }

    // ### Scoring decide methods ###

    public void scoreGoal(
            UUID clubId,
            UUID scoringPlayerId,
            int minute
    ) {
        requireInProgress();
        requirePlayerOnThePitch(scoringPlayerId);

        MatchEvent event = new MatchEvent.GoalScored(matchId, OffsetDateTime.now(), clubId, scoringPlayerId, minute);
        apply(event);
        uncommittedEvents.add(event);
    }

    /**
     * {@code clubId} is the club the scoring player belongs to, so the goal is
     * credited to the opponent.
     */
    public void scoreOwnGoal(
            UUID clubId,
            UUID playerId,
            int minute
    ) {
        requireInProgress();
        requireParticipant(clubId);
        requirePlayerOnThePitch(playerId);

        MatchEvent event = new MatchEvent.OwnGoal(matchId, OffsetDateTime.now(), clubId, playerId, minute);
        apply(event);
        uncommittedEvents.add(event);
    }

    /**
     * Cancels a previously credited goal.
     * <p>
     * {@code clubId} is the club the cancelled goal was credited to, which for an own goal is the
     * conceding club's opponent. It has to be supplied by the caller: {@code correctsEventId} points at
     * a {@code match_event_store} row, and the aggregate only ever sees deserialized payloads, which
     * carry no event id.
     */
    public void cancelGoal(
            UUID clubId,
            int minute
    ) {
        requireInProgress();
        requireParticipant(clubId);
        if (scoreOf(clubId) == 0) {
            throw new IllegalStateException("club has no goal to cancel");
        }

        MatchEvent event = new MatchEvent.GoalCanceled(matchId, OffsetDateTime.now(), clubId, minute);
        apply(event);
        uncommittedEvents.add(event);
    }

    public void awardPenalty(UUID clubId, int minute) {
        requireInProgress();
        requireParticipant(clubId);
        if (pendingPenaltyClubId != null) {
            throw new IllegalStateException("a penalty is already awaiting its outcome");
        }

        MatchEvent event = new MatchEvent.PenaltyAwarded(matchId, OffsetDateTime.now(), clubId, minute);
        apply(event);
        uncommittedEvents.add(event);
    }

    public void scorePenalty(
            UUID clubId,
            UUID playerId,
            int minute
    ) {
        requireInProgress();
        requirePendingPenaltyFor(clubId);
        requirePlayerOnThePitch(playerId);

        MatchEvent event = new MatchEvent.PenaltyScored(matchId, OffsetDateTime.now(), clubId, playerId, minute);
        apply(event);
        uncommittedEvents.add(event);
    }

    public void missPenalty(
            UUID clubId,
            UUID playerId,
            int minute
    ) {
        requireInProgress();
        requirePendingPenaltyFor(clubId);
        requirePlayerOnThePitch(playerId);

        MatchEvent event = new MatchEvent.PenaltyMissed(matchId, OffsetDateTime.now(), clubId, playerId, minute);
        apply(event);
        uncommittedEvents.add(event);
    }

    // ### Discipline decide methods ###

    public void bookPlayer(UUID clubId, UUID playerId, int minute) {
        requireInProgress();
        requireParticipant(clubId);
        requirePlayerNotSentOff(playerId);

        if (yellows(playerId) == 0) {
            MatchEvent event = new MatchEvent.YellowCardGiven(matchId, OffsetDateTime.now(), clubId, playerId, minute);
            apply(event);
            uncommittedEvents.add(event);
        }
        else if (yellows(playerId) == 1) {
            MatchEvent secondYellowEvent = new MatchEvent.SecondYellowCard(matchId, OffsetDateTime.now(), clubId, playerId, minute);
            MatchEvent redCardEvent = new MatchEvent.RedCardGiven(matchId, OffsetDateTime.now(), clubId, playerId, minute);
            apply(secondYellowEvent);
            apply(redCardEvent);
            uncommittedEvents.addAll(List.of(secondYellowEvent, redCardEvent));
        } else {
            throw new IllegalStateException("incorrect player yellows count");
        }

    }

    private int yellows(UUID playerId) {
        return this.yellowsByPlayer.getOrDefault(playerId, 0);
    }

    private void requirePlayerNotSentOff(UUID playerId) {
        if (sentOffPlayers.contains(playerId)) {
            throw new IllegalStateException("player is sent off");
        }
    }

    public void sendOffPlayer(UUID clubId, UUID playerId, int minute) {
        requireInProgress();
        requireParticipant(clubId);
        requirePlayerNotSentOff(playerId);

        MatchEvent event = new MatchEvent.RedCardGiven(matchId, OffsetDateTime.now(), clubId, playerId, minute);
        apply(event);
        uncommittedEvents.add(event);
    }

    public void substitute(UUID clubId, UUID playerOutId, UUID playerInId, int minute) {
        requireInProgress();
        requireParticipant(clubId);
        requireClubDidNotFinishSubs(clubId);
        requirePlayerNotSentOff(playerOutId);
        requirePlayerNotSentOff(playerInId);
        requirePlayerOnThePitch(playerOutId);
        requirePlayerNotOnThePitch(playerInId);

        MatchEvent event = new MatchEvent.Substitution(matchId, OffsetDateTime.now(), clubId, playerOutId, playerInId, minute);
        apply(event);
        uncommittedEvents.add(event);
    }

    private void requireClubDidNotFinishSubs(UUID clubId) {
        if (isHome(clubId)) {
            if (homeTally.getSubstitutions() == 5) {
                throw new IllegalStateException("club already reached maximum subs");
            }
        } else {
            if (awayTally.getSubstitutions() == 5) {
                throw new IllegalStateException("club already reached maximum subs");
            }
        }
    }

    private void requirePlayerNotOnThePitch(UUID playerInId) {
        if (sentOffPlayers.contains(playerInId)) {
            throw new IllegalStateException("player has been sent off");
        }
        if (subbedOutPlayers.contains(playerInId)) {
            throw new IllegalStateException("player has been substituted off and cannot return");
        }
        if (subbedInPlayers.contains(playerInId)) {
            throw new IllegalStateException("player is already on the pitch");
        }
    }

    private void requirePlayerOnThePitch(UUID playerOutId) {
        if (sentOffPlayers.contains(playerOutId)) {
            throw new IllegalStateException("player has been sent off");
        }
        if (subbedOutPlayers.contains(playerOutId)) {
            throw new IllegalStateException("player has already been substituted off");
        }
    }

    // ### Guards ###

    private void requireInProgress() {
        if (status != MatchStatus.IN_PROGRESS) {
            throw new IllegalStateException("match is not in progress");
        }
    }

    private void requireParticipant(UUID clubId) {
        if (!homeClubId.equals(clubId) && !awayClubId.equals(clubId)) {
            throw new IllegalArgumentException("club does not take part in this match");
        }
    }

    private void requirePendingPenaltyFor(UUID clubId) {
        requireParticipant(clubId);
        if (!clubId.equals(pendingPenaltyClubId)) {
            throw new IllegalStateException("no penalty is awarded to this club");
        }
    }

    // ### Score helpers ###

    private boolean isHome(UUID clubId) {
        return homeClubId.equals(clubId);
    }

    private UUID opponentOf(UUID clubId) {
        return isHome(clubId) ? awayClubId : homeClubId;
    }

    private int scoreOf(UUID clubId) {
        return isHome(clubId) ? homeScore : awayScore;
    }

    private void creditGoalTo(UUID clubId) {
        if (isHome(clubId)) {
            homeScore++;
        } else {
            awayScore++;
        }
    }

    /**
     * Deliberately unclamped: {@code cancelGoal} is the only guard against a negative score, so a
     * stream that replays into one is corrupt and should show it rather than be silently floored.
     */
    private void revokeGoalFrom(UUID clubId) {
        if (isHome(clubId)) {
            homeScore--;
        } else {
            awayScore--;
        }
    }

    public void clearUncommittedEvents() {
        this.uncommittedEvents.clear();
    }

    private void apply(MatchEvent event) {
        switch (event) {
            case MatchEvent.MatchScheduled e -> {
                this.matchId = e.matchId();
                this.homeClubId = e.homeClubId();
                this.awayClubId = e.awayClubId();
                this.homeScore = null;
                this.awayScore = null;
                this.status = MatchStatus.SCHEDULED;
                this.homeTally = null;
                this.awayTally = null;
                this.half = null;
                this.pendingPenaltyClubId = null;
            }
            case MatchEvent.MatchStarted _ -> {
                this.status = MatchStatus.IN_PROGRESS;
                this.homeScore = 0;
                this.awayScore = 0;
                this.homeTally = new TeamTally();
                this.awayTally = new TeamTally();
                this.half = 1;
                this.pendingPenaltyClubId = null;
            }
            case MatchEvent.FirstHalfEnded _ -> {
                this.status = MatchStatus.HALF_TIME;
            }
            case MatchEvent.SecondHalfStarted _ -> {
                this.status = MatchStatus.IN_PROGRESS;
                this.half = 2;
            }
            case MatchEvent.FullTime _ -> {
                this.status = MatchStatus.COMPLETED;
                this.half = null;
            }
            case MatchEvent.MatchAbandoned _ -> {
                this.status = MatchStatus.ABANDONED;
            }
            case MatchEvent.MatchPostponed _ -> {
                this.status = MatchStatus.POSTPONED;
            }
            case MatchEvent.GoalScored e -> creditGoalTo(e.clubId());
            case MatchEvent.OwnGoal e -> creditGoalTo(opponentOf(e.clubId()));
            case MatchEvent.GoalCanceled e -> revokeGoalFrom(e.clubId());
            case MatchEvent.PenaltyAwarded e -> this.pendingPenaltyClubId = e.clubId();
            case MatchEvent.PenaltyScored e -> {
                creditGoalTo(e.clubId());
                this.pendingPenaltyClubId = null;
            }
            case MatchEvent.PenaltyMissed _ -> this.pendingPenaltyClubId = null;
            case MatchEvent.YellowCardGiven e -> {
                yellowsByPlayer.put(e.playerId(), 1);
                if (isHome(e.clubId())) {
                    this.homeTally.addYellowCard();
                } else {
                    this.awayTally.addYellowCard();
                }
            }
            case MatchEvent.SecondYellowCard e -> {
                yellowsByPlayer.put(e.playerId(), 2);
                if (isHome(e.clubId())) {
                    this.homeTally.addYellowCard();
                } else {
                    this.awayTally.addYellowCard();
                }
            }
            case MatchEvent.RedCardGiven e -> {
                sentOffPlayers.add(e.playerId());
                if (isHome(e.clubId())) {
                    this.homeTally.addRedCard();
                } else {
                    this.awayTally.addRedCard();
                }
            }
            case MatchEvent.Substitution e -> {
                subbedInPlayers.add(e.playerInId());
                subbedOutPlayers.add(e.playerOutId());
                if (isHome(e.clubId())) {
                    this.homeTally.addSubstitution();
                } else {
                    this.awayTally.addSubstitution();
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

    public List<MatchEvent> getUncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }
}

