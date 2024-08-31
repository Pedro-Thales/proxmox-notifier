package com.pedrovisk.proxmox.api;


import com.pedrovisk.proxmox.CustomFeignConfiguration;
import com.pedrovisk.proxmox.models.proxmox.CommandRequest;
import com.pedrovisk.proxmox.models.proxmox.LxcStatusRoot;
import com.pedrovisk.proxmox.models.proxmox.NodeStatusRoot;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogRoot;
import com.pedrovisk.proxmox.models.proxmox.vm.VmStatusRoot;
import com.pedrovisk.proxmox.models.proxmox.vm.fs.VmFsStatusRoot;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "proxmox", url = "${proxmox.api-url}", configuration = {ProxmoxRequestInterceptor.class, CustomFeignConfiguration.class})
public interface ProxmoxApi {


    @RequestMapping(method = RequestMethod.GET, value = "nodes/{nodeName}/status")
    @MeasureRunTime
    NodeStatusRoot getNodeStatus(@PathVariable String nodeName);

    @RequestMapping(method = RequestMethod.GET, value = "nodes/{nodeName}/lxc/{lxcId}/status/current")
    @MeasureRunTime
    LxcStatusRoot getLxcContainersStatus(@PathVariable String nodeName, @PathVariable String lxcId);

    @GetMapping(value = "nodes/{nodeName}/qemu/{vmId}/status/current")
    @MeasureRunTime
    VmStatusRoot getVmStatus(@PathVariable String nodeName, @PathVariable String vmId);

    @GetMapping(value = "nodes/{nodeName}/qemu/{vmId}/agent/get-fsinfo")
    @MeasureRunTime
    VmFsStatusRoot getVmFsStatus(@PathVariable String nodeName, @PathVariable String vmId);

    @GetMapping(value = "nodes/{nodeName}/firewall/log")
    @MeasureRunTime
    FirewallLogRoot getFirewallLog(@PathVariable String nodeName, @RequestParam int start);

    @PostMapping(value = "nodes/{nodeName}/status", consumes = "application/json")
    @MeasureRunTime
    NodeStatusRoot shutdownNode(@PathVariable String nodeName, CommandRequest commandRequest);

    @PostMapping(value = "nodes/{nodeName}/stopall")
    @MeasureRunTime
    NodeStatusRoot stopEverything(@PathVariable String nodeName);

}
