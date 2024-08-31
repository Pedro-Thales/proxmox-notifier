package com.pedrovisk.proxmox.models.proxmox.vm.fs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VmFsStatusRoot {
    VmFsStatusData data;
}
