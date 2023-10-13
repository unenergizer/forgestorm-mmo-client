package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.EntityEditorData;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.MonsterData;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.game.world.entities.Appearance;

import static com.forgestorm.client.util.Log.println;

public class MonsterAppearancePanel implements AppearancePanel {

    private final MonsterTab monsterTab;

    private ImageData bodyData = new ImageData();
    private MonsterBodyPart monsterBodyPart;

    MonsterAppearancePanel(ClientMain clientMain, MonsterTab monsterTab) {
        this.monsterTab = monsterTab;
        int textureSelectScale = 3;
        monsterBodyPart = new MonsterBodyPart(clientMain, monsterTab, monsterTab.getAppearanceTable(), 89, 16 * textureSelectScale, 16 * textureSelectScale, bodyData, false);
        monsterBodyPart.build();
    }

    @Override
    public void load(AiEntity aiEntity) {
        Appearance appearance = aiEntity.getAppearance();

        bodyData.setData(appearance.getSingleBodyTexture());
        monsterBodyPart.setData(appearance.getSingleBodyTexture());
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

        ((MonsterData) entityEditorData).setSingleBodyTexture((byte) bodyData.getData());

        return entityEditorData;
    }

    @Override
    public void characterPreview() {

    }

    @Override
    public void printDebug() {
        println(MonsterAppearancePanel.class, "SingleBodyTexture: " + bodyData.getData());
    }
}
