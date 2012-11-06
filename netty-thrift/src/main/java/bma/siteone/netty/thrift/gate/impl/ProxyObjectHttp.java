package bma.siteone.netty.thrift.gate.impl;

import java.net.URL;

import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.io.HostPort;
import bma.common.netty.pool.NettyChannelPool;

public class ProxyObjectHttp extends ProxyObjectBase {

	protected URL url;
	protected String vhost;

	public ProxyObjectHttp(NettyChannelPool pool, URL url) {
		super(pool, new HostPort(url.getHost(), url.getPort() == -1 ? 80
				: url.getPort()));
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getVhost() {
		return vhost;
	}

	public void setVhost(String vhost) {
		this.vhost = vhost;
	}

	@Override
	protected void removeChannelHandler(Channel ch) {
		remove(ch, "netty_thrift_gate_proxy_codec");
		remove(ch, "netty_thrift_gate_proxy_inflater");
		remove(ch, "netty_thrift_gate_proxy");
	}

	@Override
	protected void initChannelHandler(Channel ch, AIStack<Boolean> stack) {
		ChannelPipeline p = remoteChannel.getPipeline();
		p.addBefore("handlerPlaceholder", "netty_thrift_gate_proxy_codec",
				new HttpClientCodec());
		p.addBefore("handlerPlaceholder", "netty_thrift_gate_proxy_inflater",
				new HttpContentDecompressor());
		p.addBefore("handlerPlaceholder", "netty_thrift_gate_proxy",
				new ProxyHandler(stack) {

					private boolean readingChunks;
					private ChannelBuffer buffer;

					@Override
					public void messageReceived(ChannelHandlerContext ctx,
							MessageEvent e) throws Exception {
						if (!readingChunks) {
							readingChunks = true;
							HttpResponse response = (HttpResponse) e
									.getMessage();
							if (!HttpResponseStatus.OK.equals(response
									.getStatus())) {
								stack.failure(new TTransportException(
										"HTTP Response code: "
												+ response.getStatus()));
								return;
							}
							ChannelBuffer content = response.getContent();
							int len = (int) HttpHeaders
									.getContentLength(response);
							if (len == 0) {
								if (response.isChunked()) {
									if (log.isDebugEnabled()) {
										log.debug(
												"{}-chunked <== {}",
												new Object[] {
														response.getStatus(),
														url });
									}
									buffer = ChannelBuffers.directBuffer(1024);
									buffer.writeBytes(content);
									return;
								}
								len = content.readableBytes();
							}
							if (log.isDebugEnabled()) {
								log.debug("{}-{} <== {}", new Object[] {
										response.getStatus(), len, url });
							}
							setContentLength(len);
							processContent(content);

						} else {
							HttpChunk chunk = (HttpChunk) e.getMessage();
							if (chunk.isLast()) {
								readingChunks = false;
								if (buffer != null) {
									setContentLength(buffer.readableBytes());
									processContent(buffer);
								}
							} else {
								ChannelBuffer content = chunk.getContent();
								if (buffer != null) {
									buffer.writeBytes(content);
								} else {
									processContent(content);
								}
							}
						}
					}
				});
	}

	@Override
	protected void writeMessage(Channel ch, ChannelBuffer data)
			throws Exception {
		// copyfrom TNettyHttpWriteOnlyTransport
		HttpRequest request;
		request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
				url.toURI().getRawPath());
		request.setHeader(HttpHeaders.Names.HOST,
				ValueUtil.empty(vhost) ? host.toString() : vhost);
		request.setHeader("Connection", "Keep-Alive");
		request.setHeader("Content-Type", "application/x-thrift");
		request.setHeader("Accept", "application/x-thrift");
		request.setHeader("User-Agent", "Java/TNettyHttpTransport");
		request.setHeader("Content-Length", data.writerIndex());
		request.setContent(data);

		// Send the HTTP request.
		if (log.isDebugEnabled()) {
			log.debug("send request => {}", url);
		}
		ch.write(request);
	}

	@Override
	public String toString() {
		return "Proxy[" + this.url + "]";
	}
}
