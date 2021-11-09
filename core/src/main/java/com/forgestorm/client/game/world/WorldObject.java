package com.forgestorm.client.game.world;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorldObject implements Comparable<WorldObject> {

    /**
     * The actual sprite position on the screen.
     */
    private float drawX, drawY;


    @Override
    public int compareTo(WorldObject otherWorldObject) {
        // Solve drawY and drawX sorting (fast)
//        return (int)(Math.signum(otherWorldObject.getDrawY() - this.getDrawY()) * 2 + Math.signum(otherWorldObject.getDrawX() - this.getDrawX()));

        // Solve drawY and drawX sorting (faster - @TEttinger#4280 - libGDX discord)
        int x = Float.floatToIntBits(otherWorldObject.getDrawX() - this.getDrawX());
        int y = Float.floatToIntBits(otherWorldObject.getDrawY() - this.getDrawY());
        return ((y >> 31 | -y >>> 31) << 1) + (x >> 31 | -x >>> 31);
    }

}
