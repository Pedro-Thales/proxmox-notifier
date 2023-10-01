package com.pedrovisk.proxmox.models.proxmox.firewall;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FirewallLogData {

    @JsonProperty("n")
    private int lineNumber;

    @JsonProperty("t")
    private String lineText;

}
