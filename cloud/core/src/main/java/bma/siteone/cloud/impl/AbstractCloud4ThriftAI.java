package bma.siteone.cloud.impl;

import java.util.List;

import org.apache.thrift.TException;

import bma.common.langutil.ai.common.AIGroupAll;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.common.langutil.ai.stack.AIStackStep;
import bma.siteone.cloud.CloudApi;
import bma.siteone.cloud.CloudApp;
import bma.siteone.cloud.CloudEntry;
import bma.siteone.cloud.CloudException;
import bma.siteone.cloud.CloudNode;
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.CloudService;
import bma.siteone.cloud.CloudTrackable;
import bma.siteone.cloud.thrift.TAICloud.Iface;
import bma.siteone.cloud.thrift.TCloudEntry;
import bma.siteone.cloud.thrift.TCloudRequest;
import bma.siteone.cloud.thrift.TCloudResponse;

public abstract class AbstractCloud4ThriftAI implements Iface, CloudTrackable {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AbstractCloud4ThriftAI.class);

	public abstract CloudNode getCloudNode(String nodeId);

	public CloudNode sureCloudNode(String nodeId) {
		CloudNode r = getCloudNode(nodeId);
		if (r == null)
			throw new CloudException("cloudNode[" + nodeId + "] not exists");
		return r;
	}

	@Override
	public boolean getCloudNodeDesc(AIStack<String> stack, String nodeId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCloudNodeDesc({})", nodeId);
		}
		return sureCloudNode(nodeId).getDesc(stack);
	}

	@Override
	public boolean listCloudAppDesc(AIStack<List<String>> stack, String nodeId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listCloudAppDesc({})", nodeId);
		}
		return sureCloudNode(nodeId).listApp(
				new AIStackConvert<List<CloudApp>, List<String>>(stack) {
					@Override
					protected boolean convert(List<CloudApp> result) {
						AIGroupAll.Collection<String> g = new AIGroupAll.Collection<String>(
								delegate(), result.size());
						for (CloudApp app : result) {
							app.getDesc(g.newStack());
						}
						return g.commit();
					}
				});
	}

	@Override
	public boolean createCloudApp(AIStack<Boolean> stack, String nodeId,
			String appId, String appName) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("createCloudApp({}, {},{})", new Object[] { nodeId,
					appId, appName });
		}
		return sureCloudNode(nodeId).createApp(stack, appId, appName);
	}

	@Override
	public boolean closeCloudApp(AIStack<Boolean> stack, String nodeId,
			String appId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("closeCloudApp({},{})", nodeId, appId);
		}
		return sureCloudNode(nodeId).closeApp(stack, appId);
	}

	@Override
	public boolean listCloudServiceDesc(AIStack<List<String>> stack,
			String nodeId, final String appId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listCloudServiceDesc({},{})", nodeId, appId);
		}
		AIStackConvert<List<CloudService>, List<String>> rstack = new AIStackConvert<List<CloudService>, List<String>>(
				stack) {
			@Override
			protected boolean convert(List<CloudService> result) {
				AIGroupAll.Collection<String> g = new AIGroupAll.Collection<String>(
						delegate(), result.size());
				for (CloudService app : result) {
					app.getDesc(g.newStack());
				}
				return g.commit();
			}
		};
		return sureCloudNode(nodeId).getApp(
				new AIStackStep<CloudApp, List<CloudService>>(rstack) {

					@Override
					protected boolean next(CloudApp result) {
						if (result == null) {
							return super.failure(new CloudException("app["
									+ appId + "] not exists"));
						}
						return result.listService(delegate());
					}
				}, appId);
	}

	@Override
	public boolean listCloudApiDesc(AIStack<List<String>> stack, String nodeId,
			final String appId, final String serviceId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listCloudApiDesc({},{},{})", new Object[] { nodeId,
					appId, serviceId });
		}
		AIStackConvert<List<CloudApi>, List<String>> rstack = new AIStackConvert<List<CloudApi>, List<String>>(
				stack) {
			@Override
			protected boolean convert(List<CloudApi> result) {
				AIGroupAll.Collection<String> g = new AIGroupAll.Collection<String>(
						delegate(), result.size());
				for (CloudApi app : result) {
					app.getDesc(g.newStack());
				}
				return g.commit();
			}
		};
		AIStackStep<CloudService, List<CloudApi>> r2stack = new AIStackStep<CloudService, List<CloudApi>>(
				rstack) {

			@Override
			protected boolean next(CloudService result) {
				if (result == null) {
					return super.failure(new CloudException("service["
							+ serviceId + "] not exists"));
				}
				return result.listApi(delegate());
			}
		};
		return sureCloudNode(nodeId).getApp(
				new AIStackStep<CloudApp, CloudService>(r2stack) {

					@Override
					protected boolean next(CloudApp result) {
						if (result == null) {
							return super.failure(new CloudException("app["
									+ appId + "] not exists"));
						}
						return result.getService(delegate(), serviceId);
					}
				}, appId);
	}

	@Override
	public boolean getCloudAppDesc(AIStack<String> stack, String nodeId,
			String appId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCloudAppDesc({},{})", nodeId, appId);
		}
		AIStackStep<CloudApp, String> s1 = new AIStackStep<CloudApp, String>(
				stack) {

			@Override
			protected boolean next(CloudApp result) {
				if (result == null) {
					successForward(null);
				}
				return result.getDesc(delegate());
			}
		};
		return sureCloudNode(nodeId).getApp(s1, appId);
	}

	@Override
	public boolean getCloudServiceDesc(AIStack<String> stack, String nodeId,
			String appId, final String serviceId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCloudServiceDesc({},{},{})", new Object[] { nodeId,
					appId, serviceId });
		}
		AIStackStep<CloudService, String> s2 = new AIStackStep<CloudService, String>(
				stack) {

			@Override
			protected boolean next(CloudService result) {
				if (result == null) {
					successForward(null);
				}
				return result.getDesc(delegate());
			}
		};
		AIStackStep<CloudApp, CloudService> s1 = new AIStackStep<CloudApp, CloudService>(
				s2) {

			@Override
			protected boolean next(CloudApp result) {
				if (result == null) {
					successForward(null);
				}
				return result.getService(delegate(), serviceId);
			}
		};
		return sureCloudNode(nodeId).getApp(s1, appId);
	}

	@Override
	public boolean getCloudApiDesc(AIStack<String> stack, String nodeId,
			String appId, final String serviceId, final String apiId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getCloudServiceDesc({},{},{})", new Object[] { nodeId,
					appId, serviceId });
		}
		AIStackStep<CloudApi, String> s3 = new AIStackStep<CloudApi, String>(
				stack) {

			@Override
			protected boolean next(CloudApi result) {
				if (result == null) {
					successForward(null);
				}
				return result.getDesc(delegate());
			}
		};
		AIStackStep<CloudService, CloudApi> s2 = new AIStackStep<CloudService, CloudApi>(
				s3) {

			@Override
			protected boolean next(CloudService result) {
				if (result == null) {
					successForward(null);
				}
				return result.getApi(delegate(), apiId);
			}
		};
		AIStackStep<CloudApp, CloudService> s1 = new AIStackStep<CloudApp, CloudService>(
				s2) {

			@Override
			protected boolean next(CloudApp result) {
				if (result == null) {
					successForward(null);
				}
				return result.getService(delegate(), serviceId);
			}
		};
		return sureCloudNode(nodeId).getApp(s1, appId);
	}

	protected CloudEntry from(TCloudEntry e) {
		if (e == null)
			return null;
		BaseCloudEntry r = new BaseCloudEntry();
		r.setApiId(e.getApiId());
		r.setAppId(e.getAppId());
		r.setNodeId(e.getNodeId());
		r.setServiceId(e.getServiceId());
		return r;
	}

	@Override
	public boolean cloudCall(AIStack<TCloudResponse> stack, TCloudRequest req)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("cloudCall(req)", req);
		}
		if (req.getEntry() == null) {
			throw new NullPointerException("request entry is null");
		}

		final CloudRequest creq = new CloudRequest();
		creq.setCallback(from(req.getEntry()));
		creq.setContent(req.getContent());
		creq.setEntry(from(req.getEntry()));
		creq.setLogtrack(req.isLogtrack());

		AIStack<CloudResponse> st = new AIStackConvert<CloudResponse, TCloudResponse>(
				stack) {

			@Override
			protected boolean convert(CloudResponse result) {
				if (result == null) {
					return successConvert(null);
				}
				TCloudResponse rep = new TCloudResponse();
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
		return sureCloudNode(req.getEntry().getNodeId()).cloudCall(st, creq);
	}
}
