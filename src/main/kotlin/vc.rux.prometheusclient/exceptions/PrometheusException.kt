package vc.rux.prometheusclient.exceptions

class PrometheusException(
        val errorType: String,
        val error: String
) : RuntimeException("$errorType: $error")