package com.hadihariri.wasabi.routing

import com.hadihariri.wasabi.http.Request
import com.hadihariri.wasabi.http.Response
import com.hadihariri.wasabi.http.HttpMethod


public class Route(val method: HttpMethod, val path: String, val handler: (Request, Response) -> Unit) {

    public fun isMatch(method: HttpMethod, path: String): Boolean {
        return (path == path && method == method)

    }


}