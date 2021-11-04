package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.maps.Region;
import com.forgestorm.client.game.world.maps.RegionManager;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.Map;

public class RegionEditor extends HideableVisWindow implements Buildable {

    private final RegionManager regionManager = ClientMain.getInstance().getRegionManager();
    private final VisTable regionInfoTable = new VisTable(true);
    private final VisTable flagSelectTable = new VisTable(true);
    private final VisTable resultRows = new VisTable(true);

    private final VisCheckBox allowPVP = new VisCheckBox("");
    private final VisCheckBox allowChat = new VisCheckBox("");
    private final VisCheckBox fullHeal = new VisCheckBox("");

    private final VisTextField greetingsChat = new VisTextField();
    private final VisTextField greetingsTitle = new VisTextField();
    private final VisTextField farewellChat = new VisTextField();
    private final VisTextField farewellTitle = new VisTextField();

    public RegionEditor() {
        super("Region Editor");
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        // Build actors
        buildFlagsSelectTable();

        // Add actors
        add(regionInfoTable).growX().row();
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
        info.add(new VisLabel("[YELLOW]Region Type: []" + region.getRegionType())).row();
        info.add(new VisLabel("[YELLOW]X1/Y1 : []" + region.getWorld1X() + "[GREEN]/[]" + region.getWorld1Y())).row();
        info.add(new VisLabel("[YELLOW]X2/Y2 : []" + region.getWorld2X() + "[GREEN]/[]" + region.getWorld2Y())).row();
        info.add(new VisLabel("[YELLOW]Height/Width : []" + region.getHeight() + "[GREEN]/[]" + region.getWidth())).row();

        regionInfoTable.add(new VisLabel("[YELLOW]Region Information:")).row();
        regionInfoTable.add(info).row();
        regionInfoTable.addSeparator(false).growX();
    }

    public void buildFlagsSelectTable() {
        // Build Flag Components
        VisTable table1 = new VisTable(true);
        ActorUtil.checkBox(table1, "Allow PVP:", allowPVP);
        ActorUtil.checkBox(table1, "Allow Chat:", allowChat);
        ActorUtil.checkBox(table1, "Full Heal:", fullHeal);

        VisTable table2 = new VisTable(true);
        ActorUtil.textField(table2, "Greetings Chat:", greetingsChat);
        ActorUtil.textField(table2, "Greetings Title:", greetingsTitle);
        ActorUtil.textField(table2, "Farewell Chat:", farewellChat);
        ActorUtil.textField(table2, "Farewell Title:", farewellTitle);

        VisTable table3 = new VisTable(true);
        ActorUtil.textField(table3, "TEMP:", new VisTextField());

        //Build Table
        flagSelectTable.add(table1).align(Alignment.TOP.getAlignment());
        flagSelectTable.addSeparator(true).growY();
        flagSelectTable.add(table2).align(Alignment.TOP.getAlignment());
        flagSelectTable.addSeparator(true).growY();
        flagSelectTable.add(table3).align(Alignment.TOP.getAlignment());
    }

    public void toggleOpenClose(boolean openWindow) {
        regionManager.setEditRegion(openWindow);
        regionManager.setDrawRegion(openWindow);
        pack();
    }

    public void showLoadedRegions(Map<Integer, Region> regionMap) {
        resultRows.clear();

        for (Map.Entry<Integer, Region> entry : regionMap.entrySet()) {
            Integer id = entry.getKey();
            Region region = entry.getValue();

            // Build data table
            VisTable idTable = new VisTable();
            idTable.add(new VisLabel(Integer.toString(id))).growX();

            StringBuilder stringBuilder = new StringBuilder();
            if (region.getAllowPVP() != null && region.getAllowPVP())
                stringBuilder.append("[RED]P ");
            if (region.getAllowChat() != null && region.getAllowChat())
                stringBuilder.append("[GREEN]C ");
            if (region.getFullHeal() != null && region.getFullHeal())
                stringBuilder.append("[BLUE]H ");
            if (region.getGreetingsChat() != null && !region.getGreetingsChat().isEmpty())
                stringBuilder.append("[YELLOW]GC ");
            if (region.getGreetingsTitle() != null && !region.getGreetingsTitle().isEmpty())
                stringBuilder.append("[YELLOW]GT ");
            if (region.getFarewellChat() != null && !region.getFarewellChat().isEmpty())
                stringBuilder.append("[ORANGE]FC ");
            if (region.getFarewellTitle() != null && !region.getFarewellTitle().isEmpty())
                stringBuilder.append("[ORANGE]FT ");

            idTable.add(new VisLabel(stringBuilder.toString()));

            VisTable rowTable = new VisTable();
            rowTable.addSeparator(false).growX().row();
            rowTable.add(idTable).growX();
            resultRows.add(rowTable).growX().row();
        }
    }
}
