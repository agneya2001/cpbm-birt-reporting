/*
 * Copyright Â© 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid
 * license agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citrix.cpbm.core.workflow.service.BusinessTransactionService;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.citrix.cpbm.platform.spi.ExtendedSubscriptionLifecycleHandler;
import com.citrix.cpbm.platform.spi.SubscriptionLifecycleHandler;
import com.vmops.config.BillingPostProcessor;
import com.vmops.internal.service.ActorService;
import com.vmops.internal.service.CustomFieldService;
import com.vmops.internal.service.EventService;
import com.vmops.internal.service.PaymentGatewayService;
import com.vmops.internal.service.SubscriptionService;
import com.vmops.internal.service.UsageService;
import com.vmops.model.AccountType;
import com.vmops.model.Address;
import com.vmops.model.CreditCard;
import com.vmops.model.DepositRecord;
import com.vmops.model.Event.Category;
import com.vmops.model.Event.Scope;
import com.vmops.model.Event.Severity;
import com.vmops.model.Event.Source;
import com.vmops.model.Invoice;
import com.vmops.model.PendingChange;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.ServiceFilter;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceResourceTypeGroupComponent;
import com.vmops.model.ServiceResourceTypeProperty;
import com.vmops.model.Subscription;
import com.vmops.model.Subscription.State;
import com.vmops.model.SubscriptionHandle;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.model.billing.AccountStatement;
import com.vmops.model.billing.PaymentTransaction;
import com.vmops.model.billing.SalesLedgerCreditRecord;
import com.vmops.model.billing.SalesLedgerRecord;
import com.vmops.model.billing.SalesLedgerRecord.Type;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.EmailTemplateService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.ReportService;
import com.vmops.service.TenantService;
import com.vmops.service.billing.BillingAdminService;
import com.vmops.service.billing.BillingService;
import com.vmops.service.exceptions.AccountConversionFailedException;
import com.vmops.service.exceptions.AjaxFormValidationException;
import com.vmops.service.exceptions.BillingAdminServiceException;
import com.vmops.service.exceptions.BillingServiceException;
import com.vmops.service.exceptions.CloudServiceException;
import com.vmops.service.exceptions.CreditCardFraudCheckException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.PaymentGatewayServiceException;
import com.vmops.service.exceptions.TenantStateChangeFailedException;
import com.vmops.utils.Transformer;
import com.vmops.utils.pdf.PdfGenerator;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.BillingInfoForm;
import com.vmops.web.forms.DepositRecordForm;
import com.vmops.web.forms.SetAccountTypeForm;
import com.vmops.web.forms.SubscriptionSearchForm;
import com.vmops.web.interceptors.UserContextInterceptor;
import com.vmops.web.validators.DepositRecordValidator;

/**
 * Handles billing requests
 * 
 * @author Fatima
 */

public abstract class AbstractBillingController extends AbstractAuthenticatedController {

  protected static final String SEPARATOR = " ";

  protected static final String BILLING_START_DATE = "BILLING_START_DATE";

  protected static final String BILLING_END_DATE = "BILLING_END_DATE";

  protected static final String BILLING_EXPORT_FILE_EXTENSION = "xml";

// CHECKSTYLE:OFF
  @Autowired
  protected BillingAdminService billingAdminService;

// CHECKSTYLE:ON
  @Autowired
  private BillingPostProcessor billingPostProcessor;

// CHECKSTYLE:OFF
  @Autowired
  protected SubscriptionService subscriptionService;

// CHECKSTYLE:ON
  @Autowired
  private CustomFieldService customFieldService;

// CHECKSTYLE:OFF
  @Autowired
  protected ReportService reportService;

  @Autowired
  protected ConfigurationService configurationService;

  @Autowired
  protected ActorService actorService;

  @Autowired
  protected UsageService usageService;

  @Autowired
  protected PdfGenerator pdfGenerator;

  @Autowired
  protected EmailTemplateService emailTemplateService;

  @Autowired
  protected EventService eventService;

  @Autowired
  protected BillingService billingService;

  @Autowired
  protected ProductBundleService productBundleService;

  @Autowired
  protected ConnectorManagementService connectorManagementService;

  @Autowired
  protected ChannelService channelService;

// CHECKSTYLE:ON
  private final String utiltyInvoices = "__UTILITY__CHARGES__INVOICES__";

  private final String serviceBundleInvoices = "__SERVICE__BUNDLE__INVOICES__";

  private Logger logger = Logger.getLogger(AbstractBillingController.class);

  @Autowired
  private BusinessTransactionService businessTransactionService;

// CHECKSTYLE:OFF
  @Autowired
  protected ConnectorConfigurationManager connectorConfigurationManager;

// CHECKSTYLE:ON
  protected boolean hasFullBillingView(User user) {
    // only master user has ROLE_ACCOUNT_ADMIN, certain system users has finance role.
    return userHasPrivilegeOf(user, "ROLE_ACCOUNT_ADMIN") || userHasFinancePrivilege(user)
        || userHasBillingPrivilege(user);
  }

