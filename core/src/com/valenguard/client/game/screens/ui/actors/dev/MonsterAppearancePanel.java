package com.valenguard.client.game.screens.ui.actors.dev;

import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.Appearance;

import static com.valenguard.client.util.Log.println;

public class MonsterAppearancePanel implements AppearancePanel {

    private final NPCEditor npcEditor;

    private ImageData bodyData = new ImageData();
    private MonsterBodyPart monsterBodyPart;

    MonsterAppearancePanel(NPCEditor npcEditor) {
        this.npcEditor = npcEditor;
        int textureSelectScale = 3;
        monsterBodyPart = new MonsterBodyPart(npcEditor, npcEditor.getAppearanceTable(), 89, 16 * textureSelectScale, 16 * textureSelectScale, bodyData, false);
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


        characterPreview();
        npcEditor.getPreviewTable().setWidth(16 * NPCEditor.PREVIEW_SCALE);
        npcEditor.getPreviewTable().setHeight(16 * NPCEditor.PREVIEW_SCALE);
        npcEditor.getAppearanceTable().add(npcEditor.getPreviewTable());
    }

    @Override
    public EntityEditorData getDataOut(EntityEditorData entityEditorData) {

        entityEditorData.setMonsterBodyTexture((byte) bodyData.getData());

        return entityEditorData;
    }

    @Override
    public void characterPreview() {
//        if (npcEditor.getPreviewTable().hasChildren()) npcEditor.getPreviewTable().clearChildren();
//        final int width = 16 * NPCEditor.PREVIEW_SCALE;
//
//
//        Stack imageStack = new Stack();
//        imageStack.setWidth(16 * NPCEditor.PREVIEW_SCALE);
//        imageStack.setHeight(16 * NPCEditor.PREVIEW_SCALE);
//
//        imageStack.add(npcEditor.imageTable(width, 16 * NPCEditor.PREVIEW_SCALE, NPCEditor.PREVIEW_SCALE, "monster_down_" + bodyData.getData(), Color.WHITE));
//
//        npcEditor.getPreviewTable().add(imageStack);
    }

    @Override
    public void printDebug() {
        println(MonsterAppearancePanel.class, "MonsterBodyTexture: " + bodyData.getData());
    }
}
