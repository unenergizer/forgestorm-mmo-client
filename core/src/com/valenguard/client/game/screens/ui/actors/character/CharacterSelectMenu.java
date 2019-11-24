package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.world.entities.Appearance;
import com.valenguard.client.io.FileManager;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.in.CharactersMenuLoadPacketIn;
import com.valenguard.client.network.game.packet.out.CharacterLogoutPacketOut;
import com.valenguard.client.network.game.packet.out.CharacterSelectPacketOut;

public class CharacterSelectMenu extends VisTable implements Buildable {

    private CharactersMenuLoadPacketIn.GameCharacter selectedCharacter;

    private VisTable characterButtonTable = new VisTable();
    private VisTextButton activeButton;

    private VisTable previewTable = new VisTable();

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

        sideTable.add(previewTable).row();
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
        previewTable.clearChildren(); // Clear previous image

        final int previewScale = 20;
        final int width = 16 * previewScale;
        final Appearance appearance = selectedCharacter.getAppearance();

        Stack imageStack = new Stack();
        imageStack.setWidth(16 * previewScale);
        imageStack.setHeight(16 * previewScale);

        imageStack.add(imageTable(width, 16 * previewScale, previewScale, "head_down_naked", appearance.getSkinColor()));
        imageStack.add(imageTable(width, 6 * previewScale, 4 * previewScale, "chest_down_naked", appearance.getSkinColor()));
        if (appearance.getChestTexture() != -1)
            imageStack.add(imageTable(width, 6 * previewScale, 4 * previewScale, "chest_down_" + appearance.getChestTexture(), Color.WHITE));
        imageStack.add(imageTable(width, 6 * previewScale, 4 * previewScale, "gloves_down", appearance.getGlovesColor()));
        imageStack.add(imageTable(width, 3 * previewScale, previewScale, "pants_down_naked", appearance.getSkinColor()));
        if (appearance.getPantsTexture() != -1)
            imageStack.add(imageTable(width, 3 * previewScale, previewScale, "pants_down_" + appearance.getPantsTexture(), Color.WHITE));
        imageStack.add(imageTable(width, previewScale, previewScale, "shoes_down_naked", appearance.getSkinColor()));
        if (appearance.getShoesTexture() != -1)
            imageStack.add(imageTable(width, previewScale, previewScale, "shoes_down_" + appearance.getShoesTexture(), Color.WHITE));
        imageStack.add(imageTable(width, 16 * previewScale, previewScale, "eyes_down", appearance.getEyeColor()));
        if (appearance.getHelmTexture() != -1) {
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "helm_down_" + appearance.getHelmTexture(), Color.WHITE));
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "helm_border_down_" + appearance.getHelmTexture(), Color.BLACK));
        } else {
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "hair_down_" + appearance.getHairTexture(), appearance.getHairColor()));
            imageStack.add(imageTable(width, 16 * previewScale, previewScale, "hair_border_down_" + appearance.getHairTexture(), Color.BLACK));
        }
        imageStack.add(imageTable(width, 16 * previewScale, previewScale, "body_down_border", Color.BLACK));

        previewTable.add(imageStack);
    }

    @SuppressWarnings("SameParameterValue")
    private VisTable imageTable(int width, int height, int padBottom, String region, Color color) {
        VisTable innerTable = new VisTable();

        FileManager fileManager = Valenguard.getInstance().getFileManager();

        fileManager.loadAtlas(GameAtlas.ENTITY_CHARACTER);

        TextureAtlas textureAtlas = fileManager.getAtlas(GameAtlas.ENTITY_CHARACTER);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureAtlas.findRegion(region));
        textureRegionDrawable.setMinWidth(width);
        textureRegionDrawable.setMinHeight(height);

        VisImage texture = new VisImage(textureRegionDrawable);
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setColor(color);

        innerTable.add(texture).expand().fillX().bottom().left().padBottom(padBottom);
        return innerTable;
    }

    public void reset() {
        selectedCharacter = null;
        characterButtonTable.clearChildren();
        previewTable.clearChildren();
    }
}
