package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response

public class SessionManagementInterceptor: Interceptor {
    override fun intercept(request: Request, response: Response): Boolean {
        // pass in storage. Associate it with request. Request needs new property Session type Any
        return true
    }
}