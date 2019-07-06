package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.valenguard.client.game.world.entities.AiEntity;

public interface AppearancePanel {

    void load(AiEntity entity);

    void reset();

    void buildAppearancePanel();

    void characterPreview();

    EntityEditorData getDataOut(EntityEditorData entityEditorData);

    void printDebug();
}
