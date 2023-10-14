package com.forgestorm.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.forgestorm.client.game.screens.ui.StageHandler;
import com.forgestorm.client.game.screens.ui.actors.Buildable;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RssAnnouncements extends VisTable implements Buildable {

    private static final int MAX_FEED_ENTRIES = 6;
    public static final String RSS_NEWS_FEED = "https://forgestorm.com/forums/announcements.2/index.rss";
    private final VisLabel announcementsLabel = new VisLabel("Loading Announcements...");
    private final VisTable rssFeedTable = new VisTable();

    @Override
    public Actor build(StageHandler stageHandler) {
        add(announcementsLabel).align(Alignment.TOP_LEFT.getAlignment()).padBottom(5).row();
        add(rssFeedTable);

        addListener(new RssEventListener() {
            @Override
            protected void processRssFeed(List<SyndEntry> rssFeed) {
                buildRssNewsButtons(rssFeed);
            }
        });

        asyncRss();

        setPosition(10, 40);
        setVisible(false);
        pack();
        return this;
    }

    /**
     * In order to download the RSS news feed data from the internet, we need to
     * do it in a new thread. It takes too much time for the data to load and
     * when it finally does, the actor has already been built (thus omitting
     * the news data). To combat this we need to get the data asynchronously,
     * and then we call an event on the main rendering thread to have the news
     * buttons built.
     */
    private void asyncRss() {
        new Thread(() -> {
            // Get the RSS data asynchronously to the rendering thread
            RssFeedUtil rssFeedUtil = new RssFeedUtil();
            List<SyndEntry> rssFeed = rssFeedUtil.getRssFeed(RSS_NEWS_FEED);

            // Post a Runnable to the rendering thread that processes the result
            Gdx.app.postRunnable(() -> fire(new RssEvent(rssFeed)));
        }).start();
    }

    /**
     * This is called when RSS feed data has been downloaded from the internet.
     * Then we take that data and create news buttons from it.
     *
     * @param rssFeed The RSS data we downloaded
     */
    private void buildRssNewsButtons(List<SyndEntry> rssFeed) {

        // Check to make sure announcements exists
        if (rssFeed.isEmpty()) {
            announcementsLabel.setText("No Announcements...");
            return;
        } else {
            announcementsLabel.setText("Announcements:");
        }

        // Process RSS entries
        for (int i = 0; i < MAX_FEED_ENTRIES; i++) {

            if (i + 1 > rssFeed.size()) return; // Not enough posts on website...

            // Create the button
            final SyndEntry syndEntry = rssFeed.get(i);
            final VisTextButton visTextButton = new VisTextButton(syndEntry.getTitle());
            visTextButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.net.openURI(syndEntry.getLink());
                }
            });

            // Add button to the table
            rssFeedTable.add(visTextButton).align(Alignment.TOP_LEFT.getAlignment()).padBottom(5).row();

            // Pack after each button add
            pack();
        }
    }

    /**
     * This is an event that is generated when RSS feed data has been
     * downloaded from the internet.
     */
    @Getter
    @AllArgsConstructor
    static class RssEvent extends Event {
        /**
         * A list of RSS Feed data
         */
        private final List<SyndEntry> rssFeed;
    }

    /**
     * This is a listener class used to pass RSS event data to the actor
     */
    abstract static class RssEventListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (!(event instanceof RssEvent)) return false;
            processRssFeed(((RssEvent) event).rssFeed);
            return true;
        }

        /**
         * Process the RSS feed data
         *
         * @param rssFeed A list of RSS feed data
         */
        protected abstract void processRssFeed(List<SyndEntry> rssFeed);
    }

    static class RssFeedUtil {

        /**
         * Gets an RSS Feed and returns the feed data
         *
         * @param url The web address to get the RSS data from
         * @return If the connection is successful then return
         * a list of RSS entries. Otherwise, return an empty
         * list.
         */
        public List<SyndEntry> getRssFeed(final String url) {

            SyndFeed feed = null;

            try {
                feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            } catch (FeedException | IOException e) {
                e.printStackTrace();
            }

            if (feed == null || feed.getEntries() == null) return new ArrayList<>();
            return feed.getEntries();
        }
    }
}
