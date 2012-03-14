package bma.siteone.comments.po;

public class CommentPointShared {

	private String name;

	private String url;

	private String title;

	private int reserve1;

	private int reserve2;

	private String reserve3 = "";

	private String reserve4 = "";

	public CommentPointShared() {
		super();
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String subject) {
		this.title = subject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getReserve1() {
		return reserve1;
	}

	public void setReserve1(int reserve1) {
		this.reserve1 = reserve1;
	}

	public int getReserve2() {
		return reserve2;
	}

	public void setReserve2(int reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public String getReserve4() {
		return reserve4;
	}

	public void setReserve4(String reserve4) {
		this.reserve4 = reserve4;
	}

}
