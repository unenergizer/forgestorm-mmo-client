package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.entities.animations.EntityAnimation;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.Location;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovingEntity extends Entity {

    /**
     * The exact tile location of the entity on the tile grid.
     */
    private Location futureMapLocation;

    /**
     * The direction the entity is facing. Is not always the same direction
     * as they are moving because the move direction can be NONE.
     */
    private MoveDirection facingDirection;

    /**
     * The rate of speed the entity moves across tiles.
     * The smaller the number, the faster the entity moves.
     */
    private float moveSpeed;

    /**
     * Used by entity manager to measure the walk time between tiles/locations.
     */
    private float walkTime = 0;

    /**
     * The projected future location of this entity.
     */
    private Queue<Location> futureLocationRequests = new LinkedList<Location>();

    /**
     * The animations used for this entity.
     */
    private EntityAnimation entityAnimation;

    private boolean glyphInitialized = false;
    private GlyphLayout regularText = new GlyphLayout();
    private GlyphLayout shadowText = new GlyphLayout();

    public void drawEntityName() {
        float x = getDrawX() + 8;
        float y = getDrawY() + (16 + ClientConstants.namePlateDistanceInPixels);

        BitmapFont font = Valenguard.gameScreen.getFont();
        if (!glyphInitialized) {
            font.getData().setScale(.5f);
            if (getEntityType() == EntityType.NPC) {
                font.setColor(Color.BLACK);
                shadowText.setText(font, getEntityName());
                font.setColor(Color.LIME);
                regularText.setText(font, getEntityName());
            } else if (getEntityType() == EntityType.MONSTER) {
                font.setColor(Color.BLACK);
                shadowText.setText(font, getEntityName());
                font.setColor(Color.RED);
                regularText.setText(font, getEntityName());
            } else {
                font.setColor(Color.BLACK);
                shadowText.setText(font, getEntityName());
                font.setColor(Color.GOLD);
                regularText.setText(font, getEntityName());
            }
            glyphInitialized = true;
        }

        font.setColor(Color.BLACK);
        font.draw(Valenguard.gameScreen.getSpriteBatch(), shadowText, x - (shadowText.width / 2) + .5f, y - .5f);

        font.setColor(Color.GOLD);
        font.draw(Valenguard.gameScreen.getSpriteBatch(), regularText, x - (regularText.width / 2), y);
    }

    public void addLocationToFutureQueue(Location location) {
        futureLocationRequests.add(location);
    }
}
