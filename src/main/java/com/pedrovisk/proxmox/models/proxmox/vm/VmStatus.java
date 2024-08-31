package com.pedrovisk.proxmox.models.proxmox.vm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pedrovisk.proxmox.models.proxmox.Ballooninfo;
import com.pedrovisk.proxmox.models.proxmox.BlockStat;
import com.pedrovisk.proxmox.models.proxmox.Ha;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class VmStatus {
    public int vmid;
    public double cpu;
    public int disk;
    public long mem;
    @JsonProperty("running-qemu")
    public String runningQemu;
    public long freemem;
    public String status;
    public long maxmem;
    public Ballooninfo ballooninfo;
    public VmNics nics;
    public int cpus;
    @JsonProperty("blockstat")
    Map<String, BlockStat> blockstat = new LinkedHashMap<>();
    public String qmpstatus;
    public long netin;
    public long balloon;
    public long maxdisk;
    public String name;
    @JsonProperty("running-machine")
    public String runningMachine;
    public int uptime;
    @JsonProperty("proxmox-support")
    public VmProxmoxSupport proxmoxSupport;
    public int diskread;
    public long netout;
    public int pid;
    public int diskwrite;
    public Ha ha;
}
