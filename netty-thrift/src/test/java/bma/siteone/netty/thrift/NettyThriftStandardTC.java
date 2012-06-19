package bma.siteone.netty.thrift;

import org.junit.Test;

import bma.common.langutil.ai.boot.AIServerBoot;

public class NettyThriftStandardTC {

	@Test
	public void boot() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/netty/thrift/netty-thrift-standard.xml");
		AIServerBoot.main(new String[0]);
	}
	
	@Test
	public void serviceHub() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/netty/thrift/netty-thrift-servicehub.xml");
		AIServerBoot.main(new String[0]);
	}
}
