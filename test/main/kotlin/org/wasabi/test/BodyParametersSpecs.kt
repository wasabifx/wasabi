package org.wasabi.test

import org.junit.Test as spec
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import java.util.ArrayList
import kotlin.test.assertEquals

/**
 * Created by swishy on 25/11/15.
 */
public class BodyParametersSpecs : TestServerContext() {

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
}