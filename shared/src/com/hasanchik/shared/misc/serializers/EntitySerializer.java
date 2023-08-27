package com.hasanchik.shared.misc.serializers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilder;
import com.hasanchik.shared.ecs.ComponentMappers;
import com.hasanchik.shared.ecs.Components;
import com.hasanchik.shared.networking.MyComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class EntitySerializer extends Serializer<Entity> {
    private static final Logger logger = LogManager.getLogger(EntitySerializer.class);

    @Override
    public void write(Kryo kryo, Output output, Entity entity) {
        Entity entityClone = new Entity();
        entity.getComponents().forEach(component -> {
            MyComponent myComponent = (MyComponent) component;
            try {
                entityClone.add((Component) myComponent.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        });

        if (ComponentMappers.playerComponentMapper.has(entity)) {
            entityClone.remove(Components.PlayerComponent.class);
        }
        Components.Box2DComponent box2DComponent = ComponentMappers.box2DComponentMapper.get(entityClone);
        if (box2DComponent != null) {
            box2DComponent.box2DBodyBuilder = new Box2DBodyBuilder().fromBox2DBody(box2DComponent.body);
        }

        ArrayList<Component> components = new ArrayList<>();
        entityClone.getComponents().forEach(components::add);

        kryo.writeObject(output, components);
    }

    @Override
    public Entity read(Kryo kryo, Input input, Class<Entity> type) {
        Entity entity = new Entity();
        ArrayList<Component> components = kryo.readObject(input, ArrayList.class);
        components.forEach(entity::add);

        return entity;
    }
}
