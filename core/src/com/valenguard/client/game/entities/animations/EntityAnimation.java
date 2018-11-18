package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.movement.MoveUtil;

public abstract class EntityAnimation {

    static final float WALK_INTERVAL = 0.25f;

    protected final MovingEntity movingEntity;

    private float movingStateTime = 1f;
    private float idleStateTime = 1f;

    EntityAnimation(MovingEntity movingEntity) {
        this.movingEntity = movingEntity;
    }

    public void loadAll(GameAtlas gameAtlas, short[] textureIds) {
        load(Valenguard.getInstance().getFileManager().getAtlas(gameAtlas), textureIds);
    }

    public void loadAllVarArgs(GameAtlas gameAtlas, short... textureIds) {
        load(Valenguard.getInstance().getFileManager().getAtlas(gameAtlas), textureIds);
    }

    abstract void load(TextureAtlas textureAtlas, short[] textureIds);

    abstract TextureRegion[] actIdle(float stateTime);

    abstract TextureRegion[] actMoveUp(float stateTime);

    abstract TextureRegion[] actMoveDown(float stateTime);

    abstract TextureRegion[] actMoveLeft(float stateTime);

    abstract TextureRegion[] actMoveRight(float stateTime);

    public void animate(float delta, SpriteBatch spriteBatch) {
        TextureRegion[] frames;

        if (MoveUtil.isEntityMoving(movingEntity)) {
            movingStateTime += delta;
            idleStateTime = 0f;
            frames = movingAnimation();
        } else {
            movingStateTime = 0f;
            idleStateTime += delta;
            frames = idleAnimation();
        }

        if (frames == null) return;
        for (TextureRegion frame : frames) {
            spriteBatch.draw(frame, movingEntity.getDrawX(), movingEntity.getDrawY());
        }
    }

    private TextureRegion[] movingAnimation() {
        switch (movingEntity.getFacingDirection()) {
            case NORTH:
                return actMoveUp(movingStateTime);
            case SOUTH:
                return actMoveDown(movingStateTime);
            case WEST:
                return actMoveLeft(movingStateTime);
            case EAST:
                return actMoveRight(movingStateTime);
            case NONE:
                throw new RuntimeException("Facing direction cannot be NONE.");
        }
        return null;
    }

    private TextureRegion[] idleAnimation() {
        return actIdle(idleStateTime);
    }
}
