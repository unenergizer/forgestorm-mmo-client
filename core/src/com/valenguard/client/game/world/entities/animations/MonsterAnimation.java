package com.valenguard.client.game.world.entities.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.entities.MovingEntity;

import lombok.Getter;

public class MonsterAnimation extends EntityAnimation {

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
