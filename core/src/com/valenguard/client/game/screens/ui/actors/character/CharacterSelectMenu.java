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
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.world.maps.MoveDirection;
import com.valenguard.client.network.game.packet.in.CharactersMenuLoadPacketIn;
import com.valenguard.client.network.game.packet.out.CharacterLogoutPacketOut;
import com.valenguard.client.network.game.packet.out.CharacterSelectPacketOut;

public class CharacterSelectMenu extends VisTable implements Buildable {

    private CharactersMenuLoadPacketIn.GameCharacter selectedCharacter;

    private VisTable characterButtonTable = new VisTable();
    private VisTextButton activeButton;

    private CharacterPreviewer characterPreviewer = new CharacterPreviewer();
    private int previewScale = 20;

    @Override
    public Actor build() {
        setFillParent(true);

        VisWindow visWindow = new VisWindow("Select Character");
        visWindow.setHeight(Gdx.graphics.getHeight());
        visWindow.setMovable(false);

        visWindow.add(characterButtonTable).growY().align(Alignment.TOP.getAlignment()).row();

        VisTextButton createCharacter = new VisTextButton("Create Character");
        visWindow.add(createCharacter).align(Alignment.BOTTOM.getAlignment()).row();

        VisTable sideTable = new VisTable();

        createCharacter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getCharacterCreation());
            }
        });

        VisTable bottomRow = new VisTable();

        VisTextButton play = new VisTextButton("Play");
        VisTextButton logout = new VisTextButton("Logout");

        bottomRow.add(play).align(Alignment.CENTER.getAlignment());
        bottomRow.add(logout).align(Alignment.RIGHT.getAlignment());

        sideTable.add(characterPreviewer.getPreviewTable()).row();
        sideTable.add(bottomRow);

        add(visWindow).fill();
        add(sideTable).expand().fill();


        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new CharacterSelectPacketOut(selectedCharacter.getCharacterId()).sendPacket();
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 0);
            }
        });

        logout.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new CharacterLogoutPacketOut(CharacterLogout.LOGOUT_SERVER).sendPacket();
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterSelectMenu.class, (short) 0);
                Valenguard.connectionManager.logout();
            }
        });

        return this;
    }

    public void addCharacterButton(final CharactersMenuLoadPacketIn.GameCharacter character) {
        final VisTextButton button = new VisTextButton(character.getName());
        button.setColor(Color.LIGHT_GRAY);

        characterButtonTable.add(button).fill().row();

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                activeButton.setColor(Color.LIGHT_GRAY); // Clear the current active button color

                // Set new active character information
                selectedCharacter = character;
                activeButton = button;
                activeButton.setColor(Color.GREEN);
                characterPreviewer.fillPreviewTable(selectedCharacter.getAppearance(), MoveDirection.SOUTH, 20);
            }
        });

        // Set first character loaded in as the selected character
        if (selectedCharacter == null) {
            activeButton = button;
            activeButton.setColor(Color.GREEN);
            selectedCharacter = character;
            characterPreviewer.fillPreviewTable(selectedCharacter.getAppearance(), MoveDirection.SOUTH, 20);
        }
    }


    public void reset() {
        selectedCharacter = null;
        characterButtonTable.clearChildren();
        characterPreviewer.reset();
    }
}
