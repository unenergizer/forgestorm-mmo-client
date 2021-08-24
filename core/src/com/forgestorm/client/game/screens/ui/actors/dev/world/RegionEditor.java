package com.forgestorm.client.game.screens.ui.actors.dev.world;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.world.maps.Region;
import com.forgestorm.client.game.world.maps.RegionManager;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class RegionEditor extends HideableVisWindow implements Buildable {

    private final RegionManager regionManager = ClientMain.getInstance().getRegionManager();
    private final VisTable regionInfoTable = new VisTable(true);

    public RegionEditor() {
        super("Region Editor");
    }

    @Override
    public Actor build(StageHandler stageHandler) {
        add(regionInfoTable);
        centerWindow();
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
        regionInfoTable.add(info);
    }

    public void toggleOpenClose(boolean openWindow) {
        regionManager.setEditRegion(openWindow);
        regionManager.setDrawRegion(openWindow);
    }
}
