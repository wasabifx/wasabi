package org.wasabifx.wasabi.interceptors


data public class InterceptorEntry(val interceptor: Interceptor, val path: String, val interceptOn: InterceptOn = InterceptOn.PreRequest)