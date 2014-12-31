/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.custom.persistence.hibernate;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.citrix.cpbm.custom.model.UserLoginAudit;
import com.citrix.cpbm.custom.persistence.UserLoginAuditDAO;
import com.vmops.model.User;
import com.vmops.persistence.hibernate.GenericHibernateDAO;

/**
 * The Class UserLoginAuditDAOImpl.
 * 
 * @author Shiv Prasad Khillar
 */
@Repository("userLoginAuditDAO")
public class UserLoginAuditDAOImpl extends GenericHibernateDAO<UserLoginAudit> implements UserLoginAuditDAO {

  /**
   * Find login audit records.
   * 
   * @param user the user
   * @return the list
   * @see com.citrix.cpbm.custom.persistence.UserLoginAuditDAO#findLoginAuditRecords(com.vmops.model.User)
   */
  @Override
  public List<UserLoginAudit> findLoginAuditRecords(User user) {
    Map<String, Object> conditions = getConditionsTemplate();
    conditions.put("user", user);
    return findByCriteria(null, conditions);
  }

}
