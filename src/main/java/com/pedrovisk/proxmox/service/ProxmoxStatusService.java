package com.pedrovisk.proxmox.service;


import com.pedrovisk.proxmox.api.ProxmoxApi;
import com.pedrovisk.proxmox.configuration.ThresholdProperties;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.lang.StringTemplate.STR;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProxmoxStatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxmoxStatusService.class);

    private final JavaMailSender emailSender;
    private final ProxmoxApi proxmoxApi;
    private final ThresholdProperties thresholdProperties;

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-memory", name = "proxmox.get-memory-usage")
    public String getMemory() {

        var status = proxmoxApi.getNodeStatus();

        var memory = status.getData().getMemory();
        LOGGER.info("memory = " + memory);
        var percentUsed = memory.getUsed().divide(memory.getTotal(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        LOGGER.info("Used memory: " + percentUsed + "%");
        var percentFree = memory.getFree().divide(memory.getTotal(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        LOGGER.info("Free memory: " + percentFree + "%");

        //validate if the available memory is less than the threshold defined, if it is so notify via the webhook or something else
        sendEmailAsync(percentUsed);

        return "Free memory: " + percentFree + "%";
    }

    private void sendEmailAsync(BigDecimal percentUsed) {
        Runnable runnable = () -> sendEmail(percentUsed);

        Thread.ofVirtual()
                .name("mailer-thread")
                .start(runnable);

    }

    private void sendEmailFake() throws InterruptedException {
        long initTime = System.currentTimeMillis();
        LOGGER.info("Sending Email! ");
        Thread.sleep(10000);
        long executionTime = System.currentTimeMillis() - initTime;
        LOGGER.info("Email sent in: " + executionTime + "ms" + " in the thread: " + Thread.currentThread().threadId());
    }


    private void sendEmail(BigDecimal percentUsed) {
        long initTime = System.currentTimeMillis();
        LOGGER.info("Email sending started! ");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("memory-usage@myserver.com");
        message.setTo("pedrottb01@gmail.com");
        message.setSubject("High memory usage");
        message.setText("Current memory usage is: " + percentUsed);
        LOGGER.info("Sending email! ");
        emailSender.send(message);
        LOGGER.info("Email sent! ");
        long executionTime = System.currentTimeMillis() - initTime;
        LOGGER.info("Email sent in: " + executionTime + "ms");
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-swap", name = "proxmox.get-swap-usage")
    public String getSwap() {

        var status = proxmoxApi.getNodeStatus();


        var swap = status.getData().getSwap();
        LOGGER.info("memory = " + swap);
        LOGGER.info("memory = " + swap);
        var percentUsed = BigDecimal.valueOf((swap.getUsed() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        LOGGER.info("Used swap: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((swap.getFree() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        LOGGER.info("Free swap: " + percentFree + "%");

        return "Free swap: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-disk", name = "proxmox.get-disk-usage")
    public String getDisk() {

        var status = proxmoxApi.getNodeStatus();

        var disk = status.getData().getRootfs();
        LOGGER.info("memory = " + disk);
        var percentUsed = BigDecimal.valueOf((disk.getUsed() / disk.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        LOGGER.info("Used disk: " + percentUsed + "%");
        var percentFree = BigDecimal.valueOf((disk.getFree() / disk.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);
        LOGGER.info("Free disk: " + percentFree + "%");

        return "Free disk: " + percentFree + "%";
    }

    @MeasureRunTime
    @Observed(contextualName = "proxmox.get-node-status", name = "proxmox.get-node-status-usage")
    public void getNodeStatus() {

        var status = proxmoxApi.getNodeStatus();

        var memory = status.getData().getMemory();
        var freeMemoryPercent = memory.getFree().divide(memory.getTotal(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

        var usedMemoryPercent = memory.getUsed().divide(memory.getTotal(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

        var swap = status.getData().getSwap();
        var freeSwapPercent = BigDecimal.valueOf((swap.getFree() / swap.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);

        var rootFs = status.getData().getRootfs();
        var freeRootFsPercent = BigDecimal.valueOf((rootFs.getFree() / rootFs.getTotal()) * 100).setScale(2, RoundingMode.HALF_UP);


        //TODO maybe use another threshold that points to this percentage to get warnings if is almost reaching that percentage
        // maybe to get levels of notifications (Warning, Dangerous)
        // Maybe use another type of property to get a map of key and value mapping it to threshold and notification level
        // Maybe include in this map a value of delay time to get this metric
        if (freeMemoryPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeMemory())) < 1) {
            //TODO Notify using the webhook - discord? telegram? whatsapp?
            // email - https://i12bretro.github.io/tutorials/0717.html use smtp config?
            // ntfy - https://docs.ntfy.sh/config/  - smtp config too? Flag to enable or disable smtp from ntfy or disable everything


            LOGGER.info(STR.
                    "Free memory getting dangerous. Actual free: \{freeMemoryPercent} Threshold: \{thresholdProperties.freeMemory()}");

            sendEmailAsync(usedMemoryPercent);

        }

        if (freeSwapPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeSwap())) < 1) {
            //TODO Notify using the webhook, email and ntfy?
            //TODO use string template from java 21
            LOGGER.info("Free swap getting dangerous. Actual free: " + freeSwapPercent + " Threshold: " + thresholdProperties.freeSwap());

        }

        if (freeRootFsPercent.compareTo(BigDecimal.valueOf(thresholdProperties.freeRootfs())) < 1) {
            //TODO Notify using the webhook, email and ntfy?
            //TODO use string template from java 21
            LOGGER.info("Free rootfs getting dangerous. Actual free: " + freeRootFsPercent + " Threshold: " + thresholdProperties.freeRootfs());

        }


    }


}
