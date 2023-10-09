package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.ImageBuilder;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.ItemStackDropData;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.ItemStackDrop;
import com.forgestorm.client.game.world.item.ItemStackManager;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.packet.out.AdminEditorEntityPacketOut;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.io.type.GameAtlas;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import lombok.Getter;

import static com.forgestorm.client.game.screens.ui.actors.ActorUtil.fadeInWindow;
import static com.forgestorm.client.game.screens.ui.actors.ActorUtil.fadeOutWindow;
import static com.forgestorm.client.game.screens.ui.actors.ActorUtil.textField;
import static com.forgestorm.client.util.Log.println;

public class ItemStackDropTab extends EditorTab {

    private final ItemStackManager itemStackManager = ClientMain.getInstance().getItemStackManager();
    private final int amount = itemStackManager.getItemStackArraySize();
    private final String title;
    private VisTable content;

    private short entityIDNum = -1;
    private final VisLabel entityID = new VisLabel(Short.toString(entityIDNum));
    private int itemStackIDNum = 0;
    private final VisValidatableTextField itemStackId = new VisValidatableTextField();
    private final VisValidatableTextField stackSize = new VisValidatableTextField();
    private final VisValidatableTextField respawnTimeMin = new VisValidatableTextField();
    private final VisValidatableTextField respawnTimeMax = new VisValidatableTextField();

    @Getter
    private boolean selectSpawnActivated = false;
    private final VisTextButton selectSpawn = new VisTextButton("Select Spawn Location");
    private final VisValidatableTextField worldName = new VisValidatableTextField();
    private final VisValidatableTextField mapX = new VisValidatableTextField();
    private final VisValidatableTextField mapY = new VisValidatableTextField();
    private final VisSelectBox<Floors> mapZ = new VisSelectBox<Floors>();
    private final VisTextButton deleteButton = new VisTextButton("Delete");

    @Getter
    private final VisTable itemStackDisplayTable = new VisTable();
    private final VisLabel itemStackName = new VisLabel();
    private final VisLabel scrollProgress = new VisLabel();
    private final VisImage itemStackPreview = new VisImage();

    ItemStackDropTab(StageHandler stageHandler, EntityEditor entityEditor) {
        super(stageHandler, entityEditor);
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
        worldName.setText(itemStackDrop.getCurrentMapLocation().getWorldName());
        mapX.setText(Integer.toString(itemStackDrop.getCurrentMapLocation().getX()));
        mapY.setText(Integer.toString(itemStackDrop.getCurrentMapLocation().getY()));
        mapZ.setSelected(Floors.getFloor(itemStackDrop.getCurrentMapLocation().getZ()));

        // Load Appearance
        updateEditorDisplay();

        deleteButton.setDisabled(false);
    }

