package com.valenguard.client.game.screens.ui.actors.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.valenguard.client.game.inventory.ItemStack;
import com.valenguard.client.game.screens.ui.actors.ActorUtil;
import com.valenguard.client.game.screens.ui.actors.Buildable;
import com.valenguard.client.game.screens.ui.actors.HideableVisWindow;
import com.valenguard.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.valenguard.client.game.screens.ui.actors.event.WindowResizeListener;

import static com.valenguard.client.util.Log.println;

public class ItemDropDownMenu extends HideableVisWindow implements Buildable {

    public ItemDropDownMenu() {
        super("Choose Option");
    }

    @Override
    public Actor build() {

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
                cleanUpDropDownMenu(true);
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        setVisible(false);

        return this;
    }

    public void toggleMenu(ItemStack itemStack, float x, float y) {
        cleanUpDropDownMenu(false);
        VisTable dropDownTable = new VisTable();
        setPosition(438, 141);

        addDropButton(dropDownTable);
        addCancelButton(dropDownTable);

        add(dropDownTable).expand().fill();

        pack();

        ActorUtil.fadeInWindow(this);
    }

    private void addDropButton(VisTable visTable) {
        VisTextButton cancelButton = new VisTextButton("Drop");
        visTable.add(cancelButton).expand().fill().row();

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                println(getClass(), "Dropping an ITEM!");
            }
        });
    }

    private void addCancelButton(VisTable visTable) {
        VisTextButton cancelButton = new VisTextButton("Cancel");
        visTable.add(cancelButton).expand().fill().row();

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cleanUpDropDownMenu(true);
            }
        });
    }

    private void cleanUpDropDownMenu(boolean closeWindow) {
        if (closeWindow) ActorUtil.fadeOutWindow(this);
        this.remove();
    }
}
