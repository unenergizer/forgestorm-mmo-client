package com.forgestorm.client.game.screens.ui.actors.dev.spell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class SpellAnimationEditor extends HideableVisWindow implements Buildable {

    private final List<AnimationPartData> animationPartList = new ArrayList<>();

    private TextureAtlas spellTextureAtlas;
    private Array<TextureAtlas.AtlasRegion> list;

    public SpellAnimationEditor() {
        super("Spell Animation Editor");
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        TableUtils.setSpacingDefaults(this);

        // Load Atlas
        FileManager fileManager = ClientMain.getInstance().getFileManager();
        spellTextureAtlas = fileManager.getAtlas(GameAtlas.PIXEL_FX);
        list = spellTextureAtlas.getRegions();

        VisTable infoTable = new VisTable(true);
        int id = 0; // TODO: REPLACE ME WITH AUTO GEN ID!
        VisLabel idNumber = new VisLabel("ID: " + id);
        infoTable.add(idNumber);
        ActorUtil.textField(infoTable, "Spell Name: ", new VisTextField());

        // Cast Animation Table
        addAnimationParts(new AnimationPartData(id, AnimationType.CAST));
        addAnimationParts(new AnimationPartData(id, AnimationType.PROJECTILE));
        addAnimationParts(new AnimationPartData(id, AnimationType.IMPACT));

        // Loop through and add all parts to the UI
        VisTable animationTable = new VisTable();
        animationTable.addSeparator(true);
        for (AnimationPartData partData : animationPartList) {
            animationTable.add(partData.getDataTable()).align(Alignment.TOP.getAlignment());
            animationTable.addSeparator(true);
        }

        // Finalize Buttons
        VisTable buttonsTable = new VisTable(true);
        VisTextButton createButton = new VisTextButton("Create Spell");
        createButton.setColor(Color.GREEN);
        VisTextButton saveButton = new VisTextButton("Save Spell");
        VisTextButton deleteButton = new VisTextButton("Delete Spell");
        deleteButton.setColor(Color.RED);

        buttonsTable.add(createButton);
        buttonsTable.add(saveButton);
        buttonsTable.add(deleteButton);

        // Add All tables to actor
        VisTable actorTable = new VisTable();
        actorTable.add(infoTable).align(Alignment.LEFT.getAlignment()).row();
        actorTable.addSeparator().growX();
        actorTable.add(animationTable).growY().row();
        actorTable.addSeparator().growX();
        actorTable.add(buttonsTable).align(Alignment.RIGHT.getAlignment());

        add(actorTable);

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });
        addCloseButton();
        pack();
        centerWindow();
        setVisible(false);
        setResizable(true);
        return this;
    }

    private void addAnimationParts(AnimationPartData animationPartData) {
        animationPartList.add(animationPartData);
    }

    public void renderAllAnimationPartDataTables(float deltaTime) {
        for (AnimationPartData partData : animationPartList) {
            partData.render(deltaTime);
        }
    }

    class AnimationPartData {

        @Getter
        private final transient VisTable dataTable = new VisTable();
        private final transient VisTable imageTable = new VisTable();

        private final int animationID;
        private final AnimationType animationType;

        private TextureAtlas.AtlasRegion animationRegion;
        private Animation.PlayMode playMode = Animation.PlayMode.NORMAL;

        private Short soundID;
        private float animationSpeed = 10;

        private transient Animation<TextureRegion> runningAnimation;
        private transient float stateTime = 0f;

        public AnimationPartData(int animationID, AnimationType animationType) {
            this.animationID = animationID;
            this.animationType = animationType;

            makeAnimationTable();
            buildAnimationTypeOptions();
        }

        private void resetAnimation() {
            if (animationRegion == null) return;
            if (!isVisible()) return;

            stateTime = 0f;
            runningAnimation = new Animation<>(
                    animationSpeed / 100,
                    spellTextureAtlas.findRegions(animationRegion.name),
                    playMode);

            // repack actor
            pack();
        }

        public void render(float deltaTime) {
            if (animationRegion == null) return;
            if (runningAnimation == null) return;
            if (!isVisible()) return;
            stateTime += deltaTime;
            TextureRegion currentFrame = runningAnimation.getKeyFrame(stateTime);

            imageTable.clear();
            VisImage visImage = new ImageBuilder(GameAtlas.PIXEL_FX).setTextureRegion(currentFrame).buildVisImage();
            imageTable.add(visImage);
        }

        public void makeAnimationTable() {
            // Clear the table..
            dataTable.clear();

            // Build the table..
            dataTable.add(new VisLabel(animationType.getAnimationTypeName() + ":")).row();

            // Add the animated table
            dataTable.add(imageTable).size(64).align(Alignment.CENTER.getAlignment()).row();

            // Animation chooser
            final VisSelectBox<TextureAtlas.AtlasRegion> regionSelectBox = new VisSelectBox<>();
            ActorUtil.selectBox(dataTable, "Animation", regionSelectBox, list.toArray());
            regionSelectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    animationRegion = regionSelectBox.getSelected();
                    resetAnimation();
                }
            });

            final VisSelectBox<Animation.PlayMode> playModeSelectBox = new VisSelectBox<>();
            ActorUtil.selectBox(dataTable, "Play Mode", playModeSelectBox, Animation.PlayMode.values());
            playModeSelectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    playMode = playModeSelectBox.getSelected();
                    resetAnimation();
                }
            });

            // Build sound and animation speed fields
            VisTextField soundIDTextField = new VisTextField();
            soundIDTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        soundID = Short.parseShort(soundIDTextField.getText());
                    } catch (NumberFormatException e) {
                        soundID = null;
                    }
                }
            });

            VisTextField animationSpeedTextField = new VisTextField();
            animationSpeedTextField.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        animationSpeed = Float.parseFloat(animationSpeedTextField.getText());
                    } catch (NumberFormatException e) {
                        animationSpeed = 10;
                    }
                    resetAnimation();
                }
            });

            ActorUtil.soundField(dataTable, "Sound ID: ", soundIDTextField, getClass());
            ActorUtil.textField(dataTable, "Speed: ", animationSpeedTextField);
            animationSpeedTextField.setText(Float.toString(animationSpeed));
            dataTable.addSeparator().row();
        }

        private void buildAnimationTypeOptions() {
            switch (animationType) {
                case CAST:
                    ActorUtil.checkBox(dataTable, "Hide Weapon:", new VisCheckBox(""));
                    ActorUtil.checkBox(dataTable, "Hide Shield:", new VisCheckBox(""));

                    final VisSelectBox<CharacterPose> poseSelectBox = new VisSelectBox<>();
                    ActorUtil.selectBox(dataTable, "Character Pose:", poseSelectBox, CharacterPose.values());
                    break;
                case PROJECTILE:
                    final VisSelectBox<ProjectileType> projectileTypeSelectBox = new VisSelectBox<>();
                    ActorUtil.selectBox(dataTable, "Projectile Type:", projectileTypeSelectBox, ProjectileType.values());
                    ActorUtil.checkBox(dataTable, "Repeat Sound:", new VisCheckBox(""));
                    break;
                case IMPACT:
                    ActorUtil.checkBox(dataTable, "Entity Hit Flash:", new VisCheckBox(""));
                    break;
            }
        }
    }

    @AllArgsConstructor
    enum AnimationType {
        CAST("Cast Animation"),
        PROJECTILE("Projectile Animation"),
        IMPACT("Impact Animation");

        @Getter
        private final String animationTypeName;
    }

    enum CharacterPose {
        HANDS_UP,
        HANDS_OUT,
        HANDS_DOWN
    }

    enum ProjectileType {
        STEADY_ANIMATION,
        ANIMATION_PER_TILE
    }
}
