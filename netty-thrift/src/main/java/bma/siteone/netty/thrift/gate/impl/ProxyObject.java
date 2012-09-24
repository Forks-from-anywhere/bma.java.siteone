package bma.siteone.netty.thrift.gate.impl;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackStep;
import bma.common.langutil.io.HostPort;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.core.NCHFramed;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.remote.RemoteBreakException;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class ProxyObject {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ProxyObject.class);

	protected NettyChannelPool pool;
	protected RuntimeRemote runtimeRemote;
	protected HostPort host;
	protected Channel remoteChannel;
	protected MessageContext context;

	enum STAGE {
		INIT, CREATE, WRITE2REMOTE, WAIT, WRITE2PEER, END
	}

	private STAGE stage = STAGE.INIT;

	public ProxyObject(NettyChannelPool pool, HostPort host) {
		this(pool, host, null);
	}

	public ProxyObject(NettyChannelPool pool, HostPort host,
			MessageContext context) {
		super();
		this.pool = pool;
		this.host = host;
		this.context = context;
	}

	public RuntimeRemote getRuntimeRemote() {
		return runtimeRemote;
	}

	public void setRuntimeRemote(RuntimeRemote runtimeRemote) {
		this.runtimeRemote = runtimeRemote;
	}

	public MessageContext getContext() {
		return context;
	}

	public void setContext(MessageContext context) {
		this.context = context;
	}

	public void close() {
		if (remoteChannel != null) {
			if (log.isDebugEnabled()) {
				log.debug("force close remote {}", host);
			}
			Channel ch = remoteChannel;
			remoteChannel = null;

			ch.close();
			pool.returnObject(host, ch);
		}
	}

	public void returnChannel() {
		if (remoteChannel != null) {
			if (log.isDebugEnabled()) {
				log.debug("return channel at {}", stage);
			}
			remoteChannel.getPipeline().remove("netty_thrift_gate_proxy");
			pool.returnObject(host, remoteChannel);
		}
	}

	public boolean create(AIStack<Boolean> stack) {
		AIStackStep<Channel, Boolean> step = new AIStackStep<Channel, Boolean>(
				stack) {

			@Override
			protected boolean next(Channel result) {
				if (result == null) {
					return this.delegate().failure(
							new RemoteBreakException("connect remote[" + host
									+ "] fail"));
				}
				remoteChannel = result;
				return proxy(delegate());
			}

			@Override
			public boolean failure(Throwable t) {
				if (!(t instanceof RemoteBreakException)) {
					t = new RemoteBreakException("connect remote[" + host
							+ "] fail - " + t.getMessage(), t);
				}
				return super.failure(t);
			}
		};
		if (runtimeRemote != null && runtimeRemote.isRemoteBreak(host)) {
			return stack.failure(new RemoteBreakException("remote[" + host
					+ "] check break"));
		}
		this.stage = STAGE.CREATE;
		return pool.borrowObject(step, host);
	}

	protected boolean proxy(final AIStack<Boolean> stack) {
		Channel ch = remoteChannel;
		ch.getPipeline().addAfter("handlerPlaceholder",
				"netty_thrift_gate_proxy", new SimpleChannelUpstreamHandler() {

					private byte[] header;
					private int headerPos;
					private int remain = -1;

					@Override
					public void channelDisconnected(ChannelHandlerContext ctx,
							ChannelStateEvent e) throws Exception {
						if (log.isDebugEnabled()) {
							log.debug("remote channel [{}] close at {}", host,
									stage);
						}
						if (!stage.equals(STAGE.END)) {
							if (stage.ordinal() < STAGE.WRITE2REMOTE.ordinal()) {
								stack.success(false);
							} else {
								stack.failure(new IOException("remote channel["
										+ host + "] close"));
							}
						}
						super.channelDisconnected(ctx, e);
					}

					@Override
					public void messageReceived(ChannelHandlerContext ctx,
							MessageEvent e) throws Exception {
						if (e.getMessage() instanceof ChannelBuffer) {
							ChannelBuffer hbuf = null;
							ChannelBuffer buf = (ChannelBuffer) e.getMessage();
							if (remain == -1) {
								stage = STAGE.WAIT;
								if (header == null) {
									header = new byte[4];
									headerPos = 0;
								}
								int read = 4 - headerPos;
								if (buf.readableBytes() < read) {
									read = buf.readableBytes();
								}
								if (read > 0) {
									buf.readBytes(header, headerPos, read);
									headerPos += read;
								}
								// wait next
								if (headerPos < 4) {
									if (log.isDebugEnabled()) {
										log.debug("write remote header {}/4",
												headerPos);
									}
									return;
								}
								remain = NCHFramed.decodeFrameSize(header);
								hbuf = ChannelBuffers.wrappedBuffer(header);
							}

							stage = STAGE.WRITE2PEER;
							final int writeBytes = buf.readableBytes();

							if (log.isDebugEnabled()) {
								log.debug("remote2peer - {}", writeBytes);
							}

							Channel peer = context.getNettyChannel();
							if (!peer.isOpen()) {
								remain -= writeBytes;
								if (remain <= 0) {
									stack.success(false);
								}
								return;
							}

							ChannelBuffer wbuf = buf;
							if (hbuf != null) {
								wbuf = ChannelBuffers.wrappedBuffer(hbuf, buf);
							}
							// test timeout
							// if(remain>=0)return;
							peer.write(wbuf).addListener(
									new ChannelFutureListener() {

										@Override
										public void operationComplete(
												ChannelFuture future)
												throws Exception {
											remain -= writeBytes;
											if (remain <= 0) {
												stage = STAGE.END;
												stack.success(true);
											}
										}
									});
						}
						super.messageReceived(ctx, e);
					}
				});

		if (!context.getNettyChannel().isOpen()) {
			return stack.success(false);
		}

		this.stage = STAGE.WRITE2REMOTE;
		ChannelBuffer data = context.getMessage();
		byte[] buf = new byte[4];
		int size = data.readableBytes();
		NCHFramed.encodeFrameSize(size, buf);
		ch.write(ChannelBuffers.wrappedBuffer(buf));
		ch.write(data);
		// after write to remote,clear the request content
		context.setMessage(null);
		if (log.isDebugEnabled()) {
			log.debug("peer2remote {}", size);
		}
		return false;
	}
}
