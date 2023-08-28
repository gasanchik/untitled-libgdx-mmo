package com.hasanchik.shared.misc.serializers;

import com.badlogic.ashley.core.Entity;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilder;
import com.hasanchik.shared.ecs.ComponentMappers;
import com.hasanchik.shared.ecs.Components;
import com.hasanchik.shared.networking.MyComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbstractEntitySerializer {
    private static final Logger logger = LogManager.getLogger(AbstractEntitySerializer.class);

    public static Entity makeReadyForSerialization(Entity entity) {
        Entity entityClone = new Entity();
        entity.getComponents().forEach(component -> {
            MyComponent myComponent = (MyComponent) component;
            entityClone.add(myComponent.clone());
        });

        if (ComponentMappers.playerComponentMapper.has(entity)) {
            entityClone.remove(Components.PlayerComponent.class);
        }

        Components.Box2DComponent box2DComponent = ComponentMappers.box2DComponentMapper.get(entityClone);
        if (box2DComponent != null) {
            if (box2DComponent.body != null) {
                box2DComponent.box2DBodyBuilder = new Box2DBodyBuilder().fromBox2DBody(box2DComponent.body);
            } else if (box2DComponent.box2DBodyBuilder == null) {
                throw new NullPointerException("Both box2DBodyBuilder and Body fields of the box2DComponent are null!");
            }
        }

        return entityClone;
    }
}
