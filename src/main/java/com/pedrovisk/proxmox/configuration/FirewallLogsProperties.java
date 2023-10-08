package com.pedrovisk.proxmox.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("firewall-log.configuration")
public record FirewallLogsProperties(boolean enableLoadEverything) {
}
