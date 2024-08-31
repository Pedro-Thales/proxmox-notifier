package com.pedrovisk.proxmox.models.proxmox.vm;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VmStatusRoot {
    VmStatus data;
}
