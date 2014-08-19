package org.wasabi.test.websockets

import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker

/**
 * Created by swishy on 19/08/14.
 */
public class WebSocketTestTextClient(handshaker : WebSocketClientHandshaker) : WebSocketHandlerBase(handshaker){

    val messageText = "test"
    override fun handshakeCompleted(channel: Channel) {
        channel.writeAndFlush(messageText)
    }
    override fun handleTextWebSocketFrame(channel: Channel, frame: TextWebSocketFrame) {
        assert(frame.text() == messageText.toUpperCase())
    }
    override fun handlePongWebSocketFrame(channel: Channel, frame: PongWebSocketFrame) {
        throw UnsupportedOperationException()
    }
    override fun handleCloseWebSocketFrame(channel: Channel, frame: CloseWebSocketFrame) {
        throw UnsupportedOperationException()
    }
}