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

    private final int layer;

    MapLayer(int layer) {
        this.layer = layer;
    }

    public static MapLayer getEnum(int layer) {
        return MapLayer.values()[layer];
    }
}
