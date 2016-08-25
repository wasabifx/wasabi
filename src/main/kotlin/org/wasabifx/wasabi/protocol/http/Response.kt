package org.wasabifx.wasabi.protocol.http

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.cookie.ServerCookieEncoder
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.util.*
import javax.activation.MimetypesFileTypeMap


class Response() {

    val rawHeaders: HashMap<String, String> = HashMap<String, String>()

    var etag: String = ""
    var resourceId: String? = null
    var location: String = ""
    var contentType: String = ContentType.Companion.Text.Plain.toString()
    var contentLength: Long? = null
    var statusCode: Int = 200
    var statusDescription: String = ""
    var allow: String = ""
    var absolutePathToFileToStream: String = ""
        private set
    var sendBuffer: Any? = null
        private set
    var overrideContentNegotiation: Boolean = false
    val cookies : HashMap<String, Cookie> = HashMap<String, Cookie>()
    var requestedContentTypes: ArrayList<String> = arrayListOf()
    var negotiatedMediaType: String = ""
    var connection: String = "close"
    var cacheControl: String = "max-age=0"
    var lastModified: DateTime? = null



    fun redirect(url: String, redirectType: StatusCodes = StatusCodes.Found) {
        setStatus(redirectType)
        location = url
    }

    fun setFileResponseHeaders(filename: String, contentType: String = "*/*") {

        val file = File(filename)
        if (file.exists() && !file.isDirectory) {
            this.absolutePathToFileToStream = file.getAbsolutePath()
            val fileContentType : String?
            when (contentType) {
                "*/*" -> when {
                    file.extension.compareTo("css", ignoreCase = true) == 0 -> {
                        fileContentType = "text/css"
                    }
                    file.extension.compareTo("js", ignoreCase = true) == 0 -> {
                        fileContentType = "application/javascript"
                    }
                    else -> {
                        val mimeTypesMap: MimetypesFileTypeMap? = MimetypesFileTypeMap()
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


    fun send(obj: Any, contentType: String = "*/*") {
        sendBuffer = obj
        if (contentType != "*/*") {
            negotiatedMediaType = contentType
        }
    }


    fun negotiate(vararg negotiations: Pair<String, Response.() -> Unit>) {
        for ((mediaType, func) in negotiations) {
            if (requestedContentTypes.any { it.compareTo(mediaType, ignoreCase = true) == 0}) {
                func()
                negotiatedMediaType = mediaType
                return
            }
        }
        setStatus(StatusCodes.UnsupportedMediaType)
    }

    fun setStatus(statusCode: Int, statusDescription: String) {
        this.statusCode = statusCode
        this.statusDescription = statusDescription
    }

    fun setStatus(statusCode: StatusCodes, statusDescription: String = statusCode.description) {
        this.statusCode = statusCode.code
        this.statusDescription = statusDescription
    }

    fun setAllowedMethods(allowedMethods: Array<HttpMethod>) {
        addRawHeader("Allow", allowedMethods.map { it.name() }.joinToString(","))
    }

    fun addRawHeader(name: String, value: String) {
        if (value != ""){
            rawHeaders[name] = value
        }
    }

    private fun setResponseCookies() {
        for (cookie in cookies) {
            val name = cookie.value.name.toString()
            val value = cookie.value.value.toString()
            addRawHeader("Set-Cookie", ServerCookieEncoder.STRICT.encode(name, value).toString())
        }
    }


    fun setHeaders() {
        setResponseCookies()
        addRawHeader("ETag", etag)
        addRawHeader("Location", location)
        addRawHeader("Content-Type", contentType)
        if (contentLength != null) {
            addRawHeader("Content-Length", contentLength.toString())
        }
        addRawHeader("Connection", connection)
        addRawHeader("Date", convertToDateFormat(DateTime.now()!!))
        addRawHeader("Cache-Control", cacheControl)
        if (lastModified != null) {
            addRawHeader("Last-Modified", convertToDateFormat(lastModified!!))
        }
    }

    fun convertToDateFormat(dateTime: DateTime): String {
        val dt = DateTime(dateTime, DateTimeZone.forID("GMT"))
        val dtf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss")
        return "${dtf?.print(dt).toString()} GMT"
    }
}

