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
import java.io.IOException;

public class Application {
	
	private static Robot robot;

	public static void main(String[] args) throws AWTException {
		robot = new Robot();

		Undertow server = Undertow.builder().addHttpListener(8080, "0.0.0.0")
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
								robot.keyPress(Button.valueOf(text).getKey());
								robot.keyRelease(Button.valueOf(text).getKey());
							}

							@Override
							protected void onCloseMessage(CloseMessage cm, WebSocketChannel channel) {
								System.out.println("Socket closed: " + cm.getReason());
							}
							
							

						});
						channel.resumeReceives();
					}

				})).addPrefixPath("/", resource(new ClassPathResourceManager(Application.class.getClassLoader(),
						Application.class.getPackage())).addWelcomeFiles("index.html")))
				.build();

		server.start();

	}

}
