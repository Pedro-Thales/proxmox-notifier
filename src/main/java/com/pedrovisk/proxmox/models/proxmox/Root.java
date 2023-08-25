package com.pedrovisk.proxmox.models.proxmox;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Root{
    public Data data;
}
