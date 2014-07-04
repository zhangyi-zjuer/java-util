package com.zy.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeUtility;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.log4j.Logger;

/**
 * 发送邮件,使用时先调用init初始化
 * 
 * @author 张翼
 * @email zhangyi.zjuer@gmail.com
 * @date 2013年12月11日
 */
public class EmailUtil {
	private static final Logger logger = Logger.getLogger(EmailUtil.class);
	private static String _smtpServer; // smtp服务器
	private static String _username; // 发送者用户名
	private static String _password; // 发送者密码
	private static String _from; // 发送者名称
	private static String[] _defaultTo; // 默认接收者

	/**
	 * 
	 * @param smtpServer
	 *            发送者邮箱smtp
	 * @param username
	 *            发送者邮箱地址
	 * @param password
	 *            发送者邮箱密码
	 * @param from
	 *            发送者名称
	 * @param defaultTo
	 *            默认接收者
	 */
	public static void init(String smtpServer, String username,
			String password, String from, String[] defaultTo) {

		if (from == null) {
			from = username;
		}

		_smtpServer = smtpServer;
		_username = username;
		_password = password;
		_from = from;
		_defaultTo = defaultTo;
	}

	/**
	 * 邮件初始化, 配置文件包含<br>
	 * mail.smtpserver<br>
	 * mail.username<br>
	 * mail.password<br>
	 * mail.from<br>
	 * mail.default.to: 默认接收者
	 * 
	 * @param configure
	 */
	public static void init(PropertiesConfiguration configure) {

		EmailUtil.init(configure.getString("mail.smtpserver"),
				configure.getString("mail.username"),
				configure.getString("mail.password"),
				configure.getString("mail.from"),
				configure.getStringArray("mail.default.to"));
	}

	/**
	 * 邮件初始化, 配置文件包含<br>
	 * mail.smtpserver<br>
	 * mail.username<br>
	 * mail.password<br>
	 * mail.from<br>
	 * mail.default.to: 默认接收者
	 * 
	 * @param configureFile
	 */
	public static void init(String configureFile) throws ConfigurationException {
		PropertiesConfiguration configure = ConfigUtil.load(configureFile);
		init(configure);
	}

	/**
	 * 异步发送邮件，使用线程发送邮件
	 * 
	 * @param subject
	 * @param msg
	 */
	public static void sendEmailAsync(String subject, String msg) {
		sendEmailAsync(subject, msg, _defaultTo);
	}

	/**
	 * 异步发送邮件，使用线程发送邮件
	 * 
	 * @param subject
	 * @param msg
	 */
	public static void sendEmailAsync(String subject, String msg, String[] to) {
		new EmailUtil().new SendEmailThread(to, subject, msg).start();
	}

	/**
	 * 异步发送带附件邮件，使用线程发送邮件
	 * 
	 * @param subject
	 * @param msg
	 * @param attachment
	 */
	public static void sendEmailAsync(String subject, String msg,
			String attachment, String[] to) {
		List<String> attachments = new ArrayList<String>();
		attachments.add(attachment);
		new EmailUtil().new SendEmailThread(to, subject, msg, attachments)
				.start();
	}

	/**
	 * 异步发送带附件邮件，使用线程发送邮件
	 * 
	 * @param subject
	 * @param msg
	 * @param attachment
	 */
	public static void sendEmailAsync(String subject, String msg,
			String attachment) {
		sendEmailAsync(subject, msg, attachment, _defaultTo);
	}

	/**
	 * 异步发送带附件邮件，使用线程发送邮件
	 * 
	 * @param subject
	 * @param msg
	 * @param attachments
	 */
	public static void sendEmailAsync(String subject, String msg,
			List<String> attachments, String[] to) {
		new EmailUtil().new SendEmailThread(to, subject, msg, attachments)
				.start();
	}

	/**
	 * 异步发送带附件邮件，使用线程发送邮件
	 * 
	 * @param subject
	 * @param msg
	 * @param attachments
	 */
	public static void sendEmailAsync(String subject, String msg,
			List<String> attachments) {
		sendEmailAsync(subject, msg, attachments, _defaultTo);
	}

	/**
	 * 发送邮件，同步发送
	 * 
	 * @param subject
	 * @param msg
	 */
	public static void sendEmail(String subject, String msg, String[] to) {
		new EmailUtil().new SendEmailThread(to, subject, msg).run();
	}

	/**
	 * 发送邮件，同步发送
	 * 
	 * @param subject
	 * @param msg
	 */
	public static void sendEmail(String subject, String msg) {
		sendEmail(subject, msg, _defaultTo);
	}

	/**
	 * 同步发送带附件邮件
	 * 
	 * @param subject
	 * @param msg
	 * @param attachment
	 */
	public static void sendEmail(String subject, String msg, String attachment,
			String[] to) {
		List<String> attachments = new ArrayList<String>();
		attachments.add(attachment);
		new EmailUtil().new SendEmailThread(to, subject, msg, attachments)
				.start();
	}

	/**
	 * 同步发送带附件邮件
	 * 
	 * @param subject
	 * @param msg
	 * @param attachment
	 */
	public static void sendEmail(String subject, String msg, String attachment) {
		sendEmail(subject, msg, attachment, _defaultTo);
	}

	/**
	 * 同步发送带附件邮件
	 * 
	 * @param subject
	 * @param msg
	 * @param attachments
	 */
	public static void sendEmail(String subject, String msg,
			List<String> attachments, String[] to) {
		new EmailUtil().new SendEmailThread(to, subject, msg, attachments)
				.start();
	}

	/**
	 * 同步发送带附件邮件
	 * 
	 * @param subject
	 * @param msg
	 * @param attachments
	 */
	public static void sendEmail(String subject, String msg,
			List<String> attachments) {
		sendEmail(subject, msg, attachments, _defaultTo);
	}

	class SendEmailThread extends Thread {
		private String[] to;
		private String subject;
		private String msg;
		private List<String> attachments;

		public SendEmailThread(String[] to, String subject, String msg) {
			super();
			this.to = to;
			this.subject = subject;
			this.msg = msg;

		}

		public SendEmailThread(String[] to, String subject, String msg,
				List<String> attachments) {
			super();
			this.to = to;
			this.subject = subject;
			this.msg = msg;
			this.attachments = attachments;

		}

		@Override
		public void run() {
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(_smtpServer);
			email.setAuthentication(_username, _password);// 邮件服务器验证：用户名/密码
			email.setCharset("UTF-8");// 必须放在前面，否则乱码
			try {
				String[] receivers = to;
				for (String receiver : receivers) {
					email.addTo(receiver.trim());
				}
				email.setFrom(_username, _from);
				email.setSubject(subject);
				email.setMsg(msg);

				// 添加附件
				if (!CollectionUtils.isEmpty(attachments)) {
					for (String attachment : attachments) {
						EmailAttachment attach = new EmailAttachment();
						File f = new File(attachment);
						attach.setName(MimeUtility.encodeText(f.getName())); // 防止附件名称乱码
						attach.setPath(attachment);
						email.attach(attach);
					}
				}
				email.send();
				logger.info("Send Email to:\t" + StringUtils.join(to, ","));
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
}
