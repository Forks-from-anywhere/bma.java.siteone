package bma.siteone.netty.thrift.client;

import java.io.IOException;
import java.net.URL;

import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

import bma.siteone.netty.thrift.core.NCHFramed;

public class TNettyClientHttpTransport extends TAINettyClientTransport {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(TNettyClientHttpTransport.class);

	protected ChannelBuffer writeBuffer;
	protected URL url;
	protected int maxContent = Integer.MAX_VALUE;

	public TNettyClientHttpTransport(Channel ch, URL url) {
		super(ch);
		this.url = url;
	}

	public int getMaxContent() {
		return maxContent;
	}

	public void setMaxContent(int maxContent) {
		this.maxContent = maxContent;
	}

	public void write(byte[] buf, int off, int len) throws TTransportException {
		if (writeBuffer == null) {
			writeBuffer = ChannelBuffers.dynamicBuffer(1024);
		}
		writeBuffer.writeBytes(buf, off, len);
	}

	@Override
	public void flush() throws TTransportException {
		ChannelBuffer content = writeBuffer;
		writeBuffer = null;
		try {
			
			// copyto ProxyObjectHttp
			HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
					HttpMethod.POST, url.toURI().getRawPath());
			StringBuilder host = new StringBuilder(url.getHost());
			if (url.getPort() != -1) {
				host.append(":");
				host.append(url.getPort());
			}
			request.setHeader(HttpHeaders.Names.HOST, url.getHost());
			request.setHeader("Connection", "Keep-Alive");
			request.setHeader("Content-Type", "application/x-thrift");
			request.setHeader("Accept", "application/x-thrift");
			request.setHeader("User-Agent", "Java/TNettyHttpTransport");
			request.setHeader("Content-Length", content.writerIndex());
			request.setContent(content);

			// Send the HTTP request.
			channel.write(request);

		} catch (Exception e) {
			throw new TTransportException(e);
		}
	}

	public void bindHandler() {
		ChannelPipeline p = channel.getPipeline();
		ChannelHandler ch2 = new SimpleChannelUpstreamHandler() {
			@Override
			public void channelClosed(ChannelHandlerContext ctx,
					ChannelStateEvent e) throws Exception {
				processError(new IllegalStateException("closed"));
				super.channelClosed(ctx, e);
			}
		};
		ChannelHandler ch = new SimpleChannelUpstreamHandler() {
			@Override
			public void messageReceived(ChannelHandlerContext ctx,
					MessageEvent e) throws Exception {
				Object obj = e.getMessage();
				// copyto ProxyObjectHttp
				if (obj instanceof HttpResponse) {
					HttpResponse response = (HttpResponse) e.getMessage();

					if (!HttpResponseStatus.OK.equals(response.getStatus())) {
						processError(new TTransportException(
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
							addReadBuffer(content);
							return;
						}
					}
					processError(new TTransportException(
							"response content error", new IOException(
									content.toString(CharsetUtil.UTF_8))));
					return;
				}
				super.messageReceived(ctx, e);
			}
		};
		if (p.get(PIPELINE_NAME) != null) {
			p.addBefore(PIPELINE_NAME, "closed", ch2);
			p.addBefore(PIPELINE_NAME, "codec", new HttpClientCodec());
			p.addBefore(PIPELINE_NAME, "chunkded", new HttpChunkAggregator(
					maxContent));
			p.addBefore(PIPELINE_NAME, "inflater",
					new HttpContentDecompressor());
			p.replace(PIPELINE_NAME, PIPELINE_NAME, ch);
		} else {
			p.addLast("codec", new HttpClientCodec());
			p.addLast("chunkded", new HttpChunkAggregator(maxContent));
			p.addLast("inflater", new HttpContentDecompressor());
			p.addLast("closed", ch2);
			p.addLast(PIPELINE_NAME, ch);
		}
	}
}
