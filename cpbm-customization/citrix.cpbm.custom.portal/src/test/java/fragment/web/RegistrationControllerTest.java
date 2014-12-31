/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;
import web.support.MockSessionStatus;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.core.workflow.service.BusinessTransactionService;
import com.citrix.cpbm.core.workflow.service.TaskService;
import com.citrix.cpbm.platform.admin.service.utils.ServiceInstanceConfiguration;
import com.citrix.cpbm.platform.bootstrap.service.BootstrapActivator;
import com.citrix.cpbm.platform.spi.AccountLifecycleHandler;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.UserLifecycleHandler;
import com.citrix.cpbm.portal.forms.UserRegistration;
import com.citrix.cpbm.portal.fragment.controllers.ChannelController;
import com.citrix.cpbm.portal.fragment.controllers.RegistrationController;
import com.citrix.cpbm.portal.fragment.controllers.TasksController;
import com.vmops.event.EmailVerified;
import com.vmops.event.PortalEvent;
import com.vmops.event.TenantActivation;
import com.vmops.event.VerifyEmailRequest;
import com.vmops.internal.service.PaymentGatewayService;
import com.vmops.model.AccountType;
import com.vmops.model.Address;
import com.vmops.model.CampaignPromotion;
import com.vmops.model.CampaignPromotionsInChannels;
import com.vmops.model.Channel;
import com.vmops.model.ChannelBrandingConfigurations;
import com.vmops.model.Configuration;
import com.vmops.model.Country;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.PromotionSignup;
import com.vmops.model.PromotionToken;
import com.vmops.model.Service;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.model.billing.PaymentTransaction;
import com.vmops.model.billing.PaymentTransaction.State;
import com.vmops.persistence.AccountTypeDAO;
import com.vmops.persistence.CampaignPromotionDAO;
import com.vmops.persistence.PromotionTokenDAO;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.AuthorityService;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.RegistrationService;
import com.vmops.service.exceptions.UserAuthorizationInvalidException;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.CreditCardType;
import com.vmops.web.interceptors.PortalSessionThemeResolverImpl;
import com.vmops.web.interceptors.PortalThemeResolver;
import common.MockCloudInstance;
import common.MockConnectorUtilService;
import common.MockDeviceFraudDetectionService;

public class RegistrationControllerTest extends WebTestsBaseWithMockConnectors {

  private HttpServletResponse response;

  private ModelMap map;

  private MockSessionStatus status;

  private MockHttpServletRequest request;

  private MockHttpSession session;

  @Autowired
  private ChannelService channelService2;

  @Autowired
  private MockConnectorUtilService mockConnectorUtils;

  @Autowired
  private RegistrationService registrationService;

  @Autowired
  private RegistrationController controller;

  @JsonProperty
  private com.citrix.cpbm.access.Tenant tenant;

  @Autowired
  private AccountTypeDAO accountTypeDAO;

  @Autowired
  private CampaignPromotionDAO cmpdao;

  @Autowired
  private PromotionTokenDAO tokendao;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private BusinessTransactionService businessTransactionService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private CampaignPromotionDAO campaignpromotiondao;

  private final BootstrapActivator bootstrapActivator = new BootstrapActivator();

  private static boolean isMockInstanceCreated = false;

  private PaymentGatewayService ossConnector = null;

  @Autowired
  private TasksController tasksController;

  @Autowired
  private AuthorityService authorityService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private ChannelController channelController;

  private HttpServletRequest httpServletRequest;

  private HttpServletResponse httpServletResponse;

  private HttpSession httpSession;

  private PortalThemeResolver themeResolver;

  private static final String CHANNEL_FQDN_NAME = "PortalSessionThemeResolverImpl.FQDN_NAME";

  private static final String DELETED_CHANNEL_FQDN_NAME = "PortalSessionThemeResolverImpl.DELETED_FQDN_NAME";

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    status = new MockSessionStatus();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    session = new MockHttpSession();
    request.setSession(session);
    prepareMock(true, bootstrapActivator);
    if (!isMockInstanceCreated) {

      Service ossService = serviceDAO.find(7l);
      ossService.setEnabled(true);
      Service cloudService = serviceDAO.find(6l);
      connectorManagementService.getAllServiceInstances(cloudService);

      isMockInstanceCreated = true;
    }

    httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
    httpServletResponse = EasyMock.createMock(HttpServletResponse.class);
    httpSession = EasyMock.createMock(HttpSession.class);
    themeResolver = new PortalSessionThemeResolverImpl();
    asRoot();
  }

  @Override
  protected void prepareMock(boolean adaptor, BootstrapActivator bootstrapActivator) {
    super.prepareMock(adaptor, bootstrapActivator);
    MockCloudInstance instance = getMockCloudInstance();
    CloudConnector connector = instance.getCloudConnector();
    ossConnector = EasyMock.createMock(PaymentGatewayService.class);
    mockAccountLifecycleHandler = EasyMock.createMock(AccountLifecycleHandler.class);
    mockUserLifecycleHandler = EasyMock.createMock(UserLifecycleHandler.class);
    EasyMock.expect(connector.getServiceInstanceUUID()).andReturn("12345-786").anyTimes();
    EasyMock.replay(connector);
    ServiceInstanceConfiguration sic2 = connector.getServiceInstanceConfiguration();
    EasyMock.reset(ossConnector);
    EasyMock.expect(ossConnector.getAccountLifeCycleHandler()).andReturn(mockAccountLifecycleHandler).anyTimes();
    EasyMock.expect(ossConnector.getUserLifeCycleHandler()).andReturn(mockUserLifecycleHandler).anyTimes();
    EasyMock.expect(sic2.getInstanceUUID()).andReturn("003fa8ee-fba3-467f-a517-ed806dae8a80").anyTimes();
    final Capture<BigDecimal> amount = new Capture<BigDecimal>();
    EasyMock.expect(ossConnector.authorize(EasyMock.anyObject(Tenant.class), EasyMock.capture(amount)))
        .andAnswer(new IAnswer<PaymentTransaction>() {

          @Override
          public PaymentTransaction answer() throws Exception {
            return new PaymentTransaction(new Tenant(), 0, State.COMPLETED,
                com.vmops.model.billing.PaymentTransaction.Type.CAPTURE);
          }
        }).anyTimes();
    EasyMock.replay(ossConnector);
    EasyMock.replay(sic2);
  }

  @SuppressWarnings({
    "rawtypes"
  })
  @Test
  public void testRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class controllerClass = RegistrationController.class;
    Method expected = locateMethod(controllerClass, "signupStep1", new Class[] {
        ModelMap.class, HttpServletRequest.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/account_type"));

    expected = locateMethod(controllerClass, "register", new Class[] {
        UserRegistration.class, BindingResult.class, String.class, String.class, ModelMap.class, String.class,
        SessionStatus.class, HttpServletRequest.class
    });

    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/register"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "validateUsername", new Class[] {
      String.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/validate_username"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "updatePasswordAndVerifyEmail", new Class[] {
        String.class, String.class, HttpServletRequest.class, ModelMap.class, HttpSession.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/verify_user"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "verifyPhoneVerificationPIN", new Class[] {
        String.class, String.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/phoneverification/verify_pin"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "verifyEmail", new Class[] {
        HttpServletRequest.class, ModelMap.class, HttpSession.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/verify_email"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "requestCall", new Class[] {
        String.class, String.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/request_call"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "requestSMS", new Class[] {
        String.class, String.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/request_sms"));
    Assert.assertEquals(expected, handler);
  }

  @Test
  public void testSelects() throws Exception {
    List<String> countryNames = Arrays.asList("USA", "SINGAPORE", "INDIA");
    List<String> ccNames = Arrays.asList("AMEX", "VISA", "MASTERCARD", "DISCOVER");

    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));

    /*
     * for (AccountType disposition : registration.getDisposition()) { dispNames.contains(disposition.getName()); }
     */
    for (Country country : registration.getCountryList()) {
      countryNames.contains(country.getName());
    }
    for (CreditCardType card : registration.getCcTypes()) {
      ccNames.contains(card.getName());
    }

  }

  @Test
  public void testRegister() {
    try {
      String channelCode = null;
      String view = signupOnChannel(channelCode, null);
      Assert.assertEquals("register.userinfo", view);
      Assert.assertTrue(map.containsKey("registration"));
      UserRegistration userRegistration = (UserRegistration) map.get("registration");
      Assert.assertNotNull("Account Type Id Should not be null ", userRegistration.getAccountTypeId());

      Tenant tenant = ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject();
      Assert.assertEquals("1", tenant.getSourceChannel().getId().toString());
    } catch (Exception e) {
      Assert.fail();

    }
  }

  private void beforeRegisterCall(MockHttpServletRequest request, UserRegistration registration) {
    DispatcherServletWebRequest webRequest = new DispatcherServletWebRequest(request);
    RequestContextHolder.setRequestAttributes(webRequest);
    request.getSession().setAttribute("phoneVerificationPin", "12345");
    request.getSession().setAttribute("phoneNumber", "919591241025");
    Tenant tenant1 = new Tenant("New Co", accountTypeDAO.getDefaultRegistrationAccountType(), null, randomAddress(),
        true, currencyValueService.locateBYCurrencyCode("USD"), null);
    registration.setTenant((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant1));
    registration.setUserEnteredPhoneVerificationPin("12345");
  }

  @Test
  public void testRegisterDefault() throws Exception {
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();

    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    registration.setAllowSecondary(true);
    registration.setSecondaryAddress(new Address("steve", "creek", "cupertino", "CHAN", "95014", "IN"));
    AccountType disposition = accountTypeDAO.getDefaultRegistrationAccountType();
    BindingResult result = setupRegistration(disposition, registration);

    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    Assert.assertEquals("register.registration_success", view);
    Assert.assertTrue(status.isComplete());
    verifyRegistration(disposition, registration.getUser(), registration.getTenant());
    Assert.assertEquals(registration.getTenant().getObject(),
        ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject());

  }

  /*
   * @desc test register method with username of length 4
   * @author anishkumabTODO Fix DE7711 and remove @Ignore tag from below
   */
  @Test
  public void testRegisterInvalidUsernameLength() throws Exception {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    AccountType accountType = accountTypeDAO.getAccountTypeByName("RETAIL");

    UserRegistration registration = new UserRegistration();
    // Creating user and tenant objects for registration
    final String USERNAME = "abcd";
    User user = new User("test", "test", "test@test.com", USERNAME, VALID_PASSWORD, VALID_PHONE, VALID_TIMEZONE, null,
        null, null);
    BindingResult result = setUpRegistrationForGivenUser(user, registration, accountType);

    beforeRegisterCall(mockRequest, registration);
    int beforeUserCount = userDAO.count();

    String view = controller.userInfo(registration, result, map, null, status, mockRequest);
    Assert.assertTrue(result.hasErrors());
    Assert.assertEquals("register.userinfo", view);
    List<FieldError> fieldErrorList = result.getFieldErrors();
    int errorMessage = 0;
    for (FieldError fieldError : fieldErrorList) {
      if ("javax.validation.constraints.Size.message".equals(fieldError.getCode())) {
        errorMessage++;
      } else if ("javax.validation.constraints.MinimumSize.message".equals(fieldError.getCode())) {
        errorMessage++;
      }
    }
    Assert.assertTrue(errorMessage == 2);
    int afterUserCount = userDAO.count();
    Assert.assertEquals(afterUserCount, beforeUserCount);
  }

  /*
   * @desc test register method with invalid first name TODO Fix DE7711 and remove @Ignore tag from below
   */
  @Test
  public void testRegisterInvalidFirstName() throws Exception {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    AccountType accountType = accountTypeDAO.getAccountTypeByName("RETAIL");

    UserRegistration registration = new UserRegistration();
    // Creating user object for registration
    final String INVALIDFIRSTNAME = "!@#$%12345";
    User user = new User(INVALIDFIRSTNAME, "test", "test@test.com", "test1234", VALID_PASSWORD, VALID_PHONE,
        VALID_TIMEZONE, null, null, null);
    BindingResult result = setUpRegistrationForGivenUser(user, registration, accountType);

    beforeRegisterCall(mockRequest, registration);
    int beforeUserCount = userDAO.count();
    String view = controller.userInfo(registration, result, map, null, status, mockRequest);
    Assert.assertTrue(result.hasErrors());
    Assert.assertEquals("register.userinfo", view);
    List<FieldError> fieldErrorList = result.getFieldErrors();
    Assert.assertEquals("javax.validation.constraints.Pattern.message", fieldErrorList.get(0).getCode());
    int afterUserCount = userDAO.count();
    Assert.assertEquals(afterUserCount, beforeUserCount);
  }

  /*
   * @desc test register method with invalid last name TODO Fix DE7711 and remove @Ignore tag from below
   */
  @Test
  public void testRegisterInvalidLastName() throws Exception {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    AccountType accountType = accountTypeDAO.getAccountTypeByName("RETAIL");

    UserRegistration registration = new UserRegistration();
    // Creating user object for registration
    final String INVALIDLASTNAME = "!@#$%12345";
    User user = new User("test", INVALIDLASTNAME, "test@test.com", "test1234", VALID_PASSWORD, VALID_PHONE,
        VALID_TIMEZONE, null, null, null);
    BindingResult result = setUpRegistrationForGivenUser(user, registration, accountType);

    beforeRegisterCall(mockRequest, registration);
    int beforeUserCount = userDAO.count();
    String view = controller.userInfo(registration, result, map, null, status, mockRequest);
    Assert.assertTrue(result.hasErrors());
    Assert.assertEquals("register.userinfo", view);
    List<FieldError> fieldErrorList = result.getFieldErrors();
    Assert.assertEquals("javax.validation.constraints.Pattern.message", fieldErrorList.get(0).getCode());
    int afterUserCount = userDAO.count();
    Assert.assertEquals(afterUserCount, beforeUserCount);
  }

  @Test
  public void testRegisterTrial() throws Exception {
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/signup?pc=TESTPROMOCODE");
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));

    registration.setAcceptedTerms(true);
    AccountType disposition = accountTypeDAO.getTrialAccountType();
    BindingResult result = setupRegistration(disposition, registration);
    PromotionSignup promotionSignup = new PromotionSignup("test" + random.nextInt(), "Citrix",
        "PromotionSignUp@citrix.com");
    promotionSignup.setCreateBy(getRootUser());
    promotionSignup.setCurrency(Currency.getInstance("USD"));
    promotionSignup.setPhone("9999999999");

    CampaignPromotion campaignPromotion = new CampaignPromotion();
    campaignPromotion.setCode("USD" + random.nextInt());
    campaignPromotion.setCreateBy(getRootUser());
    campaignPromotion.setTrial(true);
    campaignPromotion.setUpdateBy(getRootUser());

    CampaignPromotionsInChannels cpic = new CampaignPromotionsInChannels(campaignPromotion,
        channelService.getDefaultServiceProviderChannel());
    campaignPromotion.getCampaignPromotionsInChannels().add(cpic);
    cmpdao.save(campaignPromotion);

    PromotionToken promotionToken = new PromotionToken(campaignPromotion, "TESTPROMOCODE");
    promotionToken.setCreateBy(getRootUser());
    tokendao.save(promotionToken);

    promotionSignup.setPromotionToken(promotionToken);

    registration.setTrialCode("TESTPROMOCODE");

    DispatcherServletWebRequest webRequest = new DispatcherServletWebRequest(mockRequest);
    RequestContextHolder.setRequestAttributes(webRequest);
    mockRequest.getSession().setAttribute("phoneVerificationPin", "12345");
    mockRequest.getSession().setAttribute("phoneNumber", "919591241025");
    registration.setUserEnteredPhoneVerificationPin("12345");

    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    Assert.assertEquals("register.registration_success", view);
    Assert.assertTrue(status.isComplete());
    verifyRegistration(disposition, registration.getUser(), registration.getTenant());
    Assert.assertEquals(registration.getTenant().getAccountId(),
        ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getAccountId());
  }

  @Test
  public void testValidateSuffix() throws Exception {
    Assert.assertEquals("true", controller.validateSuffix("test"));
    asRoot();
    Tenant defaultTenant = getDefaultTenant();
    defaultTenant.setUsernameSuffix("test");
    tenantService.update(defaultTenant);
    Assert.assertEquals("false", controller.validateSuffix("test"));
  }

  @Test
  public void testRegisterBindingHasErrors() throws Exception {
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    mockRequest.setRemoteAddr("1.1.1.1");
    User user = new User("test", "test", "testtest.com", "testuser", VALID_PASSWORD, VALID_PHONE, VALID_TIMEZONE, null,
        null, getRootUser());
    user.setAddress(randomAddress());
    Tenant newTenant = new Tenant("New Co", accountTypeDAO.getDefaultRegistrationAccountType(), null, randomAddress(),
        true, currencyValueService.locateBYCurrencyCode("USD"), null);
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setUser((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    registration.setTenant((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(newTenant));
    registration.setDisposition(accountTypeDAO.getDefaultRegistrationAccountType());
    BindingResult result = validate(registration);
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    Assert.assertEquals("register.moreuserinfo", view);
    Assert.assertFalse(status.isComplete());
    Assert.assertTrue(((UserRegistration) result.getTarget()).getUser().getObject().getId() == 0);
    Assert.assertTrue(((UserRegistration) result.getTarget()).getTenant().getId() == 0);
  }

  @Test
  public void testRegisterUsernameExists() throws Exception {
    User existing = userDAO.findAll(null).get(1);
    String response = controller.validateUsername(existing.getUsername());
    Assert.assertEquals("false", response);
  }

  @Test
  public void testRegisterPostCaptchaFail() throws Exception {
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/signup");
    mockRequest.setRemoteAddr("1.1.1.1");
    User user = new User("test", "test", "testtest.com", "testuser", VALID_PASSWORD, VALID_PHONE, VALID_TIMEZONE, null,
        null, getRootUser());
    user.setAddress(randomAddress());
    Tenant newTenant = new Tenant("New Co", accountTypeDAO.getDefaultRegistrationAccountType(), null, randomAddress(),
        true, currencyValueService.locateBYCurrencyCode("USD"), null);
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setUser((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    registration.setTenant((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(newTenant));
    BindingResult result = new BindException(registration, "registration");
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "CAPTCHA_FAIL", map, "1", status, mockRequest);
    Assert.assertEquals("register.moreuserinfo", view);
    Assert.assertFalse(status.isComplete());
    Assert.assertTrue(result.hasGlobalErrors());
    Assert.assertTrue(result.getGlobalErrorCount() == 1);
    Assert.assertEquals("errors.registration.captcha", result.getGlobalError().getCode());
    Assert.assertEquals("captcha.error", map.get("registrationError"));
  }

  @Test
  public void testRegisterTrialForInvalidPromoCode() throws Exception {
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/signup?pc=testpromo");
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    AccountType disposition = accountTypeDAO.getTrialAccountType();
    BindingResult result = setupRegistration(disposition, registration);
    PromotionSignup promotionSignup = new PromotionSignup("test" + random.nextInt(), "Citrix",
        "PromotionSignUp@citrix.com");
    promotionSignup.setCreateBy(getRootUser());
    promotionSignup.setCurrency(Currency.getInstance("USD"));
    promotionSignup.setPhone("9999999999");

    CampaignPromotion campaignPromotion = new CampaignPromotion();
    campaignPromotion.setCode("USD" + random.nextInt());
    campaignPromotion.setCreateBy(getRootUser());
    campaignPromotion.setTrial(true);
    campaignPromotion.setUpdateBy(getRootUser());
    campaignpromotiondao.save(campaignPromotion);

    PromotionToken promotionToken = new PromotionToken(campaignPromotion, "TESTPROMOCODE");
    promotionToken.setCreateBy(getRootUser());
    tokendao.save(promotionToken);

    promotionSignup.setPromotionToken(promotionToken);
    registration.setTrialCode("testpromo");
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    Assert.assertEquals("register.fail", view);
    Assert.assertFalse(status.isComplete());

  }

  @Test
  public void testRegisterNotAcceptedTerms() throws Exception {
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    AccountType disposition = accountTypeDAO.getOnDemandPostPaidAccountType();
    BindingResult result = setupRegistration(disposition, registration);
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    Assert.assertEquals("register.moreuserinfo", view);
    Assert.assertFalse(status.isComplete());
    Assert.assertTrue(result.hasFieldErrors());
    Assert.assertTrue(result.getFieldErrorCount() == 1);
    Assert.assertEquals("AssertTrue", result.getFieldError("acceptedTerms").getCode());
  }

  @Test
  public void testValidateUsername() throws Exception {
    asAnonymous();
    User existing = userDAO.findAll(null).get(0);
    String response = controller.validateUsername(existing.getUsername());
    Assert.assertFalse(Boolean.parseBoolean(response));

    response = controller.validateUsername("nonexistent");
    Assert.assertTrue(Boolean.parseBoolean(response));
  }

  @Test
  public void testVerifyEmail() throws Exception {
    User user = createDisabledUser();
    String auth = user.getAuthorization(0);

    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute("regAuth", auth);
    mockSession.setAttribute("regParam", user.getParam());

    String view = controller.verifyEmail(getRequestTemplate(HttpMethod.GET, "/verify_email"), map, mockSession);
    String redirect = "redirect:/?verify";

    Assert.assertEquals(redirect, view);
    userDAO.flush();
    User found = userDAO.find(user.getId());
    Assert.assertEquals(user, found);
    Assert.assertTrue(found.isEnabled());

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetLoginPageUIRelatedConfigs() {
    HashMap<String, Object> configs = (HashMap<String, Object>) controller.getLoginPageUIRelatedConfigs();
    Assert.assertEquals(4, configs.size());
    Assert.assertEquals("false", configs.get("isDirectoryServiceAuthenticationON"));
    Assert.assertEquals("N", configs.get("showSuffix"));
    Assert.assertEquals("true", configs.get("showSuffixDropBox"));
    List<String> suffixList = (List<String>) configs.get("suffixList");
    Assert.assertEquals(0, suffixList.size());

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_directory_service_enabled);
    configuration.setValue("true");
    configurationService.update(configuration);

    configuration = configurationService.locateConfigurationByName(Names.com_citrix_cpbm_directory_mode);
    configuration.setValue("pull");
    configurationService.update(configuration);

    configs = (HashMap<String, Object>) controller.getLoginPageUIRelatedConfigs();
    Assert.assertEquals(4, configs.size());
    Assert.assertEquals("true", configs.get("isDirectoryServiceAuthenticationON"));
    Assert.assertEquals("N", configs.get("showSuffix"));
    Assert.assertEquals("true", configs.get("showSuffixDropBox"));
    suffixList = (List<String>) configs.get("suffixList");
    Assert.assertEquals(0, suffixList.size());

    configuration = configurationService.locateConfigurationByName(Names.com_citrix_cpbm_username_duplicate_allowed);
    configuration.setValue("Y");
    configurationService.update(configuration);

    configs = (HashMap<String, Object>) controller.getLoginPageUIRelatedConfigs();
    Assert.assertEquals(4, configs.size());
    Assert.assertEquals("true", configs.get("isDirectoryServiceAuthenticationON"));
    Assert.assertEquals("Y", configs.get("showSuffix"));
    Assert.assertEquals("true", configs.get("showSuffixDropBox"));
    suffixList = (List<String>) configs.get("suffixList");
    Assert.assertEquals(0, suffixList.size());

    configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_login_screen_tenant_suffix_dropdown_enabled);
    configuration.setValue("false");
    configurationService.update(configuration);

    configs = (HashMap<String, Object>) controller.getLoginPageUIRelatedConfigs();
    Assert.assertEquals(4, configs.size());
    Assert.assertEquals("true", configs.get("isDirectoryServiceAuthenticationON"));
    Assert.assertEquals("Y", configs.get("showSuffix"));
    Assert.assertEquals("false", configs.get("showSuffixDropBox"));
    suffixList = (List<String>) configs.get("suffixList");
    Assert.assertEquals(0, suffixList.size());
  }

  @Test
  public void testVerifyEmailWithNoPasswordSet() throws Exception {
    User user = createUserWithoutPassword();
    String auth = user.getAuthorization(0);

    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute("regAuth", auth);
    mockSession.setAttribute("regParam", user.getParam());

    String view = controller.verifyEmail(getRequestTemplate(HttpMethod.GET, "/verify_email"), map, mockSession);
    String redirect = "register.setpassword";

    Assert.assertEquals(redirect, view);
    userDAO.flush();
  }

  @Test(expected = UserAuthorizationInvalidException.class)
  public void testVerifyEmailBadAuth() throws Exception {
    User user = createDisabledUser();

    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute("regAuth", "garbage");
    mockSession.setAttribute("regParam", user.getParam());

    controller.verifyEmail(getRequestTemplate(HttpMethod.GET, "/verify_email"), map, mockSession);
  }

  @Test(expected = UserAuthorizationInvalidException.class)
  public void testVerifyEmailWrongAuth() throws Exception {
    User user = createDisabledUser();
    String auth = user.getAuthorization(1);
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute("regAuth", auth);
    mockSession.setAttribute("regParam", user.getParam());
    controller.verifyEmail(getRequestTemplate(HttpMethod.GET, "/verify_email"), map, new MockHttpSession());
  }

  @Test
  public void testVerifyUserPasswordNotSet() {
    User user = createUserWithoutPassword();
    String auth = user.getAuthorization(1);
    String view = controller.updatePasswordAndVerifyEmail(auth, user.getParam(), new MockHttpServletRequest(), map,
        new MockHttpSession());
    String expectedView = "register.setpassword";
    Assert.assertEquals(view, expectedView);
  }

  @Test
  public void testVerifyUserPasswordNotSetWithDirectoryServerPullOn() {
    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_directory_service_enabled);
    configuration.setValue("true");
    configurationService.update(configuration);

    configuration = configurationService.locateConfigurationByName(Names.com_citrix_cpbm_directory_mode);
    configuration.setValue("pull");
    configurationService.update(configuration);

    User user = createUserWithoutPassword();
    String auth = user.getAuthorization(1);
    String view = controller.updatePasswordAndVerifyEmail(auth, user.getParam(), new MockHttpServletRequest(), map,
        new MockHttpSession());
    String expectedView = "redirect:/portal/verify_email";
    Assert.assertEquals(view, expectedView);
  }

  @Test
  public void testVerifyUserEmailNotVerified() {
    User user = createUserWithoutEmailVerification();
    String auth = user.getAuthorization(1);
    String view = controller.updatePasswordAndVerifyEmail(auth, user.getParam(), new MockHttpServletRequest(), map,
        new MockHttpSession());
    String expectedView = "redirect:/portal/verify_email";
    Assert.assertEquals(view, expectedView);
  }

  @Test
  public void testVerifyEnabledUser() {
    User user = createEmailVerifiedUser();
    String auth = user.getAuthorization(1);
    String view = controller.updatePasswordAndVerifyEmail(auth, user.getParam(), new MockHttpServletRequest(), map,
        new MockHttpSession());
    String expectedView = "redirect:/portal/login";
    Assert.assertEquals(view, expectedView);
  }

  private User createDisabledUser() {
    User user = new User("first", "last", "verify@verify.com", "disableduser", VALID_PASSWORD, VALID_PHONE,
        VALID_TIMEZONE, getDefaultTenant(), userProfile, getRootUser());
    userDAO.save(user);
    userDAO.flush();
    return user;
  }

  private User createUserWithoutEmailVerification() {
    User user = new User("first1", "last1", "verify1@verify.com", "disableduser", VALID_PASSWORD, VALID_PHONE,
        VALID_TIMEZONE, getDefaultTenant(), userProfile, getRootUser());
    user.setEmailVerified(false);
    userDAO.save(user);
    userDAO.flush();
    return user;
  }

  private User createUserWithoutPassword() {
    User user = new User("first2", "last2", "verify2@verify.com", "disableduser", null, VALID_PHONE, VALID_TIMEZONE,
        getDefaultTenant(), userProfile, getRootUser());
    userDAO.save(user);
    userDAO.flush();
    return user;
  }

  private User createEmailVerifiedUser() {
    User user = new User("first", "last", "verify@verify.com", "disableduser", VALID_PASSWORD, VALID_PHONE,
        VALID_TIMEZONE, getDefaultTenant(), userProfile, getRootUser());
    user.setEmailVerified(true);
    userDAO.save(user);
    userDAO.flush();
    return user;
  }

  private BindingResult setupRegistration(AccountType disposition, UserRegistration registration) {
    String rand = "test" + random.nextInt();
    Address address = randomAddress();
    User user = new User("test", "test", rand + "@test.com", rand, VALID_PASSWORD, VALID_PHONE, null, null, null, null);
    user.setAddress(address);
    Tenant newTenant = new Tenant("New Co", disposition, null, address, true,
        currencyValueService.locateBYCurrencyCode("USD"), null);
    registration.setUser((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    registration.setTenant((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(newTenant));
    registration.setDisposition(disposition);
    registration.setAccountTypeId(disposition.getId() + "");
    BindingResult result = null;
    try {
      result = validate(registration);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }

  private BindingResult setUpRegistrationForGivenUser(User user, UserRegistration registration, AccountType accountType)
      throws Exception {

    Address address = randomAddress();
    user.setAddress(address);
    Tenant newTenant = new Tenant("New Co", accountType, null, address, true,
        currencyValueService.locateBYCurrencyCode("USD"), null);
    registration.setUser((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    registration.setTenant((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(newTenant));
    registration.setDisposition(accountType);
    registration.setAccountTypeId(accountType.getId() + "");
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);

    BindingResult result = validate(registration); // Ask: Check UserStep1Validator
    return result;
  }

  private void verifyRegistration(AccountType disposition, com.citrix.cpbm.access.User user,
      com.citrix.cpbm.access.Tenant tenant) {
    Assert.assertTrue(user.getObject().getId() != 0);
    Assert.assertTrue(tenant.getObject().getId() != 0);
    User foundUser = userDAO.find(user.getObject().getId());
    Assert.assertEquals(user, foundUser);
    Assert.assertEquals(getPortalUser(), user.getObject().getCreatedBy());
    Assert.assertFalse(foundUser.isEnabled());
    tenantDAO.merge(tenant.getObject());
    Tenant foundTenant = tenantDAO.find(tenant.getId());
    Assert.assertEquals(tenant, foundTenant);
    Assert.assertEquals(getPortalUser(), tenant.getObject().getCreatedBy());
    Assert.assertEquals(foundUser, tenant.getOwner());
    Assert.assertEquals(foundUser, foundTenant.getOwner());
    Assert.assertEquals(foundTenant, foundUser.getTenant());
    Assert.assertNotNull(foundTenant.getAccountId());
    Assert.assertTrue(ACCOUNT_NUMBER_PATTERN.matcher(foundTenant.getAccountId()).matches());
    Assert.assertEquals(disposition, foundTenant.getAccountType());
    Assert.assertEquals(disposition.getSupportedPaymentModes().get(0), foundTenant.getTenantExtraInformation()
        .getPaymentMode());
    Assert.assertEquals(currencyValueDAO.findByCurrencyCode("USD"), foundTenant.getCurrency());
  }

  @Test
  public void testRequestCall() throws JsonGenerationException, JsonMappingException, IOException {
    request.getSession().setAttribute("phoneVerificationPin", "12345");
    Map<String, String> keyvaluepairs = controller.requestCall("INVALIDNUMBER", "INVALIDCODE", request);

    try {
      Assert.assertTrue("failed".equalsIgnoreCase(keyvaluepairs.get("result")));

      Map<String, String> keyvaluepairs1 = controller.requestCall("123456", "91", request);
      Assert.assertTrue("success".equalsIgnoreCase(keyvaluepairs1.get("result")));
    } catch (Exception e) {

    }

    try {
      request.getSession().setAttribute("phoneVerificationPin", "ABC");
      keyvaluepairs = controller.requestCall("INVALIDNUMBER", "INVALIDCODE", request);
      Assert.assertTrue("failed".equalsIgnoreCase(keyvaluepairs.get("result")));

      Map<String, String> keyvaluepairs1 = controller.requestCall("123456", "91", request);
      Assert.assertTrue("failed".equalsIgnoreCase(keyvaluepairs1.get("result")));
    } catch (Exception e) {

    }
  }

  @Test
  public void testRequestSMS() throws JsonGenerationException, JsonMappingException, IOException {
    request.getSession().setAttribute("phoneVerificationPin", "12345");
    Map<String, String> keyvaluepairs = controller.requestSMS("INVALIDNUMBER", "INVALIDCODE", request);
    Assert.assertEquals("failed", keyvaluepairs.get("result"));

    Map<String, String> keyvaluepairs1 = controller.requestSMS("123456", "91", request);
    Assert.assertEquals("success", keyvaluepairs1.get("result"));
  }

  @Test
  public void testManualActivationFlag() throws Exception {
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    AccountType disposition = accountTypeDAO.getDefaultRegistrationAccountType();
    disposition.setManualActivation(true);
    BindingResult result = setupRegistration(disposition, registration);
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    Assert.assertEquals(2, eventListener.getEvents().size());
    PortalEvent event = eventListener.getEvents().get(0);
    Assert.assertTrue(event.getPayload() instanceof VerifyEmailRequest);
    Assert.assertEquals("register.registration_success", view);
    Assert.assertTrue(status.isComplete());
    verifyRegistration(disposition, registration.getUser(), registration.getTenant());
    Assert.assertEquals(registration.getTenant().getObject(),
        ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject());
  }

  @Test
  public void testVerifyPhoneVerificationPIN() throws Exception {
    request.getSession().setAttribute("phoneVerificationPin", "12345");
    request.getSession().setAttribute("phoneNumber", "123456789");
    String failedResult = controller.verifyPhoneVerificationPIN("54321", "123456789", request);
    Assert.assertEquals("failed", failedResult);

    String successResult = controller.verifyPhoneVerificationPIN("12345", "123456789", request);
    Assert.assertEquals("success", successResult);

  }

  @Test
  public void testEmailVerifiedEventForManualActivationAccountType() {
    // Sign up for manual activation account type
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    List<AccountType> accountTypes = accountTypeDAO.getManualRegistrationAccountTypes();
    AccountType disposition = null;
    for (AccountType accountType : accountTypes) {
      if (accountType.isManualActivation()) {
        disposition = accountType;
      }
    }
    BindingResult result = null;
    try {
      result = setupRegistration(disposition, registration);
    } catch (Exception e) {
      e.printStackTrace();
    }
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    Assert.assertEquals("register.registration_success", view);
    Assert.assertTrue(status.isComplete());
    verifyRegistration(disposition, registration.getUser(), registration.getTenant());
    Assert.assertEquals(registration.getTenant().getObject(),
        ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject());
    // account activation request event
    Assert.assertEquals(2, eventListener.getEvents().size());
    PortalEvent verifyEmailRequest = eventListener.getEvents().get(0);
    Assert.assertTrue(verifyEmailRequest.getPayload() instanceof VerifyEmailRequest);
    eventListener.clear();
    // verify email
    String auth = registration.getUser().getObject().getAuthorization(0);
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute("regAuth", auth);
    mockSession.setAttribute("regParam", registration.getUser().getObject().getParam());
    controller.verifyEmail(getRequestTemplate(HttpMethod.GET, "/verify_email"), map, mockSession);
    // Since tenant is not at activated, we will get only Welcome email
    Assert.assertEquals(1, eventListener.getEvents().size());
    PortalEvent event = eventListener.getEvents().get(0);
    Assert.assertTrue(event.getPayload() instanceof EmailVerified);
  }

  @Test
  public void testWelcomeEmailEventForRetail() {
    // Sign up for manual activation account type
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    AccountType disposition = accountTypeDAO.getDefaultSelfRegistrationAccountType();
    BindingResult result = null;
    try {
      result = setupRegistration(disposition, registration);
    } catch (Exception e) {
      e.printStackTrace();
    }
    beforeRegisterCall(mockRequest, registration);
    controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    // Tenant Activation Event
    // Assert.assertEquals(1, eventListener.getEvents().size());
    // PortalEvent tenantActivationEvent = eventListener.getEvents().get(0);
    // Assert.assertTrue(tenantActivationEvent.getPayload() instanceof TenantActivation);
    eventListener.clear();
    // verify email
    String auth = registration.getUser().getObject().getAuthorization(0);
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute("regAuth", auth);
    mockSession.setAttribute("regParam", registration.getUser().getObject().getParam());
    controller.verifyEmail(getRequestTemplate(HttpMethod.GET, "/verify_email"), map, mockSession);
    // Since tenant is at activated, we will get Email Verified event
    Assert.assertEquals(1, eventListener.getEvents().size());
  }

  @Test
  public void testWelcomeEmailAndActivationEmailEventsForRetail() {
    // Sign up for manual activation account type
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    AccountType disposition = accountTypeDAO.getDefaultSelfRegistrationAccountType();
    BindingResult result = null;
    try {
      result = setupRegistration(disposition, registration);
    } catch (Exception e) {
      e.printStackTrace();
    }
    beforeRegisterCall(mockRequest, registration);
    controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    // Tenant Activation Event
    // Assert.assertEquals(1, eventListener.getEvents().size());
    // PortalEvent tenantActivationEvent = eventListener.getEvents().get(0);
    // Assert.assertTrue(tenantActivationEvent.getPayload() instanceof TenantActivation);
    eventListener.clear();
    // verify email
    String auth = registration.getUser().getObject().getAuthorization(0);
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute("regAuth", auth);
    mockSession.setAttribute("regParam", registration.getUser().getObject().getParam());
    controller.verifyEmail(getRequestTemplate(HttpMethod.GET, "/verify_email"), map, mockSession);
    // Since tenant is at activated, we will get only Welcome email and ActivationEmail events
    Assert.assertEquals(1, eventListener.getEvents().size());
  }

  @Test
  public void testWelcomeEmailAndActivationEmailEventsForCorporate() {
    // Sign up for manual activation account type
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    List<AccountType> accountTypes = accountTypeDAO.getManualRegistrationAccountTypes();
    AccountType disposition = null;
    for (AccountType accountType : accountTypes) {
      if (accountType.isManualActivation()) {
        disposition = accountType;
      }
    }
    BindingResult result = null;
    try {
      result = setupRegistration(disposition, registration);
    } catch (Exception e) {
      e.printStackTrace();
    }
    beforeRegisterCall(mockRequest, registration);
    controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);
    // Tenant Activation Event
    // Assert.assertEquals(1, eventListener.getEvents().size());
    // PortalEvent accountActivationRequestEvent = eventListener.getEvents().get(0);
    // Assert.assertTrue(accountActivationRequestEvent.getPayload() instanceof AccountActivationRequestEvent);
    eventListener.clear();

    // verify email
    String auth = registration.getUser().getObject().getAuthorization(0);
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute("regAuth", auth);
    mockSession.setAttribute("regParam", registration.getUser().getObject().getParam());
    controller.verifyEmail(getRequestTemplate(HttpMethod.GET, "/verify_email"), map, mockSession);
    // Since tenant is at activated, we will get EmailVerified Event
    Assert.assertEquals(1, eventListener.getEvents().size());
    eventListener.clear();

    Tenant tennt = tenantService.get(registration.getTenant().getUuid());
    completeBusinessTransactionsForTenant(tennt);
    tenantService.changeState(registration.getTenant().getUuid(), "ACTIVE", null, "Manual");
    PortalEvent tenantActivationEvent = eventListener.getEvents().get(0);
    Assert.assertTrue(tenantActivationEvent.getPayload() instanceof TenantActivation);
    eventListener.clear();
  }

  @Test
  public void testTrialRegister() {
    Channel channel = channelService.locateChannel("Channel2");
    String register = controller.trialRegister("trial_camp", "Channel2", map);
    Assert.assertEquals(register, new String("register.userinfo"));
    Assert.assertEquals(map.get("title"), new String("page.order_now"));
    Assert.assertEquals(new String("http://www.home.com"), map.get("homeUrl"));
    Assert.assertNotNull(map.get("registration"));
    UserRegistration registration = (UserRegistration) map.get("registration");
    Assert.assertNotNull(registration.getTrialCode());
    Assert.assertNotNull(registration.getTenant());
    Assert.assertEquals(map.get("channelParam"), channel.getParam());
    register = controller.trialRegister("trial_camp", null, map);
    Assert.assertEquals(register, new String("register.userinfo"));
  }

  @Test
  public void testInvalidTrailRegister() {

    try {
      Assert.assertEquals(controller.trialRegister("wrong_trialcode", null, map), new String("trial.invalid"));
      Assert.fail();
    } catch (IllegalArgumentException e) {
    }

  }

  @Test
  public void testsetPassword() {

    User user = createUserWithoutPassword();
    Assert.assertNull(user.getPassword());
    session.setAttribute("regParam", user.getParam());
    String setPassword = controller.setPassword("Test123#", session);
    Assert.assertNotNull(user.getPassword());
    Assert.assertEquals(setPassword, new String("redirect:/portal/verify_email"));
    Assert.assertTrue(user.authenticate("Test123#"));
  }

  @Test
  public void testback() {
    Tenant tenant1 = tenantService.getTenantByParam("id", "1", false);
    UserRegistration registration = new UserRegistration();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setTenant((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant1));
    String back = controller.back(registration, map);
    Assert.assertEquals(back, new String("register.userinfo"));
    Assert.assertEquals(map.get("title"), new String("page.order_now"));
    Assert.assertEquals(map.get("registration"), registration);
    Assert.assertEquals(((com.citrix.cpbm.access.Tenant) map.get("tenant")).getAccountId(), registration.getTenant()
        .getAccountId());
    Assert.assertEquals(map.get("page"), Page.HOME);
  }

  @Test
  public void testutilityratesLightbox() {
    String utilityrates = controller.utilityratesLightbox("0bd2ab86-7402-4815-b785-55c8d9090580", "USD", null, null,
        map);
    Assert.assertNotNull(utilityrates);
    Assert.assertTrue(map.containsAttribute("currency"));
    Assert.assertTrue(map.containsAttribute("startDate"));
    Assert.assertEquals(map.get("currency"), currencyValueService.locateBYCurrencyCode("USD"));
    Assert.assertNotNull(map.get("startDate"));
    utilityrates = controller.utilityratesLightbox("0bd2ab86-7402-4", "USD", null, null, map);
    Assert.assertNotNull(utilityrates);
    Assert.assertTrue(map.containsAttribute("currency"));
    Assert.assertTrue(map.containsAttribute("startDate"));
    Assert.assertEquals(map.get("currency"), currencyValueService.locateBYCurrencyCode("USD"));
    Assert.assertNotNull(map.get("startDate"));
  }

  /*
   * Description : Test signup with promocode added in channel. Author : Avinashg
   */
  @Test
  public void testPromoCodeAddedInGivenChannel() throws Exception {
    AccountType accountType = accountTypeDAO.find("3");

    Channel actualChannel = channelDAO.find(3L);
    Tenant tenant2 = new Tenant("Acme Corp " + random.nextInt(), accountType, null, randomAddress(), true,
        currencyValueDAO.findByCurrencyCode("USD"), null);
    com.citrix.cpbm.access.Tenant tenant1 = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant2);
    UserRegistration userRegistration = new UserRegistration();
    userRegistration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    userRegistration.setTenant(tenant1);
    userRegistration.setAccountTypeId("3");
    controller.signup(map, null, campaignpromotiondao.find("1").getCode(), actualChannel.getCode(), "3", status,
        request);
    Assert.assertEquals(actualChannel.getParam(), map.get("channelParam"));

  }

  @Test
  public void testSignupStep1() {
    ModelMap model = new ModelMap();
    String register = controller.signupStep1(model, request);
    Assert.assertEquals(register, new String("register.account_type"));
  }

  @Test
  public void testUserInfo() {
    ModelMap model = new ModelMap();
    UserRegistration registration = new UserRegistration();
    registration.setAcceptedTerms(true);
    AccountType disposition = accountTypeDAO.getTrialAccountType();
    BindingResult result;
    try {
      result = setupRegistration(disposition, registration);
      String register = controller.userInfo(registration, result, model, null, status, request);
      Assert.assertEquals("register.moreuserinfo", register);
    } catch (Exception e) {
      Assert.fail();
    }

  }

  @Test
  public void testListAnonymousProductBundles() {
    List<ProductBundleRevision> productBundleRevisions = controller.listAnonymousProductBundles(getDefaultTenant()
        .getSourceChannel().toString(), "USD", false, null);
    Assert.assertNotNull(productBundleRevisions);
  }

  @Test
  public void testPhoneverification() {
    ModelMap model = new ModelMap();
    UserRegistration registration = new UserRegistration();
    List<Country> countryList = new ArrayList<Country>();
    countryList.addAll(countryService.getCountries(null, null, null, 1, 12, null, false));
    registration.setCountryList(countryList);
    AccountType disposition = accountTypeDAO.getTrialAccountType();
    BindingResult result;
    try {
      result = setupRegistration(disposition, registration);
      String value = controller.phoneverification(registration, result, "abs", "abs", model, null, status, request);
      Assert.assertEquals(value, new String("register.phoneverification"));
    } catch (Exception e) {
      Assert.fail();
    }
    try {
      result = setupRegistration(disposition, registration);
      controller.phoneverification(registration, result, "abs", null, model, null, status, request);
    } catch (Exception e) {
      Assert.fail();
    }
    try {
      countryList = null;
      result = setupRegistration(disposition, registration);
      controller.phoneverification(registration, result, "abs", null, model, null, status, request);
      Assert.assertTrue(true);
    } catch (Exception e) {
      Assert.fail();
    }

  }

  @Test
  public void testValidateMailDomain() {
    String value = controller.validateMailDomain(VALID_EMAIL, null);
    Assert.assertNotNull(value);
  }

  @Test
  public void testUserInfoWithIntranetOnlyModeEnabled() throws Exception {
    configurationService.clearConfigurationCache(true, "");
    com.vmops.model.Configuration isIntranetModeEnabled = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_use_intranet_only);
    isIntranetModeEnabled.setValue("true");
    configurationService.update(isIntranetModeEnabled);

    ModelMap model = new ModelMap();
    UserRegistration registration = new UserRegistration();
    AccountType disposition = accountTypeDAO.getTrialAccountType();
    BindingResult result;
    registration.setAcceptedTerms(true);
    result = setupRegistration(disposition, registration);
    String view = controller.userInfo(registration, result, model, null, status, request);
    Assert.assertEquals("register.moreuserinfo", view);
    Assert.assertFalse(model.containsKey("showCaptcha"));
    Assert.assertFalse(model.containsKey("recaptchaPublicKey"));

  }

  @Test
  public void testUserInfoWithIntranetOnlyModeDisabled() throws Exception {
    configurationService.clearConfigurationCache(true, "");
    com.vmops.model.Configuration isIntranetModeEnabled = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_use_intranet_only);
    isIntranetModeEnabled.setValue("false");
    configurationService.update(isIntranetModeEnabled);

    ModelMap model = new ModelMap();
    UserRegistration registration = new UserRegistration();
    AccountType disposition = accountTypeDAO.getTrialAccountType();
    BindingResult result;
    registration.setAcceptedTerms(true);
    result = setupRegistration(disposition, registration);
    String view = controller.userInfo(registration, result, model, null, status, request);
    Assert.assertEquals("register.moreuserinfo", view);
    Assert.assertTrue(model.containsKey("showCaptcha"));
    Assert.assertTrue(Boolean.valueOf(model.get("showCaptcha").toString()));
    Assert.assertTrue(model.containsKey("recaptchaPublicKey"));

  }

  /**
   * "Create an account on a channel which Has overridden default time zone. Verify that default time zone of the master
   * user is same as that is mentioned in that channel. Default time-zone of a user can be found by going to My Profile
   * and removing any selection from Time zone. "
   * 
   * @throws Exception
   */
  @Test
  public void testAccountCreationWithOverriddenTimezoneValueAtChannel() throws Exception {

    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();

    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    registration.setAllowSecondary(true);
    registration.setSecondaryAddress(new Address("steve", "creek", "cupertino", "CHAN", "95014", "IN"));
    AccountType disposition = accountTypeDAO.getDefaultRegistrationAccountType();
    BindingResult result = setupRegistration(disposition, registration);
    Channel fetchedChannel = channelService.getChannelById("3");
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, fetchedChannel.getUuid(), status,
        mockRequest);
    Assert.assertEquals("register.registration_success", view);
    Assert.assertTrue(status.isComplete());
    Assert.assertEquals(registration.getTenant().getObject(),
        ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject());

    Tenant fetchedTenant = tenantService.getTenantByParam("uuid", registration.getTenant().getUuid(), false);
    Assert.assertEquals(fetchedChannel.getUuid(), fetchedTenant.getSourceChannel().getUuid());
    Assert.assertEquals(fetchedChannel.getChannelBrandingConfigurations().getDefaultTimeZone(),
        ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject().getOwner().getTimeZone());

  }

  /**
   * "Create an account on a channel which has not overridden default time zone. Verify that default time zone of the
   * master user is same as that is mentioned in that channel. Default time-zone of a user can be found by going to My
   * Profile and removing any selection from Time zone. "
   * 
   * @throws Exception
   */
  @Test
  public void testAccountCreationWithoutOverriddenTimezoneValueAtChannel() throws Exception {

    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();

    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    registration.setAllowSecondary(true);
    registration.setSecondaryAddress(new Address("steve", "creek", "cupertino", "CHAN", "95014", "IN"));
    AccountType disposition = accountTypeDAO.getDefaultRegistrationAccountType();
    BindingResult result = setupRegistration(disposition, registration);

    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, channelService.getChannelById("5")
        .getUuid(), status, mockRequest);
    Assert.assertEquals("register.registration_success", view);
    Assert.assertTrue(status.isComplete());
    Assert.assertEquals(registration.getTenant().getObject(),
        ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject());

    String defTimezone = configurationService.locateConfigurationByName(
        Names.com_citrix_cpbm_portal_settings_default_timezone).getValue();
    Assert.assertEquals(defTimezone, ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject().getOwner()
        .getTimeZone());

  }

  @Test
  public void testSignupUsingFQDN() {

    Channel fqdnChannel = channelService.getChannelById("15");

    String view = signupOnChannel(fqdnChannel.getId(), null);

    Assert.assertEquals("register.userinfo", view);
    Assert.assertTrue(map.containsKey("registration"));

    Assert.assertEquals(fqdnChannel.getParam(), map.get("channelParam"));

  }

  @Test
  public void testSignupUsingChannelCodeInUrl() {

    Channel channelInUrl = channelService.getChannelById("15");

    String view = signupOnChannel(15L, channelInUrl.getCode());

    Assert.assertEquals("register.userinfo", view);
    Assert.assertTrue(map.containsKey("registration"));

    Assert.assertEquals(channelInUrl.getParam(), map.get("channelParam"));

  }

  @Test(expected = Exception.class)
  @Ignore
  public void testSignupUsingDifferentChannelCodeInUrlAndFQDN() {
    Channel fqdnChannel = channelService.getChannelById("13");
    Channel channelInUrl = channelService.getChannelById("15");

    signupOnChannel(fqdnChannel.getId(), channelInUrl.getCode());

  }

  private String signupOnChannel(Long fqdnChannelId, String channelCodeInUrl) {
    AccountType trialAccountType = accountTypeDAO.find(3L);
    UserRegistration registration = new UserRegistration();
    registration.setAcceptedTerms(true);

    setupRegistration(trialAccountType, registration);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    DispatcherServletWebRequest webRequest = new DispatcherServletWebRequest(mockRequest);
    PortalThemeResolver themeResolver = new PortalSessionThemeResolverImpl();
    themeResolver.setChannelFqdn(mockRequest, "chn1");
    mockRequest.setAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE, themeResolver);

    RequestContextHolder.setRequestAttributes(webRequest);
    Map<String, Object> globalConfig = new HashMap<String, Object>();

    if (fqdnChannelId != null) {
      globalConfig.put("channelid", fqdnChannelId);
    }
    String view = controller.signup(map, globalConfig, null, channelCodeInUrl, trialAccountType.getId().toString(),
        status, mockRequest);
    return view;
  }

  @Test
  public void testRegisterOnChannel() {

    Channel channel = channelService.getChannelById("15");
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();

    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    registration.setAllowSecondary(true);
    registration.setSecondaryAddress(new Address("steve", "creek", "cupertino", "CHAN", "95014", "IN"));
    AccountType disposition = accountTypeDAO.getDefaultRegistrationAccountType();
    BindingResult result = setupRegistration(disposition, registration);

    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, channel.getParam(), status, mockRequest);
    Assert.assertEquals("register.registration_success", view);
    Assert.assertTrue(status.isComplete());
    Tenant tenant = ((com.citrix.cpbm.access.Tenant) map.get("tenant")).getObject();
    verifyRegistration(disposition, registration.getUser(), registration.getTenant());
    Assert.assertEquals(registration.getTenant().getObject(), tenant);

    Assert.assertEquals(channel.getParam(), tenant.getSourceChannel().getParam());

  }

  @Test
  public void testRegisterWithSelfSignupEnabledAtGlobal() {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_allow_signup, "true");

    String channelCode = null;
    String view = signupOnChannel(channelCode, null);

    Assert.assertEquals("register.userinfo", view);

  }

  @Test
  public void testRegisterWithSelfSignupDisabledAtGlobal() {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_allow_signup, "false");

    String channelCode = null;
    String view = signupOnChannel(channelCode, null);

    Assert.assertEquals("register.fail", view);

  }

  @Test
  public void testRegisterOnChannelWithSelfSignupDisabled() {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_allow_signup, "true");

    Channel channel = channelService.getChannelById("3");
    editChannelSignupFlag(channel, false);

    String view = signupOnChannel(channel.getCode(), channel.getFqdnPrefix());

    Assert.assertEquals("register.fail", view);

  }

  @Test
  public void testRegisterOnChannelWithSelfSignupEnabled() {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_allow_signup, "false");

    Channel channel = channelService.getChannelById("3");
    editChannelSignupFlag(channel, true);

    String view = signupOnChannel(channel.getCode(), channel.getFqdnPrefix());

    Assert.assertEquals("register.userinfo", view);

  }

  @Test
  public void testRegisterOnDefaultChannelWithSelfSignupEnabledPositiveWithoutFQDN() {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_allow_signup, "true");

    Channel channel = channelService.getDefaultServiceProviderChannel();
    editChannelSignupFlag(channel, false);

    String view = signupOnChannel(channel.getCode(), null);

    Assert.assertEquals("register.userinfo", view);

  }

  @Test
  public void testRegisterOnDefaultChannelWithSelfSignupEnabledPositiveWithFQDN() {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_allow_signup, "true");

    Channel channel = channelService.getDefaultServiceProviderChannel();
    editChannelSignupFlag(channel, false);

    String view = signupOnChannel(channel.getCode(), channel.getFqdnPrefix());

    Assert.assertEquals("register.userinfo", view);

  }

  @Test
  public void testRegisterOnDefaultChannelWithSelfSignupEnabledNegativeWithFQDN() {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_allow_signup, "false");

    Channel channel = channelService.getDefaultServiceProviderChannel();
    editChannelSignupFlag(channel, true);

    String view = signupOnChannel(channel.getCode(), channel.getFqdnPrefix());

    Assert.assertEquals("register.fail", view);

  }

  @Test
  public void testRegisterOnDefaultChannelWithSelfSignupEnabledNegativeWithoutFQDN() {

    setConfiguration(Names.com_citrix_cpbm_portal_settings_allow_signup, "false");

    Channel channel = channelService.getDefaultServiceProviderChannel();
    editChannelSignupFlag(channel, true);

    String view = signupOnChannel(channel.getCode(), null);

    Assert.assertEquals("register.fail", view);

  }

  @Test
  public void testRegisterOnChannelWithSelfSignupEnabledNegativeWithWrongFQDN() {

    Channel channel = channelService.getChannelById("3");
    editChannelSignupFlag(channel, true);

    String view = signupOnChannel(channel.getCode(), "WrongFQDN");

    Assert.assertEquals("errors/notfound", view);

  }

  @Test
  public void testRegisterOnDefaultChannelWithSelfSignupEnabledNegativeWithWrongFQDN() {

    Channel channel = channelService.getDefaultServiceProviderChannel();
    editChannelSignupFlag(channel, true);

    String view = signupOnChannel(channel.getCode(), "WrongFQDN");

    Assert.assertEquals("errors/notfound", view);

  }

  private void editChannelSignupFlag(Channel channel, boolean signupAllowed) {

    ChannelBrandingConfigurations channelBrandingConfigurations = channel.getChannelBrandingConfigurations();
    channelBrandingConfigurations.setSignupAllowed(signupAllowed);

    channelService.update(channel);
  }

  private String signupOnChannel(String channelCode, String channelPrefix) {
    UserRegistration registration = new UserRegistration();
    registration.setAcceptedTerms(true);
    AccountType disposition = accountTypeDAO.getTrialAccountType();
    setupRegistration(disposition, registration);
    createHttpMocks(channelPrefix);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();

    DispatcherServletWebRequest webRequest = new DispatcherServletWebRequest(mockRequest);
    RequestContextHolder.setRequestAttributes(webRequest);
    String view = controller.signup(map, null, null, channelCode, "3", status, httpServletRequest);
    System.err.println(map);
    return view;
  }

  private void createHttpMocks(String channelPrefix) {
    EasyMock.reset(httpServletRequest, httpSession);
    EasyMock.expect(httpServletRequest.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE))
        .andReturn(themeResolver).anyTimes();
    EasyMock.expect(httpServletRequest.getSession()).andReturn(httpSession).anyTimes();
    EasyMock.expect(httpServletRequest.getSession(false)).andReturn(httpSession).anyTimes();
    EasyMock.expect(httpServletRequest.getParameter("previewchannelcode")).andReturn(null).anyTimes();
    httpSession.setAttribute(EasyMock.anyObject(String.class), EasyMock.anyObject());
    EasyMock.expectLastCall().anyTimes();
    httpSession.removeAttribute(EasyMock.anyObject(String.class));
    EasyMock.expectLastCall().anyTimes();
    EasyMock.expect(httpSession.getAttribute(CHANNEL_FQDN_NAME)).andReturn(channelPrefix).anyTimes();

    EasyMock.expect(httpSession.getAttribute(DELETED_CHANNEL_FQDN_NAME)).andReturn(null).anyTimes();
    EasyMock.expect(httpSession.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME))
        .andReturn(Locale.ENGLISH).anyTimes();
    EasyMock.replay(httpServletRequest, httpSession);
  }

  /**
   * Description : test to verify the blacklisted countries will not show for user to signup in the country list
   * 
   * @author nageswarap
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUserInfoWithBlackListedCountries() {

    int filteredCountriesBefore;
    int filteredCountriesAfter;

    // creating a ModelMap object and Registration object
    ModelMap model = new ModelMap();
    UserRegistration registration = new UserRegistration();

    // get the Account type for registration
    AccountType disposition = registrationService.getTrialAccountType();
    BindingResult result;
    registration.setAcceptedTerms(true);

    // calling the setupRegistration private method to setup the registration object
    result = setupRegistration(disposition, registration);

    // getting the list of countries that are available for registration, before adding the black list country
    String view = controller.userInfo(registration, result, model, null, status, request);
    Assert.assertEquals("register.moreuserinfo", view);
    List<Country> filteredCountriesListBefore = (List<Country>) model.get("filteredCountryList");
    filteredCountriesBefore = filteredCountriesListBefore.size();

    // updating the blacklisted countries in CPBM
    configurationService.clearConfigurationCache(true, "");
    com.vmops.model.Configuration blackListCountries = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.country.blacklist");
    blackListCountries.setValue("IN,US");
    configurationService.update(blackListCountries);

    // getting the list of countries that are available for registration, before adding the black list country
    String view1 = controller.userInfo(registration, result, model, null, status, request);
    Assert.assertEquals("register.moreuserinfo", view1);
    List<Country> filteredCountriesListAfter = (List<Country>) model.get("filteredCountryList");
    filteredCountriesAfter = filteredCountriesListAfter.size();

    // Asserting the countries count before and after
    Assert.assertEquals("the countries count not matched", filteredCountriesBefore - 2, filteredCountriesAfter);

    // verifying the Blacklisted country is not available in the filtered countries list
    // TODO: add verification for US not present
    boolean countryFound = false;
    for (Country country : filteredCountriesListAfter) {
      if ("IN".equalsIgnoreCase(country.getCountryCode2())) {
        countryFound = true;
        break;
      }
    }

    Assert.assertFalse("black listed country found in the filtered list", countryFound);

  }

  /**
   * Description : test to verify the whitelisted countries will only show for user to signup in the country list
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testUserInfoWithWhiteListedCountries() {

    int filteredCountriesAfter;

    // creating a ModelMap object and Registration object
    ModelMap model = new ModelMap();
    UserRegistration registration = new UserRegistration();

    // get the Account type for registration
    AccountType disposition = accountTypeDAO.getTrialAccountType();
    BindingResult result;
    registration.setAcceptedTerms(true);

    // calling the setupRegistration private method to setup the registration object
    result = setupRegistration(disposition, registration);

    // updating the blacklisted countries in CPBM
    configurationService.clearConfigurationCache(true, "");
    com.vmops.model.Configuration blackListCountries = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.country.whitelist");
    blackListCountries.setValue("IN");
    configurationService.update(blackListCountries);

    // getting the list of countries that are available for registration, before adding the black list country
    String view1 = controller.userInfo(registration, result, model, null, status, request);
    Assert.assertEquals("register.moreuserinfo", view1);
    List<Country> filteredCountries = (List<Country>) model.get("filteredCountryList");
    filteredCountriesAfter = filteredCountries.size();

    // Asserting the countries count after setting the white list
    Assert.assertEquals("the countries count not matched", 1, filteredCountriesAfter);

    // verifying the white listed country is not available in the filtered countries list
    Assert.assertEquals("white listed country not found in the filtered list", "IN", filteredCountries.get(0)
        .getCountryCode2());

  }

  /**
   * Description : Test to Verify that the CPBM will registed the Account if the Device fraud control status is REVIEW
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testDeviceFraudControlWithReviewStatus() throws Exception {

    // Creating MockRequest and Registration Object
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();

    // Populating the Registration Object with Required Data
    populateRegistrationDataForTenant(registration, new Address("steve", "creek", "cupertino", "CHAN", "1", "IN"));

    AccountType disposition = tenantService.getDefaultRegistrationAccountType();
    BindingResult result = setupRegistration(disposition, registration);

    // setting the MockDeviceFraudDetectionService flag to true
    MockDeviceFraudDetectionService.flag = true;

    int oldEventListSize = eventListener.getEvents().size();

    // calling register method
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);

    int newEventListSize = eventListener.getEvents().size();
    Assert.assertEquals("events not generated for Device fraud control status is REVIEW & count not matched ",
        oldEventListSize + 3, newEventListSize);

    // Asserting Register Status
    Assert.assertEquals("register.registration_success", view);
    Assert.assertTrue(status.isComplete());

    // Asserting Device Fraud Status
    Assert.assertEquals("Device Fraud is not detected", "true", map.get("deviceFraudDetected").toString());

    // setting the MockDeviceFraudDetectionService flag to true
    MockDeviceFraudDetectionService.flag = false;

  }

  /**
   * Description : Test to Verify that the CPBM will registed the Account if the Device fraud control status is REJECT
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testDeviceFraudControlWithRejectStatus() throws Exception {

    // Creating MockRequest and Registration Object
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();

    // Populating the Registration Object with Required Data
    populateRegistrationDataForTenant(registration, new Address("steve", "creek", "cupertino", "CHAN", "2", "IN"));

    AccountType disposition = tenantService.getDefaultRegistrationAccountType();
    BindingResult result = setupRegistration(disposition, registration);

    // setting the MockDeviceFraudDetectionService flag to true
    MockDeviceFraudDetectionService.flag = true;

    int oldEventListSize = eventListener.getEvents().size();

    // calling register method
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);

    // asserting the events
    int newEventListSize = eventListener.getEvents().size();
    Assert.assertEquals("events has been generated for Device fraud control status is REJECT & count not matched ",
        oldEventListSize + 1, newEventListSize);

    // Asserting Device Fraud Status
    Assert.assertEquals("Registration completed with fraud status  REJECT", "register.fail", view);

    // setting the MockDeviceFraudDetectionService flag to true
    MockDeviceFraudDetectionService.flag = false;
  }

  /**
   * Description : Test to Verify that the CPBM will registed the Account if the Device fraud control status is FAIL
   * 
   * @author nageswarap
   * @throws Exception
   */
  @Test
  public void testDeviceFraudControlWithFailStatus() throws Exception {

    // Creating MockRequest and Registration Object
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, "/portal/register");
    UserRegistration registration = new UserRegistration();

    // Populating the Registration Object with Required Data
    populateRegistrationDataForTenant(registration, new Address("steve", "creek", "cupertino", "CHAN", "3", "IN"));

    AccountType disposition = tenantService.getDefaultRegistrationAccountType();
    BindingResult result = setupRegistration(disposition, registration);

    // setting the MockDeviceFraudDetectionService flag to true
    MockDeviceFraudDetectionService.flag = true;

    int oldEventListSize = eventListener.getEvents().size();

    // calling register method
    beforeRegisterCall(mockRequest, registration);
    String view = controller.register(registration, result, "abc", "abc", map, null, status, mockRequest);

    // asserting the events
    int newEventListSize = eventListener.getEvents().size();
    Assert.assertEquals("events has been generated for Device fraud control status is FAIL & count not matched ",
        oldEventListSize, newEventListSize);

    // Asserting Device Fraud Status
    Assert.assertEquals("Registration completed with fraud status FAIL", "register.fail", view);

    // setting the MockDeviceFraudDetectionService flag to true
    MockDeviceFraudDetectionService.flag = false;
  }

  /**
   * Description : populating the data to the registration object
   * 
   * @author nageswarap
   * @param registration
   * @param billingAddress
   */
  private void populateRegistrationDataForTenant(UserRegistration registration, Address billingAddress) {
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    registration.setAcceptedTerms(true);
    registration.setAllowSecondary(true);
    registration.setSecondaryAddress(new Address("steve", "creek", "cupertino", "CHAN", "95014", "IN"));
    registration.setBillingAddress(billingAddress);
  }

  @Test
  public void testRegisterInvalidWhiteListCountries() throws Exception {
    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_country_whitelist, "US,UK,IN");
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    AccountType accountType = accountTypeDAO.getAccountTypeByName("RETAIL");

    UserRegistration registration = new UserRegistration();
    // Creating user object for registration
    User user = new User("DeepakOne", "test", "test@test.com", "test1234", VALID_PASSWORD, VALID_PHONE, VALID_TIMEZONE,
        null, null, null);
    BindingResult result = setUpRegistrationForGivenUser(user, registration, accountType);

    beforeRegisterCall(mockRequest, registration);
    controller.userInfo(registration, result, map, null, status, mockRequest);

    List<Country> filteredCountryList = (List<Country>) map.get("filteredCountryList");
    Assert.assertEquals(2, filteredCountryList.size());
    Assert.assertTrue(filteredCountryList.get(0).getName().equalsIgnoreCase("United States"));
    Assert.assertTrue(filteredCountryList.get(1).getName().equalsIgnoreCase("India"));

  }

  @Test
  public void testRegisterAllValidWhiteListCountries() throws Exception {
    setConfiguration(Names.com_citrix_cpbm_accountManagement_onboarding_country_whitelist, "US,GB,IN");
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    AccountType accountType = accountTypeDAO.getAccountTypeByName("RETAIL");

    UserRegistration registration = new UserRegistration();
    // Creating user object for registration
    User user = new User("DeepakOne", "test", "test@test.com", "test1234", VALID_PASSWORD, VALID_PHONE, VALID_TIMEZONE,
        null, null, null);
    BindingResult result = setUpRegistrationForGivenUser(user, registration, accountType);

    beforeRegisterCall(mockRequest, registration);
    controller.userInfo(registration, result, map, null, status, mockRequest);

    List<Country> filteredCountryList = (List<Country>) map.get("filteredCountryList");
    Assert.assertEquals(3, filteredCountryList.size());
    System.err.println(filteredCountryList);
    Assert.assertTrue(filteredCountryList.get(0).getName().equalsIgnoreCase("United States"));
    Assert.assertTrue(filteredCountryList.get(1).getName().equalsIgnoreCase("United Kingdom"));
    Assert.assertTrue(filteredCountryList.get(2).getName().equalsIgnoreCase("India"));

  }
}