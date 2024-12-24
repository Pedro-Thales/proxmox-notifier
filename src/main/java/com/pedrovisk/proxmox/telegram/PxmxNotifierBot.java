package com.pedrovisk.proxmox.telegram;

import com.pedrovisk.proxmox.configuration.TelegramProperties;
import com.pedrovisk.proxmox.service.SshService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class PxmxNotifierBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final TelegramProperties properties;
    private final TelegramClient telegramClient;
    private final SshService sshService;

    public PxmxNotifierBot(TelegramProperties properties, SshService sshService) {
        this.properties = properties;
        this.sshService = sshService;
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        log.info("Registered bot running state is: " + botSession.isRunning());
    }

    @Override
    public String getBotToken() {
        return properties.token();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (message) {
                case "/temperature", "/temp" -> sendTemperatureMessage();
                default -> sendDefaultMessage(chatId);
            }
        }

    }

    private void sendDefaultMessage(long chatId) {
        SendMessage messageToSend = SendMessage.builder().chatId(chatId).text("Invalid command").build();
        try {
            telegramClient.execute(messageToSend);
        } catch (Exception e) {
            log.error("Error while sending message", e);
        }
    }

    private void sendTemperatureMessage() {
        try {
            sshService.call(true);
        } catch (Exception e ){
            log.error("Error while getting temperature", e);
        }
    }
}
