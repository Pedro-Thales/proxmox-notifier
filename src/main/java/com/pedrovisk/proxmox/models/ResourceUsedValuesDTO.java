package com.pedrovisk.proxmox.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceUsedValuesDTO {
    public BigDecimal usedCpuPercent;
    public BigDecimal usedMemoryPercent;
    public BigDecimal usedSwapPercent;
    public BigDecimal usedDiskPercent;
}