    @Override
    public void resetValues() {
        entityIDNum = -1;
        entityID.setText(Integer.toString(entityIDNum));
        itemStackIDNum = 0;
        itemStackId.setText("0");
        stackSize.setText("1");
        respawnTimeMin.setText("");
        respawnTimeMax.setText("");
        selectSpawnActivated = false;
        worldName.setText("");
        mapX.setText("");
        mapY.setText("");
        mapZ.setSelected(Floors.GROUND_FLOOR);

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
        validator.notEmpty(worldName, "Map name must not be empty.");
        validator.integerNumber(mapX, "Map X must be a valid number.");
        validator.integerNumber(mapY, "Map Y must be a valid number.");

        // Spawn location Selection
        worldName.setDisabled(true);
        selectSpawn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectSpawnActivated = true;
                worldName.setText(EntityManager.getInstance().getPlayerClient().getCurrentMapLocation().getWorldName());
                selectSpawn.setText("Left Click Map to Set Spawn");
                selectSpawn.setDisabled(true);
                ClientMain.getInstance().getMouseManager().setHighlightHoverTile(true);
            }
        });

        ClientMain.getInstance().getInputMultiplexer().addProcessor(new InputProcessor() {

            private final MouseManager mouseManager = ClientMain.getInstance().getMouseManager();

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
                mapX.setText(Integer.toString(mouseManager.getLeftClickTileX()));
                mapY.setText(Integer.toString(mouseManager.getLeftClickTileY()));
                selectSpawnActivated = false;
                ClientMain.getInstance().getMouseManager().setHighlightHoverTile(false);
                return true;
            }

            @Override
            public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (!selectSpawnActivated) return false;
                mapX.setText(Integer.toString(mouseManager.getMouseTileX()));
                mapY.setText(Integer.toString(mouseManager.getMouseTileY()));
                return true;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                if (!selectSpawnActivated) return false;
                mapX.setText(Integer.toString(mouseManager.getMouseTileX()));
                mapY.setText(Integer.toString(mouseManager.getMouseTileY()));
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        });

        leftPane.add(selectSpawn).row();
        VisTable worldNameTable = new VisTable();
        worldNameTable.add(new VisLabel("Spawn Map:")).grow().pad(1);
        worldNameTable.add(worldName).pad(1);
        leftPane.add(worldNameTable).expandX().fillX().pad(1).row();

        VisTable mapXTable = new VisTable();
        mapXTable.add(new VisLabel("Spawn X:")).grow().pad(1);
        mapXTable.add(mapX).pad(1);
        leftPane.add(mapXTable).expandX().fillX().pad(1).row();

        VisTable mapYTable = new VisTable();
        mapYTable.add(new VisLabel("Spawn Y:")).grow().pad(1);
        mapYTable.add(mapY).pad(1);
        leftPane.add(mapYTable).expandX().fillX().pad(1).row();

        VisTable mapZTable = new VisTable();
        mapZ.setItems(Floors.values());
        mapZTable.add(new VisLabel("Spawn Z:")).grow().pad(1);
        mapZTable.add(mapZ).pad(1);
        leftPane.add(mapZTable).expandX().fillX().pad(1).row();

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
                println(MonsterTab.class, "SpawnLocation: " + worldName.getText() + ", X: " + mapX.getText() + ", Y: " + mapY.getText() + ", Z: " + mapZ.getSelected().getName());
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
                fadeOutWindow(getStageHandler().getEntityEditor());
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
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                String id = entityID.getText().toString();
                if (id.equals("-1")) {
                    Dialogs.showOKDialog(getStageHandler().getStage(), "EDITOR WARNING!", "An entity with ID -1 can not be deleted!");
                }
                fadeOutWindow(getStageHandler().getEntityEditor());
                Dialogs.showOptionDialog(getStageHandler().getStage(), "EDITOR WARNING!", "Are you sure you want to delete this entity? This can not be undone!", Dialogs.OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
                    @Override
                    public void yes() {
                        Dialogs.showOKDialog(getStageHandler().getStage(), "EDITOR WARNING!", "Entity deleted forever!");
                        new AdminEditorEntityPacketOut(generateDataOut(false, true)).sendPacket();
                        resetValues();
                        fadeOutWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }

                    @Override
                    public void no() {
                        fadeInWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }

                    @Override
                    public void cancel() {
                        fadeInWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }
                });
            }
        });

        content.add(leftPane).fill().pad(3).grow().left().top();
        content.add(buildItemStackViewer(itemStackDisplayTable)).fill().pad(3).grow().left().top().row();

        getEntityEditor().pack();
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
        itemStackPreview.setDrawable(new ImageBuilder(GameAtlas.ITEMS).setWidth(imgSize).setHeight(imgSize).setRegionName(itemStack.getTextureRegionName()).buildVisImage().getDrawable());
        scrollProgress.setText(itemStackIDNum + " / " + (amount - 1));

        // Prevent illegal stack size setting
        if (itemStack.getStackable() == 1) {
            stackSize.setText("1");
            stackSize.setDisabled(true);
        } else {
            stackSize.setDisabled(false);
        }
    }

    private ItemStackDropData generateDataOut(boolean save, boolean delete) {
        Location location = new Location(
                worldName.getText(),
                Integer.parseInt(mapX.getText()),
                Integer.parseInt(mapY.getText()),
                mapZ.getSelected().getWorldZ());

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
