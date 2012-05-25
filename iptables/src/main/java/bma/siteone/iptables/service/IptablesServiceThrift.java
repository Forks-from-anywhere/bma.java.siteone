package bma.siteone.iptables.service;

import java.util.List;

import org.apache.thrift.TException;

import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.iptables.po.IptableInfo;
import bma.siteone.iptables.thrift.TIptableInfo;
import bma.siteone.iptables.thrift.TIptableSearchResult;
import bma.siteone.iptables.thrift.TIptablesService;

public class IptablesServiceThrift implements TIptablesService.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(IptablesServiceThrift.class);

	private IptablesService service;

	public IptablesServiceThrift() {
		super();
	}

	public IptablesService getService() {
		return service;
	}

	public void setService(IptablesService service) {
		this.service = service;
	}

	protected IptableInfo f(TIptableInfo info) {
		IptableInfo r = new IptableInfo();
		r.setGroupType(info.getGroupType());
		r.setId(info.getId());
		r.setInet(info.getInet());
		r.setType(info.getType());
		return r;
	}

	protected TIptableInfo t(IptableInfo info) {
		TIptableInfo r = new TIptableInfo();
		if (info == null)
			return r;
		r.setGroupType(info.getGroupType());
		r.setId(info.getId());
		r.setInet(info.getInet());
		r.setType(info.getType());
		return r;
	}

	@Override
	public int createIptable(TIptableInfo info) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("createIptable({})", info);
		}
		return service.create(f(info));
	}

	@Override
	public boolean updateIptable(TIptableInfo info) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("updateIptable({})", info);
		}
		return service.update(f(info));
	}

	@Override
	public boolean deleteIptable(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteIptable({})", id);
		}
		return service.delete(id);
	}

	@Override
	public TIptableInfo getIptable(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getIptable({})", id);
		}
		return t(service.getOne(id));
	}

	@Override
	public String matchIptables(String groupType, String ip) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("matchIptables({},{})", groupType, ip);
		}
		return service.match(groupType, ip);
	}

	@Override
	public List<String> listIptablesGroupType() throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listIptablesGroupType()");
		}
		return service.listGroupType();
	}

	private class C implements Function<IptableInfo, TIptableInfo> {
		@Override
		public TIptableInfo apply(IptableInfo input) {
			return t(input);
		}
	};

	@Override
	public List<TIptableInfo> listIptablesInfo(String groupId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listIptablesInfo({})", groupId);
		}
		List<IptableInfo> r = service.listInfo(groupId);
		return ListUtil.toList(r, new C());
	}

	@Override
	public TIptableSearchResult searchIptables(String sql, int page,
			int pageSize) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("searchIptables({},{},{})", new Object[] { sql, page,
					pageSize });
		}
		PagerResult<IptableInfo> pr = service.search(sql, page, pageSize);
		TIptableSearchResult r = new TIptableSearchResult();
		r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(), new C()));
		return r;
	}

}
