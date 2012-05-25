package bma.siteone.wordstock.service;

import java.util.List;

import org.apache.thrift.TException;

import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.wordstock.po.WordInfo;
import bma.siteone.wordstock.thrift.TWordInfo;
import bma.siteone.wordstock.thrift.TWordStockSearchResult;
import bma.siteone.wordstock.thrift.TWordStockService;

public class WordStockServiceThrift implements TWordStockService.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(WordStockServiceThrift.class);

	private WordStockService service;

	public WordStockServiceThrift() {
		super();
	}

	public WordStockService getService() {
		return service;
	}

	public void setService(WordStockService service) {
		this.service = service;
	}

	protected WordInfo f(TWordInfo info) {
		WordInfo r = new WordInfo();
		r.setGroupType(info.getGroupType());
		r.setId(info.getId());
		r.setWords(info.getWords());
		r.setTitle(info.getTitle());
		r.setType(info.getType());
		return r;
	}

	protected TWordInfo t(WordInfo info) {
		TWordInfo r = new TWordInfo();
		if (info == null)
			return r;
		r.setGroupType(info.getGroupType());
		r.setId(info.getId());
		r.setWords(info.getWords());
		r.setTitle(info.getTitle());
		r.setType(info.getType());
		return r;
	}

	@Override
	public int createWord(TWordInfo info) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("createWord({})", info);
		}
		return service.create(f(info));
	}

	@Override
	public boolean updateWord(TWordInfo info) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("updateWord({})", info);
		}
		return service.update(f(info));
	}

	@Override
	public boolean deleteWord(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteWord({})", id);
		}
		return service.delete(id);
	}

	@Override
	public TWordInfo getWord(int id) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getWord({})", id);
		}
		return t(service.getOne(id));
	}

	@Override
	public String matchWordStock(String groupType, String content)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("matchWordStock({},{})", groupType, content);
		}
		String r = service.match(groupType, content);
		return r == null ? "" : r;
	}

	@Override
	public List<String> listWordStockGroupType() throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listWordStockGroupType()");
		}
		return service.listGroupType();
	}

	private class C implements Function<WordInfo, TWordInfo> {
		@Override
		public TWordInfo apply(WordInfo input) {
			return t(input);
		}
	};

	@Override
	public List<TWordInfo> listWordStockInfo(String groupType)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("listWordStockInfo({})", groupType);
		}
		List<WordInfo> r = service.listInfo(groupType);
		return ListUtil.toList(r, new C());
	}

	@Override
	public TWordStockSearchResult searchWordStock(String sql, int page,
			int pageSize) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("searchWordStock({},{},{})", new Object[] { sql, page,
					pageSize });
		}
		PagerResult<WordInfo> pr = service.search(sql, page, pageSize);
		TWordStockSearchResult r = new TWordStockSearchResult();
		r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(), new C()));
		return r;
	}

}
