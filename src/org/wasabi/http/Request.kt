package org.wasabi.http

import org.apache.http.client.methods.HttpRequestBase
import io.netty.handler.codec.http.HttpRequest

public class Request(httpRequest: HttpRequest) {
    val uri: String = httpRequest.getUri()!!
    val method: HttpMethod = mapNettyHttpMethodToOwn(httpRequest.getMethod())


}

