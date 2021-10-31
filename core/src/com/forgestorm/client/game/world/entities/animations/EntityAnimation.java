package com.forgestorm.client.game.world.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.movement.MoveUtil;
import com.forgestorm.client.game.world.entities.Appearance;
import com.forgestorm.client.game.world.entities.MovingEntity;
import com.forgestorm.shared.io.type.GameAtlas;

import java.util.List;

import lombok.Getter;

public abstract class EntityAnimation {

    protected static final float WALK_INTERVAL = 0.25f;

    @Getter
    protected final MovingEntity movingEntity;

    protected final Appearance appearance;

    private float movingStateTime = 1f;
    private float idleStateTime = 1f;

    protected EntityAnimation(MovingEntity movingEntity) {
        this.movingEntity = movingEntity;
        this.appearance = movingEntity.getAppearance();
    }

    public void loadAll(GameAtlas gameAtlas) {
        load(ClientMain.getInstance().getFileManager().getAtlas(gameAtlas));
    }

    protected abstract void load(TextureAtlas textureAtlas);

    protected abstract List<ColoredTextureRegion> actIdle(float stateTime);

    protected abstract List<ColoredTextureRegion> actMoveNorth(float stateTime);

    protected abstract List<ColoredTextureRegion> actMoveSouth(float stateTime);

    protected abstract List<ColoredTextureRegion> actMoveWest(float stateTime);

    protected abstract List<ColoredTextureRegion> actMoveEast(float stateTime);

    public void animate(float delta, SpriteBatch spriteBatch) {
        List<ColoredTextureRegion> frameList;

        if (MoveUtil.isEntityMoving(movingEntity)) {
            movingStateTime += delta;
            idleStateTime = 0f;
            frameList = movingAnimation();
        } else {
            movingStateTime = 0f;
            idleStateTime += delta;
            frameList = idleAnimation();
        }

        final float CORRECTION = 0.005F;


        if (frameList == null) return;
        for (ColoredTextureRegion frame : frameList) {
            TextureRegion textureRegion = frame.getTextureRegion();
            spriteBatch.setColor(frame.getRegionColor());

            if (frame.getWidth() != 0 && frame.getHeight() != 0) {
                spriteBatch.draw(textureRegion, movingEntity.getDrawX() + frame.getXAxisOffset(), movingEntity.getDrawY() + frame.getYAxisOffset(),
                        frame.getWidth()+CORRECTION, frame.getHeight()+CORRECTION);
            } else {
                spriteBatch.draw(textureRegion, movingEntity.getDrawX(), movingEntity.getDrawY() + frame.getYAxisOffset(),
                        textureRegion.getRegionWidth()+CORRECTION, textureRegion.getRegionHeight()+CORRECTION);
            }
            spriteBatch.setColor(Color.WHITE);
        }

        frameList.clear();
    }

    private List<ColoredTextureRegion> movingAnimation() {
        switch (movingEntity.getFacingDirection()) {
            case NORTH:
                return actMoveNorth(movingStateTime);
            case SOUTH:
                return actMoveSouth(movingStateTime);
            case WEST:
                return actMoveWest(movingStateTime);
            case EAST:
                return actMoveEast(movingStateTime);
            case NONE:
                throw new RuntimeException("Facing direction cannot be NONE.");
            default:
                throw new RuntimeException("Moving animation cannot be null.");
        }
    }

    private List<ColoredTextureRegion> idleAnimation() {
        return actIdle(idleStateTime);
    }

    ColoredTextureRegion getColorTextureRegion(Animation<TextureRegion> animation, float stateTime, boolean looping) {
        ColoredTextureRegion coloredTextureRegion = new ColoredTextureRegion();
        coloredTextureRegion.setTextureRegion(animation.getKeyFrame(stateTime, looping));
        coloredTextureRegion.setYAxisOffset(0);
        return coloredTextureRegion;
    }

    private ColoredTextureRegion getColorTextureRegion(Animation<TextureRegion> animation, float stateTime, boolean looping, int yAxisOffset) {
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

    protected ColoredTextureRegion getColoredTextureRegion(TextureRegion textureRegion, Color color, int xAxisOffset, int yAxisOffset, float width, float height) {
        ColoredTextureRegion coloredTextureRegion = new ColoredTextureRegion();
        coloredTextureRegion.setTextureRegion(textureRegion);
        coloredTextureRegion.setRegionColor(color);
        coloredTextureRegion.setXAxisOffset(xAxisOffset);
        coloredTextureRegion.setYAxisOffset(yAxisOffset);
        coloredTextureRegion.setWidth(width);
        coloredTextureRegion.setHeight(height);
        return coloredTextureRegion;
    }
}
