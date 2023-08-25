package com.pedrovisk.proxmox.models.proxmox;

import lombok.Data;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Cpuinfo{
    public int cpus;
    public String model;
    public String flags;
    public String hvm;
    public int cores;
    public int user_hz;
    public String mhz;
    public int sockets;
}
