package com.raid.lmee.rest;

import com.raid.lmee.model.IdResponseDTO;
import com.raid.lmee.model.PlayerDTO;
import com.raid.lmee.service.PlayerService;
import com.raid.lmee.swagger.ApiCreateResponses;
import com.raid.lmee.swagger.ApiDeleteResponses;
import com.raid.lmee.swagger.ApiGetListResponses;
import com.raid.lmee.swagger.ApiGetOneResponses;
import com.raid.lmee.swagger.ApiUpdateResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/players", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlayerResource {

    private final PlayerService playerService;

    public PlayerResource(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    @ApiGetListResponses
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        return ResponseEntity.ok(playerService.findAll());
    }

    @GetMapping("/{id}")
    @ApiGetOneResponses
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable(name = "id") final UUID id) {
        return ResponseEntity.ok(playerService.get(id));
    }

    @PostMapping
    @ApiCreateResponses
    public ResponseEntity<IdResponseDTO> createPlayer(@RequestBody @Valid final PlayerDTO playerDTO) {
        final UUID createdId = playerService.create(playerDTO);
        return new ResponseEntity<>(new IdResponseDTO(createdId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiUpdateResponses
    public ResponseEntity<IdResponseDTO> updatePlayer(@PathVariable(name = "id") final UUID id,
            @RequestBody @Valid final PlayerDTO playerDTO) {
        playerService.update(id, playerDTO);
        return ResponseEntity.ok(new IdResponseDTO(id));
    }

    @DeleteMapping("/{id}")
    @ApiDeleteResponses
    public ResponseEntity<Void> deletePlayer(@PathVariable(name = "id") final UUID id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
