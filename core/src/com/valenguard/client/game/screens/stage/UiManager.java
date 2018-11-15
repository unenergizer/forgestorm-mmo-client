package com.valenguard.client.game.screens.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameSkin;
import com.valenguard.client.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.Getter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class UiManager implements Disposable {

    private final static boolean PRINT_DEBUG = true;
    private final Map<String, AbstractUI> uiMap = new HashMap<String, AbstractUI>();

    @Getter
    private Stage stage;

    @Getter
    private Skin skin;

    public void setup(Viewport viewport, GameSkin gameSkin) {
        this.stage = new Stage(viewport);
        Valenguard.getInstance().getFileManager().loadSkin(gameSkin);
        this.skin = Valenguard.getInstance().getFileManager().getSkin(gameSkin);
    }

    public boolean exist(String name) {
        return uiMap.containsKey(name);
    }

    public void addUi(String name, AbstractUI abstractUI, boolean displayNow) {
        checkArgument(!name.isEmpty());
        checkNotNull(abstractUI);
        abstractUI.init(name, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        abstractUI.build(skin);
        abstractUI.setVisible(displayNow);
        stage.addActor(abstractUI);
        uiMap.put(name, abstractUI);
    }

    private void removeUi(String name) {
        uiMap.get(name).dispose();
        uiMap.remove(name);
        for (Actor actor : stage.getActors()) {
            if (actor.getName().equalsIgnoreCase(name)) actor.remove();
        }
    }

    public AbstractUI getAbstractUI(String name) {
        AbstractUI abstractUI = null;
        for (Actor actor : stage.getActors()) {
            if (actor.getName().equals(name)) abstractUI = (AbstractUI) actor;
        }
        return abstractUI;
    }

    public void show(String name) {
        uiMap.get(name).setVisible(true);
    }

    public void hide(String name) {
        uiMap.get(name).setVisible(false);
    }

    public void render(float deltaTime) {
        for (AbstractUI ui : uiMap.values()) if (ui.isRefreshable()) ((Refreshable) ui).refresh();
        stage.act(Math.min(deltaTime, 1 / 30f));
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void removeAllUi() {
        Log.println(getClass(), "Unloading " + uiMap.size() + " user interfaces.", false, PRINT_DEBUG);
        Log.println(getClass(), "Removing " + stage.getActors().size + " Stage Actors.", false, PRINT_DEBUG);

        Iterator it = uiMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, AbstractUI> pair = (Map.Entry) it.next();
            for (Actor actor : stage.getActors()) {
                if (actor.getName().equalsIgnoreCase(pair.getKey())) actor.remove();
            }
            pair.getValue().dispose();
            it.remove(); // prevent concurrent modification exception
        }

        Log.println(getClass(), "User Interfaces left: " + uiMap.size(), false, PRINT_DEBUG);
        Log.println(getClass(), "Stage Actors left: " + stage.getActors().size, false, PRINT_DEBUG);
    }

    @Override
    public void dispose() {
        for (AbstractUI ui : uiMap.values()) ui.dispose();
        uiMap.clear();
        if (stage != null) {
            stage.clear();
            stage.dispose();
        }
        if (skin != null) skin.dispose();
    }
}
