package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import io.netty.handler.codec.http.HttpMethod


public data class Route(val path: String, val method: HttpMethod, val params: RouteParams, val handler: RouteHandler.() -> Unit)
