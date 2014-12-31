/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.admin.service.exceptions.ConnectorManagementServiceException;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.citrix.cpbm.platform.spi.FilterComponent;
import com.citrix.cpbm.platform.spi.ResourceComponent;
import com.google.gson.JsonObject;
import com.vmops.internal.service.PrivilegeService;
import com.vmops.internal.service.SubscriptionService;
import com.vmops.model.Catalog;
import com.vmops.model.Channel;
import com.vmops.model.CurrencyValue;
import com.vmops.model.Entitlement;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.ProvisioningConstraint;
import com.vmops.model.ProvisioningConstraint.AssociationType;
import com.vmops.model.RateCardCharge;
import com.vmops.model.RateCardComponent;
import com.vmops.model.Revision;
import com.vmops.model.ServiceFilter;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceResourceType.ResourceConstraint;
import com.vmops.model.ServiceResourceTypeGroupComponent;
import com.vmops.model.Subscription;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.CurrencyValueService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.ProductService;
import com.vmops.service.TenantService;
import com.vmops.service.UserService.Handle;
import com.vmops.service.exceptions.AjaxFormValidationException;
import com.vmops.service.exceptions.CurrencyPrecisionException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.utils.DateUtils;
import com.vmops.utils.JSONUtils;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.EntitlementForm;
import com.vmops.web.forms.ProductBundleForm;
import com.vmops.web.forms.ProductBundleLogoForm;
import com.vmops.web.forms.RateCardChargesForm;
import com.vmops.web.forms.RateCardComponentChargesForm;
import com.vmops.web.forms.RateCardForm;
import com.vmops.web.interceptors.UserContextInterceptor;
import com.vmops.web.validators.ProductBundleLogoFormValidator;
import com.vmops.web.validators.ProductBundleValidator;

public abstract class AbstractProductBundlesController extends AbstractAuthenticatedController {

  @Autowired
  private ProductBundleService productBundleService;

  @Autowired
  private ProductService productService;

// CHECKSTYLE:OFF
  @Autowired
  protected SubscriptionService subscriptionService;

// CHECKSTYLE:ON
  @Autowired
  private CurrencyValueService currencyValueService;

// CHECKSTYLE:OFF
  @Autowired
  protected ChannelService channelService;

// CHECKSTYLE:ON
  @Autowired
  private TenantService tenantService;

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManager;

  @Autowired
  private ConnectorManagementService connectorManagementService;

// CHECKSTYLE:OFF
  @Autowired
  protected PrivilegeService privilegeService;

// CHECKSTYLE:ON

  private static final List<ResourceConstraint> ORDERED_CONSTRAINTS = new ArrayList<ServiceResourceType.ResourceConstraint>() {

    private static final long serialVersionUID = -6231001441117555544L;

    {
      add(ResourceConstraint.NONE);
      add(ResourceConstraint.PER_USER);
      add(ResourceConstraint.ACCOUNT);
      add(ResourceConstraint.SINGLETON);
    }
  };

  private final Logger logger = Logger.getLogger(AbstractProductBundlesController.class);

