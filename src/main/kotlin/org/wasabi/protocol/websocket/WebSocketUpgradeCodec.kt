package org.wasabi.protocol.websocket

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpServerUpgradeHandler
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
import io.netty.util.AsciiString
import org.slf4j.LoggerFactory
import org.wasabi.app.AppServer
import org.wasabi.routing.PatternMatchingChannelLocator
import org.wasabi.routing.RouteNotFoundException
import java.util.*

/**
 * Created by condaa1 on 9/04/16.
 */
class WebSocketUpgradeCodec(val appServer: AppServer, val handlerName : String): HttpServerUpgradeHandler.UpgradeCodec {

    private val log = LoggerFactory.getLogger(WebSocketUpgradeCodec::class.java)

    private var handshaker : WebSocketServerHandshaker? = null;

    private val channelLocator = PatternMatchingChannelLocator(appServer.channels)

    init {
        log.info("WebSocketUpgradeCodec created")
    }

    override fun requiredUpgradeHeaders(): MutableCollection<CharSequence>? {
        log.info("requiredUpgradeHeaders")
        return Collections.singletonList(AsciiString("Upgrade"))
    }

    override fun upgradeTo(ctx: ChannelHandlerContext?, upgradeRequest: FullHttpRequest?) {
        // Add the WebSocket connection handler to the pipeline immediately following the current handler.
        log.info("upgradeTo")
        ctx!!.pipeline().addAfter(ctx.name(), handlerName, WebSocketHandler(appServer, handshaker))
    }

    override fun prepareUpgradeResponse(ctx: ChannelHandlerContext?, upgradeRequest: FullHttpRequest?, upgradeHeaders: HttpHeaders?): Boolean {
        log.info("prepare upgrade.")
        // Setup Handshake
        val wsFactory : WebSocketServerHandshakerFactory = WebSocketServerHandshakerFactory(upgradeRequest!!.getUri(), null, false);

        handshaker = wsFactory.newHandshaker(upgradeRequest)

        try {

            log!!.info(handshaker?.uri())

            channelLocator.findChannelHandler(handshaker?.uri().toString()).handler

            if (handshaker == null) {
                log.info("Unsupported websocket protocol...")
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx?.channel()!!);
            } else {
                log.info("Handshake...")
                handshaker?.handshake(ctx?.channel()!!, upgradeRequest);
            }
            return true
        }
        catch(exception: RouteNotFoundException)
        {
            log.error("Websocket Channel not found.")
        }
        return false
    }
}