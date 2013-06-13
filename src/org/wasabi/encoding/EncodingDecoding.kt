package org.wasabi.encoding

import org.apache.commons.codec.binary.Base64


fun String.decode(encoding: String): String {
    if (encoding == "base64") {
        return String(Base64.decodeBase64(this)!!)
    } else {
        throw IllegalArgumentException()
    }
}

