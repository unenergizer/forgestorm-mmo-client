package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.valenguard.client.game.screens.ui.actors.dev.item.ItemStackEditor;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class DevMenu extends VisTable implements Buildable {

    private MenuBar menuBar = new MenuBar();

    @Override
    public Actor build(final StageHandler stageHandler) {
        VisTable menuTable = new VisTable();

        menuTable.add(menuBar.getTable()).expandX().row();
        menuTable.add().expand().fill();
        add(menuTable);

        menuBar.addMenu(createModeratorMenu());
        menuBar.addMenu(createToolsMenu(stageHandler));

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), Gdx.graphics.getHeight() - getHeight());
            }
        });

        pack();
        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), Gdx.graphics.getHeight() - getHeight());
        setVisible(false);
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

    private Menu createToolsMenu(final StageHandler stageHandler) {
        Menu toolsMenu = new Menu("Tools");

        toolsMenu.addItem(new MenuItem("World Builder", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.fadeInWindow(stageHandler.getWorldBuilder());
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));

        toolsMenu.addItem(new MenuItem("Entity Editor", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                EntityEditor entityEditor = stageHandler.getEntityEditor();
                entityEditor.resetValues();
                ActorUtil.fadeInWindow(entityEditor);
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));
        toolsMenu.addItem(new MenuItem("Drop Table Editor"));
        toolsMenu.addItem(new MenuItem("ItemStack Editor", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ItemStackEditor itemStackEditor = stageHandler.getItemStackEditor();
                itemStackEditor.resetValues();
                ActorUtil.fadeInWindow(itemStackEditor);
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));
        toolsMenu.addItem(new MenuItem("Warp Editor"));
        toolsMenu.addItem(new MenuItem("Professions Editor"));
        toolsMenu.addItem(new MenuItem("Factions Editor"));
        toolsMenu.addItem(new MenuItem("Pixel FX", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PixelFXTest pixelFXTest = stageHandler.getPixelFXTest();
                ActorUtil.fadeInWindow(pixelFXTest);
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));

        return toolsMenu;
    }
}
