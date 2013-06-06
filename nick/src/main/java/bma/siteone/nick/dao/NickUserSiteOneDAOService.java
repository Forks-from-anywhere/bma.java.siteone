package bma.siteone.nick.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import bma.common.langutil.core.ToStringUtil;
import bma.siteone.nick.po.NickUser;

public class NickUserSiteOneDAOService extends BaseNickDAOService {

	private static final Logger logger = LoggerFactory.getLogger(NickUserSiteOneDAOService.class);

	private String nickUserTable = "nick_user";

	public List<NickUser> getNickUserList(List<Long> uids) {
		StringBuffer uidStr = null;
		for (Long uid : uids) {
			if (uidStr == null) {
				uidStr = new StringBuffer();
				uidStr.append("(" + uid);
			} else {
				uidStr.append("," + uid);
			}
		}
		uidStr.append(")");
		String sql = " select * from  " + nickUserTable + " WHERE uid in  " + uidStr.toString();
		logger.debug("[getNickUserList] sql={}", sql);
		List<NickUser> list = getJdbcTemplate().query(sql, new NickUserMapper());
		return list;
	}

	/**
	 * 批量更新过期时间
	 * 
	 * @param overdueTime
	 */
	public void updateNickUserOverdueTime(List<Long> uids, int overdueTime) {
		StringBuffer uidStr = null;
		for (Long uid : uids) {
			if (uidStr == null) {
				uidStr = new StringBuffer();
				uidStr.append("(" + uid);
			} else {
				uidStr.append("," + uid);
			}
		}
		uidStr.append(")");
		String sql = " UPDATE " + nickUserTable + " SET  overdue_time = " + overdueTime + " ,modify_time = "
				+ new GregorianCalendar().getTimeInMillis() / 1000 + " WHERE overdue_time < " + overdueTime
				+ " AND uid in  " + uidStr.toString();
		logger.debug("[updateNickUserOverdueTime] sql={}", sql);
		jdbcTemplate.update(sql);
	}

	public class NickUserMapper implements RowMapper<NickUser> {

		public NickUser mapRow(ResultSet rs, int index) throws SQLException {
			NickUser c = new NickUser();
			c.setUid(rs.getLong("uid"));// 用户id
			c.setNick(rs.getString("nick"));// 昵称
			c.setOverdue_time(rs.getInt("overdue_time"));// 过期时间
			c.setModify_time(rs.getInt("modify_time"));// 修改时间
			return c;
		}
	}

	/**
	 * 批量更新用户昵称
	 * 
	 * @param list
	 */
	public void updateNickUserList(final List<NickUser> list) {
		if (logger.isDebugEnabled()) {
			logger.debug("[updateNickUserList] [" + ToStringUtil.fieldReflect(list) + "]");
		}

		if (list == null || list.isEmpty()) {
			return;
		}
		BatchPreparedStatementSetter pss = new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				NickUser t = list.get(i);
				ps.setLong(1, t.getUid());
				ps.setString(2, t.getNick());
				ps.setInt(3, t.getOverdue_time());
				ps.setLong(4, new GregorianCalendar().getTimeInMillis() / 1000);
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		};
		String batchInsertSql = "insert into "
			+ nickUserTable
			+ "(uid,nick,overdue_time,modify_time) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE nick = VALUES(nick) , overdue_time = VALUES(overdue_time) , modify_time = VALUES(modify_time)";
		jdbcTemplate.batchUpdate(batchInsertSql, pss);
	}
}
