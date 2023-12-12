package com.pedrovisk.proxmox.telegram;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.Constants;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

public class TelegramResponseHandler {

    private final SilentSender sender;
    private final Map<Long, TelegramUserState> chatStates;

    public static final String CHAT_STATES = "chatStates";
    public static final String START_TEXT = "Welcome to Pxmx Notifier.\nThis bot allows you to monitor your Proxmox cluster";

    public TelegramResponseHandler(SilentSender sender, DBContext dbContext) {
        this.sender = sender;
        this.chatStates = dbContext.getMap(CHAT_STATES);
    }

    public void replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT);
        sender.execute(message);
        chatStates.put(chatId, TelegramUserState.AWAITING_NAME);
    }

}
