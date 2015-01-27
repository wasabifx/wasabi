package org.wasabi.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.CharsetUtil
import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.channel.ChannelFutureListener
import java.util.HashMap
import io.netty.handler.codec.http.HttpMethod
import org.codehaus.jackson.map.ObjectMapper
import java.io.File
import javax.activation.MimetypesFileTypeMap
import io.netty.handler.codec.http.ServerCookieEncoder
import io.netty.handler.codec.http.DefaultCookie
import java.util.ArrayList
import org.wasabi.serializers.Serializer
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat


public class Response() {

    public val rawHeaders: HashMap<String, String> = HashMap<String, String>()

    public var etag: String = ""
    public var resourceId: String? = null
    public var location: String = ""
    public var contentType: String = ContentType.Text.Plain.toString()
    public var contentLength: Long = 0
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
    public var connection: String = "close"
    public var cacheControl: String = "max-age=0"
    public var lastModified: DateTime? = null



    public fun redirect(url: String, redirectType: StatusCodes = StatusCodes.Found) {
        setStatus(redirectType)
        location = url
    }

    public fun streamFile(filename: String, contentType: String = "*/*") {

        val file = File(filename)
        if (file.exists() && !file.isDirectory()) {
            this.absolutePathToFileToStream = file.getAbsolutePath()
            var fileContentType : String?
            when (contentType) {
                "*/*" -> when {
                    file.extension.compareToIgnoreCase("css") == 0 -> {
                        fileContentType = "text/css"
                    }
                    file.extension.compareToIgnoreCase("js") == 0 -> {
                        fileContentType = "application/javascript"
                    }
                    else -> {
                        var mimeTypesMap: MimetypesFileTypeMap? = MimetypesFileTypeMap()
                        fileContentType = mimeTypesMap!!.getContentType(file)
                    }
                }
                else -> {
                    fileContentType = contentType
                }
            }
            this.contentType = fileContentType ?: "application/unknown"
            this.contentLength = file.length()
            this.lastModified = DateTime(file.lastModified())

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

    public fun setStatus(statusCode: StatusCodes, statusDescription: String = statusCode.description) {
        this.statusCode = statusCode.code
        this.statusDescription = statusDescription
    }

    public fun setAllowedMethods(allowedMethods: Array<HttpMethod>) {
        allow = allowedMethods.joinToString(",")
    }

    public fun addRawHeader(name: String, value: String) {
        if (value != ""){
            rawHeaders[name] = value
        }
    }

    private fun setResponseCookies() {
        for (cookie in cookies) {
            val name = cookie.value.name.toString()
            val value = cookie.value.value.toString()
            addRawHeader("Set-Cookie", ServerCookieEncoder.encode(name, value).toString())
        }
    }


    public fun setHeaders() {
        setResponseCookies()
        addRawHeader("ETag", etag)
        addRawHeader("Location", location)
        addRawHeader("Content-Type", contentType)
        if (contentLength > 0) {
            addRawHeader("Content-Length", contentLength.toString())
        }
        addRawHeader("Connection", connection)
        addRawHeader("Date", convertToDateFormat(DateTime.now()!!))
        addRawHeader("Cache-Control", cacheControl)
        if (lastModified != null) {
            addRawHeader("Last-Modified", convertToDateFormat(lastModified!!))
        }
    }

    public fun convertToDateFormat(dateTime: DateTime): String {
        val dt = DateTime(dateTime, DateTimeZone.forID("GMT"))
        val dtf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss")
        return "${dtf?.print(dt).toString()} GMT"
    }
}

