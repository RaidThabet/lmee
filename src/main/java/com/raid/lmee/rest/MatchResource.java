package com.raid.lmee.rest;

import com.raid.lmee.model.*;
import com.raid.lmee.model.command.MatchCommand;
import com.raid.lmee.service.MatchService;
import com.raid.lmee.swagger.ApiCommandResponses;
import com.raid.lmee.swagger.ApiCreateResponses;
import com.raid.lmee.swagger.ApiGetListResponses;
import com.raid.lmee.swagger.ApiGetOneResponses;
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
    @ApiGetListResponses
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(matchService.findAll());
    }

    @GetMapping("{matchId}")
    @ApiGetOneResponses
    public ResponseEntity<MatchResponse> getMatch(
            @PathVariable UUID matchId
    ) {
        return ResponseEntity.ok(matchService.findMatch(matchId));
    }

    @PostMapping
    @ApiCreateResponses
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
    @ApiGetOneResponses
    public ResponseEntity<List<MatchEventDTO>> getMatchEvents(
            @PathVariable UUID matchId
    ) {
        List<MatchEventDTO> events = matchService.findMatchEvents(matchId);

        return ResponseEntity.ok(events);
    }

    // TODO: response entity of the event id?
    @PostMapping("{matchId}/events")
    @ApiCommandResponses
    public ResponseEntity<Void> recordEvent(
            @PathVariable UUID matchId,
            @RequestBody @Valid MatchCommand command
    ) {
        matchService.handle(matchId, command);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("{matchId}/state")
    @ApiGetOneResponses
    public ResponseEntity<MatchStateDTO> getMatchState(
            @PathVariable UUID matchId
    ) {
        return ResponseEntity.ok(matchService.findMatchState(matchId));
    }

}
