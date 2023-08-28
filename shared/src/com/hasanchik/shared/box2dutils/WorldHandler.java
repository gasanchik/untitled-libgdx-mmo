package com.hasanchik.shared.box2dutils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.google.gson.Gson;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilder;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilderDirector;
import com.hasanchik.shared.ecs.Components;
import com.hasanchik.shared.ecs.MyAshleyEngine;
import com.hasanchik.shared.misc.BodyUserData;
import com.hasanchik.shared.misc.FixedTimeStepExecutor;
import com.hasanchik.shared.misc.serializers.jsonserializers.EntityJsonSerializer;
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

    private Body player;

    private final FixedTimeStepExecutor fixedTimeStepExecutor;

    public WorldHandler(Vector2 gravity, boolean doSleep, int velocityIterations, int positionIterations, float fixedTimeStep) {
        this.world = new World(gravity, doSleep);

        this.currentGravity = gravity;
        this.doSleep = doSleep;
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;

        this.worldContactListener = new WorldContactListener();
        world.setContactListener(worldContactListener);

        this.fixedTimeStepExecutor = new FixedTimeStepExecutor(fixedTimeStep);
    }

    public WorldHandler(float fixedTimeStep) {
        this(new Vector2(0, -9.81f), true, 6, 2, fixedTimeStep);
    }
    //grav = -9.81f
    public void createTestScene(MyAshleyEngine myAshleyEngine) {
        //Create a circle
        Box2DBodyBuilder box2DBodyBuilder = Box2DBodyBuilderDirector.getCircle(new Vector2(2.3f, 5), 3f);
        fixtureDef.isSensor = false;
        fixtureDef.restitution = 0.5f;
        fixtureDef.friction = 0.2f;
        fixtureDef.density = 1;
        fixtureDef.filter.set(CollisionType.COLLIDABLES.getAsFilter());
        ChainShape chainShape = new ChainShape();
        chainShape.createChain(new float[]{-0.5f, -0.7f, 0.5f, -0.7f});
        fixtureDef.shape = chainShape;
        box2DBodyBuilder.addFixture(fixtureDef);
        box2DBodyBuilder.finish();

        Entity entity = new Entity();
        Components.Box2DComponent box2DComponent = new Components.Box2DComponent();
        box2DComponent.box2DBodyBuilder = box2DBodyBuilder;
        entity.add(box2DComponent);

        Gson gson = EntityJsonSerializer.getGsonBuilder().create();
        String json = gson.toJson(entity);
        logger.info(json);
        entity = gson.fromJson(json, Entity.class);

        myAshleyEngine.addEntity(entity);

        Body body = putBodyInWorld(Box2DBodyBuilderDirector.getDefaultSquare());
        entity = new Entity();
        box2DComponent = new Components.Box2DComponent();
        box2DComponent.body = body;
        entity.add(box2DComponent);
        myAshleyEngine.addEntity(entity);
        //Create a player
        player = body;


        //Create platform
        body = putBodyInWorld(
            Box2DBodyBuilderDirector.getSquare(
                BodyDef.BodyType.StaticBody,
                new Vector2(0f, -5f),
                new Vector2(4f, 0.5f) //x = 2.9
            )
        );
        entity = new Entity();
        box2DComponent = new Components.Box2DComponent();
        box2DComponent.body = body;
        entity.add(box2DComponent);
        myAshleyEngine.addEntity(entity);

        //Create a room
        body = putBodyInWorld(
            Box2DBodyBuilderDirector.getChain(
                BodyDef.BodyType.StaticBody,
                new Vector2(-4f, 7f),
                new Vector2[]{new Vector2(0f, 0f), new Vector2(8f, 0f), new Vector2(8f, -14f), new Vector2(0f, -14f), new Vector2(0f, 0f)}
            )
        );
        entity = new Entity();
        box2DComponent = new Components.Box2DComponent();
        box2DComponent.body = body;
        entity.add(box2DComponent);
        myAshleyEngine.addEntity(entity);
    }

    public void update() {
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


        fixedTimeStepExecutor.update(deltaTime -> {
            synchronized (world) {
                world.step(deltaTime, velocityIterations, positionIterations);
            }
        });
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
        Body body = box2DBodyBuilder.build(world);
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
