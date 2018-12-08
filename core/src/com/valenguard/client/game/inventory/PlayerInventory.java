package com.valenguard.client.game.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;

public class PlayerInventory extends Window  {

    private static final int NUM_ROWS = 6;
    private static final int NUM_COLUMNS = 5;

    private Table slotTable = new Table();
    private DragAndDrop draggableSystem = new DragAndDrop();

    public PlayerInventory(String title, Skin skin) {
        super(title, skin);
    }

    public void build(Skin skin) {

//        setDebug(true);
        align(Align.bottomRight);
        setOrigin(Align.bottomRight);
        setFillParent(true);


        int columnCount = 0;
        for (int i = 0; i < NUM_ROWS * NUM_COLUMNS; i++) {
            InventorySlot inventorySlot = new InventorySlot();
            draggableSystem.addSource(new InventorySourceListener(inventorySlot));
            draggableSystem.addTarget(new InventoryTargetListener(inventorySlot));
            slotTable.add(inventorySlot).pad(3);

            columnCount++;

            if (columnCount == NUM_COLUMNS) {
                System.out.println("Number of columns added before adding a row: " + columnCount);
                slotTable.row();
                columnCount = 0;
            }
        }

        this.add(slotTable);
        this.pack();
    }

    public void dispose() {

    }


   /* @Override
    public void build(Skin skin) {
        slotTable.setFillParent(true);
        slotTable.setDebug(false);
        slotTable.setWidth(ACTOR_WIDTH * NUM_COLUMNS);
        slotTable.setHeight(ACTOR_HEIGHT * NUM_ROWS);
        slotTable.align(Align.bottomRight);
        slotTable.pad(3);
        addActor(slotTable);
        setOrigin(Align.bottomRight);

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                InventorySlot inventorySlot = inventorySlots[(i * NUM_COLUMNS) + j] = new InventorySlot("" + ((i * NUM_COLUMNS) + j), skin);
                slotTable.add(inventorySlot).width(ACTOR_WIDTH).height(ACTOR_HEIGHT);
            }
            slotTable.row();
        }
    }*/

    /*public boolean addItemStack(ItemStack itemStack) {
        for (int i = 0; i < NUM_ROWS * NUM_COLUMNS; i++) {
            if (inventorySlots[i].getItemStack() == null) {

                Log.println(getClass(), "Adding item at location: " + i);
                Log.println(getClass(), "Item id: " + itemStack.getItemId());
                Log.println(getClass(), "Item amount: " + itemStack.getAmount());

                inventorySlots[i].setItemStack(itemStack);
                return true;
            }
        }
        return false;
    }*/
}
