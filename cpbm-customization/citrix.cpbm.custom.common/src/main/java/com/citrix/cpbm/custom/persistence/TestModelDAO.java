/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.custom.persistence;

import com.citrix.cpbm.custom.model.TestModel;
import com.vmops.persistence.GenericDAO;

public interface TestModelDAO extends GenericDAO<TestModel> {

  /**
   * Returns an address given its Id.
   * 
   * @param domainId the domain id.
   * @return the project tied to the supplied domain ID.
   */
  public TestModel findById(Long id);

}
