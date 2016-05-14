package org.wasabi.protocol.http

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.multipart.InterfaceHttpData
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http2.Http2Headers
import org.slf4j.LoggerFactory
import org.wasabi.app.configuration
import java.util.*

public class Request() {

    private var log = LoggerFactory.getLogger(Request::class.java)

    constructor(httpRequest: HttpRequest) : this() {
        log.info("HttpRequest Constructor called.")
        this.httpRequest = httpRequest
        this.rawHeaders = httpRequest.headers().associate({it.key to it.value})
        this.uri = httpRequest.uri!!
        this.method = httpRequest.method!!
        this.document = uri.drop(uri.lastIndexOf("/") + 1)
        this.path = uri.split('?')[0]
        this.scheme = if (configuration!!.sslEnabled) "https" else "http"
        log.info("HttpRequest Constructor completed.")
    }

    constructor(http2Headers: Http2Headers?) : this() {
        this.http2Headers = http2Headers!!
        this.rawHeaders = http2Headers.associate({it.key.toString() to it.value.toString()})
        this.uri = http2Headers.path().toString()
        this.method = HttpMethod(http2Headers.method().toString())
        this.document = uri.drop(uri.lastIndexOf("/") + 1)
        this.path = uri
        this.scheme = getHeader("scheme")
    }

    lateinit var httpRequest : HttpRequest

    lateinit var http2Headers : Http2Headers

    lateinit var uri: String
    lateinit var method: HttpMethod
    lateinit var rawHeaders: Map<String,String>
    lateinit var document: String
    lateinit var path: String
    lateinit var scheme: String

    public val host: String by lazy {
        getHeader("Host").takeWhile { it != ':' }
    }
    public val protocol: String by lazy {
        this.scheme
    }
    public val isSecure: Boolean by lazy {
        protocol.compareTo("https", ignoreCase = true) == 0
    }
    public val urlPort: String by lazy {
        getHeader("Host").dropWhile { it != ':' }.drop(1)
    }
    public val port: Int by lazy {
        if (urlPort != "") urlPort.toInt() else 80
    }
    public val connection: String by lazy {
        getHeader("Connection")
    }
    public val cacheControl: String by lazy {
        getHeader("Cache-Control")
    }
    public val userAgent: String by lazy {
        getHeader("User-Agent")
    }
    public val accept: SortedMap<String, Int> by lazy {
        parseAcceptHeader("Accept")
    }
    public val acceptEncoding: SortedMap<String, Int> by lazy {
        parseAcceptHeader("Accept-Encoding")
    }
    public val acceptLanguage: SortedMap<String, Int> by lazy {
        parseAcceptHeader("Accept-Language")
    }
    public val acceptCharset: SortedMap<String, Int> by lazy {
        parseAcceptHeader("Accept-Charset")
    }
    public val ifNoneMatch: String by lazy {
        getHeader("If-None-Match")
    }
    public val queryParams: HashMap<String, String> by lazy {
        parseQueryParams()
    }
    public val routeParams: HashMap<String, String> = HashMap<String, String>()
    public val bodyParams: HashMap<String, Any> = HashMap<String, Any>()
    public val cookies: HashMap<String, Cookie> by lazy {
        parseCookies()
    }
    public val contentType: String by lazy {
        getHeader("Content-Type")
    }
    public val chunked: Boolean by lazy {
        getHeader("Transfer-Encoding").compareTo("chunked", ignoreCase = true) == 0
    }
    public val authorization: String by lazy {
        getHeader("Authorization")
    }


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

    // TODO fix should use raw
    private fun getHeader(header: String) = this.rawHeaders[header] ?: ""

    private fun parseQueryParams(): HashMap<String, String> {
        val queryParamsList = hashMapOf<String, String>()
        // TODO fix
        val urlParams = uri.split('?')
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


