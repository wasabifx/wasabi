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
import org.wasabi.exceptions.ResourceNotFoundHttpException


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
    public var objectToSend: Any? = null
        private set
    public var sendBuffer : String = ""
        private set
    public var overrideContentNegotiation: Boolean = false

/*
    public fun send(message: String, contentType: ContentType = ContentType.TextPlain) {
        sendBuffer = message
        this.contentType = contentType.toString()
    }
*/

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
            throw ResourceNotFoundHttpException()
        }
    }

    public fun send(obj: Any) {
        if (obj is String) {
            sendBuffer = obj
        } else {
            objectToSend = obj
        }
    }

    public fun overrideSendBuffer(value: String) {
        sendBuffer = value
    }

    public fun setStatus(statusCode: Int, statusDescription: String) {
        this.statusCode = statusCode
        this.statusDescription = statusDescription
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

    public fun setCacheControl(cacheControl: CacheControl) {
        addExtraHeader("Cache-Control", cacheControl.toString())
    }

}
