package com.forgestorm.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.game.screens.ui.actors.dev.world.TilePropertiesEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.world.WorldBuilderUI;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.VisTable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.item.ItemStackEditor;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;

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

        final WorldBuilderUI worldBuilder = stageHandler.getWorldBuilderUI();
        toolsMenu.addItem(new MenuItem(worldBuilder.getTitleLabel().getText().toString(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.fadeInWindow(worldBuilder);
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));

        final EntityEditor entityEditor = stageHandler.getEntityEditor();
        toolsMenu.addItem(new MenuItem(entityEditor.getTitleLabel().getText().toString(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                entityEditor.resetValues();
                ActorUtil.fadeInWindow(entityEditor);
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));
        toolsMenu.addItem(new MenuItem("Drop Table Editor"));

        final ItemStackEditor itemStackEditor = stageHandler.getItemStackEditor();
        toolsMenu.addItem(new MenuItem(itemStackEditor.getTitleLabel().getText().toString(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                itemStackEditor.resetValues();
                ActorUtil.fadeInWindow(itemStackEditor);
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));
        toolsMenu.addItem(new MenuItem("Warp Editor"));
        toolsMenu.addItem(new MenuItem("Professions Editor"));
        toolsMenu.addItem(new MenuItem("Factions Editor"));

        final PixelFXTest pixelFXTest = stageHandler.getPixelFXTest();
        toolsMenu.addItem(new MenuItem(pixelFXTest.getTitleLabel().getText().toString(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.fadeInWindow(pixelFXTest);
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));

        final TilePropertiesEditor tilePropertiesEditor = stageHandler.getTilePropertiesEditor();
        toolsMenu.addItem(new MenuItem(tilePropertiesEditor.getTitleLabel().getText().toString(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.fadeInWindow(tilePropertiesEditor);
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }));

        return toolsMenu;
    }
}
