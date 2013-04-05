package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.HttpMethod


public class Route(val path: String, val method: HttpMethod, val handler: RouteHandler.() -> Unit) {


}
