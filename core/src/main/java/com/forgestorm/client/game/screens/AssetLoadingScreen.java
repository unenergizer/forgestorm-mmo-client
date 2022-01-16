package com.forgestorm.client.game.screens;

import static com.forgestorm.client.util.Log.println;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.client.io.type.GameFont;
import com.forgestorm.client.io.type.GameSkin;
import com.forgestorm.client.io.type.GameTexture;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.VisUI;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * Loading Screen originally from: https://github.com/Matsemann/libgdx-loading-screen
 */
public class AssetLoadingScreen implements Screen {

    private final FileManager fileManager;

    private Stage stage;

    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;

    private float startX, endX;
    private float percent;

    private Actor loadingBar;

    public AssetLoadingScreen(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void show() {
        // Copy the client-updater.jar
        fileManager.copyClientUpdaterJar();

        // Front-load the assets needed to show the loading screen.
        fileManager.loadAtlas(GameAtlas.LOADING_SCREEN, true);
        TextureAtlas atlas = fileManager.getAtlas(GameAtlas.LOADING_SCREEN);

        // Grab the regions from the atlas and create some images
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));
        loadingBar = new Image(atlas.findRegion("loading-bar1"));

        // Add all the actors to the stage
        stage = new Stage();
        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);


        ////////////////////////////////////////////////////////////////////////////
        ///// "FORCE FINISH" LOADING ITEMS HERE ------------------------------------
        ////////////////////////////////////////////////////////////////////////////

        VisUI.load(Gdx.files.internal(GameSkin.DEFAULT.getFilePath())); // Does not show progress on loading bar yet...
        fileManager.loadRevisionDocumentData();
        fileManager.loadGameWorldListData();
        for (String worldName : fileManager.getGameWorldListData().getGameWorlds()) {
            fileManager.loadGameWorldData(worldName);
        }

        ////////////////////////////////////////////////////////////////////////////
        ///// "NON-FORCE FINISH" LOADING ITEMS HERE --------------------------------
        ////////////////////////////////////////////////////////////////////////////

        // Add everything to be loaded, for instance:
        fileManager.loadTexture(GameTexture.LOGIN_BACKGROUND);
        fileManager.loadTexture(GameTexture.SHADOW);
        fileManager.loadTexture(GameTexture.LOGO_BIG);
//        fileManager.loadTexture(GameTexture.PARALLAX_BACKGROUND);

        fileManager.loadAtlas(GameAtlas.ENTITY_CHARACTER, false);
        fileManager.loadAtlas(GameAtlas.ENTITY_MONSTER, false);
        fileManager.loadAtlas(GameAtlas.SKILL_NODES, false);
        fileManager.loadAtlas(GameAtlas.TILES, false);
        fileManager.loadAtlas(GameAtlas.PIXEL_FX, false);
        fileManager.loadAtlas(GameAtlas.CURSOR, false);
        fileManager.loadAtlas(GameAtlas.ITEMS, false);
        fileManager.loadAtlas(GameAtlas.TOOLS, false);

        fileManager.loadFont(GameFont.PIXEL);

        // Testing custom loader (maintain load order)
        fileManager.loadSoundData();
        fileManager.loadMusicData();
        fileManager.loadFactionData();
        fileManager.loadTileAnimationData();
        fileManager.loadTilePropertiesData();
        fileManager.loadItemStackData();
        fileManager.loadEntityShopData();
        fileManager.loadAbilityData();
        fileManager.loadNetworkSettingsData();
        fileManager.loadRssFeedData();

        checkClientRevision();
    }

    private void checkClientRevision() {
        // Check remote revision
        int remoteRevisionNumber = -1;
        try {
            URL url = new URL("https://forgestorm.com/client_files/Revision.txt");
            Scanner scanner = new Scanner(url.openStream());
            remoteRevisionNumber = scanner.nextInt();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check local revision
        int localRevisionNumber = fileManager.getRevisionDocumentData().getRevisionNumber();
        ClientMain.getInstance().setRemoteRevisionNumber(remoteRevisionNumber);

        if (remoteRevisionNumber != localRevisionNumber) {
            println(getClass(), "REVISION NUMBERS DO NOT MATCH, UPDATER SHOULD BE STARTED!");
            ClientMain.getInstance().setNeedsUpdate(true);
        } else {
            println(getClass(), "REVISION NUMBERS MATCH, CLIENT DOES NOT NEED AN UPDATE!");
            ClientMain.getInstance().setNeedsUpdate(false);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Make the background fill the screen
        screenBg.setSize(stage.getWidth(), stage.getHeight());

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update() returns true when loading is finished.
        if (fileManager.update()) ClientMain.getInstance().initGameManagers();

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, fileManager.getProgress(), 0.1f);

        // Update positions (and size) to match the percentage
        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);
        loadingBg.invalidate();

        // Show the loading screen
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        // Dispose the loading assets as we no longer need them
        fileManager.unloadAsset(GameAtlas.LOADING_SCREEN.getFilePath());
        stage.dispose();
    }

    @Override
    public void dispose() {
    }
}
