package ecs.Systems;

import com.badlogic.ashley.core.*;
import com.hasanchik.shared.box2dutils.WorldHandler;
import com.hasanchik.shared.misc.BodyUserData;
import ecs.ComponentMappers;
import ecs.Components;
import ecs.ListenerPrioritySystem;

public class Box2DBodySystem extends EntitySystem implements ListenerPrioritySystem {
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
                        int entityID = ComponentMappers.entityComponentMapper.get(entity).entityID;
                        
                        if (box2DComponent.body == null && box2DComponent.box2DBodyBuilder != null) {
                            box2DComponent.body = worldHandler.putBodyInWorld(entityID, box2DComponent.box2DBodyBuilder);
                            box2DComponent.box2DBodyBuilder = null;
                        } else if (box2DComponent.body == null && box2DComponent.box2DBodyBuilder == null) {
                            throw new NullPointerException("Both box2DBodyBuilder and Body fields of the box2DComponent are null!");
                        }

                        synchronized (box2DComponent.body) {
                            box2DComponent.body.setUserData(new BodyUserData(entityID));
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
