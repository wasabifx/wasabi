package org.wasabifx.routing

import io.netty.handler.codec.http.HttpMethod

public class InvalidMethodException(message: String = "Invalid method exception", val allowedMethods: Array<HttpMethod>) : Exception(message) {
}