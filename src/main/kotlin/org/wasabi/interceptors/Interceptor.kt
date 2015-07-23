package org.wasabi.interceptors

import org.wasabi.http.Response
import org.wasabi.http.Request

public abstract class Interceptor {
    abstract fun intercept(request: Request, response: Response): Boolean
}