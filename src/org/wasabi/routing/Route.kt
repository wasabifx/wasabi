package org.wasabi.routing

import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.HttpMethod


public class Route(val method: HttpMethod, val path: String, val handler: (Request, Response) -> Unit) {

    public fun matchesPath(path: String): Boolean {
        return (this.path == path)

    }


}

