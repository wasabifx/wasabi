package org.wasabi.interceptors


import org.wasabi.http.Request
import org.wasabi.http.Response
import org.wasabi.http.ContentType
import org.apache.commons.codec.binary;
import io.netty.handler.codec.base64.Base64Decoder
import org.apache.commons.codec.binary.Base64


public class BasicAuthenticationInterceptor(val callback: (String, String) -> Boolean): BeforeRequestInterceptor {
    override fun handle(request: Request, response: Response): Boolean {

        if (request.authorization != "") {
            val credentialsBase64Encoded = request.authorization.dropWhile { it != ' ' }
            val credentialsDecoded = credentialsBase64Encoded.decode("base64")
            val credentials = credentialsDecoded.split(':')
            if (callback(credentials[0], credentials[1])) {
                return true
            }
        }
        response.setStatus(401, "Authentication Failed")
        response.setResponseContentType(ContentType.TextPlain)
        response.send("Authentication Failed")
        response.addExtraHeader("WWW-Authenticate", "Basic Realm=Secure")
        return false
    }
}

// TODO: Move this out
fun String.decode(encoding: String): String {
    if (encoding == "base64") {
        return String(Base64.decodeBase64(this)!!)
    } else {
        throw IllegalArgumentException()
    }
}
