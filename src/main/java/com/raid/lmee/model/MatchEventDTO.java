package com.raid.lmee.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MatchEventDTO(
        int sequenceNumber,
        MatchEventType type,
        Integer minute,
        Boolean isHomeClub,
        String playerName,
        String playerInName,
        String playerOutName,
        Integer addedMinutes,
        String reason,
        String decision
) {
}
