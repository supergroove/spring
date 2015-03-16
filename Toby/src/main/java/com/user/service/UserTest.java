package com.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.user.dao.UserDao;
import com.user.domain.Level;
import com.user.domain.User;
import com.user.service.UserService.TestUserService;
import com.user.service.UserService.TestUserServiceException;

public class UserTest {
	
	User user;
	
	@Before
	public void setUp() {
		user = new User();
	}
	
	@Test()
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if (level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if (level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
	
//	@Test
//	public void upgradeAllOrNothing() {
//		UserService testUserService = new TestUserService(users.get(3).getId());
//		testUserService.setUserDao(this.userDao); 
//		userDao.deleteAll();
//		
//		for(User user : users)
//			userDao.add(user);
//		
//		try {
//			
//		}
//		catch(TestUserServiceException e) {
//			
//		}
//		
//		checkLevelUpgraded(users.get(1), false);
//	}
	
	@After
	public void exit() {
		System.out.println("Exit");
	}

}
