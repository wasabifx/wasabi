package org.wasabi.http


enum class HttpStatusCodes {

    abstract val statusCode: Int
    open val statusDescription: String
        get() = this.toString().replaceAll("([A-Z])", " $1")

    // 1XX
    Continue {
        override val statusCode: Int = 100
    }

    // 2XX
    OK {
        override val statusCode: Int = 200
    }
    Created {
        override val statusCode: Int = 201
    }
    Accepted {
        override val statusCode: Int = 202
    }

    // 3XX
    MultipleChoices {
        override val statusCode: Int = 300
    }
    MovedPermanently {
        override val statusCode: Int = 301
    }
    Found {
        override val statusCode: Int = 302
    }
    SeeOther {
        override val statusCode: Int = 303
    }
    NotModified {
        override val statusCode: Int = 304
    }

    // 4XX
    BadRequest {
        override val statusCode: Int = 400
    }
    Unauthorized {
        override val statusCode: Int = 401
    }
    PaymentRequired {
        override val statusCode: Int = 402
    }
    Forbidden {
        override val statusCode: Int = 403
    }
    NotFound {
        override val statusCode: Int = 404
    }
    MethodNotAllowed {
        override val statusCode: Int = 405
    }

    // 5XX
    InternalServerError {
        override val statusCode: Int = 500
    }

}