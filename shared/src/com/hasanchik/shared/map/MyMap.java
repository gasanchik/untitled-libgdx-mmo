package com.hasanchik.shared.map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hasanchik.shared.box2dutils.WorldHandler;
import com.hasanchik.shared.ecs.ComponentMappers;
import com.hasanchik.shared.ecs.Components;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;

import static com.hasanchik.shared.misc.Constants.MAX_ENTITIES;

@Getter
public class MyMap extends Map {
    //TODO: implement this class
    private Array<? extends Disposable> ownedResources;

    //Ordered, all entities
    private final ArrayList<Entity> entityArrayList = new ArrayList<>(Collections.nCopies(MAX_ENTITIES, null));
    //Yes, i know, most of the indexes of this array are not initialized
    private final ArrayList<Entity>[] mapLayers = new ArrayList[MapLayer.values().length];
    //Use this hashmap when getting things in an area or in a position
    private final BodyMap bodyMap = new BodyMap(3f);
    private final WorldHandler worldHandler;

    public MyMap(WorldHandler worldHandler) {
        this.worldHandler = worldHandler;

        for (int i = 0; i < mapLayers.length; i++) {
            mapLayers[i] = new ArrayList<Entity>(Collections.nCopies(MAX_ENTITIES, null));
        }
    }

    public void update() {
        World world = worldHandler.getWorld();
        synchronized (world) {
            bodyMap.refresh(world);
        }
        worldHandler.update();
    }

    public void setOwnedResources (Array<? extends Disposable> resources) {
        this.ownedResources = resources;
    }

    public synchronized void setEntity(Entity entity) {
        Components.EntityComponent entityComponent = ComponentMappers.entityComponentMapper.get(entity);
        entityArrayList.set(entityComponent.entityID, entity);
        mapLayers[entityComponent.layer.getLayer()].set(entityComponent.entityID, entity);
    }

    public synchronized void removeEntity(Entity entity) {
        Components.EntityComponent entityComponent = ComponentMappers.entityComponentMapper.get(entity);
        entityArrayList.set(entityComponent.entityID, null);
        mapLayers[entityComponent.layer.getLayer()].set(entityComponent.entityID, null);
    }

    @Override
    public void dispose () {
        worldHandler.dispose();
        if (ownedResources != null) {
            for (Disposable resource : ownedResources) {
                resource.dispose();
            }
        }
    }
}
