package com.forgestorm.client.game.screens.ui.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.audio.AudioManager;
import com.forgestorm.client.game.audio.MusicManager;
import com.forgestorm.client.game.audio.SoundManager;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.text.DecimalFormat;

public class ActorUtil {

    private ActorUtil() {
    }

    private static final Vector2 localCords = new Vector2();

    public static Vector2 getStageLocation(Actor actor) {
        return actor.localToStageCoordinates(localCords.set(0, 0));
    }

    public static boolean fadeOutWindow(HideableVisWindow hideableVisWindow) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (visibleStatus) hideableVisWindow.fadeOut();
        return visibleStatus;
    }

    public static boolean fadeOutWindow(HideableVisWindow hideableVisWindow, float time) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (visibleStatus) hideableVisWindow.fadeOut(time);
        return visibleStatus;
    }

    public static boolean fadeInWindow(HideableVisWindow hideableVisWindow) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (!visibleStatus) hideableVisWindow.fadeIn().setVisible(true);
        hideableVisWindow.toFront();
        return visibleStatus;
    }

    public static boolean fadeInWindow(HideableVisWindow hideableVisWindow, float time) {
        boolean visibleStatus = hideableVisWindow.isVisible();
        if (!visibleStatus) hideableVisWindow.fadeIn(time).setVisible(true);
        hideableVisWindow.toFront();
        return visibleStatus;
    }

    public static void selectBox(VisTable mainTable, String labelName, VisSelectBox visSelectBox, Object[] items) {
        //noinspection unchecked
        visSelectBox.setItems(items);
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow().pad(1);
        table.add(visSelectBox).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();

        visSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(ActorUtil.class, (short) 0);
            }
        });
    }

    public static void checkBox(VisTable mainTable, String labelName, VisCheckBox visCheckBox) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow().pad(1);
        table.add(visCheckBox).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();

        visCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(ActorUtil.class, (short) 0);
            }
        });
    }

    public static void textField(VisTable mainTable, String labelName, VisTextField textField) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow().pad(1);
        table.add(textField).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();

        textField.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(ActorUtil.class, (short) 0);
                return false;
            }
        });
    }

    public static void valueSlider(VisTable mainTable, String labelName, final VisSlider slider, final DecimalFormat decimalFormat) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        final VisLabel sliderValue = new VisLabel(decimalFormat.format(slider.getValue()));
        table.add(visLabel).grow().pad(1);
        table.add(slider).pad(1);
        table.add(sliderValue).pad(1);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sliderValue.setText(decimalFormat.format(slider.getValue()));
            }
        });

        slider.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(ActorUtil.class, (short) 0);
                return false;
            }
        });

        mainTable.add(table).expandX().fillX().pad(1).row();
    }

    public static void musicField(VisTable mainTable, String labelName, VisTextField textField, Class clazz) {
        AudioManager audioManager = ClientMain.getInstance().getAudioManager();
        MusicManager musicManager = audioManager.getMusicManager();

        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);

        VisTextButton playButton = new VisTextButton(">");
        playButton.setColor(Color.GREEN);
        VisTextButton stopButton = new VisTextButton("X");
        stopButton.setColor(Color.RED);

        table.add(visLabel).grow().pad(1);
        table.add(textField).pad(1);
        table.add(playButton).pad(1);
        table.add(stopButton).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    musicManager.playMusic(clazz, Short.parseShort(textField.getText()));
                } catch (NumberFormatException ignored) {
                }
            }
        });

        stopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicManager.stopMusic(false);
            }
        });

        textField.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                audioManager.getSoundManager().playSoundFx(ActorUtil.class, (short) 0);
                return false;
            }
        });
    }

    public static void soundField(VisTable mainTable, String labelName, VisTextField textField, Class clazz) {
        SoundManager audioManager = ClientMain.getInstance().getAudioManager().getSoundManager();

        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);

        VisTextButton playButton = new VisTextButton(">");
        playButton.setColor(Color.GREEN);
        VisTextButton stopButton = new VisTextButton("X");
        stopButton.setColor(Color.RED);

        table.add(visLabel).grow().pad(1);
        table.add(textField).pad(1);
        table.add(playButton).pad(1);
        table.add(stopButton).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    audioManager.playSoundFx(clazz, Short.parseShort(textField.getText()));
                } catch (NumberFormatException ignored) {
                }
            }
        });

        stopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audioManager.stopSoundFx(false);
            }
        });

        textField.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                audioManager.playSoundFx(ActorUtil.class, (short) 0);
                return false;
            }
        });
    }

    public static StageHandler getStageHandler() {
        return ClientMain.getInstance().getStageHandler();
    }

    public static Stage getStage() {
        return getStageHandler().getStage();
    }
}
