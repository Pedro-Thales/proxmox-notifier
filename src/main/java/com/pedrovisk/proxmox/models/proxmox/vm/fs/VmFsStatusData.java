package com.pedrovisk.proxmox.models.proxmox.vm.fs;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VmFsStatusData {
    public List<VmFsStatusResult> result;
}
