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
public class FirewallNodeLogLine implements FirewallLogLine{
    private String vmId;
    private String logTypeId;
    private String chain;
    private String timestamp;
    private String policy;

    private String packetIn;
    private String packetPhysin;
    private String packetMac;
    private String packetSource;
    private String packetDestination;
    private String packetLen;
    private String packetTos;
    private String packetPrec;
    private String packetTtl;
    private String packetId;
    private String packetProtocol;
    private String packetSourcePort;
    private String packetDestinationPort;
    private String packetLen2;


    public FirewallNodeLogLine clearPacketIndexes() {
        this.packetIn = this.packetIn.replace("IN=", "");
        this.packetPhysin = this.packetPhysin.replace("PHYSIN=", "");
        this.packetMac = this.packetMac.replace("MAC=", "");
        this.packetSource = this.packetSource.replace("SRC=", "");
        this.packetDestination = this.packetDestination.replace("DST=", "");
        this.packetLen = this.packetLen.replace("LEN=", "");
        this.packetTos = this.packetTos.replace("TOS=", "");
        this.packetPrec = this.packetPrec.replace("PREC=", "");
        this.packetTtl = this.packetTtl.replace("TTL=", "");
        this.packetId = this.packetId.replace("ID=", "");
        this.packetProtocol = this.packetProtocol.replace("PROTO=", "");
        this.packetSourcePort = this.packetSourcePort.replace("SPT=", "");
        this.packetDestinationPort = this.packetDestinationPort.replace("DPT=", "");
        this.packetLen2 = this.packetLen2.replace("LEN=", "");

        return this;
    }


    @Override
    public Date getDate() {
        return DateUtils.parseDate(this.getTimestamp(), new String[]{
                "dd/MMM/yyyy:HH:mm:ssZ"
        });
    }
}
