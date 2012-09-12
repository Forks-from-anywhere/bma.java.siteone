package bma.siteone.clound;

import org.junit.Before;
import org.junit.Test;

import bma.common.langutil.ai.boot.AIServerBoot;
import bma.common.langutil.log.LogbackUtil;

public class CloundServerTC {

	@Before
	public void setUp() {
		LogbackUtil.setLevel("bma.common.langutil.concurrent.ProcessTimeWheel",
				"INFO");
	}

	@Test
	public void bootServer() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/clound/netty-thrift-clound-local.xml");
		AIServerBoot.main(new String[0]);
	}
}
