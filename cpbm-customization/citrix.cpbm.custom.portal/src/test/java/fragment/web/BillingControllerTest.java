/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;

import com.citrix.cpbm.platform.admin.service.exceptions.ConnectorManagementServiceException;
import com.citrix.cpbm.platform.bootstrap.service.BootstrapActivator;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.SubscriptionLifecycleHandler;
import com.citrix.cpbm.portal.fragment.controllers.BillingController;
import com.vmops.config.BillingPostProcessor;
import com.vmops.internal.service.SubscriptionService;
import com.vmops.model.AccountType;
import com.vmops.model.Address;
import com.vmops.model.Configuration;
import com.vmops.model.CreditCard;
import com.vmops.model.Event;
import com.vmops.model.Invoice;
import com.vmops.model.InvoiceItem;
import com.vmops.model.ProductBundle;
import com.vmops.model.Service;
import com.vmops.model.Subscription;
import com.vmops.model.SubscriptionHandle;
import com.vmops.model.SubscriptionHandle.State;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.model.billing.AccountStatement;
import com.vmops.model.billing.SalesLedgerRecord;
import com.vmops.model.billing.SalesLedgerRecord.Type;
import com.vmops.persistence.ConfigurationDAO;
import com.vmops.persistence.EventDAO;
import com.vmops.persistence.SubscriptionDAO;
import com.vmops.persistence.billing.AccountStatementDAO;
import com.vmops.service.ProductBundleService;
import com.vmops.service.billing.BillingAdminService;
import com.vmops.service.billing.BillingService;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.web.forms.BillingInfoForm;
import com.vmops.web.forms.DepositRecordForm;
import com.vmops.web.forms.SetAccountTypeForm;
import common.MockCloudInstance;
import common.MockPaymentGatewayService;

