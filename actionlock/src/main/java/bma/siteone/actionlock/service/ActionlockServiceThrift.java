package bma.siteone.actionlock.service;

import java.util.List;

import org.apache.thrift.TException;

import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.actionlock.po.ActionlockInfo;
import bma.siteone.actionlock.thrift.TActionlockInfo;
import bma.siteone.actionlock.thrift.TActionlockSearchResult;
import bma.siteone.actionlock.thrift.TActionlockService;

public class ActionlockServiceThrift implements TActionlockService.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ActionlockServiceThrift.class);

	private ActionlockService service;

	public ActionlockServiceThrift() {
		super();
	}

	public ActionlockService getService() {
		return service;
	}

	public void setService(ActionlockService service) {
		this.service = service;
	}

	protected TActionlockInfo t(ActionlockInfo info) {
		TActionlockInfo r = new TActionlockInfo();
		if (info == null)
			return r;
		r.setGroupType(info.getGroupType());
		r.setItemId(info.getItemId());
		r.setAccessCount(info.getAccessCount());
		r.setCleanTime(DateTimeUtil.formatDateTime(info.getCleanTime()));
		return r;
	}

	private class C implements Function<ActionlockInfo, TActionlockInfo> {
		@Override
		public TActionlockInfo apply(ActionlockInfo input) {
			return t(input);
		}
	};

	@Override
	public boolean checkActionlock(String groupType, String itemId,
			int accessCount, boolean release) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("checkActionlock({},{},{},{})", new Object[] { groupType,
					itemId, accessCount, release });
		}
		return service.checkLock(groupType, itemId, accessCount, release);
	}

	@Override
	public void cleanActionlock(String groupType) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("cleanActionlock({})", groupType);
		}
		service.clean(groupType);
	}

	@Override
	public int setActionlock(String groupType, String itemId, int accessCount,
			int cleanDelay) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("setActionlock({},{},{},{})", new Object[] { groupType,
					itemId, accessCount, cleanDelay });
		}
		return service.lock(groupType, itemId, accessCount, cleanDelay);
	}

	@Override
	public boolean deleteActionlock(String groupType, String itemId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteActionlock({},{})", groupType, itemId);
		}
		return service.delete(groupType, itemId);
	}

	@Override
	public boolean deleteActionlockGroup(String groupType) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteActionlockGroup({})", groupType);
		}
		return service.deleteGroup(groupType);
	}

	@Override
	public TActionlockInfo getActionlock(String groupType, String itemId,
			boolean timeout) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getActionlock({},{},{})", new Object[] { groupType,
					itemId, timeout });
		}
		return t(service.getOne(groupType, itemId, timeout));
	}

	@Override
	public List<String> listActionlockGroupType() throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listActionlockGroupType()");
		}
		return service.listGroupType();
	}

	@Override
	public TActionlockSearchResult searchActionlock(String sql, int page,
			int pageSize) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("searchActionlock({},{},{})", new Object[] { sql, page,
					pageSize });
		}
		PagerResult<ActionlockInfo> pr = service.search(sql, page, pageSize);
		TActionlockSearchResult r = new TActionlockSearchResult();
		r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(), new C()));
		return r;
	}

}
