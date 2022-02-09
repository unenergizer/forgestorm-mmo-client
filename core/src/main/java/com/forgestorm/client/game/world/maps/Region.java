package com.forgestorm.client.game.world.maps;

import com.forgestorm.shared.game.world.maps.Floors;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Region {

    private final transient int regionID;

    private final String worldName;

    // Bottom Left point
    private int x1, y1;

    // Top Right point
    private int x2, y2;

    // Z axis
    private short z;

    @Setter
    private Boolean allowPVP, allowChat, fullHeal;

    @Setter
    private String greetingsChat, greetingsTitle, farewellChat, farewellTitle;

    @Setter
    private Integer backgroundMusicID, ambianceSoundID;

    @Setter
    private RegionManager.RegionType regionType = RegionManager.RegionType.BUILDING;

    public Region(int regionID, String worldName, int x1, int y1, int x2, int y2, short z) {
        this.regionID = regionID;
        this.worldName = worldName;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.z = z;
    }

    public void setX1(int x1) {
        if (x1 >= this.x2) return;
        this.x1 = x1;
    }

    public void setY1(int y1) {
        if (y1 >= y2) return;
        this.y1 = y1;
    }

    public void setX2(int x2) {
        if (x2 <= x1) return;
        this.x2 = x2;
    }

    public void setY2(int y2) {
        if (y2 <= y1) return;
        this.y2 = y2;
    }

    public void setZ(short z) {
        if (z > Floors.getHighestFloor().getWorldZ()) return;
        if (z < Floors.getLowestFloor().getWorldZ()) return;
        this.z = z;
    }

    public int getWidth() {
        return 1 + x2 - x1;
    }

    public int getHeight() {
        return 1 + y2 - y1;
    }

    public boolean doesIntersect(int worldX, int worldY) {
        boolean intersectX = worldX >= x1 && worldX <= x2;
        boolean intersectY = worldY >= y1 && worldY <= y2;
        return intersectX && intersectY;
    }

    public boolean doesIntersect(int worldX, int worldY, short worldZ) {
        return worldZ == z && doesIntersect(worldX, worldY);
    }
}
