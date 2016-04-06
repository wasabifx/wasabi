package org.wasabi.protocol.http

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.multipart.InterfaceHttpData
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import java.util.*


public class Request(private val httpRequest: HttpRequest) {

    public val uri: String = httpRequest.getUri()!!
    public val document: String = uri.drop(uri.lastIndexOf("/") + 1)
    public val path: String = uri.split('?')[0]
    public val method: HttpMethod = httpRequest.getMethod()!!
    public val host: String = getHeader("Host").takeWhile { it != ':' }
    public val protocol: String = "http" // TODO: Fix this.
    public val isSecure: Boolean = protocol.compareTo("https", ignoreCase = true) == 0
    val urlPort = getHeader("Host").dropWhile { it != ':' }.drop(1)
    public val port: Int = if (urlPort != "") urlPort.toInt() else 80
    public val connection: String = getHeader("Connection")
    public val cacheControl: String = getHeader("Cache-Control")
    public val userAgent: String = getHeader("User-Agent")
    public val accept: SortedMap<String, Int> = parseAcceptHeader("Accept")
    public val acceptEncoding: SortedMap<String, Int> = parseAcceptHeader("Accept-Encoding")
    public val acceptLanguage: SortedMap<String, Int> = parseAcceptHeader("Accept-Language")
    public val acceptCharset: SortedMap<String, Int> = parseAcceptHeader("Accept-Charset")
    public val ifNoneMatch: String = getHeader("If-None-Match")
    public val queryParams: HashMap<String, String> = parseQueryParams()
    public val routeParams: HashMap<String, String> = HashMap<String, String>()
    public val bodyParams: HashMap<String, Any> = HashMap<String, Any>()
    public val cookies: HashMap<String, Cookie> = parseCookies()
    public val contentType: String = getHeader("Content-Type")
    public val chunked: Boolean = getHeader("Transfer-Encoding").compareTo("chunked", ignoreCase = true) == 0
    public val authorization: String = getHeader("Authorization")
    public val rawHeaders: List<Pair<String, String>> = httpRequest.headers().map({ it.key to it.value })

    public var session: Session? = null

    // TODO add charset and parse method to split charset from contentType if it exists.

    private fun parseAcceptHeader(header: String): SortedMap<String, Int> {

        val parsed = hashMapOf<String, Int>()
        val entries = getHeader(header).split(',')
        for (entry in entries) {
            val parts = entry.split(';')
            val mediaType = parts[0]
            var weight = 1
            if (parts.size == 2) {
                val float = parts[1].trim().drop(2).toFloat() * 10
                weight = float.toInt()
            }
            parsed.put(mediaType, weight)
        }
        return parsed.toSortedMap<String, Int>()
    }

    private fun getHeader(header: String) = httpRequest.headers()[header] ?: ""

    private fun parseQueryParams(): HashMap<String, String> {
        val queryParamsList = hashMapOf<String, String>()
        val urlParams = httpRequest.getUri()!!.split('?')
        if (urlParams.size == 2) {
            val queryNameValuePair = urlParams[1].split("&")
            for (entry in queryNameValuePair) {
                val nameValuePair = entry.split('=')
                if (nameValuePair.size == 2) {
                    queryParamsList[nameValuePair[0]] = nameValuePair[1]
                } else {
                    queryParamsList[nameValuePair[0]] = ""
                }
            }
        }
        return queryParamsList
    }

    private fun parseCookies(): HashMap<String, Cookie> {
        val cookieHeader = getHeader("Cookie")
        val cookieSet = ServerCookieDecoder.STRICT.decode(cookieHeader)
        val cookieList = hashMapOf<String, Cookie>()
        cookieSet?.iterator()?.forEach { cookie ->
            var path = ""
            if (cookie.path() != null) {
                path = cookie.path()
            }
            var domain = ""
            if (cookie.domain() != null) {
                domain = cookie.domain()
            }
            cookieList[cookie.name().toString()] = Cookie(cookie.name().toString(), cookie.value().toString(), path, domain, cookie.isSecure)
        }
        return cookieList
    }


    public fun parseBodyParams(httpDataList: MutableList<InterfaceHttpData>) {
        for (entry in httpDataList) {
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


}


