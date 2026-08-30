package com.raid.lmee.websocket;

import com.raid.lmee.model.MatchEventDTO;
import com.raid.lmee.model.MatchEventType;

public record MatchEventMessage(
        MatchEventType type,
        MatchEventDTO event
) {
}
