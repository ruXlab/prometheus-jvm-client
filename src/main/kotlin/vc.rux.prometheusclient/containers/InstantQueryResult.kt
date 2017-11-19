package vc.rux.prometheusclient.containers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

sealed class InstantQueryResult 

class InstantQueryVectorResult<METRIC> internal constructor(
    node: JsonNode, metricType: Class<METRIC>
) : InstantQueryResult() {

    val metric = ObjectMapper().treeToValue(node.with("metric"), metricType)

    val timestamp = Date(node.withArray("value").get(0).asLong())
    
    val value = node.withArray("value").get(1).asDouble()

    override fun toString(): String {
        return "InstantQueryVectorResult(metric=$metric, timestamp=$timestamp, value=$value)"
    }


}