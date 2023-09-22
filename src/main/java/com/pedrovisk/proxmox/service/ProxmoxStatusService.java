package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.repository.UsersInMemoryRepository;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ProxmoxStatusService {
    ProxmoxApi proxmoxApi;
    UsersInMemoryRepository repository;

    public ProxmoxStatusService(ProxmoxApi proxmoxApi, UsersInMemoryRepository repository) {
        this.proxmoxApi = proxmoxApi;
        this.repository = repository;
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-memory", name = "proxmox.get-memory-usage")
    public String getMemory() {

        var status = proxmoxApi.getNodeStatus();

        var memory = status.getData().getMemory();
        System.out.println("memory = " + memory);
        var percentUsed = BigDecimal.valueOf((memory.getUsed() / memory.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Used memory: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((memory.getFree() / memory.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Free memory: " + percentFree + "%");

        //validate if the available memory is less than the threshold defined, if it is so notify via the webhook or something else

        return "Free memory: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-swap", name = "proxmox.get-swap-usage")
    public String getSwap() {

        var status = proxmoxApi.getNodeStatus();

        var swap = status.getData().getSwap();
        System.out.println("memory = " + swap);
        var percentUsed = BigDecimal.valueOf((swap.getUsed() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Used swap: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((swap.getFree() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        System.out.println("Free swap: " + percentFree + "%");

        return "Free swap: " + percentFree + "%";
    }


}
