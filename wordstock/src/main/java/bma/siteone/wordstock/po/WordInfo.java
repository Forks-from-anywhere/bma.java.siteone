package bma.siteone.wordstock.po;

import java.io.Serializable;

import bma.common.langutil.core.ToStringUtil;

public class WordInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private String title;

	private String groupType;

	private String words;

	private String type;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String group) {
		this.groupType = group;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String inet) {
		this.words = inet;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
