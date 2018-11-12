package com.valenguard.client.game.entities;

import com.valenguard.client.game.maps.MoveDirection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerClient extends Player {

    private boolean isWarping = false;

    /**
     * The direction the entity intends to move in the future.
     */
    private MoveDirection predictedMoveDirection = MoveDirection.NONE;
}
