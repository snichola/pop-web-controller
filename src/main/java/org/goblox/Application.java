package org.goblox;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Application {
	private static Properties props;
	private static Robot robot;
	private static Map<String, Integer> keys;

	public static void main(String[] args) throws AWTException, FileNotFoundException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		robot = new Robot();
		
		loadProps();
		
		System.out.println(Paths.get("pop.properties").toAbsolutePath());

		Undertow server = Undertow.builder().addHttpListener(Integer.parseInt(props.getProperty("port")), "0.0.0.0")
				.setServerOption(UndertowOptions.IDLE_TIMEOUT, 900000)
				.setHandler(path().addPrefixPath("/controller", websocket(new WebSocketConnectionCallback() {

					public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
						System.out.println("Connect to: " + channel.getPeerAddress());
						channel.getReceiveSetter().set(new AbstractReceiveListener() {

							@Override
							protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message)
									throws IOException {

								String text = message.getData();
								System.out.println(text);
								robot.keyPress(keys.get(text));
								robot.keyRelease(keys.get(text));
							}

							@Override
							protected void onCloseMessage(CloseMessage cm, WebSocketChannel channel) {
								System.out.println("Socket closed: " + cm.getReason());
							}
							
						});
						channel.resumeReceives();
					}

				})).addPrefixPath("/", resource(new ClassPathResourceManager(Application.class.getClassLoader(), "static")).addWelcomeFiles("index.html")))
				.build();

		server.start();

	}
	
	private static void loadProps() throws FileNotFoundException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		props = new Properties();
		props.load(new FileInputStream("pop.properties"));
		
		keys = new HashMap<String, Integer>();
		keys.put("LW", (Integer) KeyEvent.class.getField(props.getProperty("leftwhite")).get(null));
		keys.put("LY", (Integer) KeyEvent.class.getField(props.getProperty("leftyellow")).get(null));
		keys.put("LG", (Integer) KeyEvent.class.getField(props.getProperty("leftgreen")).get(null));
		keys.put("LB", (Integer) KeyEvent.class.getField(props.getProperty("leftblue")).get(null));
		keys.put("MR", (Integer) KeyEvent.class.getField(props.getProperty("middlered")).get(null));
		keys.put("RB", (Integer) KeyEvent.class.getField(props.getProperty("rightblue")).get(null));
		keys.put("RG", (Integer) KeyEvent.class.getField(props.getProperty("rightgreen")).get(null));
		keys.put("RY", (Integer) KeyEvent.class.getField(props.getProperty("rightyellow")).get(null));
		keys.put("RW", (Integer) KeyEvent.class.getField(props.getProperty("whiteyellow")).get(null));
	}

}
