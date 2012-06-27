package bma.siteone.netty.thrift.hub;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.thrift.TException;

import bma.common.langutil.ai.common.AIEvent;
import bma.common.langutil.ai.common.AIFunction;
import bma.common.langutil.ai.compatible.SwitchAIException;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.common.langutil.core.ObjectFilter;
import bma.common.langutil.spring.ServerBooter;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.entry.AIThriftEntry;
import bma.common.thrift.servicehub.ThriftServiceHubClient;
import bma.common.thrift.servicehub.ThriftServiceHubListener;
import bma.common.thrift.servicehub.ThriftServicePointId;
import bma.common.thrift.servicehub.ThriftServicePointInfo;
import bma.common.thrift.servicehub.impl.ThriftServiceHub4ThriftAI;
import bma.common.thrift.servicehub.protocol.TAIServiceHub;
import bma.common.thrift.servicehub.protocol.TAIServiceHub.Client;
import bma.common.thrift.servicehub.protocol.TServicePointId;
import bma.common.thrift.servicehub.protocol.TServicePointInfo;

/**
 * Hub的Netty远程终端
 * 
 * @author guanzhong
 * 
 */
public class CopyOfNettyThriftServiceHubPeer implements ThriftServiceHubClient {

	protected AIThriftEntry entry;
	protected List<String> hubUrlList;

	protected Map<ThriftServicePointId, ThriftServicePointInfo> cacheInfoMap = new HashMap<ThriftServicePointId, ThriftServicePointInfo>();
	protected List<ThriftServiceHubListener> listenerList = new CopyOnWriteArrayList<ThriftServiceHubListener>();

	// runtime
	protected ThriftClient client;
	protected TAIServiceHub.Client api;
	protected boolean connecting = false;
	protected AIEvent<TAIServiceHub.Client> event = AIEvent
			.createManulResetEvent();

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

	public void eventNotify(String event, ThriftServicePointId id) {
		for (ThriftServiceHubListener lis : this.listenerList) {
			try {
				lis.eventNotify(event, id);
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

	protected boolean getHubClient(AIStack<TAIServiceHub.Client> stack) {
		boolean connect = false;
		synchronized (this) {
			if (this.client == null || this.client.getTransport().isOpen()) {
				this.client = null;
				if (!connecting) {
					connect = true;
					connecting = true;
				}
				event.resetEvent();
			}
		}

		if (this.api != null) {
			return stack.success(api);
		}

		if (connect) {
			AIStackConvert<ThriftClient, Client> st = new AIStackConvert<ThriftClient, TAIServiceHub.Client>(
					stack) {

				@Override
				protected boolean convert(ThriftClient result) {
					synchronized (this) {
						client = result;
						api = new Client(client.getProtocol());
						connecting = false;
					}
					boolean r = successConvert(api);
					event.checkEvent();
					return r;
				}

				@Override
				public boolean failure(Throwable t) {
					synchronized (this) {
						connecting = false;
					}
					boolean r = super.failure(t);
					event.allFailure(t);
					return r;
				}
			};

			try {
				String url = hubUrlList.get(0);
				return entry.createClient(st, url);
			} catch (TException e) {
				return st.failure(e);
			}
		} else {
			return event.waitEvent(new AIFunction<TAIServiceHub.Client>() {
				@Override
				public boolean apply(AIStack<Client> stack) {
					return getHubClient(stack);
				}
			}, stack);
		}

	}

	public void close() {
		if (this.client != null) {
			this.client.close();
		}
	}

	public boolean register(AIStack<Boolean> stack,
			final ThriftServicePointInfo info) throws SwitchAIException {
		return getHubClient(new AIStackConvert<TAIServiceHub.Client, Boolean>(
				stack) {
			@Override
			protected boolean convert(Client result) {
				TServicePointInfo o = ThriftServiceHub4ThriftAI.from(info);
				try {
					return result.registerThriftService(delegate(), o);
				} catch (TException e) {
					return failure(e);
				}
			}
		});
	}

	public boolean active(AIStack<Boolean> stack,
			final ThriftServicePointId id, final ThriftServicePointInfo newInfo)
			throws SwitchAIException {
		return getHubClient(new AIStackConvert<TAIServiceHub.Client, Boolean>(
				stack) {
			@Override
			protected boolean convert(Client result) {
				TServicePointId tid = ThriftServiceHub4ThriftAI.from(id);
				TServicePointInfo tinfo = ThriftServiceHub4ThriftAI
						.from(newInfo);
				try {
					return result.activeThriftService(delegate(), tid, tinfo);
				} catch (TException e) {
					return failure(e);
				}
			}
		});
	}

	public boolean unregister(AIStack<Boolean> stack,
			final ThriftServicePointId id) throws SwitchAIException {
		return getHubClient(new AIStackConvert<TAIServiceHub.Client, Boolean>(
				stack) {
			@Override
			protected boolean convert(Client result) {
				TServicePointId tid = ThriftServiceHub4ThriftAI.from(id);
				try {
					return result.unregisterThriftService(delegate(), tid);
				} catch (TException e) {
					return failure(e);
				}
			}
		});
	}

	@Override
	public boolean getService(AIStack<ThriftServicePointInfo> stack,
			final ThriftServicePointId id) throws SwitchAIException {
		return getHubClient(new AIStackConvert<TAIServiceHub.Client, ThriftServicePointInfo>(
				stack) {
			@Override
			protected boolean convert(Client result) {
				TServicePointId tid = ThriftServiceHub4ThriftAI.from(id);
				try {
					return result
							.getThriftService(
									new AIStackConvert<TServicePointInfo, ThriftServicePointInfo>(
											delegate()) {
										@Override
										protected boolean convert(
												TServicePointInfo result) {
											return successConvert(ThriftServiceHub4ThriftAI
													.to(result));
										}
									}, tid);
				} catch (TException e) {
					return failure(e);
				}
			}
		});

	}

	@Override
	public <PTYPE> boolean queryService(
			AIStack<List<ThriftServicePointInfo>> stack,
			ObjectFilter<ThriftServicePointInfo, PTYPE> filter,
			PTYPE filterParam) throws SwitchAIException {
		return getHubClient(new AIStackConvert<TAIServiceHub.Client, List<ThriftServicePointInfo>>(
				stack) {
			@Override
			protected boolean convert(Client result) {
				try {
					return result
							.listThriftServices(new AIStackConvert<List<TServicePointInfo>, List<ThriftServicePointInfo>>(
									delegate()) {
								@Override
								protected boolean convert(
										List<TServicePointInfo> result) {
									List<ThriftServicePointInfo> r = new LinkedList<ThriftServicePointInfo>();
									if (result != null) {
										for (TServicePointInfo info : result) {
											r.add(ThriftServiceHub4ThriftAI
													.to(info));
										}
									}
									return successConvert(r);
								}
							});
				} catch (TException e) {
					return failure(e);
				}
			}
		});
	}

}
