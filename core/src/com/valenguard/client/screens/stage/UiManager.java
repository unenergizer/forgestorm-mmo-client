package com.valenguard.client.screens.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

import lombok.Getter;

@Getter
public class UiManager implements Disposable {

    private AbstractUI abstractUI;
    protected Stage stage;
    protected Skin skin;
    private boolean refreshable = false;
    private boolean disposable = false;

    public void setup(Stage stage, Skin skin) {
        this.stage = stage;
        this.skin = skin;
    }

    public void show(AbstractUI abstractUI) {
        dispose();
        this.abstractUI = abstractUI;
        if (abstractUI == null) return;
        this.abstractUI.build(this);
        if (abstractUI instanceof Refreshable) refreshable = true;
        if (abstractUI instanceof Disposable) disposable = true;
    }

    public void refreshAbstractUi() {
        if (refreshable) {
            stage.clear();
            abstractUI.build(this);
        }
    }

    @Override
    public void dispose() {
        refreshable = false;
        if (abstractUI != null && disposable) {
            ((Disposable) abstractUI).dispose();
            disposable = false;
        }
        if (stage != null) stage.clear();
    }
}
