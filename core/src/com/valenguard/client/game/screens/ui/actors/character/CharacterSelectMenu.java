package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.in.CharactersMenuLoadPacketIn;
import com.valenguard.client.network.game.packet.out.CharacterSelectPacketOut;
import com.valenguard.client.util.ColorList;

import static com.valenguard.client.util.Log.println;

public class CharacterSelectMenu extends VisWindow implements Buildable {

    private static final int IMG_SIZE = 64;

    private VisTable characterTable = new VisTable();
    private ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ENTITY_CHARACTER, IMG_SIZE);

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

    public void addCharacterButton(final CharactersMenuLoadPacketIn.GameCharacter character) {
        VisTable innerTable = new VisTable();
        Stack stack = new Stack();
        stack.setSize(IMG_SIZE, IMG_SIZE);

        VisImage skin = imageBuilder.setRegionName("head_down_naked").buildVisImage();
        skin.setColor(ColorList.getType(character.getColorId()).getColor());
        skin.setSize(IMG_SIZE, IMG_SIZE);
        VisImage head = imageBuilder.setRegionName("head_down_" + character.getHeadId()).buildVisImage();
        head.setSize(IMG_SIZE, IMG_SIZE);
        VisImage body = imageBuilder.setRegionName("body_down_" + character.getBodyId()).buildVisImage();
        body.setSize(IMG_SIZE, IMG_SIZE);

        stack.add(skin);
        stack.add(head);
        stack.add(body);

        VisTextButton button = new VisTextButton(character.getName());

        innerTable.add(stack).expand().fill();
        innerTable.add(button).expand().fill();
        characterTable.add(innerTable).row();

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new CharacterSelectPacketOut(character.getCharacterId()).sendPacket();
            }
        });
    }

    public void reset() {
        characterTable.remove();
        characterTable = new VisTable();
        add(characterTable).row();
    }
}
