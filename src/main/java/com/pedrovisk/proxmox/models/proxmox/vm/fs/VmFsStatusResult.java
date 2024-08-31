package com.pedrovisk.proxmox.models.proxmox.vm.fs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class VmFsStatusResult {
    ArrayList<VmFsStatusDisk> disk;
    String type;
    String mountpoint;
    @JsonProperty("total-bytes")
    long totalBytes;
    @JsonProperty("used-bytes")
    long usedBytes;
    String name;
}
