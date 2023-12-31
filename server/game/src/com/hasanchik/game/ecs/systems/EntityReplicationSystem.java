package com.hasanchik.game.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.hasanchik.game.GameRoomInstance;
import com.hasanchik.game.networking.ServerNetworkingHandler;
import com.hasanchik.shared.ecs.ComponentMappers;
import com.hasanchik.shared.ecs.Components;
import com.hasanchik.shared.ecs.ListenerPrioritySystem;
import com.hasanchik.shared.map.InfiniteEntityMap;
import com.hasanchik.shared.misc.BodyUserData;
import com.hasanchik.shared.networking.Packets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EntityReplicationSystem extends IntervalSystem implements ListenerPrioritySystem {
    private static final Logger logger = LogManager.getLogger(EntityReplicationSystem.class);

    private final int listenerPriority;

    private final GameRoomInstance context;
    private final ServerNetworkingHandler serverNetworkingHandler;
    private final InfiniteEntityMap map;

    private List<Entity> entityList;

    public EntityReplicationSystem(GameRoomInstance context, int listenerPriority) {
        super(GameRoomInstance.ENTITY_REPLICATIONS_PER_SECOND);

        this.listenerPriority = listenerPriority;
        this.context = context;

        this.serverNetworkingHandler = context.getServerNetworkingHandler();
        this.map = context.getMap();
    }

    @Override
    protected void updateInterval() {
        entityList = context.getEngine().getMap().getEntityArrayList();

        ArrayList<Body> bodiesList = map.getBodyMap().getBodiesInArea(new Rectangle(-4.5f/2, -8f/2, 9f/2, 16f/2));

        bodiesList.forEach(body -> {
            synchronized (body) {
                BodyUserData bodyUserData = (BodyUserData) body.getUserData();

                if(bodyUserData == null || body == null || entityList.get(bodyUserData.entityID) == null) {
                    return;
                }

                Packets.BodyUpdatePacket bodyUpdatePacket = new Packets.BodyUpdatePacket();
                bodyUpdatePacket.entityID = bodyUserData.entityID;
                bodyUpdatePacket.transformation = body.getTransform();
                bodyUpdatePacket.angularVelocity = body.getAngularVelocity();
                bodyUpdatePacket.linearVelocity = body.getLinearVelocity();

                serverNetworkingHandler
                        .getPlayerNameToRoomID()
                        .keySet()
                        .forEach(playerName -> serverNetworkingHandler.sendUDPPacket(playerName, bodyUpdatePacket));
            }
        });


/*        entityList.forEach(entity -> {
            if (entity == null || ComponentMappers.box2DComponentMapper.has(entity) == false) {
                return;
            }
            Body body = ComponentMappers.box2DComponentMapper.get(entity).body;

            synchronized (body) {
                BodyUserData bodyUserData = (BodyUserData) body.getUserData();
                logger.info(bodyUserData.entityID);
                if(bodyUserData == null || body == null || entityList.get(bodyUserData.entityID) == null) {
                    return;
                }

                Packets.BodyUpdatePacket bodyUpdatePacket = new Packets.BodyUpdatePacket();
                bodyUpdatePacket.entityID = bodyUserData.entityID;
                bodyUpdatePacket.transformation = body.getTransform();
                bodyUpdatePacket.angularVelocity = body.getAngularVelocity();
                bodyUpdatePacket.linearVelocity = body.getLinearVelocity();

                serverNetworkingHandler
                        .getPlayerNameToRoomID()
                        .keySet()
                        .forEach(playerName -> serverNetworkingHandler.sendUDPPacket(playerName, bodyUpdatePacket));
            }
        });*/
    }

    @Override
    public void addedToEngine(Engine engine) {
        engine.addEntityListener(
                Family.all(Components.Box2DComponent.class).get(),
                listenerPriority,
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {
                        replicateNewEntityToClients(entity);
                    }

                    @Override
                    public void entityRemoved(Entity entity) {
                        removeEntityFromWorldAndReplicateToClients(entity);
                    }
                }
        );
        //entities = engine.getEntitiesFor(Family.all(Components.PlayerComponent.class).get());
    }

    public void replicateNewEntityToClient(String playerName, Entity entity) {
        logger.info("Sending entity with entityID " + ComponentMappers.entityComponentMapper.get(entity).entityID);
        Packets.NewEntityPacket newEntityPacket = new Packets.NewEntityPacket();
        newEntityPacket.entity = entity;
        serverNetworkingHandler.sendTCPPacket(playerName, newEntityPacket);
    }

    public Entity replicateNewEntityToClients(Entity entity) {
        serverNetworkingHandler
                .getPlayerNameToRoomID()
                .keySet()
                .forEach(playerName -> replicateNewEntityToClient(playerName, entity));

        return entity;
    }

    public void removeEntityFromWorldAndReplicateToClients(Entity entity) {
        Components.Box2DComponent box2DComponent = ComponentMappers.box2DComponentMapper.get(entity);
        if (box2DComponent != null) {
            map.getWorldHandler().removeBodyFromWorld(box2DComponent.body);
        }

        int entityID = ComponentMappers.entityComponentMapper.get(entity).entityID;
        Packets.EntityRemovedPacket entityRemovedPacket = new Packets.EntityRemovedPacket();
        entityRemovedPacket.entityID = entityID;

        serverNetworkingHandler
                .getPlayerNameToRoomID()
                .keySet()
                .forEach(playerName -> serverNetworkingHandler.sendTCPPacket(playerName, entityRemovedPacket));
    }
}
