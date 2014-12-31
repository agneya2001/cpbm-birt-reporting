/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.custom.service;

import java.util.Date;
import java.util.List;

import com.citrix.cpbm.custom.model.UserLoginAudit;
import com.vmops.model.User;

/**
 * The Interface UserLoginAuditService.
 * 
 * @author Shiv Prasad Khillar
 */
public interface UserLoginAuditService {

  /**
   * Find user login audit records.
   * 
   * @param date the date
   * @return the list
   */
  public List<UserLoginAudit> findUserLoginAuditRecords(Date date);

  /**
   * Find user login audit records.
   * 
   * @param user the user
   * @return the list
   */
  public List<UserLoginAudit> findUserLoginAuditRecords(User user);

  public void saveAudit(UserLoginAudit userLoginAudit);

}
