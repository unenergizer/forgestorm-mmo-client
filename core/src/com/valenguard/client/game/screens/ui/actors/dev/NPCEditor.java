package com.valenguard.client.game.screens.ui.actors.dev;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisRadioButton;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.screens.GameScreen;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.ProperName;
import com.valenguard.client.game.world.entities.AiEntity;
import com.valenguard.client.game.world.entities.EntityManager;
import com.valenguard.client.game.world.entities.EntityType;
import com.valenguard.client.game.world.maps.Location;
import com.valenguard.client.io.type.GameAtlas;
import com.valenguard.client.network.game.packet.out.AdminEditorEntityPacketOut;
import com.valenguard.client.util.color.LibGDXColorList;

import java.text.DecimalFormat;

import lombok.Getter;

import static com.valenguard.client.util.Log.println;

public class NPCEditor extends HideableVisWindow implements Buildable {

    static final int PREVIEW_SCALE = 10;

    private final DecimalFormat decimalFormat = new DecimalFormat();

    private EntityType entityType;
    private VisRadioButton monsterButton = new VisRadioButton("monster button");
    private VisRadioButton npcButton = new VisRadioButton("npc button");
    private ButtonGroup buttonGroup = new ButtonGroup(npcButton, monsterButton);
    private short entityIDNum = -1;
    private VisLabel entityID = new VisLabel(Short.toString(entityIDNum));
    private VisTextField name = new VisValidatableTextField(new ProperName());
    private VisTextField faction = new VisValidatableTextField(new ProperName());
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

    public NPCEditor() {
        super("Entity Creator");
        decimalFormat.setMaximumFractionDigits(2);

        // SETUP DEFAULT CASE
        appearancePanel = new NPCAppearancePanel(this);
        appearancePanel.buildAppearancePanel();
        npcButton.setChecked(true);
        entityType = EntityType.NPC;
    }

    void resetValues(boolean resetAppearanceTable) {
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
        selectSpawnActivated = false;
        mapName.setText("");
        mapX.setText("");
        mapY.setText("");

        // Appearance Data
        if (appearancePanel != null) appearancePanel.reset();

        // Remove right pane appearance fields
        if (resetAppearanceTable) appearanceTable.clear();
    }

    public void loadAiEntity(AiEntity aiEntity) {
        resetValues(true);
        entityType = aiEntity.getEntityType();
        entityIDNum = aiEntity.getServerEntityID();
        entityID.setText(aiEntity.getServerEntityID());

        name.setText(aiEntity.getEntityName());
        // todo faction = faction.setText(npc.getFaction());
        health.setText(Integer.toString(aiEntity.getMaxHealth()));
        damage.setText(Integer.toString(aiEntity.getDamage()));
        expDrop.setText(Integer.toString(aiEntity.getExpDrop()));
        dropTable.setText(Integer.toString(aiEntity.getDropTable()));
        walkSpeed.setValue(aiEntity.getMoveSpeed());
        probStill.setValue(aiEntity.getProbWalkStill());
        probWalk.setValue(aiEntity.getProbWalkStart());
        shopId.setText(Integer.toString(aiEntity.getShopID()));
        mapName.setText(aiEntity.getDefualtSpawnLocation().getMapName());
        mapX.setText(Short.toString(aiEntity.getDefualtSpawnLocation().getX()));
        mapY.setText(Short.toString(aiEntity.getDefualtSpawnLocation().getY()));

        // Load Appearance
        if (aiEntity.getEntityType() == EntityType.MONSTER) {
            appearancePanel = new MonsterAppearancePanel(this);
            appearancePanel.buildAppearancePanel();
        } else if (aiEntity.getEntityType() == EntityType.NPC) {
            appearancePanel = new NPCAppearancePanel(this);
            appearancePanel.buildAppearancePanel();
        }
        appearancePanel.load(aiEntity);
        appearancePanel.characterPreview();
    }

