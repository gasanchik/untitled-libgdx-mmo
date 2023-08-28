package com.hasanchik.shared.misc.serializers.jsonserializers;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.google.gson.*;
import com.hasanchik.shared.misc.serializers.AbstractEntitySerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;

public class EntityJsonSerializer extends AbstractEntitySerializer implements JsonSerializerInterface {
    private static final Logger logger = LogManager.getLogger(EntityJsonSerializer.class);

    public static GsonBuilder getGsonBuilder() {
        return Box2DShapeJsonSerializer.getGsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Entity.class, new EntityJsonSerializer.Serializer())
                .registerTypeAdapter(Entity.class, new EntityJsonSerializer.Deserializer());
    }

    private static class Serializer implements JsonSerializer<Entity> {

        @Override
        public JsonElement serialize(Entity entity, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray components = new JsonArray();
            AbstractEntitySerializer
                    .makeReadyForSerialization(entity)
                    .getComponents()
                    .forEach(component -> {
                        JsonObject componentData = new JsonObject();
                        componentData.addProperty("class", component.getClass().getName());
                        componentData.add("data", context.serialize(component));
                        components.add(componentData);
                    });
            return components;
        }
    }

    private static class Deserializer implements JsonDeserializer<Entity> {

        @Override
        public Entity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Entity entity = new Entity();

            for (JsonElement element : json.getAsJsonArray()) {
                JsonObject jsonObject = element.getAsJsonObject();
                String type = jsonObject.get("class").getAsString();
                try {
                    Class<?> clazz = Class.forName(type);
                    Component component = context.deserialize(jsonObject.get("data"), clazz);
                    entity.add(component);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            return entity;
        }
    }
}
