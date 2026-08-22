package com.raid.lmee.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ClubMatchId implements Serializable {

    @Column(name = "match_id")
    private UUID matchId;

    @Column(name = "club_id")
    private UUID clubId;
}
