package org.wasabi.routing

import org.wasabi.interceptors.Interceptor

data public class InterceptorEntry(val interceptor: Interceptor, val path: String, val interceptOn: InterceptOn = InterceptOn.PreRequest) {

}

public class InterceptorInitializer() {

}