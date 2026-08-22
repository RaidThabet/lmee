package com.raid.lmee.rest;

import com.raid.lmee.model.ClubDTO;
import com.raid.lmee.model.IdResponseDTO;
import com.raid.lmee.service.ClubService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/clubs", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClubResource {

    private final ClubService clubService;

    public ClubResource(final ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping
    public ResponseEntity<List<ClubDTO>> getAllClubs() {
        return ResponseEntity.ok(clubService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubDTO> getClub(@PathVariable(name = "id") final UUID id) {
        return ResponseEntity.ok(clubService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<IdResponseDTO> createClub(@RequestBody @Valid final ClubDTO clubDTO) {
        final UUID createdId = clubService.create(clubDTO);
        return new ResponseEntity<>(new IdResponseDTO(createdId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdResponseDTO> updateClub(@PathVariable(name = "id") final UUID id,
                                                    @RequestBody @Valid final ClubDTO clubDTO) {
        clubService.update(id, clubDTO);
        return ResponseEntity.ok(new IdResponseDTO(id));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteClub(@PathVariable(name = "id") final UUID id) {
        clubService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
