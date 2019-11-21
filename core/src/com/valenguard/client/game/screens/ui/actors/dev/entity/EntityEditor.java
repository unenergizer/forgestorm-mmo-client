package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

import lombok.Getter;

@Getter
public class EntityEditor extends HideableVisWindow implements Buildable, Focusable {

    @Getter
    private TabbedPane tabbedPane = new TabbedPane();
    private NpcTab npcTab = new NpcTab(this);
    private MonsterTab monsterTab = new MonsterTab(this);
    private ItemStackDrop itemStackDropTab = new ItemStackDrop(this);

    public EntityEditor() {
        super("Entity Editor");
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
        tabbedPane.add(itemStackDropTab);
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
        itemStackDropTab.resetValues();
    }

    @Override
    public void focusLost() {
    }

    @Override
    public void focusGained() {
    }
}
