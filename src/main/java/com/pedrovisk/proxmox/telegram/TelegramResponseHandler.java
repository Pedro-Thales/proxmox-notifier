package com.pedrovisk.proxmox.telegram;

import org.telegram.telegrambots.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.abilitybots.api.sender.SilentSender;

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
        sender.send(START_TEXT, chatId);
        chatStates.put(chatId, TelegramUserState.AWAITING_NAME);
    }

}
