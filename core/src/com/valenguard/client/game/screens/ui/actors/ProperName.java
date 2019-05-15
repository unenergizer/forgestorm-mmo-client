package com.valenguard.client.game.screens.ui.actors;

import com.kotcrab.vis.ui.util.InputValidator;
import com.valenguard.client.util.StringUtils;

public class ProperName implements InputValidator {
    @Override
    public boolean validateInput(String input) {
        return StringUtils.isValidName(input);
    }
}
