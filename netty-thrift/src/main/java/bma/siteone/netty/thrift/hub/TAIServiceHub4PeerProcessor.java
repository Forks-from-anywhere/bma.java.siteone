package bma.siteone.netty.thrift.hub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackNone;
import bma.common.langutil.core.ValueUtil;
import bma.common.netty.SupportedNettyChannel;
import bma.common.thrift.ai.AIBaseStack;
import bma.common.thrift.ai.TAIBaseProcessor;
import bma.common.thrift.ai.TAIProcessFunction;
import bma.common.thrift.servicehub.protocol.TAIServiceHub;
import bma.common.thrift.servicehub.protocol.TAIServiceHub4Peer.Iface;
import bma.common.thrift.servicehub.protocol.TServiceHub.registerThriftService_result;
import bma.common.thrift.servicehub.protocol.TServiceHub4Peer.activeThriftServicePeer_args;
import bma.common.thrift.servicehub.protocol.TServiceHub4Peer.activeThriftServicePeer_result;
import bma.common.thrift.servicehub.protocol.TServiceHub4Peer.listThriftServicesPeer_args;
import bma.common.thrift.servicehub.protocol.TServiceHub4Peer.listThriftServicesPeer_result;
import bma.common.thrift.servicehub.protocol.TServiceHub4Peer.registerThriftServicePeer_args;
import bma.common.thrift.servicehub.protocol.TServicePointId;
import bma.common.thrift.servicehub.protocol.TServicePointInfo;

public class TAIServiceHub4PeerProcessor<IFACE extends Iface> extends
		TAIBaseProcessor<IFACE> {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(TAIServiceHub4PeerProcessor.class);

	private static class PeerItem {
		public String key;
		public List<TServicePointId> serviceIdList;
		public String listenerEntry;
	}

	protected TAIServiceHub.Iface hubFace;

	// runtime
	protected Map<Integer, PeerItem> peers = new ConcurrentHashMap<Integer, TAIServiceHub4PeerProcessor.PeerItem>();

	public TAIServiceHub4PeerProcessor(TAIServiceHub.Iface iface) {
		super(
				null,
				new HashMap<String, TAIProcessFunction<IFACE, ? extends TBase>>());
		hubFace = iface;
	}

	@Override
	public TAIProcessFunction<IFACE, ? extends TBase> queryProcessFunction(
			String name) {
		if (processMap.isEmpty()) {
			buildProcessMap();
		}
		return super.queryProcessFunction(name);
	}

	private <I extends Iface> void buildProcessMap() {
		processMap.put("registerThriftServicePeer",
				new registerThriftServicePeer());
		processMap
				.put("activeThriftServicePeer", new activeThriftServicePeer());
		processMap.put("listThriftServicesPeer", new listThriftServicesPeer());
	}

	private class registerThriftServicePeer<I extends Iface> extends
			TAIProcessFunction<I, registerThriftServicePeer_args> {
		public registerThriftServicePeer() {
			super("registerThriftServicePeer");
		}

		protected registerThriftServicePeer_args getEmptyArgsInstance() {
			return new registerThriftServicePeer_args();
		}

		protected boolean getResult(AIStack<TBase> stack, I iface,
				registerThriftServicePeer_args args) throws TException {
			throw new IllegalAccessError("can't reach here");
		}

		protected boolean getResult(TProtocol in, TProtocol out,
				AIStack<TBase> stack, I iface,
				registerThriftServicePeer_args args) throws TException {

			Channel ch = null;
			if (in.getTransport() instanceof SupportedNettyChannel) {
				ch = ((SupportedNettyChannel) in.getTransport()).getChannel();
			}
			if (ch == null) {
				throw new IllegalArgumentException(
						"transport not SupportedNettyChannel");
			}

			List<TServicePointInfo> infoList = args.getInfoList();
			String listenEntry = args.getListenerEntry();

			if (log.isDebugEnabled()) {
				log.debug("HubPeer register '{}'", listenEntry);
			}

			final PeerItem peer = new PeerItem();
			peer.key = System.identityHashCode(this) + "-" + ch.getId();
			List<TServicePointId> idlist = new ArrayList<TServicePointId>(
					infoList.size());
			for (TServicePointInfo info : infoList) {
				info.getId().setSessionKey(peer.key);
				idlist.add(info.getId().deepCopy());
			}
			peer.serviceIdList = idlist;
			peer.listenerEntry = listenEntry;

			peers.put(ch.getId(), peer);

			ch.getCloseFuture().addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					if (log.isDebugEnabled()) {
						log.debug("HubPeer break '{}'", peer.listenerEntry);
					}

					peers.remove(future.getChannel().getId());
					if (ValueUtil.notEmpty(peer.listenerEntry)) {
						hubFace.removeServiceHubListener(
								new AIStackNone<Boolean>(), peer.key
										+ "-listener", peer.listenerEntry);
					}
					for (TServicePointId id : peer.serviceIdList) {
						hubFace.unregisterThriftService(
								new AIStackNone<Boolean>(), id);
					}
				}
			});

			for (TServicePointInfo info : infoList) {
				hubFace.registerThriftService(new AIStackNone<Boolean>(), info);
			}
			if (ValueUtil.notEmpty(listenEntry)) {
				hubFace.addServiceHubListener(new AIStackNone<Boolean>(),
						peer.key + "-listener", listenEntry);
			}

			registerThriftService_result result = new registerThriftService_result();
			result.setSuccess(true);
			return stack.success(result);
		}

	}

	private class activeThriftServicePeer<I extends Iface> extends
			TAIProcessFunction<I, activeThriftServicePeer_args> {
		public activeThriftServicePeer() {
			super("activeThriftServicePeer");
		}

		protected activeThriftServicePeer_args getEmptyArgsInstance() {
			return new activeThriftServicePeer_args();
		}

		protected boolean getResult(AIStack<TBase> stack, I iface,
				activeThriftServicePeer_args args) throws TException {
			throw new IllegalAccessError("can't reach here");
		}

		protected boolean getResult(TProtocol in, TProtocol out,
				AIStack<TBase> stack, I iface, activeThriftServicePeer_args args)
				throws TException {

			Channel ch = null;
			if (in.getTransport() instanceof SupportedNettyChannel) {
				ch = ((SupportedNettyChannel) in.getTransport()).getChannel();
			}
			if (ch == null) {
				throw new IllegalArgumentException(
						"transport not SupportedNettyChannel");
			}
			PeerItem peer = peers.get(ch.getId());
			activeThriftServicePeer_result result = new activeThriftServicePeer_result();

			if (peer == null) {
				result.setSuccess(false);
				return stack.success(result);
			}

			if (log.isDebugEnabled()) {
				log.debug("HubPeer active '{}'", peer.listenerEntry);
			}

			for (TServicePointId id : peer.serviceIdList) {
				hubFace.activeThriftService(new AIStackNone<Boolean>(), id,
						null);
			}

			result.setSuccess(true);
			return stack.success(result);
		}
	}

	private class listThriftServicesPeer<I extends Iface> extends
			TAIProcessFunction<I, listThriftServicesPeer_args> {
		public listThriftServicesPeer() {
			super("listThriftServicesPeer");
		}

		protected listThriftServicesPeer_args getEmptyArgsInstance() {
			return new listThriftServicesPeer_args();
		}

		@Override
		protected boolean getResult(AIStack<TBase> stack, I iface,
				listThriftServicesPeer_args args) throws TException {
			listThriftServicesPeer_result result = new listThriftServicesPeer_result();
			return hubFace
					.listThriftServices(new AIBaseStack<List<TServicePointInfo>>(
							stack, result));
		}
	}

}