package com.raid.lmee.domain.aggregate;

import com.raid.lmee.domain.event.MatchEvent;
import com.raid.lmee.model.MatchStatus;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private final List<MatchEvent> uncommittedEvents = new ArrayList<>();

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
            }
            case MatchEvent.MatchStarted _ -> {
                this.status = MatchStatus.IN_PROGRESS;
                this.homeScore = 0;
                this.awayScore = 0;
                this.homeTally = new TeamTally();
                this.awayTally = new TeamTally();
                this.half = 1;
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
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

    public List<MatchEvent> getUncommittedEvents() {
        return List.copyOf(uncommittedEvents);
    }
}

