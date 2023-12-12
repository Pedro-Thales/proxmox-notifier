package com.pedrovisk.proxmox.telegram;


import com.pedrovisk.proxmox.CustomFeignConfiguration;
import com.pedrovisk.proxmox.utils.MeasureRunTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "telegram", url = "${telegram.bot.api-url}", configuration = {CustomFeignConfiguration.class})
public interface TelegramApi {

    @RequestMapping(method = RequestMethod.GET, value = "/bot${telegram.bot.token}/sendMessage")
    @MeasureRunTime
    ResponseEntity sendMessageToBotChat(
            @RequestParam(value = "chat_id", required = false, defaultValue = "${telegram.bot.chat-id}") String chat_id,
            @RequestParam String text);


    @RequestMapping(
            method = RequestMethod.GET,
            value = "/bot${telegram.bot.token}/sendMessage?parse_mode=MarkdownV2&chat_id=${telegram.bot.chat-id}")
    ResponseEntity sendMessageToBotChatDefault(@RequestParam String text);

}
