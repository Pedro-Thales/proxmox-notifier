package com.pedrovisk.proxmox;


import com.pedrovisk.proxmox.models.json.RootConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.telegram.telegrambots.longpolling.starter.TelegramBotInitializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = "proxmox.configuration-file-path=classpath:config/config.json")
class ProxmoxNotifierApplicationTests {

	@Autowired
	RootConfiguration rootConfiguration;

	@MockBean
	private TelegramBotInitializer telegramBotInitializer;

	@Test
	void contextLoads() {

		assertNotNull(rootConfiguration);
		assertEquals(1, rootConfiguration.getNodes().size());
		assertEquals("pxmx", rootConfiguration.getNodes().getFirst().getId());

	}

}
