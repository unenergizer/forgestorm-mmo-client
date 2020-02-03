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

public class ExperienceBar extends VisWindow implements Buildable {

    private final VisLabel expLabel = new VisLabel();
    private final VisProgressBar expBar = new VisProgressBar(0, 100, 1, false);

    public ExperienceBar() {
        super("");
    }


//    public void updateExp(int exp) {
//        // TODO
//    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        pad(0);
        final GameButtonBar gameButtonBar = stageHandler.getGameButtonBar();

        VisTable visTable = new VisTable();

        final Stack expStack = new Stack();
        expStack.setWidth(gameButtonBar.getAbilityTableWidth());
        expBar.setWidth(gameButtonBar.getAbilityTableWidth());
        expStack.add(expBar);
        expLabel.setText("EXP 0/100");
        expLabel.setAlignment(Alignment.CENTER.getAlignment());
        expStack.add(expLabel);
        visTable.add(expStack);

        add(visTable);

        expBar.setValue(50);

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(gameButtonBar.getX(), gameButtonBar.getHeight() + 15);
            }
        });

        setWidth(gameButtonBar.getWidth());
        setPosition(gameButtonBar.getX(), gameButtonBar.getHeight() + 15);
        pack();
        setVisible(false);
        return this;
    }
}
