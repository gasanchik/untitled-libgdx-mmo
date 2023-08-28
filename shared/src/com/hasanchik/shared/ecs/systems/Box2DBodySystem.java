package com.hasanchik.shared.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.hasanchik.shared.box2dutils.WorldHandler;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilder;
import com.hasanchik.shared.ecs.ComponentMappers;
import com.hasanchik.shared.ecs.Components;
import com.hasanchik.shared.ecs.ListenerPrioritySystem;
import com.hasanchik.shared.misc.BodyUserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Box2DBodySystem extends EntitySystem implements ListenerPrioritySystem {
    private static final Logger logger = LogManager.getLogger(Box2DBodySystem.class);

    private final WorldHandler worldHandler;
    private final int listenerPriority;

    public Box2DBodySystem(WorldHandler worldHandler, int listenerPriority) {
        super();

        this.listenerPriority = listenerPriority;

        this.worldHandler = worldHandler;
    }

    @Override
    public void addedToEngine(Engine engine) {

        engine.addEntityListener(
                Family.all(Components.Box2DComponent.class).get(),
                listenerPriority,
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {
                        Components.Box2DComponent box2DComponent = ComponentMappers.box2DComponentMapper.get(entity);
                        Body body = box2DComponent.body;
                        Box2DBodyBuilder box2DBodyBuilder = box2DComponent.box2DBodyBuilder;
                        int entityID = ComponentMappers.entityComponentMapper.get(entity).entityID;

                        if (body == null && box2DBodyBuilder != null) {
                            box2DComponent.body = body = worldHandler.putBodyInWorld(entityID, box2DBodyBuilder);
                            box2DComponent.box2DBodyBuilder = null;
                            synchronized (body) {
                                body.setUserData(new BodyUserData(entityID));
                            }
                        } else if (body == null) {
                            throw new NullPointerException("Both box2DBodyBuilder and Body fields of the box2DComponent are null!");
                        }
                    }

                    @Override
                    public void entityRemoved(Entity entity) {

                    }
                }
        );
        //entities = engine.getEntitiesFor(Family.all(Components.PlayerComponent.class).get());
    }
}
