package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.Appearance;

import static com.valenguard.client.util.Log.println;

public class MonsterAppearancePanel implements AppearancePanel {

    private final MonsterTab monsterTab;

    private ImageData bodyData = new ImageData();
    private MonsterBodyPart monsterBodyPart;

    MonsterAppearancePanel(MonsterTab monsterTab) {
        this.monsterTab = monsterTab;
        int textureSelectScale = 3;
        monsterBodyPart = new MonsterBodyPart(monsterTab, monsterTab.getAppearanceTable(), 89, 16 * textureSelectScale, 16 * textureSelectScale, bodyData, false);
        monsterBodyPart.build();
    }

    @Override
    public void load(AiEntity aiEntity) {
        Appearance appearance = aiEntity.getAppearance();

        bodyData.setData(appearance.getMonsterBodyTexture());
        monsterBodyPart.setData(appearance.getMonsterBodyTexture());
    }

    @Override
    public void reset() {
        bodyData.setData(0);
        monsterBodyPart.setData(0);
    }

    @Override
    public void buildAppearancePanel() {

        // Create < and > buttons to scroll through monsters (89 max)
        monsterTab.getPreviewTable().setWidth(16 * NpcTab.PREVIEW_SCALE);
        monsterTab.getPreviewTable().setHeight(16 * NpcTab.PREVIEW_SCALE);
        monsterTab.getAppearanceTable().add(monsterTab.getPreviewTable());
    }

    @Override
    public EntityEditorData getDataOut(EntityEditorData entityEditorData) {

        entityEditorData.setMonsterBodyTexture((byte) bodyData.getData());

        return entityEditorData;
    }

    @Override
    public void characterPreview() {

    }

    @Override
    public void printDebug() {
        println(MonsterAppearancePanel.class, "MonsterBodyTexture: " + bodyData.getData());
    }
}
