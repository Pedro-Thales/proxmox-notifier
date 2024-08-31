package com.pedrovisk.proxmox.models.proxmox.vm.fs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class VmFsStatusDisk {
    @JsonProperty("pci-controller")
    public VmFsStatusPciController pciController;
    public int unit;
    public int target;
    public String serial;
    public int bus;
    @JsonProperty("bus-type")
    public String busType;
    public String dev;
}
