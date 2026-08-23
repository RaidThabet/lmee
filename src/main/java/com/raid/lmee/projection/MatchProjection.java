package com.raid.lmee.projection;

import com.raid.lmee.domain.Match;
import com.raid.lmee.domain.MatchState;
import com.raid.lmee.domain.event.MatchEvent;
import com.raid.lmee.model.MatchStatus;
import com.raid.lmee.repos.MatchRepository;
import com.raid.lmee.repos.MatchStateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchProjection {

    private final MatchStateRepository matchStateRepository;

    private final MatchRepository matchRepository;

    public void on(MatchEvent.MatchScheduled event) {
        Optional<Match> matchOptional = matchRepository.findById(event.matchId());
        if (matchOptional.isEmpty()) {
            throw new RuntimeException("match not found");
            // TODO: custom exception
        }
        Match match = matchOptional.get();
        MatchState matchState = new MatchState();
        matchState.setMatch(match);
        matchState.setStatus(MatchStatus.SCHEDULED);
        MatchState savedMatchState = matchStateRepository.save(matchState);

        match.setMatchState(savedMatchState);
        matchRepository.save(match);
    }
}
