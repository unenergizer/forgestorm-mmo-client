package com.valenguard.client.game.world.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.util.RandomUtil;

import lombok.Getter;
import lombok.Setter;

import static com.valenguard.client.util.Log.println;
import static com.valenguard.client.util.RandomUtil.getNewRandom;

public class HumanAnimation extends EntityAnimation {

    private short pantsId = (short) RandomUtil.getNewRandom(0, 60);
    private short shoesId = (short) RandomUtil.getNewRandom(0, 60);

    @Setter
    @Getter
    private short headId = -1;
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
    private short chestId = -1;
    private Animation<TextureRegion> bodyChestDown;
    private Animation<TextureRegion> bodyChestUp;
    private Animation<TextureRegion> bodyChestLeft;
    private Animation<TextureRegion> bodyChestRight;

    private Animation<TextureRegion> bodyPantsDown;
    private Animation<TextureRegion> bodyPantsUp;
    private Animation<TextureRegion> bodyPantsLeft;
    private Animation<TextureRegion> bodyPantsRight;

    private Animation<TextureRegion> bodyShoesDown;
    private Animation<TextureRegion> bodyShoesUp;
    private Animation<TextureRegion> bodyShoesLeft;
    private Animation<TextureRegion> bodyShoesRight;

    private Animation<TextureRegion> bodyBorderDown;
    private Animation<TextureRegion> bodyBorderUp;
    private Animation<TextureRegion> bodyBorderLeft;
    private Animation<TextureRegion> bodyBorderRight;

    private final int idleAnimationWaitMax = getNewRandom(150, 500);
    private int idleAnimationWaitTime = 0;
    private int currentFramesRendered = 0;

    public HumanAnimation(MovingEntity movingEntity) {
        super(movingEntity);
    }

    @Override
    public void load(TextureAtlas textureAtlas) {
        println(getClass(), "Loading textures");
        loadHead(textureAtlas, appearance.getTextureId(Appearance.HEAD));

        if (appearance.getTextureId(Appearance.CHEST) != -1) {
            println(getClass(), "Chest present");
            loadChest(textureAtlas, appearance.getTextureId(Appearance.CHEST));
        } else {
            println(getClass(), "Chest naked");
            loadNakedChest(textureAtlas);
        }

        if (appearance.getTextureId(Appearance.PANTS) != -1) {
            println(getClass(), "Pants present");
            loadPants(textureAtlas, appearance.getTextureId(Appearance.PANTS));
        } else {
            println(getClass(), "Pants naked");
            loadNakedPants(textureAtlas);
        }

        if (appearance.getTextureId(Appearance.SHOES) != -1) {
            println(getClass(), "Shoes present");
            loadShoes(textureAtlas, appearance.getTextureId(Appearance.SHOES));
        } else {
            println(getClass(), "Shoes naked");
            loadNakedShoes(textureAtlas);
        }

        println(getClass(), "Loading border");
        loadBorder(textureAtlas);

        println(getClass(), "All textures loaded!");
    }

    public void loadHead(TextureAtlas textureAtlas, short headId) {
        nakedHeadDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_down_naked"), Animation.PlayMode.LOOP);
        nakedHeadUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_up_naked"), Animation.PlayMode.LOOP);
        nakedHeadLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_left_naked"), Animation.PlayMode.LOOP);
        nakedHeadRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_right_naked"), Animation.PlayMode.LOOP);

        headDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_down_" + headId), Animation.PlayMode.LOOP);
        headUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_up_" + headId), Animation.PlayMode.LOOP);
        headLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_left_" + headId), Animation.PlayMode.LOOP);
        headRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_right_" + headId), Animation.PlayMode.LOOP);
    }

    public void loadChest(TextureAtlas textureAtlas, short chestId) {
        bodyChestDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_chest_" + chestId), Animation.PlayMode.LOOP);
        bodyChestUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_chest_" + chestId), Animation.PlayMode.LOOP);
        bodyChestLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_chest_" + chestId), Animation.PlayMode.LOOP);
        bodyChestRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_chest_" + chestId), Animation.PlayMode.LOOP);
    }

    public void loadPants(TextureAtlas textureAtlas, short pantsId) {
        bodyPantsDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_pants_" + pantsId), Animation.PlayMode.LOOP);
        bodyPantsUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_pants_" + pantsId), Animation.PlayMode.LOOP);
        bodyPantsLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_pants_" + pantsId), Animation.PlayMode.LOOP);
        bodyPantsRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_pants_" + pantsId), Animation.PlayMode.LOOP);
    }

