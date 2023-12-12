package com.pedrovisk.proxmox;

import com.pedrovisk.proxmox.configuration.*;
import com.pedrovisk.proxmox.telegram.PxmxNotifierBot;
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
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({ProxmoxProperties.class, ThresholdProperties.class, FirewallLogsProperties.class,
		TelegramProperties.class, SshProperties.class})
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
		var context = SpringApplication.run(ProxmoxNotifierApplication.class, args);
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(context.getBean("pxmxNotifierBot", PxmxNotifierBot.class));
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

}
