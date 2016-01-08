/*
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.    
 */

package org.opentides.dao.impl;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.opentides.bean.AuditLog;
import org.opentides.bean.BaseEntity;
import org.opentides.dao.AuditLogDao;
import org.opentides.util.DateUtil;
import org.opentides.util.StringUtil;
import org.springframework.stereotype.Repository;

/**
 * Logging DAO for audit tracking.
 * 
 * @author allantan
 * @author gino
 *
 */
@Repository("auditLogDao")
public class AuditLogDaoImpl extends BaseEntityDaoJpaImpl<AuditLog, Long> implements AuditLogDao {	
	
	private static final Logger _log = Logger.getLogger(AuditLogDaoImpl.class);
	
	@Override
	public void logEvent(final String message, final BaseEntity entity, final boolean separateEm) {
//		if(separateEm) {
//			logEvent(message, entity);
//		} else {
//			Long userId = entity.getAuditUserId();
//			String username = entity.getAuditUsername();
//			
//			if (ApplicationStartupListener.isApplicationStarted()) {
//				if (userId==null) {
//					_log.error("No userId specified for audit logging on object ["+entity.getClass().getName()
//							+ "] for message ["+message+"]. Retrieving user from interceptor.");
//					final SessionUser user = SecurityUtil.getSessionUser();
//					userId = user.getId();
//					username = user.getUsername();
//				} 
//			} else {
//				userId = new Long(0);
//				username = "System Evolve";
//			}
//			
//			final AuditLog record = 
//		            new AuditLog(
//		            			message, 
//		            			entity.getId(), 
//		            			entity.getClass(), 
//		                        entity.getReference(),
//		                        userId,
//		                        username);
//			saveEntityModel(record);
//		}
	}
	
	/**
	 * Saves the log event into the database.
	 * @param shortMessage
	 * @param message
	 * @param entity
	 */
	public static void logEvent(final String message, final BaseEntity entity) { 		
//		Long userId = entity.getAuditUserId();
//		String username = entity.getAuditUsername();
//		
//		final SessionUser user = SecurityUtil.getSessionUser();
//		if (ApplicationStartupListener.isApplicationStarted() && user != null) {
//			if (userId==null) {
//				_log.error("No userId specified for audit logging on object ["+entity.getClass().getName()
//						+ "] for message ["+message+"]. Retrieving user from interceptor.");
//				userId = user.getId();
//				username = user.getUsername();
//			} 
//		} else {
//			userId = new Long(0);
//			username = "System Evolve";
//		}
//		
//    	final EntityManager em = DatabaseUtil.getEntityManager();
//		try {
//			em.getTransaction().begin();
//		     final AuditLog record = 
//	            new AuditLog(
//	            			message, 
//	            			entity.getId(), 
//	            			entity.getClass(), 
//	                        entity.getReference(),
//	                        userId,
//	                        username); 
//			em.persist(record);
//			em.flush(); 
//			em.getTransaction().commit();
//		} finally {
//			if(em != null && em.isOpen()) {
//				em.close(); 
//			}
//		}
    }
	
	
    /* (non-Javadoc)
	 * @see com.ideyatech.core.persistence.impl.BaseEntityDAOJpaImpl#appendClauseToExample(com.ideyatech.core.bean.BaseEntity, boolean)
	 */
	@Override
	protected String appendOrderToExample(final AuditLog example) {
		return "order by createDate desc";
	}
	
	@Override
	protected String appendClauseToExample(final AuditLog example, final boolean exactMatch) {
		final StringBuilder append = new StringBuilder("");
		if (!StringUtil.isEmpty(append.toString())){
			append.append(" and ");
		}
		append.append(" obj.message is not null ");
		//append.append(" and ");
		//append.append(" obj.message <> '' ");
		if (example.getStartDate() != null){
			if (!StringUtil.isEmpty(append.toString())){
				append.append(" and ");
			}
			final Date startDateNoTime = DateUtil.removeTime(example.getStartDate());
			example.setStartDateForSearch(startDateNoTime);
			append.append(" obj.createDate >= :startDateForSearch ");
		}
		
		if (example.getEndDate() != null){
			if (!StringUtil.isEmpty(append.toString())){
				append.append(" and ");
			}
			final Date endDateNoTime = DateUtil.removeTime(DateUtils.addDays(example.getEndDate(), 1));
			example.setEndDateForSearch(endDateNoTime);
			append.append(" obj.createDate <= :endDateForSearch ");
		}

		if(!StringUtil.isEmpty(example.getLogAction())){
			if (!StringUtil.isEmpty(append.toString())){
				append.append(" and ");
			}
			append.append(" obj.message like '%").append(example.getLogAction()).append("%' ");
		}
		return append.toString();
	}
	
}
