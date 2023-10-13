package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import lombok.Getter;

@Getter
public class EntityEditor extends HideableVisWindow implements Buildable {

    @Getter
    private TabbedPane tabbedPane = new TabbedPane();
    private NpcTab npcTab;
    private MonsterTab monsterTab;
    private ItemStackDropTab itemStackDropTab;

    public EntityEditor(ClientMain clientMain) {
        super(clientMain, "Entity Editor");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
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

        npcTab = new NpcTab(stageHandler, this);
        monsterTab = new MonsterTab(stageHandler, this);
        itemStackDropTab = new ItemStackDropTab(stageHandler, this);

        tabbedPane.add(npcTab);
        tabbedPane.add(monsterTab);
        tabbedPane.add(itemStackDropTab);
        tabbedPane.switchTab(0);

        stopWindowClickThrough();

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
}
