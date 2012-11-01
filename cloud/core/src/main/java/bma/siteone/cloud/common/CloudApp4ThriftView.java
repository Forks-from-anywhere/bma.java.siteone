package bma.siteone.cloud.common;

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
import bma.siteone.cloud.CloudApi;
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.CloudService;
import bma.siteone.cloud.local.LocalCloudApi;
import bma.siteone.cloud.local.LocalCloudApp;
import bma.siteone.cloud.local.LocalCloudService;

public class CloudApp4ThriftView extends LocalCloudApp implements
		ApplicationContextAware {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CloudApp4ThriftView.class);

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
	public Map<String, CloudService> getServiceMap() {
		if (this.serviceMap == null) {
			if (this.thriftServiceNode == null) {
				this.thriftServiceNode = this.context.getBean(
						this.thriftNodeBean, ThriftServiceNode.class);
			}
			List<ThriftServiceBean> slist = this.thriftServiceNode
					.getServices();
			List<CloudService> cslist = new ArrayList<CloudService>(
					slist.size());
			for (ThriftServiceBean bean : slist) {
				String serviceId = bean.getModule();
				List<CloudApi> apiList = new ArrayList<CloudApi>();

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
						LocalCloudApi api = new LocalCloudApi() {

							@Override
							public boolean cloudCall(
									AIStack<CloudResponse> stack,
									CloudRequest req) {
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
				LocalCloudService cs = new LocalCloudService() {

					@Override
					public String getTrackString() {
						return "service(" + getServiceId() + ")";
					}

					@Override
					public Map<String, CloudApi> createApis() {
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
