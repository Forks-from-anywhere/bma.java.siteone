package bma.siteone.matchlist.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bma.common.hibernate.HibernateSearch;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.io.InetNetwork;
import bma.siteone.matchlist.po.MatchInfo;

@Transactional
public class MatchListServiceImpl implements MatchListService {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(MatchListServiceImpl.class);

	private HibernateTemplate session;
	private Map<String, Object> cache = new HashMap<String, Object>(100);

	public void setSessionFactory(SessionFactory sessionFactory) {
		session = new HibernateTemplate(sessionFactory);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int create(MatchInfo info) {
		info.setCreateTime(new Date());
		info.setLastUpdateTime(new Date());
		session.save(info);
		cache.remove(info.getGroupId());
		return info.getId();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean update(MatchInfo info) {
		MatchInfo o = getOne(info.getId());
		if (o != null) {
			info.setLastUpdateTime(new Date());
			session.merge(info);
			cache.remove(o.getGroupId());
			cache.remove(info.getGroupId());
		}
		return true;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean delete(int id) {
		MatchInfo info = getOne(id);
		if (info != null) {
			session.delete(info);
			cache.remove(info.getGroupId());
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public MatchInfo getOne(int id) {
		return (MatchInfo) session.get(MatchInfo.class, id);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public List<String> listGroupId() {
		return ListUtil.toList(
				session.find("SELECT distinct groupId FROM MatchInfo o"),
				String.class);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public List<MatchInfo> listGroup(String groupId) {
		return ListUtil.toList(
				session.find("FROM MatchInfo o WHERE groupId = ?", groupId),
				MatchInfo.class);
	}

	public static final String TYPE_IP = "ip";
	public static final String TYPE_REGEX = "regex";
	public static final String TYPE_CONTENT = "content";

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public boolean match(String groupId, String v, String type) {
		if (StringUtil.equalsIgnoreCase(TYPE_IP, type)) {
			return ipMatch(groupId, v);
		} else if (StringUtil.equalsIgnoreCase(TYPE_REGEX, type)) {
			return regexMatch(groupId, v);
		} else {
			return contentMatch(groupId, v);
		}
	}

	@SuppressWarnings("unchecked")
	protected boolean ipMatch(String groupId, String ip) {
		Object o = this.cache.get(groupId);
		if (o == null) {
			List<MatchInfo> mlist = listGroup(groupId);
			if (mlist != null) {
				List<InetNetwork> list = new LinkedList<InetNetwork>();
				for (MatchInfo info : mlist) {
					String str = info.getContent();
					String sp[] = StringUtil.tokenSplit(str, "\n\r;");
					String as[] = sp;
					int i = 0;
					for (int j = as.length; i < j; i++) {
						String ss = as[i];
						InetNetwork in = InetNetwork.create(ss.trim());
						if (in != null) {
							list.add(in);
						}
					}
				}
				this.cache.put(groupId, list);
				o = list;
			}
		}
		if (o != null && o instanceof List) {
			return InetNetwork.isMatch((List<InetNetwork>) o, ip);
		}
		return false;
	}

	protected boolean contentMatch(String groupId, String v) {
		Object o = this.cache.get(groupId);
		if (o == null) {
			List<MatchInfo> mlist = listGroup(groupId);
			List<String> list = new LinkedList<String>();
			if (mlist != null) {
				for (MatchInfo info : mlist) {
					String str = info.getContent();
					String sp[] = StringUtil.tokenSplit(str, "\n\r;,");
					String as[] = sp;
					int i = 0;
					for (int j = as.length; i < j; i++) {
						String ss = as[i];
						if (!ss.isEmpty()) {
							list.add(ss);
						}
					}
				}
			}
			this.cache.put(groupId, list);
			o = list;
		}
		if (o != null && o instanceof List) {
			List<?> nl = (List<?>) o;
			return nl.indexOf(v) != -1;
		}
		return false;
	}

	protected boolean regexMatch(String groupId, String v) {
		Object o = this.cache.get(groupId);
		if (o == null) {
			List<MatchInfo> mlist = listGroup(groupId);
			List<Pattern> list = new LinkedList<Pattern>();
			if (mlist != null) {
				for (MatchInfo info : mlist) {
					String str = info.getContent();
					String sp[] = StringUtil.tokenSplit(str, "\n\r;,");
					String as[] = sp;
					int i = 0;
					for (int j = as.length; i < j; i++) {
						String ss = as[i];
						if (!ss.isEmpty()) {
							list.add(Pattern.compile(ss));
						}
					}
				}
			}
			this.cache.put(groupId, list);
			o = list;
		}
		if (o != null && o instanceof List) {
			List<?> nl = (List<?>) o;
			for (Object lo : nl) {
				Pattern p = (Pattern) lo;
				if (p.matcher(v).find()) {
					return true;
				}
			}
		}
		return false;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public PagerResult<MatchInfo> search(final HibernateSearch search,
			final int page, final int pageSize) {
		return session.execute(new HibernateCallback<PagerResult<MatchInfo>>() {
			@Override
			public PagerResult<MatchInfo> doInHibernate(Session s)
					throws HibernateException, SQLException {
				return search.search(s, page, pageSize, MatchInfo.class);
			}
		});

	}
}
