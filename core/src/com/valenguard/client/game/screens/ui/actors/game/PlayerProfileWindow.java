package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.Player;
import com.valenguard.client.util.ImageDownloader;

import java.util.HashMap;
import java.util.Map;

public class PlayerProfileWindow extends VisWindow implements Buildable, Disposable {

    private final ImageBuilder imageBuilder = new ImageBuilder();
    private final ImageDownloader imageDownloader = new ImageDownloader();

    private Map<String, Texture> profilePicturesCache = new HashMap<String, Texture>();

    private StageHandler stageHandler;
    private VisImage profilePicture = new VisImage();
    private VisLabel accountName = new VisLabel();

    private String currentPlayer;

    public PlayerProfileWindow() {
        super("NULL : RENAME ME");
    }

    public void openPlayerProfile(Player player) {
        setVisible(true);

        currentPlayer = player.getEntityName();

        getTitleLabel().setText(currentPlayer + "'s Profile");
        accountName.setText("Need to get get from server");

        if (profilePicturesCache.containsKey(currentPlayer)) {
            profilePicture.setDrawable(profilePicturesCache.get(currentPlayer));
        } else {
            imageDownloader.download(this);
        }
    }

    public void updateImage(Texture texture) {
        // Put picture in map
        profilePicturesCache.put(currentPlayer, texture);

        // Display the image
        profilePicture.setDrawable(profilePicturesCache.get(currentPlayer));
        pack();
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        add(accountName).row();
        add(profilePicture);
        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        findPosition();
        setVisible(false);
        addCloseButton();
        return this;
    }

    private void findPosition() {
        centerWindow();
    }


    @Override
    public void dispose() {
        // Dispose all loaded profile picture textures
        for (Texture texture : profilePicturesCache.values()) {
            if (texture != null) texture.dispose();
        }
    }
}
