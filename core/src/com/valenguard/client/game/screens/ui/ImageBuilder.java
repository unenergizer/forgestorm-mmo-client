package com.valenguard.client.game.screens.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;

public class ImageBuilder {

    private String regionName;
    private GameAtlas gameAtlas;
    private float width = 0;
    private float height = 0;
    private Color tint;

    public ImageBuilder() {
    }

    public ImageBuilder(GameAtlas gameAtlas) {
        setGameAtlas(gameAtlas);
    }

    public ImageBuilder(GameAtlas gameAtlas, float size) {
        setGameAtlas(gameAtlas);
        setSize(size);
    }

    public ImageBuilder(GameAtlas gameAtlas, String regionName) {
        setGameAtlas(gameAtlas);
        setRegionName(regionName);
    }

    public ImageBuilder(GameAtlas gameAtlas, String regionName, float size) {
        setGameAtlas(gameAtlas);
        setRegionName(regionName);
        setSize(size);
    }

    public ImageBuilder(GameAtlas gameAtlas, String regionName, float width, float height) {
        setGameAtlas(gameAtlas);
        setRegionName(regionName);
        setWidth(width);
        setHeight(height);
    }

    public ImageBuilder setGameAtlas(GameAtlas gameAtlas) {
        this.gameAtlas = gameAtlas;
        return this;
    }

    public ImageBuilder setRegionName(String regionName) {
        this.regionName = regionName;
        return this;
    }

    public ImageBuilder setWidth(float width) {
        this.width = width;
        return this;
    }

    public ImageBuilder setHeight(float height) {
        this.height = height;
        return this;
    }

    public ImageBuilder setSize(float size) {
        setWidth(size);
        setHeight(size);
        return this;
    }

    public ImageBuilder setTint(Color tint) {
        this.tint = tint;
        return this;
    }

    public TextureRegionDrawable buildTextureRegionDrawable() {
        if (gameAtlas == null) throw new RuntimeException("GameAtlas must be defined.");
        if (regionName == null || regionName.isEmpty())
            throw new RuntimeException("Region Name must be defined.");
        Valenguard.getInstance().getFileManager().loadAtlas(gameAtlas);
        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(gameAtlas);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureAtlas.findRegion(regionName));

//        if (tint != null) {
//            textureRegionDrawable = new TextureRegionDrawable(textureRegionDrawable.tint(tint));
//        }

        if (width > 0) textureRegionDrawable.setMinWidth(width);
        if (height > 0) textureRegionDrawable.setMinHeight(height);

        return textureRegionDrawable;
    }

    public VisImage buildVisImage() {
        return new VisImage(buildTextureRegionDrawable());
    }
}
