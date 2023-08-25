package com.pedrovisk.proxmox.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("proxmox")
public record ProxmoxProperties(String apiUrl, String authToken) {
}
