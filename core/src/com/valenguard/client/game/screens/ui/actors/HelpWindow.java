package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.game.screens.ui.Buildable;
import com.valenguard.client.game.screens.ui.HideableVisWindow;

public class HelpWindow extends HideableVisWindow implements Buildable {

    public HelpWindow() {
        super("Help Menu");
    }

    @Override
    public Actor build() {
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

        pack();
        centerWindow();
        setVisible(false);
        return this;
    }
}
