package com.hasanchik.shared.networking;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Transform;

public class Packets {
    public static class BodyUpdatePacket {
        public int entityID;
        public Transform transformation;
        public Vector2 linearVelocity;
        public float angularVelocity;
    }

    public static class LogoffRequestPacket {

    }

    public static class LogonRequestPacket implements Packet {
        public String requestedRoomID;
        public String name;
    }

    public static class LogonResponsePacket implements Packet {
        public String rejectionMessage;
    }

    public static class MoveRequestPacket implements Packet {
        //too lazy to make an enum for this
        //1 = up, 2 = right, 3 = down, 4 is left
        public int MoveDirection;
    }

    public static class NewEntityPacket {
        public Entity entity;
    }

    public static class EntityRemovedPacket {
        public int entityID;
    }
}
