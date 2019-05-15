package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.valenguard.client.game.rpg.CharacterClasses;
import com.valenguard.client.game.rpg.CharacterGenders;
import com.valenguard.client.game.rpg.CharacterRaces;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.network.game.packet.out.CharacterCreatorPacketOut;
import com.valenguard.client.util.color.LibGDXColorList;

public class CharacterCreation extends HideableVisWindow implements Buildable {

    private final CharacterCreation characterCreation;

    private VisSelectBox characterClass;
    private VisSelectBox characterRace;
    private VisSelectBox characterGender;
    private VisSelectBox characterColor;
    private VisTextField characterName;

    public CharacterCreation() {
        super("Create a Character");
        this.characterCreation = this;
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

        topTable.add(optionTable).expand().fill();
        topTable.add(characterDemo).expand().fill();

        add(topTable).expand().fill().row();

        VisTable bottomTable = new VisTable();

        bottomTable.add(confirmTable()).expand().fill().align(Alignment.RIGHT.getAlignment());

        add(bottomTable).expand().fill().row();

        setResizable(false);
        setVisible(false);
        pack();
        return this;
    }

    private Actor classSelect() {
        characterClass = new VisSelectBox();
        characterClass.setItems(CharacterClasses.values());
        return characterClass;
    }

    private Actor raceSelect() {
        characterRace = new VisSelectBox();
        characterRace.setItems(CharacterRaces.values());
        return characterRace;
    }

    private Actor genderSelect() {
        characterGender = new VisSelectBox();
        characterGender.setItems(CharacterGenders.values());
        return characterGender;
    }

    private Actor colorSelect() {
        characterColor = new VisSelectBox();
        characterColor.setItems(LibGDXColorList.values());
        return characterColor;
    }

    private Actor outfitSelect() {
        VisTable visTable = new VisTable();


        return visTable;
    }

    private Actor confirmTable() {
        VisTable visTable = new VisTable();
        characterName = new VisTextField();
        visTable.add(characterName).row();

        VisTextButton visTextButton = new VisTextButton("Submit");
        visTable.add(visTextButton);

        visTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new CharacterCreatorPacketOut((CharacterClasses) characterClass.getSelected(),
                        (CharacterGenders) characterGender.getSelected(),
                        (CharacterRaces) characterRace.getSelected(),
                        (LibGDXColorList) characterColor.getSelected(),
                        characterName.getText()).sendPacket();
                ActorUtil.fadeOutWindow(characterCreation);
            }
        });
        return visTable;
    }

}
