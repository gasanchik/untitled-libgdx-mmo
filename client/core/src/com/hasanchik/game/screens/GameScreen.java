package com.hasanchik.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.hasanchik.game.MyClientGame;
import com.hasanchik.game.map.MyMapRenderer;
import com.hasanchik.game.networking.ClientNetworkingHandler;
import com.hasanchik.game.utils.GDXDialogsFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.hasanchik.shared.misc.Constants.BOX2D_UNIT_SCALE;

public class GameScreen extends AbstractScreen {
    private static final Logger logger = LogManager.getLogger(GameScreen.class);

    private final Box2DDebugRenderer box2DDebugRenderer = new Box2DDebugRenderer();
    private final MyMapRenderer myMapRenderer;

    private final AssetManager assetManager;

    private final OrthographicCamera camera;

    public GameScreen(MyClientGame context) {
        super(context);
        this.camera = context.getCamera();
        this.myMapRenderer = new MyMapRenderer(camera, BOX2D_UNIT_SCALE, context.getSpriteBatch());
        this.assetManager = context.getAssetManager();
        //Gdx.graphics.setWindowedMode(600,600);
    }

    @Override
    public void show() {
        myMapRenderer.setMap(assetManager.get("assets/maps/map.json"));
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

        myMapRenderer.setView(camera);
        myMapRenderer.render();
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
    }
}
