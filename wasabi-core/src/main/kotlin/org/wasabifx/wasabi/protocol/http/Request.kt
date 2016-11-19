package org.wasabifx.wasabi.protocol.http

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import io.netty.handler.codec.http.multipart.Attribute
import io.netty.handler.codec.http.multipart.InterfaceHttpData
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http2.Http2Headers
import org.wasabifx.wasabi.app.configuration
import java.net.InetSocketAddress
import java.util.*

class Request() {


    constructor(httpRequest: HttpRequest, address: InetSocketAddress) : this() {
        this.httpRequest = httpRequest
        this.rawHeaders = httpRequest.headers().associate({it.key to it.value})
        this.uri = httpRequest.uri!!
        this.method = httpRequest.method!!
        this.document = uri.drop(uri.lastIndexOf("/") + 1)
        this.path = uri.split('?')[0]
        this.scheme = if (configuration!!.sslEnabled) "https" else "http"
        this.remoteAddress = address
    }

    constructor(http2Headers: Http2Headers?, address: InetSocketAddress) : this() {
        this.http2Headers = http2Headers!!
        this.rawHeaders = http2Headers.associate({it.key.toString() to it.value.toString()})
        this.uri = http2Headers.path().toString()
        this.method = HttpMethod(http2Headers.method().toString())
        this.document = uri.drop(uri.lastIndexOf("/") + 1)
        this.path = uri
        this.scheme = header("scheme")
        this.remoteAddress = address
    }

    lateinit var httpRequest : HttpRequest

    lateinit var http2Headers : Http2Headers

    lateinit var uri: String
    lateinit var method: HttpMethod
    lateinit var rawHeaders: Map<String,String>
    lateinit var document: String
    lateinit var path: String
    lateinit var scheme: String
    lateinit var remoteAddress: InetSocketAddress

    val host: String by lazy {
        header("Host").takeWhile { it != ':' }
    }
    val protocol: String by lazy {
        this.scheme
    }
    val isSecure: Boolean by lazy {
        protocol.compareTo("https", ignoreCase = true) == 0
    }
    val urlPort: String by lazy {
        header("Host").dropWhile { it != ':' }.drop(1)
    }
    val port: Int by lazy {
        if (urlPort != "") urlPort.toInt() else 80
    }
    val connection: String by lazy {
        header("Connection")
    }
    val cacheControl: String by lazy {
        header("Cache-Control")
    }
    val userAgent: String by lazy {
        header("User-Agent")
    }
    val accept: SortedMap<String, Int> by lazy {
        parseAcceptHeader("Accept")
    }
    val acceptEncoding: SortedMap<String, Int> by lazy {
        parseAcceptHeader("Accept-Encoding")
    }
    val acceptLanguage: SortedMap<String, Int> by lazy {
        parseAcceptHeader("Accept-Language")
    }
    val acceptCharset: SortedMap<String, Int> by lazy {
        parseAcceptHeader("Accept-Charset")
    }
    val ifNoneMatch: String by lazy {
        header("If-None-Match")
    }
    val queryParams: HashMap<String, String> by lazy {
        parseQueryParams()
    }
    val routeParams: HashMap<String, String> = HashMap()
    val bodyParams: HashMap<String, Any> = HashMap()
    val cookies: HashMap<String, Cookie> by lazy {
        parseCookies()
    }
    val contentType: String by lazy {
        header("Content-Type")
    }
    val chunked: Boolean by lazy {
        header("Transfer-Encoding").compareTo("chunked", ignoreCase = true) == 0
    }
    val authorization: String by lazy {
        header("Authorization")
    }


    var session: Session? = null

    // TODO add charset and parse method to split charset from contentType if it exists.

    private fun parseAcceptHeader(header: String): SortedMap<String, Int> {

        val parsed = hashMapOf<String, Int>()
        val entries = header(header).split(',')
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

    fun header(header: String): String {
        val originalHeader = this.rawHeaders.entries.firstOrNull { it.key.equals(header, ignoreCase = true) }
        return originalHeader?.value?:""
    }

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
        val cookieHeader = header("Cookie")
        val cookieSet = ServerCookieDecoder.STRICT.decode(cookieHeader)
        val cookieList = hashMapOf<String, Cookie>()
        cookieSet?.iterator()?.forEach { cookie ->
            val tmpCookie = Cookie(cookie.name().toString(), cookie.value().toString())

            if (cookie.path() != null) {
                tmpCookie.setPath(cookie.path())
            }

            if (cookie.domain() != null) {
                tmpCookie.setDomain(cookie.domain())
            }

            tmpCookie.isSecure = cookie.isSecure
            cookieList[cookie.name().toString()] = tmpCookie
        }
        return cookieList
    }


    fun parseBodyParams(httpDataList: MutableList<InterfaceHttpData>) {
        for (entry in httpDataList) {
            addBodyParam(entry)
        }
    }

    fun addBodyParam(httpData: InterfaceHttpData) {
        // TODO: Add support for other types of attributes (namely file)
        if (httpData.httpDataType == HttpDataType.Attribute) {
            val attribute = httpData as Attribute
            bodyParams[attribute.name.toString()] = attribute.value.toString()
        }
    }
}


