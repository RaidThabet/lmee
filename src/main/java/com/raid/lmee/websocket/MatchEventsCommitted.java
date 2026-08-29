package com.raid.lmee.websocket;

import com.raid.lmee.domain.event.MatchEvent;

import java.util.List;
import java.util.UUID;

public record MatchEventsCommitted(
        UUID matchId,
        List<MatchEvent> events
) {
}
