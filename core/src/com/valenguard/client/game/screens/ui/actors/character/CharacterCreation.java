package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.network.game.packet.out.CharacterCreatorPacketOut;

public class CharacterCreation extends HideableVisWindow implements Buildable {

    private final CharacterCreation characterCreation;

    //    private VisSelectBox characterClass;
//    private VisSelectBox characterRace;
//    private VisSelectBox characterGender;
//    private VisSelectBox characterColor;
    private VisValidatableTextField characterName;

    public CharacterCreation() {
        super("Create a Character");
        this.characterCreation = this;
    }

    @Override
    public Actor build() {

        VisTable topTable = new VisTable();
//        VisTable optionTable = new VisTable();

        // Adding main character options
//        optionTable.add(classSelect()).expand().fill().row();
//        optionTable.add(raceSelect()).expand().fill().row();
//        optionTable.add(genderSelect()).expand().fill().row();
//        optionTable.add(colorSelect()).expand().fill().row();
//        optionTable.add(outfitSelect()).expand().fill().row();

        VisTable characterDemo = new VisTable();

//        topTable.add(optionTable).expand().fill();
        topTable.add(characterDemo).expand().fill();

        add(topTable).expand().fill().row();

        VisTable bottomTable = new VisTable();

        bottomTable.add(confirmTable()).expand().fill().align(Alignment.RIGHT.getAlignment());

        add(bottomTable).expand().fill().row();

        setResizable(false);
        setVisible(false);
        pack();
        centerWindow();
        return this;
    }

//    private Actor classSelect() {
//        characterClass = new VisSelectBox();
//        characterClass.setItems(CharacterClasses.values());
//        return characterClass;
//    }
//
//    private Actor raceSelect() {
//        characterRace = new VisSelectBox();
//        characterRace.setItems(CharacterRaces.values());
//        return characterRace;
//    }
//
//    private Actor genderSelect() {
//        characterGender = new VisSelectBox();
//        characterGender.setItems(CharacterGenders.values());
//        return characterGender;
//    }
//
//    private Actor colorSelect() {
//        characterColor = new VisSelectBox();
//        characterColor.setItems(LibGDXColorList.values());
//        return characterColor;
//    }
//
//    private Actor outfitSelect() {
//        VisTable visTable = new VisTable();
//
//
//        return visTable;
//    }

    private VisTable confirmTable() {
        VisTable mainTable = new VisTable();

        VisTextButton cancel = new VisTextButton("Cancel");
        VisTextButton submit = new VisTextButton("Submit");
        VisLabel errorLabel = new VisLabel();
        FormValidator validator = new FormValidator(submit, errorLabel);
        characterName = new VisValidatableTextField();
        characterName.setMaxLength(16);
        validator.notEmpty(characterName, "Name must not be empty.");

        VisTable nameTable = new VisTable();
        nameTable.add(new VisLabel("Name:  "));
        nameTable.add(characterName);

        VisTable buttonTable = new VisTable();
        buttonTable.add(cancel);
        buttonTable.add(submit);

        mainTable.add(nameTable).row();
        mainTable.add(buttonTable).align(Alignment.RIGHT.getAlignment()).row();
        mainTable.add(errorLabel).align(Alignment.CENTER.getAlignment()).row();

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                characterName.setText("");
                ActorUtil.fadeOutWindow(characterCreation);
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
            }
        });

        submit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new CharacterCreatorPacketOut(characterName.getText()).sendPacket();
                ActorUtil.fadeOutWindow(characterCreation);
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
            }
        });
        pack();
        return mainTable;
    }

}
