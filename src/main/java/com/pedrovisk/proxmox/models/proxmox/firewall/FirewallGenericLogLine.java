package com.pedrovisk.proxmox.models.proxmox.firewall;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.client.utils.DateUtils;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirewallGenericLogLine implements FirewallLogLine{
    private String vmId;
    private String logTypeId;
    private String chain;
    private String timestamp;
    private String policy;

    private String packetDetails;

    @Override
    public Date getDate() {
        return DateUtils.parseDate(this.getTimestamp(), new String[]{
                "dd/MMM/yyyy:HH:mm:ssZ"
        });
    }
}
