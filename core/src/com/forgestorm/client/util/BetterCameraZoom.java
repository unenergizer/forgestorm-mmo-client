package com.forgestorm.client.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class BetterCameraZoom {

    private static final int ZOOM_OUT = 1;
    private static final int ZOOM_IN = -1;

    @Getter
    private static final ZoomValues DEFAULT_ZOOM = ZoomValues.ZOOM_3;

    public static float findNextZoomValue(float cameraZoomLevel, int button) {
        ZoomValues currentZoomLevel = DEFAULT_ZOOM; // prevents NPE warning
        for (ZoomValues zoomValues : ZoomValues.values()) {
            if (zoomValues.getZoomValue() == cameraZoomLevel) {
                currentZoomLevel = zoomValues;
                break;
            }
        }

        if (button == ZOOM_OUT) {
            return currentZoomLevel.next().getZoomValue();
        } else if (button == ZOOM_IN) {
            return currentZoomLevel.previous().getZoomValue();
        }
        return DEFAULT_ZOOM.getZoomValue();
    }

    @SuppressWarnings("unused")
    @AllArgsConstructor
    public enum ZoomValues {

        ZOOM_0(.05f),
        ZOOM_1(.10f),
        ZOOM_2(.25f),
        ZOOM_3(.5f),
        ZOOM_4(1f),
        ZOOM_5(2f),
        ZOOM_6(4f);

        @Getter
        private final float zoomValue;

        private static final ZoomValues[] values = values();

        public ZoomValues next() {
            int index = this.ordinal() + 1;
            if (index >= values().length) index = values().length - 1;
            return values[(index) % values.length];
        }

        public ZoomValues previous() {
            int index = this.ordinal() - 1;
            if (index == -1) index = 0;
            return values[(index) % values.length];
        }
    }
}
