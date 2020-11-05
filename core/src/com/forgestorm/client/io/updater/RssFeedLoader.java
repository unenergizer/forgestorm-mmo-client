package com.forgestorm.client.io.updater;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class RssFeedLoader extends AsynchronousAssetLoader<RssFeedLoader.RssFeedWrapper, RssFeedLoader.RssFeedParameter> {

    static class RssFeedParameter extends AssetLoaderParameters<RssFeedWrapper> {
    }

    private RssFeedWrapper rssFeedWrapper = null;

    public RssFeedLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, RssFeedParameter parameter) {
        rssFeedWrapper = null;
        rssFeedWrapper = new RssFeedWrapper();

        final String url = "https://forgestorm.com/forums/announcements.2/index.rss";
        SyndFeed feed = null;
        try {
            feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
        } catch (FeedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (feed != null && feed.getEntries() != null) {
            rssFeedWrapper.setFeedData(feed.getEntries());
        }
    }

    @Override
    public RssFeedWrapper loadSync(AssetManager manager, String fileName, FileHandle file, RssFeedParameter parameter) {
        return rssFeedWrapper;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, RssFeedParameter parameter) {
        return null;
    }

    @Setter
    @Getter
    public class RssFeedWrapper {
        private List<SyndEntry> feedData;
    }
}
