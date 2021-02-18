package com.forgestorm.client.game.screens.ui;

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
import com.forgestorm.client.game.screens.ui.actors.dev.world.TileBuildMenu;
import com.forgestorm.client.game.screens.ui.actors.dev.world.WarpEditor;
import com.forgestorm.client.game.screens.ui.actors.dev.world.editor.TilePropertiesEditor;
import com.forgestorm.client.game.screens.ui.actors.dialogue.ChatDialogue;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeEvent;
import com.forgestorm.client.game.screens.ui.actors.game.CreditsWindow;
import com.forgestorm.client.game.screens.ui.actors.game.DebugTable;
import com.forgestorm.client.game.screens.ui.actors.game.EntityDropDownMenu;
import com.forgestorm.client.game.screens.ui.actors.game.EscapeWindow;
import com.forgestorm.client.game.screens.ui.actors.game.ExperienceBar;
import com.forgestorm.client.game.screens.ui.actors.game.FadeWindow;
import com.forgestorm.client.game.screens.ui.actors.game.HelpWindow;
import com.forgestorm.client.game.screens.ui.actors.game.IncomingTradeRequestWindow;
import com.forgestorm.client.game.screens.ui.actors.game.ItemDropDownMenu;
import com.forgestorm.client.game.screens.ui.actors.game.Ping;
import com.forgestorm.client.game.screens.ui.actors.game.PlayerProfileWindow;
import com.forgestorm.client.game.screens.ui.actors.game.StatusBar;
import com.forgestorm.client.game.screens.ui.actors.game.TargetStatusBar;
import com.forgestorm.client.game.screens.ui.actors.game.TradeWindow;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.BagWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.BankWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.CharacterInspectionWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.EquipmentWindow;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.HotBar;
import com.forgestorm.client.game.screens.ui.actors.game.paging.EntityShopWindow;
import com.forgestorm.client.game.screens.ui.actors.game.paging.SkillBookWindow;
import com.forgestorm.client.game.screens.ui.actors.login.ButtonTable;
import com.forgestorm.client.game.screens.ui.actors.login.ClientUpdateWindow;
import com.forgestorm.client.game.screens.ui.actors.login.ConnectionStatusWindow;
import com.forgestorm.client.game.screens.ui.actors.login.CopyrightTable;
import com.forgestorm.client.game.screens.ui.actors.login.LoginTable;
import com.forgestorm.client.game.screens.ui.actors.login.RssAnnouncements;
import com.forgestorm.client.game.screens.ui.actors.login.VersionTable;
import com.forgestorm.client.game.screens.ui.actors.settings.FPSTable;
import com.forgestorm.client.game.screens.ui.actors.settings.MainSettingsWindow;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.PopupMenu;

import lombok.Getter;

@Getter
public class StageHandler implements Disposable {

    public final static float WINDOW_PAD_X = 5;
    public final static float WINDOW_PAD_Y = 10;

    private Stage stage = new Stage();
    private final PreStageEvent preStageEvent = new PreStageEvent(this);
    private final PostStageEvent postStageEvent = new PostStageEvent(this);
    private final DragAndDrop dragAndDrop = new DragAndDrop();
    private final BitmapFont bitmapFont = VisUI.getSkin().getFont("default-font");
    private final Label.LabelStyle markupStyle = new Label.LabelStyle(bitmapFont, null);

    // login
    private final ButtonTable buttonTable = new ButtonTable();
    private final VersionTable versionTable = new VersionTable();
    private final CopyrightTable copyrightTable = new CopyrightTable();
    private final LoginTable loginTable = new LoginTable();
    private final ConnectionStatusWindow connectionStatusWindow = new ConnectionStatusWindow();
    private final RssAnnouncements rssAnnouncements = new RssAnnouncements();
    private final ClientUpdateWindow clientUpdateWindow = new ClientUpdateWindow();

    // character select
    private final CharacterSelectMenu characterSelectMenu = new CharacterSelectMenu();
    private final CharacterCreation characterCreation = new CharacterCreation();
    private final DeleteCharacter deleteCharacter = new DeleteCharacter();

    // game
    private final FadeWindow fadeWindow = new FadeWindow();
    private final HelpWindow helpWindow = new HelpWindow();
    private final CreditsWindow creditsWindow = new CreditsWindow();
    private final EscapeWindow escapeWindow = new EscapeWindow();
    private final BagWindow bagWindow = new BagWindow();
    private final BankWindow bankWindow = new BankWindow();
    private final EquipmentWindow equipmentWindow = new EquipmentWindow();
    private final HotBar hotBar = new HotBar();
    private final ExperienceBar experienceBar = new ExperienceBar();
    private final ChatWindow chatWindow = new ChatWindow();
    private final DebugTable debugTable = new DebugTable();
    private final FPSTable fpsTable = new FPSTable();
    private final EntityDropDownMenu entityDropDownMenu = new EntityDropDownMenu();
    private final ItemDropDownMenu itemDropDownMenu = new ItemDropDownMenu();
    private final TradeWindow tradeWindow = new TradeWindow();
    private final IncomingTradeRequestWindow incomingTradeRequestWindow = new IncomingTradeRequestWindow();
    private final EntityShopWindow pagedItemStackWindow = new EntityShopWindow();
    private final SkillBookWindow spellBookWindow = new SkillBookWindow();
    private final StatusBar statusBar = new StatusBar();
    private final TargetStatusBar targetStatusBar = new TargetStatusBar();
    private final ChatDialogue chatDialogue = new ChatDialogue();
    private final CharacterInspectionWindow characterInspectionWindow = new CharacterInspectionWindow();
    private final PlayerProfileWindow playerProfileWindow = new PlayerProfileWindow();
    private final Ping ping = new Ping();

