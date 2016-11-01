package org.wasabifx.wasabi.authentication

import org.wasabifx.wasabi.encoding.decodeBase64
import org.wasabifx.wasabi.protocol.http.Request
import org.wasabifx.wasabi.protocol.http.Response

public class BasicAuthentication(val realm: String, val callback: (String, String) -> Boolean, val path: String = "*") : Authentication {
    override fun authenticate(request: Request, response: Response): Boolean {
        if (request.authorization != "") {
            val credentialsBase64Encoded = request.authorization.dropWhile { it != ' ' }
            val credentialsDecoded = credentialsBase64Encoded.decodeBase64("base64")
            val credentials = credentialsDecoded.split(':')
            if (callback(credentials[0], credentials[1])) {
                return true
            }
        }
        response.addRawHeader("WWW-Authenticate", "Basic Realm=${realm}")
        return false
    }
}