package com.pedrovisk.proxmox.models.notification;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTemperature extends NotificationBase {
    private int threshold;
    private BigDecimal actualValue;

    @Override
    public String getHtmlMessage() {
        //TODO implement?
        return "";
    }

    @Override
    public String getMessage() {

        return STR.
                """
                Temperature Alert: Device \{this.getComponentId()}
                    Actual Temperature: \{String.valueOf(this.getActualValue())}
                    Threshold: \{this.getThreshold()}
                """;
    }
}
