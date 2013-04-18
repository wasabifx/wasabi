package org.wasabi.http

import org.apache.http.client.methods.HttpRequestBase
import io.netty.handler.codec.http.HttpRequest
import java.util.Dictionary
import org.wasabi.routing.BaseParams
import java.util.ArrayList
import io.netty.handler.codec.http.HttpMethod
import org.wasabi.routing.RouteParams
import org.wasabi.routing.QueryParams
import io.netty.handler.codec.http.multipart.InterfaceHttpData
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http.multipart.Attribute

public class Request(private val httpRequest: HttpRequest) {

    public val uri: String  = httpRequest.getUri()!!.split('?')[0]
    public val method: HttpMethod =  httpRequest.getMethod()!!
    public val host: String = getHeader("Host").takeWhile { it != ':' }
    public val port : Int = (getHeader("Host").dropWhile { it != ':' }).drop(1).toInt() ?: 80
    public val keepAlive: Boolean  = getHeader("Connection").compareToIgnoreCase("keep-alive") == 0
    public val cacheControl: String = getHeader("Cache-Control")
    public val userAgent: String = getHeader("User-Agent")
    public val accept: Array<String> = getHeader("Accept").split(",")
    public val acceptEncoding: Array<String> = getHeader("Accept-Encoding").split(",")
    public val acceptLanguage: Array<String> = getHeader("Accept-Language").split(",")
    public val acceptCharset: Array<String> = getHeader("Accept-Charset").split(",")
    public val queryParams : QueryParams = QueryParams()
    public var routeParams: RouteParams = RouteParams()
    public var bodyParams: BodyParams = BodyParams()
    public var contentType: String = getHeader("Content-Type")
    public var chunked: Boolean = getHeader("Transfer-Encoding").compareToIgnoreCase("chunked") == 0

    private fun getHeader(header: String): String {

        return httpRequest.headers()?.get(header).toString()
    }

    public fun parseQueryParams() {

        val urlParams = httpRequest.getUri()!!.split('?')
        if (urlParams.size == 2) {
            val queryNameValuePair = urlParams[1].split("&")
            for (entry in queryNameValuePair) {
                val nameValuePair = entry.split('=')
                if (nameValuePair.size == 2) {
                    queryParams[nameValuePair[0]] = nameValuePair[1]
                } else {
                    queryParams[nameValuePair[0]] = ""
                }
           }
        }
    }

    public fun parseBodyParams(httpDataList: MutableList<InterfaceHttpData>) {
        for(entry in httpDataList) {
            addBodyParam(entry)
        }

    }

    public fun addBodyParam(httpData: InterfaceHttpData) {
        // TODO: Add support for other types of attributes (namely file)
        if (httpData.getHttpDataType() == HttpDataType.Attribute) {
            val attribute = httpData as Attribute
            bodyParams[attribute.getName().toString()] = attribute.getValue().toString()
        }
    }
// Cookie=jetbrains.charisma.main.security.PRINCIPAL=OWM3N2U5ZTllM2Y1ZWI2ZjUwMjM2MjRiNzdmOTE1MTkwMWZkNmU5ZTA5MDNkZjdjYzgzMGNkN2RiMjU1NzUyZTpoaGFyaXJp


}

