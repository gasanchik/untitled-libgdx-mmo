package com.hasanchik.shared.networking;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public abstract class MyComponent implements Component, Cloneable, Pool.Poolable {
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}