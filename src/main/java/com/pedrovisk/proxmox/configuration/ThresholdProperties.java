package com.pedrovisk.proxmox.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("threshold.configuration")
public record ThresholdProperties(double freeMemory,
                                  double freeSwap,
                                  double freeRootfs) {
}
