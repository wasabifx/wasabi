package org.wasabi.test.websockets

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import java.net.URI
import java.net.SocketAddress
import org.slf4j.LoggerFactory

/**
 * Created by swishy on 15/08/14.
 */
public abstract class WebSocketHandlerBase(val handshaker : WebSocketClientHandshaker?) : SimpleChannelInboundHandler<Any?>() {



    var handshakeFuture : ChannelPromise? = null

    private var log = LoggerFactory.getLogger(javaClass<WebSocketHandlerBase>())

    override fun channelRead0(ctx: ChannelHandlerContext?, msg: Any?) {

        var ch = ctx!!.channel();
        if (!handshaker!!.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, msg as FullHttpResponse);
            handshakeFuture!!.setSuccess();
            this.handshakeCompleted(ch)
            return;
        }

        if (msg is FullHttpResponse) {
            var response = msg as FullHttpResponse;
            throw IllegalStateException("Unexpected HTTP response");
        }

        var frame = msg as WebSocketFrame;
        if (frame is TextWebSocketFrame) {
            this.handleTextWebSocketFrame(ch, frame as TextWebSocketFrame)
        } else if (frame is PongWebSocketFrame) {
            this.handlePongWebSocketFrame(ch, frame as PongWebSocketFrame)
        } else if (frame is CloseWebSocketFrame) {
            this.handleCloseWebSocketFrame(ch, frame as CloseWebSocketFrame)
        }
    }

    public abstract fun handshakeCompleted(channel : Channel?)


    public abstract fun handleTextWebSocketFrame(channel : Channel?, frame : TextWebSocketFrame)


    public abstract fun handlePongWebSocketFrame(channel : Channel?, frame : PongWebSocketFrame)


    public abstract fun handleCloseWebSocketFrame(channel : Channel?, frame : CloseWebSocketFrame)


    override fun handlerAdded(ctx : ChannelHandlerContext)
    {
        handshakeFuture = ctx.newPromise();
    }

    override fun channelActive(ctx : ChannelHandlerContext)
    {
        log!!.info("WebSocket Client active!");
        handshaker!!.handshake(ctx.channel());
    }

    override fun channelInactive(ctx : ChannelHandlerContext) {
        log!!.info("WebSocket Client disconnected!");
    }

}