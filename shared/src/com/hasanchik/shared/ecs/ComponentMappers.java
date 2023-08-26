package com.hasanchik.shared.ecs;

import com.badlogic.ashley.core.ComponentMapper;

public class ComponentMappers {
    public final static ComponentMapper<Components.Box2DComponent> box2DComponentMapper = ComponentMapper.getFor(Components.Box2DComponent.class);
    public final static ComponentMapper<Components.PlayerComponent> playerComponentMapper = ComponentMapper.getFor(Components.PlayerComponent.class);
    public final static ComponentMapper<Components.EntityComponent> entityComponentMapper = ComponentMapper.getFor(Components.EntityComponent.class);
}
