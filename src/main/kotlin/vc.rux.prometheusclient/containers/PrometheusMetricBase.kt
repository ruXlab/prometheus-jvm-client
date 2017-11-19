package vc.rux.prometheusclient.containers

import com.fasterxml.jackson.annotation.JsonProperty

open class PrometheusMetricBase() {
    @JsonProperty("__name__")
    var name: String? = null

    @JsonProperty("instance")
    var instance: String? = null

    @JsonProperty("job")
    var job: String? = null

}