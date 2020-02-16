package com.valenguard.client.game.screens.ui.actors;

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class LeftAlignTextButton extends VisTextButton {
    public LeftAlignTextButton(String text, String styleName) {
        super(text, styleName);
        getLabel().setAlignment(Alignment.LEFT.getAlignment());
    }

    public LeftAlignTextButton(String text) {
        super(text);
        getLabel().setAlignment(Alignment.LEFT.getAlignment());
    }

    public LeftAlignTextButton(String text, ChangeListener listener) {
        super(text, listener);
        getLabel().setAlignment(Alignment.LEFT.getAlignment());
    }

    public LeftAlignTextButton(String text, String styleName, ChangeListener listener) {
        super(text, styleName, listener);
        getLabel().setAlignment(Alignment.LEFT.getAlignment());
    }

    public LeftAlignTextButton(String text, VisTextButtonStyle buttonStyle) {
        super(text, buttonStyle);
        getLabel().setAlignment(Alignment.LEFT.getAlignment());
    }
}
