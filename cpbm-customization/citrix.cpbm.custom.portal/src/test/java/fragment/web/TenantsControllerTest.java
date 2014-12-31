/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;
import web.support.MockSessionStatus;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.core.workflow.event.TriggerTransaction;
import com.citrix.cpbm.core.workflow.model.BusinessTransaction;
import com.citrix.cpbm.core.workflow.model.CloudServiceActivationTransaction;
import com.citrix.cpbm.core.workflow.service.BusinessTransactionService;
import com.citrix.cpbm.platform.bootstrap.service.BootstrapActivator;
import com.citrix.cpbm.platform.spi.AccountLifecycleHandler;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.citrix.cpbm.platform.spi.UserLifecycleHandler;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.citrix.cpbm.portal.forms.TenantForm;
import com.citrix.cpbm.portal.fragment.controllers.ChannelController;
import com.citrix.cpbm.portal.fragment.controllers.TenantsController;
import com.citrix.cpbm.workflow.persistence.BusinessTransactionDAO;
import com.vmops.event.PortalEvent;
import com.vmops.event.ServiceAccountRegisterEvent;
import com.vmops.event.VerifyAlertEmailRequest;
import com.vmops.event.VerifyEmailRequest;
import com.vmops.internal.service.EventService;
import com.vmops.internal.service.PaymentGatewayService;
import com.vmops.model.AccountControlServiceConfigMetadata;
import com.vmops.model.AccountType;
import com.vmops.model.AccountTypeCreditExposure;
import com.vmops.model.Address;
import com.vmops.model.CampaignPromotion;
import com.vmops.model.CampaignPromotionsInChannels;
import com.vmops.model.Channel;
import com.vmops.model.Configuration;
import com.vmops.model.Country;
import com.vmops.model.CreditCard;
import com.vmops.model.CurrencyValue;
import com.vmops.model.Event;
import com.vmops.model.Event.Category;
import com.vmops.model.Event.Scope;
import com.vmops.model.Event.Severity;
import com.vmops.model.Event.Source;
import com.vmops.model.Profile;
import com.vmops.model.PromotionSignup;
import com.vmops.model.PromotionToken;
import com.vmops.model.Service;
import com.vmops.model.ServiceInstance;
import com.vmops.model.SpendAlertSubscription;
import com.vmops.model.Tenant;
import com.vmops.model.Tenant.State;
import com.vmops.model.TenantChange;
import com.vmops.model.TenantChange.Action;
import com.vmops.model.TenantPromotion;
import com.vmops.model.User;
import com.vmops.model.UserAlertPreferences;
import com.vmops.model.UserAlertPreferences.AlertType;
import com.vmops.model.billing.PaymentTransaction;
import com.vmops.persistence.AccountTypeDAO;
import com.vmops.persistence.AddressDAO;
import com.vmops.persistence.CampaignPromotionDAO;
import com.vmops.persistence.CountryDAO;
import com.vmops.persistence.EventDAO;
import com.vmops.persistence.PendingChangeDAO;
import com.vmops.persistence.PromotionTokenDAO;
import com.vmops.persistence.SpendAlertSubscriptionDAO;
import com.vmops.persistence.UserAlertPreferencesDAO;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.AccountTypeService;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.PromotionService;
import com.vmops.service.ResourceLimitService;
import com.vmops.service.TenantService;
import com.vmops.service.UserAlertPreferencesService;
import com.vmops.service.exceptions.AjaxFormValidationException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.NoManualRegistrationAccountTypeException;
import com.vmops.service.exceptions.TenantStateChangeFailedException;
import com.vmops.utils.DateUtils;
import com.vmops.web.forms.BillingInfoForm;
import com.vmops.web.forms.CustomAlertForm;
import com.vmops.web.forms.SearchForm;
import com.vmops.web.forms.SetAccountTypeForm;
import com.vmops.web.forms.TenantLogoForm;
import com.vmops.web.forms.UserAlertEmailForm;
import com.vmops.web.interceptors.UserContextInterceptor;
import common.MockCloudInstance;
import common.TestUtilityService;

import fragment.web.util.WebTestUtils;

