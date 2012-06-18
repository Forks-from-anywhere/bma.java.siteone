package bma.siteone.netty.thrift;

import java.util.concurrent.Executors;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.junit.Test;

import bma.common.langutil.ai.stack.AIStackSimple;
import bma.common.langutil.io.HostPort;
import bma.common.langutil.log.LogPrinter.LEVEL;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.netty.handler.ChannelHandlerLog;
import bma.common.netty.handler.ChannelHandlerLog.TYPE;
import bma.common.thrift.sample.Hello;
import bma.common.thrift.sample.Hello4AI;
import bma.siteone.netty.thrift.client.TNettyClientFramedTransport;
import bma.siteone.netty.thrift.core.TNettyChannelTransport;

public class NettyThriftClientTest {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NettyThriftClientTest.class);

	protected NettyClientBootstrap boot() {
		NettyClientBootstrap b = new NettyClientBootstrap();
		b.init();
		return b;
	}

	private boolean dolog = false;

	protected TTransport trans1() throws Exception {
		NettyClientBootstrap b = boot();
		HostPort host = new HostPort("localhost", 9091);

		ChannelPipelineFactory fac = new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline p = Channels.pipeline();
				if (dolog) {
					p.addLast("log", new ChannelHandlerLog(log, LEVEL.DEBUG,
							TYPE.ALL, 128));
				}
				return p;
			}
		};

		ChannelFuture cf = b.connect(host.createInetSocketAddress(), null, fac);
		cf.awaitUninterruptibly();
		if (cf.isSuccess()) {
			Channel ch = cf.getChannel();
			TNettyChannelTransport t = new TNettyChannelTransport(ch);
			t.bindHandler();

			return new TFramedTransport(t);
		}
		return null;
	}

	protected TTransport trans2() throws Exception {
		NettyClientBootstrap b = boot();
		HostPort host = new HostPort("localhost", 9091);

		ChannelPipelineFactory fac = new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline p = Channels.pipeline();
				if (dolog) {
					p.addLast("log", new ChannelHandlerLog(log, LEVEL.DEBUG,
							TYPE.ALL, 128));
				}
				return p;
			}
		};

		ChannelFuture cf = b.connect(host.createInetSocketAddress(), null, fac);
		cf.awaitUninterruptibly();
		if (cf.isSuccess()) {
			Channel ch = cf.getChannel();
			TNettyClientFramedTransport t = new TNettyClientFramedTransport(ch,
					1024 * 1024);
			t.bindHandler();

			return t;
		}
		return null;
	}

	@Test
	public void forceTest() throws Exception {

		TTransport trans = trans2();
		if (trans != null) {
			TProtocol p = new TBinaryProtocol(trans);

			Hello.Client cl = new Hello.Client(p);
			for (int i = 0; i < 10000; i++) {
				String r = null;
				r = cl.name("test1");
				log.info(r);
				r = cl.name("test2");
				log.info(r);
				// cl.error("error");
			}

			trans.close();
		}
	}

	@Test
	public void channelTransport() throws Exception {

		TTransport trans = trans1();
		if (trans != null) {
			TProtocol p = new TBinaryProtocol(trans);

			Hello.Client cl = new Hello.Client(p);
			String r = null;
			r = cl.name("test1");
			log.info(r);
			r = cl.name("test2");
			log.info(r);
			// cl.error("error");

			trans.close();
		}
	}

	@Test
	public void framedChannelTransport() throws Exception {

		TTransport trans = trans2();
		if (trans != null) {
			TProtocol p = new TBinaryProtocol(trans);

			Hello.Client cl = new Hello.Client(p);
			String r = null;
			r = cl.name("test1");
			log.info(r);
			r = cl.name("test2x");
			log.info(r);
			// cl.error("error");

			trans.close();
		}
	}

	@Test
	public void aiThriftClient() throws Exception {

		TTransport trans = trans2();
		if (trans != null) {
			TProtocol p = new TBinaryProtocol(trans);

			Hello4AI.Client cl = new Hello4AI.Client(p);
			cl.setSimulatorExecutor(Executors.newCachedThreadPool());
			String r = null;
			AIStackSimple<String> s = null;
			AIStackSimple<Boolean> b = null;

			s = new AIStackSimple<String>(null);
			cl.name(s, "test1");
			r = s.get();
			log.info(r);

			s = new AIStackSimple<String>(null);
			cl.name(s, "test2");
			r = s.get();
			log.info(r);

			b = new AIStackSimple<Boolean>(null);
			cl.say(b, "test2");
			log.info("{}", b.get());
			// cl.error("error");

			trans.close();
		}
	}
}
