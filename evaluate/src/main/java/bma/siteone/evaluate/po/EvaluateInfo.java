package bma.siteone.evaluate.po;

import java.io.Serializable;
import java.util.Date;

import bma.common.langutil.core.ToStringUtil;

/**
 * 评价信息
 * 
 * @author guanzhong
 * 
 */
public class EvaluateInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String groupType;

	private String itemId;

	private String url;

	private String title;

	private int evaAmount;

	public static final int MAX_OPTION = 10;

	private int option1;
	private int option2;
	private int option3;
	private int option4;
	private int option5;
	private int option6;
	private int option7;
	private int option8;
	private int option9;
	private int option10;

	private int reserve1;

	private int reserve2;

	private String reserve3 = "";

	private String reserve4 = "";

	public final static int ST_DISABLED = 0;

	public final static int ST_NORMAL = 1;

	private int status;

	private Date createTime;

	private Date lastUpdateTime;

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getEvaAmount() {
		return evaAmount;
	}

	public void setEvaAmount(int commentAmount) {
		this.evaAmount = commentAmount;
	}

	public int getOption1() {
		return option1;
	}

	public void setOption1(int option1) {
		this.option1 = option1;
	}

	public int getOption2() {
		return option2;
	}

	public void setOption2(int option2) {
		this.option2 = option2;
	}

	public int getOption3() {
		return option3;
	}

	public void setOption3(int option3) {
		this.option3 = option3;
	}

	public int getOption4() {
		return option4;
	}

	public void setOption4(int option4) {
		this.option4 = option4;
	}

	public int getOption5() {
		return option5;
	}

	public void setOption5(int option5) {
		this.option5 = option5;
	}

	public int getOption6() {
		return option6;
	}

	public void setOption6(int option6) {
		this.option6 = option6;
	}

	public int getOption7() {
		return option7;
	}

	public void setOption7(int option7) {
		this.option7 = option7;
	}

	public int getOption8() {
		return option8;
	}

	public void setOption8(int option8) {
		this.option8 = option8;
	}

	public int getOption9() {
		return option9;
	}

	public void setOption9(int option9) {
		this.option9 = option9;
	}

	public int getOption10() {
		return option10;
	}

	public void setOption10(int option10) {
		this.option10 = option10;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

	public int getOptionValue(int num) {
		switch (num) {
		case 1:
			return option1;
		case 2:
			return option2;
		case 3:
			return option3;
		case 4:
			return option4;
		case 5:
			return option5;
		case 6:
			return option6;
		case 7:
			return option7;
		case 8:
			return option8;
		case 9:
			return option9;
		case 10:
			return option10;
		}
		return 0;
	}

	public void setOptionValue(int num, int v) {
		switch (num) {
		case 1:
			option1 = v;
			break;
		case 2:
			option2 = v;
			break;
		case 3:
			option3 = v;
			break;
		case 4:
			option4 = v;
			break;
		case 5:
			option5 = v;
			break;
		case 6:
			option6 = v;
			break;
		case 7:
			option7 = v;
			break;
		case 8:
			option8 = v;
			break;
		case 9:
			option9 = v;
			break;
		case 10:
			option10 = v;
			break;
		}
	}
}
