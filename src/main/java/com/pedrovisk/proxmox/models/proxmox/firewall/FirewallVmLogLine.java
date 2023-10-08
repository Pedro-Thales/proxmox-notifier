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
public class FirewallVmLogLine implements FirewallLogLine{
    private String vmId;
    private String logTypeId;
    private String chain;
    private String timestamp;
    private String policy;

    private String packetIn;
    private String packetOut; //
    private String packetPhysin;
    private String packetPhysOut;//
    private String packetMac;
    private String packetSource;
    private String packetDestination;
    private String packetLen;
    private String packetTos;
    private String packetPrec;
    private String packetTtl;
    private String packetId;
    private String packetDf;//
    private String packetProtocol;
    private String packetSourcePort;
    private String packetDestinationPort;
    private String packetSequence;//
    private String packetAck;//
    private String packetWindow;//
    private String packetSyn;//


    public FirewallVmLogLine clearPacketIndexes() {
        this.packetIn = this.packetIn.replace("IN=", "");
        this.packetIn = this.packetIn.replace("OUT=", "");
        this.packetPhysin = this.packetPhysin.replace("PHYSIN=", "");
        this.packetPhysin = this.packetPhysin.replace("PHYSOUT=", "");
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
        this.packetSequence = this.packetSequence.replace("SEQ=", "");
        this.packetAck = this.packetAck.replace("ACK=", "");
        this.packetWindow = this.packetWindow.replace("WINDOW=", "");

        return this;
    }

    public Date getDate() {
        return DateUtils.parseDate(this.getTimestamp(), new String[]{
                "dd/MMM/yyyy:HH:mm:ssZ"
        });
    }





}
