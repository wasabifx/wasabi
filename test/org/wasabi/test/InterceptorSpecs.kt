package org.wasabi.test


import org.junit.Test as spec
import org.wasabi.routing.Routes
import org.wasabi.routing.InterceptorOccurence

public class InterceptorSpecs {

    spec fun add_an_interceptor_to_route() {

        Routes.intercept("/books", InterceptorOccurence.PreRequest, {


            handled = true

        })

    }
}