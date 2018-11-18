package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.movement.MoveUtil;

public abstract class EntityAnimation {

    protected static final float WALK_INTERVAL = 0.25f;

    private final MovingEntity movingEntity;

    private float stateTime = 1f;

    public EntityAnimation(MovingEntity movingEntity) {
        this.movingEntity = movingEntity;
    }

    public void loadAll(GameAtlas gameAtlas, short[] textureIds) {
        load(Valenguard.getInstance().getFileManager().getAtlas(gameAtlas), textureIds);
    }

    public void loadAllVarArgs(GameAtlas gameAtlas, short... textureIds) {
        load(Valenguard.getInstance().getFileManager().getAtlas(gameAtlas), textureIds);
    }

    abstract void load(TextureAtlas textureAtlas, short[] textureIds);

    abstract TextureRegion[] actMoveUp(float stateTime);

    abstract TextureRegion[] actMoveDown(float stateTime);

    abstract TextureRegion[] actMoveLeft(float stateTime);

    abstract TextureRegion[] actMoveRight(float stateTime);

    public void animate(float delta, SpriteBatch spriteBatch) {

        if (MoveUtil.isEntityMoving(movingEntity)) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        TextureRegion[] frames = null;
        switch (movingEntity.getFacingDirection()) {
            case UP:
                frames = actMoveUp(stateTime);
                break;
            case DOWN:
                frames = actMoveDown(stateTime);
                break;
            case LEFT:
                frames = actMoveLeft(stateTime);
                break;
            case RIGHT:
                frames = actMoveRight(stateTime);
                break;
            case NONE:
                throw new RuntimeException("Facing direction cannot be NONE.");
        }

        for (TextureRegion frame : frames) {
            spriteBatch.draw(frame, movingEntity.getDrawX(), movingEntity.getDrawY());
        }
    }
}
