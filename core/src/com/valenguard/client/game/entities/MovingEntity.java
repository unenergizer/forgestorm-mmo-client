package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.movement.MoveUtil;

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

    private float moveSpeed;

    private float walkTime = 0;

    private Queue<Location> futureLocationRequests = new LinkedList<Location>();

    // How often the animation switches region
    private static final float WALK_INTERVAL = 0.25f;

    private float stateTime = 1f;

    private Texture walkingSheet;

    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;
    private TextureAtlas textureAtlas;

    public void initAnimation(String regionName) {
        textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.MAIN_ATLAS);
        walkDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions(regionName + "_down"), Animation.PlayMode.LOOP);
        walkUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions(regionName + "_up"), Animation.PlayMode.LOOP);
        walkLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions(regionName + "_left"), Animation.PlayMode.LOOP);
        walkRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions(regionName + "_right"), Animation.PlayMode.LOOP);
    }

    public void animate(float delta, SpriteBatch spriteBatch) {
        if (MoveUtil.isEntityMoving(this)) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        // Use this current frame as the texture to draw.
        TextureRegion currentFrame = null;
        switch (facingDirection) {
            case UP:
                currentFrame = walkUp.getKeyFrame(stateTime, true);
                break;
            case DOWN:
                currentFrame = walkDown.getKeyFrame(stateTime, true);
                break;
            case LEFT:
                currentFrame = walkLeft.getKeyFrame(stateTime, true);
                break;
            case RIGHT:
                currentFrame = walkRight.getKeyFrame(stateTime, true);
                break;
            case NONE:
                throw new RuntimeException("Facing direction cannot be NONE.");
        }
        spriteBatch.draw(currentFrame, getDrawX(), getDrawY());
    }

    public void addLocationToFutureQueue(Location location) {
        futureLocationRequests.add(location);
    }
}
