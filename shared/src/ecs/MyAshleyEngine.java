package ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hasanchik.shared.misc.Constants.MAX_ENTITIES;

public abstract class MyAshleyEngine extends PooledEngine {
    private static final Logger logger = LogManager.getLogger(MyAshleyEngine.class);;

    private final float fixedTimeStep;
    private long lastUpdateTime = System.nanoTime();
    private float accumulator;

    @Getter
    private final List<Entity> entityArrayList = new ArrayList<>(Collections.nCopies(MAX_ENTITIES, null));


    public void update() {
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000f;
        lastUpdateTime = currentTime;

        //Fixed simulation timestep
        accumulator += Math.min(0.25f, deltaTime);
        while (accumulator >= fixedTimeStep) {
            super.update(deltaTime);
            accumulator -= fixedTimeStep;
        }

        final float alpha = accumulator / fixedTimeStep;
    }

    public MyAshleyEngine(float fixedTimeStep) {
        super();

        this.fixedTimeStep = fixedTimeStep;

        this.addEntityListener(
                Family.all(Components.EntityComponent.class).get(),
                0,
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {

                    }

                    @Override
                    public void entityRemoved(Entity entity) {
                        int entityID = ComponentMappers.entityComponentMapper.get(entity).entityID;
                        entityArrayList.set(entityID, null);
                    }
                }
        );
    }

    private Components.EntityComponent getEntityComponent(Entity entity) {
        Components.EntityComponent entityComponent = ComponentMappers.entityComponentMapper.get(entity);
        if (entityComponent == null) {
            entity.add(new Components.EntityComponent());
            entityComponent = ComponentMappers.entityComponentMapper.get(entity);
        }
        return entityComponent;
    }

    @Override
    public void addEntity(Entity entity) {
        synchronized (entityArrayList) {
            int firstAvailableEntityID = -1;
            for (int i = 0; i < entityArrayList.size(); i++) {
                if (entityArrayList.get(i) == null) {
                    firstAvailableEntityID = i;
                    break;
                }
            }

            if (firstAvailableEntityID == -1) {
                logger.warn("No more slots available for entities! Ignoring new entity requests!");
                return;
            }

            Components.EntityComponent entityComponent = getEntityComponent(entity);
            entityComponent.entityID = firstAvailableEntityID;
            entityArrayList.set(firstAvailableEntityID, entity);
            super.addEntity(entity);
        }
    }

    public void addEntity(int entityID, Entity entity) {
        synchronized (entityArrayList) {
            Components.EntityComponent entityComponent = getEntityComponent(entity);
            entityComponent.entityID = entityID;

            if (entityArrayList.get(entityID) == null) {
                entityArrayList.set(entityID, entity);
            } else  {
                throw new IllegalArgumentException("Entity with entityID " + entityID + " already exists!");
            }
            super.addEntity(entity);
        }
    }
}
