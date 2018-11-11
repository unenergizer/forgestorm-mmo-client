package com.valenguard.client.game.movement;


import com.valenguard.client.util.MoveNode;

import java.util.Queue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InputData {
    private ClientMovementProcessor.MovementInput movementInput;
    private Queue<MoveNode> moveNodes;
}
