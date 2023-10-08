package com.pedrovisk.proxmox;

import com.pedrovisk.proxmox.configuration.FirewallLogsProperties;
import com.pedrovisk.proxmox.configuration.ProxmoxProperties;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import feign.Logger;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({ProxmoxProperties.class, ThresholdProperties.class, FirewallLogsProperties.class})
@EnableScheduling
public class ProxmoxNotifierApplication {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
		return new ObservedAspect(observationRegistry);
	}

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

	public static void main(String[] args) {
		SpringApplication.run(ProxmoxNotifierApplication.class, args);
	}

}
