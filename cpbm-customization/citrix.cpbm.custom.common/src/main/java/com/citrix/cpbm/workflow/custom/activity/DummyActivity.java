/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/ 
package com.citrix.cpbm.workflow.custom.activity;

import com.citrix.cpbm.core.workflow.model.BusinessTransaction;
import com.citrix.cpbm.workflow.activity.AbstractActivity;
import com.citrix.cpbm.workflow.activity.WorkflowActivityExecutionException;
import com.vmops.service.AuthorityService;

public class DummyActivity extends AbstractActivity{
  
  private static boolean complete = false;
  
  private AuthorityService authorityService;

  public AuthorityService getAuthorityService() {
    return authorityService;
  }

  public void setAuthorityService(AuthorityService authorityService) {
    this.authorityService = authorityService;
  }

  @Override
  protected Boolean isActivityComplete(BusinessTransaction businessTransaction, Record record)
      throws WorkflowActivityExecutionException {
    return complete;
  }

  @Override
  protected Status doActivity(BusinessTransaction businessTransaction, Record activityPayload)
      throws WorkflowActivityExecutionException {
    complete = true;
    return Status.IN_PROGRESS;
  }

}
