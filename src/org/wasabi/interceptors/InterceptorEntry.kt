package org.wasabi.interceptors

import org.wasabi.routing.InterceptOn

data public class InterceptorEntry(val interceptor: Interceptor, val path: String, val interceptOn: InterceptOn = InterceptOn.PreRequest)