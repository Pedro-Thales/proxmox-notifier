package com.pedrovisk.proxmox.models.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
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
    @JsonProperty("usedMemoryThreshold")
    public Integer usedMemoryThreshold;
    @JsonProperty("usedSwapThreshold")
    public Integer usedSwapThreshold;
    @JsonProperty("usedRootFSThreshold")
    public Integer usedRootFSThreshold;
    @JsonProperty("containers")
    @Valid
    public List<ContainerConfiguration> containers;
    @JsonProperty("sshConfiguration")
    @Valid
    public List<SshConfiguration> sshConfiguration;
}
