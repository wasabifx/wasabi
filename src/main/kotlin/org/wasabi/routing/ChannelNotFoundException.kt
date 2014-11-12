package org.wasabi.routing

public class ChannelNotFoundException(message: String = "Websocket channel entry not found"): Exception(message) {
}