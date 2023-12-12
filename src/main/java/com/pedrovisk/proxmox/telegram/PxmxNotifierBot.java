package com.pedrovisk.proxmox.telegram;

import com.pedrovisk.proxmox.configuration.TelegramProperties;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

@Component
public class PxmxNotifierBot extends AbilityBot {

    private final TelegramProperties properties;
    private final TelegramResponseHandler responseHandler;

    //TODO user this bot to get temperature information and maybe be notified from other things.
    //      To get temperature try to call pxmx api to send a bash command to show temperature and get result


    public static final String START_DESCRIPTION = "Starts the bot";

    public PxmxNotifierBot(TelegramProperties properties) {
        super(properties.token(), "PxmxNotifierBot");
        this.properties = properties;
        this.responseHandler = new TelegramResponseHandler(silent, db);
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info(START_DESCRIPTION)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    @Override
    public long creatorId() {
        return 1L;
    }
}
