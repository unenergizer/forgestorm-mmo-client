package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.game.entities.Player;
import com.valenguard.client.util.RandomUtil;

import lombok.Getter;
import lombok.Setter;

public class HumanAnimation extends EntityAnimation {

    @Getter
    private Color bodyDrawColor;

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
        bodyDrawColor = ((Player) movingEntity).getBodyColor();
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
                            getColorTextureRegion(nakedHeadUp, stateTime, true, bodyDrawColor),
                            getColorTextureRegion(nakedBodyUp, 0, false, bodyDrawColor),
                            getColorTextureRegion(headUp, stateTime, true),
                            getColorTextureRegion(bodyUp, 0, false)
                    };
                case SOUTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadDown, stateTime, true, bodyDrawColor),
                            getColorTextureRegion(nakedBodyDown, 0, false, bodyDrawColor),
                            getColorTextureRegion(headDown, stateTime, true),
                            getColorTextureRegion(bodyDown, 0, false)
                    };
                case WEST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadLeft, stateTime, true, bodyDrawColor),
                            getColorTextureRegion(nakedBodyLeft, 0, false, bodyDrawColor),
                            getColorTextureRegion(headLeft, stateTime, true),
                            getColorTextureRegion(bodyLeft, 0, false)
                    };
                case EAST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadRight, stateTime, true, bodyDrawColor),
                            getColorTextureRegion(nakedBodyRight, 0, false, bodyDrawColor),
                            getColorTextureRegion(headRight, stateTime, true),
                            getColorTextureRegion(bodyRight, 0, false)
                    };
                case NONE:
                    return null;
            }
        } else {
            idleAnimationWaitTime++;
            switch (movingEntity.getFacingDirection()) {
                case NORTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadUp, 0, false, bodyDrawColor),
                            getColorTextureRegion(nakedBodyUp, 0, false, bodyDrawColor),
                            getColorTextureRegion(headUp, 0, false),
                            getColorTextureRegion(bodyUp, 0, false)
                    };
                case SOUTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadDown, 0, false, bodyDrawColor),
                            getColorTextureRegion(nakedBodyDown, 0, false, bodyDrawColor),
                            getColorTextureRegion(headDown, 0, false),
                            getColorTextureRegion(bodyDown, 0, false)
                    };
                case WEST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadLeft, 0, false, bodyDrawColor),
                            getColorTextureRegion(nakedBodyLeft, 0, false, bodyDrawColor),
                            getColorTextureRegion(headLeft, 0, false),
                            getColorTextureRegion(bodyLeft, 0, false)
                    };
                case EAST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadRight, 0, false, bodyDrawColor),
                            getColorTextureRegion(nakedBodyRight, 0, false, bodyDrawColor),
                            getColorTextureRegion(headRight, 0, false),
                            getColorTextureRegion(bodyRight, 0, false)
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
                getColorTextureRegion(nakedHeadUp, stateTime, true, bodyDrawColor),
                getColorTextureRegion(nakedBodyUp, stateTime, true, bodyDrawColor),
                getColorTextureRegion(headUp, stateTime, true),
                getColorTextureRegion(bodyUp, stateTime, true)
        };
    }

    @Override
    ColoredTextureRegion[] actMoveDown(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadDown, stateTime, true, bodyDrawColor),
                getColorTextureRegion(nakedBodyDown, stateTime, true, bodyDrawColor),
                getColorTextureRegion(headDown, stateTime, true),
                getColorTextureRegion(bodyDown, stateTime, true)
        };
    }

    @Override
    ColoredTextureRegion[] actMoveLeft(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadLeft, stateTime, true, bodyDrawColor),
                getColorTextureRegion(nakedBodyLeft, stateTime, true, bodyDrawColor),
                getColorTextureRegion(headLeft, stateTime, true),
                getColorTextureRegion(bodyLeft, stateTime, true)
        };
    }

    @Override
    ColoredTextureRegion[] actMoveRight(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadRight, stateTime, true, bodyDrawColor),
                getColorTextureRegion(nakedBodyRight, stateTime, true, bodyDrawColor),
                getColorTextureRegion(headRight, stateTime, true),
                getColorTextureRegion(bodyRight, stateTime, true)
        };
    }

}
