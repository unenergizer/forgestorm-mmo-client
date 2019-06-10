package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;

public class DevMenu extends Actor implements Buildable {

    private MenuBar menuBar = new MenuBar();

    @Override
    public Actor build() {

        if (!Valenguard.getInstance().isModerator()) return this;
        Table table = new Table();
        table.setFillParent(true);

        table.add(menuBar.getTable()).expandX().row();
        table.add().expand().fill();
        ActorUtil.getStage().addActor(table);

        menuBar.addMenu(createModeratorMenu());

        if (!Valenguard.getInstance().isAdmin()) return this;
        menuBar.addMenu(createToolsMenu());
        setVisible(true);
        return this;
    }

    private Menu createModeratorMenu() {
        Menu moderatorMenu = new Menu("Moderator");

        moderatorMenu.addItem(new MenuItem("Kick Player"));
        moderatorMenu.addItem(new MenuItem("Ban Player"));
        moderatorMenu.addItem(new MenuItem("Player Editor"));
        moderatorMenu.addItem(new MenuItem("Shutdown Server"));

        return moderatorMenu;
    }

    private Menu createToolsMenu() {
        Menu toolsMenu = new Menu("Tools");

        toolsMenu.addItem(new MenuItem("Entity Editor", new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                NPCEditor npcEditor = ActorUtil.getStageHandler().getNPCEditor();
                npcEditor.resetValues();
                ActorUtil.fadeInWindow(npcEditor);
            }
        }));
        toolsMenu.addItem(new MenuItem("Drop Table Editor"));
        toolsMenu.addItem(new MenuItem("ItemStack Editor"));
        toolsMenu.addItem(new MenuItem("Warp Editor"));
        toolsMenu.addItem(new MenuItem("Professions Editor"));
        toolsMenu.addItem(new MenuItem("Factions Editor"));

        return toolsMenu;
    }
}
