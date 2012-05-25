package bma.siteone.iptables.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bma.common.jdbctemplate.JdbcTemplateHelper;
import bma.common.langutil.cache.ICache;
import bma.common.langutil.core.Pager;
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.io.InetNetwork;
import bma.common.langutil.jdbc.CommonFieldValues;
import bma.siteone.iptables.po.IptableInfo;

@Transactional
public class IptablesServiceImpl implements IptablesService {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(IptablesServiceImpl.class);

	private IptablesServiceImpl() {
		super();
	}

	private String iptablesTableName = "so_iptables";

	/**
	 * JdbcTemplate对象
	 */
	private transient JdbcTemplateHelper helper;
	private JdbcTemplate jdbcTemplate;

	private ICache<String, List<IptableItem>> infoCache;

	public String getIptablesTableName() {
		return iptablesTableName;
	}

	public void setIptablesTableName(String iptablesTableName) {
		this.iptablesTableName = iptablesTableName;
	}

	public ICache<String, List<IptableItem>> getInfoCache() {
		return infoCache;
	}

	public void setInfoCache(ICache<String, List<IptableItem>> infoCache) {
		this.infoCache = infoCache;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.helper = new JdbcTemplateHelper(jdbcTemplate);
	}

	public class InfoRowMapper implements RowMapper<IptableInfo> {

		public IptableInfo mapRow(ResultSet rs, int index) throws SQLException {
			IptableInfo info = new IptableInfo();
			info.setId(rs.getInt("id"));
			info.setGroupType(rs.getString("group_type"));
			info.setInet(rs.getString("Inet"));
			if (info.getInet() == null) {
				info.setInet("");
			}
			info.setType(rs.getString("type"));
			return info;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int create(IptableInfo info) {
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("group_type", info.getGroupType());
		fvs.addString("inet", info.getInet());
		fvs.addString("type", info.getType());

		Number id = helper.executeInsert(iptablesTableName, fvs, "id");
		clearCache(info.getGroupType());
		return id.intValue();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean update(IptableInfo info) {
		IptableInfo old = getOne(info.getId());
		if (old == null) {
			return false;
		}

		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("group_type", info.getGroupType());
		fvs.addString("inet", info.getInet());
		fvs.addString("type", info.getType());

		CommonFieldValues tj = new CommonFieldValues();
		tj.addInt("id", info.getId());

		boolean r = helper.executeUpdate(iptablesTableName, fvs, tj) == 1;
		if (r) {
			clearCache(old.getGroupType());
			clearCache(info.getGroupType());
		}
		return r;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean delete(int id) {
		IptableInfo old = getOne(id);
		if (old == null) {
			return false;
		}

		int c = jdbcTemplate.update("DELETE FROM " + iptablesTableName
				+ " WHERE id = ?", id);
		if (c == 1) {
			clearCache(old.getGroupType());
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public IptableInfo getOne(int id) {
		return helper.selectOne(iptablesTableName, "id", id,
				new InfoRowMapper());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public PagerResult<IptableInfo> search(String sql, int page, int pageSize) {
		sql = sql.replaceAll("TABLE", iptablesTableName);
		int total = jdbcTemplate.query("SELECT COUNT(*) " + sql,
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
					}
				}, new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						rs.next();
						return rs.getInt(1);
					}
				});

		final Pager pager = new Pager(total, page, pageSize);
		String sql2 = "SELECT * " + sql + " LIMIT ?,?";
		List<IptableInfo> list = jdbcTemplate.query(sql2,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						int idx = 1;
						ps.setInt(idx++, pager.getStart());
						ps.setInt(idx++, pager.getPageSize());
					}
				}, new InfoRowMapper());

		PagerResult<IptableInfo> result = new PagerResult<IptableInfo>();
		result.setPager(pager);
		result.setResult(list);
		return result;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public List<IptableInfo> listInfo(final String groupId) {
		return jdbcTemplate.query("SELECT * FROM " + iptablesTableName
				+ " WHERE group_type = ? ORDER BY id",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, groupId);
					}
				}, new InfoRowMapper());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public List<String> listGroupType() {
		return jdbcTemplate.query("SELECT DISTINCT group_type FROM "
				+ iptablesTableName, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
			}
		}, new ResultSetExtractor<List<String>>() {
			@Override
			public List<String> extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				List<String> r = new LinkedList<String>();
				while (rs.next()) {
					r.add(rs.getString(1));
				}
				return r;
			}
		});
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public String match(String groupId, String ip) {
		List<IptableItem> ilist = this.infoCache.get(groupId);
		if (ilist == null) {
			List<IptableInfo> polist = listInfo(groupId);
			ilist = new ArrayList<IptableItem>();
			List<IptableItem> rjlist = new LinkedList<IptableItem>();
			List<IptableItem> aclist = new LinkedList<IptableItem>();
			List<IptableItem> alllist = new LinkedList<IptableItem>();
			for (IptableInfo info : polist) {
				String str = info.getInet();
				String sp[] = StringUtil.tokenSplit(str, "\n\r,;");
				String as[] = sp;
				List<InetNetwork> list = new ArrayList<InetNetwork>(as.length);
				for (String ss : as) {
					InetNetwork in = InetNetwork.create(ss.trim());
					if (in != null) {
						list.add(in);
					}
				}
				IptableItem item = new IptableItem();
				item.setInfo(info);
				item.setList(list);

				if (StringUtil.equals("*", info.getInet())) {
					alllist.add(item);
				} else if (StringUtil.equals(TYPE_REJECT, info.getType())) {
					rjlist.add(item);
				} else {
					aclist.add(item);
				}
			}
			ilist.addAll(rjlist);
			ilist.addAll(aclist);
			ilist.addAll(alllist);
			if (log.isDebugEnabled()) {
				log.debug("{} -> {}", groupId, ilist);
			}
			this.infoCache.put(groupId, ilist);
		}
		for (IptableItem item : ilist) {
			if (StringUtil.equals(item.getInfo().getInet().trim(), "*")) {
				if (log.isDebugEnabled()) {
					log.debug("{} match-all {}", ip, item.getInfo());
				}
				return item.getInfo().getType();
			}
			if (InetNetwork.isMatch(item.getList(), ip)) {
				if (log.isDebugEnabled()) {
					log.debug("{} match {}", ip, item.getInfo());
				}
				return item.getInfo().getType();
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("{} no-match", ip);
		}
		return TYPE_NOMATCH;
	}

	public void clearCache(String groupType) {
		infoCache.remove(groupType);
	}
}
