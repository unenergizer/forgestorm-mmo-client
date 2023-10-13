package com.forgestorm.client.game.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientConstants;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.Entity;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.game.world.maps.tile.properties.TileWalkOverSoundProperty;
import com.forgestorm.client.io.FileManager;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.forgestorm.shared.util.RandomNumberUtil;
import lombok.Getter;

import java.util.Map;

import static com.forgestorm.client.util.Log.println;

@SuppressWarnings("rawtypes")
public class SoundManager implements Disposable {

    private static final float MAX_DISTANCE = ClientConstants.CHUNK_SIZE;

    private static final boolean PRINT_DEBUG = false;

    private final ClientMain clientMain;
    private final FileManager fileManager;
    private final Map<Short, AudioData> soundFX;

    @Getter
    private final AudioPreferences audioPreferences = new AudioPreferences();
    private Sound currentSound;
    private AudioData audioData;

    SoundManager(ClientMain clientMain) {
        this.clientMain = clientMain;
        this.fileManager = clientMain.getFileManager();
        this.soundFX = fileManager.getSoundData().getSoundDataMap();
    }

    /**
     * Play sounds and adjust volume of the sound based on location.
     *
     * @param clazz    The class that is calling this method.
     * @param audioId  The sound we want to play.
     * @param location The location of the sound being played.
     */
    public void playSoundFx(Class clazz, short audioId, Location location) {
        playSoundFx(clazz, audioId, location.getX(), location.getY(), location.getZ());
    }

    public void playSoundFx(Class clazz, short audioId, int x2, int y2, short z2) {
        Location playerClientLocation = clientMain.getEntityManager().getPlayerClient().getCurrentMapLocation();

        int x1 = playerClientLocation.getX();
        int y1 = playerClientLocation.getY();
        int z1 = playerClientLocation.getZ();

        float distanceXY = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

        float distanceVolume;

        if (distanceXY > MAX_DISTANCE) {
            distanceVolume = 0.0f;
        } else {
            // Using linear relationship for distance-based volume
            distanceVolume = Math.max(0.0f, 1.0f - (distanceXY / MAX_DISTANCE));
        }

        // Skip playing sounds you can not hear!
        if (distanceVolume == 0) return;

        playSoundFx(clazz, audioId, distanceVolume);
    }

    /**
     * A convince method to play a sound at full volume. This is useful for UI.
     *
     * @param clazz   The class that is calling this method.
     * @param audioId The sound we want to play.
     */
    public void playSoundFx(Class clazz, short audioId) {
        playSoundFx(clazz, audioId, 1);
    }

    public void playSoundFx(Class clazz, short audioId, float volume) {
        assert volume >= 0 && volume <= 1;
        AudioData audioData = soundFX.get(audioId);

        if (audioData == null) {
            println(getClass(), "AudioID is null: " + audioId, true);
            return;
        }

        boolean sameSound = this.audioData == audioData;
        this.audioData = audioData;

        if (currentSound != null && audioId != audioData.getAudioId()) {
            currentSound.dispose();
        }

        fileManager.loadSound(audioData);
        currentSound = fileManager.getSound(audioData);
        long id = currentSound.play();
        float finalVolume = audioPreferences.getSoundEffectsVolume() * volume;
        currentSound.setVolume(id, finalVolume);

        // Adjust the pitch to give sound variance (less annoying sounds)
        if (sameSound) currentSound.setPitch(id, RandomNumberUtil.getNewRandom(0.5f, 1.5f));

        println(getClass(), "AudioID: " + audioData.getAudioId(), false, PRINT_DEBUG);
        println(getClass(), " -Playing: " + audioData.getFileName(), false, PRINT_DEBUG);
        println(getClass(), " -Description: " + audioData.getDescription(), false, PRINT_DEBUG);
        println(getClass(), " -Source: " + clazz, false, PRINT_DEBUG);
        println(getClass(), " -Sound Volume: " + volume, false, PRINT_DEBUG);
        println(getClass(), " -User Volume: " + audioPreferences.getSoundEffectsVolume(), false, PRINT_DEBUG);
        println(getClass(), " -Final Volume: " + finalVolume, false, PRINT_DEBUG);
    }

    public void stopSoundFx(boolean dispose) {
        if (currentSound != null) currentSound.stop();
        if (dispose) dispose();
    }

    public void setPitch(long id, float pitch) {
        if (currentSound != null) currentSound.setPitch(id, pitch);
    }

    @Override
    public void dispose() {
        if (currentSound != null) currentSound.dispose();
    }

    public void playWalkSound(Class clazz, Entity entity) {
        final int invalidNumber = -1;
        int soundId = invalidNumber;

        Tile tile = entity.getGroundTile();
        Location currentMapLocation = entity.getCurrentMapLocation();

        if (tile == null) return;

        // Get the sound of the tile the player is on or moving to
        if (tile.getTileImage() != null) {
            TileImage tileImage = tile.getTileImage();

            if (tileImage.containsProperty(TilePropertyTypes.WALK_OVER_SOUND)) {
                TileWalkOverSoundProperty tileWalkOverSoundProperty = (TileWalkOverSoundProperty) tileImage.getProperty(TilePropertyTypes.WALK_OVER_SOUND);

                switch (tileWalkOverSoundProperty.getTileWalkSound()) {
                    case BRICK:
                    case STONE:
                        soundId = RandomNumberUtil.getNewRandom(51, 60);
                        break;
                    case DIRT:
                        soundId = RandomNumberUtil.getNewRandom(31, 40);
                        break;
                    case GRASS:
                        soundId = RandomNumberUtil.getNewRandom(21, 30);
                        break;
                    case GRAVEL:
                        soundId = RandomNumberUtil.getNewRandom(61, 70);
                        break;
                    case SAND:
                        soundId = RandomNumberUtil.getNewRandom(41, 50);
                        break;
                    case WATER_SHALLOW:
                    case WATER_DEEP:
                        soundId = RandomNumberUtil.getNewRandom(71, 80);
                        break;
                    case WOOD:
                        soundId = RandomNumberUtil.getNewRandom(81, 100);
                        break;
                }
            }
        }

        if (soundId == invalidNumber) {
            // Playing the default walking sound (grass walk)
            soundId = RandomNumberUtil.getNewRandom(21, 30);
        }

        playSoundFx(clazz, (short) soundId, currentMapLocation);
    }

    public void playItemStackSoundFX(Class clazz, ItemStack itemStack) {
        switch (itemStack.getItemStackType()) {
            case HELM:
            case CHEST:
            case PANTS:
            case SHOES:
            case CAPE:
            case GLOVES:
            case BELT:
                playSoundFx(clazz, (short) 3);
                break;
            case RING:
                playSoundFx(clazz, (short) 6);
                break;
            case NECKLACE:
                playSoundFx(clazz, (short) 7);
                break;
            case SWORD:
            case SHIELD:
                playSoundFx(clazz, (short) 4);
                break;
            case BOW:
            case ARROW:
                playSoundFx(clazz, (short) 5);
                break;
            case GOLD:
                playSoundFx(clazz, (short) 1);
                break;
            case POTION:
                playSoundFx(clazz, (short) 2);
                break;
            case MATERIAL:
                playSoundFx(clazz, (short) 8);
                break;
        }
    }
}
