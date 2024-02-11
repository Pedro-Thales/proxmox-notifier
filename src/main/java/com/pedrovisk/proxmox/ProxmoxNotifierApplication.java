package com.pedrovisk.proxmox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrovisk.proxmox.configuration.*;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.telegram.PxmxNotifierBot;
import feign.Logger;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({ProxmoxProperties.class, ThresholdProperties.class, FirewallLogsProperties.class,
		TelegramProperties.class, SshProperties.class})
@EnableScheduling
@Slf4j
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

	@Bean
	public RootConfiguration loadConfigurationFile(@Value("${proxmox.configuration-file-path}") Resource path) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		try (InputStream in = path.getInputStream()) {
            //TODO make all configs get from the config json
			return objectMapper.readValue(in, RootConfiguration.class);
		} catch (Exception e) {
			log.error("Configuration file not found! " +
					"Please make sure that the file is available and readable in the path /config/config.json", e);
		}
        return null;
    }

	//TODO put a time/quantity limit to sent notifications, and email

	public static void main(String[] args) {
		var context = SpringApplication.run(ProxmoxNotifierApplication.class, args);
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(context.getBean("pxmxNotifierBot", PxmxNotifierBot.class));
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	@EventListener
	void ready(ApplicationReadyEvent readyEvent) {
		System.out.println("APP IS READY to...");
	}

}
