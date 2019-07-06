package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.rpg.EntityAlignment;
import com.valenguard.client.game.screens.GameScreen;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.ProperName;
import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.network.game.packet.out.AdminEditorEntityPacketOut;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class MonsterTab extends Tab {

    private final EntityEditor entityEditor;
    private final String title;
    private VisTable content;

    private short entityIDNum = -1;
    private VisLabel entityID = new VisLabel(Short.toString(entityIDNum));
    private VisTextField name = new VisValidatableTextField(new ProperName());
    private VisSelectBox<EntityAlignment> entityAlignment = new VisSelectBox<EntityAlignment>();
    private VisTextField health = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField damage = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField expDrop = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisTextField dropTable = new VisValidatableTextField(new Validators.IntegerValidator());
    private VisSlider walkSpeed = new VisSlider(.1f, 5, .1f, false);
    private VisSlider probStill = new VisSlider(0, 1, .1f, false);
    private VisSlider probWalk = new VisSlider(0, 1, .1f, false);
    private VisTextField shopId = new VisTextField("-1");

    @Getter
    private boolean selectSpawnActivated = false;
    private VisTextButton selectSpawn = new VisTextButton("Select Spawn Location");
    private VisTextField mapName = new VisTextField();
    private VisTextField mapX = new VisTextField();
    private VisTextField mapY = new VisTextField();

    @Getter
    private AppearancePanel appearancePanel;
    @Getter
    private VisTable appearanceTable = new VisTable();
    @Getter
    private VisTable previewTable = new VisTable();

    MonsterTab(EntityEditor entityEditor) {
        super(false, false);
        this.entityEditor = entityEditor;
        title = " Monster ";

        // SETUP DEFAULT CASE
        appearancePanel = new MonsterAppearancePanel(this);
        appearancePanel.buildAppearancePanel();

        build();
    }

    void resetValues() {
        entityIDNum = -1;
        entityID.setText(Short.toString(entityIDNum));
        name.setText("");
        entityAlignment.setSelected(EntityAlignment.FRIENDLY);
        health.setText("");
        damage.setText("");
        expDrop.setText("");
        dropTable.setText("");
        walkSpeed.setValue(0);
        probStill.setValue(0);
        probWalk.setValue(0);
        shopId.setText("-1");
        selectSpawnActivated = false;
        mapName.setText("");
        mapX.setText("");
        mapY.setText("");

        // Appearance Data
        if (appearancePanel != null) appearancePanel.reset();
    }

    public void loadAiEntity(AiEntity aiEntity) {
        resetValues();
        entityIDNum = aiEntity.getServerEntityID();
        entityID.setText(aiEntity.getServerEntityID());

        name.setText(aiEntity.getEntityName());
        entityAlignment.setSelected(aiEntity.getAlignment());
        health.setText(Integer.toString(aiEntity.getMaxHealth()));
        damage.setText(Integer.toString(aiEntity.getDamage()));
        expDrop.setText(Integer.toString(aiEntity.getExpDrop()));
        dropTable.setText(Integer.toString(aiEntity.getDropTable()));
        walkSpeed.setValue(aiEntity.getMoveSpeed());
        probStill.setValue(aiEntity.getProbWalkStill());
        probWalk.setValue(aiEntity.getProbWalkStart());
        shopId.setText(Integer.toString(aiEntity.getShopID()));
        mapName.setText(aiEntity.getDefaultSpawnLocation().getMapName());
        mapX.setText(Short.toString(aiEntity.getDefaultSpawnLocation().getX()));
        mapY.setText(Short.toString(aiEntity.getDefaultSpawnLocation().getY()));

        // Load Appearance
        appearanceTable.clear();
        appearancePanel = new MonsterAppearancePanel(this);
        appearancePanel.buildAppearancePanel();
        appearancePanel.load(aiEntity);
        appearancePanel.characterPreview();
    }

    public void build() {
        content = new VisTable(true);
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

        entityEditor.textField(leftPane, "Name:", name);
        entityEditor.selectBox(leftPane, "Alignment:", entityAlignment, EntityAlignment.values());
        entityEditor.textField(leftPane, "Health:", health);
        entityEditor.textField(leftPane, "Damage:", damage);
        entityEditor.textField(leftPane, "ExpDrop:", expDrop);
        entityEditor.textField(leftPane, "DropTable:", dropTable);
        entityEditor.valueSlider(leftPane, "Walk Speed:", walkSpeed);
        entityEditor.valueSlider(leftPane, "Probability Still:", probStill);
        entityEditor.valueSlider(leftPane, "Probability Walk:", probWalk);
        entityEditor.textField(leftPane, "Shop ID:", shopId);

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
                println(MonsterTab.class, "Name: " + name.getText());
                println(MonsterTab.class, "Faction: " + entityAlignment.getSelected());
                println(MonsterTab.class, "Health: " + health.getText());
                println(MonsterTab.class, "Damage: " + damage.getText());
                println(MonsterTab.class, "ExpDrop: " + expDrop.getText());
                println(MonsterTab.class, "DropTable: " + dropTable.getText());
                println(MonsterTab.class, "WalkSpeed: " + walkSpeed.getValue());
                println(MonsterTab.class, "Probability Still: " + probStill.getValue());
                println(MonsterTab.class, "Probability Walk: " + probWalk.getValue());
                println(MonsterTab.class, "ShopID: " + shopId.getText());
                println(MonsterTab.class, "SpawnLocation: " + mapName.getText() + ", X: " + mapX.getText() + ", Y: " + mapY.getText());
                println(MonsterTab.class, "--- Appearance ---");

                appearancePanel.printDebug();
            }
        });

        // Submit and finalize section
        VisTable submitTable = new VisTable();
        VisTextButton spawnButton = new VisTextButton("Spawn");
        VisTextButton saveButton = new VisTextButton("Save");
        VisTextButton resetButton = new VisTextButton("Reset");
        submitTable.add(spawnButton).pad(3);
        submitTable.add(saveButton).pad(3);
        submitTable.add(resetButton).pad(3);
        leftPane.add(submitTable).row();

        spawnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new AdminEditorEntityPacketOut(generateDataOut(false)).sendPacket();
            }
        });

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new AdminEditorEntityPacketOut(generateDataOut(true)).sendPacket();
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

        content.add(leftPane).fill().pad(3).grow().left().top();
        content.add(appearanceTable).fill().pad(3).grow().left().top().row();

        entityEditor.pack();
    }

    private EntityEditorData generateDataOut(boolean save) {
        EntityEditorData entityEditorData = new EntityEditorData();

        entityEditorData.setEntityType(EntityType.MONSTER);
        entityEditorData.setSpawn(true);
        entityEditorData.setSave(save);

        // Basic data
        entityEditorData.setEntityID(entityIDNum);
        entityEditorData.setName(name.getText());
        entityEditorData.setEntityAlignment(entityAlignment.getSelected());
        entityEditorData.setHealth(Integer.valueOf(health.getText()));
        entityEditorData.setDamage(Integer.valueOf(damage.getText()));
        entityEditorData.setExpDrop(Integer.valueOf(expDrop.getText()));
        entityEditorData.setDropTable(Integer.valueOf(dropTable.getText()));
        entityEditorData.setWalkSpeed(walkSpeed.getValue());
        entityEditorData.setProbStop(probStill.getValue());
        entityEditorData.setProbWalk(probWalk.getValue());
        entityEditorData.setShopId(Short.valueOf(shopId.getText()));
        entityEditorData.setBankKeeper(false); // TODO

        // World data
        entityEditorData.setSpawnLocation(new Location(
                mapName.getText(),
                Short.valueOf(mapX.getText()),
                Short.valueOf(mapY.getText()))
        );

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
