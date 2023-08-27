package com.hasanchik.shared.ecs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.hasanchik.shared.map.InfiniteEntityMap;
import com.hasanchik.shared.map.MapLayer;
import com.hasanchik.shared.misc.FixedTimeStepExecutor;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Getter
public abstract class MyAshleyEngine extends PooledEngine {
    private static final Logger logger = LogManager.getLogger(MyAshleyEngine.class);

    private final FixedTimeStepExecutor fixedTimeStepExecutor;

    private final InfiniteEntityMap map;

    public void update() {
        fixedTimeStepExecutor.update(super::update);
    }

    public MyAshleyEngine(float fixedTimeStep, InfiniteEntityMap map) {
        super();

        this.fixedTimeStepExecutor = new FixedTimeStepExecutor(fixedTimeStep);

        this.map = map;

        this.addEntityListener(
                Family.all(Components.EntityComponent.class).get(),
                0,
                new EntityListener() {
                    @Override
                    public void entityAdded(Entity entity) {

                    }

                    @Override
                    public void entityRemoved(Entity entity) {
                        map.removeEntity(entity);
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
        int firstAvailableEntityID = -1;
        ArrayList<Entity> entityArrayList = map.getEntityArrayList();
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
        entityComponent.layer = MapLayer.FOREGROUND;
        map.setEntity(entity);
        super.addEntity(entity);
    }

    public void addEntity(int entityID, Entity entity) {
        Components.EntityComponent entityComponent = getEntityComponent(entity);
        entityComponent.entityID = entityID;
        entityComponent.layer = MapLayer.FOREGROUND;

        if (map.getEntityArrayList().get(entityID) == null) {
            map.setEntity(entity);
        } else  {
            throw new IllegalArgumentException("Entity with entityID " + entityID + " already exists!");
        }
        super.addEntity(entity);
    }
}
