package com.raid.lmee.model.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Wire-level input for {@code POST /matches/{id}/events}.
 * <p>
 * The discriminator names mirror {@link com.raid.lmee.model.MatchEventType} so a client can use the
 * same vocabulary for reads and writes. Commands intentionally carry neither {@code matchId} (it comes
 * from the path) nor {@code occurredAt} (the aggregate stamps it), so a client cannot forge either.
 * <p>
 * {@code MATCH_SCHEDULED} has no command: scheduling happens through {@code POST /matches}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MatchCommand.StartMatch.class, name = "MATCH_STARTED"),
        @JsonSubTypes.Type(value = MatchCommand.EndFirstHalf.class, name = "FIRST_HALF_ENDED"),
        @JsonSubTypes.Type(value = MatchCommand.StartSecondHalf.class, name = "SECOND_HALF_STARTED"),
        @JsonSubTypes.Type(value = MatchCommand.BlowFullTime.class, name = "FULL_TIME"),
        @JsonSubTypes.Type(value = MatchCommand.AbandonMatch.class, name = "MATCH_ABANDONED"),
        @JsonSubTypes.Type(value = MatchCommand.PostponeMatch.class, name = "MATCH_POSTPONED"),
        @JsonSubTypes.Type(value = MatchCommand.ScoreGoal.class, name = "GOAL_SCORED"),
        @JsonSubTypes.Type(value = MatchCommand.ScoreOwnGoal.class, name = "OWN_GOAL"),
        @JsonSubTypes.Type(value = MatchCommand.CancelGoal.class, name = "GOAL_CANCELED"),
        @JsonSubTypes.Type(value = MatchCommand.GiveYellowCard.class, name = "YELLOW_CARD_GIVEN"),
        @JsonSubTypes.Type(value = MatchCommand.GiveRedCard.class, name = "RED_CARD_GIVEN"),
        @JsonSubTypes.Type(value = MatchCommand.Substitute.class, name = "SUBSTITUTION"),
        @JsonSubTypes.Type(value = MatchCommand.AwardPenalty.class, name = "PENALTY_AWARDED"),
        @JsonSubTypes.Type(value = MatchCommand.ScorePenalty.class, name = "PENALTY_SCORED"),
        @JsonSubTypes.Type(value = MatchCommand.MissPenalty.class, name = "PENALTY_MISSED"),
        @JsonSubTypes.Type(value = MatchCommand.StartVarCheck.class, name = "VAR_CHECK_STARTED"),
        @JsonSubTypes.Type(value = MatchCommand.RecordVarDecision.class, name = "VAR_DECISION"),
        @JsonSubTypes.Type(value = MatchCommand.AnnounceAddedTime.class, name = "ADDED_TIME_ANNOUNCED")
})
public sealed interface MatchCommand {

    record StartMatch() implements MatchCommand {
    }

    record EndFirstHalf() implements MatchCommand {
    }

    record StartSecondHalf() implements MatchCommand {
    }

    record BlowFullTime() implements MatchCommand {
    }

    record AbandonMatch(
            String reason,
            int minute
    ) implements MatchCommand {
    }

    record PostponeMatch(
            String reason
    ) implements MatchCommand {
    }

    record ScoreGoal(
            @NotNull UUID clubId,
            @NotNull UUID playerId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record ScoreOwnGoal(
            @NotNull UUID clubId,
            @NotNull UUID playerId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record CancelGoal(
            @NotNull UUID clubId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record GiveYellowCard(
            @NotNull UUID clubId,
            @NotNull UUID playerId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record GiveRedCard(
            @NotNull UUID clubId,
            @NotNull UUID playerId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record Substitute(
            @NotNull UUID clubId,
            @NotNull UUID playerOutId,
            @NotNull UUID playerInId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record AwardPenalty(
            @NotNull UUID clubId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record ScorePenalty(
            @NotNull UUID clubId,
            @NotNull UUID playerId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record MissPenalty(
            @NotNull UUID clubId,
            @NotNull UUID playerId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record StartVarCheck(
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record RecordVarDecision(
            @NotBlank String decision,
            UUID correctsEventId,
            @NotNull @Min(0) Integer minute
    ) implements MatchCommand {
    }

    record AnnounceAddedTime(
            @NotNull @Min(0) Integer addedMinutes
    ) implements MatchCommand {
    }
}