package com.forgestorm.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.io.File;
import java.io.IOException;

import static com.forgestorm.client.util.Log.println;

public class ClientUpdateWindow extends HideableVisWindow implements Buildable {

    private final VisLabel revisionLabel = new VisLabel("ERROR, SHOULD NOT BE BLANK!");

    public ClientUpdateWindow(ClientMain clientMain) {
        super(clientMain, "Client Update Available");
    }

    public void showRevisionWindow(int revisionNumber) {
        revisionLabel.setText("Revision #:" + revisionNumber);
        ActorUtil.fadeInWindow(this);
    }

    @Override
    public Actor build(StageHandler stageHandler) {

        VisTable labelTable = new VisTable(true);
        labelTable.add(new VisLabel("You must update to continue playing.")).row();
        labelTable.add(new VisLabel("Would you like to update now?")).row();
        labelTable.add(revisionLabel).row();


        VisTable buttonTable = new VisTable(true);
        VisTextButton updateNow = new VisTextButton("Update Now");
        VisTextButton closeGame = new VisTextButton("Exit Game");
        buttonTable.add(updateNow);
        buttonTable.add(closeGame);

        add(labelTable).row();
        add(buttonTable).row();

        updateNow.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: COME BACK AND CLEAN THIS SHIT UP

                String clientUpdaterJar = stageHandler.getClientMain().getFileManager().getClientUpdaterJar();
                println(getClass(), "Client Updater Path: " + clientUpdaterJar);
                // Start client updater
                try {
                    if ((new File(clientUpdaterJar)).exists()) {
                        // Windows
                        println(getClass(), "Command: java -jar " + clientUpdaterJar);
                        Runtime.getRuntime().exec("java -jar " + clientUpdaterJar);
                    } else {
                        File retroMMO = new File(clientUpdaterJar);
                        if (retroMMO.exists()) {
                            Runtime.getRuntime().exec("java -jar " + clientUpdaterJar);
                        } else {
                            Runtime.getRuntime().exec("java -jar " + "../MacOS/client-updater");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Close game client
                Gdx.app.exit();
            }
        });

        closeGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().dispose();
                Gdx.app.exit();
            }
        });

        pack();
        centerWindow();
        setVisible(false);
        stopWindowClickThrough();
        addCloseButton(new CloseButtonCallBack() {
            @Override
            public void closeButtonClicked() {
                stageHandler.getClientMain().dispose();
                Gdx.app.exit();
            }
        });
        return this;
    }
}
