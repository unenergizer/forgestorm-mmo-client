package com.valenguard.client.game.world.entities.animations;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.world.entities.MovingEntity;

import java.util.ArrayList;
import java.util.List;

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
        this.atlasId = appearance.getMonsterBodyTexture();
        facingDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("monster_down_" + atlasId), Animation.PlayMode.LOOP);
    }

    @Override
    List<ColoredTextureRegion> actIdle(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();
        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }

    @Override
    List<ColoredTextureRegion> actMoveUp(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();
        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }

    @Override
    List<ColoredTextureRegion> actMoveDown(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();
        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }

    @Override
    List<ColoredTextureRegion> actMoveLeft(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();
        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }

    @Override
    List<ColoredTextureRegion> actMoveRight(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();
        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }
}
