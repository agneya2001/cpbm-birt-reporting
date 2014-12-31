/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.custom.persistence;

import java.util.List;

import com.citrix.cpbm.custom.model.UserLoginAudit;
import com.vmops.model.User;
import com.vmops.persistence.GenericDAO;

/**
 * The Interface UserLoginAuditDAO.
 * 
 * @author Shiv Prasad Khillar
 */
public interface UserLoginAuditDAO extends GenericDAO<UserLoginAudit> {

  /**
   * Find login audit records.
   * 
   * @param user the user
   * @return the list
   */
  List<UserLoginAudit> findLoginAuditRecords(User user);

}
