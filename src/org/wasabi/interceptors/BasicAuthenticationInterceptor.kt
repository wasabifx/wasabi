package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response


public class BasicAuthenticationInterceptor: BeforeRequestInterceptor {
    override fun handle(request: Request, response: Response): Boolean {
        return false
    }
}
