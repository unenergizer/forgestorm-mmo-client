package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.animations.HumanAnimation;

import lombok.Getter;

public class Player extends MovingEntity {

    private TextureAtlas characterTextureAtlas;
    private short helm = -1;
    private short armor = -1;
    private short head;
    private short body;
    @Getter
    private Color bodyColor = new Color(1, .913f, .77f, 1);
//    private Color bodyColor = Color.LIME;

    @Override
    public void loadTextures(GameAtlas gameAtlas, short[] textureIds) {
        characterTextureAtlas = Valenguard.getInstance().getFileManager().getAtlas(gameAtlas);
        setHeadAndBody(textureIds[0], textureIds[1]);
    }

    private void setHeadAndBody(short headId, short bodyId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        head = headId;
        body = bodyId;
        if (helm == -1) {
            humanAnimation.loadHead(characterTextureAtlas, headId);
        }
        if (armor == -1) {
            humanAnimation.loadBody(characterTextureAtlas, bodyId);
        }
    }

    public void setHelm(short helmId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        helm = helmId;
        humanAnimation.loadHead(characterTextureAtlas, helmId);
    }

    public void setArmor(short armorId) {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        armor = armorId;
        humanAnimation.loadBody(characterTextureAtlas, armorId);
    }

    public void removeArmor() {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        armor = -1;
        humanAnimation.loadBody(characterTextureAtlas, body);
    }

    public void removeHelm() {
        HumanAnimation humanAnimation = (HumanAnimation) getEntityAnimation();
        helm = -1;
        humanAnimation.loadHead(characterTextureAtlas, head);
    }
}
