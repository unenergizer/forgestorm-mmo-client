package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.network.game.packet.in.CharactersMenuLoadPacketIn;
import com.valenguard.client.network.game.packet.out.CharacterLogoutPacketOut;
import com.valenguard.client.network.game.packet.out.CharacterSelectPacketOut;

public class CharacterSelectMenu extends HideableVisWindow implements Buildable {

    private CharactersMenuLoadPacketIn.GameCharacter[] gameCharacterList;
    private CharactersMenuLoadPacketIn.GameCharacter selectedCharacter;

    private VisTable characterButtonTable = new VisTable();
    private VisTextButton activeButton;
    private VisTextButton playButton;
    private VisTextButton deleteButton;

    private CharacterPreviewer characterPreviewer = new CharacterPreviewer();
    private int previewScale = 20;

    public CharacterSelectMenu() {
        super("");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
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
            }
        });

        VisTable bottomRow = new VisTable();

        playButton = new VisTextButton("Play");
        deleteButton = new VisTextButton("Delete Character");
        VisTextButton logoutButton = new VisTextButton("Logout");

        bottomRow.add(playButton).pad(3).align(Alignment.CENTER.getAlignment());
        bottomRow.add(deleteButton).pad(3);
        bottomRow.add(logoutButton).pad(3).align(Alignment.RIGHT.getAlignment());

        sideTable.add(characterPreviewer.getPreviewTable()).row();
        sideTable.add(bottomRow);

        add(visWindow).fill();
        add(sideTable).expand().fill();

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new CharacterSelectPacketOut(selectedCharacter.getCharacterId()).sendPacket();
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 0);
            }
        });

        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.fadeOutWindow(stageHandler.getCharacterSelectMenu());
                ActorUtil.fadeInWindow(stageHandler.getDeleteCharacter());
                stageHandler.getDeleteCharacter().toggleDeleteWindow(selectedCharacter.getName(), selectedCharacter.getCharacterId());
            }
        });

        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new CharacterLogoutPacketOut(CharacterLogout.LOGOUT_SERVER).sendPacket();
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 0);
                Valenguard.connectionManager.logout();
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
        int buttonsAdded = 0;
        for (final CharactersMenuLoadPacketIn.GameCharacter character : gameCharacterList) {
            if (character == null) continue;
            final VisTextButton addCharacterButton = new VisTextButton(character.getName());
            addCharacterButton.setColor(Color.LIGHT_GRAY);

            characterButtonTable.add(addCharacterButton).pad(1).fill().row();

            addCharacterButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    activeButton.setColor(Color.LIGHT_GRAY); // Clear the current active button color

                    // Set new active character information
                    selectedCharacter = character;
                    activeButton = addCharacterButton;
                    activeButton.setColor(Color.GREEN);
                    characterPreviewer.fillPreviewTable(selectedCharacter.getAppearance(), MoveDirection.SOUTH, previewScale);
                }
            });

            // Set first character loaded in as the selected character
            if (selectedCharacter == null) {
                activeButton = addCharacterButton;
                activeButton.setColor(Color.GREEN);
                selectedCharacter = character;
                characterPreviewer.fillPreviewTable(selectedCharacter.getAppearance(), MoveDirection.SOUTH, previewScale);
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

    public void characterListPacketIn(CharactersMenuLoadPacketIn.GameCharacter[] gameCharacterList) {
        this.gameCharacterList = gameCharacterList;
        addCharacterButtons();
    }

    public void reset() {
        selectedCharacter = null;
        characterButtonTable.clearChildren();
        characterPreviewer.reset();
    }
}
