package com.pedrovisk.proxmox.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("ssh.configuration")
public record SshProperties(String username, String password, String host, int port) {
}
