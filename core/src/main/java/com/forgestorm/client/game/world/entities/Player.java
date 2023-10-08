package com.forgestorm.client.game.world.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.animations.human.HumanAnimation;
import com.forgestorm.shared.game.world.entities.AppearanceType;
import com.forgestorm.shared.io.type.GameAtlas;
import com.forgestorm.shared.util.color.LibGDXColorList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player extends MovingEntity {

    private TextureAtlas characterTextureAtlas;

    @Override
    public void loadTextures(GameAtlas gameAtlas) {
        characterTextureAtlas = ClientMain.getInstance().getFileManager().getAtlas(gameAtlas);
        getEntityAnimation().loadAll(gameAtlas);
    }

    public void setBodyPart(AppearanceType appearanceType, int textureId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        Appearance appearance = getAppearance();

        switch (appearanceType) {
            case SINGLE_BODY_TEXTURE:
                break;
            case HAIR_TEXTURE:
                humanAnimation.loadHair(characterTextureAtlas, (short) textureId);
                appearance.setHairTexture((byte) textureId);
                break;
            case HELM_TEXTURE:
                humanAnimation.loadHelm(characterTextureAtlas, (short) textureId);
                humanAnimation.setShowHelm(true);
                appearance.setHelmTexture((byte) textureId);
                break;
            case CHEST_TEXTURE:
                humanAnimation.loadChest(characterTextureAtlas, (short) textureId);
                humanAnimation.setShowChest(true);
                appearance.setChestTexture((byte) textureId);
                break;
            case PANTS_TEXTURE:
                humanAnimation.loadPants(characterTextureAtlas, (short) textureId);
                humanAnimation.setShowPants(true);
                appearance.setPantsTexture((byte) textureId);
                break;
            case SHOES_TEXTURE:
                humanAnimation.loadShoes(characterTextureAtlas, (short) textureId);
                humanAnimation.setShowShoes(true);
                appearance.setShoesTexture((byte) textureId);
                break;
            case HAIR_COLOR:
                break;
            case EYE_COLOR:
                break;
            case SKIN_COLOR:
                break;
            case GLOVES_COLOR:
                humanAnimation.setShowGloves(true);
                appearance.setGlovesColor(new Color(textureId));
                break;
            case BORDER_COLOR:
                break;
            case LEFT_HAND:
                humanAnimation.loadLeftHand(characterTextureAtlas, (short) textureId);
                humanAnimation.setShowLeftHand(true);
                appearance.setLeftHandTexture((byte) textureId);
                break;
            case RIGHT_HAND:
                humanAnimation.loadRightHand(characterTextureAtlas, (short) textureId);
                humanAnimation.setShowRightHand(true);
                appearance.setRightHandTexture((byte) textureId);
                break;
        }
    }

    public void removeBodyPart(AppearanceType appearanceType) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        Appearance appearance = getAppearance();

        switch (appearanceType) {
            case HELM_TEXTURE:
                humanAnimation.setShowHelm(false);
                appearance.setHelmTexture((byte) -1);
                break;
            case CHEST_TEXTURE:
                humanAnimation.setShowChest(false);
                appearance.setChestTexture((byte) -1);
                break;
            case PANTS_TEXTURE:
                humanAnimation.setShowPants(false);
                appearance.setPantsTexture((byte) -1);
                break;
            case SHOES_TEXTURE:
                humanAnimation.setShowShoes(false);
                appearance.setShoesTexture((byte) -1);
                break;
            case GLOVES_COLOR:
                humanAnimation.setShowGloves(false);
                appearance.setGlovesColor(LibGDXColorList.CLEAR.getColor());
                break;
            case LEFT_HAND:
                humanAnimation.setShowLeftHand(false);
                appearance.setLeftHandTexture((byte) -1);
                break;
            case RIGHT_HAND:
                humanAnimation.setShowRightHand(false);
                appearance.setRightHandTexture((byte) -1);
                break;
        }
    }
}
