package bma.siteone.clound.impl;

import java.util.List;

import org.apache.thrift.TException;

import bma.common.langutil.ai.common.AIGroupAll;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.common.langutil.ai.stack.AIStackStep;
import bma.siteone.clound.CloundApi;
import bma.siteone.clound.CloundApp;
import bma.siteone.clound.CloundEntry;
import bma.siteone.clound.CloundException;
import bma.siteone.clound.CloundNode;
import bma.siteone.clound.CloundRequest;
import bma.siteone.clound.CloundResponse;
import bma.siteone.clound.CloundService;
import bma.siteone.clound.CloundTrackable;
import bma.siteone.clound.thrift.TAIClound.Iface;
import bma.siteone.clound.thrift.TCloundEntry;
import bma.siteone.clound.thrift.TCloundRequest;
import bma.siteone.clound.thrift.TCloundResponse;

public abstract class AbstractClound4ThriftAI implements Iface, CloundTrackable {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AbstractClound4ThriftAI.class);

	public abstract CloundNode getCloundNode(String nodeId);

	public CloundNode sureCloundNode(String nodeId) {
		CloundNode r = getCloundNode(nodeId);
		if (r == null)
			throw new CloundException("cloundNode[" + nodeId + "] not exists");
		return r;
	}

	@Override
	public boolean getCloundNodeDesc(AIStack<String> stack, String nodeId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCloundNodeDesc({})", nodeId);
		}
		return sureCloundNode(nodeId).getDesc(stack);
	}

	@Override
	public boolean listCloundAppDesc(AIStack<List<String>> stack, String nodeId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listCloundAppDesc({})", nodeId);
		}
		return sureCloundNode(nodeId).listApp(
				new AIStackConvert<List<CloundApp>, List<String>>(stack) {
					@Override
					protected boolean convert(List<CloundApp> result) {
						AIGroupAll.Collection<String> g = new AIGroupAll.Collection<String>(
								delegate(), result.size());
						for (CloundApp app : result) {
							app.getDesc(g.newStack());
						}
						return g.commit();
					}
				});
	}

	@Override
	public boolean createCloundApp(AIStack<Boolean> stack, String nodeId,
			String appId, String appName) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("createCloundApp({}, {},{})", new Object[] { nodeId,
					appId, appName });
		}
		return sureCloundNode(nodeId).createApp(stack, appId, appName);
	}

	@Override
	public boolean closeCloundApp(AIStack<Boolean> stack, String nodeId,
			String appId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("closeCloundApp({},{})", nodeId, appId);
		}
		return sureCloundNode(nodeId).closeApp(stack, appId);
	}

	@Override
	public boolean listCloundServiceDesc(AIStack<List<String>> stack,
			String nodeId, final String appId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listCloundServiceDesc({},{})", nodeId, appId);
		}
		AIStackConvert<List<CloundService>, List<String>> rstack = new AIStackConvert<List<CloundService>, List<String>>(
				stack) {
			@Override
			protected boolean convert(List<CloundService> result) {
				AIGroupAll.Collection<String> g = new AIGroupAll.Collection<String>(
						delegate(), result.size());
				for (CloundService app : result) {
					app.getDesc(g.newStack());
				}
				return g.commit();
			}
		};
		return sureCloundNode(nodeId).getApp(
				new AIStackStep<CloundApp, List<CloundService>>(rstack) {

					@Override
					protected boolean next(CloundApp result) {
						if (result == null) {
							return super.failure(new CloundException("app["
									+ appId + "] not exists"));
						}
						return result.listService(delegate());
					}
				}, appId);
	}

	@Override
	public boolean listCloundApiDesc(AIStack<List<String>> stack,
			String nodeId, final String appId, final String serviceId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listCloundApiDesc({},{},{})", new Object[] { nodeId,
					appId, serviceId });
		}
		AIStackConvert<List<CloundApi>, List<String>> rstack = new AIStackConvert<List<CloundApi>, List<String>>(
				stack) {
			@Override
			protected boolean convert(List<CloundApi> result) {
				AIGroupAll.Collection<String> g = new AIGroupAll.Collection<String>(
						delegate(), result.size());
				for (CloundApi app : result) {
					app.getDesc(g.newStack());
				}
				return g.commit();
			}
		};
		AIStackStep<CloundService, List<CloundApi>> r2stack = new AIStackStep<CloundService, List<CloundApi>>(
				rstack) {

			@Override
			protected boolean next(CloundService result) {
				if (result == null) {
					return super.failure(new CloundException("service["
							+ serviceId + "] not exists"));
				}
				return result.listApi(delegate());
			}
		};
		return sureCloundNode(nodeId).getApp(
				new AIStackStep<CloundApp, CloundService>(r2stack) {

					@Override
					protected boolean next(CloundApp result) {
						if (result == null) {
							return super.failure(new CloundException("app["
									+ appId + "] not exists"));
						}
						return result.getService(delegate(), serviceId);
					}
				}, appId);
	}

	@Override
	public boolean getCloundAppDesc(AIStack<String> stack, String nodeId,
			String appId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCloundAppDesc({},{})", nodeId, appId);
		}
		AIStackStep<CloundApp, String> s1 = new AIStackStep<CloundApp, String>(
				stack) {

			@Override
			protected boolean next(CloundApp result) {
				if (result == null) {
					successForward(null);
				}
				return result.getDesc(delegate());
			}
		};
		return sureCloundNode(nodeId).getApp(s1, appId);
	}

	@Override
	public boolean getCloundServiceDesc(AIStack<String> stack, String nodeId,
			String appId, final String serviceId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCloundServiceDesc({},{},{})", new Object[] { nodeId,
					appId, serviceId });
		}
		AIStackStep<CloundService, String> s2 = new AIStackStep<CloundService, String>(
				stack) {

			@Override
			protected boolean next(CloundService result) {
				if (result == null) {
					successForward(null);
				}
				return result.getDesc(delegate());
			}
		};
		AIStackStep<CloundApp, CloundService> s1 = new AIStackStep<CloundApp, CloundService>(
				s2) {

			@Override
			protected boolean next(CloundApp result) {
				if (result == null) {
					successForward(null);
				}
				return result.getService(delegate(), serviceId);
			}
		};
		return sureCloundNode(nodeId).getApp(s1, appId);
	}

	@Override
	public boolean getCloundApiDesc(AIStack<String> stack, String nodeId,
			String appId, final String serviceId, final String apiId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCloundServiceDesc({},{},{})", new Object[] { nodeId,
					appId, serviceId });
		}
		AIStackStep<CloundApi, String> s3 = new AIStackStep<CloundApi, String>(
				stack) {

			@Override
			protected boolean next(CloundApi result) {
				if (result == null) {
					successForward(null);
				}
				return result.getDesc(delegate());
			}
		};
		AIStackStep<CloundService, CloundApi> s2 = new AIStackStep<CloundService, CloundApi>(
				s3) {

			@Override
			protected boolean next(CloundService result) {
				if (result == null) {
					successForward(null);
				}
				return result.getApi(delegate(), apiId);
			}
		};
		AIStackStep<CloundApp, CloundService> s1 = new AIStackStep<CloundApp, CloundService>(
				s2) {

			@Override
			protected boolean next(CloundApp result) {
				if (result == null) {
					successForward(null);
				}
				return result.getService(delegate(), serviceId);
			}
		};
		return sureCloundNode(nodeId).getApp(s1, appId);
	}

	protected CloundEntry from(TCloundEntry e) {
		if (e == null)
			return null;
		BaseCloundEntry r = new BaseCloundEntry();
		r.setApiId(e.getApiId());
		r.setAppId(e.getAppId());
		r.setNodeId(e.getNodeId());
		r.setServiceId(e.getServiceId());
		return r;
	}

	@Override
	public boolean cloundCall(AIStack<TCloundResponse> stack, TCloundRequest req)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("cloundCall(req)", req);
		}
		if (req.getEntry() == null) {
			throw new NullPointerException("request entry is null");
		}

		final CloundRequest creq = new CloundRequest();
		creq.setCallback(from(req.getEntry()));
		creq.setContent(req.getContent());
		creq.setEntry(from(req.getEntry()));
		creq.setLogtrack(req.isLogtrack());

		AIStack<CloundResponse> st = new AIStackConvert<CloundResponse, TCloundResponse>(
				stack) {

			@Override
			protected boolean convert(CloundResponse result) {
				if (result == null) {
					return successConvert(null);
				}
				TCloundResponse rep = new TCloundResponse();
				rep.setContent(result.getContent());
				rep.setType(result.getType());
				List<String> tlist = result.getLogTrack();
				if (tlist != null) {
					for (String elem : tlist) {
						rep.addToLogtrack(elem);
					}
				}
				if (creq.isLogtrack()) {
					rep.addToLogtrack(getTrackString());
				}
				return successConvert(rep);
			}

		};
		return sureCloundNode(req.getEntry().getNodeId()).cloundCall(st, creq);
	}
}
