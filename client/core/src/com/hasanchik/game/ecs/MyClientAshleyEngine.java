package com.hasanchik.game.ecs;

import com.hasanchik.game.MyClientGame;
import com.hasanchik.shared.ecs.MyAshleyEngine;
import com.hasanchik.shared.ecs.Systems.Box2DBodySystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyClientAshleyEngine extends MyAshleyEngine {
    private static final Logger logger = LogManager.getLogger(MyClientAshleyEngine.class);

    private final MyClientGame context;

    public MyClientAshleyEngine(MyClientGame context, float fixedTimeStep) {
        super(fixedTimeStep);

        this.context = context;
        this.addSystem(new Box2DBodySystem(context.getWorldHandler(), 1));
    }
}
