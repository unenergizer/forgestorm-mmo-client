package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.forgestorm.client.game.screens.ui.StageHandler;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

import java.text.DecimalFormat;

import lombok.Getter;

@SuppressWarnings("SameParameterValue")
@Getter
public abstract class EditorTab extends Tab {

    private final StageHandler stageHandler;
    private final EntityEditor entityEditor;
    private final DecimalFormat decimalFormat = new DecimalFormat();

    EditorTab(StageHandler stageHandler, EntityEditor entityEditor) {
        super(false, false);
        this.stageHandler = stageHandler;
        this.entityEditor = entityEditor;
        decimalFormat.setMaximumFractionDigits(2);
    }

    public abstract void resetValues();

    public abstract void build();

}
