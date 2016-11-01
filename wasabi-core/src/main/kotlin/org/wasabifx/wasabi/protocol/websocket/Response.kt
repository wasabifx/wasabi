package org.wasabifx.wasabi.protocol.websocket

import io.netty.handler.codec.http.websocketx.WebSocketFrame

/**
 * Wrapper to allow interceptors / serializers to function within the
 * WebSocket realm also.s
 */
class Response() {
    public var frame: WebSocketFrame? = null
}