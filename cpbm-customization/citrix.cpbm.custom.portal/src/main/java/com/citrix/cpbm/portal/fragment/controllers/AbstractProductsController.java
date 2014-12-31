/*
 * Copyright Â© 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.PrivilegedAction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.admin.service.exceptions.ConnectorManagementServiceException;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.google.gson.JsonObject;
import com.vmops.internal.service.PrivilegeService;
import com.vmops.model.Category;
import com.vmops.model.Channel;
import com.vmops.model.ChannelRevision;
import com.vmops.model.CurrencyValue;
import com.vmops.model.Entitlement;
import com.vmops.model.MediationRule;
import com.vmops.model.MediationRuleDiscriminator;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.ProductCharge;
import com.vmops.model.ProductRevision;
import com.vmops.model.Revision;
import com.vmops.model.Service;
import com.vmops.model.ServiceDiscriminator;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceUsageType;
import com.vmops.model.ServiceUsageTypeUomScale;
import com.vmops.model.Tenant;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.CurrencyValueService;
import com.vmops.service.ProductService;
import com.vmops.service.TenantService;
import com.vmops.service.exceptions.CloudServiceException;
import com.vmops.service.exceptions.CurrencyPrecisionException;
import com.vmops.service.exceptions.FileUploadException;
import com.vmops.utils.DateUtils;
import com.vmops.utils.JSONUtils;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.ProductForm;
import com.vmops.web.forms.ProductForm.ProductChargesForm;
import com.vmops.web.forms.ProductLogoForm;
import com.vmops.web.interceptors.UserContextInterceptor;
import com.vmops.web.validators.ProductLogoFormValidator;

/**
 * Controller that handles all the product pages
 * 
 * @author Venkat
 */

public abstract class AbstractProductsController extends AbstractAuthenticatedController {

  @Autowired
  private ProductService productService;

  @Autowired
  private CurrencyValueService currencyValueService;

// CHECKSTYLE:OFF
  @Autowired
  protected PrivilegeService privilegeService;

// CHECKSTYLE:ON
  @Autowired
  private ChannelService channelService;

// CHECKSTYLE:OFF
  @Autowired
  protected ConnectorConfigurationManager connectorConfigurationManager;

// CHECKSTYLE:ON
  @Autowired
  private ConnectorManagementService connectorManagementService;

// CHECKSTYLE:OFF
  @Autowired
  protected ServiceInstanceDao serviceInstanceDao;

// CHECKSTYLE:ON
  @Autowired
  private Configuration configuration;

  private final Logger logger = Logger.getLogger(AbstractProductsController.class);

  private List<Product> getProductList(List<Product> allProductList, int pageNo, int perPage) {
    logger.debug("Entering getProductsList...");

    int count = 0;
    int toStartFrom = (pageNo - 1) * perPage;
    List<Product> productList = new ArrayList<Product>();
    for (Product product : allProductList) {
      if (count >= toStartFrom) {
        productList.add(product);
        if (productList.size() == perPage) {
          break;
        }
      }
      count += 1;
    }
    logger.debug("Leaving getProductsList....");
    return productList;
  }

  private List<Product> getProductListByFilters(List<Product> allProductList, String filterBy, String category,
      Revision effectiveRevision) {
    logger.debug("Entering getProductsListByFilters...");
    List<Product> productList = new ArrayList<Product>();

    if ((StringUtils.isEmpty(filterBy) || "All".equalsIgnoreCase(filterBy))
        && (StringUtils.isEmpty(category) || "All".equalsIgnoreCase(category))) {
      productList.addAll(allProductList);
    } else {
      for (Product product : allProductList) {
        if ((StringUtils.isEmpty(filterBy) || "All".equalsIgnoreCase(filterBy))
            && (product.getCategory().getName().equalsIgnoreCase(category))) {
          productList.add(product);
        } else {
          if (StringUtils.isEmpty(category) || "All".equalsIgnoreCase(category)) {
            if ("Active".equalsIgnoreCase(filterBy)
                && (product.getRemovedBy() == null || (product.getRemovedBy() != null && product.getRemovedInRevision()
                    .getId() > effectiveRevision.getId()))) {
              productList.add(product);
            } else if ("Retire".equalsIgnoreCase(filterBy) && product.getRemovedBy() != null
                && product.getRemovedInRevision().getId() <= effectiveRevision.getId()) {
              productList.add(product);
            }
          } else {
            if ("Active".equalsIgnoreCase(filterBy)
                && (product.getRemovedBy() == null || (product.getRemovedBy() != null && product.getRemovedInRevision()
                    .getId() > effectiveRevision.getId()))
                && product.getCategory().getName().equalsIgnoreCase(category)) {
              productList.add(product);
            } else if ("Retire".equalsIgnoreCase(filterBy) && product.getRemovedBy() != null
                && product.getCategory().getName().equalsIgnoreCase(category)
                && product.getRemovedInRevision().getId() <= effectiveRevision.getId()) {
              productList.add(product);
            }
          }
        }
      }
    }
    logger.debug("Leaving getProductsListByFilters....");
    return productList;
  }

  class ProductSortOrderSort implements Comparator<Product> {

    @Override
    public int compare(Product prod1, Product prod2) {
      if (prod1.getCategory().getOrder() > prod2.getCategory().getOrder()) {
        return 1;
      } else if (prod1.getCategory().getOrder() == prod2.getCategory().getOrder()) {
        if (prod1.getSortOrder() > prod2.getSortOrder()) {
          return 1;
        } else if (prod1.getSortOrder() == prod2.getSortOrder()) {
          return Integer.valueOf(prod1.getId().toString()) - Integer.valueOf(prod2.getId().toString());
        } else {
          return -1;
        }
      } else {
        return -1;
      }
    }
  }

  class ProductChargeCurrencyValueSort implements Comparator<ProductCharge> {

    @Override
    public int compare(ProductCharge pc1, ProductCharge pc2) {
      if (pc1.getCurrencyValue().getRank() == pc2.getCurrencyValue().getRank()) {
        return 0;
      } else if (pc1.getCurrencyValue().getRank() < pc2.getCurrencyValue().getRank()) {
        return -1;
      } else {
        return 1;
      }
    }
  }

  @RequestMapping(value = ("/isCurrentAndHistoryApplicableForRPB"), method = RequestMethod.GET)
  @ResponseBody
  public Map<String, String> isCurrentAndHistoryApplicableForRPB() {
    logger.debug("### isCurrentAndHistoryApplicableForRPB method starting...");

    Map<String, String> currentAndHistoryApplicabilityMap = new HashMap<String, String>();

    currentAndHistoryApplicabilityMap.put("current", "false");
    currentAndHistoryApplicabilityMap.put("history", "false");
    Revision currentRevision = channelService.getCurrentRevision(null);
    if (currentRevision != null && currentRevision.getStartDate() != null
        && (currentRevision.getStartDate().getTime() <= (new Date()).getTime())) {
      currentAndHistoryApplicabilityMap.put("current", "true");
    }

    List<Date> historyDates = productService.getHistoryDates(null);
    if (historyDates != null && historyDates.size() > 0) {
      currentAndHistoryApplicabilityMap.put("history", "true");
    }
    List<Service> serviceCategoryList = connectorConfigurationManager.getAllServicesByType(ConnectorType.CLOUD
        .toString());
    boolean isAtleastOneCloudServiceEnabled = false;
    if (serviceCategoryList != null && serviceCategoryList.size() > 0) {
      for (Service service : serviceCategoryList) {
        if (CollectionUtils.isNotEmpty(service.getServiceInstances())) {
          isAtleastOneCloudServiceEnabled = true;
          break;
        }
      }
    }
    currentAndHistoryApplicabilityMap.put("isAtleastOneCloudServiceEnabled", isAtleastOneCloudServiceEnabled + "");
    logger.debug("### isCurrentAndHistoryApplicableForRPB method starting...");
    return currentAndHistoryApplicabilityMap;
  }

