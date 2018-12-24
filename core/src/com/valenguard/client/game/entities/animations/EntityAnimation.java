package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
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

    abstract ColoredTextureRegion[] actIdle(float stateTime);

    abstract ColoredTextureRegion[] actMoveUp(float stateTime);

    abstract ColoredTextureRegion[] actMoveDown(float stateTime);

    abstract ColoredTextureRegion[] actMoveLeft(float stateTime);

    abstract ColoredTextureRegion[] actMoveRight(float stateTime);

    public void animate(float delta, SpriteBatch spriteBatch) {
        ColoredTextureRegion[] frames;

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
        for (ColoredTextureRegion frame : frames) {
            spriteBatch.setColor(frame.getRegionColor());
            spriteBatch.draw(frame.getTextureRegion(), movingEntity.getDrawX(), movingEntity.getDrawY());
            spriteBatch.setColor(Color.WHITE);
        }
    }

    private ColoredTextureRegion[] movingAnimation() {
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

    private ColoredTextureRegion[] idleAnimation() {
        return actIdle(idleStateTime);
    }

    protected ColoredTextureRegion getColorTextureRegion(Animation<TextureRegion> animation, float stateTime, boolean looping) {
        ColoredTextureRegion coloredTextureRegion = new ColoredTextureRegion();
        coloredTextureRegion.setTextureRegion(animation.getKeyFrame(stateTime, looping));
        return coloredTextureRegion;
    }


    protected ColoredTextureRegion getColorTextureRegion(Animation<TextureRegion> animation, float stateTime, boolean looping, Color color) {
        ColoredTextureRegion coloredTextureRegion = getColorTextureRegion(animation, stateTime, looping);
        coloredTextureRegion.setRegionColor(color);
        return coloredTextureRegion;
    }
}
