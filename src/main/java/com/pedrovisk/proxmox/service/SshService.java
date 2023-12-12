package com.pedrovisk.proxmox.service;


import com.google.common.base.CharMatcher;
import com.pedrovisk.proxmox.configuration.SshProperties;
import com.pedrovisk.proxmox.utils.SshUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshService.class);

    private final SshProperties sshProperties;

    public SshService(SshProperties sshProperties) {
        this.sshProperties = sshProperties;
    }

    public void call() throws Exception {

        //TODO print it nicely, verify threshold, and send it to telegram, email...

        System.out.println("========================== COMMANDS ==========================");
        //Get Disks readings
        getFromSmartctlCommand("sda", "Current Temperature");
        getFromSmartctlCommand("sdb", "Current Temperature");
        getFromSmartctlCommand("sdc", "Current Temperature");
        getFromSmartctlCommand("sdd", "Current Temperature");
        getFromSmartctlCommand("nvme0n1", "Temperature");


        //Get from sensors(cpu...)
        //TODO make it support multiple filters
        getFromSensorsCommand("Package id 0");

        System.out.println("========================== END ==========================");

    }

    private void getFromSmartctlCommand(String deviceId, String grepFilter) throws Exception {
        String command = STR."smartctl -l scttemp /dev/\{deviceId} | grep '\{grepFilter}'";
        if (deviceId.startsWith("nvme")) {
            command = STR."smartctl -a /dev/\{deviceId} | grep '\{grepFilter}'";
        }

        var result = executeSshCommand(command);
        var check = extractDigits(result);
        System.out.println(check);
    }

    private void getFromSensorsCommand(String grepFilter) throws Exception {

        String command = STR."sensors | grep '\{grepFilter}'";
        var cmd6 = executeSshCommand(command);
        System.out.println(cmd6);
        var cmd6subs = cmd6.substring(cmd6.indexOf(":"), cmd6.indexOf("."));
        System.out.println(cmd6subs);
        String check = extractDigits(cmd6subs);
        System.out.println(check);
    }

    private String executeSshCommand(String command) throws Exception {
        var username = sshProperties.username();
        var password = sshProperties.password();
        var host = sshProperties.host();
        var port = sshProperties.port();

        return SshUtils.executeSshCommand(username, password, host, port, command);
    }

    private static String extractDigits(String text) {
        CharMatcher ASCII_DIGITS = CharMatcher.inRange('0', '9').precomputed();
        return ASCII_DIGITS.retainFrom(text);
    }


}
