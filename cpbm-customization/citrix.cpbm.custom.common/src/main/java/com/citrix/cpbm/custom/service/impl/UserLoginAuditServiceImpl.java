/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.custom.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citrix.cpbm.custom.model.UserLoginAudit;
import com.citrix.cpbm.custom.persistence.UserLoginAuditDAO;
import com.citrix.cpbm.custom.service.UserLoginAuditService;
import com.vmops.model.User;

/**
 * The Class UserLoginAuditServiceImpl.
 * 
 * @author Shiv Prasad Khillar
 */
@Service("userLoginAuditService")
public class UserLoginAuditServiceImpl implements UserLoginAuditService {

  /** The user login audit dao. */
  @Autowired
  private UserLoginAuditDAO userLoginAuditDAO;

  /**
   * Find user login audit records.
   * 
   * @param date the date
   * @return the list
   * @see com.citrix.cpbm.custom.service.UserLoginAuditService#findUserLoginAuditRecords(java.util.Date)
   */
  @Override
  @Transactional
  public List<UserLoginAudit> findUserLoginAuditRecords(Date date) {
    Map<String, Object> conditions = new HashMap<String, Object>();
    conditions.put("createdAt", date);
    return userLoginAuditDAO.findByCriteria(null, conditions);
  }

  /**
   * Find user login audit records.
   * 
   * @param user the user
   * @return the list
   * @see com.citrix.cpbm.custom.service.UserLoginAuditService#findUserLoginAuditRecords(com.vmops.model.User)
   */
  @Override
  @Transactional
  public List<UserLoginAudit> findUserLoginAuditRecords(User user) {
    return userLoginAuditDAO.findLoginAuditRecords(user);
  }

  @Override
  @Transactional
  public void saveAudit(UserLoginAudit userLoginAudit) {
    userLoginAuditDAO.save(userLoginAudit);

  }

}
