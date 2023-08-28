package com.hasanchik.shared.ecs;

import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Connection;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilder;
import com.hasanchik.shared.map.MapLayer;
import com.hasanchik.shared.networking.MyComponent;

public interface Components {

    class Box2DComponent extends MyComponent {
        public transient Body body;
        public Box2DBodyBuilder box2DBodyBuilder; //Don't be fooled, this is the object to write to when serializing this component
        //The bodyID is practically equal to the entityID

        public Box2DComponent() {}

        public Box2DComponent(Body body) {
            this.body = body;
        }

        public Box2DComponent(Body body, Box2DBodyBuilder box2DBodyBuilder) {
            this.body = body;
            this.box2DBodyBuilder = box2DBodyBuilder;
        }

        @Override
        public void reset() {
            if (body == null) {
                return;
            }

            body.getWorld().destroyBody(body);
            body = null;
        }

        @Override
        public Box2DComponent clone() {
            return new Box2DComponent(body, box2DBodyBuilder);
        }
    }

    //Component that every entity has
    class EntityComponent extends MyComponent {
        public int entityID = -1;
        public MapLayer layer = null;
        //Instead of serializing and sending the full entity over the air, send a simple entity which (mostly) only contains the entityComponent
        //and use a shared copy of the entity to still be synchronized.
        //Only works if the properties of the entity haven't changed (too much)
        //This is the index in the json file of that shared entity
        //-1 means that it has changed too much to be synchronized
        public int sharedEntityIndex = -1;


        public EntityComponent() {}

        public EntityComponent(int entityID, MapLayer mapLayer) {
            this.entityID = entityID;
            this.layer = mapLayer;
        }

        @Override
        public void reset() {
            entityID = -1;
            layer = null;
        }

        @Override
        public EntityComponent clone() {
            return new EntityComponent(entityID, layer);
        }
    }

    class PlayerComponent extends MyComponent {
        public String playerName;
        public Connection connection;

        public PlayerComponent() {}

        public PlayerComponent(String playerName, Connection connection) {
            this.playerName = playerName;
            this.connection = connection;
        }

        @Override
        public void reset() {
            playerName = null;
            connection = null;
        }

        @Override
        public PlayerComponent clone() {
            return new PlayerComponent(playerName, connection);
        }
    }
}
