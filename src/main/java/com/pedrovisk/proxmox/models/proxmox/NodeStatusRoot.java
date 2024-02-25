package com.pedrovisk.proxmox.models.proxmox;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NodeStatusRoot {
    public NodeStatus data;
}
