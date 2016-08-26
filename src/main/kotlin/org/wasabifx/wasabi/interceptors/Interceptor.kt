package org.wasabifx.wasabi.interceptors

import org.wasabifx.wasabi.protocol.http.Response
import org.wasabifx.wasabi.protocol.http.Request

interface Interceptor {
    fun intercept(request: Request, response: Response): Boolean
}