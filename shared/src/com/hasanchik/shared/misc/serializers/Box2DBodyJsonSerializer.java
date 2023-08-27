package com.hasanchik.shared.misc.serializers;

import com.badlogic.gdx.physics.box2d.*;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;

public class Box2DBodyJsonSerializer {
    private static final Logger logger = LogManager.getLogger(Box2DBodyJsonSerializer.class);
    private static final Gson gson = new Gson();

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Shape.class, new ShapeDeserializer());
        gsonBuilder.registerTypeAdapter(Shape.class, new ShapeSerializer());
        return gsonBuilder.create();
    }

    private static class ShapeDeserializer implements JsonDeserializer<Shape> {

        @Override
        public Shape deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Shape.Type shapeType = Shape.Type.values()[jsonObject.get("shapeType").getAsInt()];
            Shape shape = null;
            switch (shapeType) {
                case Circle ->
                    shape = new CircleShape();
                case Polygon ->
                    shape = new PolygonShape();
                case Edge ->
                    shape = new EdgeShape();
                case Chain ->
                    shape = new ChainShape();
            }
            return gson.fromJson(json, shape.getClass());
        }
    }

    private static class ShapeSerializer implements JsonSerializer<Shape> {

        @Override
        public JsonElement serialize(Shape shape, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = context.serialize(shape).getAsJsonObject();
            jsonObject.addProperty("shapeType", shape.getType().ordinal());
            return jsonObject;
        }
    }
}
