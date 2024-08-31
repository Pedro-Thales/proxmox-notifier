package com.pedrovisk.proxmox.models.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationBase {

    private String componentId = "";
    private String componentType = "";
    private String valueType = "";
    private String message = "";


    public String getHtmlMessage() {
        return message;
    }

}
