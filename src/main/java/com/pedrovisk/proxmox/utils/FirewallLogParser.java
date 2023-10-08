package com.pedrovisk.proxmox.utils;

import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallGenericLogLine;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogLine;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallNodeLogLine;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallVmLogLine;

import java.text.ParseException;

public final class FirewallLogParser {
    public static FirewallLogLine parseFirewallLogLine(String[] lineSplitted) {

        if (lineSplitted.length == 20) {
            return FirewallNodeLogLine.builder()
                    .vmId(lineSplitted[0])
                    .logTypeId(lineSplitted[1])
                    .chain(lineSplitted[2])
                    .timestamp(lineSplitted[3] + lineSplitted[4])
                    .policy(lineSplitted[5])
                    .packetIn(lineSplitted[6])
                    .packetPhysin(lineSplitted[7])
                    .packetMac(lineSplitted[8])
                    .packetSource(lineSplitted[9])
                    .packetDestination(lineSplitted[10])
                    .packetLen(lineSplitted[11])
                    .packetTos(lineSplitted[12])
                    .packetPrec(lineSplitted[13])
                    .packetTtl(lineSplitted[14])
                    .packetId(lineSplitted[15])
                    .packetProtocol(lineSplitted[16])
                    .packetSourcePort(lineSplitted[17])
                    .packetDestinationPort(lineSplitted[18])
                    .packetLen2(lineSplitted[19])
                    .build().clearPacketIndexes();
        }

        if (lineSplitted.length == 26) {
            return FirewallVmLogLine.builder()
                    .vmId(lineSplitted[0])
                    .logTypeId(lineSplitted[1])
                    .chain(lineSplitted[2])
                    .timestamp(lineSplitted[3] + lineSplitted[4])
                    .policy(lineSplitted[5])
                    .packetIn(lineSplitted[6])
                    .packetOut(lineSplitted[7])
                    .packetPhysin(lineSplitted[8])
                    .packetPhysOut(lineSplitted[9])
                    .packetMac(lineSplitted[10])
                    .packetSource(lineSplitted[11])
                    .packetDestination(lineSplitted[12])
                    .packetLen(lineSplitted[13])
                    .packetTos(lineSplitted[14])
                    .packetPrec(lineSplitted[15])
                    .packetTtl(lineSplitted[16])
                    .packetId(lineSplitted[17])
                    .packetDf(lineSplitted[18])
                    .packetProtocol(lineSplitted[19])
                    .packetSourcePort(lineSplitted[20])
                    .packetDestinationPort(lineSplitted[21])
                    .packetSequence(lineSplitted[22])
                    .packetAck(lineSplitted[23])
                    .packetWindow(lineSplitted[24])
                    .packetSyn(lineSplitted[25])
                    .build().clearPacketIndexes();
        }

        var genericLine = FirewallGenericLogLine.builder()
                .vmId(lineSplitted[0])
                .logTypeId(lineSplitted[1])
                .chain(lineSplitted[2])
                .timestamp(lineSplitted[3] + lineSplitted[4])
                .policy(lineSplitted[5]).build();

        StringBuilder sb = new StringBuilder();
        for (int i = 6; i < lineSplitted.length; i++) {
            sb.append(" ");
            sb.append(lineSplitted[i]);
        }

        genericLine.setPacketDetails(sb.toString());
        return genericLine;

    }
}
