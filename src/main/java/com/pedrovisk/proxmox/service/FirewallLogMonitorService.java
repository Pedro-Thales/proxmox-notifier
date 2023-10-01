package com.pedrovisk.proxmox.service;

import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogData;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogLine;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public class FirewallLogMonitorService {
    private final ProxmoxApi proxmoxApi;

    private final ThresholdProperties thresholdProperties;

    private static int LOG_COUNTER = 0;
    private final static String POLICY_DROP = "DROP:";
    private final static String POLICY_ACCEPT = "DROP:";

    public FirewallLogMonitorService(ProxmoxApi proxmoxApi, ThresholdProperties thresholdProperties) {
        this.proxmoxApi = proxmoxApi;
        this.thresholdProperties = thresholdProperties;
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-firewall-logs", name = "proxmox.get-firewall-logs-total")
    public void getFirewallLogs() {

        var firewallLogRoot = proxmoxApi.getFirewallLog(LOG_COUNTER);
        LOG_COUNTER = firewallLogRoot.getTotal();

        // 0 6 PVEFW-HOST-IN 29/Sep/2023:21:37:18 -0300 DROP: IN=vmbr0 PHYSIN=enp6s0 MAC=ff:ff:ff:ff:ff:ff:a8:80:55:69:9d:0d:08:00 SRC=192.168.0.52 DST=255.255.255.255 LEN=200 TOS=0x00 PREC=0x00 TTL=255 ID=2606 PROTO=UDP SPT=59729 DPT=6667 LEN=180

        var data = firewallLogRoot.getData();
        for (FirewallLogData line : data) {

            System.out.println("line.getLineNumber() = " + line.getLineNumber());
            System.out.println("line.getLineText() = " + line.getLineText());

            var lineSplitted = line.getLineText().split(" ");


            try {
                var lineParsed = parseFirewallLogLine(lineSplitted);

                //TODO error handling?
                if (Integer.parseInt(lineParsed.getLogTypeId()) < 7 && POLICY_DROP.equals(lineParsed.getPolicy())) {
                    System.out.println("Line Dropped: " + lineParsed);

                    //Line Dropped: FirewallLogLine(
                    //      vmId=0,
                    //      logTypeId=6,
                    //      chain=PVEFW-HOST-IN,
                    //      timestamp=01/Oct/2023:12:52:09-0300,
                    //      policy=DROP:,
                    //      packetIn=vmbr0,
                    //      packetPhysin=enp6s0,
                    //      packetMac=ff:ff:ff:ff:ff:ff:a8:80:55:6a:12:d5:08:00,
                    //      packetSource=192.168.0.51,
                    //      packetDestination=255.255.255.255,
                    //      packetLen=200,
                    //      packetTos=0x00,
                    //      packetPrec=0x00,
                    //      packetTtl=255,
                    //      packetId=35814,
                    //      packetProtocol=UDP,
                    //      packetSourcePort=59729,
                    //      packetDestinationPort=6667,
                    //      packetLen2=180
                    //   )
                }


            } catch (Exception e) {
                System.out.println("Exception while parsing line. ");
            }


        }

    }

    private static FirewallLogLine parseFirewallLogLine(String[] lineSplitted) throws ParseException {
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
