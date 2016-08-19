package org.wasabifx.wasabi.interceptors

enum class InterceptOn {
    PreRequest,
    PreExecution,
    PostExecution,
    PostRequest,
    Error
}