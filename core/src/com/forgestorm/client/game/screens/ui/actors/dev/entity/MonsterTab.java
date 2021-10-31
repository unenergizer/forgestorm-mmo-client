package com.forgestorm.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.input.MouseManager;
import com.forgestorm.client.game.rpg.EntityAlignment;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.EntityEditorData;
import com.forgestorm.client.game.screens.ui.actors.dev.entity.data.MonsterData;
import com.forgestorm.client.game.world.entities.AiEntity;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.network.game.packet.out.AdminEditorEntityPacketOut;
import com.forgestorm.shared.game.world.entities.FirstInteraction;
import com.forgestorm.shared.game.world.maps.Floors;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import lombok.Getter;

import static com.forgestorm.client.util.Log.println;

public class MonsterTab extends EditorTab {

    private final String title;
    private VisTable content;

    private short entityIDNum = -1;
    private final VisLabel entityID = new VisLabel(Short.toString(entityIDNum));
    private final VisValidatableTextField name = new VisValidatableTextField();
    private final VisSelectBox<FirstInteraction> firstInteraction = new VisSelectBox<FirstInteraction>();
    private final VisSelectBox<EntityAlignment> entityAlignment = new VisSelectBox<EntityAlignment>();
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
    private final VisTextButton deleteButton = new VisTextButton("Delete");

    @Getter
    private AppearancePanel appearancePanel;
    @Getter
    private final VisTable appearanceTable = new VisTable();
    @Getter
    private final VisTable previewTable = new VisTable();

    MonsterTab(StageHandler stageHandler, EntityEditor entityEditor) {
        super(stageHandler, entityEditor);
        title = " Monster ";

        // SETUP DEFAULT CASE
        appearancePanel = new MonsterAppearancePanel(this);
        appearancePanel.buildAppearancePanel();

        build();
    }

    @Override
    public void resetValues() {
        entityIDNum = -1;
        entityID.setText(Short.toString(entityIDNum));
        name.setText("");
        firstInteraction.setSelected(FirstInteraction.ATTACK);
        entityAlignment.setSelected(EntityAlignment.FRIENDLY);
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

        deleteButton.setDisabled(true);

        // Appearance Data
        if (appearancePanel != null) appearancePanel.reset();
    }

