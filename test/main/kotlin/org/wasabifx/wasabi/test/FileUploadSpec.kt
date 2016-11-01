package org.wasabifx.wasabi.test

import org.junit.Test as spec
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.MediaType
import org.wasabifx.wasabi.app.FileUpload
import org.wasabifx.wasabi.configuration.ConfigurationStorage
import org.wasabifx.wasabi.interceptors.serveErrorsFromFolder
import org.wasabifx.wasabi.protocol.http.StatusCodes
import java.io.File

class FileUploadSpec: TestServerContext() {
    @spec fun fileupload_works() {
        val testTitle = "test title"
        TestServer.reset()
        TestServer.appServer.post("/fileupload", {
            if((request.bodyParams["file"] is FileUpload) &&
               (request.bodyParams["title"].toString() == testTitle)) {
                response.setStatus(StatusCodes.OK)
            } else {
                response.setStatus(StatusCodes.BadRequest)
            }
        })

        val client = OkHttpClient()
        val uploadFile = File(javaClass.classLoader.getResource("fileupload-test.txt").toURI())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", testTitle)
                .addFormDataPart("file", "file.txt", RequestBody.create(MediaType.parse("text/plain"), uploadFile))
                .build()

        val request = Request.Builder()
                .url("http://localhost:${TestServer.definedPort}/fileupload")
                .post(body)
                .build()

        val response = client.newCall(request).execute()
        assert(response.isSuccessful)
    }
}