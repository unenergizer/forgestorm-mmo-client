package com.valenguard.client.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.entities.animations.EntityAnimation;
import com.valenguard.client.game.maps.MoveDirection;
import com.valenguard.client.game.maps.data.Location;
import com.valenguard.client.game.rpg.Attributes;
import com.valenguard.client.game.screens.GameScreen;

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

    /**
     * The rate of speed the entity moves across tiles.
     * The smaller the number, the faster the entity moves.
     */
    private float moveSpeed;

    /**
     * Used by entity manager to measure the walk time between tiles/locations.
     */
    private float walkTime = 0;

    /**
     * The projected future location of this entity.
     */
    private Queue<Location> futureLocationRequests = new LinkedList<Location>();

    /**
     * The animations used for this entity.
     */
    private EntityAnimation entityAnimation;

    /**
     * Entity attributes
     */
    private Attributes attributes = new Attributes();

    /**
     * Entity name drawing
     */
    private boolean glyphInitialized = false;
    private final GlyphLayout regularText = new GlyphLayout();
    private final GlyphLayout shadowText = new GlyphLayout();

    public void drawEntityName() {
        float x = getDrawX() + 8;
        float y = getDrawY() + (16 + ClientConstants.namePlateDistanceInPixels);

        BitmapFont font = Valenguard.gameScreen.getFont();
        if (!glyphInitialized) {
            if (getEntityType() == EntityType.NPC) {
                font.setColor(Color.BLACK);
                shadowText.setText(font, getEntityName());
                font.setColor(Color.LIME);
                regularText.setText(font, getEntityName());
            } else if (getEntityType() == EntityType.MONSTER) {
                font.setColor(Color.BLACK);
                shadowText.setText(font, getEntityName());
                font.setColor(Color.RED);
                regularText.setText(font, getEntityName());
            } else {
                font.setColor(Color.BLACK);
                shadowText.setText(font, getEntityName());
                font.setColor(Color.GOLD);
                regularText.setText(font, getEntityName());
            }
            glyphInitialized = true;
        }

        font.getData().setScale(.5f);
        font.setColor(Color.BLACK);
        font.draw(Valenguard.gameScreen.getSpriteBatch(), shadowText, x - (shadowText.width / 2) + .3f, y - .3f);

        font.setColor(Color.GOLD);
        font.draw(Valenguard.gameScreen.getSpriteBatch(), regularText, x - (regularText.width / 2), y);
    }

    private final GlyphLayout regularFloatingNumber = new GlyphLayout();
    private final GlyphLayout shadowFloatingNumber = new GlyphLayout();
    private float distanceMoved = 0;
    private int damageTaken = 0;
    private boolean showDamage = false;

    public void drawFloatingNumbers() {
        if (damageTaken == 0) return;
        float x = getDrawX() + 8;
        float y = getDrawY() + 8 + distanceMoved;

        BitmapFont font = Valenguard.gameScreen.getFont();
        font.getData().setScale(1f);

        font.setColor(Color.BLACK);
        shadowFloatingNumber.setText(font, Integer.toString(damageTaken));
        font.draw(Valenguard.gameScreen.getSpriteBatch(), shadowFloatingNumber, x - (shadowFloatingNumber.width / 2) + .3f, y - .3f);

        font.setColor(Color.RED);
        regularFloatingNumber.setText(font, Integer.toString(damageTaken));
        font.draw(Valenguard.gameScreen.getSpriteBatch(), regularFloatingNumber, x - (regularFloatingNumber.width / 2), y);

        distanceMoved = distanceMoved + 0.11f;
        if (distanceMoved >= 8) {
            distanceMoved = 0;
            damageTaken = 0;
            showDamage = false;
        }
    }

    // TODO: CLEAN ME
    int maxHealth;
    int currentHealth; // get temp hp (to start)

    public void drawEntityHpBar() {
        float x = getDrawX() + 8;
        float y = getDrawY() + 16;
        float width = 14;
        float xPos = x - (width / 2);
        GameScreen gameScreen = Valenguard.gameScreen;
        gameScreen.getSpriteBatch().draw(gameScreen.getHpBase(), xPos, y, width, 3);
        gameScreen.getSpriteBatch().draw(gameScreen.getHpArea(), xPos, y + 1, width * ((float) currentHealth / maxHealth), 1);
    }

    public void addLocationToFutureQueue(Location location) {
        futureLocationRequests.add(location);
    }

    public void loadTextures(GameAtlas gameAtlas) {
        entityAnimation.loadAll(gameAtlas);
    }
}
