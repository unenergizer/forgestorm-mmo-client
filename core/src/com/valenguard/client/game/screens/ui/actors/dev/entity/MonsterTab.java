package com.valenguard.client.game.screens.ui.actors.dev.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.util.form.FormValidator;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.input.MouseManager;
import com.valenguard.client.game.rpg.EntityAlignment;
import com.valenguard.client.game.screens.GameScreen;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
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
    private VisValidatableTextField name = new VisValidatableTextField();
    private VisSelectBox<EntityAlignment> entityAlignment = new VisSelectBox<EntityAlignment>();
    private VisValidatableTextField health = new VisValidatableTextField();
    private VisValidatableTextField damage = new VisValidatableTextField();
    private VisValidatableTextField expDrop = new VisValidatableTextField();
    private VisValidatableTextField dropTable = new VisValidatableTextField();
    private VisSlider walkSpeed = new VisSlider(.1f, .99f, .01f, false);
    private VisSlider probStill = new VisSlider(0, .99f, .01f, false);
    private VisSlider probWalk = new VisSlider(0, .99f, .01f, false);
    private VisValidatableTextField shopId = new VisValidatableTextField("-1");

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

        deleteButton.setDisabled(true);

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

        validator.notEmpty(name, "Name must not be empty.");
        validator.valueGreaterThan(health, "Health must be greater than 0.", 1, true);
        validator.integerNumber(damage, "Damage must be a valid number.");
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
        content.add(appearanceTable).fill().pad(3).grow().left().top().row();

        entityEditor.pack();
    }

    private EntityEditorData generateDataOut(boolean save, boolean delete) {
        EntityEditorData entityEditorData = new EntityEditorData();

        entityEditorData.setEntityType(EntityType.MONSTER);
        entityEditorData.setSpawn(true);
        entityEditorData.setSave(save);
        entityEditorData.setDelete(delete);

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
