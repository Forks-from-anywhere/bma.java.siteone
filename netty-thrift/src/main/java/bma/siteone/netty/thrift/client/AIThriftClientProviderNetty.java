package bma.siteone.netty.thrift.client;

import org.apache.thrift.TException;
import org.jboss.netty.channel.ChannelHandler;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.pipeline.CommonPipelineBuilder;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.ThriftClientFactory;
import bma.common.thrift.ai.AIThriftClientFactory;
import bma.common.thrift.provider.AIThriftClientProvider;
import bma.common.thrift.provider.ThriftClientProviderSimple;

public class AIThriftClientProviderNetty implements AIThriftClientProvider {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AIThriftClientProviderNetty.class);

	protected NettyClientBootstrap bootstrap;
	protected CommonPipelineBuilder<ChannelHandler> pipelineBuilder;
	protected int traceBufferSize = 0;

	public NettyClientBootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(NettyClientBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	public CommonPipelineBuilder<ChannelHandler> getPipelineBuilder() {
		return pipelineBuilder;
	}

	public void setPipelineBuilder(
			CommonPipelineBuilder<ChannelHandler> pipelineBuilder) {
		this.pipelineBuilder = pipelineBuilder;
	}

	public int getTraceBufferSize() {
		return traceBufferSize;
	}

	public void setTraceBufferSize(int traceBufferSize) {
		this.traceBufferSize = traceBufferSize;
	}

	@Override
	public ThriftClientFactory createFactory(String entry) {
		return createAIFactory(entry);
	}

	@Override
	public AIThriftClientFactory createAIFactory(String entry) {
		AIThriftClientFactoryNetty fac = new AIThriftClientFactoryNetty();
		fac.setBootstrap(bootstrap);
		fac.setPipelineBuilder(pipelineBuilder);
		fac.setTraceBufferSize(traceBufferSize);
		if (!ThriftClientProviderSimple.buildConfig(entry, fac)) {
			if (log.isWarnEnabled()) {
				log.warn("unknow entry({})", entry);
			}
			return null;
		}
		if (log.isDebugEnabled()) {
			log.debug("{} => {}", entry, fac);
		}
		return fac;
	}

	@Override
	public ThriftClient createClient(String entry) throws TException {
		ThriftClientFactory fac = createFactory(entry);
		return fac == null ? null : fac.createThriftClient();
	}

	@Override
	public boolean createClient(AIStack<ThriftClient> stack, String entry)
			throws TException {
		AIThriftClientFactory fac = createAIFactory(entry);
		if (fac == null)
			return stack.success(null);
		return fac.createThriftClient(stack);
	}

}
