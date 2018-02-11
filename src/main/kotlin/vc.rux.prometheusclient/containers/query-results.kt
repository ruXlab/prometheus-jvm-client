package vc.rux.prometheusclient.containers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

sealed class InstantQueryResult<METRIC> {
    companion object {
        @JvmStatic
        protected val objectMapper = ObjectMapper()
    }
}

class InstantQueryVectorResult<METRIC> internal constructor(
    node: JsonNode, metricType: Class<METRIC>
) : InstantQueryResult<METRIC>() {

    val metric = objectMapper.treeToValue(node.with("metric"), metricType)

    val timestamp = Date(node.withArray("value").get(0).asLong())
    
    val value = node.withArray("value").get(1).asDouble()

    override fun toString(): String {
        return "InstantQueryVectorResult(metric=$metric, timestamp=$timestamp, value=$value)"
    }
}

class RangeQueryVectorResult<METRIC> internal constructor(
        node: JsonNode, metricType: Class<METRIC>
) : InstantQueryResult<METRIC>() {

    val metric = objectMapper.treeToValue(node.with("metric"), metricType)

    val values = node.withArray("values")
            .map { TimestampValuePair(Date(it.get(0).asLong()), it.get(1).asDouble()) }

    override fun toString(): String {
        return "RangeQueryVectorResult(metric=$metric, values=$values)"
    }
}