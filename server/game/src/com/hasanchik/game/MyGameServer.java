package com.hasanchik.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.minlog.Log;
import com.hasanchik.game.networking.ServerNetworkingHandler;
import com.hasanchik.shared.misc.DefaultThreadFactory;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@Getter
public class MyGameServer extends Game {
    private static final Logger logger = LogManager.getLogger(MyGameServer.class);
    public static final int GAME_ROOMS_CAPACITY = 50;

    //Graphics related things are for debugging only
    private FitViewport viewport;
    private ServerNetworkingHandler serverNetworkingHandler;
    private ArrayList<GameRoomInstance> gameRooms;
    private Box2DDebugRenderer box2DDebugRenderer;
    private ShapeRenderer shapeRenderer;

    public MyGameServer() {
    }

    @Override
    public void create () {
        Log.set(Log.LEVEL_INFO);
        Box2D.init();

        viewport = new FitViewport(9, 16);
        gameRooms = new ArrayList<GameRoomInstance>(Collections.nCopies(GAME_ROOMS_CAPACITY, null));

        box2DDebugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);

        Thread serverNetworkingThread = new DefaultThreadFactory().newThread(ServerNetworkingHandler.getInstance(this, 38442, 38443).init());
        serverNetworkingThread.setName("ServerNetworkingThread");
        serverNetworkingThread.start();

        makeAndStartNewGameRoom(1);
    }

    @Override
    public void render () {
        final float delta = Gdx.graphics.getDeltaTime();

        viewport.apply();
        ScreenUtils.clear(0, 0, 0, 1);

        //Creates a small memory leak
        gameRooms
                .stream()
                .filter(Objects::nonNull)
                .forEach(gameRoom -> {
                    World world = gameRoom.getWorldHandler().getWorld();
                    synchronized (world) {
                        box2DDebugRenderer.render(world, viewport.getCamera().combined);
                    }
                    shapeRenderer.begin();
                    gameRoom.getWorldHandler().getBodyMap().drawSquares(shapeRenderer);
                    shapeRenderer.end();
                });
    }

    @Override
    public void dispose () {
        box2DDebugRenderer.dispose();
        gameRooms
                .stream()
                .filter(Objects::nonNull)
                .forEach(GameRoomInstance::dispose);
        super.dispose();
    }

    @Override
    public void resize (int width, int height) {
        viewport.setScreenSize(width, height);
    }

    public GameRoomInstance getGameRoom (int gameRoomID) {
        GameRoomInstance gameRoomInstance = gameRooms.get(gameRoomID);
        if (gameRoomInstance == null) {
            gameRoomInstance = makeAndStartNewGameRoom(gameRoomID);
        }
        return gameRoomInstance;
    }

    public GameRoomInstance makeAndStartNewGameRoom(int gameRoomID) {
        GameRoomInstance newGameRoom = new GameRoomInstance(this);
        gameRooms.set(gameRoomID, newGameRoom);
        new DefaultThreadFactory().newThread(newGameRoom).start();
        return newGameRoom;
    }
}