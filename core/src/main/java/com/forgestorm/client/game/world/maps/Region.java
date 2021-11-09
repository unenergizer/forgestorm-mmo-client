package com.forgestorm.client.game.world.maps;

import com.forgestorm.shared.game.world.maps.Floors;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Region {

    private final String worldName;

    // Bottom Left point
    private int world1X, world1Y;

    // Top Right point
    private int world2X, world2Y;

    // Z axis
    private short worldZ;

    @Setter
    private Boolean allowPVP, allowChat, fullHeal;

    @Setter
    private String greetingsChat, greetingsTitle, farewellChat, farewellTitle;

    @Setter
    private RegionManager.RegionType regionType = RegionManager.RegionType.BUILDING;

    public Region(String worldName, int world1X, int world1Y, int world2X, int world2Y, short worldZ) {
        this.worldName = worldName;
        this.world1X = world1X;
        this.world1Y = world1Y;
        this.world2X = world2X;
        this.world2Y = world2Y;
        this.worldZ = worldZ;
    }

    public void setWorld1X(int world1X) {
        if (world1X >= this.world2X) return;
        this.world1X = world1X;
    }

    public void setWorld1Y(int world1Y) {
        if (world1Y >= world2Y) return;
        this.world1Y = world1Y;
    }

    public void setWorld2X(int world2X) {
        if (world2X <= world1X) return;
        this.world2X = world2X;
    }

    public void setWorld2Y(int world2Y) {
        if (world2Y <= world1Y) return;
        this.world2Y = world2Y;
    }

    public void setWorldZ(short worldZ) {
        if (worldZ > Floors.getHighestFloor().getWorldZ()) return;
        if (worldZ < Floors.getLowestFloor().getWorldZ()) return;
        this.worldZ = worldZ;
    }

    public int getWidth() {
        return 1 + world2X - world1X;
    }

    public int getHeight() {
        return 1 + world2Y - world1Y;
    }

    public boolean doesIntersect(int worldX, int worldY) {
        boolean intersectX = worldX >= world1X && worldX <= world2X;
        boolean intersectY = worldY >= world1Y && worldY <= world2Y;
        return intersectX && intersectY;
    }

}
