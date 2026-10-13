package com.raid.lmee.rest;

import com.raid.lmee.model.ClubDTO;
import com.raid.lmee.model.IdResponseDTO;
import com.raid.lmee.service.UserClubService;
import com.raid.lmee.swagger.ApiCreateResponses;
import com.raid.lmee.swagger.ApiDeleteResponses;
import com.raid.lmee.swagger.ApiGetListResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-clubs")
@RequiredArgsConstructor
public class UserClubResource {

    private final UserClubService userClubService;

    @GetMapping
    @ApiGetListResponses
    public ResponseEntity<List<ClubDTO>> getUserFavouriteClubs(
            @AuthenticationPrincipal Jwt principal
    ) {
        return ResponseEntity.ok(userClubService.getUserFavClubs(userID(principal)));
    }

    @PostMapping
    @ApiCreateResponses
    public ResponseEntity<ClubDTO> addFavouriteClub(
            @AuthenticationPrincipal Jwt principal,
            @RequestBody IdResponseDTO request
    ) {
        ClubDTO addedClub = userClubService.addFavClub(userID(principal), request.getId());
        return new ResponseEntity<>(addedClub, HttpStatus.CREATED);
    }

    @DeleteMapping("{clubId}")
    @ApiDeleteResponses
    public ResponseEntity<Void> removeClubFromFavourites(
            @AuthenticationPrincipal Jwt principal,
            @PathVariable UUID clubId
    ) {
        userClubService.removeClubFromFav(userID(principal), clubId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private UUID userID(Jwt principal) {
        String userId = principal.getSubject();

        assert userId != null;

        return UUID.fromString(userId);
    }
}
