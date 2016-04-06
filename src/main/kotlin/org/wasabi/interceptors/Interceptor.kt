package org.wasabi.interceptors

import org.wasabi.protocol.http.Response
import org.wasabi.protocol.http.Request

public abstract class Interceptor {
    abstract fun intercept(request: Request, response: Response): Boolean
}