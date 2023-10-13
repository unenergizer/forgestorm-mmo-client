package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.dev.ColorPickerColorHandler;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.EntityEditorData;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.NPCData;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.game.world.entities.NPC;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.packet.out.AdminEditorEntityPacketOut;
import com.forgestorm.shared.game.world.entities.FirstInteraction;
import com.forgestorm.shared.game.world.maps.Floors;
import com.forgestorm.shared.io.type.GameAtlas;
import com.forgestorm.shared.util.color.LibGDXColorList;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.*;
import lombok.Getter;

import static com.forgestorm.client.game.screens.ui.actors.ActorUtil.*;
import static com.forgestorm.client.util.Log.println;

public class NpcTab extends EditorTab {

    static final int PREVIEW_SCALE = 10;

    private final String title;
    private VisTable content;

    private short entityIDNum = -1;
    private final VisLabel entityID = new VisLabel(Short.toString(entityIDNum));
    private final VisValidatableTextField name = new VisValidatableTextField();
    private final VisSelectBox<FirstInteraction> firstInteraction = new VisSelectBox<FirstInteraction>();
    private final VisValidatableTextField faction = new VisValidatableTextField();
    private final VisValidatableTextField health = new VisValidatableTextField();
    private final VisValidatableTextField damage = new VisValidatableTextField();
    private final VisValidatableTextField expDrop = new VisValidatableTextField();
    private final VisValidatableTextField dropTable = new VisValidatableTextField();
    private final VisValidatableTextField walkSpeed = new VisValidatableTextField();
    private final VisSlider probStill = new VisSlider(0, .99f, .01f, false);
    private final VisSlider probWalk = new VisSlider(0, .99f, .01f, false);
    private final VisValidatableTextField shopId = new VisValidatableTextField("-1");
    private final VisCheckBox isBankKeeper = new VisCheckBox("", false);

    @Getter
    private boolean selectSpawnActivated = false;
    private final VisTextButton selectSpawn = new VisTextButton("Select Spawn Location");
    private final VisValidatableTextField worldName = new VisValidatableTextField();
    private final VisValidatableTextField mapX = new VisValidatableTextField();
    private final VisValidatableTextField mapY = new VisValidatableTextField();
    private final VisSelectBox<Floors> mapZ = new VisSelectBox<Floors>();
    private final VisValidatableTextField walkRadius = new VisValidatableTextField();
    private final VisTextButton deleteButton = new VisTextButton("Delete");

    @Getter
    private AppearancePanel appearancePanel;
    @Getter
    private final VisTable appearanceTable = new VisTable();
    @Getter
    private final VisTable previewTable = new VisTable();

    private final StageHandler stageHandler;

    NpcTab(StageHandler getStageHandler, EntityEditor entityEditor) {
        super(getStageHandler, entityEditor);
        this.stageHandler = getStageHandler;
        title = " NPC ";

        // SETUP DEFAULT CASE
        appearancePanel = new NPCAppearancePanel(stageHandler.getClientMain(), this);
        appearancePanel.buildAppearancePanel();

        build();
    }

