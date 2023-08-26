package com.hasanchik.game.ecs;

import com.hasanchik.game.GameRoomInstance;
import com.hasanchik.game.ecs.systems.EntityReplicationSystem;
import com.hasanchik.game.ecs.systems.PlayerSystem;
import ecs.MyAshleyEngine;
import ecs.Systems.Box2DBodySystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyServerAshleyEngine extends MyAshleyEngine {
    private static final Logger logger = LogManager.getLogger(MyServerAshleyEngine.class);

    private final GameRoomInstance context;

    public MyServerAshleyEngine(GameRoomInstance context, float fixedTimeStep) {
        super(fixedTimeStep);

        this.context = context;

        this.addSystem(new EntityReplicationSystem(context, 1));
        this.addSystem(new Box2DBodySystem(context.getWorldHandler(), 2));
        this.addSystem(new PlayerSystem(context,3));
    }
}
