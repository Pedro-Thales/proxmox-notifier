package com.pedrovisk.proxmox.models.proxmox;

import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Swap{
    public double free;
    public double total;
    public double used;
}
