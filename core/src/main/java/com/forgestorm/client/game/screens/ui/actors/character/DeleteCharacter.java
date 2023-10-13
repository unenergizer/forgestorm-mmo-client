package com.forgestorm.client.game.screens.ui.actors.character;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.network.game.packet.out.CharacterDeletePacketOut;
import com.forgestorm.shared.io.type.GameAtlas;
import com.forgestorm.shared.util.RandomNumberUtil;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.*;

public class DeleteCharacter extends HideableVisWindow implements Buildable {

    private final DeleteCharacter deleteCharacterWindow;

    private final ClientMain clientMain;
    private StageHandler stageHandler;
    private final VisTextField deleteCodeBox = new VisTextField();
    private final VisLabel characterNameLabel = new VisLabel();
    private VisLabel confirmStringLabel;
    private String deleteCode;
    private byte characterListIndex;

    public DeleteCharacter(ClientMain clientMain) {
        super(clientMain, "Delete Character");
        this.clientMain = clientMain;
        this.deleteCharacterWindow = this;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        confirmStringLabel = new VisLabel("", stageHandler.getMarkupStyle());

        VisTable layoutTable = new VisTable();
        VisImage visImage = new ImageBuilder(stageHandler.getClientMain(), GameAtlas.ITEMS, "skill_165", 16 * 3).buildVisImage();

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
                confirm.setDisabled(!deleteCodeBox.getText().equals(deleteCode));
            }
        });

        confirm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CharacterSelectMenu characterSelectMenu = stageHandler.getCharacterSelectMenu();
                ActorUtil.fadeOutWindow(deleteCharacterWindow);
                ActorUtil.fadeInWindow(characterSelectMenu);
                characterSelectMenu.reprocessCharacterButtons(characterListIndex);
                new CharacterDeletePacketOut(clientMain, characterListIndex).sendPacket();
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 10);
                confirm.setDisabled(true);
            }
        });

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ActorUtil.fadeOutWindow(deleteCharacterWindow);
                ActorUtil.fadeInWindow(stageHandler.getCharacterSelectMenu());
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(CharacterCreation.class, (short) 0);
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
        deleteCode = String.valueOf(RandomNumberUtil.getNewRandom(10000, 99999));
        characterNameLabel.setText("[GREEN]Character Name: " + characterName);
        confirmStringLabel.setText("Enter [YELLOW]" + deleteCode + "[WHITE] to delete your character.");
        FocusManager.switchFocus(stageHandler.getStage(), deleteCodeBox);
        stageHandler.getStage().setKeyboardFocus(deleteCodeBox);
        centerWindow();
        pack();
    }
}
