package com.forgestorm.client.game.world.maps;

import java.util.ArrayList;
import java.util.List;

public class Tile {

    private final transient List<TileImage> collisionParents = new ArrayList<TileImage>(0);

    public Tile() {
    }

    public Tile(List<TileImage> collisionParents) {
        this.collisionParents.addAll(collisionParents);
    }

    public void removeCollision(TileImage parent) {
        collisionParents.remove(parent);
    }

    public void addCollision(TileImage parent) {
        collisionParents.add(parent);
    }

    public boolean hasCollision() {
        return !collisionParents.isEmpty();
    }

    public List<TileImage> getCollisionParents() {
        return collisionParents;
    }
}
