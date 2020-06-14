package com.valenguard.client.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StreamUtils;
import com.valenguard.client.game.screens.ui.actors.game.PlayerProfileWindow;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {

    public void download(final PlayerProfileWindow playerProfileWindow, final String url) {
        new Thread(new Runnable() {
            /** Downloads the content of the specified url to the array. The array has to be big enough. */
            private int download(byte[] out, String url) {
                InputStream inputStream = null;
                try {
                    HttpURLConnection httpURLConnection;
                    httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.setUseCaches(true);
                    httpURLConnection.connect();
                    inputStream = httpURLConnection.getInputStream();
                    int readBytes = 0;
                    while (true) {
                        int length = inputStream.read(out, readBytes, out.length - readBytes);
                        if (length == -1) break;
                        readBytes += length;
                    }
                    return readBytes;
                } catch (Exception ex) {
                    return 0;
                } finally {
                    StreamUtils.closeQuietly(inputStream);
                }
            }

            @Override
            public void run() {
                byte[] bytes = new byte[10000]; // assuming the content is not bigger than 200kb.

                int numBytes = download(bytes, url);
                if (numBytes != 0) {
                    // load the pixmap, make it a power of two if necessary (not needed for GL ES 2.0!)
                    Pixmap pixmap = new Pixmap(bytes, 0, numBytes);
                    int width = MathUtils.nextPowerOfTwo(pixmap.getWidth());
                    int height = MathUtils.nextPowerOfTwo(pixmap.getHeight());
                    final Pixmap potPixmap = new Pixmap(width, height, pixmap.getFormat());
                    potPixmap.setBlending(Pixmap.Blending.None);
                    potPixmap.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
                    pixmap.dispose();
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            playerProfileWindow.updateProfilePicture(new Texture(potPixmap));
                        }
                    });
                }
            }
        }).start();
    }
}
