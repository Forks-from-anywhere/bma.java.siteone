package bma.siteone.evaluate.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bma.common.jdbctemplate.JdbcTemplateHelper;
import bma.common.jdbctemplate.SQLFilter;
import bma.common.langutil.cache.ICache;
import bma.common.langutil.core.Pager;
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.jdbc.CommonFieldValues;
import bma.siteone.evaluate.po.EvaluateInfo;

@Transactional
public class EvaluateServiceImpl implements EvaluateService {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(EvaluateServiceImpl.class);

	private EvaluateServiceImpl() {
		super();
	}

	private String evaluateTableName = "so_evaluate";

	/**
	 * JdbcTemplate对象
	 */
	private transient JdbcTemplateHelper helper;
	private JdbcTemplate jdbcTemplate;

	private ICache<String, EvaluateInfo> infoCache;

	public String getEvaluateTableName() {
		return evaluateTableName;
	}

	public void setEvaluateTableName(String evaluateTableName) {
		this.evaluateTableName = evaluateTableName;
	}

	public ICache<String, EvaluateInfo> getInfoCache() {
		return infoCache;
	}

	public void setInfoCache(ICache<String, EvaluateInfo> infoCache) {
		this.infoCache = infoCache;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.helper = new JdbcTemplateHelper(jdbcTemplate);
	}

	public class InfoRowMapper implements RowMapper<EvaluateInfo> {

