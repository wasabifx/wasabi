package org.wasabi.test

import org.junit.Test as spec
import org.wasabai.test.get
import kotlin.test.assertEquals
import org.wasabi.http.ContentType
import org.wasabi.routing.InterceptOn
import org.wasabai.test.TestServer
import org.wasabi.routing.InterceptorInitializer

public class InterceptorSpecs {

    spec fun init_should_classify_interceptors_into_four_groups() {

        val interceptorInitializer = InterceptorInitializer()

        interceptorInitializer.create()



    }
}