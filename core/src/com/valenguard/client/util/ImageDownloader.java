package com.valenguard.client.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StreamUtils;
import com.valenguard.client.game.screens.ui.actors.game.PlayerProfileWindow;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {

    TextureRegion image;

    PlayerProfileWindow playerProfileWindow;

    public void download(final PlayerProfileWindow playerProfileWindow) {
        this.playerProfileWindow = playerProfileWindow;
        new Thread(new Runnable() {
            /** Downloads the content of the specified url to the array. The array has to be big enough. */
            private int download(byte[] out, String url) {
                InputStream in = null;
                try {
                    HttpURLConnection conn = null;
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(false);
                    conn.setUseCaches(true);
                    conn.connect();
                    in = conn.getInputStream();
                    int readBytes = 0;
                    while (true) {
                        int length = in.read(out, readBytes, out.length - readBytes);
                        if (length == -1) break;
                        readBytes += length;
                    }
                    return readBytes;
                } catch (Exception ex) {
                    return 0;
                } finally {
                    StreamUtils.closeQuietly(in);
                }
            }

            @Override
            public void run() {
                byte[] bytes = new byte[90000]; // assuming the content is not bigger than 200kb.
                int numBytes = download(bytes, "https://forgestorm.com/data/avatars/o/0/1.jpg");
                if (numBytes != 0) {
                    // load the pixmap, make it a power of two if necessary (not needed for GL ES 2.0!)
                    Pixmap pixmap = new Pixmap(bytes, 0, numBytes);
                    final int originalWidth = pixmap.getWidth();
                    final int originalHeight = pixmap.getHeight();
                    int width = MathUtils.nextPowerOfTwo(pixmap.getWidth());
                    int height = MathUtils.nextPowerOfTwo(pixmap.getHeight());
                    final Pixmap potPixmap = new Pixmap(width, height, pixmap.getFormat());
                    potPixmap.setBlending(Pixmap.Blending.None);
                    potPixmap.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());
                    pixmap.dispose();
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Texture texture = new Texture(potPixmap);
                            image = new TextureRegion(texture, 0, 0, originalWidth, originalHeight);
                            playerProfileWindow.updateImage(texture);
                        }
                    });
                }
            }
        }).start();
    }
}
