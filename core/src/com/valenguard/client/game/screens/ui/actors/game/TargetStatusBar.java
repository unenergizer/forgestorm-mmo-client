package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.MovingEntity;
import com.valenguard.client.game.world.entities.PlayerClient;

import lombok.Getter;

public class TargetStatusBar extends VisWindow implements Buildable {

    private final VisLabel targetName = new VisLabel();
    private final VisLabel hpLabel = new VisLabel();
    private final VisProgressBar hpBar = new VisProgressBar(0, 100, 1, false);

    private StageHandler stageHandler;

    @Getter
    private MovingEntity movingEntity;

    public TargetStatusBar() {
        super("");
    }

    public void initTarget(MovingEntity movingEntity) {
        if (movingEntity == null) return;
        this.movingEntity = movingEntity;
        if (!isVisible()) setVisible(true);
        targetName.setText(movingEntity.getEntityName());
        initHealth(movingEntity.getCurrentHealth(), movingEntity.getMaxHealth());
    }

    private void initHealth(int health, int maxHealth) {
        hpBar.setRange(0, maxHealth);
        hpBar.setValue(health);
        hpLabel.setText(health + "/" + maxHealth);
    }

    public void updateHealth(int health) {
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        hpBar.setValue(health);
        hpLabel.setText(health + "/" + playerClient.getMaxHealth());
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        pad(5f);
        VisTable visTable = new VisTable();

        visTable.add(targetName).colspan(2).row();

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

        add(visTable);

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition();
            }
        });

        pack();
        findPosition();
        setVisible(false);
        return this;
    }

    private void findPosition() {
        StatusBar statusBar = stageHandler.getStatusBar();
        float endPosition = statusBar.getX() + statusBar.getWidth();
        setPosition(endPosition + StageHandler.WINDOW_PAD_X, statusBar.getY());
    }
}
