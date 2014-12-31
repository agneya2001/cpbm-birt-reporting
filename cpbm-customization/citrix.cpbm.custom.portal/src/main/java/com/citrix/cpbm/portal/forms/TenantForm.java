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
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

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
 * Form backing object for tenant form.
 * 
 * @author vijay
 */
@JsonAutoDetect(JsonMethod.NONE)
public class TenantForm {

  /**
   * The user entry.
   */
  @Valid
  @NotNull
  @JsonProperty
  private com.citrix.cpbm.access.User user = (com.citrix.cpbm.access.User) CustomProxy.newInstance(new User());

  /**
   * The disposition of the tenant.
   */
  private AccountType disposition;

  /**
   * Trial code if any.
   */
  private String trialCode;

  private String productSKU;

  private String rateplanName;

  private BigDecimal spendLimit;

  private BigDecimal creditBalance;

  /**
   * Tenant details.
   */
  @JsonProperty
  private com.citrix.cpbm.access.Tenant tenant;

  private String channelParam;

  /**
   * Billing address if different from tenant address
   */
  // TODO: Enable validation
  private Address billingAddress;

  private CreditCard creditCard;

  /**
   * Initial Payment if any.
   */
  private BigDecimal initialPayment;

  /**
   * Currency Value
   */
  private BigDecimal currencyValue;

  private String accountTypeId;

  private String confirmEmail;

  private boolean allowSecondary = false;

  /**
   * Account currency.
   */
  private String currency;

  /**
   * Supported currencies in the channel.
   */
  private List<CurrencyValue> currencyValueList = new ArrayList<CurrencyValue>();

  private Long userLimit;

  private List<Country> countryList = new ArrayList<Country>();

  public List<Country> getCountryList() {
    return countryList;
  }

  public void setCountryList(List<Country> countryList) {
    this.countryList = countryList;
  }

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

  private Address secondaryAddress;

  public Address getSecondaryAddress() {
    return secondaryAddress;
  }

  public void setSecondaryAddress(Address secondaryAddress) {
    this.secondaryAddress = secondaryAddress;
  }

  public boolean isAllowSecondary() {
    return allowSecondary;
  }

  public void setAllowSecondary(boolean allowSecondary) {
    this.allowSecondary = allowSecondary;
  }

  private List<AccountType> accountTypes = new ArrayList<AccountType>();

  public TenantForm() {

  }

  public TenantForm(com.citrix.cpbm.access.Tenant tenant) {
    this.tenant = tenant;
  }

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

  public String getConfirmEmail() {
    return confirmEmail;
  }

  public void setConfirmEmail(String confirmEmail) {
    this.confirmEmail = confirmEmail;
  }

  public final AccountType getDisposition() {
    return disposition;
  }

  public final void setDisposition(AccountType disposition) {
    this.disposition = disposition;
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
   * @return the initialPayment
   */
  public final BigDecimal getInitialPayment() {
    return initialPayment;
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
   * @return the productSKU
   */
  public String getProductSKU() {
    return productSKU;
  }

  /**
   * @param productSKU the productSKU to set
   */
  public void setProductSKU(String productSKU) {
    this.productSKU = productSKU;
  }

  /**
   * @return the rateplanName
   */
  public String getRateplanName() {
    return rateplanName;
  }

  /**
   * @param rateplanName the rateplanName to set
   */
  public void setRateplanName(String rateplanName) {
    this.rateplanName = rateplanName;
  }

  /**
   * Reset this form (ie recreate user and tenant object for a second submit). This is needed because attempts may have
   * been made to persist these objects prior to an error occurring.
   */
  public void reset() {
    try {
      tenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance((Tenant) tenant.clone());
      user = (com.citrix.cpbm.access.User) CustomProxy.newInstance((User) user.clone());
      user.setAddress(tenant.getAddress());
    } catch (CloneNotSupportedException e) {
      user = (com.citrix.cpbm.access.User) CustomProxy.newInstance(new User());
      tenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant());
      tenant.setAddress(new Address());
      user.setAddress(tenant.getAddress());
    }
  }

  /**
   * @param spendLimit the spendLimit to set
   */
  public void setSpendLimit(BigDecimal spendLimit) {
    this.spendLimit = spendLimit;
  }

  /**
   * @return the spendLimit
   */
  public BigDecimal getSpendLimit() {
    return spendLimit;
  }

  public void setChannelParam(String channelParam) {
    this.channelParam = channelParam;
  }

  public String getChannelParam() {
    return channelParam;
  }

  /**
   * @return the accountTypes
   */
  public List<AccountType> getAccountTypes() {
    return accountTypes;
  }

  /**
   * @param accountTypes the accountTypes to set
   */
  public void setAccountTypes(List<AccountType> accountTypes) {
    this.accountTypes = accountTypes;
  }

  /**
   * @return the accountTypeId
   */
  public String getAccountTypeId() {
    return accountTypeId;
  }

  /**
   * @param accountTypeId the accountTypeId to set
   */
  public void setAccountTypeId(String accountTypeId) {
    this.accountTypeId = accountTypeId;
  }

  public BigDecimal getCreditBalance() {
    return creditBalance;
  }

  public void setCreditBalance(BigDecimal creditBalance) {
    this.creditBalance = creditBalance;
  }

  public Long getUserLimit() {
    return userLimit;
  }

  public void setUserLimit(Long userLimit) {
    this.userLimit = userLimit;
  }
}
