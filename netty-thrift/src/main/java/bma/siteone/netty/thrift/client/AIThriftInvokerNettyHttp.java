package bma.siteone.netty.thrift.client;

import java.io.IOException;
import java.net.URL;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.netty.SupportedNettyChannel;
import bma.common.netty.client.CommonNettyClientSender;
import bma.common.netty.client.NettyClient;
import bma.common.thrift.ThriftClientConfig;
import bma.common.thrift.ai.AIThriftInvoker;
import bma.common.thrift.ai.TAIBaseServiceClient;
import bma.siteone.netty.thrift.core.NCHFramed;
import bma.siteone.netty.thrift.core.TNettyChannelBufferTransport;
import bma.siteone.netty.thrift.core.TNettyHttpWriteOnlyTransport;

public class AIThriftInvokerNettyHttp implements AIThriftInvoker,
		SupportedNettyChannel {

	protected NettyClient client;
	protected ThriftClientConfig config;

	public AIThriftInvokerNettyHttp(NettyClient client, ThriftClientConfig cfg) {
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
			public boolean messageReceived(NettyClient client, Object message) {
				if (message instanceof HttpResponse) {
					HttpResponse response = (HttpResponse) message;

					if (!HttpResponseStatus.OK.equals(response.getStatus())) {
						end(client, null, new TTransportException(
								"HTTP Response code: " + response.getStatus()));
					}

					ChannelBuffer content = response.getContent();
					if (content.readableBytes() > 4) {
						content.markReaderIndex();
						byte[] buf = new byte[4];
						content.readBytes(buf);
						int size = NCHFramed.decodeFrameSize(buf);
						if (size <= content.readableBytes()) {
							content.resetReaderIndex();
							TTransport transport = new TNettyChannelBufferTransport(
									content);
							try {
								TAIBaseServiceClient.readBase(subStack(client),
										config.createProtocol(transport), seqid,
										name, result);
							} catch (Exception e) {
								end(client, null, e);
							}
							return true;
						}
					}
					end(client,
							null,
							new TTransportException("response content error",
									new IOException(content
											.toString(CharsetUtil.UTF_8))));
					return true;
				}
				return false;
			}

			@Override
			public void sendRequest(NettyClient client) {
				try {
					URL url = new URL(config.getUrl());
					TTransport transport = new TNettyHttpWriteOnlyTransport(
							client.getChannel(), url, config.getFrameMaxLength());
					TAIBaseServiceClient.sendBase(
							config.createProtocol(transport), name, avgs, seqid);
				} catch (Exception e) {
					end(client, null, e);
				}
			}
		});
		return false;
	}
}
