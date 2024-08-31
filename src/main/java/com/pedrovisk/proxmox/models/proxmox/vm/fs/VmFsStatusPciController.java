package com.pedrovisk.proxmox.models.proxmox.vm.fs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class VmFsStatusPciController {
    public int bus;
    public int slot;
    public int domain;
    public int function;
}
