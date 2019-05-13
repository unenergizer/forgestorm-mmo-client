package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.PlayerClient;

public class DevMenu extends Actor implements Buildable {

    private MenuBar menuBar = new MenuBar();

    @Override
    public Actor build() {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        if (!playerClient.isModerator()) return this;
        Table table = new Table();
        table.setFillParent(true);

        table.add(menuBar.getTable()).expandX().row();
        table.add().expand().fill();
        ActorUtil.getStage().addActor(table);

        menuBar.addMenu(createModeratorMenu());

        if (!playerClient.isAdmin()) return this;
        menuBar.addMenu(createToolsMenu());

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
                ActorUtil.getStageHandler().getEntityCreator().setVisible(true);
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
