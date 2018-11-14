package com.valenguard.client.game.screens.stage;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Disposable;

import lombok.Getter;

abstract class AbstractUI extends WidgetGroup implements Disposable {

    @Getter
    private boolean refreshable = false;

    void init(String name, int width, int height) {
        if (this instanceof Refreshable) refreshable = true; // Check instance once, for speed.
        setWidth(width);
        setHeight(height);
        setName(name);
    }

    abstract void build(Skin skin);
}
