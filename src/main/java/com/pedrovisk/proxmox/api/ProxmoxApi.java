package com.pedrovisk.proxmox.api;


import com.pedrovisk.proxmox.CustomFeignConfiguration;
import com.pedrovisk.proxmox.models.proxmox.CommandRequest;
import com.pedrovisk.proxmox.models.proxmox.LxcStatusRoot;
import com.pedrovisk.proxmox.models.proxmox.NodeStatusRoot;
import com.pedrovisk.proxmox.models.proxmox.VmStatusRoot;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogRoot;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "proxmox", url = "${proxmox.api-url}", configuration = {ProxmoxRequestInterceptor.class, CustomFeignConfiguration.class})
public interface ProxmoxApi {


    @RequestMapping(method = RequestMethod.GET, value = "nodes/{nodeName}/status")
    @MeasureRunTime
    NodeStatusRoot getNodeStatus(@PathVariable String nodeName);

    @RequestMapping(method = RequestMethod.GET, value = "nodes/{nodeName}/lxc/{lxcId}/status/current")
    @MeasureRunTime
    LxcStatusRoot getLxcContainersStatus(@PathVariable String nodeName, @PathVariable String lxcId);

    @RequestMapping(method = RequestMethod.GET, value = "nodes/{nodeName}/qemu/{vmId}/status/current")
    @MeasureRunTime
    VmStatusRoot getVmStatus(@PathVariable String nodeName, @PathVariable String vmId);

    @RequestMapping(method = RequestMethod.GET, value = "nodes/{nodeName}/firewall/log")
    @MeasureRunTime
    FirewallLogRoot getFirewallLog(@PathVariable String nodeName, @RequestParam int start);

    @RequestMapping(method = RequestMethod.POST, value = "nodes/{nodeName}/status", consumes = "application/json")
    @MeasureRunTime
    NodeStatusRoot shutdownNode(@PathVariable String nodeName, CommandRequest commandRequest);

    @RequestMapping(method = RequestMethod.POST, value = "nodes/{nodeName}/stopall")
    @MeasureRunTime
    NodeStatusRoot stopEverything(@PathVariable String nodeName);

}
