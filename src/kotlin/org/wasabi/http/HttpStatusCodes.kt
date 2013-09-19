package org.wasabi.http


enum class HttpStatusCodes {

    abstract val statusCode: Int
    open val statusDescription: String
        get() = this.toString().replaceAll("([A-Z])", " $1")

    // 1XX
    Continue {
        override val statusCode: Int = 100
    }
    SwitchingProtocols {
        override val statusCode: Int = 101
    }
    Processing {
        override val statusCode: Int = 102
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
    NonAuthoritativeInformation {
        override val statusCode: Int = 203
    }
    NoContent {
        override val statusCode: Int = 204

    }
    ResetContent {
        override val statusCode: Int = 205

    }
    PartialContent {
        override val statusCode: Int = 206

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
    UseProxy {
        override val statusCode: Int = 305
    }
    SwitchProxy {
        override val statusCode: Int = 306
    }
    TemporaryRedirect {
        override val statusCode: Int = 307
    }
    PermanentRedirect {
        override val statusCode: Int = 308
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
    NotAcceptable {
        override val statusCode: Int = 406
    }
    ProxyAuthenticationRequired {
        override val statusCode: Int = 407
    }
    RequestTimeout {
        override val statusCode: Int = 408
    }
    Conflict {
        override val statusCode: Int = 409
    }
    Gone {
        override val statusCode: Int = 410
    }
    LengthRequired {
        override val statusCode: Int = 411
    }
    PreconditionFailed {
        override val statusCode: Int = 412
    }
    RequestEntityTooLarge {
        override val statusCode: Int = 413
    }
    RequestURITooLarge {
        override val statusCode: Int = 414
        override val statusDescription : String = "Request-URI Too Large"
    }
    UnsupportedMediaType {
        override val statusCode: Int = 415
    }
    RequestedRageNotSatisfiable {
        override val statusCode: Int = 416
    }
    ExceptionFailed {
        override val statusCode: Int = 417
    }
    TooManyRequests {
        override val statusCode: Int = 429
    }
    RequestHeaderFieldTooLarge {
        override val statusCode: Int = 431
    }

    // 5XX
    InternalServerError {
        override val statusCode: Int = 500
    }
    NotImplemented {
        override val statusCode: Int = 501
    }
    BadGateway {
        override val statusCode: Int = 502
    }
    ServiceUnavailable {
        override val statusCode: Int = 503
    }
    GatewayTimeout {
        override val statusCode: Int = 504
    }
    VersionNotSupported {
        override val statusCode: Int = 505
    }
    VariantAlsoNegotiates {
        override val statusCode: Int = 506
    }
    InsufficientStorage {
        override val statusCode: Int = 507
    }
    BandwidthLimitExceeded {
        override val statusCode: Int = 509
    }

}