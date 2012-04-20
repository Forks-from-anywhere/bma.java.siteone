package bma.siteone.filestore.service;

import java.io.File;

import bma.common.langutil.core.ToStringUtil;

public class FileStoreConfig {

	/**
	 * 应用编号
	 */
	private String appId;

	/**
	 * 应用的验证密匙
	 */
	private String key;

	/**
	 * 文件存储的根目录
	 */
	private File root;

	/**
	 * 文件存的临时目录，如果不设置，采用FileStore本身的临时目录
	 */
	private File temp;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public File getRoot() {
		return root;
	}

	public void setRoot(File root) {
		this.root = root;
	}

	public void setRootFile(String root) {
		this.root = new File(root);
	}

	public File getTemp() {
		return temp;
	}

	public void setTemp(File temp) {
		this.temp = temp;
	}

	public void setTempFile(String temp) {
		this.temp = new File(temp);
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
