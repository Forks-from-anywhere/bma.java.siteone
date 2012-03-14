package bma.siteone.matchlist.service;

import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.hibernate.Query;

import bma.common.hibernate.HibernateSearch;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.matchlist.po.MatchInfo;
import bma.siteone.matchlist.thrift.TMatchInfo;
import bma.siteone.matchlist.thrift.TMatchListService;
import bma.siteone.matchlist.thrift.TMatchSearchResult;

public class MatchListServiceThrift implements TMatchListService.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(MatchListServiceThrift.class);

	private MatchListService service;

	public MatchListService getService() {
		return service;
	}

	public void setService(MatchListService service) {
		this.service = service;
	}

	private MatchInfo convert(MatchInfo r, TMatchInfo info) {
		r.setContent(info.getContent());
		if (info.getCreateTime() != null) {
			r.setCreateTime(DateTimeUtil.parseDateTime(info.getCreateTime(),
					new Date()));
		}
		r.setGroupId(info.getGroupId());
		if (info.getId() > 0) {
			r.setId(info.getId());
		}
		if (info.getLastUpdateTime() != null) {
			r.setLastUpdateTime(DateTimeUtil.parseDateTime(
					info.getLastUpdateTime(), new Date()));
		}
		r.setReserve1(info.getReserve1());
		r.setReserve2(info.getReserve2());
		if (info.getReserve3() != null) {
			r.setReserve3(info.getReserve3());
		}
		if (info.getReserve4() != null) {
			r.setReserve4(info.getReserve4());
		}
		r.setTitle(info.getTitle());
		return r;
	}

	private TMatchInfo convert(TMatchInfo r, MatchInfo info) {
		r.setContent(info.getContent());
		r.setCreateTime(DateTimeUtil.formatDateTime(info.getCreateTime()));
		r.setGroupId(info.getGroupId());
		r.setId(info.getId());
		r.setLastUpdateTime(DateTimeUtil.formatDateTime(info
				.getLastUpdateTime()));
		r.setReserve1(info.getReserve1());
		r.setReserve2(info.getReserve2());
		r.setReserve3(info.getReserve3());
		r.setReserve4(info.getReserve4());
		r.setTitle(info.getTitle());
		return r;
	}

	private Function<MatchInfo, TMatchInfo> convert() {
		return new Function<MatchInfo, TMatchInfo>() {
			@Override
			public TMatchInfo apply(MatchInfo input) {
				return convert(new TMatchInfo(), input);
			}
		};
	}

	@Override
	public int createOne(TMatchInfo info) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("createOne({})", info);
		}
		MatchInfo o = convert(new MatchInfo(), info);
		return service.create(o);
	}

	@Override
	public boolean updateOne(TMatchInfo info) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("updateOne({})", info);
		}
		MatchInfo o = service.getOne(info.getId());
		if (o != null) {
			convert(o, info);
			return service.update(o);
		}
		return false;
	}

	@Override
	public boolean deleteOne(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteOne({})", id);
		}
		return service.delete(id);
	}

	@Override
	public TMatchInfo getOne(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getOne({})", id);
		}
		MatchInfo r = service.getOne(id);
		TMatchInfo o = new TMatchInfo();
		if (r != null)
			return convert(o, r);
		return o;
	}

	@Override
	public boolean match(String groupId, String v, String type)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("match({},{},{})", new Object[] { groupId, v, type });
		}
		return service.match(groupId, v, type);
	}

	@Override
	public List<String> listGroupId() throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listGroupId()");
		}
		return service.listGroupId();
	}

	@Override
	public List<TMatchInfo> listGroup(String groupId) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listGroup({})", groupId);
		}
		return ListUtil.toList(service.listGroup(groupId), convert());
	}

	@Override
	public TMatchSearchResult search(final String sql, int page, int pageSize)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("search({},{},{})", new Object[] { sql, page, pageSize });
		}

		TMatchSearchResult r = new TMatchSearchResult();
		PagerResult<MatchInfo> pr = service.search(new HibernateSearch() {

			@Override
			public String hsql(boolean count) {
				return sql.replace("TABLE", "MatchInfo");
			}

			@Override
			public void bind(Query q) {

			}
		}, page, pageSize);
		r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(), convert()));
		return r;
	}
}
