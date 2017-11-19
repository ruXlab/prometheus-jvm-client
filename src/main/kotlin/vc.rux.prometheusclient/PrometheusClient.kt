package vc.rux.prometheusclient

import okhttp3.OkHttpClient
import okhttp3.Request

class PrometheusClient(val host: String, val httpClient: OkHttpClient = OkHttpClient()) {
    val baseUrl = "http://$host/api/v1/"


    private fun queryInstant(vararg params: Pair<String, String>) {
        val request = Request.Builder()
            .url("${baseUrl}/query")
            .get()
            .build()
        return httpClient.newCall(request).execute()
    }

}