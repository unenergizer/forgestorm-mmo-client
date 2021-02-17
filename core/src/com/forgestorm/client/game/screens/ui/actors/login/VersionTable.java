package com.forgestorm.client.game.screens.ui.actors.login;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;

public class VersionTable extends VisTable implements Buildable {

    private final VisLabel versionLabel = new VisLabel("ERROR THIS SHOULD NOT BE BLANK");

    public void setVersionLabel(int clientVersionNumber) {
        versionLabel.setText("Client Revision #" + clientVersionNumber);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        add(versionLabel);

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(10, 10);
            }
        });

        pack();
        setPosition(10, 10);
        setVisible(false);
        return this;
    }
}
