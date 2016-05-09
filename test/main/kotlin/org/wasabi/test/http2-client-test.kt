package org.wasabi.test

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Created by condaa1 on 15/04/16.
 */

var client = OkHttpClient();

fun main(args: Array<String>) {

    var request = Request.Builder()
            .url("http://localhost:3000/js")
            .header("SecurityToken", "b08c85073c1a2d02")
            .header("Accept", "application/json")
            .header("Accept-Encoding", "gzip, deflate").build();

    var response = client.newCall(request).execute();
    System.out.println(response.body().string())
}
