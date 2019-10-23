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
    protected void load(TextureAtlas textureAtlas) {
        this.atlasId = appearance.getMonsterBodyTexture();
        facingDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("monster_down_" + atlasId), Animation.PlayMode.LOOP);
    }

    @Override
    protected List<ColoredTextureRegion> actIdle(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }

    @Override
    protected List<ColoredTextureRegion> actMoveNorth(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }

    @Override
    protected List<ColoredTextureRegion> actMoveSouth(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }

    @Override
    protected List<ColoredTextureRegion> actMoveWest(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }

    @Override
    protected List<ColoredTextureRegion> actMoveEast(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(facingDown, stateTime, true));
        return frameList;
    }
}
