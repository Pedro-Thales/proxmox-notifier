package com.pedrovisk.proxmox.service;

import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogData;
import com.pedrovisk.proxmox.utils.FirewallLogParser;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

@Service
public class FirewallLogMonitorService {
    private final ProxmoxApi proxmoxApi;
    private static int LOG_COUNTER = 0;
    private final static String POLICY_DROP = "DROP:";
    private final static String POLICY_ACCEPT = "ACCEPT:";

    public FirewallLogMonitorService(ProxmoxApi proxmoxApi) {
        this.proxmoxApi = proxmoxApi;
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-firewall-logs", name = "proxmox.get-firewall-logs-total")
    public void getFirewallLogs() {

        var firewallLogRoot = proxmoxApi.getFirewallLog(LOG_COUNTER);
        LOG_COUNTER = firewallLogRoot.getTotal();

        var data = firewallLogRoot.getData();
        for (FirewallLogData line : data) {

            System.out.println("line.getLineNumber() = " + line.getLineNumber());
            System.out.println("line.getLineText() = " + line.getLineText());

            var lineSplitted = line.getLineText().split(" ");

            try {
                var lineParsed = FirewallLogParser.parseFirewallLogLine(lineSplitted);

                //TODO error handling
                // and create a property to configure what type of logs will be stored
                if (Integer.parseInt(lineParsed.getLogTypeId()) < 7 && POLICY_DROP.equals(lineParsed.getPolicy())) {
                    System.out.println("Line Dropped: " + lineParsed);
                }


            } catch (Exception e) {
                System.out.println("Exception while parsing line. ");
            }
        }
    }

    public static int getLogCounter() {
        return LOG_COUNTER;
    }
}
