package com.pedrovisk.proxmox;


import com.pedrovisk.proxmox.models.json.RootConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = "proxmox.configuration-file-path=classpath:config/config.json")
class ProxmoxNotifierApplicationTests {

	@Autowired
	RootConfiguration rootConfiguration;

	@Test
	void contextLoads() {
		assertNotNull(rootConfiguration);
		assertEquals(1, rootConfiguration.getNodes().size());
		assertEquals("pxmx", rootConfiguration.getNodes().getFirst().getId());

	}

}
