package com.pedrovisk.proxmox.models.notification;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@SuperBuilder
public class NotificationStoppedComponent extends NotificationBase {

    @Override
    public String getMessage() {

        return STR.
                """
                \{this.getComponentType()}: \{this.getComponentId()} is not running.
                """;
    }
}
