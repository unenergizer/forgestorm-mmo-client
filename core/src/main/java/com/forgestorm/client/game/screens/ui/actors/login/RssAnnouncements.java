package com.forgestorm.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.forgestorm.client.io.RssFeedLoader;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.List;

public class RssAnnouncements extends VisTable implements Buildable {

    private static final int MAX_FEED_ENTRIES = 6;
    private VisTable rssFeedTable;

    @Override
    public Actor build(final StageHandler stageHandler) {
        rssFeedTable = new VisTable();

        add(new VisLabel("Announcements:")).align(Alignment.TOP_LEFT.getAlignment()).padBottom(5).row();
        add(rssFeedTable);

        createClickableEntryBox(stageHandler);

        setPosition(10, 40);
        pack();
        setVisible(false);
        return this;
    }

    private void createClickableEntryBox(StageHandler stageHandler) {
        RssFeedLoader.RssFeedWrapper rssFeedWrapper = stageHandler.getClientMain().getFileManager().getRssFeedData();

        if (rssFeedWrapper == null) return;
        if (rssFeedWrapper.getFeedData() == null) return;
        if (rssFeedWrapper.getFeedData().isEmpty()) return;

        List<SyndEntry> rssFeed = rssFeedWrapper.getFeedData();

        for (int i = 0; i < MAX_FEED_ENTRIES; i++) {
            if (i + 1 > rssFeed.size()) return; // Not enough posts on website...

            final SyndEntry syndEntry = rssFeed.get(i);
            final VisTextButton visTextButton = new VisTextButton(syndEntry.getTitle());

            visTextButton.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Gdx.net.openURI(syndEntry.getLink());
                    stageHandler.getClientMain().getAudioManager().getSoundManager().playSoundFx(RssAnnouncements.class, (short) 0);
                    return true;
                }

                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    visTextButton.setColor(Color.LIGHT_GRAY);
                }

                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    visTextButton.setColor(Color.WHITE);
                }
            });

            rssFeedTable.add(visTextButton).align(Alignment.TOP_LEFT.getAlignment()).padBottom(5).row();
            pack();
        }
    }
}
