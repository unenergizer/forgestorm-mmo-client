package com.valenguard.client.game.entities.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.entities.MovingEntity;

import lombok.Getter;

public class MonsterAnimation extends EntityAnimation {

    @Getter
    private short atlasId = 0;
    private Animation<TextureRegion> facingDown;

    public MonsterAnimation(MovingEntity movingEntity) {
        super(movingEntity);
    }

    @Override
    public void load(TextureAtlas textureAtlas, short[] textureIds) {
        this.atlasId = textureIds[0];
        facingDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("monster_down_" + atlasId), Animation.PlayMode.LOOP);
    }

    @Override
    TextureRegion[] actMoveUp(float stateTime) {
        return new TextureRegion[]{facingDown.getKeyFrame(stateTime, true)};
    }

    @Override
    TextureRegion[] actMoveDown(float stateTime) {
        return new TextureRegion[]{facingDown.getKeyFrame(stateTime, true)};
    }

    @Override
    TextureRegion[] actMoveLeft(float stateTime) {
        return new TextureRegion[]{facingDown.getKeyFrame(stateTime, true)};
    }

    @Override
    TextureRegion[] actMoveRight(float stateTime) {
        return new TextureRegion[]{facingDown.getKeyFrame(stateTime, true)};
    }
}
