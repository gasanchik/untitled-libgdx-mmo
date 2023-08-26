package com.hasanchik.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hasanchik.game.MyClientGame;

public class MainMenuScreen extends AbstractScreen {
    public MainMenuScreen(MyClientGame context) {
        super(context);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        viewport.apply();
        ScreenUtils.clear(1, 0, 0, 1);

        if(Gdx.input.isKeyPressed(Input.Keys.Q)
                || (Gdx.input.justTouched())
        ) context.setScreen(ScreenType.LOADING);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
