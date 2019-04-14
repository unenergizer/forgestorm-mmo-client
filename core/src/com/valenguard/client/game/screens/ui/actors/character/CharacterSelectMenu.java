package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.network.game.packet.out.CharacterSelectPacketOut;

import static com.valenguard.client.util.Log.println;

public class CharacterSelectMenu extends VisWindow implements Buildable {

    private VisTable characterTable = new VisTable();

    public CharacterSelectMenu() {
        super("Select a character");
    }

    @Override
    public Actor build() {
        VisTextButton createCharacter = new VisTextButton("Create Character");
        add(createCharacter).row();

        createCharacter.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getCharacterCreation());
            }
        });

        add(characterTable).row();

        centerWindow();
        setMovable(false);
        return this;
    }

    public void addCharacterButton(final String characterName, final byte characterId) {
        VisTextButton button = new VisTextButton(characterName + " / " + characterId);
        characterTable.add(button).row();

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                println(CharacterSelectMenu.class, "CharacterSelected: " + characterName + ", ID: " + characterId);
                new CharacterSelectPacketOut(characterId).sendPacket();
            }
        });
    }

    public void reset() {
        characterTable.remove();
        characterTable = new VisTable();
        add(characterTable).row();
    }
}
