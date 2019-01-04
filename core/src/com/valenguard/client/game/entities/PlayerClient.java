package com.valenguard.client.game.entities;

import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.rpg.Skills;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerClient extends Player {

    private Skills skills = new Skills();

    private boolean isWarping = false;

    /**
     * The direction the entity intends to move in the future.
     */
    private MoveDirection predictedMoveDirection = MoveDirection.NONE;
}
