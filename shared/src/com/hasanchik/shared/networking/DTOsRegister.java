package com.hasanchik.shared.networking;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DBodyBuilder;
import com.hasanchik.shared.box2dutils.bodybuilders.Box2DFixtureBuilder;
import com.hasanchik.shared.ecs.Components;
import com.hasanchik.shared.misc.BodyUserData;
import com.hasanchik.shared.misc.serializers.Box2DShapeSerializer;
import com.hasanchik.shared.misc.serializers.EntitySerializer;

import java.util.ArrayList;


public class DTOsRegister {
    private final Kryo kryoObject;

    public DTOsRegister(EndPoint endPoint) {
        this.kryoObject = endPoint.getKryo();
    }

    public void registerDTOs() {
        //Primitive classes
        kryoObject.register(float[].class);
        kryoObject.register(int[].class);
        kryoObject.register(ArrayList.class);
        kryoObject.register(Object[].class);
        //Box2d

        kryoObject.register(Box2DBodyBuilder.class);
        kryoObject.register(Box2DFixtureBuilder.class);

        kryoObject.register(BodyDef.class);
        kryoObject.register(FixtureDef.class);

        kryoObject.register(BodyDef.BodyType.class);
        kryoObject.register(Shape.Type.class);
        kryoObject.register(Shape.class);
        kryoObject.register(PolygonShape.class, new Box2DShapeSerializer.PolygonShapeSerializer());
        kryoObject.register(CircleShape.class, new Box2DShapeSerializer.CircleShapeSerializer());
        kryoObject.register(EdgeShape.class, new Box2DShapeSerializer.EdgeShapeSerializer());
        kryoObject.register(ChainShape.class, new Box2DShapeSerializer.ChainShapeSerializer());

        kryoObject.register(Filter.class);
        kryoObject.register(Transform.class);
        kryoObject.register(Vector2.class);

        kryoObject.register(BodyUserData.class);

        //Components & ashley
        kryoObject.register(Entity.class, new EntitySerializer());

        kryoObject.register(Components.class);
        kryoObject.register(Components.Box2DComponent.class);
        kryoObject.register(Components.EntityComponent.class);

        //Packets
        kryoObject.register(Packets.LogonRequestPacket.class);
        kryoObject.register(Packets.LogonResponsePacket.class);
        kryoObject.register(Packets.LogoffRequestPacket.class);
        kryoObject.register(Packets.NewEntityPacket.class);
        kryoObject.register(Packets.EntityRemovedPacket.class);
        kryoObject.register(Packets.BodyUpdatePacket.class);
        kryoObject.register(Packets.MoveRequestPacket.class);
    }
}
