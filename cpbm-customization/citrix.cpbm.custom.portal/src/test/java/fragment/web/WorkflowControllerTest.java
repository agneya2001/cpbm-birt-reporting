/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.support.SessionStatus;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;
import web.support.MockSessionStatus;
import workflow.common.MockCPBMBundleContext;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.core.workflow.event.TriggerTransaction;
import com.citrix.cpbm.core.workflow.model.BusinessTransaction;
import com.citrix.cpbm.core.workflow.model.BusinessTransaction.State;
import com.citrix.cpbm.core.workflow.model.BusinessTransaction.Type;
import com.citrix.cpbm.core.workflow.service.BusinessTransactionService;
import com.citrix.cpbm.platform.bootstrap.service.CustomizationResourceService;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.SubscriptionLifecycleHandler;
import com.citrix.cpbm.portal.forms.TenantForm;
import com.citrix.cpbm.portal.fragment.controllers.BillingController;
import com.citrix.cpbm.portal.fragment.controllers.TenantsController;
import com.citrix.cpbm.portal.fragment.controllers.WorkflowController;
import com.citrix.cpbm.workflow.activity.Activity;
import com.citrix.cpbm.workflow.activity.Activity.Bucket;
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
import com.vmops.event.PortalEvent;
import com.vmops.event.TenantActivation;
import com.vmops.event.listeners.EventQueue;
import com.vmops.internal.service.CustomizationInitializerService;
import com.vmops.internal.service.SubscriptionService;
import com.vmops.model.AccountType;
import com.vmops.model.BillingInfo;
import com.vmops.model.CampaignPromotion;
import com.vmops.model.Country;
import com.vmops.model.CreditCard;
import com.vmops.model.PaymentMode;
import com.vmops.model.ProductBundle;
import com.vmops.model.Profile;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceResourceType.ResourceConstraint;
import com.vmops.model.Subscription;
import com.vmops.model.Tenant;
import com.vmops.model.TrialAccount;
import com.vmops.model.User;
import com.vmops.persistence.CampaignPromotionDAO;
import com.vmops.persistence.CountryDAO;
import com.vmops.persistence.ServiceResourceTypeDAO;
import com.vmops.persistence.TrialAccountDAO;
import com.vmops.service.AccountTypeService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.exceptions.InvalidPaymentModeException;
import common.MockCloudInstance;

/**
 * @author damodar
 */
