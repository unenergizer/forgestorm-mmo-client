package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.audio.MusicManager;
import com.valenguard.client.game.screens.UserInterfaceType;
import com.valenguard.client.game.screens.ui.actors.character.CharacterCreation;
import com.valenguard.client.game.screens.ui.actors.character.CharacterSelectMenu;
import com.valenguard.client.game.screens.ui.actors.dev.ColorPickerController;
import com.valenguard.client.game.screens.ui.actors.dev.DevMenu;
import com.valenguard.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.valenguard.client.game.screens.ui.actors.dev.item.ItemStackEditor;
import com.valenguard.client.game.screens.ui.actors.dialogue.ChatDialogue;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeEvent;
import com.valenguard.client.game.screens.ui.actors.game.AbilityBar;
import com.valenguard.client.game.screens.ui.actors.game.ButtonBar;
import com.valenguard.client.game.screens.ui.actors.game.ChatWindow;
import com.valenguard.client.game.screens.ui.actors.game.CreditsWindow;
import com.valenguard.client.game.screens.ui.actors.game.DebugTable;
import com.valenguard.client.game.screens.ui.actors.game.EntityDropDownMenu;
import com.valenguard.client.game.screens.ui.actors.game.EntityShopWindow;
import com.valenguard.client.game.screens.ui.actors.game.EscapeWindow;
import com.valenguard.client.game.screens.ui.actors.game.FadeWindow;
import com.valenguard.client.game.screens.ui.actors.game.HelpWindow;
import com.valenguard.client.game.screens.ui.actors.game.IncomingTradeRequestWindow;
import com.valenguard.client.game.screens.ui.actors.game.ItemDropDownMenu;
import com.valenguard.client.game.screens.ui.actors.game.StatusBar;
import com.valenguard.client.game.screens.ui.actors.game.TradeWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.valenguard.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.valenguard.client.game.screens.ui.actors.login.ButtonTable;
import com.valenguard.client.game.screens.ui.actors.login.ConnectionStatusWindow;
import com.valenguard.client.game.screens.ui.actors.login.CopyrightTable;
import com.valenguard.client.game.screens.ui.actors.login.LoginTable;
import com.valenguard.client.game.screens.ui.actors.login.RssAnnouncements;
import com.valenguard.client.game.screens.ui.actors.login.VersionTable;
import com.valenguard.client.game.screens.ui.actors.settings.FPSTable;
import com.valenguard.client.game.screens.ui.actors.settings.MainSettingsWindow;
import com.valenguard.client.game.scripting.NPCTextDialog;
import com.valenguard.client.io.type.GameSkin;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

@Getter
public class StageHandler implements Disposable {

    static {
        VisUI.load(Gdx.files.internal(GameSkin.DEFAULT.getFilePath()));
    }

    private Stage stage = new Stage();
    private PreStageEvent preStageEvent = new PreStageEvent(this);
    private PostStageEvent postStageEvent = new PostStageEvent(this);
    private DragAndDrop dragAndDrop = new DragAndDrop();
    // login
    private ButtonTable buttonTable = new ButtonTable();
    private VersionTable versionTable = new VersionTable();
    private CopyrightTable copyrightTable = new CopyrightTable();
    private LoginTable loginTable = new LoginTable();
    private ConnectionStatusWindow connectionStatusWindow = new ConnectionStatusWindow();
    private RssAnnouncements rssAnnouncements = new RssAnnouncements();
    // character select
    private CharacterSelectMenu characterSelectMenu = new CharacterSelectMenu();
    private CharacterCreation characterCreation = new CharacterCreation();
    // game
    private FadeWindow fadeWindow = new FadeWindow();
    private HelpWindow helpWindow = new HelpWindow();
    private CreditsWindow creditsWindow = new CreditsWindow();
    private EscapeWindow escapeWindow = new EscapeWindow();
    private ChatWindow chatWindow = new ChatWindow();
    private BagWindow bagWindow = new BagWindow();
    private BankWindow bankWindow = new BankWindow();
    private EquipmentWindow equipmentWindow = new EquipmentWindow();
    private ButtonBar buttonBar = new ButtonBar();
    private DebugTable debugTable = new DebugTable();
    private FPSTable fpsTable = new FPSTable();
    private EntityDropDownMenu entityDropDownMenu = new EntityDropDownMenu();
    private ItemDropDownMenu itemDropDownMenu = new ItemDropDownMenu();
    private TradeWindow tradeWindow = new TradeWindow();
    private IncomingTradeRequestWindow incomingTradeRequestWindow = new IncomingTradeRequestWindow();
    private EntityShopWindow entityShopWindow = new EntityShopWindow();
    private StatusBar statusBar = new StatusBar();
    private AbilityBar abilityBar = new AbilityBar();
    private ChatDialogue chatDialogue = new ChatDialogue();

    private Pixmap bgPixmap;
    private TextureRegionDrawable itemStackCellBackground;
    private NPCTextDialog npcTextDialog = new NPCTextDialog();
    // developer
    private DevMenu devMenu = new DevMenu();
    private EntityEditor entityEditor = new EntityEditor();
    private ItemStackEditor itemStackEditor = new ItemStackEditor();
    // shared
    private MainSettingsWindow mainSettingsWindow = new MainSettingsWindow(this);
    private ColorPickerController colorPickerController = new ColorPickerController();

