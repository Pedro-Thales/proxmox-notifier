package com.pedrovisk.proxmox.models.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ConfigurationBase {

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
    @JsonProperty("usedDiskThreshold")
    public Integer usedDiskThreshold;
}
