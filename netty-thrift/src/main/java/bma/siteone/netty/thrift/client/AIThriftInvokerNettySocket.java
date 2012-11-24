package bma.siteone.netty.thrift.client;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.netty.SupportedNettyChannel;
import bma.common.netty.client.CommonNettyClientSender;
import bma.common.netty.client.NettyClient;
import bma.common.thrift.ThriftClientConfig;
import bma.common.thrift.ai.AIThriftInvoker;
import bma.common.thrift.ai.TAIBaseServiceClient;
import bma.siteone.netty.thrift.core.NCHFramed;
import bma.siteone.netty.thrift.core.TNettyChannelBufferTransport;
import bma.siteone.netty.thrift.core.TNettyFramedWriteOnlyTransport;

public class AIThriftInvokerNettySocket implements AIThriftInvoker,
		SupportedNettyChannel {

	protected NettyClient client;
	protected ThriftClientConfig config;

	public AIThriftInvokerNettySocket(NettyClient client, ThriftClientConfig cfg) {
		super();
		this.client = client;
		this.config = cfg;
	}

	public NettyClient getClient() {
		return client;
	}

	public void setClient(NettyClient client) {
		this.client = client;
	}

	@Override
	public Channel getChannel() {
		return this.client != null ? this.client.getChannel() : null;
	}

	@Override
	public <TYPE> boolean invoke(AIStack<TYPE> stack, final int seqid,
			final TBase result, final String name, final TBase avgs)
			throws TException {
		client.send(new CommonNettyClientSender<TYPE>(stack) {

			@Override
			public void bindNettyClient(NettyClient client, boolean bind) {
				if (bind) {
					client.addHandler("frame",
							new NCHFramed(config.getFrameMaxLength()));
				} else {
					client.removeHandler("frame");
				}
			}

			@Override
			public boolean messageReceived(final NettyClient client,
					Object message) {
				if (message instanceof ChannelBuffer) {
					ChannelBuffer cb = (ChannelBuffer) message;
					TTransport transport = new TNettyChannelBufferTransport(cb);
					try {
						TAIBaseServiceClient.readBase(subStack(client),
								config.createProtocol(transport), seqid, name,
								result);
					} catch (Exception e) {
						end(client, null, e);
					}
					return true;
				}
				return false;
			}

			@Override
			public void sendRequest(NettyClient client) {
				try {
					TTransport transport = new TNettyFramedWriteOnlyTransport(
							client.getChannel(), config.getFrameMaxLength());
					TAIBaseServiceClient.sendBase(
							config.createProtocol(transport), name, avgs, seqid);
					if (result == null) {
						end(client, null, null);
					}
				} catch (Exception e) {
					end(client, null, e);
				}
			}
		});
		return false;
	}
}
