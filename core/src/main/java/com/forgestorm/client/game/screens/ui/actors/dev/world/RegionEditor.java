package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.audio.MusicManager;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.maps.Region;
import com.forgestorm.client.game.world.maps.RegionManager;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.*;

import java.util.Map;

public class RegionEditor extends HideableVisWindow implements Buildable {

    private final ClientMain clientMain;
    private final RegionManager regionManager;
    private final VisTable regionInfoTable = new VisTable(true);
    private final VisTable flagSelectTable = new VisTable(true);
    private final VisTable resultRows = new VisTable(true);

    private VisCheckBox allowPVP;
    private VisCheckBox allowChat;
    private VisCheckBox fullHeal;

    private VisTextField greetingsChat;
    private VisTextField greetingsTitle;
    private VisTextField farewellChat;
    private VisTextField farewellTitle;

    private VisTextField backgroundMusicID;
    private VisTextField ambianceSoundID;

    public RegionEditor(ClientMain clientMain) {
        super(clientMain, "Region Editor");
        this.clientMain = clientMain;
        regionManager = clientMain.getRegionManager();
    }

    @Override
    public Actor build(StageHandler stageHandler) {

        // Build create/save/delete buttons
        VisTable crudTable = new VisTable();
        VisTextButton createButton = new VisTextButton("Create Region");
        createButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionManager.createRegion();
            }
        });

        VisTextButton removeButton = new VisTextButton("Delete Region");
        removeButton.setColor(Color.RED);
        removeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionManager.deleteRegion();
            }
        });

        VisTextButton saveButton = new VisTextButton("Save All Regions");
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionManager.saveRegionsToFile();
            }
        });

        crudTable.add(createButton).row();
        crudTable.add(removeButton).row();
        crudTable.add(saveButton).row();

        // regionInfoTable and crudTable
        VisTable comboTable = new VisTable();
        comboTable.add(regionInfoTable).growX();
        comboTable.add(crudTable);
