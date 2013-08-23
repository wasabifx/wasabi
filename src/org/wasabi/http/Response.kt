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
import java.nio.file.Files
import java.nio.file.Paths
import java.io.File
import javax.activation.MimetypesFileTypeMap
import io.netty.handler.codec.http.ServerCookieEncoder
import io.netty.handler.codec.http.DefaultCookie
import java.util.ArrayList
import org.wasabi.serializers.Serializer


public class Response() {

    public val extraHeaders: HashMap<String, String> = HashMap<String, String>()

    public var etag: String = ""
        private set
    public var location: String = ""
        private set
    public var contentType: String = ContentType.TextPlain.toContentTypeString()
        private set
    public var statusCode: Int = 200
        private set
    public var statusDescription: String = ""
        private set
    public var allow: String = ""
        private set
    public var absolutePathToFileToStream: String = ""
        private set
    public var sendBuffer: Any? = null
        private set
    public var overrideContentNegotiation: Boolean = false
    public val cookies : HashMap<String, Cookie> = HashMap<String, Cookie>()
    // DISCLAIMER (HACK / TODO): I hate this. I really do. It doesn't belong here. It's part of the request
    // but this would imply changing a load of crap and the question isn't whether I have the
    // time now to change it but whether that would actually solve the problem. Somehow this needs to
    // be referenced in the response. I'm not happy with it. I really am not, but I think the time
    // has come to move on and revisit this later. And as they say, don't judge a developer by a single
    // line of code. Yours truly, Hadi Hariri. (@hhariri in case you want to humiliate me in public forum)
    public var requestedContentTypes: ArrayList<String> = arrayListOf()
    public var negotiatedMediaType: String = ""




    public fun streamFile(filename: String, explicitContentType: String = "") {

        val file = File(filename)
        if (file.exists()) {
            this.absolutePathToFileToStream = file.getAbsolutePath()
            var fileContentType : String?
            if (explicitContentType  == "") {
                var mimeTypesMap : MimetypesFileTypeMap? = MimetypesFileTypeMap()
                fileContentType = mimeTypesMap!!.getContentType(file)
            } else {
                fileContentType = explicitContentType
            }
            setResponseContentType(fileContentType ?: "application/unknown")
            addExtraHeader("Content-Length", file.length().toString())
            // TODO: Caching and redirect here too?
        } else {
            setHttpStatus(HttpStatusCodes.NotFound)
        }
    }

    public fun send(obj: Any) {
        sendBuffer = obj
    }

    public fun negotiate(vararg negotiations: Pair<String, Response.() -> Unit>) {
        for ((mediaType, func) in negotiations) {
            if (requestedContentTypes.any { it.compareToIgnoreCase(mediaType) == 0}) {
                func()
                negotiatedMediaType = mediaType
                return
            }
        }
        setHttpStatus(HttpStatusCodes.UnsupportedMediaType)
    }

    public fun setHttpStatus(statusCode: Int, statusDescription: String) {
        this.statusCode = statusCode
        this.statusDescription = statusDescription
    }

    public fun setHttpStatus(httpStatus: HttpStatusCodes) {
        statusCode = httpStatus.statusCode
        statusDescription = httpStatus.statusDescription
    }

    public fun setResponseContentType(contentType: String) {
        this.contentType = contentType
    }

    public fun setResponseContentType(contentType: ContentType) {
        setResponseContentType(contentType.toContentTypeString())
    }

    public fun setAllowedMethods(allowedMethods: Array<HttpMethod>) {
        allow = allowedMethods.makeString(",")
    }
    public fun addExtraHeader(name: String, value: String) {
        extraHeaders[name] = value
    }

    public fun setResponseCookies() {
        for (cookie in cookies) {
            val name = cookie.value.name.toString()
            val value = cookie.value.value.toString()
            addExtraHeader("Set-Cookie", ServerCookieEncoder.encode(name, value).toString())
        }
    }

    public fun setCacheControl(cacheControl: CacheControl) {
        addExtraHeader("Cache-Control", cacheControl.toString())
    }

}

fun String.with(handler : Response.() -> Unit) : Pair<String, Response.() -> Unit> = this to handler
