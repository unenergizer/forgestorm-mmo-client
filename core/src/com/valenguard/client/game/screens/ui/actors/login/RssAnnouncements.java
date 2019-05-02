package com.valenguard.client.game.screens.ui.actors.login;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.building.utilities.Alignment;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.valenguard.client.game.screens.ui.actors.Buildable;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RssAnnouncements extends VisTable implements Buildable {

    private static final int MAX_FEED_ENTRIES = 6;

    @Override
    public Actor build() {

        List<SyndEntry> rssFeed = grabRssFeed();

        if (rssFeed != null) {
            add(new VisLabel("Announcements:")).align(Alignment.TOP_LEFT.getAlignment()).padBottom(5).row();
            createClickableEntryBox(rssFeed);
        }

        setPosition(10, 40);
        pack();
        return this;
    }

    private void createClickableEntryBox(List<SyndEntry> feed) {
        for (int i = 0; i < MAX_FEED_ENTRIES; i++) {

            if (i + 1 > feed.size()) return; // Not enough posts on website...

            final SyndEntry syndEntry = feed.get(i);
            final VisTextButton visLabel = new VisTextButton(syndEntry.getTitle());

            visLabel.addListener(new InputListener() {
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    Gdx.net.openURI(syndEntry.getLink());
                    return true;
                }

                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    visLabel.setColor(Color.LIGHT_GRAY);
                }

                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    visLabel.setColor(Color.WHITE);
                }
            });

            add(visLabel).align(Alignment.TOP_LEFT.getAlignment()).padBottom(5).row();
        }
    }

    private List<SyndEntry> grabRssFeed() {
        String url = "https://forgestorm.com/forums/announcements.2/index.rss";
        SyndFeed feed = null;
        try {
            feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
        } catch (FeedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return feed.getEntries();
    }
}
