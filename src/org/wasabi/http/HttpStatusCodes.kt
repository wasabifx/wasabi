package org.wasabi.http

/*
open data public class HttpStatus(val statusCode: Int, val statusDescription: String)


public object BadRequest: HttpStatus(400, "Bad Request")
public object Unauthorized: HttpStatus(401, "Unauthorized")
public object PaymentRequired: HttpStatus(402, "Payment Required")
public object Forbidden: HttpStatus(403, "Forbidden")
public object NotFound: HttpStatus(404, "Not Found")
public object MethodNotAllowed: HttpStatus(404, "Method Not Allowed")


public object InternalServerError: HttpStatus(500, "Internal Server Error")*/

enum class HttpStatusCodes {

    // TODO: Make default status Description convert CamelCase to Space
    abstract val statusCode: Int
    abstract val statusDescription: String

    // 1XX
    Continue {
        override val statusCode: Int = 100
        override val statusDescription: String = "Continue"

    }

    // 2XX
    OK {
        override val statusCode: Int = 200
        override val statusDescription: String = "OK"

    }
    Created {
        override val statusCode: Int = 201
        override val statusDescription: String = "Created"

    }
    Accepted {
        override val statusCode: Int = 202
        override val statusDescription: String = "Accepted"

    }

    // 3XX
    MultipleChoices {
        override val statusCode: Int = 300
        override val statusDescription: String = "Mutliple Choices"

    }
    MovedPermanently {
        override val statusCode: Int = 301
        override val statusDescription: String = "Moved Permanently"

    }
    Found {
        override val statusCode: Int = 302
        override val statusDescription: String = "Found"

    }
    SeeOther {
        override val statusCode: Int = 303
        override val statusDescription: String = "See Other"

    }
    NotModified {
        override val statusCode: Int = 304
        override val statusDescription: String = "Not Modified"

    }

    // 4XX
    BadRequest {
        override val statusCode: Int = 400
        override val statusDescription: String = "Bad Request"

    }
    Unauthorized {
        override val statusCode: Int = 401
        override val statusDescription: String = "Unauthorized"

    }
    PaymentRequired {
        override val statusCode: Int = 402
        override val statusDescription: String = "Payment Required"

    }
    Forbidden {
        override val statusCode: Int = 403
        override val statusDescription: String = "Forbidden"

    }
    NotFound {
        override val statusCode: Int = 404
        override val statusDescription: String = "Not Found"

    }
    MethodNotAllowed {
        override val statusCode: Int = 405
        override val statusDescription: String = "Method Not Allowed"

    }

    // 5XX
    InternalServerError {
        override val statusCode: Int = 500
        override val statusDescription: String = "Internal Server Error"

    }

}