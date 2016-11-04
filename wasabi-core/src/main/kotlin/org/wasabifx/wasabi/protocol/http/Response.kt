package org.wasabifx.wasabi.protocol.http

import io.netty.handler.codec.http.HttpMethod
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.util.*
import javax.activation.MimetypesFileTypeMap

val headerNames = listOf(
        "Etag",
        "Location",
        "Content-Type",
        "Connection",
        "Date",
        "Cache-Control",
        "Content-Length",
        "Last-Modified",
        "Set-Cookie"
)

class Response() {

    private val rawHeaders = HashMap<String, String>()

    var etag: String = ""
    var resourceId: String? = null
    var location: String = ""
    var contentType: String = ContentType.Companion.Text.Plain.toString()
    var contentLength: Long = 0
    var statusCode: Int = 200
    var statusDescription: String = ""
    var allow: String = ""
    var sendBuffer: Any? = null
        private set
    var overrideContentNegotiation: Boolean = false
    val cookies: HashMap<String, Cookie> = HashMap()
    var requestedContentTypes: ArrayList<String> = arrayListOf()
    var negotiatedMediaType: String = ""
    var connection: String = "close"
    var cacheControl: String = "max-age=0"
    var lastModified: DateTime? = null


    fun redirect(url: String, redirectType: StatusCodes = StatusCodes.Found) {
        setStatus(redirectType)
        location = url
    }

    @Deprecated("Use sendFile() instead", ReplaceWith("sendFile(filename, contentType)"))
    fun setFileResponseHeaders(filename: String, contentType: String = "*/*") {
        this.sendFile(filename, contentType)
    }

    fun sendFile(filename: String, contentType: String = "*/*") {

        val file = File(filename)
        if (file.exists() && !file.isDirectory) {
            sendBuffer = file.readBytes()

            val fileContentType: String?
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
            this.negotiatedMediaType = fileContentType ?: "application/unknown"
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
            if (requestedContentTypes.any { it.compareTo(mediaType, ignoreCase = true) == 0 }) {
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
        if (headerNames.contains(name)) {
            throw InvalidHeaderNameException("Setting $name header is not supported here. It should be handled as Response property")
        }

        if (value != "") {
            rawHeaders[name] = value
        }
    }

    fun getHeaders(): HashMap<String, String> {
        rawHeaders["Etag"] = etag
        rawHeaders["Location"] = location
        rawHeaders["ContentType"] = contentType
        rawHeaders["Connection"] = connection
        rawHeaders["Date"] = convertToDateFormat(DateTime.now())
        rawHeaders["Cache-Control"] = cacheControl
        if (contentLength != 0L) {
            rawHeaders["Content-Length"] = contentLength.toString()
        }
        if (lastModified != null) {
            rawHeaders["Last-Modified"] = convertToDateFormat(lastModified!!)
        }

        for (cookie in cookies) {
            rawHeaders["Set-Cookie"]=  cookie.toString()
        }
        return rawHeaders
    }

    fun getCookie(name: String): Cookie? {
        return cookies[name] ?: null
    }

    fun setCookie(cookie: Cookie) {
        cookies[cookie.name()] = cookie
    }

    fun convertToDateFormat(dateTime: DateTime): String {
        val dt = DateTime(dateTime, DateTimeZone.forID("GMT"))
        val dtf = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss")
        return "${dtf?.print(dt).toString()} GMT"
    }
}

