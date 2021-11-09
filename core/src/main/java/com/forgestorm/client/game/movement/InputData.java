package com.forgestorm.client.game.movement;


import com.forgestorm.client.util.MoveNode;

import java.util.Queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InputData {
    private ClientMovementProcessor.MovementInput movementInput;
    private Queue<MoveNode> moveNodes;
    private AbstractPostProcessor abstractPostProcessor;
}
