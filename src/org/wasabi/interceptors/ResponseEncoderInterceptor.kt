package org.wasabi.interceptors

import org.wasabi.http.Request
import org.wasabi.http.Response

public class ResponseEncoderInterceptor: AfterRequestInterceptor {
    override fun handle(request: Request, response: Response): Boolean {

        return true
    }
}