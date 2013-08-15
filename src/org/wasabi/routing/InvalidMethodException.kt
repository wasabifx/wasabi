package org.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

public class InvalidMethodException(val message: String = "Invalid method exception", val allowedMethods: Array<HttpMethod>) : Exception(message) {
}