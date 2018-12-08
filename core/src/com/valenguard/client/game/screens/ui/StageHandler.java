package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.valenguard.client.game.assets.GameSkin;
import com.valenguard.client.game.screens.ui.actors.VisabilityToggle;
import com.valenguard.client.game.screens.ui.actors.WindowResizeEvent;
import com.valenguard.client.game.screens.ui.actors.game.ChatWindow;
import com.valenguard.client.game.screens.ui.actors.game.CreditsWindow;
import com.valenguard.client.game.screens.ui.actors.game.DebugTable;
import com.valenguard.client.game.screens.ui.actors.game.EscapeWindow;
import com.valenguard.client.game.screens.ui.actors.game.HelpWindow;
import com.valenguard.client.game.screens.ui.actors.game.InventoryWindow;
import com.valenguard.client.game.screens.ui.actors.login.ButtonTable;
import com.valenguard.client.game.screens.ui.actors.login.CopyrightTable;
import com.valenguard.client.game.screens.ui.actors.login.LoginTable;
import com.valenguard.client.game.screens.ui.actors.login.VersionTable;
import com.valenguard.client.game.screens.ui.actors.settings.MainSettingsWindow;

import lombok.Getter;

@Getter
public class StageHandler implements Disposable {

    private final Stage stage = new Stage();
    private final PreStageEvent preStageEvent = new PreStageEvent(this);
    private final PostStageEvent postStageEvent = new PostStageEvent(this);

    private boolean initialized = false;

    private ButtonTable buttonTable;
    private VersionTable versionTable;
    private CopyrightTable copyrightTable;
    private LoginTable loginTable;
    private HelpWindow helpWindow;
    private CreditsWindow creditsWindow;
    private EscapeWindow escapeWindow;
    private ChatWindow chatWindow;
    private InventoryWindow inventoryWindow;
    private MainSettingsWindow mainSettingsWindow;
    private DebugTable debugTable;

    public void init() {
        if (initialized) return;
        initialized = true;
        VisUI.load(Gdx.files.internal(GameSkin.DEFAULT.getFilePath()));

        buttonTable = new ButtonTable();
        versionTable = new VersionTable();
        copyrightTable = new CopyrightTable();
        loginTable = new LoginTable();
        helpWindow = new HelpWindow();
        creditsWindow = new CreditsWindow();
        escapeWindow = new EscapeWindow();
        chatWindow = new ChatWindow();
        inventoryWindow = new InventoryWindow();
        mainSettingsWindow = new MainSettingsWindow();
        debugTable = new DebugTable();

        stage.addActor(buttonTable.build());
        stage.addActor(versionTable.build());
        stage.addActor(copyrightTable.build());
        stage.addActor(loginTable.build());
        stage.addActor(helpWindow.build());
        stage.addActor(creditsWindow.build());
        stage.addActor(chatWindow.build());
        stage.addActor(inventoryWindow.build());
        stage.addActor(escapeWindow.build());
        stage.addActor(mainSettingsWindow.build());

        FocusManager.resetFocus(stage); // Clear focus after building windows
    }

    public void setVisible(Actor actor, boolean visible) {
        boolean isInstance = actor instanceof com.valenguard.client.game.screens.ui.actors.VisabilityToggle;
        if (visible && isInstance)
            ((com.valenguard.client.game.screens.ui.actors.VisabilityToggle) actor).show();
        if (!visible && isInstance) ((VisabilityToggle) actor).hide();
        actor.setVisible(visible);
    }

    public void render(float delta) {
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    public void resize(int width, int height) {
        // See https://github.com/libgdx/libgdx/issues/3673#issuecomment-177606278
        if (width == 0 && height == 0) return;
        stage.getViewport().update(width, height, true);
        PopupMenu.removeEveryMenu(stage);
        WindowResizeEvent resizeEvent = new WindowResizeEvent();
        for (Actor actor : stage.getActors()) actor.fire(resizeEvent);
    }

    @Override
    public void dispose() {
        VisUI.dispose();
        stage.dispose();
    }
}
