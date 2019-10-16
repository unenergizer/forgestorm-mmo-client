package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.abilities.AbilityManager;
import com.valenguard.client.game.audio.AudioManager;
import com.valenguard.client.game.input.KeyBinds;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.io.type.GameAtlas;

import lombok.Getter;

public class AbilityBar extends VisTable implements Buildable {

    private final AbilityManager abilityManager = Valenguard.getInstance().getAbilityManager();
    private final AudioManager audioManager = Valenguard.getInstance().getAudioManager();

    private final AbilityBar abilityBar;

    @Getter
    private VisImageButton actionOne;
    @Getter
    private VisImageButton actionTwo;
    @Getter
    private VisImageButton actionThree;

    public AbilityBar() {
        this.abilityBar = this;
    }

    @Override
    public Actor build() {

        VisTable buttonTable = new VisTable();

        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEMS, 32);
        actionOne = new VisImageButton(imageBuilder.setRegionName("skill_061").buildTextureRegionDrawable(), "Spell 1 (" + KeyBinds.printKey(KeyBinds.ACTION_1) + ")");
        actionTwo = new VisImageButton(imageBuilder.setRegionName("weapon_arrow_06").buildTextureRegionDrawable(), "Spell 2 (" + KeyBinds.printKey(KeyBinds.ACTION_2) + ")");
        actionThree = new VisImageButton(imageBuilder.setRegionName("quest_004").buildTextureRegionDrawable(), "Spell 3 (" + KeyBinds.printKey(KeyBinds.ACTION_3) + ")");

        actionThree.setColor(Color.RED);
        actionThree.setDisabled(true);

        buttonTable.add(actionOne).padRight(10);
        buttonTable.add(actionTwo).padRight(10);
        buttonTable.add(actionThree);

        add(buttonTable);

        actionOne.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 0, abilityBar, actionOne);
                audioManager.getSoundManager().playSoundFx(AbilityBar.class, (short) 0);
            }
        });

        actionTwo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 1, abilityBar, actionTwo);
                audioManager.getSoundManager().playSoundFx(AbilityBar.class, (short) 0);
            }
        });

        actionThree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                abilityManager.toggleAbility((short) 2, abilityBar, actionThree);
                audioManager.getSoundManager().playSoundFx(AbilityBar.class, (short) 0);
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

    public void setCoolingDown(VisImageButton button) {
        button.setDisabled(true);
        button.setColor(Color.RED);
    }

    public void resetButton(VisImageButton button) {
        button.setDisabled(false);
        button.setColor(Color.WHITE);
    }
}