    public void loadShoes(TextureAtlas textureAtlas, short shoesId) {
        bodyShoesDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_shoes_" + shoesId), Animation.PlayMode.LOOP);
        bodyShoesUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_shoes_" + shoesId), Animation.PlayMode.LOOP);
        bodyShoesLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_shoes_" + shoesId), Animation.PlayMode.LOOP);
        bodyShoesRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_shoes_" + shoesId), Animation.PlayMode.LOOP);
    }

    public void loadNakedChest(TextureAtlas textureAtlas) {
        bodyChestDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_chest_naked"), Animation.PlayMode.LOOP);
        bodyChestUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_chest_naked"), Animation.PlayMode.LOOP);
        bodyChestLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_chest_naked"), Animation.PlayMode.LOOP);
        bodyChestRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_chest_naked"), Animation.PlayMode.LOOP);
    }

    public void loadNakedPants(TextureAtlas textureAtlas) {
        bodyPantsDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_pants_naked"), Animation.PlayMode.LOOP);
        bodyPantsUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_pants_naked"), Animation.PlayMode.LOOP);
        bodyPantsLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_pants_naked"), Animation.PlayMode.LOOP);
        bodyPantsRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_pants_naked"), Animation.PlayMode.LOOP);
    }

    public void loadNakedShoes(TextureAtlas textureAtlas) {
        bodyShoesDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_shoes_naked"), Animation.PlayMode.LOOP);
        bodyShoesUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_shoes_naked"), Animation.PlayMode.LOOP);
        bodyShoesLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_shoes_naked"), Animation.PlayMode.LOOP);
        bodyShoesRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_shoes_naked"), Animation.PlayMode.LOOP);
    }

    public void loadBorder(TextureAtlas textureAtlas) {
        bodyBorderDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_0"), Animation.PlayMode.LOOP);
        bodyBorderUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_0"), Animation.PlayMode.LOOP);
        bodyBorderLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_0"), Animation.PlayMode.LOOP);
        bodyBorderRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_0"), Animation.PlayMode.LOOP);
    }

