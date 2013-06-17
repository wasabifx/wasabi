package org.wasabi.interceptors

import org.wasabi.http.Response
import org.wasabi.http.Request

public trait BeforeRequestInterceptor {

    fun handle(request: Request, response: Response): Boolean

}