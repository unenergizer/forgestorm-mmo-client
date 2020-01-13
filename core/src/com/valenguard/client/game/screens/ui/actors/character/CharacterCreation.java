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
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.ProperName;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.network.game.packet.out.CharacterCreatorPacketOut;
import com.valenguard.client.util.RandomUtil;
import com.valenguard.client.util.color.EyeColorList;
import com.valenguard.client.util.color.HairColorList;
import com.valenguard.client.util.color.SkinColorList;

import lombok.Getter;
import lombok.Setter;

public class CharacterCreation extends HideableVisWindow implements Buildable {

    private final CharacterCreation characterCreation;
    private final int maxHairStyles = 14;

    private CharacterPreviewer characterPreviewer = new CharacterPreviewer();
    private VisValidatableTextField characterName;
    private VisTable previewTable = new VisTable();
    private Appearance appearance;

    private CharacterOption hairStyleOption = new CharacterOption("Hair Style", (byte) maxHairStyles); // Number of hair textures
    private CharacterOption hairColorOption = new CharacterOption("Hair Color", (byte) (HairColorList.values().length - 1));
    private CharacterOption eyeColorOption = new CharacterOption("Eye Color", (byte) (EyeColorList.values().length - 1));
    private CharacterOption skinColorOption = new CharacterOption("Skin Color", (byte) (SkinColorList.values().length - 1));

    public CharacterCreation() {
        super("Create a Character");
        this.characterCreation = this;

        // Build default appearance;
        this.appearance = characterPreviewer.generateBasicAppearance();
    }

    /**
     * TODO: Pick Shirt Color
     * TODO: Pick Pants Color
     * TODO: Pick Shoes Color
     */

    @Override
    public Actor build(final StageHandler stageHandler) {

        VisTable topTable = new VisTable();

        // Adding main character options
        VisTable characterOptions = new VisTable();

        characterOptions.add(buildOptionTable(hairStyleOption)).row();
        characterOptions.add(buildOptionTable(hairColorOption)).row();
        characterOptions.add(buildOptionTable(eyeColorOption)).row();
        characterOptions.add(buildOptionTable(skinColorOption)).row();

        VisTextButton randomize = new VisTextButton("Randomize");
        VisTextButton reset = new VisTextButton("Reset");

        randomize.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                randomizeCharacter();
            }
        });

        reset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetCharacter();
            }
        });

        characterOptions.add(randomize).row();
        characterOptions.add(reset).row();

        rebuildPreviewTable();
        topTable.add(previewTable).expand().fill();
        topTable.add(characterOptions).expand().fill();

        add(topTable).expand().fill().row();

        // Confirm or cancel buttons
        VisTable confirmButtons = new VisTable();
        confirmButtons.add(confirmTable(stageHandler)).expand().fill().align(Alignment.RIGHT.getAlignment());
        add(confirmButtons).expand().fill().row();

        setResizable(false);
        setVisible(false);
        pack();
        centerWindow();
        return this;
    }

    private void rebuildPreviewTable() {
        previewTable.clear();

        appearance.setHairTexture(hairStyleOption.optionValue);
        appearance.setHairColor(HairColorList.getColorFromOrdinal(hairColorOption.optionValue));
        appearance.setEyeColor(EyeColorList.getColorFromOrdinal(eyeColorOption.optionValue));
        appearance.setSkinColor(SkinColorList.getColorFromOrdinal(skinColorOption.optionValue));

        VisTable visImageTable = characterPreviewer.fillPreviewTable(appearance, MoveDirection.SOUTH, 15);
        previewTable.add(visImageTable);
    }

    private VisTable confirmTable(final StageHandler stageHandler) {
        VisTable mainTable = new VisTable();

        VisTextButton cancel = new VisTextButton("Cancel");
        VisTextButton submit = new VisTextButton("Submit");
        VisLabel errorLabel = new VisLabel();
        FormValidator validator = new FormValidator(submit, errorLabel);
        characterName = new VisValidatableTextField(new ProperName());
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
                resetCharacter();
                characterName.setText("");
                ActorUtil.fadeOutWindow(characterCreation);
                ActorUtil.fadeInWindow(stageHandler.getCharacterSelectMenu());
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
            }
        });

        submit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new CharacterCreatorPacketOut(characterName.getText(), hairStyleOption.optionValue, hairColorOption.optionValue, eyeColorOption.optionValue, skinColorOption.optionValue).sendPacket();
                ActorUtil.fadeOutWindow(characterCreation);
                ActorUtil.fadeInWindow(stageHandler.getCharacterSelectMenu());
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
            }
        });
        pack();
        return mainTable;
    }

    private VisTable buildOptionTable(final CharacterOption characterOption) {
        // TODO: Left and Right scroll buttons

        VisTable visTable = new VisTable();

        VisTextButton left = new VisTextButton("<");
        final VisLabel optionName = new VisLabel(characterOption.optionName);
        VisTextButton right = new VisTextButton(">");

        visTable.add(left);
        visTable.add(optionName).expandX().fill();
        visTable.add(right);

        left.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                byte optionValue = characterOption.getOptionValue();

                characterOption.setOptionValue((byte) (optionValue - 1));

                if (characterOption.getOptionValue() < 0) {
                    characterOption.setOptionValue(characterOption.getMaxOptions());
                }
                rebuildPreviewTable();
            }
        });
        right.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                byte optionValue = characterOption.getOptionValue();

                characterOption.setOptionValue((byte) (optionValue + 1));

                if (characterOption.getOptionValue() > characterOption.getMaxOptions()) {
                    characterOption.setOptionValue((byte) 0);
                }
                rebuildPreviewTable();
            }
        });

        return visTable;
    }

    private void resetCharacter() {
        hairStyleOption.setOptionValue((byte) 0);
        hairColorOption.setOptionValue((byte) 0);
        eyeColorOption.setOptionValue((byte) 0);
        skinColorOption.setOptionValue((byte) 0);
        rebuildPreviewTable();
    }

    private void randomizeCharacter() {
        hairStyleOption.setOptionValue((byte) RandomUtil.getNewRandom(0, maxHairStyles));
        hairColorOption.setOptionValue((byte) RandomUtil.getNewRandom(0, HairColorList.values().length - 1));
        eyeColorOption.setOptionValue((byte) RandomUtil.getNewRandom(0, EyeColorList.values().length - 1));
        skinColorOption.setOptionValue((byte) RandomUtil.getNewRandom(0, SkinColorList.values().length - 1));
        rebuildPreviewTable();
    }

    @Getter
    @Setter
    class CharacterOption {
        private final String optionName;
        private final byte maxOptions;
        private byte optionValue;

        CharacterOption(String optionName, byte maxOptions) {
            this.optionName = optionName;
            this.maxOptions = maxOptions;
        }
    }

}
