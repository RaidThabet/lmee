package com.raid.lmee.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "matches", indexes = @Index(name = "idx_matches_kickoff", columnList = "scheduled_kickoff"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Match {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column
    private OffsetDateTime scheduledKickoff;

    @Column(columnDefinition = "text")
    private String venue;

    @OneToMany(mappedBy = "match")
    private Set<ClubMatch> matchClubs = new HashSet<>();

    @OneToOne(mappedBy = "match", cascade = CascadeType.REMOVE)
    private MatchState matchState;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
