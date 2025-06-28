package com.pedrovisk.proxmox.repository;

import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogLine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@ConditionalOnProperty(name = "firewall-log.enabled", havingValue = "true")
public class FirewallLogInMemoryRepository {

    private final List<FirewallLogLine> firewallLogLines = new ArrayList<>();

    public List<FirewallLogLine> getFirewallLogLines() {
        return firewallLogLines;
    }

    public void addFirewallLogLines(FirewallLogLine logLine) {
        firewallLogLines.add(logLine);
    }
}
