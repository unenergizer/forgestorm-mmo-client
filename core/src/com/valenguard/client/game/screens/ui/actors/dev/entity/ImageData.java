package com.valenguard.client.game.screens.ui.actors.dev.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ImageData {

    int data = 0;
    boolean use;

    void reset() {
        data = 0;
        use = false;
    }
}
