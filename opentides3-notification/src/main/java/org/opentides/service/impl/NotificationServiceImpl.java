/*
 * Copyright 2007-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opentides.service.impl;

import java.io.File;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.opentides.bean.BaseEntity;
import org.opentides.bean.Event;
import org.opentides.bean.Notification;
import org.opentides.bean.Notification.Status;
import org.opentides.dao.NotificationDao;
import org.opentides.eventhandler.EmailHandler;
import org.opentides.service.MailingService;
import org.opentides.util.DateUtil;
import org.opentides.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author allantan
 *
 */

@Service(value = "notificationService")
public class NotificationServiceImpl extends BaseCrudServiceImpl<Notification>
implements NotificationService {

	private static Logger _log = Logger.getLogger(NotificationServiceImpl.class);

	@Value("#{notificationSettings['mail.default-template']}")
	private String mailVmTemplate;
	
	@Value("#{notificationSettings['notification.execute-limit']}")
	private String limit;

	@Autowired
	protected MessageSource messageSource;
	
	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	private NotificationDao notificationDao;
	
	@Autowired
	private EmailHandler emailHandler;
	
	@Autowired 
	private MailingService mailingService;
	
//	public String buildEmailMessage() {
//		Map<String, Object> model = new HashMap<String, Object>();
//		
//		String text = VelocityEngineUtils.mergeTemplateIntoString(
//                velocityEngine, "com/dns/registration-confirmation.vm", "UTF-8", model);
//	}
	
	@Scheduled(fixedDelayString = "${notification.delay}")
	@Transactional
	public void executeNotification() {
		int max = StringUtil.convertToInt(limit,20);
		for (int i=0; i<max; i++) {
			List<Notification> notifications = notificationDao.findNewNotifications(1);
			if (notifications== null || notifications.isEmpty()) 
				break;
			Notification n = notifications.get(0);
			Status status = null;
			boolean processed = false;
			StringBuffer remarks = new StringBuffer();
			try {
				if ("EMAIL".equals(n.getMedium())) {
					n.setStatus(Status.IN_PROCESS.toString());
					notificationDao.saveEntityModel(n);
					// send email
					if (n.getSubject()==null) n.setSubject("");
					if (StringUtil.isEmpty(n.getAttachment())) {
						emailHandler.sendEmail(n.getRecipientReference().split(","), new String [] {n.getEmailCC()}, 
								new String [] {}, n.getEmailReplyTo(), n.getSubject(), n.getMessage());						
					} else {
						// send with attachment
						File attachment = new File(n.getAttachment());
						emailHandler.sendEmail(n.getRecipientReference().split(","), new String [] {n.getEmailCC()}, 
								new String [] {}, n.getEmailReplyTo(), n.getSubject(), n.getMessage(), new File[] {attachment});						
					}

					status=Status.PROCESSED;
					processed = true;
					remarks.append("Email successfully sent to "+n.getRecipientReference()+".\n");
				}
				if ("SMS".equals(n.getMedium())) {
					n.setStatus(Status.IN_PROCESS.toString());
					notificationDao.saveEntityModel(n);
					// send SMS
//					processed = smsService.send(n.getRecipientReference(), n.getMessage());
//					if(processed) {
//						status = Status.PROCESSED;
//						remarks.append("SMS sent to " + n.getRecipientReference() + "\n");
//					} else {
//						status = Status.FAILED;
//					}
				}
				if (processed) {
					if (status!=null) n.setStatus(status.toString());
					n.setRemarks(remarks.toString());				
					notificationDao.saveEntityModel(n);
				}
			} catch (Exception e) {
				_log.error("Error encountered while sending notification", e);
				remarks.append(e.getMessage());
				if (remarks.length()>3999)
					n.setRemarks(remarks.substring(0, 3999));
				else
					n.setRemarks(remarks.toString()+"\n");				
				n.setStatus(Status.FAILED.toString());
				notificationDao.saveEntityModel(n);	 
			}
		}
	}
	
	public void notify(String userId) {
	    Broadcaster b = BroadcasterFactory.getDefault().lookup(userId);
	    if (b!=null) {
	        long nCount = notificationDao.countNewPopup(new Long(userId));
	        List<Notification> notifications = notificationDao.findMostRecentPopup(new Long(userId));
	        StringBuffer response = new StringBuffer("[");
	        response.append(nCount);
	        for (Notification n:notifications) {
	        	response.append(",\"")
	        			.append(n.getMessage())
	        			.append("\"");
	        }
	        response.append("]");
	        b.broadcast(response.toString());
	    }
	}
	
	@Override
	public String buildMessage(Event event, Object[] params) {				
		return messageSource.getMessage(event.getMessageCode(), params, 
				Locale.getDefault());
	}
	
	@Override
	public void triggerEvent(Event event, BaseEntity command) {
		// build the message
		
		// get the recipients
		
		// save as new notification
	}

	@Override
	public long countNewPopup(long userId) {
		return notificationDao.countNewPopup(userId);
	}

	@Override
	public void clearPopup(long userId) {
		notificationDao.clearPopup(userId);
	}

	@Override	
	public String getPopupNotification(long userId) {
		// Let's manually build the json
		StringBuilder jsonBuilder = new StringBuilder();
		List<Notification> notifs = null;
		long count = 0;
		notifs = this.findMostRecentPopup(userId);
		count = this.countNewPopup(userId);
		
		jsonBuilder.append("{\"notifications\":[");
		if (notifs != null) {
			int idx = 0;
			for (Notification n : notifs) {
				if (idx++ > 0) jsonBuilder.append(",");
				jsonBuilder.append("{\"createDate\":\"")
				.append(DateUtil.dateToString(n.getCreateDate(), "MMM dd, yyyy hh:mm a"))
				.append("\",\"message\":\"").append(n.getMessage()).append("\"}");
			}
		}
		
		jsonBuilder.append("],\"notifyCount\":\"")
				   .append(count)
				   .append("\"}");

		return jsonBuilder.toString(); 
	}
	
	@Override
	public List<Notification> findMostRecentPopup(long userId) {
		return notificationDao.findMostRecentPopup(userId);
	}
}
