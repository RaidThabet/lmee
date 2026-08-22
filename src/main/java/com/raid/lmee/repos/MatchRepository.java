package com.raid.lmee.repos;

import com.raid.lmee.domain.Match;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MatchRepository extends JpaRepository<Match, UUID> {
}
