package com.valenguard.client.game.screens.ui.actors.character;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.valenguard.client.ClientMain;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.out.CharacterDeletePacketOut;
import com.valenguard.client.util.RandomUtil;

public class DeleteCharacter extends HideableVisWindow implements Buildable {

    private final DeleteCharacter deleteCharacterWindow;

    private StageHandler stageHandler;
    private VisTextField deleteCodeBox = new VisTextField();
    private VisLabel characterNameLabel = new VisLabel();
    private VisLabel confirmStringLabel;
    private String deleteCode;
    private byte characterListIndex;

    public DeleteCharacter() {
        super("Delete Character");
        this.deleteCharacterWindow = this;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        confirmStringLabel = new VisLabel("", stageHandler.getMarkupStyle());

        VisTable layoutTable = new VisTable();
        VisImage visImage = new ImageBuilder(GameAtlas.ITEMS, "skill_165", 16 * 3).buildVisImage();

        layoutTable.add(visImage).pad(3);

        VisTable infoTable = new VisTable();

        VisLabel infoLabel = new VisLabel("Are you sure you want to delete this character?");
        VisLabel deleteWarning = new VisLabel("[RED]WARNING! This action can not be undone.", stageHandler.getMarkupStyle());

        infoTable.add(infoLabel).row();
        infoTable.add(characterNameLabel).pad(3).row();
        infoTable.add(confirmStringLabel).pad(3).row();
        infoTable.add(deleteCodeBox).pad(3).row();
        infoTable.add(deleteWarning).pad(3).row();

        VisTable buttonTable = new VisTable();
        final VisTextButton confirm = new VisTextButton("Confirm");
        confirm.setDisabled(true);
        final VisTextButton cancel = new VisTextButton("Cancel");

        buttonTable.add(confirm).pad(3);
        buttonTable.add(cancel).pad(3);
        infoTable.add(buttonTable).pad(3).row();

        layoutTable.add(infoTable).pad(3);

        add(layoutTable);

        deleteCodeBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (deleteCodeBox.getText().equals(deleteCode)) {
                    confirm.setDisabled(false);
                } else {
                    confirm.setDisabled(true);
                }
            }
        });

        confirm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CharacterSelectMenu characterSelectMenu = stageHandler.getCharacterSelectMenu();
                ActorUtil.fadeOutWindow(deleteCharacterWindow);
                ActorUtil.fadeInWindow(characterSelectMenu);
                characterSelectMenu.reprocessCharacterButtons(characterListIndex);
                new CharacterDeletePacketOut(characterListIndex).sendPacket();
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
                confirm.setDisabled(true);
            }
        });

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.fadeOutWindow(deleteCharacterWindow);
                ActorUtil.fadeInWindow(stageHandler.getCharacterSelectMenu());
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
                confirm.setDisabled(true);
            }
        });

        pack();
        centerWindow();
        setMovable(false);
        setVisible(false);
        return this;
    }

    void toggleDeleteWindow(String characterName, byte characterListIndex) {
        this.characterListIndex = characterListIndex;
        deleteCodeBox.setText("");
        deleteCode = String.valueOf(RandomUtil.getNewRandom(10000, 99999));
        characterNameLabel.setText("[GREEN]Character Name: " + characterName);
        confirmStringLabel.setText("Enter [YELLOW]" + deleteCode + "[WHITE] to delete your character.");
        FocusManager.switchFocus(stageHandler.getStage(), deleteCodeBox);
        stageHandler.getStage().setKeyboardFocus(deleteCodeBox);
        centerWindow();
        pack();
    }
}
