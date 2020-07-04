package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.dev.ColorPickerColorHandler;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.EntityEditorData;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.NPCData;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.entities.NPC;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.io.type.GameAtlas;
import com.forgestorm.client.network.game.packet.out.AdminEditorEntityPacketOut;
import com.forgestorm.client.util.color.LibGDXColorList;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class NpcTab extends EditorTab {

    static final int PREVIEW_SCALE = 10;

    private final String title;
    private VisTable content;

    private short entityIDNum = -1;
    private VisLabel entityID = new VisLabel(Short.toString(entityIDNum));
    private VisValidatableTextField name = new VisValidatableTextField();
    private VisValidatableTextField faction = new VisValidatableTextField();
    private VisValidatableTextField health = new VisValidatableTextField();
    private VisValidatableTextField damage = new VisValidatableTextField();
    private VisValidatableTextField expDrop = new VisValidatableTextField();
    private VisValidatableTextField dropTable = new VisValidatableTextField();
    private VisSlider walkSpeed = new VisSlider(.1f, .99f, .01f, false);
    private VisSlider probStill = new VisSlider(0, .99f, .01f, false);
    private VisSlider probWalk = new VisSlider(0, .99f, .01f, false);
    private VisValidatableTextField shopId = new VisValidatableTextField("-1");
    private VisCheckBox isBankKeeper = new VisCheckBox("", false);

    @Getter
    private boolean selectSpawnActivated = false;
    private VisTextButton selectSpawn = new VisTextButton("Select Spawn Location");
    private VisValidatableTextField mapName = new VisValidatableTextField();
    private VisValidatableTextField mapX = new VisValidatableTextField();
    private VisValidatableTextField mapY = new VisValidatableTextField();
    private VisTextButton deleteButton = new VisTextButton("Delete");

    @Getter
    private AppearancePanel appearancePanel;
    @Getter
    private VisTable appearanceTable = new VisTable();
    @Getter
    private VisTable previewTable = new VisTable();

    NpcTab(StageHandler getStageHandler, EntityEditor entityEditor) {
        super(getStageHandler, entityEditor);
        title = " NPC ";

        // SETUP DEFAULT CASE
        appearancePanel = new NPCAppearancePanel(this);
        appearancePanel.buildAppearancePanel();

        build();
    }

    @Override
    public void resetValues() {
        entityIDNum = -1;
        entityID.setText(Short.toString(entityIDNum));
        name.setText("");
        faction.setText("THE EMPIRE");
        health.setText("");
        damage.setText("");
        expDrop.setText("");
        dropTable.setText("");
        walkSpeed.setValue(0);
        probStill.setValue(0);
        probWalk.setValue(0);
        shopId.setText("-1");
        isBankKeeper.setChecked(false);
        selectSpawnActivated = false;
        mapName.setText("");
        mapX.setText("");
        mapY.setText("");

        deleteButton.setDisabled(true);

        // Appearance Data
        if (appearancePanel != null) appearancePanel.reset();

        // Remove right pane appearance fields
//        if (resetAppearanceTable) appearanceTable.clear();
    }

    public void loadAiEntity(AiEntity aiEntity) {
        resetValues();
        NPC npc = (NPC) aiEntity;
        entityIDNum = npc.getServerEntityID();
        entityID.setText(npc.getServerEntityID());

        name.setText(npc.getEntityName());
        faction.setText(ClientMain.getInstance().getFactionManager().getFactionFromByte(npc.getFaction()));
        health.setText(Integer.toString(npc.getMaxHealth()));
        damage.setText(Integer.toString(npc.getDamage()));
        expDrop.setText(Integer.toString(npc.getExpDrop()));
        dropTable.setText(Integer.toString(npc.getDropTable()));
        walkSpeed.setValue(npc.getMoveSpeed());
        probStill.setValue(npc.getProbWalkStill());
        probWalk.setValue(npc.getProbWalkStart());
        shopId.setText(Integer.toString(npc.getShopID()));
        isBankKeeper.setChecked(npc.isBankKeeper());
        mapName.setText(npc.getDefaultSpawnLocation().getMapName());
        mapX.setText(Short.toString(npc.getDefaultSpawnLocation().getX()));
        mapY.setText(Short.toString(npc.getDefaultSpawnLocation().getY()));

        // Load Appearance
        appearanceTable.clear();
        appearancePanel = new NPCAppearancePanel(this);
        appearancePanel.buildAppearancePanel();
        appearancePanel.load(npc);
        appearancePanel.characterPreview();

        deleteButton.setDisabled(false);
    }

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

        textField(leftPane, "Name:", name);
        textField(leftPane, "Faction:", faction);
        textField(leftPane, "Health:", health);
        textField(leftPane, "Damage:", damage);
        textField(leftPane, "ExpDrop:", expDrop);
        textField(leftPane, "DropTable:", dropTable);
        valueSlider(leftPane, "Walk Speed:", walkSpeed);
        valueSlider(leftPane, "Probability Still:", probStill);
        valueSlider(leftPane, "Probability Walk:", probWalk);
        textField(leftPane, "Shop ID:", shopId);
        checkBox(leftPane, "Set as Bank Keeper?", isBankKeeper);

        validator.notEmpty(name, "Name must not be empty.");
        validator.notEmpty(faction, "Faction must not be empty.");
        validator.valueGreaterThan(health, "Health must be greater than 0.", 1, true);
        validator.integerNumber(damage, "Damage must be a valid number.");
        validator.valueGreaterThan(damage, "Damage must be greater than 1", 1);
        validator.integerNumber(expDrop, "Experience Drop must be a valid number.");
        validator.integerNumber(dropTable, "Drop Table must be a valid number.");
        validator.integerNumber(shopId, "Shop ID must be a valid number.");
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
                ClientMain.getInstance().getMouseManager().setHighlightHoverTile(true);
            }
        });

        ClientMain.getInstance().getInputMultiplexer().addProcessor(new InputProcessor() {

            private MouseManager mouseManager = ClientMain.getInstance().getMouseManager();

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
                ClientMain.getInstance().getMouseManager().setHighlightHoverTile(false);
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
                println(NpcTab.class, "--- Settings ---");
                println(NpcTab.class, "EntityID: " + entityID.getText());
                println(NpcTab.class, "Name: " + name.getText());
                println(NpcTab.class, "Faction: " + faction.getText());
                println(NpcTab.class, "Health: " + health.getText());
                println(NpcTab.class, "Damage: " + damage.getText());
                println(NpcTab.class, "ExpDrop: " + expDrop.getText());
                println(NpcTab.class, "DropTable: " + dropTable.getText());
                println(NpcTab.class, "WalkSpeed: " + walkSpeed.getValue());
                println(NpcTab.class, "Probability Still: " + probStill.getValue());
                println(NpcTab.class, "Probability Walk: " + probWalk.getValue());
                println(NpcTab.class, "ShopID: " + shopId.getText());
                println(NpcTab.class, "IsBanker: " + isBankKeeper.isChecked());
                println(NpcTab.class, "SpawnLocation: " + mapName.getText() + ", X: " + mapX.getText() + ", Y: " + mapY.getText());
                println(NpcTab.class, "--- Appearance ---");

                appearancePanel.printDebug();
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
                ActorUtil.fadeOutWindow(getStageHandler().getEntityEditor());
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
            }
        });

        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetValues();
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
            }
        });

        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
                String id = entityID.getText().toString();
                if (id.equals("-1")) {
                    Dialogs.showOKDialog(getStageHandler().getStage(), "EDITOR WARNING!", "An entity with ID -1 can not be deleted!");
                }
                ActorUtil.fadeOutWindow(getStageHandler().getEntityEditor());
                Dialogs.showOptionDialog(getStageHandler().getStage(), "EDITOR WARNING!", "Are you sure you want to delete this entity? This can not be undone!", Dialogs.OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
                    @Override
                    public void yes() {
                        Dialogs.showOKDialog(getStageHandler().getStage(), "EDITOR WARNING!", "Entity deleted forever!");
                        new AdminEditorEntityPacketOut(generateDataOut(false, true)).sendPacket();
                        resetValues();
                        ActorUtil.fadeOutWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
                    }

                    @Override
                    public void no() {
                        ActorUtil.fadeInWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
                    }

                    @Override
                    public void cancel() {
                        ActorUtil.fadeInWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
                    }
                });
            }
        });

        content.add(leftPane).fill().pad(3).grow().left().top();
        content.add(appearanceTable).fill().pad(3).grow().left().top().row();

        getEntityEditor().pack();
    }

    void colorPicker(VisTable mainTable, String labelName, final VisSelectBox visSelectBox, final ColorPickerColorHandler colorPickerColorHandler) {
        VisTable visTable = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        VisTextButton visTextButton = new VisTextButton("Pick Color");
        visTable.add(visLabel).grow().pad(1);
        visTable.add(visSelectBox).pad(1);
        visTable.add(visTextButton).pad(1);
        mainTable.add(visTable).expandX().fillX().pad(1).row();

        visSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                @SuppressWarnings("ConstantConditions") Color color = LibGDXColorList.getType((byte) visSelectBox.getSelectedIndex()).getColor();
                colorPickerColorHandler.doColorChange(color);
                colorPickerColorHandler.setFinishedColor(color);
            }
        });

        visTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStageHandler().getColorPickerController().show(colorPickerColorHandler);
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    VisTable imageTable(int width, int height, int padBottom, String region, Color color) {
        VisTable innerTable = new VisTable();

        TextureAtlas textureAtlas = ClientMain.getInstance().getFileManager().getAtlas(GameAtlas.ENTITY_CHARACTER);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureAtlas.findRegion(region));
        textureRegionDrawable.setMinWidth(width);
        textureRegionDrawable.setMinHeight(height);

        VisImage texture = new VisImage(textureRegionDrawable);
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setColor(color);

        innerTable.add(texture).expand().fillX().bottom().left().padBottom(padBottom);
        return innerTable;
    }

    private EntityEditorData generateDataOut(boolean save, boolean delete) {
        Location location = new Location(
                mapName.getText(),
                Short.valueOf(mapX.getText()),
                Short.valueOf(mapY.getText()));

        EntityEditorData entityEditorData = new NPCData(true, save, delete, location, entityIDNum);

        // Basic data
        ((NPCData) entityEditorData).setName(name.getText());
        ((NPCData) entityEditorData).setFaction(faction.getText());
        ((NPCData) entityEditorData).setHealth(Integer.valueOf(health.getText()));
        ((NPCData) entityEditorData).setDamage(Integer.valueOf(damage.getText()));
        ((NPCData) entityEditorData).setExpDrop(Integer.valueOf(expDrop.getText()));
        ((NPCData) entityEditorData).setDropTable(Integer.valueOf(dropTable.getText()));
        ((NPCData) entityEditorData).setWalkSpeed(walkSpeed.getValue());
        ((NPCData) entityEditorData).setProbStop(probStill.getValue());
        ((NPCData) entityEditorData).setProbWalk(probWalk.getValue());
        ((NPCData) entityEditorData).setShopId(Short.valueOf(shopId.getText()));
        ((NPCData) entityEditorData).setBankKeeper(isBankKeeper.isChecked());

        // Appearance
        entityEditorData = appearancePanel.getDataOut(entityEditorData);

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