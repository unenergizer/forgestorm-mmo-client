package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.game.entities.EntityManager;
import com.valenguard.client.game.entities.PlayerClient;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class StatusBar extends VisWindow implements Buildable {

    private final float posX = 5;
    private final float posY = Gdx.graphics.getHeight() - 5;

    private final VisLabel stackedHP = new VisLabel();
    private VisProgressBar visProgressBar;

    public StatusBar() {
        super("");
    }

    public void init(int health, int maxHealth) {
        visProgressBar.setStepSize(1);
        visProgressBar.setValue(health);
        visProgressBar.setRange(0, maxHealth);
        stackedHP.setText(health + "/" + maxHealth);
    }

    public void updateHealth(int health) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        visProgressBar.setValue(health);
        stackedHP.setText(health + "/" + playerClient.getMaxHealth());
    }

    public void updateMana(int mana) {
        // TODO
    }

    @Override
    public Actor build() {
        pad(5f);
        VisTable visTable = new VisTable();

        VisLabel hp = new VisLabel("HP: ");
        visTable.add(hp);

        Stack stack = new Stack();

        visProgressBar = new VisProgressBar(0, 100, 1, false);
        visProgressBar.setValue(100);
        stack.add(visProgressBar);

        stackedHP.setText("100/100");
        stackedHP.setAlignment(Alignment.CENTER.getAlignment());
        stack.add(stackedHP);

        visTable.add(stack);
        add(visTable);

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(posX, posY - getHeight());
            }
        });

        pack();
        setPosition(posX, posY - getHeight());
        setVisible(true);
        return this;
    }
}
