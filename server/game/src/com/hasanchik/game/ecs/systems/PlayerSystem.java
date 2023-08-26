package com.hasanchik.game.ecs.systems;

import com.badlogic.ashley.core.*;
import com.esotericsoftware.kryonet.Connection;
import com.hasanchik.game.GameRoomInstance;
import com.hasanchik.game.ecs.MyServerAshleyEngine;
import com.hasanchik.shared.box2dutils.WorldHandler;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilderDirector;
import ecs.ComponentMappers;
import ecs.Components;
import ecs.ListenerPrioritySystem;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerSystem extends EntitySystem implements ListenerPrioritySystem {
    private static final Logger logger = LogManager.getLogger(PlayerSystem.class);

    private final int listenerPriority;

    private final GameRoomInstance context;

    private final WorldHandler worldHandler;
    private final ConcurrentHashMap<String, Entity> playerNameToEntityMap = new ConcurrentHashMap<>();

    private MyServerAshleyEngine engine = null;

    public PlayerSystem(GameRoomInstance context, int listenerPriority) {
        super();

        this.listenerPriority = listenerPriority;
        this.context = context;

        this.worldHandler = context.getWorldHandler();
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = (MyServerAshleyEngine) engine;
        //entities = engine.getEntitiesFor(Family.all(Components.PlayerComponent.class).get());
    }

    public synchronized void addPlayer(String playerName, Connection connection) {
        Entity player = new Entity();
        player.add(new Components.Box2DComponent(worldHandler.putBodyInWorld(Box2DBodyBuilderDirector.getDefaultSquare())));
        player.add(new Components.PlayerComponent(playerName, connection));
        playerNameToEntityMap.put(playerName, player);
        engine.addEntity(player);
    }

    public synchronized void removePlayer(String playerName) {
        Entity player = playerNameToEntityMap.get(playerName);
        playerNameToEntityMap.remove(ComponentMappers.playerComponentMapper.get(player).playerName);
        logger.info(player.getComponent(Components.Box2DComponent.class));
        engine.removeEntity(player);
    }
}
