package com.pedrovisk.proxmox;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class WiremockConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockProxmoxService() {
        return new WireMockServer(8006);
    }
}