    @Override
    ColoredTextureRegion[] actIdle(float stateTime) {

        Color chestColor = determinColor(Appearance.CHEST);
        Color pantsColor = determinColor(Appearance.PANTS);
        Color shoesColor = determinColor(Appearance.SHOES);

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
                            getColorTextureRegion(nakedHeadUp, stateTime, true, appearance.getColor(), 0),
                            getColorTextureRegion(bodyBorderUp, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(headUp, stateTime, true, Color.WHITE, 0),
                            getColorTextureRegion(bodyChestUp, 0, false, chestColor, 3),
                            getColorTextureRegion(bodyPantsUp, 0, false, pantsColor, 1),
                            getColorTextureRegion(bodyShoesUp, 0, false, shoesColor, 0),
                    };
                case SOUTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadDown, stateTime, true, appearance.getColor(), 0),
                            getColorTextureRegion(bodyBorderDown, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(headDown, stateTime, true, Color.WHITE, 0),
                            getColorTextureRegion(bodyChestDown, 0, false, chestColor, 3),
                            getColorTextureRegion(bodyPantsDown, 0, false, pantsColor, 1),
                            getColorTextureRegion(bodyShoesDown, 0, false, shoesColor, 0),
                    };
                case WEST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadLeft, stateTime, true, appearance.getColor(), 0),
                            getColorTextureRegion(bodyBorderLeft, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(headLeft, stateTime, true, Color.WHITE, 0),
                            getColorTextureRegion(bodyChestLeft, 0, false, chestColor, 3),
                            getColorTextureRegion(bodyPantsLeft, 0, false, pantsColor, 1),
                            getColorTextureRegion(bodyShoesLeft, 0, false, shoesColor, 0),
                    };
                case EAST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadRight, stateTime, true, appearance.getColor(), 0),
                            getColorTextureRegion(bodyBorderRight, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(headRight, stateTime, true, Color.WHITE, 0),
                            getColorTextureRegion(bodyChestRight, 0, false, chestColor, 3),
                            getColorTextureRegion(bodyPantsRight, 0, false, pantsColor, 1),
                            getColorTextureRegion(bodyShoesRight, 0, false, shoesColor, 0),
                    };
                case NONE:
                    return null;
            }
        } else {
            idleAnimationWaitTime++;
            switch (movingEntity.getFacingDirection()) {
                case NORTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadUp, 0, false, appearance.getColor(), 0),
                            getColorTextureRegion(bodyBorderUp, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(headUp, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(bodyChestUp, 0, false, chestColor, 3),
                            getColorTextureRegion(bodyPantsUp, 0, false, pantsColor, 1),
                            getColorTextureRegion(bodyShoesUp, 0, false, shoesColor, 0),
                    };
                case SOUTH:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadDown, 0, false, appearance.getColor(), 0),
                            getColorTextureRegion(bodyBorderDown, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(headDown, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(bodyChestDown, 0, false, chestColor, 3),
                            getColorTextureRegion(bodyPantsDown, 0, false, pantsColor, 1),
                            getColorTextureRegion(bodyShoesDown, 0, false, shoesColor, 0),
                    };
                case WEST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadLeft, 0, false, appearance.getColor(), 0),
                            getColorTextureRegion(bodyBorderLeft, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(headLeft, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(bodyChestLeft, 0, false, chestColor, 3),
                            getColorTextureRegion(bodyPantsLeft, 0, false, pantsColor, 1),
                            getColorTextureRegion(bodyShoesLeft, 0, false, shoesColor, 0),
                    };
                case EAST:
                    return new ColoredTextureRegion[]{
                            getColorTextureRegion(nakedHeadRight, 0, false, appearance.getColor(), 0),
                            getColorTextureRegion(bodyBorderRight, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(headRight, 0, false, Color.WHITE, 0),
                            getColorTextureRegion(bodyChestRight, 0, false, chestColor, 3),
                            getColorTextureRegion(bodyPantsRight, 0, false, pantsColor, 1),
                            getColorTextureRegion(bodyShoesRight, 0, false, shoesColor, 0),
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
                getColorTextureRegion(nakedHeadUp, stateTime, true, appearance.getColor(), 0),
                getColorTextureRegion(bodyBorderUp, stateTime, true, Color.WHITE, 0),
                getColorTextureRegion(headUp, stateTime, true, Color.WHITE, 0),
                getColorTextureRegion(bodyChestUp, stateTime, true, determinColor(Appearance.CHEST), 3 + josephsCrazyFix(stateTime)),
                getColorTextureRegion(bodyPantsUp, stateTime, true, determinColor(Appearance.PANTS), 1),
                getColorTextureRegion(bodyShoesUp, stateTime, true, determinColor(Appearance.SHOES), 0),
        };
    }

    @Override
    ColoredTextureRegion[] actMoveDown(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadDown, stateTime, true, appearance.getColor(), 0),
                getColorTextureRegion(bodyBorderDown, stateTime, true, Color.WHITE, 0),
                getColorTextureRegion(headDown, stateTime, true, Color.WHITE, 0),
                getColorTextureRegion(bodyChestDown, stateTime, true, determinColor(Appearance.CHEST), 3 + josephsCrazyFix(stateTime)),
                getColorTextureRegion(bodyPantsDown, stateTime, true, determinColor(Appearance.PANTS), 1),
                getColorTextureRegion(bodyShoesDown, stateTime, true, determinColor(Appearance.SHOES), 0),
        };
    }

    @Override
    ColoredTextureRegion[] actMoveLeft(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadLeft, stateTime, true, appearance.getColor(), 0),
                getColorTextureRegion(bodyBorderLeft, stateTime, true, Color.WHITE, 0),
                getColorTextureRegion(headLeft, stateTime, true, Color.WHITE, 0),
                getColorTextureRegion(bodyChestLeft, stateTime, true, determinColor(Appearance.CHEST), 3 + josephsCrazyFix(stateTime)),
                getColorTextureRegion(bodyPantsLeft, stateTime, true, determinColor(Appearance.PANTS), 1),
                getColorTextureRegion(bodyShoesLeft, stateTime, true, determinColor(Appearance.SHOES), 0),
        };
    }

    @Override
    ColoredTextureRegion[] actMoveRight(float stateTime) {
        return new ColoredTextureRegion[]{
                getColorTextureRegion(nakedHeadRight, stateTime, true, appearance.getColor(), 0),
                getColorTextureRegion(bodyBorderRight, stateTime, true, Color.WHITE, 0),
                getColorTextureRegion(headRight, stateTime, true, Color.WHITE, 0),
                getColorTextureRegion(bodyChestRight, stateTime, true, determinColor(Appearance.CHEST), 3 + josephsCrazyFix(stateTime)),
                getColorTextureRegion(bodyPantsRight, stateTime, true, determinColor(Appearance.PANTS), 1),
                getColorTextureRegion(bodyShoesRight, stateTime, true, determinColor(Appearance.SHOES), 0),
        };
    }

    private Color determinColor(int slot) {
        return appearance.getTextureId(slot) == -1 ? appearance.getColor() : Color.WHITE;
    }

    private int josephsCrazyFix(float stateTime) {
        float test = stateTime - (int) stateTime;

        if (test >= 0 && test < .25f) {
            return 0;
        } else if (test >= .25f && test < .50f) {
            return 1;
        } else if (test >= .50f && test < .75f) {
            return 0;
        } else {
            return 1;
        }
    }
}
