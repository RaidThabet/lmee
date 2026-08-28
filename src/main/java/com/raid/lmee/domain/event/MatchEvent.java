package com.raid.lmee.domain.event;

import com.raid.lmee.model.MatchEventType;

import java.time.OffsetDateTime;
import java.util.UUID;

public sealed interface MatchEvent {
    UUID matchId();
    OffsetDateTime occurredAt();
    MatchEventType eventType();


    record MatchScheduled(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID homeClubId,
            UUID awayClubId,
            OffsetDateTime kickoff,
            String venue
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.MATCH_SCHEDULED;
        }
    }

    record MatchStarted(
            UUID matchId,
            OffsetDateTime occurredAt
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.MATCH_STARTED;
        }
    }

    record FirstHalfEnded(
            UUID matchId,
            OffsetDateTime occurredAt
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.FIRST_HALF_ENDED;
        }
    }

    record SecondHalfStarted(
            UUID matchId,
            OffsetDateTime occurredAt
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.SECOND_HALF_STARTED;
        }
    }

    record FullTime(
            UUID matchId,
            OffsetDateTime occurredAt
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.FULL_TIME;
        }
    }

    record MatchAbandoned(
            UUID matchId,
            String reason,
            int minute,
            OffsetDateTime occurredAt
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.MATCH_ABANDONED;
        }
    }

    record MatchPostponed(
            UUID matchId,
            String reason,
            OffsetDateTime occurredAt
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.MATCH_POSTPONED;
        }
    }

    record GoalScored(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            UUID playerId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.GOAL_SCORED;
        }
    }

    record OwnGoal(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            UUID playerId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.OWN_GOAL;
        }
    }

    record GoalCanceled(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.GOAL_CANCELED;
        }
    }

    record YellowCardGiven(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            UUID playerId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.YELLOW_CARD_GIVEN;
        }
    }

    record RedCardGiven(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            UUID playerId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.RED_CARD_GIVEN;
        }
    }

    record SecondYellowCard(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            UUID playerId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.SECOND_YELLOW_CARD;
        }
    }

    record Substitution(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            UUID playerOutId,
            UUID playerInId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.SUBSTITUTION;
        }
    }

    record PenaltyAwarded(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.PENALTY_AWARDED;
        }
    }

    record PenaltyScored(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            UUID playerId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.PENALTY_SCORED;
        }
    }

    record PenaltyMissed(
            UUID matchId,
            OffsetDateTime occurredAt,
            UUID clubId,
            UUID playerId,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.PENALTY_MISSED;
        }
    }

    record VarCheckStarted(
            UUID matchId,
            OffsetDateTime occurredAt,
            String reason,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.VAR_CHECK_STARTED;
        }
    }

    record VarDecision(
            UUID matchId,
            OffsetDateTime occurredAt,
            String decision,
            int minute
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.VAR_DECISION;
        }
    }

    record AddedTimeAnnounced(
            UUID matchId,
            OffsetDateTime occurredAt,
            int addedMinutes
    ) implements MatchEvent {
        @Override
        public MatchEventType eventType() {
            return MatchEventType.ADDED_TIME_ANNOUNCED;
        }
    }

}
