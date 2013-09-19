package org.wasabi.interceptors

import org.wasabi.http.Response
import org.wasabi.http.Request

public trait Interceptor {
    fun intercept(request: Request, response: Response): Boolean
}