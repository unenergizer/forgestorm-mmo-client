package com.forgestorm.client.game.screens.ui.actors.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.audio.AudioPreferences;
import com.forgestorm.client.game.audio.MusicManager;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

class AudioTab extends Tab {

    private final String title;
    private Table content;

    AudioTab(ClientMain clientMain) {
        super(false, false);
        title = " Audio ";
        build(clientMain);
    }

    private void build(ClientMain clientMain) {
        content = new VisTable(true);
        final MusicManager musicManager = clientMain.getAudioManager().getMusicManager();
        final AudioPreferences audioPreferences = musicManager.getAudioPreferences();

        /*
         * Pause Audio on Lost Window Focus Toggle
         */
        final VisCheckBox audioFocusPauseRadioButton = new VisCheckBox("");
        audioFocusPauseRadioButton.setChecked(audioPreferences.isPauseMusicOnWindowLooseFocus());

        content.add(new VisLabel("Pause Audio on Lost Window Focus")).padRight(3);
        content.add(audioFocusPauseRadioButton).left();

        audioFocusPauseRadioButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audioPreferences.setPauseMusicOnWindowLooseFocus(audioFocusPauseRadioButton.isChecked());
                event.handle();
            }
        });

        /*
         * Music Volume
         */
        final VisSlider musicVolumeSlider = new VisSlider(0, 1, .01f, false);
        musicVolumeSlider.setValue(ClientConstants.ZOOM_DEFAULT);

        content.row();
        content.add(new VisLabel("Music Volume")).padRight(3);
        content.add(musicVolumeSlider).left();

        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audioPreferences.setMusicVolume(musicVolumeSlider.getValue());
                musicManager.setVolume(musicVolumeSlider.getValue());
                event.handle();
            }
        });
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public Table getContentTable() {
        return content;
    }
}
