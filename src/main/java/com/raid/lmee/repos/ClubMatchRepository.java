package com.raid.lmee.repos;

import com.raid.lmee.domain.ClubMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClubMatchRepository extends JpaRepository<ClubMatch, UUID> {
}
