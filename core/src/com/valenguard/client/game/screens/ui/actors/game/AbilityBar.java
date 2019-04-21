package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.io.type.GameAtlas;

public class AbilityBar extends VisTable implements Buildable {

    private final AbilityBar abilityBar;

    private VisImageButton actionOne;
    private VisImageButton actionTwo;
    private VisImageButton actionThree;

    public AbilityBar() {
        this.abilityBar = this;
    }

    @Override
    public Actor build() {

        VisTable buttonTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);
        actionOne = new VisImageButton(imageBuilder.setRegionName("skill_061").buildTextureRegionDrawable(), "Spell 1");
        actionTwo = new VisImageButton(imageBuilder.setRegionName("weapon_arrow_06").buildTextureRegionDrawable(), "Spell 2");
        actionThree = new VisImageButton(imageBuilder.setRegionName("quest_004").buildTextureRegionDrawable(), "Spell 3");

        actionThree.setColor(Color.RED);
        actionThree.setDisabled(true);

        buttonTable.add(actionOne).padRight(10);
        buttonTable.add(actionTwo).padRight(10);
        buttonTable.add(actionThree);

        add(buttonTable);

        actionOne.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAbilityManager().toggleAbility((short) 0, abilityBar, actionOne);
            }
        });

        actionTwo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAbilityManager().toggleAbility((short) 1, abilityBar, actionTwo);
            }
        });

        actionThree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAbilityManager().toggleAbility((short) 2, abilityBar, actionThree);
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), 10 + 64);
            }
        });

        pack();
        setPosition((Gdx.graphics.getWidth() / 2) - (getWidth() / 2), 10 + 64);
        setVisible(true);
        return this;
    }

    public void setCoolDown(int totalTime) {

    }

    public void tickCoolDown(int timeLeft) {

    }
}
