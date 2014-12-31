/*
 * Copyright ¬© 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid
 * license agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.access.proxy.CustomProxyUtils;
import com.citrix.cpbm.core.workflow.model.BusinessTransaction;
import com.citrix.cpbm.core.workflow.model.CloudServiceActivationTransaction;
import com.citrix.cpbm.core.workflow.model.Task;
import com.citrix.cpbm.core.workflow.service.TaskService;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.spi.AccountLifecycleHandler;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.citrix.cpbm.platform.spi.UserLifecycleHandler;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.citrix.cpbm.portal.forms.TenantForm;
import com.vmops.config.BillingPostProcessor;
import com.vmops.internal.service.CustomFieldService;
import com.vmops.internal.service.NotificationService;
import com.vmops.internal.service.PaymentGatewayService;
import com.vmops.model.AccountControlServiceConfigMetadata;
import com.vmops.model.AccountHolder;
import com.vmops.model.AccountType;
import com.vmops.model.CampaignPromotion;
import com.vmops.model.Catalog;
import com.vmops.model.Channel;
import com.vmops.model.Country;
import com.vmops.model.CreditCard;
import com.vmops.model.CurrencyValue;
import com.vmops.model.DepositRecord;
import com.vmops.model.Event;
import com.vmops.model.Event.Category;
import com.vmops.model.Event.Scope;
import com.vmops.model.Event.Severity;
import com.vmops.model.Event.Source;
import com.vmops.model.IPtoCountry;
import com.vmops.model.PaymentMode;
import com.vmops.model.PendingChange;
import com.vmops.model.ResourceLimit;
import com.vmops.model.Service;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceInstanceTenantConfiguration;
import com.vmops.model.SpendAlertSubscription;
import com.vmops.model.SupportedCurrency;
import com.vmops.model.Tenant;
import com.vmops.model.Tenant.State;
import com.vmops.model.User;
import com.vmops.model.UserAlertPreferences;
import com.vmops.model.UserAlertPreferences.AlertType;
import com.vmops.model.billing.AccountStatement;
import com.vmops.model.billing.SalesLedgerRecord;
import com.vmops.model.billing.SalesLedgerRecord.Type;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.AccountTypeService;
import com.vmops.service.AuthorityService;
import com.vmops.service.ChannelService;
import com.vmops.service.PromotionService;
import com.vmops.service.RegistrationService;
import com.vmops.service.ResourceLimitService;
import com.vmops.service.SearchService;
import com.vmops.service.TenantService;
import com.vmops.service.UserAlertPreferencesService;
import com.vmops.service.UserService;
import com.vmops.service.UserService.Handle;
import com.vmops.service.billing.BillingAdminService;
import com.vmops.service.exceptions.AjaxFormValidationException;
import com.vmops.service.exceptions.BeanValidationException;
import com.vmops.service.exceptions.BillingAdminServiceException;
import com.vmops.service.exceptions.CloudServiceException;
import com.vmops.service.exceptions.IPtoCountryException;
import com.vmops.service.exceptions.InternalIPException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.LDAPException;
import com.vmops.service.exceptions.NoManualRegistrationAccountTypeException;
import com.vmops.service.exceptions.ServiceException;
import com.vmops.service.exceptions.TenantStateChangeFailedException;
import com.vmops.service.exceptions.UserLimitServiceException;
import com.vmops.utils.JSONUtils;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Level1;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.AdvancedSearchForm;
import com.vmops.web.forms.BillingInfoForm;
import com.vmops.web.forms.CustomAlertForm;
import com.vmops.web.forms.ResourceLimitForm;
import com.vmops.web.forms.SearchForm;
import com.vmops.web.forms.SetAccountTypeForm;
import com.vmops.web.forms.TenantLogoForm;
import com.vmops.web.forms.UserAlertEmailForm;
import com.vmops.web.interceptors.UserContextInterceptor;
import com.vmops.web.validators.AlertValidator;
import com.vmops.web.validators.TenantLogoFormValidator;
import com.vmops.web.validators.ValidationUtil;

public class AbstractTenantController extends AbstractAuthenticatedController {

// CHECKSTYLE:OFF
  @Autowired
  protected BillingAdminService billingAdminService;

// CHECKSTYLE:ON
  @Autowired
  private BillingPostProcessor billingPostProcessor;

// CHECKSTYLE:OFF
  @Autowired
  protected SearchService searchService;

  @Autowired
  protected CustomFieldService customFieldService;

  @Autowired
  protected RegistrationService registrationService;

  @Autowired
  protected ResourceLimitService resourceLimitService;

  @Autowired
  protected NotificationService notificationService;

  @Autowired
  protected UserAlertPreferencesService userAlertPreferencesService;

  @Autowired
  protected ChannelService channelService;

  @Autowired
  protected AccountTypeService accountTypeService;

  @Autowired
  protected PromotionService promotionService;

// CHECKSTYLE:ON

  @Autowired
  private MessageSource messageSource;

// CHECKSTYLE:OFF
  @Autowired
  protected AuthorityService authorityScopeService;

// CHECKSTYLE:ON

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManager;

  @Autowired
  private TaskService taskService;

  private final Logger logger = Logger.getLogger(AbstractTenantController.class);

  protected enum EventType {
    SYSTEM,
    SUBSCRIBED
  };

  protected enum Resource_Limit_Enum {
    VM_LIMIT,
    PUBLIC_IP_LIMIT,
    VOLUME_LIMIT,
    SNAPSHOT_LIMIT,
    TEMPLATE_LIMIT
  };

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.setDisallowedFields("state");
  }

  /**
   * This method returns the Tenant View
   * 
   * @param tenantParam
   * @param map
   * @return String (tenant.view)
   */

  @RequestMapping(value = "/viewtenant", method = RequestMethod.GET)
  public String show(@RequestParam(value = "tenant", required = true) String tenantParam, ModelMap map) {
    logger.debug("###Entering in show(tenantId,map) method @GET");
    Tenant tenant = tenantService.get(tenantParam);
    customFieldService.populateCustomFields(tenant);
    map.addAttribute("tenant", tenant);
    BigDecimal creditBalance = BigDecimal.ZERO;
    if (!tenant.getAccountId().equals(TenantService.SYSTEM_TENANT)) {
      creditBalance = billingAdminService.getOrCreateProvisionalAccountStatement(tenant).getFinalCharges().negate();
    }
    map.addAttribute("creditBalance", creditBalance);
    User user = getCurrentUser();
    Map<Task, String> actions = taskService.getPendingTasksMap(tenant, user, 0, 0);
    map.addAttribute("pendingActions", actions);

    List<ServiceInstance> enabledCSInstances = tenantService.getEnabledCSInstances(tenant);
    Set<Service> services = tenantService.getEnabledServices(tenant);
    map.addAttribute("instances", enabledCSInstances);
    map.addAttribute("services", services);

    Long userLimit = tenant.getMaxUsers();
    // TODO show userlimit in UI
    map.addAttribute("userLimit", userLimit);

    logger.debug("###Exiting show(map) method @GET");
    return "tenant.view";
  }

  /**
   * This method returns the edited tenant view.
   * 
   * @param tenantParam
   * @param map
   * @return String (tenant.edit)
   */
  @RequestMapping(value = "/edit", method = RequestMethod.GET)
  public String edit(@RequestParam(value = "tenant", required = true) String tenantParam, ModelMap map, HttpServletRequest request ) {
    logger.debug("### edit method starting...(GET)");
    Tenant tenant = tenantService.get(tenantParam);
    customFieldService.populateCustomFields(tenant);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
    if (proxyTenant.getSpendLimit() != null) {
      proxyTenant.setSpendLimit(proxyTenant.getSpendLimit().setScale(
          Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
          RoundingMode.HALF_UP));
    }
    TenantForm tenantForm = new TenantForm(proxyTenant);
    List<Country> allCountries = countryService.getCountries(null, null, null, null, null, null, null);
    String channelParam = null;
    if (tenant.getSourceChannel() != null) {
      channelParam = tenant.getSourceChannel().getParam();
    }
    tenantForm.setCountryList(getFilteredCountryList(allCountries, channelParam));

    map.addAttribute("tenantForm", tenantForm);
    request.setAttribute("tenantForm_cid", tenant.getParam());
    map.addAttribute("allowSecondaryCheckBox", proxyTenant.getAccountType().isEnableSecondaryAddress());
    if (proxyTenant.getSecondaryAddress() != null) {
      tenantForm.setAllowSecondary(true);
    }
    logger.debug("### edit method end");
    return "tenant.edit";
  }

  /**
   * Returns View for the Current Tenant Edit.
   * 
   * @param action
   * @param tenantParam
   * @param tabParam
   * @param map
   * @param request
   * @return String (tenants.editcurrent)
   */
  @RequestMapping(value = "/editcurrent", method = RequestMethod.GET)
  public String editCurrentTenant(@RequestParam(value = "action", required = false) String action,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "currenttab", required = false) String tabParam, ModelMap map, HttpServletRequest request) {
    logger.debug("### edit current tenant method starting...(GET)");

    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    List<Country> countryList = countryService.getCountries(null, null, null, null, null, null, null);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy
        .newInstance(effectiveTenant);
    map.addAttribute("tenant", proxyTenant);
    map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(proxyTenant.getOwner()));
    customFieldService.populateCustomFields(proxyTenant.getObject());
    TenantForm tenantForm = new TenantForm(proxyTenant);

    boolean isSPUser = effectiveTenant.getOwner().getProfile().isOperationsProfile();
    tenantForm.setCountryList(getFilteredCountryList(countryList, effectiveTenant.getSourceChannel() == null ? null
        : effectiveTenant.getSourceChannel().getUuid(), isSPUser));

    if (!TenantService.SYSTEM_TENANT.equals(proxyTenant.getObject().getAccountId())) {
      tenantForm.setCreditBalance(getCreditBalance(proxyTenant.getObject()));
    }
    map.addAttribute("tenantForm", tenantForm);
    map.addAttribute("allowSecondaryCheckBox", proxyTenant.getAccountType().isEnableSecondaryAddress());
    if (proxyTenant.getSecondaryAddress() != null) {
      tenantForm.setSecondaryAddress(proxyTenant.getSecondaryAddress());
    } else {
      tenantForm.setSecondaryAddress(null);
    }
    tenantForm.setAllowSecondary(proxyTenant.getAccountType().isEnableSecondaryAddress());

    CreditCard creditCard = null;

    List<AccountType> allowedTargetTypes = tenantService.getAllowedTargetAccountTypes(proxyTenant.getObject());
    if (allowedTargetTypes != null && !allowedTargetTypes.isEmpty()) {
      map.addAttribute("showChangeAccountType", true);
    } else {
      map.addAttribute("showChangeAccountType", false);
    }
    List<PendingChange> pendingConversions = tenantService.getPendingConversions(proxyTenant.getObject());

    if (pendingConversions != null && !pendingConversions.isEmpty()) {
      map.addAttribute("showMessagePendingConversion", true);
    } else {
      map.addAttribute("showMessagePendingConversion", false);
    }
    if (proxyTenant.getAccountType().isDepositRequired()
        && !(((PaymentGatewayService) connectorManagementService
            .getOssServiceInstancebycategory(ConnectorType.PAYMENT_GATEWAY)).isAccountExistInPaymentGateway(proxyTenant
            .getObject()))) {
      DepositRecord depositRecord = tenantService.getDepositRecordByTenant(proxyTenant.getObject());
      if (depositRecord == null
          && proxyTenant.getAccountType().getInitialDeposit(proxyTenant.getObject().getCurrency().getCurrencyCode())
              .doubleValue() > 0) {
        map.addAttribute("show_deposit_record_message", true);
        map.addAttribute("initialDepositAmount", proxyTenant.getCurrency().getCurrencyCode() + " "
            + proxyTenant.getAccountType().getInitialDeposit(proxyTenant.getCurrency().getCurrencyCode()));
      } else {
        map.addAttribute("show_deposit_record_message", false);
      }
    } else {
      map.addAttribute("show_deposit_record_message", false);
    }

    effectiveTenant = tenantService.get(proxyTenant.getObject().getParam());
    try {

      creditCard = billingAdminService.getCreditCard(effectiveTenant);
    } catch (Exception e) {
      logger.error("###Failed to get payment information", e);
      // we have profile id but failing to get cc details.
      map.addAttribute("errormsg", messageSource.getMessage("ui.usage.billing.paymentinfo.cc.errormsg", null,
          proxyTenant.getOwner().getLocale()));
      if (((PaymentGatewayService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PAYMENT_GATEWAY))
          .isAccountExistInPaymentGateway(effectiveTenant)) {
        creditCard = new CreditCard();

      }

    }

    boolean paymentInfoExists = creditCard != null;

    map.addAttribute("paymentInfoExists", paymentInfoExists);

    BillingInfoForm billingInfoForm = new BillingInfoForm(effectiveTenant, null, creditCard);
    billingInfoForm.setCountryList(getFilteredCountryList(countryList,
        effectiveTenant.getSourceChannel() == null ? null : effectiveTenant.getSourceChannel().getUuid()));

    boolean showCreditCardtab = true;
    if (action != null) {
      billingInfoForm.setAction(action);
    } else {
      showCreditCardtab = false;
    }
    map.addAttribute("showCreditCardTabVar", showCreditCardtab);

    User user = actorService.getActor();
    int currenttab = 1;
    if (userService.hasAuthority(user, "ROLE_ACCOUNT_ADMIN") || userService.hasAuthority(user, "ROLE_ACCOUNT_CRUD")) {
      currenttab = 1;
    } else if (userService.hasAuthority(user, "ROLE_ACCOUNT_BILLING_ADMIN")) {
      currenttab = 4;
    }

    if ("billingAddress".equals(tabParam)) {
      setPage(map, Page.COMPANY_BILLING_DETAILS);
      currenttab = 4;
    } else if ("contact".equals(tabParam)) {
      setPage(map, Page.COMPANY_CONTACT_PAGE);
      currenttab = 2;
    } else {
      setPage(map, Page.COMPANY_PROFILE_PAGE);
    }

    map.addAttribute("currenttab", currenttab);

    map.addAttribute("billingInfo", billingInfoForm);

    SetAccountTypeForm setAccountTypeForm = new SetAccountTypeForm(effectiveTenant);
    map.addAttribute("setAccountTypeForm", setAccountTypeForm);

    List<AccountType> targetAccountTypes = tenantService.getAllowedTargetAccountTypes(effectiveTenant.getAccountType());
    map.addAttribute("targetAccountTypes", targetAccountTypes);
    if (!TenantService.SYSTEM_TENANT.equals(effectiveTenant.getAccountId())) {
      AccountStatement accountStatement = billingAdminService.getOrCreateProvisionalAccountStatement(effectiveTenant);
      map.addAttribute("currentBillingStart", accountStatement.getBillingPeriodStartDate());
      map.addAttribute("currentBillingEnd", accountStatement.getBillingPeriodEndDate());
      map.addAttribute("BillingPeriodLength", accountStatement.getTotalDaysInBillingPeriod());
    }
    List<BusinessTransaction> transactionHistory = businessTransactionService.getAllBusinessTransactions(
        effectiveTenant, 0, 0);
    map.addAttribute("transactionHistory", transactionHistory);

    String doNotShowPasswordEditLink = "false";
    if (config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled)
        && config.getValue(Names.com_citrix_cpbm_directory_mode).equals("pull")) {
      doNotShowPasswordEditLink = "true";
    }
    map.addAttribute("doNotShowPasswordEditLink", doNotShowPasswordEditLink);

    logger.debug("### edit current tenant method end");

    return "tenants.editcurrent";
  }

  /**
   * Get method for EditCurrentLogo
   * 
   * @param map
   * @return String (tenants.editcurrentlogo)
   */
  @RequestMapping(value = "/editcurrentlogo", method = RequestMethod.GET)
  public String editCurrentTenantLogo(ModelMap map) {
    logger.debug("### edit current tenant logo method starting...(GET)");
    TenantLogoForm tenantLogoForm = new TenantLogoForm(getTenant());
    map.addAttribute("tenantLogoForm", tenantLogoForm);
    logger.debug("### edit current tenant logo method end");
    setPage(map, Page.HOME);
    return "tenants.editcurrentlogo";
  }

  /**
   * Edit Tenant Method
   * 
   * @param tenantForm
   * @param result
   * @return Map
   */
  @RequestMapping(value = ("/edit"), method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> edit(@ModelAttribute("tenantForm") TenantForm tenantForm, BindingResult result) {

    logger.debug("### edit method starting...(POST)");
    com.citrix.cpbm.access.Tenant proxyTenant = tenantForm.getTenant();

    if (proxyTenant != null) {
      if (tenantForm.getSecondaryAddress() == null && !tenantForm.isAllowSecondary()) {
        proxyTenant.setSecondaryAddress(null);
      } else {
        if (tenantForm.getSecondaryAddress() != null && tenantForm.getSecondaryAddress().getCountry() != null) {
          proxyTenant.setSecondaryAddress(tenantForm.getSecondaryAddress());
        }
      }

      if (proxyTenant.getSpendLimit() != null) {
        proxyTenant.setSpendLimit(proxyTenant.getSpendLimit().setScale(
            Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
            RoundingMode.HALF_UP));
      }

      try {
        if (isAdmin() && proxyTenant.getObject().getAccountId().equals(TenantService.SYSTEM_TENANT)) {
          // root does not have extra information, so setting it null
          proxyTenant.getObject().setTenantExtraInformation(null);
        }
        tenantService.update(proxyTenant, result);
      } catch (ServiceException ex) {
        throw new InvalidAjaxRequestException(ex.getMessage());
      } catch (BeanValidationException ex) {
        throw new InvalidAjaxRequestException(ex.getMessage());
      }
      return getTenantDataHashMap(proxyTenant.getObject());
    }
    return new HashMap<String, String>();
  }

  /**
   * This method used for Editing Tenant Logo
   * 
   * @param tenantParam
   * @param tenantLogoForm
   * @param result
   * @param map
   * @return String (tenants.editcurrentlogo)
   */
  @RequestMapping(value = "/{tenantParam}/editlogo", method = RequestMethod.POST)
  public String editTenantLogo(@PathVariable String tenantParam,
      @ModelAttribute("tenantLogoForm") TenantLogoForm tenantLogoForm, BindingResult result, ModelMap map,
      HttpServletRequest httpServletRequest) {

    logger.debug("### edit logo method starting...(POST)");
    setPage(map, Page.HOME);

    String fileSize = checkFileUploadMaxSizeException(httpServletRequest);
    if (fileSize != null) {
      result.reject("error.image.max.upload.size.exceeded", new Object[] {
        fileSize
      }, "");
      return "tenants.editcurrentlogo";
    }

    String rootImageDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    if (StringUtils.isNotBlank(rootImageDir)) {
      Tenant tenant = tenantService.get(tenantParam);
      com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
      String relativeImageDir = tenant.getParam();
      TenantLogoFormValidator validator = new TenantLogoFormValidator();
      validator.validate(tenantLogoForm, result);
      if (result.hasErrors()) {
        return "tenants.editcurrentlogo";
      } else {
        MultipartFile logoFile = tenantLogoForm.getLogo();
        MultipartFile faviconFile = tenantLogoForm.getFavicon();
        try {
          if (StringUtils.isNotEmpty(logoFile.getOriginalFilename())) {
            String logoFileRelativePath = writeMultiPartFileToLocalFile(rootImageDir, relativeImageDir, logoFile);
            proxyTenant.getObject().setImagePath(logoFileRelativePath);
          }
          if (StringUtils.isNotEmpty(faviconFile.getOriginalFilename())) {
            String faviconFileRelativePath = writeMultiPartFileToLocalFile(rootImageDir, relativeImageDir, faviconFile);
            proxyTenant.getObject().setFaviconPath(faviconFileRelativePath);
          }
          tenantService.update(proxyTenant, result);
          return "redirect:/portal/home";
        } catch (IOException e) {
          logger.debug("###IO Exception in writing custom image file");
        }
      }
    }

    result.reject("error.custom.image.upload.dir", null, "");

    return "tenants.editcurrentlogo";
  }

  /**
   * Retrieves view for Tenants by CharRange and Pattern.
   * 
   * @param map
   * @param currentPage
   * @param perPage
   * @param size
   * @param charRange
   * @param pattern
   * @return String (tenant.search.list)
   */
  @RequestMapping(value = "/searchlist", method = RequestMethod.GET)
  public String searchlist(ModelMap map,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "perPage", required = false, defaultValue = "5") String perPage,
      @RequestParam(value = "size", required = false) String size,
      @RequestParam(value = "charRange", required = false, defaultValue = "All") String charRange,
      @RequestParam(value = "pattern", required = false) String pattern) {
    logger.debug("###Entering in searchlist(map) method @GET");
    int currentPageValue = Integer.parseInt(currentPage);
    int perPageValue = Integer.parseInt(perPage);
    List<Tenant> tenants = tenantService.list(currentPageValue, perPageValue, null, null, false, null, null, null,
        null, null, null, charRange, pattern);
    int sizeInt = 0;
    if (StringUtils.isNotEmpty(size)) {
      sizeInt = tenantService.getCount(charRange, pattern);
    }
    map.addAttribute("tenants", tenants);
    map.addAttribute("pattern", pattern);
    setPaginationValues(map, perPageValue, currentPageValue, sizeInt, charRange);
    logger.debug("###Exiting searchlist(map) method @GET");
    return "tenant.search.list";
  }

  /**
   * This method lists Tenant.
   * 
   * @param tenantUUId
   * @param showAddAccountWizard
   * @param accountType
   * @param filterBy
   * @param currentPage
   * @param tenantParam
   * @param size
   * @param map
   * @param request
   * @return String (tenant.listing)
   */
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String list(@RequestParam(value = "tenantUUId", required = false) String tenantUUId,
      @RequestParam(value = "showAddAccountWizard", required = false) String showAddAccountWizard,
      @RequestParam(value = "accountType", required = false) String accountType,
      @RequestParam(value = "filterBy", required = false) String filterBy,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "tenantParam", required = false) String tenantParam,
      @RequestParam(value = "size", required = false) String size,
      @RequestParam(value = "selectedAccount", required = false) String selectedAccount, ModelMap map,
      HttpServletRequest request) {
    logger.debug("###Entering in list(map) method @GET");
    // The tenantForm object is added into map just to populate country list in country_states.jsp
    TenantForm tenantForm = new TenantForm();
    tenantForm.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    map.addAttribute("tenantForm", tenantForm);

    List<AccountType> accountTypes = tenantService.getAllAccountTypes();
    map.addAttribute("accountTypes", accountTypes);
    if (showAddAccountWizard != null) {
      map.addAttribute("showAddAccountWizard", showAddAccountWizard);
    }
    AccountType accountTypeObj = null;
    if (accountType != null && !("".equals(accountType))) {
      accountTypeObj = tenantService.getAccountTypeById(accountType);
    }

    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    int perPageValue = getDefaultPageSize();
    int sizeInt = 0;
    Tenant newlyAddedTenant = null;
    if (StringUtils.isBlank(size)) {
      if (tenantParam != null) {
        newlyAddedTenant = tenantService.get(tenantParam);
        sizeInt = 1;
      } else {
        List<Tenant> tenantsFound = new ArrayList<Tenant>();
        if (StringUtils.isNotBlank(tenantUUId)) {
          Tenant tenant = tenantService.getTenantByParam("uuid", tenantUUId, false);
          if (tenant != null) {
            if (accountTypeObj == null || tenant.getAccountType().equals(accountTypeObj)) {
              tenantsFound.add(tenant);
            }
          }
        } else {
          Map<String, String> searchVar = new HashMap<String, String>();
          if (StringUtils.isNotBlank(filterBy)) {
            searchVar.put("filterBy", filterBy);
          }
          tenantsFound = tenantService.list(0, 0, null, null, false, filterBy, null, accountType, null, null,
              searchVar, null, null);
        }
        if (CollectionUtils.isNotEmpty(tenantsFound)) {
          sizeInt = tenantsFound.size();
        } else {
          sizeInt = 0;
        }
      }
    } else {
      sizeInt = Integer.parseInt(size);
    }

    Map<String, String> filtersMap = new LinkedHashMap<String, String>();
    filtersMap.put("All", messageSource.getMessage("ui.dropdown.account.filter.all", null, getSessionLocale(request)));
    filtersMap.put("0", messageSource.getMessage("ui.dropdown.account.filter.new", null, getSessionLocale(request)));
    filtersMap.put("1", messageSource.getMessage("ui.dropdown.account.filter.active", null, getSessionLocale(request)));
    filtersMap.put("2", messageSource.getMessage("ui.dropdown.account.filter.locked", null, getSessionLocale(request)));
    filtersMap.put("3",
        messageSource.getMessage("ui.dropdown.account.filter.suspended", null, getSessionLocale(request)));
    filtersMap.put("4",
        messageSource.getMessage("ui.dropdown.account.filter.terminated", null, getSessionLocale(request)));
    map.addAttribute("filtersMap", filtersMap);

    if (filterBy == null) {
      filterBy = "All";
    } else {
      if (!filterBy.equals("All")) {
        map.addAttribute("filterresult", "true");
      }
    }
    if (newlyAddedTenant != null) {
      filterBy = null;
    }
    map.addAttribute("filterBy", filterBy);

    List<Tenant> tenants = new ArrayList<Tenant>();
    AccountType tenantAccountTypeObj = null;
    if (newlyAddedTenant != null) {
      tenants.add(newlyAddedTenant);
    } else {
      if (StringUtils.isNotBlank(tenantUUId)) {
        Tenant tenant = tenantService.getTenantByParam("uuid", tenantUUId, false);
        if (tenant != null) {
          if (accountTypeObj == null || tenant.getAccountType().equals(accountTypeObj)) {
            tenants.add(tenant);
          }
        }
      } else {
        Map<String, String> searchVar = new HashMap<String, String>();
        if (StringUtils.isNotBlank(filterBy)) {
          searchVar.put("filterBy", filterBy);
        }
        tenants = tenantService.list(currentPageValue, perPageValue, null, null, false, filterBy, null, accountType,
            null, null, searchVar, null, null);
      }

      if (tenantUUId != null && !tenants.isEmpty()) {
        // Set account type for tabs
        tenantAccountTypeObj = tenants.get(0).getAccountType();
        accountType = tenantAccountTypeObj.getId().toString();
        // Set state for tabs
        State tenantState = tenants.get(0).getState();
        map.addAttribute("filterBy", Integer.toString(tenantState.ordinal()));
        map.addAttribute("filterresult", "true");
      }
    }
    if (accountType == null) {
      accountType = "";
      setPage(map, Page.ACCOUNT_ALL_ACCOUNTS);
    } else {
      String accountTypeName = accountTypeObj != null ? accountTypeObj.getName()
          : tenantAccountTypeObj != null ? tenantAccountTypeObj.getName() : "";

      if (StringUtils.isNotBlank(accountType)) {
        map.addAttribute("accountresult", "true");
        map.addAttribute("selectedTab", accountTypeName);
        if (accountType.equals("1")) {
          setPage(map, Page.ACCOUNT_SYSTEM);
        } else if (accountType.equals("3")) {
          setPage(map, Page.ACCOUNT_RETAIL);
        } else if (accountType.equals("4")) {
          setPage(map, Page.ACCOUNT_CORPORATE);
        } else if (accountType.equals("5")) {
          setPage(map, Page.ACCOUNT_TRIAL);
        } else {
          setCustomizePage(map, "page.title." + accountTypeName, Level1.Crm.getName(), accountTypeName, null);
        }
      }
    }
    map.addAttribute("on", "on");
    map.addAttribute("selectedAccountType", accountType);
    map.addAttribute("tenant", getTenant());
    map.addAttribute("tenants", tenants);
    Channel channel = channelService.getDefaultServiceProviderChannel();
    if (channel != null) {
      map.addAttribute("defaultChannel", channel.getParam());
    }
    map.addAttribute("channelSize", channelService.getChannels(null, null, null, null) != null ? channelService
        .getChannels(null, null, null, null).size() : 0);
    if (CollectionUtils.isNotEmpty(tenants)) {
      Tenant firstTenant = tenants.get(0);
      BigDecimal creditBalance = BigDecimal.ZERO;
      if (!firstTenant.getAccountId().equals(TenantService.SYSTEM_TENANT)) {
        creditBalance = getCreditBalance(firstTenant);
      }
      map.addAttribute("creditBalance", creditBalance);
    }
    setPaginationValues(map, perPageValue, currentPageValue, sizeInt, null);

    SearchForm searchForm = new SearchForm();

    String customFieldsProp = CustomProxyUtils.getSearchableFields(com.citrix.cpbm.access.Tenant.class);

    String[] customFields = null;

    if (customFieldsProp != null && customFieldsProp.length() != 0) {
      customFields = customFieldsProp.split(",");
      for (String customField : customFields) {
        searchForm.getCustomFields().put(customField, null);
      }
    }
    map.addAttribute("customFieldList", customFields);
    map.addAttribute("searchForm", searchForm);
    if (StringUtils.isNotEmpty(selectedAccount)) {
      map.addAttribute("selectedAccount", selectedAccount);
    }
    logger.debug("###Exiting list(map) method @GET");
    return "tenant.listing";
  }

  protected AdvancedSearchForm searchHelper() {
    HashMap<String, String> searchablefields = new HashMap<String, String>();
    List<String> entityList = new ArrayList<String>();
    entityList.add("com.vmops.model.Tenant");
    for (TenantService.SearchFields c : TenantService.SearchFields.values()) {
      searchablefields.put("com.vmops.model.Tenant" + "." + c.name(), c.getPropertyName());
    }

    entityList.add("com.vmops.model.User");
    for (UserService.SearchFields c : UserService.SearchFields.values()) {
      searchablefields.put("com.vmops.model.User" + "." + c.name(), c.getPropertyName());
    }

    AdvancedSearchForm searchForm = new AdvancedSearchForm();
    searchForm.setResultantEntity("com.vmops.model.Tenant");
    searchForm.setFieldList(searchService.getSerachField(entityList, searchablefields));

    return searchForm;
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public String create(ModelMap map, HttpServletRequest request) throws IPtoCountryException, InternalIPException {
    IPtoCountry iPtoCountry = new IPtoCountry();
    iPtoCountry.setCountryCode("");
    iPtoCountry.setBlackListed(false);
    com.citrix.cpbm.access.Tenant tenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant());
    TenantForm tenantForm = new TenantForm(tenant);
    try {
      iPtoCountry = super.getGeoIpToCountry(request, channelService.getDefaultServiceProviderChannel().getParam());
    } catch (InternalIPException e) {
      if (!getCurrentUser().equals(userService.getSystemUser(Handle.ROOT))) {
        logger.error(e.getMessage());
        throw e;
      }
    }
    setPage(map, Page.TENANT_ADD);
    tenant.getObject().getTenantExtraInformation().setPaymentMode(PaymentMode.CREDIT_CARD);

    tenantForm.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));

    map.addAttribute("tenant", CustomProxy.newInstance(getCurrentUser().getTenant()));
    tenantForm.setCurrency(config.getValue(Names.com_citrix_cpbm_portal_settings_default_currency));
    if (tenantService.getDefaultRegistrationAccountType() != null) {
      tenantForm.setAccountTypeId(tenantService.getDefaultRegistrationAccountType().getId().toString());
    }
    List<AccountType> manualRegistrationAccountTypes = tenantService.getManualRegistrationAccountTypes();
    if (manualRegistrationAccountTypes.size() == 0) {
      throw new NoManualRegistrationAccountTypeException("Manual Registration Not Allowed");
    }
    tenantForm.setAccountTypes(manualRegistrationAccountTypes);
    tenantForm.setCurrencyValueList(getCurrencies());
    map.addAttribute("account", tenantForm);
    map.addAttribute("channels", channelService.getChannels(null, null, null));

    List<Country> filteredCountryList = getFilteredCountryList(tenantForm.getCountryList(), channelService
        .getDefaultServiceProviderChannel().getParam());
    map.addAttribute("filteredCountryList", filteredCountryList);
    map.addAttribute("ipToCountryCode", iPtoCountry.getCountryCode());
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("defaultLocale", config.getDefaultLocale());
    String showImportFromDirectoryServiceButton = "false";
    if (config.getBooleanValue(Names.com_citrix_cpbm_portal_directory_service_enabled)
        && config.getValue(Names.com_citrix_cpbm_directory_mode).equals("pull")) {
      showImportFromDirectoryServiceButton = "true";
    }
    map.addAttribute("showImportAdButton", showImportFromDirectoryServiceButton);
    String showSuffixTextBox = "false";
    if (config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed).equals("true")) {
      showSuffixTextBox = "true";
    }
    map.addAttribute("showSuffixTextBox", showSuffixTextBox);
    return "tenants.new";
  }

  /**
   * Post method for creating Tenant.
   * 
   * @param form the Tenant form. {@link TenantForm}
   * @param result {@link BindingResult}
   * @param map {@link ModelMap}
   * @param sessionStatus
   * @param request the {@link HttpServletRequest}
   * @return View.
   * @throws Exception
   */
  @RequestMapping(value = {
      "", "/"
  }, method = RequestMethod.POST)
  public String create(@ModelAttribute("account") final TenantForm form, BindingResult result, ModelMap map,
      SessionStatus sessionStatus, HttpServletRequest request) throws Exception {
    logger.debug("###Entering in create(form,map,status) method @POST");
    String promocode = form.getTrialCode();
    if (promocode != null) {
      CampaignPromotion campaignPromotion = promotionService.locatePromotionByToken(promocode);
      if (campaignPromotion == null) {
        logger.debug("Campaign is not valid");
        result.reject("trialCode",
            messageSource.getMessage("errors.registration.invalid_trial_code", null, getSessionLocale(request)));
        return "tenants.new";
      }
    }
    String email = form.getUser().getEmail();
    if (isEmailBlacklisted(email.toLowerCase(), form.getChannelParam())) {
      fillTenantFormData(form, map);
      map.addAttribute("signuperror", "emaildomainblacklisted");
      return "tenants.new";
    }
    if (!isCountryAllowed(form)) {
      fillTenantFormData(form, map);
      map.addAttribute("signuperror", "emailcountryblacklisted");
      return "tenants.new";
    }
    if (result.hasErrors()) {
      displayErrors(result);
      parseResult(result, map);
      return "tenants.new";
    }
    // set currency
    for (CurrencyValue cv : form.getCurrencyValueList()) {
      if (cv.getCurrencyCode().equals(form.getCurrency())) {
        form.getTenant().setCurrency(cv);
        break;
      }
    }
    form.getTenant().setAddress(form.getUser().getAddress());

    if (form.isAllowSecondary()) {
      form.getTenant().setSecondaryAddress(form.getSecondaryAddress());
    }
    AccountType at = tenantService.getAccountTypeById(form.getAccountTypeId());
    List<PaymentMode> paymentModes = at.getSupportedPaymentModes();
    if (paymentModes != null && paymentModes.size() > 0) {
      form.getTenant().getObject().getTenantExtraInformation().setPaymentMode(paymentModes.get(0));
    }
    List<String> errorMsgList = new ArrayList<String>();
    try {
      final com.citrix.cpbm.access.User owner = form.getUser();

      String phoneNo = owner.getObject().getCountryCode().replaceAll(PHONE_NUMBER_REGEX, "")
          + COUNTRY_CODE_TO_PHONE_NUMBER_SEPERATOR + owner.getPhone().replaceAll(PHONE_NUMBER_REGEX, "");
      owner.setPhone(phoneNo);
      owner.setLocale(form.getUser().getLocale());

      form.getTenant().setRemoteAddress(getRemoteUserIp(request));
      String channelParam = form.getChannelParam();
      if (at.equals(tenantService.getTrialAccountType())) {
        com.citrix.cpbm.access.Tenant trialTenant = form.getTenant();
        trialTenant.setAccountType(at);
        registrationService.registerTrialAccount(form.getTrialCode(), trialTenant, owner, channelParam, result);
      } else {
        tenantService.createAccount(form.getTenant(), owner, channelParam, form.getAccountTypeId(), result);
        com.citrix.cpbm.access.Tenant tenant = form.getTenant();
        if (promocode != null && !promocode.isEmpty()) {
          promotionService.createTenantPromotion(promocode, tenant.getObject());
        }
      }
      map.addAttribute("tenant", form.getTenant());
      String homeUrl = channelService.getValue(Names.com_citrix_cpbm_portal_marketing_home_url, channelParam);
      if (homeUrl != null) {
        map.addAttribute("homeUrl", homeUrl);
      }
      // clean up parameters in session.
      sessionStatus.setComplete();
    } catch (DataAccessException ex) {
      logger.error(ex);
      result.reject("errors.registration", new Object[] {
        ex.getMessage()
      }, null);
      errorMsgList.add("You must accept the terms and conditions to use this service");
    } catch (LDAPException le) {
      logger.error(le);
      result.reject("errors.registration", new Object[] {
        le.getMessage()
      }, null);
    } catch (Exception ex) {
      logger.error("###handleTenantCreationError:" + ex.getMessage());
      throw ex;
    }
    if (result.hasErrors()) {
      displayErrors(result);
      form.reset();
      if (errorMsgList.size() > 0) {
        map.addAttribute("errorMsgList", errorMsgList);
        map.addAttribute("errormsg", true);
      }
      logger.debug("###Exiting register(registration,result,captchaChallenge,captchaResponse,"
          + "map,sessionStatus,request) method @POST");
      throw new AjaxFormValidationException(result);
    } else {
      String tenantParam = form.getTenant().getUuid();
      map.clear();
      map.addAttribute("tenantParam", tenantParam);
      map.addAttribute("tenantName", form.getTenant().getName());
      map.addAttribute("tenantAccountTypeName", form.getTenant().getAccountType().getName());
      map.addAttribute("tenantAccountId", form.getTenant().getObject().getAccountId());
      map.addAttribute("tenantOwnerUserName", form.getTenant().getOwner().getUsername());
      map.addAttribute("tenantId", form.getTenant().getObject().getId());
      logger.debug("###Exiting create(registration,result,map,sessionStatus,request) method @POST");
      return result.toString();
    }
  }

  /**
   * @param form
   * @param map
   */
  public void fillTenantFormData(final TenantForm form, ModelMap map) {
    List<Country> filteredCountryList = getFilteredCountryList(form.getCountryList(), form.getChannelParam());
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("defaultLocale", channelService.getDefaultLocale(getCurrentUser().getTenant().getSourceChannel()));
    map.addAttribute("ipToCountryCode", form.getUser().getAddress().getCountry());
    form.setAccountTypes(tenantService.getManualRegistrationAccountTypes());
    map.addAttribute("tenant", CustomProxy.newInstance(getCurrentUser().getTenant()));
    map.addAttribute("account", form);
    map.addAttribute("channels", channelService.getChannels(null, null, null));
    map.addAttribute("filteredCountryList", filteredCountryList);
  }

  private boolean isCountryAllowed(TenantForm form) {
    boolean countryAllowed = false;
    String countryCode = form.getUser().getAddress().getCountry();
    List<Country> filteredCountryList = getFilteredCountryList(
        countryService.getCountries(null, null, null, null, null, null, null), form.getChannelParam());
    if (CollectionUtils.isNotEmpty(filteredCountryList)) {
      for (Country country : filteredCountryList) {
        if (country.getCountryCode2().equals(countryCode)) {
          countryAllowed = true;
          break;
        }
      }
    }
    return countryAllowed;
  }

  /**
   * GET method for tenant State Change.
   * 
   * @param tenantParam
   * @param map {@link ModelMap} object.
   * @return
   */
  @RequestMapping(value = "/{tenantParam}/changeState", method = RequestMethod.GET)
  public String changeState(@PathVariable String tenantParam, ModelMap map) {
    logger.debug("###Starting changeState() method @GET start");
    Tenant tenant = tenantService.get(tenantParam);
    map.addAttribute("tenant", tenant);
    logger.debug("###Exiting changeState() method @GET start");
    return "tenant.change.state";
  }

  /**
   * Add credit method.
   * 
   * @param tenantParam the tenant param.
   * @param credit the Credit Amount
   * @param comment
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   * @return
   */
  @RequestMapping(value = "/{tenantParam}/add_credit", method = RequestMethod.POST)
  @ResponseBody
  public String issueCredit(@PathVariable String tenantParam,
      @RequestParam(value = "credit", required = true) String credit,
      @RequestParam(value = "comment", required = true) String comment, HttpServletRequest request,
      HttpServletResponse response) {
    logger.debug("###Entering in issueCredit(tenantId,credit,comment,response) method @POST");
    try {
      Tenant tenant = tenantService.get(tenantParam);
      JSONObject result = new JSONObject();
      BigDecimal creditDecimal = billingPostProcessor.setScaleByCurrency(new BigDecimal(credit), tenant.getCurrency());
      // negative credit
      if (creditDecimal.compareTo(new BigDecimal(0)) < 0) {
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      } else {

        SalesLedgerRecord creditSLR = billingAdminService.addPaymentOrCredit(tenant, creditDecimal,
            Type.SERVICE_CREDIT, comment, null);

        AccountStatement accountStatement = creditSLR.getAccountStatement();
        response.setStatus(HttpStatus.OK.value());
        String message = messageSource.getMessage("tenant.credit.confirmation", null, getSessionLocale(request));
        result.put("creditBalance", accountStatement.getFinalCharges().negate())
            .put("message", StringEscapeUtils.escapeHtml(message)).put("creditamount", accountStatement.getCredits());
      }
      logger.debug(result.toString());
      logger.debug("###Exiting issueCredit(tenantId,credit,comment,response) method @POST");
      return result.toString();
    } catch (Exception ex) {
      response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      logger.error(ex);
      return null;
    }
  }

  /**
   * Cancel credit method.
   * 
   * @param tenantParam
   * @param slrUuid
   * @param comment
   * @param request {@link HttpServletRequest}
   * @param response {@link HttpServletResponse}
   * @return
   */
  @RequestMapping(value = "/{tenantParam}/cancel_credit", method = RequestMethod.POST)
  @ResponseBody
  public String cancelCredit(@PathVariable String tenantParam,
      @RequestParam(value = "slrUuid", required = true) String slrUuid,
      @RequestParam(value = "comment", required = true) String comment, HttpServletRequest request,
      HttpServletResponse response) {
    logger.debug("###Entering in cancelCredit(tenantId,credit,comment,response) method @POST");
    try {
      Tenant tenant = tenantService.get(tenantParam);
      JSONObject result = new JSONObject();
      SalesLedgerRecord slr = billingAdminService.getSalesLedgerRecord(slrUuid);

      // Check if it is already cancelled, if cancelled then return error
      if (slr != null && slr.getCancellationReferenceId() != null) {
        throw new BillingAdminServiceException(" This payment has already been cancelled by slr:"
            + slr.getCancellationReferenceId().getParam());
      }

      if (slr != null && !slr.getType().equals(SalesLedgerRecord.Type.INVOICE)) {
        SalesLedgerRecord slr1 = billingAdminService.cancelCredit(tenant, slr, comment);
        // null is returned if any precondition fails
        if (slr1 == null) {
          response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
          return "failure";
        } else {
          response.setStatus(HttpStatus.OK.value());
          String message = messageSource.getMessage("tenant.credit.confirmation", null, getSessionLocale(request));
          result.put("creditBalance", slr1.getAccountStatement().getFinalCharges().negate()).put("message",
              StringEscapeUtils.escapeHtml(message));
        }
      } else {
        response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
        return "failure";
      }
      logger.debug(result.toString());
      logger.debug("###Exiting cancelCredit(tenantId,credit,comment,response) method @POST");
      return "success";
    } catch (Exception ex) {
      response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      logger.error(ex);
      return "failure";
    }
  }

  /**
   * Issue credit method.
   * 
   * @param tenantParam the tenant parameter.
   * @param map {@link ModelMap}
   * @return
   */
  @RequestMapping(value = "/issueCredit", method = RequestMethod.GET)
  public String issueCredit(@RequestParam(value = "tenant", required = true) String tenantParam, ModelMap map) {
    logger.debug("###Starting issueCredit() method @GET start");
    Tenant tenant = tenantService.get(tenantParam);
    map.addAttribute("tenant", tenant);
    logger.debug("###Exiting issueCredit() method @GET start");
    return "tenant.issue.credit";
  }

  /**
   * Exception Handler for {@link TenantStateChangeFailedException}
   * 
   * @param exception
   * @param request {@link HttpServletRequest}
   * @return
   */
  @ExceptionHandler(TenantStateChangeFailedException.class)
  @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
  public ModelAndView handleTenantStateChangeFailedException(TenantStateChangeFailedException ex,
      HttpServletRequest request) {
    logger.error("handleTenantStateChangeFailedException####" + ex.getMessage(), ex);
    ModelAndView viewData = new ModelAndView();
    viewData.setViewName("errors.messagepage");
    viewData.addObject("errorMessage", ex.getMessage());
    return viewData;
  }

  /**
   * @param tenantParam the tenant parameter
   * @param state {@link State}
   * @param memo description for doing change state
   * @param response {@link HttpServletResponse}
   * @return
   */
  @RequestMapping(value = "/{tenantParam}/changeState", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> changeState(@PathVariable String tenantParam,
      @RequestParam(value = "new_state", required = true) State state,
      @RequestParam(value = "memo", required = true) String memo, HttpServletResponse response) {
    logger.debug("###Entering in changeState(tenantId,state,memo,response) method @POST");
    Tenant tenant = tenantService.getTenantByParam("uuid", tenantParam, true);
    if (tenant.getState() == State.TERMINATED) {
      throw new TenantStateChangeFailedException("State of terminated  account cannot be changed.");
    }
    if (tenant.getState() != state) {
      switch (state) {
        case ACTIVE:
        case LOCKED:
        case SUSPENDED:
        case TERMINATED:
        case NEW:
          tenantService.changeState(tenant.getUuid(), state.getName(), null, memo);
          break;
        default:
          throw new TenantStateChangeFailedException("Invalid state change requested");
      }
      String message = "state.changed";
      String stateStr = getStateStr(tenant.getState());
      String messageArgs = stateStr + "," + tenant.getName() + "," + tenant.getName() + " : " + tenant.getParam() + ","
          + stateStr + "," + tenant.getName();

      eventService.createEvent(new Date(), tenant, message, messageArgs, Source.PORTAL, Scope.ACCOUNT, Category.SYSTEM,
          Severity.INFORMATION, true);
    }
    logger.debug("###Exiting changeState(tenantId,state,memo,response) method @POST");
    response.setStatus(HttpStatus.OK.value());
    return getTenantDataHashMap(tenant);
  }

  /**
   * Search tenant by accountId/name
   * 
   * @param form
   * @param result
   * @param tenantId
   * @param accountType
   * @param filterBy
   * @param currentPage
   * @param size
   * @param map
   * @param request
   * @return
   */
  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public String search(SearchForm form, BindingResult result,
      @RequestParam(value = "tenantId", required = false) String tenantId,
      @RequestParam(value = "accountType", required = false) String accountType,
      @RequestParam(value = "filterBy", required = false) String filterBy,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size, ModelMap map, HttpServletRequest request) {

    logger.debug("###Entering in search(form,result,map) method @POST");
    List<AccountType> accountTypes = tenantService.getAllAccountTypes();
    map.addAttribute("accountTypes", accountTypes);

    // Search should take care of filter and accountType next-prev in search - pagination

    AccountType accountTypeObj = null;
    if (StringUtils.isNotEmpty(accountType)) {
      accountTypeObj = tenantService.getAccountTypeById(accountType);
    }
    if (accountType == null) {
      accountType = "";
      setPage(map, Page.ACCOUNT_ALL_ACCOUNTS);
    } else {
      if (!"".equals(accountType)) {
        String accountTypeName = accountTypeObj != null ? accountTypeObj.getName() : "";

        map.addAttribute("accountresult", "true");
        map.addAttribute("selectedTab", accountTypeName);
        map.addAttribute("on", "on");
        if (accountType.equals("1")) {
          setPage(map, Page.ACCOUNT_SYSTEM);
        } else if (accountType.equals("3")) {
          setPage(map, Page.ACCOUNT_RETAIL);
        } else if (accountType.equals("4")) {
          setPage(map, Page.ACCOUNT_CORPORATE);
        } else if (accountType.equals("5")) {
          setPage(map, Page.ACCOUNT_TRIAL);
        }
      } else {
        setPage(map, Page.ACCOUNT_ALL_ACCOUNTS);
      }
    }
    map.addAttribute("selectedAccountType", accountType);

    Map<String, String> filtersMap = new LinkedHashMap<String, String>();
    filtersMap.put("All", messageSource.getMessage("ui.dropdown.account.filter.all", null, getSessionLocale(request)));
    filtersMap.put("0", messageSource.getMessage("ui.dropdown.account.filter.new", null, getSessionLocale(request)));
    filtersMap.put("1", messageSource.getMessage("ui.dropdown.account.filter.active", null, getSessionLocale(request)));
    filtersMap.put("2", messageSource.getMessage("ui.dropdown.account.filter.locked", null, getSessionLocale(request)));
    filtersMap.put("3",
        messageSource.getMessage("ui.dropdown.account.filter.suspended", null, getSessionLocale(request)));
    filtersMap.put("4",
        messageSource.getMessage("ui.dropdown.account.filter.terminated", null, getSessionLocale(request)));
    map.addAttribute("filtersMap", filtersMap);

    if (filterBy == null) {
      filterBy = "All";
    } else {
      if (!filterBy.equals("All")) {
        map.addAttribute("filterresult", "true");
      }
    }
    map.addAttribute("filterBy", filterBy);

    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    int perPageValue = getDefaultPageSize();
    int sizeInt = 0;
    map.addAttribute("searchresult", "true");
    List<Tenant> tenants = new ArrayList<Tenant>();
    Map<String, String> searchKeyMap = new HashMap<String, String>();
    String stateString = null;
    String filterByString = null;
    if (!filterBy.equalsIgnoreCase("All")) {
      State state = State.values()[Integer.parseInt(filterBy)];
      stateString = state.getName();
      filterByString = state.getName();
    }
    // Populate searchKeyMap as per search keys coming from UI
    boolean hasStaticCriteria = false;
    if (StringUtils.isNotEmpty(accountType)) {
      searchKeyMap.put("accountType", accountType);
      hasStaticCriteria = true;
    }
    if (filterBy != null && !filterBy.equals("") && !filterBy.equals("All")) {
      searchKeyMap.put("filterBy", filterByString);
      hasStaticCriteria = true;
    }
    if (form.getfieldName() != null) {
      searchKeyMap.put("fieldName", form.getfieldName());
      map.addAttribute("fieldName", form.getfieldName());
      hasStaticCriteria = true;
    }
    if (form.getName() != null) {
      searchKeyMap.put("name", form.getName());
      map.addAttribute("name", form.getName());
      hasStaticCriteria = true;
    }
    if (form.getAccountId() != null) {
      searchKeyMap.put("accountId", form.getAccountId());
      map.addAttribute("accountId", form.getAccountId());
      hasStaticCriteria = true;
    }

    // Retrieve the tenants satisfying given criteria
    if (hasStaticCriteria) {
      tenants = tenantService.listTenants(currentPageValue, perPageValue, null, null, false, stateString, null, null,
          null, null, searchKeyMap, null, null);
    } else {
      tenants = tenantService.listTenants(currentPageValue, perPageValue, null, null, false, stateString, null,
          (accountTypeObj != null ? accountTypeObj.getId().toString() : null), null, null, null, null, null);
    }
    if (size == null || size.equals("")) {
      if (hasStaticCriteria) {
        sizeInt = tenantService.listTenants(0, 0, null, null, false, stateString, null, null, null, null, searchKeyMap,
            null, null).size();
      } else {
        sizeInt = tenantService.listTenants(0, 0, null, null, false, stateString, null,
            (accountTypeObj != null ? accountTypeObj.getId().toString() : null), null, null, null, null, null).size();
      }
    } else {
      sizeInt = Integer.parseInt(size);
    }

    // Check if there are any custom field criteria
    boolean hasCustomFieldCriteria = false;
    SearchForm searchForm = new SearchForm();
    String customFieldsProp = CustomProxyUtils.getSearchableFields(com.citrix.cpbm.access.Tenant.class);
    String[] customFields = null;
    Map<String, String> customFieldSearchMap = new HashMap<String, String>();
    if (customFieldsProp != null && customFieldsProp.length() != 0) {
      customFields = customFieldsProp.split(",");
      for (String customField : customFields) {
        if (form.getCustomFields().get(customField) != null) {
          customFieldSearchMap.put(CustomProxyUtils.getCustomFieldWithPrefix(customField, Tenant.class), form
              .getCustomFields().get(customField));
          hasCustomFieldCriteria = true;
        }
        searchForm.getCustomFields().put(customField, null);
      }
    }
    if (hasCustomFieldCriteria) {
      tenants = tenantService.filterByCustomFields(customFieldSearchMap, tenants);
    }

    map.addAttribute("tenant", getTenant());
    map.addAttribute("tenants", tenants);
    setPaginationValues(map, perPageValue, currentPageValue, sizeInt, null);

    // As CPBM-UI requires credit balance to show -- Get that for first retrieved tenant
    if (tenants.size() > 0) {
      Tenant firstTenant = tenants.get(0);
      BigDecimal creditBalance = BigDecimal.ZERO;
      if (!firstTenant.getAccountId().equals(TenantService.SYSTEM_TENANT)) {
        creditBalance = getCreditBalance(firstTenant);
      }
      map.addAttribute("creditBalance", creditBalance);
    }

    map.addAttribute("customFieldList", customFields);
    map.addAttribute("searchForm", searchForm);

    logger.debug("###Exiting search(form,result,map) method @POST");
    return "tenant.listing";
  }

  protected ResourceLimitForm getResourceLimit(Tenant tenant) {
    ResourceLimitForm rlForm = new ResourceLimitForm();
    rlForm.setId(tenant.getId());
    rlForm.setDefaultMaxUsers(tenant.getAccountType().getMaxUsers().toString());
    ResourceLimit userrs = resourceLimitService.getResourceLimitByTenantAndType(tenant, ResourceLimit.USER);
    if (userrs != null) {
      rlForm.setMaxUsers("" + userrs.getMax());
    }
    return rlForm;
  }

  /**
   * Retrieve the AccountType by accountName
   * 
   * @param accountTypeName
   * @return
   */
  @RequestMapping(value = "/get_account_type", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> getAccountType(
      @RequestParam(value = "accountTypeName", required = true) String accountTypeName,
      @RequestParam(value = "tenant", required = false) String tenantParam) {
    AccountType accountType = tenantService.getAccountTypeByName(accountTypeName);

    HashMap<String, Object> accountTypeMap = new HashMap<String, Object>();
    accountTypeMap.put("name", accountType.getName());
    accountTypeMap.put("paymentModes", accountType.getPaymentModes());
    accountTypeMap.put("autoPayRequired", accountType.isAutoPayRequired());
    accountTypeMap.put("manualActivation", accountType.isManualActivation());

    accountTypeMap.put("show_deposit_record_message", false);
    if (tenantParam != null) {
      Tenant tenant = tenantService.get(tenantParam);
      if (accountType.isDepositRequired()
          && !(((PaymentGatewayService) connectorManagementService
              .getOssServiceInstancebycategory(ConnectorType.PAYMENT_GATEWAY)).isAccountExistInPaymentGateway(tenant))) {
        DepositRecord depositRecord = tenantService.getDepositRecordByTenant(tenant);
        if (depositRecord == null
            && accountType.getInitialDeposit(tenant.getCurrency().getCurrencyCode()).doubleValue() > 0) {
          accountTypeMap.put("show_deposit_record_message", true);
          accountTypeMap.put(
              "initialDepositAmount",
              tenant.getCurrency().getCurrencyCode() + " "
                  + accountType.getInitialDeposit(tenant.getCurrency().getCurrencyCode()));
        }
      }
    }
    return accountTypeMap;
  }

  /**
   * @param tenant
   * @param tenantParam
   * @param filterBy
   * @param currentPage
   * @param size
   * @param map
   * @param request
   * @return
   */
  @RequestMapping(value = "/notifications", method = RequestMethod.GET)
  public String listNotifications(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "filterBy", required = false) String filterBy,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size, ModelMap map, HttpServletRequest request) {
    logger.debug("Entering in listNotifications");
    User user = getCurrentUser();
    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    int perPageValue = getDefaultPageSize();
    int sizeInt = 0;

    if ((userService.hasAuthority(user, "ROLE_ACCOUNT_CRUD") || userService.hasAuthority(user, "ROLE_ACCOUNT_MGMT"))
        && (Boolean) request.getAttribute("isSurrogatedTenant")) {
      tenant = tenantService.get(tenantParam);
      user = tenant.getOwner();
      setPage(map, Page.CRM_ALL_NOTIFICATIONS);
      map.addAttribute("showUserProfile", true);
    } else {
      user = userService.get(user.getParam());
      setPage(map, Page.HOME_ALL_NOTIFICATIONS);
    }
    map.addAttribute("tenant", tenant);
    map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));
    List<Event> eventLst = new ArrayList<Event>();
    try {
      List<Event> allEvents = eventService.showEventDates(user, filterBy, 0, 0, null, null, null, null);
      sizeInt = allEvents.size();
      if (size == null || size.equals("")) {
        int fromIndex = (currentPageValue - 1) * perPageValue;
        int toIndex = fromIndex + perPageValue;
        if (toIndex > sizeInt) {
          toIndex = sizeInt;
        }
        eventLst = allEvents.subList(fromIndex, toIndex);
      } else {
        eventLst = eventService.showEventDates(user, filterBy, currentPageValue, perPageValue, null, null, null, null);
      }
      map.addAttribute("notificationsList", eventLst);
      map.addAttribute("size", eventLst.size());
    } catch (Exception e) {
      logger.error("Error while retrieving notifications: " + e);
    }

    map.addAttribute("user", user);
    // filters
    // The filters entry are made in ApplicationResources.properties for internationalization.
    // Adding filters need an entry in ApplicationResources.prperties
    List<String> filtersList = new ArrayList<String>();
    Map<String, String> filtersMap = new LinkedHashMap<String, String>();
    filtersMap.put("All",
        messageSource.getMessage("ui.dropdown.notification.filter.all", null, getSessionLocale(request)));
    filtersMap.put("Today",
        messageSource.getMessage("ui.dropdown.notification.filter.today", null, getSessionLocale(request)));
    filtersMap.put("This Week",
        messageSource.getMessage("ui.dropdown.notification.filter.thisWeek", null, getSessionLocale(request)));
    filtersMap.put("Last Week",
        messageSource.getMessage("ui.dropdown.notification.filter.lastWeek", null, getSessionLocale(request)));
    filtersMap.put("Older",
        messageSource.getMessage("ui.dropdown.notification.filter.older", null, getSessionLocale(request)));

    filtersList.add("All");
    filtersList.add("Today");
    filtersList.add("This Week");
    filtersList.add("Last Week");
    filtersList.add("Older");
    map.addAttribute("filtersList", filtersList);
    map.addAttribute("filtersMap", filtersMap);

    if (filterBy == null) {
      filterBy = "All";
    }
    map.addAttribute("filterBy", filterBy);
    // Need for pagination
    map.addAttribute("url", "/portal/portal/tenants/notifications?");
    setPaginationValues(map, perPageValue, currentPageValue, sizeInt, null);
    logger.debug("Leaving in listNotifications");
    return "tenants.notifications";
  }

  /**
   * View Notification
   * 
   * @param date
   * @param map
   * @return
   */
  @RequestMapping(value = "/view_notification", method = RequestMethod.GET)
  public String viewNotification(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "date", required = true) String date,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "perPage", required = false, defaultValue = "10") String perPage,
      @RequestParam(value = "size", required = false) String size, ModelMap map, HttpServletRequest request) {
    logger.debug("### viewNotification method starting...(GET)");
    User user = getCurrentUser();
    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    int perPageValue = perPage != null ? Integer.parseInt(perPage) : 10;
    int sizeInt = 0;

    if ((userService.hasAuthority(user, "ROLE_ACCOUNT_CRUD") || userService.hasAuthority(user, "ROLE_ACCOUNT_MGMT"))
        && (Boolean) request.getAttribute("isSurrogatedTenant")) {
      tenant = tenantService.get(tenantParam);
      user = tenant.getOwner();
      setPage(map, Page.CRM_ALL_NOTIFICATIONS);
      map.addAttribute("showUserProfile", true);
    } else {
      user = userService.get(user.getParam());
      setPage(map, Page.HOME_ALL_NOTIFICATIONS);
    }
    map.addAttribute("tenant", tenant);
    map.addAttribute("selectedDate", date);
    List<Event> eventLst = new ArrayList<Event>();
    try {
      // get date as '%Y-%m-%d' from 'yyyy-MM-dd HH:mm:ss'
      date = date.substring(0, date.indexOf(' '));
      List<Event> allEvents = eventService.showEvents(user, date, 0, 0, null, null, null, null, null, false);
      sizeInt = allEvents.size();
      if (size == null || size.equals("")) {
        int fromIndex = (currentPageValue - 1) * perPageValue;
        int toIndex = fromIndex + perPageValue;
        if (toIndex > sizeInt) {
          toIndex = sizeInt;
        }
        eventLst = allEvents.subList(fromIndex, toIndex);
      } else {
        eventLst = eventService.showEvents(user, date, currentPageValue, perPageValue, null, null, null, null, null,
            false);
      }

      map.addAttribute("notificationsList2", eventLst);
      map.addAttribute("notificationListLen2", eventLst.size());
      map.addAttribute("notificationsList2", eventLst);

    } catch (Exception e) {
      logger.error("Error while retrieving notifications: " + e);
    }

    map.addAttribute("user", user);

    // Need for pagination
    map.addAttribute("url", "/portal/portal/tenants/notifications?");

    int totalpages = 1;
    if (perPageValue > 0) {
      if (sizeInt != 0 && sizeInt % perPageValue == 0) {
        totalpages = sizeInt / perPageValue;
      } else {
        totalpages = (sizeInt / perPageValue) + 1;
      }
    }

    // necessary values for pagination
    map.addAttribute("currentPage2", currentPageValue);
    map.addAttribute("totalpages2", totalpages);
    map.addAttribute("size2", sizeInt);
    map.addAttribute("perPage2", perPageValue);
    map.addAttribute("prevPage2", currentPageValue > 1 ? currentPageValue - 1 : 1);
    map.addAttribute("nextPage2", currentPageValue < totalpages ? currentPageValue + 1 : totalpages);
    map.addAttribute("lowerBound2", currentPageValue + 2 < totalpages ? currentPageValue : (totalpages - 2 < 1 ? 1
        : totalpages - 2));
    map.addAttribute("upperBound2", currentPageValue + 2 < totalpages ? currentPageValue + 2 : totalpages);
    logger.debug("### viewNotification method end");
    return "notifications.view";
  }

  /**
   * list the alert preferences
   * 
   * @param tenantParam
   * @param userParameter
   * @param map
   * @param request
   * @return
   */
  @RequestMapping(value = "/alert_prefs", method = RequestMethod.GET)
  public String viewAlertsDeliveryOptions(@RequestParam(value = "param", required = true) String tenantParam,
      @RequestParam(value = "userParam", required = true) String userParameter, ModelMap map, HttpServletRequest request) {
    logger.debug("###Entering in viewAlertsDeliveryOptions(map) method @GET");
    setPage(map, Page.DASHBOARD_ALL_ALERTS);
    User currentUser = getCurrentUser();
    User effectiveUser = currentUser;

    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    if (userService.hasAnyAuthority(currentUser, "ROLE_ACCOUNT_CRUD", "ROLE_ACCOUNT_MGMT")
        && (Boolean) request.getAttribute("isSurrogatedTenant")) {
      effectiveUser = effectiveTenant.getOwner();
    }
    if (userParameter != null) {
      effectiveUser = userService.get(userParameter);
    }

    List<UserAlertPreferences> alertsPrefs = new ArrayList<UserAlertPreferences>();
    try {
      alertsPrefs = userAlertPreferencesService.listAllUserAlertPreferences(effectiveUser);
      map.addAttribute("alertsPrefs", alertsPrefs);
      map.addAttribute("alertsPrefsSize", alertsPrefs.size());
      map.addAttribute("addAlertEmailLimit",
          config.getIntValue(Names.com_citrix_cpbm_accountManagement_resourceLimits_registered_emailAddresses));
      map.addAttribute("userAlertEmailForm", new UserAlertEmailForm(effectiveUser, AlertType.USER_ALERT_EMAIL));
    } catch (Exception e) {
      logger.error(e);
    }
    map.addAttribute("user", effectiveUser);
    map.addAttribute("tenant", effectiveTenant);

    logger.debug("###Exiting viewAlertsDeliveryOptions(map) method @GET");
    return "alerts.delivery_opts";
  }

  /**
   * @param tenantParam
   * @param userParameter
   * @param notificationPrefId
   * @param map
   * @param request
   * @return
   */
  @RequestMapping(value = "/change_primary_email", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> saveNewPrimaryEmail(@RequestParam(value = "param", required = true) String tenantParam,
      @RequestParam(value = "userParam", required = true) String userParameter,
      @RequestParam(value = "newEmail", required = false) String notificationPrefId, ModelMap map,
      HttpServletRequest request) {
    logger.debug("###Entering in saveNewPrimaryEmail(map) method @POST");
    User currentUser = getCurrentUser();
    User effectiveUser = currentUser;

    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    if (userService.hasAnyAuthority(currentUser, "ROLE_ACCOUNT_CRUD", "ROLE_ACCOUNT_MGMT")
        && (Boolean) request.getAttribute("isSurrogatedTenant")) {
      effectiveUser = effectiveTenant.getOwner();
    }
    if (userParameter != null) {
      effectiveUser = userService.get(userParameter);
    }
    Map<String, String> returnMap = new HashMap<String, String>();
    try {
      UserAlertPreferences userAlertPreferences = userAlertPreferencesService.locateUserAlertPreference(new Long(
          notificationPrefId));
      UserAlertPreferences modifiedUserAlertPreference = userService.setPrimaryEmailAddress(effectiveUser,
          userAlertPreferences);
      returnMap.put("email", modifiedUserAlertPreference.getEmailAddress());
      returnMap.put("isVerified", Boolean.toString(modifiedUserAlertPreference.isEmailVerified()));
      returnMap.put("status", "success");

      // After making the secondary email id as primary the old primary email id should become secondary (DE3540)
      logger.debug("###Exiting saveNewPrimaryEmail(map) method @POST - success");
    } catch (Exception e) {
      logger.debug("###Exiting saveNewPrimaryEmail(map) method @POST - failure", e);
      returnMap.put("status", "failure");
    }
    return returnMap;
  }

  /**
   * list the verified email addresses
   * 
   * @param map
   * @return
   */
  @RequestMapping(value = "/change_primary_email", method = RequestMethod.GET)
  public String viewVerifiedEmails(@RequestParam(value = "param", required = true) String tenantParam,
      @RequestParam(value = "userParam", required = true) String userParameter, ModelMap map, HttpServletRequest request) {
    logger.debug("###Entering in viewVerifiedEmails(map) method @GET");
    setPage(map, Page.DASHBOARD_ALL_ALERTS);
    User currentUser = getCurrentUser();
    User effectiveUser = currentUser;

    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    if (userService.hasAnyAuthority(currentUser, "ROLE_ACCOUNT_CRUD", "ROLE_ACCOUNT_MGMT")
        && (Boolean) request.getAttribute("isSurrogatedTenant")) {
      effectiveUser = effectiveTenant.getOwner();
    }
    if (userParameter != null) {
      effectiveUser = userService.get(userParameter);
    }
    List<UserAlertPreferences> alertsPrefs = new ArrayList<UserAlertPreferences>();
    try {
      alertsPrefs = userAlertPreferencesService.listAllUserAlertPreferences(effectiveUser);
      List<UserAlertPreferences> verifiedAlertPrefs = userAlertPreferencesService.getAllVerifiedEmailPrefs(alertsPrefs);
      map.addAttribute("verifiedAlertsPrefs", verifiedAlertPrefs);
      map.addAttribute("verifiedAlertsPrefsSize", verifiedAlertPrefs.size());
    } catch (Exception e) {
      logger.error(e);
    }
    map.addAttribute("user", effectiveUser);
    map.addAttribute("tenant", effectiveTenant);
    logger.debug("###Exiting viewVerifiedEmails(map) method @GET");
    return "alerts.change_primary_email";
  }

  /**
   * Return alert preference id on successful creation of UserAlertPreference
   * 
   * @param form
   * @param result
   * @return
   */
  @RequestMapping(value = "/alert_prefs", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> saveAlertsDeliveryOptions(
      @Valid @ModelAttribute("userAlertEmailForm") UserAlertEmailForm form, BindingResult result) {
    logger.debug("###Entering in viewAlertsDeliveryOptions(map) method @POST");
    Map<String, String> returnMap = new HashMap<String, String>();
    if (result.hasErrors()) {
      throw new AjaxFormValidationException(result);
    }
    User user = form.getUser();
    user = userService.get(user.getUuid());
    String email = form.getEmail();
    AlertType alertType = form.getAlertType();

    long addAlertEmailLimit = config
        .getIntValue(Names.com_citrix_cpbm_accountManagement_resourceLimits_registered_emailAddresses);
    long alertsPrefsSize = userAlertPreferencesService.getCount(user);
    if (alertsPrefsSize >= addAlertEmailLimit) {
      result.rejectValue("email", "error.verifyAlertEmail.limitReached");
      throw new AjaxFormValidationException(result);
    }

    if (isEmailBlacklisted(email, user.getTenant().getSourceChannel() == null ? null : user.getTenant()
        .getSourceChannel().getParam())) {
      result.rejectValue("email", "signup.emaildomain.blacklist.error");
      throw new AjaxFormValidationException(result);
    }

    if (email.equals(user.getEmail())) {
      result.rejectValue("email", "js.errors.addsecAlert.sameAsPrimaryEmail");
      throw new AjaxFormValidationException(result);
    }

    if (userAlertPreferencesService.checkUserAlertPrefsExists(alertType, user, email)) {
      result.rejectValue("email", "error.verifyAlertEmail.exists");
      throw new AjaxFormValidationException(result);
    }

    UserAlertPreferences alertPref = userAlertPreferencesService.createUserAlertPreference(user, email, alertType);
    returnMap.put("id", alertPref.getId().toString());
    logger.debug("###Exiting viewAlertsDeliveryOptions(map) method @POST - success");
    return returnMap;
  }

  /**
   * Delete the alert preference
   * 
   * @param map
   * @return
   */
  @RequestMapping(value = "/alert_prefs/{prefId}/delete", method = RequestMethod.POST)
  @ResponseBody
  public String deleteAlertsDeliveryOptions(@PathVariable Long prefId, ModelMap map) {
    logger.debug("###Entering in DeleteAlertsDeliveryOptions(map) method @POST");
    try {
      UserAlertPreferences pref = userAlertPreferencesService.locateUserAlertPreference(prefId);
      userAlertPreferencesService.deleteUserAlertPreference(pref);
    } catch (Exception e) {
      logger.debug("###Exiting DeleteAlertsDeliveryOptions(map) method @GET - failure");
      return "failure";
    }

    logger.debug("###Exiting DeleteAlertsDeliveryOptions(map) method @POST - success");
    return "success";
  }

  /**
   * Verify the email for an alert preference
   * 
   * @param map
   * @return
   */
  @RequestMapping(value = "/alert_prefs/{prefId}/verify", method = RequestMethod.POST)
  @ResponseBody
  public String verifyAlertsDeliveryOptions(@PathVariable Long prefId, ModelMap map) {
    logger.debug("###Entering in VerifyAlertsDeliveryOptions(map) method @POST");

    try {
      UserAlertPreferences pref = userAlertPreferencesService.locateUserAlertPreference(prefId);
      userAlertPreferencesService.verifyUserAlertPreference(pref);
    } catch (Exception e) {
      logger.debug("###Exiting VerifyAlertsDeliveryOptions(map) method @POST - failure", e);
      return "failure";
    }

    logger.debug("###Exiting VerifyAlertsDeliveryOptions(map) method @POST - success");
    return "success";
  }

  /**
   * Retrieved the list of all subscriptions associated with tenant
   * 
   * @param tenant
   * @param tenantParam
   * @param budget
   * @param currentPage
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @param request
   * @return
   */
  @RequestMapping(value = "/alerts", method = RequestMethod.GET)
  public String listSubscriptions(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "budget", required = false) String budget,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage, ModelMap map,
      HttpServletRequest request) {
    logger.debug("###Entering in viewSubscriptions(map) method @GET");

    // Setting page and perPage values for pagination
    int page;
    int perPage;
    try {
      page = Integer.parseInt(currentPage);
    } catch (NumberFormatException nFE) {
      page = 1;
    }
    try {
      perPage = getDefaultPageSize();
      if (perPage > 14) {
        perPage = 14;
      }
    } catch (NumberFormatException nFE) {
      perPage = 14;
    }

    User user = getCurrentUser();
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      user = effectiveTenant.getOwner();
      setPage(map, Page.CRM_ALL_ALERTS);
      map.addAttribute("showUserProfile", true);
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));
    } else {
      setPage(map, Page.DASHBOARD_ALL_ALERTS);
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(getCurrentUser()));
    }
    map.addAttribute("tenant", effectiveTenant);
    int totalSubscriptions;
    List<SpendAlertSubscription> subscriptions = null;
    if (user != effectiveTenant.getOwner() && !userService.hasAuthority(user, "ROLE_ACCOUNT_BILLING_ADMIN")) {
      subscriptions = notificationService.getAllSubscriptions(user, page, perPage);
      totalSubscriptions = notificationService.getCountByAccountHolder(user);
      map.addAttribute("subscrptionalerttype", "user");
    } else {
      subscriptions = notificationService.getAllSubscriptions(effectiveTenant, page, perPage);
      totalSubscriptions = notificationService.getCountByAccountHolder(effectiveTenant);
      map.addAttribute("subscrptionalerttype", "tenant");
    }

    CustomAlertForm subscriptionForm = new CustomAlertForm(user);
    subscriptionForm.setThresholdType("percentage");
    map.addAttribute("subscriptionForm", subscriptionForm);

    map.addAttribute("subscriptions", subscriptions);
    map.addAttribute("size", subscriptions.size());

    if (budget != null && budget.toString().equals("true")) {
      TenantForm tenantForm = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(effectiveTenant));
      tenantForm.setSpendLimit(effectiveTenant.getSpendLimit());
      map.addAttribute("tenantForm", tenantForm);
      map.addAttribute("budget", budget);
    } else {
      // budget is not passed in cmd, look it up
      map.addAttribute("spendbudget", tenant.getSpendBudget().toString());
    }
    map.addAttribute("spendbudget_effectiveTenant", effectiveTenant.getSpendBudget().toString());

    // Depending upon size of subscription list draw enable_next option
    if (totalSubscriptions - (page * perPage) > 0) {
      map.addAttribute("enable_next", "True");
    } else {
      map.addAttribute("enable_next", "False");
    }
    map.addAttribute("current_page", page);

    logger.debug("###Exiting viewSubscriptions(map) method @GET");
    return "tenants.alerts";
  }

  /**
   * Retrieves view for specific SpendAlertSubscription
   * 
   * @param id
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @return SpendAlertSubscription details
   */
  @RequestMapping(value = ("/alerts/view"), method = RequestMethod.GET)
  public String viewAlert(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### viewAlert method starting...(POST)");
    SpendAlertSubscription subscription = notificationService.getSpendAlertSubscription(new Long(id));
    Tenant tenant = subscription.getUser().getTenant();
    map.addAttribute("subscription", subscription);
    map.addAttribute("spendbudget", tenant.getSpendBudget().toString());
    map.addAttribute("tenant", tenant);

    AccountHolder target = subscription.getAccountHolder();
    if (target instanceof Tenant) {
      if (subscription.getUser().getTenant().equals(getCurrentUser().getTenant())) {
        map.addAttribute("allowEdit", true);
      }
    } else if (target instanceof User) {
      if (subscription.getUser().equals(getCurrentUser())) {
        map.addAttribute("allowEdit", true);
      }
    }

    // Calculate the exact limit for spend budget
    float spendBudgetAlertCap;
    spendBudgetAlertCap = (tenant.getSpendBudget().floatValue()) * (subscription.getPercentage().floatValue()) / 100;
    map.addAttribute("spend_budget_alert_cap", spendBudgetAlertCap);
    String userEmail = subscription.getUser().getEmail();
    String userPhone = subscription.getUser().getPhone();
    map.addAttribute("user_email", userEmail);
    map.addAttribute("user_phone", userPhone);

    logger.debug("### viewAlert method end");
    return "alerts.view";
  }

  /**
   * Create new Alert GET method
   * 
   * @param tenant
   * @param tenantParam
   * @param map {@link ModelMap}
   * @param request {@link HttpServletRequest}
   * @return
   */
  @RequestMapping(value = ("/alerts/new"), method = RequestMethod.GET)
  public String createSpendAlertSubscription(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpServletRequest request) {
    logger.debug("### createSubscription method starting...(GET)");
    User user = getCurrentUser();
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      user = effectiveTenant.getOwner();
    }
    CustomAlertForm subscriptionForm = new CustomAlertForm(user);
    if (user != effectiveTenant.getOwner() && !userService.hasAuthority(user, "ROLE_ACCOUNT_BILLING_ADMIN")) {
      subscriptionForm.setType("user");
      subscriptionForm.setBudget(user.getSpendBudget());
    } else {
      subscriptionForm.setType("tenant");
      subscriptionForm.setBudget(user.getTenant().getSpendBudget());
    }

    map.addAttribute("tenant", effectiveTenant);

    subscriptionForm.setThresholdType("percentage");
    map.addAttribute("subscriptionForm", subscriptionForm);
    logger.debug("### createSubscription method end");
    return "alerts.new";
  }

  /**
   * Create new Alert POST method.
   * 
   * @param tenant
   * @param tenantParam
   * @param form
   * @param result
   * @param map
   * @param request
   * @return
   */
  @RequestMapping(value = "/alerts/new", method = RequestMethod.POST)
  @ResponseBody
  public SpendAlertSubscription createSpendAlertSubscription(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @ModelAttribute("subscriptionForm") CustomAlertForm form, BindingResult result, ModelMap map,
      HttpServletRequest request) {
    logger.debug("###Entering in createSubscription(form,result,map) method @POST");
    User user = getCurrentUser();
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      user = effectiveTenant.getOwner();
    }
    // Spend Limit money value type
    int subscriptionType = 1;
    SpendAlertSubscription subscription = new SpendAlertSubscription();
    subscription.setSubscriptionType(subscriptionType);
    AccountHolder accountHolder = null;
    if (form.getType().equals("tenant")) {
      accountHolder = effectiveTenant;
    } else {
      accountHolder = user;
    }
    // Validation code.
    AlertValidator validator = new AlertValidator();
    validator.validate(form, result);
    if (result.hasErrors()) {
      throw new AjaxFormValidationException(result);
    }
    if (effectiveTenant != null) {
      List<SpendAlertSubscription> spendAlertSubscription = notificationService.getAllSubscriptions(effectiveTenant);
      if (CollectionUtils.isNotEmpty(spendAlertSubscription)) {
        for (SpendAlertSubscription spendAlertSubscription2 : spendAlertSubscription) {
          if (spendAlertSubscription2.getPercentage().doubleValue() == form.getTenantPercentage().doubleValue()) {
            result.rejectValue("tenantPercentage", "js.errors.tenantPercentage.validatePercentage");
            throw new AjaxFormValidationException(result);
          }
        }
      }
    }

    subscription.setPercentage(form.getTenantPercentage());
    subscription.setAccountHolder(accountHolder);
    subscription.setUser(user);
    // add new subscription
    subscription = notificationService.saveSubscription(subscription);
    return subscription;
  }

  /**
   * Soft delete the Subscription for given id
   * 
   * @param subscriptionId
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/alerts/remove", method = RequestMethod.GET)
  public String removeSubscription(@RequestParam(value = "Id", required = true) String subscriptionId, ModelMap map) {
    logger.debug("###Entering removeSubscription method @POST");
    // removes subscription
    notificationService.removeSubscription(notificationService.getSpendAlertSubscription(new Long(subscriptionId)));
    map.clear();
    logger.debug("###Leaving removeSubscription method @POST");
    return "success";
  }

  /**
   * Edit the Spend Alert Subscription for given id
   * 
   * @param id
   * @param tenant
   * @param tenantParam
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @param request
   * @return
   */
  @RequestMapping(value = ("/alerts/edit"), method = RequestMethod.GET)
  public String editSpendAlertSubscription(@RequestParam(value = "Id", required = true) String id,
      @ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpServletRequest request) {
    logger.debug("### editProduct method starting...(GET)");
    SpendAlertSubscription subscription = notificationService.getSpendAlertSubscription(new Long(id));
    User user = getCurrentUser();
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      user = effectiveTenant.getOwner();
    }
    map.addAttribute("tenant", effectiveTenant);
    CustomAlertForm subscriptionForm = new CustomAlertForm(user);
    subscriptionForm.setThresholdType("percentage");
    subscriptionForm.setTenantPercentage(subscription.getPercentage());
    map.addAttribute("subscriptionForm", subscriptionForm);
    map.addAttribute("subscriptionId", id);
    logger.debug("### editProduct method end");
    return "alerts.edit";
  }

  /**
   * deletes subscription
   * 
   * @param subscriptionId
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @param form
   * @param result
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/alerts/edit", method = RequestMethod.POST)
  public SpendAlertSubscription editSpendAlertSubscription(
      @RequestParam(value = "Id", required = true) String subscriptionId,
      @RequestParam(value = "newValue", required = true) String newVal,
      @ModelAttribute("subscriptionForm") CustomAlertForm form, BindingResult result, ModelMap map,
      HttpServletRequest request) {
    logger.debug("###Entering editSubscription method @POST");
    SpendAlertSubscription subscription = notificationService.getSpendAlertSubscription(new Long(subscriptionId));
    if (newVal == null) {
      result.rejectValue("tenantPercentage", "js.errors.tenantPercentage.required");
      throw new AjaxFormValidationException(result);
    }
    BigDecimal percentage = new BigDecimal(newVal);
    if ((percentage.intValue() < 0) || (percentage.intValue() > 100)) {
      result.rejectValue("tenantPercentage", "js.errors.tenantPercentage.percentage");
      throw new AjaxFormValidationException(result);
    }
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    form.setTenantPercentage(new BigDecimal(newVal));
    if (effectiveTenant != null) {
      List<SpendAlertSubscription> spendAlertSubscription = notificationService.getAllSubscriptions(effectiveTenant);
      if (CollectionUtils.isNotEmpty(spendAlertSubscription)) {
        for (SpendAlertSubscription spendAlertSubscription2 : spendAlertSubscription) {
          if (spendAlertSubscription2.getPercentage().doubleValue() == form.getTenantPercentage().doubleValue()) {
            result.rejectValue("tenantPercentage", "js.errors.tenantPercentage.validatePercentage");
            throw new AjaxFormValidationException(result);
          }
        }
      }
    }
    subscription.setPercentage(percentage);
    subscription = notificationService.saveSubscription(subscription);
    logger.debug("###Leaving editSubscription method @POST");

    return subscription;
  }

  /**
   * Set account budget GET method.
   * 
   * @param tenant {@link Tenant}
   * @param tenantParam
   * @param map {@link ModelMap}
   * @param request {@link HttpServletRequest}
   * @return
   */

  @RequestMapping(value = ("/set_account_budget"), method = RequestMethod.GET)
  public String setAccountBudget(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpServletRequest request) {
    logger.debug("### setAccountBudget method starting...(GET)");
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    TenantForm tenantForm = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(effectiveTenant));
    tenantForm.setSpendLimit(effectiveTenant.getSpendBudget());
    map.addAttribute("tenantForm", tenantForm);
    map.addAttribute("tenant", effectiveTenant);
    logger.debug("### setAccountBudget method end");
    return "alerts.setaccountbudget";
  }

  /**
   * Set Account Budget POST method.
   * 
   * @param tenant
   * @param tenantParam
   * @param form {@link TenantForm}
   * @param map {@link ModelMap}
   * @return
   */
  @RequestMapping(value = "/set_account_budget", method = RequestMethod.POST)
  @ResponseBody
  public String setAccountBudget(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenantparam", required = false) String tenantParam,
      @ModelAttribute("tenantForm") final TenantForm form, ModelMap map) {
    logger.debug("###Entering in setAccountBudget(map) method @POST");
    Tenant effectiveTenant = tenantService.get(tenantParam);
    User user = effectiveTenant.getOwner();
    effectiveTenant.setSpendBudget(form.getTenant().getObject().getSpendBudget());
    tenantService.save(effectiveTenant);
    List<SpendAlertSubscription> subscriptions = notificationService.getAllSubscriptions(effectiveTenant);
    map.addAttribute("subscriptions", subscriptions);
    map.addAttribute("size", subscriptions.size());
    map.addAttribute("user", user);
    return "success";
  }

  protected void parseResult(BindingResult result, ModelMap map) {
    if (result.getFieldErrors().size() > 0) {
      List<String> errorMsgList = new ArrayList<String>();
      for (FieldError fieldError : result.getFieldErrors()) {
        String fieldName = fieldError.getField();
        if (fieldName.contains(".")) {
          fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
        }
        errorMsgList.add(fieldName + " field value '" + fieldError.getRejectedValue() + "' is not valid.");
      }

      map.addAttribute("errorMsgList", errorMsgList);
      map.addAttribute("errormsg", true);
    }
  }

  @Override
  protected String writeMultiPartFileToLocalFile(String imageFolder, String relativeImageDir,
      MultipartFile multipartFile) throws IOException {
    File file = new File(FilenameUtils.concat(imageFolder, relativeImageDir));
    if (!file.exists()) {
      file.mkdir();
    }

    String fileRelativePath = FilenameUtils.concat(relativeImageDir,
        FilenameUtils.getName(multipartFile.getOriginalFilename()));
    String logoFilelocalPath = FilenameUtils.concat(imageFolder, fileRelativePath);
    FileOutputStream outputStream = new FileOutputStream(logoFilelocalPath);
    InputStream inputStream = multipartFile.getInputStream();
    byte buf[] = new byte[1024];
    int len;
    while ((len = inputStream.read(buf)) > 0) {
      outputStream.write(buf, 0, len);
    }
    outputStream.close();
    inputStream.close();
    return fileRelativePath;
  }

  @RequestMapping(value = "/{tenantParam}/delete", method = RequestMethod.POST)
  @ResponseBody
  public String delete(@PathVariable String tenantParam, ModelMap map) {
    logger.debug("###Entering in delete(@PathVariable String tenantParam, ModelMap map) method @POST");
    Tenant tenant = tenantService.get(tenantParam);
    try {
      tenantService.cleanTenant(tenant, "Deleting tenant id:" + tenant.getId());

    } catch (Exception e) {
      logger.error("###Exception whilst deleting tenant id:" + tenant.getId(), e);
    }
    logger.debug("###Exitng in delete(@PathVariable String tenantParam, ModelMap map) method @POST");
    return "success";
  }

  protected Map<String, String> getTenantDataHashMap(Tenant tenant) {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("param", tenant.getParam());
    map.put("name", tenant.getName());
    map.put("accountType", tenant.getAccountType().getDisplayName());
    map.put("accountId", tenant.getAccountId());
    map.put("state", tenant.getState().getName());
    map.put("stateOrdinal", "" + tenant.getState().ordinal());
    map.put("username", tenant.getOwner().getUsername());
    return map;
  }

  @RequestMapping(value = "/stateChanges", method = RequestMethod.GET)
  public String listStateChanges(@RequestParam(value = "tenant", required = true) String tenantParam, ModelMap map) {
    Tenant tenant = tenantService.get(tenantParam);
    List<BusinessTransaction> transactionHistory = businessTransactionService.getAllBusinessTransactions(tenant, 0, 0);
    map.addAttribute("transactionHistory", transactionHistory);
    map.addAttribute("tenant", tenant);
    return "tenant.state.changes";
  }

  private String getAccountLimits(ModelMap map, String instanceParam, String tenantParam) {
    logger.debug("###Entering getAccountLimits method...(GET)");
    Tenant tenant = tenantService.get(tenantParam);

    Map<String, String> resLimitMap = connectorConfigurationManager.getAccountControls(tenant, instanceParam);
    Map<String, String> resUserLimitMap = connectorConfigurationManager.getUserControls(tenant.getOwner(),
        instanceParam);
    if (resUserLimitMap != null && !resLimitMap.isEmpty()) {
      resLimitMap.putAll(resUserLimitMap);
    }
    ServiceInstance instance = connectorConfigurationManager.getInstance(instanceParam);
    if (resLimitMap != null && !resLimitMap.isEmpty()) {
      Set<AccountControlServiceConfigMetadata> properties = instance.getService()
          .getAccountControlServiceConfigMetadata();
      List<AccountControlServiceConfigMetadata> accountControlProperties = new ArrayList<AccountControlServiceConfigMetadata>();

      for (AccountControlServiceConfigMetadata accountControlServiceConfigMetadata : properties) {
        if (resLimitMap.get(accountControlServiceConfigMetadata.getName()) != null) {
          accountControlProperties.add(accountControlServiceConfigMetadata);
        }
      }

      Collections.sort(accountControlProperties);

      map.addAttribute("account_control_edit_properties", accountControlProperties);
      map.addAttribute("resourceLimitsMap", resLimitMap);
    }

    map.addAttribute("tenantid", tenant.getId());
    map.addAttribute("tenantuuid", tenantParam);
    map.addAttribute("instance", instance);

    logger.debug("### Exiting getAccountLimits method...(GET)");
    return "tenant.showlimits";
  }

  private String editAccountLimits(String tenantParam, String instanceParam, ModelMap map) {
    logger.debug("### Entering editAccountLimits method...(GET)");
    Tenant tenant = tenantService.get(tenantParam);
    Map<String, String> resLimitMap = connectorConfigurationManager.getAccountControls(tenant, instanceParam);
    Map<String, String> resUserLimitMap = connectorConfigurationManager.getUserControls(tenant.getOwner(),
        instanceParam);
    if (resUserLimitMap != null && !resLimitMap.isEmpty()) {
      resLimitMap.putAll(resUserLimitMap);
    }
    ServiceInstance instance = connectorConfigurationManager.getInstance(instanceParam);

    if (resLimitMap != null && !resLimitMap.isEmpty()) {
      Set<AccountControlServiceConfigMetadata> properties = instance.getService()
          .getAccountControlServiceConfigMetadata();
      List<AccountControlServiceConfigMetadata> accountControlProperties = new ArrayList<AccountControlServiceConfigMetadata>();

      for (AccountControlServiceConfigMetadata accountControlServiceConfigMetadata : properties) {
        if (resLimitMap.get(accountControlServiceConfigMetadata.getName()) != null) {
          accountControlProperties.add(accountControlServiceConfigMetadata);
        }
      }

      Collections.sort(accountControlProperties);

      map.addAttribute("account_control_edit_properties", accountControlProperties);
      map.addAttribute("resLimitMap", resLimitMap);
    }

    map.addAttribute("service", instance.getService());
    map.addAttribute("instance", instance);
    map.addAttribute("tenantuuid", tenantParam);

    logger.debug("### Exiting editAccountLimits method...(GET)");
    return "tenant.editaccountlimits";
  }

  private Map<String, String> editAccountLimits(String tenantParam, String instanceParam, String configProperties,
      HttpServletRequest request) {
    logger.debug("### Entering edit account limits method...(POST)");

    HashMap<String, String> map = new HashMap<String, String>();
    HashMap<String, String> limitMap = new HashMap<String, String>();
    ServiceInstance instance = connectorConfigurationManager.getInstance(instanceParam);
    Service service = instance.getService();
    Set<AccountControlServiceConfigMetadata> properties = service.getAccountControlServiceConfigMetadata();
    Map<String, AccountControlServiceConfigMetadata> mapOfServiceMetaData = new HashMap<String, AccountControlServiceConfigMetadata>();
    for (AccountControlServiceConfigMetadata serviceConfigurationMetadata : properties) {
      mapOfServiceMetaData.put(serviceConfigurationMetadata.getName(), serviceConfigurationMetadata);
    }
    try {
      JSONArray jsonArray = new JSONArray(configProperties);
      for (int index = 0; index < jsonArray.length(); index++) {
        JSONObject jsonObj = jsonArray.getJSONObject(index);
        String fieldName = jsonObj.get("name").toString();
        String fieldValue = jsonObj.get("value").toString();

        AccountControlServiceConfigMetadata configurationMetadata = mapOfServiceMetaData.get(fieldName);
        map.put("validationResult", CssdkConstants.SUCCESS);
        String validationJson = configurationMetadata.getValidation();

        String validationResult = ValidationUtil.valid(validationJson, fieldName, fieldValue, messageSource,
            getSessionLocale(request));
        if (!CssdkConstants.SUCCESS.equals(validationResult)) {
          map.put("validationResult", validationResult);
          return map;
        }
        limitMap.put(fieldName, fieldValue);
      }
      Tenant tenant = tenantService.get(tenantParam);
      getAccountLifecycleHandler(instanceParam).setControls(tenant, limitMap);
      getUserLifecycleHandler(instanceParam).setControls(tenant.getOwner(), limitMap);
      map.put("result", CssdkConstants.SUCCESS);
    } catch (CloudServiceException e) {
      throw e;
    } catch (Exception ex) {
      map.put("result", CssdkConstants.FAILURE);
      logger.error("Exception in editing account controls ", ex);
      map.put("message", ex.getMessage());
    }

    logger.debug("### Exiting edit account limits method...(POST)");
    return map;
  }

  @RequestMapping(value = "/list_account_limits", method = RequestMethod.GET)
  public String listAccountLimits(ModelMap map,
      @RequestParam(value = "tenantParam", required = true, defaultValue = "1") String tenantParam,
      @RequestParam(value = "instanceParam", required = false) String instanceParam) {
    return getAccountLimits(map, instanceParam, tenantParam);
  }

  private AccountLifecycleHandler getAccountLifecycleHandler(String instanceParam) {
    CloudConnector connector = (CloudConnector) connectorManagementService.getServiceInstance(instanceParam);
    return connector != null ? connector.getAccountLifeCycleHandler() : null;
  }

  private UserLifecycleHandler getUserLifecycleHandler(String instanceParam) {
    CloudConnector connector = (CloudConnector) connectorManagementService.getServiceInstance(instanceParam);
    return connector != null ? connector.getUserLifeCycleHandler() : null;
  }

  @RequestMapping(value = "/edit_account_limits", method = RequestMethod.GET)
  public String editAccountlimits(@RequestParam(value = "tenantuuid", required = true) String tenantuuid,
      @RequestParam(value = "instanceParam", required = false) String instanceParam, ModelMap map) {
    return editAccountLimits(tenantuuid, instanceParam, map);
  }

  @RequestMapping(value = "/validate_user_limit", method = RequestMethod.GET)
  @ResponseBody
  public String validateUserLimit(@RequestParam(value = "userlimit", required = true) String userLimit,
      @RequestParam(value = "tenantid", required = true) String tenantParam) {
    Tenant tenant = tenantService.get(tenantParam);
    try {
      Long userLimitValue = Long.parseLong(userLimit);
      tenantService.validateUserLimit(tenant, userLimitValue);
    } catch (UserLimitServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage());
    } catch (NumberFormatException e) {
      String message = messageSource.getMessage("js.errors.tenants.resourceLimitForm.unacceptableMaxUserValue", null,
          getCurrentUser().getLocale());
      throw new InvalidAjaxRequestException(message);
    }
    return "true";
  }

  @RequestMapping(value = ("/edit_account_limits"), method = RequestMethod.POST)
  public Map<String, String> editAccountlimits(
      @RequestParam(value = "tenantParam", required = true) String tenantParam,
      @RequestParam(value = "instanceParam", required = true) String instanceParam,
      @RequestParam("configProperties") final String configProperties, HttpServletRequest request) {
    return editAccountLimits(tenantParam, instanceParam, configProperties, request);
  }

  @RequestMapping(value = "/{channelParam}/list_currencies", method = RequestMethod.GET)
  public String listCurrencies(@PathVariable String channelParam, ModelMap map) {
    TenantForm tenantForm = (TenantForm) map.get("account");
    List<CurrencyValue> currenciesList = channelService.listCurrencies(channelParam);
    tenantForm.setCurrencyValueList(currenciesList);
    return "tenant.channel.currencies";
  }

  @RequestMapping(value = "/{channelParam}/verify_channel_promocode", method = RequestMethod.GET)
  @ResponseBody
  public String isPromoCodeAvailable(@PathVariable String channelParam, ModelMap map) {
    Channel channel = channelService.getChannel(channelParam);
    if (promotionService.isChannelContainsPromotion(channel)) {
      return "success";
    }
    return "failure";
  }

  @RequestMapping(value = "/{channelParam}/default_locale", method = RequestMethod.GET)
  @ResponseBody
  public String getDefaultLocale(@PathVariable String channelParam) {
    Channel channel = channelService.getChannel(channelParam);
    return channelService.getDefaultLocale(channel);
  }

  @RequestMapping(value = "/{channelParam}/filtered_countries", method = RequestMethod.GET)
  @ResponseBody
  public List<Country> getChannelCountries(@PathVariable String channelParam) {
    logger.debug("###getChannelCountries() method starting...." + channelParam);
    List<Country> countries = countryService.getCountries(null, null, null, null, null, null, null);
    return getFilteredCountryList(countries, channelParam, true);
  }

  /**
   * Check the given trialCode is valid or not
   * 
   * @param trialCode
   * @param channelParam
   * @return
   */
  @RequestMapping(value = "/validate_trial")
  @ResponseBody
  public String validatePromotionCodeforTenant(@RequestParam("trialCode") final String trialCode,
      @RequestParam("channelParam") final String channelParam, @RequestParam("accountTypeId") final String accountTypeId) {

    logger.debug("###validatePromotionCode() method starting...." + trialCode);
    CampaignPromotion promotion = promotionService.locatePromotionByToken(trialCode);
    if (promotion == null) {
      return Boolean.FALSE.toString();
    }
    AccountType targetAccountType = null;
    if (StringUtils.isNotBlank(accountTypeId)) {
      targetAccountType = accountTypeService.locateAccountTypeById(accountTypeId);
      if (!targetAccountType.isTrial() && promotion.isTrial()) {
        return Boolean.FALSE.toString();
      }
    }
    if (promotion.getSupportedChannels() != null && promotion.getSupportedChannels().size() > 0) {
      Channel channel = channelService.getChannel(channelParam);
      return promotionService.isValidPromotion(trialCode, channel.getCode()).toString();
    }
    return Boolean.FALSE.toString();
  }

  /**
   * Retrieve the map containing service name, service uuid, service instance name
   * 
   * @param password
   * @param request
   * @return
   */
  @RequestMapping(value = "/get_api_details", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> getApiCredentials(
      @RequestParam(value = "password", required = true) final String password, HttpServletRequest request) {
    Map<String, Object> mapresult = new HashMap<String, Object>();
    logger.debug("###Entering in getapidetails(password,session) method @POST");
    User user = getCurrentUser();
    List<Map<String, String>> credentialList = new ArrayList<Map<String, String>>();
    try {
      credentialList = connectorConfigurationManager.getApiCredentials(user, getTenant(), password,
          getSessionLocale(request), true);
    } catch (Exception e) {
      logger.error(e);
    }
    mapresult.put("tenantCredentialList", credentialList);
    mapresult.put("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));

    boolean isSystemRoot = user.equals(userService.getSystemUser(Handle.ROOT));
    boolean authenticated = false;
    if ("cas".compareToIgnoreCase(config.getAuthenticationService()) == 0) {
      authenticated = user.authenticate(password);
      if (!isSystemRoot && config.getBooleanValue(Names.com_citrix_cpbm_portal_directory_service_enabled)
          && !authenticated) {
        authenticated = userService.authenticateUserInDirectoryService(user.getUsername(), password);
      }
    } else {
      if (!isSystemRoot && config.getBooleanValue(Names.com_citrix_cpbm_portal_directory_service_enabled)) {
        authenticated = userService.authenticateUserInDirectoryService(user.getUsername(), password);
      } else {
        authenticated = user.authenticate(password);
      }
    }
    mapresult.put("success", authenticated);

    logger.debug("###Exiting getapidetails(password,map) method @POST returning  for " + user.getName());
    return mapresult;
  }

  private BigDecimal getCreditBalance(Tenant tenant) {
    AccountStatement accountStatement = billingAdminService.getOrCreateProvisionalAccountStatement(tenant);
    List<SalesLedgerRecord> salesLedgerRecordList = accountStatement.getSalesLedgerRecords();
    if (salesLedgerRecordList.size() > 0) {
      return salesLedgerRecordList.get(0).getBalanceAmount();
    } else {
      return accountStatement.getBalanceForwardAmount().negate();
    }
  }

  @SuppressWarnings("unchecked")
  @ResponseBody
  @RequestMapping(value = "/enable_service", method = RequestMethod.POST)
  public ModelMap enableService(@RequestParam(value = "tenantparam", required = true) String tenantParam,
      @RequestParam(value = "instanceUuid", required = true) String serviceInstanceUuid,
      @RequestParam(value = "instanceProperty", required = true) String instancePropertyJson,
      @RequestParam(value = "enableAllUsers", required = false, defaultValue = "false") boolean enableAllUsers,
      ModelMap map, HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {

    Tenant tenant = tenantService.get(tenantParam);
    Map<String, String> instanceProperty = new HashMap<String, String>();
    if (StringUtils.isNotBlank(instancePropertyJson)) {
      instanceProperty = JSONUtils.fromJSONString(instancePropertyJson, Map.class);
    }
    ServiceInstance serviceInstance = connectorManagementService.getInstance(serviceInstanceUuid);
    List<BusinessTransaction> businessTransactions = businessTransactionService.getBusinessTransactions(tenant,
        serviceInstance.getId(), BusinessTransaction.Type.cloudServiceActivation, new BusinessTransaction.State[] {
          BusinessTransaction.State.SUCCESS
        }, 0, 0);

    tenantService.setServiceInstanceTenantConfiguration(tenant, serviceInstanceUuid, enableAllUsers, instanceProperty);
    // Directly call enable service if the BT for the Service instance is already completed
    if (businessTransactions.size() > 0) {
      logger.info("######@@@@@ got business transaction for in success for " + tenant.getName()
          + " for service instance " + serviceInstance.getName());
      tenantService.enableService(tenant, serviceInstanceUuid, instanceProperty, enableAllUsers);
    } else {
      BusinessTransaction businessTransaction = new CloudServiceActivationTransaction(serviceInstance, tenant,
          instancePropertyJson, enableAllUsers);
      businessTransactionService.trigger(businessTransaction);
      logger.info("######@@@@@ added cloudServiceActivation business transaction for " + tenant.getName()
          + " for service instance " + serviceInstance.getName());
    }
    map.addAttribute("result", CssdkConstants.SUCCESS);

    return map;
  }

  @ResponseBody
  @RequestMapping(value = "/set_autoprovision", method = RequestMethod.POST)
  public ModelMap setAutoProvisioning(@RequestParam(value = "tenantparam", required = true) String tenantParam,
      @RequestParam(value = "instanceUuid", required = true) String serviceInstanceUuid,
      @RequestParam(value = "enableAllUsers", required = false, defaultValue = "false") boolean enableAllUsers,
      ModelMap map, HttpServletRequest request) {

    Tenant tenant = tenantService.get(tenantParam);
    try {
      ServiceInstanceTenantConfiguration sitc = tenantService.getServiceInstanceTenantConfiguration(tenant,
          serviceInstanceUuid);
      tenantService.setServiceInstanceTenantConfiguration(tenant, serviceInstanceUuid, enableAllUsers, sitc.getData());
      map.addAttribute("result", CssdkConstants.SUCCESS);
    } catch (Exception e) {
      map.addAttribute("result", CssdkConstants.FAILURE);
    }
    return map;
  }

  protected List<CurrencyValue> getCurrencies() {
    List<CurrencyValue> currenciesList = new ArrayList<CurrencyValue>();
    List<Channel> channels = channelService.getChannels(null, null, null);
    if (CollectionUtils.isNotEmpty(channels)) {
      Channel channel = channels.get(0);
      Catalog catalog = channel.getCatalog();
      if (catalog != null) {
        for (SupportedCurrency sc : catalog.getSupportedCurrenciesByOrder()) {
          currenciesList.add(sc.getCurrency());
        }
      }
    }
    return currenciesList;
  }

  private String getStateStr(State state) {
    String code = "tenant.state." + state.getName().toLowerCase();

    return messageSource.getMessage(code, null, actorService.getActor().getLocale());
  }
}
