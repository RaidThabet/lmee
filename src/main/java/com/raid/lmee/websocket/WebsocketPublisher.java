package com.raid.lmee.websocket;

import com.raid.lmee.model.MatchEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebsocketPublisher {

    private static final String MATCH_TOPIC_PREFIX = "/topic/matches/";

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMatchEventsCommitted(MatchEventsCommitted committed) {
        publish(committed.matchId(), committed.events());
    }

    void publish(UUID matchId, List<MatchEventDTO> events) {
        String destination = MATCH_TOPIC_PREFIX + matchId;

        events.forEach(event -> messagingTemplate.convertAndSend(
                destination,
                new MatchEventMessage(event.type(), event)
        ));
    }
}
