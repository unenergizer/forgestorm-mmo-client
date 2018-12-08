package com.valenguard.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.game.screens.ui.Buildable;
import com.valenguard.client.game.screens.ui.HideableVisWindow;

public class CopyrightTable extends VisTable implements Buildable {

    @Override
    public Actor build() {
        add(new VisLabel("CopyrightTable © 2017-2018 Valenguard MMO. All Rights Reserved."));

        pack();
        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2) + 10, 10);
        setVisible(false);
        return this;
    }
}
