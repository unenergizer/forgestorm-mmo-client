package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.valenguard.client.game.assets.GameSkin;
import com.valenguard.client.game.screens.ui.actors.CreditsWindow;
import com.valenguard.client.game.screens.ui.actors.EscapeWindow;
import com.valenguard.client.game.screens.ui.actors.HelpWindow;
import com.valenguard.client.game.screens.ui.actors.InventoryWindow;
import com.valenguard.client.game.screens.ui.actors.game.ChatWindow;
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

    public void init() {
        VisUI.load(Gdx.files.internal(GameSkin.DEFAULT.getFilePath()));

        buttonTable = new ButtonTable();
        versionTable = new VersionTable();
        copyrightTable = new CopyrightTable();
        loginTable = new LoginTable(this);
        helpWindow = new HelpWindow();
        creditsWindow = new CreditsWindow();
        escapeWindow = new EscapeWindow(this);
        chatWindow = new ChatWindow(this);
        inventoryWindow = new InventoryWindow(this);
        mainSettingsWindow = new MainSettingsWindow();

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
        boolean isInstance = actor instanceof VisabilityToggle;
        if (visible && isInstance) ((VisabilityToggle) actor).show();
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
