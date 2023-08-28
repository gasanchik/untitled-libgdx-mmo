package com.hasanchik.shared.misc.serializers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class EntitySerializer  extends Serializer<Entity> {
    private static final Logger logger = LogManager.getLogger(EntitySerializer.class);

    @Override
    public void write(Kryo kryo, Output output, Entity entity) {
        ArrayList<Component> components = new ArrayList<>();
        AbstractEntitySerializer
                .makeReadyForSerialization(entity)
                .getComponents()
                .forEach(components::add);

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
