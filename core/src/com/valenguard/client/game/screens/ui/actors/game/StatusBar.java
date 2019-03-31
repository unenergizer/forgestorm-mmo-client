package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.PlayerClient;

public class StatusBar extends VisWindow implements Buildable {

    private final float posX = 5;
    private final float posY = Gdx.graphics.getHeight() - 5;

    private final VisLabel hpLabel = new VisLabel();
    private final VisProgressBar hpBar = new VisProgressBar(0, 100, 1, false);

    private final VisLabel mpLabel = new VisLabel();
    private final VisProgressBar mpBar = new VisProgressBar(0, 100, 1, false);

    private final VisLabel expLabel = new VisLabel();
    private final VisProgressBar expBar = new VisProgressBar(0, 100, 1, false);

    public StatusBar() {
        super("");
    }

    public void initHealth(int health, int maxHealth) {
        hpBar.setRange(0, maxHealth);
        hpBar.setValue(health);
        hpLabel.setText(health + "/" + maxHealth);
    }

    public void updateHealth(int health) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        hpBar.setValue(health);
        hpLabel.setText(health + "/" + playerClient.getMaxHealth());
    }

    public void updateMana(int mana) {
        // TODO
    }

    public void updateExp(int exp) {
        // TODO
    }

    @Override
    public Actor build() {
        pad(5f);
        VisTable visTable = new VisTable();

        // HP ----------------------------------------
        final VisLabel hp = new VisLabel("HP: ");
        visTable.add(hp);

        final Stack hpStack = new Stack();
        hpStack.add(hpBar);
        hpLabel.setText("0/100");
        hpLabel.setAlignment(Alignment.CENTER.getAlignment());
        hpStack.add(hpLabel);
        visTable.add(hpStack);
        visTable.row();

        // MP ----------------------------------------
        final VisLabel mp = new VisLabel("MP: ");
        visTable.add(mp);

        final Stack mpStack = new Stack();
        mpStack.add(mpBar);
        mpLabel.setText("0/100");
        mpLabel.setAlignment(Alignment.CENTER.getAlignment());
        mpStack.add(mpLabel);
        visTable.add(mpStack);
        visTable.row();

        // EXP ---------------------------------------
        final VisLabel exp = new VisLabel("EXP: ");
        visTable.add(exp);

        final Stack expStack = new Stack();
        expStack.add(expBar);
        expLabel.setText("0/100");
        expLabel.setAlignment(Alignment.CENTER.getAlignment());
        expStack.add(expLabel);
        visTable.add(expStack);
        visTable.row();

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
