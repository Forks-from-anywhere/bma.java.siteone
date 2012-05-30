package bma.siteone.evaluate.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;

import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.evaluate.po.EvaluateInfo;
import bma.siteone.evaluate.thrift.TEvaluateInfo;
import bma.siteone.evaluate.thrift.TEvaluateSearchResult;
import bma.siteone.evaluate.thrift.TEvaluateService;

public class EvaluateServiceThrift implements TEvaluateService.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(EvaluateServiceThrift.class);

	private EvaluateService service;

	public EvaluateServiceThrift() {
		super();
	}

	public EvaluateService getService() {
		return service;
	}

	public void setService(EvaluateService service) {
		this.service = service;
	}

	protected EvaluateInfo f(TEvaluateInfo info) {
		EvaluateInfo r = new EvaluateInfo();
		r.setGroupType(info.getGroupType());
		r.setItemId(info.getItemId());
		r.setUrl(info.getUrl());
		r.setTitle(info.getTitle());
		r.setEvaAmount(info.getEvaAmount());
		r.setOption1(info.getOption1());
		r.setOption2(info.getOption2());
		r.setOption3(info.getOption3());
		r.setOption4(info.getOption4());
		r.setOption5(info.getOption5());
		r.setOption6(info.getOption6());
		r.setOption7(info.getOption7());
		r.setOption8(info.getOption8());
		r.setOption9(info.getOption9());
		r.setOption10(info.getOption10());
		r.setStatus(info.getStatus());
		r.setReserve1(info.getReserve1());
		r.setReserve2(info.getReserve2());
		r.setReserve3(info.getReserve3());
		r.setReserve4(info.getReserve4());

		return r;
	}

	protected TEvaluateInfo t(EvaluateInfo info) {
		TEvaluateInfo r = new TEvaluateInfo();
		if(info==null)return r;
		r.setGroupType(info.getGroupType());
		r.setItemId(info.getItemId());
		r.setUrl(info.getUrl());
		r.setTitle(info.getTitle());
		r.setEvaAmount(info.getEvaAmount());
		r.setOption1(info.getOption1());
		r.setOption2(info.getOption2());
		r.setOption3(info.getOption3());
		r.setOption4(info.getOption4());
		r.setOption5(info.getOption5());
		r.setOption6(info.getOption6());
		r.setOption7(info.getOption7());
		r.setOption8(info.getOption8());
		r.setOption9(info.getOption9());
		r.setOption10(info.getOption10());
		r.setStatus(info.getStatus());
		r.setReserve1(info.getReserve1());
		r.setReserve2(info.getReserve2());
		r.setReserve3(info.getReserve3());
		r.setReserve4(info.getReserve4());
		r.setCreateTime(DateTimeUtil.formatDateTime(info.getCreateTime()));
		r.setLastUpdateTime(DateTimeUtil.formatDateTime(info
				.getLastUpdateTime()));
		return r;
	}

	@Override
	public boolean voteEvaluate(TEvaluateInfo info) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("voteEvaluate({})", info);
		}
		Set<Integer> ops = new HashSet<Integer>();
		EvaluateInfo e = f(info);
		for (int i = 1; i <= EvaluateInfo.MAX_OPTION; i++) {
			if (e.getOptionValue(i) >= 0) {
				ops.add(i);
			}
		}
		return service.vote(e, ops);
	}

	@Override
	public boolean updateEvaluate(TEvaluateInfo info) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("updateEvaluate({})", info);
		}
		return service.update(f(info));
	}

	@Override
	public boolean deleteEvaluate(String groupType, String itemId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteEvaluate({},{})", groupType, itemId);
		}
		return service.delete(groupType, itemId);
	}

	@Override
	public boolean deleteEvaluateGroup(String groupType) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteEvaluateGroup({})", groupType);
		}
		return service.deleteGroup(groupType);
	}

	@Override
	public TEvaluateInfo getEvaluate(String groupType, String itemId)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getEvaluate({},{})", groupType, itemId);
		}
		return t(service.getOne(groupType, itemId));
	}

	@Override
	public List<String> listEvaluateGroupType() throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listEvaluateGroupType()");
		}
		return service.listGroupType();
	}

	private class C implements Function<EvaluateInfo, TEvaluateInfo> {
		@Override
		public TEvaluateInfo apply(EvaluateInfo input) {
			return t(input);
		}
	};

	@Override
	public TEvaluateSearchResult searchEvaluate(String sql, int page,
			int pageSize) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("searchEvaluate({},{},{})", new Object[] { sql, page,
					pageSize });
		}
		PagerResult<EvaluateInfo> pr = service.search(sql, page, pageSize);
		TEvaluateSearchResult r = new TEvaluateSearchResult();
		r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(), new C()));
		return r;
	}

}
