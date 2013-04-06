package org.wasabi.http

import org.apache.http.client.methods.HttpRequestBase
import io.netty.handler.codec.http.HttpRequest

public class Request(private val httpRequest: HttpRequest) {

    val uri  = httpRequest.getUri()!!
    val method =  mapNettyHttpMethodToOwn(httpRequest.getMethod()!!)
    val host = getHeader("Host").takeWhile { it != ':' }
    val port = (getHeader("Host").dropWhile { it != ':' }).drop(1).toInt() ?: 80
    val keepAlive = getHeader("Connection").compareToIgnoreCase("keep-alive") == 0
    val cacheControl = getHeader("Cache-Control")
    val userAgent = getHeader("User-Agent")
    val accept = getHeader("Accept").split(",")
    val acceptEncoding = getHeader("Accept-Encoding").split(",")
    val acceptLanguage = getHeader("Accept-Language").split(",")
    val acceptCharset = getHeader("Accept-Charset").split(",")






    private fun getHeader(header: String): String {
        return httpRequest.headers()?.get(header).toString()
    }

// Cookie=jetbrains.charisma.main.security.PRINCIPAL=OWM3N2U5ZTllM2Y1ZWI2ZjUwMjM2MjRiNzdmOTE1MTkwMWZkNmU5ZTA5MDNkZjdjYzgzMGNkN2RiMjU1NzUyZTpoaGFyaXJp
// Query
// RouteParams
// PostFields


}

