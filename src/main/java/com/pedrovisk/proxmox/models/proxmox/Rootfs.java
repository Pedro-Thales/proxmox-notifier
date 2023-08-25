package com.pedrovisk.proxmox.models.proxmox;

import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Rootfs{
    public long free;
    public long used;
    public long total;
    public long avail;
}
