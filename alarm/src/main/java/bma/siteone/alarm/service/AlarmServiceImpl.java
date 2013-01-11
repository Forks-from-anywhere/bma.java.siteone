package bma.siteone.alarm.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import bma.common.jdbctemplate.JdbcTemplateHelper;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.jdbc.CommonFieldValues;
import bma.siteone.alarm.thrift.TAlarm;
import bma.siteone.alarm.thrift.TAlarmQueryForm;

public class AlarmServiceImpl implements AlarmService {

	private String alarmRecordTable = "alarm_record";

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AlarmServiceImpl.class);

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
		this.setHelper(new JdbcTemplateHelper(jdbcTemplate));
	}

	public class IAlarmRecordTableMapper implements RowMapper<TAlarm> {

		public TAlarm mapRow(ResultSet rs, int index) throws SQLException {
			TAlarm alarm = new TAlarm();
			alarm.setContent(rs.getString("content"));
			alarm.setEndtime(rs.getInt("endtime"));
			alarm.setFrequency(rs.getInt("frequency"));
			alarm.setId(rs.getInt("id"));
			alarm.setLevel(rs.getInt("level"));
			alarm.setOp_time(DateTimeUtil.formatDateTime(new Date(rs.getTimestamp("op_time").getTime())));
			alarm.setOp_user(rs.getString("op_user"));
			alarm.setStarttime(rs.getInt("starttime"));
			alarm.setStatus(rs.getInt("status"));
			alarm.setStype1(rs.getString("stype1"));
			alarm.setStype2(rs.getString("stype2"));
			alarm.setSystem(rs.getString("system"));
			alarm.setTimes(rs.getInt("times"));
			alarm.setType(rs.getInt("type"));
			return alarm;
		}
	}

	@Override
	public int createTAlarm(TAlarm alarm) throws TException {
		CommonFieldValues fvs = new CommonFieldValues();
		Map<String,Object> alarmMap = alarm2Map(alarm);
		for (String key : alarmMap.keySet()) {
			//这里设置创建时间
			if(alarmMap.get(key) instanceof String){
				if(key.equals("op_time")){
					fvs.addTimestamp("op_time",new Date());
				}else{
					fvs.addString(key , (String)alarmMap.get(key));
				}
			}else if(alarmMap.get(key) instanceof Integer){
				fvs.addInt(key , (Integer)alarmMap.get(key));
			}
		}
		Number id = (Number) helper.executeInsert(alarmRecordTable, fvs, "id");
		
		return id.intValue(); 
	}

	@Override
	public List<TAlarm> queryAlarm(TAlarmQueryForm alarmQueryForm, int page, int pageSize, Map<String, String> orders)
			throws TException {
		String limit = getLimit(page, pageSize);
		String tj = getCondition(alarmQueryForm);
		String order = getOrder(orders);
		String sql = "SELECT * FROM " + alarmRecordTable + tj + order + limit;
		log.debug(sql);
		List<TAlarm> alarmList = jdbcTemplate.query(sql, new IAlarmRecordTableMapper());
		return alarmList;
	}

	@Override
	public int queryAlarmCount(TAlarmQueryForm alarmQueryForm) throws TException {
		String tj = getCondition(alarmQueryForm);
		String sql = "SELECT count(1) FROM " + alarmRecordTable + tj;
		log.debug(sql);
		return jdbcTemplate.queryForInt(sql);
	}

	@Override
	public List<TAlarm> queryAlarmByIds(List<Integer> ids) throws TException {
		if(ids.isEmpty()){
			return null;
		}
		String tj = " WHERE 1 ";
		if(ids.size() == 1){
			tj += " AND id = " + ids.get(0);
		}else{
			StringBuilder sb = new StringBuilder();
			Iterator<Integer> i = ids.iterator();
			for (;;) {
			    Integer e = i.next();
			    sb.append(e);
			    if (! i.hasNext())
					break;
			    sb.append(", ");
			}
			tj += " AND id in ( " +sb.toString()+ ")" ;
		}		
		String sql = "SELECT * FROM " + alarmRecordTable + tj ;
		log.debug(sql);
		List<TAlarm> alarmList = jdbcTemplate.query(sql, new IAlarmRecordTableMapper());
		return alarmList;
	}

	@Override
	public int queryAlarmByIdsCount(List<Integer> ids) throws TException {
		if(ids.isEmpty()){
			return 0;
		}
		String tj = " WHERE 1 ";
		if(ids.size() == 1){
			tj += " AND id = " + ids.get(0);
		}else{
			StringBuilder sb = new StringBuilder();
			Iterator<Integer> i = ids.iterator();
			for (;;) {
			    Integer e = i.next();
			    sb.append(e);
			    if (! i.hasNext())
					break;
			    sb.append(", ");
			}
			tj += " AND id in ( " +sb.toString()+ ")" ;
		}
		String sql = "SELECT count(1) FROM " + alarmRecordTable + tj;
		log.debug(sql);
		return jdbcTemplate.queryForInt(sql);
	}

	@Override
	public void updateTAlarmStatusByIds(List<Integer> ids, int status) throws TException {
		if(ids.isEmpty()){
			return ;
		}
		String tj = " WHERE 1 ";
		if(ids.size() == 1){
			tj += " AND id = " + ids.get(0);
		}else{
			StringBuilder sb = new StringBuilder();
			Iterator<Integer> i = ids.iterator();
			for (;;) {
			    Integer e = i.next();
			    sb.append(e);
			    if (! i.hasNext())
					break;
			    sb.append(", ");
			}
			tj += " AND id in ( " +sb.toString()+ ")" ;
		}
		String sql = "UPDATE "+alarmRecordTable+" set status = " + status + tj;
		log.debug(sql);
		jdbcTemplate.execute(sql);
	}

	@Override
	public void deleteHistoryByOptime(Date opTime) throws TException {
		if(opTime == null){
			return ;
		}
		String tj = "";
		tj = " WHERE 1 AND " ;
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		tj +=  " op_time <= '" + simple.format(opTime) + "'";
		String sql = "DELETE FROM " +alarmRecordTable + tj;
		log.debug(sql);
		jdbcTemplate.execute(sql);
	}

	@Override
	public void deleteTAlarmByIds(List<Integer> ids) throws TException {
		if(ids.isEmpty()){
			return ;
		}
		String tj = " WHERE 1 ";
		if(ids.size() == 1){
			tj += " AND id = " + ids.get(0);
		}else{
			StringBuilder sb = new StringBuilder();
			Iterator<Integer> i = ids.iterator();
			for (;;) {
			    Integer e = i.next();
			    sb.append(e);
			    if (! i.hasNext())
					break;
			    sb.append(", ");
			}
			tj += " AND id in ( " +sb.toString()+ ")" ;
		}
		String sql = "DELETE FROM "+alarmRecordTable + tj;
		log.debug(sql);
		jdbcTemplate.execute(sql);
	}

	public void setHelper(JdbcTemplateHelper helper) {
		this.helper = helper;
	}

	public JdbcTemplateHelper getHelper() {
		return helper;
	}

	/**
	 * 构造限制查询条数
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	private String getLimit(int page, int pageSize) {
		String limit = "";
		if (pageSize == 0) {
			pageSize = 20;
		}
		if (page == 0) {
			page = 1;
		}
		limit = " LIMIT " + (page - 1) * pageSize + "," + pageSize;
		return limit;
	}

	/**
	 * sql排列语句order by
	 * 
	 * @param array
	 *            $orders
	 * @return type
	 */
	private String getOrder(Map<String, String> orders) {
		String order = "";
		if (orders != null && orders.size() > 0) {
			for (String key : orders.keySet()) {
				if (orders.get(key) == null || orders.get(key).equals("")) {
					continue;
				}
				if (order.equals("")) {
					order += key + " " + orders.get(key);
				} else {
					order = order + " , " + key + " " + orders.get(key);
				}
			}

			order = " ORDER BY " + order;
		}
		return order;
	}

	/**
	 * 灵活构造查询条件
	 * 
	 * @param alarmQueryForm
	 * @return
	 * @throws TException
	 */
	private String getCondition(TAlarmQueryForm alarmQueryForm) throws TException {
		String tj = " WHERE 1 ";
		Map<String,Object> queryMap = queryForm2Map(alarmQueryForm);
		String opt = "";
		String lk = "";
		String between = "";
		for (String key : queryMap.keySet()) {
			// 如果是string 则使用like
			if (queryMap.get(key) instanceof String) {
				if (lk.equals("")) {
					lk += key + " LIKE '%" + queryMap.get(key) + "%'";
				} else {
					lk = " AND " + key + " LIKE " + queryMap.get(key);
				}
			} else {
				// 如果是int则使用等于
				//如果是`starttime`或`endtime`则放入表中
				if(key.equals("starttime") || key.equals("endtime")){
					if(!between.equals("")){
						between += " AND " ;
					}
					if(key.equals("starttime")){
						between += "  op_time >= " + queryMap.get(key);
					}else if(key.equals("endtime")){
						between += "  op_time <= " + queryMap.get(key);
					}
				}else{
					if(opt.equals("")) {
						opt += key + "='" + queryMap.get(key) + "'";
					} else {
						opt = " AND " + key + "'=" + queryMap.get(key) + "'";
					}
				}
			}
		}
		if (!opt.equals("")) {
			tj += " AND (" + opt + ")";
		}
		if (!lk.equals("")) {
			tj += " AND (" + lk + ")";
		}
		if (!between.equals("")) {
			tj += " AND (" + between + ")";
		}
		return tj;
	}

	public static Map<String,Object> queryForm2Map(TAlarmQueryForm alarmQueryForm) {
		Map<String,Object> queryMap = new HashMap<String,Object>();
		if(alarmQueryForm.getContent() != null && !alarmQueryForm.getContent().equals("")){
			queryMap.put("content", alarmQueryForm.getContent());
		}
		if(alarmQueryForm.getEndtime() != 0){
			queryMap.put("endtime", alarmQueryForm.getEndtime());
		}
		if(alarmQueryForm.getLevel() != 0 ){
			queryMap.put("level", alarmQueryForm.getLevel());
		}
		if(alarmQueryForm.getStarttime() != 0 ){
			queryMap.put("starttime", alarmQueryForm.getStarttime());
		}
		if(alarmQueryForm.getStatus() != 0 ){
			queryMap.put("status", alarmQueryForm.getStatus());
		}
		if(alarmQueryForm.getStype1() != null && !alarmQueryForm.getStype1().equals("")){
			queryMap.put("stype1", alarmQueryForm.getStype1());
		}
		if(alarmQueryForm.getStype2() != null && !alarmQueryForm.getStype2().equals("")){
			queryMap.put("stype2", alarmQueryForm.getStype2());
		}
		if(alarmQueryForm.getSystem() != null && !alarmQueryForm.getSystem().equals("")){
			queryMap.put("system", alarmQueryForm.getSystem());
		}
		if(alarmQueryForm.getType() != 0 ){
			queryMap.put("type", alarmQueryForm.getType());
		}
		return queryMap;
	}

	public static Map<String,Object> alarm2Map(TAlarm alarm) {
		Map<String,Object> alarmMap = new HashMap<String,Object>();
		if(alarm.getSystem() != null && !alarm.getSystem().equals("")){
			alarmMap.put("system", alarm.getSystem());
		}
		if(alarm.getStype1() != null && !alarm.getStype1().equals("")){
			alarmMap.put("stype1", alarm.getStype1());
		}
		if(alarm.getStype2() != null && !alarm.getStype2().equals("")){
			alarmMap.put("stype2", alarm.getStype2());
		}
		if(alarm.getContent() != null && !alarm.getContent().equals("")){
			alarmMap.put("content", alarm.getContent());
		}
		if(alarm.getOp_time() != null && !alarm.getOp_time().equals("")){
			alarmMap.put("op_time", alarm.getOp_time());
		}else{
			alarmMap.put("op_time", "");
		}
		if(alarm.getOp_user() != null && !alarm.getOp_user().equals("")){
			alarmMap.put("op_user", alarm.getOp_user());
		}else{
			alarmMap.put("op_user", "system");
		}
		if(alarm.getId() != 0){
			alarmMap.put("id", alarm.getId());
		}
		if(alarm.getType() != 0){
			alarmMap.put("type", alarm.getType());
		}
		if(alarm.getLevel() != 0){
			alarmMap.put("level", alarm.getLevel());
		}
		if(alarm.getStarttime() != 0){
			alarmMap.put("starttime", alarm.getStarttime());
		}
		if(alarm.getEndtime() != 0){
			alarmMap.put("endtime", alarm.getEndtime());
		}
		if(alarm.getFrequency() != 0){
			alarmMap.put("frequency", alarm.getFrequency());
		}
		if(alarm.getTimes() != 0){
			alarmMap.put("times", alarm.getTimes());
		}
		if(alarm.getStatus() != 0){
			alarmMap.put("status", alarm.getStatus());
		}
		return alarmMap;
	}


}
