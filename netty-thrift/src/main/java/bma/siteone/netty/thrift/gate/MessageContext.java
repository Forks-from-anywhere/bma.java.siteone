package bma.siteone.netty.thrift.gate;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

public interface MessageContext {

	public Channel getNettyChannel();

	public ChannelBuffer getMessage();

	public void setMessage(ChannelBuffer v);

	public <OTYPE> OTYPE getProperty(String name, Class<OTYPE> cls);

	public void setProperty(String name, Object v);

}
