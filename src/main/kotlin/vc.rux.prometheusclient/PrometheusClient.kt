package vc.rux.prometheusclient

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import vc.rux.prometheusclient.containers.InstantQueryResult
import vc.rux.prometheusclient.containers.InstantQueryVectorResult
import java.util.*

class PrometheusClient(
    private val host: String,
    private val port: Int = 9090,
    private val httpClient: OkHttpClient = OkHttpClient()
) {
    val baseUrl = "http://$host/api/v1/"

    fun instant(query: String, time: Date? = null, timeout: Long? = null): List<InstantQueryResult> {
        val response = queryInstant(
            "query" to query,
            "time" to time?.time?.div(1000.0)?.toString(),
            "timeout" to timeout?.toString()
        )
        val tree = objectReader.readTree(response.body()?.byteStream()).get("data")

        return when(tree.get("resultType").textValue()) {
            "vector" -> parseVector(tree.get("result"))
//            "matrix" -> NotImplementedError
//            "scalar" ->
//            "string" -> NotImplementedError("")
            else -> throw NotImplementedError("Given result type ${tree.get("resultType").textValue()} is not supported")
        }
    }

    private fun parseVector(vector: JsonNode): List<InstantQueryVectorResult> =
        vector.map(::InstantQueryVectorResult)
    

    private fun queryInstant(vararg params: Pair<String, String?>): Response {
        val url = HttpUrl.Builder()
            .scheme("http")
            .host(host)
            .port(port)
            .addPathSegments("/api/v1/query")
            .apply { params.filter {it.second != null}.forEach { addQueryParameter(it.first, it.second) } }
            .build()

        log.trace("Querying {}", url)

        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return httpClient.newCall(request).execute()
    }

    companion object {
        val log = LoggerFactory.getLogger(PrometheusClient::class.java)
        val objectReader = ObjectMapper().reader()
    }

}