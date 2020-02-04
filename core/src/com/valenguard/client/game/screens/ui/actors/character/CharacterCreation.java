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
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.network.game.packet.out.CharacterCreatorPacketOut;
import com.valenguard.client.util.RandomUtil;
import com.valenguard.client.util.color.EyeColorList;
import com.valenguard.client.util.color.HairColorList;
import com.valenguard.client.util.color.SkinColorList;
import com.valenguard.client.util.string.NameGenerator;

import java.io.IOException;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

public class CharacterCreation extends HideableVisWindow implements Buildable {

    private final NameGenerator nameGenerator = new NameGenerator();
    private final Random random = new Random();
    private final CharacterCreation characterCreation;
    private final int maxHairStyles = 14;

    private CharacterPreviewer characterPreviewer = new CharacterPreviewer();
    private VisValidatableTextField characterName;
    private VisTable previewTable = new VisTable();
    private Appearance appearance;
    private byte facingDirection = 0;

    private CharacterOption hairStyleOption = new CharacterOption("Hair Style", (byte) maxHairStyles, (byte) 0); // Number of hair textures
    private CharacterOption hairColorOption = new CharacterOption("Hair Color", (byte) (HairColorList.values().length - 1), (byte) 22);
    private CharacterOption eyeColorOption = new CharacterOption("Eye Color", (byte) (EyeColorList.values().length - 1), (byte) 0);
    private CharacterOption skinColorOption = new CharacterOption("Skin Color", (byte) (SkinColorList.values().length - 1), (byte) 1);

    public CharacterCreation() {
        super("Create a Character");
        this.characterCreation = this;

        // Build default appearance;
        this.appearance = characterPreviewer.generateBasicAppearance();

        // Setup name generate file
        try {
            nameGenerator.changeFile(NameGenerator.NameGenTypes.FANTASY);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        characterOptions.add(buildOptionTable(hairStyleOption)).pad(3).row();
        characterOptions.add(buildOptionTable(hairColorOption)).pad(3).row();
        characterOptions.add(buildOptionTable(eyeColorOption)).pad(3).row();
        characterOptions.add(buildOptionTable(skinColorOption)).pad(3).row();

        VisTextButton randomize = new VisTextButton("Randomize");
        VisTextButton rotate = new VisTextButton("Rotate");
        VisTextButton reset = new VisTextButton("Reset");

        randomize.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                randomizeCharacter();
            }
        });

        rotate.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                facingDirection++;
                if (facingDirection > 3) {
                    // Rotate back to beginning, skipping the NONE value.
                    facingDirection = 0;
                }
                rebuildPreviewTable();
            }
        });

        reset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetCharacter();
            }
        });

        characterOptions.add(randomize).growX().pad(3).row();
        characterOptions.add(rotate).growX().pad(3).row();
        characterOptions.add(reset).growX().pad(3).row();

        rebuildPreviewTable();
        topTable.add(previewTable).expand().fill().pad(3);
        topTable.add(characterOptions).expand().fill().pad(3);

        add(topTable).expand().fill().pad(3).row();

        // Confirm or cancel buttons
        VisTable confirmButtons = new VisTable();
        confirmButtons.add(confirmTable(stageHandler)).expand().fill().align(Alignment.RIGHT.getAlignment()).pad(3);
        add(confirmButtons).expand().fill().pad(3).row();

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

        VisTable visImageTable = characterPreviewer.fillPreviewTable(appearance, MoveDirection.getDirection(facingDirection), 15);
        previewTable.add(visImageTable).row();
    }

    private VisTable confirmTable(final StageHandler stageHandler) {
        VisTable mainTable = new VisTable();

        VisTextButton cancel = new VisTextButton("Cancel");
        VisTextButton submit = new VisTextButton("Submit");
        VisLabel errorLabel = new VisLabel();
        FormValidator validator = new FormValidator(submit, errorLabel);
        characterName = new VisValidatableTextField();
        characterName.setMaxLength(16);
        validator.notEmpty(characterName, "Name must not be empty.");

        VisTable nameTable = new VisTable();
        nameTable.add(new VisLabel("Name:  ")).pad(3);
        nameTable.add(characterName).pad(3);
        VisTextButton generateName = new VisTextButton("Generate Name");
        nameTable.add(generateName).pad(3);

        generateName.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                characterName.setText(nameGenerator.compose(2 + random.nextInt(3)));
            }
        });

        VisTable buttonTable = new VisTable();
        buttonTable.add(cancel).pad(3);
        buttonTable.add(submit).pad(3);

        mainTable.add(nameTable).pad(3).row();
        mainTable.add(buttonTable).align(Alignment.RIGHT.getAlignment()).pad(3).row();
        mainTable.add(errorLabel).align(Alignment.CENTER.getAlignment()).pad(3).row();

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetCharacter();
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
                resetCharacter(); // Clear current design, so next character creation is cleared.
            }
        });

        pack();
        setMovable(false);
        centerWindow();
        return mainTable;
    }

    private VisTable buildOptionTable(final CharacterOption characterOption) {
        // TODO: Left and Right scroll buttons

        VisTable visTable = new VisTable();

        VisTextButton left = new VisTextButton("<");
        final VisLabel optionName = new VisLabel(characterOption.optionName);
        VisTextButton right = new VisTextButton(">");

        visTable.add(left).pad(3);
        visTable.add(optionName).expandX().fill().pad(3);
        visTable.add(right).pad(3);

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
        characterName.clearText();
        hairStyleOption.reset();
        hairColorOption.reset();
        eyeColorOption.reset();
        skinColorOption.reset();
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
        private final byte defaultOption;
        private byte optionValue;

        CharacterOption(String optionName, byte maxOptions, byte defaultOption) {
            this.optionName = optionName;
            this.maxOptions = maxOptions;
            this.defaultOption = defaultOption;

            reset();
        }

        void reset() {
            optionValue = defaultOption;
        }
    }

}
