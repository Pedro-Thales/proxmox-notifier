package com.pedrovisk.proxmox.repository;

import com.pedrovisk.proxmox.models.proxmox.firewall.FirewallLogLine;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FirewallLogInMemoryRepository {

    private final List<FirewallLogLine> firewallLogLines = new ArrayList<>();

    public List<FirewallLogLine> getFirewallLogLines() {
        return firewallLogLines;
    }

    public void addFirewallLogLines(FirewallLogLine logLine) {
        firewallLogLines.add(logLine);
    }
}
