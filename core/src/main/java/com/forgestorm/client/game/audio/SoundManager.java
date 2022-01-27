package com.forgestorm.client.game.audio;

import static com.forgestorm.client.util.Log.println;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.forgestorm.client.ClientMain;
import com.forgestorm.client.game.world.entities.EntityManager;
import com.forgestorm.client.game.world.maps.Location;
import com.forgestorm.client.game.world.maps.tile.Tile;
import com.forgestorm.client.game.world.maps.tile.TileImage;
import com.forgestorm.client.game.world.maps.tile.properties.TileWalkOverSoundProperty;
import com.forgestorm.shared.game.world.item.ItemStack;
import com.forgestorm.shared.game.world.maps.building.LayerDefinition;
import com.forgestorm.shared.game.world.maps.tile.properties.TilePropertyTypes;
import com.forgestorm.shared.util.RandomNumberUtil;

import java.util.Map;

import lombok.Getter;

@SuppressWarnings("rawtypes")
public class SoundManager implements Disposable {

    private static final boolean PRINT_DEBUG = false;

    private final Map<Short, AudioData> soundFX;

    @Getter
    private final AudioPreferences audioPreferences = new AudioPreferences();
    private Sound currentSound;
    private AudioData audioData;
    private long id;

    SoundManager() {
        this.soundFX = ClientMain.getInstance().getFileManager().getSoundData().getSoundDataMap();
    }

    public void playSoundFx(Class clazz, short audioId) {
        AudioData audioData = soundFX.get(audioId);

        if (audioData == null) {
            println(getClass(), "AudioID is null: " + audioId, true);
            return;
        }

        boolean sameSound = false;
        if (this.audioData == audioData) sameSound = true;
        this.audioData = audioData;

        if (currentSound != null && audioId != audioData.getAudioId()) {
            currentSound.dispose();
        }

        ClientMain.getInstance().getFileManager().loadSound(audioData);
        currentSound = ClientMain.getInstance().getFileManager().getSound(audioData);
        id = currentSound.play();
        currentSound.setVolume(id, audioPreferences.getSoundEffectsVolume());

        // Adjust the pitch to give sound variance (less annoying sounds)
        if (sameSound) currentSound.setPitch(id, RandomNumberUtil.getNewRandom(0.5f, 1.5f));

        println(getClass(), "AudioID: " + audioData.getAudioId(), false, PRINT_DEBUG);
        println(getClass(), " -Playing: " + audioData.getFileName(), false, PRINT_DEBUG);
        println(getClass(), " -Description: " + audioData.getDescription(), false, PRINT_DEBUG);
        println(getClass(), " -Source: " + clazz, false, PRINT_DEBUG);
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

    public void playWalkSound(Class clazz) {
        final int invalidNumber = -1;
        int soundId = invalidNumber;
        boolean playDefaultWalkSound = false;

        Tile tile = EntityManager.getInstance().getPlayerClient().getGroundTile();

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
                }
            } else {
                playDefaultWalkSound = true;
            }
        } else {
            playDefaultWalkSound = true;
        }

        if (soundId == invalidNumber || playDefaultWalkSound) {
            soundId = RandomNumberUtil.getNewRandom(21, 30);
        }

        playSoundFx(clazz, (short) soundId);
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
