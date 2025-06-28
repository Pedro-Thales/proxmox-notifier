package com.pedrovisk.proxmox.service;


import com.google.common.base.CharMatcher;
import com.pedrovisk.proxmox.configuration.SshProperties;
import com.pedrovisk.proxmox.models.json.RootConfiguration;
import com.pedrovisk.proxmox.models.json.SshConfiguration;
import com.pedrovisk.proxmox.models.notification.NotificationTemperature;
import com.pedrovisk.proxmox.service.notifications.NotificationSenderService;
import com.pedrovisk.proxmox.telegram.TelegramApi;
import com.pedrovisk.proxmox.utils.SshUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class SshService {

    private final SshProperties sshProperties;
    private final TelegramApi telegramApi;
    private final RootConfiguration rootConfiguration;
    private final NotificationSenderService notificationSenderService;

    @Scheduled(initialDelay = 5000, fixedDelayString = "${update.frequency.temperature}")
    public void call() throws Exception {
        call(false);
    }

    public void call(boolean ignoreThreshold) throws Exception {

        for (var nodes : rootConfiguration.nodes) {
            for (var sshConfiguration : nodes.getSshConfiguration()) {

                Integer result = switch (sshConfiguration.getType()) {
                    case "CUSTOM" -> getFromCustomCommand(sshConfiguration.getCommand());
                    case "SENSORS" -> getFromSensorsCommand(sshConfiguration.getGrep());
                    case "SMARTCTL" -> getFromSmartctlCommand(sshConfiguration);
                    default -> null;
                };

                if (result != null) {
                    if (ignoreThreshold || result > sshConfiguration.getThreshold()) {
                        sendNotification(sshConfiguration, result);
                    }
                } else {
                    log.warn("Result was null for configuration: {}", sshConfiguration.getName());
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

        var grepFilter = (sshConfiguration.getGrep() != null) ? sshConfiguration.getGrep() : "Current Temperature";
        var deviceId = sshConfiguration.getDevice();

        String command = deviceId.startsWith("nvme") ?
                "smartctl -a /dev/" + deviceId + " | grep 'Temperature:'" :
                "smartctl -l scttemp /dev/" + deviceId + " | grep '" + grepFilter + "'";

        var result = executeSshCommand(command);
        return extractDigits(result);
    }

    private Integer getFromCustomCommand(String command) throws Exception {
        var result = executeSshCommand(command);
        return extractDigits(result);
    }

    private Integer getFromSensorsCommand(String grepFilter) throws Exception {

        String command = "sensors | grep '" + grepFilter + "'";
        var cmdResult = executeSshCommand(command);
        if (cmdResult.contains(":")) {
            var cmdSubs = cmdResult.substring(cmdResult.indexOf(":") + 1, cmdResult.indexOf("."));
            return extractDigits(cmdSubs);
        }
        return null;
    }

    private String executeSshCommand(String command) throws Exception {
        var username = sshProperties.username();
        var password = sshProperties.password();
        var host = sshProperties.host();
        var port = sshProperties.port();

        var commandResult = SshUtils.executeSshCommand(username, password, host, port, command);

        log.debug("Command executed: [ {} ]", command);
        log.debug("Command result: [ {} ]", commandResult);

        return commandResult;
    }

    private static Integer extractDigits(String text) {
        CharMatcher ASCII_DIGITS = CharMatcher.inRange('0', '9').precomputed();
        //TODO evaluate if we can extract double digits when/if they appear
        log.debug("Extracting numbers from: {}", text);
        int resultInt;
        try {
            resultInt = Integer.parseInt(ASCII_DIGITS.retainFrom(text));
        } catch (Exception e) {
            log.error("Error while trying to convert to integer from text: {}", text, e);
            return null;
        }

        return resultInt;
    }


}
