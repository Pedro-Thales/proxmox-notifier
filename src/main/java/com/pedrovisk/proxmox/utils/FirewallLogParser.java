package com.pedrovisk.proxmox.utils;

import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogLine;

import java.text.ParseException;

public final class FirewallLogParser {
    public static FirewallLogLine parseFirewallLogLine(String[] lineSplitted) throws ParseException {

        if (lineSplitted.length != 20) {
            throw new ParseException("Invalid firewall log line separator size: " + lineSplitted.length, lineSplitted.length);
        }

        return FirewallLogLine.builder()
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
}
