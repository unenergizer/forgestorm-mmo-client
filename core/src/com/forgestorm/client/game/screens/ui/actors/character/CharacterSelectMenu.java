package com.forgestorm.client.game.screens.ui.actors.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.entities.Appearance;
import com.forgestorm.client.game.world.maps.MoveDirection;
import com.forgestorm.client.network.game.packet.out.CharacterLogoutPacketOut;
import com.forgestorm.client.network.game.packet.out.CharacterSelectPacketOut;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import java.util.Arrays;

public class CharacterSelectMenu extends HideableVisWindow implements Buildable {

    private final CharacterSelectMenu characterSelectMenu;
    private final CharacterPreviewer characterPreviewer = new CharacterPreviewer(20);

    private StageHandler stageHandler;

    private GameCharacter[] gameCharacterList;
    private GameCharacter selectedCharacter;
    private Appearance appearance;

    private VisTable characterButtonTable = new VisTable();
    private VisTextButton activeButton;
    private VisTextButton playButton;
    private VisTextButton deleteButton;


    public CharacterSelectMenu() {
        super("");
        this.characterSelectMenu = this;

        // Build default appearance;
        this.appearance = characterPreviewer.generateInvisibleAppearance();
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        setFillParent(true);

        VisWindow visWindow = new VisWindow("Select Character");
        visWindow.setHeight(Gdx.graphics.getHeight());
        visWindow.setMovable(false);

        visWindow.add(characterButtonTable).growY().align(Alignment.TOP.getAlignment()).row();

        VisTextButton createCharacterButton = new VisTextButton("Create Character");
        visWindow.add(createCharacterButton).align(Alignment.BOTTOM.getAlignment()).row();

        VisTable sideTable = new VisTable();

        createCharacterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                ActorUtil.fadeOutWindow(stageHandler.getCharacterSelectMenu());
                ActorUtil.fadeInWindow(stageHandler.getCharacterCreation());
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 16);
            }
        });

        VisTable bottomRow = new VisTable();

        playButton = new VisTextButton("Play");
        deleteButton = new VisTextButton("Delete Character");
        VisTextButton logoutButton = new VisTextButton("Logout");

        bottomRow.add(playButton).pad(3).align(Alignment.CENTER.getAlignment());
        bottomRow.add(deleteButton).pad(3);
        bottomRow.add(logoutButton).pad(3).align(Alignment.RIGHT.getAlignment());

        characterPreviewer.generateCharacterPreview(appearance, null);

        sideTable.add(characterPreviewer.generatePreviewTable()).row();
        sideTable.add(bottomRow);

        add(visWindow).fill();
        add(sideTable).expand().fill();

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new CharacterSelectPacketOut(selectedCharacter.getCharacterId()).sendPacket();
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 12);
            }
        });

        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.fadeOutWindow(stageHandler.getCharacterSelectMenu());
                ActorUtil.fadeInWindow(stageHandler.getDeleteCharacter());
                stageHandler.getDeleteCharacter().toggleDeleteWindow(selectedCharacter.getName(), selectedCharacter.getCharacterId());
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 9);
            }
        });

        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new CharacterLogoutPacketOut(CharacterLogout.LOGOUT_SERVER).sendPacket();
                ClientMain.getInstance().getConnectionManager().logout();
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 13);
            }
        });
        setVisible(false);
        return this;
    }

    void reprocessCharacterButtons(byte characterIndexToDelete) {
        gameCharacterList[characterIndexToDelete] = null;
        selectedCharacter = null;
        addCharacterButtons();
    }

    private void addCharacterButtons() {
        reset();

        // If the account doesn't have any characters, then we will show the
        // character creation screen.
        if (!doesAccountHaveCharacters()) {
            characterSelectMenu.setVisible(false);
            ActorUtil.fadeInWindow(stageHandler.getCharacterCreation());
            playButton.setDisabled(true);
            deleteButton.setDisabled(true);
        } else {

            int buttonsAdded = 0;
            for (final GameCharacter character : gameCharacterList) {
                if (character == null) continue;
                final VisTextButton addCharacterButton = new VisTextButton(character.getName());
                addCharacterButton.setColor(Color.LIGHT_GRAY);

                characterButtonTable.add(addCharacterButton).pad(1).growX().row();

                addCharacterButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        activeButton.setColor(Color.LIGHT_GRAY); // Clear the current active button color

                        // Set new active character information
                        selectedCharacter = character;
                        activeButton = addCharacterButton;
                        activeButton.setColor(Color.GREEN);
                        characterPreviewer.generateCharacterPreview(selectedCharacter.getAppearance(), MoveDirection.SOUTH);
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 14);
                    }
                });

                // Set first character loaded in as the selected character
                if (selectedCharacter == null) {
                    activeButton = addCharacterButton;
                    activeButton.setColor(Color.GREEN);
                    selectedCharacter = character;
                    characterPreviewer.generateCharacterPreview(selectedCharacter.getAppearance(), MoveDirection.SOUTH);
                }
                buttonsAdded++;
            }

            // Disable the playButton button if no characters are on the screen.
            if (buttonsAdded == 0) {
                playButton.setDisabled(true);
                deleteButton.setDisabled(true);
            } else {
                playButton.setDisabled(false);
                deleteButton.setDisabled(false);
            }
        }
    }

    private boolean doesAccountHaveCharacters() {
        if (gameCharacterList.length == 0) return false;
        for (GameCharacter gameCharacter : gameCharacterList) {
            if (gameCharacter != null) return true;
        }
        return false; // Has no characters
    }

    public void characterListPacketIn(GameCharacter[] gameCharacterList) {
        this.gameCharacterList = Arrays.copyOf(gameCharacterList, gameCharacterList.length);
        addCharacterButtons();
    }

    public void reset() {
        selectedCharacter = null;
        characterButtonTable.clearChildren();
        characterPreviewer.reset();
    }
}