    @Override
    public Actor build() {
        VisTable leftPane = new VisTable();

        VisTable entityTypeTable = new VisTable();

        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setUncheckLast(true);

        entityTypeTable.add(monsterButton).pad(3);
        entityTypeTable.add(npcButton).pad(3);

        leftPane.add(entityTypeTable).grow().row();

        monsterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetValues(true);
                appearancePanel = new MonsterAppearancePanel(ActorUtil.getStageHandler().getNPCEditor());
                entityType = EntityType.MONSTER;
            }
        });

        npcButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetValues(true);
                appearancePanel = new NPCAppearancePanel(ActorUtil.getStageHandler().getNPCEditor());
                entityType = EntityType.NPC;
            }
        });

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
                println(NPCEditor.class, "--- Settings ---");
                println(NPCEditor.class, "EntityID: " + entityID.getText());
                println(NPCEditor.class, "Name: " + name.getText());
                println(NPCEditor.class, "Faction: " + faction.getText());
                println(NPCEditor.class, "Health: " + health.getText());
                println(NPCEditor.class, "Damage: " + damage.getText());
                println(NPCEditor.class, "ExpDrop: " + expDrop.getText());
                println(NPCEditor.class, "DropTable: " + dropTable.getText());
                println(NPCEditor.class, "WalkSpeed: " + walkSpeed.getValue());
                println(NPCEditor.class, "Probability Still: " + probStill.getValue());
                println(NPCEditor.class, "Probability Walk: " + probWalk.getValue());
                println(NPCEditor.class, "ShopID: " + shopId.getText());
                println(NPCEditor.class, "SpawnLocation: " + mapName.getText() + ", X: " + mapX.getText() + ", Y: " + mapY.getText());
                println(NPCEditor.class, "--- Appearance ---");

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
                resetValues(true);
                ActorUtil.fadeOutWindow(ActorUtil.getStageHandler().getNPCEditor());
            }
        });

        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resetValues(false);
            }
        });

        add(leftPane).fill().pad(3).grow().left().top();
        add(appearanceTable).fill().pad(3).grow().left().top().row();

        setResizable(false);
        addCloseButton();
        centerWindow();
        setVisible(false);
        pack();
        return this;
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
                ActorUtil.getStageHandler().getColorPickerController().show(colorPickerColorHandler);
            }
        });
    }

    private void textField(VisTable mainTable, String labelName, VisTextField textField) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        table.add(visLabel).grow().pad(1);
        table.add(textField).pad(1);
        mainTable.add(table).expandX().fillX().pad(1).row();
    }

    private void valueSlider(VisTable mainTable, String labelName, final VisSlider slider) {
        VisTable table = new VisTable();
        VisLabel visLabel = new VisLabel(labelName);
        final VisLabel sliderValue = new VisLabel(decimalFormat.format(slider.getValue()));
        table.add(visLabel).grow().pad(1);
        table.add(slider).pad(1);
        table.add(sliderValue).pad(1);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sliderValue.setText(decimalFormat.format(slider.getValue()));
            }
        });

        mainTable.add(table).expandX().fillX().pad(1).row();
    }

    @SuppressWarnings("SameParameterValue")
    VisTable imageTable(int width, int height, int padBottom, String region, Color color) {
        VisTable innerTable = new VisTable();

        TextureAtlas textureAtlas = Valenguard.getInstance().getFileManager().getAtlas(GameAtlas.ENTITY_CHARACTER);
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

    private EntityEditorData generateDataOut(boolean save) {
        EntityEditorData entityEditorData = new EntityEditorData();

        entityEditorData.setEntityType(entityType);
        entityEditorData.setSpawn(true);
        entityEditorData.setSave(save);

        // Basic data
        entityEditorData.setEntityID(entityIDNum);
        entityEditorData.setName(name.getText());
        entityEditorData.setFaction(faction.getText());
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
}
