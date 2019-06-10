package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.graphics.Color;
import com.valenguard.client.util.color.LibGDXColorList;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ColorPickerColorHandler {

    private Color colorChange = LibGDXColorList.PLAYER_DEFAULT.getColor();
    private Color finishedColor = LibGDXColorList.PLAYER_DEFAULT.getColor();

    void doColorChange(Color newColor) {
        this.colorChange = newColor;
        change(newColor);
    }

    void doFinishedColor(Color newColor) {
        this.finishedColor = newColor;
        finish(newColor);
    }

    void setColor(Color newColor) {
        this.colorChange = newColor;
        this.finishedColor = newColor;
        finish(newColor);
    }

    public abstract void change(Color newColor);

    public abstract void finish(Color newColor);
}
