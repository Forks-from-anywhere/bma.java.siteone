package bma.siteone.netty.thrift.gate.impl;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import bma.common.langutil.core.ClassUtil;
import bma.siteone.netty.thrift.gate.MessageContext;

public class SimpleMessageContext implements MessageContext {

	protected Channel nettyChannel;
	protected ChannelBuffer message;
	protected Map<String, Object> properties;

	@Override
	public Channel getNettyChannel() {
		return nettyChannel;
	}

	public void setNettyChannel(Channel ch) {
		nettyChannel = ch;
	}

	@Override
	public ChannelBuffer getMessage() {
		return message;
	}

	@Override
	public void setMessage(ChannelBuffer v) {
		message = v;
	}

	@Override
	public <OTYPE> OTYPE getProperty(String name, Class<OTYPE> cls) {
		if (properties == null)
			return null;
		return ClassUtil.cast(cls, properties.get(name));
	}

	@Override
	public void setProperty(String name, Object v) {
		if (properties == null) {
			properties = new HashMap<String, Object>();
		}
		properties.put(name, v);
	}

}
