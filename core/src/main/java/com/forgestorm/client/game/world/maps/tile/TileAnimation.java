package com.forgestorm.client.game.world.maps.tile;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class TileAnimation {

    @Getter
    private final Map<Integer, AnimationFrame> animationFrames = new HashMap<>();

    @Getter
    private transient final int animationId;

    private transient int activeFrame = 0;

    @Getter
    private PlaybackType playbackType;

    private transient boolean playForwards = true;

    public TileAnimation(int animationId, PlaybackType playbackType) {
        this.animationId = animationId;
        this.playbackType = playbackType;
    }

    public TileAnimation(TileAnimation tileAnimation) {
        this.animationId = tileAnimation.animationId;
        this.playbackType = tileAnimation.playbackType;

        // Copy contents of the Animation frames over to this new animation
        for (Map.Entry<Integer, AnimationFrame> entry : tileAnimation.getAnimationFrames().entrySet()) {
            int id = entry.getKey();
            AnimationFrame animationFrame = entry.getValue();
            addAnimationFrame(id, animationFrame.getTileId(), animationFrame.getDuration());
        }
    }

    /**
     * A simple method to swap frames around. Or in other words a method to
     * rearrange frames.
     *
     * @param frameId       The id of the frame we want to move.
     * @param swapDirection The direction we want to move the frame.
     * @return Return true if swap happened, return false if it couldn't.
     */
    public boolean swapFrame(int frameId, SwapDirection swapDirection) {
        // Prevent frames from moving out of bounds. Prevent IndexOutOfBoundsException.
        if (frameId == 0 && swapDirection == SwapDirection.MOVE_UP) return false;
        if (frameId == animationFrames.size() - 1 && swapDirection == SwapDirection.MOVE_DOWN)
            return false;

        int swapId = frameId + swapDirection.getGetDirection();

        AnimationFrame movingFrame = animationFrames.get(frameId);
        movingFrame.setFrameId(swapId);

        AnimationFrame frameToAdjust = animationFrames.get(swapId);
        frameToAdjust.setFrameId(frameId);

        animationFrames.put(frameId, frameToAdjust);
        animationFrames.put(swapId, movingFrame);

        // Set active frame to 0 (animation start)
        activeFrame = 0;

        return true;
    }

    public void playAnimation(PlaybackType animationControl) {
        resetAnimationFrameDurations();

        switch (animationControl) {
            case PLAY_BACKWARDS:
            case PLAY_BACKWARDS_LOOPING:
                activeFrame = animationFrames.size() - 1;
                break;
            case PLAY_NORMAL:
            case PLAY_NORMAL_LOOPING:
            case STOP:
            default:
                activeFrame = 0;
                break;
        }

        this.playbackType = animationControl;
    }

    private void resetAnimationFrameDurations() {
        for (AnimationFrame animationFrame : animationFrames.values()) {
            animationFrame.resetDuration();
        }
    }

    /**
     * This processes the rendering order and timing of an animation frame.
     *
     * @return Returns the active frame currently being rendered.
     */
    public int getActiveFrame() {
        AnimationFrame animationFrame = animationFrames.get(activeFrame);

        // If no frames exist, return -1.
        if (animationFrame == null) return -1;
        if (playbackType == PlaybackType.STOP) return activeFrame;

        int durationLeft = animationFrame.getDurationLeft() - 1;

        if (durationLeft > 0) {
            // Continue playing out the duration of this animation frame
            animationFrame.setDurationLeft(durationLeft);
        } else {
            // The duration of the frame has ended. Decide what is next.
            animationFrame.setDurationLeft(animationFrame.getDuration()); // Reset duration

            if (playbackType == PlaybackType.PLAY_NORMAL
                    || playbackType == PlaybackType.PLAY_NORMAL_LOOPING
                    || (playbackType == PlaybackType.PING_PONG && playForwards)) {
                // Animation playing normally (looping and non-looping)
                int tempFrame = activeFrame + 1;
                int totalFrames = animationFrames.size();

                // Check we reached animation end
                if (tempFrame == totalFrames) {
                    switch (playbackType) {
                        case PLAY_NORMAL:
                            // Stop animating, last frame reached
                            playbackType = PlaybackType.STOP;
                            break;
                        case PLAY_NORMAL_LOOPING:
                            // Continue to loop
                            activeFrame = 0; // Go back to first frame
                            break;
                        case PING_PONG:
                            playForwards = false;
                            activeFrame = animationFrames.size() - 1; // Go back to last frame
                            break;
                    }
                } else {
                    activeFrame++;
                }
            } else //noinspection ConstantConditions - This thing trippin...
                if (playbackType == PlaybackType.PLAY_BACKWARDS
                        || playbackType == PlaybackType.PLAY_BACKWARDS_LOOPING
                        || (playbackType == PlaybackType.PING_PONG && !playForwards)) {
                    // Animation playing in reverse (looping and non-looping)
                    int tempFrame = activeFrame - 1;

                    // Check we reached animation end
                    if (tempFrame < 0) {
                        switch (playbackType) {
                            case PLAY_BACKWARDS:
                                // Stop animating, last frame reached
                                playbackType = PlaybackType.STOP;
                                break;
                            case PLAY_BACKWARDS_LOOPING:
                                // Continue to loop
                                activeFrame = animationFrames.size() - 1; // Go back to last frame
                                break;
                            case PING_PONG:
                                playForwards = true;
                                activeFrame = 0; // Go back to first frame
                                break;
                        }
                    } else {
                        activeFrame--;
                    }
                }
        }

        return activeFrame;
    }

    /**
     * Adds a new frame to the current animation.
     *
     * @param frameId     The ID of the frame we are adding.
     * @param tileImageId The ID of the image we are adding.
     * @param duration    How long the animation will last during animating.
     */
    public void addAnimationFrame(int frameId, int tileImageId, int duration) {
        animationFrames.put(frameId, new AnimationFrame(frameId, tileImageId, duration));
    }

    /**
     * Removes a frame from the animation.
     *
     * @param frameId The ID of the frame we are removing.
     */
    public void removeFrame(int frameId) {
        // Move the removed frame to the end.
        for (int i = frameId; i < animationFrames.size(); i++) {
            AnimationFrame frameToAdjust = animationFrames.get(frameId + 1);

            if (frameToAdjust == null) continue;
            frameToAdjust.setFrameId(i);
            animationFrames.put(i, frameToAdjust);
        }

        // Delete the end frame
        animationFrames.remove(animationFrames.size() - 1);

        // Set active frame to 0 (animation start)
        activeFrame = 0;
    }

    public void changeFrameDuration(int frameId, int duration) {
        AnimationFrame animationFrame = animationFrames.get(frameId);
        animationFrame.setDuration(duration);
        animationFrame.setDurationLeft(duration);
    }

    public int getNumberOfFrames() {
        return animationFrames.size();
    }

    public AnimationFrame getAnimationFrame(int frameId) {
        return animationFrames.get(frameId);
    }

    public enum PlaybackType {
        PLAY_NORMAL,
        PLAY_NORMAL_LOOPING,
        PLAY_BACKWARDS,
        PLAY_BACKWARDS_LOOPING,
        PING_PONG,
        STOP
    }

    @Getter
    @AllArgsConstructor
    public enum SwapDirection {
        MOVE_UP(-1),
        MOVE_DOWN(1);

        public transient int getDirection;
    }

    @Getter
    public static class AnimationFrame {
        @Setter
        private transient int frameId;
        private final int tileId;

        @Setter
        private int duration; // Total duration

        @Setter
        private transient int durationLeft;

        public AnimationFrame(int frameId, int tileId, int duration) {
            this.frameId = frameId;
            this.tileId = tileId;
            this.duration = duration;
            this.durationLeft = duration;
        }

        public void resetDuration() {
            durationLeft = duration;
        }
    }
}
