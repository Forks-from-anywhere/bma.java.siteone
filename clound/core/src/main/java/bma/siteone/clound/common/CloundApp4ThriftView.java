package bma.siteone.clound.common;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.thrift.TProcessorWrapInterface;
import bma.common.thrift.servicehub.ThriftServiceBean;
import bma.common.thrift.servicehub.ThriftServiceNode;
import bma.siteone.clound.CloundApi;
import bma.siteone.clound.CloundRequest;
import bma.siteone.clound.CloundResponse;
import bma.siteone.clound.CloundService;
import bma.siteone.clound.local.LocalCloundApi;
import bma.siteone.clound.local.LocalCloundApp;
import bma.siteone.clound.local.LocalCloundService;

public class CloundApp4ThriftView extends LocalCloundApp implements
		ApplicationContextAware {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CloundApp4ThriftView.class);

	protected ThriftServiceNode thriftServiceNode;
	protected String thriftNodeBean;
	protected ApplicationContext context;

	public ThriftServiceNode getThriftServiceNode() {
		return thriftServiceNode;
	}

	public void setThriftServiceNode(ThriftServiceNode thriftServiceNode) {
		this.thriftServiceNode = thriftServiceNode;
	}

	public String getThriftNodeBean() {
		return thriftNodeBean;
	}

	public void setThriftNodeBean(String thriftNodeBean) {
		this.thriftNodeBean = thriftNodeBean;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public Map<String, CloundService> getServiceMap() {
		if (this.serviceMap == null) {
			if (this.thriftServiceNode == null) {
				this.thriftServiceNode = this.context.getBean(
						this.thriftNodeBean, ThriftServiceNode.class);
			}
			List<ThriftServiceBean> slist = this.thriftServiceNode
					.getServices();
			List<CloundService> cslist = new ArrayList<CloundService>(
					slist.size());
			for (ThriftServiceBean bean : slist) {
				String serviceId = bean.getModule();
				List<CloundApi> apiList = new ArrayList<CloundApi>();

				// copy from TProcessRouter:addRouter				
				TProcessor p = bean.getProcessor();
				TProcessor cp = p;
				try {
					if (p instanceof TProcessorWrapInterface) {
						TProcessorWrapInterface wi = (TProcessorWrapInterface) p;
						cp = wi.getInnerProcessor();
					}
					TypeVariable tv = cp.getClass().getTypeParameters()[0];
					Class cls = (Class) tv.getBounds()[0];
					Method[] ms = cls.getDeclaredMethods();
					for (int i = 0; i < ms.length; i++) {
						Method m = ms[i];
						LocalCloundApi api = new LocalCloundApi() {

							@Override
							public boolean cloundCall(
									AIStack<CloundResponse> stack,
									CloundRequest req) {
								throw new IllegalStateException("can't execute");
							}
						};
						api.setApiId(m.getName());
						api.setTitle(m.toGenericString());
						apiList.add(api);
					}
				} catch (Exception e) {
					log.debug("get thrift method fail", e);
				}
				LocalCloundService cs = new LocalCloundService() {

					@Override
					public String getTrackString() {
						return "service(" + getServiceId() + ")";
					}

					@Override
					public Map<String, CloundApi> createApis() {
						return null;
					}
				};
				cs.setTitle(p.getClass().getName());
				cs.setServiceId(serviceId);
				cs.setApis(apiList);
				cslist.add(cs);
			}
			setServices(cslist);
		}
		return super.getServiceMap();
	}
}