@ContextConfiguration(inheritLocations = true, locations = {
    "classpath:/applicationContext-testjmsconsumer.xml", "classpath:/applicationContext-workflow-customizations.xml",
    "classpath:/applicationContext-workflow-test.xml"
})
public class WorkflowControllerTest extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  @Autowired
  private WorkflowController controller;

  @Autowired
  private WorkflowDAO workflowDAO;

  @Autowired
  private BusinessTransactionService businessTransactionService;

  @Autowired
  private BusinessTransactionDAO businessTransactionDAO;

  @Autowired
  private EventListenerJmsConsumer consumer;

  @Resource(name = "workflowInitializerService")
  private CustomizationInitializerService customizationInitializerService;

  private BundleContext bundleContext;

  @Autowired
  private WorkflowEngine workflowEngine;

  @Autowired
  private WorkflowService workflowService;

  @Autowired
  private EventQueue eventListenerJmsProducer;

  @Autowired
  private CustomizationResourceService customizationResourceService;

  private WorkflowJob workflowJob;

  @Autowired
  private CountryDAO countryDAO;

  @Autowired
  private TenantsController tenantsController;

  @Autowired
  private AccountTypeService accountTypeService;

  @Autowired
  private CampaignPromotionDAO campaignPromotionDAO;

  @Autowired
  private TrialAccountDAO trialAccountDAO;

  @Autowired
  private SubscriptionService subscriptionService;

  @Autowired
  private ServiceResourceTypeDAO serviceResourceTypeDAO;

  @Autowired
  private ProductBundleService bundleService;

  @Autowired
  private BillingController billingController;

  private MockHttpServletRequest request;

  private SessionStatus status;

  private com.citrix.cpbm.access.Subscription proxySubscription = (com.citrix.cpbm.access.Subscription) CustomProxy
      .newInstance(new Subscription());

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    request = new MockHttpServletRequest();
    status = new MockSessionStatus();
  }

  @Override
  public void prepareMock() {
    MockCloudInstance mock = getMockCloudInstance();
    CloudConnector connector = mock.getCloudConnector();
    EasyMock.expect(connector.getServiceInstanceUUID()).andReturn("003fa8ee-fba3-467f-a517-ed806dae8a80").anyTimes();
    EasyMock.replay(connector);
    SubscriptionLifecycleHandler subscriptionLifecycleHandler = connector.getSubscriptionLifecycleHandler();
    subscriptionLifecycleHandler.destroy(EasyMock.anyObject(Subscription.class));
    EasyMock.expectLastCall().anyTimes();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testRouting() throws Exception {

    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class<WorkflowController> controllerClass = (Class<WorkflowController>) controller.getClass();

    Method expected = locateMethod(controllerClass, "show", new Class[] {
        String.class, ModelMap.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/workflow/abcuuid"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "reset", new Class[] {
      String.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/workflow/abcuuid/reset"));
    Assert.assertEquals(expected, handler);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testShow() {
    String tilesDef = controller.show("4CD810BC-8167-4676-8A4C-FD60D84E1736", map);
    Assert.assertNotNull(tilesDef);
    Assert.assertEquals("workflow.details.popup", tilesDef);
    Assert.assertNotNull(map.get("workflow"));
    Map<Bucket, List<WorkflowActivity>> workflowActivites = (Map<Bucket, List<WorkflowActivity>>) map.get("bucketMap");
    Assert.assertNotNull(workflowActivites);
    Assert.assertEquals(1, workflowActivites.size());
    Bucket bucket = new Bucket();
    bucket.setOrder(1);
    bucket.setName("bucket1");
    Assert.assertNotNull(workflowActivites.get(bucket));
    Assert.assertEquals(1, workflowActivites.get(bucket).size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testShowUnOrderList() {
    String tilesDef = controller.show("4CD810BC-8167-4676-8A4F-FD60D84E1736", map);
    Assert.assertNotNull(tilesDef);
    Assert.assertEquals("workflow.details.popup", tilesDef);
    Assert.assertNotNull(map.get("workflow"));
    Assert.assertNotNull(map.get("bucketMap"));
    Map<Bucket, List<WorkflowActivity>> workflowActivites = (Map<Bucket, List<WorkflowActivity>>) map.get("bucketMap");
    Assert.assertNotNull(workflowActivites);
    Assert.assertEquals(2, workflowActivites.size());
    Set<Bucket> buckets = workflowActivites.keySet();
    Integer i = 1;
    for (Bucket bucket : buckets) {
      Assert.assertEquals(i, bucket.getOrder());
      i++;
    }
  }

  /**
   * Description: Test to reset the workflow which was errored.
   * 
   * @author vinayv
   */
  @Test
  public void testResetWithErrorActivities() {

    Workflow workflow = workflowDAO.find(4L);
    int beforeActivityList = workflow.getWorkflowActivities().size();
    BusinessTransaction businessTransaction = businessTransactionService.getBusinessTransaction(workflow.getUuid());
    Assert.assertEquals(State.ERROR, businessTransaction.getState());
    boolean result = controller.reset(workflow.getUuid());
    Assert.assertEquals(true, result);
    businessTransaction = businessTransactionService.getBusinessTransaction(workflow.getUuid());
    Assert.assertEquals(State.RUNNING, businessTransaction.getState());
    workflow = workflowDAO.find(4L);
    int afterActivityList = workflow.getWorkflowActivities().size();
    Assert.assertEquals(beforeActivityList + 1, afterActivityList);
  }

  /**
   * Description: Test to reset the workflow which was not errored.
   * 
   * @author vinayv
   */
  @Test
  public void testResetWithoutErrorActivities() {

    Workflow workflow = workflowDAO.find(9L);
    int beforeActivityList = workflow.getWorkflowActivities().size();
    BusinessTransaction businessTransaction = businessTransactionService.getBusinessTransaction(workflow.getUuid());
    Assert.assertEquals(State.RUNNING, businessTransaction.getState());
    boolean result = controller.reset(workflow.getUuid());
    Assert.assertEquals(true, result);
    businessTransaction = businessTransactionService.getBusinessTransaction(workflow.getUuid());
    Assert.assertEquals(State.RUNNING, businessTransaction.getState());
    workflow = workflowDAO.find(9L);
    int afterActivityList = workflow.getWorkflowActivities().size();
    Assert.assertEquals(beforeActivityList, afterActivityList);
  }

  /**
   * Description: Test to create a Retail account with Workflow (Completing the activities in the workflow for the
   * tenant to become Active)
   * 
   * @author vinayv
   */
  @Test
  public void testCreateRetailAccountWithWorkflow() throws Exception {
    logger.info("Entering testCreateRetailAccountWithWorkflow test");
    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("RETAIL");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.WAITING, activityList.get(1).getStatus());
    activityList.get(1).getActivityRecord().getTask().setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(workflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    activityList = workflow.getWorkflowActivities();
    for (int i = 0; i < activityList.size(); i++) {
      Assert.assertEquals(Status.SUCCESS, activityList.get(i).getStatus());
    }
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenant.getId()).getState());
    logger.info("Exiting testCreateRetailAccountWithWorkflow test");
  }

  /**
   * Description: Test to create a Retail account with Workflow (Checking the Account state when the workflow activities
   * are in PENDING and FAILURE state)
   * 
   * @author vinayv
   */
  @Test
  public void testAccountStateWithFailedWorkflow() throws Exception {

    logger.info("Entering testAccountStateWithFailedWorkflow test");
    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("RETAIL");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.WAITING, activityList.get(1).getStatus());
    // Verifying the workflow state, tenant state when the Creditcard activity is in PENDING state
    activityList.get(1).getActivityRecord().getTask().setState(com.citrix.cpbm.core.workflow.model.Task.State.PENDING);
    runWorkflow(workflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.RUNNING, workflow.getState());
    Assert.assertEquals(com.vmops.model.Tenant.State.NEW, tenantDAO.find(tenant.getId()).getState());
    activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.WAITING, activityList.get(1).getStatus());
    // Verifying the workflow state, tenant state when the Creditcard activity is in FAILURE state
    activityList.get(1).getActivityRecord().getTask().setState(com.citrix.cpbm.core.workflow.model.Task.State.FAILURE);
    runWorkflow(workflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.FAILURE, workflow.getState());
    Assert.assertEquals(com.vmops.model.Tenant.State.NEW, tenantDAO.find(tenant.getId()).getState());
    logger.info("Exiting testAccountStateWithFailedWorkflow test");
  }

  /**
   * Description: Test to create a Trial account with Workflow (Completing the activities in the workflow for the tenant
   * to become Active)
   * 
   * @author vinayv
   */
  @Test
  public void testCreateTrialAccountWithWorkflow() throws Exception {
    logger.info("Entering testCreateTrialAccountWithWorkflow test");
    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("Trial");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenant.getId()).getState());
    logger.info("Exiting testCreateTrialAccountWithWorkflow test");
  }

  /**
   * Description: Test to create a Corporate account with Workflow (Completing the activities in the workflow for the
   * tenant to become Active)
   * 
   * @author vinayv
   */
  @Test
  public void testCreateCorporateAccountWithWorkflow() throws Exception {
    logger.info("Entering testCreateCorporateAccountWithWorkflow test");
    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("Corporate");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.WAITING, activityList.get(1).getStatus());
    activityList.get(1).getActivityRecord().getTask().setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(workflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    activityList = workflow.getWorkflowActivities();
    for (int i = 0; i < activityList.size(); i++) {
      Assert.assertEquals(Status.SUCCESS, activityList.get(i).getStatus());
    }
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenant.getId()).getState());
    logger.info("Exiting testCreateCorporateAccountWithWorkflow test");
  }

  /**
   * Description: Test to create a Corporate account and rejecting Finance Approval Activity in Workflow and checking
   * that the tenant remains in New state
   * 
   * @author vinayv
   */
  @Test
  public void testCreateCorporateAccountWithWorkflowRejectFinanceApproval() throws Exception {
    logger.info("Entering testCreateCorporateAccountWithWorkflowRejectFinanceApproval test");
    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("Corporate");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.WAITING, activityList.get(1).getStatus());
    activityList.get(1).getActivityRecord().getTask().setState(com.citrix.cpbm.core.workflow.model.Task.State.FAILURE);
    runWorkflow(workflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.FAILURE, workflow.getState());
    activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.FAILURE, activityList.get(1).getStatus());
    Assert.assertEquals(com.vmops.model.Tenant.State.NEW, tenantDAO.find(tenant.getId()).getState());
    logger.info("Exiting testCreateCorporateAccountWithWorkflowRejectFinanceApproval test");
  }

  /**
   * Description: Test to convert a Trial account into corporate account with Workflow
   * 
   * @author vinayv
   */
  @Test
  public void testConvertTrialToCorporateWithWorkflow() throws Exception {
    logger.info("Entering testConvertTrialToCorporateWithWorkflow test");

    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("Trial");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenant.getId()).getState());
    int beforeBusinessCount = businessTransactionDAO.count();
    changeAccountType(tenant, "Corporate");
    int afterBusinessCount = businessTransactionDAO.count();
    int beforeConversionWorkflowCount = workflowDAO.count();
    Assert.assertEquals(beforeBusinessCount + 1, afterBusinessCount);
    BusinessTransaction bt = null;
    List<BusinessTransaction> btlist = businessTransactionDAO.findAll(null);
    for (int i = 0; i < btlist.size(); i++) {
      if (btlist.get(i).getTenant().equals(tenant) && btlist.get(i).getType().equals(Type.tenantAccountTypeConversion)) {
        bt = btlist.get(i);
      }
    }
    Assert.assertEquals(State.NEW, bt.getState());
    consumer.receive(new TriggerTransaction(bt.getId()));
    int afterConversionWorkflowCount = workflowDAO.count();
    Assert.assertEquals(beforeConversionWorkflowCount + 1, afterConversionWorkflowCount);
    Workflow accountConversionWorkflow = workflowDAO.findByParam(bt.getWorkflowId());
    runWorkflow(accountConversionWorkflow);
    List<WorkflowActivity> accountConversionActivityList = accountConversionWorkflow.getWorkflowActivities();
    Assert.assertEquals(Status.WAITING, accountConversionActivityList.get(0).getStatus());
    accountConversionActivityList.get(0).getActivityRecord().getTask()
        .setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(accountConversionWorkflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, accountConversionWorkflow.getState());
    Assert.assertEquals("Corporate", tenant.getAccountType().getName());

    logger.info("Exiting testConvertTrialToCorporateWithWorkflow test");
  }

  /**
   * Description: Test to convert a Trial account into corporate account with Workflow (first rejecting the Finance
   * Approval and then Approving it again)
   * 
   * @author vinayv
   */
  @Test
  public void testConvertTrialToCorporateWithWorkflowRejectionAndApproval() throws Exception {
    logger.info("Entering testConvertTrialToCorporateWithWorkflowRejectionAndApproval test");

    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("Trial");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenant.getId()).getState());
    int beforeBusinessCount = businessTransactionDAO.count();
    changeAccountType(tenant, "Corporate");
    int afterBusinessCount = businessTransactionDAO.count();
    int beforeConversionWorkflowCount = workflowDAO.count();
    Assert.assertEquals(beforeBusinessCount + 1, afterBusinessCount);
    BusinessTransaction bt = null;
    List<BusinessTransaction> btlist = businessTransactionDAO.findAll(null);
    for (int i = 0; i < btlist.size(); i++) {
      if (btlist.get(i).getTenant().equals(tenant) && btlist.get(i).getType().equals(Type.tenantAccountTypeConversion)) {
        bt = btlist.get(i);
      }
    }
    Assert.assertEquals(State.NEW, bt.getState());
    consumer.receive(new TriggerTransaction(bt.getId()));
    int afterConversionWorkflowCount = workflowDAO.count();
    Assert.assertEquals(beforeConversionWorkflowCount + 1, afterConversionWorkflowCount);
    Workflow accountConversionWorkflow = workflowDAO.findByParam(bt.getWorkflowId());
    runWorkflow(accountConversionWorkflow);
    List<WorkflowActivity> accountConversionActivityList = accountConversionWorkflow.getWorkflowActivities();
    Assert.assertEquals(Status.WAITING, accountConversionActivityList.get(0).getStatus());
    accountConversionActivityList.get(0).getActivityRecord().getTask()
        .setState(com.citrix.cpbm.core.workflow.model.Task.State.FAILURE);
    runWorkflow(accountConversionWorkflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.FAILURE, accountConversionWorkflow.getState());
    Assert.assertEquals("Trial", tenant.getAccountType().getName());

    // Resetting errored workflow
    boolean result = controller.reset(accountConversionWorkflow.getUuid());
    Assert.assertEquals(true, result);
    accountConversionActivityList = accountConversionWorkflow.getWorkflowActivities();
    accountConversionActivityList.get(0).getActivityRecord().getTask()
        .setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(accountConversionWorkflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, accountConversionWorkflow.getState());
    Assert.assertEquals("Corporate", tenant.getAccountType().getName());
    logger.info("Exiting testConvertTrialToCorporateWithWorkflowRejectionAndApproval test");
  }

  /**
   * Description: Test to convert a Trial account into Retail account with Workflow
   * 
   * @author vinayv
   */
  @Test
  public void testConvertTrialToRetailWithWorkflow() throws Exception {
    logger.info("Entering testConvertTrialToRetailWithWorkflow test");

    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("Trial");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenant.getId()).getState());
    int beforeBusinessCount = businessTransactionDAO.count();
    changeAccountType(tenant, "RETAIL");
    int afterBusinessCount = businessTransactionDAO.count();
    int beforeConversionWorkflowCount = workflowDAO.count();
    Assert.assertEquals(beforeBusinessCount + 1, afterBusinessCount);
    BusinessTransaction bt = null;
    List<BusinessTransaction> btlist = businessTransactionDAO.findAll(null);
    for (int i = 0; i < btlist.size(); i++) {
      if (btlist.get(i).getTenant().equals(tenant) && btlist.get(i).getType().equals(Type.tenantAccountTypeConversion)) {
        bt = btlist.get(i);
      }
    }
    Assert.assertEquals(State.NEW, bt.getState());
    consumer.receive(new TriggerTransaction(bt.getId()));
    int afterConversionWorkflowCount = workflowDAO.count();
    Assert.assertEquals(beforeConversionWorkflowCount + 1, afterConversionWorkflowCount);
    Workflow accountConversionWorkflow = workflowDAO.findByParam(bt.getWorkflowId());
    runWorkflow(accountConversionWorkflow);
    List<WorkflowActivity> accountConversionActivityList = accountConversionWorkflow.getWorkflowActivities();
    Assert.assertEquals(Status.WAITING, accountConversionActivityList.get(0).getStatus());
    accountConversionActivityList.get(0).getActivityRecord().getTask()
        .setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(accountConversionWorkflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, accountConversionWorkflow.getState());
    Assert.assertEquals("RETAIL", tenant.getAccountType().getName());

    logger.info("Exiting testConvertTrialToRetailWithWorkflow test");
  }

  /**
   * Description: Test to convert a Retail account into corporate account with Workflow (first rejecting the Finance
   * Approval and then Approving it again)
   * 
   * @author vinayv
   */
  @Test
  public void testConvertRetailToCorporateWithWorkflowRejectionAndApproval() throws Exception {
    logger.info("Entering testConvertRetailToCorporateWithWorkflowRejectionAndApproval test");

    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("RETAIL");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.WAITING, activityList.get(1).getStatus());
    activityList.get(1).getActivityRecord().getTask().setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(workflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    activityList = workflow.getWorkflowActivities();
    for (int i = 0; i < activityList.size(); i++) {
      Assert.assertEquals(Status.SUCCESS, activityList.get(i).getStatus());
    }
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenant.getId()).getState());
    int beforeBusinessCount = businessTransactionDAO.count();
    changeAccountType(tenant, "Corporate");
    int afterBusinessCount = businessTransactionDAO.count();
    int beforeConversionWorkflowCount = workflowDAO.count();
    Assert.assertEquals(beforeBusinessCount + 1, afterBusinessCount);
    BusinessTransaction bt = null;
    List<BusinessTransaction> btlist = businessTransactionDAO.findAll(null);
    for (int i = 0; i < btlist.size(); i++) {
      if (btlist.get(i).getTenant().equals(tenant) && btlist.get(i).getType().equals(Type.tenantAccountTypeConversion)) {
        bt = btlist.get(i);
      }
    }
    Assert.assertEquals(State.NEW, bt.getState());
    consumer.receive(new TriggerTransaction(bt.getId()));
    int afterConversionWorkflowCount = workflowDAO.count();
    Assert.assertEquals(beforeConversionWorkflowCount + 1, afterConversionWorkflowCount);
    Workflow accountConversionWorkflow = workflowDAO.findByParam(bt.getWorkflowId());
    runWorkflow(accountConversionWorkflow);
    List<WorkflowActivity> accountConversionActivityList = accountConversionWorkflow.getWorkflowActivities();
    Assert.assertEquals(Status.WAITING, accountConversionActivityList.get(0).getStatus());
    accountConversionActivityList.get(0).getActivityRecord().getTask()
        .setState(com.citrix.cpbm.core.workflow.model.Task.State.FAILURE);
    runWorkflow(accountConversionWorkflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.FAILURE, accountConversionWorkflow.getState());
    Assert.assertEquals("RETAIL", tenant.getAccountType().getName());

    // Resetting errored workflow
    boolean result = controller.reset(accountConversionWorkflow.getUuid());
    Assert.assertEquals(true, result);
    accountConversionActivityList = accountConversionWorkflow.getWorkflowActivities();
    accountConversionActivityList.get(0).getActivityRecord().getTask()
        .setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(accountConversionWorkflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, accountConversionWorkflow.getState());
    Assert.assertEquals("Corporate", tenant.getAccountType().getName());

    logger.info("Exiting testConvertRetailToCorporateWithWorkflowRejectionAndApproval test");
  }

  /**
   * @Desc: Test to check workflow is triggered when a subscription is created for a tenant with Pre-Auth enabled
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateSubscriptionWithWorkflow() throws Exception {
    logger.info("Entering testCreateSubscriptionWithWorkflow test");
    // Setting Pre-Auth to true for Retail Account Type
    AccountType accountType = accountTypeService.locateAccountTypeName("RETAIL");
    accountType.setPreAuthRequired(true);
    accountTypeDAO.merge(accountType);
    Assert.assertEquals(true, accountType.isPreAuthRequired());
    // Creating a new retail Tenant
    Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("RETAIL");
    Workflow workflow = (Workflow) workflowAndTenantIdMap.get("workflow");
    Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
    List<WorkflowActivity> activityList = workflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(Status.WAITING, activityList.get(1).getStatus());
    activityList.get(1).getActivityRecord().getTask().setState(com.citrix.cpbm.core.workflow.model.Task.State.SUCCESS);
    runWorkflow(workflow);
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, workflow.getState());
    activityList = workflow.getWorkflowActivities();
    for (int i = 0; i < activityList.size(); i++) {
      Assert.assertEquals(Status.SUCCESS, activityList.get(i).getStatus());
    }
    Assert.assertEquals(com.vmops.model.Tenant.State.ACTIVE, tenantDAO.find(tenant.getId()).getState());
    // Creating a subscription for the Tenant
    Workflow subscriptionWorkflow = createSubscriptionWithWorkflow(tenant);
    activityList = subscriptionWorkflow.getWorkflowActivities();
    Assert.assertEquals(Status.NEW, activityList.get(0).getStatus());
    Assert.assertEquals("subscription-activation.preAuthActivity", activityList.get(0).getName());
    runWorkflow(subscriptionWorkflow);
    activityList = subscriptionWorkflow.getWorkflowActivities();
    Assert.assertEquals(Status.SUCCESS, activityList.get(0).getStatus());
    Assert.assertEquals(com.citrix.cpbm.workflow.model.Workflow.State.COMPLETED, subscriptionWorkflow.getState());
    logger.info("Exiting testCreateSubscriptionWithWorkflow test");
  }

  /**
   * @Desc: Test to check user is not able to subscribe when Tenant state is New
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateSubscriptionWithNewStateTenant() throws Exception {
    logger.info("Entering testCreateSubscriptionWithNewStateTenant test");
    try {
      // Creating a new retail Tenant and keeping in NEW state
      Map<Object, Object> workflowAndTenantIdMap = tenantCreationWithWorkFlowAndEmailVerification("RETAIL");
      Tenant tenant = (Tenant) workflowAndTenantIdMap.get("tenant");
      Assert.assertEquals(com.vmops.model.Tenant.State.NEW, tenantDAO.find(tenant.getId()).getState());
      // Creating a subscription for the Tenant
      createSubscriptionWithWorkflow(tenant);
    } catch (Exception e) {
      Assert.assertEquals("User account is not active", e.getMessage());
    }
    logger.info("Exiting testCreateSubscriptionWithNewStateTenant test");
  }

  /**
   * @desc private method to create a specified account type tenant with workflow
   * @author vinayv
   * @param accountTypeName
   * @return Map containing Workflow and Tenant objects
   * @throws Exception
   */
  private Map<Object, Object> tenantCreationWithWorkFlowAndEmailVerification(String accountTypeName) throws Exception {
    Map<Object, Object> workflowAndTenantIdMap = new HashMap<Object, Object>();
    setupWorkflow();
    int beforeBusinessCount = businessTransactionDAO.count();
    int beforeTenantCount = tenantDAO.count();
    int beforeUserCount = userDAO.count();
    int beforeWrkflwCount = workflowDAO.count();
    AccountType accountType = accountTypeService.locateAccountTypeName(accountTypeName);
    Tenant tenant = createTenant(accountType.getId());
    workflowAndTenantIdMap.put("tenant", tenant);
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
    workflowAndTenantIdMap.put("workflow", workflow);
    return workflowAndTenantIdMap;
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
   * Description: Private method to create a Tenant for the given Account Type
   * 
   * @author vinayv
   */
  private Tenant createTenant(Long accountType) throws Exception {
    int prevTList = tenantDAO.count();
    int prevUList = userDAO.count();
    int prevBTList = businessTransactionDAO.count();
    AccountType type = accountTypeDAO.find(accountType);
    Tenant tenant = new Tenant("Tenant" + random.nextInt(), type, getRootUser(), randomAddress(), true,
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
    // Trial AccountType ID is 5
    if (accountType.intValue() == 5) {
      CampaignPromotion promo = campaignPromotionDAO.find(2L);
      promo.setTrial(true);
      campaignPromotionDAO.merge(promo);
      form.setTrialCode(promo.getCode());
    }

    BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
    String tenantCreation = tenantsController.create(form, result, map, status, request);
    logger.debug("RESULT :" + tenantCreation);
    Assert.assertTrue("verifying the form has zero error", tenantCreation.contains("0 errors"));
    Long newTenantId = (Long) map.get("tenantId");
    Tenant obtainedTenant = tenantDAO.find(newTenantId);
    // If AccountType is Trial then linking the TrialAccount row to newly created Tenant
    if (accountType.intValue() == 5) {
      List<TrialAccount> trialAccountList = trialAccountDAO.findAll(null);
      for (int i = 0; i < trialAccountList.size(); i++) {
        if (trialAccountList.get(i).getTenant().equals(obtainedTenant)) {
          obtainedTenant.setTrialAccount(trialAccountList.get(0));
          tenantDAO.merge(obtainedTenant);
        }
      }
    }
    obtainedTenant = tenantDAO.find(newTenantId);
    Assert.assertEquals(tenant.getOwner().getUsername(), obtainedTenant.getOwner().getUsername());
    int afterTList = tenantDAO.count();
    int afterUList = userDAO.count();
    int afterBTList = businessTransactionDAO.count();

    Assert.assertEquals("", prevTList + 1, afterTList);
    Assert.assertEquals("", prevUList + 1, afterUList);
    Assert.assertEquals("", prevBTList + 1, afterBTList);

    logger.debug("Tenants before  ::" + prevTList + ",  Tenants After :: " + afterTList);
    logger.debug("Users before  ::" + prevUList + ",  Users After :: " + afterUList);
    logger.debug("businessTransactions before  ::" + prevBTList + ",  businessTransactions After :: " + afterBTList);

    List<PortalEvent> eventList = eventListener.getEvents();
    Assert.assertTrue(eventList.get(0).getPayload() instanceof TenantActivation);
    return obtainedTenant;
  }

  /**
   * @desc Private method to change the account type of the given tenant to the given account type
   * @author vinayv
   * @param tenant
   * @param accountTypeName
   * @throws IOException
   */
  private void changeAccountType(Tenant tenant, String accountTypeName) throws IOException {

    CreditCard creditCard = new CreditCard("VISA", "4111111111111111", 3, 2015, "123",
        tenant.getOwner().getFirstName(), VALID_BILLING_ADDRESS);
    if (creditCard != null) {
      creditCard.setNameOnCard(creditCard.getFirstNameOnCard() + " " + creditCard.getLastNameOnCard());
    }
    AccountType accountType = accountTypeService.locateAccountTypeName(accountTypeName);
    if (accountType.getSupportedPaymentModes().get(0) == PaymentMode.CREDIT_CARD) {
      if (creditCard == null || creditCard.getCreditCardNumber() == null) {
        throw new InvalidPaymentModeException("Credit card is required for this account type");
      }
    }
    tenantService.convertAccountType(tenant, new BillingInfo(creditCard), BigDecimal.TEN, accountTypeName, null);
  }

  /**
   * Private method to setup the initial configurations for a workflow
   * 
   * @author vinayv
   */
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

    // setupMail();
  }

  /**
   * @Desc Private method to create a subscription with workflow for a given tenant
   * @author vinayv
   * @param tenant
   * @return subscription workflow object
   */
  private Workflow createSubscriptionWithWorkflow(Tenant tenant) {

    ServiceResourceType srt = serviceResourceTypeDAO.find(1L);
    srt.setConstraint(ResourceConstraint.NONE);
    serviceResourceTypeDAO.save(srt);
    User user = tenant.getOwner();
    int subscriptionCount = subscriptionService.getCountByTenant(tenant);
    ProductBundle productBundle = bundleService.locateProductBundleById("2");
    productBundle.setNotificationEnabled(true);
    productBundle.setBusinessConstraint(ResourceConstraint.NONE);
    bundleService.updateProductBundle(productBundle, false);
    productBundle = bundleService.locateProductBundleById("2");
    Subscription createdSubscription = subscriptionService.createSubscription(user, productBundle, productBundle
        .getServiceInstanceId().getUuid(), "VirtualMachine", false, true, proxySubscription,
        new HashMap<String, String>());
    Assert.assertNotNull(createdSubscription);
    Assert.assertEquals(tenant, createdSubscription.getTenant());
    subscriptionService.activateSubscription(createdSubscription);
    Assert.assertEquals(subscriptionCount + 1, subscriptionService.getCountByTenant(tenant));
    BusinessTransaction bt = null;
    List<BusinessTransaction> btlist = businessTransactionDAO.findAll(null);
    for (int i = 0; i < btlist.size(); i++) {
      if (btlist.get(i).getTenant().equals(tenant)
          && btlist.get(i).getType().equals(BusinessTransaction.Type.subscriptionActivation)) {
        bt = btlist.get(i);
      }
    }
    Assert.assertEquals(State.NEW, bt.getState());
    Assert.assertEquals(Type.subscriptionActivation, bt.getType());
    consumer.receive(new TriggerTransaction(bt.getId()));
    String workflowParam = bt.getWorkflowId();
    Workflow workflow = workflowDAO.findByParam(workflowParam);

    return workflow;
  }

}
