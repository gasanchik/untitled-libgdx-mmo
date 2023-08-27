package com.hasanchik.shared.box2dutils.bodybuilders;

import com.badlogic.gdx.physics.box2d.*;
import com.hasanchik.shared.misc.BodyUserData;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

@Getter
@ToString
public class Box2DFixtureBuilder {
    private static final Logger logger = LogManager.getLogger(Box2DFixtureBuilder.class);

    public FixtureDef fixtureDef = new FixtureDef();
    public Box2DBodyBuilder box2DBodyBuilder;
    public BodyUserData userData = null;

    public Box2DFixtureBuilder() {}

    public Box2DFixtureBuilder(Box2DBodyBuilder box2DBodyBuilder) {
        this.box2DBodyBuilder = box2DBodyBuilder;
    }

    public Box2DFixtureBuilder fromBox2DFixture(Fixture fixture) {

        return setShape(fixture.getShape())
                .setFriction(fixture.getFriction())
                .setRestitution(fixture.getRestitution())
                .setDensity(fixture.getDensity())
                .setIsSensor(fixture.isSensor())
                .setFilterData(fixture.getFilterData());
    }

    public Box2DFixtureBuilder fromBox2DFixtureDef(FixtureDef fixtureDef) {

        return setShape(fixtureDef.shape)
                .setFriction(fixtureDef.friction)
                .setRestitution(fixtureDef.restitution)
                .setDensity(fixtureDef.density)
                .setIsSensor(fixtureDef.isSensor)
                .setFilterData(fixtureDef.filter);
    }

    public Fixture build(Body body) {
        synchronized (body) {
            Fixture fixture = body.createFixture(fixtureDef);
            fixture.setUserData(userData);
            fixtureDef.shape.dispose();
            return fixture;
        }
    }

    public Box2DBodyBuilder finish() {
        return box2DBodyBuilder;
    }

    public void makeReadyForSerialization() {
        box2DBodyBuilder = null;
    }

    private Box2DFixtureBuilder setShape(Shape shape) {
        fixtureDef.shape = shape;
        return this;
    }

    public Box2DFixtureBuilder attachShape(Shape shape, Consumer<? super Shape> action) {
        action.accept(shape);
        return setShape(shape);
    }

    public Box2DFixtureBuilder setFriction(float friction) {
        this.fixtureDef.friction = friction;
        return this;
    }

    public Box2DFixtureBuilder setRestitution(float restitution) {
        this.fixtureDef.restitution = restitution;
        return this;
    }

    public Box2DFixtureBuilder setDensity(float density) {
        this.fixtureDef.density = density;
        return this;
    }

    public Box2DFixtureBuilder setIsSensor(boolean isSensor) {
        this.fixtureDef.isSensor = isSensor;
        return this;
    }

    public Box2DFixtureBuilder setFilterData(Filter filter) {
        this.fixtureDef.filter.set(filter);
        return this;
    }
}