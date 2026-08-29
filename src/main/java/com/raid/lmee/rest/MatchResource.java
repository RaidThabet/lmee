package com.raid.lmee.rest;

import com.raid.lmee.model.CreateMatchRequest;
import com.raid.lmee.model.CreateMatchResponse;
import com.raid.lmee.model.MatchEventDTO;
import com.raid.lmee.model.MatchResponse;
import com.raid.lmee.model.command.MatchCommand;
import com.raid.lmee.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchResource {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(matchService.findAll());
    }

    @GetMapping("{matchId}")
    public ResponseEntity<MatchResponse> getMatch(
            @PathVariable UUID matchId
    ) {
        return ResponseEntity.ok(matchService.findMatch(matchId));
    }

    @PostMapping
    public ResponseEntity<CreateMatchResponse> registerMatch(
            @RequestBody @Valid CreateMatchRequest request
    ) {
        UUID createdMatchId = matchService.scheduleMatch(
                request.homeTeamId(),
                request.awayTeamId(),
                request.scheduledKickoff(),
                request.venue()
        );

        CreateMatchResponse response = new CreateMatchResponse(createdMatchId);

        return new ResponseEntity<>(response, CREATED);
    }

    @GetMapping("{matchId}/events")
    public ResponseEntity<List<MatchEventDTO>> getMatchEvents(
            @PathVariable UUID matchId
    ) {
        List<MatchEventDTO> events = matchService.findMatchEvents(matchId);

        return ResponseEntity.ok(events);
    }

    // TODO: response entity of the event id?
    @PostMapping("{matchId}/events")
    public ResponseEntity<Void> recordEvent(
            @PathVariable UUID matchId,
            @RequestBody @Valid MatchCommand command
    ) {
        matchService.handle(matchId, command);

        return ResponseEntity.accepted().build();
    }

}
