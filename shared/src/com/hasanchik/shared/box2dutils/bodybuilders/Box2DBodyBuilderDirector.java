package com.hasanchik.shared.box2dutils.bodybuilders;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.hasanchik.shared.box2dutils.CollisionType;

public class Box2DBodyBuilderDirector {
    public static Box2DBodyBuilder getDefaultSquare() {
        return new Box2DBodyBuilder()
                .setBodyType(BodyDef.BodyType.DynamicBody)

                .addFixture()
                .attachShape(new PolygonShape(), shape -> {
                    PolygonShape polygonShape = (PolygonShape) shape;
                    polygonShape.setAsBox(0.5f, 0.5f);
                })

                .setDensity(7f)
                .setRestitution(0.45f)
                .setFilterData(CollisionType.COLLIDABLES.getAsFilter())
                .finish()

                .finish();

    }

    public static Box2DBodyBuilder getSquare(BodyDef.BodyType bodyType, Vector2 position, Vector2 size) {
        return new Box2DBodyBuilder()
                .setPosition(position)
                .setBodyType(bodyType)

                .addFixture()
                .attachShape(new PolygonShape(), shape -> {
                    PolygonShape polygonShape = (PolygonShape) shape;
                    polygonShape.setAsBox(size.x, size.y);
                })

                .setDensity(7f)
                .setRestitution(0.45f)
                .setFilterData(CollisionType.COLLIDABLES.getAsFilter())
                .finish()

                .finish();

    }

    public static Box2DBodyBuilder getPolygon(BodyDef.BodyType bodyType, Vector2 position, Vector2[] vertices) {
        return new Box2DBodyBuilder()
                .setPosition(position)
                .setBodyType(bodyType)

                .addFixture()
                .attachShape(new PolygonShape(), shape -> {
                    PolygonShape polygonShape = (PolygonShape) shape;
                    polygonShape.set(vertices);
                })

                .setDensity(7f)
                .setRestitution(0.45f)
                .setFilterData(CollisionType.COLLIDABLES.getAsFilter())
                .finish()

                .finish();

    }

    public static Box2DBodyBuilder getChain(BodyDef.BodyType bodyType, Vector2 position, Vector2[] vertices) {
        return new Box2DBodyBuilder()
                .setPosition(position)
                .setBodyType(BodyDef.BodyType.StaticBody)

                .addFixture()
                .attachShape(new ChainShape(), shape -> {
                    ChainShape chainShape = (ChainShape) shape;
                    chainShape.createChain(vertices);
                })

                .setDensity(7f)
                .setRestitution(0.45f)
                .setFilterData(CollisionType.COLLIDABLES.getAsFilter())
                .finish()

                .finish();

    }

    public static Box2DBodyBuilder getCircle(Vector2 position, float radius) {
        return new Box2DBodyBuilder()
                .setBodyType(BodyDef.BodyType.DynamicBody)

                .addFixture()
                .attachShape(new CircleShape(), shape -> {
                    CircleShape circleShape = (CircleShape) shape;
                    circleShape.setRadius(radius);
                    circleShape.setPosition(position);
                })

                .setDensity(7f)
                .setRestitution(0.5f)
                .setFilterData(CollisionType.COLLIDABLES.getAsFilter())
                .finish()

                .finish();

    }
}
