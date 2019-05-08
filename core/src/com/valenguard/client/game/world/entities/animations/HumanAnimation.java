package com.valenguard.client.game.world.entities.animations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.game.world.entities.MovingEntity;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

import static com.valenguard.client.util.RandomUtil.getNewRandom;

public class HumanAnimation extends EntityAnimation {

    //TODO:  left ring, right ring

    @Setter
    boolean showHelm = false;
    @Setter
    boolean showChest = false;
    @Setter
    boolean showPants = false;
    @Setter
    boolean showShoes = false;
    @Setter
    boolean showGloves = true;

    // Eyes (eyesUp is not a thing!)
    private Animation<TextureRegion> eyesDown;
    private Animation<TextureRegion> eyesLeft;
    private Animation<TextureRegion> eyesRight;

    // Hair
    private Animation<TextureRegion> hairDown;
    private Animation<TextureRegion> hairUp;
    private Animation<TextureRegion> hairLeft;
    private Animation<TextureRegion> hairRight;

    private Animation<TextureRegion> hairDownBorder;
    private Animation<TextureRegion> hairUpBorder;
    private Animation<TextureRegion> hairLeftBorder;
    private Animation<TextureRegion> hairRightBorder;

    // Clothing parts
    private Animation<TextureRegion> helmDown;
    private Animation<TextureRegion> helmUp;
    private Animation<TextureRegion> helmLeft;
    private Animation<TextureRegion> helmRight;

    private Animation<TextureRegion> helmDownBorder;
    private Animation<TextureRegion> helmUpBorder;
    private Animation<TextureRegion> helmLeftBorder;
    private Animation<TextureRegion> helmRightBorder;

    private Animation<TextureRegion> chestDown;
    private Animation<TextureRegion> chestUp;
    private Animation<TextureRegion> chestLeft;
    private Animation<TextureRegion> chestRight;

    private Animation<TextureRegion> pantsDown;
    private Animation<TextureRegion> pantsUp;
    private Animation<TextureRegion> pantsLeft;
    private Animation<TextureRegion> pantsRight;

    private Animation<TextureRegion> shoesDown;
    private Animation<TextureRegion> shoesUp;
    private Animation<TextureRegion> shoesLeft;
    private Animation<TextureRegion> shoesRight;

    private Animation<TextureRegion> glovesDown;
    private Animation<TextureRegion> glovesUp;
    private Animation<TextureRegion> glovesLeft;
    private Animation<TextureRegion> glovesRight;

    // Naked body
    private Animation<TextureRegion> headDownNaked;
    private Animation<TextureRegion> headUpNaked;
    private Animation<TextureRegion> headLeftNaked;
    private Animation<TextureRegion> headRightNaked;

    private Animation<TextureRegion> chestDownNaked;
    private Animation<TextureRegion> chestUpNaked;
    private Animation<TextureRegion> chestLeftNaked;
    private Animation<TextureRegion> chestRightNaked;

    private Animation<TextureRegion> pantsDownNaked;
    private Animation<TextureRegion> pantsUpNaked;
    private Animation<TextureRegion> pantsLeftNaked;
    private Animation<TextureRegion> pantsRightNaked;

    private Animation<TextureRegion> shoesDownNaked;
    private Animation<TextureRegion> shoesUpNaked;
    private Animation<TextureRegion> shoesLeftNaked;
    private Animation<TextureRegion> shoesRightNaked;

