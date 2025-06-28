package com.pedrovisk.proxmox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedrovisk.proxmox.configuration.FirewallLogsProperties;
import com.pedrovisk.proxmox.configuration.ProxmoxProperties;
import com.pedrovisk.proxmox.configuration.SshProperties;
import com.pedrovisk.proxmox.configuration.TelegramProperties;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import feign.Logger;
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

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({ProxmoxProperties.class, FirewallLogsProperties.class, TelegramProperties.class,
		SshProperties.class})
@EnableScheduling
@Slf4j
public class ProxmoxNotifierApplication {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.BASIC;
	}

	@Bean
	public RootConfiguration loadConfigurationFile(@Value("${proxmox.configuration-file-path}") Resource path) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		try (InputStream in = path.getInputStream()) {
            //TODO make all configs get from the config json
			//TODO validate json at start and throw errors or warnings at start to logs,
			// and maybe notification? If enabled like sendNotificationsOnErrors?
			return objectMapper.readValue(in, RootConfiguration.class);
		} catch (Exception e) {
			log.error("Configuration file not found! " +
					"Please make sure that the file is available and readable in the path /config/config.json", e);
		}
        return null;
    }

	public static void main(String[] args) {
		//https://rubenlagus.github.io/TelegramBotsDocumentation/how-to-update-7.html#migrating-your-existing-longpolling-bots
		SpringApplication.run(ProxmoxNotifierApplication.class, args);
	}

	@EventListener
	void ready(ApplicationReadyEvent readyEvent) {
		log.info("APP IS READY");
	}

}