  @RequestMapping(value = "/utility_charges", method = RequestMethod.GET)
  public String showUtilityCharges(@RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "id", required = false) String uuid,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "useruuid", required = false) String useruuid,
      @RequestParam(value = "instanceuuid", required = false) String instanceuuid,
      @RequestParam(value = "productBundleID", required = false) Long productBundleID,
      @RequestParam(value = "resourceTypeId", required = false) Long resourceTypeId, ModelMap map,
      HttpServletRequest request) {

    logger.debug("Entering showUtilityCharges for id : " + uuid);
    return fetchSubscriptions(currentPage, uuid, state, useruuid, instanceuuid, productBundleID, resourceTypeId, map,
        request, true);
  }

  @RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
  public String showSubscriptions(@RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "id", required = false) String uuid,
      @RequestParam(value = "state", required = false) String state,
      @RequestParam(value = "useruuid", required = false) String useruuid,
      @RequestParam(value = "instanceuuid", required = false) String instanceuuid,
      @RequestParam(value = "productBundleID", required = false) Long productBundleID,
      @RequestParam(value = "resourceTypeId", required = false) Long resourceTypeId, ModelMap map,
      HttpServletRequest request) {

    logger.debug("Entering showSubscriptions for id : " + uuid);
    return fetchSubscriptions(currentPage, uuid, state, useruuid, instanceuuid, productBundleID, resourceTypeId, map,
        request, false);
  }

  private String fetchSubscriptions(String currentPage, String uuid, String state, String useruuid,
      String instanceuuid, Long productBundleID, Long resourceTypeId, ModelMap map, HttpServletRequest request,
      boolean utilityViewSelected) {

    boolean isPayAsYouGoChosen = config.getBooleanValue(Names.com_citrix_cpbm_catalog_payAsYouGoMode);
    map.addAttribute("isPayAsYouGoChosen", isPayAsYouGoChosen);

    if (isPayAsYouGoChosen) {
      utilityViewSelected = true;
    }

    int page;
    int perPage;

    try {
      page = Integer.parseInt(currentPage);
    } catch (NumberFormatException nFE) {
      page = 1;
    }

    try {
      perPage = getDefaultPageSize();
    } catch (NumberFormatException nFE) {
      perPage = 14;
    }
    State subscriptionState = null;
    if (state == null) {
      subscriptionState = State.ACTIVE;
      state = subscriptionState.getName();
    } else if (!state.equalsIgnoreCase("All")) {
      subscriptionState = State.getState(state);
    }
    User currentUser = getCurrentUser();
    boolean showUserProfile = false;
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      showUserProfile = true;
      setPage(map, Page.CRM_USAGE_BILLING_SUBSCRIPTION);
      map.addAttribute("userHasCloudServiceAccount",
          userService.isUserHasAnyActiveCloudService(effectiveTenant.getOwner()));
    } else {
      setPage(map, Page.USAGE_BILLING_SUBSCRIPTION);
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(currentUser));
    }
    map.addAttribute("showUserProfile", showUserProfile);
    map.addAttribute("tenant", effectiveTenant);
    int totalSubscriptions = 0;
    int filtersApplied = 0;
    List<Subscription> subscriptions = null;
    Long instanceID = null;
    ServiceInstance serviceInstance = null;
    if (StringUtils.isNotBlank(instanceuuid)) {
      serviceInstance = connectorConfigurationManager.getInstance(instanceuuid);
      instanceID = serviceInstance != null ? serviceInstance.getId() : null;
    }

    if (uuid == null) {
      if (hasFullBillingView(currentUser)) {
        if (useruuid == null) {
          subscriptions = subscriptionService.getSubscriptions(effectiveTenant, subscriptionState, instanceID,
              productBundleID, page, perPage, resourceTypeId, utilityViewSelected);
          totalSubscriptions = subscriptionService.getSubscriptionCount(effectiveTenant, subscriptionState, instanceID,
              productBundleID, 0, 0, resourceTypeId, utilityViewSelected);
        } else {
          User filterUser = userService.get(useruuid);
          subscriptions = subscriptionService.getSubscriptions(filterUser, subscriptionState, instanceID,
              productBundleID, page, perPage, resourceTypeId, utilityViewSelected);
          totalSubscriptions = subscriptionService.getSubscriptionCount(filterUser, subscriptionState, instanceID,
              productBundleID, 0, 0, resourceTypeId, utilityViewSelected);
          map.addAttribute("useruuid", useruuid);
        }
      } else {
        subscriptions = subscriptionService.getSubscriptions(currentUser, subscriptionState, instanceID,
            productBundleID, page, perPage, resourceTypeId, utilityViewSelected);
        totalSubscriptions = subscriptionService.getSubscriptionCount(currentUser, subscriptionState, instanceID,
            productBundleID, 0, 0, resourceTypeId, utilityViewSelected);
      }
    } else {
      subscriptions = new ArrayList<Subscription>();
      Subscription subscription = subscriptionService.locateSubscriptionByParam(uuid, true);
      state = subscription.getState().getName();
      subscriptions.add(subscription);
      totalSubscriptions = subscriptions != null ? subscriptions.size() : 0;
      map.addAttribute("idForDetails", uuid);
      if (subscription != null && subscription.getProductBundle() == null) {
        utilityViewSelected = true;
      }
    }
    for (Subscription subscription : subscriptions) {
      customFieldService.populateCustomFields(subscription);
      customFieldService.populateCustomFields(subscription.getTenant());
      customFieldService.populateCustomFields(subscription.getUser());
    }
    // Add a map attribute to know in common jsp about which tab is selected
    map.addAttribute("utility_charges_view", utilityViewSelected);
    map.addAttribute("subscriptions", subscriptions);
    map.addAttribute("stateSelected", state);
    map.addAttribute("instanceuuid", instanceuuid);
    map.addAttribute("productBundleID", productBundleID);
    map.addAttribute("resourceTypeID", resourceTypeId);

    filtersApplied = StringUtils.isNotBlank(useruuid) ? filtersApplied + 1 : filtersApplied;
    filtersApplied = StringUtils.isNotBlank(state) && !state.equalsIgnoreCase("ALL") ? filtersApplied + 1
        : filtersApplied;
    filtersApplied = StringUtils.isNotBlank(instanceuuid) ? filtersApplied + 1 : filtersApplied;
    filtersApplied = resourceTypeId != null ? filtersApplied + 1 : filtersApplied;
    if (!utilityViewSelected) {
      filtersApplied = productBundleID != null ? filtersApplied + 1 : filtersApplied;
    }
    map.addAttribute("filtersApplied", filtersApplied);

    if (totalSubscriptions - page * perPage > 0) {
      map.addAttribute("enable_next", true);
    } else {
      map.addAttribute("enable_next", false);
    }

    map.addAttribute("current_page", page);
    map.addAttribute("states", State.values());

    SubscriptionSearchForm searchForm = new SubscriptionSearchForm();
    searchForm.setUsers(effectiveTenant.getUsers());
    searchForm.setStates(Arrays.asList(State.values()));
    searchForm.setServiceInstances(tenantService.getEnabledCSInstances(effectiveTenant));

    if (utilityViewSelected) {
      Set<ServiceResourceType> resourceTypes = new HashSet<ServiceResourceType>();
      for (ServiceInstance si : tenantService.getEnabledCSInstances(effectiveTenant)) {
        resourceTypes.addAll(si.getService().getServiceResourceTypes());
      }
      searchForm.setResourceTypes(resourceTypes);
    } else {
      List<ProductBundleRevision> productBundleRevisions = channelService.getCurrentChannelRevisionForTenant(
          effectiveTenant).getProductBundleRevisions();
      if (CollectionUtils.isNotEmpty(productBundleRevisions)) {
        List<ProductBundle> productBundles = new ArrayList<ProductBundle>();
        for (ProductBundleRevision productBundleRevision : productBundleRevisions) {
          productBundles.add(productBundleRevision.getProductBundle());
        }
        searchForm.setProductBundles(productBundles);
      }
    }

    map.addAttribute("searchForm", searchForm);

    return "billing.showSubscriptions";
  }

  @RequestMapping(value = {
    "/utility_charges/showDetails"
  }, method = RequestMethod.POST)
  public String showUtilityChargeDetails(@RequestParam(value = "id", required = true) String uuid,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpServletRequest request) {
    logger.debug("Entry showUtilityChargeDetails with Id:" + uuid);
    return fetchSubscriptionDetails(uuid, tenantParam, map, true);
  }

  @RequestMapping(value = {
    "/subscriptions/showDetails"
  }, method = RequestMethod.POST)
  public String showSubscriptionDetails(@RequestParam(value = "id", required = true) String uuid,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpServletRequest request) {
    logger.debug("Entry showSubscriptionDetails with Id:" + uuid);
    return fetchSubscriptionDetails(uuid, tenantParam, map, false);
  }

  private String fetchSubscriptionDetails(String uuid, String tenantParam, ModelMap map, boolean utilityViewSelected) {

    boolean isPayAsYouGoChosen = config.getBooleanValue(Names.com_citrix_cpbm_catalog_payAsYouGoMode);
    map.addAttribute("isPayAsYouGoChosen", isPayAsYouGoChosen);

    if (isPayAsYouGoChosen) {
      utilityViewSelected = true;
    }

    map.addAttribute("utility_charges_view", utilityViewSelected);
    map.addAttribute("tenantParam", tenantParam);
    Subscription subscription = subscriptionService.locateSubscriptionByParam(uuid, true);
    try {
      subscription = subscriptionService.updateFilterAndResourceComponentNamesinConfigurationData(subscription);
    } catch (CloudServiceException cse) {
      map.addAttribute("cloudStackCallFailed", true);
      map.addAttribute("cloudServiceErrorMessage", cse.getMessage());
      map.addAttribute("cloudServiceDetailedErrorMessage", cloudExceptionService.getMostSpecificCause(cse).getMessage());
    }
    customFieldService.populateCustomFields(subscription);
    customFieldService.populateCustomFields(subscription.getTenant());
    customFieldService.populateCustomFields(subscription.getUser());
    map.addAttribute("subscription", subscription);
    Map<String, String> configurationProperties = prepareConfigurationMap(subscription);
    map.addAttribute("configurationProperties", configurationProperties);
    SubscriptionHandle activeHandle = subscription.getActiveHandle();
    if (activeHandle != null) {
      map.addAttribute("vmId", activeHandle.getResourceHandle());
    }
    Boolean hasCloudAccess = false;
    if (userService.isUserHasAnyActiveCloudService(getCurrentUser())) {
      hasCloudAccess = true;
    }
    Map<String, Object> actionMap = subscriptionService.checkActionsOnSubscription(subscription);
    map.addAllAttributes(actionMap);

    map.addAttribute("hasCloudAccess", hasCloudAccess);
    map.addAttribute("endDate", subscription.getTerminationDate());
    String workflowUuid = businessTransactionService.getWorkflowUuid(subscription);
    if (StringUtils.isNotEmpty(workflowUuid)) {
      map.addAttribute("workflowUuid", workflowUuid);
    }
    logger.debug("Exit ShowSubscriptionDetails");
    return "billing.viewSubscription";
  }

  private Map<String, String> prepareConfigurationMap(Subscription subscription) {
    Map<String, String> configurationProperties = new LinkedHashMap<String, String>();
    Map<String, String> configurationMap = new HashMap<String, String>(subscription.getConfigurationMap());

    if (subscription != null && subscription.getResourceType() != null) {
      SubscriptionLifecycleHandler slh = ((CloudConnector) connectorManagementService.getServiceInstance(subscription
          .getServiceInstance().getUuid())).getSubscriptionLifecycleHandler();
      if (slh != null && slh instanceof ExtendedSubscriptionLifecycleHandler) {
        configurationMap.putAll(((ExtendedSubscriptionLifecycleHandler) slh).getConfigurationData(subscription));
      }
      for (ServiceFilter filter : subscription.getServiceInstance().getService().getServiceFilters()) {
        putValueToMap(configurationProperties, configurationMap, filter.getDiscriminatorName());
      }
      List<ServiceResourceTypeGroupComponent> uniqueResourceTypeComponents = connectorConfigurationManager
          .getResourceComponents(subscription.getServiceInstance().getUuid(), subscription.getResourceType()
              .getResourceTypeName());
      for (ServiceResourceTypeGroupComponent serviceResourceTypeGroupComponent : uniqueResourceTypeComponents) {
        putValueToMap(configurationProperties, configurationMap,
            serviceResourceTypeGroupComponent.getResourceComponentName());
      }
      for (ServiceResourceTypeProperty property : subscription.getResourceType().getServiceResourceTypeProperty()) {
        putValueToMap(configurationProperties, configurationMap, property.getName());
      }
    }

    return configurationProperties;
  }

  private void putValueToMap(Map<String, String> configurationProperties, Map<String, String> configurationMap,
      String name) {
    String value = configurationMap.get(name);
    if (value == null) {
      // If value not configured atleast add empty entry in the map for UI to show the name
      configurationProperties.put(name, "");
    }
    value = configurationMap.get(name + "_name");
    if (StringUtils.isBlank(value)) {
      value = configurationMap.get(name);
    }
    configurationProperties.put(name, value);
  }

  @RequestMapping(value = "/subscriptions/terminate/{subscriptionParam}", method = RequestMethod.POST)
  @ResponseBody
  public Subscription terminateSubscription(@PathVariable String subscriptionParam, ModelMap map) {
    logger.debug("Entry terminateSubscription with Id:" + subscriptionParam);
    Subscription subscription = subscriptionService.locateSubscriptionByParam(subscriptionParam, false);
    subscriptionService.terminateSubscription(subscription, true);
    logger.debug("Exit terminateSubscription");
    return subscription;
  }

  @RequestMapping(value = "/subscriptions/cancel/{subscriptionParam}", method = RequestMethod.POST)
  @ResponseBody
  public Subscription cancelSubscription(@PathVariable String subscriptionParam, ModelMap map) {
    logger.debug("Entry cancelSubscription with Id:" + subscriptionParam);
    Subscription subscription = subscriptionService.locateSubscriptionByParam(subscriptionParam, false);
    subscriptionService.cancelSubscription(subscription);
    logger.debug("Exit cancelSubscription");
    return subscription;
  }

  @RequestMapping(value = {
    "/history"
  }, method = RequestMethod.GET)
  public String showHistory(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "user", required = false) String userParam,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage, HttpSession session,
      ModelMap map, HttpServletRequest request) {
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

    setPage(map, Page.USAGE_BILLING_BILLING_HISTORY);

    User currentUser = getCurrentUser();
    boolean showUserProfile = false;
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      showUserProfile = true;
      map.addAttribute("userHasCloudServiceAccount",
          userService.isUserHasAnyActiveCloudService(effectiveTenant.getOwner()));
    } else {
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(currentUser));
    }

    map.addAttribute("showUserProfile", showUserProfile);
    map.addAttribute("currentUser", currentUser);

    boolean showUsers = false;
    try {
      List<AccountStatement> accountStatements = billingService.getAccountStatements(effectiveTenant, null, page,
          perPage, null, null, null);
      map.addAttribute("billingActivities", accountStatements);
    } catch (Exception e) {
      logger.error("Error occurred while getting account statements " + e);

    }
    if (hasFullBillingView(currentUser)) {
      List<User> users = effectiveTenant.getUsers();
      map.addAttribute("users", users);
      showUsers = true;

    }
    map.addAttribute("showUsers", showUsers);
    map.addAttribute("tenant", effectiveTenant);

    map.addAttribute("current_page", page);

    if (session.getAttribute("makepayment_status") != null && session.getAttribute("makepayment_status").equals("true")) {
      map.addAttribute("statusMessage", "succeeded");
      session.removeAttribute("makepayment_status");
    } else if (session.getAttribute("makepayment_status") != null
        && session.getAttribute("makepayment_status").equals("false")) {
      map.addAttribute("statusMessage", "failed");
      session.removeAttribute("makepayment_status");
    }

    boolean isPayAsYouGoChosen = config.getBooleanValue(Names.com_citrix_cpbm_catalog_payAsYouGoMode);
    map.addAttribute("isPayAsYouGoChosen", isPayAsYouGoChosen);

    return "billing.history";

  }

  @RequestMapping(value = {
    "/paymenthistory"
  }, method = RequestMethod.GET)
  public String showPaymentHistory(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage, HttpSession session,
      HttpServletRequest request, ModelMap map) {
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
    setPage(map, Page.USAGE_BILLING_BILLING_HISTORY);

    boolean showUserProfile = false;
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    User currentUser = getCurrentUser();
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      showUserProfile = true;
      map.addAttribute("userHasCloudServiceAccount",
          userService.isUserHasAnyActiveCloudService(effectiveTenant.getOwner()));
    } else {
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(currentUser));
    }

    map.addAttribute("showUserProfile", showUserProfile);
    map.addAttribute("errorMsg", "");
    map.addAttribute("tenant", effectiveTenant);
    List<SalesLedgerRecord> slrList = billingService.listPaymentAndCredits(effectiveTenant, page, perPage);
    long totalPayments = 0l;
    try {
      totalPayments = billingService.getPaymentAndCreditsRecordCount(effectiveTenant);
    } catch (Exception e) {
      map.addAttribute("errorMsg",
          messageSource.getMessage("error.message.tenant.not.active", null, getSessionLocale(request)));
    }

    DepositRecord initialDeposit = tenantService.getDepositRecordByTenant(effectiveTenant);
    if (initialDeposit != null) {
      if (totalPayments - page * perPage < 0) {
        map.addAttribute("initialDeposit", initialDeposit);
      }
      totalPayments++;
    }

    map.addAttribute("salesLedgerRecords", slrList);

    if (totalPayments - page * perPage > 0) {
      map.addAttribute("enable_next", true);
    } else {
      map.addAttribute("enable_next", false);
    }

    map.addAttribute("current_page", page);

    boolean isPayAsYouGoChosen = config.getBooleanValue(Names.com_citrix_cpbm_catalog_payAsYouGoMode);
    map.addAttribute("isPayAsYouGoChosen", isPayAsYouGoChosen);

    return "billing.paymentHistory";

  }

  /**
   * view billing activity
   * 
   * @param activityId(invoice p or payment id
   * @param type
   * @param map
   * @return
   */
  @RequestMapping(value = {
    "/{activityParam}/viewbillingactivity"
  }, method = RequestMethod.GET)
  public String viewBillingActivity(@PathVariable String activityParam,
      @RequestParam(value = "type", required = true) String type,
      @RequestParam(value = "user", required = false) String userParam, ModelMap map) {

    User currentUser = getCurrentUser();
    try {
      AccountStatement accountStatement = billingService.getAccountStatement(activityParam);

      if (hasFullBillingView(currentUser)) {
        if (userParam != null && !userParam.equals("All")) {
          User effectiveUser = userService.get(userParam);
          Set<Invoice> invoices = accountStatement.getInvoices(effectiveUser);
          map.addAttribute("invoices", invoices);
        } else {
          Set<Invoice> invoices = accountStatement.getInvoices();
          map.addAttribute("invoices", invoices);
        }
      } else {
        Set<Invoice> invoices = accountStatement.getInvoices(currentUser);
        map.addAttribute("invoices", invoices);
      }
    } catch (Exception e) {
      logger.error("###Failed to get account statement", e);
    }

    if (type.equals("Invoice")) {
      return "billing.view.billing.activity";
    }
    return "billing.view.billing.paymentactivity";
  }

  @RequestMapping(value = {
    "/{slrUuid}/viewslr"
  }, method = RequestMethod.GET)
  public String viewSalesLedgerRecord(@PathVariable String slrUuid, ModelMap map) {
    SalesLedgerRecord slr = billingAdminService.getSalesLedgerRecord(slrUuid);
    map.addAttribute("salesLedgerRecord", slr);
    map.addAttribute("tenant", slr.getTenant());
    map.addAttribute("isInitialDeposit", false);
    return "billing.view.billing.paymentactivity";
  }

  @RequestMapping(value = "/viewInitialDeposit", method = RequestMethod.GET)
  public String viewInitialDeposit(@RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map) {
    Tenant tenant = tenantService.get(tenantParam);
    DepositRecord record = tenantService.getDepositRecordByTenant(tenant);
    map.addAttribute("initialDeposit", record);
    map.addAttribute("tenant", tenant);
    map.addAttribute("isInitialDeposit", true);
    return "billing.view.billing.paymentactivity";
  }

  @RequestMapping(value = "/showcreditcarddetails", method = RequestMethod.GET)
  public String showCreditCard(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpServletRequest request) {
    logger.debug("###Entering in edit()");

    setPage(map, Page.USAGE_BILLING_PAYMENT_INFO);

    CreditCard creditCard = null;
    map.addAttribute("showUserProfile", request.getAttribute("isSurrogatedTenant"));
    Tenant effectiveTenant = tenantService.get(tenantParam);

    map.addAttribute("tenant", effectiveTenant);
    List<AccountType> allowedTargetTypes = tenantService.getAllowedTargetAccountTypes(effectiveTenant);
    if (allowedTargetTypes != null && !allowedTargetTypes.isEmpty()) {
      map.addAttribute("showChangeAccountType", true);
    } else {
      map.addAttribute("showChangeAccountType", false);
    }
    List<PendingChange> pendingConversions = tenantService.getPendingConversions(effectiveTenant);

    if (pendingConversions != null && !pendingConversions.isEmpty()) {
      map.addAttribute("showMessagePendingConversion", true);
    } else {
      map.addAttribute("showMessagePendingConversion", false);
    }

    BillingInfoForm billingInfoForm = null;
    try {
      creditCard = billingService.getCreditCard(effectiveTenant);
      billingInfoForm = new BillingInfoForm(effectiveTenant, null, creditCard);
      billingInfoForm.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));

    } catch (Exception e) {
      logger.error("###Failed to get payment information", e);
      creditCard = new CreditCard();
      billingInfoForm = new BillingInfoForm(effectiveTenant, null, creditCard);
      map.addAttribute("errormsg",
          messageSource.getMessage("ui.usage.billing.paymentinfo.cc.errormsg", null, getSessionLocale(request)));
    }
    logger.debug("###Exiting showCreditCard()");
    map.addAttribute("billingInfo", billingInfoForm);

    return "billing.showCreditCardDetails";

  }

  @RequestMapping(value = "/editcreditcarddetails", method = RequestMethod.POST)
  public ModelMap editCreditCard(@RequestParam(value = "tenant", required = false) String tenantParam,
      @ModelAttribute("billingInfo") BillingInfoForm form, HttpServletResponse response, HttpServletRequest request,
      ModelMap map) throws IOException {
    logger.debug("###Entering in edit(tenantId, billingAddress,response) method @POST");

    Tenant effectiveTenant = tenantService.get(tenantParam);
    map.addAttribute("tenant", effectiveTenant);
    try {
      map.addAttribute("isAccountExistInPaymentGateway", ((PaymentGatewayService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PAYMENT_GATEWAY))
          .isAccountExistInPaymentGateway(effectiveTenant));
      CreditCard creditCard = form.getCreditCard();
      billingService.editCreditCard(effectiveTenant, creditCard, getRemoteUserIp(request), request.getLocale(),
          getCurrentUser());
    } catch (CreditCardFraudCheckException ex) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      throw new InvalidAjaxRequestException(ex.getMessage(), ex);
    } catch (PaymentGatewayServiceException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      throw new InvalidAjaxRequestException(e.getMessage());
    } catch (BillingServiceException e) {
      response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      throw new InvalidAjaxRequestException(e.getMessage());
    } catch (Exception e) {
      logger.error("Got Exception while updating billing info : ", e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      throw new InvalidAjaxRequestException(messageSource.getMessage("ui.usage.billing.paymentinfo.cc.edit.errormsg",
          null, getSessionLocale(request)));
    }
    String redirectToURL = "/portal/portal/tenants/editcurrent?action=showcreditcardtab&tenant=" + tenantParam;
    map.put("redirecturl", redirectToURL);
    String message = "billing.info.updated";
    String messageArgs = effectiveTenant.getName();
    eventService.createEvent(new Date(), effectiveTenant, message, messageArgs, Source.PORTAL, Scope.ACCOUNT,
        Category.ACCOUNT, Severity.INFORMATION, true);
    return map;
  }

  private ServiceResourceType getNewDummyResourceType(String resourceTypeName) {
    ServiceResourceType dummyResourceType = new ServiceResourceType();
    dummyResourceType.setResourceTypeName(resourceTypeName);
    return dummyResourceType;
  }

  private void pushInvoiceToMap(Map<ServiceResourceType, ArrayList<Invoice>> chargesMap,
      ServiceResourceType serviceResourceType, Invoice invoice, List<Invoice> newServiceInvoices,
      List<Invoice> renewServiceInvoices) {
    if (serviceResourceType == null) {
      if (invoice.getType().equals(com.vmops.model.Invoice.Type.Subscription)) {
        newServiceInvoices.add(invoice);
      } else {
        renewServiceInvoices.add(invoice);
      }
      return;
    }
    if (chargesMap.containsKey(serviceResourceType)) {
      chargesMap.get(serviceResourceType).add(invoice);
    } else {
      ArrayList<Invoice> newInvoiceList = new ArrayList<Invoice>();
      newInvoiceList.add(invoice);
      chargesMap.put(serviceResourceType, newInvoiceList);
    }
  }

  @RequestMapping(value = {
    "/usageBilling"
  }, method = RequestMethod.GET)
  public String usageBilling(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "viewBy", required = false) String viewBy,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "accountStatementUuid", required = false) String accountStatementUuid,
      @RequestParam(value = "useruuid", required = false, defaultValue = "ALL_USERS") String useruuid, ModelMap map,
      HttpServletRequest request) {
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
    if (TenantService.SYSTEM_TENANT.equals(user.getTenant().getAccountId())) {
      map.addAttribute("isSystemProviderUser", "Y");
    } else {
      map.addAttribute("isSystemProviderUser", "N");
    }
    AccountStatement accountStatement = null;

    if ((userService.hasAuthority(user, "ROLE_ACCOUNT_CRUD") || userService.hasAuthority(user, "ROLE_ACCOUNT_MGMT"))
        && (Boolean) request.getAttribute("isSurrogatedTenant")) {
      tenant = tenantService.get(tenantParam);
      user = tenant.getOwner();
      setPage(map, Page.CRM_USAGE_BILLING);
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(tenant.getOwner()));
      map.addAttribute("showUserProfile", true);
    } else {

      setPage(map, Page.DASHBOARD_USAGE_BILLING);
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));
    }
    if (accountStatementUuid == null) {
      accountStatement = billingAdminService.getOrCreateProvisionalAccountStatement(tenant);
    } else {
      accountStatement = billingAdminService.getAccountStatement(accountStatementUuid);
    }

    List<AccountStatement> accountStatements = billingAdminService.getAccountStatements(tenant, null, page, perPage,
        null, null, null);
    map.addAttribute("accountStatements", accountStatements);
    map.addAttribute("accountStatementUuid", accountStatement.getUuid());
    map.addAttribute("accountStatementState", accountStatement.getState().name());
    int accStatSize = billingAdminService.countAccountStatements(tenant, null, null);
    if (accStatSize - page * perPage > 0) {
      map.addAttribute("enable_next", true);
    } else {
      map.addAttribute("enable_next", false);
    }

    map.addAttribute("current_page", page);
    LinkedHashMap<ServiceResourceType, ArrayList<Invoice>> newChargesMap = new LinkedHashMap<ServiceResourceType, ArrayList<Invoice>>();
    LinkedHashMap<ServiceResourceType, ArrayList<Invoice>> renewalChargesMap = new LinkedHashMap<ServiceResourceType, ArrayList<Invoice>>();

    ArrayList<Invoice> utilityInvoiceList = new ArrayList<Invoice>();
    ArrayList<Invoice> newServiceInvoiceList = new ArrayList<Invoice>();
    ArrayList<Invoice> renewServiceInvoiceList = new ArrayList<Invoice>();

    Set<Invoice> invoices = null;
    if (StringUtils.isNotBlank(useruuid) && !useruuid.equals("ALL_USERS")) {
      // if user does not have permission to access somebody else's invoice then change the uuid to his own uuid.
      if (!userService.hasAuthority(user, "ROLE_ACCOUNT_BILLING_ADMIN")) {
        useruuid = user.getUuid();
      }
      User filterUser = userService.get(useruuid);
      invoices = accountStatement.getInvoices(filterUser);
      map.addAttribute("useruuid", useruuid);
    } else {
      if (userService.hasAuthority(user, "ROLE_ACCOUNT_CRUD") || userService.hasAuthority(user, "ROLE_ACCOUNT_MGMT")
          || userService.hasAuthority(user, "ROLE_ACCOUNT_BILLING_ADMIN")) {
        invoices = accountStatement.getInvoices();
      } else {
        // For regular user send only their invoices regardless of what they call.
        invoices = accountStatement.getInvoices(user);
      }
    }

    BigDecimal newBigAmount = BigDecimal.ZERO;
    BigDecimal newBigDiscount = BigDecimal.ZERO;
    BigDecimal newBigSubTotal = BigDecimal.ZERO;
    BigDecimal newBigTax = BigDecimal.ZERO;
    BigDecimal newBigTotal = BigDecimal.ZERO;
    BigDecimal renewBigAmount = BigDecimal.ZERO;
    BigDecimal renewBigDiscount = BigDecimal.ZERO;
    BigDecimal renewBigSubTotal = BigDecimal.ZERO;
    BigDecimal renewBigTax = BigDecimal.ZERO;
    BigDecimal renewBigTotal = BigDecimal.ZERO;

    List<Invoice> invoiceList = new ArrayList<Invoice>(invoices);
    Collections.sort(invoiceList);
    for (Invoice invoice : invoiceList) {
      if (invoice.getType().equals(com.vmops.model.Invoice.Type.Renewal)) {
        pushInvoiceToMap(renewalChargesMap, invoice.getSubscription().getProductBundle().getResourceType(), invoice,
            newServiceInvoiceList, renewServiceInvoiceList);
        renewBigAmount = renewBigAmount.add(invoice.getRawAmount());
        renewBigDiscount = renewBigDiscount.add(invoice.getDiscountAmount());
        renewBigSubTotal = renewBigSubTotal.add(invoice.getSubTotal());
        renewBigTax = renewBigTax.add(invoice.getTaxAmount());
        renewBigTotal = renewBigTotal.add(invoice.getAmount());
      } else if (invoice.getType().equals(com.vmops.model.Invoice.Type.Subscription)) {
        pushInvoiceToMap(newChargesMap, invoice.getSubscription().getResourceType(), invoice, newServiceInvoiceList,
            renewServiceInvoiceList);
        newBigAmount = newBigAmount.add(invoice.getRawAmount());
        newBigDiscount = newBigDiscount.add(invoice.getDiscountAmount());
        newBigSubTotal = newBigSubTotal.add(invoice.getSubTotal());
        newBigTax = newBigTax.add(invoice.getTaxAmount());
        newBigTotal = newBigTotal.add(invoice.getAmount());
      } else {
        Collections.sort(invoice.getInvoiceItems());
        utilityInvoiceList.add(invoice);
        newBigAmount = newBigAmount.add(invoice.getRawAmount());
        newBigDiscount = newBigDiscount.add(invoice.getDiscountAmount());
        newBigSubTotal = newBigSubTotal.add(invoice.getSubTotal());
        newBigTax = newBigTax.add(invoice.getTaxAmount());
        newBigTotal = newBigTotal.add(invoice.getAmount());
      }
    }

    Collections.sort(utilityInvoiceList);

    newChargesMap.put(getNewDummyResourceType(serviceBundleInvoices), newServiceInvoiceList);
    newChargesMap.put(getNewDummyResourceType(utiltyInvoices), utilityInvoiceList);
    renewalChargesMap.put(getNewDummyResourceType(serviceBundleInvoices), renewServiceInvoiceList);

    map.addAttribute("newChargesMap", newChargesMap);
    map.addAttribute("renewalChargesMap", renewalChargesMap);
    map.addAttribute("newBigAmount", newBigAmount);
    map.addAttribute("newBigDiscount", newBigDiscount);
    map.addAttribute("newBigTax", newBigTax);
    map.addAttribute("newBigSubTotal", newBigSubTotal);
    map.addAttribute("newBigTotal", newBigTotal);
    map.addAttribute("renewBigAmount", renewBigAmount);
    map.addAttribute("renewBigDiscount", renewBigDiscount);
    map.addAttribute("renewBigTax", renewBigTax);
    map.addAttribute("renewBigSubTotal", renewBigSubTotal);
    map.addAttribute("renewBigTotal", renewBigTotal);

    map.addAttribute("tenant", tenant);
    map.addAttribute("user", user);
    map.addAttribute("accountStatement", accountStatement);
    map.addAttribute("current_page", page);

    List<SalesLedgerCreditRecord> creditRecords = accountStatement.getSalesLedgerCreditRecords();
    List<SalesLedgerCreditRecord> payments = new ArrayList<SalesLedgerCreditRecord>();
    List<SalesLedgerCreditRecord> creditsIssued = new ArrayList<SalesLedgerCreditRecord>();
    BigDecimal bigPaymentsSum = BigDecimal.ZERO;

    for (SalesLedgerCreditRecord creditRecord : creditRecords) {
      if (!creditRecord.isVoided()) {
        if (creditRecord.getType().equals(Type.SERVICE_CREDIT)) {
          creditsIssued.add(creditRecord);
        } else {
          payments.add(creditRecord);
        }
        bigPaymentsSum = bigPaymentsSum.add(creditRecord.getTransactionAmount());
      }
    }

    map.addAttribute("payments", payments);
    map.addAttribute("creditsIssued", creditsIssued);
    map.addAttribute("bigPaymentsSum", bigPaymentsSum);

    boolean isPayAsYouGoChosen = config.getBooleanValue(Names.com_citrix_cpbm_catalog_payAsYouGoMode);
    map.addAttribute("isPayAsYouGoChosen", isPayAsYouGoChosen);

    return "billing.usageBilling";
  }

  @RequestMapping(value = {
    "/generateUDR"
  }, method = RequestMethod.GET)
  public void generateUDR(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "viewBy", required = false) String viewBy,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "accountStatementUuid", required = false) String accountStatementUuid,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage, ModelMap map,
      HttpServletRequest request, HttpServletResponse response) {

    String portalTempPath = config.getValue(Names.com_citrix_cpbm_portal_settings_temp_path);
    String fileExtension = BILLING_EXPORT_FILE_EXTENSION;
    File targetXmlFile = new File(portalTempPath + File.separator + "UB" + System.currentTimeMillis() + "."
        + fileExtension);

    AccountStatement accountStatement = null;
    if (accountStatementUuid == null) {
      accountStatement = billingAdminService.getOrCreateProvisionalAccountStatement(tenant);
    } else {
      accountStatement = billingAdminService.getAccountStatement(accountStatementUuid);
    }

    InputStream templateInputStream = null;

    try {
      Transformer.transform(accountStatement, targetXmlFile, templateInputStream);
    } catch (Exception ex) {
      logger.error("### Exception in transforming usage", ex);
    }
    logger.info("### Leaving Transform");

    response.setContentType("application/xml");
    response.setHeader("Content-Disposition", "attachment; filename=" + targetXmlFile.getName());
    OutputStream out = null;
    InputStream is = null;
    try {
      out = response.getOutputStream();
      is = new BufferedInputStream(new FileInputStream(targetXmlFile));
      FileCopyUtils.copy(is, response.getOutputStream());
      out.flush();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    } finally {
      try {
        if (is != null) {
          is.close();
        }
        out.close();
      } catch (IOException e) {
        logger.error("Error while closing streams", e);
      }

    }
  }

  @RequestMapping(value = "/make_payment", method = RequestMethod.POST)
  @ResponseBody
  public String makePayment(@RequestParam BigDecimal amount, @RequestParam String memo,
      @RequestParam String tenantParam, HttpSession session, HttpServletResponse response) {
    logger.debug("###Entering in makePaymentInvoice(tenantId,invoiceId,form, amount,response) method @POST");
    try {
      Tenant tenant = tenantService.get(tenantParam);
      BigDecimal paymentAmount = billingPostProcessor.setScaleByCurrency(amount, tenant.getCurrency());
      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        return "failed";
      }
      SalesLedgerRecord salesLedgerRecord = billingService.addPaymentOrCredit(tenant, paymentAmount, Type.MANUAL, memo,
          null);
      if (salesLedgerRecord != null
          && salesLedgerRecord.getPaymentTransaction().getState().equals(PaymentTransaction.State.COMPLETED)) {
        response.setStatus(HttpStatus.OK.value());
        logger.debug("###Exiting makePaymentInvoice(tenantId,form ,response) method @POST");

        String message = "payment.received";
        String messageArgs = tenant.getName();
        eventService.createEvent(new Date(), tenant, message, messageArgs, Source.PORTAL, Scope.ACCOUNT,
            Category.ACCOUNT, Severity.INFORMATION, true);

        return "success";
      }
      response.setStatus(HttpStatus.OK.value());
      logger.debug("###Exiting makePaymentInvoice(tenantId,form ,response) method @POST. Failed to make the payment.");
      return "failed";

    } catch (BillingServiceException e) {
      session.setAttribute("makepayment_status", "false");
      response.setStatus(HttpStatus.OK.value());
      logger.debug("###Exiting makePaymentInvoice(tenantId,form ,response) method @POST. Failed to make the payment.");
      return "failed";
    }

  }

  @RequestMapping(value = "/recordpayment", method = RequestMethod.POST)
  @ResponseBody
  public String recordPayment(@RequestParam BigDecimal amount, @RequestParam String memo,
      @RequestParam String tenantParam, HttpServletResponse response, ModelMap map) {
    logger.debug("###Entering in recordPaymentInvoice(tenantId,form, amount,response) method @POST");
    Tenant tenant = tenantService.get(tenantParam);
    try {
      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        return "failed";
      }
      BigDecimal roundedAmount = billingPostProcessor.setScaleByCurrency(amount, tenant.getCurrency());
      billingService.addPaymentOrCredit(tenant, roundedAmount, Type.RECORD, memo, null);

      String message = "payment.received";
      String messageArgs = tenant.getName();
      eventService.createEvent(new Date(), tenant, message, messageArgs, Source.PORTAL, Scope.ACCOUNT,
          Category.ACCOUNT, Severity.INFORMATION, true);
      logger.debug("###Exiting recordPaymentInvoice(tenantId,amount,form ,response) method @POST");
    } catch (BillingServiceException e) {
      response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      return "failed";
    }
    map.addAttribute("tenant", tenant);
    return "success";
  }

  @RequestMapping(value = "/chargeback", method = RequestMethod.POST)
  @ResponseBody
  public String chargeBack(@RequestParam(value = "slrparam", required = true) String slrparam,
      @RequestParam String tenantParam, HttpServletRequest request, HttpServletResponse response, ModelMap map) {
    logger.debug("###Entering in chargeBack() @POST");
    Tenant tenant = tenantService.get(tenantParam);
    try {
      SalesLedgerRecord slr = billingAdminService.cancelPayment(tenant, slrparam);
      if (slr == null) {
        throw new InvalidAjaxRequestException("Charge back failed. Please contact your admin about this failure.");
      }
    } catch (BillingAdminServiceException e) {
      throw new InvalidAjaxRequestException(e.getMessage());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new InvalidAjaxRequestException("Charge back failed. Please contact your admin about this failure.");
    }
    response.setStatus(HttpStatus.OK.value());
    map.addAttribute("tenant", tenant);
    return "success";
  }

  @RequestMapping(value = "/changeaccounttype", method = RequestMethod.POST)
  public ModelMap changeAccountType(@RequestParam(value = "tenant", required = false) String tenantParam,
      @Valid @ModelAttribute("setAccountTypeForm") SetAccountTypeForm form, HttpServletRequest request,
      HttpServletResponse response, ModelMap map) {
    logger.debug("###Entering in setAccountType(tenantId, form, request, response, map) method @POST");
    Map<String, String> browserDetails = new HashMap<String, String>();
    browserDetails.put("remoteAddress", getRemoteUserIp(request));
    browserDetails.put("userAgent", request.getHeader("User-Agent"));
    browserDetails.put("referer", request.getHeader("Referer"));
    Address billingAddress = form.getBillingAddress();
    CreditCard creditCard = form.getCreditCard();
    try {
      tenantService.convertAccountType(tenantService.get(tenantParam), creditCard, billingAddress,
          form.getInitialPayment(), form.getAccountTypeName(), browserDetails, getRemoteUserIp(request),
          getSessionLocale(request), getCurrentUser(), form.getTenant().isSyncBillingAddress());
    } catch (HibernateOptimisticLockingFailureException ex) {
      logger.error(ex);
      tenantService.refresh(tenantService.get(tenantParam));
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      throw new InvalidAjaxRequestException(ex.getMessage());
    } catch (TenantStateChangeFailedException ex) {
      logger.error(ex);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      throw new InvalidAjaxRequestException(ex.getMessage());
    } catch (CreditCardFraudCheckException ex) {
      logger.error(ex);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      throw new InvalidAjaxRequestException(ex.getMessage());
    } catch (AccountConversionFailedException ex) {
      String message = messageSource.getMessage(ex.getMessage(), null, getSessionLocale(request));
      logger.error(message);
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      throw new InvalidAjaxRequestException(message);
    } catch (Exception ex) {
      logger.error(ex);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      throw new InvalidAjaxRequestException(messageSource.getMessage("ui.usage.billing.paymentinfo.cc.edit.errormsg",
          null, getSessionLocale(request)));
    }
    map.put("redirecturl", "/portal/portal/tenants/editcurrent");
    return map;
  }

  @RequestMapping(value = "/changeaccounttype", method = RequestMethod.GET)
  public String changeAccountType(@RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map) {
    logger.debug("###Entering in changeAccountType(tenantId,map,) method @GET");
    Tenant tenant = tenantService.get(tenantParam);
    List<AccountType> targetAccountTypes = tenantService.getAllowedTargetAccountTypes(tenant.getAccountType());
    SetAccountTypeForm setAccountTypeForm = new SetAccountTypeForm(tenant);
    map.addAttribute("setAccountTypeForm", setAccountTypeForm);
    map.addAttribute("targetAccountTypes", targetAccountTypes);
    map.addAttribute("tenant", tenant);
    return "billing.change.account.type";
  }

  @RequestMapping(value = "/show_record_deposit", method = RequestMethod.GET)
  public String showRecordDeposit(@RequestParam(value = "tenant", required = true) String tenantParam,
      @ModelAttribute("currentTenant") Tenant currentTenant, ModelMap map, HttpServletRequest request) {
    logger.debug("###Entering in record deposit");

    setPage(map, Page.USAGE_BILLING_RECORD_DEPOSIT);

    boolean showUserProfile = false;
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    User currentUser = getCurrentUser();
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      showUserProfile = true;
      map.addAttribute("userHasCloudServiceAccount",
          userService.isUserHasAnyActiveCloudService(effectiveTenant.getOwner()));
    } else {
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(currentUser));
    }

    map.addAttribute("showUserProfile", showUserProfile);

    Tenant tenant = tenantService.get(tenantParam);
    DepositRecord depositRecord = tenantService.getDepositRecordByTenant(tenant);
    if (depositRecord != null) {
      map.addAttribute("show_deposit_record", true);
      map.addAttribute("depositRecord", depositRecord);
    } else {
      map.addAttribute("show_deposit_record", false);
    }

    map.addAttribute("tenant", tenant);

    boolean isPayAsYouGoChosen = config.getBooleanValue(Names.com_citrix_cpbm_catalog_payAsYouGoMode);
    map.addAttribute("isPayAsYouGoChosen", isPayAsYouGoChosen);

    return "billing.show_record_deposit";
  }

  @RequestMapping(value = "/record_deposit", method = RequestMethod.GET)
  public String recordDeposit(@RequestParam(value = "tenant", required = true) String tenantParam,
      @ModelAttribute("currentTenant") Tenant currentTenant, ModelMap map, HttpServletRequest request) {
    logger.debug("###Entering in record deposit");

    setPage(map, Page.USAGE_BILLING_RECORD_DEPOSIT);
    map.addAttribute("showUserProfile", request.getAttribute("isSurrogatedTenant"));

    Tenant tenant = tenantService.get(tenantParam);
    map.addAttribute("tenant", tenant);
    DepositRecordForm depositRecordForm = new DepositRecordForm();
    depositRecordForm.setAmount(tenant.getAccountType().getInitialDeposit(tenant.getCurrency().getCurrencyCode())
        .toPlainString());
    map.addAttribute("depositRecordForm", depositRecordForm);
    return "billing.record_deposit";
  }

  @RequestMapping(value = "/record_deposit", method = RequestMethod.POST)
  @ResponseBody
  public DepositRecordForm recordDeposit(@RequestParam(value = "tenant", required = false) String tenantParam,
      @Valid @ModelAttribute("depositRecordForm") DepositRecordForm depositRecordForm, BindingResult result,
      ModelMap map, HttpServletResponse response) {
    logger.debug("###Entering in recordDeposit method @POST");

    setPage(map, Page.USAGE_BILLING_RECORD_DEPOSIT);
    map.addAttribute("showUserProfile", true);

    Tenant tenant = tenantService.get(tenantParam);
    map.addAttribute("tenant", tenant);
    map.addAttribute("depositForm", depositRecordForm);
    DepositRecordValidator depositRecordValidator = new DepositRecordValidator();
    depositRecordValidator.validate(depositRecordForm, result);
    if (result.hasErrors()) {
      map.addAttribute("depositRecordForm", depositRecordForm);
      List<FieldError> l = result.getFieldErrors();
      for (FieldError f : l) {
        if (f.getCode().equals("js.deposit.record.validationReceivedOn")) {
          throw new AjaxFormValidationException(result);
        }
      }
    }

    try {
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      Date recievedOn = null;
      try {
        recievedOn = sdf.parse(depositRecordForm.getReceivedOn());
      } catch (ParseException e) {
      }
      tenantService.recordDeposit(tenant, new DepositRecord(tenant, recievedOn, tenant.getAccountType()
          .getInitialDeposit(tenant.getCurrency().getCurrencyCode()).doubleValue(), getCurrentUser()));

    } catch (HibernateOptimisticLockingFailureException ex) {
      logger.error(ex);
      tenantService.refresh(tenant);
      throw new InvalidAjaxRequestException(ex.getMessage());
    }
    response.setStatus(HttpStatus.OK.value());
    logger.debug("###Exiting recordDeposit method @POST");

    return depositRecordForm;
  }

  @RequestMapping(value = {
    "/generatePdfInvoice"
  }, method = RequestMethod.GET)
  public String generatePdfAccountStatement(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "viewBy", required = false) String viewBy,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "accountStatementUuid", required = false) String accountStatementUuid,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage, ModelMap map,
      HttpServletRequest request, HttpServletResponse response) {

    logger.debug("###Entering in generatePdfAccountStaement method @GET");

    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    String targetXmlFile = "UB" + System.currentTimeMillis() + "." + "pdf";

    OutputStream pdfOut = null;
    AccountStatement accountStatement = billingService.getAccountStatement(accountStatementUuid);
    Locale locale = getCurrentUser().getLocale() != null ? getCurrentUser().getLocale() : LocaleUtils
        .toLocale(channelService.getDefaultLocale(getCurrentUser().getTenant().getSourceChannel()));
    String pdfHtmlString = getInvoicePdfHtmlString(effectiveTenant, accountStatement, locale.toString());

    if (pdfHtmlString != null) {
      ByteArrayInputStream htmlSource;
      try {
        htmlSource = new ByteArrayInputStream(pdfHtmlString.getBytes("UTF-8"));
        pdfOut = pdfGenerator.generatePdfFromHtml(htmlSource);
      } catch (UnsupportedEncodingException e) {
        logger.error(e.getMessage(), e);
      } catch (FileNotFoundException e) {
        logger.error(e.getMessage(), e);
      } catch (IOException e) {
        logger.error(e.getMessage(), e);
      }
    }

    if (pdfOut != null) {
      response.setHeader("Content-Disposition", "attachment; filename=" + targetXmlFile);
      response.setContentType("application/pdf");
      OutputStream out = null;
      InputStream is = null;
      try {
        out = response.getOutputStream();
        is = new BufferedInputStream(new ByteArrayInputStream(((ByteArrayOutputStream) pdfOut).toByteArray()));
        FileCopyUtils.copy(is, response.getOutputStream());
        out.flush();
      } catch (IOException e) {
        logger.error(e.getMessage(), e);
      } finally {
        try {
          if (is != null) {
            is.close();
          }
          out.close();
        } catch (IOException e) {
          logger.error("Error while closing streams", e);
        }
      }
    } else {
      return "generatePdfAccountStatement.error";
    }
    return null;
  }

  @RequestMapping(value = {
    "/sendEmailPdfInvoice"
  }, method = RequestMethod.GET)
  public void sendEmailPdfAccountStatement(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "viewBy", required = false) String viewBy,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "accountStatementUuid", required = false) String accountStatementUuid,
      @RequestParam(value = "page", required = false, defaultValue = "1") String currentPage, ModelMap map,
      HttpServletRequest request, HttpServletResponse response) {

    logger.debug("###Entering in sendEmailPdfAccountStaement method @GET");

    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    AccountStatement accountStatement = billingService.getAccountStatement(accountStatementUuid);
    Locale locale = getCurrentUser().getLocale() != null ? getCurrentUser().getLocale() : LocaleUtils
        .toLocale(channelService.getDefaultLocale(getCurrentUser().getTenant().getSourceChannel()));
    String invoicePdfString = getInvoicePdfHtmlString(effectiveTenant, accountStatement, locale.toString());

    List<String> listUser = new ArrayList<String>();
    User currentUser = getCurrentUser();
    listUser.add(currentUser.getEmail());
    billingService.sendInvoiceEmail(currentUser, invoicePdfString, accountStatement, locale, listUser);
  }

  private String getInvoicePdfHtmlString(Tenant tenant, AccountStatement accountStatement, String locale) {
    User currentUser = getCurrentUser();
    boolean hasFullView = hasFullBillingView(currentUser);
    if (hasFullView) {
      currentUser = null;
    }
    return billingService.getInvoiceAsHTMLString(accountStatement, currentUser, locale);
  }

}