  @RequestMapping(value = {
      "getServiceCategories", "/", ""
  }, method = RequestMethod.GET)
  public String getServiceCategories(ModelMap map,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "revisionDate", required = false) String revisionDate) {
    logger.debug("### showProducts method starting...");

    setPage(map, Page.PRODUCTS);

    map.addAttribute("whichPlan", whichPlan);
    if (whichPlan.equals("history")) {
      List<Date> historyDates = productService.getHistoryDates(null);
      map.addAttribute("historyDates", historyDates);
      if (historyDates != null && historyDates.size() > 0) {
        map.addAttribute("revisionDate", historyDates.get(0));
      } else {
        map.addAttribute("revisionDate", "");
      }
    } else if (whichPlan.equals("planned")) {
      map.addAttribute("futurePlanDate", channelService.getFutureRevision(null).getStartDate());
    } else if (whichPlan.equals("current")) {
      map.addAttribute("currentPlanDate", channelService.getCurrentRevision(null).getStartDate());
    }

    List<Service> serviceCategoryList = new ArrayList<Service>();
    Set<String> serviceCategory = new LinkedHashSet<String>();
    try {
      serviceCategoryList = connectorConfigurationManager.getAllServicesByType(ConnectorType.CLOUD.toString());
      for (Service service : serviceCategoryList) {
        List<ServiceInstance> serviceInstances = connectorConfigurationManager.getServiceInstances(service.getUuid());
        if (CollectionUtils.isNotEmpty(serviceInstances)) {
          serviceCategory.add(service.getCategory());
        }

      }
      if (serviceCategoryList != null && serviceCategoryList.size() > 0) {
        map.addAttribute("selectedCategory", serviceCategoryList.get(0).getCategory());
      } else {
        map.addAttribute("selectedCategory", "");
      }
    } catch (Exception e) {
      logger.error("Error in getting the Service Categories list...", e);
    }
    map.addAttribute("serviceCategoryList", serviceCategory);

    logger.debug("### showProducts method ending...");
    return "products.service.categories.show";
  }

  @RequestMapping(value = {
    "getServiceInstances"
  }, method = RequestMethod.GET)
  public String getServiceInstances(ModelMap map,
      @RequestParam(value = "serviceUUID", required = true) String serviceUUID,
      @RequestParam(value = "whichPlan", required = true, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "revisionDate", required = false) String revisionDate) {
    logger.debug("### showServiceInstances method starting...");

    setPage(map, Page.PRODUCTS);

    map.addAttribute("whichPlan", whichPlan);
    if (whichPlan.equals("history")) {
      map.addAttribute("historyDates", productService.getHistoryDates(null));
      map.addAttribute("revisionDate", revisionDate);
    } else if (whichPlan.equals("planned")) {
      map.addAttribute("futurePlanDate", channelService.getFutureRevision(null).getStartDate());
    } else if (whichPlan.equals("current")) {
      map.addAttribute("currentPlanDate", channelService.getCurrentRevision(null).getStartDate());
    }

    List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
    try {
      serviceInstances = connectorManagementService.getServiceInstanceByCategory(serviceUUID);
    } catch (Exception e) {
      logger.error("Caught Exception while getting Service instance list", e);
    }

    List<Service> serviceCategoryList = new ArrayList<Service>();
    try {
      serviceCategoryList = connectorConfigurationManager.getAllServicesByType(ConnectorType.CLOUD.toString());
    } catch (Exception e) {
      logger.error("Error in getting the Service Categories list...", e);
    }
    map.addAttribute("serviceCategoryList", serviceCategoryList);
    map.addAttribute("serviceUUID", serviceUUID);

    map.addAttribute("serviceInstances", serviceInstances);

    logger.debug("### showServiceInstances method ending...");
    return "products.instances.show";
  }

  /**
   * List all the products
   * 
   * @return
   */
  @RequestMapping(value = {
    "listproducts"
  }, method = RequestMethod.GET)
  public String listProducts(@RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size,
      @RequestParam(value = "namePattern", required = false) String namePattern,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "revisionDate", required = false) String revisionDate,
      @RequestParam(value = "filterBy", required = false) String filterBy,
      @RequestParam(value = "category", required = false) String category, ModelMap map) {
    logger.debug("### listProdcuts method starting...");

    setPage(map, Page.PRODUCTS);

    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    // Page size is decreased by one as category section is included.
    int perPageValue = getDefaultPageSize() - 1;

    List<ProductRevision> allProductRevisions = new ArrayList<ProductRevision>();
    Revision effectiveRevision = null;
    if (whichPlan.equals("planned")) {
      effectiveRevision = channelService.getFutureRevision(null);
      allProductRevisions = channelService.getChannelRevision(null, effectiveRevision.getStartDate(), false)
          .getProductRevisions();
    } else if (whichPlan.equals("current")) {
      effectiveRevision = channelService.getCurrentRevision(null);
      if (effectiveRevision.getStartDate() != null && effectiveRevision.getStartDate().before(new Date())) {
        allProductRevisions = channelService.getChannelRevision(null, effectiveRevision.getStartDate(), false)
            .getProductRevisions();
      }
    } else if (whichPlan.equals("history")) {
      if (revisionDate == null || revisionDate.trim().equals("")) {
        List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
        revisionDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
            DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
      }

      Date historyDate = DateUtils.getCalendarForDate(revisionDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"))
          .getTime();
      effectiveRevision = channelService.getRevisionForTheDateGiven(historyDate, null);
      allProductRevisions = channelService.getChannelRevision(null, historyDate, false).getProductRevisions();
    }

    // Use of contains(namePattern) will list entities with the pattern string
    // anywhere, but resorting to startsWith to reflect the current behaviour
    List<Product> allProductList = new ArrayList<Product>();
    for (ProductRevision productRevision : allProductRevisions) {
      Product product = productRevision.getProduct();
      // Add only those products which have removedBy null or if it is removed, then in a revision after the
      // revision under consideration
      if (effectiveRevision != null && product.getServiceInstance() != null
          && product.getServiceInstance().getUuid().equals(serviceInstanceUUID)) {
        if (namePattern != null && !namePattern.trim().equals("")
            && !product.getName().toLowerCase().startsWith(namePattern.toLowerCase())) {
          continue;
        } else {
          allProductList.add(product);
        }
      }
    }

    Collections.sort(allProductList, new ProductSortOrderSort());
    List<Product> productListByFilters = getProductListByFilters(allProductList, filterBy, category, effectiveRevision);
    List<Product> productList = getProductList(productListByFilters, currentPageValue, perPageValue);

    map.addAttribute("namePattern", namePattern);
    map.addAttribute("productsList", productList);
    map.addAttribute("size", productListByFilters.size());

    int sizeInt = 0;
    if (size == null || size.equals("")) {
      sizeInt = productListByFilters.size();
    } else {
      sizeInt = Integer.parseInt(size);
    }
    setPaginationValues(map, perPageValue, currentPageValue, sizeInt, null);

    long numberOfPages = getNumberofPages(productListByFilters.size(), perPageValue);
    map.addAttribute("enableNext", Long.parseLong(currentPage) < numberOfPages);
    map.addAttribute("tenant", getCurrentUser().getTenant());
    map.addAttribute("whichPlan", whichPlan);

    List<Category> categories = productService.getAllCategories();
    map.addAttribute("categories", categories);

    logger.debug("### listProdcuts method ending...");
    return "products.list";
  }

  /**
   * List all the products
   * 
   * @return
   */
  @RequestMapping(value = {
    "searchlist"
  }, method = RequestMethod.GET)
  public String searchListByName(
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID,
      @RequestParam(value = "currentPage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size,
      @RequestParam(value = "namePattern", required = false) String namePattern,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "revisionDate", required = false) String revisionDate,
      @RequestParam(value = "filterBy", required = false) String filterBy,
      @RequestParam(value = "category", required = false) String category, ModelMap map) {
    logger.debug("### searchListByName method starting...");

    listProducts(serviceInstanceUUID, currentPage, size, namePattern, whichPlan, revisionDate, filterBy, category, map);

    logger.debug("### searchListByName method ending...");
    return "products.search.list";
  }

  @RequestMapping(value = ("/createproduct"), method = RequestMethod.GET)
  public String createProduct(@RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID,
      ModelMap map) {
    logger.debug("### createProduct method starting...(GET)");

    Product product = new Product();
    ProductForm productForm = new ProductForm(product);

    map.addAttribute("productForm", productForm);

    Date plannedDate = productService.hasPlanndedProductRevisions();
    map.addAttribute("hasplannedcharges", plannedDate != null ? true : false);

    Revision currentRevision = productService.getCurrentRevision(null);
    map.addAttribute("date", currentRevision.getStartDate());

    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    map.addAttribute("activeCurrencies", activeCurrencies);

    List<Category> categories = productService.getAllCategories();
    map.addAttribute("categories", categories);
    productForm.setCurrencyPresicion(Integer.parseInt(config
        .getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)));
    productForm.createChargeEntriesForTheProductYetToBeCreated(activeCurrencies, currentRevision);

    Set<String> serviceUsageTypeNames = new HashSet<String>();
    Set<String> discrimintaorNames = new HashSet<String>();
    ServiceInstance serviceInstance = connectorConfigurationManager.getInstance(serviceInstanceUUID);
    for (ServiceUsageType serviceUsageType : serviceInstance.getService().getServiceUsageTypes()) {
      serviceUsageTypeNames.add(serviceUsageType.getUsageTypeName());
      for (ServiceDiscriminator serviceDiscriminator : serviceInstance.getService().getServiceUsageTypeDiscriminator(
          serviceUsageType)) {
        discrimintaorNames.add(serviceDiscriminator.getDiscriminatorName());
      }
    }

    map.addAttribute("serviceName", serviceInstance.getService().getServiceName());
    map.addAttribute("serviceUuid", serviceInstance.getService().getUuid());
    map.addAttribute("serviceUsageTypeNames", serviceUsageTypeNames);
    map.addAttribute("discrimintaorNames", discrimintaorNames);

    logger.debug("### createProduct method end");
    return "products.new";
  }

  @RequestMapping(value = ("/listProductCategories"), method = RequestMethod.GET)
  @ResponseBody
  public List<Category> listProductCategories() {
    return productService.getAllCategories();
  }

  @RequestMapping(value = ("/listUsageTypes"), method = RequestMethod.GET)
  @ResponseBody
  public List<ServiceUsageType> listUsageTypes(
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID) {
    ServiceInstance serviceInstance = serviceInstanceDao.getServiceInstance(serviceInstanceUUID);
    Service service = serviceInstance.getService();
    return service.getServiceUsageTypes();
  }

  @RequestMapping(value = ("/listDiscriminators"), method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> listDiscriminators(
      @RequestParam(value = "serviceUsageTypeId", required = true) final Long usageTypeId,
      @RequestParam(value = "serviceInstanceUUID", required = true) final String serviceInstanceUUID) {

    Map<String, Object> finalMap = privilegeService.runAsPortal(new PrivilegedAction<Map<String, Object>>() {

      @Override
      public Map<String, Object> run() {
        Set<ServiceDiscriminator> serviceDiscriminators = new HashSet<ServiceDiscriminator>();
        ServiceInstance serviceInstance = serviceInstanceDao.getServiceInstance(serviceInstanceUUID);
        for (ServiceUsageType serviceUsageType : serviceInstance.getService().getServiceUsageTypes()) {
          if (serviceUsageType.getId().equals(usageTypeId)) {
            serviceDiscriminators = serviceInstance.getService().getServiceUsageTypeDiscriminator(serviceUsageType);
            break;
          }
        }
        Map<String, Object> finalMap = new HashMap<String, Object>();
        for (ServiceDiscriminator serviceDiscriminator : serviceDiscriminators) {
          String discriminatorName = serviceDiscriminator.getDiscriminatorName();
          Map<String, String> discriminatorValuesMap = new HashMap<String, String>();
          try {
            discriminatorValuesMap = ((CloudConnector) connectorManagementService
                .getServiceInstance(serviceInstanceUUID)).getMetadataRegistry().getDiscriminatorValues(
                discriminatorName);
          } catch (CloudServiceException e) {
            logger.error("Failed to get discriminator value for:" + discriminatorName + " serviceInstance:"
                + serviceInstanceUUID);
            throw e;
          }
          Map<String, Object> discriminatorValMap = new HashMap<String, Object>();
          discriminatorValMap.put("name", discriminatorName);
          discriminatorValMap.put("discriminatorValues", discriminatorValuesMap);
          finalMap.put(serviceDiscriminator.getId().toString(), discriminatorValMap);
        }
        return finalMap;
      }
    });
    return finalMap;
  }

  @RequestMapping(value = ("/createproduct"), method = RequestMethod.POST)
  @ResponseBody
  public Product createProduct(@ModelAttribute("productForm") ProductForm form, BindingResult result, ModelMap map,
      HttpServletResponse response, HttpServletRequest request) throws JSONException {
    logger.debug("### createProduct method starting...(POST)");
    Product product = form.getProduct();
    if (!productService.isCodeUnique(product.getCode())) {
      logger.debug("### product code is NOT unique ");
      response.setStatus(CODE_NOT_UNIQUE_ERROR_CODE);
      return null;
    }
    String serviceInstanceUUID = form.getServiceInstanceUUID();
    String categoryID = form.getCategoryID();
    List<ProductCharge> productCharges = form.getProductCharges();
    boolean ifReplacementProduct = !form.getIsNewProduct();
    JSONArray mediationRules = new JSONArray(form.getProductMediationRules());
    BigDecimal conversionFactor = new BigDecimal(form.getConversionFactor());
    Product createdProduct = productService.createProduct(product, serviceInstanceUUID, categoryID, productCharges,
        ifReplacementProduct, conversionFactor, mediationRules);
    logger.debug("### createProduct method end");
    return createdProduct;
  }

  /**
   * View product
   * 
   * @param id
   * @param map
   * @return
   */
  @RequestMapping(value = ("/viewproduct"), method = RequestMethod.GET)
  public String viewProduct(@RequestParam(value = "Id", required = true) String id,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan, ModelMap map) {
    logger.debug("### viewProduct method starting...(POST)");

    Product product = productService.locateProduct(id, true);
    map.addAttribute("product", product);
    map.addAttribute("whichPlan", whichPlan);
    Date currentDate = new Date();
    map.addAttribute("timestamp", currentDate.getTime());

    logger.debug("### viewProduct method end");
    return "products.view";
  }

  /**
   * Edit product
   * 
   * @param id
   * @param map
   * @return
   */
  @RequestMapping(value = ("/editproduct"), method = RequestMethod.GET)
  public String editProduct(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### editProduct method starting...(GET)");
    Product product = productService.locateProductById(id);
    ProductForm productForm = new ProductForm(product);
    productForm.setCurrencyPresicion(Integer.parseInt(config
        .getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)));
    productForm.setCategoryID(product.getCategory().getId().toString());
    map.addAttribute("productForm", productForm);

    ProductRevision productRevision = productService.getProductRevision(product, channelService.getFutureRevision(null)
        .getStartDate(), null);
    List<MediationRule> mediationRules = productRevision.getMediationRules();
    Map<ServiceUsageType, Set<MediationRuleDiscriminator>> usgaeTypeForWhichDiscValesAreToBeGotAndDiscsMap = new HashMap<ServiceUsageType, Set<MediationRuleDiscriminator>>();
    for (MediationRule mediationRule : mediationRules) {
      if (mediationRule.getMediationRuleDiscriminators().size() > 0) {
        usgaeTypeForWhichDiscValesAreToBeGotAndDiscsMap.put(mediationRule.getServiceUsageType(),
            mediationRule.getMediationRuleDiscriminators());
      }
    }

    Set<String> serviceUsageTypeNames = new HashSet<String>();
    Set<String> discrimintaorNames = new HashSet<String>();
    for (ServiceUsageType serviceUsageType : product.getServiceInstance().getService().getServiceUsageTypes()) {
      serviceUsageTypeNames.add(serviceUsageType.getUsageTypeName());
      for (ServiceDiscriminator serviceDiscriminator : product.getServiceInstance().getService()
          .getServiceUsageTypeDiscriminator(serviceUsageType)) {
        discrimintaorNames.add(serviceDiscriminator.getDiscriminatorName());
      }
    }
    map.addAttribute("serviceName", product.getServiceInstance().getService().getServiceName());
    map.addAttribute("serviceUsageTypeNames", serviceUsageTypeNames);
    map.addAttribute("discrimintaorNames", discrimintaorNames);

    List<Category> categories = productService.getAllCategories();
    map.addAttribute("categories", categories);

    Map<String, Object> mediationRuleMap = new HashMap<String, Object>();
    Map<String, Object> usageTypeDiscMap = new HashMap<String, Object>();
    try {
      for (final MediationRule mediationRule : mediationRules) {
        Map<String, Object> mediationRuleEntitiesMap = new HashMap<String, Object>();
        mediationRuleEntitiesMap.put("usageType", mediationRule.getServiceUsageType().getUsageTypeName());
        mediationRuleEntitiesMap.put("conversionFactor",
            productService.getConversionFactor(mediationRule.getConversionFactor(), mediationRule.isMonthly()));
        mediationRuleEntitiesMap.put("operator", mediationRule.getOperator().toString().toLowerCase());
        mediationRuleEntitiesMap.put("uom", mediationRule.getServiceUsageType().getServiceUsageTypeUom().getName());
        mediationRuleEntitiesMap.put("productUom", product.getUom());
        mediationRuleEntitiesMap.put("usageTypeId", mediationRule.getServiceUsageType().getId());
        mediationRuleEntitiesMap.put("discrete", mediationRule.getServiceUsageType().getDiscrete());
        Map<String, Object> medDiscsMap = new HashMap<String, Object>();
        for (MediationRuleDiscriminator mediationRuleDiscriminator : mediationRule.getMediationRuleDiscriminators()) {
          Map<String, Object> medRuleDisEntitiesMap = new HashMap<String, Object>();
          medRuleDisEntitiesMap.put("discriminatorType", mediationRuleDiscriminator.getServiceDiscriminator()
              .getDiscriminatorName());
          medRuleDisEntitiesMap.put("discrimniatorValue", mediationRuleDiscriminator.getDiscriminatorValue());
          medRuleDisEntitiesMap.put("operator", mediationRuleDiscriminator.getOperator().toString().toLowerCase());
          medRuleDisEntitiesMap
              .put("discriminatorTypeId", mediationRuleDiscriminator.getServiceDiscriminator().getId());

          medDiscsMap.put(mediationRuleDiscriminator.getId().toString(), medRuleDisEntitiesMap);
        }

        mediationRuleEntitiesMap.put("discriminators", medDiscsMap);
        mediationRuleMap.put(mediationRule.getId().toString(), mediationRuleEntitiesMap);

        if (medDiscsMap.size() > 0) {
          final ServiceInstance serviceInstance = product.getServiceInstance();
          final ServiceUsageType serviceUsageType = mediationRule.getServiceUsageType();
          // Loop over usage types to get the discriminator values
          // TODO: Need a call to directly get a Service Usage Type from its id
          // Loop over usage discriminators to get the values
          Map<String, Object> discValueMap = privilegeService.runAsPortal(new PrivilegedAction<Map<String, Object>>() {

            @Override
            public Map<String, Object> run() {
              Map<String, Object> discValueMap = new HashMap<String, Object>();
              Set<ServiceDiscriminator> usageTypeDiscriminators = serviceInstance.getService()
                  .getServiceUsageTypeDiscriminator(serviceUsageType);
              for (ServiceDiscriminator usageTypeDiscriminator : usageTypeDiscriminators) {
                Map<String, String> discriminatorValMap = new HashMap<String, String>();
                discriminatorValMap = ((CloudConnector) connectorManagementService.getServiceInstance(serviceInstance
                    .getUuid())).getMetadataRegistry().getDiscriminatorValues(
                    usageTypeDiscriminator.getDiscriminatorName());
                // Create map of discriminator to its values
                Map<String, Object> discVals = new HashMap<String, Object>();
                discVals.put("name", usageTypeDiscriminator.getDiscriminatorName());
                discVals.put("discriminatorValues", discriminatorValMap);
                discValueMap.put(usageTypeDiscriminator.getId().toString(), discVals);
              }
              return discValueMap;
            }
          });
          // Add entries to the service Usage type map
          Map<String, Object> usageTypeMap = new HashMap<String, Object>();
          usageTypeMap.put("name", serviceUsageType.getUsageTypeName());
          usageTypeMap.put("discriminators", discValueMap);
          usageTypeDiscMap.put(serviceUsageType.getId().toString(), usageTypeMap);
        } else {
          usageTypeDiscMap.put(mediationRule.getServiceUsageType().getId().toString(), new HashMap<String, Object>());
        }
      }
    } catch (CloudServiceException e) {
      logger.error("Error in creating the usage discriminator map...", e);
      throw e;
    } catch (Exception e) {
      logger.error("Error in creating the usage discriminator map...", e);
    }

    String jsonUsageTypeDiscriminatorMap = "";
    try {
      jsonUsageTypeDiscriminatorMap = JSONUtils.toJSONString(usageTypeDiscMap);
    } catch (Exception e) {
      logger.error("Error in creating json string from the usage discriminator map ...", e);
    }

    String jsonMediationRuleMap = "";
    try {
      jsonMediationRuleMap = JSONUtils.toJSONString(mediationRuleMap);
    } catch (Exception e) {
      logger.error("Error in creating json string from the mediation Rule Discriminator Map ...", e);
    }

    map.addAttribute("jsonUsageTypeDiscriminatorMap", jsonUsageTypeDiscriminatorMap);
    map.addAttribute("jsonMediationRuleMap", jsonMediationRuleMap);
    map.addAttribute("serviceName", product.getServiceInstance().getService().getServiceName());
    map.addAttribute("serviceInstanceName", product.getServiceInstance().getName());

    logger.debug("### editProduct method end");
    return "products.edit";
  }

  @RequestMapping(value = ("/editproduct"), method = RequestMethod.POST)
  @ResponseBody
  public Product editProduct(@ModelAttribute("productForm") ProductForm form, BindingResult result, ModelMap map)
      throws JSONException {
    logger.debug("### editProduct method starting...(POST)");
    Product product = form.getProduct();
    String categoryID = form.getCategoryID();
    JSONArray mediationRules = new JSONArray(form.getProductMediationRules());
    productService.editProduct(product, categoryID, mediationRules);
    return product;
  }

  @RequestMapping(value = ("/editlogo"), method = RequestMethod.GET)
  public String editProductLogo(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### edit product logo method starting...(GET)");

    ProductLogoForm productLogoForm = new ProductLogoForm(productService.locateProductById(id));
    map.addAttribute("productLogoForm", productLogoForm);
    setPage(map, Page.PRODUCTS);

    logger.debug("### edit product logo method end...(GET)");
    return "products.editlogo";
  }

  @RequestMapping(value = ("/editlogo"), method = RequestMethod.POST)
  public ResponseEntity<String> editProductLogo(@ModelAttribute("productLogoForm") ProductLogoForm form,
      BindingResult result, HttpServletRequest request, ModelMap map) {
    logger.debug("### edit product logo method starting...(POST)");
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
      return new ResponseEntity<String>(error.toString(), responseHeaders, HttpStatus.OK);

    }

    String rootImageDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    if (rootImageDir != null && !rootImageDir.trim().equals("")) {
      Product product = form.getProduct();
      ProductLogoFormValidator validator = new ProductLogoFormValidator();
      validator.validate(form, result);
      if (result.hasErrors()) {
        setPage(map, Page.PRODUCTS);
        JsonObject error = new JsonObject();
        error.addProperty("errormessage",
            messageSource.getMessage(result.getFieldError("logo").getCode(), null, locale));
        return new ResponseEntity<String>(error.toString(), responseHeaders, HttpStatus.OK);
      } else {
        try {
          product = productService.editProductLogo(product, form.getLogo());
        } catch (FileUploadException e) {
          logger.debug("###IO Exception in writing custom image file", e);
          result.rejectValue("logo", "error.uploading.file");
          JsonObject error = new JsonObject();
          error.addProperty("errormessage",
              messageSource.getMessage(result.getFieldError("logo").getCode(), null, locale));
          return new ResponseEntity<String>(error.toString(), responseHeaders, HttpStatus.OK);
        }
      }
      String response = null;
      try {
        response = JSONUtils.toJSONString(product);
      } catch (JsonGenerationException e) {
        logger.debug("###IO Exception in writing custom image file");
      } catch (JsonMappingException e) {
        logger.debug("###IO Exception in writing custom image file");
      } catch (IOException e) {
        logger.debug("###IO Exception in writing custom image file");
      }

      return new ResponseEntity<String>(response, responseHeaders, HttpStatus.OK);
    } else {
      result.rejectValue("logo", "error.custom.image.upload.dir");
      setPage(map, Page.PRODUCTS);
      JsonObject error = new JsonObject();
      error.addProperty("errormessage", messageSource.getMessage(result.getFieldError("logo").getCode(), null, locale));
      return new ResponseEntity<String>(error.toString(), responseHeaders, HttpStatus.OK);
    }
  }

  @RequestMapping(value = {
    "editproductsorder"
  }, method = RequestMethod.POST)
  @ResponseBody
  public String editproductsorder(@RequestParam(value = "productOrderData", required = true) String productOrderData,
      ModelMap map) {
    logger.debug("### editProductsOrder method starting...(POST)");

    String[] productIds = productOrderData.split(",");
    productService.updateProductsSortOrder(productIds);

    logger.debug("### editProductsOrder method ending...(POST)");
    return "success";
  }

  @RequestMapping(value = {
    "validateCode"
  }, method = RequestMethod.GET)
  @ResponseBody
  public boolean validateProductCode(@RequestParam(value = "product.code", defaultValue = "") final String productCode,
      @RequestParam(value = "catalog.code", defaultValue = "") final String catalogCode,
      @RequestParam(value = "productBundle.code", defaultValue = "") final String productBundleCode,
      @RequestParam(value = "campaignPromotion.code", defaultValue = "") final String campaignPromotionCode,
      @RequestParam(value = "promoCode", defaultValue = "") final String promoCode,
      @RequestParam(value = "channelCode", defaultValue = "") final String channelCode,
      @RequestParam(value = "serviceInstanceCode", defaultValue = "") final String serviceInstanceCode) {

    logger.debug("###validateProductCode method starting.....");

    String code = "";
    if (productCode != null && !"".equalsIgnoreCase(productCode)) {
      code = productCode;
    } else if (catalogCode != null && !"".equalsIgnoreCase(catalogCode)) {
      code = catalogCode;
    } else if (productBundleCode != null && !"".equalsIgnoreCase(productBundleCode)) {
      code = productBundleCode;
    } else if (campaignPromotionCode != null && !"".equalsIgnoreCase(campaignPromotionCode)) {
      code = campaignPromotionCode;
    } else if (promoCode != null && !"".equalsIgnoreCase(promoCode)) {
      code = promoCode;
    } else if (channelCode != null && !"".equalsIgnoreCase(channelCode)) {
      code = channelCode;
    } else if (serviceInstanceCode != null && !"".equalsIgnoreCase(serviceInstanceCode)) {
      code = serviceInstanceCode;
    } else {
      logger.debug("code is null ");
      return false;
    }

    logger.debug("###validateProductCode method ending and code is :" + code);
    return productService.isCodeUnique(code);
  }

  @RequestMapping(value = "/{productCode}/viewproductchannelpricing", method = RequestMethod.GET)
  public String viewProductChannelPricingDetails(@PathVariable String productCode, ModelMap map,
      @RequestParam(value = "currenciesToDisplay", required = false, defaultValue = "") String currenciesToDisplay,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "historyDate", required = false) String historyDate) {
    logger.debug("### viewProductChannelPricingDetails method starting...");

    Product product = productService.locateProductByCode(productCode);
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    Map<Channel, List<ProductCharge>> productChannelChargesMap = new HashMap<Channel, List<ProductCharge>>();

    List<Channel> channelsList = channelService.getChannels(0, 0, "");

    for (Channel channel : channelsList) {
      List<ProductCharge> productCharges = new ArrayList<ProductCharge>();

      if (whichPlan.equals("planned")) {
        Revision channelRevision = channelService.getFutureRevision(channel);
        if (channelRevision != null) {
          productCharges = productService.getProductChannelCharges(product, channel, channelRevision.getStartDate());
          map.addAttribute("date", channelRevision.getStartDate());
        } else {
          map.addAttribute("date", null);
        }
      } else if (whichPlan.equals("current")) {
        Revision channelRevision = channelService.getCurrentRevision(channel);
        if (channelRevision != null) {
          productCharges = productService.getProductChannelCharges(product, channel, channelRevision.getStartDate());
          map.addAttribute("date", channelRevision.getStartDate());
        } else {
          map.addAttribute("date", null);
        }
      } else if (whichPlan.equals("history")) {
        if (historyDate == null || historyDate.trim().equals("")) {
          List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
          historyDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
              DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
        }
        Date date = DateUtils.truncate(DateUtils.getCalendarForDate(historyDate,
            DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss")).getTime());
        productCharges = productService.getProductChannelCharges(product, channel, date);
        map.addAttribute("date", date);
      }

      if (productCharges != null && productCharges.size() > 0) {
        Collections.sort(productCharges, new ProductChargeCurrencyValueSort());
        productChannelChargesMap.put(channel, productCharges);
      }
    }

    map.addAttribute("productChannelChargesMap", productChannelChargesMap);
    map.addAttribute("currencieslist", activeCurrencies);

    if (StringUtils.isEmpty(currenciesToDisplay)) {
      currenciesToDisplay = Integer.toString(activeCurrencies.size());
    }

    map.addAttribute("currenciesToDisplay", currenciesToDisplay);

    logger.debug("### viewProductChannelPricingDetails method ending...");
    return "product.view.channelpricing";
  }

  @RequestMapping(value = "/{productCode}/viewproductcurrentcharges", method = RequestMethod.GET)
  public String viewProductCurrentCharges(@PathVariable String productCode, ModelMap map) {
    logger.debug("### viewCurrentProductCharges method starting...");

    Product product = productService.locateProductByCode(productCode);
    Revision currentRevision = channelService.getCurrentRevision(null);

    List<ProductCharge> productChargesList = productService.getProductCharges(product, currentRevision.getStartDate());
    Collections.sort(productChargesList, new ProductChargeCurrencyValueSort());

    map.addAttribute("productChargesList", productChargesList);

    map.addAttribute("date", currentRevision.getStartDate());

    boolean noChargesSetYet = false;
    if (productChargesList == null || productChargesList.size() == 0) {
      noChargesSetYet = true;
    }
    map.addAttribute("noChargesSetYet", noChargesSetYet);
    map.addAttribute("hasHistoricalRevisions", productService.doesReferencePriceBookHaveHistoricalRevisions());

    return "view.product.current.charges";
  }

  @RequestMapping(value = "/{productCode}/viewmediationrules", method = RequestMethod.GET)
  public String viewMediationRules(@PathVariable String productCode, ModelMap map,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "historyDate", required = false) String historyDate) {
    logger.debug("### viewmediationrules method starting...");

    Product product = productService.locateProductByCode(productCode);
    List<MediationRule> mediationRules = new ArrayList<MediationRule>();
    if (whichPlan.equals("planned")) {
      mediationRules = productService.getProductRevision(product,
          channelService.getFutureRevision(null).getStartDate(), null).getMediationRules();

    } else if (whichPlan.equals("current")) {
      mediationRules = productService.getProductRevision(product,
          channelService.getCurrentRevision(null).getStartDate(), null).getMediationRules();

    } else if (whichPlan.equals("history")) {
      if (historyDate == null || historyDate.trim().equals("")) {
        List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
        historyDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
            DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
      }
      Date date = DateUtils.getCalendarForDate(historyDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"))
          .getTime();
      mediationRules = productService.getProductRevision(product, date, null).getMediationRules();
    }
    map.addAttribute("mediationRules", mediationRules);
    Integer hoursInMonths = configuration.getIntValue(Names.com_citrix_cpbm_portal_billing_hoursIn_month);
    map.addAttribute("hoursInMonths", hoursInMonths);
    map.addAttribute("isProductDiscrete", product.getDiscrete());
    logger.debug("### viewmediationrules method ending...");
    return "view.product.mediation.rules";
  }

  @RequestMapping(value = "/{productCode}/viewproductplannedcharges", method = RequestMethod.GET)
  public String viewProductPlannedCharges(@PathVariable String productCode, ModelMap map) {
    logger.debug("### viewPlannedProductCharges method starting...");

    Product product = productService.locateProductByCode(productCode);
    Revision futureRevision = channelService.getFutureRevision(null);

    List<ProductCharge> plannedCharges = productService.getProductCharges(product, futureRevision.getStartDate());
    Collections.sort(plannedCharges, new ProductChargeCurrencyValueSort());

    map.addAttribute("productChargesList", plannedCharges);

    map.addAttribute("date", futureRevision.getStartDate());

    map.addAttribute("hasHistoricalRevisions", productService.doesReferencePriceBookHaveHistoricalRevisions());

    return "view.product.planned.charges";
  }

  /**
   * View charges for all products.
   * 
   * @param map
   * @return String
   */
  @RequestMapping(value = "/viewplannedcharges", method = RequestMethod.GET)
  public String viewPlannedCharges(ModelMap map) {
    logger.debug("### viewPlannedCharges method starting...");

    Revision futureRevision = channelService.getFutureRevision(null);
    Map<Product, List<ProductCharge>> plannedCharges = new HashMap<Product, List<ProductCharge>>();
    for (ProductRevision productRevision : channelService.getFutureChannelRevision(null, false).getProductRevisions()) {
      // Ignore removed products in the future revision
      if (productRevision.getProduct().getRemoved() == null) {
        plannedCharges.put(productRevision.getProduct(), productRevision.getProductCharges());
      }
    }

    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();

    map.addAttribute("currencieslist", activeCurrencies);
    map.addAttribute("currencieslistsize", activeCurrencies.size());
    map.addAttribute("plannedCharges", plannedCharges);
    map.addAttribute("date", futureRevision.getStartDate());

    logger.debug("### viewPlannedCharges method ending...");
    return "view.planned.charges";
  }

  @RequestMapping(value = "/{productCode}/addproductcurrentcharges", method = RequestMethod.GET)
  public String addProductCurrentCharges(@PathVariable String productCode, ModelMap map) {
    logger.debug("### addProductCurrentCharges method starting...");

    Product product = productService.locateProductByCode(productCode);
    List<CurrencyValue> currencyValueList = currencyValueService.listActiveCurrencies();
    ProductForm productForm = new ProductForm(product);
    productForm.setCurrencyPresicion(Integer.parseInt(config
        .getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)));
    for (CurrencyValue cv : currencyValueList) {
      ProductCharge charge = new ProductCharge();
      // TODO: a configuration for scale (incase we change the scale in db, this wont get changed)
      charge.setPrice(BigDecimal.ZERO.setScale(4, BigDecimal.ROUND_HALF_UP));
      charge.setProduct(product);
      charge.setCurrencyValue(cv);
      productForm.addProductCharges(charge);
    }
    map.addAttribute("productForm", productForm);

    logger.debug("### addProductCurrentCharges method ending...");
    return "add.product.current.charges";
  }

  @RequestMapping(value = "/editplannedcharges", method = RequestMethod.GET)
  public String editPlannedCharges(
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID, ModelMap map) {
    logger.debug("### editPlannedCharges method starting...(GET)");
    ProductForm productForm = new ProductForm();
    productForm.setCurrencyPresicion(Integer.parseInt(config
        .getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)));
    Revision futureRevision = channelService.getFutureRevision(null);
    productForm.setStartDate(futureRevision.getStartDate());

    Map<Product, List<ProductCharge>> plannedCharges = new HashMap<Product, List<ProductCharge>>();
    for (ProductRevision productRevision : channelService.getFutureChannelRevision(null, false).getProductRevisions()) {
      // Ignore removed products in the future revision
      if (productRevision.getProduct().getRemoved() == null
          && (productRevision.getProduct().getServiceInstance() != null && productRevision.getProduct()
              .getServiceInstance().getUuid().equals(serviceInstanceUUID))) {
        plannedCharges.put(productRevision.getProduct(), productRevision.getProductCharges());
      }
    }
    productForm.setCurrentProductChargesMap(plannedCharges);
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    Set<Entry<Product, List<ProductCharge>>> entrySet = plannedCharges.entrySet();
    for (Entry<Product, List<ProductCharge>> entry : entrySet) {
      try {
        // if current price exists then clone otherwise create new product charge object for each currency.
        if (entry.getValue() != null && entry.getValue().size() > 0) {

          productForm.updateProductChargesFormList(entry.getKey(), entry.getValue(), activeCurrencies, futureRevision,
              false);
        } else {
          productForm.updateProductChargesFormList(entry.getKey(), null, activeCurrencies, futureRevision, true);
        }
      } catch (ArithmeticException ae) {
        logger.error("ArithmeticException while editing the product charge, Possible Cause- "
            + "the currency precision level was reduced " + ae);
        throw new CurrencyPrecisionException(ae.getMessage(), ae);
      }

    }
    map.addAttribute("productForm", productForm);
    map.addAttribute("currencieslist", activeCurrencies);
    map.addAttribute("currencieslistsize", activeCurrencies.size());
    logger.debug("### editPlannedCharges method ending...(GET)");
    return "edit.planned.charges";
  }

  @RequestMapping(value = "/editplannedcharges", method = RequestMethod.POST)
  @ResponseBody
  public String editPlannedCharges(@ModelAttribute("productForm") ProductForm form, BindingResult result, ModelMap map) {
    logger.debug("### planProductCharges method starting...(POST)");

    List<ProductChargesForm> productChargesFormList = form.getProductChargesFormList();
    Map<Product, List<ProductCharge>> plannedChargesMap = form.getCurrentProductChargesMap();
    Map<Product, List<ProductCharge>> newProductChargesMap = new HashMap<Product, List<ProductCharge>>();
    for (ProductChargesForm productChargesForm : productChargesFormList) {
      newProductChargesMap.put(productChargesForm.getProduct(), productChargesForm.getCharges());
    }

    productService.editPlannedCharges(plannedChargesMap, newProductChargesMap, channelService.getFutureRevision(null));

    logger.debug("### planProductCharges method ending...(POST)");
    return "success";
  }

  /**
   * List all the products
   * 
   * @return
   */
  @RequestMapping(value = "/sortproducts", method = RequestMethod.GET)
  public String sortProducts(@RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID,
      @RequestParam(value = "whichPlan", required = false, defaultValue = "planned") String whichPlan,
      @RequestParam(value = "historyDate", required = false) String historyDate,
      @RequestParam(value = "filterBy", required = false) String filterBy,
      @RequestParam(value = "category", required = false) String category, ModelMap map) {
    logger.debug("### sortProducts method starting...");

    List<Product> productsList = new ArrayList<Product>();
    List<Product> productListByFilters = new ArrayList<Product>();
    Revision effectiveRevision = null;
    if (!(serviceInstanceUUID == null || serviceInstanceUUID.trim().equals(""))) {
      List<Product> products = new ArrayList<Product>();
      if (whichPlan.equals("planned")) {
        effectiveRevision = channelService.getFutureRevision(null);
        products = productService.listProducts(effectiveRevision);
      } else if (whichPlan.equals("current")) {
        effectiveRevision = channelService.getCurrentRevision(null);
        products = productService.listProducts(effectiveRevision);
      } else if (whichPlan.equals("history")) {
        if (historyDate == null || historyDate.trim().equals("")) {
          List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
          historyDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
              DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
        }
        Date date = DateUtils.getCalendarForDate(historyDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"))
            .getTime();
        effectiveRevision = channelService.getRevisionForTheDateGiven(date, null);
        products = productService.listProducts(effectiveRevision);
      }
      for (Product product : products) {
        // Add only those products which have removedBy null or if it is removed, then in a revision after the
        // revision under consideration
        if (product.getServiceInstance() != null
            && product.getServiceInstance().getUuid().toString().equals(serviceInstanceUUID.trim())) {
          productsList.add(product);
        }
      }
      productListByFilters = getProductListByFilters(productsList, filterBy, category, effectiveRevision);
      Collections.sort(productListByFilters, new ProductSortOrderSort());
    }
    map.addAttribute("productsList", productListByFilters);

    logger.debug("### sortProducts method ending...");
    return "products.sort";
  }

  class DateCompare implements Comparator<Date> {

    @Override
    public int compare(Date one, Date two) {
      return one.compareTo(two);
    }
  }

  @RequestMapping(value = "/{productCode}/viewproductchargeshistory", method = RequestMethod.GET)
  public String viewProductChargesHistory(@PathVariable String productCode,
      @RequestParam(value = "revisionDate", required = false) String revisionDate, ModelMap map) {
    logger.debug("### viewProductChargesHistory method starting...(GET)");

    Product product = productService.locateProductByCode(productCode);

    if (revisionDate == null || revisionDate.equals("")) {
      List<Revision> revisions = productService.getReferencePriceBookHistoryRevisions();
      revisionDate = DateUtils.getStringForCalendar(revisions.get(0).getStartDate(),
          DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"));
    }

    Date historyDate = DateUtils.getCalendarForDate(revisionDate, DateUtils.getDateFormatter("MM/dd/yyyy HH:mm:ss"))
        .getTime();
    List<ProductCharge> productChargesList = productService.getProductCharges(product, historyDate);
    Collections.sort(productChargesList, new ProductChargeCurrencyValueSort());

    map.addAttribute("productChargesList", productChargesList);
    map.addAttribute("historyDate", historyDate);

    logger.debug("### viewProductChargesHistory method ending...(GET)");
    return "product.charges.history";
  }

  @RequestMapping(value = "/setplandate", method = RequestMethod.GET)
  public String setPlanDate(ModelMap map) {
    logger.debug("### setPlanDate method starting...(GET)");

    ProductForm productForm = new ProductForm();
    productForm.setCurrencyPresicion(Integer.parseInt(config
        .getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)));
    Revision currentRevision = channelService.getCurrentRevision(null);
    // Case of 0th revision with null plan date. We need to allow today as a plan date
    if (currentRevision.getStartDate() == null || currentRevision.getStartDate().after(new Date())) {
      if (currentRevision.getStartDate() == null) {
        map.addAttribute("isPlanDateThere", false);
        productForm.setStartDate(DateUtils.truncate(new Date()));
      } else {
        map.addAttribute("isPlanDateThere", true);
        productForm.setStartDate(DateUtils.truncate(currentRevision.getStartDate()));
      }
    } else {
      Revision futureRevision = channelService.getFutureRevision(null);
      if (futureRevision.getStartDate() == null) {
        map.addAttribute("isPlanDateThere", false);
        productForm.setStartDate(DateUtils.truncate(new Date()));
      } else {
        productForm.setStartDate(DateUtils.truncate(futureRevision.getStartDate()));
        map.addAttribute("isPlanDateThere", true);
      }
    }
    // always allow plan date for today
    map.addAttribute("isTodayAllowed", true);
    map.addAttribute("planDateForm", productForm);
    map.addAttribute("date_today", new Date());

    logger.debug("### planChargesDate method ending...(GET)");
    return "plan.global.catalog.chargerevisions.date";
  }

  @RequestMapping(value = "/setplandate", method = RequestMethod.POST)
  @ResponseBody
  public String setPlanDate(@ModelAttribute("planDateForm") ProductForm form, BindingResult result, ModelMap map) {
    logger.debug("### setPlanDate method starting...(POST)");

    Date startDate = form.getStartDate();
    if(StringUtils.isNotBlank(form.getStartDateString())) {
      try {
        startDate = (DateUtils.getDateFormatter(messageSource.getMessage("dateonly.filter.format", null,
            getCurrentUser().getLocale()))).parse(form.getStartDateString());
      } catch (NoSuchMessageException e) {
        throw new IllegalArgumentException(e);
      } catch (ParseException e) {
        throw new IllegalArgumentException(e);
      }
    }
    if (channelService.getCurrentRevision(null).getStartDate() == null
        || channelService.getCurrentRevision(null).getStartDate().after(new Date())) {
      if (startDate.compareTo(DateUtils.truncate(DateUtils.minusOneDay(new Date()))) < 0) {
        logger.debug("### Activation not allowed. Plan date should be greater than or equal to today.");
        return "plan_date_should_be_greater_or_equal_to_today";
      }
    } else {
      if (startDate.compareTo(DateUtils.minusOneDay(new Date())) <= 0) {
        logger.debug("### Activation not allowed. Plan date should be greater than today.");
        return "plan_date_should_be_greater_to_today";
      }
    }
    // If we are activating today and there is no product added yet, don't allow activation
    ChannelRevision channelRevision = channelService.getChannelRevision(null, channelService.getFutureRevision(null)
        .getStartDate(), false);
    if (startDate.compareTo(DateUtils.truncate(new Date())) == 0 && channelRevision.getProductRevisions().size() == 0
        && channelRevision.getProductBundleRevisions().size() == 0) {
      logger.debug("### Activation not allowed. No product/product bundle added.");
      return "no_product_added";
    }
    long todayMinTIme = DateUtils.truncate((new Date())).getTime();
    long todayMaxTIme = DateUtils.setMaxTime((new Date())).getTime();

    if (startDate.getTime() >= todayMinTIme && startDate.getTime() <= todayMaxTIme) {
      // current time stamp
      startDate = new Date();
    }
    productService.setReferencePriceBookFutureRevisionDate(startDate);

    logger.debug("### planChargesDate method starting...(POST)");
    return "success";
  }

  @RequestMapping(value = ("/retireproduct"), method = RequestMethod.GET)
  @ResponseBody
  public String retireProduct(
      @RequestParam(value = "productId", required = true) String productId,
      @RequestParam(value = "checkforentitlements", required = false, defaultValue = "true") String checkforentitlements,
      ModelMap map) {
    logger.debug("### retireProduct method starting...(POST)");

    Product product = productService.locateProductById(productId);
    boolean foundProductAsEntitlement = false;
    boolean isReplaced = false;
    if (checkforentitlements.equals("true")) {
      isReplaced = true;
      ChannelRevision channelrevison = channelService.getFutureChannelRevision(null, false);
      for (ProductBundleRevision productBundleRevision : channelrevison.getProductBundleRevisions()) {
        for (Entitlement entitlement : productBundleRevision.getEntitlements()) {
          if (entitlement.getProduct().equals(product)) {
            foundProductAsEntitlement = true;
            break;
          }
        }
        if (foundProductAsEntitlement) {
          break;
        }
      }
    }

    if (checkforentitlements.equals("true") && foundProductAsEntitlement) {
      logger.debug("### Product is still an entitlement in one of the bundles.");
      return "entitlementscheckfailed";
    }
    try {
      productService.removeProductById(productId, isReplaced);
    } catch (Exception e) {
      logger.debug("### Product remove failed.", e);
      return "failed";
    }

    logger.debug("### retireProduct method end..(GET)");
    return "success";
  }

  @RequestMapping(value = ("/listscales"), method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> listScales(@RequestParam(value = "serviceUuid", required = true) String serviceUuid,
      @RequestParam(value = "UomName", required = true) String uomName, ModelMap map) {
    logger.debug("### listscales method starting...");
    Map<String, Object> returnValues = new HashMap<String, Object>();
    List<ServiceUsageTypeUomScale> scales = connectorConfigurationManager.getServiceUsageTypeUomScales(serviceUuid,
        uomName);
    Map<String, BigDecimal> originalConversionFactors = new HashMap<String, BigDecimal>();
    for (ServiceUsageTypeUomScale currentScale : scales) {
      originalConversionFactors.put(currentScale.getName(), currentScale.getConversionFactor());
      currentScale.setConversionFactor(productService.getConversionFactor(currentScale.getConversionFactor(),
          currentScale.isMonthly()));
    }
    returnValues.put("original", originalConversionFactors);
    returnValues.put("modified", scales);
    return returnValues;
  }

  @RequestMapping(value = {
    "/listProductsForSelectedContext"
  }, method = RequestMethod.GET)
  @ResponseBody
  public List<ProductCharge> listProducts(
      @RequestParam(value = "serviceInstanceUuid", required = true) final String serviceInstanceUuid,
      @RequestParam(value = "resourceType", required = true) final String resourceType,
      @RequestParam(value = "contextString", required = true) final String contextString,
      @RequestParam(value = "viewCatalog", required = false, defaultValue = "false") Boolean viewCatalog,
      @RequestParam(value = "filters", required = false) final String filters,
      @RequestParam(value = "currencyCode", required = false) String currencyCode,
      @RequestParam(value = "listAll", required = false, defaultValue = "false") final boolean listAll,
      @RequestParam(value = "channelParam", required = false) String channelParam,
      @RequestParam(value = "revision", required = false) String timeline,
      @RequestParam(value = "revisionDate", required = false) String revisionDateString,
      @RequestParam(value = "dateFormat", required = false) String dateFormat, HttpServletRequest request)
      throws ConnectorManagementServiceException {

    Tenant tenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    Channel channel = null;
    CurrencyValue currency = null;

    if (StringUtils.isNotBlank(channelParam) && StringUtils.isNotBlank(currencyCode)) {
      channel = channelService.getChannelById(channelParam);
      currency = currencyValueService.locateBYCurrencyCode(currencyCode);
    } else {
      if (tenant == null) {
        channel = channelService.getDefaultServiceProviderChannel();
        currency = currencyValueService.locateBYCurrencyCode(currencyCode);
      } else {
        channel = tenant.getSourceChannel();
        currency = tenant.getCurrency();
      }
    }

    if (channel == null) {
      channel = channelService.getDefaultServiceProviderChannel();
      if (currencyCode != null && !currencyCode.equals("")) {
        currency = currencyValueService.locateBYCurrencyCode(currencyCode);
      }
    }

    Date revisionDate = null;

    if (StringUtils.isNotBlank(dateFormat) && !dateFormat.equals("undefined")) {
      SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
      try {
        revisionDate = sdf.parse(revisionDateString);
      } catch (ParseException e) {
        logger.info("Incorrect date passed in revision date : " + revisionDateString + " for date format " + dateFormat
            + ". Skipping...");
      }
    }

    ServiceResourceType serviceResourceType = null;
    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    List<ProductRevision> productRevisions = new ArrayList<ProductRevision>();
    if (StringUtils.isNotBlank(serviceInstanceUuid) && StringUtils.isNotBlank(resourceType)) {
      ServiceInstance serviceInstance = connectorConfigurationManager.getInstanceByUUID(serviceInstanceUuid);
      com.vmops.model.Service service = serviceInstance.getService();
      List<ServiceResourceType> resourceTypes = service.getServiceResourceTypes();
      for (ServiceResourceType resType : resourceTypes) {
        if (resType.getResourceTypeName().equals(resourceType)) {
          serviceResourceType = resType;
          break;
        }
      }
      if (listAll) {
        if (tenant == null || tenant.getAccountId().equals(TenantService.SYSTEM_TENANT)) {
          Revision revision = channelService.getRevision(channel, timeline, revisionDate);
          ChannelRevision channelRevision = channelService.getChannelRevision(revision, currency, channel);
          productRevisions = channelRevision.getProductRevisions();
        } else {
          productRevisions = channelService.getCurrentChannelRevisionForTenant(tenant).getProductRevisions();
        }

      } else {
        if (!resourceType.equals(ProductBundle.SERVICEBUNDLE)) {
          Map<String, String> discriminators = createStringMap(contextString);
          discriminators.putAll(createStringMap(filters));
          productRevisions = productService.listProductRevisions(serviceInstance, serviceResourceType, discriminators,
              tenant.getOwner());
        }
      }
    }
    for (ProductRevision pr : productRevisions) {
      if (CollectionUtils.isNotEmpty(pr.getProductCharges())) {
        productCharges.add(pr.getProductCharges().get(0));
      }
    }
    return productCharges;
  }
}
