package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.movement.MoveUtil;

import java.util.LinkedList;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovingEntity extends Entity {

    /**
     * The exact tile location of the entity on the tile grid.
     */
    private Location futureMapLocation;

    /**
     * The direction the entity is facing. Is not always the same direction
     * as they are moving because the move direction can be NONE.
     */
    private MoveDirection facingDirection;

    private float moveSpeed;

    private float walkTime = 0;

    private Queue<Location> futureLocationRequests = new LinkedList<Location>();

    // How often the animation switches region
    private static final float WALK_INTERVAL = 0.25f;

    private float stateTime = 1f;

    private Texture walkingSheet;
    private TextureAtlas textureAtlas;

    @Getter
    private int headId = 0;
    private Animation<TextureRegion> headDown;
    private Animation<TextureRegion> headUp;
    private Animation<TextureRegion> headLeft;
    private Animation<TextureRegion> headRight;

    @Getter
    private int bodyId = 0;
    private Animation<TextureRegion> walkDown;
    private Animation<TextureRegion> walkUp;
    private Animation<TextureRegion> walkLeft;
    private Animation<TextureRegion> walkRight;


    public void setBodyParts(int headId, int bodyId) {
        this.headId = headId;
        this.bodyId = bodyId;
        textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.MAIN_ATLAS);

        headDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_down_" + headId), Animation.PlayMode.LOOP);
        headUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_up_" + headId), Animation.PlayMode.LOOP);
        headLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_left_" + headId), Animation.PlayMode.LOOP);
        headRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("head_right_" + headId), Animation.PlayMode.LOOP);

        walkDown = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_down_" + bodyId), Animation.PlayMode.LOOP);
        walkUp = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_up_" + bodyId), Animation.PlayMode.LOOP);
        walkLeft = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_left_" + bodyId), Animation.PlayMode.LOOP);
        walkRight = new Animation<TextureRegion>(WALK_INTERVAL, textureAtlas.findRegions("body_right_" + bodyId), Animation.PlayMode.LOOP);
    }

    public void animate(float delta, SpriteBatch spriteBatch) {
        if (MoveUtil.isEntityMoving(this)) {
            stateTime += delta;
        } else {
            stateTime = 0f;
        }

        // Use this current frame as the texture to draw.
        TextureRegion currentHeadFrame = null;
        TextureRegion currentBodyFrame = null;
        switch (facingDirection) {
            case UP:
                currentHeadFrame = headUp.getKeyFrame(stateTime, true);
                currentBodyFrame = walkUp.getKeyFrame(stateTime, true);
                break;
            case DOWN:
                currentHeadFrame = headDown.getKeyFrame(stateTime, true);
                currentBodyFrame = walkDown.getKeyFrame(stateTime, true);
                break;
            case LEFT:
                currentHeadFrame = headLeft.getKeyFrame(stateTime, true);
                currentBodyFrame = walkLeft.getKeyFrame(stateTime, true);
                break;
            case RIGHT:
                currentHeadFrame = headRight.getKeyFrame(stateTime, true);
                currentBodyFrame = walkRight.getKeyFrame(stateTime, true);
                break;
            case NONE:
                throw new RuntimeException("Facing direction cannot be NONE.");
        }

        spriteBatch.draw(currentBodyFrame, getDrawX(), getDrawY());
        spriteBatch.draw(currentHeadFrame, getDrawX(), getDrawY());
        drawEntityName();
    }


    private GlyphLayout layout1 = null;
    private GlyphLayout layout2 = null;

    private void drawEntityName() {
        float x = getDrawX() + 8;
        float y = getDrawY() + (16 + ClientConstants.namePlateDistanceInPixels);

        BitmapFont font = Valenguard.gameScreen.getFont();

        if (getEntityType() == EntityType.NPC) {
            font.setColor(Color.BLACK);
            layout2 = new GlyphLayout(font, getEntityName());
            font.setColor(Color.LIME);
            layout1 = new GlyphLayout(font, getEntityName());
        } else {
            font.setColor(Color.BLACK);
            layout2 = new GlyphLayout(font, getEntityName());
            font.setColor(Color.GOLD);
            layout1 = new GlyphLayout(font, getEntityName());
        }

        font.setColor(Color.BLACK);
        font.draw(Valenguard.gameScreen.getSpriteBatch(), layout2, x - (layout2.width / 2) + .8f, y - .8f);

        font.setColor(Color.GOLD);
        font.draw(Valenguard.gameScreen.getSpriteBatch(), layout1, x - (layout1.width / 2), y);
    }

    public void addLocationToFutureQueue(Location location) {
        futureLocationRequests.add(location);
    }
}
