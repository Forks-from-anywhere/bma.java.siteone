package bma.siteone.netty.thrift.core;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class NCHServerFramedTransport extends OneToOneDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (msg instanceof NCHFramed.FramedChannelBuffer) {
			NCHFramed.FramedChannelBuffer fcb = (NCHFramed.FramedChannelBuffer) msg;
			return new TNettyServerFramedTransport(ctx.getChannel(),
					fcb.getBuffer(), fcb.getMaxLength());
		}
		return msg;
	}

}
