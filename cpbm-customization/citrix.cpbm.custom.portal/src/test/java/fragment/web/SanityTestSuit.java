/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.support.SessionStatus;

import web.WebTestsBaseWithMockConnectors;
import web.support.MockSessionStatus;
import workflow.common.MockCPBMBundleContext;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.core.workflow.event.TriggerTransaction;
import com.citrix.cpbm.core.workflow.model.BusinessTransaction;
import com.citrix.cpbm.core.workflow.model.BusinessTransaction.State;
import com.citrix.cpbm.core.workflow.model.BusinessTransaction.Type;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.bootstrap.service.CustomizationResourceService;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.citrix.cpbm.portal.forms.TenantForm;
import com.citrix.cpbm.portal.fragment.controllers.AbstractConnectorController;
import com.citrix.cpbm.portal.fragment.controllers.ChannelController;
import com.citrix.cpbm.portal.fragment.controllers.ProductBundlesController;
import com.citrix.cpbm.portal.fragment.controllers.ProductsController;
import com.citrix.cpbm.portal.fragment.controllers.TenantsController;
import com.citrix.cpbm.workflow.activity.Activity;
import com.citrix.cpbm.workflow.activity.Activity.Status;
import com.citrix.cpbm.workflow.engine.WorkflowEngine;
import com.citrix.cpbm.workflow.engine.impl.WorkflowEngineImpl;
import com.citrix.cpbm.workflow.model.Workflow;
import com.citrix.cpbm.workflow.model.WorkflowActivity;
import com.citrix.cpbm.workflow.persistence.BusinessTransactionDAO;
import com.citrix.cpbm.workflow.persistence.WorkflowDAO;
import com.citrix.cpbm.workflow.service.WorkflowService;
import com.vmops.admin.event.listeners.EventListenerJmsConsumer;
import com.vmops.admin.jobs.WorkflowJob;
import com.vmops.event.listeners.EventQueue;
import com.vmops.internal.service.CustomizationInitializerService;
import com.vmops.internal.service.SubscriptionService;
import com.vmops.model.AccountType;
import com.vmops.model.Category;
import com.vmops.model.Channel;
import com.vmops.model.Configuration;
import com.vmops.model.Country;
import com.vmops.model.Event;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.ProductCharge;
import com.vmops.model.Profile;
import com.vmops.model.ProfileAuthority;
import com.vmops.model.RateCard;
import com.vmops.model.RateCardCharge;
import com.vmops.model.Service;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceResourceType.ResourceConstraint;
import com.vmops.model.ServiceUsageType;
import com.vmops.model.Subscription;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.persistence.ChargeRecurrenceFrequencyDAO;
import com.vmops.persistence.CountryDAO;
import com.vmops.persistence.EventDAO;
import com.vmops.persistence.RevisionDAO;
import com.vmops.persistence.ServiceDAO;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.persistence.ServiceResourceTypeDAO;
import com.vmops.portal.config.SearchParams;
import com.vmops.portal.config.SearchParams.Order;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.ProductService;
import com.vmops.service.ProfileService;
import com.vmops.web.forms.ProductBundleForm;
import com.vmops.web.forms.ProductForm;

import fragment.web.util.WebTestUtils;

/**
 * Sanity Test Suit contains unit tests for sanity testing activities.
 * 
 * @author vinayv
 */

