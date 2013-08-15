package org.wasabi.exceptions

import io.netty.handler.codec.http.HttpMethod

public class MethodNotAllowedHttpException(val message: String = "Method not allowed", val allowedMethods: Array<HttpMethod>): HttpException(405, message) {
}