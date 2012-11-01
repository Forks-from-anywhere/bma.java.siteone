package bma.siteone.cloud.common;

import java.util.Map;

import bma.common.langutil.core.SizeUtil;
import bma.common.langutil.core.SizeUtil.Unit;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.cloud.local.SimpleLocalCloudApi;

public class CloudApi4GC extends SimpleLocalCloudApi<String, Boolean> {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CloudApi4GC.class);

	private String confirmWord;

	public String getConfirmWord() {
		return confirmWord;
	}

	public void setConfirmWord(String confirmWord) {
		this.confirmWord = confirmWord;
	}

	public CloudApi4GC() {
		super();
		setTitle("JVM memory garbage collection");
	}

	@Override
	public Map<String, Object> getDesc() {
		Map<String, Object> r = super.getDesc();
		Runtime rt = Runtime.getRuntime();
		long v;
		v = rt.freeMemory();
		r.put("freeMemory", v);
		r.put("freeMemoryM", SizeUtil.convert(v, 1024, Unit.M));
		v = rt.totalMemory();
		r.put("totalMemory", v);
		r.put("totalMemoryM", SizeUtil.convert(v, 1024, Unit.M));
		return r;
	}

	@Override
	public Class<String> getParamClass() {
		return String.class;
	}

	@Override
	public Boolean execute(String req) {
		if (ValueUtil.notEmpty(confirmWord)) {
			if (!StringUtil.equals(req, confirmWord)) {
				if (log.isWarnEnabled()) {
					log.warn("GC error confirmWord({})", req);
				}
				return false;
			}
		}
		if (log.isInfoEnabled()) {
			log.info("GC!!!!!!!!");
		}
		Runtime.getRuntime().gc();
		return true;
	}

}
