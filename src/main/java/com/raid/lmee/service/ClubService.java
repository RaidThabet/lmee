package com.raid.lmee.service;

import com.raid.lmee.domain.Club;
import com.raid.lmee.model.ClubDTO;
import com.raid.lmee.repos.ClubRepository;
import com.raid.lmee.util.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@Transactional(rollbackFor = Exception.class)
public class ClubService {

    private final ClubRepository clubRepository;

    public ClubService(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public List<ClubDTO> findAll() {
        final List<Club> clubs = clubRepository.findAll(Sort.by("id"));
        return clubs.stream()
                .map(club -> mapToDTO(club, new ClubDTO()))
                .toList();
    }

    public ClubDTO get(final UUID id) {
        return clubRepository.findById(id)
                .map(club -> mapToDTO(club, new ClubDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UUID create(final ClubDTO clubDTO) {
        final Club club = new Club();
        mapToEntity(clubDTO, club);
        return clubRepository.save(club).getId();
    }

    public void update(final UUID id, final ClubDTO clubDTO) {
        final Club club = clubRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(clubDTO, club);
        clubRepository.save(club);
    }

    public void delete(final UUID id) {
        final Club club = clubRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        clubRepository.delete(club);
    }

    private ClubDTO mapToDTO(final Club club, final ClubDTO clubDTO) {
        clubDTO.setId(club.getId());
        clubDTO.setName(club.getName());
        clubDTO.setCountry(club.getCountry());
        return clubDTO;
    }

    private Club mapToEntity(final ClubDTO clubDTO, final Club club) {
        club.setName(clubDTO.getName());
        club.setCountry(clubDTO.getCountry());
        return club;
    }

    public boolean nameExists(final String name) {
        return clubRepository.existsByNameIgnoreCase(name);
    }

}
