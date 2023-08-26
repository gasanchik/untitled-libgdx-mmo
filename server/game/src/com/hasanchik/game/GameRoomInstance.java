package com.hasanchik.game;

import com.hasanchik.game.ecs.MyServerAshleyEngine;
import com.hasanchik.game.ecs.systems.EntityReplicationSystem;
import com.hasanchik.game.ecs.systems.PlayerSystem;
import com.hasanchik.game.networking.ServerNetworkingHandler;
import com.hasanchik.shared.box2dutils.WorldHandler;
import com.hasanchik.shared.misc.DefaultThreadFactory;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class GameRoomInstance implements Runnable {
    private static final Logger logger = LogManager.getLogger(GameRoomInstance.class);

    private final MyGameServer context;

    public static final float ENTITY_REPLICATIONS_PER_SECOND = 1f / 10f;
    public static final float ECS_UPDATES_PER_SECOND = 1f / 30f;
    public static final float PHYSICS_UPDATES_PER_SECOND = 1f / 45f;

    private final ServerNetworkingHandler serverNetworkingHandler = ServerNetworkingHandler.getInstanceIfExists();

    private final WorldHandler worldHandler = new WorldHandler(PHYSICS_UPDATES_PER_SECOND, true);

    private final MyServerAshleyEngine engine;
    private final PlayerSystem playerSystem;
    private final EntityReplicationSystem entityReplicationSystem;

    //playerName, body
    //private final ConcurrentHashMap<String, Body> playerBodies;

    public GameRoomInstance(MyGameServer context) {
        this.context = context;
        //worldHandler.createTestScene();

        this.engine = new MyServerAshleyEngine(this, ECS_UPDATES_PER_SECOND);
        this.playerSystem = engine.getSystem(PlayerSystem.class);
        this.entityReplicationSystem = engine.getSystem(EntityReplicationSystem.class);
    }

    @Override
    public void run() {
        //Ensure that this stays thread-safe
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2, new DefaultThreadFactory());

        executorService.scheduleAtFixedRate(() -> {
            try {
                worldHandler.step();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0,  (long) (PHYSICS_UPDATES_PER_SECOND*1000L), TimeUnit.MILLISECONDS);

        //WorldHandler thread and ecs thread, keep them seperate
        executorService.scheduleAtFixedRate(() -> {
            try {
                engine.update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, (long) (ECS_UPDATES_PER_SECOND*1000L), TimeUnit.MILLISECONDS);
    }

    public void dispose() {
        worldHandler.dispose();
    }
}
