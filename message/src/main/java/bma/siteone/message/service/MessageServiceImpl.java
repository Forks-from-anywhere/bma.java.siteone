package bma.siteone.message.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import bma.common.jdbctemplate.JdbcTemplateHelper;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.Pager;
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.jdbc.CommonFieldValues;
import bma.siteone.message.po.MessageInfo;

@Transactional
public class MessageServiceImpl implements MessageService{
	
	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(MessageServiceImpl.class);
	
	private MessageServiceImpl(){
		super();
	}
	
	private String siteoneMessageTableName = "so_message";

	public String getSiteoneMessageTableName() {
		return siteoneMessageTableName;
	}

	public void setSiteoneMessageTableName(String siteoneMessageTableName) {
		this.siteoneMessageTableName = siteoneMessageTableName;
	}
	
	/**
	 * JdbcTemplate对象
	 */
	private transient JdbcTemplateHelper helper;
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.helper = new JdbcTemplateHelper(jdbcTemplate);
	}

	@Override
	public int sendMessage(SendMessageForm sendMessageForm) {
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("app", sendMessageForm.getApp());
		fvs.addInt("type",sendMessageForm.getType());
		fvs.addString("receiver", sendMessageForm.getReceiver());
		fvs.addString("sender", sendMessageForm.getSender());
		fvs.addString("title", sendMessageForm.getTitle());
		fvs.addString("content", sendMessageForm.getContent());
		fvs.addSysdate("send_time");
		fvs.addInt("is_read",0);
		
		Number n = helper.executeInsert(siteoneMessageTableName, fvs, null);
		return n.intValue();
	}

	@Override
	public int deleteMessages(List<Integer> ids) {
		if(!ids.isEmpty()){
			StringBuilder sql = new StringBuilder();
			sql.append(" delete from ").append(siteoneMessageTableName).append(" where id in ");
			sql.append("(");
			sql.append(bma.common.langutil.core.StringUtil.join(ids, ","));
			sql.append(")");
			return jdbcTemplate.update(sql.toString());
		}else{
			return 0;
		}
		
	}

	@Override
	public PagerResult<MessageInfo> searchReceiverMessage(
			SearchReceiverMessageForm searchForm) {
		
		CommonFieldValues tj = new CommonFieldValues();
		if (ValueUtil.notEmpty(searchForm.getReceiver())) {
			tj.addString("receiver", searchForm.getReceiver());
		}
		if (ValueUtil.notEmpty(searchForm.getApp())) {
			tj.addString("app", searchForm.getApp());
		}
		
		int total = helper.selectCount(siteoneMessageTableName, tj);
		
		Pager pager = new Pager(total, searchForm.getPage(), searchForm.getPageSize());
		List<MessageInfo> list = helper.selectLimit(siteoneMessageTableName, tj,
				" send_time DESC ", pager.getStart(),
				pager.getPageSize(), new MessageInfoRowMapper());
		
		PagerResult<MessageInfo> result = new PagerResult<MessageInfo>();
		result.setPager(pager);
		result.setResult(list);
		
		return result;
		
	}
	
	public class MessageInfoRowMapper implements RowMapper<MessageInfo> {
		
		public MessageInfo mapRow(ResultSet rs, int index) throws SQLException {
			MessageInfo m = new MessageInfo();
			m.setId(rs.getInt("id"));
			m.setApp(rs.getString("app"));
			m.setType(rs.getInt("type"));
			m.setReceiver(rs.getString("receiver"));
			m.setSender(rs.getString("sender"));
			m.setTitle(rs.getString("title"));
			m.setContent(rs.getString("content"));
			m.setSendTime(new Date(rs.getTimestamp("send_time").getTime()));
			m.setIsRead(rs.getInt("is_read"));
			
			return m;
		}
	}

	@Override
	public int setMessagesRead(List<Integer> ids) {
		if(!ids.isEmpty()){
			StringBuilder sql = new StringBuilder();
			sql.append(" update ").append(siteoneMessageTableName).append(" set is_read=1 ").append(" where id in ");
			sql.append("(");
			sql.append(bma.common.langutil.core.StringUtil.join(ids, ","));
			sql.append(")");
			return jdbcTemplate.update(sql.toString());
		}else{
			return 0;
		}
		
	}

	private Map<String, Integer> deleteExpireConfig;
	
	public Map<String, Integer> getDeleteExpireConfig() {
		return deleteExpireConfig;
	}

	public void setDeleteExpireConfig(Map<String, Integer> deleteExpireConfig) {
		this.deleteExpireConfig = deleteExpireConfig;
	}

	@Override
	public boolean deleteExpireMessages() {
		
		if(deleteExpireConfig != null && !deleteExpireConfig.isEmpty()){
			
			List<String> appList = new ArrayList<String>();
			//按照应用的配置删除过期信息
			for (Entry<String, Integer> ec : deleteExpireConfig.entrySet()) {
				appList.add("'"+ec.getKey()+"'");
				
				Date today = new Date();
				long p = ec.getValue().longValue()*24*60*60*1000;
				
				String dt = DateTimeUtil.formatDate(new Date(today.getTime()-p));
				String sql = "delete from "+siteoneMessageTableName+" where app ='"+ec.getKey()+"' and send_time< '"+dt+"'";

				jdbcTemplate.execute(sql);
				
			}
			
			//其它没有配置的，默认全部半年180天过期
			Date today = new Date();
			long p = (long) 6*30*24*60*60*1000;
			
			String dt = DateTimeUtil.formatDate(new Date(today.getTime()-p));
			String sql = "delete from "+siteoneMessageTableName+" where app not in ("+bma.common.langutil.core.StringUtil.join(appList, ",")+") and send_time< '"+dt+"'";

			jdbcTemplate.execute(sql);
			
		}else{
			//没有配置，默认全部半年180天过期
			Date today = new Date();
			long p = (long) 6*30*24*60*60*1000;
			
			String dt = DateTimeUtil.formatDate(new Date(today.getTime()-p));
			String sql = "delete from "+siteoneMessageTableName+" where send_time< '"+dt+"'";

			jdbcTemplate.execute(sql);
		}
		
		return true;
	}
	
	private ScheduledExecutorService timer;
	
	public ScheduledExecutorService getTimer() {
		return timer;
	}

	public void setTimer(ScheduledExecutorService timer) {
		this.timer = timer;
	}

	public void init(){
		if(timer == null){
			timer = Executors.newSingleThreadScheduledExecutor();
		}
		timer.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				
				deleteExpireMessages();
				
			}
		}, 1, 24*3600, TimeUnit.SECONDS);
	}
	

}
