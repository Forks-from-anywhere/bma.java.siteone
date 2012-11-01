package bma.siteone.cloud.common;

import java.io.File;
import java.util.List;
import java.util.Map;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.ToStringUtil;
import bma.common.langutil.io.IOUtil;
import bma.common.langutil.runtime.RuntimeConfig;
import bma.siteone.cloud.common.CloudApi4RuntimeConfig.REQ;
import bma.siteone.cloud.local.SimpleLocalCloudApi;

public class CloudApi4RuntimeConfig extends SimpleLocalCloudApi<REQ, String> {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CloudApi4RuntimeConfig.class);

	public static class REQ {
		private boolean update;
		private String content;

		public boolean isUpdate() {
			return update;
		}

		public void setUpdate(boolean query) {
			this.update = query;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

	private RuntimeConfig config;
	private String fileName;

	public CloudApi4RuntimeConfig() {
		super();
		setTitle("access RuntimeConfig file content");
	}

	public RuntimeConfig getConfig() {
		return config;
	}

	public void setConfig(RuntimeConfig config) {
		this.config = config;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public Map<String, Object> getDesc() {
		Map<String, Object> r = super.getDesc();
		r.put("fileName", fileName);
		return r;
	}

	@Override
	public Class<REQ> getParamClass() {
		return REQ.class;
	}

	public File getConfigFile() {
		if (this.config == null) {
			throw new NullPointerException("config object is null");
		}
		List<String> list = this.config.getFiles();
		if (list != null) {
			for (String n : list) {
				if (n.indexOf(fileName) != -1) {
					if (log.isDebugEnabled()) {
						log.debug("{} match {}", fileName, n);
					}
					return new File(n);
				}
			}
		}
		throw new NullPointerException("config file (" + fileName
				+ ") not found");
	}

	@Override
	public String execute(REQ req) {
		if (log.isDebugEnabled()) {
			log.info("execute({})", ToStringUtil.fieldReflect(req));
		}

		File file = getConfigFile();
		try {
			if (req.isUpdate()) {
				if(req.getContent()==null) {
					throw new NullPointerException("content is null");					
				}
				IOUtil.writeStringToFile(file, req.getContent(), "UTF-8");
				return "done";
			} else {
				if (file.exists()) {

					return IOUtil.readFileToString(file, "UTF-8");

				}
				return "";
			}
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}
}
