package org.wasabifx.wasabi.routing

import io.netty.handler.codec.http.HttpMethod
import java.util.HashMap


class Route(val path: String, val method: HttpMethod, val params: HashMap<String, String>, vararg val handler: RouteHandler.() -> Unit)

