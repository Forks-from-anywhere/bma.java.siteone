package bma.siteone.actionlock.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bma.common.jdbctemplate.JdbcTemplateHelper;
import bma.common.jdbctemplate.SQLFilter;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.Pager;
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.jdbc.CommonFieldValues;
import bma.siteone.actionlock.po.ActionlockInfo;

@Transactional
public class ActionlockServiceImpl implements ActionlockService {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ActionlockServiceImpl.class);

	private ActionlockServiceImpl() {
		super();
	}

	private String actionlockTableName = "so_actionlock";

	/**
	 * JdbcTemplate对象
	 */
	private transient JdbcTemplateHelper helper;
	private JdbcTemplate jdbcTemplate;

	private long defaultCleanDelay = 30 * DateTimeUtil.DAY;
	private ScheduledExecutorService timer;
	private long cleanPeriod = 5 * DateTimeUtil.MINUTE;

	public long getCleanPeriod() {
		return cleanPeriod;
	}

	public void setCleanPeriod(long cleanPeriod) {
		this.cleanPeriod = cleanPeriod;
	}

	public ScheduledExecutorService getTimer() {
		return timer;
	}

	public void setTimer(ScheduledExecutorService schedule) {
		this.timer = schedule;
	}

	public long getDefaultCleanDelay() {
		return defaultCleanDelay;
	}

	public void setDefaultCleanDelay(long defaultCleanTime) {
		this.defaultCleanDelay = defaultCleanTime;
	}

	public String getActionlockTableName() {
		return actionlockTableName;
	}

	public void setActionlockTableName(String evaluateTableName) {
		this.actionlockTableName = evaluateTableName;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.helper = new JdbcTemplateHelper(jdbcTemplate);
	}

	public void init() {
		if (this.timer == null) {
			this.timer = Executors.newSingleThreadScheduledExecutor();
		}

		this.timer.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				clean(null);
			}

		}, this.cleanPeriod, this.cleanPeriod, TimeUnit.MILLISECONDS);

	}

	public class InfoRowMapper implements RowMapper<ActionlockInfo> {

		public ActionlockInfo mapRow(ResultSet rs, int index)
				throws SQLException {
			ActionlockInfo info = new ActionlockInfo();
			info.setGroupType(rs.getString("group_type"));
			info.setItemId(rs.getString("item_id"));

			info.setAccessCount(rs.getInt("access_count"));
			info.setCleanTime(new Date(rs.getTimestamp("clean_time").getTime()));

			info.setCreateTime(new Date(rs.getTimestamp("create_time")
					.getTime()));
			info.setLastUpdateTime(new Date(rs.getTimestamp("last_update_time")
					.getTime()));

			return info;
		}
	}

	@Override
	public ActionlockInfo getOne(final String groupType, final String itemId,
			boolean timeout) {
		List<ActionlockInfo> r = jdbcTemplate.query(
				"SELECT * FROM " + actionlockTableName
						+ " WHERE group_type = ? AND item_id = ?",
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, groupType);
						ps.setString(2, itemId);
					}
				}, new InfoRowMapper());
		if (r.isEmpty()) {
			return null;
		}
		ActionlockInfo info = r.get(0);
		if (info != null) {
			if (!timeout
					&& System.currentTimeMillis() >= info.getCleanTime()
							.getTime()) {
				CommonFieldValues tj = new CommonFieldValues();
				tj.addString("group_type", groupType);
				tj.addString("item_id", itemId);
				tj.addTimestamp("clean_time", System.currentTimeMillis(), "<=");
				helper.executeDelete(actionlockTableName, tj);
				return null;
			}
		}
		return info;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public List<String> listGroupType() {
		return jdbcTemplate.query("SELECT DISTINCT group_type FROM "
				+ actionlockTableName, new PreparedStatementSetter() {
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
	public PagerResult<ActionlockInfo> search(String sql, int page, int pageSize) {
		sql = sql.replaceAll("TABLE", actionlockTableName);
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
		List<ActionlockInfo> list = jdbcTemplate.query(sql2,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						int idx = 1;
						ps.setInt(idx++, pager.getStart());
						ps.setInt(idx++, pager.getPageSize());
					}
				}, new InfoRowMapper());

		PagerResult<ActionlockInfo> result = new PagerResult<ActionlockInfo>();
		result.setPager(pager);
		result.setResult(list);
		return result;
	}

	protected boolean _update(String groupType, String itemId, int accessCount,
			Date ct) {
		CommonFieldValues fvs = new CommonFieldValues();

		fvs.addConst("access_count", "access_count + " + accessCount);
		fvs.addTimestamp("clean_time", ct);
		fvs.addSysdate("last_update_time");

		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("group_type", groupType);
		tj.addString("item_id", itemId);

		return helper.executeUpdate(actionlockTableName, fvs, tj) == 1;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int lock(String groupType, String itemId, int accessCount,
			long cleanDelay) {

		Date ct = new Date(System.currentTimeMillis()
				+ (cleanDelay > 0 ? cleanDelay : defaultCleanDelay));
		boolean r = false;
		if (groupType != null) {
			r = _update(groupType, itemId, accessCount, ct);
		}

		if (!r) {
			CommonFieldValues fvs = new CommonFieldValues();
			fvs.addString("group_type", groupType);
			fvs.addString("item_id", itemId);
			fvs.addInt("access_count", accessCount);
			fvs.addTimestamp("clean_time", ct);
			fvs.addSysdate("create_time");
			fvs.addSysdate("last_update_time");

			SQLFilter filter = new SQLFilter() {

				@Override
				public String filter(String sql) {
					return sql.replaceAll("INSERT ", "INSERT IGNORE ");
				}
			};
			r = helper.executeInsert(actionlockTableName, fvs, null, filter)
					.intValue() == 1;
			if (r)
				return accessCount;
			_update(groupType, itemId, accessCount, ct);
		}

		ActionlockInfo info = getOne(groupType, itemId, false);
		if (info != null)
			return info.getAccessCount();
		return accessCount;
	}

	@Override
	public boolean checkLock(String groupType, String itemId, int accessCount,
			boolean release) {
		ActionlockInfo info = getOne(groupType, itemId, false);
		if (info == null) {
			return false;
		}
		if (info.getAccessCount() >= accessCount) {
			if (release) {
				delete(groupType, itemId);
			}
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean delete(String groupType, String itemId) {
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("group_type", groupType);
		tj.addString("item_id", itemId);
		int c = this.helper.executeDelete(actionlockTableName, tj);

		if (c == 1) {
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean deleteGroup(String groupType) {
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("group_type", groupType);
		int c = this.helper.executeDelete(actionlockTableName, tj);

		if (c > 0) {
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void clean(String groupType) {
		CommonFieldValues tj = new CommonFieldValues();
		if (groupType != null && ValueUtil.notEmpty(groupType)) {
			tj.addString("group_type", groupType);
		}
		tj.addTimestamp("clean_time", new Date(), "<=");
		this.helper.executeDelete(actionlockTableName, tj);
	}
}
