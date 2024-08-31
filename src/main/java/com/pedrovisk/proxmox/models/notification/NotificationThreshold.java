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
public class NotificationThreshold extends NotificationBase {
    private int threshold;
    private BigDecimal actualValue;

    @Override
    public String getHtmlMessage() {
        //TODO implement?
        return "";
    }

    //TODO Aggregate all resource notifications in only one message? How to do it?

    @Override
    public String getMessage() {

        return STR.
                """
                \{this.getComponentType()}: \{this.getComponentId()} with used \{this.getValueType()} getting dangerous
                    Actual used: \{String.valueOf(this.getActualValue())}
                    Threshold: \{this.getThreshold()}
                """;
    }
}
