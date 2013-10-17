package bma.siteone.thrift.stub;

import bma.common.langutil.core.ToStringUtil;

public class ThriftStubInfo {

	private String name;
	private String method;
	private String type;
	private String content;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

}
