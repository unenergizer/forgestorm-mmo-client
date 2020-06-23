package com.forgestorm.client.game.screens.ui.actors.game.paging;

import com.kotcrab.vis.ui.widget.VisTable;

/**
 * This class holds information for a particular {@link PagedWindowSlot}
 * within a {@link PagedWindow}
 */
abstract class PagedWindowSlot extends VisTable {

    /**
     * Builds a window slot. Place slot contents inside this method.
     */
    abstract void buildSlot();
}
