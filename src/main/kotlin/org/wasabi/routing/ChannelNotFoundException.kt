package org.wasabi.routing

public class ChannelNotFoundException(val message: String = "Websocket channel entry not found"): Exception(message) {
}