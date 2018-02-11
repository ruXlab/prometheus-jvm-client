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
import vc.rux.prometheusclient.containers.PrometheusMetricMap
import vc.rux.prometheusclient.containers.RangeQueryVectorResult
import vc.rux.prometheusclient.exceptions.PrometheusException
import java.util.*

class PrometheusClient(
    private val host: String,
    private val port: Int = 9090,
    private val httpClient: OkHttpClient = OkHttpClient()
) {
    private val baseUrl = "http://$host/api/v1/"

    fun <METRIC> instantVector(query: String, time: Date? = null, timeout: Long? = null, metricMapClass: Class<METRIC>): List<InstantQueryVectorResult<METRIC>> =
        instant(query, time, timeout, metricMapClass) as List<InstantQueryVectorResult<METRIC>>

    fun <METRIC> rangeVector(query: String, time: Date? = null, timeout: Long? = null, metricMapClass: Class<METRIC>): List<RangeQueryVectorResult<METRIC>> =
            instant(query, time, timeout, metricMapClass) as List<RangeQueryVectorResult<METRIC>>

    fun <METRIC> instant(query: String, time: Date? = null, timeout: Long? = null, metricMapClass: Class<METRIC>): List<InstantQueryResult<METRIC>> {
        val response = queryInstant(
            "query" to query,
            "time" to time?.time?.div(1000.0)?.toString(),
            "timeout" to timeout?.toString()
        )

        val root = objectReader.readTree(response.body()?.byteStream())
        if (root.get("status").asText() ==  "error")
            throw PrometheusException(root.get("errorType").asText(), root.get("error").asText())

        val tree = root.get("data")

        return when(tree.get("resultType").textValue()) {
            "vector" -> parseVector(tree.get("result"), metricMapClass)
            "matrix" -> parseMatrix(tree.get("result"), metricMapClass)
//            "scalar" ->
//            "string" -> NotImplementedError("")
            else -> throw NotImplementedError("Given result type ${tree.get("resultType").textValue()} is not supported")
        }
    }

    private fun <METRIC> parseMatrix(matrix: JsonNode, metricMapClass: Class<METRIC>): List<InstantQueryResult<METRIC>> =
        matrix.map { node -> RangeQueryVectorResult(node, metricMapClass) }


    private fun <METRIC> parseVector(vector: JsonNode, metricType: Class<METRIC>): List<InstantQueryVectorResult<METRIC>> =
        vector.map { node -> InstantQueryVectorResult(node, metricType) }


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