//        comboTable.addSeparator(false).colspan(2).growX();

        // Add actors
        add(comboTable).growX().row();
        add(new VisLabel("Flags")).row();
        add(flagSelectTable).growX().row();
        add(resultRows).growX().row();

        pack();
        centerWindow();
        setResizable(true);
        addCloseButton(new CloseButtonCallBack() {
            @Override
            public void closeButtonClicked() {
                toggleOpenClose(false);
            }
        });
        return this;
    }

    public void updateRegionInfo() {
        // Clear previous data shown
        regionInfoTable.clear();

        Region region = regionManager.getRegionToEdit();

        VisTable info = new VisTable(true);
        info.add(new VisLabel("[YELLOW]Region ID: []" + region.getRegionID())).row();
        info.add(new VisLabel("[YELLOW]Region Type: []" + region.getRegionType())).row();
        info.add(new VisLabel("[YELLOW]X1/Y1 : []" + region.getX1() + "[GREEN]/[]" + region.getY1())).row();
        info.add(new VisLabel("[YELLOW]X2/Y2 : []" + region.getX2() + "[GREEN]/[]" + region.getY2())).row();
        info.add(new VisLabel("[YELLOW]Height/Width : []" + region.getHeight() + "[GREEN]/[]" + region.getWidth())).row();

        regionInfoTable.add(new VisLabel("[YELLOW]Region Information:")).row();
        regionInfoTable.add(info).row();
        regionInfoTable.addSeparator(true).growX();
    }

    public void updateFlagsTable(Region regionToEdit) {
        rebuildFlagsSelectTable(regionToEdit);

        // SET PRELOADED FLAGS FROM FILE
        // TABLE 1
        if (regionToEdit.getAllowPVP() != null)
            allowPVP.setChecked(regionToEdit.getAllowPVP());
        if (regionToEdit.getAllowChat() != null)
            allowChat.setChecked(regionToEdit.getAllowChat());
        if (regionToEdit.getFullHeal() != null)
            fullHeal.setChecked(regionToEdit.getFullHeal());

        // TABLE 2
        if (regionToEdit.getGreetingsChat() != null)
            greetingsChat.setText(regionToEdit.getGreetingsChat());
        if (regionToEdit.getGreetingsTitle() != null)
            greetingsTitle.setText(regionToEdit.getGreetingsTitle());
        if (regionToEdit.getFarewellChat() != null)
            farewellChat.setText(regionToEdit.getFarewellChat());
        if (regionToEdit.getFarewellTitle() != null)
            farewellTitle.setText(regionToEdit.getFarewellTitle());

        // TABLE 3
        if (regionToEdit.getBackgroundMusicID() != null)
            backgroundMusicID.setText(Integer.toString(regionToEdit.getBackgroundMusicID()));
        if (regionToEdit.getAmbianceSoundID() != null)
            ambianceSoundID.setText(Integer.toString(regionToEdit.getAmbianceSoundID()));

        pack();
    }

    private void rebuildFlagsSelectTable(Region regionToEdit) {
        // Clear previous table and rebuild the components.
        // This prevents bugs where the UI thinks the Region is null.
        // Regions are world specific and don't get loaded until way after
        // scene2D is initialized.
        flagSelectTable.clear();

        // Build Flag Components
        VisTable table1 = new VisTable(true);
        ActorUtil.checkBox(clientMain, table1, "Allow PVP:", allowPVP = new VisCheckBox(""));
        ActorUtil.checkBox(clientMain, table1, "Allow Chat:", allowChat = new VisCheckBox(""));
        ActorUtil.checkBox(clientMain, table1, "Full Heal:", fullHeal = new VisCheckBox(""));

        VisTable table2 = new VisTable(true);
        ActorUtil.textField(clientMain, table2, "Greetings Chat:", greetingsChat = new VisTextField());
        ActorUtil.textField(clientMain, table2, "Greetings Title:", greetingsTitle = new VisTextField());
        ActorUtil.textField(clientMain, table2, "Farewell Chat:", farewellChat = new VisTextField());
        ActorUtil.textField(clientMain, table2, "Farewell Title:", farewellTitle = new VisTextField());

        VisTable table3 = new VisTable(true);
        ActorUtil.musicField(clientMain, table3, "Background Music ID:", backgroundMusicID = new VisTextField(), getClass());
        ActorUtil.soundField(clientMain, table3, "Ambiance Sound ID:", ambianceSoundID = new VisTextField(), getClass());

        //Build Table
        flagSelectTable.add(table1).align(Alignment.TOP.getAlignment());
        flagSelectTable.addSeparator(true).growY();
        flagSelectTable.add(table2).align(Alignment.TOP.getAlignment());
        flagSelectTable.addSeparator(true).growY();
        flagSelectTable.add(table3).align(Alignment.TOP.getAlignment());

        // ### SETUP LISTENERS ############################################################

        // TABLE 1
        allowPVP.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionToEdit.setAllowPVP(allowPVP.isChecked());
            }
        });

        allowChat.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionToEdit.setAllowChat(allowChat.isChecked());
            }
        });

        fullHeal.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionToEdit.setFullHeal(fullHeal.isChecked());
            }
        });

        // TABLE 2
        greetingsChat.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionToEdit.setGreetingsChat(greetingsChat.getText());
            }
        });

        greetingsTitle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionToEdit.setGreetingsTitle(greetingsTitle.getText());
            }
        });

        farewellChat.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionToEdit.setFarewellChat(farewellChat.getText());
            }
        });

        farewellTitle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                regionToEdit.setFarewellTitle(farewellTitle.getText());
            }
        });

        // TABLE 3
        backgroundMusicID.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    if (regionManager.isPlayerCurrentRegion(regionToEdit)) {
                        MusicManager musicManager = clientMain.getAudioManager().getMusicManager();
                        musicManager.stopMusic(false);
                    }
                    regionToEdit.setBackgroundMusicID(Integer.parseInt(backgroundMusicID.getText()));
                } catch (NumberFormatException e) {
                    regionToEdit.setBackgroundMusicID(null);
                }
            }
        });

        ambianceSoundID.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    regionToEdit.setAmbianceSoundID(Integer.parseInt(ambianceSoundID.getText()));
                } catch (NumberFormatException e) {
                    regionToEdit.setAmbianceSoundID(null);
                }
            }
        });

    }

    public void toggleOpenClose(boolean openWindow) {
        regionManager.setEditRegion(openWindow);
        regionManager.setDrawRegion(openWindow);
        pack();
    }

    public void updateRegionSelectionList(Map<Integer, Region> regionMap) {
        resultRows.clear();

        for (Map.Entry<Integer, Region> entry : regionMap.entrySet()) {
            Integer id = entry.getKey();
            Region region = entry.getValue();

            // Build data table
            VisTable idTable = new VisTable();

            VisTextButton regionSelectionButton = new VisTextButton(Integer.toString(id));
            idTable.add(regionSelectionButton).growX();

            regionSelectionButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    regionManager.changeRegion(id);
                }
            });

            StringBuilder stringBuilder = new StringBuilder();
            if (region.getAllowPVP() != null && region.getAllowPVP())
                stringBuilder.append("[RED]P");
            if (region.getAllowChat() != null && region.getAllowChat())
                stringBuilder.append(", [GREEN]C");
            if (region.getFullHeal() != null && region.getFullHeal())
                stringBuilder.append(", [BLUE]H");
            if (region.getGreetingsChat() != null && !region.getGreetingsChat().isEmpty())
                stringBuilder.append(", [YELLOW]GC");
            if (region.getGreetingsTitle() != null && !region.getGreetingsTitle().isEmpty())
                stringBuilder.append(", [YELLOW]GT");
            if (region.getFarewellChat() != null && !region.getFarewellChat().isEmpty())
                stringBuilder.append(", [ORANGE]FC");
            if (region.getFarewellTitle() != null && !region.getFarewellTitle().isEmpty())
                stringBuilder.append(", [ORANGE]FT");

            idTable.add(new VisLabel(stringBuilder.toString()));

            VisTable rowTable = new VisTable();
            rowTable.addSeparator(false).growX().row();
            rowTable.add(idTable).growX();
            resultRows.add(rowTable).growX().row();
        }
    }
}