    @Override
    public void resetValues() {
        entityIDNum = -1;
        entityID.setText(Short.toString(entityIDNum));
        name.setText("");
        firstInteraction.setSelected(FirstInteraction.ATTACK);
        faction.setText("THE EMPIRE");
        health.setText("");
        damage.setText("");
        expDrop.setText("");
        dropTable.setText("");
        walkSpeed.setText("");
        probStill.setValue(0);
        probWalk.setValue(0);
        shopId.setText("-1");
        isBankKeeper.setChecked(false);
        selectSpawnActivated = false;
        worldName.setText("");
        mapX.setText("");
        mapY.setText("");
        mapZ.setSelected(Floors.GROUND_FLOOR);
        walkRadius.setText("");

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
        firstInteraction.setSelected(npc.getFirstInteraction());
        faction.setText(stageHandler.getClientMain().getFactionManager().getFactionFromByte(npc.getFaction()));
        health.setText(Integer.toString(npc.getMaxHealth()));
        damage.setText(Integer.toString(npc.getDamage()));
        expDrop.setText(Integer.toString(npc.getExpDrop()));
        dropTable.setText(Integer.toString(npc.getDropTable()));
        walkSpeed.setText(Float.toString(npc.getMoveSpeed()));
        probStill.setValue(npc.getProbWalkStill());
        probWalk.setValue(npc.getProbWalkStart());
        shopId.setText(Integer.toString(npc.getShopID()));
        isBankKeeper.setChecked(npc.isBankKeeper());
        worldName.setText(npc.getDefaultSpawnLocation().getWorldName());
        mapX.setText(Integer.toString(npc.getDefaultSpawnLocation().getX()));
        mapY.setText(Integer.toString(npc.getDefaultSpawnLocation().getY()));
        mapZ.setSelected(Floors.getFloor(npc.getDefaultSpawnLocation().getZ()));
        walkRadius.setText(Integer.toString(npc.getWalkRadius()));

        // Load Appearance
        appearanceTable.clear();
        appearancePanel = new NPCAppearancePanel(stageHandler.getClientMain(), this);
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

        firstInteraction.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                switch (firstInteraction.getSelected()) {
                    case SHOP:
                        if (Integer.parseInt(shopId.getText()) != -1) return;
                        Dialogs.showOKDialog(stageHandler.getStage(), "Error!", "You must set a valid \"ShopID\"!");
                        firstInteraction.setSelected(FirstInteraction.ATTACK);
                        break;
                    case BANK:
                        if (isBankKeeper.isChecked()) return;
                        Dialogs.showOKDialog(stageHandler.getStage(), "Error!", "You must check \"Set as Banker Keeper\"!");
                        firstInteraction.setSelected(FirstInteraction.ATTACK);
                        break;
                }
            }
        });

        ClientMain clientMain = stageHandler.getClientMain();
        textField(clientMain, leftPane, "Name:", name);
        selectBox(clientMain, leftPane, "FirstInteraction:", firstInteraction, FirstInteraction.values());
        textField(clientMain, leftPane, "Faction:", faction);
        textField(clientMain, leftPane, "Health:", health);
        textField(clientMain, leftPane, "Damage:", damage);
        textField(clientMain, leftPane, "ExpDrop:", expDrop);
        textField(clientMain, leftPane, "DropTable:", dropTable);
        textField(clientMain, leftPane, "Walk Speed:", walkSpeed);
        valueSlider(clientMain, leftPane, "Probability Still:", probStill, getDecimalFormat());
        valueSlider(clientMain, leftPane, "Probability Walk:", probWalk, getDecimalFormat());
        textField(clientMain, leftPane, "Shop ID:", shopId);
        checkBox(clientMain, leftPane, "Set as Bank Keeper?", isBankKeeper);

        validator.notEmpty(name, "Name must not be empty.");
        validator.notEmpty(faction, "Faction must not be empty.");
        validator.valueGreaterThan(health, "Health must be greater than 0.", 1, true);
        validator.integerNumber(damage, "Damage must be a valid number.");
        validator.valueGreaterThan(damage, "Damage must be greater than 1", 1);
        validator.integerNumber(expDrop, "Experience Drop must be a valid number.");
        validator.integerNumber(dropTable, "Drop Table must be a valid number.");
        validator.floatNumber(walkSpeed, "Walk Speed must be a valid number.");
        validator.valueLesserThan(walkSpeed, "Walk Speed must be less than 59.", 59, true);
        validator.valueGreaterThan(walkSpeed, "Walk Speed must be greater than 0.", 0, true);
        validator.integerNumber(shopId, "Shop ID must be a valid number.");
        validator.notEmpty(worldName, "Map name must not be empty.");
        validator.integerNumber(mapX, "Map X must be a valid number.");
        validator.integerNumber(mapY, "Map Y must be a valid number.");
        validator.integerNumber(walkRadius, "Walk Radius must be a valid number.");

        // Spawn location Selection
        worldName.setDisabled(true);
        selectSpawn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectSpawnActivated = true;
                worldName.setText(stageHandler.getClientMain().getEntityManager().getPlayerClient().getCurrentMapLocation().getWorldName());
                selectSpawn.setText("Left Click Map to Set Spawn");
                selectSpawn.setDisabled(true);
                stageHandler.getClientMain().getMouseManager().setHighlightHoverTile(true);
            }
        });

        stageHandler.getClientMain().getInputMultiplexer().addProcessor(new InputProcessor() {

            private final MouseManager mouseManager = stageHandler.getClientMain().getMouseManager();

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
                stageHandler.getClientMain().getMouseManager().setHighlightHoverTile(false);
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

        VisTable walkRadiusTable = new VisTable();
        walkRadiusTable.add(new VisLabel("Walk Radius:")).grow().pad(1);
        walkRadiusTable.add(walkRadius).pad(1);
        leftPane.add(walkRadiusTable).expandX().fillX().pad(1).row();

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
                println(NpcTab.class, "FirstInteraction: " + firstInteraction.getSelected());
                println(NpcTab.class, "Faction: " + faction.getText());
                println(NpcTab.class, "Health: " + health.getText());
                println(NpcTab.class, "Damage: " + damage.getText());
                println(NpcTab.class, "ExpDrop: " + expDrop.getText());
                println(NpcTab.class, "DropTable: " + dropTable.getText());
                println(NpcTab.class, "WalkSpeed: " + walkSpeed.getText());
                println(NpcTab.class, "Probability Still: " + probStill.getValue());
                println(NpcTab.class, "Probability Walk: " + probWalk.getValue());
                println(NpcTab.class, "ShopID: " + shopId.getText());
                println(NpcTab.class, "IsBanker: " + isBankKeeper.isChecked());
                println(NpcTab.class, "SpawnLocation: " + worldName.getText() + ", X: " + mapX.getText() + ", Y: " + mapY.getText() + ", Z: " + mapZ.getSelected().getWorldZ());
                println(NpcTab.class, "Walk Radius: " + walkRadius.getText());
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
                new AdminEditorEntityPacketOut(clientMain, generateDataOut(true, false)).sendPacket();
                resetValues();
                fadeOutWindow(getStageHandler().getEntityEditor());
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
            }
        });

        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetValues();
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
            }
        });

        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
                String id = entityID.getText().toString();
                if (id.equals("-1")) {
                    Dialogs.showOKDialog(getStageHandler().getStage(), "EDITOR WARNING!", "An entity with ID -1 can not be deleted!");
                }
                fadeOutWindow(getStageHandler().getEntityEditor());
                Dialogs.showOptionDialog(getStageHandler().getStage(), "EDITOR WARNING!", "Are you sure you want to delete this entity? This can not be undone!", Dialogs.OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
                    @Override
                    public void yes() {
                        Dialogs.showOKDialog(getStageHandler().getStage(), "EDITOR WARNING!", "Entity deleted forever!");
                        new AdminEditorEntityPacketOut(clientMain, generateDataOut(false, true)).sendPacket();
                        resetValues();
                        fadeOutWindow(getStageHandler().getEntityEditor());
                        stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
                    }

                    @Override
                    public void no() {
                        fadeInWindow(getStageHandler().getEntityEditor());
                        stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
                    }

                    @Override
                    public void cancel() {
                        fadeInWindow(getStageHandler().getEntityEditor());
                        stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(NpcTab.class, (short) 0);
                    }
                });
            }
        });

        content.add(leftPane).fill().pad(3).grow().left().top();
        content.add(appearanceTable).fill().pad(3).grow().left().top().row();

        getEntityEditor().pack();
    }

    void colorPicker(VisTable mainTable, String labelName, final VisSelectBox<LibGDXColorList> visSelectBox, final ColorPickerColorHandler colorPickerColorHandler) {
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
                Color color = LibGDXColorList.getType((byte) visSelectBox.getSelectedIndex()).getColor();
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

        TextureAtlas textureAtlas = stageHandler.getClientMain().getFileManager().getAtlas(GameAtlas.ENTITY_CHARACTER);
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
        Location location = new Location(stageHandler.getClientMain(),
                worldName.getText(),
                Integer.parseInt(mapX.getText()),
                Integer.parseInt(mapY.getText()),
                mapZ.getSelected().getWorldZ());

        EntityEditorData entityEditorData = new NPCData(true, save, delete, location, entityIDNum);

        // Basic data
        ((NPCData) entityEditorData).setName(name.getText());
        ((NPCData) entityEditorData).setFirstInteraction(firstInteraction.getSelected());
        ((NPCData) entityEditorData).setFaction(faction.getText());
        ((NPCData) entityEditorData).setHealth(Integer.parseInt(health.getText()));
        ((NPCData) entityEditorData).setDamage(Integer.parseInt(damage.getText()));
        ((NPCData) entityEditorData).setExpDrop(Integer.parseInt(expDrop.getText()));
        ((NPCData) entityEditorData).setDropTable(Integer.parseInt(dropTable.getText()));
        ((NPCData) entityEditorData).setWalkSpeed(Float.parseFloat(walkSpeed.getText()));
        ((NPCData) entityEditorData).setProbStop(probStill.getValue());
        ((NPCData) entityEditorData).setProbWalk(probWalk.getValue());
        ((NPCData) entityEditorData).setShopId(Short.parseShort(shopId.getText()));
        ((NPCData) entityEditorData).setBankKeeper(isBankKeeper.isChecked());
        ((NPCData) entityEditorData).setWalkRadius(Integer.parseInt(walkRadius.getText()));

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
