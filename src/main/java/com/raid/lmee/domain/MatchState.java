package com.raid.lmee.domain;

import com.raid.lmee.model.MatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "matches_states")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MatchState {

    @Id
    @Column(name = "match_id", nullable = false, updatable = false)
    private UUID matchId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Column(nullable = false)
    private int homeScore;

    @Column(nullable = false)
    private int awayScore;

    @Column(nullable = false)
    private int homeYellows;

    @Column(nullable = false)
    private int awayYellows;

    @Column(nullable = false)
    private int homeReds;

    @Column(nullable = false)
    private int awayReds;

    @Column(nullable = false)
    private int homeSubs;

    @Column(nullable = false)
    private int awaySubs;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false, updatable = false)
    @MapsId
    private Match match;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
