package bma.siteone.netty.thrift.hub;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import bma.common.langutil.ai.common.AIEvent;
import bma.common.langutil.ai.common.AIFunction;
import bma.common.langutil.ai.compatible.SwitchAIException;
import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackAbstract;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.common.langutil.ai.stack.AIStackNone;
import bma.common.langutil.ai.stack.AIStackStep;
import bma.common.langutil.ai.stack.AIStackWrap;
import bma.common.langutil.concurrent.TimerManager;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.ObjectFilter;
import bma.common.langutil.spring.ServerBooter;
import bma.common.netty.SupportedNettyChannel;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.entry.AIThriftEntry;
import bma.common.thrift.servicehub.ThriftServiceBean;
import bma.common.thrift.servicehub.ThriftServiceHubClient;
import bma.common.thrift.servicehub.ThriftServiceHubListener;
import bma.common.thrift.servicehub.ThriftServiceNode;
import bma.common.thrift.servicehub.ThriftServicePointId;
import bma.common.thrift.servicehub.ThriftServicePointInfo;
import bma.common.thrift.servicehub.impl.ThriftServiceHub4ThriftAI;
import bma.common.thrift.servicehub.protocol.TAIServiceHub4Peer;
import bma.common.thrift.servicehub.protocol.TAIServiceHubListener.Iface;
import bma.common.thrift.servicehub.protocol.TServicePointId;
import bma.common.thrift.servicehub.protocol.TServicePointInfo;

/**
 * Hub的Netty远程终端
 * 
 * @author guanzhong
 * 
 */
