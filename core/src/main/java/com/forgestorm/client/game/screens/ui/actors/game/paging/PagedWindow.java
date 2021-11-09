package com.forgestorm.client.game.screens.ui.actors.game.paging;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.ActorUtil;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.game.screens.ui.actors.HideableVisWindow;
import com.forgestorm.client.game.screens.ui.actors.event.ForceCloseWindowListener;
import com.forgestorm.client.game.screens.ui.actors.event.WindowResizeListener;
import com.forgestorm.client.game.screens.ui.actors.game.chat.ChatChannelType;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.ArrayList;
import java.util.List;

import static com.forgestorm.client.util.Log.println;

public abstract class PagedWindow extends HideableVisWindow implements Buildable {

    private final int slotsWide;
    private final int windowSize;

    private final VisLabel pageDisplay = new VisLabel();
    private final VisTextButton previousPage = new VisTextButton("Previous Page");
    private final VisTextButton nextPage = new VisTextButton("Next Page");

    private VisTable pageContainer = new VisTable();
    private VisTable navTable = new VisTable();

    private List<VisTable> pages;
    private int currentPageIndex = 0;

    StageHandler stageHandler;

    PagedWindow(String windowTitle, int slotsWide, int slotsVertical) {
        super(windowTitle);
        this.slotsWide = slotsWide;
        this.windowSize = slotsWide * slotsVertical;
    }

    @Override
    public Actor build(final StageHandler stageHandler) {
        this.stageHandler = stageHandler;
        PagedWindow pagedWindow = this;
        TableUtils.setSpacingDefaults(this);
        addCloseButton(new CloseButtonCallBack() {
            @Override
            public void closeButtonClicked() {
                closePagedWindow(false);
            }
        });
        setResizable(false);

        pagedWindow.add(pageDisplay).row();

        navTable.add(previousPage).expand().fill();
        navTable.add(nextPage).expand().fill();

        pagedWindow.add(pageContainer).row();
        pagedWindow.add(navTable).expand().fill();

        previousPage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(PagedWindow.class, (short) 0);

                if (currentPageIndex > 0) {
                    currentPageIndex--;
                }

                setupButtons();
                changeWindowPage(currentPageIndex);
            }
        });

        nextPage.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(PagedWindow.class, (short) 0);

                if (currentPageIndex < pages.size() - 1) {
                    currentPageIndex++;
                }

                setupButtons();
                changeWindowPage(currentPageIndex);
            }
        });

        stopWindowClickThrough();

        addListener(new ForceCloseWindowListener() {
            @Override
            public void handleClose() {
            }
        });

        addListener(new WindowResizeListener() {
            @Override
            public void resize() {
                centerWindow();
            }
        });

        centerWindow();
        pack();
        setVisible(false);
        return this;
    }

    /**
     * Call this when this window is closed.
     *
     * @param playerMoved True if the window closed because of player movement.
     */
    public void closePagedWindow(boolean playerMoved) {
        if (!isVisible()) return;
        if (playerMoved) {
            stageHandler.getChatWindow().appendChatMessage(ChatChannelType.GENERAL, "[RED]" + getTitleLabel().getText() + " closed because you moved.");
        }

        ClientMain.getInstance().getAudioManager().getSoundManager().playSoundFx(PagedWindow.class, (short) 0);

        ActorUtil.fadeOutWindow(this);

        windowClosedAction();
    }

    /**
     * When the paged window is closed, use this abstract method
     * to do anything the abstract window might need.
     */
    abstract void windowClosedAction();

    /**
     * Builds and adds all the pages to the Window.
     *
     * @param windowSlots All of the {@link PagedWindowSlot} for this Window.
     * @return Returns a list of pages.
     */
    private List<VisTable> buildPageWindow(List<PagedWindowSlot> windowSlots) {

        // Generate window pages
        List<VisTable> pageList = new ArrayList<VisTable>();
        int columnCount = 0;
        int pageCount = 0;
        VisTable pageTable = new VisTable();
        pageList.add(pageTable); // Add first page

        for (int i = 0; i < windowSlots.size(); i++) {
            PagedWindowSlot pagedWindowSlot = windowSlots.get(i);
            pagedWindowSlot.buildSlot();

            pageTable.add(pagedWindowSlot).pad(5);
            columnCount++;
            pageCount++;

            // Test if we need to make a new page
            if (pageCount >= windowSize) {
                // Start new page
                pageTable = new VisTable();
                pageList.add(pageTable);

                pageCount = 0;
                columnCount = 0;
            } else if (columnCount == slotsWide) {
                // Test if we need to make a item row
                pageTable.row();
                columnCount = 0;
            }
        }

        // Generate blank spots
        int blankSpots = (pageList.size() * windowSize) - windowSlots.size();
        columnCount = windowSlots.size() % slotsWide;

        VisTable lastWindowPage = pageList.get(pageList.size() - 1);

        for (int i = 0; i < blankSpots; i++) {
            // TODO: REMOVE BlankWindowSlot
            PagedWindowSlot pagedWindowSlot = new BlankWindowSlot();
            pagedWindowSlot.buildSlot();
            lastWindowPage.add(pagedWindowSlot).pad(5);

            columnCount++;

            if (columnCount == slotsWide) {
                // Test if we need to make a item row
                pageTable.row();
                columnCount = 0;
            }
        }
        return pageList;
    }

    /**
     * Changes the page of the window.
     *
     * @param currentPageIndex The current window page.
     */
    private void changeWindowPage(int currentPageIndex) {
        pageDisplay.setText("Page: " + (currentPageIndex + 1));

        // Reset content tables
        for (Actor actor : pageContainer.getChildren()) {
            actor.remove();
        }

        // Build item table
        pageContainer.add(pages.get(currentPageIndex));

        setupButtons();
        pack();
    }

    /**
     * You must call this when you want to openWindow/open a paged window from the child class!
     */
    void loadPagedWindow() {
        resetPagedWindow();
        List<PagedWindowSlot> windowSlots = loadPagedWindowSlots();

        // Dynamic build window pages
        pages = buildPageWindow(windowSlots);
        println(getClass(), "Page Count: " + pages.size());
        changeWindowPage(0);
        if (pages.size() == 1) {
            previousPage.setVisible(false);
            nextPage.setVisible(false);
            pageDisplay.setVisible(false);
        }
        ActorUtil.fadeInWindow(this);
    }

    /**
     * Loads the slots needed to build the page.
     */
    abstract List<PagedWindowSlot> loadPagedWindowSlots();

    /**
     * Gets buttons ready use based on what page the user is on.
     */
    private void setupButtons() {
        // Setup previous page
        if (currentPageIndex == 0) {
            previousPage.setDisabled(true);
        } else {
            previousPage.setDisabled(false);
        }

        // Setup
        if (currentPageIndex == pages.size() - 1) {
            nextPage.setDisabled(true);
        } else {
            nextPage.setDisabled(false);
        }
    }

    /**
     * Resets the Window for next use.
     */
    private void resetPagedWindow() {
        if (pages != null) pages.clear();
        pages = null;
        currentPageIndex = 0;
        previousPage.setVisible(true);
        nextPage.setVisible(true);
        pageDisplay.setVisible(true);
        previousPage.setDisabled(true);
        nextPage.setDisabled(false);
    }
}
