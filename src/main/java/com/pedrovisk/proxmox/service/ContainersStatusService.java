package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.TelegramProperties;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import com.pedrovisk.proxmox.models.proxmox.ContainerStatus;
import com.pedrovisk.proxmox.telegram.TelegramApi;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContainersStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainersStatusService.class);


    private final ProxmoxApi proxmoxApi;
    private final ThresholdProperties thresholdProperties;
    private final Environment environment;
    private final TelegramApi telegramApi;
    private final TelegramProperties telegramProperties;

    @MeasureRunTime
    public void getAllLxcStatus() {

        var status = proxmoxApi.getLxcContainersStatus();

        var lxcs = status.getData();
        for (ContainerStatus lxc : lxcs) {

            if ("stopped".equalsIgnoreCase(lxc.getStatus())) {
                continue;
            }

            var freeMemoryPercent = BigDecimal.valueOf(100 - (lxc.getMem() / lxc.getMaxmem()) * 100).setScale(2, RoundingMode.HALF_UP);
            var freeSwapPercent = BigDecimal.valueOf(100 - (lxc.getSwap() / lxc.getMaxswap()) * 100).setScale(2, RoundingMode.HALF_UP);
            var freeRootFsPercent = BigDecimal.valueOf(100 - (lxc.getDisk() / lxc.getMaxdisk()) * 100).setScale(2, RoundingMode.HALF_UP);


            //TODO maybe use another threshold that points to this percentage to get warnings if is almost reaching that percentage
            // maybe to get levels of notifications (Warning, Dangerous)

            verifyThreshold(lxc, freeMemoryPercent, "memory");
            verifyThreshold(lxc, freeSwapPercent, "swap");
            verifyThreshold(lxc, freeRootFsPercent, "root");

        }

    }

    public void verifyThreshold(ContainerStatus lxc, BigDecimal actualValue, String suffix) {
        var memoryThreshold = Optional.ofNullable(getThreshold(lxc.getVmid(), ".free-" + suffix));
        memoryThreshold.ifPresent(threshold -> {
            if (actualValue.compareTo(threshold) < 1) {

                //TODO Create a webhook(whatsapp, telegram bots?) to inform when temperature is high,
                //      and maybe we can ask about the current temperature

                //TODO Notify using the webhook, email and ntfy?
                //TODO use string template from java 21
                var message = STR.
                        """
                        Container \{lxc.getName()} with free \{suffix} getting dangerous

                            Actual free: \{String.valueOf(actualValue)}
                            Threshold: \{threshold}
                        """;

                LOGGER.info(message);
                //sendMessageToTelegram(message);
            }
        });
    }

    public void sendMessageToTelegram(String message) {
        try {
            //TODO send maybe in html to be more easy to convert to email too
            // https://stackoverflow.com/questions/38119481/send-bold-italic-text-on-telegram-bot-with-html

            String escapedMessage = message
                    .replace(".", "\\.")
                    .replace("Actual free:", "*Actual free:*")
                    .replace("Threshold:", "*Threshold:*");

            var response = telegramApi.sendMessageToBotChatDefault(escapedMessage);
            if (response.getStatusCode() != HttpStatus.OK) {
                LOGGER.error("Error while sending message to telegram! Response: {} ", response);
                throw new TelegramApiException("Status was not OK");
            }
        } catch (Exception e) {
            LOGGER.error("Not able to sent message to telegram, check logs to see more information! ", e);
        }

    }

    public BigDecimal getThreshold(String lxcId, String suffix) {
        var envName = STR."threshold.configuration.lxc.\{lxcId}\{suffix}";
        try {
            var env = environment.getProperty(envName, BigDecimal.class);

            if (env == null) {
                LOGGER.error("Environment Variable {} not defined. Ignoring it! ", envName);
            }

            return env;

        } catch (Exception e) {
            LOGGER.error("Environment Variable {} with error, must be a numeric value! ", envName);
            return null;
        }

    }

}