		public EvaluateInfo mapRow(ResultSet rs, int index) throws SQLException {
			EvaluateInfo info = new EvaluateInfo();
			info.setGroupType(rs.getString("group_type"));
			info.setItemId(rs.getString("item_id"));
			info.setUrl(rs.getString("url"));
			info.setTitle(rs.getString("title"));
			info.setEvaAmount(rs.getInt("eva_amount"));

			for (int i = 1; i <= EvaluateInfo.MAX_OPTION; i++) {
				info.setOptionValue(i, rs.getInt("option" + i));
			}

			info.setStatus(rs.getInt("status"));

			info.setReserve1(rs.getInt("reserve1"));
			info.setReserve2(rs.getInt("reserve2"));
			info.setReserve3(rs.getString("reserve3"));
			info.setReserve4(rs.getString("reserve4"));

			info.setCreateTime(new Date(rs.getTimestamp("create_time")
					.getTime()));
			info.setLastUpdateTime(new Date(rs.getTimestamp("last_update_time")
					.getTime()));

			return info;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean vote(EvaluateInfo info, Set<Integer> options) {

		boolean r = false;
		EvaluateInfo old = getOne(info.getGroupType(), info.getItemId());
		if (old == null) {
			CommonFieldValues fvs = new CommonFieldValues();
			fvs.addString("group_type", info.getGroupType());
			fvs.addString("item_id", info.getItemId());
			fvs.addString("url", info.getUrl());
			fvs.addString("title", info.getTitle());
			if (info.getEvaAmount() >= 0) {
				fvs.addInt("eva_amount", info.getEvaAmount());
			}
			for (int i = 1; i <= EvaluateInfo.MAX_OPTION; i++) {
				fvs.addInt("option" + i, 0);
			}

			fvs.addInt("status", info.getStatus());
			fvs.addInt("reserve1", info.getReserve1());
			fvs.addInt("reserve2", info.getReserve2());
			fvs.addString("reserve3",
					ValueUtil.stringValue(info.getReserve3(), ""));
			fvs.addString("reserve4",
					ValueUtil.stringValue(info.getReserve4(), ""));

			fvs.addSysdate("create_time");
			fvs.addSysdate("last_update_time");

			SQLFilter filter = new SQLFilter() {

				@Override
				public String filter(String sql) {
					return sql.replaceAll("INSERT ", "INSERT IGNORE ");
				}
			};
			helper.executeInsert(evaluateTableName, fvs, null, filter);
			r = true;
		}

		if (options != null && options.size() > 0) {
			CommonFieldValues fvs = new CommonFieldValues();

			fvs.addConst("eva_amount", "eva_amount + 1");
			for (int op : options) {
				if (op >= 0 && op <= EvaluateInfo.MAX_OPTION) {
					fvs.addConst("option" + op,
							"option" + op + " + " + info.getOptionValue(op));
				}
			}
			fvs.addSysdate("last_update_time");

			CommonFieldValues tj = new CommonFieldValues();
			tj.addString("group_type", info.getGroupType());
			tj.addString("item_id", info.getItemId());

			r = helper.executeUpdate(evaluateTableName, fvs, tj) == 1;
		}
		clearCache(info.getGroupType(), info.getItemId());
		return r;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean update(EvaluateInfo info) {
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("url", info.getUrl());
		fvs.addString("title", info.getTitle());
		if (info.getEvaAmount() >= 0) {
			fvs.addInt("eva_amount", info.getEvaAmount());
		}
		for (int i = 1; i <= EvaluateInfo.MAX_OPTION; i++) {
			if (info.getOptionValue(i) >= 0) {
				fvs.addInt("option" + i, info.getOptionValue(i));
			}
		}

		fvs.addInt("status", info.getStatus());
		fvs.addInt("reserve1", info.getReserve1());
		fvs.addInt("reserve2", info.getReserve2());
		fvs.addString("reserve3", ValueUtil.stringValue(info.getReserve3(), ""));
		fvs.addString("reserve4", ValueUtil.stringValue(info.getReserve4(), ""));

		fvs.addSysdate("last_update_time");

		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("group_type", info.getGroupType());
		tj.addString("item_id", info.getItemId());

		boolean r = helper.executeUpdate(evaluateTableName, fvs, tj) == 1;
		if (r) {
			clearCache(info.getGroupType(), info.getItemId());
		}
		return r;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean delete(String groupType, String itemId) {
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("group_type", groupType);
		tj.addString("item_id", itemId);
		int c = this.helper.executeDelete(evaluateTableName, tj);

		if (c == 1) {
			clearCache(groupType, itemId);
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean deleteGroup(String groupType) {
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("group_type", groupType);
		int c = this.helper.executeDelete(evaluateTableName, tj);

		if (c > 0) {
			this.infoCache.clear();
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public PagerResult<EvaluateInfo> search(String sql, int page, int pageSize) {
		sql = sql.replaceAll("TABLE", evaluateTableName);
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
		List<EvaluateInfo> list = jdbcTemplate.query(sql2,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						int idx = 1;
						ps.setInt(idx++, pager.getStart());
						ps.setInt(idx++, pager.getPageSize());
					}
				}, new InfoRowMapper());

		PagerResult<EvaluateInfo> result = new PagerResult<EvaluateInfo>();
		result.setPager(pager);
		result.setResult(list);
		return result;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public List<String> listGroupType() {
		return jdbcTemplate.query("SELECT DISTINCT group_type FROM "
				+ evaluateTableName, new PreparedStatementSetter() {
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

	public String cacheKey(String g, String i) {
		return g + ":" + i;
	}

	private static EvaluateInfo NULL = new EvaluateInfo();

	@Override
	public EvaluateInfo getOne(final String groupType, final String itemId) {
		String key = cacheKey(groupType, itemId);
		EvaluateInfo o = infoCache.get(key);
		if (o != null) {
			if (o == NULL)
				return null;
			return o;
		}
		List<EvaluateInfo> r = jdbcTemplate.query("SELECT * FROM "
				+ evaluateTableName + " WHERE group_type = ? AND item_id = ?",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, groupType);
						ps.setString(2, itemId);
					}
				}, new InfoRowMapper());
		if (r.isEmpty()) {
			EvaluateInfo v = infoCache.put(key, NULL);
			if (v != null && v != NULL)
				infoCache.put(key, v);
			return null;
		}
		o = r.get(0);
		infoCache.put(key, o);
		return o;
	}

	public void clearCache(String groupType, String itemId) {
		infoCache.remove(cacheKey(groupType, itemId));
	}
}
