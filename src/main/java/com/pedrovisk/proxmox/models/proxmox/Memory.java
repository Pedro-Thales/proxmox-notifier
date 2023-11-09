package com.pedrovisk.proxmox.models.proxmox;

import lombok.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Memory{
    public BigDecimal total;
    public BigDecimal used;
    public BigDecimal free;
}
