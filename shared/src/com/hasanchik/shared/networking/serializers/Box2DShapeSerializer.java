package com.hasanchik.shared.networking.serializers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.IntStream;

public interface Box2DShapeSerializer  {
    //TODO: implement all of the shapes
    static final Logger logger = LogManager.getLogger(Box2DShapeSerializer.class);

    class CircleShapeSerializer extends Serializer<CircleShape> implements Box2DShapeSerializer {

        @Override
        public void write(Kryo kryo, Output output, CircleShape circleShape) {
            kryo.writeObject(output, circleShape.getRadius());
            kryo.writeObject(output, circleShape.getPosition());
        }

        @Override
        public CircleShape read(Kryo kryo, Input input, Class<CircleShape> type) {
            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(kryo.readObject(input, float.class));
            circleShape.setPosition(kryo.readObject(input, Vector2.class));

            return circleShape;
        }
    }

    class PolygonShapeSerializer extends Serializer<PolygonShape> implements Box2DShapeSerializer {

        @Override
        public void write(Kryo kryo, Output output, PolygonShape polygonShape) {
            kryo.writeObject(output, polygonShape.getRadius());
            float[] vertices = new float[polygonShape.getVertexCount()*2];
            IntStream.range(0, polygonShape.getVertexCount())
                    .forEach(i -> {
                        Vector2 vector2 = new Vector2();
                        polygonShape.getVertex(i, vector2);
                        vertices[i*2] = vector2.x;
                        vertices[i*2+1] = vector2.y;
                    });
            kryo.writeObject(output, vertices);
        }

        @Override
        public PolygonShape read(Kryo kryo, Input input, Class<PolygonShape> type) {
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setRadius(kryo.readObject(input, float.class));
            polygonShape.set(kryo.readObject(input, float[].class));

            return polygonShape;
        }
    }

    class EdgeShapeSerializer extends Serializer<EdgeShape> implements Box2DShapeSerializer {

        @Override
        public void write(Kryo kryo, Output output, EdgeShape edgeShape) {

        }

        @Override
        public EdgeShape read(Kryo kryo, Input input, Class<EdgeShape> type) {
            return null;
        }
    }

    class ChainShapeSerializer extends Serializer<ChainShape> implements Box2DShapeSerializer {

        @Override
        public void write(Kryo kryo, Output output, ChainShape chainShape) {
            kryo.writeObject(output, chainShape.getRadius());
            kryo.writeObject(output, chainShape.isLooped());
            float[] vertices = new float[chainShape.getVertexCount()*2];
            IntStream.range(0, chainShape.getVertexCount())
                    .forEach(i -> {
                        Vector2 vector2 = new Vector2();
                        chainShape.getVertex(i, vector2);
                        vertices[i*2] = vector2.x;
                        vertices[i*2+1] = vector2.y;
                    });
            kryo.writeObject(output, vertices);
        }

        @Override
        public ChainShape read(Kryo kryo, Input input, Class<ChainShape> type) {
            ChainShape chainShape = new ChainShape();
            chainShape.setRadius(kryo.readObject(input, float.class));
            if (kryo.readObject(input, boolean.class)) {
                chainShape.createLoop(kryo.readObject(input, float[].class));;
            } else {
                chainShape.createChain(kryo.readObject(input, float[].class));;
            }

            return chainShape;
        }
    }
}
