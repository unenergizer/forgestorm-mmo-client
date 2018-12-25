package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.animations.HumanAnimation;

public class Player extends MovingEntity {

    private TextureAtlas characterTextureAtlas;

    @Override
    public void loadTextures(GameAtlas gameAtlas) {
        characterTextureAtlas = Valenguard.getInstance().getFileManager().getAtlas(gameAtlas);
        applyAppearance();
    }

    private void applyAppearance() {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        short[] textureIds = getAppearance().getTextureIds();
        System.out.println("APPLYING APPEARANCE");
        if (textureIds[Appearance.HELM] != -1) {
            humanAnimation.loadHead(characterTextureAtlas, textureIds[Appearance.HELM]);
        } else {
            humanAnimation.loadHead(characterTextureAtlas, textureIds[Appearance.HEAD]);
        }
        if (textureIds[Appearance.ARMOR] != -1) {
            humanAnimation.loadBody(characterTextureAtlas, textureIds[Appearance.ARMOR]);
        } else {
            humanAnimation.loadBody(characterTextureAtlas, textureIds[Appearance.BODY]);
        }
    }

    public void setHelm(short helmId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        getAppearance().getTextureIds()[Appearance.HELM] = helmId;
        humanAnimation.loadHead(characterTextureAtlas, helmId);
    }

    public void setArmor(short armorId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        getAppearance().getTextureIds()[Appearance.ARMOR] = armorId;
        humanAnimation.loadBody(characterTextureAtlas, armorId);
    }

    public void removeHelm() {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        getAppearance().getTextureIds()[Appearance.HELM] = -1;
        humanAnimation.loadHead(characterTextureAtlas, getAppearance().getTextureId(Appearance.HEAD));
    }

    public void removeArmor() {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        getAppearance().getTextureIds()[Appearance.ARMOR] = -1;
        humanAnimation.loadBody(characterTextureAtlas, getAppearance().getTextureId(Appearance.BODY));
    }
}
