package com.forgestorm.client.game.screens.ui.actors.character;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.world.entities.Appearance;
import com.forgestorm.client.network.game.packet.out.CharacterCreatorPacketOut;
import com.forgestorm.client.util.string.NameGenerator;
import com.forgestorm.shared.network.game.CharacterCreatorResponses;
import com.forgestorm.shared.util.RandomNumberUtil;
import com.forgestorm.shared.util.color.EyeColorList;
import com.forgestorm.shared.util.color.HairColorList;
import com.forgestorm.shared.util.color.SkinColorList;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.Random;

public class CharacterCreation extends HideableVisWindow implements Buildable {

    private final ClientMain clientMain;
    private final NameGenerator nameGenerator = new NameGenerator();
    private final Random random = new Random();
    private final CharacterCreation characterCreation;
    private final int maxHairStyles = 14;

    private StageHandler stageHandler;
    private final CharacterPreviewer characterPreviewer;
    private VisValidatableTextField characterName;
    private final Appearance appearance;

    private final CharacterOption hairStyleOption = new CharacterOption("Hair Style", (byte) maxHairStyles, (byte) 0); // Number of hair textures
    private final CharacterOption hairColorOption = new CharacterOption("Hair Color", (byte) (HairColorList.values().length - 1), (byte) 22);
    private final CharacterOption eyeColorOption = new CharacterOption("Eye Color", (byte) (EyeColorList.values().length - 1), (byte) 0);
    private final CharacterOption skinColorOption = new CharacterOption("Skin Color", (byte) (SkinColorList.values().length - 1), (byte) 1);

    private final VisTextButton submit = new VisTextButton("Submit");
    private final VisLabel errorLabel = new VisLabel();

    public CharacterCreation(ClientMain clientMain) {
        super(clientMain, "Create a Character");
        this.clientMain = clientMain;
        this.characterCreation = this;
        characterPreviewer = new CharacterPreviewer(clientMain, 15);

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
        this.stageHandler = stageHandler;
        VisTable topTable = new VisTable();

        // Adding main character options
        VisTable characterOptions = new VisTable();

        characterOptions.add(buildOptionTable(hairStyleOption)).pad(3).row();
        characterOptions.add(buildOptionTable(hairColorOption)).pad(3).row();
        characterOptions.add(buildOptionTable(eyeColorOption)).pad(3).row();
        characterOptions.add(buildOptionTable(skinColorOption)).pad(3).row();

        VisTextButton randomize = new VisTextButton("Randomize");
        VisTextButton reset = new VisTextButton("Reset");

        randomize.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                randomizeCharacter();
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 19);
            }
        });

        reset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetCharacter();
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
            }
        });

        characterOptions.add(randomize).growX().pad(3).row();
        characterOptions.add(reset).growX().pad(3).row();

        topTable.add(characterPreviewer.generatePreviewTable()).expand().fill().pad(3);
        topTable.add(characterOptions).expand().fill().pad(3);

        add(topTable).expand().fill().pad(3).row();

        // Confirm or cancel buttons
        VisTable confirmButtons = new VisTable();
        confirmButtons.add(confirmTable(stageHandler)).expand().fill().align(Alignment.RIGHT.getAlignment()).pad(3);
        add(confirmButtons).expand().fill().pad(3).row();

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        setResizable(false);
        setVisible(false);
        pack();
        centerWindow();
        return this;
    }

    private void rebuildPreviewTable() {
        appearance.setHairTexture(hairStyleOption.optionValue);
        appearance.setHairColor(HairColorList.getColorFromOrdinal(hairColorOption.optionValue));
        appearance.setEyeColor(EyeColorList.getColorFromOrdinal(eyeColorOption.optionValue));
        appearance.setSkinColor(SkinColorList.getColorFromOrdinal(skinColorOption.optionValue));

        characterPreviewer.generateCharacterPreview(appearance, null);
    }

    private VisTable confirmTable(final StageHandler stageHandler) {
        VisTable mainTable = new VisTable();

        VisTextButton cancel = new VisTextButton("Cancel");
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
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 19);
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
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 18);
            }
        });

        submit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                submit.setDisabled(true);
                new CharacterCreatorPacketOut(clientMain, characterName.getText(), hairStyleOption.optionValue, hairColorOption.optionValue, eyeColorOption.optionValue, skinColorOption.optionValue).sendPacket();
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 16);
            }
        });

        pack();
        setMovable(false);
        centerWindow();
        return mainTable;
    }

    public void creationSuccess() {
        ActorUtil.fadeOutWindow(characterCreation);
        ActorUtil.fadeInWindow(stageHandler.getCharacterSelectMenu());
        stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
        resetCharacter(); // Clear current design, so next character creation is cleared.
        submit.setDisabled(false);
        errorLabel.setText("");
    }

    public void creationFail(CharacterCreatorResponses characterCreatorResponses) {
        submit.setDisabled(false);
        switch (characterCreatorResponses) {
            case FAIL_BLACKLIST_NAME:
                errorLabel.setText("[RED]Name not available!");
                break;
            case FAIL_NAME_TAKEN:
                errorLabel.setText("[RED]Name taken!");
                break;
            case FAIL_TOO_MANY_CHARACTERS:
                errorLabel.setText("[RED]You can not create any more characters!");
                break;
        }
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
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 17);
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
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 17);
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
        hairStyleOption.setOptionValue((byte) RandomNumberUtil.getNewRandom(0, maxHairStyles));
        hairColorOption.setOptionValue((byte) RandomNumberUtil.getNewRandom(0, HairColorList.values().length - 1));
        eyeColorOption.setOptionValue((byte) RandomNumberUtil.getNewRandom(0, EyeColorList.values().length - 1));
        skinColorOption.setOptionValue((byte) RandomNumberUtil.getNewRandom(0, SkinColorList.values().length - 1));
        rebuildPreviewTable();
    }

    @Getter
    @Setter
    static class CharacterOption {
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
