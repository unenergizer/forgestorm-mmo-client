package com.forgestorm.client.game.screens.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.forgestorm.client.game.screens.ui.actors.game.Ping;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.audio.MusicManager;
import com.forgestorm.client.game.screens.UserInterfaceType;
import com.forgestorm.client.game.screens.ui.actors.character.CharacterCreation;
import com.forgestorm.client.game.screens.ui.actors.character.CharacterSelectMenu;
import com.forgestorm.client.game.screens.ui.actors.character.DeleteCharacter;
import com.forgestorm.client.game.screens.ui.actors.dev.ColorPickerController;
import com.forgestorm.client.game.screens.ui.actors.dev.DevMenu;
import com.forgestorm.client.game.screens.ui.actors.dev.PixelFXTest;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.EntityEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.item.ItemStackEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.world.WorldBuilder;
import com.forgestorm.client.game.screens.ui.actors.dialogue.ChatDialogue;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeEvent;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatWindow;
import com.forgestorm.client.game.screens.ui.actors.game.CreditsWindow;
import com.forgestorm.client.game.screens.ui.actors.game.DebugTable;
import com.forgestorm.client.game.screens.ui.actors.game.EntityDropDownMenu;
import com.forgestorm.client.game.screens.ui.actors.game.EscapeWindow;
import com.forgestorm.client.game.screens.ui.actors.game.ExperienceBar;
import com.forgestorm.client.game.screens.ui.actors.game.FadeWindow;
import com.forgestorm.client.game.screens.ui.actors.game.HelpWindow;
import com.forgestorm.client.game.screens.ui.actors.game.IncomingTradeRequestWindow;
import com.forgestorm.client.game.screens.ui.actors.game.ItemDropDownMenu;
import com.forgestorm.client.game.screens.ui.actors.game.PlayerProfileWindow;
import com.forgestorm.client.game.screens.ui.actors.game.StatusBar;
import com.forgestorm.client.game.screens.ui.actors.game.TargetStatusBar;
import com.forgestorm.client.game.screens.ui.actors.game.TradeWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.CharacterInspectionWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.HotBar;
import com.forgestorm.client.game.screens.ui.actors.game.paging.EntityShopWindow;
import com.forgestorm.client.game.screens.ui.actors.game.paging.SkillBookWindow;
import com.forgestorm.client.game.screens.ui.actors.login.ButtonTable;
import com.forgestorm.client.game.screens.ui.actors.login.ConnectionStatusWindow;
import com.forgestorm.client.game.screens.ui.actors.login.CopyrightTable;
import com.forgestorm.client.game.screens.ui.actors.login.LoginTable;
import com.forgestorm.client.game.screens.ui.actors.login.RssAnnouncements;
import com.forgestorm.client.game.screens.ui.actors.login.VersionTable;
import com.forgestorm.client.game.screens.ui.actors.settings.FPSTable;
import com.forgestorm.client.game.screens.ui.actors.settings.MainSettingsWindow;
import com.forgestorm.client.game.scripting.NPCTextDialog;
import com.forgestorm.client.io.type.GameSkin;

import lombok.Getter;

@Getter
public class StageHandler implements Disposable {

    static {
        VisUI.load(Gdx.files.internal(GameSkin.DEFAULT.getFilePath()));
    }

    public final static float WINDOW_PAD_X = 5;
    public final static float WINDOW_PAD_Y = 10;

    private Stage stage = new Stage();
    private PreStageEvent preStageEvent = new PreStageEvent(this);
    private PostStageEvent postStageEvent = new PostStageEvent(this);
    private DragAndDrop dragAndDrop = new DragAndDrop();
    private BitmapFont bitmapFont = VisUI.getSkin().getFont("default-font");
    private Label.LabelStyle markupStyle = new Label.LabelStyle(bitmapFont, null);

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
    private DeleteCharacter deleteCharacter = new DeleteCharacter();

    // game
    private FadeWindow fadeWindow = new FadeWindow();
    private HelpWindow helpWindow = new HelpWindow();
    private CreditsWindow creditsWindow = new CreditsWindow();
    private EscapeWindow escapeWindow = new EscapeWindow();
    private BagWindow bagWindow = new BagWindow();
    private BankWindow bankWindow = new BankWindow();
    private EquipmentWindow equipmentWindow = new EquipmentWindow();
    private HotBar hotBar = new HotBar();
    private ExperienceBar experienceBar = new ExperienceBar();
    private ChatWindow chatWindow = new ChatWindow();
    private DebugTable debugTable = new DebugTable();
    private FPSTable fpsTable = new FPSTable();
    private EntityDropDownMenu entityDropDownMenu = new EntityDropDownMenu();
    private ItemDropDownMenu itemDropDownMenu = new ItemDropDownMenu();
    private TradeWindow tradeWindow = new TradeWindow();
    private IncomingTradeRequestWindow incomingTradeRequestWindow = new IncomingTradeRequestWindow();
    private EntityShopWindow pagedItemStackWindow = new EntityShopWindow();
    private SkillBookWindow spellBookWindow = new SkillBookWindow();
    private StatusBar statusBar = new StatusBar();
    private TargetStatusBar targetStatusBar = new TargetStatusBar();
    private ChatDialogue chatDialogue = new ChatDialogue();
    private CharacterInspectionWindow characterInspectionWindow = new CharacterInspectionWindow();
    private PlayerProfileWindow playerProfileWindow = new PlayerProfileWindow();
    private Ping ping = new Ping();

