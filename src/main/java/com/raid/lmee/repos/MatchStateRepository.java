package com.raid.lmee.repos;

import com.raid.lmee.domain.MatchState;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MatchStateRepository extends JpaRepository<MatchState, UUID> {
}
