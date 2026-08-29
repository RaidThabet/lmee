package com.raid.lmee.mapper;

import com.raid.lmee.domain.Player;
import com.raid.lmee.domain.event.MatchEvent;
import com.raid.lmee.model.MatchEventDTO;
import com.raid.lmee.repos.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MatchEventDTOMapper {

    private final PlayerRepository playerRepository;

    public List<MatchEventDTO> toDTOs(List<SequencedEvent> history) {
        MatchEvent.MatchScheduled scheduled = scheduledEventOf(history);
        Map<UUID, String> playerNames = loadSquadNames(scheduled);
        UUID homeClubId = scheduled == null ? null : scheduled.homeClubId();

        return history.stream()
                .map(sequenced -> toDTO(sequenced, playerNames, homeClubId))
                .toList();
    }

    /**
     * Loads both squads in one query. Every player an event can name is on one of the two teams, so
     * this needs no per-event id collection.
     */
    private Map<UUID, String> loadSquadNames(MatchEvent.MatchScheduled scheduled) {
        if (scheduled == null) {
            return Map.of();
        }

        return playerRepository.findByClubIdIn(List.of(scheduled.homeClubId(), scheduled.awayClubId()))
                .stream()
                .collect(Collectors.toMap(Player::getId, Player::getName));
    }

    /**
     * The scheduling event is the only place the stream itself records the two clubs, so it supplies
     * both the squads to load and the answer to {@code isHomeClub}.
     */
    private MatchEvent.MatchScheduled scheduledEventOf(List<SequencedEvent> history) {
        for (SequencedEvent sequenced : history) {
            if (sequenced.event() instanceof MatchEvent.MatchScheduled scheduled) {
                return scheduled;
            }
        }
        return null;
    }

    private MatchEventDTO toDTO(
            SequencedEvent sequenced,
            Map<UUID, String> playerNames,
            UUID homeClubId
    ) {
        MatchEvent event = sequenced.event();
        MatchEventDTO.MatchEventDTOBuilder dto = MatchEventDTO.builder()
                .sequenceNumber(sequenced.sequenceNumber())
                .type(event.eventType());

        return switch (event) {
            case MatchEvent.MatchScheduled _ -> dto.build();
            case MatchEvent.MatchStarted _ -> dto.build();
            case MatchEvent.FirstHalfEnded _ -> dto.build();
            case MatchEvent.SecondHalfStarted _ -> dto.build();
            case MatchEvent.FullTime _ -> dto.build();
            case MatchEvent.MatchAbandoned e -> dto
                    .minute(e.minute())
                    .reason(e.reason())
                    .build();
            case MatchEvent.MatchPostponed e -> dto
                    .reason(e.reason())
                    .build();
            case MatchEvent.GoalScored e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .playerName(playerNames.get(e.playerId()))
                    .build();
            case MatchEvent.OwnGoal e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .playerName(playerNames.get(e.playerId()))
                    .build();
            case MatchEvent.GoalCanceled e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .build();
            case MatchEvent.PenaltyAwarded e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .build();
            case MatchEvent.PenaltyScored e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .playerName(playerNames.get(e.playerId()))
                    .build();
            case MatchEvent.PenaltyMissed e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .playerName(playerNames.get(e.playerId()))
                    .build();
            case MatchEvent.YellowCardGiven e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .playerName(playerNames.get(e.playerId()))
                    .build();
            case MatchEvent.SecondYellowCard e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .playerName(playerNames.get(e.playerId()))
                    .build();
            case MatchEvent.RedCardGiven e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .playerName(playerNames.get(e.playerId()))
                    .build();
            case MatchEvent.Substitution e -> dto
                    .minute(e.minute())
                    .isHomeClub(isHomeClub(e.clubId(), homeClubId))
                    .playerOutName(playerNames.get(e.playerOutId()))
                    .playerInName(playerNames.get(e.playerInId()))
                    .build();
            case MatchEvent.VarCheckStarted e -> dto
                    .minute(e.minute())
                    .reason(e.reason())
                    .build();
            case MatchEvent.VarDecision e -> dto
                    .minute(e.minute())
                    .decision(e.decision())
                    .build();
            case MatchEvent.AddedTimeAnnounced e -> dto
                    .addedMinutes(e.addedMinutes())
                    .build();
        };
    }

    private Boolean isHomeClub(UUID clubId, UUID homeClubId) {
        return homeClubId == null ? null : homeClubId.equals(clubId);
    }

    public record SequencedEvent(int sequenceNumber, MatchEvent event) {
    }
}
