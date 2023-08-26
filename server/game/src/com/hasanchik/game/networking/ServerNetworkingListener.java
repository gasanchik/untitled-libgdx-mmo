package com.hasanchik.game.networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.hasanchik.shared.networking.Packets;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ServerNetworkingListener extends Listener {
    private static final Logger logger = LogManager.getLogger(ServerNetworkingListener.class);

    final private ServerNetworkingHandler serverNetworkingHandler = ServerNetworkingHandler.getInstanceIfExists();

    ServerNetworkingListener() {
    }

    public void received (Connection connection, Object packet) {
        //TODO: find a way to erase these horrible if statements from the world
        int connectionID = connection.getID();

        if (packet instanceof FrameworkMessage.KeepAlive) {
            return;
        }

        if (packet instanceof Packets.LogonRequestPacket logonRequestPacket) {
            String rejectionMessage;//null means success

            String name = logonRequestPacket.name;
            String requestedGameRoomID = logonRequestPacket.requestedRoomID;

            rejectionMessage = serverNetworkingHandler.validateAndJoinPlayer(connection, name, requestedGameRoomID);

            Packets.LogonResponsePacket response = new Packets.LogonResponsePacket();
            response.rejectionMessage = rejectionMessage;
            connection.sendTCP(response);

            if (rejectionMessage == null) {
                //We already checked if the gameRoomID was an int earlier
                logger.info(name + " successfully joined room " + Integer.parseInt(requestedGameRoomID));
            } else {
                logger.info("Player " + name + " rejected for reason: " + rejectionMessage);
            }

            return;
        }

        String playerName = serverNetworkingHandler.getConnectionIDToPlayerNameMap().get(connectionID);
        if (playerName == null) {
            logger.error("Connection " + connectionID + " tried to send other types of packets without logging on first! Type: " + packet.getClass());
            return;
        }
        int gameRoomID = serverNetworkingHandler.getPlayerNameToRoomID().get(playerName);
        if (packet instanceof Packets.MoveRequestPacket moveRequestPacket) {
            System.out.println(moveRequestPacket.MoveDirection);
        }

        if (packet instanceof Packets.LogoffRequestPacket logoffRequestPacket) {
            serverNetworkingHandler.leavePlayer(playerName);

            logger.info("Player" + playerName + " successfully left room " + gameRoomID);
        }

        //TODO: implement all of the packets
    }

    @Override
    public void disconnected(Connection connection) {
        if (!serverNetworkingHandler.getConnectionIDToPlayerNameMap().containsKey(connection.getID())) {
            return;
        }
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!serverNetworkingHandler.getConnectionIDToPlayerNameMap().containsKey(connection.getID())) {
                //Player probably already sent a logoffRequestPacket
                return;
            }
            //disconnected unexpectedly
            String playerName = serverNetworkingHandler.getConnectionIDToPlayerNameMap().get(connection.getID());
            int gameRoomID = serverNetworkingHandler.getPlayerNameToRoomID().get(playerName);
            logger.info("Player" + playerName + " unexpectedly left room " + gameRoomID);
            serverNetworkingHandler.leavePlayer(playerName);
            super.disconnected(connection);
        }).start();
    }
}
