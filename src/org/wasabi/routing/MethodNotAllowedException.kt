package org.wasabi.routing

import io.netty.handler.codec.http.HttpMethod

public class MethodNotAllowedException(val message: String, val allowedMethods: Array<HttpMethod>): Exception(message) {
}