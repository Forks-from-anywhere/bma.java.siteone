package bma.siteone.comments.service;

import bma.common.langutil.core.SearchForm;

public class SearchCommentPointForm extends SearchForm {

	private String name;

	private String url;

	private String title;

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String point) {
		this.name = point;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String subject) {
		this.title = subject;
	}

}
