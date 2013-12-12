package org.wasabi.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.CharsetUtil
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelInboundMessageHandlerAdapter
import java.util.HashMap
import io.netty.handler.codec.http.HttpMethod
import org.codehaus.jackson.map.ObjectMapper
import java.io.File
import javax.activation.MimetypesFileTypeMap
import io.netty.handler.codec.http.ServerCookieEncoder
import io.netty.handler.codec.http.DefaultCookie
import java.util.ArrayList
import org.wasabi.serializers.Serializer


public class Response() {

    public val extraHeaders: HashMap<String, String> = HashMap<String, String>()

    public var etag: String = ""
    public var location: String = ""
    public var contentType: String = ContentType.TextPlain.convertToString()
    public var statusCode: Int = 200
    public var statusDescription: String = ""
    public var allow: String = ""
    public var absolutePathToFileToStream: String = ""
        private set
    public var sendBuffer: Any? = null
        private set
    public var overrideContentNegotiation: Boolean = false
    public val cookies : HashMap<String, Cookie> = HashMap<String, Cookie>()
    public var requestedContentTypes: ArrayList<String> = arrayListOf()
    public var negotiatedMediaType: String = ""



    public fun redirect(url: String, redirectType: StatusCodes = StatusCodes.Found) {
        setStatus(redirectType)
        location = url
    }

    public fun streamFile(filename: String, contentType: String = "*/*") {

        val file = File(filename)
        if (file.exists()) {
            this.absolutePathToFileToStream = file.getAbsolutePath()
            var fileContentType : String?
            if (contentType == "*/*") {
                var mimeTypesMap : MimetypesFileTypeMap? = MimetypesFileTypeMap()
                fileContentType = mimeTypesMap!!.getContentType(file)
            } else {
                fileContentType = contentType
            }
            this.contentType = fileContentType ?: "application/unknown"
            addExtraHeader("Content-Length", file.length().toString())
            // TODO: Caching and redirect here too?
        } else {
            setStatus(StatusCodes.NotFound)
        }
    }


    public fun send(obj: Any, contentType: String = "*/*") {
        sendBuffer = obj
        if (contentType != "*/*") {
            negotiatedMediaType = contentType
        }
    }


    public fun negotiate(vararg negotiations: Pair<String, Response.() -> Unit>) {
        for ((mediaType, func) in negotiations) {
            if (requestedContentTypes.any { it.compareToIgnoreCase(mediaType) == 0}) {
                func()
                negotiatedMediaType = mediaType
                return
            }
        }
        setStatus(StatusCodes.UnsupportedMediaType)
    }

    public fun setStatus(statusCode: Int, statusDescription: String) {
        this.statusCode = statusCode
        this.statusDescription = statusDescription
    }

    public fun setStatus(httpStatus: StatusCodes) {
        statusCode = httpStatus.code
        statusDescription = httpStatus.description
    }

    public fun setAllowedMethods(allowedMethods: Array<HttpMethod>) {
        allow = allowedMethods.makeString(",")
    }

    public fun addExtraHeader(name: String, value: String) {
        if (value != ""){
            extraHeaders[name] = value
        }
    }

    private fun setResponseCookies() {
        for (cookie in cookies) {
            val name = cookie.value.name.toString()
            val value = cookie.value.value.toString()
            addExtraHeader("Set-Cookie", ServerCookieEncoder.encode(name, value).toString())
        }
    }

    public fun setCacheControl(cacheControl: CacheControl) {
        addExtraHeader("Cache-Control", cacheControl.toString())
    }

    public fun setHeaders() {
        setResponseCookies()
        addExtraHeader("E-Tag", etag)
        addExtraHeader("Location", location)
    }
}

