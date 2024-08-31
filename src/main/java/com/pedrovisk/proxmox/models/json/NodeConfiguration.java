package com.pedrovisk.proxmox.models.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeConfiguration {

    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("usedCpuThreshold")
    public Integer usedCpuThreshold;
    @JsonProperty("usedMemoryThreshold")
    public Integer usedMemoryThreshold;
    @JsonProperty("usedSwapThreshold")
    public Integer usedSwapThreshold;
    @JsonProperty("usedRootFSThreshold")
    public Integer usedRootFSThreshold;
    @JsonProperty("containers")
    public List<ContainerConfiguration> containers;
    @JsonProperty("vms")
    public List<VmsConfiguration> vms;
    @JsonProperty("sshConfiguration")
    public List<SshConfiguration> sshConfiguration;
}
