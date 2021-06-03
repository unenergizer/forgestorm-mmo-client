package com.forgestorm.client.game.world.maps;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class TileAnimation {

    @Getter
    private final Map<Integer, AnimationFrame> animationFrames = new HashMap<Integer, AnimationFrame>();

    @Getter
    private transient final int animationId;

    private transient int activeFrame = 0;

    public TileAnimation(int animationId) {
        this.animationId = animationId;
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

    /**
     * This processes the rendering order and timing of an animation frame.
     *
     * @return Returns the active frame currently being rendered.
     */
    public int getActiveFrame() {
        AnimationFrame animationFrame = animationFrames.get(activeFrame);

        // If no frames exist, return -1.
        if (animationFrame == null) return -1;

        int durationLeft = animationFrame.getDurationLeft() - 1;

        if (durationLeft <= 0) {
            animationFrame.setDurationLeft(animationFrame.getDuration()); // Reset duration

            activeFrame++;
            int totalFrames = animationFrames.size();

            if (activeFrame >= totalFrames) activeFrame = 0; // Go back to first frame
        } else {
            animationFrame.setDurationLeft(durationLeft);
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
    }
}
