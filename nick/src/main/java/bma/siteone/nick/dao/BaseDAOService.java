package bma.siteone.nick.dao;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDAOService {

	protected JdbcTemplate jdbcTemplate;
	
	protected JdbcTemplate jdbcTemplateInfobright;

	public void setJdbcTemplateInfobright(JdbcTemplate jdbcTemplateInfobright) {
		this.jdbcTemplateInfobright = jdbcTemplateInfobright;
	}

	public JdbcTemplate getJdbcTemplateInfobright() {
		return jdbcTemplateInfobright;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
