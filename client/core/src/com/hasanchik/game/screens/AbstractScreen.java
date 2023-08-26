package com.hasanchik.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hasanchik.game.MyClientGame;

public abstract class AbstractScreen implements Screen {
    protected final Viewport viewport;
    protected final MyClientGame context;

    public AbstractScreen(final MyClientGame context) {
        this.context = context;
        this.viewport = context.getViewport();
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        ScreenUtils.clear(1, 0, 0, 1);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void dispose() {

    }
}
