package com.hasanchik.game.networking;

import com.esotericsoftware.kryonet.Client;
import com.hasanchik.game.MyClientGame;
import com.hasanchik.game.screens.GameScreen;
import com.hasanchik.game.screens.LoadingScreen;
import com.hasanchik.game.screens.ScreenType;
import com.hasanchik.shared.networking.DTOsRegister;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Getter
public class ClientNetworkingHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientNetworkingHandler.class);

    private final MyClientGame context;

    private static volatile ClientNetworkingHandler instance = null;

    private final Client client;
    private int udpPort, tcpPort;
    private String hostIP;

    private ClientNetworkingListener mainListener = null;

    private ClientNetworkingHandler(MyClientGame context, String hostIP, int udpPort, int tcpPort) {
        client = new Client();
        this.context = context;
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;
        this.hostIP = hostIP;

        new DTOsRegister(client).registerDTOs();
    }

    @Override
    public void run() {
        client.start();
    }

    public ClientNetworkingHandler init() {
        mainListener = new ClientNetworkingListener(
                context,
                ((LoadingScreen)context.getScreen(ScreenType.LOADING))::onConnected,
                ((GameScreen)context.getScreen(ScreenType.GAME))::onConnectionClosed
        );
        client.addListener(mainListener);
        return this;
    }

    public static ClientNetworkingHandler getInstance(MyClientGame context, String hostIP, int udpPort, int tcpPort) {
        ClientNetworkingHandler result = instance;
        //Very confusing multithreading code, but the indian tech guy never lies
        if (result == null) {
            synchronized (ClientNetworkingHandler.class) {
                result = instance;
                if (result == null) {
                    instance = result = new ClientNetworkingHandler(context, hostIP, udpPort, tcpPort);
                }
            }
        }
        return result;
    }

    public static ClientNetworkingHandler getInstanceIfExists() {
        if (instance == null) {
            throw new NullPointerException("clientNetworkingHandler is null!");
        }
        return instance;
    }

    public void connectToServer() {
        mainListener.setTryingToConnect(true);
        try {
            logger.info("Connecting to server on TCP port " + tcpPort + " and on UDP port " + udpPort + " and host ip " + hostIP);
            client.connect(10000, hostIP, tcpPort, udpPort);
        } catch (IOException e) {
            logger.error("Could not connect to server: " + e);
            mainListener.getServerConnectedCallback().call(false);
        }
    }

    public void closeConnection() {
        client.close();
    }

    public void sendTCPPacket(Object packet) {
        client.sendTCP(packet);
    }

    public void sendUDPPacket(Object packet) {
        client.sendUDP(packet);
    }
}
