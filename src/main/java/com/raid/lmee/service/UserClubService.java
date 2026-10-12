package com.raid.lmee.service;

import com.raid.lmee.domain.Club;
import com.raid.lmee.domain.User;
import com.raid.lmee.exception.ClubNotFoundException;
import com.raid.lmee.exception.UserNotFoundException;
import com.raid.lmee.model.ClubDTO;
import com.raid.lmee.repos.ClubRepository;
import com.raid.lmee.repos.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserClubService {

    private final UserRepository userRepository;

    private final ClubRepository clubRepository;

    public List<ClubDTO> getUserFavClubs(UUID userId) {
        List<Club> userFavClubs = userRepository.findFavouriteClubsByUserId(userId);

        return userFavClubs.stream()
                .map(c -> mapToDTO(
                        c,
                        new ClubDTO()
                ))
                .toList();
    }

    @Transactional
    public ClubDTO addFavClub(UUID userId, UUID clubId) {
        Optional<User> userOptional = userRepository.findByKeycloakId(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        Optional<Club> clubOptional = clubRepository.findById(clubId);
        if (clubOptional.isEmpty()) {
            throw new ClubNotFoundException(clubId);
        }

        User user = userOptional.get();
        Club club = clubOptional.get();

        user.getFavouriteClubs().add(club);

        userRepository.save(user);

        return mapToDTO(club, new ClubDTO());


    }

    @Transactional
    public void removeClubFromFav(UUID userId, UUID clubId) {
        Optional<User> userOptional = userRepository.findByKeycloakId(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        Optional<Club> clubOptional = clubRepository.findById(clubId);
        if (clubOptional.isEmpty()) {
            throw new ClubNotFoundException(clubId);
        }

        User user = userOptional.get();
        Club club = clubOptional.get();

        user.getFavouriteClubs().remove(club);

        userRepository.save(user);
    }

    private ClubDTO mapToDTO(final Club club, final ClubDTO clubDTO) {
        clubDTO.setId(club.getId());
        clubDTO.setName(club.getName());
        clubDTO.setCountry(club.getCountry());
        return clubDTO;
    }
}
