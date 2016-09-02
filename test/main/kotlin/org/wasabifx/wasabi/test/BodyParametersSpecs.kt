package org.wasabifx.wasabi.test

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.wasabifx.wasabi.routing.routeHandler
import java.util.*
import org.junit.Test as spec

/**
 * Created by swishy on 25/11/15.
 */
class BodyParametersSpecs : TestServerContext() {

    val createMember = routeHandler {
        var memberName = request.bodyParams["memberName"]
        assert(memberName == "bob")
    }

    @spec fun requests_should_have_isolated_body_params() {

        TestServer.appServer.post("/params", {
            response.send(request.bodyParams.size.toString())
        })

        val urlParameters = ArrayList<NameValuePair>()
        urlParameters.add(BasicNameValuePair("foo", "bar"))

        val urlParameters2 = ArrayList<NameValuePair>()
        urlParameters2.add(BasicNameValuePair("bar", "baz"))

        val response = post("http://localhost:${TestServer.definedPort}/params", hashMapOf(), urlParameters)

        assert(response.body.contains("1"))

        val response2 = post("http://localhost:${TestServer.definedPort}/params", hashMapOf(), urlParameters2)

        assert(response2.body.contains("1"))

    }

    @spec fun body_params_present_on_request() {

        TestServer.appServer.post("/body", createMember)

        var urlParameters3 = ArrayList<NameValuePair>()
        urlParameters3.add(BasicNameValuePair("memberName", "bob"))

        post("http://localhost:${TestServer.definedPort}/body", hashMapOf(), urlParameters3)
    }
}