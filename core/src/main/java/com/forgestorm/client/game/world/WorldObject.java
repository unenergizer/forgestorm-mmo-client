package com.forgestorm.client.game.world;

import com.forgestorm.client.ClientMain;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorldObject implements Comparable<WorldObject> {

    private final ClientMain clientMain;

    /**
     * The actual sprite position on the screen.
     */
    private float drawX, drawY;

    public WorldObject(ClientMain clientMain) {
        this.clientMain = clientMain;
    }


    @Override
    public int compareTo(WorldObject otherWorldObject) {
        // Solve drawY and drawX sorting (faster - @TEttinger#4280 - libGDX discord)
        int x = Float.floatToIntBits(otherWorldObject.getDrawX() - this.getDrawX() + 0f);
        int y = Float.floatToIntBits(otherWorldObject.getDrawY() - this.getDrawY() + 0f);
        return ((y >> 31 | -y >>> 31) << 1) + (x >> 31 | -x >>> 31);
    }

}
