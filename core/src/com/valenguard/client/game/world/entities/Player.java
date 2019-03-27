package com.valenguard.client.game.world.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.world.entities.animations.HumanAnimation;
import com.valenguard.client.io.type.GameAtlas;

import static com.valenguard.client.util.Log.println;

public class Player extends MovingEntity {

    private static final boolean PRINT_DEBUG = false;

    private TextureAtlas characterTextureAtlas;

    @Override
    public void loadTextures(GameAtlas gameAtlas) {
        characterTextureAtlas = Valenguard.getInstance().getFileManager().getAtlas(gameAtlas);
        applyAppearance();
    }

    private void applyAppearance() {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        short[] textureIds = getAppearance().getTextureIds();
        println(getClass(), "APPLYING APPEARANCE", false, PRINT_DEBUG);
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
