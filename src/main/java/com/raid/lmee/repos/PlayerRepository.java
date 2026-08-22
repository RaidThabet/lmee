package com.raid.lmee.repos;

import com.raid.lmee.domain.Player;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Player findFirstByClubId(UUID id);

}
