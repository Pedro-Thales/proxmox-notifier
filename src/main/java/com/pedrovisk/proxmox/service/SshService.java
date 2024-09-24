package com.pedrovisk.proxmox.service;


import com.google.common.base.CharMatcher;
import com.pedrovisk.proxmox.configuration.SshProperties;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.json.SshConfiguration;
import com.pedrovisk.proxmox.models.json.SshConfigurationType;
import com.pedrovisk.proxmox.models.notification.NotificationTemperature;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import com.pedrovisk.proxmox.telegram.TelegramApi;
import com.pedrovisk.proxmox.utils.SshUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class SshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshService.class);

    private final SshProperties sshProperties;
    private final TelegramApi telegramApi;
    private final RootConfiguration rootConfiguration;
    private final NotificationSenderService notificationSenderService;


    public void call() throws Exception {

        for (var nodes : rootConfiguration.nodes) {
            for (var sshConfiguration : nodes.getSshConfiguration()) {
                if (SshConfigurationType.CUSTOM.name().equals(sshConfiguration.getType())) {
                    var result = getFromCustomCommand(sshConfiguration.getCommand());
                    verifyThresholdAndSendMessage(sshConfiguration, result);
                }
                if (SshConfigurationType.SENSORS.name().equals(sshConfiguration.getType())) {
                    var result = getFromSensorsCommand(sshConfiguration.getGrep());
                    verifyThresholdAndSendMessage(sshConfiguration, result);
                }
                if (SshConfigurationType.SMARTCTL.name().equals(sshConfiguration.getType())) {
                    var result = getFromSmartctlCommand(sshConfiguration);
                    verifyThresholdAndSendMessage(sshConfiguration, result);
                }
            }
        }

    }

    private void verifyThresholdAndSendMessage(SshConfiguration sshConfiguration, Integer result) {
        if (result > sshConfiguration.getThreshold()) {
            sendNotification(sshConfiguration, result);
        }
    }

    public void sendNotification(SshConfiguration sshConfiguration, Integer actualValue) {
        var notification = NotificationTemperature.builder().actualValue(BigDecimal.valueOf(actualValue))
                .threshold(sshConfiguration.getThreshold()).valueType("temperature")
                .componentId(sshConfiguration.getName()).build();

        notificationSenderService.sendNotification(notification);
    }

    private Integer getFromSmartctlCommand(SshConfiguration sshConfiguration) throws Exception {

        var grepFilter = sshConfiguration.getGrep();
        if (grepFilter == null) {
            grepFilter = "Current Temperature";
        }

        var deviceId = sshConfiguration.getDevice();

        String command = STR."smartctl -l scttemp /dev/\{deviceId} | grep '\{grepFilter}'";

        if (deviceId.startsWith("nvme")) {
            grepFilter = "Temperature:";
            command = STR."smartctl -a /dev/\{deviceId} | grep '\{grepFilter}'";
        }

        var result = executeSshCommand(command);
        return extractDigits(result);
    }

    private Integer getFromCustomCommand(String command) throws Exception {
        var result = executeSshCommand(command);
        return extractDigits(result);
    }

    private Integer getFromSensorsCommand(String grepFilter) throws Exception {

        String command = STR."sensors | grep '\{grepFilter}'";
        var cmd6 = executeSshCommand(command);
        //TODO maybe use if (indexOf(".") = -1) then return
        var cmd6subs = cmd6.substring(cmd6.indexOf(":"), cmd6.indexOf("."));
        return extractDigits(cmd6subs);
    }

    private String executeSshCommand(String command) throws Exception {
        var username = sshProperties.username();
        var password = sshProperties.password();
        var host = sshProperties.host();
        var port = sshProperties.port();

        var commandResult = SshUtils.executeSshCommand(username, password, host, port, command);

        LOGGER.debug(STR."Command executed: [ \{command} ] ");
        LOGGER.debug(STR."Command result: [ \{commandResult} ] ");

        return commandResult;
    }

    private static Integer extractDigits(String text) {
        CharMatcher ASCII_DIGITS = CharMatcher.inRange('0', '9').precomputed();
        LOGGER.debug(STR."Extracting numbers from: \{text}");
        int resultInt;
        try {
            resultInt = Integer.parseInt(ASCII_DIGITS.retainFrom(text));
        } catch (Exception e) {
            LOGGER.error(STR."Error while trying to convert to integer from text: \{text}");
            return null;
        }

        return resultInt;
    }


}
