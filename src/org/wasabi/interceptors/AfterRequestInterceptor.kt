package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response

public trait AfterRequestInterceptor {

    fun handle(request: Request, response: Response): Boolean
}