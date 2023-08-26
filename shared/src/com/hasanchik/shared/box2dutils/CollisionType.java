package com.hasanchik.shared.box2dutils;

import com.badlogic.gdx.physics.box2d.Filter;
import lombok.Getter;

@Getter
public enum CollisionType {
    BACKGROUND(1 << 0, 0, 0),
    COLLIDABLES(1 << 1, 1 << 1 | 1 << 2, 0),
    SHIPS(1 << 2, 1 << 1, 0);

    private final short categoryBits;
    private final short maskBits;
    private final short groupIndex;

    CollisionType(int categoryBits, int maskBits, int groupIndex)
    {
        this.categoryBits = (short) categoryBits;
        this.maskBits = (short) maskBits;
        this.groupIndex = (short) groupIndex;
    }

    public Filter getAsFilter() {
        Filter filter = new Filter();
        filter.categoryBits = categoryBits;
        filter.maskBits = maskBits;
        filter.groupIndex = groupIndex;
        return filter;
    }
}
