package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.util.RandomUtil;

import lombok.Getter;
import lombok.Setter;

public class HumanAnimation extends EntityAnimation {

    @Setter
    @Getter
    private short headId = 0;
    private Animation<TextureRegion> headDown;
    private Animation<TextureRegion> headUp;
    private Animation<TextureRegion> headLeft;
    private Animation<TextureRegion> headRight;

    @Setter
    @Getter
    private short bodyId = 0;
    private Animation<TextureRegion> bodyDown;
    private Animation<TextureRegion> bodyUp;
    private Animation<TextureRegion> bodyLeft;
    private Animation<TextureRegion> bodyRight;

    private final int idleAnimationWaitMax = RandomUtil.getNewRandom(150, 500);
    private int idleAnimationWaitTime = 0;
    private int currentFramesRendered = 0;

    public HumanAnimation(MovingEntity movingEntity) {
        super(movingEntity);
    }

    @Override
    public void load(TextureAtlas textureAtlas, short[] textureIds) {
        this.headId = textureIds[0];
        this.bodyId = textureIds[1];

        headDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_down_" + headId), Animation.PlayMode.LOOP);
        headUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_up_" + headId), Animation.PlayMode.LOOP);
        headLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_left_" + headId), Animation.PlayMode.LOOP);
        headRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_right_" + headId), Animation.PlayMode.LOOP);

        bodyDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_" + bodyId), Animation.PlayMode.LOOP);
        bodyUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_" + bodyId), Animation.PlayMode.LOOP);
        bodyLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_" + bodyId), Animation.PlayMode.LOOP);
        bodyRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_" + bodyId), Animation.PlayMode.LOOP);
    }

    @Override
    TextureRegion[] actIdle(float stateTime) {
        if (idleAnimationWaitTime >= idleAnimationWaitMax) {

            if (currentFramesRendered >= 4) {
                idleAnimationWaitTime = 0;
                currentFramesRendered = 0;
            } else {
                currentFramesRendered++;
            }

            switch (movingEntity.getFacingDirection()) {
                case NORTH:
                    return new TextureRegion[]{
                            headUp.getKeyFrame(stateTime, true),
                            bodyUp.getKeyFrame(0, false)
                    };
                case SOUTH:
                    return new TextureRegion[]{
                            headDown.getKeyFrame(stateTime, true),
                            bodyDown.getKeyFrame(0, false)
                    };
                case WEST:
                    return new TextureRegion[]{
                            headLeft.getKeyFrame(stateTime, true),
                            bodyLeft.getKeyFrame(0, false)
                    };
                case EAST:
                    return new TextureRegion[]{
                            headRight.getKeyFrame(stateTime, true),
                            bodyRight.getKeyFrame(0, false)
                    };
                case NONE:
                    return null;
            }
        } else {
            idleAnimationWaitTime++;
            switch (movingEntity.getFacingDirection()) {
                case NORTH:
                    return new TextureRegion[]{
                            headUp.getKeyFrame(0, false),
                            bodyUp.getKeyFrame(0, false)
                    };
                case SOUTH:
                    return new TextureRegion[]{
                            headDown.getKeyFrame(0, false),
                            bodyDown.getKeyFrame(0, false)
                    };
                case WEST:
                    return new TextureRegion[]{
                            headLeft.getKeyFrame(0, false),
                            bodyLeft.getKeyFrame(0, false)
                    };
                case EAST:
                    return new TextureRegion[]{
                            headRight.getKeyFrame(0, false),
                            bodyRight.getKeyFrame(0, false)
                    };
                case NONE:
                    return null;
            }
        }
        return null;
    }

    @Override
    TextureRegion[] actMoveUp(float stateTime) {
        return new TextureRegion[]{
                headUp.getKeyFrame(stateTime, true),
                bodyUp.getKeyFrame(stateTime, true)
        };
    }

    @Override
    TextureRegion[] actMoveDown(float stateTime) {
        return new TextureRegion[]{
                headDown.getKeyFrame(stateTime, true),
                bodyDown.getKeyFrame(stateTime, true)
        };
    }

    @Override
    TextureRegion[] actMoveLeft(float stateTime) {
        return new TextureRegion[]{
                headLeft.getKeyFrame(stateTime, true),
                bodyLeft.getKeyFrame(stateTime, true)
        };
    }

    @Override
    TextureRegion[] actMoveRight(float stateTime) {
        return new TextureRegion[]{
                headRight.getKeyFrame(stateTime, true),
                bodyRight.getKeyFrame(stateTime, true)
        };
    }

}
