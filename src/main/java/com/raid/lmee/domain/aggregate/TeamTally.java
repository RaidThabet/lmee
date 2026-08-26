package com.raid.lmee.domain.aggregate;

import lombok.Data;

@Data
class TeamTally {

    private int yellowCards;

    private int redCards;

    private int substitutions;

    public void addYellowCard() {
        yellowCards++;
    }

    public void addRedCard() {
        redCards++;
    }

    public void addSubstitution() {
        substitutions++;
    }
}
