package com.pedrovisk.proxmox.service;

import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.FirewallLogsProperties;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogData;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallNodeLogLine;
import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallVmLogLine;
import com.pedrovisk.proxmox.repository.FirewallLogInMemoryRepository;
import com.pedrovisk.proxmox.utils.FirewallLogParser;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirewallLogMonitorService {

    private final ProxmoxApi proxmoxApi;
    private final FirewallLogInMemoryRepository firewallLogRepository;
    private final FirewallLogsProperties firewallLogsProperties;
    private final RootConfiguration rootConfiguration;

    public static int LOG_COUNTER = 0;
    private final static String POLICY_DROP = "DROP:";
    private final static String POLICY_ACCEPT = "ACCEPT:";

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-firewall-logs", name = "proxmox.get-firewall-logs-usage")
    @Timed(value = "timed-firewall-log")
    public void getFirewallLogs() {

        var firewallLogRoot = proxmoxApi.getFirewallLog(rootConfiguration.getNodes().getFirst().getId(), LOG_COUNTER);

        var data = firewallLogRoot.getData();

        if(!firewallLogsProperties.enableLoadEverything()){
            LOG_COUNTER = firewallLogRoot.getTotal();
        }

        for (FirewallLogData line : data) {

            if (LOG_COUNTER > line.getLineNumber()) {
                continue;
            }

            var lineSplitted = line.getLineText().split(" ");

            try {
                var lineParsed = FirewallLogParser.parseFirewallLogLine(lineSplitted);
                firewallLogRepository.addFirewallLogLines(lineParsed);

                //TODO error handling
                // and create a property to configure what type of logs will be stored

                //TODO save in a database with fixed "VMID LOGID CHAIN TIMESTAMP POLICY:" and generic "packet details" using a Json column that can be checked

                if (lineParsed instanceof FirewallNodeLogLine nodeLogLine) {
                    if (Integer.parseInt(nodeLogLine.getLogTypeId()) < 7 && POLICY_DROP.equals(nodeLogLine.getPolicy())) {
                        log.info("NODE LINE: " + lineParsed);
                    }
                }

                if (lineParsed instanceof FirewallVmLogLine vmLogLine) {
                    if (Integer.parseInt(vmLogLine.getLogTypeId()) < 7 && POLICY_DROP.equals(vmLogLine.getPolicy())) {
                        log.info("VM LINE: " + lineParsed);
                    }
                }


                log.info("REPO SIZE: " + firewallLogRepository.getFirewallLogLines().size());

                //TODO evaluate to save data to a database to maybe notify by patterns:
                // Some Ip is being dropped multiple times
                // New IP Dropped or accepted


            } catch (Exception e) {
                log.error("Exception while parsing line. -> " + line.getLineText());
                log.error("Size. -> " + line.getLineText().split(" ").length);
                log.error("Exception while parsing line: ",e);
            }

            LOG_COUNTER = line.getLineNumber();

        }
    }

    public static int getLogCounter() {
        return LOG_COUNTER;
    }
}
