package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;
import com.valenguard.client.Valenguard;
import com.valenguard.client.game.assets.GameAtlas;
import com.valenguard.client.game.screens.ui.ImageBuilder;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

public class CharacterWindow extends HideableVisWindow implements Buildable, Focusable {

    public CharacterWindow() {
        super("Character");
    }

    @Override
    public Actor build() {
        TableUtils.setSpacingDefaults(this);
        DragAndDrop dragManager = Valenguard.getInstance().getStageHandler().getDragAndDrop();
        dragManager.setDragTime(0);
        addCloseButton();
        setResizable(false);

        // top table (head)
        ImageBuilder imageBuilder = new ImageBuilder(GameAtlas.ITEM_TEXTURES, 32);
        VisImage headSlot = imageBuilder.setRegionName("helmet_11").buildVisImage();
        add(headSlot);
        this.row();

        // main table (body etc)
        VisTable mainTable = new VisTable(true);

        VisImage arrow = imageBuilder.setRegionName("weapon_arrow_02").buildVisImage();
        VisImage necklace = imageBuilder.setRegionName("accessory_04").buildVisImage();
        VisImage cape = imageBuilder.setRegionName("armor_028").buildVisImage();

        mainTable.add(arrow);
        mainTable.add(necklace);
        mainTable.add(cape);
        mainTable.row();

        VisImage ring1 = imageBuilder.setRegionName("ring_008").buildVisImage();
        VisImage chest = imageBuilder.setRegionName("armor_014").buildVisImage();
        VisImage hand = imageBuilder.setRegionName("glove_11").buildVisImage();

        mainTable.add(ring1);
        mainTable.add(chest);
        mainTable.add(hand);
        mainTable.row();

        VisImage ring2 = imageBuilder.setRegionName("ring_009").buildVisImage();
        VisImage belt = imageBuilder.setRegionName("accessory_01").buildVisImage();
        VisImage boot = imageBuilder.setRegionName("boot_02").buildVisImage();

        mainTable.add(ring2);
        mainTable.add(belt);
        mainTable.add(boot);
        add(mainTable);
        this.row();

        // main hand/off hand
        VisTable weaponTable = new VisTable(true);
        VisImage mainHand = imageBuilder.setRegionName("weapon_claw_15").buildVisImage();
        VisImage offHand = imageBuilder.setRegionName("shield_13").buildVisImage();

        weaponTable.add(mainHand);
        weaponTable.add(offHand);
        add(weaponTable);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        pack();
        centerWindow();
        setVisible(false);
        return this;
    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }
}
