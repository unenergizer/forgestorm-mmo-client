package com.forgestorm.client.util;

import java.util.Queue;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PathSolution {
    private boolean foundGoal;
    private Queue<MoveNode> path;
}
