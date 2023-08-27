package com.hasanchik.shared.box2dutils.bodybuilders;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.hasanchik.shared.misc.BodyUserData;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

//A class to conveniently build Box2D bodies, while also allowing kryo to easily serialize it
//Yes, I could make a custom serializer for that, but why not kill two birds with one stone?

//Here's where my obsession with chained methods started
@Getter
@ToString
public class Box2DBodyBuilder {
    private static final Logger logger = LogManager.getLogger(Box2DBodyBuilder.class);

    //TODO: Make a builder interface/abstract class
    //TODO: add support for joints
    private final BodyDef bodyDef = new BodyDef();
    private BodyUserData userData;
    private final ArrayList<Box2DFixtureBuilder> fixtureBuilders = new ArrayList<>();
    private JointDef[] joints;

    public Box2DBodyBuilder() {
        setUserData(new BodyUserData());
    }

    public Box2DFixtureBuilder addFixture() {
        Box2DFixtureBuilder box2DFixtureBuilder = new Box2DFixtureBuilder(this);
        fixtureBuilders.add(box2DFixtureBuilder);
        return box2DFixtureBuilder;
    }

    public Box2DFixtureBuilder addFixture(FixtureDef fixtureDef) {
        Box2DFixtureBuilder box2DFixtureBuilder = new Box2DFixtureBuilder(this);
        box2DFixtureBuilder.fromBox2DFixtureDef(fixtureDef);
        fixtureBuilders.add(box2DFixtureBuilder);
        return box2DFixtureBuilder;
    }


    public Box2DBodyBuilder fromBox2DBody(Body body) {
        synchronized (body) {
            this.setUserData((BodyUserData) body.getUserData())
                    .setBodyType(body.getType())
                    .setPosition(body.getPosition())
                    .setAngle(body.getAngle())
                    .setLinearVelocity(body.getLinearVelocity())
                    .setAngularVelocity(body.getAngularVelocity())
                    .setAngularDamping(body.getAngularDamping())
                    .setAllowSleep(body.isSleepingAllowed())
                    .setAwake(body.isAwake())
                    .setFixedRotation(body.isFixedRotation())
                    .setIsBullet(body.isBullet())
                    .setGravityScale(body.getGravityScale());
            Array<Fixture> fixtureList = body.getFixtureList();
            fixtureList.forEach(fixture -> {
                Box2DFixtureBuilder box2DFixtureBuilder = new Box2DFixtureBuilder();
                box2DFixtureBuilder.fromBox2DFixture(fixture);
                fixtureBuilders.add(box2DFixtureBuilder);
            });
        }
        return this;
    }

    public Body build(World world) {
        synchronized (world) {
            Body body = world.createBody(bodyDef);
            body.setUserData(userData);
            fixtureBuilders.forEach(fixtureBuilder -> fixtureBuilder.build(body));
            return body;
        }
    }

    public Box2DBodyBuilder finish() {
        fixtureBuilders.forEach(Box2DFixtureBuilder::makeReadyForSerialization);
        return this;
    }

    public Box2DBodyBuilder setUserData(BodyUserData userData) {
        this.userData = userData;
        return this;
    }

    public Box2DBodyBuilder setBodyType(BodyDef.BodyType bodyType) {
        this.bodyDef.type = bodyType;
        return this;
    }

    public Box2DBodyBuilder setPosition(Vector2 position) {
        this.bodyDef.position.set(position);
        return this;
    }

    public Box2DBodyBuilder setAngle(float angle) {
        this.bodyDef.angle = angle;
        return this;
    }

    public Box2DBodyBuilder setLinearVelocity(Vector2 linearVelocity) {
        this.bodyDef.linearVelocity.set(linearVelocity);
        return this;
    }

    public Box2DBodyBuilder setAngularVelocity(float angularVelocity) {
        this.bodyDef.angularVelocity = angularVelocity;
        return this;
    }

    public Box2DBodyBuilder setAngularDamping(float angularDamping) {
        this.bodyDef.angularDamping = angularDamping;
        return this;
    }

    public Box2DBodyBuilder setAllowSleep(boolean allowSleep) {
        this.bodyDef.allowSleep = allowSleep;
        return this;
    }

    public Box2DBodyBuilder setAwake(boolean awake) {
        this.bodyDef.awake = awake;
        return this;
    }

    public Box2DBodyBuilder setFixedRotation(boolean fixedRotation) {
        this.bodyDef.fixedRotation = fixedRotation;
        return this;
    }

    public Box2DBodyBuilder setIsBullet(boolean isBullet) {
        this.bodyDef.bullet = isBullet;
        return this;
    }

    public Box2DBodyBuilder setGravityScale(float gravityScale) {
        this.bodyDef.gravityScale = gravityScale;
        return this;
    }
}
