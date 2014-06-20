package org.wasabi.interceptors

import org.wasabi.http.Response
import org.wasabi.http.Request

public abstract class Interceptor {

    var executeNext = false


    public fun next() {
        executeNext = true
    }

    abstract fun intercept(request: Request, response: Response)
}