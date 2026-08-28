package com.raid.lmee.service;

import com.raid.lmee.domain.Club;
import com.raid.lmee.domain.Player;
import com.raid.lmee.exception.ClubNotFoundException;
import com.raid.lmee.exception.PlayerNotFoundException;
import com.raid.lmee.model.PlayerDTO;
import com.raid.lmee.repos.ClubRepository;
import com.raid.lmee.repos.PlayerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;

    public PlayerService(final PlayerRepository playerRepository,
            final ClubRepository clubRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;
    }

    public List<PlayerDTO> findAll() {
        final List<Player> players = playerRepository.findAll(Sort.by("id"));
        return players.stream()
                .map(player -> mapToDTO(player, new PlayerDTO()))
                .toList();
    }

    public PlayerDTO get(final UUID id) {
        return playerRepository.findById(id)
                .map(player -> mapToDTO(player, new PlayerDTO()))
                .orElseThrow(() -> new PlayerNotFoundException(id));
    }

    public UUID create(final PlayerDTO playerDTO) {
        final Player player = new Player();
        mapToEntity(playerDTO, player);
        return playerRepository.save(player).getId();
    }

    public void update(final UUID id, final PlayerDTO playerDTO) {
        final Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
        mapToEntity(playerDTO, player);
        playerRepository.save(player);
    }

    public void delete(final UUID id) {
        final Player player = playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException(id));
        playerRepository.delete(player);
    }

    private PlayerDTO mapToDTO(final Player player, final PlayerDTO playerDTO) {
        playerDTO.setId(player.getId());
        playerDTO.setName(player.getName());
        playerDTO.setClub(player.getClub() == null ? null : player.getClub().getId());
        return playerDTO;
    }

    private Player mapToEntity(final PlayerDTO playerDTO, final Player player) {
        player.setName(playerDTO.getName());
        final Club club = playerDTO.getClub() == null ? null : clubRepository.findById(playerDTO.getClub())
                .orElseThrow(() -> new ClubNotFoundException(playerDTO.getClub()));
        player.setClub(club);
        return player;
    }

}