public class NettyThriftServiceHubPeer implements ThriftServiceHubClient,
		ServerBooter {

	public static class Listener implements Iface {

		private NettyThriftServiceHubPeer peer;

		public Listener(NettyThriftServiceHubPeer peer) {
			super();
			this.peer = peer;
		}

		@Override
		public boolean serviceHubEvent(AIStack<Boolean> stack, boolean changed)
				throws TException {
			return peer.serviceHubEvent(stack, changed);
		}

	}

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NettyThriftServiceHubPeer.class);

	protected TimerManager timer;
	protected long heartbeatTime = 0;
	protected long reconnectTime = 3 * 1000;

	protected AIThriftEntry entry;
	protected List<String> hubUrlList;
	protected String listenerUrl;

	protected List<ThriftServiceNode> registerNodeList;

	// runtime
	protected ThriftClient client;
	protected TAIServiceHub4Peer.Client api;
	protected boolean connecting = false;
	protected boolean logRetry = false;
	protected AIEvent<Boolean> event = AIEvent.createManulResetEvent();

	// cache
	protected Map<ThriftServicePointId, ThriftServicePointInfo> infoMap = new ConcurrentHashMap<ThriftServicePointId, ThriftServicePointInfo>();

	public List<ThriftServiceNode> getRegisterNodeList() {
		return registerNodeList;
	}

	public void setRegisterNodeList(List<ThriftServiceNode> nodeList) {
		this.registerNodeList = nodeList;
	}

	public void setRegisterNode(ThriftServiceNode node) {
		if (this.registerNodeList == null)
			this.registerNodeList = new ArrayList();
		this.registerNodeList.add(node);
	}

	public String getListenerUrl() {
		return listenerUrl;
	}

	public void setListenerUrl(String listenerEntry) {
		this.listenerUrl = listenerEntry;
	}

	public List<String> getHubUrlList() {
		return hubUrlList;
	}

	public void setHubUrlList(List<String> hubUrlList) {
		this.hubUrlList = hubUrlList;
	}

	public void setHubUrl(String url) {
		if (this.hubUrlList == null)
			this.hubUrlList = new LinkedList<String>();
		this.hubUrlList.add(url);
	}

	public AIThriftEntry getEntry() {
		return entry;
	}

	public void setEntry(AIThriftEntry entry) {
		this.entry = entry;
	}

	public TimerManager getTimer() {
		if (timer != null)
			return timer;
		return AIExecutor.getTimerManager();
	}

	public void setTimer(TimerManager timer) {
		this.timer = timer;
	}

	public long getHeartbeatTime() {
		return heartbeatTime;
	}

	public long queryHeartbeatTime() {
		if (heartbeatTime <= 0)
			return reconnectTime;
		return heartbeatTime;
	}

	public void setHeartbeatTime(long heartBeartTime) {
		this.heartbeatTime = heartBeartTime;
	}

	public void setHeartbeat(String p) {
		setHeartbeatTime(DateTimeUtil.parsePeriodValue(p, -1));
	}

	public long getReconnectTime() {
		return reconnectTime;
	}

	public void setReconnectTime(long reconnectTime) {
		this.reconnectTime = reconnectTime;
	}

	public void setReconnect(String p) {
		setReconnectTime(DateTimeUtil.parsePeriodValue(p, reconnectTime));
	}

	protected boolean open(AIStack<Boolean> stack, boolean skipOnBreak,
			boolean skipWaitConnect) {
		boolean connect = false;
		synchronized (this) {
			if (this.client == null || !this.client.isOpen()) {
				this.client = null;
				this.api = null;
				if (!connecting) {
					connect = true;
					connecting = true;
				}
				event.resetEvent();
			}
		}

		if (this.client != null) {
			return stack.success(true);
		}
		if (skipOnBreak) {
			return stack.success(false);
		}

		if (connect) {
			AIStackStep<ThriftClient, Boolean> st = new AIStackStep<ThriftClient, Boolean>(
					stack) {

				@Override
				protected boolean next(ThriftClient result) {
					synchronized (this) {
						client = result;
						api = new TAIServiceHub4Peer.Client(
								client.getProtocol());
						connecting = false;
						logRetry = false;
					}

					TTransport ts = result.getTransport();
					if (ts instanceof SupportedNettyChannel) {
						Channel ch = ((SupportedNettyChannel) ts).getChannel();
						ch.getCloseFuture().addListener(
								new ChannelFutureListener() {

									@Override
									public void operationComplete(
											ChannelFuture future)
											throws Exception {
										retry();
									}
								});
					}

					// register all serviceNode
					// register listener
					List<TServicePointInfo> infoList = new ArrayList<TServicePointInfo>(
							0);
					if (registerNodeList != null) {
						for (ThriftServiceNode node : registerNodeList) {
							for (ThriftServiceBean bean : node.getServices()) {
								TServicePointId id = new TServicePointId();
								id.setNodeName(node.getName());
								id.setServiceName(bean.getModule());

								TServicePointInfo info = new TServicePointInfo();
								info.setId(id);
								info.setProperties(bean.getServiceProperties());
								info.setServiceEntry(bean.getEntry());

								infoList.add(info);
							}
						}
					}
					try {
						api.registerThriftServicePeer(
								new AIStackNone<Boolean>(), infoList,
								listenerUrl);
					} catch (TException e) {
						return failure(e);
					}

					// load all service from Hub
					return reloadHubServices(new AIStackStep<Boolean, Boolean>(
							delegate()) {
						@Override
						public boolean next(Boolean result) {
							// do heartbeat
							doHeartbeat();

							boolean r = successForward(true);
							event.checkEvent();
							return r;
						}
					});

				}

				@Override
				public boolean failure(Throwable t) {
					synchronized (this) {
						connecting = false;
					}
					try {
						boolean r = super.failure(t);
						event.allFailure(t);
						return r;
					} finally {
						retry();
					}
				}
			};

			try {
				String url = hubUrlList.get(0);
				return entry.createClient(st, url);
			} catch (TException e) {
				return st.failure(e);
			}
		} else {
			if (skipWaitConnect) {
				return stack.success(null);
			}
			return event.waitEvent(new AIFunction<Boolean>() {
				@Override
				public boolean apply(AIStack<Boolean> stack) {
					return stack.success(true);
				}
			}, stack);
		}

	}

	public void retry() {

		if (!logRetry) {
			if (log.isInfoEnabled()) {
				log.info("wait {} to retry",
						DateTimeUtil.formatPeriod(reconnectTime));
			}
			logRetry = true;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("wait {} to retry",
						DateTimeUtil.formatPeriod(reconnectTime));
			}
		}

		getTimer().postTimerTask(TimerManager.delay(new Runnable() {

			@Override
			public void run() {
				open(new AIStackNone<Boolean>(), false, true);
			}
		}, reconnectTime));
	}

	public void doHeartbeat() {

		if (log.isDebugEnabled()) {
			log.debug("wait {} to heartbeat",
					DateTimeUtil.formatPeriod(queryHeartbeatTime()));
		}

		getTimer().postTimerTask(TimerManager.delay(new Runnable() {

			@Override
			public void run() {
				open(new AIStackAbstract<Boolean>() {

					@Override
					public boolean success(Boolean result) {
						if (result != null && result.booleanValue()) {
							try {
								return api
										.activeThriftServicePeer(new AIStackWrap<Boolean>(
												this) {
											public boolean success(
													Boolean result) {
												doHeartbeat();
												return true;
											}
										});
							} catch (TException e) {
								return failure(e);
							}
						}
						return true;
					}

					@Override
					public boolean failure(Throwable t) {
						if (log.isDebugEnabled()) {
							log.debug("heartbeat fail", t);
						}
						return true;
					}

					@Override
					public AIStack<?> getParent() {
						return null;
					}

				}, false, false);
			}
		}, queryHeartbeatTime()));
	}

	public boolean serviceHubEvent(AIStack<Boolean> stack, boolean changed)
			throws TException {
		if (log.isInfoEnabled()) {
			log.info("receive serviceHubEvent({})", changed);
		}
		return reloadHubServices(stack);
	}

	public void start() {
		open(new AIStackNone<Boolean>(), false, true);
	}

	public void close() {
		if (this.client != null) {
			this.client.close();
			this.client = null;
			this.api = null;
		}
	}

	@Override
	public void startServer() {
		start();
	}

	@Override
	public void stopServer() {
		close();
	}

	protected static boolean isDone(Boolean r) {
		if (r == null)
			return false;
		return r.booleanValue();
	}

	protected List<ThriftServiceHubListener> listenerList = new CopyOnWriteArrayList<ThriftServiceHubListener>();

	@Override
	public boolean addListener(AIStack<Boolean> stack,
			ThriftServiceHubListener lis) {
		if (!listenerList.contains(lis)) {
			listenerList.add(lis);
			return stack.success(true);
		} else {
			return stack.success(false);
		}
	}

	public void eventNotify(boolean change) {
		for (ThriftServiceHubListener lis : this.listenerList) {
			try {
				lis.eventNotify(change);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public boolean removeListener(AIStack<Boolean> stack,
			final ThriftServiceHubListener lis) {
		boolean r = listenerList.remove(lis);
		return stack.success(r);
	}

	@Override
	public boolean getService(AIStack<ThriftServicePointInfo> stack,
			ThriftServicePointId id) throws SwitchAIException {
		return queryService(
				new AIStackConvert<List<ThriftServicePointInfo>, ThriftServicePointInfo>(
						stack) {
					@Override
					protected boolean convert(
							List<ThriftServicePointInfo> result) {
						if (result == null || result.isEmpty()) {
							return successConvert(null);
						}
						return successConvert(result.get(0));
					}
				},
				new ObjectFilter<ThriftServicePointInfo, ThriftServicePointId>() {
					@Override
					public boolean accept(ThriftServicePointInfo obj,
							ThriftServicePointId id) {
						return obj.equals(id);
					}
				}, id);
	}

	@Override
	public <PTYPE> boolean queryService(
			AIStack<List<ThriftServicePointInfo>> stack,
			ObjectFilter<ThriftServicePointInfo, PTYPE> filter,
			PTYPE filterParam) throws SwitchAIException {
		List<ThriftServicePointInfo> r = new ArrayList<ThriftServicePointInfo>();
		for (Map.Entry<ThriftServicePointId, ThriftServicePointInfo> entry : this.infoMap
				.entrySet()) {
			ThriftServicePointInfo info = entry.getValue();
			if (filter != null) {
				if (filter.accept(info, filterParam)) {
					r.add(info);
				}
			} else {
				r.add(info);
			}
		}
		return stack.success(r);
	}

	protected boolean reloadHubServices(AIStack<Boolean> stack) {
		AIStack<List<ThriftServicePointInfo>> loadStack = new AIStackStep<List<ThriftServicePointInfo>, Boolean>(
				stack) {
			@Override
			protected boolean next(List<ThriftServicePointInfo> result) {

				if (log.isInfoEnabled()) {
					log.info("reloadHubServices({})", result == null ? 0
							: result.size());
				}

				if (result != null) {
					Set<ThriftServicePointId> keys = infoMap.keySet();
					for (ThriftServicePointInfo info : result) {
						keys.remove(info);
						ThriftServicePointId id = info.cloneId();
						infoMap.put(id, info);
					}
					for (ThriftServicePointId delId : keys) {
						infoMap.remove(delId);
					}
				}
				try {
					return super.successForward(true);
				} finally {
					eventNotify(true);
				}
			}

			@Override
			public boolean failure(Throwable t) {
				if (log.isWarnEnabled()) {
					log.warn("reloadHubServices fail: " + t);
				}
				return super.failure(t);
			}
		};
		return loadHubServices(loadStack);
	}

	protected boolean loadHubServices(
			AIStack<List<ThriftServicePointInfo>> stack) {
		return open(new AIStackStep<Boolean, List<ThriftServicePointInfo>>(
				stack) {

			@Override
			protected boolean next(Boolean result) {
				if (!isDone(result)) {
					return successForward(null);
				}

				try {
					return api.listThriftServicesPeer(new AIStackConvert<List<TServicePointInfo>, List<ThriftServicePointInfo>>(
							delegate()) {
						@Override
						protected boolean convert(List<TServicePointInfo> result) {
							List<ThriftServicePointInfo> r = new LinkedList<ThriftServicePointInfo>();
							if (result != null) {
								for (TServicePointInfo info : result) {
									r.add(ThriftServiceHub4ThriftAI.to(info));
								}
							}
							return successConvert(r);
						}
					});
				} catch (TException e) {
					return failure(e);
				}
			}
		}, false, false);
	}

}
