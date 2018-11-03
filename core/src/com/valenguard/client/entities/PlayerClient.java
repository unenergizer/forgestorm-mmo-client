package com.valenguard.client.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerClient extends Entity {

    /**
     * The direction the entity intends to move in the future.
     */
    private MoveDirection predictedMoveDirection = MoveDirection.NONE;
}
