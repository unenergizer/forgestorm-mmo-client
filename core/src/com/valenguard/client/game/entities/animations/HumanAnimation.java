package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.util.RandomUtil;

import lombok.Getter;
import lombok.Setter;

public class HumanAnimation extends EntityAnimation {

    @Getter
    private MovingEntity movingEntity;

    @Setter
    @Getter
    private short headId = 0;
    private Animation<TextureRegion> headDown;
    private Animation<TextureRegion> headUp;
    private Animation<TextureRegion> headLeft;
    private Animation<TextureRegion> headRight;
    private Animation<TextureRegion> nakedHeadDown;
    private Animation<TextureRegion> nakedHeadUp;
    private Animation<TextureRegion> nakedHeadLeft;
    private Animation<TextureRegion> nakedHeadRight;

    @Setter
    @Getter
    private short bodyId = 0;
    private Animation<TextureRegion> bodyDown;
    private Animation<TextureRegion> bodyUp;
    private Animation<TextureRegion> bodyLeft;
    private Animation<TextureRegion> bodyRight;
    private Animation<TextureRegion> nakedBodyDown;
    private Animation<TextureRegion> nakedBodyUp;
    private Animation<TextureRegion> nakedBodyLeft;
    private Animation<TextureRegion> nakedBodyRight;

    private final int idleAnimationWaitMax = RandomUtil.getNewRandom(150, 500);
    private int idleAnimationWaitTime = 0;
    private int currentFramesRendered = 0;

    public HumanAnimation(MovingEntity movingEntity) {
        super(movingEntity);
        this.movingEntity = movingEntity;
    }

    @Override
    public void load(TextureAtlas textureAtlas, short[] textureIds) {
        loadBody(textureAtlas, textureIds[1]);
        loadHead(textureAtlas, textureIds[0]);
    }

    public void loadBody(TextureAtlas textureAtlas, short textureId) {
        bodyId = textureId;

        nakedBodyDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_naked"), Animation.PlayMode.LOOP);
        nakedBodyUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_naked"), Animation.PlayMode.LOOP);
        nakedBodyLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_naked"), Animation.PlayMode.LOOP);
        nakedBodyRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_naked"), Animation.PlayMode.LOOP);

        bodyDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_" + bodyId), Animation.PlayMode.LOOP);
        bodyUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_" + bodyId), Animation.PlayMode.LOOP);
        bodyLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_" + bodyId), Animation.PlayMode.LOOP);
        bodyRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_" + bodyId), Animation.PlayMode.LOOP);
    }

    public void loadHead(TextureAtlas textureAtlas, short textureId) {
        headId = textureId;

        nakedHeadDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_down_naked"), Animation.PlayMode.LOOP);
        nakedHeadUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_up_naked"), Animation.PlayMode.LOOP);
        nakedHeadLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_left_naked"), Animation.PlayMode.LOOP);
        nakedHeadRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_right_naked"), Animation.PlayMode.LOOP);

        headDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_down_" + headId), Animation.PlayMode.LOOP);
        headUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_up_" + headId), Animation.PlayMode.LOOP);
        headLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_left_" + headId), Animation.PlayMode.LOOP);
        headRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_right_" + headId), Animation.PlayMode.LOOP);
    }

    @Override
    ColoredTextureRegion[] actIdle(float stateTime) {
        if (idleAnimationWaitTime >= idleAnimationWaitMax) {

            if (currentFramesRendered >= 4) {
                idleAnimationWaitTime = 0;
                currentFramesRendered = 0;
            } else {
                currentFramesRendered++;
            }

            switch (movingEntity.getFacingDirection()) {
                case NORTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadUp, stateTime, true, movingEntity.getBodyColor()),
                            getColorTextureRegion(nakedBodyUp, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(headUp, stateTime, true, Color.WHITE),
                            getColorTextureRegion(bodyUp, 0, false, Color.WHITE)
                    };
                case SOUTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadDown, stateTime, true, movingEntity.getBodyColor()),
                            getColorTextureRegion(nakedBodyDown, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(headDown, stateTime, true, Color.WHITE),
                            getColorTextureRegion(bodyDown, 0, false, Color.WHITE)
                    };
                case WEST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadLeft, stateTime, true, movingEntity.getBodyColor()),
                            getColorTextureRegion(nakedBodyLeft, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(headLeft, stateTime, true, Color.WHITE),
                            getColorTextureRegion(bodyLeft, 0, false, Color.WHITE)
                    };
                case EAST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadRight, stateTime, true, movingEntity.getBodyColor()),
                            getColorTextureRegion(nakedBodyRight, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(headRight, stateTime, true, Color.WHITE),
                            getColorTextureRegion(bodyRight, 0, false, Color.WHITE)
                    };
                case NONE:
                    return null;
            }
        } else {
            idleAnimationWaitTime++;
            switch (movingEntity.getFacingDirection()) {
                case NORTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadUp, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(nakedBodyUp, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(headUp, 0, false, Color.WHITE),
                            getColorTextureRegion(bodyUp, 0, false, Color.WHITE)
                    };
                case SOUTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadDown, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(nakedBodyDown, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(headDown, 0, false, Color.WHITE),
                            getColorTextureRegion(bodyDown, 0, false, Color.WHITE)
                    };
                case WEST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadLeft, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(nakedBodyLeft, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(headLeft, 0, false, Color.WHITE),
                            getColorTextureRegion(bodyLeft, 0, false, Color.WHITE)
                    };
                case EAST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadRight, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(nakedBodyRight, 0, false, movingEntity.getBodyColor()),
                            getColorTextureRegion(headRight, 0, false, Color.WHITE),
                            getColorTextureRegion(bodyRight, 0, false, Color.WHITE)
                    };
                case NONE:
                    return null;
            }
        }
        return null;
    }

    @Override
    ColoredTextureRegion[] actMoveUp(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadUp, stateTime, true, movingEntity.getBodyColor()),
                getColorTextureRegion(nakedBodyUp, stateTime, true, movingEntity.getBodyColor()),
                getColorTextureRegion(headUp, stateTime, true, Color.WHITE),
                getColorTextureRegion(bodyUp, stateTime, true, Color.WHITE)
        };
    }

    @Override
    ColoredTextureRegion[] actMoveDown(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadDown, stateTime, true, movingEntity.getBodyColor()),
                getColorTextureRegion(nakedBodyDown, stateTime, true, movingEntity.getBodyColor()),
                getColorTextureRegion(headDown, stateTime, true, Color.WHITE),
                getColorTextureRegion(bodyDown, stateTime, true, Color.WHITE)
        };
    }

    @Override
    ColoredTextureRegion[] actMoveLeft(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadLeft, stateTime, true, movingEntity.getBodyColor()),
                getColorTextureRegion(nakedBodyLeft, stateTime, true, movingEntity.getBodyColor()),
                getColorTextureRegion(headLeft, stateTime, true, Color.WHITE),
                getColorTextureRegion(bodyLeft, stateTime, true, Color.WHITE)
        };
    }

    @Override
    ColoredTextureRegion[] actMoveRight(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadRight, stateTime, true, movingEntity.getBodyColor()),
                getColorTextureRegion(nakedBodyRight, stateTime, true, movingEntity.getBodyColor()),
                getColorTextureRegion(headRight, stateTime, true, Color.WHITE),
                getColorTextureRegion(bodyRight, stateTime, true, Color.WHITE)
        };
    }

}
