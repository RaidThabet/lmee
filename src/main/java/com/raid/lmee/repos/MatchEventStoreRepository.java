package com.raid.lmee.repos;

import com.raid.lmee.domain.MatchEventStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface MatchEventStoreRepository extends JpaRepository<MatchEventStore, UUID> {
    int countByMatchId(UUID matchId);

    List<MatchEventStore> findByMatchId(UUID matchId, Sort sort);
}
