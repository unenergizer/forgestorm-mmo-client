package com.forgestorm.client.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class BetterCameraZoom {

    private static final int ZOOM_OUT = 1;
    private static final int ZOOM_IN = -1;

    @Getter
    private static ZoomValues defaultZoom = ZoomValues.ZOOM_1;

    public static float findNextZoomValue(float cameraZoomLevel, int button) {
        ZoomValues currentZoomLevel = defaultZoom; // prevents NPE warning
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
        return defaultZoom.getZoomValue();
    }

    @SuppressWarnings("unused")
    @AllArgsConstructor
    public enum ZoomValues {

        ZOOM_0(.25f),
        ZOOM_1(.5f),
        ZOOM_2(1f),
        ZOOM_3(2f),
        ZOOM_4(4f);

        @Getter
        private float zoomValue;

        private static ZoomValues[] values = values();

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