    // Border
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
        loadNakedParts(textureAtlas);
        loadEyes(textureAtlas);
        loadHair(textureAtlas, appearance.getHairTexture());
        if (appearance.getHelmTexture() != -1) {
            loadHelm(textureAtlas, appearance.getHelmTexture());
            setShowHelm(true);
        }
        if (appearance.getChestTexture() != -1) {
            loadChest(textureAtlas, appearance.getChestTexture());
            setShowChest(true);
        }
        if (appearance.getPantsTexture() != -1) {
            loadPants(textureAtlas, appearance.getPantsTexture());
            setShowPants(true);
        }
        if (appearance.getShoesTexture() != -1) {
            loadShoes(textureAtlas, appearance.getShoesTexture());
            setShowShoes(true);
        }
        loadGloves(textureAtlas);
        loadBorder(textureAtlas);
    }

    public void loadEyes(TextureAtlas textureAtlas) {
        eyesDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("eyes_down"), Animation.PlayMode.LOOP);
        eyesLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("eyes_left"), Animation.PlayMode.LOOP);
        eyesRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("eyes_right"), Animation.PlayMode.LOOP);
    }

    public void loadHair(TextureAtlas textureAtlas, short hairId) {
        hairDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("hair_down_" + hairId), Animation.PlayMode.LOOP);
        hairUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("hair_up_" + hairId), Animation.PlayMode.LOOP);
        hairLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("hair_left_" + hairId), Animation.PlayMode.LOOP);
        hairRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("hair_right_" + hairId), Animation.PlayMode.LOOP);

        hairDownBorder = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("hair_border_down_" + hairId), Animation.PlayMode.LOOP);
        hairUpBorder = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("hair_border_up_" + hairId), Animation.PlayMode.LOOP);
        hairLeftBorder = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("hair_border_left_" + hairId), Animation.PlayMode.LOOP);
        hairRightBorder = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("hair_border_right_" + hairId), Animation.PlayMode.LOOP);
    }

    public void loadHelm(TextureAtlas textureAtlas, short helmId) {
        helmDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("helm_down_" + helmId), Animation.PlayMode.LOOP);
        helmUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("helm_up_" + helmId), Animation.PlayMode.LOOP);
        helmLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("helm_left_" + helmId), Animation.PlayMode.LOOP);
        helmRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("helm_right_" + helmId), Animation.PlayMode.LOOP);

        helmDownBorder = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("helm_border_down_" + helmId), Animation.PlayMode.LOOP);
        helmUpBorder = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("helm_border_up_" + helmId), Animation.PlayMode.LOOP);
        helmLeftBorder = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("helm_border_left_" + helmId), Animation.PlayMode.LOOP);
        helmRightBorder = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("helm_border_right_" + helmId), Animation.PlayMode.LOOP);
    }

    public void loadChest(TextureAtlas textureAtlas, short chestId) {
        chestDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("chest_down_" + chestId), Animation.PlayMode.LOOP);
        chestUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("chest_up_" + chestId), Animation.PlayMode.LOOP);
        chestLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("chest_left_" + chestId), Animation.PlayMode.LOOP);
        chestRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("chest_right_" + chestId), Animation.PlayMode.LOOP);
    }

    public void loadPants(TextureAtlas textureAtlas, short pantsId) {
        pantsDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("pants_down_" + pantsId), Animation.PlayMode.LOOP);
        pantsUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("pants_up_" + pantsId), Animation.PlayMode.LOOP);
        pantsLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("pants_left_" + pantsId), Animation.PlayMode.LOOP);
        pantsRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("pants_right_" + pantsId), Animation.PlayMode.LOOP);
    }

    public void loadShoes(TextureAtlas textureAtlas, short shoesId) {
        shoesDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("shoes_down_" + shoesId), Animation.PlayMode.LOOP);
        shoesUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("shoes_up_" + shoesId), Animation.PlayMode.LOOP);
        shoesLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("shoes_left_" + shoesId), Animation.PlayMode.LOOP);
        shoesRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("shoes_right_" + shoesId), Animation.PlayMode.LOOP);
    }

    public void loadGloves(TextureAtlas textureAtlas) {
        glovesDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("gloves_down"), Animation.PlayMode.LOOP);
        glovesUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("gloves_up"), Animation.PlayMode.LOOP);
        glovesLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("gloves_left"), Animation.PlayMode.LOOP);
        glovesRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("gloves_right"), Animation.PlayMode.LOOP);
    }

    public void loadNakedParts(TextureAtlas textureAtlas) {
        headDownNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_down_naked"), Animation.PlayMode.LOOP);
        headUpNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_up_naked"), Animation.PlayMode.LOOP);
        headLeftNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_left_naked"), Animation.PlayMode.LOOP);
        headRightNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_right_naked"), Animation.PlayMode.LOOP);

        chestDownNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("chest_down_naked"), Animation.PlayMode.LOOP);
        chestUpNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("chest_up_naked"), Animation.PlayMode.LOOP);
        chestLeftNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("chest_left_naked"), Animation.PlayMode.LOOP);
        chestRightNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("chest_right_naked"), Animation.PlayMode.LOOP);

        pantsDownNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("pants_down_naked"), Animation.PlayMode.LOOP);
        pantsUpNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("pants_up_naked"), Animation.PlayMode.LOOP);
        pantsLeftNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("pants_left_naked"), Animation.PlayMode.LOOP);
        pantsRightNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("pants_right_naked"), Animation.PlayMode.LOOP);

        shoesDownNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("shoes_down_naked"), Animation.PlayMode.LOOP);
        shoesUpNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("shoes_up_naked"), Animation.PlayMode.LOOP);
        shoesLeftNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("shoes_left_naked"), Animation.PlayMode.LOOP);
        shoesRightNaked = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("shoes_right_naked"), Animation.PlayMode.LOOP);
    }

    public void loadBorder(TextureAtlas textureAtlas) {
        bodyBorderDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_border"), Animation.PlayMode.LOOP);
        bodyBorderUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_border"), Animation.PlayMode.LOOP);
        bodyBorderLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_border"), Animation.PlayMode.LOOP);
        bodyBorderRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_border"), Animation.PlayMode.LOOP);
    }

    @Override
    List<ColoredTextureRegion> actIdle(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        if (idleAnimationWaitTime >= idleAnimationWaitMax) {

            if (currentFramesRendered >= 4) {
                idleAnimationWaitTime = 0;
                currentFramesRendered = 0;
            } else {
                currentFramesRendered++;
            }

            switch (movingEntity.getFacingDirection()) {
                case NORTH:
                    frameList.add(getColorTextureRegion(bodyBorderUp, 0, false, appearance.getBorderColor(), 0));
                    frameList.add(getColorTextureRegion(headUpNaked, stateTime, true, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(chestUpNaked, 0, false, appearance.getSkinColor(), 3));
                    frameList.add(getColorTextureRegion(pantsUpNaked, 0, false, appearance.getSkinColor(), 1));
                    frameList.add(getColorTextureRegion(shoesUpNaked, 0, false, appearance.getSkinColor(), 0));
                    if (showHelm) {
                        frameList.add(getColorTextureRegion(helmUp, stateTime, true, Color.WHITE, 0));
                        frameList.add(getColorTextureRegion(helmUpBorder, stateTime, true, appearance.getBorderColor(), 0));
                    } else {
                        frameList.add(getColorTextureRegion(hairUp, stateTime, true, appearance.getHairColor(), 0));
                        frameList.add(getColorTextureRegion(hairUpBorder, stateTime, true, appearance.getBorderColor(), 0));
                    }
                    if (showChest)
                        frameList.add(getColorTextureRegion(chestUp, 0, false, Color.WHITE, 3));
                    if (showPants)
                        frameList.add(getColorTextureRegion(pantsUp, 0, false, Color.WHITE, 1));
                    if (showShoes)
                        frameList.add(getColorTextureRegion(shoesUp, 0, false, Color.WHITE, 0));
                    if (showGloves)
                        frameList.add(getColorTextureRegion(glovesUp, 0, false, appearance.getGlovesColor(), 3));
                    return frameList;
                case SOUTH:
                    frameList.add(getColorTextureRegion(bodyBorderDown, 0, false, appearance.getBorderColor(), 0));
                    frameList.add(getColorTextureRegion(headDownNaked, stateTime, true, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(chestDownNaked, 0, false, appearance.getSkinColor(), 3));
                    frameList.add(getColorTextureRegion(pantsDownNaked, 0, false, appearance.getSkinColor(), 1));
                    frameList.add(getColorTextureRegion(shoesDownNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(eyesDown, 0, false, appearance.getEyeColor(), 0));
                    if (showHelm) {
                        frameList.add(getColorTextureRegion(helmDown, stateTime, true, Color.WHITE, 0));
                        frameList.add(getColorTextureRegion(helmDownBorder, stateTime, true, appearance.getBorderColor(), 0));
                    } else {
                        frameList.add(getColorTextureRegion(hairDown, stateTime, true, appearance.getHairColor(), 0));
                        frameList.add(getColorTextureRegion(hairDownBorder, stateTime, true, appearance.getBorderColor(), 0));
                    }
                    if (showChest)
                        frameList.add(getColorTextureRegion(chestDown, 0, false, Color.WHITE, 3));
                    if (showPants)
                        frameList.add(getColorTextureRegion(pantsDown, 0, false, Color.WHITE, 1));
                    if (showShoes)
                        frameList.add(getColorTextureRegion(shoesDown, 0, false, Color.WHITE, 0));
                    if (showGloves)
                        frameList.add(getColorTextureRegion(glovesDown, 0, false, appearance.getGlovesColor(), 3));
                    return frameList;
                case WEST:
                    frameList.add(getColorTextureRegion(bodyBorderLeft, 0, false, appearance.getBorderColor(), 0));
                    frameList.add(getColorTextureRegion(headLeftNaked, stateTime, true, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(chestLeftNaked, 0, false, appearance.getSkinColor(), 3));
                    frameList.add(getColorTextureRegion(pantsLeftNaked, 0, false, appearance.getSkinColor(), 1));
                    frameList.add(getColorTextureRegion(shoesLeftNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(eyesLeft, 0, false, appearance.getEyeColor(), 0));
                    if (showHelm) {
                        frameList.add(getColorTextureRegion(helmLeft, stateTime, true, Color.WHITE, 0));
                        frameList.add(getColorTextureRegion(helmLeftBorder, stateTime, true, appearance.getBorderColor(), 0));
                    } else {
                        frameList.add(getColorTextureRegion(hairLeft, stateTime, true, appearance.getHairColor(), 0));
                        frameList.add(getColorTextureRegion(hairLeftBorder, stateTime, true, appearance.getBorderColor(), 0));
                    }
                    if (showChest)
                        frameList.add(getColorTextureRegion(chestLeft, 0, false, Color.WHITE, 3));
                    if (showPants)
                        frameList.add(getColorTextureRegion(pantsLeft, 0, false, Color.WHITE, 1));
                    if (showShoes)
                        frameList.add(getColorTextureRegion(shoesLeft, 0, false, Color.WHITE, 0));
                    if (showGloves)
                        frameList.add(getColorTextureRegion(glovesLeft, 0, false, appearance.getGlovesColor(), 3));
                    return frameList;
                case EAST:
                    frameList.add(getColorTextureRegion(bodyBorderRight, 0, false, appearance.getBorderColor(), 0));
                    frameList.add(getColorTextureRegion(headRightNaked, stateTime, true, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(chestRightNaked, 0, false, appearance.getSkinColor(), 3));
                    frameList.add(getColorTextureRegion(pantsRightNaked, 0, false, appearance.getSkinColor(), 1));
                    frameList.add(getColorTextureRegion(shoesRightNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(eyesRight, 0, false, appearance.getEyeColor(), 0));
                    if (showHelm) {
                        frameList.add(getColorTextureRegion(helmRight, stateTime, true, Color.WHITE, 0));
                        frameList.add(getColorTextureRegion(helmRightBorder, stateTime, true, appearance.getBorderColor(), 0));
                    } else {
                        frameList.add(getColorTextureRegion(hairRight, stateTime, true, appearance.getHairColor(), 0));
                        frameList.add(getColorTextureRegion(hairRightBorder, stateTime, true, appearance.getBorderColor(), 0));
                    }
                    if (showChest)
                        frameList.add(getColorTextureRegion(chestRight, 0, false, Color.WHITE, 3));
                    if (showPants)
                        frameList.add(getColorTextureRegion(pantsRight, 0, false, Color.WHITE, 1));
                    if (showShoes)
                        frameList.add(getColorTextureRegion(shoesRight, 0, false, Color.WHITE, 0));
                    if (showGloves)
                        frameList.add(getColorTextureRegion(glovesRight, 0, false, appearance.getGlovesColor(), 3));
                    return frameList;
                case NONE:
                    return null;
            }
        } else {
            idleAnimationWaitTime++;

            switch (movingEntity.getFacingDirection()) {
                case NORTH:
                    frameList.add(getColorTextureRegion(bodyBorderUp, 0, false, appearance.getBorderColor(), 0));
                    frameList.add(getColorTextureRegion(headUpNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(chestUpNaked, 0, false, appearance.getSkinColor(), 3));
                    frameList.add(getColorTextureRegion(pantsUpNaked, 0, false, appearance.getSkinColor(), 1));
                    frameList.add(getColorTextureRegion(shoesUpNaked, 0, false, appearance.getSkinColor(), 0));
                    if (showHelm) {
                        frameList.add(getColorTextureRegion(helmUp, 0, false, Color.WHITE, 0));
                        frameList.add(getColorTextureRegion(helmUpBorder, 0, false, appearance.getBorderColor(), 0));
                    } else {
                        frameList.add(getColorTextureRegion(hairUp, 0, false, appearance.getHairColor(), 0));
                        frameList.add(getColorTextureRegion(hairUpBorder, 0, false, appearance.getBorderColor(), 0));
                    }
                    if (showChest)
                        frameList.add(getColorTextureRegion(chestUp, 0, false, Color.WHITE, 3));
                    if (showPants)
                        frameList.add(getColorTextureRegion(pantsUp, 0, false, Color.WHITE, 1));
                    if (showShoes)
                        frameList.add(getColorTextureRegion(shoesUp, 0, false, Color.WHITE, 0));
                    if (showGloves)
                        frameList.add(getColorTextureRegion(glovesUp, 0, false, appearance.getGlovesColor(), 3));
                    return frameList;

                case SOUTH:
                    frameList.add(getColorTextureRegion(bodyBorderDown, 0, false, appearance.getBorderColor(), 0));
                    frameList.add(getColorTextureRegion(headDownNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(chestDownNaked, 0, false, appearance.getSkinColor(), 3));
                    frameList.add(getColorTextureRegion(pantsDownNaked, 0, false, appearance.getSkinColor(), 1));
                    frameList.add(getColorTextureRegion(shoesDownNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(eyesDown, 0, false, appearance.getEyeColor(), 0));
                    if (showHelm) {
                        frameList.add(getColorTextureRegion(helmDown, 0, false, Color.WHITE, 0));
                        frameList.add(getColorTextureRegion(helmDownBorder, 0, false, appearance.getBorderColor(), 0));
                    } else {
                        frameList.add(getColorTextureRegion(hairDown, 0, false, appearance.getHairColor(), 0));
                        frameList.add(getColorTextureRegion(hairDownBorder, 0, false, appearance.getBorderColor(), 0));
                    }
                    if (showChest)
                        frameList.add(getColorTextureRegion(chestDown, 0, false, Color.WHITE, 3));
                    if (showPants)
                        frameList.add(getColorTextureRegion(pantsDown, 0, false, Color.WHITE, 1));
                    if (showShoes)
                        frameList.add(getColorTextureRegion(shoesDown, 0, false, Color.WHITE, 0));
                    if (showGloves)
                        frameList.add(getColorTextureRegion(glovesDown, 0, false, appearance.getGlovesColor(), 3));
                    return frameList;
                case WEST:
                    frameList.add(getColorTextureRegion(bodyBorderLeft, 0, false, appearance.getBorderColor(), 0));
                    frameList.add(getColorTextureRegion(headLeftNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(chestLeftNaked, 0, false, appearance.getSkinColor(), 3));
                    frameList.add(getColorTextureRegion(pantsLeftNaked, 0, false, appearance.getSkinColor(), 1));
                    frameList.add(getColorTextureRegion(shoesLeftNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(eyesLeft, 0, false, appearance.getEyeColor(), 0));
                    if (showHelm) {
                        frameList.add(getColorTextureRegion(helmLeft, 0, false, Color.WHITE, 0));
                        frameList.add(getColorTextureRegion(helmLeftBorder, 0, false, appearance.getBorderColor(), 0));
                    } else {
                        frameList.add(getColorTextureRegion(hairLeft, 0, false, appearance.getHairColor(), 0));
                        frameList.add(getColorTextureRegion(hairLeftBorder, 0, false, appearance.getBorderColor(), 0));
                    }
                    if (showChest)
                        frameList.add(getColorTextureRegion(chestLeft, 0, false, Color.WHITE, 3));
                    if (showPants)
                        frameList.add(getColorTextureRegion(pantsLeft, 0, false, Color.WHITE, 1));
                    if (showShoes)
                        frameList.add(getColorTextureRegion(shoesLeft, 0, false, Color.WHITE, 0));
                    if (showGloves)
                        frameList.add(getColorTextureRegion(glovesLeft, 0, false, appearance.getGlovesColor(), 3));
                    return frameList;
                case EAST:
                    frameList.add(getColorTextureRegion(bodyBorderRight, 0, false, appearance.getBorderColor(), 0));
                    frameList.add(getColorTextureRegion(headRightNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(chestRightNaked, 0, false, appearance.getSkinColor(), 3));
                    frameList.add(getColorTextureRegion(pantsRightNaked, 0, false, appearance.getSkinColor(), 1));
                    frameList.add(getColorTextureRegion(shoesRightNaked, 0, false, appearance.getSkinColor(), 0));
                    frameList.add(getColorTextureRegion(eyesRight, 0, false, appearance.getEyeColor(), 0));
                    if (showHelm) {
                        frameList.add(getColorTextureRegion(helmRight, 0, false, Color.WHITE, 0));
                        frameList.add(getColorTextureRegion(helmRightBorder, 0, false, appearance.getBorderColor(), 0));
                    } else {
                        frameList.add(getColorTextureRegion(hairRight, 0, false, appearance.getHairColor(), 0));
                        frameList.add(getColorTextureRegion(hairRightBorder, 0, false, appearance.getBorderColor(), 0));
                    }
                    if (showChest)
                        frameList.add(getColorTextureRegion(chestRight, 0, false, Color.WHITE, 3));
                    if (showPants)
                        frameList.add(getColorTextureRegion(pantsRight, 0, false, Color.WHITE, 1));
                    if (showShoes)
                        frameList.add(getColorTextureRegion(shoesRight, 0, false, Color.WHITE, 0));
                    if (showGloves)
                        frameList.add(getColorTextureRegion(glovesRight, 0, false, appearance.getGlovesColor(), 3));
                    return frameList;
                case NONE:
                    return null;
            }
        }
        return null;
    }

    @Override
    List<ColoredTextureRegion> actMoveUp(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(bodyBorderUp, stateTime, true, appearance.getBorderColor(), 0));
        frameList.add(getColorTextureRegion(headUpNaked, stateTime, true, appearance.getSkinColor(), 0));
        frameList.add(getColorTextureRegion(chestUpNaked, stateTime, true, appearance.getSkinColor(), 3));
        frameList.add(getColorTextureRegion(pantsUpNaked, stateTime, true, appearance.getSkinColor(), 1));
        frameList.add(getColorTextureRegion(shoesUpNaked, stateTime, true, appearance.getSkinColor(), 0));
        if (showHelm) {
            frameList.add(getColorTextureRegion(helmUp, stateTime, true, Color.WHITE, 0));
            frameList.add(getColorTextureRegion(helmUpBorder, stateTime, true, appearance.getBorderColor(), 0));
        } else {
            frameList.add(getColorTextureRegion(hairUp, stateTime, true, appearance.getHairColor(), 0));
            frameList.add(getColorTextureRegion(hairUpBorder, stateTime, true, appearance.getBorderColor(), 0));
        }
        if (showChest)
            frameList.add(getColorTextureRegion(chestUp, stateTime, true, Color.WHITE, 3 + yAxisOffsetFix(stateTime)));
        if (showPants)
            frameList.add(getColorTextureRegion(pantsUp, stateTime, true, Color.WHITE, 1));
        if (showShoes)
            frameList.add(getColorTextureRegion(shoesUp, stateTime, true, Color.WHITE, 0));
        if (showGloves)
            frameList.add(getColorTextureRegion(glovesUp, stateTime, true, appearance.getGlovesColor(), 3));

        return frameList;
    }

    @Override
    List<ColoredTextureRegion> actMoveDown(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(bodyBorderDown, stateTime, true, appearance.getBorderColor(), 0));
        frameList.add(getColorTextureRegion(headDownNaked, stateTime, true, appearance.getSkinColor(), 0));
        frameList.add(getColorTextureRegion(chestDownNaked, stateTime, true, appearance.getSkinColor(), 3));
        frameList.add(getColorTextureRegion(pantsDownNaked, stateTime, true, appearance.getSkinColor(), 1));
        frameList.add(getColorTextureRegion(shoesDownNaked, stateTime, true, appearance.getSkinColor(), 0));
        frameList.add(getColorTextureRegion(eyesDown, 0, false, appearance.getEyeColor(), yAxisOffsetFix(stateTime)));
        if (showHelm) {
            frameList.add(getColorTextureRegion(helmDown, stateTime, true, Color.WHITE, 0));
            frameList.add(getColorTextureRegion(helmDownBorder, stateTime, true, appearance.getBorderColor(), 0));
        } else {
            frameList.add(getColorTextureRegion(hairDown, stateTime, true, appearance.getHairColor(), 0));
            frameList.add(getColorTextureRegion(hairDownBorder, stateTime, true, appearance.getBorderColor(), 0));
        }
        if (showChest)
            frameList.add(getColorTextureRegion(chestDown, stateTime, true, Color.WHITE, 3 + yAxisOffsetFix(stateTime)));
        if (showPants)
            frameList.add(getColorTextureRegion(pantsDown, stateTime, true, Color.WHITE, 1));
        if (showShoes)
            frameList.add(getColorTextureRegion(shoesDown, stateTime, true, Color.WHITE, 0));
        if (showGloves)
            frameList.add(getColorTextureRegion(glovesDown, stateTime, true, appearance.getGlovesColor(), 3));

        return frameList;
    }

    @Override
    List<ColoredTextureRegion> actMoveLeft(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(bodyBorderLeft, stateTime, true, appearance.getBorderColor(), 0));
        frameList.add(getColorTextureRegion(headLeftNaked, stateTime, true, appearance.getSkinColor(), 0));
        frameList.add(getColorTextureRegion(chestLeftNaked, stateTime, true, appearance.getSkinColor(), 3 + yAxisOffsetFix(stateTime)));
        frameList.add(getColorTextureRegion(pantsLeftNaked, stateTime, true, appearance.getSkinColor(), 1));
        frameList.add(getColorTextureRegion(shoesLeftNaked, stateTime, true, appearance.getSkinColor(), 0));
        frameList.add(getColorTextureRegion(eyesLeft, 0, false, appearance.getEyeColor(), yAxisOffsetFix(stateTime)));
        if (showHelm) {
            frameList.add(getColorTextureRegion(helmLeft, stateTime, true, Color.WHITE, 0));
            frameList.add(getColorTextureRegion(helmLeftBorder, stateTime, true, appearance.getBorderColor(), 0));
        } else {
            frameList.add(getColorTextureRegion(hairLeft, stateTime, true, appearance.getHairColor(), 0));
            frameList.add(getColorTextureRegion(hairLeftBorder, stateTime, true, appearance.getBorderColor(), 0));
        }
        if (showChest)
            frameList.add(getColorTextureRegion(chestLeft, stateTime, true, Color.WHITE, 3 + yAxisOffsetFix(stateTime)));
        if (showPants)
            frameList.add(getColorTextureRegion(pantsLeft, stateTime, true, Color.WHITE, 1));
        if (showShoes)
            frameList.add(getColorTextureRegion(shoesLeft, stateTime, true, Color.WHITE, 0));
        if (showGloves)
            frameList.add(getColorTextureRegion(glovesLeft, stateTime, true, appearance.getGlovesColor(), 3));

        return frameList;
    }

    @Override
    List<ColoredTextureRegion> actMoveRight(float stateTime) {
        List<ColoredTextureRegion> frameList = new ArrayList<ColoredTextureRegion>();

        frameList.add(getColorTextureRegion(bodyBorderRight, stateTime, true, appearance.getBorderColor(), 0));
        frameList.add(getColorTextureRegion(headRightNaked, stateTime, true, appearance.getSkinColor(), 0));
        frameList.add(getColorTextureRegion(chestRightNaked, stateTime, true, appearance.getSkinColor(), 3 + yAxisOffsetFix(stateTime)));
        frameList.add(getColorTextureRegion(pantsRightNaked, stateTime, true, appearance.getSkinColor(), 1));
        frameList.add(getColorTextureRegion(shoesRightNaked, stateTime, true, appearance.getSkinColor(), 0));
        frameList.add(getColorTextureRegion(eyesRight, 0, false, appearance.getEyeColor(), yAxisOffsetFix(stateTime)));
        if (showHelm) {
            frameList.add(getColorTextureRegion(helmRight, stateTime, true, Color.WHITE, 0));
            frameList.add(getColorTextureRegion(helmRightBorder, stateTime, true, appearance.getBorderColor(), 0));
        } else {
            frameList.add(getColorTextureRegion(hairRight, stateTime, true, appearance.getHairColor(), 0));
            frameList.add(getColorTextureRegion(hairRightBorder, stateTime, true, appearance.getBorderColor(), 0));
        }
        if (showChest)
            frameList.add(getColorTextureRegion(chestRight, stateTime, true, Color.WHITE, 3 + yAxisOffsetFix(stateTime)));
        if (showPants)
            frameList.add(getColorTextureRegion(pantsRight, stateTime, true, Color.WHITE, 1));
        if (showShoes)
            frameList.add(getColorTextureRegion(shoesRight, stateTime, true, Color.WHITE, 0));
        if (showGloves)
            frameList.add(getColorTextureRegion(glovesRight, stateTime, true, appearance.getGlovesColor(), 3));

        return frameList;
    }

    private int yAxisOffsetFix(float stateTime) {
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
