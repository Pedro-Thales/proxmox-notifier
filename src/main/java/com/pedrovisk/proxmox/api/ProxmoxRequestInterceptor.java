package com.pedrovisk.proxmox.api;

import com.pedrovisk.proxmox.configuration.ProxmoxProperties;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class ProxmoxRequestInterceptor implements RequestInterceptor {

    @Autowired
    private ProxmoxProperties proxmoxProperties;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization",
                "PVEAPIToken=" + proxmoxProperties.authToken());
    }
}

