package com.pedrovisk.proxmox.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("telegram.bot")
public record TelegramProperties(String token, String chatId, String dbPath) {
}
