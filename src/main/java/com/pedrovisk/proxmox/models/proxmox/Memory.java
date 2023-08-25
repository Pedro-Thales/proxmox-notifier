package com.pedrovisk.proxmox.models.proxmox;

import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Memory{
    public double total;
    public double used;
    public double free;
}
