package com.valenguard.client.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class ApplicationUtil {

    private ApplicationUtil() {
    }

    public static boolean userOnMobile() {
        return Gdx.app.getType() == Application.ApplicationType.iOS ||
                Gdx.app.getType() == Application.ApplicationType.Android;
    }

}
