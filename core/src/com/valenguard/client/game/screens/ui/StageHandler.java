package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameSkin;
import com.valenguard.client.game.screens.ScreenType;
import com.valenguard.client.game.screens.ui.actors.VisabilityToggle;
import com.valenguard.client.game.screens.ui.actors.WindowResizeEvent;
import com.valenguard.client.game.screens.ui.actors.game.ButtonBar;
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

    private Stage stage;
    private PreStageEvent preStageEvent;
    private PostStageEvent postStageEvent;

    private boolean initialized = false;

    // login
    private ButtonTable buttonTable;
    private VersionTable versionTable;
    private CopyrightTable copyrightTable;
    private LoginTable loginTable;

    // game
    private HelpWindow helpWindow;
    private CreditsWindow creditsWindow;
    private EscapeWindow escapeWindow;
    private ChatWindow chatWindow;
    private InventoryWindow inventoryWindow;
    private ButtonBar buttonBar;
    private DebugTable debugTable;

    // shared
    private MainSettingsWindow mainSettingsWindow;

    public void init(Viewport viewport) {
        if (initialized) return;
        initialized = true;
        if (viewport != null) stage = new Stage(viewport);
        else stage = new Stage();
        preStageEvent = new PreStageEvent(this);
        postStageEvent = new PostStageEvent(this);
        VisUI.load(Gdx.files.internal(GameSkin.DEFAULT.getFilePath()));

        // login
        if (Valenguard.getInstance().getScreenType() == ScreenType.LOGIN) buildLoginScreenUI();

        // game
        if (Valenguard.getInstance().getScreenType() == ScreenType.GAME) buildGameScreenUI();

        // shared
        buildSharedActorsUI();

        FocusManager.resetFocus(stage); // Clear focus after building windows
    }

    private void buildLoginScreenUI() {
        buttonTable = new ButtonTable();
        versionTable = new VersionTable();
        copyrightTable = new CopyrightTable();
        loginTable = new LoginTable();

        stage.addActor(buttonTable.build());
        stage.addActor(versionTable.build());
        stage.addActor(copyrightTable.build());
        stage.addActor(loginTable.build());

        buttonTable.setVisible(true);
        versionTable.setVisible(true);
        copyrightTable.setVisible(true);
        loginTable.setVisible(true);
        FocusManager.switchFocus(stage, loginTable.getAccountField());
        stage.setKeyboardFocus(loginTable.getAccountField());
    }

    private void buildGameScreenUI() {
        buttonTable.setVisible(false);
        versionTable.setVisible(false);
        copyrightTable.setVisible(false);
        loginTable.setVisible(false);

        helpWindow = new HelpWindow();
        creditsWindow = new CreditsWindow();
        escapeWindow = new EscapeWindow();
        chatWindow = new ChatWindow();
        inventoryWindow = new InventoryWindow();
        buttonBar = new ButtonBar();
        debugTable = new DebugTable();

        stage.addActor(helpWindow.build());
        stage.addActor(creditsWindow.build());
        stage.addActor(chatWindow.build());
        stage.addActor(inventoryWindow.build());
        stage.addActor(escapeWindow.build());
        stage.addActor(buttonBar.build());

        chatWindow.fadeIn().setVisible(true);
        buttonBar.setVisible(true);
    }

    private void buildSharedActorsUI() {
        mainSettingsWindow = new MainSettingsWindow();

        stage.addActor(mainSettingsWindow.build());
    }

    // TODO: REMOVE OR CONTINUE TO IMPLEMENT???????????????????
    public void setVisible(Actor actor, boolean visible) {
        boolean isInstance = actor instanceof com.valenguard.client.game.screens.ui.actors.VisabilityToggle;
        if (visible && isInstance)
            ((com.valenguard.client.game.screens.ui.actors.VisabilityToggle) actor).show();
        if (!visible && isInstance) ((VisabilityToggle) actor).hide();
        actor.setVisible(visible);
    }

    public void render(float delta) {
//        if (Valenguard.getInstance().getScreenType() == ScreenType.GAME) debugTable.refresh(delta);
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
        initialized = false;
        VisUI.dispose();
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}
