package com.valenguard.client.game.screens.ui.actors.game.draggable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.valenguard.client.game.screens.ui.StageHandler;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;
import com.valenguard.client.game.screens.ui.actors.game.GameButtonBar;
import com.valenguard.client.game.world.item.inventory.InventoryConstants;
import com.valenguard.client.game.world.item.inventory.InventoryType;

public class BagWindow extends ItemSlotContainer implements Buildable, Focusable {

    private StageHandler stageHandler;

    public BagWindow() {
        super("Inventory", InventoryConstants.BAG_SIZE);
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        DragAndDrop dragAndDrop = stageHandler.getDragAndDrop();
        addCloseButton();
        setResizable(false);

        int columnCount = 0;
        for (byte i = 0; i < InventoryConstants.BAG_SIZE; i++) {

            // Create a slot for items
            ItemStackSlot itemStackSlot = new ItemStackSlot(this, InventoryType.BAG_1, i);
            itemStackSlot.build(stageHandler);

            add(itemStackSlot); // Add slot to BagWindow
            dragAndDrop.addSource(new ItemStackSource(stageHandler, dragAndDrop, itemStackSlot));
            dragAndDrop.addTarget(new ItemStackTarget(itemStackSlot));

            itemStackSlots[i] = itemStackSlot;
            columnCount++;

            if (columnCount == InventoryConstants.BAG_WIDTH) {
                row();
                columnCount = 0;
            }
        }

        stopWindowClickThrough();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {

            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                findPosition();
            }
        });

        pack();
        findPosition();
        setVisible(false);
        return this;
    }

    public void openWindow() {
        ActorUtil.fadeInWindow(this);
        findPosition();
        stageHandler.getBankWindow().findWindowPosition(false);
    }

    public void closeWindow() {
        ActorUtil.fadeOutWindow(this);
        stageHandler.getBankWindow().findWindowPosition(true);
    }

    private void findPosition() {
        GameButtonBar gameButtonBar = stageHandler.getGameButtonBar();
        float endPosition = gameButtonBar.getX() + gameButtonBar.getWidth() + gameButtonBar.getPadLeft();
        float bagX = stageHandler.getStage().getViewport().getScreenWidth() - getWidth() - StageHandler.WINDOW_PAD_X;

        if (endPosition > bagX) {
            setPosition(bagX, gameButtonBar.getHeight() + gameButtonBar.getY());
        } else {
            setPosition(bagX, StageHandler.WINDOW_PAD_Y);
        }
    }

    /**
     * EDIT: unenregizer -> Override this method so that we can call {@link #closeWindow()} instead of close();
     * Adds close button to window, next to window title. After pressing that button, {@link #close()} is called. If nothing
     * else was added to title table, and current title alignment is center then the title will be automatically centered.
     */
    @Override
    public void addCloseButton() {
        Label titleLabel = getTitleLabel();
        Table titleTable = getTitleTable();

        VisImageButton closeButton = new VisImageButton("close-window");
        titleTable.add(closeButton).padRight(-getPadRight() + 0.7f);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                closeWindow();
            }
        });
        closeButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.cancel();
                return true;
            }
        });

        if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
            titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }
}
