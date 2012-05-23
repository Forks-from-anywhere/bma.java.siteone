package bma.siteone.comments.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.core.Pager;
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.jdbc.CommonFieldValues;
import bma.common.langutil.jdbc.JdbcTypeMapper;
import bma.common.langutil.jdbc.StatementHelper;
import bma.siteone.comments.po.CommentInfo;
import bma.siteone.comments.po.CommentPoint;

@Transactional
public class CommentsServiceImpl implements CommentsService {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CommentsServiceImpl.class);

	private CommentsServiceImpl() {
		super();
	}

	private String commentPointTableName = "comment_point";
	private String commentTableName = "comment_info";

	/**
	 * JdbcTemplate对象
	 */
	private transient JdbcTemplateHelper helper;
	private JdbcTemplate jdbcTemplate;

	private ICache<Integer, CommentInfo> commentCache;
	private ICache<Integer, CommentPointCacheItem> commentPointCache;
	private ICache<String, Integer> commentPointNameCache;

	public ICache<String, Integer> getCommentPointNameCache() {
		return commentPointNameCache;
	}

	public void setCommentPointNameCache(
			ICache<String, Integer> commentPointNameCache) {
		this.commentPointNameCache = commentPointNameCache;
	}

	public String getCommentPointTableName() {
		return commentPointTableName;
	}

	public void setCommentPointTableName(String commentPointTableName) {
		this.commentPointTableName = commentPointTableName;
	}

	public String getCommentTableName() {
		return commentTableName;
	}

	public void setCommentTableName(String commentTableName) {
		this.commentTableName = commentTableName;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.helper = new JdbcTemplateHelper(jdbcTemplate);
	}

	public ICache<Integer, CommentInfo> getCommentCache() {
		return commentCache;
	}

	public void setCommentCache(ICache<Integer, CommentInfo> commentCache) {
		this.commentCache = commentCache;
	}

	public ICache<Integer, CommentPointCacheItem> getCommentPointCache() {
		return commentPointCache;
	}

	public void setCommentPointCache(
			ICache<Integer, CommentPointCacheItem> commentPointCache) {
		this.commentPointCache = commentPointCache;
	}

	public class CommentRowMapper implements RowMapper<CommentInfo> {

		public CommentInfo mapRow(ResultSet rs, int index) throws SQLException {
			CommentInfo info = new CommentInfo();
			info.setContent(rs.getString("content"));
			info.setCreateTime(new Date(rs.getTimestamp("create_time")
					.getTime()));
			info.setHideFlag(rs.getInt("hide_flag"));
			info.setId(rs.getInt("id"));
			info.setIp(rs.getString("ip"));
			info.setNeedAuth(rs.getInt("need_auth"));
			info.setOppose(rs.getInt("oppose"));
			info.setPointId(rs.getInt("point_id"));
			info.setReplyId(rs.getInt("reply_id"));
			info.setReserve1(rs.getInt("reserve1"));
			info.setReserve2(rs.getInt("reserve2"));
			info.setReserve3(rs.getString("reserve3"));
			info.setReserve4(rs.getString("reserve4"));
			info.setStatus(rs.getInt("status"));
			info.setSupport(rs.getInt("support"));
			info.setUserId(rs.getInt("user_id"));
			info.setUserName(rs.getString("user_name"));
			return info;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int createComment(CommentForm form, CommentPointForm pointForm) {

		int commentPointId = form.getPointId();
		if (commentPointId == 0) {
			CommentPoint point = getCommentPoint(pointForm.getName());
			if (point != null) {
				commentPointId = point.getId();
			}
		}
		if (commentPointId == 0) {
			commentPointId = createCommentPoint(pointForm);
		}

		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addInt("point_id", commentPointId);
		fvs.addInt("reply_id", form.getReplyId());
		fvs.addInt("user_id", form.getUserId());
		fvs.addString("user_name", form.getUserName());
		fvs.addString("content", form.getContent());
		fvs.addString("ip", form.getIp());
		fvs.addInt("status", form.getStatus());
		fvs.addInt("need_auth", form.getNeedAuth());
		fvs.addInt("hide_flag", form.getHideFlag());
		fvs.addInt("reserve1", form.getReserve1());
		fvs.addInt("reserve2", form.getReserve2());
		if (ValueUtil.notEmpty(form.getReserve3())) {
			fvs.addString("reserve3", form.getReserve3());
		}
		if (ValueUtil.notEmpty(form.getReserve4())) {
			fvs.addString("reserve4", form.getReserve4());
		}
		fvs.addSysdate("create_time");

		Number id = helper.executeInsert(commentTableName, fvs, "id");
		addComment(commentPointId, 1);
		return id.intValue();
	}

	protected boolean isUpdate(String name, List<String> fields) {
		if (fields == null)
			return true;
		for (String s : fields) {
			if (StringUtil.equalsIgnoreCase(name, s))
				return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean updateComment(int id, CommentForm form, List<String> fields) {
		CommentInfo comment = getComment(id);
		if (comment == null) {
			return false;
		}
		// TODO
		CommonFieldValues fvs = new CommonFieldValues();
		if (isUpdate("content", fields)) {
			fvs.addString("content", form.getContent());
		}
		if (isUpdate("status", fields)) {
			fvs.addInt("status", form.getStatus());
		}
		if (isUpdate("need_auth", fields)) {
			fvs.addInt("need_auth", form.getNeedAuth());
		}
		if (isUpdate("hide_flag", fields)) {
			fvs.addInt("hide_flag", form.getHideFlag());
		}
		if (isUpdate("reserve1", fields)) {
			fvs.addInt("reserve1", form.getReserve1());
		}
		if (isUpdate("reserve2", fields)) {
			fvs.addInt("reserve2", form.getReserve2());
		}
		if (isUpdate("reserve3", fields)) {
			fvs.addString("reserve3", form.getReserve3());
		}
		if (isUpdate("reserve4", fields)) {
			fvs.addString("reserve4", form.getReserve4());
		}

		if (fvs.empty())
			return false;

		CommonFieldValues tj = new CommonFieldValues();
		tj.addInt("id", id);

		boolean r = helper.executeUpdate(commentTableName, fvs, tj) == 1;
		if (r) {
			clearCommentCache(id);
			// clearPointCache(commentPoint.getName());
			// clearPointCache(point);
		}
		return r;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean deleteComment(int id) {
		CommentInfo comment = getComment(id);
		if (comment == null) {
			return false;
		}
		int pointId = comment.getPointId();
		int c = jdbcTemplate.update("DELETE FROM " + commentTableName
				+ " WHERE id = ?", id);
		if (c == 1) {
			clearCommentCache(id);
			addComment(pointId, -1);
		}
		return true;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public CommentInfo getComment(int id) {
		CommentInfo info = commentCache.get(id);
		if (info != null) {
			return info;
		}
		info = helper.selectOne(commentTableName, "id", id,
				new CommentRowMapper());
		if (info != null) {
			commentCache.put(id, info);
		}
		return info;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public PagerResult<CommentInfo> searchComment(SearchCommentForm form) {

		if (ValueUtil.notEmpty(form.getCache())) {
			int pid = 0;
			if (form.getPointId() > 0) {
				pid = form.getPointId();
			} else if (ValueUtil.notEmpty(form.getPoint())) {
				CommentPoint cp = getCommentPoint(form.getPoint());
				if (cp != null) {
					pid = cp.getId();
				}
			}
			if (pid > 0) {
				CommentPointCacheItem item = commentPointCache.get(pid);
				if (item != null) {
					PagerResult<CommentInfo> r = item.get(form.getCache());
					if (r != null)
						return r;
				}
			}
		}

		StringBuffer buf = new StringBuffer(256);
		CommonFieldValues cptj = null;
		CommonFieldValues tj = new CommonFieldValues();

		SearchCommentPointForm pform = new SearchCommentPointForm();
		pform.setName(form.getPoint());
		pform.setTitle(form.getSubject());
		pform.setUrl(form.getUrl());
		if (form.getPointId() > 0) {
			tj.addInt("point_id", form.getPointId());
			cptj = new CommonFieldValues();
		} else {
			cptj = buildCommentPointSearchQuery(pform);
		}
		if (form.getReplyId() > 0) {
			tj.addInt("reply_id", form.getReplyId());
		}
		if (ValueUtil.notEmpty(form.getUserName())) {
			tj.addLikeString("user_name", form.getUserName());
		}
		if (form.getUserId() > 0) {
			tj.addInt("user_id", form.getUserId());
		}
		if (ValueUtil.notEmpty(form.getIp())) {
			tj.addLikeString("ip", form.getIp());
		}
		if (ValueUtil.notEmpty(form.getContent())) {
			tj.addLikeString("content", form.getContent());
		}
		String tempTime = form.getStartTime();
		if (ValueUtil.notEmpty(tempTime)) {
			Date date = DateTimeUtil.parseDateTime(tempTime, null);
			if (date != null) {
				tj.addTimestamp("create_time", date, ">=");
			}
		}
		tempTime = form.getEndTime();
		if (ValueUtil.notEmpty(tempTime)) {
			Date date = DateTimeUtil.parseDateTime(tempTime, null);
			if (date != null) {
				tj.addTimestamp("create_time", date, "<=");
			}
		}
		if (form.getStatus() >= 0) {
			tj.addInt("status", form.getStatus());
		}
		if (form.getNeedAuth() >= 0) {
			tj.addInt("need_auth", form.getNeedAuth());
		}
		if (form.getHide() >= 0) {
			tj.addInt("hide_flag", form.getHide());
		}

		buf.append("FROM ").append(commentTableName).append(" c");
		if (cptj.notEmpty()) {
			buf.append(" INNER JOIN ").append(commentPointTableName)
					.append(" cp");
			buf.append(" On c.point_id = cp.id");
		}
		if (!(cptj.empty() && tj.empty())) {
			buf.append(" WHERE ");
		} else {
			buf.append(" ");
		}
		boolean and = false;
		if (cptj.notEmpty()) {
			and = StatementHelper.JDBC.buildCriteria(buf, cptj, "cp", and);
		}
		StatementHelper.JDBC.buildCriteria(buf, tj, "c", and);

		if (log.isDebugEnabled()) {
			log.debug("search: {}", buf);
		}

		final CommonFieldValues fcptj = cptj;
		final CommonFieldValues ftj = tj;
		int total = jdbcTemplate.query("SELECT COUNT(*) " + buf.toString(),
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						int idx = 1;
						idx = StatementHelper.JDBC.buildParam(fcptj, ps, idx,
								JdbcTypeMapper.O);
						idx = StatementHelper.JDBC.buildParam(ftj, ps, idx,
								JdbcTypeMapper.O);
					}
				}, new ResultSetExtractor<Integer>() {
					@Override
					public Integer extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						rs.next();
						return rs.getInt(1);
					}
				});

		final Pager pager = new Pager(total, form.getPage(), form.getPageSize());
		String sql = "SELECT * " + buf.toString() + " ORDER BY " + "c."
				+ form.orderStr("create_time", "DESC") + " LIMIT ?,?";
		List<CommentInfo> list = jdbcTemplate.query(sql,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						int idx = 1;
						idx = StatementHelper.JDBC.buildParam(fcptj, ps, idx,
								JdbcTypeMapper.O);
						idx = StatementHelper.JDBC.buildParam(ftj, ps, idx,
								JdbcTypeMapper.O);
						ps.setInt(idx++, pager.getStart());
						ps.setInt(idx++, pager.getPageSize());
					}
				}, new CommentRowMapper());

		PagerResult<CommentInfo> result = new PagerResult<CommentInfo>();
		result.setPager(pager);
		result.setResult(list);

		if (ValueUtil.notEmpty(form.getCache())) {
			int pid = 0;
			if (form.getPointId() > 0) {
				pid = form.getPointId();
			} else if (ValueUtil.notEmpty(form.getPoint())) {
				CommentPoint cp = getCommentPoint(form.getPoint());
				if (cp != null) {
					pid = cp.getId();
				}
			}
			if (pid > 0) {
				CommentPointCacheItem item = commentPointCache.get(pid);
				if (item == null) {
					item = new CommentPointCacheItem();
					commentPointCache.put(pid, item);
				}
				item.put(form.getCache(), result);
			}
		}
		return result;

	}

	public class CommentPointRowMapper implements RowMapper<CommentPoint> {

		public CommentPoint mapRow(ResultSet rs, int index) throws SQLException {
			CommentPoint info = new CommentPoint();
			info.setCommentAmount(rs.getInt("comment_amount"));
			info.setCreateTime(new Date(rs.getTimestamp("create_time")
					.getTime()));
			info.setId(rs.getInt("id"));
			info.setLastCommentTime(new Date(rs.getTimestamp(
					"last_comment_time").getTime()));
			info.setLastUpdateTime(new Date(rs.getTimestamp("last_update_time")
					.getTime()));
			info.setName(rs.getString("name"));
			info.setReserve1(rs.getInt("reserve1"));
			info.setReserve2(rs.getInt("reserve2"));
			info.setReserve3(rs.getString("reserve3"));
			info.setReserve4(rs.getString("reserve4"));
			info.setStatus(rs.getInt("status"));
			info.setTitle(rs.getString("title"));
			info.setUrl(rs.getString("url"));

			return info;
		}
	}

	protected CommentPoint getFromCache(int id) {
		CommentPointCacheItem item = commentPointCache.get(id);
		if (item != null) {
			return item.getPoint();
		}
		return null;
	}

	protected void putToCache(CommentPoint cp) {
		CommentPointCacheItem item = commentPointCache.get(cp.getId());
		if (item == null) {
			item = new CommentPointCacheItem();
			commentPointCache.put(cp.getId(), item);
		}
		item.setPoint(cp);
		commentPointNameCache.put(cp.getName(), cp.getId());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public CommentPoint getCommentPoint(int id) {
		CommentPoint info = getFromCache(id);
		if (info != null) {
			return info;
		}
		info = helper.selectOne(commentPointTableName, "id", id,
				new CommentPointRowMapper());
		if (info != null) {
			putToCache(info);
		}
		return info;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public CommentPoint getCommentPoint(String name) {
		Integer pointId = commentPointNameCache.get(name);
		CommentPoint info = null;
		if (pointId != null) {
			if (pointId.intValue() < 0)
				return null;
			info = getFromCache(pointId.intValue());
		}
		if (info == null) {
			info = helper.selectOne(commentPointTableName, "name", name,
					new CommentPointRowMapper());
		}
		if (info != null) {
			putToCache(info);
		} else {
			commentPointNameCache.put(name, -1);
		}
		return info;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int createCommentPoint(CommentPointForm form) {
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("name", form.getName());
		fvs.addString("url", form.getUrl());
		fvs.addString("title", form.getTitle());
		fvs.addInt("reserve1", form.getReserve1());
		fvs.addInt("reserve2", form.getReserve2());
		if (ValueUtil.notEmpty(form.getReserve3())) {
			fvs.addString("reserve3", form.getReserve3());
		}
		if (ValueUtil.notEmpty(form.getReserve4())) {
			fvs.addString("reserve4", form.getReserve4());
		}
		fvs.addSysdate("last_comment_time");
		fvs.addSysdate("create_time");
		fvs.addSysdate("last_update_time");
		fvs.addConstInt("status", 1);

		Number id = (Number) helper.executeInsert(commentPointTableName, fvs,
				"id");
		int r = id.intValue();
		commentPointNameCache.remove(form.getName());
		commentCache.remove(r);
		return r;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean updateCommentPoint(int id, CommentPointForm form) {
		CommentPoint commentPoint = getCommentPoint(id);
		if (commentPoint == null) {
			return false;
		}
		String point = form.getName();
		if (!ObjectUtil.equals(commentPoint.getName(), point)
				&& getCommentPoint(point) != null) {
			throw new IllegalArgumentException("invalid point name[" + point
					+ "]");
		}
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("name", form.getName());
		fvs.addString("url", form.getUrl());
		fvs.addString("title", form.getTitle());
		fvs.addInt("reserve1", form.getReserve1());
		fvs.addInt("reserve2", form.getReserve2());
		if (ValueUtil.notEmpty(form.getReserve3())) {
			fvs.addString("reserve3", form.getReserve3());
		}
		if (ValueUtil.notEmpty(form.getReserve4())) {
			fvs.addString("reserve4", form.getReserve4());
		}
		fvs.addSysdate("last_update_time");

		CommonFieldValues tj = new CommonFieldValues();
		tj.addInt("id", id);

		boolean r = helper.executeUpdate(commentPointTableName, fvs, tj) == 1;
		if (r) {
			clearPointCache(commentPoint.getId());
		}
		return r;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean deleteCommentPoint(int id) {
		CommonFieldValues tj = new CommonFieldValues();
		tj.addInt("id", id);
		int c = helper.executeDelete(commentPointTableName, tj);
		if (c == 0) {
			return false;
		}

		CommonFieldValues tj2 = new CommonFieldValues();
		tj2.addInt("point_id", id);
		helper.executeDelete(commentTableName, tj2);

		clearPointCache(id);
		clearAllCommentCache();

		return true;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public PagerResult<CommentPoint> searchCommentPoint(
			SearchCommentPointForm form) {
		CommonFieldValues tj = buildCommentPointSearchQuery(form);
		int total = helper.selectCount(commentPointTableName, tj);
		Pager pager = new Pager(total, form.getPage(), form.getPageSize());
		List<CommentPoint> list = helper.selectLimit(commentPointTableName, tj,
				form.orderStr("create_time", "DESC"), pager.getStart(),
				pager.getPageSize(), new CommentPointRowMapper());

		PagerResult<CommentPoint> result = new PagerResult<CommentPoint>();
		result.setPager(pager);
		result.setResult(list);
		return result;

	}

	protected CommonFieldValues buildCommentPointSearchQuery(
			SearchCommentPointForm form) {
		CommonFieldValues tj = new CommonFieldValues();
		if (ValueUtil.notEmpty(form.getUrl())) {
			tj.addLikeString("url", form.getUrl());
		}
		if (ValueUtil.notEmpty(form.getName())) {
			tj.addLikeString("name", form.getName());
		}
		if (ValueUtil.notEmpty(form.getTitle())) {
			tj.addLikeString("title", form.getTitle());
		}
		return tj;
	}

	protected void addComment(int pointId, int c) {
		String sql = "UPDATE "
				+ commentPointTableName
				+ " SET comment_amount = comment_amount + ?,last_comment_time = NOW() WHERE id = ?";
		int r = jdbcTemplate.update(sql, c, pointId);
		if (r > 0) {
			clearPointCache(pointId);
		}
	}

	public void clearPointCache(int id) {
		CommentPointCacheItem item = commentPointCache.remove(id);
		if (item != null) {
			CommentPoint cp = item.getPoint();
			if (cp != null) {
				commentPointNameCache.remove(cp.getName());
			}
		}
	}

	public void clearCommentCache(int id) {
		commentCache.remove(id);
	}

	public void clearAllCommentCache() {
		commentCache.clear();
	}

	@Override
	public void clearCache(CacheForm form) {
		if (form.getCommentPointId() > 0) {
			if (log.isDebugEnabled()) {
				log.debug("clear comment point cache - {}",
						form.getCommentPointId());
			}
			if (ValueUtil.notEmpty(form.getCache())) {
				CommentPointCacheItem item = commentPointCache.get(form
						.getCommentPointId());
				if (item != null) {
					item.clear(form.getCache());
				}
			} else {
				clearPointCache(form.getCommentPointId());
			}
		}
		if (ValueUtil.notEmpty(form.getCommentPointName())) {
			CommentPoint cp = getCommentPoint(form.getCommentPointName());
			if (cp != null) {
				if (log.isDebugEnabled()) {
					log.debug("clear comment point cache,{}", cp.getId());
				}
				if (ValueUtil.notEmpty(form.getCache())) {
					CommentPointCacheItem item = commentPointCache.get(cp
							.getId());
					if (item != null) {
						item.clear(form.getCache());
					}
				} else {
					clearPointCache(cp.getId());
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("clear comment point cache,skip {}",
							form.getCommentPointName());
				}
			}
		}
		if (form.getCommentId() > 0) {
			if (log.isDebugEnabled()) {
				log.debug("clear comment cache - {}", form.getCommentId());
			}
			commentCache.remove(form.getCommentId());
		}
	}
}
