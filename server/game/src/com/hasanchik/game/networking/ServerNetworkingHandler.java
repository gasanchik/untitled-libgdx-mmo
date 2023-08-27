package com.hasanchik.game.networking;

import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.hasanchik.game.GameRoomInstance;
import com.hasanchik.game.MyGameServer;
import com.hasanchik.shared.networking.DTOsRegister;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ServerNetworkingHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ServerNetworkingHandler.class);

    private final MyGameServer context;

    private static volatile ServerNetworkingHandler instance;

    private final Server server;
    private final int udpPort, tcpPort;
    //Thread safety
    private final ConcurrentHashMap<Integer, String> connectionIDToPlayerNameMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Connection> playerNameToConnectionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> playerNameToRoomID = new ConcurrentHashMap<>();

    private ServerNetworkingHandler(MyGameServer context, int udpPort, int tcpPort) {
        server = new Server();

        this.context = context;
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;

        new DTOsRegister(server).registerDTOs();
    }

    @Override
    public void run() {
        logger.info("Starting server on TCP port " + tcpPort + " and on UDP port " + udpPort);
        try {
            server.bind(tcpPort, udpPort);
        } catch (IOException e) {
            logger.error("Could not bind server because: " + e);
        }

        server.start();
    }

    public ServerNetworkingHandler init() {
        server.addListener(new ServerNetworkingListener());
        return this;
    }

    public static ServerNetworkingHandler getInstance(MyGameServer context, int udpPort, int tcpPort) {
        ServerNetworkingHandler result = instance;
        //Very confusing multithreading code, but the indian tech guy never lies
        if (result == null) {
            synchronized (ServerNetworkingHandler.class) {
                result = instance;
                if (result == null) {
                    instance = result = new ServerNetworkingHandler(context, udpPort, tcpPort);
                }
            }
        }
        return result;
    }

    public static ServerNetworkingHandler getInstanceIfExists() {
        return instance;
    }

    public void sendTCPPacket(String playerName, Object packet) {
        playerNameToConnectionMap.get(playerName).sendTCP(packet);
    }

    public void sendUDPPacket(String playerName, Object packet) {
        playerNameToConnectionMap.get(playerName).sendUDP(packet);
    }

    public String validateAndJoinPlayer(Connection connection, String playerName, String requestedGameRoomID) {
        int connectionID = connection.getID();

        int gameRoomID;
        try {
            gameRoomID = Integer.parseInt(requestedGameRoomID);
        } catch (NumberFormatException e) {
            //Invalid number
            return "Requested gameRoomID is not a number";
        }

        if (gameRoomID < 0 || gameRoomID > MyGameServer.GAME_ROOMS_CAPACITY) {
            return String.format("Your requested roomID %s is out of bounds", gameRoomID);
        } else if (connectionIDToPlayerNameMap.containsKey(connectionID)) {
            return String.format("You have already joined a game on the same connection (Name of player:%s)", connectionIDToPlayerNameMap.get(connectionID));
        } else if(playerNameToConnectionMap.containsKey(playerName)) {
            return "You have already joined a game with the same account";
        } else if(playerName.length() > 30) {
            return String.format("Your name (%s) is too big (max 30)", playerName);
        } else if(playerName.length() < 1) {
            return "You need to have a name";
        } else {
            connectionIDToPlayerNameMap.put(connectionID, playerName);
            playerNameToConnectionMap.put(playerName, connection);
            playerNameToRoomID.put(playerName, gameRoomID);

            GameRoomInstance gameRoomInstance = context.getGameRoom(gameRoomID);

            //TODO: Replace this with the bodymap
            List<Entity> entityArrayList = gameRoomInstance.getEngine().getMap().getEntityArrayList();
            synchronized (entityArrayList) {
                entityArrayList.forEach(entity -> {
                    if (entity == null) {
                        return;
                    }

                    gameRoomInstance.getEntityReplicationSystem().replicateNewEntityToClient(playerName, entity);
                });
            }

            gameRoomInstance.getPlayerSystem().addPlayer(playerName, connection);
        }
        return null;
    }

    //TODO: implement gameroom closing after all players have left
    public void leavePlayer(String playerName) {
        GameRoomInstance gameRoomInstance = context.getGameRoom(playerNameToRoomID.get(playerName));
        gameRoomInstance.getPlayerSystem().removePlayer(playerName);
        connectionIDToPlayerNameMap.remove(playerNameToConnectionMap.get(playerName).getID());
        playerNameToConnectionMap.remove(playerName);
        playerNameToRoomID.remove(playerName);
    }
}
