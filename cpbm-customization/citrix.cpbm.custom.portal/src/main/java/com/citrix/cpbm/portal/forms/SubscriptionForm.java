/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.portal.forms;

import com.citrix.cpbm.access.Subscription;
import com.citrix.cpbm.access.proxy.CustomProxy;

public class SubscriptionForm {

  private Subscription subscription = (Subscription) CustomProxy.newInstance(new com.vmops.model.Subscription());

  public SubscriptionForm() {
    super();
  }

  public SubscriptionForm(Subscription subscription) {
    super();
    this.subscription = subscription;
  }

  public Subscription getSubscription() {
    return subscription;
  }

  public void setSubscription(Subscription subscription) {
    this.subscription = subscription;
  }

}
