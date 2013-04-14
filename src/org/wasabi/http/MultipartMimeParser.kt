package org.wasabi.http

public class MultipartMimeParser: BodyParser {

    override fun parseBody(rawBody: String): BodyParams {
        val bodyParams = BodyParams()
        if (rawBody.size > 0) {

        }
        return bodyParams
    }
}