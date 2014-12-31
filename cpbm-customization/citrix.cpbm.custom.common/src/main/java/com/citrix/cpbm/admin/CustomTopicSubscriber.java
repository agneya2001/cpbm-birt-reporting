/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.admin;

import org.apache.log4j.Logger;

import com.citrix.cpbm.subscriber.AbstractTopicSubscriber;
import com.vmops.event.AccountConverted;
import com.vmops.event.CloudServiceEvent;
import com.vmops.event.CreditCardFraudCheckEvent;
import com.vmops.event.DepositReceived;
import com.vmops.event.DeviceFraudDetectionEvent;
import com.vmops.event.EmailVerified;
import com.vmops.event.MissingProductEvent;
import com.vmops.event.PasswordResetRequest;
import com.vmops.event.PaymentInfoChangeEvent;
import com.vmops.event.ProvisionResourceFailedEvent;
import com.vmops.event.SubscriptionActivation;
import com.vmops.event.SubscriptionChangeEvent;
import com.vmops.event.SubscriptionCreation;
import com.vmops.event.SubscriptionDeletion;
import com.vmops.event.SubscriptionTermination;
import com.vmops.event.TenantActivation;
import com.vmops.event.TenantLock;
import com.vmops.event.TenantReactivation;
import com.vmops.event.TenantSuspension;
import com.vmops.event.TenantTermination;
import com.vmops.event.TrialExpiryWarning;
import com.vmops.event.TrialSuspended;
import com.vmops.event.UserCreation;
import com.vmops.event.UserDeactivateEmail;
import com.vmops.event.UserDeletion;
import com.vmops.event.UtilitySubscriptionEvent;
import com.vmops.event.VerifyEmailRequest;

/**
 * A default implementation of TopicSubscriber which receives all CPBM events
 * 
 * @author Manish
 */
public class CustomTopicSubscriber extends AbstractTopicSubscriber {

  private static Logger logger = Logger.getLogger(CustomTopicSubscriber.class);

  /**
   * Receive subscription creation events
   * 
   * @param event
   */
  @Override
  public void receive(SubscriptionCreation event) {
    logger.info("###Received subscription creation event with id:" + event.getSubscriptionId());
  }

  /**
   * Receive subscription deletion events
   * 
   * @param event
   */
  @Override
  public void receive(SubscriptionDeletion event) {
    logger.info("###Received subscription deletion event with id:" + event.getSubscription().getId());
  }

  /**
   * Receive subscription termination events
   * 
   * @param event
   */
  @Override
  public void receive(SubscriptionTermination event) {
    logger.info("###Received subscription termination event with id:" + event.getSubscriptionId());
  }

  /**
   * This receives the cs related events
   * 
   * @param event -- cs event
   */
  @Override
  public void receive(CloudServiceEvent event) {
    logger.debug("Cloudstack event received: reference type:" + event.getCloudServiceReferenceType() + " reference id:"
        + event.getCloudServiceReferenceId());
  }

  @Override
  public void receive(TenantActivation event) {
    logger.debug("Received a tenant activation event with account id:" + event.getAccountId());
  }

  @Override
  public void receive(TenantReactivation event) {
    logger.debug("Received a tenant Reactivation event with account id:" + event.getAccountId());
  }

  @Override
  public void receive(TenantLock event) {
    logger.debug("Received a tenant lock event with account id:" + event.getAccountId());
  }

  @Override
  public void receive(TenantSuspension event) {
    logger.debug("Received a tenant suspension event with account id:" + event.getAccountId());
  }

  @Override
  public void receive(TenantTermination event) {
    logger.debug("Received a tenant termination event with account id:" + event.getAccountId());
  }

  @Override
  public void receive(UserCreation event) {
    logger.debug("Received a user creation event with username:" + event.getUsername());
  }

  @Override
  public void receive(UserDeletion event) {
    logger.debug("Received a user deletion event with username:" + event.getUsername());
  }

  @Override
  public void receive(SubscriptionChangeEvent event) {
    logger.info("###Received SubscriptionChangeEvent  old subscription id:" + event.getOldSubscriptionId()
        + " new Utility Subscription Id:" + event.getNewSubscriptionId());
  }

  @Override
  public void receive(UtilitySubscriptionEvent event) {
    logger.info("###Received UtilitySubscriptionEvent  new utility subscription id:" + event.getSubscriptionId());
  }

  @Override
  public void receive(MissingProductEvent event) {
    logger.info("###Received MissingProductEvent ");
  }

  @Override
  public void receive(UserDeactivateEmail event) {
    logger.info("###Received UserDeactivateEvent ");
  }

  @Override
  public void receive(SubscriptionActivation event) {
    logger.info("###Received subscription activation event with id:" + event.getSubscriptionId());
  }

  @Override
  public void receive(AccountConverted event) {
    logger.info("###Received AccountConverted event with tenant uuid:" + event.getTenantUuid());
  }

  @Override
  public void receive(CreditCardFraudCheckEvent event) {
    logger.info("###Received CreditCardFraudCheckEvent event with details:" + event.getDetails());
  }

  @Override
  public void receive(TrialExpiryWarning event) {
    logger.info("###Received TrialExpiryWarning event with trial account id:" + event.getTrialAccountId());
  }

  @Override
  public void receive(TrialSuspended event) {
    logger.info("###Received TrialSuspended event with trial account id:" + event.getTrialAccountId());
  }

  @Override
  public void receive(DeviceFraudDetectionEvent event) {
    logger.info("###Received DeviceFraudDetectionEvent event");
  }

  @Override
  public void receive(ProvisionResourceFailedEvent event) {
    logger.info("###Received ProvisionResourceFailedEvent event for subscription id" + event.getSubscriptionId());
  }

  @Override
  public void receive(PasswordResetRequest event) {
    logger.info("###Received PasswordResetRequest event for user:" + event.getUsername());
  }

  @Override
  public void receive(DepositReceived event) {
    logger.info("###Received DepositReceived event for user:" + event.getUsername() + " with amount:"
        + event.getAmount());
  }

  /*
   * (non-Javadoc)
   * @see com.citrix.cpbm.subscriber.TopicSubscriber#receive(com.vmops.event.PaymentInfoChangeEvent)
   */
  @Override
  public void receive(PaymentInfoChangeEvent event) {
    logger.info("###Received PaymentInfoChangeEvent event for business transaction: " + event.getTransactionId());

  }

  /*
   * (non-Javadoc)
   * @see com.citrix.cpbm.subscriber.TopicSubscriber#receive(com.vmops.event.VerifyEmailRequest)
   */
  @Override
  public void receive(VerifyEmailRequest event) {
    logger.info("###Received VerifyEmailRequest event for user: " + event.getUsername());
  }

  /*
   * (non-Javadoc)
   * @see com.citrix.cpbm.subscriber.TopicSubscriber#receive(com.vmops.event.EmailVerified)
   */
  @Override
  public void receive(EmailVerified event) {
    logger.info("###Received EmailVerified event ");
  }

}