    private Pixmap bgPixmap;
    private TextureRegionDrawable itemStackCellBackground;
    private NPCTextDialog npcTextDialog = new NPCTextDialog();

    // developer
    private DevMenu devMenu = new DevMenu();
    private EntityEditor entityEditor = new EntityEditor();
    private ItemStackEditor itemStackEditor = new ItemStackEditor();
    private WorldBuilder worldBuilder = new WorldBuilder();
    private PixelFXTest pixelFXTest = new PixelFXTest();

    // shared
    private MainSettingsWindow mainSettingsWindow = new MainSettingsWindow(this);
    private ColorPickerController colorPickerController = new ColorPickerController();

    public StageHandler() {
        dragAndDrop.setDragTime(0);
        bitmapFont.getData().markupEnabled = true;

        // WARNING! Only add actors after markup has been enabled!
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
        stage.addActor(deleteCharacter.build(this));

        // Game
        bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        bgPixmap.setColor(new Color(0, 0, 0, 0.4f)); // ItemStack cell background
        bgPixmap.fill();
        itemStackCellBackground = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

        stage.addActor(fadeWindow.build(this));
        stage.addActor(helpWindow.build(this));
        stage.addActor(creditsWindow.build(this));
        stage.addActor(bagWindow.build(this));
        stage.addActor(bankWindow.build(this));
        stage.addActor(equipmentWindow.build(this));
        stage.addActor(escapeWindow.build(this));
        stage.addActor(hotBar.build(this));
        stage.addActor(experienceBar.build(this));
        stage.addActor(chatWindow.build(this));
        stage.addActor(fpsTable.build(this));
        stage.addActor(entityDropDownMenu.build(this));
        stage.addActor(itemDropDownMenu.build(this));
        stage.addActor(tradeWindow.build(this));
        stage.addActor(incomingTradeRequestWindow.build(this));
//        stage.addActor(new TestToasts(stage));
        stage.addActor(pagedItemStackWindow.build(this));
        stage.addActor(spellBookWindow.build(this));
        stage.addActor(statusBar.build(this));
        stage.addActor(targetStatusBar.build(this));
        stage.addActor(chatDialogue.build(this));
        stage.addActor(npcTextDialog.build(this));
        stage.addActor(characterInspectionWindow.build(this));
        stage.addActor(playerProfileWindow.build(this));
        stage.addActor(ping.build(this));

        // Multi purpose
        stage.addActor(mainSettingsWindow.build(this));
        stage.addActor(debugTable.build(this));

        // Dev tools
        stage.addActor(devMenu.build(this));
        stage.addActor(colorPickerController.build(this));
        stage.addActor(entityEditor.build(this));
        stage.addActor(itemStackEditor.build(this));
        stage.addActor(worldBuilder.build(this));
        stage.addActor(pixelFXTest.build(this));
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
        if (playerProfileWindow != null) {
            playerProfileWindow.dispose();
        }
        if (pixelFXTest != null) {
            pixelFXTest.dispose();
        }
        VisUI.dispose();
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (colorPickerController != null && !colorPickerController.isDisposed())
            colorPickerController.dispose();
        if (bgPixmap != null && !bgPixmap.isDisposed()) bgPixmap.dispose();
        bitmapFont.dispose();
    }

    public void setUserInterface(UserInterfaceType userInterfaceType) {
        ClientMain.getInstance().setUserInterfaceType(userInterfaceType);
        hideAllUI();
        MusicManager musicManager = ClientMain.getInstance().getAudioManager().getMusicManager();

        switch (userInterfaceType) {
            case LOGIN:
                // Play audio
                if (musicManager.getAudioPreferences().isPlayLoginScreenMusic()) {
                    if (!musicManager.isMusicPlaying() && ClientMain.getInstance().getGameScreen().isGameFocused()) {
                        musicManager.playMusic(getClass(), (short) 0);
                    }
                }

                ClientMain.getInstance().gameWorldQuit();

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
                    if (!musicManager.isMusicPlaying() && ClientMain.getInstance().getGameScreen().isGameFocused()) {
                        musicManager.playMusic(getClass(), (short) 0);
                    }
                }

                ClientMain.getInstance().gameWorldQuit();

                connectionStatusWindow.setVisible(false);
                characterSelectMenu.setVisible(true);
                break;
            case GAME:
                musicManager.stopMusic(true);

//                ClientMain.getInstance().getScriptProcessor().setNPCTextDialog(npcTextDialog);

                if (ClientMain.getInstance().isAdmin()) devMenu.setVisible(true);
                chatWindow.setVisible(true);
                chatWindow.showChannel(ChatChannelType.GENERAL);
                statusBar.setVisible(true);
                experienceBar.setVisible(true);
                hotBar.setVisible(true);
                ping.setVisible(true);

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
