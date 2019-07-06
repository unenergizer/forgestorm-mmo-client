package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.valenguard.client.game.rpg.EntityAlignment;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

import java.text.DecimalFormat;

import lombok.Getter;

@Getter
public class EntityEditor extends HideableVisWindow implements Buildable, Focusable {

    private final DecimalFormat decimalFormat = new DecimalFormat();

    @Getter
    private TabbedPane tabbedPane = new TabbedPane();
    private NpcTab npcTab = new NpcTab(this);
    private MonsterTab monsterTab = new MonsterTab(this);

    public EntityEditor() {
        super("Entity Editor");
        decimalFormat.setMaximumFractionDigits(2);
    }

    @Override
    public Actor build() {
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(true);

        final VisTable mainTable = new VisTable();
        mainTable.pad(3);

        tabbedPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                mainTable.clearChildren();
                mainTable.add(tab.getContentTable()).expand().fill();
            }
        });

        add(tabbedPane.getTable()).expandX().fillX();
        row();
        add(mainTable).expand().fill();

        tabbedPane.add(npcTab);
        tabbedPane.add(monsterTab);
        tabbedPane.switchTab(0);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
                resetValues();
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });
        setSize(500, 575);
        centerWindow();
        setVisible(false);
        return this;
    }

    public void resetValues() {
        npcTab.resetValues();
        monsterTab.resetValues();
    }

    void selectBox(VisTable mainTable, String labelName, VisSelectBox visSelectBox, EntityAlignment[] items) {
        visSelectBox.setItems(items);
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow().pad(1);
        table.add(visSelectBox).pad(1);
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

    @Override
    public void focusLost() {
    }

    @Override
    public void focusGained() {
    }
}
