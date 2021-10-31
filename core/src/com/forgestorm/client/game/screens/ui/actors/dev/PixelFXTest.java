package com.forgestorm.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.PlayerClient;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import static com.forgestorm.client.util.Log.println;

public class PixelFXTest extends HideableVisWindow implements Buildable, Disposable {

    private Animation<TextureRegion> runningAnimation;

    /**
     * A variable for tracking elapsed time for the animation
     */
    private float stateTime = 0f;

    private TextureAtlas textureAtlas;

    public PixelFXTest() {
        super("Animation Test");
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        // Load Atlas
        FileManager fileManager = ClientMain.getInstance().getFileManager();
        textureAtlas = fileManager.getAtlas(GameAtlas.PIXEL_FX);
        Array<TextureAtlas.AtlasRegion> list = textureAtlas.getRegions();


        final VisSelectBox<TextureAtlas.AtlasRegion> atlasRegions = new VisSelectBox<TextureAtlas.AtlasRegion>();
        atlasRegions.setItems(list);
        add(atlasRegions).row();

        Animation.PlayMode[] playModes = Animation.PlayMode.values();
        final VisSelectBox<Animation.PlayMode> animationType = new VisSelectBox<Animation.PlayMode>();
        animationType.setItems(playModes);
        add(animationType).row();

        final VisTextField animationSpeed = new VisTextField();
        animationSpeed.setText("25");
        add(animationSpeed).row();

        VisTextButton playButton = new VisTextButton("Play");
        add(playButton).row();

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stateTime = 0f;
                float num = Float.parseFloat(animationSpeed.getText().trim());
                float speed = num / 100;
                runningAnimation = new Animation<TextureRegion>(speed, textureAtlas.findRegions(atlasRegions.getSelected().name), animationType.getSelected());
                println(PixelFXTest.class,
                        "Name: " + atlasRegions.getSelected().name
                                + ", Frames: " + runningAnimation.getKeyFrames().length
                                + ", Speed: " + speed
                                + ", AnimationType: " + animationType.getSelected());
            }
        });

        setVisible(false);
        centerWindow();
        addCloseButton();
        pack();
        return this;
    }

    public void render(float deltaTime, SpriteBatch spriteBatch) {
        if (runningAnimation == null) return;
        if (!isVisible()) return;
        stateTime += deltaTime;
        TextureRegion currentFrame = runningAnimation.getKeyFrame(stateTime, true);
        PlayerClient playerClient = EntityManager.getInstance().getPlayerClient();
        spriteBatch.draw(currentFrame, playerClient.getDrawX(), playerClient.getDrawY() + 16);
    }

    @Override
    public void dispose() {
        textureAtlas.dispose();
    }
}
