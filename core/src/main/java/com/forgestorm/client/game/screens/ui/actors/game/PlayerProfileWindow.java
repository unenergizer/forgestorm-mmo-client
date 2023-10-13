package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.login.ButtonTable;
import com.forgestorm.client.game.world.entities.Player;
import com.forgestorm.client.network.game.packet.in.ProfileRequestPacketIn;
import com.forgestorm.client.network.game.packet.out.ProfileRequestPacketOut;
import com.forgestorm.client.util.ImageDownloader;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.HashMap;
import java.util.Map;

import static com.forgestorm.client.util.Log.println;

public class PlayerProfileWindow extends HideableVisWindow implements Buildable, Disposable {

    private static final boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;
    private final ImageDownloader imageDownloader = new ImageDownloader();

    private final Map<String, Texture> profilePicturesCache = new HashMap<String, Texture>();

    private final VisImage profilePicture = new VisImage();
    private final VisLabel accountName = new VisLabel();
    private final VisLabel messageCount = new VisLabel();
    private final VisLabel trophyPoints = new VisLabel();
    private final VisLabel reactionScore = new VisLabel();
    private final VisTextButton profileURLButton = new VisTextButton("NULL : TEXT BUTTON NOT SET");
    private String profileURL = "";

    private String currentPlayer;

    public PlayerProfileWindow(ClientMain clientMain) {
        super(clientMain, "NULL : WINDOW NAME NOT SET");
        this.clientMain = clientMain;
    }

    /**
     * Request via packet to get information on the player provided.
     *
     * @param playerToGetProfileFor Player to get profile data for.
     */
    public void requestPlayerProfile(Player playerToGetProfileFor) {
        currentPlayer = playerToGetProfileFor.getEntityName();
        new ProfileRequestPacketOut(clientMain, playerToGetProfileFor.getServerEntityID()).sendPacket();
    }

    /**
     * If we get a response from the server, let's update the windows actors with the
     * data from the forum.
     *
     * @param xenforoProfilePacket The class that holds forum data for said user.
     */
    public void packetResponse(ProfileRequestPacketIn.XenforoProfilePacket xenforoProfilePacket) {
        setVisible(true);

        // Update actor information
        getTitleLabel().setText(currentPlayer + "'s Profile");
        accountName.setText("Account Name: " + xenforoProfilePacket.getAccountName());
        messageCount.setText("Message Count: " + xenforoProfilePacket.getMessageCount());
        trophyPoints.setText("Trophy Points: " + xenforoProfilePacket.getTrophyPoints());
        reactionScore.setText("Reaction Score: " + xenforoProfilePacket.getReactionScore());
        profileURLButton.setText("Visit " + xenforoProfilePacket.getAccountName() + "'s Profile");
        profileURL = "https://forgestorm.com/members/" + xenforoProfilePacket.getAccountName() + "." + xenforoProfilePacket.getXenforoUserID() + "/";


        // Update picture
        if (profilePicturesCache.containsKey(currentPlayer)) {
            // Picture exists in cache
            profilePicture.setDrawable(profilePicturesCache.get(currentPlayer));
        } else {
            // Download Picture
            String pictureURL;

            if (xenforoProfilePacket.getGravatarHash().isEmpty()) {
                // No gravatar, try to get avatar from ForgeStorm website
                int folderID = (int) Math.floor(xenforoProfilePacket.getXenforoUserID() / 1000f);
                pictureURL = "https://forgestorm.com/data/avatars/s/" + folderID + "/" + xenforoProfilePacket.getXenforoUserID() + ".jpg";
            } else {
                // Try to get gravatar url
                pictureURL = "https://www.gravatar.com/avatar/" + xenforoProfilePacket.getGravatarHash() + ".jpg";
            }

            println(getClass(), "Picture URL: " + pictureURL, false, PRINT_DEBUG);

            imageDownloader.download(this, pictureURL);
        }
    }

    /**
     * The users profile picture.
     *
     * @param texture The profile picture.
     */
    public void updateProfilePicture(Texture texture) {
        // Put picture in map
        profilePicturesCache.put(currentPlayer, texture);

        // Display the image
        profilePicture.setDrawable(profilePicturesCache.get(currentPlayer));
        pack();
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        add(accountName).row();
        add(profilePicture).row();
        add(messageCount).row();
        add(trophyPoints).row();
        add(reactionScore).row();
        add(profileURLButton).row();

        profileURLButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(ButtonTable.class, (short) 0);
                Gdx.net.openURI(profileURL);
            }
        });

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
        profilePicturesCache.clear();
    }
}
