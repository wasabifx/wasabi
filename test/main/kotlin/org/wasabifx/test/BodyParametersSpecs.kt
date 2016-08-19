package org.wasabifx.test

import org.junit.Test as spec
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.wasabi.routing.routeHandler
import java.util.ArrayList
import kotlin.test.assertEquals

/**
 * Created by swishy on 25/11/15.
 */
public class BodyParametersSpecs : TestServerContext() {

    val createMember = routeHandler {
        var memberName = request.bodyParams["memberName"] as String?
        assert(memberName == "bob")
    }

    @spec fun requests_should_have_isolated_body_params () {

        TestServer.appServer.post("/params", {
            response.send(request.bodyParams.size.toString())
        })

        var urlParameters  = ArrayList<NameValuePair>();
        urlParameters.add(BasicNameValuePair("foo", "bar"));

        var urlParameters2  = ArrayList<NameValuePair>();
        urlParameters2.add(BasicNameValuePair("bar", "baz"));

        var response = post("http://localhost:${TestServer.definedPort}/params", hashMapOf(), urlParameters)

        assert(response.body.contains("1"))

        var response2 = post("http://localhost:${TestServer.definedPort}/params", hashMapOf(), urlParameters2)

        assert(response2.body.contains("1"))

    }

    @spec fun body_params_present_on_request () {

        TestServer.appServer.post("/body", createMember)

        var urlParameters3  = ArrayList<NameValuePair>();
        urlParameters3.add(BasicNameValuePair("memberName", "bob"));

        post("http://localhost:${TestServer.definedPort}/body", hashMapOf(), urlParameters3)
    }
}