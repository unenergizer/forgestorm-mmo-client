package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class CreditsWindow extends HideableVisWindow implements Buildable {

    public CreditsWindow() {
        super("Credits");
    }

    @Override
    public Actor build() {
        TableUtils.setSpacingDefaults(this);
        addCloseButton();
        setResizable(false);

        Color titleColor = new Color(125 / 255f, 242 / 255f, 207 / 255f, 1f);
        VisTable mainTable = new VisTable();
        mainTable.pad(3);

        // Programmers:
        VisLabel titleLabel = new VisLabel("Programming:");
        titleLabel.setColor(titleColor);
        mainTable.add(titleLabel).left().padBottom(5).row();

        VisTable table = new VisTable(true);
        table.add(new VisLabel("hposej"));
        table.add(new VisLabel("unenergizer"));
        mainTable.add(table).padBottom(15).row();

        // Graphics:
        titleLabel = new VisLabel("Graphics:");
        titleLabel.setColor(titleColor);
        mainTable.add(titleLabel).left().padBottom(5).row();

        table = new VisTable(true);
        table.add(new VisLabel("7soul"));
        mainTable.add(table).padBottom(15).row();

        // Libraries:
        titleLabel = new VisLabel("Programming Libraries:");
        titleLabel.setColor(titleColor);
        mainTable.add(titleLabel).left().padBottom(5).row();

        table = new VisTable(true);
        table.add(new LinkLabel("LibGDX", "https://libgdx.badlogicgames.com/"));
        table.add(new LinkLabel("VisUI", "https://github.com/kotcrab/vis-editor/wiki/VisUI"));
        table.add(new LinkLabel("Google Guava", "https://github.com/google/guava"));
        mainTable.add(table).row();

        add(mainTable);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
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