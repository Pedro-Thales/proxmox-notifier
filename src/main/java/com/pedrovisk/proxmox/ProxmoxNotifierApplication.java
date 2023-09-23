package com.pedrovisk.proxmox;

import com.pedrovisk.proxmox.configuration.ProxmoxProperties;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({ProxmoxProperties.class, ThresholdProperties.class})
@EnableScheduling
public class ProxmoxNotifierApplication {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	public static void main(String[] args) {
		SpringApplication.run(ProxmoxNotifierApplication.class, args);
	}

}
