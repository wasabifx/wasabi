package org.wasabi.test

import org.junit.Test as spec
import org.wasabi.routing.PatternAndVerbMatchingRouteLocator
import io.netty.handler.codec.http.HttpMethod
import kotlin.test.assertNotNull
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.DefaultHttpHeaders;
import java.net.URI
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker
import java.net.InetSocketAddress
import org.wasabi.routing.PatternMatchingChannelLocator
import org.wasabi.test.websockets.WebSocketTestTextClient

/**
 * Created by swishy on 15/08/14.
 */
public class WebSocketSpecs {

    spec fun channel_handler_added_and_found() {

        TestServer.reset()
        TestServer.appServer.channel("/test", {
            if (frame is TextWebSocketFrame)
            {
                ctx?.channel()?.write(TextWebSocketFrame(frame.text()?.toUpperCase()));
            }
        })

        val channelLocator = PatternMatchingChannelLocator(TestServer.appServer.channels)

        val channel = channelLocator.findChannelHandler("/test")

        assertNotNull(channel)
    }

    spec fun basic_client_should_connect_and_get_response() {

        TestServer.reset()
        TestServer.appServer.channel("/test", {
            if (frame is TextWebSocketFrame)
            {
                ctx?.channel()?.write(TextWebSocketFrame(frame.text()?.toUpperCase()));
            }
        })

        val url = URI("ws://localhost:${TestServer.definedPort}/test");

       WebSocketTestTextClient(WebSocketClientHandshakerFactory.newHandshaker(
                url, WebSocketVersion.V13, null, false, DefaultHttpHeaders()))


    }
}