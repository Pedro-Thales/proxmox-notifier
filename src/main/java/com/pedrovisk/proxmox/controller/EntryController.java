package com.pedrovisk.proxmox.controller;

import com.pedrovisk.proxmox.service.ProxmoxStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("monitor")
public class EntryController {

    ProxmoxStatusService statusService;

    public EntryController(ProxmoxStatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/memory")
    String memoryMonitor() {

        var memoryFree = statusService.getMemory();

        return memoryFree;
    }

    @GetMapping("/swap")
    String swapMonitor() {

        var swapFree = statusService.getSwap();

        return swapFree;
    }


}