  /**
   * This method list the product bundles
   * 
   * @param currentTenant
   * @param tenantParam
   * @param serviceInstaceUuid
   * @param resourceType
   * @param filters
   * @param context
   * @param viewCatalog
   * @param revision
   * @param channelId
   * @param currencyCode
   * @param historyDate
   * @param dateFormat
   * @param request
   * @return
   */
  @RequestMapping(value = {
    "list.json"
  }, method = RequestMethod.POST)
  @ResponseBody
  public List<ProductBundleRevision> listProductBundles(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "serviceInstaceUuid", required = false) String serviceInstaceUuid,
      @RequestParam(value = "resourceType", required = false) String resourceType,
      @RequestParam(value = "filters", required = false) String filters,
      @RequestParam(value = "context", required = false) String context,
      @RequestParam(value = "viewCatalog", required = false, defaultValue = "false") Boolean viewCatalog,
      @RequestParam(value = "revision", required = false) String revision,
      @RequestParam(value = "channelParam", required = false) String channelId,
      @RequestParam(value = "currencyCode", required = false) String currencyCode,
      @RequestParam(value = "revisionDate", required = false, defaultValue = "") String historyDate,
      @RequestParam(value = "dateFormat", required = false) String dateFormat, HttpServletRequest request) {
    logger.debug("# listProductBundles method entered...(list.json)");

    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    User user = actorService.getActor();
    boolean anonymousBrowsing = false;
    if (getCurrentUser() == null) {
      anonymousBrowsing = true;
    }
    if (!anonymousBrowsing && !user.getTenant().equals(effectiveTenant)) {
      user = effectiveTenant.getOwner();
    }

    ServiceInstance serviceInstance = connectorConfigurationManager.getInstanceByUUID(serviceInstaceUuid);
    ServiceResourceType serviceResourceType = connectorConfigurationManager.getServiceResourceType(serviceInstaceUuid,
        resourceType);
    List<ProductBundleRevision> filteredBundleRevisions = new ArrayList<ProductBundleRevision>();
    Date historyDateObj = null;
    Channel channel = null;
    if (StringUtils.isNotBlank(channelId)) {
      channel = channelService.getChannelById(channelId);
    } else {
      channel = channelService.getDefaultServiceProviderChannel();
    }
    CurrencyValue currency = currencyValueService.locateBYCurrencyCode(currencyCode);
    if (viewCatalog && user.getTenant().getAccountId().equals(TenantService.SYSTEM_TENANT)) {
      Revision channelRevision = null;
      Boolean includeFutureBundle = false;
      if (StringUtils.isBlank(revision) || revision.equalsIgnoreCase("current")) {
        channelRevision = channelService.getCurrentRevision(channel);
      } else if (revision.equalsIgnoreCase("planned")) {
        includeFutureBundle = true;
        channelRevision = channelService.getFutureRevision(channel);
      } else if (revision.equalsIgnoreCase("history")) {

        if (historyDate != null && !historyDate.isEmpty()) {
          dateFormat = "MM/dd/yyyy HH:mm:ss";
          DateFormat formatter = new SimpleDateFormat(dateFormat);
          try {
            historyDateObj = formatter.parse(historyDate);
          } catch (ParseException e) {
            throw new InvalidAjaxRequestException(e.getMessage());
          }
        } else {
          List<Date> historyDatesForCatalog = productService.getHistoryDates(channel.getCatalog());
          historyDateObj = historyDatesForCatalog.get(0);
        }
        channelRevision = channelService.getRevisionForTheDateGiven(historyDateObj, channel);
      }
      filteredBundleRevisions = productBundleService.getProductBundleRevisions(serviceInstance, serviceResourceType,
          createStringMapCombined(context), createStringMapCombined(filters), user, getSessionLocale(request), channel,
          currency, channelRevision, includeFutureBundle);
    } else if (anonymousBrowsing) {
      filteredBundleRevisions = productBundleService.listAnonymousProductBundleRevisions(serviceInstance,
          serviceResourceType, createStringMapCombined(context), createStringMapCombined(filters), user,
          getSessionLocale(request), channel, currency);
    } else {
      filteredBundleRevisions = productBundleService.getProductBundleRevisions(serviceInstance, serviceResourceType,
          createStringMapCombined(context), createStringMapCombined(filters), user, getSessionLocale(request));
    }

    logger.debug("# listProductBundles method leaving...(list.json)");

    return getLighterProductBundles(filteredBundleRevisions);
  }

  private List<ProductBundle> getProductBundleList(List<ProductBundle> productBundles, int pageNo, int perPage) {
    logger.debug("Entering getProductBundlesList...");

    int count = 0;
    int toStartFrom = (pageNo - 1) * perPage;
    List<ProductBundle> productBundlesList = new ArrayList<ProductBundle>();
    for (ProductBundle productBundle : productBundles) {
      if (count >= toStartFrom) {
        productBundlesList.add(productBundle);
        if (productBundlesList.size() == perPage) {
          break;
        }
      }
      count += 1;
    }

    logger.debug("Leaving getProductBundlesList....");
    return productBundlesList;
  }

  private List<ProductBundle> getProductBundleListByFilters(List<ProductBundleRevision> productBundleRevisions,
      String filterBy) {
    logger.debug("Entering getProductBundlesListByFilter...");
    List<ProductBundle> productBundlesList = new ArrayList<ProductBundle>();
    for (ProductBundleRevision productBundleRevision : productBundleRevisions) {
      if ((StringUtils.isEmpty(filterBy)) || ("All".equalsIgnoreCase(filterBy))
          || ("publish".equalsIgnoreCase(filterBy) && productBundleRevision.getProductBundle().getPublish())
          || ("unpublish".equalsIgnoreCase(filterBy) && !productBundleRevision.getProductBundle().getPublish())) {
        productBundlesList.add(productBundleRevision.getProductBundle());
      }
    }

    logger.debug("Leaving getProductBundlesListByFilter....");
    return productBundlesList;
  }

  /**
   * This method return the map of Subscription IDs and Product Bundle Revision
   * 
   * @param currentTenant
   * @param tenantParam
   * @param serviceInstaceUuid
   * @param resourceType
   * @param filters
   * @param context
   * @param request
   * @return
   */
  @RequestMapping(value = {
    "listValidSubscriptions.json"
  }, method = RequestMethod.POST)
  @ResponseBody
  public Map<Long, ProductBundleRevision> listValidSubscriptions(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "serviceInstaceUuid", required = false) String serviceInstaceUuid,
      @RequestParam(value = "resourceType", required = false) String resourceType,
      @RequestParam(value = "filters", required = false) String filters,
      @RequestParam(value = "context", required = false) String context, HttpServletRequest request) {
    logger.debug("# listValidSubscriptions method entered...(listValidSubscriptions.json)");
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    User effectiveUser = getCurrentUser();
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      effectiveUser = effectiveTenant.getOwner();
    }
    Map<Long, ProductBundleRevision> subMap = new HashMap<Long, ProductBundleRevision>();
    Map<Subscription, ProductBundleRevision> subscriptionProductBundleRevisionMap = productBundleService
        .getSubscriptionProductBundleRevisionByServiceInstance(serviceInstaceUuid, resourceType, effectiveTenant,
            effectiveUser);
    for (Subscription subscriptions : subscriptionProductBundleRevisionMap.keySet()) {
      ProductBundleRevision productBundleRevision = subscriptionProductBundleRevisionMap.get(subscriptions);
      if (productBundleService.isValidBundleForGivenDiscriminators(productBundleRevision,
          createStringMapCombined(context + "," + filters))) {
        subMap.put(subscriptions.getId(), productBundleRevision.minClone());
      }
    }
    logger.debug("# listValidSubscriptions method leaving...(listValidSubscriptions.json)");
    return subMap;
  }

  @RequestMapping(value = {
    "getValidSubscriptionsCount.json"
  }, method = RequestMethod.POST)
  @ResponseBody
  public int getValidSubscriptionsCount(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "serviceInstaceUuid", required = false) String serviceInstaceUuid,
      @RequestParam(value = "resourceType", required = false) String resourceType,
      @RequestParam(value = "filters", required = false) String filters,
      @RequestParam(value = "context", required = false) String context, HttpServletRequest request) {
    return listValidSubscriptions(currentTenant, tenantParam, serviceInstaceUuid, resourceType, filters, context,
        request).keySet().size();
  }

  /**
   * This method return get the Product Bundle Revision of a Subscription
   * 
   * @param currentTenant
   * @param subscriptionId
   * @param tenantParam
   * @param serviceInstanceUuid
   * @param filters
   * @param context
   * @param request
   * @return
   */
  @RequestMapping(value = {
    "getBundleBySubscription.json"
  }, method = RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> getBundleRevisionBySubscription(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "subscriptionId", required = true) String subscriptionId,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "serviceInstaceUuid", required = true) String serviceInstanceUuid,
      @RequestParam(value = "filters", required = false) String filters,
      @RequestParam(value = "context", required = false) String context, HttpServletRequest request) {
    logger.debug("# getBundleRevisionBySubscription method entered...(getBundleBySubscription.json)");
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    boolean isCompatible = false;
    ProductBundleRevision productBundleRevision = null;
    Map<String, Object> subMap = new HashMap<String, Object>();
    Subscription subscription = subscriptionService.locateSubscriptionById(new Long(subscriptionId));
    if (subscription.getProductBundle() != null && subscription.getProductBundle().getResourceType() != null) {
      productBundleRevision = getRespectiveBundleRevision(subscriptionId, effectiveTenant,
          subscription.getProductBundle());
      if (productBundleRevision == null) {
        return null;
      } else {
        isCompatible = productBundleService.isValidBundleForGivenDiscriminators(productBundleRevision,
            createStringMapCombined(context + "," + filters));
      }
    }
    logger.debug("# getBundleRevisionBySubscription method leaving...(getBundleBySubscription.json)");
    subMap.put("isCompatible", isCompatible);
    subMap.put("bundleRevision", productBundleRevision.minClone());
    subMap.put("subscriptionId", subscriptionId);
    return subMap;
  }

  /**
   * This method is used to view the Product Bundle
   * 
   * @param map
   * @param whichPlan
   * @param revisionDate
   * @return
   */
  @RequestMapping(value = {
      "showbundles", "/", ""
  }, method = RequestMethod.GET)
  public String showBundles(ModelMap map,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "revisionDate", required = false) String revisionDate) {
    logger.debug("### showBundles method starting...");

    setPage(map, Page.PRODUCTS_BUNDLES);
    map.addAttribute("whichPlan", whichPlan);
    if (whichPlan.equals("history")) {
      map.addAttribute("historyDates", productService.getHistoryDates(null));
    }
    map.addAttribute("tenant", getCurrentUser().getTenant());

    logger.debug("### showBundles method ending...");
    return "bundles.show";
  }

  /**
   * This method returns the list of Product Bundles
   * 
   * @param serviceUUID
   * @param serviceInstanceUUID
   * @param currentPage
   * @param size
   * @param namePattern
   * @param whichPlan
   * @param revisionDate
   * @param filterBy
   * @param map
   * @return
   */
  @RequestMapping(value = {
    "list"
  }, method = RequestMethod.GET)
  public String listProductBundles(@RequestParam(value = "serviceUUID", required = false) String serviceUUID,
      @RequestParam(value = "serviceInstanceUUID", required = false) String serviceInstanceUUID,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size,
      @RequestParam(value = "namePattern", required = false) String namePattern,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "revisionDate", required = false) String revisionDate,
      @RequestParam(value = "filterBy", required = false) String filterBy, ModelMap map) {
    logger.debug("### listProductBundles method starting...");

    setPage(map, Page.PRODUCTS_BUNDLES);
    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    int perPageValue = getDefaultPageSize();
    int sizeInt = 0;
    if (size == null || size.equals("")) {
      sizeInt = productBundleService.getBundlesCount();
    } else {
      sizeInt = Integer.parseInt(size);
    }

    try {
      List<String> serviceCategoryList = connectorConfigurationManager.getAllCategories(ConnectorType.CLOUD.toString());
      map.addAttribute("serviceCategoryList", serviceCategoryList);
      if (serviceUUID == null || serviceUUID.trim().equals("")) {
        serviceUUID = serviceCategoryList.get(0);
      }
    } catch (Exception e) {
      logger.error("Error in getting the Service Categories list...", e);
      return "";
    }
    map.addAttribute("serviceUUID", serviceUUID);

    try {
      List<ServiceInstance> serviceInstances = connectorManagementService.getServiceInstanceByCategory(serviceUUID);
      map.addAttribute("serviceInstances", serviceInstances);
      if (serviceInstanceUUID == null || serviceInstanceUUID.trim().equals("")) {
        serviceInstanceUUID = serviceInstances.get(0).getUuid();
      }
    } catch (Exception e) {
      logger.error("Caught Exception while getting Service instance list", e);
    }
    map.addAttribute("serviceInstanceUUID", serviceInstanceUUID);

    List<ProductBundleRevision> allProductBundleRevisions = new ArrayList<ProductBundleRevision>();
    if (whichPlan.equals("planned")) {
      allProductBundleRevisions = channelService.getChannelRevision(null,
          channelService.getFutureRevision(null).getStartDate(), false).getProductBundleRevisions();
    } else if (whichPlan.equals("current")) {
      allProductBundleRevisions = channelService.getChannelRevision(null,
          channelService.getCurrentRevision(null).getStartDate(), false).getProductBundleRevisions();
    } else if (whichPlan.equals("history")) {
      if (revisionDate == null || revisionDate.trim().equals("")) {
        List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
        revisionDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
            DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
      }

      Date historyDate = DateUtils.getCalendarForDate(revisionDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"))
          .getTime();
      allProductBundleRevisions = channelService.getChannelRevision(null, historyDate, false)
          .getProductBundleRevisions();
    }

    // Use of contains(namePattern) will list entities with the pattern string
    // anywhere, but resorting to startsWith to reflect the current behaviour
    List<ProductBundleRevision> productBundleRevisions = new ArrayList<ProductBundleRevision>();
    for (ProductBundleRevision productBundleRevision : allProductBundleRevisions) {
      if (productBundleRevision.getProductBundle().getServiceInstanceId().getUuid().equals(serviceInstanceUUID)) {
        if (namePattern != null && !namePattern.trim().equals("")
            && !productBundleRevision.getProductBundle().getName().toLowerCase().startsWith(namePattern.toLowerCase())) {
          continue;
        } else {
          productBundleRevisions.add(productBundleRevision);
        }
      }
    }
    List<ProductBundle> productBundleListByFilter = getProductBundleListByFilters(productBundleRevisions, filterBy);
    List<ProductBundle> productBundleList = getProductBundleList(productBundleListByFilter, currentPageValue,
        perPageValue);
    int totalCount = productBundleListByFilter.size();

    map.addAttribute("namePattern", namePattern);
    map.addAttribute("productBundlesList", productBundleList);
    map.addAttribute("bundlesLen", productBundleList.size());
    setPaginationValues(map, perPageValue, currentPageValue, totalCount, null);

    map.addAttribute("tenant", getCurrentUser().getTenant());
    map.addAttribute("whichPlan", whichPlan);

    logger.debug("### listProductBundles method ending..." + sizeInt);
    return "bundles.list";
  }

  /**
   * This method list all the productBundles based on the Search Pattern by product bundle name
   * 
   * @param serviceUUID
   * @param serviceInstanceUUID
   * @param currentPage
   * @param size
   * @param namePattern
   * @param whichPlan
   * @param historyDate
   * @param filterBy
   * @param map
   * @return
   */
  @RequestMapping(value = {
    "searchlist"
  }, method = RequestMethod.GET)
  public String searchByName(@RequestParam(value = "serviceUUID", required = false) String serviceUUID,
      @RequestParam(value = "serviceInstanceUUID", required = false) String serviceInstanceUUID,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size,
      @RequestParam(value = "namePattern", required = false) String namePattern,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "historyDate", required = false) String historyDate,
      @RequestParam(value = "filterBy", required = false) String filterBy, ModelMap map) {
    logger.debug("### searchByName method starting...");

    listProductBundles(serviceUUID, serviceInstanceUUID, currentPage, size, namePattern, whichPlan, historyDate,
        filterBy, map);

    logger.debug("### searchByName method ending...");
    return "bundles.search.list";
  }

  /**
   * This method list the Product Bundle for a Catalog
   * 
   * @param catalogId
   * @param map
   * @return
   */
  @RequestMapping(value = ("/{catalogId}/listbundles"), method = RequestMethod.GET)
  public String listProductBundlesByCatalog(@PathVariable String catalogId, ModelMap map) {
    logger.debug("### listProductBundles method starting...");

    setPage(map, Page.PRODUCTS_CATALOGS);
    Catalog catalog = productBundleService.getCatalog(Long.valueOf(catalogId));
    map.addAttribute("catalog", catalog);

    List<ProductBundleRevision> productBundlesList = productBundleService.listProductBundlesByCatalog(catalog, null, 0,
        0, null);

    map.addAttribute("productBundlesList", productBundlesList);
    map.addAttribute("size", productBundlesList.size());

    logger.debug("### listProductBundles method ending...");
    return "bundles.list";
  }

  /**
   * This method get the Collection of Service Resource Type Group Component name
   * 
   * @param resourceTypeId
   * @return
   */
  @RequestMapping(value = ("/get_filters_and_components"), method = RequestMethod.GET)
  @ResponseBody
  public Map<String, List<String>> getServiceResourceComponents(
      @RequestParam(value = "resourceType", required = true) long resourceTypeId) {
    logger.debug("### getServiceResourceComponents method starting...");

    // TODO: So current implementation of ServiceResourceTypeGroupComponent
    // differentiates this object by group, which should not happen.
    // So somebody needs to look into it, somebody but me.
    Map<String, List<String>> filtersAndComponents = new HashMap<String, List<String>>();
    ServiceResourceType resourceTypeObj = connectorConfigurationManager.getServiceResourceTypeById(resourceTypeId);

    List<String> filters = new ArrayList<String>();
    for (ServiceFilter serviceFilter : resourceTypeObj.getService().getServiceFilters()) {
      filters.add(serviceFilter.getDiscriminatorName());
    }
    filtersAndComponents.put("filters", filters);

    // Using list instead of set to maintain the order
    List<String> components = new ArrayList<String>();
    for (ServiceResourceTypeGroupComponent groupComponent : resourceTypeObj.getServiceResourceGroupsComponents()) {
      components.add(groupComponent.getResourceComponentName());
    }
    filtersAndComponents.put("components", components);

    logger.debug("### getServiceResourceComponents method starting...");
    return filtersAndComponents;
  }

  /**
   * This method returns the Collection of Resource Component
   * 
   * @param serviceInstanceUUID
   * @param resourceTypeId
   * @param component
   * @return
   * @throws ConnectorManagementServiceException
   */
  @RequestMapping(value = ("/getComponentValue"), method = RequestMethod.GET)
  @ResponseBody
  public Collection<ResourceComponent> getServiceResourceComponentValues(
      @RequestParam(value = "serviceInstance", required = true) final String serviceInstanceUUID,
      @RequestParam(value = "resourceType", required = true) long resourceTypeId,
      @RequestParam(value = "component", required = true) final String component)
      throws ConnectorManagementServiceException {
    logger.debug("### getServiceResourceComponentValues method starting...");

    final ServiceResourceType resourceTypeObj = connectorConfigurationManager
        .getServiceResourceTypeById(resourceTypeId);

    return privilegeService.runAsPortal(new PrivilegedAction<Collection<ResourceComponent>>() {

      @Override
      public Collection<ResourceComponent> run() {
        return ((CloudConnector) connectorManagementService.getServiceInstance(serviceInstanceUUID))
            .getMetadataRegistry().getResourceComponentValues(resourceTypeObj.getResourceTypeName(), component);
      }
    });
  }

  /**
   * This method returns the Collection of Filter Component
   * 
   * @param serviceInstanceUUID
   * @param component
   * @return
   * @throws ConnectorManagementServiceException
   */
  @RequestMapping(value = ("/get_filter_value"), method = RequestMethod.GET)
  @ResponseBody
  public Collection<FilterComponent> getServiceFilterValues(
      @RequestParam(value = "serviceInstance", required = true) final String serviceInstanceUUID,
      @RequestParam(value = "component", required = true) final String component)
      throws ConnectorManagementServiceException {
    logger.debug("### getServiceResourceComponentValues method starting...");

    return privilegeService.runAsPortal(new PrivilegedAction<Collection<FilterComponent>>() {

      @Override
      public Collection<FilterComponent> run() {
        return ((CloudConnector) connectorManagementService.getServiceInstance(serviceInstanceUUID))
            .getMetadataRegistry().getFilterValues(
                tenantService.getTenantHandle(tenantService.getSystemTenant().getUuid(), serviceInstanceUUID)
                    .getHandle(),
                userService.getUserHandleByServiceInstanceUuid(tenantService.getSystemUser(Handle.PORTAL).getUuid(),
                    serviceInstanceUUID).getHandle(), component);
      }
    });
  }

  /**
   * This method returns the collection of Service Resource Type
   * 
   * @param serviceInstanceUUID
   * @return
   */
  @RequestMapping(value = ("/getServiceResources"), method = RequestMethod.GET)
  @ResponseBody
  public Collection<ServiceResourceType> getServiceResources(
      @RequestParam(value = "serviceInstance", required = true) String serviceInstanceUUID) {
    logger.debug("### getServiceResources method starting...");
    return connectorConfigurationManager.getAvailableResourceTypesForSystem(serviceInstanceUUID);
  }

  /**
   * This method returns the collection of Association Type
   * 
   * @return
   */
  @RequestMapping(value = ("/getAssociationTypes"), method = RequestMethod.GET)
  @ResponseBody
  public Collection<AssociationType> getAssociationTypes() {
    return Arrays.asList(AssociationType.values());
  }

  /**
   * This method returns the collection of Resource Constraints
   * 
   * @param serviceResourceTypeStr
   * @return
   */
  @RequestMapping(value = ("/getApplicableBusinessConstraints"), method = RequestMethod.GET)
  @ResponseBody
  public Collection<ResourceConstraint> getApplicableBusinessConstraints(
      @RequestParam(value = "resourceType", required = true) String serviceResourceTypeStr) {
    Long serviceResourceType = null;

    try {
      serviceResourceType = Long.parseLong(serviceResourceTypeStr);
    } catch (NumberFormatException e) {
      return ORDERED_CONSTRAINTS;
    }

    ServiceResourceType resType = connectorConfigurationManager.getServiceResourceTypeById(serviceResourceType);
    ResourceConstraint constraint = resType.getConstraint();

    return ORDERED_CONSTRAINTS.subList(ORDERED_CONSTRAINTS.indexOf(constraint), ORDERED_CONSTRAINTS.size());
  }

  /**
   * This method is used to get the details for creating Produdt Bundle
   * 
   * @param serviceInstanceUUID
   * @param map
   * @return
   */
  @RequestMapping(value = ("/create"), method = RequestMethod.GET)
  public String createProductBundle(
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID, ModelMap map) {
    logger.debug("### createProductBundle method starting...(GET)");

    ProductBundle productBundle = new ProductBundle();
    ProductBundleForm productBundleForm = new ProductBundleForm(productBundle);
    productBundleForm.setCurrencyPresicion(Integer.parseInt(config
        .getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)));
    productBundleForm.setChargeRecurrenceFrequencyList(productBundleService.getChargeRecurrenceFrequencyList());

    productBundleForm.createChargeEntriesForTheProductBundle(currencyValueService.listActiveCurrencies(),
        channelService.getFutureRevision(null));

    map.addAttribute("productBundleForm", productBundleForm);
    List<ServiceInstance> instances = null;

    instances = connectorManagementService.getCloudTypeServiceInstances();
    Revision currentChargeRevision = productService.getCurrentRevision(null);

    map.addAttribute("date", currentChargeRevision.getStartDate());
    map.addAttribute("instances", instances);
    map.addAttribute("constraints", ResourceConstraint.values());
    map.addAttribute("activeCurrencies", currencyValueService.listActiveCurrencies());

    Map<String, Set<String>> serviceResourceTypesAndComponentsMap = new HashMap<String, Set<String>>();
    ServiceInstance serviceInstance = connectorConfigurationManager.getInstance(serviceInstanceUUID);
    Set<String> filters = new HashSet<String>();
    for (ServiceFilter serviceFilter : serviceInstance.getService().getServiceFilters()) {
      filters.add(serviceFilter.getDiscriminatorName());
    }
    List<ServiceResourceType> availableServiceResourceTypes = connectorConfigurationManager
        .getAvailableResourceTypesForSystem(serviceInstanceUUID);
    for (ServiceResourceType serviceResourceType : availableServiceResourceTypes) {
      List<ServiceResourceTypeGroupComponent> serviceResourceTypeGroupComponents = serviceResourceType
          .getServiceResourceGroupsComponents();
      Set<String> resourceComponents = new HashSet<String>();
      for (ServiceResourceTypeGroupComponent groupComponent : serviceResourceTypeGroupComponents) {
        resourceComponents.add(groupComponent.getResourceComponentName());
      }
      resourceComponents.addAll(filters);
      serviceResourceTypesAndComponentsMap.put(serviceResourceType.getResourceTypeName(), resourceComponents);
    }

    map.addAttribute("serviceName", serviceInstance.getService().getServiceName());
    map.addAttribute("serviceResourceTypesAndComponentsMap", serviceResourceTypesAndComponentsMap);

    logger.debug("### createProductBundle method end");
    return "bundles.new";
  }

  /**
   * This method is used to create Product Bundle
   * 
   * @param productBundleForm
   * @param result
   * @param map
   * @param response
   * @return
   * @throws JSONException
   */
  @RequestMapping(value = ("/create"), method = RequestMethod.POST)
  @ResponseBody
  public ProductBundle createProductBundle(@ModelAttribute("productBundleForm") ProductBundleForm productBundleForm,
      BindingResult result, ModelMap map, HttpServletResponse response) throws JSONException {
    logger.debug("### createProductBundle method starting...(POST)");
    ProductBundle productBundle = productBundleForm.getProductBundle();
    ProductBundleValidator validator = new ProductBundleValidator();

    validator.validate(productBundleForm, result);

    if (!productService.isCodeUnique(productBundle.getCode())) {
      logger.debug("### bundle code is NOT unique ");
      result.rejectValue("productBundle.code", "js.errors.common.CodeNotUnique");
    }

    if (result.hasErrors()) {
      if (result.getFieldError("productBundle.code") != null) {
        response.setStatus(CODE_NOT_UNIQUE_ERROR_CODE);
      }

      throw new AjaxFormValidationException(result);
    }

    JSONArray compAssociations = new JSONArray(productBundleForm.getCompAssociationJson());
    List<ProvisioningConstraint> provisioningConstraints = new ArrayList<ProvisioningConstraint>();
    for (int index = 0; index < compAssociations.length(); index++) {
      JSONObject compAssociationObj = compAssociations.getJSONObject(index);

      AssociationType associationType = AssociationType.valueOf(compAssociationObj.getString("association")
          .toUpperCase());
      String componentName = compAssociationObj.getString("compName");
      JSONArray componentVals = new JSONArray(compAssociationObj.getString("compValues"));
      for (int i = 0; i < componentVals.length(); i++) {
        JSONObject nameValDict = componentVals.getJSONObject(i);
        String compVal = nameValDict.getString("compName");
        String compValueName = nameValDict.getString("compValueName");

        ProvisioningConstraint provisioningConstraint = new ProvisioningConstraint(componentName, associationType,
            compVal, productBundle);
        provisioningConstraint.setComponentValueDisplayName(compValueName);
        provisioningConstraints.add(provisioningConstraint);
      }
    }

    productBundle = productBundleService.createBundle(productBundle, productBundleForm.getChargeType(),
        productBundleForm.getResourceType(), productBundleForm.getServiceInstanceUUID(), provisioningConstraints,
        productBundleForm.getBundleOneTimeCharges(), productBundleForm.getBundleRecurringCharges());

    logger.debug("### createProductBundle method end");
    return productBundle;
  }

  /**
   * This method is used to view the Product Bundle
   * 
   * @param id
   * @param whichPlan
   * @param map
   * @return
   */
  @RequestMapping(value = ("/view"), method = RequestMethod.GET)
  public String viewProductBundle(@RequestParam(value = "Id", required = true) String id,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan, ModelMap map) {
    logger.debug("### viewProductBundle method starting...(POST)");

    ProductBundle productBundle = productBundleService.locateProductBundleById(id);
    map.addAttribute("productBundle", productBundle);
    map.addAttribute("whichPlan", whichPlan);
    Date currentDate = new Date();
    map.addAttribute("timestamp", currentDate.getTime());

    logger.debug("### viewProductBundle method end...(POST)");
    return "bundles.view";
  }

  /**
   * This method is used to get details for editing Product Bundle
   * 
   * @param id
   * @param map
   * @return
   */
  @RequestMapping(value = ("/edit"), method = RequestMethod.GET)
  public String editProductBundle(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### editProductBundle method starting...(GET)");

    ProductBundle productBundle = productBundleService.locateProductBundleById(id);
    ProductBundleForm productBundleForm = new ProductBundleForm(productBundle);
    productBundleForm.setChargeRecurrenceFrequencyList(productBundleService.getChargeRecurrenceFrequencyList());
    map.addAttribute("productBundleForm", productBundleForm);

    Revision futureRevision = channelService.getFutureRevision(null);
    ProductBundleRevision productBundleRevision = productBundleService.getProductBundleRevision(
        productBundleForm.getProductBundle(), futureRevision.getStartDate(), null);

    String jsonProvisionalConstraints = "";
    try {
      jsonProvisionalConstraints = JSONUtils.toJSONString(productBundleRevision.getProvisioningConstraints());
    } catch (Exception e) {
      logger.error("Error in creating json from ProvisionalConstraints ...", e);
    }
    map.addAttribute("jsonProvisionalConstraints", jsonProvisionalConstraints);

    Map<String, Set<String>> serviceResourceTypesAndComponentsMap = new HashMap<String, Set<String>>();
    List<ServiceResourceType> serviceResourceTypes = productBundle.getServiceInstanceId().getService()
        .getServiceResourceTypes();
    Set<String> filters = new HashSet<String>();
    for (ServiceFilter serviceFilter : productBundle.getServiceInstanceId().getService().getServiceFilters()) {
      filters.add(serviceFilter.getDiscriminatorName());
    }
    for (ServiceResourceType serviceResourceType : serviceResourceTypes) {
      Set<String> resourceComponents = new HashSet<String>();
      List<ServiceResourceTypeGroupComponent> serviceResourceTypeGroupComponents = serviceResourceType
          .getServiceResourceGroupsComponents();
      for (ServiceResourceTypeGroupComponent groupComponent : serviceResourceTypeGroupComponents) {
        resourceComponents.add(groupComponent.getResourceComponentName());
      }
      resourceComponents.addAll(filters);
      serviceResourceTypesAndComponentsMap.put(serviceResourceType.getResourceTypeName(), resourceComponents);
    }

    map.addAttribute("serviceName", productBundle.getServiceInstanceId().getService().getServiceName());
    map.addAttribute("serviceResourceTypesAndComponentsMap", serviceResourceTypesAndComponentsMap);

    logger.debug("### editProductBundle method end...(GET)");
    return "bundles.edit";
  }

  /**
   * This method is used to edit a Product Bundle
   * 
   * @param productBundleForm
   * @param result
   * @param map
   * @return
   * @throws JSONException
   */
  @RequestMapping(value = ("/edit"), method = RequestMethod.POST)
  @ResponseBody
  public ProductBundle editProductBundle(@ModelAttribute("productBundleForm") ProductBundleForm productBundleForm,
      BindingResult result, ModelMap map) throws JSONException {
    logger.debug("### editProductBundle method starting...(POST)");

    ProductBundleValidator validator = new ProductBundleValidator();
    validator.validate(productBundleForm, result);
    if (result.hasErrors()) {
      throw new AjaxFormValidationException(result);
    }

    ProductBundle productBundle = productBundleForm.getProductBundle();
    productBundleService.updateProductBundle(productBundle, false);
    Revision futureRevision = channelService.getFutureRevision(null);
    List<ProvisioningConstraint> provisioningConstraints = productBundleService.getProductBundleRevision(productBundle,
        futureRevision.getStartDate(), null).getProvisioningConstraints();

    List<String> dbIdsRetained = new ArrayList<String>();
    JSONArray compAssociations = new JSONArray(productBundleForm.getCompAssociationJson());
    for (int index = 0; index < compAssociations.length(); index++) {
      JSONObject compAssociationObj = compAssociations.getJSONObject(index);
      String dbId = compAssociationObj.getString("compDbId");
      AssociationType associationType = AssociationType.valueOf(compAssociationObj.getString("association")
          .toUpperCase());
      String componentName = compAssociationObj.getString("compName");
      String componentVal = compAssociationObj.getString("compValue");
      String componentValName = compAssociationObj.getString("compValueName");

      productBundleService.updateProvisioningConstraints(productBundle, dbId, associationType, componentName,
          componentVal, componentValName);
      dbIdsRetained.add(dbId);
    }

    productBundleService.removeProvisioningConstraints(provisioningConstraints, dbIdsRetained, productBundle);
    logger.debug("### editProductBundle method ending...(POST)");
    return productBundle;
  }

  /**
   * This method is used to publish a Product Bundle
   * 
   * @param bundleCode
   * @param publish
   * @param map
   * @return
   */
  @RequestMapping(value = ("/publish"), method = RequestMethod.POST)
  @ResponseBody
  public String publishBundle(@RequestParam(value = "bundleCode", required = true) String bundleCode,
      @RequestParam(value = "publish", required = true) String publish, ModelMap map) {
    logger.debug("### publishBundle method starting...(POST)");

    ProductBundle bundle = productBundleService.locateProductBundleByCode(bundleCode);
    Boolean publishflag = new Boolean(publish);
    bundle.setPublish(publishflag);
    productBundleService.updateProductBundle(bundle, true);

    logger.debug("### publishBundle method ending...(POST)");
    return "success";
  }

  /**
   * This method is used to edit the Product Bundle order
   * 
   * @param productBundleOrderData
   * @param map
   * @return
   */
  @RequestMapping(value = {
    "editproductbundleorder"
  }, method = RequestMethod.POST)
  @ResponseBody
  public String editproductbundleorder(
      @RequestParam(value = "productBundleOrderData", required = true) String productBundleOrderData, ModelMap map) {
    logger.debug("### editproductbundleorder method starting...(POST)");

    String[] productRowIds;
    productRowIds = productBundleOrderData.split(",");
    String[] productIds = productBundleOrderData.split(",");
    for (int i = 0; i < productRowIds.length; i++) {
      productIds[i] = productRowIds[i].split("row")[1];
    }
    productBundleService.updateProductBundleSortOrder(productIds);

    logger.debug("### editproductbundleorder method ending...(POST)");
    return "success";
  }

  private void mapBundleEntitlements(ProductBundle bundle, ModelMap map, Date filterDate, Integer currentPage,
      Integer perPage) {
    List<Entitlement> entitlementSet = productBundleService.listEntitlements(bundle, filterDate, null, null, null);
    List<Entitlement> entitlements = productBundleService.listEntitlements(bundle, filterDate, null, currentPage,
        perPage);

    map.addAttribute("entitlements", entitlements);

    EntitlementForm entitlementForm = new EntitlementForm();
    Entitlement entitlement = new Entitlement();
    entitlement.setProductBundle(bundle);
    entitlement.setCreatedBy(getCurrentUser());
    entitlement.setUpdatedBy(getCurrentUser());
    entitlementForm.setEntitlement(entitlement);
    List<Product> products = productBundleService.getAllowedProductsForEntitlementsInProductBundle(bundle, filterDate,
        null);
    map.addAttribute("entitlementForm", entitlementForm);
    map.addAttribute("entitlementFilterDate", filterDate);
    map.addAttribute("products", products);
    map.addAttribute("currentPage", currentPage);
    map.addAttribute("perPage", perPage);
    map.addAttribute("currentPageRecords", entitlements != null ? entitlements.size() : 0);
    map.addAttribute("pages", getNumberofPages(entitlementSet.size(), perPage));
  }

  /**
   * This method is used to get details for creating Entitlements
   * 
   * @param bundleCode
   * @param response
   * @param map
   * @return
   */
  @RequestMapping(value = "/{bundleCode}/entitlement/create", method = RequestMethod.GET)
  public String createEntitlement(@PathVariable String bundleCode, HttpServletResponse response, ModelMap map) {
    logger.debug("# getEditFutureRateCard method starting...(GET)");

    ProductBundle bundle = productBundleService.findProductBundleByCode(bundleCode);
    map.put("productBundle", bundle);
    Revision futureRevision = channelService.getFutureRevision(null);
    mapBundleEntitlements(bundle, map, futureRevision.getStartDate(), null, null);

    logger.debug("# getEditFutureRateCard method ending...(GET)");
    return "bundle.entitlement.add";
  }

  /**
   * This method is used to create Entitlement for Product Bundle
   * 
   * @param bundleCode
   * @param entitlementForm
   * @param result
   * @param response
   * @param map
   * @return
   */
  @RequestMapping(value = "/{bundleCode}/entitlement/create", method = RequestMethod.POST)
  @ResponseBody
  public String createEntitlement(@PathVariable String bundleCode,
      @ModelAttribute("entitlementForm") EntitlementForm entitlementForm, BindingResult result,
      HttpServletResponse response, ModelMap map) {
    logger.debug("### createEntitlement method starting...(POST) " + entitlementForm.getProductId());

    Product product = productService.locateProductById(entitlementForm.getProductId());
    Entitlement entitlement = entitlementForm.getEntitlement();
    entitlement.setProduct(product);

    if (entitlementForm.isUnlimitedUsage()) {
      entitlement.setIncludedUnits(-1);
    }
    Revision futureRevision = channelService.getFutureRevision(null);
    entitlement = productBundleService.createEntitlement(entitlement);
    if (entitlement == null) {
      try {
        response.sendError(HttpStatus.SC_MULTIPLE_CHOICES, "error due to already added");
      } catch (IOException e) {
        logger.error(e, e);
      }
      return "failure";
    }
    map.addAttribute("entitlement", entitlement);
    populateEntitlementForm(bundleCode, map, futureRevision.getStartDate());

    logger.debug("### createEntitlement method ending...(POST)");
    return "success";
  }

  private void populateEntitlementForm(String bundleCode, ModelMap map, Date revisionDate) {
    EntitlementForm entitlementForm = new EntitlementForm();
    ProductBundle bundle = productBundleService.findProductBundleByCode(bundleCode);

    Entitlement entitlement = new Entitlement();
    entitlement.setProductBundle(bundle);
    entitlement.setCreatedBy(getCurrentUser());
    entitlement.setUpdatedBy(getCurrentUser());
    entitlementForm.setEntitlement(entitlement);
    List<Product> products = productBundleService.getAllowedProductsForEntitlementsInProductBundle(bundle,
        revisionDate, null);

    map.addAttribute("entitlementForm", entitlementForm);
    map.addAttribute("products", products);
    map.addAttribute("currency",
        Currency.getInstance(config.getValue(Names.com_citrix_cpbm_portal_settings_default_currency)));
  }

  @RequestMapping(value = ("/entitlement/{entitlementId}/save"), method = RequestMethod.POST)
  @ResponseBody
  public Entitlement saveEntitlement(@PathVariable String entitlementId,
      @RequestParam(value = "includedUnits", required = true) int includetedUnits, ModelMap map) {
    logger.debug("Entering saveEntitlement");

    Entitlement entitlement = productBundleService.saveEntitlement(entitlementId, includetedUnits);

    logger.debug("saveEntitlement end...");
    return entitlement;
  }

  /**
   * This method is used to get details for deleting Entitlements
   * 
   * @param entitlementId
   * @param bundleId
   * @param map
   * @return
   */
  @RequestMapping(value = "/entitlement/{entitlementId}/delete", method = RequestMethod.GET)
  @ResponseBody
  public Product deleteEntitlement(@PathVariable String entitlementId,
      @RequestParam(value = "bundleId", required = true) String bundleId, ModelMap map) {
    logger.debug("### deleteEntitlement method start...(GET)");

    Product product = productBundleService.deleteEntitlement(entitlementId);

    logger.debug("### deleteEntitlement method end...(GET)");
    return product;

  }

  /**
   * This method is used to filter the Entitlement
   * 
   * @param bundleCode
   * @param whichPlan
   * @param filterDate
   * @param currentPage
   * @param perPage
   * @param map
   * @param request
   * @return
   */
  @RequestMapping(value = "/{bundleCode}/entitlements/view_filtered", method = RequestMethod.GET)
  public String getFilteredEntitlements(@PathVariable String bundleCode,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "filterDate", required = false) String filterDate,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") int currentPage,
      @RequestParam(value = "perPage", required = false, defaultValue = "8") int perPage, ModelMap map,
      HttpServletRequest request) {
    logger.debug("### getFilteredEntitlements method start...(GET) : " + bundleCode);

    map.addAttribute("filterDateInFuture", false);

    Date date = null;
    if (whichPlan.equals("planned")) {
      map.addAttribute("filterDateInFuture", true);
      date = channelService.getFutureRevision(null).getStartDate();
    } else if (whichPlan.equals("current")) {
      date = channelService.getCurrentRevision(null).getStartDate();
    } else if (whichPlan.equals("history")) {
      if (filterDate != null && !filterDate.trim().equals("") && !filterDate.equals("null")) {
        date = DateUtils.getCalendarForDate(filterDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss")).getTime();
      }
    }
    ProductBundle bundle = productBundleService.findProductBundleByCode(bundleCode);
    map.addAttribute("productBundle", bundle);

    map.addAttribute("whichPlan", whichPlan);
    mapBundleEntitlements(bundle, map, date, currentPage > 0 ? currentPage : 1, perPage);

    logger.debug("### getFilteredEntitlements method end...(GET) " + map.get("entitlementFilterDate"));
    return "bundle.entitlement.list";
  }

  /**
   * This method is used to get details for Editing the logo
   * 
   * @param id
   * @param map
   * @return
   */
  @RequestMapping(value = ("/editlogo"), method = RequestMethod.GET)
  public String editBundleLogo(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### editBundleLogo method starting...(GET)");

    ProductBundleLogoForm bundleLogoForm = new ProductBundleLogoForm(productBundleService.locateProductBundleById(id));
    map.addAttribute("bundleLogoForm", bundleLogoForm);
    Date currentDate = new Date();
    map.addAttribute("currenttime", currentDate.getTime());
    setPage(map, Page.PRODUCTS_BUNDLES);

    logger.debug("### edit product logo method end...(GET)");
    return "bundles.editlogo";
  }

  /**
   * This method is used to edit the Product Bundle logo
   * 
   * @param form
   * @param result
   * @param request
   * @param map
   * @return
   */
  @RequestMapping(value = ("/editlogo"), method = RequestMethod.POST)
  public ResponseEntity<String> editBundleLogo(@ModelAttribute("bundleLogoForm") ProductBundleLogoForm form,
      BindingResult result, HttpServletRequest request, ModelMap map) {
    logger.debug("### editBundleLogo method starting...(POST)");
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "text/html; charset=utf-8");
    Locale locale = getCurrentUser().getLocale();
    String fileSize = checkFileUploadMaxSizeException(request);
    if (fileSize != null) {
      result.rejectValue("logo", "error.image.max.upload.size.exceeded");
      JsonObject error = new JsonObject();
      error.addProperty("errormessage", messageSource.getMessage(result.getFieldError("logo").getCode(), new Object[] {
        fileSize
      }, locale));
      return new ResponseEntity<String>(error.toString(), responseHeaders, org.springframework.http.HttpStatus.OK);
    }

    String rootImageDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    if (rootImageDir != null && !rootImageDir.trim().equals("")) {
      ProductBundle bundle = form.getBundle();
      ProductBundleLogoFormValidator validator = new ProductBundleLogoFormValidator();
      validator.validate(form, result);
      if (result.hasErrors()) {
        return new ResponseEntity<String>(
            messageSource.getMessage(result.getFieldError("logo").getCode(), null, locale), responseHeaders,
            org.springframework.http.HttpStatus.OK);
      } else {
        String bundlesDir = "productbundles";
        File file = new File(FilenameUtils.concat(rootImageDir, bundlesDir));
        if (!file.exists()) {
          file.mkdir();
        }
        String bundlesAbsoluteDir = FilenameUtils.concat(rootImageDir, bundlesDir);
        String relativeImageDir = FilenameUtils.concat(bundlesDir, bundle.getId().toString());
        File file1 = new File(FilenameUtils.concat(bundlesAbsoluteDir, bundle.getId().toString()));
        if (!file1.exists()) {
          file1.mkdir();
        }

        MultipartFile logoFile = form.getLogo();
        try {
          if (!logoFile.getOriginalFilename().trim().equals("")) {
            String logoFileRelativePath = writeMultiPartFileToLocalFile(rootImageDir, relativeImageDir, logoFile);
            bundle.setImagePath(logoFileRelativePath);
          }
          bundle = productBundleService.updateProductBundle(bundle, false);
        } catch (IOException e) {
          logger.debug("###IO Exception in writing custom image file");
          result.rejectValue("logo", "error.uploading.file");
          JsonObject error = new JsonObject();
          error.addProperty("errormessage",
              messageSource.getMessage(result.getFieldError("logo").getCode(), null, locale));
          return new ResponseEntity<String>(error.toString(), responseHeaders, org.springframework.http.HttpStatus.OK);
        }
      }
      String response = null;
      try {
        response = JSONUtils.toJSONString(bundle);
      } catch (JsonGenerationException e) {
        logger.debug("###IO Exception in writing custom image file");
      } catch (JsonMappingException e) {
        logger.debug("###IO Exception in writing custom image file");
      } catch (IOException e) {
        logger.debug("###IO Exception in writing custom image file");
      }
      return new ResponseEntity<String>(response, responseHeaders, org.springframework.http.HttpStatus.OK);

    } else {
      result.rejectValue("logo", "error.custom.image.upload.dir");
      JsonObject error = new JsonObject();
      error.addProperty("errormessage", messageSource.getMessage(result.getFieldError("logo").getCode(), null, locale));
      return new ResponseEntity<String>(error.toString(), responseHeaders, org.springframework.http.HttpStatus.OK);
    }
  }

  /**
   * This method is used to get the sorted list of Product Bundle
   * 
   * @param serviceInstanceUUID
   * @param map
   * @return
   */
  @RequestMapping(value = "/sortbundles", method = RequestMethod.GET)
  public String sortBundles(@RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "historyDate", required = false) String revisionDate,
      @RequestParam(value = "filterBy", required = false) String filterBy, ModelMap map) {
    logger.debug("### sortBundles method starting...");

    List<ProductBundle> productBundleListByFilter = new ArrayList<ProductBundle>();
    if (!(serviceInstanceUUID == null || serviceInstanceUUID.trim().equals(""))) {
      List<ProductBundleRevision> allProductBundleRevisions = new ArrayList<ProductBundleRevision>();
      if (whichPlan.equals("planned")) {
        allProductBundleRevisions = channelService.getChannelRevision(null,
            channelService.getFutureRevision(null).getStartDate(), false).getProductBundleRevisions();
      } else if (whichPlan.equals("current")) {
        allProductBundleRevisions = channelService.getChannelRevision(null,
            channelService.getCurrentRevision(null).getStartDate(), false).getProductBundleRevisions();
      } else if (whichPlan.equals("history")) {
        if (revisionDate == null || revisionDate.trim().equals("")) {
          List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
          revisionDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
              DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
        }

        Date historyDate = DateUtils
            .getCalendarForDate(revisionDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss")).getTime();
        allProductBundleRevisions = channelService.getChannelRevision(null, historyDate, false)
            .getProductBundleRevisions();
      }

      List<ProductBundleRevision> productBundleRevisions = new ArrayList<ProductBundleRevision>();
      for (ProductBundleRevision productBundleRevision : allProductBundleRevisions) {
        if (productBundleRevision.getProductBundle().getServiceInstanceId().getUuid().toString()
            .equals(serviceInstanceUUID.trim())) {
          productBundleRevisions.add(productBundleRevision);
        }
      }
      productBundleListByFilter = getProductBundleListByFilters(productBundleRevisions, filterBy);
    }
    map.addAttribute("bundlesList", productBundleListByFilter);
    logger.debug("### sortBundles method ending...");
    return "bundles.sort";
  }

  /**
   * This method is used to edit the Product Bundle sort order
   * 
   * @param bundlesOrderData
   * @param map
   * @return
   */
  @RequestMapping(value = {
    "editbundlesorder"
  }, method = RequestMethod.POST)
  @ResponseBody
  public String editproductsorder(@RequestParam(value = "bundlesOrderData", required = true) String bundlesOrderData,
      ModelMap map) {
    logger.debug("### editProductsOrder method starting...(POST)");

    String[] bundleIds = bundlesOrderData.split(",");
    productBundleService.updateProductBundleSortOrder(bundleIds);
    logger.info("IDs are : " + Arrays.asList(bundleIds));

    logger.debug("### editProductsOrder method ending...(POST)");
    return "success";
  }

  /**
   * This method is used to get the plan charges
   * 
   * @param map
   * @return
   */
  @RequestMapping(value = "/plancharges", method = RequestMethod.GET)
  public String planCharges(ModelMap map) {
    logger.debug("### planCharges method starting...");

    RateCardForm rateCardForm = new RateCardForm();

    Revision futureRevision = channelService.getFutureRevision(null);
    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> currentBundleChargesMap = productBundleService
        .getChargesForAllBundles(futureRevision.getStartDate());
    rateCardForm.setCurrentBundleChargesMap(currentBundleChargesMap);

    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    Set<Entry<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>>> entrySet = currentBundleChargesMap
        .entrySet();
    for (Entry<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> entry : entrySet) {
      // if current price exists then clone otherwise create new product charge object for each currency.
      if (entry.getValue() != null && entry.getValue().size() > 0) {
        rateCardForm.updateRateCardChargesFormList(entry.getKey(), entry.getValue(), futureRevision, activeCurrencies,
            false);
      } else {
        rateCardForm.updateRateCardChargesFormList(entry.getKey(), entry.getValue(), futureRevision, activeCurrencies,
            true);
      }
    }
    Date startDate = productService.hasPlanndedProductRevisions();
    if (startDate == null) {
      startDate = new Date();
    }
    rateCardForm.setStartDate(startDate);
    map.addAttribute("rateCardForm", rateCardForm);
    map.addAttribute("currencieslist", activeCurrencies);
    map.addAttribute("currencieslistsize", activeCurrencies.size());
    logger.debug("### planCharges method ending...");
    return "plan.bundle.charges";
  }

  /**
   * This method is used to create the plan charges
   * 
   * @param form
   * @param result
   * @param map
   * @return
   */
  @RequestMapping(value = "/plancharges", method = RequestMethod.POST)
  @ResponseBody
  public String planCharges(@ModelAttribute("rateCardForm") RateCardForm form, BindingResult result, ModelMap map) {
    logger.debug("### planCharges(POST) method starting...");

    productBundleService.planProductBundleCharges(form.getCurrentBundleChargesMap(), prepareBundleChargesMap(form),
        form.getStartDate());

    logger.debug("### planCharges(POST) method ending...");
    return "success";
  }

  /**
   * This method is used to view Product Bundle current charges
   * 
   * @param bundleCode
   * @param map
   * @param currenciesToDisplay
   * @return
   */
  @RequestMapping(value = "/{bundleCode}/viewbundlecurrentcharges", method = RequestMethod.GET)
  public String viewBundleCurrentCharges(@PathVariable String bundleCode, ModelMap map,
      @RequestParam(value = "currenciesToDisplay", required = false, defaultValue = "") String currenciesToDisplay) {
    logger.debug("### viewBundleCurrentCharges method starting...");

    Revision currentChargeRevision = productService.getCurrentRevision(null);
    ProductBundle bundle = productBundleService.findProductBundleByCode(bundleCode);

    Map<RateCardComponent, List<RateCardCharge>> rateCardComponentChargesMap = productBundleService
        .getReferencePriceBookBundleCharges(bundle, currentChargeRevision.getStartDate());

    RateCardChargesForm rateCardChargesForm = getRateCardChargesForm(rateCardComponentChargesMap);
    rateCardChargesForm.setBundle(bundle);

    map.addAttribute("rateCardChargesForm", rateCardChargesForm);
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    map.addAttribute("currencieslist", activeCurrencies);
    if (StringUtils.isEmpty(currenciesToDisplay)) {
      currenciesToDisplay = Integer.toString(activeCurrencies.size());
    }

    map.addAttribute("currenciesToDisplay", currenciesToDisplay);
    map.addAttribute("hasHistoricalRevisions", productService.doesReferencePriceBookHaveHistoricalRevisions());
    map.addAttribute("date", currentChargeRevision.getStartDate());

    rateCardChargesForm.setPlanForCurrentCharges(rateCardComponentChargesMap.size() == 0);

    logger.debug("### viewBundleCurrentCharges method ending...");
    return "view.bundle.current.charges";
  }

  private RateCardChargesForm getRateCardChargesForm(
      Map<RateCardComponent, List<RateCardCharge>> rateCardComponentChargesMap) {

    RateCardChargesForm rateCardChargesForm = new RateCardChargesForm();
    Set<Entry<RateCardComponent, List<RateCardCharge>>> entrySet = rateCardComponentChargesMap.entrySet();

    for (Entry<RateCardComponent, List<RateCardCharge>> entry : entrySet) {
      RateCardComponentChargesForm rateCardComponentChargesForm = new RateCardComponentChargesForm();
      RateCardComponent rcc = entry.getKey();
      rateCardComponentChargesForm.setRcc(rcc);
      List<RateCardCharge> chargesList = entry.getValue();
      if (chargesList != null && chargesList.size() > 0) {
        rateCardChargesForm.setPlanForCurrentCharges(false);
      }
      rateCardComponentChargesForm.getCharges().addAll(chargesList);
      if (rcc.isRecurring()) {
        rateCardChargesForm.getRecurringRateCardChargesFormList().add(rateCardComponentChargesForm);
      } else {
        rateCardChargesForm.getNonRecurringRateCardChargesFormList().add(rateCardComponentChargesForm);
      }
    }

    return rateCardChargesForm;
  }

  /**
   * This method is used to get details for addding the current charges
   * 
   * @param bundleCode
   * @param map
   * @return
   */
  @RequestMapping(value = "/{bundleCode}/addbundlecurrentcharges", method = RequestMethod.GET)
  public String addBundleCurrentCharges(@PathVariable String bundleCode, ModelMap map) {
    logger.debug("### addBundleCurrentCharges method starting...(GET)");

    ProductBundle bundle = productBundleService.findProductBundleByCode(bundleCode);
    List<CurrencyValue> currencyValueList = currencyValueService.listActiveCurrencies();

    RateCardChargesForm rateCardChargesForm = new RateCardChargesForm();
    rateCardChargesForm.setBundle(bundle);

    List<RateCardComponent> rccList = bundle.getRateCard().getRateCardComponents();

    if (rccList != null && rccList.size() > 0) {
      // if rate card components were already added to rate card as part of plan rate card, no need to create new again
      rateCardChargesForm.addRateCardComponentChargesForm(rccList, currencyValueList);
    } else {
      Revision currentRevision = channelService.getCurrentRevision(null);
      rateCardChargesForm.addRateCardComponentChargesForm(false, currencyValueList, currentRevision);
      if (bundle.getRateCard().getChargeType().getFrequencyInMonths().longValue() != 0) {
        rateCardChargesForm.addRateCardComponentChargesForm(true, currencyValueList, currentRevision);
      }
    }

    map.addAttribute("rateCardChargesForm", rateCardChargesForm);
    map.addAttribute("currencyValueList", currencyValueList);
    map.addAttribute("currencieslistsize", currencyValueList.size());

    logger.debug("### addBundleCurrentCharges method ending...(GET)");
    return "add.bundle.current.charges";
  }

  /**
   * This method is used to add current charges for Product Bundle
   * 
   * @param form
   * @param result
   * @param map
   * @return
   */
  @RequestMapping(value = "/addbundlecurrentcharges", method = RequestMethod.POST)
  @ResponseBody
  public String addBundleCurrentCharges(@ModelAttribute("rateCardChargesForm") RateCardChargesForm form,
      BindingResult result, ModelMap map) {
    logger.debug("### addBundleCurrentCharges(POST) method starting...");

    ProductBundle bundle = productBundleService.findProductBundleByCode(form.getBundle().getCode());
    Map<RateCardComponent, List<RateCardCharge>> rateCardcomponentChargesMap = new HashMap<RateCardComponent, List<RateCardCharge>>();
    for (RateCardComponentChargesForm componentChargesForm : form.getRecurringRateCardChargesFormList()) {
      rateCardcomponentChargesMap.put(componentChargesForm.getRcc(), componentChargesForm.getCharges());
    }
    for (RateCardComponentChargesForm componentChargesForm : form.getNonRecurringRateCardChargesFormList()) {
      rateCardcomponentChargesMap.put(componentChargesForm.getRcc(), componentChargesForm.getCharges());
    }
    productBundleService.addBundleRateCardComponentsCharge(bundle, rateCardcomponentChargesMap);

    logger.debug("### addBundleCurrentCharges(POST) method ending...");
    return "success";
  }

  /**
   * This method is used to get the channel pricing
   * 
   * @param bundleCode
   * @param currentPage
   * @param perPage
   * @param currenciesToDisplay
   * @param size
   * @param whichPlan
   * @param historyDate
   * @param map
   * @return
   */
  @RequestMapping(value = "/{bundleCode}/channelPricing", method = RequestMethod.GET)
  public String viewBundleChannelPricing(@PathVariable String bundleCode,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "perPage", required = false, defaultValue = "2") String perPage,
      @RequestParam(value = "currenciesToDisplay", required = false, defaultValue = "") String currenciesToDisplay,
      @RequestParam(value = "size", required = false) String size,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "historyDate", required = false) String historyDate, ModelMap map) {
    logger.debug("### Entering channelPricing");

    ProductBundle bundle = productBundleService.findProductBundleByCode(bundleCode);
    map.put("productBundle", bundle);

    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    int perPageValue = perPage != null ? Integer.parseInt(perPage) : 10;
    int sizeInt = 0;
    if (size == null || size.equals("")) {
      sizeInt = channelService.listChannelsByProductBundle(bundle, 0, 0).size();
    } else {
      sizeInt = Integer.parseInt(size);
    }

    int totalpages = 1;
    if (perPageValue > 0) {
      if (sizeInt != 0 && sizeInt % perPageValue == 0) {
        totalpages = sizeInt / perPageValue;
      } else {
        totalpages = (sizeInt / perPageValue) + 1;
      }
    }

    // necessary values for pagination
    map.addAttribute("channelsCurrentPage", currentPageValue);
    map.addAttribute("channelstotalpages", sizeInt);
    map.addAttribute("channelsPerPage", perPageValue);
    map.addAttribute("prevPage", currentPageValue > 1 ? currentPageValue - 1 : 1);
    map.addAttribute("nextPage", currentPageValue < totalpages ? currentPageValue + 1 : totalpages);

    if (sizeInt - (currentPageValue * perPageValue) > 0) {
      map.addAttribute("enable_next", "True");
    } else {
      map.addAttribute("enable_next", "False");
    }

    List<Channel> channelList = channelService.listChannelsByProductBundle(bundle, currentPageValue, perPageValue);
    Map<Channel, RateCardChargesForm> channelRateCardChargesFormMap = new HashMap<Channel, RateCardChargesForm>();

    Map<Channel, List<CurrencyValue>> channelCurrencyMap = new HashMap<Channel, List<CurrencyValue>>();

    int maxChannelCurrenciesSize = 1;
    for (Channel channel : channelList) {
      List<CurrencyValue> currencies = channelService.listCurrencies(channel.getParam());
      if (currencies.size() > maxChannelCurrenciesSize) {
        maxChannelCurrenciesSize = currencies.size();
      }
      channelCurrencyMap.put(channel, currencies);

      Revision channelRevision = null;
      Revision referenceCatalogRevision = null;
      if (whichPlan.equals("planned")) {
        channelRevision = channelService.getFutureRevision(channel);
        referenceCatalogRevision = channelService.getFutureRevision(null);
      } else if (whichPlan.equals("current")) {
        channelRevision = channelService.getCurrentRevision(channel);
        referenceCatalogRevision = channelService.getCurrentRevision(null);
      } else if (whichPlan.equals("history")) {
        if (historyDate == null || historyDate.trim().equals("")) {
          List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
          historyDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
              DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
        }
        Date date = DateUtils.getCalendarForDate(historyDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"))
            .getTime();
        channelRevision = channelService.getRevisionForTheDateGiven(date, channel);
        referenceCatalogRevision = channelService.getRevisionForTheDateGiven(date, null);
      }

      Map<RateCardComponent, List<RateCardCharge>> defaultRateCardCharges = new HashMap<RateCardComponent, List<RateCardCharge>>();
      Map<RateCardComponent, List<RateCardCharge>> overriddenRateCardCharges = new HashMap<RateCardComponent, List<RateCardCharge>>();

      if (channelRevision != null) {
        Date channelRevisionDate = channelRevision.getStartDate();
        Date rpbRevisionDate = referenceCatalogRevision.getStartDate();
        defaultRateCardCharges = productBundleService.getReferencePriceBookBundleCharges(bundle, rpbRevisionDate);
        overriddenRateCardCharges = productBundleService.getProductBundleChargesForTheCatalogAndDateGiven(
            channel.getCatalog(), bundle, channelRevisionDate);
      }

      RateCardChargesForm rccForm = new RateCardChargesForm();
      rccForm.setBundle(bundle);
      for (Entry<RateCardComponent, List<RateCardCharge>> rccEntry : defaultRateCardCharges.entrySet()) {
        RateCardComponent rcc = rccEntry.getKey();
        RateCardComponentChargesForm rccChargesForm = new RateCardComponentChargesForm();
        rccChargesForm.setRcc(rcc);
        rccChargesForm.setCharges(new ArrayList<RateCardCharge>());

        Map<CurrencyValue, Boolean> isCurrencyAdded = new HashMap<CurrencyValue, Boolean>();
        if (overriddenRateCardCharges.get(rcc) != null) {
          for (RateCardCharge charge : overriddenRateCardCharges.get(rcc)) {
            isCurrencyAdded.put(charge.getCurrencyValue(), true);
            rccChargesForm.getCharges().add(charge);
          }
        }

        if (defaultRateCardCharges.get(rcc) != null) {
          for (RateCardCharge charge : defaultRateCardCharges.get(rcc)) {
            if (isCurrencyAdded.get(charge.getCurrencyValue()) != null
                && isCurrencyAdded.get(charge.getCurrencyValue())) {
              continue;
            } else {
              rccChargesForm.getCharges().add(charge);
            }
          }
        }

        if (rccChargesForm.getRcc().isRecurring()) {
          rccForm.getRecurringRateCardChargesFormList().add(rccChargesForm);
        } else {
          rccForm.getNonRecurringRateCardChargesFormList().add(rccChargesForm);
        }
      }
      channelRateCardChargesFormMap.put(channel, rccForm);
    }
    if (StringUtils.isEmpty(currenciesToDisplay)) {
      currenciesToDisplay = Integer.toString(maxChannelCurrenciesSize);
    }
    map.addAttribute("currenciesToDisplay", currenciesToDisplay);
    map.addAttribute("channelCurrencyMap", channelCurrencyMap);
    map.addAttribute("channelRateCardChargesFormMap", channelRateCardChargesFormMap);
    logger.debug("### Exiting channelPricing");
    return "bundle.channelpricing";
  }

  /**
   * This method is used to get Provisioning Constraints
   * 
   * @param bundleCode
   * @param whichPlan
   * @param historyDate
   * @param map
   * @return
   */
  @RequestMapping(value = "/{bundleCode}/provisioningconstraints", method = RequestMethod.GET)
  public String viewProvisioningConstraints(@PathVariable String bundleCode,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "historyDate", required = false) String historyDate, ModelMap map) {
    logger.debug("### Entering provisioningconstraints");

    ProductBundle bundle = productBundleService.findProductBundleByCode(bundleCode);
    map.put("productBundle", bundle);

    List<ProvisioningConstraint> provisioningConstarints = new ArrayList<ProvisioningConstraint>();
    if (whichPlan.equals("planned")) {
      provisioningConstarints = productBundleService.getProductBundleRevision(bundle,
          channelService.getFutureRevision(null).getStartDate(), null).getProvisioningConstraints();
    } else if (whichPlan.equals("current")) {
      provisioningConstarints = productBundleService.getProductBundleRevision(bundle,
          channelService.getCurrentRevision(null).getStartDate(), null).getProvisioningConstraints();
    } else if (whichPlan.equals("history")) {
      if (historyDate == null || historyDate.trim().equals("")) {
        List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
        historyDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
            DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
      }

      Date historyRevDate = DateUtils
          .getCalendarForDate(historyDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss")).getTime();
      provisioningConstarints = productBundleService.getProductBundleRevision(bundle, historyRevDate, null)
          .getProvisioningConstraints();
    }
    map.addAttribute("constraints", provisioningConstarints);
    logger.debug("### Exiting constraints");
    return "bundle.provisioningconstraints";
  }

  /**
   * This method is used to get the planned charges
   * 
   * @param map
   * @return
   */
  @RequestMapping(value = "/viewplannedcharges", method = RequestMethod.GET)
  public String viewPlannedCharges(ModelMap map) {
    logger.debug("### viewPlannedCharges method starting...");

    RateCardForm rateCardForm = new RateCardForm();
    Revision futureRevision = channelService.getFutureRevision(null);
    rateCardForm.setStartDate(futureRevision.getStartDate());

    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> plannedBundleChargesMap = productBundleService
        .getPlannedChargesForAllBundles(futureRevision.getStartDate());

    Set<Entry<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>>> entrySet = plannedBundleChargesMap
        .entrySet();
    for (Entry<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> entry : entrySet) {
      RateCardChargesForm rateCardChargesForm = new RateCardChargesForm();
      rateCardChargesForm.setBundle(entry.getKey());
      Map<RateCardComponent, List<RateCardCharge>> rateCardComponentChargesMap = entry.getValue();
      Set<Entry<RateCardComponent, List<RateCardCharge>>> rateCardComponentEntrySet = rateCardComponentChargesMap
          .entrySet();

      for (Entry<RateCardComponent, List<RateCardCharge>> rateCardComponentEntry : rateCardComponentEntrySet) {
        RateCardComponentChargesForm rateCardComponentChargesForm = new RateCardComponentChargesForm();
        RateCardComponent rcc = rateCardComponentEntry.getKey();
        rateCardComponentChargesForm.setRcc(rcc);
        rateCardComponentChargesForm.getCharges().addAll(rateCardComponentEntry.getValue());

        if (rcc.isRecurring()) {
          rateCardChargesForm.getRecurringRateCardChargesFormList().add(rateCardComponentChargesForm);
        } else {
          rateCardChargesForm.getNonRecurringRateCardChargesFormList().add(rateCardComponentChargesForm);
        }
      }
      rateCardForm.getRateCardChargesList().add(rateCardChargesForm);
    }

    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    map.addAttribute("currencieslist", activeCurrencies);
    map.addAttribute("currencieslistsize", activeCurrencies.size());
    map.addAttribute("rateCardForm", rateCardForm);
    return "view.all.bundle.planned.charges";
  }

  /**
   * This method is used to get the details for editting the plan charges
   * 
   * @param map
   * @return
   */
  @RequestMapping(value = "/editplannedcharges", method = RequestMethod.GET)
  public String editPlannedCharges(ModelMap map) {
    logger.debug("### editPlannedCharges method starting...");

    RateCardForm rateCardForm = new RateCardForm();

    Date startDate = productBundleService.hasPlanndedBundleRevisions();
    if (startDate == null) {
      startDate = new Date();
    }

    rateCardForm.setStartDate(startDate);
    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> plannedBundleChargesMap = productBundleService
        .getPlannedChargesForAllBundles(channelService.getFutureRevision(null).getStartDate());

    rateCardForm.setCurrencyPresicion(Integer.parseInt(config
        .getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)));
    rateCardForm.setCurrentBundleChargesMap(plannedBundleChargesMap);
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    Revision futureRevision = channelService.getFutureRevision(null);
    Set<Entry<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>>> entrySet = plannedBundleChargesMap
        .entrySet();
    for (Entry<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> entry : entrySet) {
      try {
        // if current price exists then clone otherwise create new product charge object for each currency.
        if (entry.getValue() != null && entry.getValue().size() > 0) {

          rateCardForm.updateRateCardChargesFormList(entry.getKey(), entry.getValue(), futureRevision,
              activeCurrencies, false);

        } else {
          rateCardForm.updateRateCardChargesFormList(entry.getKey(), entry.getValue(), futureRevision,
              activeCurrencies, true);
        }
      } catch (ArithmeticException e) {
        logger.error("ArithmeticException while editing the product charge, Possible Cause- "
            + "the currency precision level was reduced " + e);
        throw new CurrencyPrecisionException(e);
      }
    }
    map.addAttribute("rateCardForm", rateCardForm);
    map.addAttribute("currencieslist", activeCurrencies);
    map.addAttribute("currencieslistsize", activeCurrencies.size());

    logger.debug("### viewPlannedCharges method ending...");
    return "edit.bundle.planned.charges";
  }

  /**
   * This method is used to edit the plan charges
   * 
   * @param form
   * @param result
   * @param map
   * @return
   */
  @RequestMapping(value = "/editplannedcharges", method = RequestMethod.POST)
  @ResponseBody
  public String editPlannedCharges(@ModelAttribute("rateCardForm") RateCardForm form, BindingResult result, ModelMap map) {
    logger.debug("### editPlannedCharges(POST) method starting...");

    productBundleService.editPlannedProductBundleCharges(form.getCurrentBundleChargesMap(),
        prepareBundleChargesMap(form), channelService.getFutureRevision(null));

    logger.debug("### editPlannedCharges(POST) method ending...");
    return "success";
  }

  private Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> prepareBundleChargesMap(RateCardForm form) {
    Map<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>> newBundleChargesMap = new HashMap<ProductBundle, Map<RateCardComponent, List<RateCardCharge>>>();
    for (RateCardChargesForm chargeForm : form.getRateCardChargesList()) {

      Map<RateCardComponent, List<RateCardCharge>> rateCardComponentChargeMap = new HashMap<RateCardComponent, List<RateCardCharge>>();
      for (RateCardComponentChargesForm recurringRateCardComponentCharge : chargeForm
          .getRecurringRateCardChargesFormList()) {
        rateCardComponentChargeMap.put(recurringRateCardComponentCharge.getRcc(),
            recurringRateCardComponentCharge.getCharges());
      }

      for (RateCardComponentChargesForm nonRecurringRateCardComponentCharge : chargeForm
          .getNonRecurringRateCardChargesFormList()) {
        rateCardComponentChargeMap.put(nonRecurringRateCardComponentCharge.getRcc(),
            nonRecurringRateCardComponentCharge.getCharges());
      }

      ProductBundle bundle = productBundleService.findProductBundleByCode(chargeForm.getBundle().getCode());
      newBundleChargesMap.put(bundle, rateCardComponentChargeMap);
    }

    return newBundleChargesMap;
  }

  /**
   * This method is used to view planned charges
   * 
   * @param bundleCode
   * @param map
   * @param currenciesToDisplay
   * @return
   */
  @RequestMapping(value = "/{bundleCode}/viewbundleplannedcharges", method = RequestMethod.GET)
  public String viewBundlePlannedCharges(@PathVariable String bundleCode, ModelMap map,
      @RequestParam(value = "currenciesToDisplay", required = false, defaultValue = "") String currenciesToDisplay) {
    logger.debug("### viewBundlePlannedCharges method starting...");

    ProductBundle bundle = productBundleService.findProductBundleByCode(bundleCode);
    Revision futureRevision = channelService.getFutureRevision(null);

    Map<RateCardComponent, List<RateCardCharge>> rateCardComponentChargesMap = productBundleService
        .getReferencePriceBookPlannedBundleCharges(bundle, futureRevision.getStartDate());

    RateCardChargesForm rateCardChargesForm = getRateCardChargesForm(rateCardComponentChargesMap);
    rateCardChargesForm.setBundle(bundle);

    map.addAttribute("rateCardChargesForm", rateCardChargesForm);

    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    map.addAttribute("currencieslist", activeCurrencies);
    if (StringUtils.isEmpty(currenciesToDisplay)) {
      currenciesToDisplay = Integer.toString(activeCurrencies.size());
    }
    map.addAttribute("currenciesToDisplay", currenciesToDisplay);

    map.addAttribute("date", futureRevision.getStartDate());
    map.addAttribute("hasHistoricalRevisions", productService.doesReferencePriceBookHaveHistoricalRevisions());

    logger.debug("### viewBundlePlannedCharges method ending...");
    return "view.bundle.planned.charges";
  }

  /**
   * This method is used to view history charges
   * 
   * @param productBundleCode
   * @param revisionDate
   * @param map
   * @param currenciesToDisplay
   * @return
   */
  @RequestMapping(value = "/{productBundleCode}/viewbundlechargeshistorydata", method = RequestMethod.GET)
  public String viewBundleChargesHistoryData(@PathVariable String productBundleCode,
      @RequestParam(value = "revisionDate", required = true) String revisionDate, ModelMap map,
      @RequestParam(value = "currenciesToDisplay", required = false, defaultValue = "") String currenciesToDisplay) {

    logger.debug("### viewproductbundlechargeshistorydata method starting...");

    ProductBundle productBundle = productBundleService.locateProductBundleByCode(productBundleCode);

    if (revisionDate == null || revisionDate.equals("")) {
      List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
      revisionDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
          DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
    }

    Date historyDate = DateUtils.getCalendarForDate(revisionDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"))
        .getTime();
    Map<RateCardComponent, List<RateCardCharge>> chargesHistoryMap = productBundleService
        .getReferencePriceBookBundleCharges(productBundle, historyDate);

    RateCardChargesForm rateCardChargesForm = getRateCardChargesForm(chargesHistoryMap);
    rateCardChargesForm.setBundle(productBundle);
    map.addAttribute("rateCardChargesForm", rateCardChargesForm);

    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    if (StringUtils.isEmpty(currenciesToDisplay)) {
      currenciesToDisplay = Integer.toString(activeCurrencies.size());
    }
    map.addAttribute("currencieslist", activeCurrencies);
    map.addAttribute("currenciesToDisplay", currenciesToDisplay);
    map.addAttribute("date", historyDate);

    logger.debug("### viewproductbundlechargeshistorydata method ending...");
    return "productbundle.charges.history";
  }

  /**
   * This method is used to get Product Bundle
   * 
   * @param currentTenant
   * @param tenantParam
   * @param bundleId
   * @return
   */
  @RequestMapping(value = {
    "getBundle.json"
  }, method = RequestMethod.GET)
  @ResponseBody
  public ProductBundle getProductBundle(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "bundleId", required = true) String bundleId) {
    logger.debug("### getProductBundle method starting...");

    ProductBundle bundle = productBundleService.locateProductBundleById(bundleId);

    logger.debug("### getProductBundle method ending...");
    return bundle;
  }

  private List<ProductBundleRevision> getLighterProductBundles(List<ProductBundleRevision> bundleRevisions) {
    if (bundleRevisions == null) {
      return null;
    }

    List<ProductBundleRevision> returnProductBundle = new ArrayList<ProductBundleRevision>();
    for (ProductBundleRevision pbr : bundleRevisions) {
      returnProductBundle.add(pbr.minClone());
    }

    return returnProductBundle;
  }

  @RequestMapping(value = {
    "getResourceComponentvalues.json"
  }, method = RequestMethod.GET)
  @ResponseBody
  public String getResourceComponentDetails(
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID,
      @RequestParam(value = "resourceType", required = true) Long resourceType,
      @RequestParam(value = "resourceComponentName", required = true) String resourceComponentName,
      @RequestParam(value = "resourceComponentVal", required = true) String resourceComponentVal)
      throws ConnectorManagementServiceException {
    logger.debug("### getResourceComponentValues method starting...");
    String displayAttribute = null;
    List<ServiceFilter> serviceFilters = connectorConfigurationManager.getInstanceByUUID(serviceInstanceUUID)
        .getService().getServiceFilters();
    Iterator<ServiceFilter> sfIterator = serviceFilters.iterator();
    while (sfIterator.hasNext()) {
      ServiceFilter serviceFilter = sfIterator.next();
      if (resourceComponentName.equals(serviceFilter.getDiscriminatorName())) {
        Collection<FilterComponent> filterComponents = getServiceFilterValues(serviceInstanceUUID,
            resourceComponentName);
        Iterator<FilterComponent> itrFilterComp = filterComponents.iterator();
        while (itrFilterComp.hasNext()) {
          FilterComponent objFC = itrFilterComp.next();
          if (objFC.getValue().equals(resourceComponentVal)) {
            displayAttribute = objFC.getDisplayAttributesString();
            break;
          }
        }
        return displayAttribute;
      }
    }
    Collection<ResourceComponent> resourceComponents = getServiceResourceComponentValues(serviceInstanceUUID,
        resourceType, resourceComponentName);
    Iterator<ResourceComponent> itr = resourceComponents.iterator();
    while (itr.hasNext()) {
      ResourceComponent objRC = itr.next();
      if (objRC.getValue().equals(resourceComponentVal)) {
        displayAttribute = objRC.getDisplayAttributesString();
        break;
      }
    }
    logger.debug("### getResourceComponentValues method ending...");
    return displayAttribute;
  }

  @RequestMapping(value = ("/getPlannedEntitlements"), method = RequestMethod.GET)
  public String getPlannedEntitlements(
      @RequestParam(value = "bundleid", required = true) String bundleId,
      @RequestParam(value = "channelid", required = true) String channelId,
      ModelMap map) {
    logger.debug("### getPlannedEntitlements method starting...(GET)");

    ProductBundle bundle = productBundleService.locateProductBundleById(bundleId);
    Channel channel = channelService.getChannelById(channelId);
    ProductBundleRevision bundleRev = productBundleService.getProductBundleRevision(bundle, null, channel);

    map.addAttribute("entitlements", bundleRev.getEntitlements());
    return "catalog.entitlements.diag";
  }
}
