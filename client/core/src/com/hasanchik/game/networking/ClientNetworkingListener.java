package com.hasanchik.game.networking;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.hasanchik.game.MyClientGame;
import com.hasanchik.game.interfaces.callbacks.ServerConnectedCallback;
import com.hasanchik.game.interfaces.callbacks.ServerDisconnectedCallback;
import com.hasanchik.game.utils.GDXDialogsFacade;
import com.hasanchik.shared.box2dutils.WorldHandler;
import com.hasanchik.shared.ecs.Components;
import com.hasanchik.shared.networking.Packets;
import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
public class ClientNetworkingListener extends Listener {
    private static final Logger logger = LogManager.getLogger(ClientNetworkingListener.class);

    final private ServerConnectedCallback serverConnectedCallback;
    final private ServerDisconnectedCallback serverDisconnectedCallback;

    final private ClientNetworkingHandler clientNetworkingHandler = ClientNetworkingHandler.getInstanceIfExists();

    final private MyClientGame context;

    final private GDXDialogs dialogs = GDXDialogsSystem.install();

    @Setter
    private boolean tryingToConnect;
    private WorldHandler worldHandler = MyClientGame.getInstance().getWorldHandler();
    public ClientNetworkingListener(MyClientGame context, ServerConnectedCallback serverConnectedCallback, ServerDisconnectedCallback serverDisconnectedCallback) {
        super();
        this.context = context;
        this.serverConnectedCallback = serverConnectedCallback;
        this.serverDisconnectedCallback = serverDisconnectedCallback;
    }

    private void closeConnection() {
        clientNetworkingHandler.getClient().close();
    }

    @Override
    public void connected(Connection connection) {
        final String[] name = new String[1];
        GDXTextPrompt roomIDPrompt = GDXDialogsFacade.getTextPromptPopup(
                "i know where you live!...... " + connection.getRemoteAddressUDP() + " *fanfare playing in background*",
                "please tell me the room you want to join.",
                new TextPromptListener() {

                    @Override
                    public void confirm(String requestedRoomID) {
                        if (requestedRoomID.equals("")) {
                            //Player clicked on X button
                            closeConnection();
                            return;
                        }
                        Packets.LogonRequestPacket logonRequestPacket = new Packets.LogonRequestPacket();
                        logonRequestPacket.requestedRoomID = requestedRoomID;
                        logonRequestPacket.name = name[0];
                        clientNetworkingHandler.sendTCPPacket(logonRequestPacket);
                    }

                    @Override
                    public void cancel() {
                        closeConnection();
                    }
                }
        );
        GDXTextPrompt namePrompt = GDXDialogsFacade.getTextPromptPopup(
                "i know where you live..!     " + connection.getRemoteAddressUDP() + " *fanfare playing in background*",
                "type in your name please                                                                                  .",
                new TextPromptListener() {

                    @Override
                    public void confirm(String requestedName) {
                        if (requestedName.equals("")) {
                            //Player clicked on X button
                            closeConnection();
                            return;
                        }
                        name[0] = requestedName;
                        roomIDPrompt.build().show();
                    }

                    @Override
                    public void cancel() {
                        closeConnection();
                    }
                }

        );
        namePrompt.build().show();
    }

    @Override
    public void disconnected(Connection connection) {
        if (tryingToConnect) {
            serverConnectedCallback.call(false);
        } else {
            serverDisconnectedCallback.call();
        }
        tryingToConnect = false;
    }

    @Override
    public void received(Connection connection, Object packet) {
        if (packet instanceof Packets.LogonResponsePacket logonResponsePacket) {
            String rejectionMessage = logonResponsePacket.rejectionMessage;
            if (rejectionMessage == null) {
                tryingToConnect = false;
                serverConnectedCallback.call(true);
            } else {
                GDXDialogsFacade.getSimpleInfoPopup(rejectionMessage).build().show();
                closeConnection();
            }
        }

        if (packet instanceof Packets.NewEntityPacket newEntityPacket) {
            Entity entity = newEntityPacket.entity;
            int entityID = entity.getComponent(Components.EntityComponent.class).entityID;
            logger.info("Received new entity with entityID " + entityID);
            context.getEngine().addEntity(entityID, entity);
        }

        if (packet instanceof Packets.BodyUpdatePacket bodyUpdatePacket) {
            int entityID = bodyUpdatePacket.entityID;
            if (entityID <= -1) {
                throw new IllegalArgumentException("Received invalid body update with entityID " + entityID + "!");
            }

            //logger.debug("Received body update with BodyID " + entityID);
            Body body = worldHandler.getBodiesList().get(entityID);
            synchronized (worldHandler.getWorld()) {
                if (body != null) {
                    synchronized (body) {
                        body.setTransform(bodyUpdatePacket.transformation.getPosition(), bodyUpdatePacket.transformation.getRotation());
                        body.setAngularVelocity(bodyUpdatePacket.angularVelocity);
                        body.setLinearVelocity(bodyUpdatePacket.linearVelocity);
                    }
                } else {
                    //throw new NullPointerException("Tried to update null body with entityID " + entityID);
                    logger.warn("Tried to update null body with BodyID " + entityID);
                }
            }
        }
    }

    @Override
    public void idle(Connection connection) {

    }
}
