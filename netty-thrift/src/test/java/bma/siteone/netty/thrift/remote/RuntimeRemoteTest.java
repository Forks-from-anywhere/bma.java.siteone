package bma.siteone.netty.thrift.remote;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bma.common.langutil.ai.common.AIExecutorPassive;
import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.concurrent.TimerManager;
import bma.common.langutil.io.HostPort;
import bma.common.netty.client.NettyClientBootstrap;
import bma.siteone.netty.thrift.client.AIThriftClientProviderNetty;
import bma.siteone.netty.thrift.remote.impl.RuntimeRemoteImpl;

public class RuntimeRemoteTest {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(RuntimeRemoteTest.class);

	protected NettyClientBootstrap boot() {
		NettyClientBootstrap b = new NettyClientBootstrap();
		b.init();
		return b;
	}

	AIExecutorPassive main;
	TimerManager timer;

	@Before
	public void setupMain() {
		main = new AIExecutorPassive("main");
		main.setUp();
		main.makeShutdownHook();

		timer = new TimerManager();
		timer.startManager();
		AIExecutor.setTimerManager(timer);
	}

	@After
	public void closeMain() {
		main.close(1, TimeUnit.SECONDS);
		timer.stopManager();
	}

	public RuntimeRemoteImpl runtimeRemote() {

		AIThriftClientProviderNetty te = new AIThriftClientProviderNetty();
		te.setBootstrap(boot());

		RuntimeRemoteImpl r = new RuntimeRemoteImpl();
		r.setFrameMaxLength(1024 * 1024);
		r.setThriftClientProvider(te);
		return r;
	}

	@Test
	public void base() throws Exception {
		RuntimeRemoteImpl rr = runtimeRemote();

		HostPort host = new HostPort();
		host.setHostString("localhost", 9091);
		for (int i = 0; i < 60; i++) {
			Object v1 = rr.isRemoteBreak(host);
			System.out.println(host + " break " + v1);
			main.run(1 * 1000);
		}

		rr.close();
	}
}
