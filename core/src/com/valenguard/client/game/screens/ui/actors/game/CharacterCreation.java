package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.game.rpg.CharacterClasses;
import com.valenguard.client.game.rpg.CharacterGenders;
import com.valenguard.client.game.rpg.CharacterRaces;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.util.ColorList;

public class CharacterCreation extends HideableVisWindow implements Buildable {


    public CharacterCreation() {
        super("Create a Character");
    }

    @Override
    public Actor build() {

        VisTable topTable = new VisTable();
        VisTable optionTable = new VisTable();

        // Adding main character options
        optionTable.add(classSelect()).expand().fill().row();
        optionTable.add(raceSelect()).expand().fill().row();
        optionTable.add(genderSelect()).expand().fill().row();
        optionTable.add(colorSelect()).expand().fill().row();
        optionTable.add(outfitSelect()).expand().fill().row();

        VisTable characterDemo = new VisTable();

        topTable.add(optionTable);
        topTable.add(characterDemo);

        add(topTable).row();

        VisTable bottomTable = new VisTable();

        bottomTable.add(confirmTable()).expand().fill().align(Alignment.RIGHT.getAlignment());

        add(bottomTable).row();

        setResizable(false);
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setVisible(false);
        return this;
    }

    private Actor classSelect() {
        VisSelectBox visSelectBox = new VisSelectBox();
        visSelectBox.setItems(CharacterClasses.values());
        return visSelectBox;
    }

    private Actor raceSelect() {
        VisSelectBox visSelectBox = new VisSelectBox();
        visSelectBox.setItems(CharacterRaces.values());
        return visSelectBox;
    }

    private Actor genderSelect() {
        VisSelectBox visSelectBox = new VisSelectBox();
        visSelectBox.setItems(CharacterGenders.values());
        return visSelectBox;
    }

    private Actor colorSelect() {
        VisSelectBox visSelectBox = new VisSelectBox();
        visSelectBox.setItems(ColorList.values());
        return visSelectBox;
    }

    private Actor outfitSelect() {
        VisTable visTable = new VisTable();



        return visTable;
    }

    private Actor confirmTable() {
        return null;
    }

}
