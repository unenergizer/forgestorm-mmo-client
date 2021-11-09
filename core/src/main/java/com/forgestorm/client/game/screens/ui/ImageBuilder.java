package com.forgestorm.client.game.screens.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.widget.VisImage;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue", "unused"})
public class ImageBuilder {

    private String regionName;
    private GameAtlas gameAtlas;
    private float width = 0;
    private float height = 0;
    private float x = 0;
    private float y = 0;
    private Color color = Color.WHITE;

    private TextureRegion[][] textureRegions;
    private int row, column;
    private boolean useSplitTextureRegions = false;

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

    public ImageBuilder setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ImageBuilder setColor(Color color) {
        this.color = color;
        return this;
    }

    public ImageBuilder setTextureRegions(TextureRegion[][] textureRegions, int row, int column) {
        this.textureRegions = textureRegions;
        this.row = row;
        this.column = column;
        this.useSplitTextureRegions = true;
        return this;
    }

    public TextureRegionDrawable buildTextureRegionDrawable() {
        if (gameAtlas == null) throw new RuntimeException("GameAtlas must be defined.");

        ClientMain.getInstance().getFileManager().loadAtlas(gameAtlas, true);
        TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(gameAtlas);
        TextureRegionDrawable textureRegionDrawable;

        if (!useSplitTextureRegions) {
            if (regionName == null || regionName.isEmpty()) {
                throw new RuntimeException("Region Name must be defined.");
            }
            textureRegionDrawable = new TextureRegionDrawable(textureAtlas.findRegion(regionName));
        } else {
            if (textureRegions == null || textureRegions.length == 0) {
                throw new RuntimeException("TextureRegions[][] must be defined.");
            }
            textureRegionDrawable = new TextureRegionDrawable(textureRegions[row][column]);
        }

        textureRegionDrawable.tint(color);
        if (width > 0) textureRegionDrawable.setMinWidth(width);
        if (height > 0) textureRegionDrawable.setMinHeight(height);

        return textureRegionDrawable;
    }

    public VisImage buildVisImage() {
        VisImage visImage = new VisImage(buildTextureRegionDrawable());
        visImage.setColor(color);
        visImage.setPosition(x, y);
        return visImage;
    }
}
