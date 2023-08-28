package com.hasanchik.shared.map;

import lombok.Getter;

@Getter
public enum MapLayer {
    BACKGROUND_FOLIAGE(0),
    BACKGROUND(1),
    MIDDLEGROUND_FOLIAGE(2),
    MIDDLEGROUND(3),
    FOREGROUND_FOLIAGE(4),
    FOREGROUND(5),
    BEFOREGROUND_FOLIAGE(6);

    private final byte layer;

    MapLayer(int layer) {
        this.layer = (byte) layer;
    }

    public static MapLayer getEnum(byte layer) {
        return MapLayer.values()[layer];
    }
}
