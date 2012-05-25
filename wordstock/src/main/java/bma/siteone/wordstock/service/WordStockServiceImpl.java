package bma.siteone.wordstock.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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
import bma.common.langutil.jdbc.CommonFieldValues;
import bma.siteone.wordstock.po.WordInfo;

@Transactional
public class WordStockServiceImpl implements WordStockService {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(WordStockServiceImpl.class);

	private WordStockServiceImpl() {
		super();
	}

	private String wordstockTableName = "so_wordstock";

	/**
	 * JdbcTemplate对象
	 */
	private transient JdbcTemplateHelper helper;
	private JdbcTemplate jdbcTemplate;

	private ICache<String, List<WordItem>> infoCache;

	public String getWordstockTableName() {
		return wordstockTableName;
	}

	public void setWordstockTableName(String iptablesTableName) {
		this.wordstockTableName = iptablesTableName;
	}

	public ICache<String, List<WordItem>> getInfoCache() {
		return infoCache;
	}

	public void setInfoCache(ICache<String, List<WordItem>> infoCache) {
		this.infoCache = infoCache;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.helper = new JdbcTemplateHelper(jdbcTemplate);
	}

	public class InfoRowMapper implements RowMapper<WordInfo> {

		public WordInfo mapRow(ResultSet rs, int index) throws SQLException {
			WordInfo info = new WordInfo();
			info.setId(rs.getInt("id"));
			info.setGroupType(rs.getString("group_type"));
			info.setTitle(rs.getString("title"));
			info.setWords(rs.getString("words"));
			if (info.getWords() == null) {
				info.setWords("");
			}
			info.setType(rs.getString("type"));
			return info;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int create(WordInfo info) {
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("group_type", info.getGroupType());
		fvs.addString("title", info.getTitle());
		fvs.addString("words", info.getWords());
		fvs.addString("type", info.getType());

		Number id = helper.executeInsert(wordstockTableName, fvs, "id");
		clearCache(info.getGroupType());
		return id.intValue();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean update(WordInfo info) {
		WordInfo old = getOne(info.getId());
		if (old == null) {
			return false;
		}

		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("group_type", info.getGroupType());
		fvs.addString("title", info.getTitle());
		fvs.addString("words", info.getWords());
		fvs.addString("type", info.getType());

		CommonFieldValues tj = new CommonFieldValues();
		tj.addInt("id", info.getId());

		boolean r = helper.executeUpdate(wordstockTableName, fvs, tj) == 1;
		if (r) {
			clearCache(old.getGroupType());
			clearCache(info.getGroupType());
		}
		return r;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public boolean delete(int id) {
		WordInfo old = getOne(id);
		if (old == null) {
			return false;
		}

		int c = jdbcTemplate.update("DELETE FROM " + wordstockTableName
				+ " WHERE id = ?", id);
		if (c == 1) {
			clearCache(old.getGroupType());
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public WordInfo getOne(int id) {
		return helper.selectOne(wordstockTableName, "id", id,
				new InfoRowMapper());
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public PagerResult<WordInfo> search(String sql, int page, int pageSize) {
		sql = sql.replaceAll("TABLE", wordstockTableName);
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
		List<WordInfo> list = jdbcTemplate.query(sql2,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						int idx = 1;
						ps.setInt(idx++, pager.getStart());
						ps.setInt(idx++, pager.getPageSize());
					}
				}, new InfoRowMapper());

		PagerResult<WordInfo> result = new PagerResult<WordInfo>();
		result.setPager(pager);
		result.setResult(list);
		return result;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public List<WordInfo> listInfo(final String groupId) {
		return jdbcTemplate.query("SELECT * FROM " + wordstockTableName
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
				+ wordstockTableName, new PreparedStatementSetter() {
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
	public String match(String groupId, String content) {
		List<WordItem> ilist = this.infoCache.get(groupId);
		if (ilist == null) {
			List<WordInfo> polist = listInfo(groupId);
			ilist = new ArrayList<WordItem>();
			for (WordInfo info : polist) {
				String type = info.getType();
				String str = info.getWords();
				String sp[] = StringUtil.tokenSplit(str, "\n\r,;");
				String as[] = sp;
				int i = 0;

				WordItem item = new WordItem();
				item.setInfo(info);
				if (TYPE_TEXT.equals(type)) {
					List<String> list = Arrays.asList(as);
					item.setWordList(list);
				} else {
					List<Pattern> list = new ArrayList<Pattern>(as.length);
					for (int j = as.length; i < j; i++) {
						String ss = as[i].trim();
						if (TYPE_NORMAL.equals(type)) {
							Pattern p = StringUtil
									.commonPatternToRegexPattern(ss);
							if (p != null) {
								list.add(p);
							}
						} else {
							Pattern p = Pattern.compile(ss);
							if (p != null) {
								list.add(p);
							}
						}
					}
					item.setPatternList(list);
				}
				ilist.add(item);
			}
			if (log.isDebugEnabled()) {
				log.debug("{} -> {}", groupId, ilist);
			}
			this.infoCache.put(groupId, ilist);
		}
		for (WordItem item : ilist) {
			List<String> wlist = item.getWordList();
			if (wlist != null) {
				for (String w : wlist) {
					if (content.indexOf(w) != -1) {
						if (log.isDebugEnabled()) {
							log.debug("{} match {}", content, w);
						}
						return w;
					}
				}
			} else {
				List<Pattern> plist = item.getPatternList();
				for (Pattern p : plist) {
					if (p.matcher(content).find()) {
						if (log.isDebugEnabled()) {
							log.debug("{} match {}", content, p);
						}
						return p.toString();
					}
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("{} no-match", content);
		}
		return null;
	}

	public void clearCache(String groupType) {
		infoCache.remove(groupType);
	}
}
