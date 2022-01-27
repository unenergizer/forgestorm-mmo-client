package com.forgestorm.client.game.screens.ui.actors.dev.world;

import static com.forgestorm.client.util.Log.println;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.maps.building.WorldBuilder;
import com.forgestorm.client.game.world.maps.tile.TileAnimation;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.util.yaml.YamlUtil;
import com.forgestorm.shared.io.type.GameAtlas;
import com.forgestorm.shared.util.StringUtil;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import java.io.File;
import java.util.Map;

public class TileAnimationEditor extends HideableVisWindow implements Buildable {

    private final WorldBuilder worldBuilder = ClientMain.getInstance().getWorldBuilder();
    private final TextureAtlas gameAtlas;

    private final VisTable animatedTileListContentTable = new VisTable();
    private final VisTable editorTableContent = new VisTable();
    private final VisTable animatedTable = new VisTable();

    private TileAnimation workingTileAnimation;

    private TileSelectWindow tileSelectWindow;
    private final VisTextButton saveButton = new VisTextButton("Save All Animations");
    private final VisLabel errorLabel = new VisLabel();
    private final FormValidator validator = new FormValidator(saveButton, errorLabel);

    public TileAnimationEditor() {
        super("Tile Animation Editor");
        this.gameAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.TILES);
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        if (tileSelectWindow == null) {
            tileSelectWindow = new TileSelectWindow(stageHandler);
            stageHandler.getStage().addActor(tileSelectWindow);
        }

        // Button view
        VisTable editorTable = new VisTable();
        editorTable.add(animatedTable);
        editorTable.add(editorTableContent).growX();

        // Build the layout
        VisTable layoutTable = new VisTable();
        layoutTable.add(updateAnimatedTileList()).growY();
        layoutTable.addSeparator(true).growY();
        layoutTable.add(editorTable).growX().row();

        VisTable masterTable = new VisTable();
        masterTable.add(layoutTable).grow().row();
        masterTable.addSeparator().row();
        masterTable.add(buildMainTableButtons());

        add(masterTable).grow();

