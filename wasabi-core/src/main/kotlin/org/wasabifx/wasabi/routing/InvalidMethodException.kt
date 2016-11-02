package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

class InvalidMethodException(message: String = "Invalid method exception", val allowedMethods: Array<HttpMethod>) : Exception(message) {
}