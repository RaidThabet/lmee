package com.raid.lmee.repos;

import com.raid.lmee.domain.ClubMatch;
import com.raid.lmee.domain.ClubMatchId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubMatchRepository extends JpaRepository<ClubMatch, ClubMatchId> {
}
