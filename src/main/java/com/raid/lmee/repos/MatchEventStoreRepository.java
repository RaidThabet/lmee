package com.raid.lmee.repos;

import com.raid.lmee.domain.MatchEventStore;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MatchEventStoreRepository extends JpaRepository<MatchEventStore, UUID> {
}
