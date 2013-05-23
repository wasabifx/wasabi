package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import io.netty.handler.codec.http.HttpMethod
import java.util.HashMap


public data class Route(val path: String, val method: HttpMethod, val params: HashMap<String, String>, val handler: RouteHandler.() -> Unit)
