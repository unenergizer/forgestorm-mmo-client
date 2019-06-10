package com.valenguard.client.game.world.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.entities.animations.HumanAnimation;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player extends MovingEntity {

    private TextureAtlas characterTextureAtlas;

    @Override
    public void loadTextures(GameAtlas gameAtlas) {
        characterTextureAtlas = Valenguard.getInstance().getFileManager().getAtlas(gameAtlas);
        getEntityAnimation().loadAll(gameAtlas);
    }

    public void setBodyPart(AppearanceType appearanceType, short textureId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();

        switch (appearanceType) {
            case MONSTER_BODY_TEXTURE:
                break;
            case HAIR_TEXTURE:
                humanAnimation.loadHair(characterTextureAtlas, textureId);
                break;
            case HELM_TEXTURE:
                humanAnimation.loadHelm(characterTextureAtlas, textureId);
                humanAnimation.setShowHelm(true);
                break;
            case CHEST_TEXTURE:
                humanAnimation.loadChest(characterTextureAtlas, textureId);
                humanAnimation.setShowChest(true);
                break;
            case PANTS_TEXTURE:
                humanAnimation.loadPants(characterTextureAtlas, textureId);
                humanAnimation.setShowPants(true);
                break;
            case SHOES_TEXTURE:
                humanAnimation.loadShoes(characterTextureAtlas, textureId);
                humanAnimation.setShowShoes(true);
                break;
            case HAIR_COLOR:
                break;
            case EYE_COLOR:
                break;
            case SKIN_COLOR:
                break;
            case GLOVES_COLOR:
                break;
            case BORDER_COLOR:
                break;
        }
    }

    public void removeBodyPart(AppearanceType appearanceType) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        switch (appearanceType) {
            case HELM_TEXTURE:
                humanAnimation.setShowHelm(false);
                break;
            case CHEST_TEXTURE:
                humanAnimation.setShowChest(false);
                break;
            case PANTS_TEXTURE:
                humanAnimation.setShowPants(false);
                break;
            case SHOES_TEXTURE:
                humanAnimation.setShowShoes(false);
                break;
            case GLOVES_COLOR:
                humanAnimation.setShowGloves(false);
                break;
        }
    }
}
