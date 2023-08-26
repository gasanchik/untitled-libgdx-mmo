package com.hasanchik.shared.box2dutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilder;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilderDirector;
import com.hasanchik.shared.misc.BodyMap;
import com.hasanchik.shared.misc.BodyUserData;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hasanchik.shared.misc.Constants.MAX_ENTITIES;

@Getter
public class WorldHandler {
    private static final Logger logger = LogManager.getLogger(WorldHandler.class);

    private final float fixedTimeStep;

    private float accumulator;

    private final World world;
    private final WorldContactListener worldContactListener;

    @Setter
    private Vector2 currentGravity;
    private final boolean doSleep;
    private final int velocityIterations;
    private final int positionIterations;

    private final FixtureDef fixtureDef = new FixtureDef();
    private final JointDef jointDef = new JointDef();

    //index = bodyID
    //If I'm correct this is going to be called by three threads at once, which is the networking thread, the bodyReplication thread and the world updater thread
    //Use this list when iterating or getting bodies by ID
    private final List<Body> bodiesList = Collections.synchronizedList(new ArrayList<>(Collections.nCopies(MAX_ENTITIES, null)));
    //Use this hashmap when getting things in an area or in a position
    private final BodyMap bodyMap = new BodyMap(3f);

    private Body player;
    private long lastUpdateTime = System.nanoTime();

    private boolean refreshBodyMap = false;

    public WorldHandler(Vector2 gravity, boolean doSleep, int velocityIterations, int positionIterations, float fixedTimeStep, boolean refreshBodyMap) {
        this.world = new World(gravity, doSleep);

        this.currentGravity = gravity;
        this.doSleep = doSleep;
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;

        this.worldContactListener = new WorldContactListener();
        world.setContactListener(worldContactListener);

        this.fixedTimeStep = fixedTimeStep;

        this.refreshBodyMap = refreshBodyMap;
    }

    public WorldHandler(float fixedTimeStep, boolean refreshBodyMap) {
        this(new Vector2(0, 0), true, 6, 2, fixedTimeStep, refreshBodyMap);
    }
    //grav = -9.81f
    public void createTestScene() {
        //Create a circle
        Body body = putBodyInWorld(Box2DBodyBuilderDirector.getCircle(new Vector2(2.3f, 5), 3f));

        fixtureDef.isSensor = false;
        fixtureDef.restitution = 0.5f;
        fixtureDef.friction = 0.2f;
        fixtureDef.density = 1;
        fixtureDef.filter.set(CollisionType.COLLIDABLES.getAsFilter());
        ChainShape chainShape = new ChainShape();
        chainShape.createChain(new float[]{-0.5f, -0.7f, 0.5f, -0.7f});
        fixtureDef.shape = chainShape;
        body.createFixture(fixtureDef);
        chainShape.dispose();

        //Create a player
        body = putBodyInWorld(Box2DBodyBuilderDirector.getDefaultSquare());
        player = body;

        //Create platform
        body = putBodyInWorld(
            Box2DBodyBuilderDirector.getSquare(
                BodyDef.BodyType.StaticBody,
                new Vector2(0f, -5f),
                new Vector2(4f, 0.5f) //x = 2.9
            )
        );

        //Create a room
        body = putBodyInWorld(
            Box2DBodyBuilderDirector.getChain(
                BodyDef.BodyType.StaticBody,
                new Vector2(-4f, 7f),
                new Vector2[]{new Vector2(0f, 0f), new Vector2(8f, 0f), new Vector2(8f, -14f), new Vector2(0f, -14f), new Vector2(0f, 0f)}
            )
        );
    }

    public void step() {
        final float speedX;
        final float speedY;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            speedX = -4;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            speedX = 4;
        } else {
            speedX = 0;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            speedY = -4;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            speedY = 4;
        } else {
            speedY = 0;
        }

        if (player != null) {
            player.applyLinearImpulse(
                    (speedX - player.getLinearVelocity().x) * player.getMass(),
                    (speedY - player.getLinearVelocity().y) * player.getMass(),
                    player.getWorldCenter().x,
                    player.getWorldCenter().y,
                    true
            );

            if (Gdx.input.isKeyPressed(Input.Keys.R) && player.getUserData() != null) {
                removeBodyFromWorld(player);
            }
        }

        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000f;
        lastUpdateTime = currentTime;

        //Fixed simulation timestep
        accumulator += Math.min(0.25f, deltaTime);
        while (accumulator >= fixedTimeStep) {
            synchronized (world) {
                if (refreshBodyMap) {
                    bodyMap.refresh(world, true);
                }
                world.step(deltaTime, velocityIterations, positionIterations);
            }
            accumulator -= fixedTimeStep;
        }

        final float alpha = accumulator / fixedTimeStep;
    }

    public void dispose() {
        synchronized (world) {
            world.dispose();
        }
        //debugRenderer.dispose();
    }

    public void removeBodyFromWorld(Body body) {
        int bodyID;
        synchronized (world) {
            synchronized (body) {
                BodyUserData bodyUserData = (BodyUserData) body.getUserData();
                bodyID = bodyUserData.entityID;
                body.setUserData(null);
                world.destroyBody(body);
            }
        }
        bodiesList.set(bodyID, null);
    }

    public Body putBodyInWorld(Box2DBodyBuilder box2DBodyBuilder) {
        int firstAvailableBodyID = -1;
        for (int i = 0; i < bodiesList.size(); i++) {
            if (bodiesList.get(i) == null) {
                firstAvailableBodyID = i;
                break;
            }
        }

        if (firstAvailableBodyID == -1) {
            logger.warn("No more slots available for bodies! Ignoring new body request!");
            //throw new IllegalStateException("No more slots available for bodies!");
        }

        return putBodyInWorld(firstAvailableBodyID, box2DBodyBuilder);
    }

    public Body putBodyInWorld(int bodyID, Box2DBodyBuilder box2DBodyBuilder) {
        Body body;
        synchronized (world) {
            body = box2DBodyBuilder.build(world);
        }
        return putBodyInWorld(bodyID, body);
    }

    public Body putBodyInWorld(int bodyID, Body body) {
        BodyUserData bodyUserData;

        synchronized (body) {
            bodyUserData = (BodyUserData) body.getUserData();
            bodyUserData.entityID = bodyID;
        }

        if (bodiesList.get(bodyID) != null) {
            throw new IllegalArgumentException("A body with the same BodyID already exists! BodyID: " + bodyID);
        }

        bodiesList.set(bodyID, body);
        return body;
    }


    public World getWorld() {
        synchronized (world) {
            return world;
        }
    }
}