    public StageHandler() {
        dragAndDrop.setDragTime(0);
        addActors();
    }

    public void setViewport(Viewport viewport) {
        stage.setViewport(viewport);
    }

    private void addActors() {

        // Login
        stage.addActor(buttonTable.build(this));
        stage.addActor(versionTable.build(this));
        stage.addActor(copyrightTable.build(this));
        stage.addActor(loginTable.build(this));
        stage.addActor(connectionStatusWindow.build(this));
        stage.addActor(rssAnnouncements.build(this));

        // Character select
        stage.addActor(characterSelectMenu.build(this));
        stage.addActor(characterCreation.build(this));

        // Game
        bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        bgPixmap.setColor(new Color(0, 0, 0, 0.4f)); // ItemStack cell background
        bgPixmap.fill();
        itemStackCellBackground = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

        stage.addActor(fadeWindow.build(this));
        stage.addActor(helpWindow.build(this));
        stage.addActor(creditsWindow.build(this));
        stage.addActor(chatWindow.build(this));
        stage.addActor(bagWindow.build(this));
        stage.addActor(bankWindow.build(this));
        stage.addActor(equipmentWindow.build(this));
        stage.addActor(escapeWindow.build(this));
        stage.addActor(buttonBar.build(this));
        stage.addActor(fpsTable.build(this));
        stage.addActor(entityDropDownMenu.build(this));
        stage.addActor(itemDropDownMenu.build(this));
        stage.addActor(tradeWindow.build(this));
        stage.addActor(incomingTradeRequestWindow.build(this));
//        stage.addActor(new TestToasts(stage));
        stage.addActor(entityShopWindow.build(this));
        stage.addActor(statusBar.build(this));
        stage.addActor(abilityBar.build(this));
        stage.addActor(chatDialogue.build(this));
        stage.addActor(npcTextDialog.build(this));

        // Multi purpose
        stage.addActor(mainSettingsWindow.build(this));
        stage.addActor(debugTable.build(this));

        // Dev tools
        stage.addActor(devMenu.build(this));
        stage.addActor(colorPickerController.build(this));
        stage.addActor(entityEditor.build(this));
        stage.addActor(itemStackEditor.build(this));
    }

    public void render(float delta) {
        if (debugTable != null && debugTable.isVisible()) debugTable.refresh(delta);
        if (fpsTable != null && fpsTable.isVisible()) fpsTable.refresh();
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
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (colorPickerController != null && !colorPickerController.isDisposed())
            colorPickerController.dispose();
        if (bgPixmap != null && !bgPixmap.isDisposed()) bgPixmap.dispose();
    }

    public void setUserInterface(UserInterfaceType userInterfaceType) {
        Valenguard.getInstance().setUserInterfaceType(userInterfaceType);
        hideAllUI();
        MusicManager musicManager = Valenguard.getInstance().getAudioManager().getMusicManager();

        switch (userInterfaceType) {
            case LOGIN:
                // Play audio
                if (musicManager.getAudioPreferences().isPlayLoginScreenMusic()) {
                    if (!musicManager.isMusicPlaying())
                        musicManager.playMusic(getClass(), (short) 0);
                }

                Valenguard.getInstance().gameWorldQuit();

                buttonTable.setVisible(true);
                versionTable.setVisible(true);
                copyrightTable.setVisible(true);
                loginTable.setVisible(true);
                rssAnnouncements.setVisible(true);

                FocusManager.switchFocus(stage, loginTable.getUsernameField());
                stage.setKeyboardFocus(loginTable.getUsernameField());
                loginTable.resetButton();
                break;
            case CHARACTER_SELECT:
                // Play audio
                if (musicManager.getAudioPreferences().isPlayLoginScreenMusic()) {
                    if (!musicManager.isMusicPlaying())
                        musicManager.playMusic(getClass(), (short) 0);
                }

                Valenguard.getInstance().gameWorldQuit();

                characterSelectMenu.setVisible(true);
                break;
            case GAME:
                musicManager.stopMusic(true);
                if (Valenguard.getInstance().isAdmin()) {
                    println(getClass(), "User is an admin!");
                    devMenu.setVisible(true);
                    if (!devMenu.isVisible()) {
                        println(getClass(), "Still not visible....");
                    } else {
                        println(getClass(), "So it is visible???");
                    }
                }

                Valenguard.getInstance().getScriptProcessor().setNPCTextDialog(npcTextDialog);

                chatWindow.setVisible(true);
                buttonBar.setVisible(true);
                statusBar.setVisible(true);
                abilityBar.setVisible(true);

                FocusManager.resetFocus(stage); // Clear focus after building windows
                break;
        }
    }

    private void hideAllUI() {
        // Set all current actors to non visible
        for (Actor actor : stage.getActors()) {
            if (actor.isVisible()) actor.setVisible(false);
        }
    }
}
