package org.wasabifx.wasabi.test

import org.apache.http.message.BasicNameValuePair
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class TestClientSpecs : TestServerContext(){

    @Test fun test_get_request() {
        TestServer.reset()
        TestServer.appServer.get("/testget", {
            response.send("Correct", "text/plain")
        })

        val client = TestClient(TestServer.appServer)

        assertEquals("Correct", client.sendSimpleRequest("/testget", TestClient.GET).body)
    }

    @Test fun test_multiple_get_requests() {
        TestServer.reset()
        TestServer.appServer.get("/testget", { response.send("Correct", "text/plain") })
        TestServer.appServer.get("/testget2", { response.send("Correct2", "text/plain") })

        val client = TestClient(TestServer.appServer)

        assertEquals("Correct", client.sendSimpleRequest("/testget", TestClient.GET).body)
        assertEquals("Correct2", client.sendSimpleRequest("/testget2", TestClient.GET).body)
    }

    @Test fun test_json_post_put_patch_json_string_requests() {
        TestServer.reset()
        TestServer.appServer.post("/json", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })
        TestServer.appServer.put("/json", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })
        TestServer.appServer.patch("/json", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })

        val client = TestClient(TestServer.appServer)

        assertEquals("posttest", client.sendJson("/json", TestClient.POST, """{"test":"posttest"}""").body)
        assertEquals("puttest", client.sendJson("/json", TestClient.PUT, """{"test":"puttest"}""").body)
        assertEquals("patchtest", client.sendJson("/json", TestClient.PATCH, """{"test":"patchtest"}""").body)
    }

    @Test fun test_json_post_put_patch_json_hashmap_requests() {
        TestServer.reset()
        TestServer.appServer.post("/json", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })
        TestServer.appServer.put("/json", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })
        TestServer.appServer.patch("/json", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })

        val client = TestClient(TestServer.appServer)

        assertEquals("posttest", client.sendJson("/json", TestClient.POST, hashMapOf("test" to "posttest")).body)
        assertEquals("puttest", client.sendJson("/json", TestClient.PUT, hashMapOf("test" to "puttest")).body)
        assertEquals("patchtest", client.sendJson("/json", TestClient.PATCH, hashMapOf("test" to "patchtest")).body)
    }

    @Test fun test_json_post_json_multilevel_hashmap_request() {
        TestServer.reset()
        TestServer.appServer.post("/json", { response.send(request.bodyParams["test"] ?: "key not found", "application/json") })

        val client = TestClient(TestServer.appServer)

        assertEquals("""{"another":"weeee"}""", client.sendJson("/json", TestClient.POST, hashMapOf("test" to hashMapOf("another" to "weeee"))).body)
    }

    @Test fun test_form_post_put_patch_arraylist_requests() {
        TestServer.reset()
        TestServer.appServer.post("/form", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })
        TestServer.appServer.put("/form", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })
        TestServer.appServer.patch("/form", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })

        val client = TestClient(TestServer.appServer)

        val postFormFields = ArrayList<BasicNameValuePair>()
        postFormFields.add(BasicNameValuePair("test", "posttest"))

        val putFormFields = ArrayList<BasicNameValuePair>()
        putFormFields.add(BasicNameValuePair("test", "puttest"))

        val patchFormFields = ArrayList<BasicNameValuePair>()
        patchFormFields.add(BasicNameValuePair("test", "patchtest"))

        assertEquals("posttest", client.sendForm("/form", TestClient.POST, postFormFields).body)
        assertEquals("puttest", client.sendForm("/form", TestClient.PUT, putFormFields).body)
        assertEquals("patchtest", client.sendForm("/form", TestClient.PATCH, patchFormFields).body)
    }

    @Test fun test_form_post_put_patch_hashmap_requests() {
        TestServer.reset()
        TestServer.appServer.post("/form", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })
        TestServer.appServer.put("/form", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })
        TestServer.appServer.patch("/form", { response.send(request.bodyParams["test"] ?: "key not found", "text/plain") })

        val client = TestClient(TestServer.appServer)

        assertEquals("posttest", client.sendForm("/form", TestClient.POST, hashMapOf("test" to "posttest")).body)
        assertEquals("puttest", client.sendForm("/form", TestClient.PUT, hashMapOf("test" to "puttest")).body)
        assertEquals("patchtest", client.sendForm("/form", TestClient.PATCH, hashMapOf("test" to "patchtest")).body)
    }

    @Test fun test_delete_options_requests() {
        TestServer.reset()
        TestServer.appServer.delete("/resource", { response.send("delete method", "text/plain") })
        TestServer.appServer.options("/resource", { response.send("options method", "text/plain") })
        // @TODO add head method test. For some unknown reason currently it doesn't work.


        val client = TestClient(TestServer.appServer)

        assertEquals("delete method", client.sendSimpleRequest("/resource", TestClient.DELETE).body)
        assertEquals("options method", client.sendSimpleRequest("/resource", TestClient.OPTIONS).body)
    }

    // @TODO add methods which test additional headers in request.
}