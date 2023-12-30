package com.pedrovisk.proxmox.service;


import com.google.common.base.CharMatcher;
import com.pedrovisk.proxmox.configuration.SshProperties;
import com.pedrovisk.proxmox.telegram.TelegramApi;
import com.pedrovisk.proxmox.utils.SshUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SshService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshService.class);

    private final SshProperties sshProperties;
    private final TelegramApi telegramApi;


    public void call() throws Exception {

        int temperatureThreshold = 50;
        int cpuTemperatureThreshold = 50;

        var hddMap = Map.of("sda", "Current Temperature",
                        "sdb", "Current Temperature",
                        "sdc", "Current Temperature",
                        "sdd", "Current Temperature",
                        "nvme0n1", "Temperature:");

        for (var entries : hddMap.entrySet()) {
            var result = getFromSmartctlCommand(entries.getKey(), entries.getValue());

            if (result > temperatureThreshold ) {
                sendMessageToTelegram(entries.getKey(), result);
            }
        }

        var result = getFromSensorsCommand("Package id 0");
        if (result > cpuTemperatureThreshold ) {
            sendMessageToTelegram("CPU", result);
        }


    }

    public void sendMessageToTelegram(String device, Integer temperature) {
        try {

            var message = STR.
                    """
                        *Temperature Alert:*
                        Device \{device}: *\{temperature}*
                    """;

            LOGGER.info(message);

            String escapedMessage = message.replace(".", "\\.");

            var response = telegramApi.sendMessageToBotChatDefault(escapedMessage);
            if (response.getStatusCode() != HttpStatus.OK) {
                LOGGER.error("Error while sending message to telegram! Response: {} ", response);
                throw new TelegramApiException("Status was not OK");
            }
        } catch (Exception e) {
            LOGGER.error("Not able to sent message to telegram, check logs to see more information! ", e);
        }

    }

    private Integer getFromSmartctlCommand(String deviceId, String grepFilter) throws Exception {
        String command = STR."smartctl -l scttemp /dev/\{deviceId} | grep '\{grepFilter}'";

        if (deviceId.startsWith("nvme")) {
            command = STR."smartctl -a /dev/\{deviceId} | grep '\{grepFilter}'";
        }

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
        Integer resultInt = null;
        try {
            resultInt = Integer.parseInt(ASCII_DIGITS.retainFrom(text));
        } catch (Exception e) {
            LOGGER.error(STR."Error while trying to convert to integer from text: \{text}");
            return null;
        }

        return resultInt;
    }


}
