package com.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.user.dao.UserDao;
import com.user.domain.Level;
import com.user.domain.User;

public class UserService {
	
//	private DataSource dataSource;
	
	private PlatformTransactionManager transactionManager;
	private MailSender mailSender;
	
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void upgradeLevels() throws Exception {
		
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
//		PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
//		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
//		TransactionSynchronizationManager.initSynchronization();
//		Connection c = DataSourceUtils.getConnection(dataSource);
//		c.setAutoCommit(false);
		
		try {
			List<User> users = userDao.getAll();
			
			for(User user : users) {
				if(canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}
//			c.commit();
			this.transactionManager.commit(status);
		}
		catch(Exception e) {
//			c.rollback(); 
			this.transactionManager.rollback(status);
			throw e;
		} 
//		finally {
//			DataSourceUtils.releaseConnection(c, dataSource);
//			TransactionSynchronizationManager.unbindResource(this.dataSource);
//			TransactionSynchronizationManager.clearSynchronization();
//		}
	}
	
	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		
		switch(currentLevel) {
			case BASIC:
				return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER:
				return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD:
				return false;
			default:
				throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	protected void upgradeLevel(User user) {  
		user.upgradeLevel();
		userDao.update(user); 
		sendUpgradeEMail(user);
	} 
	
	private void sendUpgradeEMail(User user) { 
		
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//		mailSender.setHost("mail.server.com");
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());
		
		this.mailSender.send(mailMessage);
		
//		Properties props = new Properties();
//		props.put("mail.smtp.host",  "mail.ksug.org");
//		Session s = Session.getInstance(props, null); 
//		
//		MimeMessage message = new MimeMessage(s);
//		
//		try {
//			message.setFrom(new InternetAddress("useradmin@ksug.org"));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
//			message.setSubject("Upgrade 안내");
//			message.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드 되었습니다");
//			Transport.send(message);
//		}
//		catch(AddressException e) {
//			throw new RuntimeException(e);
//		}
//		catch(MessagingException e) {
//			throw new RuntimeException(e);
//		}
//		catch(UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	public void add(User user) {
		if(user.getLevel() == null)
			user.setLevel(Level.BASIC);
		userDao.add(user);
	}
	
	static class TestUserService extends UserService {
		private String id;
		
		protected TestUserService(String id) {
			this.id = id;
		} 
		
		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id))
				throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	static class TestUserServiceException extends RuntimeException {
		
	}
}

