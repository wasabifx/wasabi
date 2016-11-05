package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

class MethodNotAllowedException(message: String = "Invalid method exception", val allowedMethods: Array<HttpMethod>) : Exception(message) {
}