package com.forgestorm.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.item.ItemStackEditor;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.kotcrab.vis.ui.widget.*;

public class DevMenu extends VisTable implements Buildable {

    private final MenuBar menuBar = new MenuBar();
    private StageHandler stageHandler;

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        VisTable menuTable = new VisTable();

        menuTable.add(menuBar.getTable()).expandX().row();
        menuTable.add().expand().fill();
        add(menuTable);

        menuBar.addMenu(createModeratorMenu());
        menuBar.addMenu(createToolsMenu(stageHandler));

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((Gdx.graphics.getWidth() / 2f) - (getWidth() / 2), Gdx.graphics.getHeight() - getHeight());
            }
        });

        pack();
        setPosition((Gdx.graphics.getWidth() / 2f) - (getWidth() / 2), Gdx.graphics.getHeight() - getHeight());
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

        MenuItem worldSubmenu = new MenuItem("World Editing");
        worldSubmenu.setSubMenu(createWorldEditorMenus(stageHandler));
        toolsMenu.addItem(worldSubmenu);

        final EntityEditor entityEditor = stageHandler.getEntityEditor();
        addMenuItem(toolsMenu, entityEditor, "", entityEditor::resetValues);
        toolsMenu.addItem(new MenuItem("Drop Table Editor"));
        final ItemStackEditor itemStackEditor = stageHandler.getItemStackEditor();
        addMenuItem(toolsMenu, itemStackEditor, "", itemStackEditor::resetValues);
        toolsMenu.addItem(new MenuItem("Professions Editor"));
        toolsMenu.addItem(new MenuItem("Factions Editor"));
        addMenuItem(toolsMenu, stageHandler.getPixelFXTest(), "", null);
        addMenuItem(toolsMenu, stageHandler.getSpellAnimationEditor(), "", null);

        return toolsMenu;
    }

    private PopupMenu createWorldEditorMenus(final StageHandler stageHandler) {
        PopupMenu popupMenu = new PopupMenu();

        addMenuItem(popupMenu, stageHandler.getTileBuildMenu(), "CTRL + B", null);
        addMenuItem(popupMenu, stageHandler.getTilePropertiesEditor(), "", null);
        addMenuItem(popupMenu, stageHandler.getTileAnimationEditor(), "", null);
        addMenuItem(popupMenu, stageHandler.getWarpEditor(), "", null);
        addMenuItem(popupMenu, stageHandler.getRegionEditor(), "", () -> stageHandler.getRegionEditor().toggleOpenClose(true));

        return popupMenu;
    }

    private void addMenuItem(PopupMenu menu, HideableVisWindow hideableVisWindow, String shortCut, CodeExecutor codeExecutor) {
        menu.addItem(new MenuItem(hideableVisWindow.getTitleLabel().getText().toString(), new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (codeExecutor != null) codeExecutor.runCode();
                ActorUtil.fadeInWindow(hideableVisWindow);
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(DevMenu.class, (short) 0);
            }
        }).setShortcut(shortCut));
    }
}
