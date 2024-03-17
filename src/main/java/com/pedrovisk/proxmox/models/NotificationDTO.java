package com.pedrovisk.proxmox.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class NotificationDTO {
    private String componentId;
    private String componentType;
    private String valueType;
    private BigDecimal threshold;
    private BigDecimal actualValue;
    private String message;
}
