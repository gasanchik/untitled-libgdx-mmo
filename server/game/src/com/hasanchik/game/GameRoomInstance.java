package com.hasanchik.game;

import com.hasanchik.game.ecs.MyServerAshleyEngine;
import com.hasanchik.game.ecs.systems.EntityReplicationSystem;
import com.hasanchik.game.ecs.systems.PlayerSystem;
import com.hasanchik.game.networking.ServerNetworkingHandler;
import com.hasanchik.shared.box2dutils.WorldHandler;
import com.hasanchik.shared.map.InfiniteEntityMap;
import com.hasanchik.shared.misc.DefaultThreadFactory;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.hasanchik.shared.misc.Constants.ECS_UPDATES_PER_SECOND;
import static com.hasanchik.shared.misc.Constants.PHYSICS_UPDATES_PER_SECOND;

@Getter
public class GameRoomInstance implements Runnable {
    private static final Logger logger = LogManager.getLogger(GameRoomInstance.class);

    private final MyGameServer context;

    public static final float ENTITY_REPLICATIONS_PER_SECOND = 1f / 10f;

    private final ServerNetworkingHandler serverNetworkingHandler = ServerNetworkingHandler.getInstanceIfExists();

    private final InfiniteEntityMap map = new InfiniteEntityMap(new WorldHandler(PHYSICS_UPDATES_PER_SECOND));
    private final WorldHandler worldHandler = map.getWorldHandler();

    private final MyServerAshleyEngine engine;
    private final PlayerSystem playerSystem;
    private final EntityReplicationSystem entityReplicationSystem;

    public GameRoomInstance(MyGameServer context) {
        this.context = context;

        this.engine = new MyServerAshleyEngine(this, ECS_UPDATES_PER_SECOND, map);

        this.playerSystem = engine.getSystem(PlayerSystem.class);
        this.entityReplicationSystem = engine.getSystem(EntityReplicationSystem.class);

        worldHandler.createTestScene(engine);
    }

    @Override
    public void run() {
        //Ensure that this stays thread-safe
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2, new DefaultThreadFactory());

        executorService.scheduleAtFixedRate(() -> {
            try {
                engine.getMap().update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0,  (long) (PHYSICS_UPDATES_PER_SECOND*1000L), TimeUnit.MILLISECONDS);

        //InfiniteEntityMap thread and ecs thread, keep them separate
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
