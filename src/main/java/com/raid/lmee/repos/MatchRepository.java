package com.raid.lmee.repos;

import com.raid.lmee.domain.Match;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MatchRepository extends JpaRepository<Match, UUID> {

    @Query("""
            select m from Match m
            left join fetch m.matchClubs mc
            left join fetch mc.club
            left join fetch m.matchState
            order by m.scheduledKickoff
            """)
    List<Match> findAllWithClubsAndState();

    @Query("""
            select m from Match m
            left join fetch m.matchClubs mc
            left join fetch mc.club
            left join fetch m.matchState
            where m.id = :matchId
            """)
    Optional<Match> findByIdWithClubsAndState(UUID matchId);
}
