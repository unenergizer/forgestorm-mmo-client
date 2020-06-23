package com.forgestorm.client.game.rpg;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReputationTypes {
    DIGNIFIED(6000, AttackStatus.NO_ATTACK),
    CHERISHED(4000, AttackStatus.NO_ATTACK),
    FRIENDLY(2000, AttackStatus.NO_ATTACK),

    NEUTRAL(1000, AttackStatus.ATTACK_ON_PROVOKE),

    NUISANCE(2000, AttackStatus.ATTACK_ON_SIGHT),
    HOSTILE(4000, AttackStatus.ATTACK_ON_SIGHT),
    ABOMINATION(6000, AttackStatus.ATTACK_ON_SIGHT);

    private int range;
    private AttackStatus attackStatus;
}
