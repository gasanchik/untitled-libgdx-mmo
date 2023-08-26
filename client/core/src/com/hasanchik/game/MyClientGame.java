package com.hasanchik.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.minlog.Log;
import com.hasanchik.game.ecs.MyClientAshleyEngine;
import com.hasanchik.game.networking.ClientNetworkingHandler;
import com.hasanchik.game.screens.AbstractScreen;
import com.hasanchik.game.screens.ScreenType;
import com.hasanchik.shared.box2dutils.WorldHandler;
import com.hasanchik.shared.networking.Packets;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;

public class MyClientGame extends Game {
	private static final Logger logger = LogManager.getLogger(MyClientGame.class);
	private EnumMap<ScreenType,AbstractScreen> screenCache;
	@Getter
	private Viewport viewport;
	@Getter
	private WorldHandler worldHandler;
	@Getter
	private MyClientAshleyEngine engine;

	private static MyClientGame instance;

	@Override
	public void create() {
		Log.set(Log.LEVEL_INFO);

		instance = this;

		screenCache = new EnumMap<>(ScreenType.class);
		viewport = new FitViewport(9, 16);
		worldHandler = new WorldHandler(1 / 45f, false);
		engine = new MyClientAshleyEngine(this, 1f / 30f);

		ClientNetworkingHandler.getInstance(this, "192.168.2.14", 38442, 38443).init().run();
		setScreen(ScreenType.MAINMENU);
	}

	public void setScreen(final ScreenType screenType) {
		final AbstractScreen screen = screenCache.get(screenType);
		if(screen == null) {
			try {
				logger.debug("Creating new screen: " + screenType);
				final AbstractScreen newScreen = (AbstractScreen) ClassReflection.getConstructor(screenType.getScreenClass(), MyClientGame.class).newInstance(this);
				screenCache.put(screenType, newScreen);
				setScreen(newScreen);
			} catch (ReflectionException e) {
				throw new RuntimeException("Screen " + screenType + " could not be created because", e );
			}

		} else {
			logger.debug("Reusing old screen: " + screenType);
			setScreen(screen);
		}
	}

	public AbstractScreen getScreen(final ScreenType screenType) {
		final AbstractScreen screen = screenCache.get(screenType);
		if(screen == null) {
			try {
				logger.debug("Creating new screen: " + screenType);
				final AbstractScreen newScreen = (AbstractScreen) ClassReflection.getConstructor(screenType.getScreenClass(), MyClientGame.class).newInstance(this);
				screenCache.put(screenType, newScreen);
				return newScreen;
			} catch (ReflectionException e) {
				throw new RuntimeException("Screen " + screenType + " could not be created because", e );
			}

		} else {
			logger.debug("Reusing old screen: " + screenType);
			return screen;
		}
	}

	@Override
	public void dispose() {
		screenCache
				.values()
				.forEach(AbstractScreen::dispose);

		ClientNetworkingHandler clientNetworkingHandler = ClientNetworkingHandler.getInstanceIfExists();
		if (clientNetworkingHandler.getClient().isConnected()) {
			Packets.LogoffRequestPacket logoffRequestPacket = new Packets.LogoffRequestPacket();
			clientNetworkingHandler.sendTCPPacket(logoffRequestPacket);
			clientNetworkingHandler.getClient().close();
		}

		super.dispose();
	}

	public static MyClientGame getInstance() {
		return instance;
	}

	@Override
	public void resize (int width, int height) {
		viewport.setScreenSize(width, height);
	}
}