    public void loadAiEntity(AiEntity aiEntity) {
        resetValues();
        entityIDNum = aiEntity.getServerEntityID();
        entityID.setText(aiEntity.getServerEntityID());

        name.setText(aiEntity.getEntityName());
        firstInteraction.setSelected(aiEntity.getFirstInteraction());
        entityAlignment.setSelected(aiEntity.getAlignment());
        health.setText(Integer.toString(aiEntity.getMaxHealth()));
        damage.setText(Integer.toString(aiEntity.getDamage()));
        expDrop.setText(Integer.toString(aiEntity.getExpDrop()));
        dropTable.setText(Integer.toString(aiEntity.getDropTable()));
        walkSpeed.setText(Float.toString(aiEntity.getMoveSpeed()));
        probStill.setValue(aiEntity.getProbWalkStill());
        probWalk.setValue(aiEntity.getProbWalkStart());
        shopId.setText(Integer.toString(aiEntity.getShopID()));
        isBankKeeper.setChecked(aiEntity.isBankKeeper());
        worldName.setText(aiEntity.getDefaultSpawnLocation().getWorldName());
        mapX.setText(Integer.toString(aiEntity.getDefaultSpawnLocation().getX()));
        mapY.setText(Integer.toString(aiEntity.getDefaultSpawnLocation().getY()));
        mapZ.setSelected(Floors.getFloor(aiEntity.getDefaultSpawnLocation().getZ()));

        // Load Appearance
        appearanceTable.clear();
        appearancePanel = new MonsterAppearancePanel(this);
        appearancePanel.buildAppearancePanel();
        appearancePanel.load(aiEntity);
        appearancePanel.characterPreview();

        deleteButton.setDisabled(false);
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

        textField(leftPane, "Name:", name);
        selectBox(leftPane, "FirstInteraction:", firstInteraction, FirstInteraction.values());
        selectBox(leftPane, "Alignment:", entityAlignment, EntityAlignment.values());
        textField(leftPane, "Health:", health);
        textField(leftPane, "Damage:", damage);
        textField(leftPane, "ExpDrop:", expDrop);
        textField(leftPane, "DropTable:", dropTable);
        textField(leftPane, "Walk Speed:", walkSpeed);
        valueSlider(leftPane, "Probability Still:", probStill);
        valueSlider(leftPane, "Probability Walk:", probWalk);
        textField(leftPane, "Shop ID:", shopId);
        checkBox(leftPane, "Set as Bank Keeper?", isBankKeeper);

        validator.notEmpty(name, "Name must not be empty.");
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
                println(MonsterTab.class, "Name: " + name.getText());
                println(MonsterTab.class, "Faction: " + entityAlignment.getSelected());
                println(MonsterTab.class, "Health: " + health.getText());
                println(MonsterTab.class, "Damage: " + damage.getText());
                println(MonsterTab.class, "ExpDrop: " + expDrop.getText());
                println(MonsterTab.class, "DropTable: " + dropTable.getText());
                println(MonsterTab.class, "WalkSpeed: " + walkSpeed.getText());
                println(MonsterTab.class, "Probability Still: " + probStill.getValue());
                println(MonsterTab.class, "Probability Walk: " + probWalk.getValue());
                println(MonsterTab.class, "ShopID: " + shopId.getText());
                println(NpcTab.class, "IsBanker: " + isBankKeeper.isChecked());
                println(MonsterTab.class, "SpawnLocation: " + worldName.getText() + ", X: " + mapX.getText() + ", Y: " + mapY.getText() + ", Z: " + mapZ.getSelected().getWorldZ());
                println(MonsterTab.class, "--- Appearance ---");

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
                ActorUtil.fadeOutWindow(getStageHandler().getEntityEditor());
                Dialogs.showOptionDialog(getStageHandler().getStage(), "EDITOR WARNING!", "Are you sure you want to delete this entity? This can not be undone!", Dialogs.OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
                    @Override
                    public void yes() {
                        Dialogs.showOKDialog(getStageHandler().getStage(), "EDITOR WARNING!", "Entity deleted forever!");
                        new AdminEditorEntityPacketOut(generateDataOut(false, true)).sendPacket();
                        resetValues();
                        ActorUtil.fadeOutWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }

                    @Override
                    public void no() {
                        ActorUtil.fadeInWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }

                    @Override
                    public void cancel() {
                        ActorUtil.fadeInWindow(getStageHandler().getEntityEditor());
                        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(MonsterTab.class, (short) 0);
                    }
                });
            }
        });

        content.add(leftPane).fill().pad(3).grow().left().top();
        content.add(appearanceTable).fill().pad(3).grow().left().top().row();

        getEntityEditor().pack();
    }

    private EntityEditorData generateDataOut(boolean save, boolean delete) {
        Location location = new Location(
                worldName.getText(),
                Integer.parseInt(mapX.getText()),
                Integer.parseInt(mapY.getText()),
                mapZ.getSelected().getWorldZ());

        EntityEditorData entityEditorData = new MonsterData(true, save, delete, location, entityIDNum);

        // Basic data
        ((MonsterData) entityEditorData).setName(name.getText());
        ((MonsterData) entityEditorData).setFirstInteraction(firstInteraction.getSelected());
        ((MonsterData) entityEditorData).setEntityAlignment(entityAlignment.getSelected());
        ((MonsterData) entityEditorData).setHealth(Integer.parseInt(health.getText()));
        ((MonsterData) entityEditorData).setDamage(Integer.parseInt(damage.getText()));
        ((MonsterData) entityEditorData).setExpDrop(Integer.parseInt(expDrop.getText()));
        ((MonsterData) entityEditorData).setDropTable(Integer.parseInt(dropTable.getText()));
        ((MonsterData) entityEditorData).setWalkSpeed(Float.parseFloat(walkSpeed.getText()));
        ((MonsterData) entityEditorData).setProbStop(probStill.getValue());
        ((MonsterData) entityEditorData).setProbWalk(probWalk.getValue());
        ((MonsterData) entityEditorData).setShopId(Short.parseShort(shopId.getText()));
        ((MonsterData) entityEditorData).setBankKeeper(isBankKeeper.isChecked());

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
