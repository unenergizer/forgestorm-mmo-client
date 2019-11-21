package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.game.rpg.EntityAlignment;

import java.text.DecimalFormat;

public abstract class EditorTab extends Tab {

    private final DecimalFormat decimalFormat = new DecimalFormat();

    EditorTab() {
        super(false, false);
        decimalFormat.setMaximumFractionDigits(2);
    }

    public abstract void resetValues();
    public abstract void build();


    void selectBox(VisTable mainTable, String labelName, VisSelectBox visSelectBox, EntityAlignment[] items) {
        visSelectBox.setItems(items);
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow().pad(1);
        table.add(visSelectBox).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();
    }

    void checkBox(VisTable mainTable, String labelName, VisCheckBox visCheckBox) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow().pad(1);
        table.add(visCheckBox).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();
    }

    void textField(VisTable mainTable, String labelName, VisTextField textField) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow().pad(1);
        table.add(textField).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();
    }

    void valueSlider(VisTable mainTable, String labelName, final VisSlider slider) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        final VisLabel sliderValue = new VisLabel(decimalFormat.format(slider.getValue()));
        table.add(visLabel).grow().pad(1);
        table.add(slider).pad(1);
        table.add(sliderValue).pad(1);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sliderValue.setText(decimalFormat.format(slider.getValue()));
            }
        });

        mainTable.add(table).expandX().fillX().pad(1).row();
    }

}
