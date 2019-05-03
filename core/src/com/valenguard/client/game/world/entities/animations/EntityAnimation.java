package com.valenguard.client.game.world.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.movement.MoveUtil;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;

public abstract class EntityAnimation {

    static final float WALK_INTERVAL = 0.25f;

    @Getter
    protected final MovingEntity movingEntity;

    final Appearance appearance;

    private float movingStateTime = 1f;
    private float idleStateTime = 1f;

    EntityAnimation(MovingEntity movingEntity) {
        this.movingEntity = movingEntity;
        this.appearance = movingEntity.getAppearance();
    }

    public void loadAll(GameAtlas gameAtlas) {
        load(Valenguard.getInstance().getFileManager().getAtlas(gameAtlas));
    }

    abstract void load(TextureAtlas textureAtlas);

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
            spriteBatch.draw(frame.getTextureRegion(), movingEntity.getDrawX(), movingEntity.getDrawY() + frame.getYAxisOffset());
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
        coloredTextureRegion.setYAxisOffset(0);
        return coloredTextureRegion;
    }

    protected ColoredTextureRegion getColorTextureRegion(Animation<TextureRegion> animation, float stateTime, boolean looping, int yAxisOffset) {
        ColoredTextureRegion coloredTextureRegion = new ColoredTextureRegion();
        coloredTextureRegion.setTextureRegion(animation.getKeyFrame(stateTime, looping));
        coloredTextureRegion.setYAxisOffset(yAxisOffset);
        return coloredTextureRegion;
    }


    protected ColoredTextureRegion getColorTextureRegion(Animation<TextureRegion> animation, float stateTime, boolean looping, Color color, int yAxisOffset) {
        ColoredTextureRegion coloredTextureRegion = getColorTextureRegion(animation, stateTime, looping, yAxisOffset);
        coloredTextureRegion.setRegionColor(color);
        coloredTextureRegion.setYAxisOffset(yAxisOffset);
        return coloredTextureRegion;
    }
}
