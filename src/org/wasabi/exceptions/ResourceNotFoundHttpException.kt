package org.wasabi.exceptions

public class ResourceNotFoundHttpException(val message: String = "Not found"): HttpException(404, message) {
}