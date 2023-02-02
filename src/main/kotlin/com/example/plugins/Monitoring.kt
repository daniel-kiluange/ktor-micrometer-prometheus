package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import java.time.Duration

val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun Application.configureMonitoring() {
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        metricName = "test_app"

        distributionStatisticConfig = DistributionStatisticConfig.Builder()
            .percentilesHistogram(true)
            .maximumExpectedValue(Duration.ofSeconds(20).toNanos().toDouble())
            .serviceLevelObjectives(
                Duration.ofMillis(100).toNanos().toDouble(),
                Duration.ofMillis(500).toNanos().toDouble()
            )
            .build()

        meterBinders = listOf(
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            ProcessorMetrics()
        )
    }

}
