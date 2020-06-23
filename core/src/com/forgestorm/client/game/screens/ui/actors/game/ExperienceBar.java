package com.forgestorm.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.draggable.HotBar;

import static com.forgestorm.client.util.Log.println;


public class ExperienceBar extends VisWindow implements Buildable {

    private static final boolean PRINT_DEBUG = false;

    private final VisLabel expLabel = new VisLabel();
    private final VisProgressBar expBar = new VisProgressBar(0, 100, 1, false);

    public ExperienceBar() {
        super("");
    }

    public void updateExp(float percentToLevel, int currentExp, int currentLevel, int nextLevelExp) {
        String exp = "LVL " + currentLevel + ", EXP " + currentExp + "/" + nextLevelExp;
        expLabel.setText(exp);
        expBar.setValue(percentToLevel);

        println(getClass(), exp, false, PRINT_DEBUG);
        println(getClass(), "Visible: " + isVisible(), false, PRINT_DEBUG);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        pad(0);
        final HotBar hotBar = stageHandler.getHotBar();
        float width = hotBar.getItemStackTableWidth();
        VisTable visTable = new VisTable();

        final Stack expStack = new Stack();
        expStack.setWidth(width);
        expBar.setWidth(width);
        expStack.add(expBar);
        expLabel.setText("EXP 0/0");
        expLabel.setAlignment(Alignment.CENTER.getAlignment());
        expStack.add(expLabel);
        visTable.add(expStack).padLeft(6).padRight(6).width(width - 12);

        add(visTable);

        expBar.setValue(0);

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition(hotBar.getX(), hotBar.getHeight() + 15);
            }
        });

        setWidth(hotBar.getWidth());
        setPosition(hotBar.getX(), hotBar.getHeight() + 15);
        pack();
        setVisible(false);
        return this;
    }
}
