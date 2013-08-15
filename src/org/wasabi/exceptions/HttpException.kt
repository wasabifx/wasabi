package org.wasabi.exceptions


open public class HttpException(val statusCode: Int, val statusDescription: String): Exception() {

}