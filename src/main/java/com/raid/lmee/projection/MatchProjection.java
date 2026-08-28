package com.raid.lmee.projection;

import com.raid.lmee.domain.ClubMatchId;
import com.raid.lmee.domain.Match;
import com.raid.lmee.domain.MatchState;
import com.raid.lmee.domain.event.MatchEvent;
import com.raid.lmee.model.MatchStatus;
import com.raid.lmee.repos.ClubMatchRepository;
import com.raid.lmee.repos.MatchRepository;
import com.raid.lmee.repos.MatchStateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchProjection {

    private final MatchStateRepository matchStateRepository;

    private final MatchRepository matchRepository;

    private final ClubMatchRepository clubMatchRepository;

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

    public void on(MatchEvent.MatchStarted event) {
        MatchState matchState = loadState(event.matchId());
        matchState.setStatus(MatchStatus.IN_PROGRESS);
        matchState.setHomeScore(0);
        matchState.setAwayScore(0);
        matchState.setHomeYellows(0);
        matchState.setAwayYellows(0);
        matchState.setHomeReds(0);
        matchState.setAwayReds(0);
        matchState.setHomeSubs(0);
        matchState.setAwaySubs(0);
        matchStateRepository.save(matchState);
    }

    public void on(MatchEvent.FirstHalfEnded event) {
        updateStatus(event.matchId(), MatchStatus.HALF_TIME);
    }

    public void on(MatchEvent.SecondHalfStarted event) {
        updateStatus(event.matchId(), MatchStatus.IN_PROGRESS);
    }

    public void on(MatchEvent.FullTime event) {
        updateStatus(event.matchId(), MatchStatus.COMPLETED);
    }

    public void on(MatchEvent.MatchAbandoned event) {
        updateStatus(event.matchId(), MatchStatus.ABANDONED);
    }

    public void on(MatchEvent.MatchPostponed event) {
        updateStatus(event.matchId(), MatchStatus.POSTPONED);
    }

    private void updateStatus(UUID matchId, MatchStatus status) {
        MatchState matchState = loadState(matchId);
        matchState.setStatus(status);
        matchStateRepository.save(matchState);
    }

    public void on(MatchEvent.GoalScored event) {
        adjustScore(event.matchId(), event.clubId(), 1, false);
    }

    public void on(MatchEvent.OwnGoal event) {
        adjustScore(event.matchId(), event.clubId(), 1, true);
    }

    public void on(MatchEvent.GoalCanceled event) {
        adjustScore(event.matchId(), event.clubId(), -1, false);
    }

    public void on(MatchEvent.PenaltyAwarded event) {
    }

    public void on(MatchEvent.PenaltyScored event) {
        adjustScore(event.matchId(), event.clubId(), 1, false);
    }

    public void on(MatchEvent.PenaltyMissed event) {
    }

    public void on(MatchEvent.YellowCardGiven event) {
        incrementSide(
                event.matchId(),
                event.clubId(),
                state -> state.setHomeYellows(state.getHomeYellows() + 1),
                state -> state.setAwayYellows(state.getAwayYellows() + 1)
        );
    }

    /**
     * A second yellow is a booking as well as a dismissal, so it bumps the yellow count here and the
     * red count through the {@code RedCardGiven} that always follows it. Counting bookings from
     * {@code YELLOW_CARD_GIVEN} alone misses every player sent off for two.
     */
    public void on(MatchEvent.SecondYellowCard event) {
        incrementSide(
                event.matchId(),
                event.clubId(),
                state -> state.setHomeYellows(state.getHomeYellows() + 1),
                state -> state.setAwayYellows(state.getAwayYellows() + 1)
        );
    }

    public void on(MatchEvent.RedCardGiven event) {
        incrementSide(
                event.matchId(),
                event.clubId(),
                state -> state.setHomeReds(state.getHomeReds() + 1),
                state -> state.setAwayReds(state.getAwayReds() + 1)
        );
    }

    public void on(MatchEvent.Substitution event) {
        incrementSide(
                event.matchId(),
                event.clubId(),
                state -> state.setHomeSubs(state.getHomeSubs() + 1),
                state -> state.setAwaySubs(state.getAwaySubs() + 1)
        );
    }

    public void on(MatchEvent.VarCheckStarted event) {
    }

    public void on(MatchEvent.VarDecision event) {
    }

    public void on(MatchEvent.AddedTimeAnnounced event) {
    }

    /**
     * Applies whichever increment belongs to {@code clubId}'s side of the tally.
     */
    private void incrementSide(
            UUID matchId,
            UUID clubId,
            Consumer<MatchState> incrementHome,
            Consumer<MatchState> incrementAway
    ) {
        MatchState matchState = loadState(matchId);
        Consumer<MatchState> increment = isHomeSide(matchId, clubId) ? incrementHome : incrementAway;
        increment.accept(matchState);

        matchStateRepository.save(matchState);
    }

    /**
     * Moves one side's score by {@code delta}.
     * <p>
     * {@code creditOpponent} flips which side is credited, so an own goal passes the conceding
     * player's club and still lands on the opposing scoreline.
     */
    private void adjustScore(UUID matchId, UUID clubId, int delta, boolean creditOpponent) {
        MatchState matchState = loadState(matchId);
        boolean creditHome = isHomeSide(matchId, clubId) ^ creditOpponent;

        Integer current = creditHome ? matchState.getHomeScore() : matchState.getAwayScore();
        if (current == null) {
            throw new IllegalStateException("match " + matchId + " has no score to adjust; it was never started");
        }

        if (creditHome) {
            matchState.setHomeScore(current + delta);
        } else {
            matchState.setAwayScore(current + delta);
        }

        matchStateRepository.save(matchState);
    }

    private MatchState loadState(UUID matchId) {
        return matchStateRepository.findById(matchId)
                .orElseThrow(() -> new IllegalStateException("no match state projected for match " + matchId));
    }

    private boolean isHomeSide(UUID matchId, UUID clubId) {
        return clubMatchRepository.findById(new ClubMatchId(matchId, clubId))
                .orElseThrow(() -> new IllegalStateException("club " + clubId + " does not take part in match " + matchId))
                .getIsHome();
    }
}
