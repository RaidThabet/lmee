package com.raid.lmee.websocket;

import com.raid.lmee.model.MatchEventDTO;

import java.util.List;
import java.util.UUID;

public record MatchEventsCommitted(
        UUID matchId,
        List<MatchEventDTO> events
) {
}