    private Pixmap bgPixmap;
    private TextureRegionDrawable itemStackCellBackground;

    // developer
    private final DevMenu devMenu = new DevMenu();
    private final EntityEditor entityEditor = new EntityEditor();
    private final ItemStackEditor itemStackEditor = new ItemStackEditor();
    private final PixelFXTest pixelFXTest = new PixelFXTest();
    private final TilePropertiesEditor tilePropertiesEditor = new TilePropertiesEditor();
    private final TileBuildMenu tileBuildMenu = new TileBuildMenu();
    private final WarpEditor warpEditor = new WarpEditor();

    // shared
    private final MainSettingsWindow mainSettingsWindow = new MainSettingsWindow(this);
    private final ColorPickerController colorPickerController = new ColorPickerController();

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
        stage.addActor(clientUpdateWindow.build(this));

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
        stage.addActor(pixelFXTest.build(this));
        stage.addActor(tilePropertiesEditor.build(this));
        stage.addActor(tileBuildMenu.build(this));
        stage.addActor(warpEditor.build(this));
    }

    public void render(float delta) {
        if (debugTable.isVisible()) debugTable.refresh(delta);
        if (fpsTable.isVisible()) fpsTable.refresh();
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    public void resize(int width, int height) {
        // See https://github.com/libgdx/libgdx/issues/3673#issuecomment-177606278
        if (width == 0 && height == 0) return;
        stage.getViewport().update(width, height, true);
        PopupMenu.removeEveryMenu(stage);
        WindowResizeEvent resizeEvent = new WindowResizeEvent();
        //noinspection LibGDXUnsafeIterator
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
                if (ClientMain.getInstance().isNeedsUpdate() && !ClientMain.getInstance().isIgnoreRevisionNumber()) {
                    //GAME CLIENT IS OUT OF DATE!
                    clientUpdateWindow.showRevisionWindow(ClientMain.getInstance().getRemoteRevisionNumber());
                } else {
                    // GAME CLIENT IS UP TO DATE!
                    // Play audio
                    if (musicManager.getAudioPreferences().isPlayLoginScreenMusic()) {
                        if (!musicManager.isMusicPlaying() && ClientMain.getInstance().getGameScreen().isGameFocused()) {
                            musicManager.playMusic(getClass(), (short) 0);
                        }
                    }

                    ClientMain.getInstance().gameWorldQuit();

                    buttonTable.setVisible(true);
                    versionTable.setVersionLabel(ClientMain.getInstance().getRemoteRevisionNumber());
                    versionTable.setVisible(true);
                    copyrightTable.setVisible(true);
                    loginTable.setVisible(true);
                    rssAnnouncements.setVisible(true);

                    FocusManager.switchFocus(stage, loginTable.getUsernameField());
                    stage.setKeyboardFocus(loginTable.getUsernameField());
                    loginTable.resetButton();
                    debugTable.findPosition();
                }
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
                debugTable.findPosition();
                break;
            case GAME:
                musicManager.stopMusic(true);

//                ClientMain.getInstance().getScriptProcessor().setNPCTextDialog(npcTextDialog);

                if (ClientMain.getInstance().isAdmin() || ClientMain.getInstance().isContentDeveloper())
                    devMenu.setVisible(true);
                chatWindow.setVisible(true);
                chatWindow.showChannel(ChatChannelType.GENERAL);
                statusBar.setVisible(true);
                experienceBar.setVisible(true);
                hotBar.setVisible(true);
                ping.setVisible(true);
                debugTable.findPosition();

                FocusManager.resetFocus(stage); // Clear focus after building windows
                break;
        }
    }

    private void hideAllUI() {
        // Set all current actors to non visible
        for (Actor actor : stage.getActors()) {
            if (actor instanceof DebugTable) continue;
            if (actor.isVisible()) actor.setVisible(false);
        }
    }

    public void resetUI() {
        chatWindow.gameQuitReset();
        bagWindow.getItemSlotContainer().resetItemSlotContainer();
        bankWindow.getItemSlotContainer().resetItemSlotContainer();
        equipmentWindow.getItemSlotContainer().resetItemSlotContainer();
        hotBar.getItemSlotContainer().resetItemSlotContainer();
    }
}