@ContextConfiguration(inheritLocations = true, locations = {
    "classpath:/applicationContext-testjmsconsumer.xml", "classpath:/applicationContext-workflow-customizations.xml",
    "classpath:/applicationContext-workflow-test.xml"
})
public class SanityTestSuit extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  private WorkflowJob workflowJob;

  @Resource(name = "workflowInitializerService")
  private CustomizationInitializerService customizationInitializerService;

  private BundleContext bundleContext;

  @Autowired
  private AbstractConnectorController connectorController;

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManager;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ServiceInstanceDao serviceInstanceDao;

  @Autowired
  private ServiceDAO servicedao;

  @Autowired
  private ProfileService profileService;

  @Autowired
  private WorkflowEngine workflowEngine;

  @Autowired
  private ProductsController productsController;

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductBundlesController bundleController;

  @Autowired
  private ProductBundleService bundleService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private ChannelController channelController;

  @Autowired
  private RevisionDAO revisionDAO;

  @Autowired
  private WorkflowService workflowService;

  @Autowired
  private EventQueue eventListenerJmsProducer;

  @Autowired
  private ChargeRecurrenceFrequencyDAO chargeRecurrenceFrequencyDAO;

  @Autowired
  private ServiceResourceTypeDAO serviceResourceTypeDAO;

  @Autowired
  private BusinessTransactionDAO businessTransactionDAO;

  @Autowired
  private WorkflowDAO workflowDAO;

  @Autowired
  private EventDAO eventDAO;

  @Autowired
  private CountryDAO countryDAO;

  @Autowired
  private TenantsController tenantsController;

  @Autowired
  private SubscriptionService subscriptionService;

  @Autowired
  private CustomizationResourceService customizationResourceService;

  @Autowired
  private EventListenerJmsConsumer consumer;

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  private SessionStatus status;

  private final com.citrix.cpbm.access.Subscription proxySubscription = (com.citrix.cpbm.access.Subscription) CustomProxy
      .newInstance(new Subscription());

  @Before
  public void init() {
    map = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    status = new MockSessionStatus();
  }

  /**
   * Description: Test to enable a Service
   * 
   * @author vinayv
   */
  @Test
  public void testEnableService() throws Exception {

    Configuration config = configurationService
        .locateConfigurationByName("com.citrix.cpbm.portal.settings.services.datapath");
    config.setValue("src\\test\\resources\\");
    Profile opsProfile = profileService.findProfileByName("Ops Admin");
    List<ProfileAuthority> beforeAuthorityList = opsProfile.getAuthorityList();
    int beforeAuthorityListSize = beforeAuthorityList.size();
    Service service = servicedao.find("6");
    String profileDetails = "[{\"profileid\":\"2\",\"roles\":[]},{\"profileid\":\"3\",\"roles\":[\"ROLE_CLOUD_MANAGEMENT\"]},{\"profileid\":\"4\",\"roles\":[]},{\"profileid\":\"5\",\"roles\":[]},{\"profileid\":\"6\",\"roles\":[]},{\"profileid\":\"7\",\"roles\":[]},{\"profileid\":\"8\",\"roles\":[]},{\"profileid\":\"9\",\"roles\":[]},{\"profileid\":\"10\",\"roles\":[\"ROLE_USER_CLOUD_MANAGEMENT\"]},{\"profileid\":\"11\",\"roles\":[\"ROLE_ACCOUNT_CLOUD_MANAGEMENT\",\"ROLE_USER_CLOUD_MANAGEMENT\"]}]";
    String result = connectorController.enableService(service.getUuid(), profileDetails, map);
    Assert.assertNotNull(result);
    Assert.assertEquals("success", result);
    opsProfile = profileService.findProfileByName("Ops Admin");
    List<ProfileAuthority> afterAuthorityList = opsProfile.getAuthorityList();
    int afterAuthorityListSize = afterAuthorityList.size();
    Assert.assertEquals(beforeAuthorityListSize + 1, afterAuthorityListSize);
  }

  /**
   * Description: Test to add new Service Instance
   * 
   * @author vinayv
   */
  @Test
  public void testSaveInstanceWithProducts() {

    request.setParameter("lang", Locale.getDefault().toString());
    Service service = servicedao.find("6");
    int serviceInstancesCount = serviceInstanceDao.count();
    int productListCount = productDAO.count();
    String configProperties = "[{\"name\":\"instancename\",\"value\":\"anusha-CS\"},{\"name\":\"instancecode\",\"value\":\"anusha-CS\"},{\"name\":\"publicProtocol\",\"value\":\"http\"},{\"name\":\"publicHost\",\"value\":\"10.102.153.119\"},{\"name\":\"publicPort\",\"value\":\"8080\"},{\"name\":\"ssoKey\",\"value\":\"4bUudGCm3lAFf54EbgMRAE7b_LAdhs4MO4M8v-uvA1uEo9D1zD6eFauAtBJRrabCcLCg_uqXE-OjTMc1EeNcEA\"},{\"name\":\"apiKey\",\"value\":\"pmHmI9h5rEQdcl34Tgi7crx5DjTQs-5vR6vvwdO4F_Jsw0tKgMu2X0bALYKZYMjh9qoXG4Q0UAacNJR7vLvDcw\"},{\"name\":\"secretKey\",\"value\":\"0eAYlinSBnmBnM7RED1MRzfsC5Wnoa3199WVaF-3nVh9vFioHLXvwyDoGm3SLaVdRbOopM4CKxKBbFat44c9QA\"},{\"name\":\"parentDomainId\",\"value\":\"1\"},{\"name\":\"apiProxySuffix\",\"value\":\"ccpapi\"},{\"name\":\"cloud.jdbc.host\",\"value\":\"10.102.153.119\"},{\"name\":\"cloud.jdbc.username\",\"value\":\"cloud\"},{\"name\":\"cloud.jdbc.password\",\"value\":\"cloud\"},{\"name\":\"cloud.jdbc.database.schemaname\",\"value\":\"cloud\"},{\"name\":\"cloud.usage.jdbc.host\",\"value\":\"10.102.153.119\"},{\"name\":\"cloud.usage.jdbc.username\",\"value\":\"cloud\"},{\"name\":\"cloud.usage.jdbc.password\",\"value\":\"cloud_usage\"},{\"name\":\"cloud.usage.jdbc.database.schemaname\",\"value\":\"cloud_usage\"},{\"name\":\"adminServerList\",\"value\":\"10.102.153.119:8096\"},{\"name\":\"nonAdminServerList\",\"value\":\"10.102.153.119:8080\"},{\"name\":\"apiWhitelist\",\"value\":\"\"},{\"name\":\"apiBlacklist\",\"value\":\"\"},{\"name\":\"default.vm.locale\",\"value\":\"us\"},{\"name\":\"max.custom.disk.offering.size\",\"value\":\"1024\"},{\"name\":\"instancedescription\",\"value\":\"Instance Desc\"}]";
    String quickProducts = "[{\"name\":\"RUNNING_VM\",\"code\":\"anusha-CS.RUNNING_VM\",\"scale\":\"1.0000000000\",\"uom\":\"Compute-Hours\",\"category\":\"1\",\"usageTypeId\":\"31\",\"createdBy\":\"1\",\"price\":[{\"currencyCode\":\"USD\",\"currencyVal\":\"12\"}]},{\"name\":\"ALLOCATED_VM\",\"code\":\"anusha-CS.ALLOCATED_VM\",\"scale\":\"1.0000000000\",\"uom\":\"Compute-Hours\",\"category\":\"1\",\"usageTypeId\":\"32\",\"createdBy\":\"1\",\"price\":[{\"currencyCode\":\"USD\",\"currencyVal\":\"15\"}]}]";

    connectorController.saveInstance(service.getUuid(), "save", configProperties, quickProducts, map, request);
    Assert.assertEquals(serviceInstancesCount + 1, serviceInstanceDao.count());
    Assert.assertEquals(productListCount + 2, productDAO.count());
    Assert.assertEquals(CssdkConstants.SUCCESS, map.get("validationResult"));
    String instanceMessage = (String) map.get("message");
    Assert.assertEquals("Instance Added Successfully.", instanceMessage);
  }

  /**
   * Description: Test to add and enable payment gateway Instance
   * 
   * @author vinayv
   */
  @Test
  public void testAddAndEnablePaymentGatewayInstance() {

    Service service = servicedao.find("7");
    int serviceInstancesCount = serviceInstanceDao.count();
    String configProperties = "[{\"name\":\"serverurl\",\"value\":\"https://ics2wstest.ic3.com/commerce/1.x/transactionProcessor\"},{\"name\":\"merchantName\",\"value\":\"citrix_cpbm\"},{\"name\":\"transactionKey\",\"value\":\"h03g5h+quEOQUE8YPDrAugUAwaZZeo5qm6BCAOhuzBTPxKYD4DHHJq5rFPSIgLodymo5uJyM02bP+NjJKO0ua+RHPkJPyo96RuzWN05qWd17bGNVNIZWiVayp5LvdZgag8SbqZl2UUme4mcauNOBCUGlWxiMNRTnRquu+XSGc7CONzEz7PqrZy5qTxg8OsC6BQDBpll6jmqboEIA6G7MFM/EpgPgMccmrmsU9IiAuh3Kajm4nIzTZs/42Mko7S5r5Ec+Qk/Kj3pG7NY3TmpZ3XtsY1U0hlaJVrKnku91mBqDxJupmXZRSZ7iZxq404EJQaVbGIw1FOdGq675dIZzsA==\"}]";
    String quickProducts = null;

    connectorController.saveInstance(service.getUuid(), "save", configProperties, quickProducts, map, request);
    Assert.assertEquals(serviceInstancesCount + 1, serviceInstanceDao.count());
    Assert.assertEquals(CssdkConstants.SUCCESS, map.get("validationResult"));
    String instanceMessage = (String) map.get("message");
    Assert.assertEquals("Instance Added Successfully.", instanceMessage);

    // Enabling Payment Gateway
    Assert.assertEquals(Boolean.FALSE, service.getEnabled());
    map = new ModelMap();
    connectorController.enable(service.getUuid(), true, map, request);
    Assert.assertEquals(Boolean.TRUE, service.getEnabled());
    String message = (String) map.get("message");
    Assert.assertEquals("Service enabled successfully", message);

  }

  /**
   * Description: Test to create a product with Multiple Usage Types
   * 
   * @author vinayv
   */
  @SuppressWarnings("deprecation")
  @Test
  public void testCreateProductWithMultipleUsageTypes() throws Exception {

    String[] usageTypes = {
        "ALLOCATED_VM", "RUNNING_VM"
    };
    ServiceInstance serviceInstance = serviceInstanceDao.find("7");
    Service service = serviceInstance.getService();
    ServiceUsageType serviceUsageType = null;
    int beforeProductsCount = productService.getProductsCount();
    Product product = new Product("New", "New_Prod", "New_Prod", "", getRootUser());
    product.setCode("New_Prod_Code");
    product.setServiceInstance(serviceInstanceDao.find(serviceInstance.getId()));
    product.setUom("Hours");

    Category category = productService.getCategory(1L);
    product.setCategory(category);

    ProductForm form = new ProductForm(product);
    form.setStartDate(new Date());
    form.setServiceUUID(service.getUuid());
    form.setServiceInstanceUUID(serviceInstance.getUuid());
    form.setCategoryID(category.getId().toString());
    form.setConversionFactor("1");

    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    ProductCharge productCharge = new ProductCharge();
    productCharge.setRevision(revisionDAO.getCurrentRevision(null));
    productCharge.setCatalog(null);
    productCharge.setCurrencyValue(currencyValueDAO.find(1L));
    productCharge.setPrice(BigDecimal.valueOf(100));
    productCharge.setCreatedAt(new Date());
    productCharge.setCreatedBy(getRootUser());
    productCharge.setUpdatedBy(getRootUser());
    productService.saveProductCharge(productCharge);
    productCharges.add(productCharge);

    form.setProductCharges(productCharges);
    form.setIsNewProduct(true);

    String jsonString = "";
    String operator = "COMBINE";
    for (int i = 0; i < usageTypes.length; i++) {

      List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
      for (ServiceUsageType sut : sutlist) {
        if (sut.getUsageTypeName().equalsIgnoreCase(usageTypes[i])) {
          serviceUsageType = sut;
          break;
        }
      }
      if (i == 2) {
        operator = "EXCLUDE";
      } else {
        operator = "COMBINE";
      }
      jsonString = jsonString + "{conversionFactor: 1.00, operator: " + operator + ", usageTypeId: "
          + serviceUsageType.getId() + "}";
      jsonString = jsonString + ",";
    }
    jsonString = jsonString.substring(0, jsonString.length() - 1);
    String productMediationRules = "[" + jsonString + "]";
    form.setProductMediationRules(productMediationRules);
    BindingResult result = validate(form);
    Product obtainedProduct = productsController.createProduct(form, result, map, response, request);
    Assert.assertNotNull(obtainedProduct);
    Assert.assertEquals(obtainedProduct.getName(), product.getName());
    int afterProductCount = productService.getProductsCount();
    Assert.assertEquals(beforeProductsCount + 1, afterProductCount);
    List<ProductCharge> obtainedProductCharges = productService.getProductCharges(obtainedProduct,
        obtainedProduct.getCreatedAt());
    for (ProductCharge pc : obtainedProductCharges) {
      Assert.assertEquals(BigDecimal.valueOf(100), pc.getPrice());
      Assert.assertEquals(currencyValueDAO.find(1L), pc.getCurrencyValue());
    }
    Map<ServiceUsageType, List<Product>> productUsageMap = productService.getProductsByUsageType(revisionDAO
        .getCurrentRevision(null));
    boolean cond = false;
    for (int i = 0; i < usageTypes.length; i++) {
      List<ServiceUsageType> sutlist = serviceInstance.getService().getServiceUsageTypes();
      for (ServiceUsageType sut : sutlist) {
        if (sut.getUsageTypeName().equalsIgnoreCase(usageTypes[i])) {
          serviceUsageType = sut;
          break;
        }
      }
      List<Product> prodlist = productUsageMap.get(serviceUsageType);
      if (prodlist != null && !prodlist.isEmpty() && prodlist.contains(obtainedProduct)) {
        cond = true;
      }
    }
    Assert.assertTrue(cond);
  }

  /**
   * Description: Test to create Compute Bundles with None, Monthly, Quarterly and Annual charge frequency
   * 
   * @author vinayv
   */
  @Test
  public void testCreateBundleForDifferentChargeFrequency() throws Exception {

    ServiceResourceType resourceType = serviceResourceTypeDAO.find("1");
    String[] chargeFrequency = {
        "NONE", "MONTHLY", "QUARTERLY", "ANNUAL"
    };
    for (int i = 0; i < chargeFrequency.length; i++) {

      String compAssociationJson = "[{association: INCLUDES, compName: ISO, compValues:[ {compName: DOS-ISO, compValueName: 10 } ] }]";
      int noOfdays = 3;
      Calendar createdAt = Calendar.getInstance();
      createdAt.add(Calendar.DATE, 0 - noOfdays);
      int beforeBundleCount = bundleService.getBundlesCount();
      boolean trialEligible = false;
      if (i == 1) {
        trialEligible = true;
      }
      ProductBundle obtainedBundle = testCreateProductBundle("1", resourceType.getId().toString(), chargeFrequency[i],
          chargeFrequency[i] + "Compute", "USD", BigDecimal.valueOf(100), createdAt.getTime(), compAssociationJson,
          ResourceConstraint.PER_USER, trialEligible);
      Assert.assertNotNull(obtainedBundle);
      Assert.assertEquals(chargeFrequency[i] + "Compute", obtainedBundle.getName());
      Assert.assertEquals(resourceType, obtainedBundle.getResourceType());
      Assert.assertEquals(ResourceConstraint.PER_USER, obtainedBundle.getBusinessConstraint());
      int afterBundleCount = bundleService.getBundlesCount();
      Assert.assertEquals(beforeBundleCount + 1, afterBundleCount);
    }
  }

  /**
   * Description: Test to create a channel and add bundles to the newly created channel
   * 
   * @author vinayv
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testChannelCreationAndAddingBundles() throws JSONException {

    String[] currencyValueList = {
        "USD", "EUR", "GBP"
    };
    int beforeChannelCount = channelDAO.count();
    Channel obtainedChannel = channelController.createChannel(WebTestUtils.createChannelForm("SanityChannel",
        "SanityChannel", "SanityChannel", null, "en_US", "Asia/Kolkata", currencyValueList, map, response), null, map,
        response);
    Assert.assertNotNull(obtainedChannel);
    int afterChannelCount = channelDAO.count();
    Assert.assertEquals(beforeChannelCount + 1, afterChannelCount);
    ModelMap map1 = new ModelMap();
    String selectedProductBundles = "[2]";
    String result = channelController.attachProductBundles(obtainedChannel.getId().toString(), selectedProductBundles,
        map1);
    Assert.assertNotNull(result);
    Assert.assertEquals("success", result);
    String listBundlesResult = channelController.listbundles(obtainedChannel.getId().toString(), map1);
    Assert.assertNotNull(listBundlesResult);
    Assert.assertEquals("productbundle.add", listBundlesResult);
    List<ProductBundleRevision> productbundles = (List<ProductBundleRevision>) map1.get("productBundles");
    for (ProductBundleRevision productbundleRevision : productbundles) {
      ProductBundle productBundle = productbundleRevision.getProductBundle();
      Assert.assertTrue(productBundle.getId() != 2L);
    }
  }

  /**
   * Description: Test to create a Retail account with Workflow (Completing the activities in the workflow for the
   * tenant to become Active)
   * 
   * @author vinayv
   */
  @Test
  public void testCreateAccountWithWorkflow() throws Exception {
    setupWorkflow();
    int beforeBusinessCount = businessTransactionDAO.count();
    int beforeTenantCount = tenantDAO.count();
    int beforeUserCount = userDAO.count();
    int beforeWrkflwCount = workflowDAO.count();
    AccountType accountType = accountTypeDAO.find(3L);
    Tenant tenant = testTenantCreation(accountType.getId());
    long tenantId = tenant.getId();
    Assert.assertEquals(State.NEW.toString(), tenant.getState().toString());
    List<User> userList = userDAO.findAll(null);
    Long userId = 0L;
    for (int i = 0; i < userList.size(); i++) {
      if (userList.get(i).equals(tenant.getOwner())) {
        userId = userList.get(i).getId();
      }
    }
    int afterBusinessCount = businessTransactionDAO.count();
    int afterTenantCount = tenantDAO.count();
    int afterUserCount = userDAO.count();
    Assert.assertEquals(beforeBusinessCount + 1, afterBusinessCount);
    Assert.assertEquals(beforeTenantCount + 1, afterTenantCount);
    Assert.assertEquals(beforeUserCount + 1, afterUserCount);
    BusinessTransaction bt = null;
    List<BusinessTransaction> btlist = businessTransactionDAO.findAll(null);
    for (int i = 0; i < btlist.size(); i++) {
      if (btlist.get(i).getTenant().equals(tenant)) {
        bt = btlist.get(i);
      }
    }
    Assert.assertEquals(State.NEW, bt.getState());
    Assert.assertEquals(Type.tenantStateChange, bt.getType());
    consumer.receive(new TriggerTransaction(bt.getId()));
    btlist = businessTransactionDAO.findAll(null);
    for (int i = 0; i < btlist.size(); i++) {
      if (btlist.get(i).getTenant().equals(tenant)) {
        bt = btlist.get(i);
      }
    }
    int afterWrkflwCount = workflowDAO.count();
    Assert.assertEquals(beforeWrkflwCount + 1, afterWrkflwCount);
    String workflowParam = bt.getWorkflowId();
    Workflow workflow = workflowDAO.findByParam(workflowParam);
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    for (int i = 0; i < activityList.size(); i++) {
      Assert.assertEquals(Status.NEW, activityList.get(i).getStatus());
    }
    User user = userDAO.find(userId);
    user.setEmailVerified(true);
    user.setEnabled(true);
    userDAO.save(user);
    runWorkflow(workflow);
    workflow = workflowDAO.findByParam(workflowParam);
    activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.WAITING, activityList.get(1).getStatus());
    activityList.get(1).getActivityRecord().getTask().setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(workflow);
    workflow = workflowDAO.findByParam(workflowParam);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    activityList = workflow.getWorkflowActivities();
    for (int i = 0; i < activityList.size(); i++) {
      Assert.assertEquals(Status.SUCCESS, activityList.get(i).getStatus());
    }
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenantId).getState());
  }

  /**
   * Description: Test to create a Subscription
   * 
   * @author vinayv
   */
  @Test
  public void createSubscription() {

    SearchParams params = new SearchParams();
    params.setStart(0);
    params.setMaxResults(1000);
    params.setOrderBy("generatedAt", Order.DESC);
    Map<String, Object> conditions = new HashMap<String, Object>();

    ServiceResourceType srt = serviceResourceTypeDAO.find(1L);
    srt.setConstraint(ResourceConstraint.NONE);
    serviceResourceTypeDAO.save(srt);

    Tenant tenant = tenantService.get("dfc84388-d44d-4d8e-9d6a-a62c1c16b7e4");
    User user = tenant.getOwner();
    int subscriptionCount = subscriptionService.getCountByTenant(tenant);
    ProductBundle productBundle = bundleService.locateProductBundleById("2");
    productBundle.setNotificationEnabled(true);
    productBundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundleService.updateProductBundle(productBundle, false);
    productBundle = bundleService.locateProductBundleById("2");

    List<Event> eventList = eventDAO.findByCriteria(params, conditions);
    int eventSize = eventList.size();
    Subscription createdSubscription = subscriptionService.createSubscription(user, productBundle, productBundle
        .getServiceInstanceId().getUuid(), "VirtualMachine", false, false, proxySubscription,
        new HashMap<String, String>());
    Assert.assertNotNull(createdSubscription);
    Assert.assertEquals(tenant, createdSubscription.getTenant());
    subscriptionService.activateSubscription(createdSubscription);
    Assert.assertEquals(subscriptionCount + 1, subscriptionService.getCountByTenant(tenant));
    eventList = eventDAO.findByCriteria(params, conditions);
    Assert.assertEquals(eventSize + 1, eventList.size());
  }

  /**
   * Description: Private Test to create Bundles based on the parameters
   * 
   * @author vinayv
   */
  private ProductBundle testCreateProductBundle(String serviceInstanceID, String resourceTypeID, String chargeType,
      String bundleName, String currencyCode, BigDecimal currencyValue, Date startDate, String jsonString,
      ResourceConstraint businessConstraint, boolean trialEligible) throws Exception {

    ServiceInstance serviceInstance = serviceInstanceDao.find(serviceInstanceID);
    ServiceResourceType resourceType = null;
    if (!resourceTypeID.equalsIgnoreCase("ServiceBundle")) {
      resourceType = serviceResourceTypeDAO.find(resourceTypeID);
    }
    List<RateCardCharge> rateCardChargeList = new ArrayList<RateCardCharge>();
    RateCardCharge rcc = new RateCardCharge(currencyValueService.locateBYCurrencyCode(currencyCode),
        catalogDAO.find(1L), currencyValue, "RateCharge", getRootUser(), getRootUser(),
        channelService.getCurrentRevision(null));
    rateCardChargeList.add(rcc);
    String chargeTypeName = chargeType;
    RateCard rateCard = new RateCard("Rate", chargeRecurrenceFrequencyDAO.findByName(chargeTypeName), new Date(),
        getRootUser(), getRootUser());
    String compAssociationJson = jsonString;

    ProductBundle bundle = new ProductBundle(bundleName, bundleName, "", startDate, startDate, getRootUser());
    bundle.setBusinessConstraint(businessConstraint);
    bundle.setCode(bundleName);
    bundle.setPublish(true);
    bundle.setResourceType(resourceType);
    bundle.setServiceInstanceId(serviceInstance);
    bundle.setTrialEligibility(trialEligible);
    bundle.setRateCard(rateCard);

    ProductBundleForm form = new ProductBundleForm(bundle);
    form.setChargeType(chargeType);
    form.setResourceType(resourceType.getId().toString());
    form.setServiceInstanceUUID(serviceInstance.getUuid());
    form.setBundleOneTimeCharges(rateCardChargeList);
    if (!chargeType.equalsIgnoreCase("NONE")) {
      form.setBundleRecurringCharges(rateCardChargeList);
    }
    form.setCompAssociationJson(compAssociationJson);

    BindingResult result = validate(form);
    ProductBundle obtainedBundle = bundleController.createProductBundle(form, result, map, response);
    Assert.assertNotNull(obtainedBundle);
    Assert.assertEquals(bundle.getName(), obtainedBundle.getName());

    return obtainedBundle;
  }

  /**
   * Description: Private Test to run Workflow using Workflow engine
   * 
   * @author vinayv
   */
  private void runWorkflow(Workflow workflow) throws SecurityException, NoSuchMethodException,
      IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    Method runWorkflowMethod = WorkflowEngineImpl.class.getDeclaredMethod("runWorkflow", Workflow.class);
    runWorkflowMethod.setAccessible(true);
    runWorkflowMethod.invoke(workflowEngine, workflow);
  }

  /**
   * Description: Private Test to create a Tenant
   * 
   * @author vinayv
   */
  private Tenant testTenantCreation(Long accountType) throws Exception {
    int prevTList = tenantDAO.count();
    int prevUList = userDAO.count();
    int prevBTList = businessTransactionDAO.count();
    AccountType type = accountTypeDAO.find(accountType);
    Tenant tenant = new Tenant("Acme Corp " + random.nextInt(), type, getRootUser(), randomAddress(), true,
        currencyValueService.locateBYCurrencyCode("USD"), getPortalUser());
    List<Country> countryList = countryDAO.findAll(null);
    Profile profile = profileDAO.find(8L);
    User user = new User("firstName", "lastName", "nageswarareddy.poli@citrix.com", "username14", "Portal123#",
        "91-9885098850", "GMT", null, profile, getRootUser());
    user.setAddress(randomAddress());
    tenant.setOwner(user);
    com.citrix.cpbm.access.User newUser = (com.citrix.cpbm.access.User) CustomProxy.newInstance(user);
    com.citrix.cpbm.access.Tenant newTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
    TenantForm form = new TenantForm();
    form.setAccountTypeId(accountType.toString());
    form.setUser(newUser);
    form.setTenant(newTenant);
    form.setCountryList(countryList);

    BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
    String tenantCreation = tenantsController.create(form, result, map, status, request);
    logger.debug("RESULT------------------------" + tenantCreation);
    Assert.assertTrue("verifying the form has zero error", tenantCreation.contains("0 errors"));
    Long newTenantId = (Long) map.get("tenantId");
    Tenant obtainedTenant = tenantDAO.find(newTenantId);
    int afterTList = tenantDAO.count();
    int afterUList = userDAO.count();
    int afterBTList = businessTransactionDAO.count();

    Assert.assertEquals("", prevTList + 1, afterTList);
    Assert.assertEquals("", prevUList + 1, afterUList);
    Assert.assertEquals("", prevBTList + 1, afterBTList);

    logger.debug("Tenants before  ::" + prevTList + ",  Tenants After :: " + afterTList);
    logger.debug("Users before  ::" + prevUList + ",  Users After :: " + afterUList);
    logger.debug("businessTransactions before  ::" + prevBTList + ",  businessTransactions After :: " + afterBTList);

    return obtainedTenant;
  }

  private void setupWorkflow() {
    workflowJob = new WorkflowJob();
    workflowJob.setWorkflowEngine(workflowEngine);
    workflowJob.setQueue(eventListenerJmsProducer);
    bundleContext = new MockCPBMBundleContext();
    ((BundleContextAware) workflowService).setBundleContext(bundleContext);

    Map<String, Activity> beanMap = applicationContext.getBeansOfType(Activity.class);

    for (Entry<String, Activity> entry : beanMap.entrySet()) {
      Dictionary<String, String> props = new Hashtable<String, String>();
      props.put("beanName", entry.getKey());
      bundleContext.registerService(Activity.class.getName(), entry.getValue(), props);
    }

    customizationResourceService
        .setWorkflows(this.getClass().getClassLoader().getResourceAsStream("workflowsTest.xml"));
    customizationResourceService.setTransactionWorkflowMap(this.getClass().getClassLoader()
        .getResourceAsStream("transactionWorkflowMapTest.xml"));
    customizationInitializerService.initialize();
    workflowJob.run(null);

    setupMail();
  }

}
