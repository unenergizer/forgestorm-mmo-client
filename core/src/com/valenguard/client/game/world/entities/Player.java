package com.valenguard.client.game.world.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.entities.animations.HumanAnimation;
import com.valenguard.client.io.type.GameAtlas;

import static com.valenguard.client.util.Log.println;

public class Player extends MovingEntity {

    private TextureAtlas characterTextureAtlas;

    @Override
    public void loadTextures(GameAtlas gameAtlas) {
        println(getClass(), "PLAYER: LOADING ALL TEXTURES...");
        characterTextureAtlas = Valenguard.getInstance().getFileManager().getAtlas(gameAtlas);
        applyAppearance();
    }

    private void applyAppearance() {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        short[] textureIds = getAppearance().getTextureIds();
        if (textureIds[Appearance.HELM] != -1) {
            humanAnimation.loadHead(characterTextureAtlas, textureIds[Appearance.HELM]);
        } else {
            humanAnimation.loadHead(characterTextureAtlas, textureIds[Appearance.HEAD]);
        }
        if (textureIds[Appearance.CHEST] == -1) {
            humanAnimation.loadNakedChest(characterTextureAtlas);
        } else {
            humanAnimation.loadChest(characterTextureAtlas, textureIds[Appearance.CHEST]);
        }
        if (textureIds[Appearance.PANTS] == -1) {
            humanAnimation.loadNakedPants(characterTextureAtlas);
        } else {
            humanAnimation.loadPants(characterTextureAtlas, textureIds[Appearance.PANTS]);
        }
        if (textureIds[Appearance.SHOES] == -1) {
            humanAnimation.loadNakedShoes(characterTextureAtlas);
        } else {
            humanAnimation.loadShoes(characterTextureAtlas, textureIds[Appearance.SHOES]);
        }
        humanAnimation.loadBorder(characterTextureAtlas);
    }

    public void setBodyPart(int appearanceId, short textureId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        getAppearance().getTextureIds()[appearanceId] = textureId;
        switch (appearanceId) {
            case Appearance.HELM:
                humanAnimation.loadHead(characterTextureAtlas, textureId);
                break;
            case Appearance.CHEST:
                humanAnimation.loadChest(characterTextureAtlas, textureId);
                break;
            case Appearance.PANTS:
                humanAnimation.loadPants(characterTextureAtlas, textureId);
                break;
            case Appearance.SHOES:
                humanAnimation.loadShoes(characterTextureAtlas, textureId);
                break;
        }
    }

    public void removeBodyPart(int appearanceId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        getAppearance().getTextureIds()[appearanceId] = -1;
        switch (appearanceId) {
            case Appearance.HELM:
                humanAnimation.loadHead(characterTextureAtlas, getAppearance().getTextureId(Appearance.HEAD));
                break;
            case Appearance.CHEST:
                humanAnimation.loadNakedChest(characterTextureAtlas);
                break;
            case Appearance.PANTS:
                humanAnimation.loadNakedPants(characterTextureAtlas);
                break;
            case Appearance.SHOES:
                humanAnimation.loadNakedShoes(characterTextureAtlas);
                break;
        }
    }
}
