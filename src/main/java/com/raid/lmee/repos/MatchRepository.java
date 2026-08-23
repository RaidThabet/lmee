package com.raid.lmee.repos;

import com.raid.lmee.domain.Match;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MatchRepository extends JpaRepository<Match, UUID> {

    @Query("""
            select distinct m from Match m
            left join fetch m.matchClubs mc
            left join fetch mc.club
            left join fetch m.matchState
            order by m.scheduledKickoff
            """)
    List<Match> findAllWithClubsAndState();

}
