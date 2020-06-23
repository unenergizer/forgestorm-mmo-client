package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;

public class HelpWindow extends HideableVisWindow implements Buildable {

    public HelpWindow() {
        super("Help Menu");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(false);

        VisTable mainTable = new VisTable();
        mainTable.pad(3);

        mainTable.add(new VisLabel("Forums:")).left().padBottom(5).row();
        mainTable.add(new LinkLabel("http://valenguard.com/")).padBottom(15).row();
        mainTable.add(new VisLabel("Discord:")).left().padBottom(5).row();
        mainTable.add(new LinkLabel("https://discord.gg/NhtvMgR")).row();

        add(mainTable);

        stopWindowClickThrough();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        pack();
        centerWindow();
        setVisible(false);
        return this;
    }
}
