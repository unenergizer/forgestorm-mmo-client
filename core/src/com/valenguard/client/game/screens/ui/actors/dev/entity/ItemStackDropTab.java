package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.screens.GameScreen;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.dev.entity.data.ItemStackDropData;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.ItemStackDrop;
import com.valenguard.client.game.world.item.ItemStack;
import com.valenguard.client.game.world.item.ItemStackManager;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.out.AdminEditorEntityPacketOut;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class ItemStackDropTab extends EditorTab {

    private final ItemStackManager itemStackManager = Valenguard.getInstance().getItemStackManager();
    private final int amount = itemStackManager.getItemStackArraySize();
    private final EntityEditor entityEditor;
    private final String title;
    private VisTable content;

    private short entityIDNum = -1;
    private VisLabel entityID = new VisLabel(Short.toString(entityIDNum));
    private int itemStackIDNum = 0;
    private VisValidatableTextField itemStackId = new VisValidatableTextField();
    private VisValidatableTextField stackSize = new VisValidatableTextField();
    private VisValidatableTextField respawnTimeMin = new VisValidatableTextField();
    private VisValidatableTextField respawnTimeMax = new VisValidatableTextField();

    @Getter
    private boolean selectSpawnActivated = false;
    private VisTextButton selectSpawn = new VisTextButton("Select Spawn Location");
    private VisValidatableTextField mapName = new VisValidatableTextField();
    private VisValidatableTextField mapX = new VisValidatableTextField();
    private VisValidatableTextField mapY = new VisValidatableTextField();
    private VisTextButton deleteButton = new VisTextButton("Delete");

    @Getter
    private VisTable itemStackDisplayTable = new VisTable();
    private VisLabel itemStackName = new VisLabel();
    private VisLabel scrollProgress = new VisLabel();
    private VisImage itemStackPreview = new VisImage();

    ItemStackDropTab(EntityEditor entityEditor) {
        this.entityEditor = entityEditor;
        this.title = " ItemStack Drop ";

        build();
    }

    public void loadEntity(ItemStackDrop itemStackDrop) {
        resetValues();
        entityIDNum = itemStackDrop.getServerEntityID();
        entityID.setText(itemStackDrop.getServerEntityID());
        itemStackIDNum = itemStackDrop.getItemStackId();
        stackSize.setText(Integer.toString(itemStackDrop.getStackSize()));
        respawnTimeMin.setText(Integer.toString(itemStackDrop.getRespawnTimeMin()));
        respawnTimeMax.setText(Integer.toString(itemStackDrop.getRespawnTimeMax()));
        mapName.setText(itemStackDrop.getCurrentMapLocation().getMapName());
        mapX.setText(Short.toString(itemStackDrop.getCurrentMapLocation().getX()));
        mapY.setText(Short.toString(itemStackDrop.getCurrentMapLocation().getY()));

        // Load Appearance
        updateEditorDisplay();

        deleteButton.setDisabled(false);
    }

    @Override
    public void resetValues() {
        entityIDNum = -1;
        entityID.setText(Short.toString(entityIDNum));
        itemStackIDNum = 0;
        itemStackId.setText("0");
        stackSize.setText("1");
        respawnTimeMin.setText("");
        respawnTimeMax.setText("");
        selectSpawnActivated = false;
        mapName.setText("");
        mapX.setText("");
        mapY.setText("");

        updateEditorDisplay();
    }

    @Override
    public void build() {
        content = new VisTable(true);
        VisTextButton saveButton = new VisTextButton("Save");
        VisLabel errorLabel = new VisLabel();
        FormValidator validator = new FormValidator(saveButton, errorLabel);
        VisTable leftPane = new VisTable();

        VisTable entityIdTable = new VisTable();
        VisLabel entityIDString = new VisLabel("EntityID: ");
        VisTextButton resetEntityID = new VisTextButton("Reset ID (create new entity)");
        entityIdTable.add(entityIDString).pad(3);
        entityIdTable.add(entityID).pad(3);
        entityIdTable.add(resetEntityID).pad(3);

        resetEntityID.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                entityIDNum = -1;
                entityID.setText(Short.toString(entityIDNum));
            }
        });

        leftPane.add(entityIdTable).row();

        textField(leftPane, "ItemStack ID:", itemStackId);
        textField(leftPane, "ItemStack Amount:", stackSize);
        textField(leftPane, "Minimal Respawn Time (minutes):", respawnTimeMin);
        textField(leftPane, "Maximal Respawn Time (minutes):", respawnTimeMax);
        itemStackId.setDisabled(true);

        validator.valueGreaterThan(stackSize, "Stack size must be greater than 0.", 1, true);
        validator.valueGreaterThan(respawnTimeMin, "Respawn times must be greater than -1.", 0, true);
        validator.valueGreaterThan(respawnTimeMax, "Respawn times must be greater than -1.", 0, true);
        validator.notEmpty(mapName, "Map name must not be empty.");
        validator.valueGreaterThan(mapX, "Map X must be greater than -1.", 0, true);
        validator.valueLesserThan(mapX, "Map X must be less than 97.", 96, true);
        validator.valueGreaterThan(mapY, "Map Y must be greater than -1.", 0, true);
        validator.valueLesserThan(mapY, "Map Y must be less than 97.", 54, true);

        // Spawn location Selection
        mapName.setDisabled(true);
        selectSpawn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectSpawnActivated = true;
                mapName.setText(EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().getMapName());
                selectSpawn.setText("Left Click Map to Set Spawn");
                selectSpawn.setDisabled(true);
                Valenguard.getInstance().getMouseManager().setHighlightHoverTile(true);
            }
        });

        ((GameScreen) Valenguard.getInstance().getScreen()).getMultiplexer().addProcessor(new InputProcessor() {

            private MouseManager mouseManager = Valenguard.getInstance().getMouseManager();

            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (!selectSpawnActivated) return false;
                if (button != Input.Buttons.LEFT) return false;
                selectSpawn.setText("Select Spawn Location");
                selectSpawn.setDisabled(false);
                mapX.setText(Short.toString(mouseManager.getLeftClickTileX()));
                mapY.setText(Short.toString(mouseManager.getLeftClickTileY()));
                selectSpawnActivated = false;
                Valenguard.getInstance().getMouseManager().setHighlightHoverTile(false);
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (!selectSpawnActivated) return false;
                mapX.setText(Short.toString(mouseManager.getMouseTileX()));
                mapY.setText(Short.toString(mouseManager.getMouseTileY()));
                return true;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                if (!selectSpawnActivated) return false;
                mapX.setText(Short.toString(mouseManager.getMouseTileX()));
                mapY.setText(Short.toString(mouseManager.getMouseTileY()));
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        });

        leftPane.add(selectSpawn).row();
        VisTable mapNameTable = new VisTable();
        mapNameTable.add(new VisLabel("Spawn Map:")).grow().pad(1);
        mapNameTable.add(mapName).pad(1);
        leftPane.add(mapNameTable).expandX().fillX().pad(1).row();

        VisTable mapXTable = new VisTable();
        mapXTable.add(new VisLabel("Spawn X:")).grow().pad(1);
        mapXTable.add(mapX).pad(1);
        leftPane.add(mapXTable).expandX().fillX().pad(1).row();

        VisTable mapYTable = new VisTable();
        mapYTable.add(new VisLabel("Spawn Y:")).grow().pad(1);
        mapYTable.add(mapY).pad(1);
        leftPane.add(mapYTable).expandX().fillX().pad(1).row();
        VisTable texturePrintTable = new VisTable();
        VisLabel textures = new VisLabel("DEBUG:");
        VisTextButton textButton = new VisTextButton("Print Details to Console");
        texturePrintTable.add(textures).pad(3);
        texturePrintTable.add(textButton).row();
        leftPane.add(texturePrintTable).row();

        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                println(MonsterTab.class, "--- Settings ---");
                println(MonsterTab.class, "EntityID: " + entityID.getText());
                println(MonsterTab.class, "ItemStackID: " + itemStackId.getText());
                println(MonsterTab.class, "StackSize: " + stackSize.getText());
                println(MonsterTab.class, "Min RespawnTime (minutes): " + respawnTimeMin.getText());
                println(MonsterTab.class, "Max RespawnTime (minutes): " + respawnTimeMax.getText());
                println(MonsterTab.class, "SpawnLocation: " + mapName.getText() + ", X: " + mapX.getText() + ", Y: " + mapY.getText());
                println(MonsterTab.class, "--- Appearance ---");
            }
        });

        // Submit and finalize section
        VisTable submitTable = new VisTable();
        VisTextButton resetButton = new VisTextButton("Reset");
        deleteButton.setDisabled(true);
        submitTable.add(saveButton).pad(3);
        submitTable.add(resetButton).pad(3);
        submitTable.add(deleteButton).pad(3);
        leftPane.add(submitTable).row();
        leftPane.add(errorLabel).row();

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new AdminEditorEntityPacketOut(generateDataOut(true, false)).sendPacket();
                resetValues();
                ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getEntityEditor());
            }
        });

        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetValues();
            }
        });

        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                String id = entityID.getText().toString();
                if (id.equals("-1")) {
                    Dialogs.showOKDialog(ActorUtil.getStage(), "EDITOR WARNING!", "An entity with ID -1 can not be deleted!");
                }
                ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getEntityEditor());
                Dialogs.showOptionDialog(ActorUtil.getStage(), "EDITOR WARNING!", "Are you sure you want to delete this entity? This can not be undone!", Dialogs.OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
                    @Override
                    public void yes() {
                        Dialogs.showOKDialog(ActorUtil.getStage(), "EDITOR WARNING!", "Entity deleted forever!");
                        new AdminEditorEntityPacketOut(generateDataOut(false, true)).sendPacket();
                        resetValues();
                        ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getEntityEditor());
                        Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }

                    @Override
                    public void no() {
                        ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getEntityEditor());
                        Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }

                    @Override
                    public void cancel() {
                        ActorUtil.fadeInWindow(ActorUtil.getStageHandler().getEntityEditor());
                        Valenguard.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }
                });
            }
        });

        content.add(leftPane).fill().pad(3).grow().left().top();
        content.add(buildItemStackViewer(itemStackDisplayTable)).fill().pad(3).grow().left().top().row();

        entityEditor.pack();
    }

    private VisTable buildItemStackViewer(VisTable visTable) {
        // Talk to ItemStack manager and get the number of items.
        final int amount = itemStackManager.getItemStackArraySize();

        // Show "scroll left" and "scroll right" buttons to change item
        VisTextButton scrollLeft = new VisTextButton(" < ");
        VisTextButton scrollRight = new VisTextButton(" > ");

        scrollLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                itemStackIDNum--;

                if (itemStackIDNum < 0) {
                    itemStackIDNum = amount - 1;
                }

                updateEditorDisplay();
            }
        });

        scrollRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                itemStackIDNum++;

                if (itemStackIDNum >= amount) {
                    itemStackIDNum = 0;
                }

                updateEditorDisplay();
            }
        });

        visTable.add(itemStackName).row();

        VisTable scrollTable = new VisTable();
        scrollTable.add(scrollLeft).pad(2);
        scrollTable.add(itemStackPreview).pad(2);
        scrollTable.add(scrollRight).pad(2);
        scrollTable.add(scrollProgress).pad(2);

        visTable.add(scrollTable).row();

        updateEditorDisplay();

        return visTable;
    }

    private void updateEditorDisplay() {
        final int imgSize = 64;
        ItemStack itemStack = itemStackManager.makeItemStack(itemStackIDNum, 0);
        itemStackName.setText(itemStack.getName());
        itemStackId.setText(Integer.toString(itemStackIDNum));
        itemStackPreview.setDrawable(new ImageBuilder(GameAtlas.ITEMS).setWidth(imgSize).setHeight(imgSize).setRegionName(itemStack.getTextureRegion()).buildVisImage().getDrawable());
        scrollProgress.setText(itemStackIDNum + " / " + (amount - 1));
    }

    private ItemStackDropData generateDataOut(boolean save, boolean delete) {
        Location location = new Location(
                mapName.getText(),
                Short.valueOf(mapX.getText()),
                Short.valueOf(mapY.getText()));

        ItemStackDropData entityEditorData = new ItemStackDropData(true, save, delete, location, entityIDNum);

        // Basic data
        entityEditorData.setItemStackId(Integer.parseInt(itemStackId.getText()));
        entityEditorData.setAmount(Integer.parseInt(stackSize.getText()));
        entityEditorData.setRespawnTimeMin(Short.parseShort(respawnTimeMin.getText()));
        entityEditorData.setRespawnTimeMax(Short.parseShort(respawnTimeMax.getText()));

        return entityEditorData;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public Table getContentTable() {
        return content;
    }
}
