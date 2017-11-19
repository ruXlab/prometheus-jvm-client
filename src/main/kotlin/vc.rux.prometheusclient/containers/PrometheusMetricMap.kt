package vc.rux.prometheusclient.containers


class PrometheusMetricMap: HashMap<String, String>() {
    val name: String? get() = get("__name__")
    val instance: String? get() = get("instance")
}