package ecs;

import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Connection;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilder;
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
            return new Box2DComponent(body);
        }
    }

    //Component that every entity has
    class EntityComponent extends MyComponent {
        public int entityID = -1;

        public EntityComponent() {}

        public EntityComponent(int entityID) {
            this.entityID = entityID;
        }

        @Override
        public void reset() {
            entityID = -1;
        }

        @Override
        public EntityComponent clone() {
            return new EntityComponent(entityID);
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
