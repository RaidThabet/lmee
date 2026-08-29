package com.raid.lmee.websocket;

import com.raid.lmee.domain.event.MatchEvent;
import com.raid.lmee.model.MatchEventType;

public record MatchEventMessage(
        MatchEventType type,
        MatchEvent event
) {
}
