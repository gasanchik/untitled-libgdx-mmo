package com.hasanchik.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hasanchik.game.MyClientGame;
import com.hasanchik.game.networking.ClientNetworkingHandler;
import com.hasanchik.game.utils.GDXDialogsFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameScreen extends AbstractScreen {
    private static final Logger logger = LogManager.getLogger(GameScreen.class);

    private final Box2DDebugRenderer box2DDebugRenderer;

    public GameScreen(MyClientGame context) {
        super(context);
        box2DDebugRenderer = new Box2DDebugRenderer();
        //Gdx.graphics.setWindowedMode(600,600);
    }

    @Override
    public void show() {

    }

    public void onConnectionClosed() {
        GDXDialogsFacade.getSimpleInfoPopup("Connection closed").build().show();
        context.setScreen(ScreenType.MAINMENU);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        ScreenUtils.clear(0, 0, 0, 1);

        context.getWorldHandler().step();
        box2DDebugRenderer.render(context.getWorldHandler().getWorld(), viewport.getCamera().combined);

        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            context.setScreen(ScreenType.MAINMENU);
            ClientNetworkingHandler.getInstanceIfExists().closeConnection();
        }
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
        box2DDebugRenderer.dispose();
        super.dispose();
    }
}
