package bma.siteone.netty.thrift.core;

import java.net.URL;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class TNettyHttpWriteOnlyTransport extends TTransport {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(TNettyHttpWriteOnlyTransport.class);

	protected Channel channel;
	protected ChannelBuffer writeBuffer;
	protected URL url;
	protected int maxContent = Integer.MAX_VALUE;

	public TNettyHttpWriteOnlyTransport(Channel ch, URL url, int mlen) {
		super();
		this.channel = ch;
		this.url = url;
		this.maxContent = mlen;
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

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void open() throws TTransportException {

	}

	@Override
	public void close() {
		channel.close();
	}

	@Override
	public int read(byte[] buf, int off, int len) throws TTransportException {
		throw new UnsupportedOperationException("read");
	}

	public void bindHandler(ChannelPipeline p) {
		String PIPELINE_NAME = "a";
		ChannelHandler ch2, ch;
		ch2 = ch = null;
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
