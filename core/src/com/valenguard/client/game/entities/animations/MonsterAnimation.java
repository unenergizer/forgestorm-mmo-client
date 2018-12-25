package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.entities.Appearance;
import com.valenguard.client.game.entities.MovingEntity;
import com.valenguard.client.util.RandomUtil;

import lombok.Getter;

public class MonsterAnimation extends EntityAnimation {

    private final int idleAnimationWaitMax = RandomUtil.getNewRandom(150, 500);
    private int idleAnimationWaitTime = 0;
    private int currentFramesRendered = 0;
    @Getter
    private short atlasId = 0;
    private Animation<TextureRegion> facingDown;

    public MonsterAnimation(MovingEntity movingEntity) {
        super(movingEntity);
    }

    @Override
    public void load(TextureAtlas textureAtlas) {
        this.atlasId = appearance.getTextureId(Appearance.BODY);
        facingDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("monster_down_" + atlasId), Animation.PlayMode.LOOP);
    }

    @Override
    ColoredTextureRegion[] actIdle(float stateTime) {
        return new ColoredTextureRegion[]{getColorTextureRegion(facingDown, stateTime, true)};
//        if (idleAnimationWaitTime >= idleAnimationWaitMax) {
//            if (currentFramesRendered >= 2) {
//                idleAnimationWaitTime = 0;
//                currentFramesRendered = 0;
//            } else {
//                currentFramesRendered++;
//            }
//            return new TextureRegion[]{getColorTextureRegion(facingDown, stateTime, true)};
//        } else {
//            idleAnimationWaitTime++;
//            return new TextureRegion[]{getColorTextureRegion(facingDown, 0, false)};
//        }
    }

    @Override
    ColoredTextureRegion[] actMoveUp(float stateTime) {
        return new ColoredTextureRegion[]{getColorTextureRegion(facingDown, stateTime, true)};
    }

    @Override
    ColoredTextureRegion[] actMoveDown(float stateTime) {
        return new ColoredTextureRegion[]{getColorTextureRegion(facingDown, stateTime, true)};
    }

    @Override
    ColoredTextureRegion[] actMoveLeft(float stateTime) {
        return new ColoredTextureRegion[]{getColorTextureRegion(facingDown, stateTime, true)};
    }

    @Override
    ColoredTextureRegion[] actMoveRight(float stateTime) {
        return new ColoredTextureRegion[]{getColorTextureRegion(facingDown, stateTime, true)};
    }
}
