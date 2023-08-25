package com.pedrovisk.proxmox.models.proxmox; 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Data{
    public Ksm ksm;
    public Swap swap;
    public int idle;
    public String thermalstate;
    public String kversion;
    public Memory memory;
    public List<String> loadavg;
    public int wait;
    public int cpu;
    public Cpuinfo cpuinfo;
    public Rootfs rootfs;
    public String pveversion;
    public int uptime;
}
