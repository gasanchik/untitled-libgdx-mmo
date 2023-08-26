package com.hasanchik.game.screens;

import com.badlogic.gdx.utils.ScreenUtils;
import com.hasanchik.game.MyClientGame;
import com.hasanchik.game.networking.ClientNetworkingHandler;
import com.hasanchik.game.utils.GDXDialogsFacade;
import com.hasanchik.shared.misc.DefaultThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadingScreen extends AbstractScreen {
    private static final Logger logger = LogManager.getLogger(MyClientGame.class);

    public LoadingScreen(MyClientGame context) {
        super(context);
    }

    @Override
    public void show() {
        Thread networkingThread = new DefaultThreadFactory().newThread(() -> {
            ClientNetworkingHandler.getInstanceIfExists().connectToServer();
        });
        networkingThread.start();
    }

    public void onConnected(boolean success) {
        if (success) {
            GDXDialogsFacade.getSimpleInfoPopup("Successfully connected to server").build().show();
            context.setScreen(ScreenType.GAME);
        } else {
            //GDXDialogsFacade.getSimpleInfoPopup("Failed to connect").build().show();
            context.setScreen(ScreenType.MAINMENU);
        }
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        ScreenUtils.clear(0, 1, 0, 1);
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
