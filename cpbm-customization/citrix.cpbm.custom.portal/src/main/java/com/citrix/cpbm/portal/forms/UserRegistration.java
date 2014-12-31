/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
/**
 * 
 */
package com.citrix.cpbm.portal.forms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.vmops.model.AccountType;
import com.vmops.model.Address;
import com.vmops.model.Country;
import com.vmops.model.CreditCard;
import com.vmops.model.CurrencyValue;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.web.forms.CreditCardType;

/**
 * Backing form object for a user registration.
 * 
 * @author vijay
 */
public class UserRegistration {

  /**
   * The user entry.
   */
  @Valid
  @NotNull
  private com.citrix.cpbm.access.User user = (com.citrix.cpbm.access.User) CustomProxy.newInstance(new User());

  /**
   * The disposition of the tenant.
   */
  private AccountType disposition;

  /**
   * Trial code if any.
   */
  private String trialCode;

  /**
   * List of account types with self registration allowed
   */
  private List<AccountType> selfRegistrationAccountTypes = new ArrayList<AccountType>();

  private String accountTypeId;

  /**
   * Tenant details.
   */
  @Valid
  @NotNull
  private com.citrix.cpbm.access.Tenant tenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant());

  /**
   * Billing address if different from tenant address
   */
  // TODO: Enable validation
  private Address billingAddress;

  /**
   * Account currency.
   */
  private String currency;

  /**
   * Supported currencies in the channel.
   */
  private List<CurrencyValue> currencyValueList = new ArrayList<CurrencyValue>();

  public List<CurrencyValue> getCurrencyValueList() {
    return currencyValueList;
  }

  public void setCurrencyValueList(List<CurrencyValue> currencyValueList) {
    this.currencyValueList = currencyValueList;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  private CreditCard creditCard;

  /**
   * @return the creditCard
   */
  public CreditCard getCreditCard() {
    return creditCard;
  }

  /**
   * @param creditCard the creditCard to set
   */
  public void setCreditCard(CreditCard creditCard) {
    this.creditCard = creditCard;
  }

  /**
   * Contains the phone verification PIN entered by User
   */
  private String userEnteredPhoneVerificationPin;

  /**
   * System generated PIN
   */
  private String generatedPhoneVerificationPin;

  /**
   * Shows if telephone verification is enabled.
   */
  private boolean phoneVerificationEnabled;

  /**
   * ISD Dialiing code of country
   */
  private String countryCode;

  /**
   * Name of country
   */
  private String countryName;

  private boolean allowSecondary = false;

  private Address secondaryAddress;

  private List<Country> countryList;

  public List<Country> getCountryList() {
    return countryList;
  }

  public void setCountryList(List<Country> countryList) {
    this.countryList = countryList;
  }

  public boolean isAllowSecondary() {
    return allowSecondary;
  }

  public void setAllowSecondary(boolean allowSecondary) {
    this.allowSecondary = allowSecondary;
  }

  public Address getSecondaryAddress() {
    return secondaryAddress;
  }

  public void setSecondaryAddress(Address secondaryAddress) {
    this.secondaryAddress = secondaryAddress;
  }

  public String getUserEnteredPhoneVerificationPin() {
    return userEnteredPhoneVerificationPin;
  }

  public void setUserEnteredPhoneVerificationPin(String userEnteredPhoneVerificationPin) {
    this.userEnteredPhoneVerificationPin = userEnteredPhoneVerificationPin;
  }

  public String getGeneratedPhoneVerificationPin() {
    return generatedPhoneVerificationPin;
  }

  public void setGeneratedPhoneVerificationPin(String generatedPhoneVerificationPin) {
    this.generatedPhoneVerificationPin = generatedPhoneVerificationPin;
  }

  public void setPhoneVerificationEnabled(boolean phoneVerificationEnabled) {
    this.phoneVerificationEnabled = phoneVerificationEnabled;
  }

  public boolean getPhoneVerificationEnabled() {
    return phoneVerificationEnabled;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public String getCountryCode() {
    if (countryCode == null) {
      countryCode = "";
    }
    return countryCode;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public String getCountryName() {
    return countryName;
  }

  /**
   * accepted Terms And Conditions
   */
  @AssertTrue(message = "errors.registration.terms")
  private boolean acceptedTerms;

  /**
   * Initial Payment if any.
   */
  private BigDecimal initialPayment;

  /**
   * Currency Value
   */
  private BigDecimal currencyValue;

  public final List<CreditCardType> getCcTypes() {
    return CreditCardType.values();
  }

  public final List<Integer> getCreditCardExpYearList() {
    Calendar calendar = Calendar.getInstance();
    List<Integer> yearList = new ArrayList<Integer>();
    yearList.add(calendar.get(Calendar.YEAR));
    for (int i = 0; i < 10; i++) {
      calendar.add(Calendar.YEAR, 1);
      yearList.add(calendar.get(Calendar.YEAR));
    }
    return yearList;
  }

  public final com.citrix.cpbm.access.User getUser() {
    return user;
  }

  public final void setUser(com.citrix.cpbm.access.User user) {
    this.user = user;
  }

  public final AccountType getDisposition() {
    return disposition;
  }

  public final void setDisposition(AccountType disposition) {
    this.disposition = disposition;
  }

  public List<AccountType> getSelfRegistrationAccountTypes() {
    return selfRegistrationAccountTypes;
  }

  public void setSelfRegistrationAccountTypes(List<AccountType> selfRegistrationAccountTypes) {
    this.selfRegistrationAccountTypes = selfRegistrationAccountTypes;
  }

  public String getAccountTypeId() {
    return accountTypeId;
  }

  public void setAccountTypeId(String accountTypeId) {
    this.accountTypeId = accountTypeId;
  }

  public final com.citrix.cpbm.access.Tenant getTenant() {
    return tenant;
  }

  public final void setTenant(com.citrix.cpbm.access.Tenant tenant) {
    this.tenant = tenant;
  }

  /**
   * @return the billingAddress
   */
  public final Address getBillingAddress() {
    if (billingAddress == null) {
      billingAddress = new Address();
    }
    return billingAddress;
  }

  /**
   * @param billingAddress the billingAddress to set
   */
  public final void setBillingAddress(Address billingAddress) {
    this.billingAddress = billingAddress;
  }

  /**
   * @return the trialCode
   */
  public final String getTrialCode() {
    return trialCode;
  }

  /**
   * @param trialCode the trialCode to set
   */
  public final void setTrialCode(String trialCode) {
    this.trialCode = trialCode;
  }

  /**
   * @return the acceptedTerms
   */
  public final boolean isAcceptedTerms() {
    return acceptedTerms;
  }

  /**
   * @param acceptedTerms the acceptedTerms to set
   */
  public final void setAcceptedTerms(boolean acceptedTerms) {
    this.acceptedTerms = acceptedTerms;
  }

  /**
   * @return the initialPayment
   */
  public final BigDecimal getInitialPayment() {
    return initialPayment;
  }

  /**
   * @param initialPayment the initialPayment to set
   */
  public final void setInitialPayment(BigDecimal initialPayment) {
    this.initialPayment = initialPayment;
  }

  /**
   * @return the currencyValue
   */
  public final BigDecimal getCurrencyValue() {
    return currencyValue;
  }

  /**
   * @param currencyValue the currencyValue to set
   */
  public final void setCurrencyValue(BigDecimal currencyValue) {
    this.currencyValue = currencyValue;
  }

  /**
   * Reset this form (ie recreate user and tenant object for a second submit). This is needed because attempts may have
   * been made to persist these objects prior to an error occurring.
   */
  public void reset() {
    try {
      tenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance((Tenant) tenant.clone());
      setBillingAddress(tenant.getAddress());
      user = (com.citrix.cpbm.access.User) CustomProxy.newInstance((User) user.clone());
      user.setAddress(tenant.getAddress());
      user.setPhone(user.getPhoneWithoutIsdCode());
      if (creditCard != null) {
        creditCard.setCreditCardCVV(null);
        creditCard.setCreditCardNumber(null);
        creditCard.setCreditCardExpirationMonth(0);
        creditCard.setCreditCardExpirationYear(0);
      }

    } catch (CloneNotSupportedException e) {
      user = (com.citrix.cpbm.access.User) CustomProxy.newInstance(new User());
      tenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant());
      tenant.setAddress(new Address());
      user.setAddress(tenant.getAddress());
    }
  }

}
