package com.user.sqlservice.updatable;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.user.sqlservice.SqlNotFoundException;
import com.user.sqlservice.SqlUpdateFailureException;
import com.user.sqlservice.UpdatableSqlRegistry;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry { 
	
	SimpleJdbcTemplate jdbc; 
	TransactionTemplate transactionTemplate;
	
	public void setDataSource(DataSource dataSource) {
		jdbc = new SimpleJdbcTemplate(dataSource);
		transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
	}

	public void registerSql(String key, String sql) {
		jdbc.update("insert into sqlmap(key_, sql_) values(?, ?)", key, sql);
	}

	public String findSql(String key) throws SqlNotFoundException {
		try {
			return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
		}
		catch(EmptyResultDataAccessException e) {
			throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다", e);
		}
	}

	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		int affected = jdbc.update("update sqlmap set sql_ = ? where key_ = ?", sql, key);
		if(affected == 0)
			throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다");
	}

	public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				for(Map.Entry<String, String> entry : sqlmap.entrySet()) {
					updateSql(entry.getKey(), entry.getValue());
				}
			}
			
		});
		
	}

}
