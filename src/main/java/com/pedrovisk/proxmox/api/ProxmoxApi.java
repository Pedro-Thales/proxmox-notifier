package com.pedrovisk.proxmox.api;


import com.pedrovisk.proxmox.CustomFeignConfiguration;
import com.pedrovisk.proxmox.models.proxmox.CommandRequest;
import com.pedrovisk.proxmox.models.proxmox.Root;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "proxmox", url = "${proxmox.api-url}", configuration = {ProxmoxRequestInterceptor.class, CustomFeignConfiguration.class})
public interface ProxmoxApi {


    @RequestMapping(method = RequestMethod.GET, value = "nodes/pxmx/status")
    @MeasureRunTime
    Root getNodeStatus();

    @RequestMapping(method = RequestMethod.POST, value = "nodes/pxmx/status", consumes = "application/json")
    @MeasureRunTime
    Root shutdownNode(CommandRequest commandRequest);

    @RequestMapping(method = RequestMethod.POST, value = "nodes/pxmx/stopall")
    @MeasureRunTime
    Root stopEverything();

}
