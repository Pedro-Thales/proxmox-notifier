package com.pedrovisk.proxmox.models.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NodeConfiguration extends ConfigurationBase{

    @JsonProperty("containers")
    public List<ContainerConfiguration> containers;
    @JsonProperty("vms")
    public List<VmsConfiguration> vms;
    @JsonProperty("sshConfiguration")
    public List<SshConfiguration> sshConfiguration;
}
