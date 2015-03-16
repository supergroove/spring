package com.user.dao;

import java.sql.Connection;
import java.util.List;

import com.user.domain.User;

public interface UserDao {
	public void add(User user);
	public User get(String id);
	public List<User> getAll();
	public void deleteAll();
	public int getCount();
	public void update(User user1);
}

/*
public class UserDao {

	private JdbcTemplate jdbcTemplate;
	
	private RowMapper<User> userMapper = new RowMapper<User> () {

		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			return user;
		}
		
	};
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void add(final User user) throws SQLException {		
		this.jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)", user.getId(), user.getName(), user.getPassword());
	}

	public User get(String id) throws SQLException {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?", 
				new Object[] {id},
				this.userMapper);
	}   
	
	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id",
				this.userMapper);
	}
	
	public void deleteAll() throws SQLException {
		this.jdbcTemplate.update("delete from users");
	} 
	
	public int getCount() throws SQLException {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
	
}
*/