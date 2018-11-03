package com.valenguard.client.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.Valenguard;
import com.valenguard.client.assets.FileManager;
import com.valenguard.client.assets.GameTexture;
import com.valenguard.client.movement.MoveUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerClient extends Entity {

    // How often the animation switches region
    private static final float WALK_INTERVAL = 0.025f;

    private static final int
            FRAME_COLUMNS = 1, // Number of columns to be rendered
            FRAME_ROWS = 1;    // Number of rows to be rendered

    private float stateTime = 0f;

    private Texture walkingSheet;

    /**
     * The direction the entity intends to move in the future.
     */
    private MoveDirection predictedMoveDirection = MoveDirection.NONE;

    private Animation<TextureRegion> walkingAnimation;

    public void initAnimation() {
        FileManager fileManager = Valenguard.getInstance().getFileManager();
        fileManager.loadTexture(GameTexture.TEMP_OTHER_PLAYER_IMG);

        walkingSheet = Valenguard.getInstance().getFileManager().getTexture(GameTexture.TEMP_OTHER_PLAYER_IMG);

        TextureRegion[][] regions2D = TextureRegion.split(walkingSheet, walkingSheet.getWidth() / FRAME_COLUMNS, walkingSheet.getHeight() / FRAME_ROWS);

        TextureRegion[] regions1D = new TextureRegion[FRAME_COLUMNS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLUMNS; j++) {
                regions1D[index++] = regions2D[i][j];
            }
        }

        walkingAnimation = new Animation<TextureRegion>(WALK_INTERVAL, regions1D);
    }

    public void animate(float delta, SpriteBatch spriteBatch) {

        if (MoveUtil.isEntityMoving(this)) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        // Use this current frame as the texture to draw.
        TextureRegion currentFrame = walkingAnimation.getKeyFrame(stateTime, true);
        spriteBatch.draw(currentFrame, getDrawX(), getDrawY());
    }

    public void dispose() {
        walkingSheet.dispose();
    }
}
