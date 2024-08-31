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
public class RootConfiguration {
    @JsonProperty("nodes")
    public List<NodeConfiguration> nodes;
}
