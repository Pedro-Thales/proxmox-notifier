package com.pedrovisk.proxmox.models.proxmox;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ContainerStatus {
    String name;
    int cpus;
    String type;
    String status;
    long maxswap;
    double maxmem;
    String vmid;
    long disk;
    double netin;
    double cpu;
    double maxdisk;
    double mem;
    double diskread;
    double netout;
    double diskwrite;
    double uptime;
    double swap;
    int pid;
}
