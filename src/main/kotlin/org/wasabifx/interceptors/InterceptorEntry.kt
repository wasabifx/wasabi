package org.wasabifx.interceptors

import org.wasabifx.routing.InterceptOn

data public class InterceptorEntry(val interceptor: Interceptor, val path: String, val interceptOn: InterceptOn = InterceptOn.PreRequest)