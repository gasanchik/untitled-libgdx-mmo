package com.hasanchik.shared.misc.serializers.jsonserializers;

import com.google.gson.*;
import com.hasanchik.shared.map.InfiniteEntityMap;

import java.lang.reflect.Type;

public class InfiniteEntityMapJsonSerializer implements JsonSerializerInterface {
    public static GsonBuilder getGsonBuilder() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(InfiniteEntityMap.class, new Serializer())
                .registerTypeAdapter(InfiniteEntityMap.class, new Deserializer());
    }

    private static class Serializer implements JsonSerializer<InfiniteEntityMap> {
        @Override
        public JsonElement serialize(InfiniteEntityMap infiniteEntityMap, Type typeOfSrc, JsonSerializationContext context) {
            return null;
        }
    }

    private static class Deserializer implements JsonDeserializer<InfiniteEntityMap> {
        @Override
        public InfiniteEntityMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return null;
        }
    }
}
