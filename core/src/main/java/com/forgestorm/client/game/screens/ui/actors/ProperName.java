package com.forgestorm.client.game.screens.ui.actors;

import com.forgestorm.shared.util.StringUtil;
import com.kotcrab.vis.ui.util.InputValidator;

public class ProperName implements InputValidator {
    @Override
    public boolean validateInput(String input) {
        return StringUtil.isValidName(input);
    }
}
