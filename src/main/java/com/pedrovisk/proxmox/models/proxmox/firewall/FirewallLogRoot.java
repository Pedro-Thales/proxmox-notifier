package com.pedrovisk.proxmox.models.proxmox.firewall;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FirewallLogRoot {

    private List<FirewallLogData> data;
    private int total;
}
