package com.hasanchik.shared.misc.serializers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.stream.IntStream;

public class Box2DShapeJsonSerializer {
    //TODO: implement edge shape
    private static final Logger logger = LogManager.getLogger(Box2DShapeJsonSerializer.class);

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Shape.class, new ShapeDeserializer())
                .registerTypeAdapter(CircleShape.class, new CircleShapeDeserializer())
                .registerTypeAdapter(CircleShape.class, new CircleShapeSerializer())
                .registerTypeAdapter(PolygonShape.class, new PolygonShapeDeserializer())
                .registerTypeAdapter(PolygonShape.class, new PolygonShapeSerializer())
                .registerTypeAdapter(ChainShape.class, new ChainShapeDeserializer())
                .registerTypeAdapter(ChainShape.class, new ChainShapeSerializer());
        return gsonBuilder.create();
    }

    public static class ShapeDeserializer implements JsonDeserializer<Shape> {
        @Override
        public Shape deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.get("shapeType").getAsString();
            return switch (type) {
                case "circle" -> context.deserialize(json, CircleShape.class);
                case "polygon" -> context.deserialize(json, PolygonShape.class);
                case "chain" -> context.deserialize(json, ChainShape.class);
                default -> throw new IllegalArgumentException("Unsupported shape type: " + type);
            };
        }
    }

    private static class CircleShapeSerializer implements JsonSerializer<CircleShape> {
        @Override
        public JsonElement serialize(CircleShape circleShape, Type sourceType, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("shapeType", "circle");
            jsonObject.addProperty("radius", circleShape.getRadius());
            jsonObject.add("position", context.serialize(circleShape.getPosition()));
            return jsonObject;
        }
    }

    private static class CircleShapeDeserializer implements JsonDeserializer<CircleShape> {
        @Override
        public CircleShape deserialize(JsonElement json, Type typeToDeserializeInto, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(jsonObject.get("radius").getAsFloat());
            circleShape.setPosition(context.deserialize(jsonObject.get("position"), Vector2.class));
            return circleShape;
        }
    }

    private static class PolygonShapeSerializer implements JsonSerializer<PolygonShape> {
        @Override
        public JsonElement serialize(PolygonShape polygonShape, Type sourceType, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("shapeType", "polygon");
            jsonObject.addProperty("radius", polygonShape.getRadius());
            float[] vertices = new float[polygonShape.getVertexCount() * 2];
            IntStream.range(0, polygonShape.getVertexCount())
                    .forEach(i -> {
                        Vector2 vector2 = new Vector2();
                        polygonShape.getVertex(i, vector2);
                        vertices[i * 2] = vector2.x;
                        vertices[i * 2 + 1] = vector2.y;
                    });
            jsonObject.add("vertices", context.serialize(vertices));
            return jsonObject;
        }
    }

    private static class PolygonShapeDeserializer implements JsonDeserializer<PolygonShape> {
        @Override
        public PolygonShape deserialize(JsonElement json, Type typeToDeserializeInto, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setRadius(jsonObject.get("radius").getAsFloat());
            polygonShape.set((Vector2[]) context.deserialize(jsonObject.get("vertices"), float[].class));
            return polygonShape;
        }
    }

    private static class ChainShapeSerializer implements JsonSerializer<ChainShape> {
        @Override
        public JsonElement serialize(ChainShape chainShape, Type sourceType, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("shapeType", "chain");
            jsonObject.addProperty("radius", chainShape.getRadius());
            float[] vertices = new float[chainShape.getVertexCount() * 2];
            IntStream.range(0, chainShape.getVertexCount())
                    .forEach(i -> {
                        Vector2 vector2 = new Vector2();
                        chainShape.getVertex(i, vector2);
                        vertices[i * 2] = vector2.x;
                        vertices[i * 2 + 1] = vector2.y;
                    });
            jsonObject.add("vertices", context.serialize(vertices));
            jsonObject.addProperty("isLoop", chainShape.isLooped());
            return jsonObject;
        }
    }

    private static class ChainShapeDeserializer implements JsonDeserializer<ChainShape> {
        @Override
        public ChainShape deserialize(JsonElement json, Type typeToDeserializeInto, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            ChainShape chainShape = new ChainShape();
            chainShape.setRadius(jsonObject.get("radius").getAsFloat());
            float[] vertices = context.deserialize(jsonObject.get("vertices"), float[].class);
            if (jsonObject.get("isLoop").getAsBoolean()) {
                chainShape.createLoop(vertices);
            } else {
                chainShape.createChain(vertices);
            }
            return chainShape;
        }
    }
}