package bma.siteone.admin.cloud;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.StringUtil;
import bma.siteone.clound.local.SimpleLocalCloundApi;

public class CloundApi4Password extends SimpleLocalCloundApi<String, String> {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CloundApi4Password.class);

	private String confirmWord;

	public String getConfirmWord() {
		return confirmWord;
	}

	public void setConfirmWord(String confirmWord) {
		this.confirmWord = confirmWord;
	}

	public CloundApi4Password() {
		super();
		setTitle("计算密码校验数值");
	}

	@Override
	public Class<String> getParamClass() {
		return String.class;
	}

	@Override
	public String execute(String req) {
		try {
			return StringUtil.md5(req.getBytes("UTF-8"));
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

}
