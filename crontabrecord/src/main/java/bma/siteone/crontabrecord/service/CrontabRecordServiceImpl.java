package bma.siteone.crontabrecord.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import bma.common.langutil.core.ToStringUtil;
import bma.siteone.crontabrecord.po.CrontabTaskInfo;
import bma.siteone.crontabrecord.po.CrontabTaskInfo.CrontabRunStatus;

public class CrontabRecordServiceImpl implements CrontabRecordService {
	private static final Logger logger = LoggerFactory.getLogger(CrontabRecordService.class);
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void startCrontabTask(CrontabTaskInfo info) {
		CrontabTaskRecord record = getRecord(info.geteName());
		if (record == null) {
			record = new CrontabTaskRecord();
			record.seteName(info.geteName());
			record.setcName(info.getcName());
			record.setServiceName(info.getServiceName());
			record.setDescription(info.getDescription());
		}
		record.setStartTime((int) (new Date().getTime() / 1000));
		record.setStatus(CrontabRunStatus.RUNNING);
		record.setElapsedTime(0);
		record.setLastRunTime(0);
		addOrUpdateRecord(record);
	}

	public void endCrontabTaskWhenSuccessfully(CrontabTaskInfo info, int elapsedTime) {
		CrontabTaskRecord record = getRecord(info.geteName());
		if (record == null) {
			throw new RuntimeException("can not find record.eName=" + info.geteName());
		}
		record.setStatus(CrontabRunStatus.SUCCESSFUL);
		record.setElapsedTime(elapsedTime);
		record.setLastRunTime((int) (new Date().getTime() / 1000));
		addOrUpdateRecord(record);
	}

	public void endCrontabTaskWhenFailed(CrontabTaskInfo info, int elapsedTime) {
		CrontabTaskRecord record = getRecord(info.geteName());
		if (record == null) {
			throw new RuntimeException("can not find record.eName=" + info.geteName());
		}
		record.setStatus(CrontabRunStatus.FAILED);
		record.setElapsedTime(elapsedTime);
		record.setLastRunTime((int) (new Date().getTime() / 1000));
		addOrUpdateRecord(record);
	}

	private void addOrUpdateRecord(CrontabTaskRecord record) {
		try {
			StringBuilder sb = new StringBuilder();
			sb
					.append("insert into crontab_run_time(eName,cName,service,cronTimeDes,status,start_time,elapsed_time,last_run_time) VALUES('");
			sb.append(record.geteName()).append("','");
			sb.append(record.getcName()).append("','");
			sb.append(record.getServiceName()).append("','");
			sb.append(record.getDescription()).append("',");
			sb.append(record.getStatus().ordinal()).append(",");
			sb.append(record.getStartTime()).append(",");
			sb.append(record.getElapsedTime()).append(",");
			sb.append(record.getLastRunTime()).append(") ON DUPLICATE KEY UPDATE status=").append(
					record.getStatus().ordinal());
			sb.append(",start_time=").append(record.getStartTime());
			sb.append(",elapsed_time=").append(record.getElapsedTime());
			sb.append(",last_run_time=").append(record.getLastRunTime());
			jdbcTemplate.execute(sb.toString());
			logger.info("record information " + ToStringUtil.toString(record));
		} catch (Exception e) {
			logger.error("record information error.", e);
		}
	}

	private CrontabTaskRecord getRecord(String eName) {
		String sql = "select * from crontab_run_time where eName='" + eName + "'";
		List<CrontabTaskRecord> list = jdbcTemplate.query(sql, new RowMapper<CrontabTaskRecord>() {
			@Override
			public CrontabTaskRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
				CrontabTaskRecord record = new CrontabTaskRecord();
				record.seteName(rs.getString("eName"));
				record.setcName(rs.getString("cName"));
				record.setServiceName(rs.getString("service"));
				record.setDescription(rs.getString("cronTimeDes"));
				record.setStatus(CrontabRunStatus.values()[rs.getInt("status")]);
				record.setStartTime(rs.getInt("start_time"));
				record.setElapsedTime(rs.getInt("elapsed_time"));
				record.setLastRunTime(rs.getInt("last_run_time"));
				return record;
			}
		});
		if (CollectionUtils.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	private class CrontabTaskRecord extends CrontabTaskInfo {
		/**
		 *运行状态
		 */
		private CrontabRunStatus status;

		/**
		 * 开始运行时间(s的时间戳)
		 */
		private int startTime;
		/**
		 * 消耗时间（ms）
		 */
		private int elapsedTime;
		/**
		 * 运行结束时间(s的时间戳)
		 */
		private int lastRunTime;

		public CrontabRunStatus getStatus() {
			return status;
		}

		public void setStatus(CrontabRunStatus status) {
			this.status = status;
		}

		public int getStartTime() {
			return startTime;
		}

		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}

		public int getElapsedTime() {
			return elapsedTime;
		}

		public void setElapsedTime(int elapsedTime) {
			this.elapsedTime = elapsedTime;
		}

		public int getLastRunTime() {
			return lastRunTime;
		}

		public void setLastRunTime(int lastRunTime) {
			this.lastRunTime = lastRunTime;
		}

	}
}
