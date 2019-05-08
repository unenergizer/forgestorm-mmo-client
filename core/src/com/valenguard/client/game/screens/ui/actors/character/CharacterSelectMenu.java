package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.in.CharactersMenuLoadPacketIn;
import com.valenguard.client.network.game.packet.out.CharacterLogoutPacketOut;
import com.valenguard.client.network.game.packet.out.CharacterSelectPacketOut;
import com.valenguard.client.util.ColorList;

public class CharacterSelectMenu extends VisTable implements Buildable {

    private static final int IMG_SIZE = 64;

    private CharactersMenuLoadPacketIn.GameCharacter selectedCharacter;

    private VisTable characterButtonTable = new VisTable();
    private VisTable characterImageTable = new VisTable();
    private ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ENTITY_CHARACTER, IMG_SIZE);
    private VisTextButton activeButton;

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
        characterImageTable = new VisTable();
        characterImageTable.setSize(Gdx.graphics.getWidth() - visWindow.getWidth(), Gdx.graphics.getHeight());

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

        sideTable.add(characterImageTable).grow().row();
        sideTable.add(bottomRow);

        add(visWindow).fill();
        add(sideTable).expand().fill();


        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new CharacterSelectPacketOut(selectedCharacter.getCharacterId()).sendPacket();
            }
        });

        logout.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                new CharacterLogoutPacketOut(CharacterLogout.LOGOUT_SERVER).sendPacket();
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
                setImageTable();
            }
        });

        // Set first character loaded in as the selected character
        if (selectedCharacter == null) {
            activeButton = button;
            activeButton.setColor(Color.GREEN);
            selectedCharacter = character;
            setImageTable();
        }
    }

    private void setImageTable() {
        characterImageTable.clearChildren(); // Clear previous image

        Stack stack = new Stack();

        Color skinColor = ColorList.getType(selectedCharacter.getColorId()).getColor();

        VisImage skinHead = imageBuilder.setRegionName("head_down_naked").buildVisImage();
        VisImage skinChest = imageBuilder.setRegionName("chest_down_naked").buildVisImage();
        VisImage skinPants = imageBuilder.setRegionName("pants_down_naked").buildVisImage();
        VisImage skinShoes = imageBuilder.setRegionName("shoes_down_naked").buildVisImage();

        skinHead.setColor(skinColor);
        skinChest.setColor(skinColor);
        skinPants.setColor(skinColor);
        skinShoes.setColor(skinColor);

        //        if (selectedCharacter.getHeadId() != -1) {
//            VisImage head = imageBuilder.setRegionName("head_down_" + selectedCharacter.getHeadId()).buildVisImage();
//        }
//        VisImage bodyChest = imageBuilder.setRegionName("body_down_chest_" + selectedCharacter.getBodyId()).buildVisImage();
//        VisImage bodyPants = imageBuilder.setRegionName("body_down_pants_" + selectedCharacter.getBodyId()).buildVisImage();
//        VisImage bodyShoes = imageBuilder.setRegionName("body_down_shoes_" + selectedCharacter.getBodyId()).buildVisImage();

        stack.add(skinHead);
        stack.add(skinChest);
        stack.add(skinPants);
        stack.add(skinShoes);
//        stack.add(head);
//        stack.add(bodyChest);
//        stack.add(bodyPants);
//        stack.add(bodyShoes);
        characterImageTable.add(stack).expand().fill();
    }

    public void reset() {
        selectedCharacter = null;
        characterButtonTable.clearChildren();
        characterImageTable.clearChildren();
    }
}
