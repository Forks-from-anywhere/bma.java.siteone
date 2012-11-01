package bma.siteone.netty.thrift;

import org.junit.Before;
import org.junit.Test;

import bma.common.langutil.ai.boot.AIServerBoot;
import bma.common.langutil.log.LogbackUtil;

public class NettyThriftStandardTC {

	@Before
	public void setUp() {
		LogbackUtil.setLevel("bma.common.langutil.concurrent.ProcessTimeWheel",
				"INFO");
	}

	@Test
	public void boot() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/netty/thrift/netty-thrift-standard.xml");
		AIServerBoot.main(new String[0]);
	}
	
	@Test
	public void gate() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/netty/thrift/netty-thrift-gate.xml");
		AIServerBoot.main(new String[0]);
	}
	
	@Test
	public void remoteInfo() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/netty/thrift/netty-thrift-remoteinfo.xml");
		AIServerBoot.main(new String[0]);
	}

	@Test
	public void serviceHub() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/netty/thrift/netty-thrift-servicehub.xml");
		AIServerBoot.main(new String[0]);
	}

	@Test
	public void servicePeer() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/netty/thrift/netty-thrift-hubpeer.xml");
		AIServerBoot.main(new String[0]);
	}

	@Test
	public void serviceHello() {
		System.setProperty("spring_server_xml",
				"classpath:bma/siteone/netty/thrift/netty-thrift-hello.xml");
		AIServerBoot.main(new String[0]);
	}
	
	
}
