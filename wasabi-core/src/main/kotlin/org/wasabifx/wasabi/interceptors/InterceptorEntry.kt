package org.wasabifx.wasabi.interceptors


data class InterceptorEntry(val interceptor: Interceptor, val path: String, val interceptOn: InterceptOn = InterceptOn.PreRequest)