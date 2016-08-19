package org.wasabifx.interceptors

import org.wasabifx.protocol.http.Response
import org.wasabifx.protocol.http.Request

public abstract class Interceptor {
    abstract fun intercept(request: Request, response: Response): Boolean
}