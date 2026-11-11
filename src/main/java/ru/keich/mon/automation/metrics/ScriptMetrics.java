package ru.keich.mon.automation.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

import ru.keich.mon.automation.script.ScriptService;

@Component
public class ScriptMetrics {

	public ScriptMetrics(MeterRegistry registry, ScriptService scriptService) {

		Gauge.builder("automation.scripts.active", scriptService, ScriptService::getTotalActiveScripts)
				.description("Currently active scripts").register(registry);
	}
}