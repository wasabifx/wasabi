package org.wasabi.http



enum class StatusCodes(val code: Int) {

    open val description: String
        get() = this.toString().replaceAll("([A-Z])", " $1")
    // 1XX
    Continue: StatusCodes(100)
    SwitchingProtocols: StatusCodes(101)
    Processing: StatusCodes(102)
    // 2XX

    OK: StatusCodes(200)
    Created: StatusCodes(201)
    Accepted: StatusCodes(202)
    NonAuthoritativeInformation: StatusCodes(203)
    NoContent: StatusCodes(204)
    ResetContent: StatusCodes(205)
    PartialContent: StatusCodes(206)

    // 3XX
    MultipleChoices: StatusCodes(300)
    MovedPermanently: StatusCodes(301)
    Found: StatusCodes(302)
    SeeOther: StatusCodes(303)
    NotModified: StatusCodes(304)
    UseProxy: StatusCodes(305)
    SwitchProxy: StatusCodes(306)
    TemporaryRedirect: StatusCodes(307)
    PermanentRedirect: StatusCodes(308)

    // 4XX
    BadRequest: StatusCodes(400)
    Unauthorized: StatusCodes(401)
    PaymentRequired: StatusCodes(402)
    Forbidden: StatusCodes(403)
    NotFound: StatusCodes(404)
    MethodNotAllowed: StatusCodes(405)
    NotAcceptable: StatusCodes(406)
    ProxyAuthenticationRequired: StatusCodes(407)
    RequestTimeout: StatusCodes(408)
    Conflict: StatusCodes(409)
    Gone: StatusCodes(410)
    LengthRequired: StatusCodes(411)
    PreconditionFailed: StatusCodes(412)
    RequestEntityTooLarge: StatusCodes(413)
    RequestURITooLarge: StatusCodes(414) {
        override val description: String = "Request-URI Too Large"
    }
    UnsupportedMediaType: StatusCodes(415)
    RequestedRageNotSatisfiable: StatusCodes(416)
    ExceptionFailed: StatusCodes(417)
    TooManyRequests: StatusCodes(429)
    RequestHeaderFieldTooLarge: StatusCodes(431)

    // 5XX
    InternalServerError: StatusCodes(500)
    NotImplemented: StatusCodes(501)
    BadGateway: StatusCodes(502)
    ServiceUnavailable: StatusCodes(503)
    GatewayTimeout: StatusCodes(504)
    VersionNotSupported: StatusCodes(505)
    VariantAlsoNegotiates: StatusCodes(506)
    InsufficientStorage: StatusCodes(507)
    BandwidthLimitExceeded: StatusCodes(509)

}