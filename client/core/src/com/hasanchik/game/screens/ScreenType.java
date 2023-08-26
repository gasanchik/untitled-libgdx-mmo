package com.hasanchik.game.screens;

import com.badlogic.gdx.Screen;
import lombok.Getter;

@Getter
public enum ScreenType {
    GAME(GameScreen.class),
    LOADING(LoadingScreen.class),
    MAINMENU(MainMenuScreen.class);

    private final Class<? extends Screen> screenClass;

    ScreenType(final Class<? extends Screen> screenClass) {
        this.screenClass = screenClass;
    }
}
