package vc.rux.prometheusclient.containers

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

sealed class InstantQueryResult 

class InstantQueryVectorResult internal constructor(
    node: JsonNode
) : InstantQueryResult() {
    val metric = PrometheusMetricMap().also {
        for ((key, jsonNode) in node.with("metric").fields())
            it.put(key, jsonNode.textValue())
    }

    val timestamp = Date(node.withArray("value").get(0).asLong())
    
    val value = node.withArray("value").get(1).asDouble()

    override fun toString(): String {
        return "InstantQueryVectorResult(metric=$metric, timestamp=$timestamp, value=$value)"
    }


}