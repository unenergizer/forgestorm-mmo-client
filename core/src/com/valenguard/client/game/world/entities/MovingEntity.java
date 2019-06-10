package com.valenguard.client.game.world.entities;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.game.screens.GameScreen;
import com.valenguard.client.game.world.entities.animations.EntityAnimation;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.util.GameTextUtil;

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

    /**
     * Entity attributes
     */
    private Attributes attributes = new Attributes();

    private int damage;
    private int expDrop;
    private int dropTable;
    private float probWalkStill;
    private float probWalkStart;

    /**
     * Entity name drawing
     */
    public void drawEntityName() {
        float x = getDrawX() + 8;
        float y = getDrawY() + 16 + ClientConstants.namePlateDistanceInPixels;

        GameTextUtil.drawMessage(getEntityName(), Color.WHITE, .5f, x, y);
    }

    private float distanceMoved = 0;
    private int damageTaken = 0;
    private boolean showDamage = false;

    public void drawFloatingNumbers() {
        if (!showDamage) return;
        float x = getDrawX() + 8;
        float y = getDrawY() + 8 + distanceMoved;

        GameTextUtil.drawMessage(Integer.toString(damageTaken), Color.RED, 1f, x, y);

        distanceMoved = distanceMoved + 0.11f;
        if (distanceMoved >= 9) {
            distanceMoved = 0;
            damageTaken = 0;
            showDamage = false;
        }
    }

    private int maxHealth;
    private int currentHealth;

    public void drawEntityHpBar() {
        float x = getDrawX() + 8;
        float y = getDrawY() + 16;
        float width = 14;
        float xPos = x - (width / 2);
        GameScreen gameScreen = Valenguard.gameScreen;
        gameScreen.getSpriteBatch().draw(gameScreen.getHpBase(), xPos, y, width, 1);
        gameScreen.getSpriteBatch().draw(gameScreen.getHpArea(), xPos, y, width * ((float) currentHealth / maxHealth), 1);
    }

    public void addLocationToFutureQueue(Location location) {
        futureLocationRequests.add(location);
    }

    public void loadTextures(GameAtlas gameAtlas) {
        entityAnimation.loadAll(gameAtlas);
    }
}
