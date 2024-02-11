package com.pedrovisk.proxmox.models.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContainerConfiguration {

    @JsonProperty("id")
    public Integer id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("usedMemoryThreshold")
    public Integer usedMemoryThreshold;
    @JsonProperty("usedSwapThreshold")
    public Integer usedSwapThreshold;
    @JsonProperty("usedRootFSThreshold")
    public Integer usedRootFSThreshold;
}
