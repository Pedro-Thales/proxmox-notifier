package com.pedrovisk.proxmox.models.proxmox.vm;

import com.pedrovisk.proxmox.models.proxmox.Tap101i0;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VmNics {
    public Tap101i0 tap101i0;
}