        centerWindow();
        addCloseButton();
        stopWindowClickThrough();
        setResizable(true);
        setSize(400, 300);
        return this;
    }

    private void setWorkingTileAnimation(TileAnimation tileAnimation) {
        if (tileAnimation != null) workingTileAnimation = tileAnimation;
    }

    public void render() {
        animatedTable.clear();
        if (workingTileAnimation == null) return;

        animatedTable.add(new VisLabel("Preview:")).row();

        int activeFrame = workingTileAnimation.getActiveFrame();

        // Return if their is no active frame
        if (activeFrame == -1) return;

        TileImage tileImage = worldBuilder.getTileImage(workingTileAnimation.getAnimationFrame(activeFrame).getTileId());
        animatedTable.add(new ImageBuilder(GameAtlas.TILES, tileImage.getFileName()).setSize(32).buildVisImage());
    }

    private VisTable buildMainTableButtons() {
        VisTable controlsTable = new VisTable();
        VisTextButton createAnimation = new VisTextButton("Create Animation");
        VisTextButton deleteAnimation = new VisTextButton("Delete Animation");
        saveButton.setColor(Color.GREEN);
        deleteAnimation.setColor(Color.RED);

        controlsTable.add(createAnimation);
        controlsTable.add(deleteAnimation);
        controlsTable.add(saveButton).row();
        controlsTable.add(errorLabel).colspan(3);

        createAnimation.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Map<Integer, TileAnimation> tileAnimationMap = worldBuilder.getTileAnimationMap();

                // Get highest animationID number in the animation list
                int highestNumber = 0;
                for (int i = 1; i < tileAnimationMap.size(); i++) {
                    if (i < highestNumber) continue;
                    highestNumber = i;
                }

                // Loop through the list. Find an unused ID or get one at the end
                int animationID = 0;
                for (int i = 1; i < highestNumber + 10; i++) {
                    if (tileAnimationMap.containsKey(i)) continue;
                    animationID = i;
                    break;
                }

                tileAnimationMap.put(animationID, new TileAnimation(animationID, TileAnimation.PlaybackType.PLAY_NORMAL_LOOPING));
                setWorkingTileAnimation(tileAnimationMap.get(animationID));

                // Update view
                updateAnimatedTileList();
                updateEditorTableContent();
            }
        });

        deleteAnimation.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                int animationID = workingTileAnimation.getAnimationId();
                worldBuilder.getTileAnimationMap().remove(animationID);
                workingTileAnimation = null;

                updateEditorTableContent();
                updateAnimatedTileList();
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String filePath = ClientMain.getInstance().getFileManager().getClientFilesDirectoryPath() + File.separator + "TileAnimations.yaml";
                YamlUtil.saveYamlToFile(worldBuilder.getTileAnimationMap(), filePath);
                println(getClass(), filePath);
            }
        });

        return controlsTable;
    }

    private VisTable buildEditorTableButtons() {
        VisTable controlsTable = new VisTable();
        VisTextButton addTile = new VisTextButton("Add Tile");

        controlsTable.add(addTile);

        addTile.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!tileSelectWindow.isVisible()) {
                    ActorUtil.fadeInWindow(tileSelectWindow);
                }
            }
        });


        return controlsTable;
    }

    private void updateEditorTableContent() {
        editorTableContent.clear();

        if (workingTileAnimation == null) return;

        // Set animation playback type
        VisTable controlsTable = new VisTable(true);
        VisLabel controls = new VisLabel("Playback Type: ");

        VisSelectBox<TileAnimation.PlaybackType> animationControlsVisSelectBox = new VisSelectBox<>();
        animationControlsVisSelectBox.setItems(TileAnimation.PlaybackType.values());
        animationControlsVisSelectBox.setSelected(workingTileAnimation.getPlaybackType());

        animationControlsVisSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingTileAnimation.playAnimation(animationControlsVisSelectBox.getSelected());
            }
        });

        controlsTable.add(controls);
        controlsTable.add(animationControlsVisSelectBox);

        editorTableContent.add(controlsTable).align(Alignment.LEFT.getAlignment()).row();

        // Add frames to table
        for (Map.Entry<Integer, TileAnimation.AnimationFrame> entry : workingTileAnimation.getAnimationFrames().entrySet()) {
            VisTable frameTable = new VisTable(true);
            final int frameId = entry.getKey();
            final TileAnimation.AnimationFrame animationFrame = entry.getValue();

            VisLabel frameLabel = new VisLabel(Integer.toString(frameId));

            // Build image
            TileImage tileImage = worldBuilder.getTileImage(animationFrame.getTileId());
            final VisImage visImageButton = new VisImage(new ImageBuilder(GameAtlas.TILES, tileImage.getFileName()).setSize(32).buildTextureRegionDrawable());

            // Duration
            VisTable durationTable = new VisTable();
            durationTable.add(new VisLabel("Duration:"));

            final VisValidatableTextField duration = new VisValidatableTextField();
            duration.setText(Integer.toString(animationFrame.getDuration()));
            validator.notEmpty(duration, "This field must not be empty.");
            validator.integerNumber(duration, "Must contain an integer value.");
            validator.valueGreaterThan(duration, "Value must be greater than 0", 0);

            durationTable.add(duration).row();

            durationTable.add("File: " + tileImage.getFileName()).colspan(2);

            duration.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String durationLength = duration.getText();

                    // If the durationLength String contains anything other than numbers,
                    // then we do not update the frame with new animation time.
                    if (!StringUtil.isNumeric(durationLength)) return;

                    // Update the frame animation as it is being typed
                    TileAnimation.AnimationFrame animationFrame = workingTileAnimation.getAnimationFrame(frameId);
                    workingTileAnimation.changeFrameDuration(animationFrame.getFrameId(), Integer.parseInt(durationLength));
                }
            });

            // Change position
            VisTable positionTable = new VisTable();
            VisTextButton moveUp = new VisTextButton("/\\");
            VisTextButton moveDown = new VisTextButton("\\/");
            VisTextButton deleteButton = new VisTextButton("X");

            if (frameId == 0) moveUp.setDisabled(true);
            if (frameId == workingTileAnimation.getAnimationFrames().size() - 1)
                moveDown.setDisabled(true);

            moveUp.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    boolean success = workingTileAnimation.swapFrame(frameId, TileAnimation.SwapDirection.MOVE_UP);
                    if (success) {
                        updateEditorTableContent();
                        updateAnimatedTileList();
                    }
                }
            });

            moveDown.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    boolean success = workingTileAnimation.swapFrame(frameId, TileAnimation.SwapDirection.MOVE_DOWN);
                    if (success) {
                        updateEditorTableContent();
                        updateAnimatedTileList();
                    }
                }
            });

            deleteButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    workingTileAnimation.removeFrame(frameId);

                    updateEditorTableContent();
                    updateAnimatedTileList();
                }
            });

            positionTable.add(moveUp);
            positionTable.add(moveDown);
            positionTable.add(deleteButton);

            // Add content to table
            frameTable.add(frameLabel);
            frameTable.add(visImageButton);
            frameTable.add(durationTable);
            frameTable.add(positionTable);

            editorTableContent.add(frameTable).row();
        }
        editorTableContent.add(buildEditorTableButtons()).align(Alignment.LEFT.getAlignment());
    }

    private VisTable updateAnimatedTileList() {
        animatedTileListContentTable.clear();

        VisTable buttonTable = new VisTable();
        buttonTable.setWidth(25);
        // Create a scroll pane.
        VisScrollPane animatedScrollPane = new VisScrollPane(buttonTable);
        animatedScrollPane.setOverscroll(false, false);
        animatedScrollPane.setFlickScroll(false);
        animatedScrollPane.setFadeScrollBars(false);
        animatedScrollPane.setScrollbarsOnTop(true);
        animatedScrollPane.setScrollingDisabled(true, false);
        animatedTileListContentTable.add(animatedScrollPane).prefHeight(1).grow().colspan(2);

        // QUERY SOME LIST OF ANIMATIONS AND POPULATE THIS "EXISTING" LIST VIEW
        Map<Integer, TileAnimation> tileAnimationMap = worldBuilder.getTileAnimationMap();

        for (TileAnimation tileAnimation : tileAnimationMap.values()) {

            // Test to see if we can use a tile image here for as an icon.
            String tileImageRegionName;
            if (tileAnimation.getAnimationFrame(0) == null) {
                // No tile image exists, we just pick the first tile.
                tileImageRegionName = worldBuilder.getTileImage(1).getFileName();
            } else {
                tileImageRegionName = worldBuilder.getTileImage(tileAnimation.getAnimationFrame(0).getTileId()).getFileName();
            }

            final VisImageButton animationImageButton = new VisImageButton(new ImageBuilder(GameAtlas.TILES, tileImageRegionName).setSize(32).buildTextureRegionDrawable());
            buttonTable.add(animationImageButton).row();

            animationImageButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Get the animation clicked
                    setWorkingTileAnimation(tileAnimation);

                    // Now rebuild the animation editor settings
                    updateEditorTableContent();
                }
            });
        }

        return animatedTileListContentTable;
    }

    /**
     * A Window for selecting tiles to animate
     */
    class TileSelectWindow extends HideableVisWindow implements Buildable {

        private VisScrollPane scrollPane;
        private TileImage tileSelected;

        public TileSelectWindow(StageHandler stageHandler) {
            super("Tile Select");
            build(stageHandler);
        }

        @Override
        public Actor build(StageHandler stageHandler) {
            VisTable contentTable = new VisTable(true);
            VisTable buttonTable = new VisTable();
            // Create a scroll pane.
            scrollPane = new VisScrollPane(buttonTable);
            scrollPane.setOverscroll(false, false);
            scrollPane.setFlickScroll(false);
            scrollPane.setFadeScrollBars(false);
            scrollPane.setScrollbarsOnTop(true);
            scrollPane.setScrollingDisabled(false, false);
            contentTable.add(scrollPane).prefHeight(1).grow().row();

            buildImageList(buttonTable);

            // Add use tile button
            VisTextButton useTileButton = new VisTextButton("Use Tile");
            contentTable.add(useTileButton);
            useTileButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    int numberOfFrames = workingTileAnimation.getNumberOfFrames();
                    workingTileAnimation.addAnimationFrame(numberOfFrames, tileSelected.getImageId(), 111);
                    updateEditorTableContent();

                    // If were adding the first frame of a newly created animation list, update the main list with the new tile image.
                    if (numberOfFrames == 0) updateAnimatedTileList();

                    //reset
                    tileSelected = null;
                    ActorUtil.fadeOutWindow(tileSelectWindow);
                }
            });

            add(contentTable).grow().expand();
            setSize(400, 450);
            centerWindow();
            addCloseButton();
            stopWindowClickThrough();
            return this;
        }

        private void buildImageList(VisTable buttonTable) {
            buttonTable.clear();

            int tilesAdded = 0;
            VisTable moduloTable = null;

            //noinspection GDXJavaUnsafeIterator
            for (final TextureAtlas.AtlasRegion atlasRegion : gameAtlas.getRegions()) {

                TileImage tileImageFound = null;
                for (TileImage tileImage : worldBuilder.getTileImageMap().values()) {
                    if (tileImage.getFileName().equals(atlasRegion.name)) {
                        tileImageFound = tileImage;
                        break;
                    }
                }

                // If a tile image was not found, skip this entry
                if (tileImageFound == null) continue;

                if (tilesAdded % 8 == 0) {
                    // Create a new table every X amount of images added for scrolling purposes
                    moduloTable = new VisTable();
                    buttonTable.add(moduloTable).align(Alignment.LEFT.getAlignment()).row();
                }
                tilesAdded++;
                final VisImageButton visImageButton = new VisImageButton(new ImageBuilder(GameAtlas.TILES, atlasRegion.name).setSize(32).buildTextureRegionDrawable());
                moduloTable.add(visImageButton);


                final TileImage finalTileImageFound = tileImageFound;
                visImageButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        tileSelected = finalTileImageFound;
                    }
                });
            }

            scrollPane.layout();
            pack();
        }
    }
}