public class TenantsControllerTest extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  private MockSessionStatus status;

  private MockHttpServletResponse response;

  private MockHttpServletRequest request;

  @Autowired
  private CampaignPromotionDAO cmpdao;

  @Autowired
  private PromotionTokenDAO tokendao;

  @Autowired
  private TenantsController controller;

  @Autowired
  private ChannelController channelController;

  @Autowired
  private PendingChangeDAO pendingChangeDAO;

  @Autowired
  private TenantService tenantService;

  @Autowired
  private PromotionService promotionService;

  @Autowired
  private UserAlertPreferencesService userAlertPreferenceService;

  @Autowired
  private AddressDAO addressDAO;

  @Autowired
  private AccountTypeService acctypeService;

  @Autowired
  private ResourceLimitService service;

  @Autowired
  private UserAlertPreferencesDAO userAlertDAO;

  @Autowired
  private SpendAlertSubscriptionDAO spendAlertDAO;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private AccountTypeDAO accTypeDAO;

  @Autowired
  private CountryDAO countryDAO;

  @Autowired
  private BusinessTransactionDAO businessTransactionDAO;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private EventService eventService;

  private final BootstrapActivator bootstrapActivator = new BootstrapActivator();

  private static boolean isMockInstanceCreated = false;

  @Autowired
  private PaymentGatewayService paymentGatewayService;

  private PaymentGatewayService ossConnector = null;

  @Autowired
  private BusinessTransactionService businessTransactionService;

  @Autowired
  private EventDAO eventDAO;

  @Autowired
  private TestUtilityService testUtilityService;

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    status = new MockSessionStatus();
    response = new MockHttpServletResponse();
    request = new MockHttpServletRequest();
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

  /*
   * (non-Javadoc)
   * @see common.ServiceTestsBase#prepareMock()
   */
  @Override
  public void prepareMock() {

    MockCloudInstance instance = getMockCloudInstance();
    CloudConnector connector = instance.getCloudConnector();

    AccountLifecycleHandler alh = instance.getAccountLifecycleHandler();
    EasyMock.expect(alh.getControls(EasyMock.anyObject(Tenant.class))).andReturn(getResourceMap()).anyTimes();
    alh.setControls(EasyMock.anyObject(Tenant.class), EasyMock.<Map<String, String>> anyObject());
    EasyMock.expectLastCall().anyTimes();

    alh.update(EasyMock.anyObject(Tenant.class));
    EasyMock.expectLastCall().anyTimes();

    alh.terminate(EasyMock.anyObject(Tenant.class));
    EasyMock.expectLastCall().anyTimes();

    alh.register(EasyMock.anyObject(Tenant.class), EasyMock.<Map<String, String>> anyObject());
    EasyMock.expectLastCall().anyTimes();

    EasyMock.replay(alh);

    UserLifecycleHandler ulh = instance.getUserLifecycleHandler();
    EasyMock.expect(ulh.getControls(EasyMock.anyObject(User.class))).andReturn(getResourceMap()).anyTimes();
    ulh.setControls(EasyMock.anyObject(User.class), EasyMock.<Map<String, String>> anyObject());
    ulh.update(EasyMock.anyObject(User.class));
    EasyMock.expectLastCall().anyTimes();

    ulh.setControls(EasyMock.anyObject(User.class), EasyMock.<Map<String, String>> anyObject());

    EasyMock.replay(ulh);

    EasyMock.reset(connector);
    EasyMock.expect(connector.getAccountLifeCycleHandler()).andReturn(alh).anyTimes();
    EasyMock.expect(connector.getUserLifeCycleHandler()).andReturn(ulh).anyTimes();

    EasyMock.expect(connector.getServiceInstanceUUID()).andReturn("003fa8ee-fba3-467f-a517-ed806dae8a80").anyTimes();
    EasyMock.replay(connector);

    ossConnector = EasyMock.createMock(PaymentGatewayService.class);
    EasyMock.expect(ossConnector.getAccountLifeCycleHandler()).andReturn(mockAccountLifecycleHandler).anyTimes();
    EasyMock.expect(ossConnector.getUserLifeCycleHandler()).andReturn(mockUserLifecycleHandler).anyTimes();
    final Capture<BigDecimal> amount = new Capture<BigDecimal>();
    EasyMock.expect(ossConnector.authorize(EasyMock.anyObject(Tenant.class), EasyMock.capture(amount)))
        .andAnswer(new IAnswer<PaymentTransaction>() {

          @Override
          public PaymentTransaction answer() throws Exception {
            return new PaymentTransaction(new Tenant(), 0, com.vmops.model.billing.PaymentTransaction.State.COMPLETED,
                com.vmops.model.billing.PaymentTransaction.Type.CAPTURE);
          }
        }).anyTimes();
    EasyMock.replay(ossConnector);

  }

  private SpendAlertSubscription createSpendAlertSubscription(Tenant tenant) {
    SpendAlertSubscription spendAlert = new SpendAlertSubscription();
    spendAlert.setAccountHolder(tenant);
    spendAlert.setData("TEST");
    spendAlert.setUser(userService.getUserByParam("id", "4", false));
    spendAlert.setPercentage(BigDecimal.valueOf(100));
    spendAlert.setSubscriptionType(1);
    spendAlertDAO.save(spendAlert);
    return spendAlert;
  }

  @Test
  public void testValidTrialCode() throws Exception {
    String validateResponse = controller.validatePromotionCodeforTenant("INVALID", "", "");
    Assert.assertEquals("false", validateResponse);

    PromotionSignup promotionSignup = new PromotionSignup("test" + random.nextInt(), "Citrix",
        "PromotionSignUp@citrix.com");
    promotionSignup.setCreateBy(getRootUser());
    promotionSignup.setCurrency(Currency.getInstance("USD"));
    promotionSignup.setPhone("9999999999");

    CampaignPromotion campaignPromotion = new CampaignPromotion();
    campaignPromotion.setCode("USD" + random.nextInt());
    campaignPromotion.setCreateBy(getRootUser());
    campaignPromotion.setUpdateBy(getRootUser());
    campaignPromotion.setTrial(true);
    campaignPromotion.setCampaignPromotionsInChannels(new HashSet<CampaignPromotionsInChannels>());
    campaignPromotion = cmpdao.save(campaignPromotion);

    PromotionToken promotionToken = new PromotionToken(campaignPromotion, "TESTPROMOCODE");
    promotionToken.setCreateBy(getRootUser());
    tokendao.save(promotionToken);

    promotionSignup.setPromotionToken(promotionToken);

    String[] currencyValueList = {
        "USD", "EUR"
    };
    Channel newChannel = channelController.createChannel(WebTestUtils.createChannelForm("ChannelForTrialAccount",
        "ToTestValidPromoCode", "PROMOCODE", null, "en_US", "Asia/Kolkata", currencyValueList, map,
        new MockHttpServletResponse()), null, map, new MockHttpServletResponse());
    Set<CampaignPromotionsInChannels> campPromoChannelSet = new HashSet<CampaignPromotionsInChannels>();
    campPromoChannelSet.add(new CampaignPromotionsInChannels(campaignPromotion, newChannel));
    campaignPromotion.setCampaignPromotionsInChannels(campPromoChannelSet);
    cmpdao.save(campaignPromotion);
    validateResponse = controller.validatePromotionCodeforTenant("TESTPROMOCODE", newChannel.getParam(), "");
    Assert.assertEquals("true", validateResponse);

  }

  @Test
  public void testRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class<? extends TenantsController> controllerClass = controller.getClass();
    Method expected = locateMethod(controllerClass, "show", new Class[] {
        String.class, ModelMap.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/viewtenant"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "edit", new Class[] {
        String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/edit"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "edit", new Class[] {
        TenantForm.class, BindingResult.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/edit"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "editCurrentTenant", new Class[] {
        String.class, String.class, String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/editcurrent"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "searchlist", new Class[] {
        ModelMap.class, String.class, String.class, String.class, String.class, String.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/searchlist"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "list", new Class[] {
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/list"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "create", new Class[] {
        ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/new"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "create", new Class[] {
        TenantForm.class, BindingResult.class, ModelMap.class, SessionStatus.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "changeState", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/1/changeState"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "changeState", new Class[] {
        String.class, State.class, String.class, HttpServletResponse.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/1/changeState"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "issueCredit", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/issueCredit"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "issueCredit", new Class[] {
        String.class, String.class, String.class, HttpServletRequest.class, HttpServletResponse.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/1/add_credit"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "search", new Class[] {
        SearchForm.class, BindingResult.class, String.class, String.class, String.class, String.class, String.class,
        ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/search"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "getAccountType", new Class[] {
      String.class,String.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/get_account_type"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "listNotifications", new Class[] {
        Tenant.class, String.class, String.class, String.class, String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/notifications"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "viewNotification", new Class[] {
        Tenant.class, String.class, String.class, String.class, String.class, String.class, ModelMap.class,
        HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/view_notification"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "viewAlertsDeliveryOptions", new Class[] {
        String.class, String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/alert_prefs"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "saveAlertsDeliveryOptions", new Class[] {
        UserAlertEmailForm.class, BindingResult.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/alert_prefs"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "deleteAlertsDeliveryOptions", new Class[] {
        Long.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/alert_prefs/1/delete"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "listSubscriptions", new Class[] {
        Tenant.class, String.class, String.class, String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/alerts"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "viewAlert", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/alerts/view"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "createSpendAlertSubscription", new Class[] {
        Tenant.class, String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/alerts/new"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "createSpendAlertSubscription", new Class[] {
        Tenant.class, String.class, CustomAlertForm.class, BindingResult.class, ModelMap.class,
        HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/alerts/new"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "removeSubscription", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/alerts/remove"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "editSpendAlertSubscription", new Class[] {
        String.class, Tenant.class, String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/alerts/edit"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "editCurrentTenantLogo", new Class[] {
      ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/editcurrentlogo"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "editTenantLogo", new Class[] {
        String.class, TenantLogoForm.class, BindingResult.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/123/editlogo"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "editSpendAlertSubscription", new Class[] {
        String.class, String.class, CustomAlertForm.class, BindingResult.class, ModelMap.class,
        HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/alerts/edit"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "setAccountBudget", new Class[] {
        Tenant.class, String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/set_account_budget"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "setAccountBudget", new Class[] {
        Tenant.class, String.class, TenantForm.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/set_account_budget"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "delete", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/1/delete"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "listStateChanges", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/stateChanges"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "listAccountLimits", new Class[] {
        ModelMap.class, String.class, String.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/list_account_limits"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "editAccountlimits", new Class[] {
        String.class, String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/tenants/edit_account_limits"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "editAccountlimits", new Class[] {
        String.class, String.class, String.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/tenants/edit_account_limits"));
    Assert.assertEquals(expected, handler);

  }

  @Test
  public void testTenantShow() throws Exception {
    Tenant expected = tenantDAO.findAll(null).get(0);

    String view = controller.show(expected.getUuid(), map);

    Assert.assertEquals("tenant.view", view);
    Assert.assertTrue(map.containsKey("creditBalance"));
    Assert.assertTrue(map.containsKey("pendingActions"));

    Assert.assertTrue(map.containsKey("tenant"));
    Tenant found = (Tenant) map.get("tenant");
    Assert.assertEquals(expected, found);
  }

  @Test
  public void testTenantActivationNoOwner() throws Exception {
    asRoot();
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    completeBusinessTransactionsForTenant(tenant);
    controller.changeState(tenant.getParam(), State.ACTIVE, "memo", response);
  }

  @Test
  public void testTenantActivationNoCurrency() throws Exception {
    Tenant tenant = new Tenant("New Tenant", accountTypeDAO.getDefaultRegistrationAccountType(), null, randomAddress(),
        true, null, null);
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    for (CurrencyValue currencyValue : activeCurrencies) {
      tenant.setCurrency(currencyValue);
      break;
    }
    tenantService.createAccount(tenant, getUser(), null, null);
    completeBusinessTransactionsForTenant(tenant);
    controller.changeState(tenant.getParam(), State.ACTIVE, "memo", response);
  }

  public User getUser() {
    User newUser = new User();
    newUser.setFirstName("test");
    newUser.setLastName("user");
    newUser.setEmail("test@test.com");
    newUser.setUsername(VALID_USER + random.nextInt());
    newUser.setClearPassword(VALID_PASSWORD);
    newUser.setPhone(VALID_PHONE);
    newUser.setTimeZone(VALID_TIMEZONE);
    newUser.setProfile(userProfile);
    newUser.setCreatedBy(getPortalUser());
    return newUser;
  }

  @Test(expected = TenantStateChangeFailedException.class)
  public void testTenantActivationAfterTermination() throws Exception {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenantService.setOwner(tenant, getRootUser());
    controller.changeState(tenant.getParam(), State.TERMINATED, "Terminate Account", response);
    controller.changeState(tenant.getParam(), State.ACTIVE, "Reactivate Account", response);
  }

  @Test
  public void testTenantTermination() throws Exception {
    asRoot();
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = new User("test", "user", "test@test.com", VALID_USER + random.nextInt(), VALID_PASSWORD, VALID_PHONE,
        VALID_TIMEZONE, tenant, ownerProfile, getRootUser());
    userDAO.save(user);
    tenantService.setOwner(tenant, user);
    HashMap<String, String> result = (HashMap<String, String>) controller.changeState(tenant.getParam(),
        State.TERMINATED, "memo", response);
    Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
    Assert.assertEquals("TERMINATED", result.get("state"));

    Tenant found = tenantDAO.find(tenant.getId());
    Assert.assertTrue(found.getState() == State.TERMINATED);
  }

  @Test
  public void testTenantNew() {
    asRoot();
    String view = controller.create(map, new MockHttpServletRequest());
    Tenant tenant = ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject();
    Assert.assertNotNull(tenant);
    Assert.assertEquals("tenants.new", view);
    Assert.assertEquals(config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed).equals("true"),
        Boolean.valueOf(map.get("showSuffixTextBox").toString()));
    Assert.assertEquals(config.getBooleanValue(Names.com_citrix_cpbm_portal_directory_service_enabled)
        && config.getValue(Names.com_citrix_cpbm_directory_mode).equals("pull"),
        Boolean.valueOf(map.get("showImportAdButton").toString()));
  }

  @Test
  public void testTenantNewWithSuffixBox() {
    asRoot();
    com.vmops.model.Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_username_duplicate_allowed);
    configuration.setValue("true");
    configurationService.update(configuration);

    configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_directory_service_enabled);
    configuration.setValue("true");
    configurationService.update(configuration);

    configuration = configurationService.locateConfigurationByName(Names.com_citrix_cpbm_directory_mode);
    configuration.setValue("pull");
    configurationService.update(configuration);

    String view = controller.create(map, new MockHttpServletRequest());
    Tenant tenant = ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject();
    Assert.assertNotNull(tenant);
    Assert.assertEquals("true", map.get("showSuffixTextBox"));
    Assert.assertEquals("true", map.get("showImportAdButton"), "true");
    Assert.assertEquals("tenants.new", view);
  }

  @Test
  public void testTenantNewWithNoManualActivationAccountType() {
    asRoot();
    List<AccountType> accountTypes = accountTypeDAO.getAllAccountTypes();
    for (AccountType type : accountTypes) {
      if (!type.isManualRegistrationAllowed()) {
        continue;
      }
      type.setManualRegistrationAllowed(false);
      accountTypeDAO.save(type);
    }
    try {
      controller.create(map, new MockHttpServletRequest());
      Assert.fail();
    } catch (NoManualRegistrationAccountTypeException e) {
      Assert.assertEquals("Manual Registration Not Allowed", e.getMessage());
    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void testCreateTenant() throws Exception {
    asRoot();
    TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant()));
    form.setAccountTypeId(tenantService.getDefaultRegistrationAccountType().getId().toString());

    com.citrix.cpbm.access.Tenant newTenant = form.getTenant();
    newTenant.setName("ACME Corp");
    newTenant.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    for (CurrencyValue currencyValue : activeCurrencies) {
      newTenant.setCurrency(currencyValue);
      break;
    }

    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setFirstName("test");
    newUser.setLastName("user");
    newUser.setEmail("test@test.com");
    newUser.setUsername(VALID_USER + random.nextInt());
    newUser.getObject().setClearPassword(VALID_PASSWORD);
    newUser.setPhone(VALID_PHONE);
    newUser.setTimeZone(VALID_TIMEZONE);
    newUser.setProfile(userProfile);
    newUser.getObject().setCreatedBy(getPortalUser());

    newUser.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    // test add for secondary address
    form.setAllowSecondary(true);
    form.setSecondaryAddress(new Address("steve", "creek", "cupertino", "CHAN", "95014", "IN"));
    form.setTrialCode("promocode");

    BindingResult result = validate(form);
    Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
    controller.create(form, result, map, status, new MockHttpServletRequest());

    Tenant found = tenantDAO.find(newTenant.getId());
    TenantPromotion tenantPromotion = promotionService.findActiveTenantPromotionAsOf(found, new Date());
    Assert.assertNotNull(tenantPromotion);
    Assert.assertNotNull(found);
    Assert.assertEquals(newTenant, found);
    Assert.assertTrue(status.isComplete());
    Assert.assertEquals(2, eventListener.getEvents().size());
    Assert.assertTrue(eventListener.getEvents().get(0).getPayload() instanceof VerifyEmailRequest);
    Assert.assertTrue(eventListener.getEvents().get(1).getPayload() instanceof TriggerTransaction);
    Assert.assertEquals(newUser.getObject(), eventListener.getEvents().get(0).getSource());

    Assert.assertNotNull(found.getAddress());
    Assert.assertNotNull(found.getSecondaryAddress());

    Assert.assertEquals(found.getAddress().getCountry(), "US");
    Assert.assertEquals(found.getSecondaryAddress().getCountry(), "IN");
  }

  @Test
  public void testCreateTenantWithSuffix() throws Exception {
    asRoot();
    TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant()));
    form.setAccountTypeId(tenantService.getDefaultRegistrationAccountType().getId().toString());
    com.citrix.cpbm.access.Tenant newTenant = form.getTenant();
    newTenant.setName("ACME Corp");
    newTenant.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    String suffix = "testSuffix";
    newTenant.getObject().setUsernameSuffix(suffix);
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    for (CurrencyValue currencyValue : activeCurrencies) {
      newTenant.setCurrency(currencyValue);
      break;
    }

    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setFirstName("test");
    newUser.setLastName("user");
    newUser.setEmail("test@test.com");
    String userName = VALID_USER + random.nextInt();
    newUser.setUsername(userName);
    newUser.getObject().setClearPassword(VALID_PASSWORD);
    newUser.setPhone(VALID_PHONE);
    newUser.getObject().setPhone(VALID_PHONE);
    newUser.setTimeZone(VALID_TIMEZONE);
    newUser.setProfile(userProfile);
    newUser.getObject().setCreatedBy(getPortalUser());

    newUser.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    // test add for secondary address
    form.setAllowSecondary(true);
    form.setSecondaryAddress(new Address("steve", "creek", "cupertino", "CHAN", "95014", "IN"));

    BindingResult result = validate(form);
    Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
    newUser.getObject().setUserName(userName + "@" + suffix);
    controller.create(form, result, map, status, new MockHttpServletRequest());

    Tenant found = userService.getUserByParam("email", newUser.getEmail(), true).getTenant();
    Assert.assertNotNull(found);
    Assert.assertEquals(userName + "@" + suffix, found.getOwner().getUsername());
    Assert.assertEquals(newTenant, found);
    Assert.assertTrue(status.isComplete());
    Assert.assertEquals(2, eventListener.getEvents().size());
    Assert.assertTrue(eventListener.getEvents().get(0).getPayload() instanceof VerifyEmailRequest);
    Assert.assertTrue(eventListener.getEvents().get(1).getPayload() instanceof TriggerTransaction);
    Assert.assertEquals(newUser.getObject(), eventListener.getEvents().get(0).getSource());

    Assert.assertNotNull(found.getAddress());
    Assert.assertNotNull(found.getSecondaryAddress());

    Assert.assertEquals(found.getAddress().getCountry(), "US");
    Assert.assertEquals(found.getSecondaryAddress().getCountry(), "IN");
  }

  @Test
  public void testTenantEdit() throws Exception {
    Tenant expected = tenantDAO.findAll(null).get(0);

    String view = controller.edit(expected.getParam(), map, request);
    Assert.assertEquals("tenant.edit", view);
    Assert.assertTrue(map.containsKey("tenantForm"));
    com.citrix.cpbm.access.Tenant found = ((TenantForm) map.get("tenantForm")).getTenant();
    Assert.assertEquals(expected, found.getObject());
  }

  @Test
  public void testTenantsUpdate() throws Exception {
    Tenant tenant = tenantDAO.find(3L);
    tenant.setName("UPDATED");
    tenant.getAddress().setCity("UPDATECITY");
    TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant));
    BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");

    HashMap<String, String> paramMap = (HashMap<String, String>) controller.edit(form, result);
    Assert.assertEquals(tenant.getParam(), paramMap.get("param"));

    Tenant found = tenantDAO.find(tenant.getId());
    Assert.assertEquals("UPDATED", found.getName());
    Assert.assertEquals("UPDATECITY", found.getAddress().getCity());
  }

  @Test
  public void testTenantsUpdateBillingSyncAddress() throws Exception {
    Tenant tenant = tenantDAO.find(3L);
    tenant.setName("UPDATED");
    tenant.getAddress().setCity("UPDATECITY");
    User user = new User("test", "user", "test@test.com", VALID_USER + random.nextInt(), VALID_PASSWORD, VALID_PHONE,
        VALID_TIMEZONE, tenant, ownerProfile, getRootUser());
    userDAO.save(user);
    tenantService.setOwner(tenant, user);
    TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant));
    form.setUser((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
    HashMap<String, String> controllerMap = (HashMap<String, String>) controller.edit(form, result);

    Assert.assertEquals(tenant.getParam(), controllerMap.get("param"));
    Tenant found = tenantDAO.find(tenant.getId());
    Assert.assertEquals("UPDATED", found.getName());
    Assert.assertEquals("UPDATECITY", found.getAddress().getCity());
  }

  @Test(expected = InvalidAjaxRequestException.class)
  public void testTenantsUpdateFail() throws Exception {
    // Test should fail as we are trying to save a tenant with name as null
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    tenant.setName(null);
    TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant));
    BindingResult result = validate(form);
    controller.edit(form, result);
  }

  @Test
  public void testVerifyAlertsDeliveryOptions() throws Exception {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = createTestUserInTenant(tenant);
    String emailAddress = "test_verify_email@test.com";

    userAlertPreferenceService.createUserAlertPreference(user, emailAddress, AlertType.USER_ALERT_EMAIL);

    List<UserAlertPreferences> prefs = userAlertPreferenceService.listAllUserAlertPreferences(user);
    Assert.assertEquals(1, prefs.size());

    UserAlertPreferences selected = null;
    for (UserAlertPreferences pref : prefs) {
      if (pref.getUser().getId() == user.getId() && pref.getAlertType() == AlertType.USER_ALERT_EMAIL) {
        selected = pref;
        break;
      }
    }
    Assert.assertEquals(emailAddress, selected.getEmailAddress());
    Assert.assertEquals(user.getId(), selected.getUser().getId());
    Assert.assertEquals(AlertType.USER_ALERT_EMAIL, selected.getAlertType());

    String view = controller.verifyAlertsDeliveryOptions(selected.getId(), map);
    Assert.assertEquals("success", view);
    Assert.assertEquals(4, eventListener.getEvents().size());
    PortalEvent verifyEvent = eventListener.getEvents().get(2);
    Assert.assertTrue(verifyEvent.getPayload() instanceof VerifyAlertEmailRequest);
    Assert.assertEquals(user.getUsername(), ((VerifyAlertEmailRequest) verifyEvent.getPayload()).getUsername());
    Assert.assertEquals(user, verifyEvent.getSource());
  }

  // private PendingChange createPendingChangeForAccountConversion(Tenant
  // tenant) {
  // PendingChange pendingChange = new PendingChange();
  // pendingChange.setChangeType(ChangeType.CONVERT_ACCOUNT_TYPE);
  // pendingChange.setTargetAccountType(accountTypeDAO.find(3L));
  // pendingChange.setCreateDate(new Date());
  // pendingChange.setTenant(tenant);
  // pendingChange.setTargetPaymentInfo("payment_info");
  // return pendingChangeDAO.save(pendingChange);
  // }

  @Test
  public void testManualActivationFlag() throws Exception {
    asRoot();
    List<AccountType> accountTypes = accountTypeDAO.getAllAccountTypes();
    for (AccountType type : accountTypes) {
      if (type.isTrial()) {
        continue;
      }
      if (!type.isManualRegistrationAllowed()) {
        continue;
      }
      type.setManualActivation(true);
      TenantForm form = new TenantForm((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(new Tenant()));
      form.setAccountTypeId(type.getId().toString());

      com.citrix.cpbm.access.Tenant newTenant = form.getTenant();
      newTenant.setName("ACME Corp");
      newTenant.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
      newTenant.setCurrency(null);
      List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
      for (CurrencyValue currencyValue : activeCurrencies) {
        newTenant.setCurrency(currencyValue);
        break;
      }

      com.citrix.cpbm.access.User newUser = form.getUser();
      newUser.setFirstName("test");
      newUser.setLastName("user");
      newUser.setEmail("test@test.com");
      newUser.setUsername(VALID_USER + random.nextInt());
      newUser.getObject().setClearPassword(VALID_PASSWORD);
      newUser.setPhone(VALID_PHONE);
      newUser.setTimeZone(VALID_TIMEZONE);
      newUser.setProfile(userProfile);
      newUser.getObject().setCreatedBy(getPortalUser());
      newUser.setAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
      BindingResult result = validate(form);
      Assert.assertEquals("validating that the form has no errors", 0, result.getErrorCount());
      controller.create(form, result, map, status, new MockHttpServletRequest());

      Assert.assertTrue(status.isComplete());
      Assert.assertEquals(2, eventListener.getEvents().size());
      Assert.assertTrue(eventListener.getEvents().get(0).getPayload() instanceof VerifyEmailRequest);
      Assert.assertTrue(eventListener.getEvents().get(1).getPayload() instanceof TriggerTransaction);
      Assert.assertEquals(newUser.getObject(), eventListener.getEvents().get(0).getSource());
      eventListener.clear();
    }
  }

  @Test
  public void testeditCurrentTenant() {
    Tenant tenant = tenantService.getTenantByParam("id", "2", false);
    tenant.getAccountType().setDepositRequired(true);
    tenant.setSecondaryAddress(VALID_BILLING_ADDRESS);
    tenant.getAccountType().setEnableSecondaryAddress(true);
    tenantService.save(tenant);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
    String editTenant = controller.editCurrentTenant(Action.CONVERTED.toString(), tenant.getParam(), "contact", map,
        request);
    Assert.assertEquals(editTenant, new String("tenants.editcurrent"));
    Assert.assertTrue(map.containsAttribute("tenant"));
    Assert.assertTrue(map.containsAttribute("tenantForm"));
    Assert.assertTrue(map.containsAttribute("showChangeAccountType"));
    Assert.assertTrue(map.containsAttribute("currenttab"));
    Assert.assertEquals(map.get("currenttab"), 2);
    Assert.assertTrue(map.containsAttribute("showMessagePendingConversion"));
    Assert.assertTrue(map.containsAttribute("show_deposit_record_message"));
    Assert.assertTrue(map.containsAttribute("paymentInfoExists"));
    Assert.assertTrue(map.containsAttribute("showCreditCardTabVar"));
    Assert.assertTrue(map.containsAttribute("billingInfo"));
    Assert.assertTrue(map.containsAttribute("setAccountTypeForm"));
    Assert.assertTrue(map.containsAttribute("targetAccountTypes"));
    Assert.assertTrue(map.containsAttribute("currentBillingStart"));
    Assert.assertTrue(map.containsAttribute("currentBillingEnd"));
    Assert.assertTrue(map.containsAttribute("BillingPeriodLength"));
    Assert.assertEquals(map.get("tenant"), tenant);
    TenantForm tenantForm = ((TenantForm) map.get("tenantForm"));
    Assert.assertEquals(tenantForm.getTenant(), tenant);
    Assert.assertEquals(tenantForm.getSecondaryAddress(), VALID_BILLING_ADDRESS);
    Assert.assertEquals(true, tenantForm.isAllowSecondary());
    Assert.assertEquals(true, map.get("showChangeAccountType"));
    Assert.assertEquals(map.get("showMessagePendingConversion"), false);
    Assert.assertEquals(map.get("showCreditCardTabVar"), true);
    BillingInfoForm billingInfoForm = ((BillingInfoForm) map.get("billingInfo"));
    Assert.assertEquals(billingInfoForm.getTenant(), tenant);
    PaymentGatewayService payGateService = (PaymentGatewayService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.PAYMENT_GATEWAY);
    if (payGateService != null) {
      CreditCard creditCard = payGateService.getCreditCard(tenant);
      Assert.assertEquals(billingInfoForm.getCreditCard(), creditCard);
    }
    Assert.assertEquals(map.get("paymentInfoExists"), true);
    SetAccountTypeForm setAccountTypeForm = ((SetAccountTypeForm) map.get("setAccountTypeForm"));
    Assert.assertEquals(setAccountTypeForm.getTenant(), tenant);
    Assert.assertNotNull(map.get("currentBillingStart"));
    Assert.assertNotNull(map.get("currentBillingEnd"));
    Assert.assertNotNull(map.get("BillingPeriodLength"));
    Assert.assertTrue(map.containsKey("transactionHistory"));
  }

  @Test
  public void testeditCurrentTenantLogo() {
    String editLogo = controller.editCurrentTenantLogo(map);
    Assert.assertEquals(editLogo, new String("tenants.editcurrentlogo"));
    Assert.assertTrue(map.containsAttribute("tenantLogoForm"));
    TenantLogoForm tenantLogoForm = ((TenantLogoForm) map.get("tenantLogoForm"));
    Assert.assertNotNull(tenantLogoForm.getTenant());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testsearchlist() {
    String searchList = controller.searchlist(map, "1", "5", null, "All", null);
    Assert.assertEquals(searchList, new String("tenant.search.list"));
    Assert.assertTrue(map.containsAttribute("tenants"));
    List<Tenant> tenants = ((List<Tenant>) map.get("tenants"));
    Assert.assertTrue(tenants.size() == 5);
    map.clear();
    controller.searchlist(map, "1", "10", null, "All", null);
    tenants = ((List<Tenant>) map.get("tenants"));
    Assert.assertTrue(tenants.size() == 10);

  }

  @Test
  public void testchangeState() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    String changeState = controller.changeState(tenant.getParam(), map);
    Assert.assertEquals(changeState, new String("tenant.change.state"));
    Assert.assertTrue(map.containsAttribute("tenant"));
    Assert.assertEquals(map.get("tenant"), tenant);
  }

  @Test
  public void testchangeStatePost() {
    Tenant tenant = new Tenant("Acme Corp " + random.nextInt(), accountTypeDAO.getAccountTypeByName("Corporate"), null,
        randomAddress(), true, currencyValueDAO.findByCurrencyCode("USD"), null);
    User owner = new User("ACME First", "ACME Last", VALID_EMAIL, "acmeuser", null, "", "", null, ownerProfile,
        getRootUser());
    tenant = tenantService.createAccount(tenant, owner, null, null);
    completeBusinessTransactionsForTenant(tenant);
    controller.changeState(tenant.getUuid(), State.ACTIVE, "", response);
  }

  @Test(expected = TenantStateChangeFailedException.class)
  public void testchangeStatePostIncompleteBusinessTransaction() {
    Tenant tenant = new Tenant("Acme Corp " + random.nextInt(), accountTypeDAO.getAccountTypeByName("Corporate"), null,
        randomAddress(), true, currencyValueDAO.findByCurrencyCode("USD"), null);
    User owner = new User("ACME First", "ACME Last", VALID_EMAIL, "acmeuser", null, "", "", null, ownerProfile,
        getRootUser());
    tenant = tenantService.createAccount(tenant, owner, null, null);
    controller.changeState(tenant.getUuid(), State.ACTIVE, "", response);
  }
  
  @Test(expected = TenantStateChangeFailedException.class)
  public void testInvalidChangeState() {
    Tenant tenant = new Tenant("Acme Corp " + random.nextInt(), accountTypeDAO.getAccountTypeByName("Corporate"), null,
        randomAddress(), true, currencyValueDAO.findByCurrencyCode("USD"), null);
    User owner = new User("ACME First", "ACME Last", VALID_EMAIL, "acmeuser", null, "", "", null, ownerProfile,
        getRootUser());
    tenant = tenantService.createAccount(tenant, owner, null, null);
    controller.changeState(tenant.getUuid(), State.LOCKED, "", response);
  }

  public TenantsControllerTest() {
    // TODO Auto-generated constructor stub
  }

  @Test
  public void testissueCredit() {
    request = new MockHttpServletRequest();
    HttpServletResponse res = new MockHttpServletResponse();

    Tenant tenant = tenantService.getTenantByParam("id", "3", false);
    String issueCredit = controller.issueCredit(tenant.getParam(), "100", "JUNIT TEST", request, res);
    Assert.assertNotNull(issueCredit);
    Assert.assertTrue(issueCredit.contains("100"));

  }

  @Test
  public void testIssueCredit() {
    Tenant tenant = tenantService.getTenantByParam("id", "3", false);
    String issueCredit = controller.issueCredit(tenant.getParam(), map);
    Assert.assertEquals(issueCredit, new String("tenant.issue.credit"));
    Assert.assertTrue(map.containsAttribute("tenant"));
  }

  @Test
  public void testhandleTenantStateChangeFailedException() {
    TenantStateChangeFailedException ex = new TenantStateChangeFailedException("JUNIT TEST");
    ModelAndView view = controller.handleTenantStateChangeFailedException(ex, null);
    Assert.assertNotNull(view);
    Assert.assertTrue(view.getModel().toString().contains("JUNIT TEST"));

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testlist() {
    request = new MockHttpServletRequest();
    AccountType accType = acctypeService.locateAccountTypeById("1");
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    String tenantlist = controller.list(tenant.getUuid(), "TEST", "1", null, "1", tenant.getParam(), null, null, map,
        request);
    Assert.assertEquals(tenantlist, new String("tenant.listing"));
    Assert.assertTrue(map.containsAttribute("accountTypes"));
    Assert.assertTrue(map.containsAttribute("showAddAccountWizard"));
    Assert.assertTrue(map.containsAttribute("filterBy"));
    Assert.assertTrue(map.containsAttribute("accountresult"));
    Assert.assertTrue(map.containsAttribute("selectedTab"));
    Assert.assertTrue(map.containsAttribute("on"));
    Assert.assertTrue(map.containsAttribute("selectedAccountType"));
    Assert.assertTrue(map.containsAttribute("tenant"));
    Assert.assertTrue(map.containsAttribute("tenants"));
    Assert.assertTrue(map.containsAttribute("creditBalance"));

    List<AccountType> accountTypes = tenantService.getAllAccountTypes();
    Assert.assertEquals(new HashSet<AccountType>((List<AccountType>) map.get("accountTypes")),
        new HashSet<AccountType>(accountTypes));
    Assert.assertEquals(map.get("showAddAccountWizard"), new String("TEST"));
    Assert.assertNull(map.get("filterBy"));
    Assert.assertEquals(map.get("accountresult"), new String("true"));
    Assert.assertEquals(map.get("selectedTab"), accType.getName());
    Assert.assertEquals(map.get("on"), new String("on"));
    Assert.assertEquals(map.get("selectedAccountType"), accType.getId().toString());
    List<Tenant> list = ((List<Tenant>) map.get("tenants"));
    Assert.assertTrue(list.size() == 1);
    Assert.assertEquals(list.get(0), tenant);

    map.clear();
    tenant.setState(State.NEW);
    tenantService.save(tenant);
    tenantlist = controller.list(tenant.getUuid(), "TEST", "1", "1", "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "0");

    map.clear();
    tenant.setState(State.ACTIVE);
    tenantService.save(tenant);
    tenantlist = controller.list(tenant.getUuid(), "TEST", "1", "2", "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "1");

    map.clear();
    tenant.setState(State.LOCKED);
    tenantService.save(tenant);
    tenantlist = controller.list(tenant.getUuid(), "TEST", "1", "3", "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "2");

    map.clear();
    tenant.setState(State.SUSPENDED);
    tenantService.save(tenant);
    tenantlist = controller.list(tenant.getUuid(), "TEST", "1", "4", "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "3");

    map.clear();
    tenant.setState(State.TERMINATED);
    tenantService.save(tenant);
    tenantlist = controller.list(tenant.getUuid(), "TEST", "1", null, "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "4");
    Assert.assertEquals(map.get("filterresult"), new String("true"));

    map.clear();
    tenant.setState(State.SUSPENDED);
    tenantService.save(tenant);
    tenantlist = controller.list(tenant.getUuid(), "TEST", "3", "1", "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "1");

    map.clear();
    tenant.setState(State.SUSPENDED);
    tenantService.save(tenant);
    tenantlist = controller.list(tenant.getUuid(), "TEST", "4", "1", "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "1");

    map.clear();
    tenant.setState(State.SUSPENDED);
    tenantService.save(tenant);
    tenantlist = controller.list(tenant.getUuid(), "TEST", "5", "1", "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "1");

    map.clear();
    tenantlist = controller.list(null, "TEST", "3", "1", "1", null, null, null, map, request);
    Assert.assertEquals(map.get("filterBy"), "1");

    map.clear();
    tenantlist = controller.list(null, "TEST", "3", "1", "1", null, null, "testAccount", map, request);
    Assert.assertEquals(map.get("selectedAccount"), "testAccount");

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testsearch() {
    request = new MockHttpServletRequest();
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    SearchForm form = new SearchForm();
    form.setfieldName(tenant.getOwner().getFirstName());
    form.setName(tenant.getName());
    form.setAccountId(tenant.getAccountId());
    HashMap<String, String> custMap = new HashMap<String, String>();
    custMap.put("name", "Acme Corp");
    form.setCustomFields(custMap);

    String search = controller.search(form, null, "1", "1", null, "1", null, map, request);
    Assert.assertEquals(search, new String("tenant.listing"));
    Assert.assertTrue(map.containsAttribute("accountTypes"));
    Assert.assertTrue(map.containsAttribute("selectedAccountType"));
    Assert.assertTrue(map.containsAttribute("filterBy"));
    Assert.assertTrue(map.containsAttribute("searchresult"));
    Assert.assertTrue(map.containsAttribute("fieldName"));
    Assert.assertTrue(map.containsAttribute("name"));
    Assert.assertTrue(map.containsAttribute("accountId"));
    Assert.assertTrue(map.containsAttribute("tenants"));
    Assert.assertTrue(map.containsAttribute("searchForm"));

    List<AccountType> accountTypes = tenantService.getAllAccountTypes();
    Assert.assertEquals(new HashSet<AccountType>((List<AccountType>) map.get("accountTypes")),
        new HashSet<AccountType>(accountTypes));
    Assert.assertEquals(map.get("selectedAccountType"), "1");
    Assert.assertEquals(map.get("filterBy"), new String("All"));
    Assert.assertEquals(map.get("searchresult"), new String("true"));
    Assert.assertEquals(map.get("fieldName"), form.getfieldName());
    Assert.assertEquals(map.get("name"), form.getName());
    Assert.assertEquals(map.get("accountId"), form.getAccountId());
    List<Tenant> tenants = ((List<Tenant>) map.get("tenants"));
    Assert.assertTrue(tenants.size() == 1);
    Assert.assertEquals(tenants.get(0), tenant);

  }

  @Test
  public void testgetAccountType() {
    AccountType atType = tenantService.getAccountTypeByName("RETAIL");
    HashMap<String, Object> accountTypeMap = (HashMap<String, Object>) controller.getAccountType("RETAIL", null);
    Assert.assertNotNull(accountTypeMap);
    Assert.assertEquals(atType.getName(), accountTypeMap.get("name"));
    Assert.assertEquals(atType.getPaymentModes(), accountTypeMap.get("paymentModes"));
    Assert.assertEquals(atType.isAutoPayRequired(), accountTypeMap.get("autoPayRequired"));
    Assert.assertEquals(atType.isManualActivation(), accountTypeMap.get("manualActivation"));
    Assert.assertEquals(Boolean.FALSE, accountTypeMap.get("show_deposit_record_message"));
  }

  @Test
  public void testgetAccountTypeWithInitialDeposit() {
    AccountType atType = tenantService.getAccountTypeByName("RETAIL");

    atType.setDepositRequired(true);
    for (AccountTypeCreditExposure a : atType.getAccountTypeCreditExposureList()) {
      a.setInitialDeposit(new BigDecimal(50));
    }
    accountTypeDAO.save(atType);

    HashMap<String, Object> accountTypeMap = (HashMap<String, Object>) controller.getAccountType("RETAIL",
        "CF319413-5DD7-4040-81FE-E2B1BBCF57F6");
    Assert.assertNotNull(accountTypeMap);
    Assert.assertEquals(atType.getName(), accountTypeMap.get("name"));
    Assert.assertEquals(atType.getPaymentModes(), accountTypeMap.get("paymentModes"));
    Assert.assertEquals(atType.isAutoPayRequired(), accountTypeMap.get("autoPayRequired"));
    Assert.assertEquals(atType.isManualActivation(), accountTypeMap.get("manualActivation"));
    Assert.assertEquals(Boolean.TRUE, accountTypeMap.get("show_deposit_record_message"));

    Tenant tenant = tenantService.get("CF319413-5DD7-4040-81FE-E2B1BBCF57F6");
    Assert
        .assertEquals(
            tenant.getCurrency().getCurrencyCode() + " "
                + atType.getInitialDeposit(tenant.getCurrency().getCurrencyCode()),
        accountTypeMap.get("initialDepositAmount"));

    accountTypeMap = (HashMap<String, Object>) controller.getAccountType("RETAIL",
        "f132a5e3-f1ae-478b-999f-ddaf68e2b711");
    Assert.assertNotNull(accountTypeMap);
    Assert.assertEquals(Boolean.FALSE, accountTypeMap.get("show_deposit_record_message"));
  }

  @Test
  public void testsaveNewPrimaryEmail() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    UserAlertPreferences userAlertPreferences = new UserAlertPreferences();
    userAlertPreferences.setUser(getRootUser());
    userAlertPreferences.setCreatedBy(getRootUser());
    String newEmail = "test" + random.nextInt() + "@test.com";
    userAlertPreferences.setEmailAddress(newEmail);
    userAlertPreferences.setUpdatedBy(getRootUser());
    userAlertPreferences.setAlertType(AlertType.USER_EMAIL);
    userAlertDAO.save(userAlertPreferences);
    request.setAttribute("isSurrogatedTenant", false);
    Map<String, String> saveEmailMap = new HashMap<String, String>();
    saveEmailMap = controller.saveNewPrimaryEmail(tenant.getParam(), tenant.getOwner().getParam(), userAlertPreferences
        .getId().toString(), map, request);
    Assert.assertEquals(saveEmailMap.get("email"), userAlertPreferences.getEmailAddress());
    Assert.assertEquals(saveEmailMap.get("isVerified"), Boolean.toString(tenant.getOwner().isEmailVerified()));
    Assert.assertEquals(saveEmailMap.get("status"), "success");
    User user = userService.get(tenant.getOwner().getParam());
    Assert.assertEquals(newEmail, user.getEmail());
    saveEmailMap = controller.saveNewPrimaryEmail(tenant.getParam(), tenant.getOwner().getParam(), null, map, request);
    Assert.assertEquals(saveEmailMap.get("status"), "failure");

  }

  @Test
  public void testdeleteAlertsDeliveryOptions() {
    UserAlertPreferences userAlertPreferences = new UserAlertPreferences();
    userAlertPreferences.setUser(getRootUser());
    userAlertPreferences.setCreatedBy(getRootUser());
    userAlertPreferences.setEmailAddress("test" + random.nextInt() + "@test.com");
    userAlertPreferences.setUpdatedBy(getRootUser());
    userAlertPreferences.setAlertType(AlertType.USER_EMAIL);
    userAlertDAO.save(userAlertPreferences);

    String delete = controller.deleteAlertsDeliveryOptions(userAlertPreferences.getId(), map);
    Assert.assertEquals(delete, new String("success"));

    delete = controller.deleteAlertsDeliveryOptions(null, map);
    Assert.assertEquals(delete, new String("failure"));

  }

  @Test
  public void testviewAlert() {
    Tenant tenant = tenantService.getTenantByParam("id", "3", false);
    tenant.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant);
    SpendAlertSubscription spendAlert = createSpendAlertSubscription(tenant);
    String viewAlert = controller.viewAlert(spendAlert.getId().toString(), map);

    Assert.assertEquals(viewAlert, new String("alerts.view"));
    Assert.assertTrue(map.containsAttribute("subscription"));
    Assert.assertTrue(map.containsAttribute("spendbudget"));
    Assert.assertTrue(map.containsAttribute("tenant"));
    Assert.assertTrue(map.containsAttribute("user_email"));
    Assert.assertTrue(map.containsAttribute("user_phone"));
    Assert.assertEquals(map.get("subscription"), spendAlert);
    Assert.assertEquals(map.get("spendbudget"), tenant.getSpendBudget().toString());
    Assert.assertEquals(map.get("tenant"), tenant);
    Assert.assertEquals(map.get("user_email"), tenant.getOwner().getEmail());
    Assert.assertEquals(map.get("user_phone"), tenant.getOwner().getPhone());
  }

  @Test
  public void testcreateSpendAlertSubscription() {
    Tenant tenant1 = tenantService.getTenantByParam("id", "1", false);
    Tenant tenant2 = tenantService.getTenantByParam("id", "3", false);
    tenant2.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant2);
    request.setAttribute("isSurrogatedTenant", true);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant2);
    String create = controller.createSpendAlertSubscription(tenant1, tenant2.getParam(), map, request);
    Assert.assertEquals(create, new String("alerts.new"));
    Assert.assertTrue(map.containsAttribute("tenant"));
    Assert.assertTrue(map.containsAttribute("subscriptionForm"));
    Assert.assertEquals(map.get("tenant"), tenant2);
    CustomAlertForm subscriptionForm = ((CustomAlertForm) map.get("subscriptionForm"));
    Assert.assertEquals(subscriptionForm.getType(), "tenant");
    Assert.assertEquals(subscriptionForm.getBudget().toString(), "100");
    Assert.assertEquals(subscriptionForm.getThresholdType(), "percentage");

  }

  @Test
  public void testeditSpendAlertSubscription() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    tenant.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant);
    SpendAlertSubscription spendAlert = new SpendAlertSubscription();
    spendAlert.setAccountHolder(tenant);
    spendAlert.setData("TEST");
    spendAlert.setUser(userService.getUserByParam("id", "1", false));
    spendAlert.setPercentage(BigDecimal.valueOf(100));
    spendAlert.setSubscriptionType(1);
    spendAlertDAO.save(spendAlert);
    request.setAttribute("isSurrogatedTenant", false);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
    String edit = controller.editSpendAlertSubscription(spendAlert.getId().toString(), tenant, tenant.getParam(), map,
        request);
    Assert.assertEquals(edit, new String("alerts.edit"));
    Assert.assertTrue(map.containsAttribute("subscriptionForm"));
    Assert.assertTrue(map.containsAttribute("subscriptionId"));
    CustomAlertForm form = ((CustomAlertForm) map.get("subscriptionForm"));
    Assert.assertEquals(form.getThresholdType(), new String("percentage"));
    Assert.assertEquals(form.getTenantPercentage(), spendAlert.getPercentage());
    Assert.assertEquals(map.get("subscriptionId"), spendAlert.getId().toString());
    Assert.assertEquals(map.get("tenant"), tenant);

    map.clear();

    Tenant tenant1 = tenantService.getTenantByParam("id", "3", false);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant1);

    spendAlert = new SpendAlertSubscription();
    spendAlert.setAccountHolder(tenant1);
    spendAlert.setData("TEST");
    spendAlert.setUser(tenant1.getOwner());
    spendAlert.setPercentage(BigDecimal.valueOf(100));
    spendAlert.setSubscriptionType(1);
    spendAlertDAO.save(spendAlert);
    asUser(tenant1.getOwner());
    edit = controller.editSpendAlertSubscription(spendAlert.getId().toString(), tenant, tenant1.getParam(), map,
        request);
    Assert.assertEquals(map.get("tenant"), tenant1);
    form = ((CustomAlertForm) map.get("subscriptionForm"));
    Assert.assertEquals(form.getUser(), tenant1.getOwner());

  }

  @Test
  public void testsetAccountBudget() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    tenant.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
    String setBudget = controller.setAccountBudget(tenant, tenant.getParam(), map, request);
    Assert.assertEquals(setBudget, new String("alerts.setaccountbudget"));
    Assert.assertTrue(map.containsAttribute("tenantForm"));
    Assert.assertTrue(map.containsAttribute("tenant"));
    Assert.assertEquals(map.get("tenant"), tenant);
    TenantForm tenantForm = ((TenantForm) map.get("tenantForm"));
    Assert.assertEquals(tenantForm.getSpendLimit(), tenant.getSpendBudget());

    map.clear();
    Tenant tenant1 = tenantService.getTenantByParam("id", "3", false);
    tenant1.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant1);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant1);
    setBudget = controller.setAccountBudget(tenant, tenant1.getParam(), map, request);
    Assert.assertEquals(map.get("tenant"), tenant1);
    tenantForm = ((TenantForm) map.get("tenantForm"));
    Assert.assertEquals(tenantForm.getSpendLimit(), tenant1.getSpendBudget());

  }

  @Test
  public void testSetAccountBudget() {
    Tenant tenant1 = tenantService.getTenantByParam("id", "1", false);
    tenant1.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant1);
    Tenant tenant2 = tenantService.getTenantByParam("id", "3", false);
    TenantForm form = new TenantForm();
    form.setTenant((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant1));

    String setBudget = controller.setAccountBudget(tenant1, tenant2.getParam(), form, map);
    Assert.assertEquals(("success"), setBudget);
    Assert.assertTrue(map.containsAttribute("subscriptions"));
    Assert.assertTrue(map.containsAttribute("size"));
    Assert.assertTrue(map.containsAttribute("user"));
    Assert.assertEquals(map.get("user"), tenant2.getOwner());
    Assert.assertNotNull(map.get("subscriptions"));
    Assert.assertEquals(tenant2.getSpendBudget().toString(), "100");

  }

  @Test
  public void testdelete() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    String delete = controller.delete(tenant.getParam(), map);
    Assert.assertEquals(delete, new String("success"));
  }

  @Test
  public void testdeleteWithSuffix() {
    asRoot();
    com.vmops.model.Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_username_duplicate_allowed);
    configuration.setValue("true");
    configurationService.update(configuration);

    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    tenant.setUsernameSuffix("test");
    tenantService.update(tenant);

    String delete = controller.delete(tenant.getParam(), map);
    Assert.assertEquals(delete, new String("success"));

    tenant = tenantService.getTenantByParam("id", "1", true);
    Assert.assertEquals("_1_test", tenant.getUsernameSuffix());
  }

  @Test
  public void testlistStateChanges() {
    AccountType accountType = new AccountType("NewACType_" + System.currentTimeMillis(), "New AccountType",
        "New AccountType Description", false);
    accTypeDAO.save(accountType);

    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    TenantChange tenantChange = new TenantChange();
    tenantChange.setTenant(tenant);
    tenantChange.setOldState(State.SUSPENDED);
    tenantChange.setNewState(State.ACTIVE);
    tenantChange.setOldAccountType(accountType);
    tenantChange.setNewAccountType(accountType);
    tenantChange.setAction(Action.ACTIVATED.toString());
    tenantChange.setCreatedBy(getPortalUser());
    tenantService.saveTenantChange(tenantChange);
    String stateChange = controller.listStateChanges(tenant.getParam(), map);
    Assert.assertEquals(stateChange, new String("tenant.state.changes"));
    Assert.assertTrue(map.containsAttribute("tenant"));
    Assert.assertEquals(map.get("tenant"), tenant);
    Assert.assertTrue(map.containsKey("transactionHistory"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testeditAccountLimits() {
    Tenant tenant = tenantService.getTenantByParam("id", "2", false);
    String edit = controller.editAccountlimits(tenant.getParam(), "003fa8ee-fba3-467f-a517-ed806dae8a87", map);
    Assert.assertEquals(edit, new String("tenant.editaccountlimits"));
    Assert.assertTrue(map.containsAttribute("account_control_edit_properties"));
    Map<String, String> resLimitMap = (Map<String, String>) map.get("resLimitMap");
    List<AccountControlServiceConfigMetadata> properties = (List<AccountControlServiceConfigMetadata>) map
        .get("account_control_edit_properties");
    for (AccountControlServiceConfigMetadata accountControlServiceConfigMetadata : properties) {
      Assert.assertEquals(resLimitMap.get(accountControlServiceConfigMetadata.getName()), "-1");
    }
  }

  private Map<String, String> getResourceMap() {

    Map<String, String> resMap = new HashMap<String, String>();
    resMap.put("perUserTemplateCount", "-1");
    resMap.put("perUserPublicIPCount", "-1");
    resMap.put("perAccountPublicIPCount", "-1");
    resMap.put("perAccountPublicIPCount", "-1");
    resMap.put("perAccountPublicIPCount", "-1");
    resMap.put("perUserSnapshotCount", "-1");
    resMap.put("perAccountSnapshotCount", "-1");
    resMap.put("sharedAccount", "-1");
    resMap.put("perUserVmCount", "-1");
    resMap.put("perAccountTemplateCount", "-1");
    resMap.put("perAccountVolumeCount", "-1");
    resMap.put("perAccountVmCount", "-1");
    resMap.put("defaultNetworkOffering", "-1");
    resMap.put("perUserVolumeCount", "-1");
    return resMap;
  }

  @Test
  public void testlistAccountLimits() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    String show = controller.listAccountLimits(map, tenant.getUuid(), "003fa8ee-fba3-467f-a517-fd806dae8a80");
    Assert.assertEquals(show, new String("tenant.showlimits"));
    Assert.assertEquals(map.get("domainid"), tenantService.getTenantHandle(tenant.getUuid(), "1"));
    @SuppressWarnings("unchecked")
    Map<String, String> resLimitMap = (Map<String, String>) map.get("resourceLimitsMap");
    for (String key : resLimitMap.keySet()) {
      Assert.assertEquals(resLimitMap.get(key), "-1");
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testviewVerifiedEmails() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    User user = tenant.getOwner();
    String emailAddress = "test_verify_email@test.com";

    UserAlertPreferences userAlertPreference = userAlertPreferenceService.createUserAlertPreference(user, emailAddress,
        AlertType.USER_ALERT_EMAIL);
    userAlertPreference.setEmailVerified(true);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
    request.setAttribute("isSurrogatedTenant", false);
    String verifiedmails = controller.viewVerifiedEmails(tenant.getParam(), user.getParam(), map, request);

    Assert.assertEquals(verifiedmails, new String("alerts.change_primary_email"));
    Assert.assertEquals(map.get("user"), user);
    Assert.assertEquals(map.get("tenant"), tenant);
    List<UserAlertPreferences> verifiedAlertPrefs = ((List<UserAlertPreferences>) map.get("verifiedAlertsPrefs"));
    Assert.assertEquals(verifiedAlertPrefs.get(0).toString(), userAlertPreference.toString());
    Assert.assertTrue(verifiedAlertPrefs.size() == 1);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testlistSubscriptions() {
    Tenant tenant1 = tenantService.getTenantByParam("id", "1", false);
    tenant1.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant1);
    Tenant tenant2 = tenantService.getTenantByParam("id", "3", false);
    SpendAlertSubscription spendAlert = createSpendAlertSubscription(tenant2);
    request.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant2);
    request.setAttribute("isSurrogatedTenant", true);
    String listSubscriptions = controller.listSubscriptions(tenant1, tenant2.getParam().toString(), "true", "1", map,
        request);
    Assert.assertEquals(listSubscriptions, new String("tenants.alerts"));
    Assert.assertEquals(map.get("showUserProfile"), true);
    Assert.assertEquals(map.get("tenant"), tenant2);
    Assert.assertEquals(map.get("subscrptionalerttype"), "tenant");
    Assert.assertNotNull(map.get("subscriptionForm"));
    List<SpendAlertSubscription> subscriptions = ((List<SpendAlertSubscription>) map.get("subscriptions"));
    Assert.assertEquals(subscriptions.get(0), spendAlert);
    Assert.assertEquals(map.get("size"), subscriptions.size());
    Assert.assertNotNull(map.get("tenantForm"));
    Assert.assertEquals(map.get("budget"), "true");
  }

  @Test
  public void testremoveSubscription() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    SpendAlertSubscription spendAlert = createSpendAlertSubscription(tenant);
    Assert.assertNotNull(spendAlert.getUser().getSpendAlertSubscriptions());
    Assert.assertTrue(spendAlert.getUser().getSpendAlertSubscriptions().size() == 1);
    String remove = controller.removeSubscription(spendAlert.getId().toString(), map);
    Assert.assertEquals(remove, new String("success"));
    Assert.assertTrue(spendAlert.getUser().getSpendAlertSubscriptions().size() == 0);
  }

  @Test
  public void testEditAccountLimits() {
    Tenant tenant = tenantService.getTenantByParam("id", "2", false);
    Map<String, String> resMap = getResourceMap();
    JSONArray jsonArray = new JSONArray();
    for (String key : resMap.keySet()) {
      JSONObject json = new JSONObject();
      json.element("name", key);
      json.element("value", resMap.get(key));
      jsonArray.add(json);
    }

    resMap.clear();
    resMap = controller.editAccountlimits(tenant.getParam(), "003fa8ee-fba3-467f-a517-ed806dae8a87",
        jsonArray.toString(), request);
    Assert.assertEquals(resMap.get("result"), "SUCCESS");
    Assert.assertEquals(resMap.get("validationResult"), "SUCCESS");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testviewAlertsDeliveryOptions() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    User user = tenant.getOwner();
    String emailAddress = "test_verify_email@test.com";

    UserAlertPreferences userAlertPreference = userAlertPreferenceService.createUserAlertPreference(user, emailAddress,
        AlertType.USER_ALERT_EMAIL);
    userAlertPreference.setEmailVerified(true);
    request.setAttribute("isSurrogatedTenant", false);
    String options = controller.viewAlertsDeliveryOptions(tenant.getParam(), user.getParam(), map, request);
    Assert.assertEquals(options, new String("alerts.delivery_opts"));
    List<UserAlertPreferences> alertsPrefs = ((List<UserAlertPreferences>) map.get("alertsPrefs"));
    Assert.assertEquals(alertsPrefs.get(0), userAlertPreference);
    Assert.assertEquals(map.get("alertsPrefsSize"), 1);
    Assert.assertEquals(map.get("addAlertEmailLimit"), 2);
    Assert.assertNotNull(map.get("userAlertEmailForm"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetApiCredentials() {
    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    asUser(tenant.getOwner());
    Map<String, Object> controllerMap = controller.getApiCredentials("Portal123#", request);
    Assert.assertNotNull(controllerMap.get("tenantCredentialList"));
    List<Map<String, String>> credentialList = new ArrayList<Map<String, String>>();
    credentialList = (List<Map<String, String>>) controllerMap.get("tenantCredentialList");
    Assert.assertEquals(2, credentialList.size());
    if (credentialList.get(1).get("ServiceName").equalsIgnoreCase("com.company1.service1.service.name")) {
      Assert.assertEquals("com.company1.service1.service.name", credentialList.get(1).get("ServiceName"));
      Assert.assertEquals(credentialList.get(1).get("ServiceUuid"), "b1c9fbb0-8dab-42dc-ae0a-ce1384a1e6");
      Assert.assertEquals(credentialList.get(1).get("InstanceName"), "SERVICE11");
    } else {
      Assert.assertEquals("CloudPlatform.service.name", credentialList.get(1).get("ServiceName"));
      Assert.assertEquals(credentialList.get(1).get("ServiceUuid"), "fc3c6f30-a44a-4754-a8cc-9cea97e0a129");
      Assert.assertEquals(credentialList.get(1).get("InstanceName"), "SERVICE16");
    }
  }

  @Test
  public void testGetApiCredentialsForEmpty() {
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    asUser(tenant.getOwner());
    Map<String, Object> controllerMap = controller.getApiCredentials("passW0rd", request);
    @SuppressWarnings("unchecked")
    List<Map<String, String>> credentialList = (ArrayList<Map<String, String>>) controllerMap
        .get("tenantCredentialList");
    Assert.assertTrue(credentialList.isEmpty());
  }

  @Test
  public void testShow() {
    Tenant tenant = tenantService.getTenantByParam("id", "2", false);
    String view = controller.show(tenant.getParam(), map);
    Assert.assertEquals("tenant.view", view);

    Assert.assertTrue("map must have tenant", map.containsKey("tenant"));
    Assert.assertEquals(tenant, map.get("tenant"));
    Assert.assertTrue("map must have actions", map.containsKey("pendingActions"));

  }

  @Test
  public void testcancelCredit() {
    Tenant tenant = tenantService.get("f132a5e3-f1ae-478b-999f-ddaf68e2b711");
    String cancelStatus1 = controller.cancelCredit(tenant.getParam(), "8c4832c3-c319-4aef-ad47-25f9b187cfd4",
        "comments", request, response);

    Assert.assertEquals("Test to validate, the cancellation of SLR ID , expected success", "success", cancelStatus1);

    String cancelStatus2 = controller.cancelCredit(tenant.getParam(), "8c4832c3-c319-4aef-ad47-25f9b187cfd4",
        "comments", request, response);

    Assert.assertEquals("Test to validate, the cancellation of same SLR ID , expected failure", "failure",
        cancelStatus2);
  }

  @Test
  public void testcancelCreditWithInvalidSLR() {
    Tenant tenant = tenantService.get("f132a5e3-f1ae-478b-999f-ddaf68e2b711");
    String cancelStatus3 = controller.cancelCredit(tenant.getParam(), "8c4832c3-c319-4aef-ad47-25f9b187cf", "comments",
        request, response);

    Assert.assertEquals("Test to validate with an  non exist SLR ID , expected failure", "failure", cancelStatus3);
  }

  @Test
  public void testcancelCreditWithINVOICETypeSLR() {
    Tenant tenant = tenantService.get("f132a5e3-f1ae-478b-999f-ddaf68e2b711");
    String cancelStatus4 = controller.cancelCredit(tenant.getParam(), "8c4732b3-c319-4aef-ad46-25f9b187cf04",
        "comments", request, response);

    Assert.assertEquals("Test to validate cancel credit with INVOICE type SLR ID , expected failure", "failure",
        cancelStatus4);
  }

  @Test
  public void testcancelCreditWithSLRAsNULL() {
    Tenant tenant = tenantService.get("f132a5e3-f1ae-478b-999f-ddaf68e2b711");
    String cancelStatus5 = controller.cancelCredit(tenant.getParam(), null, "comments", request, response);

    Assert.assertEquals("Test to validate cancel credit with SLR ID as null, expected failure", "failure",
        cancelStatus5);
  }

  @Test
  public void testcancelCreditCommentAsNULL() {
    Tenant tenant = tenantService.get("f132a5e3-f1ae-478b-999f-ddaf68e2b711");
    String cancelStatus6 = controller.cancelCredit(tenant.getParam(), "8c4832c3-c319-4aef-ad46-25f9b187cf04", null,
        request, response);

    Assert.assertEquals("Test to validate cancel credit with comment as null, expected success", "success",
        cancelStatus6);
  }

  @Test
  public void testSaveAlertsDeliveryOptionsOnReachingUserLimit() throws Exception {

    Configuration configuration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.resourceLimits.registered.emailAddresses");
    configuration.setValue("1");
    configurationService.update(configuration);

    User user = userService.getUserByParam("username", "2_retail1", false);
    UserAlertEmailForm userAlertEmailForm = new UserAlertEmailForm();
    userAlertEmailForm.setUser(user);
    userAlertEmailForm.setEmail("subodh.kr@gmail.com");
    userAlertEmailForm.setAlertType(AlertType.USER_ALERT_EMAIL);

    BindingResult result = validate(userAlertEmailForm);
    Map<String, String> returnMap = controller.saveAlertsDeliveryOptions(userAlertEmailForm, result);
    List<UserAlertPreferences> list = userAlertPreferenceService.listAllUserAlertPreferences(user);
    Assert.assertEquals(list.size(), Integer.parseInt("1"));
    Assert.assertNotNull(returnMap);
    Assert.assertFalse(returnMap.isEmpty());
    try {
      controller.saveAlertsDeliveryOptions(userAlertEmailForm, result);
      Assert.fail();
    } catch (AjaxFormValidationException e) {
      Assert.assertEquals(e.getMessage(), "Ajax Form Validation Error");
      logger.debug("###Exiting testSaveAlertsDeliveryOptionsOnReachingUserLimit");

    }

  }

  @Test
  public void testSaveAlertsDeliveryOptionsForBlacklistedEmail() throws Exception {
    Configuration configuration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.emailDomain.blacklist");
    configuration.setValue("yahoo.com");
    configurationService.update(configuration);

    User user = userService.getUserByParam("username", "2_retail1", false);
    UserAlertEmailForm userAlertEmailForm = new UserAlertEmailForm();
    userAlertEmailForm.setUser(user);
    userAlertEmailForm.setEmail("subodh.kr@yahoo.com");
    userAlertEmailForm.setAlertType(AlertType.USER_ALERT_EMAIL);

    BindingResult result = validate(userAlertEmailForm);
    try {
      controller.saveAlertsDeliveryOptions(userAlertEmailForm, result);
      Assert.fail();
    } catch (AjaxFormValidationException e) {
      Assert.assertEquals(e.getMessage(), "Ajax Form Validation Error");
      logger.debug("###Exiting testSaveAlertsDeliveryOptionsForBlacklistedEmail");
    }
  }

  @Test
  public void testSaveAlertsDeliveryOptionsForAddingPrimaryEmail() throws Exception {

    User user = userService.getUserByParam("username", "2_retail1", false);
    String email = user.getEmail();

    UserAlertEmailForm userAlertEmailForm = new UserAlertEmailForm();
    userAlertEmailForm.setUser(user);
    userAlertEmailForm.setEmail(email);
    userAlertEmailForm.setAlertType(AlertType.USER_ALERT_EMAIL);
    BindingResult result = validate(userAlertEmailForm);

    try {
      controller.saveAlertsDeliveryOptions(userAlertEmailForm, result);
      Assert.fail();
    } catch (AjaxFormValidationException e) {
      Assert.assertEquals(e.getMessage(), "Ajax Form Validation Error");
      logger.debug("###Exiting testSaveAlertsDeliveryOptionsForAddingPrimaryEmail============" + e);
    }
  }

  @Test
  public void testViewNotification() {

    String date = DateUtils.getSimpleDateTimeString(new Date());
    System.err.println(date);
    Tenant tenant = tenantService.get("dfc84388-d44d-4d8e-9d6a-a62c1c16b7e4");

    int prevCount = eventDAO.count();
    createTestUserInTenant(tenant);

    int currentCount = eventDAO.count();
    Assert.assertEquals(prevCount + 1, currentCount);

    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    String result = controller.viewNotification(tenant, tenant.getParam(), date, "1", null, null, map, request);
    List<Event> eventList = eventDAO.findAll(null);

    Assert.assertTrue(eventList.get(0).getMessage().contains("user.created"));

    Assert.assertEquals(result, new String("notifications.view"));

  }

  @Test
  public void testListNotification() {

    Tenant tenant = tenantService.get("dfc84388-d44d-4d8e-9d6a-a62c1c16b7e4");

    int prevCount = eventDAO.count();
    createTestUserInTenant(tenant);
    int currentCount = eventDAO.count();
    Assert.assertEquals(prevCount + 1, currentCount);

    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    String result = controller.listNotifications(tenant, tenant.getParam(), null, "1", null, map, request);
    List<com.vmops.model.Event> eventList = eventDAO.findAll(null);

    Assert.assertTrue(eventList.get(0).getMessage().contains("user.created"));
    Assert.assertEquals(result, new String("tenants.notifications"));

  }

  @Test
  @Transactional
  public void testListNotificationWithSize() {

    Tenant tenant = tenantService.get("dfc84388-d44d-4d8e-9d6a-a62c1c16b7e4");

    int prevCount = eventDAO.count();
    createTestUserInTenant(tenant);
    int currentCount = eventDAO.count();
    Assert.assertEquals(prevCount + 1, currentCount);

    request.setAttribute("isSurrogatedTenant", Boolean.FALSE);

    asUser(tenant.getOwner());

    Event event = new Event(mockDateTimeServiceSetter.getCurrentDate(), "test111", null, tenant, Source.PORTAL,
        Scope.ACCOUNT, Category.ACCOUNT, Severity.INFORMATION, false);
    eventService.createEvent(event, false);
    Event event2 = new Event(mockDateTimeServiceSetter.getCurrentDate(), "test222", null, tenant, Source.PORTAL,
        Scope.ACCOUNT, Category.ACCOUNT, Severity.INFORMATION, false);
    eventService.createEvent(event2, false);

    currentCount = eventDAO.count();
    Assert.assertEquals(prevCount + 3, currentCount);

    List<Event> events = eventService.getEvents(tenant, 0, 0);
    Assert.assertEquals(events.size(), 3);

    String result = controller.listNotifications(tenant, tenant.getParam(), null, "1", "1", map, request);
    List<com.vmops.model.Event> eventList = eventDAO.findAll(null);

    Assert.assertTrue(eventList.get(0).getMessage().contains("user.created"));
    Assert.assertEquals(result, new String("tenants.notifications"));
    // TODO: Test always get the value as zero because the internal call makes
    Assert.assertEquals(map.get("size"), 0);
// a direct database connection , and hence does not find transient records.

  }

  @Test
  public void testListCurrencies() {

    TenantForm tenantForm = new TenantForm();
    Channel channel = channelDAO.find(1L);
    map.addAttribute("account", tenantForm);
    String result = controller.listCurrencies(channel.getParam(), map);
    Assert.assertEquals(result, "tenant.channel.currencies");
    TenantForm tenantForm2 = (TenantForm) map.get("account");
    Assert.assertEquals(1, tenantForm2.getCurrencyValueList().size());

  }

  @Test
  public void testValidateUserLimitWithZero() {
    try {
      Tenant tenant = tenantService.get("dfc84388-d44d-4d8e-9d6a-a62c1c16b7e4");
      controller.validateUserLimit("0", tenant.getParam());
    } catch (Exception e) {
      Assert.assertEquals("Cannot set max user limit to 0.", e.getMessage());
      logger.debug("###Exiting testValidateUserLimitWithZero");
    }
  }

  @Test
  public void testValidateUserLimitWithMaxCount() {
    Tenant tenant = tenantService.get("dfc84388-d44d-4d8e-9d6a-a62c1c16b7e4");
    List<User> userList = tenant.getAllUsers();
    String count = Integer.toString(userList.size());
    String result = controller.validateUserLimit(count, tenant.getParam());
    Assert.assertNotNull(result);
    Assert.assertEquals("true", result);
  }

  @Test
  public void testValidateUserLimitExceedMaxCount() {
    try {
      Tenant tenant = tenantService.get("f132a5e3-f1ae-478b-999f-ddaf68e2b7db");
      List<User> userList = tenant.getAllUsers();
      String count = Integer.toString(userList.size() - 1);
      controller.validateUserLimit(count, tenant.getParam());
    } catch (Exception e) {
      Assert.assertEquals("Failed to set max user limit as this  account has more users.", e.getMessage());
      logger.debug("###Exiting testValidateUserLimitExceedMaxCount");
    }
  }

  @Test
  public void testValidateUserLimitWithInvalidValue() {
    try {
      Tenant tenant = tenantService.get("f132a5e3-f1ae-478b-999f-ddaf68e2b7db");
      controller.validateUserLimit("count", tenant.getParam());
    } catch (Exception e) {
      Assert.assertEquals("This value for user limit is unacceptable. ", e.getMessage());
      logger.debug("###Exiting testValidateUserLimitWithInvalidValue");
    }
  }

  @Test
  public void testTenantCreationRetail() throws Exception {
    Tenant tenant = testTenantCreation(3L, null);
    Assert.assertNotNull(tenant);
    Assert.assertEquals(accountTypeDAO.getAccountTypeByName("RETAIL"), tenant.getAccountType());
  }

  @Test
  public void testTenantCreationCorporate() throws Exception {
    Tenant tenant = testTenantCreation(4L, null);
    Assert.assertNotNull(tenant);
    Assert.assertEquals(accountTypeDAO.getAccountTypeByName("Corporate"), tenant.getAccountType());
  }

  @Test
  public void testTenantCreationTrial() throws Exception {
    // TODO: need to create a new campaign here other wise this test will fail
    CampaignPromotion campaignPromotion = new CampaignPromotion();
    campaignPromotion.setCode("USD" + random.nextInt());
    campaignPromotion.setCreateBy(getRootUser());
    campaignPromotion.setUpdateBy(getRootUser());
    campaignPromotion.setTrial(true);
    campaignPromotion.setCampaignPromotionsInChannels(new HashSet<CampaignPromotionsInChannels>());
    campaignPromotion = cmpdao.save(campaignPromotion);

    PromotionToken promotionToken = new PromotionToken(campaignPromotion, "TESTPROMOCODE");
    promotionToken.setCreateBy(getRootUser());
    tokendao.save(promotionToken);

    Tenant tenant = testTenantCreation(5L, "TESTPROMOCODE");
    Assert.assertNotNull(tenant);
    Assert.assertEquals(accountTypeDAO.getAccountTypeByName("Trial"), tenant.getAccountType());
  }

  @Test
  public void testTenantCreationNegative() throws Exception {

    AccountType type = accountTypeDAO.find(4L);
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
    form.setAccountTypeId("4");
    form.setUser(newUser);
    form.setTenant(newTenant);
    form.setCountryList(countryList);

    BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
    result.addError(new FieldError("Error", "Error", "Error"));
    String tenantCreation = controller.create(form, result, map, status, request);
    logger.debug("RESULT------------------------" + tenantCreation);
    Assert.assertFalse("verifying the form has zero error", tenantCreation.contains("0 errors"));
  }

  private Tenant testTenantCreation(Long accountType, String trialCode) throws Exception {
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
    form.setTrialCode(trialCode);
    form.setAccountTypeId(accountType.toString());
    form.setUser(newUser);
    form.setTenant(newTenant);
    form.setCountryList(countryList);

    BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
    String tenantCreation = controller.create(form, result, map, status, request);
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

  @Test
  public void testcreateSpendAlertSubscriptionAddAlert() throws Exception {

    Tenant tenant = tenantService.getTenantByParam("id", "3", false);
    tenant.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant);

    request.setAttribute("isSurrogatedTenant", true);
    request.setAttribute("effectiveTenant", tenant);
    CustomAlertForm form = new CustomAlertForm();
    form.setType("tenant");
    form.setTenantPercentage(new BigDecimal(10.0));
    BindingResult result = validate(form);

    SpendAlertSubscription spendAlertSubscription = controller.createSpendAlertSubscription(tenant, tenant.getParam(),
        form, result, map, request);
    Assert.assertEquals(new BigDecimal(10.0), spendAlertSubscription.getPercentage());

  }

  @Test
  public void testeditSpendAlertSubscriptionEditSpendAlert() throws Exception {
    Tenant tenant = tenantService.getTenantByParam("id", "3", false);
    tenant.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant);

    SpendAlertSubscription spendAlert = new SpendAlertSubscription();
    spendAlert.setAccountHolder(tenant);
    spendAlert.setData("TEST");
    spendAlert.setUser(userService.getUserByParam("id", "4", false));
    spendAlert.setPercentage(BigDecimal.valueOf(100));
    spendAlert.setSubscriptionType(1);
    spendAlertDAO.save(spendAlert);

    CustomAlertForm form = new CustomAlertForm();
    form.setType("tenant");
    form.setTenantPercentage(spendAlert.getPercentage());
    BindingResult result = validate(form);
    SpendAlertSubscription spendAlertSubscription = controller.editSpendAlertSubscription(
        spendAlert.getId().toString(), "14", form, result, map, request);
    Assert.assertEquals(new BigDecimal(14), spendAlertSubscription.getPercentage());
  }

  @Test(expected = AjaxFormValidationException.class)
  public void testeditSpendAlertSubscriptionEditSpendAlertAsNULL() throws Exception {
    Tenant tenant = tenantService.getTenantByParam("id", "3", false);
    tenant.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant);

    SpendAlertSubscription spendAlert = new SpendAlertSubscription();
    spendAlert.setAccountHolder(tenant);
    spendAlert.setData("TEST");
    spendAlert.setUser(userService.getUserByParam("id", "4", false));
    spendAlert.setPercentage(BigDecimal.valueOf(100));
    spendAlert.setSubscriptionType(1);
    spendAlertDAO.save(spendAlert);

    CustomAlertForm form = new CustomAlertForm();
    form.setType("tenant");
    form.setTenantPercentage(spendAlert.getPercentage());
    BindingResult result = validate(form);
    controller.editSpendAlertSubscription(spendAlert.getId().toString(), null, form, result, map, request);

  }

  @Test(expected = AjaxFormValidationException.class)
  public void testeditSpendAlertSubscriptionEditSpendAlertMoreThan100Percent() throws Exception {
    Tenant tenant = tenantService.getTenantByParam("id", "3", false);
    tenant.setSpendBudget(BigDecimal.valueOf(100));
    tenantService.save(tenant);

    SpendAlertSubscription spendAlert = new SpendAlertSubscription();
    spendAlert.setAccountHolder(tenant);
    spendAlert.setData("TEST");
    spendAlert.setUser(userService.getUserByParam("id", "4", false));
    spendAlert.setPercentage(BigDecimal.valueOf(100));
    spendAlert.setSubscriptionType(1);
    spendAlertDAO.save(spendAlert);

    CustomAlertForm form = new CustomAlertForm();
    form.setType("tenant");
    form.setTenantPercentage(spendAlert.getPercentage());
    BindingResult result = validate(form);
    controller.editSpendAlertSubscription(spendAlert.getId().toString(), "120", form, result, map, request);

  }

  @Test
  public void testeditTenantLogo() throws Exception {

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources");
    configurationService.update(configuration);

    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    TenantLogoForm tenantLogoForm = new TenantLogoForm();

    MultipartFile logo = new MockMultipartFile("poli.jpg", "poli.jpg", "bytes", "poli.jpg".getBytes());
    MultipartFile favicon = new MockMultipartFile("poli.ico", "poli.ico", "bytes", "poli.ico".getBytes());

    tenantLogoForm.setFavicon(favicon);
    tenantLogoForm.setLogo(logo);
    BindingResult result = validate(tenantLogoForm);
    String actualResult = controller.editTenantLogo(tenant.getUuid(), tenantLogoForm, result, map, null);
    Assert.assertEquals("redirect:/portal/home", actualResult);

  }

  @Test
  public void testeditTenantLogoInvalidFiles() throws Exception {

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources");
    configurationService.update(configuration);

    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    TenantLogoForm tenantLogoForm = new TenantLogoForm();

    MultipartFile logo = new MockMultipartFile("poli.txt", "poli.txt", "bytes", "poli.txt".getBytes());
    MultipartFile favicon = new MockMultipartFile("poli.ico", "poli.ico", "bytes", "poli.ico".getBytes());

    tenantLogoForm.setFavicon(favicon);
    tenantLogoForm.setLogo(logo);
    BindingResult result = validate(tenantLogoForm);
    String actualResult = controller.editTenantLogo(tenant.getUuid(), tenantLogoForm, result, map, null);
    Assert.assertEquals("tenants.editcurrentlogo", actualResult);
  }

  @Test
  public void testeditTenantLogoInvalidUploadPath() throws Exception {

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("");
    configurationService.update(configuration);

    Tenant tenant = tenantService.getTenantByParam("id", "1", false);
    TenantLogoForm tenantLogoForm = new TenantLogoForm();

    MultipartFile logo = new MockMultipartFile("poli.txt", "poli.txt", "bytes", "poli.txt".getBytes());
    MultipartFile favicon = new MockMultipartFile("poli.ico", "poli.ico", "bytes", "poli.ico".getBytes());

    tenantLogoForm.setFavicon(favicon);
    tenantLogoForm.setLogo(logo);
    BindingResult result = validate(tenantLogoForm);
    String actualResult = controller.editTenantLogo(tenant.getUuid(), tenantLogoForm, result, map, null);
    Assert.assertEquals("tenants.editcurrentlogo", actualResult);
  }

  @Test
  public void testTenantEditWithSecondaryAddress() throws Exception {
    Tenant expected = tenantDAO.findAll(null).get(0);
    expected.setSecondaryAddress(new Address("steve", "creek", "cupertino", "ca", "95014", "US"));
    String view = controller.edit(expected.getParam(), map, request);
    Assert.assertEquals("tenant.edit", view);
    Assert.assertTrue(map.containsKey("tenantForm"));
    com.citrix.cpbm.access.Tenant found = ((TenantForm) map.get("tenantForm")).getTenant();
    Assert.assertEquals(expected, found.getObject());
  }

  @Test
  public void testVerifyPromoAvail() {
    String channelParam = "9fc7754c-6d46-11e0-a026-065287aed31b";
    String promoStatus = controller.isPromoCodeAvailable(channelParam, map);
    Assert.assertEquals("success", promoStatus);

  }

  @Test
  public void testEnableService() throws JsonParseException, JsonMappingException, IOException {

    controller.enableService("f132a5e3-f1ae-478b-999f-ddaf68e2b711", "003fa8ee-fba3-467f-a517-ed806dae8a87", null,
        false, map, request).get("serviceEnabled");

    Assert.assertEquals(CssdkConstants.SUCCESS, map.get("result"));

    Assert.assertTrue("There should be an event raised", eventListener.getEvents().size() > 0);
    Assert.assertEquals("Event should be CloudServiceActivation event", eventListener.getEvents().get(0).getPayload()
        .getClass(), TriggerTransaction.class);

  }

  // Test when connector is not down and create a account successfully there will not be any error message
  @Test
  public void testEnableServiceConnector() throws Exception {
    ModelMap resultMap = controller.enableService("f132a5e3-f1ae-478b-999f-ddaf68e2b711",
        "003fa8ee-fba3-467f-a517-ed806dae8a87", "{}", false, map, request);
    Assert.assertEquals(CssdkConstants.SUCCESS, resultMap.get("result"));
    Assert.assertNull(resultMap.get("message"));

    controller.setAutoProvisioning("f132a5e3-f1ae-478b-999f-ddaf68e2b711", "003fa8ee-fba3-467f-a517-ed806dae8a87",
        true, map, request);
    Tenant tenant = tenantService.get("f132a5e3-f1ae-478b-999f-ddaf68e2b711");

    Assert.assertEquals(true,
        testUtilityService.getServiceInstanceTenantConfiguration(tenant, "003fa8ee-fba3-467f-a517-ed806dae8a87")
            .getAutoProvision());
    Assert.assertEquals(CssdkConstants.SUCCESS, resultMap.get("result"));
  }

  @Test
  public void testEnableServiceConnectorWFCompleted() throws Exception {
    Tenant tenant = tenantService.get("CF319413-5DD7-4040-81FE-E2B1BBCF57F6");
    ServiceInstance serviceInstance = connectorManagementService.getInstance("003fa8ee-fba3-467f-a517-fd806dae8a80");
    BusinessTransaction transaction = new CloudServiceActivationTransaction(serviceInstance, tenant, "{}", true);
    transaction.setState(BusinessTransaction.State.SUCCESS);
    businessTransactionService.save(transaction);

    ModelMap resultMap = controller.enableService("CF319413-5DD7-4040-81FE-E2B1BBCF57F6",
        "003fa8ee-fba3-467f-a517-fd806dae8a80", "{}", false, map, request);
    Assert.assertTrue("There should be an event raised", eventListener.getEvents().size() > 0);
    Assert.assertEquals("Event should be ServiceAccountRegisterEvent event", eventListener.getEvents().get(0)
        .getPayload().getClass(), ServiceAccountRegisterEvent.class);

    Assert.assertEquals(CssdkConstants.SUCCESS, resultMap.get("result"));
    Assert.assertNull(resultMap.get("message"));
  }

  /**
   * @author vinayv
   * @throws Exception
   * @desc Test to verify that account creation is failed when username length is less than required.
   */
  @Test
  public void testCreateAccountWithInvalidUsernameLengthThroughRoot() throws Exception {
    try {
      final String INVALIDUSERNAME = "usr";
      AccountType type = accountTypeDAO.find("3");
      Tenant tenant = new Tenant("Acme Corp " + random.nextInt(), type, getRootUser(), randomAddress(), true,
          currencyValueService.locateBYCurrencyCode("USD"), getPortalUser());
      List<Country> countryList = countryDAO.findAll(null);
      Profile profile = profileDAO.find(8L);
      User user = new User("firstName", "lastName", "nageswarareddy.poli@citrix.com", INVALIDUSERNAME, "Portal123#",
          "91-9885098850", "GMT", null, profile, getRootUser());
      user.setAddress(randomAddress());
      tenant.setOwner(user);
      com.citrix.cpbm.access.User newUser = (com.citrix.cpbm.access.User) CustomProxy.newInstance(user);
      com.citrix.cpbm.access.Tenant newTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
      TenantForm form = new TenantForm();
      form.setAccountTypeId("3");
      form.setUser(newUser);
      form.setTenant(newTenant);
      form.setCountryList(countryList);

      BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
      controller.create(form, result, map, status, request);

    } catch (AjaxFormValidationException e) {
      Assert.assertEquals("Ajax Exception is caught when username length is less", "Ajax Form Validation Error",
          e.getMessage());
    }
  }

  /**
   * @author vinayv
   * @throws Exception
   * @desc Test to verify that account creation is failed when invalid firstname is entered while creation
   */
  @Test
  public void testCreateAccountWithInvalidFirstnameThroughRoot() throws Exception {
    try {
      final String INVALIDFIRSTNAME = "Name123!@#";
      AccountType type = accountTypeDAO.find("3");
      Tenant tenant = new Tenant("Acme Corp " + random.nextInt(), type, getRootUser(), randomAddress(), true,
          currencyValueService.locateBYCurrencyCode("USD"), getPortalUser());
      List<Country> countryList = countryDAO.findAll(null);
      Profile profile = profileDAO.find(8L);
      User user = new User(INVALIDFIRSTNAME, "lastName", "nageswarareddy.poli@citrix.com", "username14", "Portal123#",
          "91-9885098850", "GMT", null, profile, getRootUser());
      user.setAddress(randomAddress());
      tenant.setOwner(user);
      com.citrix.cpbm.access.User newUser = (com.citrix.cpbm.access.User) CustomProxy.newInstance(user);
      com.citrix.cpbm.access.Tenant newTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
      TenantForm form = new TenantForm();
      form.setAccountTypeId("3");
      form.setUser(newUser);
      form.setTenant(newTenant);
      form.setCountryList(countryList);

      BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
      controller.create(form, result, map, status, request);

    } catch (AjaxFormValidationException e) {
      Assert.assertEquals("Ajax Exception is caught when invalid Firstname is entered", "Ajax Form Validation Error",
          e.getMessage());
    }
  }

  /**
   * @author vinayv
   * @throws Exception
   * @desc Test to verify that account creation is failed when invalid lastname is entered while creation
   */
  @Test
  public void testCreateAccountWithInvalidLastnameThroughRoot() throws Exception {
    try {
      final String INVALIDLASTNAME = "Name123!@#";
      AccountType type = accountTypeDAO.find("3");
      Tenant tenant = new Tenant("Acme Corp " + random.nextInt(), type, getRootUser(), randomAddress(), true,
          currencyValueService.locateBYCurrencyCode("USD"), getPortalUser());
      List<Country> countryList = countryDAO.findAll(null);
      Profile profile = profileDAO.find(8L);
      User user = new User("firstName", INVALIDLASTNAME, "nageswarareddy.poli@citrix.com", "username14", "Portal123#",
          "91-9885098850", "GMT", null, profile, getRootUser());
      user.setAddress(randomAddress());
      tenant.setOwner(user);
      com.citrix.cpbm.access.User newUser = (com.citrix.cpbm.access.User) CustomProxy.newInstance(user);
      com.citrix.cpbm.access.Tenant newTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
      TenantForm form = new TenantForm();
      form.setAccountTypeId("3");
      form.setUser(newUser);
      form.setTenant(newTenant);
      form.setCountryList(countryList);

      BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
      controller.create(form, result, map, status, request);

    } catch (AjaxFormValidationException e) {
      Assert.assertEquals("Ajax Exception is caught when invalid Lastname is entered", "Ajax Form Validation Error",
          e.getMessage());
    }
  }

  @Test
  public void testGetDefaultLocale() {

    // Fetching default locale when it has been overridden at channel level
    Channel channel = channelService.getChannelById("3");
    String defaultLocale = controller.getDefaultLocale(channel.getUuid());
    Assert.assertEquals(channel.getChannelBrandingConfigurations().getDefaultLocale(), defaultLocale);

    // Fetching default locale when it has not been overridden at channel level
    channel = channelService.getChannelById("5");
    defaultLocale = controller.getDefaultLocale(channel.getUuid());
    Assert.assertEquals(config.getDefaultLocale(), defaultLocale);

  }

  @Test
  public void testTenantCreationBlackListedDomainNegative() {

    String blackDomain1 = "gmail.com";
    String blackDomain2 = "yahoo.com";

    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_blacklist, blackDomain1 + ","
        + blackDomain2);

    String email = "blackdomainuser@" + blackDomain2;

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertTenantCreationError("emaildomainblacklisted");

  }

  @Test
  public void testTenantCreationBlackListedDomainPositive() {

    String blackDomain1 = "gmail.com";
    String blackDomain2 = "yahoo.com";

    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_blacklist, blackDomain1 + ","
        + blackDomain2);

    String email = "blackdomainuser@citrix.com";

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationWhiteListedDomainPositive() {

    String whiteDomain1 = "gmail.com";
    String whiteDomain2 = "yahoo.com";

    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_whitelist, whiteDomain1 + ","
        + whiteDomain2);

    String email = "whitedomainuser@" + whiteDomain1;

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationWhiteListedDomainNegative() {

    String whiteDomain1 = "gmail.com";
    String whiteDomain2 = "yahoo.com";

    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_emailDomain_whitelist, whiteDomain1 + ","
        + whiteDomain2);

    String email = "blackdomainuser@citrix" + whiteDomain2;

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertTenantCreationError("emaildomainblacklisted");

  }

  @Test
  public void testTenantCreationWhiteListedDomainNegativeOverriddenAtChannel() {

    String whiteDomain1 = "gmail.com";
    String whiteDomain2 = "yahoo.com";

    setAllowedConfigOnDefaultChannel(null, whiteDomain1 + "," + whiteDomain2, null, null);

    String email = "blackdomainuser@citrix.com";

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertTenantCreationError("emaildomainblacklisted");

  }

  @Test
  public void testTenantCreationWhiteListedDomainPositiveOverriddenAtChannel() {

    String whiteDomain1 = "gmail.com";
    String whiteDomain2 = "yahoo.com";

    setAllowedConfigOnDefaultChannel(null, whiteDomain1 + "," + whiteDomain2, null, null);

    String email = "whitedomainuser@" + whiteDomain1;

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationWhiteListedDomainPositiveOverriddenAtChannelCapital() {

    String whiteDomain1 = "gmail.com";
    String whiteDomain2 = "yahoo.com";

    setAllowedConfigOnDefaultChannel(null, whiteDomain1 + "," + whiteDomain2, null, null);

    String email = "whitedomainuser@YAHOO.COM";

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationBlackListedDomainNegativeOverriddenAtChannel() {

    String blackDomain1 = "gmail.com";
    String blackDomain2 = "yahoo.com";

    setAllowedConfigOnDefaultChannel(blackDomain1 + "," + blackDomain2, null, null, null);

    String email = "blackdomainuser@" + blackDomain2;

    TenantForm form = createTenantForm(email, null);

    createTenant(form);
    assertTenantCreationError("emaildomainblacklisted");

  }

  @Test
  public void testTenantCreationBlackListedDomainPositiveOverriddenAtChannel() {

    String blackDomain1 = "gmail.com";
    String blackDomain2 = "yahoo.com";

    setAllowedConfigOnDefaultChannel(blackDomain1 + "," + blackDomain2, null, null, null);

    String email = "blackdomainuser@citrix.com";

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationBlackListedCountryNegative() {

    String blackCountry1 = "IN";
    String blackCountry2 = "US";

    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_country_blacklist, blackCountry1 + ","
        + blackCountry2);

    TenantForm form = createTenantForm(null, blackCountry2);

    createTenant(form);

    assertTenantCreationError("emailcountryblacklisted");

  }

  @Test
  public void testTenantCreationBlackListedCountryPositive() {

    String blackCountry1 = "IN";
    String blackCountry2 = "US";

    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_country_blacklist, blackCountry1 + ","
        + blackCountry2);

    TenantForm form = createTenantForm(null, "PK");

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationWhiteListedCountryPositive() {

    String whiteCountry1 = "IN";
    String whiteCountry2 = "US";

    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_country_whitelist, whiteCountry1 + ","
        + whiteCountry2);

    TenantForm form = createTenantForm(null, whiteCountry1);

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationWhiteListedCountryNegative() {

    String whiteCountry1 = "IN";
    String whiteCountry2 = "US";

    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_country_whitelist, whiteCountry1 + ","
        + whiteCountry2);

    TenantForm form = createTenantForm(null, "PK");

    createTenant(form);

    assertTenantCreationError("emailcountryblacklisted");

  }

  @Test
  public void testTenantCreationBlackListedCountryNegativeOverriddenAtChannel() {

    String blackCountry1 = "IN";
    String blackCountry2 = "US";

    setAllowedConfigOnDefaultChannel(null, null, blackCountry1 + "," + blackCountry2, null);

    TenantForm form = createTenantForm(null, blackCountry2);

    createTenant(form);

    assertTenantCreationError("emailcountryblacklisted");

  }

  @Test
  public void testTenantCreationBlackListedCountryPositiveOverriddenAtChannel() {

    String blackCountry1 = "IN";
    String blackCountry2 = "US";

    setAllowedConfigOnDefaultChannel(null, null, blackCountry1 + "," + blackCountry2, null);

    TenantForm form = createTenantForm(null, "PK");

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationWhiteListedCountryPositiveOverriddenAtChannel() {

    String whiteCountry1 = "IN";
    String whiteCountry2 = "US";

    setAllowedConfigOnDefaultChannel(null, null, null, whiteCountry1 + "," + whiteCountry2);

    TenantForm form = createTenantForm(null, whiteCountry2);

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  @Test
  public void testTenantCreationWhiteListedCountryNegativeOverriddenAtChannel() {

    String whiteCountry1 = "IN";
    String whiteCountry2 = "US";

    setAllowedConfigOnDefaultChannel(null, null, null, whiteCountry1 + "," + whiteCountry2);

    TenantForm form = createTenantForm(null, "PK");

    createTenant(form);

    assertTenantCreationError("emailcountryblacklisted");

  }

  /**
   * There exists a channel with same value in blacklisted country list and whitelisted country list. Verify that white
   * list takes preference in account creation.
   */
  @Test
  public void testTenantCreationWhiteListedCountryHasPrecedence() {

    String whiteCountry1 = "IN";
    String whiteCountry2 = "US";
    String blackCountry1 = "IN";

    setAllowedConfigOnDefaultChannel(null, null, blackCountry1, whiteCountry1 + "," + whiteCountry2);

    TenantForm form = createTenantForm(null, "IN");

    createTenant(form);

    assertSuccessfulTenantCreation();

  }

  /**
   * There exists a channel with same value in blacklisted domain and whitelisted domain. Verify that white list takes
   * preference in account creation.
   */
  @Test
  public void testTenantCreationWhiteListedDomainHasPrecedence() {

    String whiteDomain1 = "gmail.com";
    String whiteDomain2 = "yahoo.com";
    String blackDomain1 = "gmail.com";
    setAllowedConfigOnDefaultChannel(blackDomain1, whiteDomain1 + "," + whiteDomain2, null, null);

    String email = "whitedomainuser@gmail.com";

    TenantForm form = createTenantForm(email, null);

    createTenant(form);

    assertSuccessfulTenantCreation();
  }

  private TenantForm createTenantForm(String email, String countryCode) {
    if (email == null) {
      email = "abc@xyz.com";
    }
    if (countryCode == null) {
      countryCode = "US";
    }
    AccountType type = accountTypeDAO.find(4L);
    Tenant tenant = new Tenant("Acme Corp " + random.nextInt(), type, getRootUser(), randomAddress(), true,
        currencyValueService.locateBYCurrencyCode("USD"), getPortalUser());
    List<Country> countryList = countryDAO.findAll(null);
    Profile profile = profileDAO.find(8L);
    User user = new User("firstName", "lastName", email, "username14", "Portal123#", "91-9885098850", "GMT", null,
        profile, getRootUser());
    user.setAddress(new Address(random.nextInt() + " First Street", null, "Any Town", "New York", "12309", countryCode));
    tenant.setOwner(user);
    com.citrix.cpbm.access.User newUser = (com.citrix.cpbm.access.User) CustomProxy.newInstance(user);
    com.citrix.cpbm.access.Tenant newTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
    TenantForm form = new TenantForm();
    form.setChannelParam(channelService.getDefaultServiceProviderChannel().getParam());
    form.setAccountTypeId("4");
    form.setUser(newUser);
    form.setTenant(newTenant);
    form.setCountryList(countryList);
    return form;
  }

  private void setAllowedConfigOnDefaultChannel(String blackDomainList, String whiteDomainList,
      String blackCountryList, String whiteCountryList) {

    Channel defaultChannel = channelService.getDefaultServiceProviderChannel();

    defaultChannel.getChannelBrandingConfigurations().setBlacklistdomains(blackDomainList);
    defaultChannel.getChannelBrandingConfigurations().setWhitelistdomains(whiteDomainList);
    defaultChannel.getChannelBrandingConfigurations().setBlacklistcountries(blackCountryList);
    defaultChannel.getChannelBrandingConfigurations().setWhitelistcountries(whiteCountryList);

    channelService.updateChannel(defaultChannel);
    defaultChannel = channelService.getDefaultServiceProviderChannel();

    Assert.assertEquals(blackDomainList, defaultChannel.getChannelBrandingConfigurations().getBlacklistdomains());
    Assert.assertEquals(whiteDomainList, defaultChannel.getChannelBrandingConfigurations().getWhitelistdomains());
    Assert.assertEquals(blackCountryList, defaultChannel.getChannelBrandingConfigurations().getBlacklistcountries());
    Assert.assertEquals(whiteCountryList, defaultChannel.getChannelBrandingConfigurations().getWhitelistcountries());
  }

  private void createTenant(TenantForm form) {
    BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");

    try {
      controller.create(form, result, map, status, request);
    } catch (Exception e) {
      Assert.fail("Unexpected Exception :" + e.getMessage());
      e.printStackTrace();
    }
  }

  private void assertSuccessfulTenantCreation() {
    Object errorString = map.get("signuperror");
    Assert.assertNull(errorString);

    Assert.assertNotNull(map.get("tenantId"));
    Assert.assertNotNull(map.get("tenantParam"));
    Assert.assertNotNull(map.get("tenantOwnerUserName"));
    Assert.assertNotNull(map.get("tenantName"));

  }

  private void assertTenantCreationError(String error) {
    Object errorString = map.get("signuperror");
    Assert.assertNotNull(errorString);
    Assert.assertEquals(error, errorString.toString());
  }
}
