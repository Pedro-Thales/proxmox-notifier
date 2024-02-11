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
public class SshConfiguration {
    @JsonProperty("name")
    public String name;
    @JsonProperty("type")
    public String type;
    @JsonProperty("command")
    public String command;
    @JsonProperty("threshold")
    public Integer threshold;
    @JsonProperty("grep")
    public String grep;
    @JsonProperty("device")
    public String device;
}
