package org.wasabi.routing

import org.wasabi.http.HttpMethod

public class MethodNotAllowedException(val message: String, val allowedMethods: Array<HttpMethod>): Exception(message) {
}