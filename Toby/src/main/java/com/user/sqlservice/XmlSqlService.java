package com.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.user.dao.UserDao;
import com.user.sqlservice.jaxb.SqlType;
import com.user.sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader { 
	
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	private Map<String, String> sqlMap = new HashMap<String, String> ();
	private String sqlmapFile;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
//	public XmlSqlService() {
//		String contextPath = Sqlmap.class.getPackage().getName();
//		
//		try {
//			JAXBContext context = JAXBContext.newInstance(contextPath);
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");
//			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
//			
//			for(SqlType sql : sqlmap.getSql()) {
//				sqlMap.put(sql.getKey(), sql.getValue());
//			}
//		}
//		catch(JAXBException e) {
//			throw new RuntimeException(e);
//		}
//	} 
	
	@PostConstruct
	public void loadSql() {
		this.sqlReader.read(this.sqlRegistry);
	}

	public String getSql(String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		}
		catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}
	}

	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null)
			throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다");
		else
			return sql;
	}

	public void read(SqlRegistry sqlRegistry) {
		String contextPath = Sqlmap.class.getPackage().getName();
		
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
		}
		catch(JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
