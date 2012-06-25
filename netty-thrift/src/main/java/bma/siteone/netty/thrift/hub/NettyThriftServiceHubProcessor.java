package bma.siteone.netty.thrift.hub;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackNone;
import bma.common.langutil.core.ValueUtil;
import bma.common.thrift.ai.AIBaseStack;
import bma.common.thrift.ai.TAIProcessFunction;
import bma.common.thrift.servicehub.ThriftServicePointInfo;
import bma.common.thrift.servicehub.impl.ThriftServiceHub4ThriftAI;
import bma.common.thrift.servicehub.protocol.TServiceHub.registerThriftService_args;
import bma.common.thrift.servicehub.protocol.TServiceHub.registerThriftService_result;
import bma.common.thrift.servicehub.protocol.TServiceHub4AI.Iface;
import bma.common.thrift.servicehub.protocol.TServiceHub4AI.Processor;
import bma.common.thrift.servicehub.protocol.TServicePointId;
import bma.common.thrift.servicehub.protocol.TServicePointInfo;
import bma.siteone.netty.thrift.core.TNettyServerFramedTransport;

public class NettyThriftServiceHubProcessor<IFACE extends Iface> extends
		Processor<IFACE> {

	public NettyThriftServiceHubProcessor(IFACE iface) {
		super(iface);
		if (!(iface instanceof ThriftServiceHub4ThriftAI)) {

		}
		processMap.put("registerThriftService", new registerThriftServiceExt());
	}

	private static class registerThriftServiceExt<I extends Iface> extends
			TAIProcessFunction<I, registerThriftService_args> {
		public registerThriftServiceExt() {
			super("registerThriftService");
		}

		@Override
		protected registerThriftService_args getEmptyArgsInstance() {
			return new registerThriftService_args();
		}

		@Override
		protected boolean getResult(TProtocol in, TProtocol out,
				AIStack<TBase> stack, final I iface,
				registerThriftService_args args) throws TException {
			TServicePointInfo info = args.getInfo();
			String sessionKey = null;

			if (info.getId() != null && info.getProperties() != null) {
				boolean keepAlive = ValueUtil.booleanValue(info.getProperties()
						.get(ThriftServicePointInfo.PROPERTY_KEEPALIVE), false);
				if (!keepAlive) {
					if (in.getTransport() instanceof TNettyServerFramedTransport) {
						final TServicePointId pid = info.getId();

						TNettyServerFramedTransport ts = (TNettyServerFramedTransport) in
								.getTransport();
						sessionKey = pid.getSessionKey();
						if (ValueUtil.empty(sessionKey)) {
							sessionKey = "netty-" + ts.getChannel().getId();
							pid.setSessionKey(sessionKey);
						}
						ts.getChannel().getCloseFuture()
								.addListener(new ChannelFutureListener() {

									@Override
									public void operationComplete(
											ChannelFuture future)
											throws Exception {
										if (future.isDone()) {
											iface.unregisterThriftService(
													new AIStackNone<Boolean>(),
													pid);
										}
									}
								});
					}
				}
			}

			registerThriftService_result result = new registerThriftService_result();
			return iface.registerThriftService(new AIBaseStack<Boolean>(stack,
					result), info);
		}

		@Override
		protected boolean getResult(AIStack<TBase> stack, I iface,
				registerThriftService_args args) throws TException {
			return stack.success(null);
		}
	}

}