public class BillingControllerTest extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  @Autowired
  private BillingController controller;

  @Autowired
  private ConfigurationDAO configurationDAO;

  @Autowired
  private SubscriptionDAO subscriptionDAO;

  @Autowired
  private SubscriptionService subscriptionService;

  @Autowired
  private AccountStatementDAO accountStatementDAO;

  @Autowired
  private BillingAdminService billingAdminService;

  @Autowired
  private ProductBundleService productBundleService;

  @Autowired
  private BillingService billingService;

  @Autowired
  private EventDAO eventDAO;

  @Autowired
  private BillingPostProcessor billingPostProcessor;

  private BootstrapActivator bootstrapActivator = new BootstrapActivator();

  private static boolean isMockInstanceCreated = false;

  private HttpSession session;

  private MockPaymentGatewayService paymentGatewayService;

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    prepareMock(true, bootstrapActivator);
    if (!isMockInstanceCreated) {

      Service ossService = serviceDAO.find(7l);
      ossService.setEnabled(true);
      Service cloudService = serviceDAO.find(6l);
      connectorManagementService.getAllServiceInstances(cloudService);

      isMockInstanceCreated = true;
    }
    asRoot();
  }

  @Override
  public void prepareMock() {

    MockCloudInstance mock = this.getMockCloudInstance();
    CloudConnector connector = mock.getCloudConnector();
    EasyMock.expect(connector.getServiceInstanceUUID()).andReturn("12345-786").anyTimes();
    EasyMock.replay(connector);
  }

  private IAnswer<Boolean> mockSubscriptiontHandles(final State state, final boolean validationState, final User user) {
    return new IAnswer<Boolean>() {

      @Override
      public Boolean answer() throws Exception {
        Subscription subscription = ((Subscription) EasyMock.getCurrentArguments()[0]);
        subscription.getHandle().setState(state);
        return validationState;
      }

    };
  }

  private void prepareMockForShowSubscriptionDetails(State state, boolean validationState, User user) {

    MockCloudInstance mock = this.getMockCloudInstance();
    SubscriptionLifecycleHandler slh = mock.getSubscriptionLifecycleHandler();
    EasyMock.expect(slh.validate(EasyMock.anyObject(Subscription.class)));
    EasyMock.expectLastCall().andAnswer(mockSubscriptiontHandles(state, validationState, user)).anyTimes();
    slh.destroy(EasyMock.anyObject(Subscription.class));
    EasyMock.expectLastCall().andAnswer(mockSubscriptiontHandles(State.TERMINATED, false, user)).anyTimes();
    EasyMock.replay(slh);
  }

  private void prepareMockForTerminateAndCancelSubscriptionDetails(User user) {

    MockCloudInstance mock = this.getMockCloudInstance();
    SubscriptionLifecycleHandler slh = mock.getSubscriptionLifecycleHandler();
    slh.destroy(EasyMock.anyObject(Subscription.class));
    EasyMock.expectLastCall().andAnswer(mockSubscriptiontHandles(State.TERMINATED, false, user)).anyTimes();
    EasyMock.replay(slh);
  }

  @Test
  public void testRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class<?> controllerClass = controller.getClass();
    Method expected = locateMethod(controllerClass, "showSubscriptions", new Class[] {
        String.class, String.class, String.class, String.class, String.class, String.class, Long.class, Long.class,
        ModelMap.class, HttpServletRequest.class
    });

    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/billing/subscriptions"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "showUtilityCharges", new Class[] {
        String.class, String.class, String.class, String.class, String.class, String.class, Long.class, Long.class,
        ModelMap.class, HttpServletRequest.class
    });

    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/billing/utility_charges"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "showSubscriptionDetails", new Class[] {
        String.class, String.class, ModelMap.class, HttpServletRequest.class
    });

    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/billing/subscriptions/showDetails"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "showUtilityChargeDetails", new Class[] {
        String.class, String.class, ModelMap.class, HttpServletRequest.class
    });

    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/billing/utility_charges/showDetails"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "viewInitialDeposit", new Class[] {
        String.class, ModelMap.class
    });

    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/billing/viewInitialDeposit"));
    Assert.assertEquals(expected, handler);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void usageTest() throws ConnectorManagementServiceException {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = createTestUserInTenant(tenant);
    tenantService.setOwner(tenant, user);
    asUser(tenant.getOwner());
    String view = controller.usageBilling(tenant, null, null, "1", null, null, map, request);
    Assert.assertEquals("billing.usageBilling", view);
    Assert.assertEquals(true, map.containsAttribute("userHasCloudServiceAccount"));
    Assert.assertEquals(false, map.containsAttribute("showUserProfile"));
    Assert.assertEquals(true, map.containsAttribute("accountStatements"));
    Assert.assertEquals(true, map.containsAttribute("accountStatementUuid"));
    Assert.assertEquals(true, map.containsAttribute("accountStatementState"));
    Assert.assertEquals(true, map.containsAttribute("payments"));
    Assert.assertEquals(true, map.containsAttribute("creditsIssued"));
    Assert.assertEquals(true, map.containsAttribute("bigPaymentsSum"));
    Assert.assertEquals(map.get("isSystemProviderUser"), new String("N"));
    Assert.assertEquals(map.get("userHasCloudServiceAccount"), Boolean.valueOf(false));
    List<AccountStatement> accountStatements = (List<AccountStatement>) map.get("accountStatements");
    Assert.assertEquals(accountStatements.size(), Integer.parseInt("1"));
    Assert.assertEquals(map.get("accountStatementUuid"), accountStatements.get(0).getUuid());
    Assert.assertEquals(map.get("accountStatementState"), accountStatements.get(0).getState().name());
    Assert.assertEquals(map.get("newBigAmount"), BigDecimal.ZERO);
    List<InvoiceItem> items = new ArrayList<InvoiceItem>();
    InvoiceItem invoiceItem = new InvoiceItem();
    invoiceItem.setDescription("Testing");
    items.add(invoiceItem);

  }

  @Test
  public void usageTestMoreThanOneBP() throws ConnectorManagementServiceException {
    int billingPeriodParam = 30;
    Configuration configuration = configurationDAO
        .findByName(com.vmops.portal.config.Configuration.Names.com_citrix_cpbm_portal_billing_billingPeriod_type
            .name().replaceAll("_", "."));
    configuration.setValue(new Integer(1).toString());
    configurationDAO.save(configuration);
    configuration = configurationDAO
        .findByName(com.vmops.portal.config.Configuration.Names.com_citrix_cpbm_portal_billing_billingPeriod_config
            .name().replaceAll("_", "."));
    configuration.setValue(new Integer(billingPeriodParam).toString());
    configurationDAO.save(configuration);

    int noOfdays = 120;
    Calendar createdAt = Calendar.getInstance();
    createdAt.add(Calendar.DATE, 0 - noOfdays);
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType(), createdAt.getTime());
    User user = createTestUserInTenant(tenant);
    tenantService.setOwner(tenant, user);

    asUser(tenant.getOwner());
    String view = controller.usageBilling(tenant, null, null, "1", null, null, map, request);
    Assert.assertEquals("billing.usageBilling", view);

  }

  // TODO: Ashishj Fix the test as per new design.
  /*
   * @Test
   * @SuppressWarnings("unchecked") public void usageTestRetrieveLatest() throws ConnectorManagementServiceException {
   * int billingPeriodParam = 30; Configuration configuration =
   * configurationDAO.findByName(com.vmops.portal.config.Configuration
   * .Names.com_citrix_cpbm_portal_billing_billingPeriod_type .name().replaceAll("_", ".")); configuration.setValue(new
   * Integer(BillingPeriodType.FIXED_DATE.ordinal() + 1).toString()); configurationDAO.save(configuration);
   * configuration = configurationDAO.findByName(com.vmops.portal.config.Configuration.Names.
   * com_citrix_cpbm_portal_billing_billingPeriod_config .name().replaceAll("_", ".")); configuration.setValue(new
   * Integer(billingPeriodParam).toString()); configurationDAO.save(configuration); int noOfdays = 120; Calendar
   * createdAt = Calendar.getInstance(); createdAt.add(Calendar.DATE, 0 - noOfdays); //TODO is there any API which
   * returns -ve of given integer Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType(),
   * createdAt.getTime()); User user = createTestUserInTenant(tenant); tenantService.setOwner(tenant, user); //Creating
   * Inovices... BillingPeriodDetail billingPeriodDetails = config.getBillingPeriodDetail(tenant);
   * List<BillingPeriodDates> billingPeriodDatesList = billingPeriodDetails.getAllBillingPeriodDates(new Date()); for
   * (BillingPeriodDates billingPeriodDates : billingPeriodDatesList) { Invoice invoice = new Invoice(); Date
   * currentDate = new Date(); invoice.setType(Type.Subscription); invoice.setAmount(new
   * BigDecimal(random.nextDouble())); invoice.setAmountDue(invoice.getAmount()); invoice.setCreatedAt(currentDate);
   * invoice.setTenant(tenant); invoice.setInvoiceHandle(new Long(random.nextLong()).toString()); invoice.
   * setBillingPeriodStartDate(billingPeriodDates.getBillingPeriodStartDate ().getTime());
   * invoice.setBillingPeriodEndDate(billingPeriodDates.getBillingPeriodEndDate ().getTime()); Calendar generatedOn =
   * (Calendar) billingPeriodDates.getBillingPeriodEndDate().clone(); generatedOn.add(Calendar.DATE, 1);
   * invoice.setGeneratedOn(generatedOn); billingService.updateInvoiceSummary(invoice, createInvoiceItems(tenant,
   * invoice)); invoiceDAO.save(invoice); } asUser(tenant.getOwner()); String view = controller.usageBilling(tenant,
   * "1", null,"1", map, request); Assert.assertEquals("billing.usageBilling", view); Assert.assertEquals((noOfdays /
   * billingPeriodParam) + 1, ((LinkedHashMap<BillingPeriodDates, Map<String, Object>>) map
   * .get("billingPeriodDatesList")).size()); Assert.assertEquals(false, map.get("isCurrent")); Assert.assertEquals("1",
   * map.get("viewBy")); }
   */

  protected List<InvoiceItem> createInvoiceItems(Tenant tenant, Invoice invoice) {

    List<InvoiceItem> items = new ArrayList<InvoiceItem>();
    InvoiceItem invoiceItem = new InvoiceItem();
    // invoiceItem.setAmountAfterDiscount(invoice.getAmount().subtract(invoice.getTaxAmount()));
    // invoiceItem.setAmountAfterTax(invoice.getAmount());
    invoiceItem.setDescription("Testing");
    invoiceItem.setAmount(invoice.getAmount().subtract(invoice.getTaxAmount()));
    // invoiceItem.setChargeType(ChargeType.CHARGE);
    // invoiceItem.setDiscountAmount(new BigDecimal(0));
    // invoiceItem.setTaxAmount(new BigDecimal(0));
    invoiceItem.setQuantity(new BigDecimal(1));
    invoiceItem.setInvoice(invoice);
    invoiceItem.setServiceStartDate(invoice.getServiceStartDate());
    invoiceItem.setServiceEndDate(invoice.getServiceEndDate());
    items.add(invoiceItem);
    return items;
  }

  @Test
  public void testRecordDepositPost() throws Exception {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    DepositRecordForm recordForm = new DepositRecordForm();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    recordForm.setReceivedOn(sdf.format(getDaysFromNow(-1)));
    recordForm.setAmount("1000.00");
    BindingResult result = validate(recordForm);
    Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
    DepositRecordForm returnForm = controller.recordDeposit(tenant.getParam(), recordForm, result, map, response);
    Assert.assertNotNull(returnForm);
    Assert.assertEquals(200, response.getStatus());
    tenantDAO.flush();
    tenantDAO.clear();
    Tenant found = tenantDAO.find(tenant.getId());
    Assert.assertEquals(tenantService.getDepositRecordByTenant(tenant), found.getDeposit());
  }

  @Test
  public void testRecordDepositPostBindFail() throws Exception {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.save(tenant);
    DepositRecordForm recordForm = new DepositRecordForm();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    recordForm.setReceivedOn(sdf.format(getDaysFromNow(-3)));
    recordForm.setAmount("1000.00");
    BindingResult result = validate(recordForm);
    Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
    DepositRecordForm returnForm = controller.recordDeposit(tenant.getParam(), recordForm, result, map, response);
    Assert.assertNotNull(returnForm);
    Assert.assertNull(tenant.getDeposit());
  }

  @Test
  public void testShowSubscriptionDetails() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(2L);
    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(true, map.get("allowTermination"));
    Assert.assertEquals(true, map.get("toProvision"));
    Assert.assertEquals(sub.getState().toString(), "ACTIVE");
    Assert.assertNotNull(((Subscription) map.get("subscription")).getUtilityCharges());
    Assert.assertTrue(((Subscription) map.get("subscription")).getUtilityCharges().size() > 0);
  }

  /*
   * New Subscription and Provisioning handle. connector's Validate moves handle to Active and returns true.
   */
  @Test
  public void testDetailsWithNewSubsProvToActHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);
    Subscription sub = subscriptionService.locateSubscriptionById(1731L);
    Assert.assertEquals(com.vmops.model.Subscription.State.NEW, sub.getState());
    sub.getHandle().setState(State.PROVISIONING);
    subscriptionDAO.merge(sub);

    prepareMockForShowSubscriptionDetails(State.ACTIVE, true, tenant.getOwner());

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(false, map.get("toProvision"));
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertFalse("Since subscription is in New and resource active state, termination should not be allowed",
        map.containsKey("allowTermination"));
    Assert.assertEquals("Since subscription is in New and resource active state, cancellation should not be allowed",
        false, map.containsKey("allowTermination"));
    Assert.assertEquals(Subscription.State.NEW, sub.getState());
  }

  /*
   * New Subscription and Provisioning handle. connector's Validate keeps handle in Provisioning state and returns true.
   */
  @Test
  public void testDetailsWithNewSubsProvToProvHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);
    Subscription sub = subscriptionService.locateSubscriptionById(1731L);
    Assert.assertEquals(com.vmops.model.Subscription.State.NEW, sub.getState());
    sub.getHandle().setState(State.PROVISIONING);
    subscriptionDAO.merge(sub);

    prepareMockForShowSubscriptionDetails(State.PROVISIONING, true, tenant.getOwner());

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(false, map.get("toProvision"));
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(false, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.NEW, sub.getState());
  }

  /*
   * New Subscription and Provisioning handle. connector's Validate moves handle to Error and returns false.
   */
  @Test
  public void testDetailsWithNewSubsProvToErrrHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(1731L);
    Assert.assertEquals(com.vmops.model.Subscription.State.NEW, sub.getState());
    sub.getHandle().setState(State.PROVISIONING);
    subscriptionDAO.merge(sub);

    prepareMockForShowSubscriptionDetails(State.ERROR, false, tenant.getOwner());

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(true, map.get("toProvision"));
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(false, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.NEW, sub.getState());
    Assert.assertEquals(State.ERROR, sub.getHandle().getState());

  }

  /*
   * New Subscription and Provisioning handle. connector's Validate moves handle to TERMINATED and returns false.
   */
  @Test
  public void testDetailsWithNewSubsProvToTermHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(1731L);
    Assert.assertEquals(com.vmops.model.Subscription.State.NEW, sub.getState());
    sub.getHandle().setState(State.PROVISIONING);
    subscriptionDAO.merge(sub);

    prepareMockForShowSubscriptionDetails(State.TERMINATED, false, tenant.getOwner());

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(false, map.get("toProvision"));
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(false, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.NEW, sub.getState());
    Assert.assertEquals(State.TERMINATED, sub.getHandle().getState());

  }

  /*
   * New Subscription and NO handle. connector's Validate returns false.
   */
  @Test
  public void testDetailsWithNewSubWithNoHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(1731L);
    Assert.assertEquals(com.vmops.model.Subscription.State.NEW, sub.getState());
    sub.setSubscriptionHandles(new HashSet<SubscriptionHandle>());
    subscriptionDAO.merge(sub);

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(false, map.get("toProvision"));
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(false, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.NEW, sub.getState());
  }

  /*
   * Active Subscription, Active handle. connector's Validate returns true.
   */
  @Test
  public void testDetailsWithActSubWithActHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(2L);
    Assert.assertEquals(com.vmops.model.Subscription.State.ACTIVE, sub.getState());
    Assert.assertEquals(State.ACTIVE, sub.getHandle().getState());

    prepareMockForShowSubscriptionDetails(State.ACTIVE, true, tenant.getOwner());

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(false, map.get("toProvision"));
    // isReconfigure will be false for bundle subscriptions.
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(true, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.ACTIVE, sub.getState());
    Assert.assertEquals(State.ACTIVE, sub.getHandle().getState());
  }

  /*
   * Active Subscription, Active handle. connector's Validate moves handle to TERMINATED state and returns false.
   */
  @Test
  public void testDetailsWithActSubWithTermHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(2L);
    Assert.assertEquals(com.vmops.model.Subscription.State.ACTIVE, sub.getState());
    Assert.assertEquals(State.ACTIVE, sub.getHandle().getState());

    prepareMockForShowSubscriptionDetails(State.TERMINATED, false, tenant.getOwner());

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(true, map.get("toProvision"));
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(true, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.ACTIVE, sub.getState());
    Assert.assertEquals(State.TERMINATED, sub.getHandle().getState());
  }

  /*
   * Active Subscription, Active handle. connector's Validate ERROR and returns false.
   */
  @Test
  public void testDetailsWithActSubWithErrHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(2L);
    Assert.assertEquals(com.vmops.model.Subscription.State.ACTIVE, sub.getState());
    Assert.assertEquals(State.ACTIVE, sub.getHandle().getState());

    prepareMockForShowSubscriptionDetails(State.ERROR, false, tenant.getOwner());

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(true, map.get("toProvision"));
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(true, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.ACTIVE, sub.getState());
    Assert.assertEquals(State.ERROR, sub.getHandle().getState());
  }

  /*
   * Active Subscription, NO handle. connector's Validate returns false.
   */
  @Test
  public void testDetailsWithActSubWithNOHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(2L);
    Assert.assertEquals(com.vmops.model.Subscription.State.ACTIVE, sub.getState());
    sub.setSubscriptionHandles(new HashSet<SubscriptionHandle>());
    subscriptionDAO.merge(sub);

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(true, map.get("toProvision"));
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(true, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.ACTIVE, sub.getState());

  }

  /*
   * Active Subscription, Provisioning handle. connector's Validate keeps it to provisioning state and returns true.
   */
  @Test
  public void testDetailsWithActSubWithProvHandle() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(2L);
    Assert.assertEquals(com.vmops.model.Subscription.State.ACTIVE, sub.getState());
    Assert.assertEquals(State.ACTIVE, sub.getHandle().getState());

    prepareMockForShowSubscriptionDetails(State.PROVISIONING, true, tenant.getOwner());

    String view = controller.showSubscriptionDetails(sub.getUuid(), tenant.getUuid(), map, request);
    sub = subscriptionDAO.find(sub.getId());

    Assert.assertEquals("billing.viewSubscription", view);
    Assert.assertEquals(false, map.get("toProvision"));
    // isReconfigure is false for bundle subscriptions.
    Assert.assertEquals(false, map.get("toReconfigure"));
    Assert.assertEquals(true, map.get("allowTermination"));
    Assert.assertEquals(Subscription.State.ACTIVE, sub.getState());
    Assert.assertEquals(State.PROVISIONING, sub.getHandle().getState());

  }

  @Test
  public void testSendEmailPdfAccountStatement() {
    Tenant tenant = tenantDAO.find(2L);
    List<Event> list = eventDAO.findByTenant(tenant);
    asUser(tenant.getOwner());
    Assert.assertEquals(list.size(), 0);

    AccountStatement accountStatement = accountStatementDAO.find(17291L);
    request.setAttribute("effectiveTenant", tenant);
    controller.sendEmailPdfAccountStatement(tenant, "1", tenant.getParam(), accountStatement.getUuid(), "1", map,
        request, response);
    Assert.assertNotNull(tenant);
    list = eventDAO.findByTenant(tenant);
    Assert.assertNotNull(list);
    Assert.assertEquals(list.size(), 1);

  }

  @Test
  public void testGeneratePdfAccountStatement() {

    Tenant tenant = tenantDAO.find(2L);
    AccountStatement accountStatement = accountStatementDAO.find(17291L);
    request.setAttribute("effectiveTenant", tenant);
    asUser(tenant.getOwner());
    String view = controller.generatePdfAccountStatement(tenant, "1", tenant.getParam(), accountStatement.getUuid(),
        "1", map, request, response);
    Assert.assertEquals(response.getContentType(), new String("application/pdf"));
    Assert.assertNull(view);
  }

  @Test
  public void testGenerateUDR() {

    Tenant tenant = tenantDAO.find(2L);
    AccountStatement accountStatement = accountStatementDAO.find(17291L);
    request.setAttribute("effectiveTenant", tenant);
    controller.generateUDR(tenant, "1", tenant.getParam(), accountStatement.getUuid(), "1", map, request, response);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testShowSubscriptions() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = createTestUserInTenant(tenant);
    tenantService.setOwner(tenant, user);
    asUser(tenant.getOwner());
    tenantService.update(tenant);

    Subscription sub = subscriptionService.locateSubscriptionById(2L);
    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    request.setAttribute("effectiveTenant", tenant);
    String view = controller.showSubscriptions(tenant.getUuid(), "1", sub.getUuid(), sub.getState().toString(),
        user.getUuid(), null, null, null, map, request);
    Assert.assertTrue(map.containsAttribute("isPayAsYouGoChosen"));
    Assert.assertEquals("billing.showSubscriptions", view);
    Assert.assertEquals(null, map.get("allowTermination"));
    Assert.assertEquals(sub.getState().toString(), "ACTIVE");

    tenant = tenantDAO.find(2L);
    request.setAttribute("effectiveTenant", tenant);
    asUser(tenant.getOwner());
    view = controller.showSubscriptions(tenant.getUuid(), "1", null, null, userDAO.find(3L).getUuid(),
        "003fa8ee-fba3-467f-a517-fd806dae8a80", 2L, null, map, request);
    Assert.assertEquals(5, ((List<Subscription>) map.get("subscriptions")).size());

    view = controller.showSubscriptions(tenant.getUuid(), "1", null, null, null,
        "003fa8ee-fba3-467f-a517-fd806dae8a80", 8L, null, map, request);
    Assert.assertEquals(1, ((List<Subscription>) map.get("subscriptions")).size());

    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    view = controller.showSubscriptions(tenant.getUuid(), "1", null, sub.getState().toString(), null,
        "003fa8ee-fba3-467f-a517-fd806dae8a80", 8L, null, map, request);
    Assert.assertEquals(1, ((List<Subscription>) map.get("subscriptions")).size());
  }

  @Test
  public void testTerminateSubscription() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setState(com.vmops.model.Tenant.State.ACTIVE);
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = createTestUserInTenant(tenant);
    tenantService.setOwner(tenant, user);
    List<ProductBundle> bundles = productBundleService.listProductBundles(0, 0);
    ProductBundle nonVmBundle = null;
    for (ProductBundle bundle : bundles) {
      if (!bundle.getResourceType().getResourceTypeName().equals("VirtualMachine")) {
        nonVmBundle = bundle;
        break;
      }
    }
    tenant.getOwner().setEnabled(true);
    Subscription subscription = subscriptionService.createSubscription(tenant.getOwner(), nonVmBundle, null, null,
        false, false, null, new HashMap<String, String>());
    Assert.assertNotNull(subscription);
    SubscriptionHandle subscriptionHandle = new SubscriptionHandle(subscription, "", subscription.getServiceInstance()
        .getUuid(), "VirtualMachine", user, State.ACTIVE, null, null, "TestVM");
    subscription.addHandle(subscriptionHandle);
    subscriptionDAO.merge(subscription);
    prepareMockForTerminateAndCancelSubscriptionDetails(tenant.getOwner());
    Date timeBeforeTermination = new Date();
    Subscription sub = controller.terminateSubscription(subscription.getParam(), map);
    Assert.assertNotNull(sub);
    Assert.assertTrue((timeBeforeTermination.before(sub.getTerminationDateWithTime()) || timeBeforeTermination
        .equals(sub.getTerminationDateWithTime())));
    Assert.assertEquals(sub.getState().name(), new String("EXPIRED"));
    Assert.assertEquals(State.TERMINATED, sub.getHandle().getState());

  }

  @Test
  public void testCancelSubscription() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setState(com.vmops.model.Tenant.State.ACTIVE);
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = createTestUserInTenant(tenant);
    tenantService.setOwner(tenant, user);
    List<ProductBundle> bundles = productBundleService.listProductBundles(0, 0);
    ProductBundle nonVmBundle = null;
    for (ProductBundle bundle : bundles) {
      if (!bundle.getResourceType().getResourceTypeName().equals("VirtualMachine")) {
        nonVmBundle = bundle;
        break;
      }
    }
    tenant.getOwner().setEnabled(true);
    Subscription subscription = subscriptionService.createSubscription(tenant.getOwner(), nonVmBundle, null, null,
        false, false, null, new HashMap<String, String>());
    Assert.assertNotNull(subscription);
    SubscriptionHandle subscriptionHandle = new SubscriptionHandle(subscription, "", subscription.getServiceInstance()
        .getUuid(), "VirtualMachine", user, State.ACTIVE, null, null, "TestVM");
    subscription.addHandle(subscriptionHandle);
    subscriptionDAO.merge(subscription);
    prepareMockForTerminateAndCancelSubscriptionDetails(user);
    Subscription sub = controller.cancelSubscription(subscription.getParam(), map);
    Assert.assertNotNull(sub);
    Assert.assertEquals(sub.getState().name(), new String("CANCELED"));
    Assert.assertEquals(State.TERMINATED, sub.getHandle().getState());
  }

  @Test
  public void testShowHistory() {
    session = new MockHttpSession();
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setState(com.vmops.model.Tenant.State.ACTIVE);
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = createTestUserInTenant(tenant);
    tenantService.setOwner(tenant, user);
    List<ProductBundle> bundles = productBundleService.listProductBundles(0, 0);
    ProductBundle nonVmBundle = null;
    for (ProductBundle bundle : bundles) {
      if (!bundle.getResourceType().getResourceTypeName().equals("VirtualMachine")) {
        nonVmBundle = bundle;
        break;
      }
    }
    tenant.getOwner().setEnabled(true);
    Subscription subscription = subscriptionService.createSubscription(tenant.getOwner(), nonVmBundle, null, null,
        false, false, null, new HashMap<String, String>());
    Assert.assertNotNull(subscription);
    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    request.setAttribute("effectiveTenant", tenant);
    session.setAttribute("makepayment_status", "false");
    String view = controller.showHistory(tenant, tenant.getParam(), user.getParam(), "1", session, map, request);
    Assert.assertNotNull(view);
    Assert.assertTrue(map.containsAttribute("isPayAsYouGoChosen"));
    Assert.assertEquals(false, view.contentEquals("showUserProfile"));
    Assert.assertEquals(false, view.contentEquals("userHasCloudServiceAccount"));
    Assert.assertEquals(map.get("currentUser"), getRootUser());
    Assert.assertNotNull(map.get("billingActivities"));
    Assert.assertNotNull(map.get("users"));
    Assert.assertEquals(map.get("showUsers"), true);
    Assert.assertNotNull(map.get("tenant"));
    Assert.assertNotNull(map.get("statusMessage"));
    Assert.assertNull(map.get("makepayment_status"));

  }

  @Test
  public void testShowHistory1() {
    session = new MockHttpSession();

    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setState(com.vmops.model.Tenant.State.ACTIVE);
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = createTestUserInTenant(tenant);
    tenantService.setOwner(tenant, user);
    List<ProductBundle> bundles = productBundleService.listProductBundles(0, 0);
    ProductBundle nonVmBundle = null;
    for (ProductBundle bundle : bundles) {
      if (!bundle.getResourceType().getResourceTypeName().equals("VirtualMachine")) {
        nonVmBundle = bundle;
        break;
      }
    }
    tenant.getOwner().setEnabled(true);
    Subscription subscription = subscriptionService.createSubscription(tenant.getOwner(), nonVmBundle, null, null,
        false, false, null, new HashMap<String, String>());
    Assert.assertNotNull(subscription);
    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    request.setAttribute("effectiveTenant", tenant);
    session.setAttribute("makepayment_status", "true");
    String view = controller.showHistory(tenant, tenant.getParam(), user.getParam(), "1", session, map, request);
    Assert.assertNotNull(view);
    Assert.assertEquals(false, view.contentEquals("showUserProfile"));
    Assert.assertEquals(false, view.contentEquals("userHasCloudServiceAccount"));
    Assert.assertEquals(map.get("currentUser"), getRootUser());
    Assert.assertNotNull(map.get("billingActivities"));
    Assert.assertNotNull(map.get("users"));
    Assert.assertEquals(map.get("showUsers"), true);
    Assert.assertNotNull(map.get("tenant"));
    Assert.assertNotNull(map.get("statusMessage"));
    Assert.assertNull(map.get("makepayment_status"));

  }

  @Test
  public void testShowPaymentHistory() {
    session = new MockHttpSession();

    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setState(com.vmops.model.Tenant.State.ACTIVE);
    tenant.setAccountType(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = createTestUserInTenant(tenant);
    tenantService.setOwner(tenant, user);
    List<ProductBundle> bundles = productBundleService.listProductBundles(0, 0);
    ProductBundle nonVmBundle = null;
    for (ProductBundle bundle : bundles) {
      if (!bundle.getResourceType().getResourceTypeName().equals("VirtualMachine")) {
        nonVmBundle = bundle;
        break;
      }
    }
    tenant.getOwner().setEnabled(true);
    Subscription subscription = subscriptionService.createSubscription(tenant.getOwner(), nonVmBundle, null, null,
        false, false, null, new HashMap<String, String>());
    Assert.assertNotNull(subscription);
    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    request.setAttribute("effectiveTenant", tenant);
    session.setAttribute("makepayment_status", "true");
    String view = controller.showPaymentHistory(tenant, tenant.getParam(), "1", session, request, map);
    Assert.assertNotNull(view);

    Assert.assertNotNull(map.get("userHasCloudServiceAccount"));
    Assert.assertTrue(map.containsAttribute("isPayAsYouGoChosen"));
    Assert.assertNotNull(map.get("showUserProfile"));
    Assert.assertNotNull(map.get("errorMsg"));
    Assert.assertNotNull(map.get("tenant"));

    Assert.assertNotNull(map.get("errorMsg"));
    Assert.assertNotNull(map.get("salesLedgerRecords"));
    Assert.assertNotNull(map.get("enable_next"));
    Assert.assertNotNull(map.get("current_page"));

  }

  @Test
  public void testViewBillingActivity() {

    Tenant tenant1 = tenantDAO.find(20L);
    AccountStatement accountStatement = accountStatementDAO.find(41000L);
    asUser(tenant1.getOwner());

    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    request.setAttribute("effectiveTenant", tenant1);
    controller.viewBillingActivity(accountStatement.getUuid(), "Invoice", tenant1.getOwner().getParam(), map);

  }

  @Test
  public void testViewSalesLedgerRecord() {
    Tenant tenant = tenantDAO.find(25L);

    BigDecimal creditDecimal = billingPostProcessor.setScaleByCurrency(

    new BigDecimal("100"), tenant.getCurrency());

    SalesLedgerRecord creditSLR = billingAdminService

    .addPaymentOrCredit(tenant, creditDecimal, Type.SERVICE_CREDIT,

    "Payment Done", null);

    String view = controller.viewSalesLedgerRecord(creditSLR.getUuid(), map);
    Assert.assertNotNull(view);
    Assert.assertEquals(view, new String("billing.view.billing.paymentactivity"));
    Assert.assertEquals(map.get("salesLedgerRecord"), creditSLR);
    Assert.assertEquals(map.get("tenant"), tenant);
    Assert.assertEquals(Boolean.FALSE, map.get("isInitialDeposit"));
  }

  @Test
  public void testViewInitialDeposit() {
    Tenant tenant = tenantDAO.find(25L);
    String view = controller.viewInitialDeposit(tenant.getUuid(), map);
    Assert.assertNotNull(view);
    Assert.assertEquals(view, new String("billing.view.billing.paymentactivity"));
    Assert.assertEquals(map.get("initialDeposit"), tenantService.getDepositRecordByTenant(tenant));
    Assert.assertEquals(map.get("tenant"), tenant);
    Assert.assertEquals(Boolean.TRUE, map.get("isInitialDeposit"));
  }

  @Test
  public void testViewShowPaymentHistory() {

    Tenant tenant = tenantDAO.find(25L);
    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    request.setAttribute("effectiveTenant", tenant);
    String makePayment = controller.makePayment(new BigDecimal("100"), "Payment Memo", tenant.getParam(), session,
        response);
    controller.showPaymentHistory(tenant, tenant.getParam(), "1", session, request, map);
    Assert.assertEquals(map.get("userHasCloudServiceAccount"), Boolean.valueOf(false));
    Assert.assertEquals(map.get("showUserProfile"), Boolean.valueOf(true));
    Assert.assertEquals(map.get("errorMsg"), "");
    Assert.assertEquals(map.get("tenant"), tenant);
    Assert.assertNotNull(map.get("salesLedgerRecords"));
    makePayment = controller.makePayment(new BigDecimal("0"), "Payment Memo", tenant.getParam(), session, response);
    Assert.assertEquals(makePayment, new String("failed"));

  }

  @Test
  public void testRecordDeposit2() throws Exception {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    DepositRecordForm recordForm = new DepositRecordForm();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    recordForm.setReceivedOn(sdf.format(getDaysFromNow(-1)));
    recordForm.setAmount("1000.00");
    BindingResult result = validate(recordForm);
    Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
    String returnForm = controller.recordDeposit(tenant.getParam(), tenant, map, request);
    Assert.assertNotNull(returnForm);
    Assert.assertEquals(200, response.getStatus());
    tenantDAO.flush();
    tenantDAO.clear();
    Tenant found = tenantDAO.find(tenant.getId());
    Assert.assertEquals(tenantService.getDepositRecordByTenant(tenant), found.getDeposit());
  }

  @Test
  public void testMakePayment() {

    Tenant tenant = tenantDAO.find(25L);
    String makePayment = controller.makePayment(new BigDecimal("100"), "Payment Memo", tenant.getParam(), session,
        response);
    Assert.assertEquals("success", makePayment);
    Assert.assertNotNull("response");

  }

  @Test
  public void testMakePaymentFail() {

    Tenant tenant = tenantDAO.find(25L);
    String makePayment = controller.makePayment(new BigDecimal("-100"), "Payment Memo", tenant.getParam(), session,
        response);
    Assert.assertEquals("failed", makePayment);
    Assert.assertNotNull("response");
  }

  @Test
  public void testRecordPayment() {
    Tenant tenant = tenantDAO.find(25L);
    String recordPayment = controller.recordPayment(new BigDecimal("100"), "Payment Memo", tenant.getParam(), response,
        map);
    Assert.assertEquals("success", recordPayment);
    Assert.assertNotNull("response");
  }

  @Test
  public void testRecordPaymentFail() {
    Tenant tenant = tenantDAO.find(25L);
    String recordPayment = controller.recordPayment(new BigDecimal("-100"), "Payment Memo", tenant.getParam(),
        response, map);
    Assert.assertEquals("failed", recordPayment);
    Assert.assertNotNull("response");
  }

  @Test
  public void testShowRecordDeposit() throws Exception {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    DepositRecordForm recordForm = new DepositRecordForm();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    recordForm.setReceivedOn(sdf.format(getDaysFromNow(-1)));
    recordForm.setAmount("1000.00");
    BindingResult result = validate(recordForm);
    Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
    DepositRecordForm returnForm = controller.recordDeposit(tenant.getParam(), recordForm, result, map, response);
    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    request.setAttribute("effectiveTenant", tenant);
    controller.showRecordDeposit(tenant.getParam(), tenant, map, request);
    Assert.assertTrue(map.containsAttribute("isPayAsYouGoChosen"));
    Assert.assertNotNull(returnForm);
    Assert.assertEquals(200, response.getStatus());
    tenantDAO.flush();
    tenantDAO.clear();
    Tenant found = tenantDAO.find(tenant.getId());
    Assert.assertEquals(tenantService.getDepositRecordByTenant(tenant), found.getDeposit());
    Assert.assertEquals(map.get("show_deposit_record"), true);
    Assert.assertNotNull(map.get("depositRecord"));
  }

  @Test
  public void testShowCreditCard() {

    Tenant tenant = tenantDAO.find(25L);
    String viewCreditCardDetails = controller.showCreditCard(tenant, tenant.getParam(), map, request);
    Assert.assertNull(map.get("showUserProfile"));
    Assert.assertEquals(map.get("tenant"), tenant);
    Assert.assertEquals(map.get("showChangeAccountType"), true);
    Assert.assertEquals(map.get("errorMsg"), null);
    Assert.assertNotNull(map.get("billingInfo"));
    Assert.assertEquals(map.get("showMessagePendingConversion"), false);
    BillingInfoForm billingInfoForm = (BillingInfoForm) map.get("billingInfo");
    Assert.assertNotNull(billingInfoForm.getCountryList());
    Assert.assertEquals(billingInfoForm.getCreditCard(), billingService.getCreditCard(tenant));
    Assert.assertEquals(viewCreditCardDetails, new String("billing.showCreditCardDetails"));

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testChangeAccountType() throws IOException {

    Tenant tenant = tenantDAO.find(25L);
    String resuString = controller.changeAccountType(tenant.getParam(), map);
    Assert.assertNotNull(resuString);
    Assert.assertEquals("billing.change.account.type", resuString);
    List<AccountType> targetAccountTypes = (List<AccountType>) map.get("targetAccountTypes");
    Assert.assertEquals(1, targetAccountTypes.size());
  }

  @Test
  public void testChangeAccountType1() throws IOException {

    Tenant tenant = tenantDAO.find(25L);
    CreditCard cc = new CreditCard("VISA", "4111111111111111", 3, 2015, "123", tenant.getOwner().getFirstName(),
        VALID_BILLING_ADDRESS);
    SetAccountTypeForm form = new SetAccountTypeForm(tenant);
    form.setAccountTypeName(accountTypeDAO.find(4L).getName());
    form.setBillingAddress(VALID_BILLING_ADDRESS);
    form.setInitialPayment(BigDecimal.TEN);
    form.setCreditCard(cc);
    ModelMap resultMap = controller.changeAccountType(tenant.getParam(), form, request, response, map);
    Assert.assertNotNull(resultMap);
    String result = (String) resultMap.get("redirecturl");
    Assert.assertNotNull(result);
    Assert.assertEquals("/portal/portal/tenants/editcurrent", result);

  }

  @Test
  @ExpectedException(InvalidAjaxRequestException.class)
  public void testChangeAccountTypeSecondTime() throws IOException {

    Tenant tenant = tenantDAO.find(25L);
    CreditCard cc = new CreditCard("VISA", "4111111111111111", 3, 2015, "123", tenant.getOwner().getFirstName(),
        VALID_BILLING_ADDRESS);
    SetAccountTypeForm form = new SetAccountTypeForm(tenant);
    form.setAccountTypeName(accountTypeDAO.find(4L).getName());
    form.setBillingAddress(VALID_BILLING_ADDRESS);
    form.setInitialPayment(BigDecimal.TEN);
    form.setCreditCard(cc);
    ModelMap resultMap = controller.changeAccountType(tenant.getParam(), form, request, response, map);
    Assert.assertNotNull(resultMap);
    String result = (String) resultMap.get("redirecturl");
    Assert.assertNotNull(result);
    Assert.assertEquals("/portal/portal/tenants/editcurrent", result);

    controller.changeAccountType(tenant.getParam(), form, request, response, map);

  }

  @Test
  public void testEditCreditCard() throws IOException {

    paymentGatewayService = new MockPaymentGatewayService();
    Tenant tenant = tenantDAO.find(3L);
    Address billingAddress = paymentGatewayService.getBillingAddress(tenant);
    CreditCard creditCard = paymentGatewayService.getCreditCard(tenant);
    creditCard.setCreditCardNumber("4111111111111444");
    creditCard.setFirstNameOnCard("FName");
    creditCard.setLastNameOnCard("LName");
    BillingInfoForm form = new BillingInfoForm(tenant, billingAddress, creditCard);
    form.setAction("launchvm");
    ModelMap obtainedMap = controller.editCreditCard(tenant.getParam(), form, response, request, map);
    Assert.assertNotNull(obtainedMap);
    Assert.assertTrue(map.containsKey("redirecturl"));
    String redirectURL = (String) map.get("redirecturl");
    Assert.assertEquals("/portal/portal/tenants/editcurrent?action=showcreditcardtab&tenant=" + tenant.getParam(),
        redirectURL);
    Tenant obtainedTenant = (Tenant) map.get("tenant");
    CreditCard newCreditCard = paymentGatewayService.getCreditCard(obtainedTenant);
    Assert.assertEquals("4111111111111444", newCreditCard.getCreditCardNumber());
    Assert.assertEquals("FName", newCreditCard.getFirstNameOnCard());
    Assert.assertEquals("LName", newCreditCard.getLastNameOnCard());
  }

  @Test
  public void testChargeBack() {

    Tenant tenant = tenantDAO.find(25L);
    String result = controller.chargeBack("8c4732b3-b319-4aef-ad46-26f9b187cf12", tenant.getParam(), request, response,
        map);
    Assert.assertEquals("success", result);
    Assert.assertNotNull("response");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testUsageInvoice() throws ConnectorManagementServiceException {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setState(com.vmops.model.Tenant.State.ACTIVE);
    User user = createTestUserInTenant(tenant);
    user.setEnabled(true);
    userService.update(user);
    tenantService.setOwner(tenant, user);
    asUser(tenant.getOwner());

    Subscription subscription = subscriptionService.createSubscription(tenant.getOwner(),
        productBundleService.getProductBundleById(2L), null, null, false, false, null, new HashMap<String, String>());
    Assert.assertNotNull(subscription);

    String view = controller.usageBilling(tenant, null, null, "1", null, null, map, request);
    Assert.assertEquals("billing.usageBilling", view);
    Assert.assertTrue(map.containsAttribute("isPayAsYouGoChosen"));
    Assert.assertEquals(true, map.containsAttribute("userHasCloudServiceAccount"));
    Assert.assertEquals(false, map.containsAttribute("showUserProfile"));
    Assert.assertEquals(true, map.containsAttribute("accountStatements"));
    Assert.assertEquals(true, map.containsAttribute("accountStatementUuid"));
    Assert.assertEquals(true, map.containsAttribute("accountStatementState"));
    Assert.assertEquals(true, map.containsAttribute("payments"));
    Assert.assertEquals(true, map.containsAttribute("creditsIssued"));
    Assert.assertEquals(true, map.containsAttribute("bigPaymentsSum"));
    Assert.assertEquals(map.get("isSystemProviderUser"), new String("N"));
    Assert.assertEquals(map.get("userHasCloudServiceAccount"), Boolean.valueOf(false));
    List<AccountStatement> accountStatements = (List<AccountStatement>) map.get("accountStatements");
    Assert.assertEquals(accountStatements.size(), Integer.parseInt("1"));
    Assert.assertEquals(map.get("accountStatementUuid"), accountStatements.get(0).getUuid());
    Assert.assertEquals(map.get("accountStatementState"), accountStatements.get(0).getState().name());
    Assert.assertEquals(map.get("newBigAmount"), BigDecimal.ZERO);
    List<InvoiceItem> items = new ArrayList<InvoiceItem>();
    InvoiceItem invoiceItem = new InvoiceItem();
    invoiceItem.setDescription("Testing");
    items.add(invoiceItem);

  }

  @Test
  public void testEditCreditCardWithNullCreditCardNumber() throws IOException {

    paymentGatewayService = new MockPaymentGatewayService();
    Tenant tenant = tenantDAO.find(3L);
    Address billingAddress = paymentGatewayService.getBillingAddress(tenant);
    CreditCard creditCard = paymentGatewayService.getCreditCard(tenant);
    creditCard.setCreditCardNumber(null);
    creditCard.setFirstNameOnCard("FName");
    creditCard.setLastNameOnCard("LName");
    BillingInfoForm form = new BillingInfoForm(tenant, billingAddress, creditCard);
    form.setAction("launchvm");
    ModelMap obtainedMap = controller.editCreditCard(tenant.getParam(), form, response, request, map);
    Assert.assertNotNull(obtainedMap);
    Assert.assertTrue(map.containsKey("redirecturl"));
    String redirectURL = (String) map.get("redirecturl");
    Assert.assertEquals("/portal/portal/tenants/editcurrent?action=showcreditcardtab&tenant=" + tenant.getParam(),
        redirectURL);
    Tenant obtainedTenant = (Tenant) map.get("tenant");
    CreditCard newCreditCard = paymentGatewayService.getCreditCard(obtainedTenant);
    Assert.assertNull(newCreditCard.getCreditCardNumber());
    Assert.assertEquals("FName", newCreditCard.getFirstNameOnCard());
    Assert.assertEquals("LName", newCreditCard.getLastNameOnCard());
  }
}