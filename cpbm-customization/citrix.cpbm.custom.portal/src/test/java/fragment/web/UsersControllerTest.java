/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;
import web.support.MockSessionStatus;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.utils.ServiceInstanceConfiguration;
import com.citrix.cpbm.platform.bootstrap.service.BootstrapActivator;
import com.citrix.cpbm.platform.spi.AccountLifecycleHandler;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.UserLifecycleHandler;
import com.citrix.cpbm.platform.spi.View;
import com.citrix.cpbm.platform.spi.View.ViewMode;
import com.citrix.cpbm.platform.spi.ViewResolver;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.citrix.cpbm.portal.fragment.controllers.UsersController;
import com.vmops.event.PortalEvent;
import com.vmops.event.UserActivateEmail;
import com.vmops.event.UserCreation;
import com.vmops.event.UserDeactivateEmail;
import com.vmops.event.UserDeletion;
import com.vmops.event.VerifyAlertEmailRequest;
import com.vmops.internal.service.PaymentGatewayService;
import com.vmops.model.Configuration;
import com.vmops.model.Profile;
import com.vmops.model.Service;
import com.vmops.model.ServiceInstance;
import com.vmops.model.SpendAlertSubscription;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.model.UserAlertPreferences;
import com.vmops.model.UserAlertPreferences.AlertType;
import com.vmops.model.UserHandle;
import com.vmops.model.billing.PaymentTransaction;
import com.vmops.model.billing.PaymentTransaction.State;
import com.vmops.persistence.EventDAO;
import com.vmops.persistence.SpendAlertSubscriptionDAO;
import com.vmops.persistence.UserHandleDAO;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ConfigurationService;
import com.vmops.service.TenantService;
import com.vmops.service.UserAlertPreferencesService;
import com.vmops.service.UserService;
import com.vmops.service.UserService.Handle;
import com.vmops.service.exceptions.NoSuchTenantException;
import com.vmops.service.exceptions.NoSuchUserException;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.UserForm;
import com.vmops.web.interceptors.UserContextInterceptor;
import common.MockCloudInstance;
import common.MockTelephoneVerificationService;

public class UsersControllerTest extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  private MockSessionStatus status;

  @Autowired
  private UsersController controller;

  @Autowired
  private UserService userService;

  private HttpSession session;

  private MockHttpServletRequest request;

  @Autowired
  private UserAlertPreferencesService userAlertPreferencesService;

  @Autowired
  private TenantService service;

  @Autowired
  private SpendAlertSubscriptionDAO dao;

  @Autowired
  private EventDAO eventDAO;

  @Autowired
  private UserHandleDAO userHandleDAO;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManagerService;

  private BootstrapActivator bootstrapActivator = new BootstrapActivator();

  private static boolean isMockInstanceCreated = false;

  private PaymentGatewayService ossConnector = null;

  private CloudConnector iaasConnector = null;

  private ViewResolver viewResolver;

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    status = new MockSessionStatus();
    session = new MockHttpSession();
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

  @Override
  protected void prepareMock(boolean adaptor, BootstrapActivator bootstrapActivator) {
    super.prepareMock(adaptor, bootstrapActivator);
    MockCloudInstance mock = this.getMockCloudInstance();
    CloudConnector cloudConnector = mock.getCloudConnector();
    viewResolver = mock.getViewResolver();

    ossConnector = EasyMock.createMock(PaymentGatewayService.class);
    iaasConnector = EasyMock.createMock(CloudConnector.class);
    mockAccountLifecycleHandler = EasyMock.createMock(AccountLifecycleHandler.class);
    mockUserLifecycleHandler = EasyMock.createMock(UserLifecycleHandler.class);

    EasyMock.reset(ossConnector);

    EasyMock.expect(iaasConnector.getAccountLifeCycleHandler()).andReturn(mockAccountLifecycleHandler).anyTimes();

    View view = new View("test", "test", ViewMode.WINDOW);

    EasyMock.expect(viewResolver.getConsoleView(EasyMock.anyObject(User.class))).andReturn(view).anyTimes();

    EasyMock.expect(iaasConnector.getUserLifeCycleHandler()).andReturn(mockUserLifecycleHandler).anyTimes();
    EasyMock.expect(cloudConnector.getStatus()).andReturn(Boolean.TRUE).anyTimes();
    EasyMock.expect(cloudConnector.getServiceInstanceUUID()).andReturn("003fa8ee-fba3-467f-a517-ed806dae8a87")
        .anyTimes();
    ServiceInstanceConfiguration serviceInstanceConfiguration = mock.getSic();
    EasyMock.expect(serviceInstanceConfiguration.getInstanceUUID()).andReturn("003fa8ee-fba3-467f-a517-ed806dae8a87")
        .anyTimes();
    EasyMock.expect(ossConnector.getAccountLifeCycleHandler()).andReturn(mockAccountLifecycleHandler).anyTimes();
    EasyMock.expect(ossConnector.getUserLifeCycleHandler()).andReturn(mockUserLifecycleHandler).anyTimes();
    final Capture<BigDecimal> amount = new Capture<BigDecimal>();
    EasyMock.expect(ossConnector.authorize(EasyMock.anyObject(Tenant.class), EasyMock.capture(amount)))
        .andAnswer(new IAnswer<PaymentTransaction>() {

          @Override
          public PaymentTransaction answer() throws Exception {
            return new PaymentTransaction(new Tenant(), 0, State.COMPLETED,
                com.vmops.model.billing.PaymentTransaction.Type.CAPTURE);
          }
        }).anyTimes();
    EasyMock.replay(iaasConnector);
    EasyMock.replay(ossConnector);
    EasyMock.replay(viewResolver);
    EasyMock.replay(cloudConnector, serviceInstanceConfiguration);
  }

  @SuppressWarnings({
    "rawtypes"
  })
  @Test
  public void testRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class controllerClass = controller.getClass();
    Method expected = locateMethod(controllerClass, "listUsersForAccount", new Class[] {
        Tenant.class, Boolean.TYPE, String.class, ModelMap.class, HttpSession.class, String.class, int.class,
        int.class, String.class, HttpServletRequest.class
    });

    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/users"));
    Assert.assertEquals(expected, handler);

    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/users/"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "show", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/users/1"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "createStepOne", new Class[] {
        Tenant.class, String.class, ModelMap.class, HttpSession.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/users/new/step1"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "login", new Class[] {
        Tenant.class, String.class, String.class, String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/users/cloud_login"));
    Assert.assertEquals(expected, handler);

  }

  @Test
  public void testUsersShow() throws Exception {
    User expected = userDAO.find(11L);
    asUser(expected);

    String view = controller.show(expected.getUuid(), map);
    Assert.assertEquals("user.show", view);
    Assert.assertTrue(map.containsKey("user"));
    User found = (User) map.get("user");
    Assert.assertEquals(expected, found);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUsersListShowAll() {
    asRoot();

    List<User> expected = userDAO.findAll(null);
    User portalUser = userDAO.find("2");
    if (expected.contains(portalUser)) {
      // As root user doesn't have permission on Portal User
      expected.remove(portalUser);
    }
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    String view = controller.listUsersForAccount(controller.getTenant(), true, null, map, session, null, 1, 20,
        "false", mockRequest);
    Assert.assertEquals("users.list_with_admin_menu", view);
    Assert.assertTrue(map.containsKey("users"));
    List<User> found = (List<User>) map.get("users");
    Assert.assertEquals(new HashSet<User>(expected), new HashSet<User>(found));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUsersListShowTenant() {
    asRoot();
    Tenant tenant = getDefaultTenant();
    List<User> expected = userService.list(0, 0, null, null, false, null, tenant.getId().toString(), null);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    String view = controller.listUsersForAccount(controller.getTenant(), true, null, map, session, null, 1, 20, "true",
        mockRequest);
    Assert.assertEquals("users.list_with_admin_menu", view);
    Assert.assertTrue(map.containsKey("users"));
    List<User> found = (List<User>) map.get("users");
    Assert.assertTrue(found.containsAll(expected));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUsersList() {
    User user = userDAO.find(3L);
    asUser(user);
    List<User> expected = userService.list(0, 0, null, null, false, null, user.getTenant().getId().toString(), null);
    MockHttpServletRequest mockrequest = new MockHttpServletRequest();
    String view = controller.listUsersForAccount(controller.getTenant(), true, null, map, session, null, 1, 20, "true",
        mockrequest);
    Assert.assertEquals("users.nonroot.list_with_user_menu", view);
    Assert.assertTrue(map.containsKey("users"));
    List<User> found = (List<User>) map.get("users");
    Assert.assertTrue(found.containsAll(expected));
    Assert.assertTrue(map.get("page") == Page.ADMIN_ALL_USERS);
  }

  /**
   * create a user in an account which is created under a channel which HAS overridden default time-zone and Locale. The
   * overridden timezone and Locale should be autoselected in user creation wizard
   */
  @Test
  public void testUserNewStep1() {
    User user = userDAO.find(3L);
    asUser(user);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    String view = controller.createStepOne(controller.getTenant(), null, map, new MockHttpSession(), mockRequest);
    UserForm form = (UserForm) map.get("user");
    String channelLocale = user.getSourceChannel().getChannelBrandingConfigurations().getDefaultLocale();
    String channelTimezone = user.getSourceChannel().getChannelBrandingConfigurations().getDefaultTimeZone();
    Assert.assertNotNull(form);
    Assert.assertNotNull(form.getUser());
    Assert.assertEquals("users.new.step1", view);
    Assert.assertEquals(user.getTenant().getAccountId(),
        ((com.citrix.cpbm.access.Tenant) map.get("userTenant")).getAccountId());
    Assert.assertEquals(channelLocale, map.get("defaultLocale"));
    Assert.assertEquals(channelTimezone, ((UserForm)map.get("user")).getTimeZone());
  }

  /**
   * create a user in an account which is created under a channel without overridden default time-zone and Locale value.
   * The default time-zone and Locale should be autoselected in user creation wizard.
   */
  @Test
  public void testUserNewStep1WithoutConfigOverriddenAtChannel() {
    User user = userDAO.find(4L);
    asUser(user);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    String view = controller.createStepOne(controller.getTenant(), null, map, new MockHttpSession(), mockRequest);
    UserForm form = (UserForm) map.get("user");
    String channelLocale = config.getDefaultLocale();
    String channelTimezone = configurationService.locateConfigurationByName(
        Names.com_citrix_cpbm_portal_settings_default_timezone).getValue();
    Assert.assertNotNull(form);
    Assert.assertNotNull(form.getUser());
    Assert.assertEquals("users.new.step1", view);
    Assert.assertEquals(user.getTenant().getAccountId(),
        ((com.citrix.cpbm.access.Tenant) map.get("userTenant")).getAccountId());
    Assert.assertEquals(channelLocale, map.get("defaultLocale"));
    Assert.assertEquals(channelTimezone, ((UserForm)map.get("user")).getTimeZone());
  }

  @Test
  public void testUserNewDifferentTenantAsRoot() {
    asRoot();

    Tenant other = tenantDAO.find(3L);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.setAttribute("isSurrogatedTenant", false);
    String view = controller.createStepOne(other, other.getUuid(), map, new MockHttpSession(), mockRequest);
    UserForm form = (UserForm) map.get("user");
    Assert.assertNotNull(form);
    Assert.assertNotNull(form.getUser());
    Assert.assertEquals("users.new.step1", view);
    Assert.assertEquals(other.getAccountId(), ((com.citrix.cpbm.access.Tenant) map.get("userTenant")).getAccountId());
  }

  @Test
  public void testCreateUserStep2() throws Exception {
    User user = userDAO.find(3L);
    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("test@test.com");
    newUser.setUsername("testuser");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    BindingResult bindingResult = validate(form);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest,
        new MockHttpSession());
    Assert.assertEquals("users.newuserregistration.finish", view);
    Assert.assertNotNull(map.get("user"));
    Assert.assertEquals(form, map.get("user"));
  }

  @Test
  public void testCreateUserStep2WithSuffix() throws Exception {
    asRoot();
    com.vmops.model.Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_username_duplicate_allowed);
    configuration.setValue("true");
    configurationService.update(configuration);

    User user = userDAO.find(3L);

    Tenant userTenant = user.getTenant();
    String suffix = "testSuffix";
    userTenant.setUsernameSuffix(suffix);
    tenantService.update(userTenant);

    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("test@test.com");
    newUser.setUsername("testuser");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    BindingResult bindingResult = validate(form);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest,
        new MockHttpSession());
    Assert.assertEquals("users.newuserregistration.finish", view);
    Assert.assertNotNull(map.get("user"));
    Assert.assertEquals(form, map.get("user"));

    User found = userService.getUserByParam("username", "testuser@" + suffix, false);
    Assert.assertNotNull(found);
    Assert.assertEquals("firstName", found.getFirstName());
    Assert.assertEquals("lastName", found.getLastName());

  }

  @Test
  public void testValidateUsernameWithSuffix() throws Exception {
    asRoot();
    com.vmops.model.Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_username_duplicate_allowed);
    configuration.setValue("true");
    configurationService.update(configuration);

    User user = userDAO.find(3L);

    Tenant userTenant = user.getTenant();
    String suffix = "testSuffix";
    userTenant.setUsernameSuffix(suffix);
    tenantService.update(userTenant);

    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("test@test.com");
    newUser.setUsername("testuser");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    BindingResult bindingResult = validate(form);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest, new MockHttpSession());
    Assert.assertEquals(Boolean.FALSE.toString(), controller.validateUsername("testuser"));
    Assert.assertEquals(Boolean.TRUE.toString(), controller.validateUsername("testuser1"));
  }

  @Test
  public void testValidateUserName() throws Exception {
    asRoot();
    User user = userDAO.find(3L);

    asUser(user);
    Assert.assertEquals(Boolean.FALSE.toString(), controller.validateUsername(user.getUsername()));

  }

  @Test
  public void testCreateUserStep2CustomizeEmail() throws Exception {
    User user = userDAO.find(3L);
    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("test@test.com");
    newUser.setUsername("testuser");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "CustomeEmail");
    BindingResult bindingResult = validate(form);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest,
        new MockHttpSession());
    Assert.assertEquals("users.newuser.customemail", view);
    Assert.assertNotNull(map.get("emailText"));
    Assert.assertEquals(form, map.get("user"));
  }

  @Test
  public void testCreateUserStep2Failed() throws Exception {
    User user = userDAO.find(3L);
    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("test@test.com");
    newUser.setUsername("testuser###");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());
    BeanPropertyBindingResult result = new BeanPropertyBindingResult(form, "validation");
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, result, proxyTenant, map, new MockHttpServletRequest(),
        new MockHttpSession());
    Assert.assertTrue(result.hasErrors());
    Assert.assertFalse("users.newuserregistration.finish".equals(view));
  }

  @Test
  public void testCreateUserwithEmailAsUserName() throws Exception {
    User user = userDAO.find(3L);
    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("test@test.com");
    newUser.setUsername("testuser@gmail.com");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());

    BindingResult bindingResult = validate(form);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest,
        new MockHttpSession());
    Assert.assertEquals("users.newuserregistration.finish", view);
  }

  @Test(expected = AccessDeniedException.class)
  public void testCreateUserUnprivileged() throws Exception {
    User user = createTestUserInTenant(getDefaultTenant());
    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setUsername("foobar");
    newUser.setEmail("test@test.com");
    validate(form);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    controller.createStepOne(controller.getTenant(), null, map, new MockHttpSession(), mockRequest);
  }

  @Test
  public void testEditUser() throws Exception {
    User user = userDAO.find(3L);
    asRoot();

    String view = controller.edit(null, user.getUuid(),
        getRequestTemplate(HttpMethod.GET, "/users/" + user.getUuid() + "/myprofile"), map);
    UserForm form = (UserForm) map.get("user");
    Assert.assertNotNull(form);
    Assert.assertNotNull(form.getUser());
    Assert.assertEquals(user.getUsername(), form.getUser().getUsername());
    Assert.assertEquals("users.edit.myprofile", view);
  }

  /**
   * @Desc Test to check editing of User First name, Last name in My Profile page
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testUpdateUser() throws Exception {
    asRoot();
    User user = userDAO.find(3L);
    Assert.assertEquals("firstName", user.getFirstName());
    Assert.assertEquals("lastName", user.getLastName());
    UserForm form = new UserForm((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    user.setFirstName("UpdatedFirstName");
    user.setLastName("UpdatedLastName");
    Assert.assertEquals("1-1", user.getPhone());
    form.setCountryCode("91");
    form.setPhone("9902399549");
    BindingResult result = validate(form);
    form.setUserClone((com.citrix.cpbm.access.User) CustomProxy.newInstance((User) user.clone()));
    String view = controller.edit(user.getParam(), form, result,
        getRequestTemplate(HttpMethod.POST, "/users/3/myprofile"), map, status);
    Assert.assertEquals("redirect:/portal/users/" + user.getUuid() + "/myprofile", view);
    Assert.assertTrue(status.isComplete());
    User found = userDAO.find(user.getId());
    Assert.assertEquals("UpdatedFirstName", found.getFirstName());
    Assert.assertEquals("UpdatedLastName", found.getLastName());
    Assert.assertEquals("91-9902399549", found.getPhone());
    Assert.assertTrue(status.isComplete());
  }

  @Test
  public void testVerifyOldPassword() throws Exception {
    asRoot();
    User user = userDAO.find(3L);
    UserForm form = new UserForm((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    user.setClearPassword("Portal123#");
    user.setOldPassword("Anything123#");
    BindingResult result = validate(form);
    form.setUserClone((com.citrix.cpbm.access.User) CustomProxy.newInstance((User) user.clone()));
    String response = controller.changePassword(form, result,
        getRequestTemplate(HttpMethod.POST, "/users/changePassword?userParam=3"), map, status, user.getParam());
    Assert.assertEquals("failure", response);

    user = userDAO.find(3L);
    form = new UserForm((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    user.setClearPassword("Portal123#");
    user.setOldPassword("Portal123#");
    result = validate(form);
    form.setUserClone((com.citrix.cpbm.access.User) CustomProxy.newInstance((User) user.clone()));
    response = controller.changePassword(form, result,
        getRequestTemplate(HttpMethod.POST, "/users/changePassword?userParam=3"), map, status, user.getParam());
    Assert.assertEquals("success", response);
  }

  @Test
  public void testUpdateUserFail() throws Exception {
    asRoot();
    User user = userDAO.find(3L);
    UserForm form = new UserForm((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    user.setFirstName("Updated");
    form.setUserClone((com.citrix.cpbm.access.User) CustomProxy.newInstance((User) user.clone()));
    BindingResult result = validate(form);
    result.reject("dummy");
    String view = controller.edit(user.getParam(), form, result,
        getRequestTemplate(HttpMethod.POST, "/users/" + user.getUuid() + "/myprofile"), map, status);
    Assert.assertEquals("redirect:/portal/users/" + user.getUuid() + "/myprofile", view);
    Object o = map.get("user");
    Assert.assertNull(o);
    Assert.assertFalse(status.isComplete());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUserSearchAsRoot() throws Exception {
    asRoot();

    String view = controller.search(controller.getTenant(), "po", null, map);
    Assert.assertEquals("users.search_results", view);
    Object o = map.get("users");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof List);
    List<User> list = (List<User>) o;
    Assert.assertEquals(0, list.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUserSearchDifferentTenant() throws Exception {
    asRoot();
    String view = controller.search(controller.getTenant(), "2_", getDefaultTenant().getUuid(), map);
    Assert.assertEquals("users.search_results", view);
    Object o = map.get("users");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof List);
    List<User> list = (List<User>) o;
    Assert.assertEquals(1, list.size());
    Assert.assertEquals("2_retail1", list.get(0).getUsername());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUserSearch() throws Exception {
    asUser(getDefaultTenant().getOwner());

    String view = controller.search(controller.getTenant(), "2_", null, map);
    Assert.assertEquals("users.search_results", view);
    Object o = map.get("users");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof List);
    List<User> list = (List<User>) o;
    Assert.assertEquals(1, list.size());
    Assert.assertEquals("2_retail1", list.get(0).getUsername());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUserSearchDifferentTenantNotRoot() throws Exception {
    asRoot();
    Tenant tenant = createTestTenant(accountTypeDAO.getOnDemandPostPaidAccountType());
    asUser(getDefaultTenant().getOwner());

    String view = controller.search(controller.getTenant(), "2_", tenant.getAccountId(), map);
    Assert.assertEquals("users.search_results", view);
    Object o = map.get("users");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof List);
    List<User> list = (List<User>) o;
    Assert.assertEquals(1, list.size());
    Assert.assertEquals("2_retail1", list.get(0).getUsername());
  }

  @Test
  public void testGetISDCode() throws Exception {
    String code = controller.getISDCodeByCounty("US");
    Assert.assertEquals("1", code);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testverifyPassword() {
    Map<String, Object> controllerMap = controller.verifyPassword("Portal123#", new MockHttpServletRequest());
    Assert.assertNotNull(controllerMap.get("userCredentialList"));
    List<Map<String, String>> credentialList = new ArrayList<Map<String, String>>();
    credentialList = (List<Map<String, String>>) controllerMap.get("userCredentialList");
    Set<UserHandle> userHandles = getRootUser().getUserHandles();
    Assert.assertTrue(userHandles.size() > 0);
    Assert.assertTrue(credentialList.size() > 0);
    for (UserHandle userHandle : userHandles) {
      Map<String, String> apiCreds = null;
      ServiceInstance si = connectorConfigurationManagerService.getInstance(userHandle.getServiceInstanceUuid());
      if (si.getService().getType().equals(CssdkConstants.OSS)) {
        continue;
      }
      for (Map<String, String> credentialMap : credentialList) {
        if (credentialMap.entrySet().containsAll(userHandle.getApiCredentials().entrySet())
            && si.getName().equals(credentialMap.get("InstanceName"))) {
          apiCreds = credentialMap;
          break;
        }
      }
      Assert.assertNotNull("Returned credential list does not have the api credential for the servieInstance uuid:"
          + userHandle.getServiceInstanceUuid(), si);
      Assert.assertNotNull("Returned credential list does not have the api credential for the servieInstance uuid:"
          + userHandle.getServiceInstanceUuid(), apiCreds);
      Assert.assertEquals(si.getName(), apiCreds.get("InstanceName"));
      Assert.assertEquals(si.getService().getUuid(), apiCreds.get("ServiceUuid"));
      Assert.assertEquals(messageSource.getMessage(si.getService().getServiceName() + ".service.name", null, null),
          apiCreds.get("ServiceName"));
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void generateApiKey() {
    Map<String, Object> controllerMap = controller.verifyPassword("Portal123#", new MockHttpServletRequest());
    Assert.assertNotNull(controllerMap.get("userCredentialList"));
    List<Map<String, String>> credentialList = new ArrayList<Map<String, String>>();
    credentialList = (List<Map<String, String>>) controllerMap.get("userCredentialList");
    String oldApiKey = credentialList.get(0).get("apiKey");
    String oldSecretKey = credentialList.get(0).get("secretKey");
    Map<String, Object> generateApiKeyHashmap = controller.generateApiKey(new MockHttpServletRequest());
    Map<String, String> generatedBSSAPICreds = (Map<String, String>) generateApiKeyHashmap.get("bssApiCredentials");
    Assert.assertNotNull(generatedBSSAPICreds);
    Assert.assertFalse(generatedBSSAPICreds.get("apiKey").equals(oldApiKey));
    Assert.assertFalse(generatedBSSAPICreds.get("secretKey").equals(oldSecretKey));
  }

  @Test
  public void testdeactivate() {
    // Case 1: user is active
    User user = userService.getUserByParam("id", "3", false);
    user.setEnabled(true);
    user.setLocked(false);
    userService.save(user);
    String view = controller.deactivate(user.getParam(), "ajax", map);
    Assert.assertNotNull(view);
    Assert.assertEquals(view, new String("user.show"));
    Assert.assertEquals(user.isEnabled(), true);
    Assert.assertEquals(user.isLocked(), false);
    Assert.assertTrue(map.containsAttribute("user"));
    Assert.assertTrue(map.containsAttribute("profileName"));
    Assert.assertEquals(user, map.get("user"));
    Assert.assertEquals(map.get("profileName"), user.getProfile().getName());

    // Case 2: user is not active
    user = userService.getUserByParam("id", "3", false);
    user.setEnabled(false);
    user.setLocked(true);
    userService.save(user);
    view = controller.deactivate(user.getParam(), "ajax", map);
    Assert.assertNotNull(view);
    Assert.assertEquals(view, new String("user.show"));
    Assert.assertEquals(user.isEnabled(), false);
    Assert.assertEquals(user.isLocked(), true);
    Assert.assertTrue(map.containsAttribute("user"));
    Assert.assertTrue(map.containsAttribute("profileName"));
    Assert.assertEquals(user, map.get("user"));
    Assert.assertEquals(map.get("profileName"), user.getProfile().getName());
  }

  @Test
  public void testdeactivateUser() {
    User user = userService.getUserByParam("id", "1", false);
    user = controller.deactivateUser(user.getParam(), map);
    Assert.assertEquals(user.isEnabled(), false);
    Assert.assertEquals(user.isLocked(), true);
    Assert.assertTrue(map.containsAttribute("user"));
    Assert.assertEquals(user, map.get("user"));

  }

  @Test
  public void testActivate() {
    // Case 1: when the user is not active
    User user = userService.getUserByParam("id", "3", false);
    user.setEnabled(false);
    user.setLocked(true);
    userService.save(user);
    String view = controller.activate(user.getParam(), "ajax", map);
    Assert.assertNotNull(view);
    Assert.assertEquals(view, new String("user.show"));
    Assert.assertEquals(user.isEnabled(), false);
    Assert.assertEquals(user.isLocked(), true);
    Assert.assertEquals(user.getFailedLoginAttempts(), 0);
    Assert.assertTrue(map.containsAttribute("user"));
    Assert.assertTrue(map.containsAttribute("profileName"));
    Assert.assertEquals(user, map.get("user"));
    Assert.assertEquals(map.get("profileName"), user.getProfile().getName());

    // Case 2: when the user is active
    user = userService.getUserByParam("id", "4", false);
    user.setEnabled(true);
    user.setLocked(false);
    userService.save(user);
    view = controller.activate(user.getParam(), "ajax", map);
    Assert.assertNotNull(view);
    Assert.assertEquals(view, new String("user.show"));
    Assert.assertEquals(user.isEnabled(), true);
    Assert.assertEquals(user.isLocked(), false);
    Assert.assertEquals(user.getFailedLoginAttempts(), 0);
    Assert.assertTrue(map.containsAttribute("user"));
    Assert.assertTrue(map.containsAttribute("profileName"));
    Assert.assertEquals(user, map.get("user"));
    Assert.assertEquals(map.get("profileName"), user.getProfile().getName());
  }

  @Test
  public void testactivateUser() {
    User user = userService.getUserByParam("id", "1", false);
    user = controller.activateUser(user.getParam(), map);
    Assert.assertEquals(user.isEnabled(), true);
    Assert.assertEquals(user.isLocked(), false);
    Assert.assertTrue(map.containsAttribute("user"));
    Assert.assertEquals(user, map.get("user"));
  }

  @Test
  public void testresendVerificationail() {
    User user = userService.getUserByParam("id", "1", false);
    String view = controller.resendVerificationEmail(user.getParam(), "ajax", map);
    Assert.assertNotNull(view);
    Assert.assertEquals(view, new String("user.show"));
    Assert.assertTrue(map.containsAttribute("user"));
    Assert.assertTrue(map.containsAttribute("profileName"));
    Assert.assertEquals(user, map.get("user"));
    Assert.assertEquals(map.get("profileName"), user.getProfile().getName());

  }

  @Test
  public void testverifyMaxSubscription() {
    Tenant tenant = service.getTenantByParam("id", "2", false);
    String verify = controller.verifyMaxSubscription("2", tenant.getAccountId());
    Assert.assertTrue(Boolean.valueOf(verify));
    try {
      controller.verifyMaxSubscription("2", "2");
      Assert.fail();
    } catch (NoSuchTenantException e) {
    }
  }

  @Test
  public void testgetUserTimezoneOffset() {
    Tenant tenant = service.getTenantByParam("id", "2", false);
    Tenant tenant1 = service.getTenantByParam("id", "3", false);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.setAttribute("isSurrogatedTenant", false);
    mockRequest.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant);
    asUser(tenant.getOwner());
    String timeZone = controller.getUserTimezoneOffset(tenant, mockRequest);
    Assert.assertEquals(timeZone, new String("0.00"));
    tenant1.getOwner().setTimeZone(null);
    service.save(tenant1);
    mockRequest.setAttribute("isSurrogatedTenant", true);
    mockRequest.setAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY, tenant1);
    timeZone = controller.getUserTimezoneOffset(tenant, mockRequest);
    Assert.assertNotNull(timeZone);
  }

  @Test
  public void testremoveSpendAlertSubscription() {
    Tenant tenant = createTestTenant(accountTypeDAO.getOnDemandPostPaidAccountType());
    User owner = createTestUserInTenant(tenant);
    SpendAlertSubscription subscription = new SpendAlertSubscription(41, "444", tenant, owner);
    dao.save(subscription);
    String remove = controller.removeSpendAlertSubscription(subscription.getId().toString(), map);
    Assert.assertEquals(remove, new String("redirect:/portal/users/subscribe"));
  }

  /**
   * @Desc Test to add a valid secondary email's
   * @author vinayv
   */
  @Test
  public void testsaveAlertsDeliveryOptions() {
    logger.info("Entering testsaveAlertsDeliveryOptions test");
    User user = userDAO.find("3");
    asUser(user);
    int beforeCount = userAlertPreferencesService.listAllUserAlertPreferences(user).size();
    int beforeEventCount = eventDAO.count();
    String saveStatus = controller.saveAlertsDeliveryOptions("Test@citrix.com", map);
    Assert.assertEquals("success", saveStatus);
    Assert.assertNotNull(map.containsAttribute("user"));
    int afterCount = userAlertPreferencesService.listAllUserAlertPreferences(user).size();
    int afterEventCount = eventDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals(beforeEventCount + 1, afterEventCount);
    PortalEvent event = eventListener.getEvents().get(0);
    Assert.assertEquals(user, event.getSource());
    Assert.assertTrue(event.getPayload() instanceof VerifyAlertEmailRequest);
    logger.info("Exiting testsaveAlertsDeliveryOptions test");
  }

  @Test
  public void testLogin() {
    Tenant tenant = service.getTenantByParam("id", "2", false);
    String login = controller.login(tenant, tenant.getParam(), "Test", "4847df70-63bb-4273-a8db-30662b32d098", map);
    // as the mock returns view with url test
    Assert.assertEquals("test&lp=Test", login);
  }

  @Test
  public void testvalidateemail() {
    String valid = controller.validateemail("0e04370a-e407-490f-8256-40c4e8c4fb6b", "TEST@citrix.com", null, map);
    Assert.assertTrue(Boolean.valueOf(valid));

  }

  @Test
  public void testeditPrefs() throws Exception {
    User user = userService.getUserByParam("id", "3", false);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    form.setUser((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    BindingResult bindingResult = validate(form);
    Map<String, String> controllerMap = controller.editPrefs(form, bindingResult, VALID_TIMEZONE,
        Locale.ENGLISH.toString(), map, mockRequest);
    Assert.assertNotNull(controllerMap);
    Assert.assertEquals(user.getTimeZone(), VALID_TIMEZONE);
    Assert.assertEquals(user.getLocale(), Locale.ENGLISH);
    Assert.assertTrue(controllerMap.containsKey("timeZone"));
    Assert.assertTrue(controllerMap.containsKey("locale"));
    Assert.assertTrue(controllerMap.containsKey("lastLogin"));
    Assert.assertEquals(controllerMap.get("timeZone"), VALID_TIMEZONE);
    Assert.assertEquals(controllerMap.get("locale").toString(), new String("English/US"));
    controllerMap.clear();
    controllerMap = controller.editPrefs(form, null, null, null, map, mockRequest);
    user = userService.getUserByParam("id", "3", false);
    Assert.assertNull(user.getLocale());

  }

  /**
   * @Desc Test to remove a user By Root
   * @author vinayv
   */
  @Test
  public void testdelete() {
    User user = userDAO.find(3L);
    Assert.assertEquals(null, user.getRemoved());
    String view = null;
    try {
      view = controller.delete(user.getParam(), map, session);
    } catch (NoSuchUserException e) {
      e.printStackTrace();
    }
    Assert.assertNull(view);
    Assert.assertNotNull(user.getRemoved());
    List<PortalEvent> eventList = eventListener.getEvents();
    PortalEvent deletionEvent = eventList.get(eventList.size() - 1);
    Assert.assertEquals(user, deletionEvent.getSource());
    Assert.assertTrue(deletionEvent.getPayload() instanceof UserDeletion);
  }

  /**
   * @Desc Test to remove a user By Master User
   * @author vinayv
   */
  @Test
  public void testDeleteAsTenant() {
    logger.info("Entering testDeleteAsTenant test");
    User masterUser = userDAO.find(21L);
    asUser(masterUser);
    List<User> userList = userService.getUsersInTenantByProfile(masterUser.getTenant(), 1, 10,
        profileDAO.findByName("User"));
    User user = userList.get(0);
    Assert.assertEquals(null, user.getRemoved());
    String view = null;
    try {
      view = controller.delete(user.getParam(), map, session);
    } catch (NoSuchUserException e) {
      e.printStackTrace();
    }
    Assert.assertNull(view);
    Assert.assertNotNull(user.getRemoved());
    List<PortalEvent> eventList = eventListener.getEvents();
    PortalEvent deletionEvent = eventList.get(eventList.size() - 1);
    Assert.assertEquals(user, deletionEvent.getSource());
    Assert.assertTrue(deletionEvent.getPayload() instanceof UserDeletion);
    logger.info("Exiting testDeleteAsTenant test");
  }

  /**
   * @Desc Test to remove a user By Normal User
   * @author vinayv
   */
  @Test
  public void testDeleteAsNormalUser() {
    logger.info("Entering testDeleteAsNormalUser test");
    User normalUser = userDAO.find(22L);
    User masterUser = userDAO.find(21L);
    asUser(normalUser);
    try {
      controller.delete(masterUser.getParam(), map, session);
    } catch (Exception e) {
      Assert.assertEquals("Access is denied", e.getMessage());
    }
    logger.info("Exiting testDeleteAsNormalUser test");
  }

  @Test
  public void testResolveViewForSettingFromServiceInstance() {
    View view = controller.resolveViewForSettingFromServiceInstance(getDefaultTenant(),
        "003fa8ee-fba3-467f-a517-fd806dae8a80");
    Assert.assertEquals("view", view.getName());
    Assert.assertEquals("http://www.google.com", view.getURL());
    Assert.assertEquals(ViewMode.IFRAME, view.getMode());
  }

  @Test
  public void testResolveViewForAccountSettingFromServiceInstance() {
    Tenant tenant = service.getTenantByParam("id", "2", false);
    View view = controller.resolveViewForAccountSettingFromServiceInstance(tenant.getParam(),
        "003fa8ee-fba3-467f-a517-fd806dae8a80");
    Assert.assertEquals("view", view.getName());
    Assert.assertEquals("http://www.google.com", view.getURL());
    Assert.assertEquals(ViewMode.IFRAME, view.getMode());
  }

  @Test
  public void testshow() {
    Tenant tenant = tenantService.getTenantByParam("id", "3", false);
    asUser(tenant.getOwner());
    controller.show(tenant.getOwner().getParam(), map);
    Assert.assertTrue(map.containsKey("serviceRegistrationStatus"));
    Assert.assertEquals(Boolean.FALSE, map.get("showEnableServiceLink"));

    asUser(tenantService.getSystemUser(Handle.ROOT));
    tenant = tenantService.getTenantByParam("id", "5", false);
    asUser(tenant.getOwner());
    controller.show(tenant.getOwner().getParam(), map);
    Assert.assertTrue(map.containsKey("serviceRegistrationStatus"));
    Assert.assertEquals(Boolean.TRUE, map.get("showEnableServiceLink"));
  }

  @Test
  public void testEnableService() {
    Tenant tenant = tenantService.get("17eda09f-506b-4364-b67e-469071429b76");
    User user = createTestUserInTenant(tenant);
    user = controller.activateUser(user.getParam(), map);
    user.setEnabled(true);
    userService.save(user);
    String userUUID = user.getUuid();
    controller.enableServices("17eda09f-506b-4364-b67e-469071429b76", userUUID, "003fa8ee-fba3-467f-a517-fd806dae8a80");
    Map<String, Object> es = controller.getServiceProvisioningStatus("17eda09f-506b-4364-b67e-469071429b76", userUUID,
        "003fa8ee-fba3-467f-a517-fd806dae8a80");
    Assert.assertTrue(!es.isEmpty());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUsersListShowForSurrogatedTenant() {
    asRoot();
    Tenant tenant = getDefaultTenant();
    List<User> expected = userService.list(0, 0, null, null, false, null, tenant.getId().toString(), null);
    HttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    String view = controller.listUsersForAccount(controller.getTenant(), false, tenant.getParam(), map, session, null,
        1, 20, "true", mockRequest);
    Assert.assertEquals("users.list_with_user_menu", view);
    Assert.assertTrue(map.containsKey("users"));
    List<User> found = (List<User>) map.get("users");
    Assert.assertTrue(found.containsAll(expected));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUsersListWithUserParam() {
    User user = userDAO.find(3L);
    asUser(user);
    List<User> expected = userService.list(0, 0, null, null, false, null, user.getTenant().getId().toString(), null);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    String view = controller.listUsersForAccount(controller.getTenant(), true, null, map, session, user.getParam(), 1,
        20, "true", mockRequest);
    Assert.assertEquals("users.nonroot.list_with_user_menu", view);
    Assert.assertTrue(map.containsKey("users"));
    List<User> found = (List<User>) map.get("users");
    Assert.assertTrue(found.containsAll(expected));
    Assert.assertTrue(map.get("page") == Page.ADMIN_ALL_USERS);
  }

  @Test
  public void testCreateUserwithBlackListedEmail() throws Exception {
    Configuration configuration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.emailDomain.blacklist");
    configuration.setValue("test.com");
    configurationService.update(configuration);

    User user = userDAO.find(3L);
    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("subodh@test.com");
    newUser.setUsername("Subodh");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());

    BindingResult bindingResult = validate(form);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest,
        new MockHttpSession());
    Assert.assertEquals("users.new.step1", view);
  }

  @Test
  public void testCreateUserwithBlackListedEmail2() throws Exception {
    Configuration configuration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.emailDomain.blacklist");
    configuration.setValue("test.com");
    configurationService.update(configuration);
    asRoot();
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("subodh@test.com");
    newUser.setUsername("Subodh");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());

    BindingResult bindingResult = validate(form);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest,
        new MockHttpSession());
    Assert.assertEquals("users.new.step1", view);
  }

  @Test
  public void testCreateUserwithSurrogatedTenantBlackListedEmail() throws Exception {
    Configuration configuration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.emailDomain.blacklist");
    configuration.setValue("test.com");
    configurationService.update(configuration);
    asRoot();
    // Tenant tenant = getDefaultTenant();
    // User user = tenant.getOwner();
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("subodh@test.com");
    newUser.setUsername("Subodh");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());
    request = new MockHttpServletRequest();
    request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    BindingResult bindingResult = validate(form);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    Tenant tenant = tenantDAO.find(3L);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest,
        new MockHttpSession());
    Assert.assertEquals("users.new.step1", view);
  }

  @Test
  public void testCreateUserwithSurrogatedTenantBlackListedEmail2() throws Exception {
    Configuration configuration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.emailDomain.blacklist");
    configuration.setValue("test.com");
    configurationService.update(configuration);
    asRoot();
    // Tenant tenant = getDefaultTenant();
    // User user = tenant.getOwner();
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("subodh@test.com");
    newUser.setUsername("Subodh");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());

    BindingResult bindingResult = validate(form);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    // request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
    mockRequest.addParameter("submitButtonEmail", "Finish");
    Tenant tenant = tenantDAO.find(3L);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest,
        new MockHttpSession());
    Assert.assertEquals("users.new.step1", view);
  }

  @Test
  public void testCreateUserMaxUserCount() throws Exception {

    Configuration configuration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.emailDomain.blacklist");
    configuration.setValue("test.com");
    configurationService.update(configuration);
    Tenant tenant = tenantDAO.find(3L);
    List<User> userList = tenant.getAllUsers();
    int count = userList.size();
    long temp = Long.valueOf(count);
    tenant.setMaxUsers(temp);
    tenantService.update(tenant);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("subodh@test.com");
    newUser.setUsername("Subodh");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());

    BindingResult bindingResult = validate(form);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(tenant);
    try {
      controller.createUserStepTwo(form, bindingResult, proxyTenant, map, mockRequest, new MockHttpSession());
    } catch (Exception e) {
      Assert.assertEquals(
          "You have reached the maximum number of users. To increase your limit, please contact support",
          e.getMessage());
      logger.debug("###Exiting testCreateUserMaxUserCount");
    }
  }

  @Test
  public void testCreateStepThree() throws Exception {

    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("subodh@test.com");
    newUser.setUsername("Subodh");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());
    form.setEmailText("Customized Email");

    BindingResult bindingResult = validate(form);
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.addParameter("submitButtonEmail", "Finish");
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String response = controller.createStepThree(form, bindingResult, proxyTenant, map, mockRequest);
    Assert.assertEquals("users.newuserregistration.finish", response);
  }

  @Test
  public void testEditUserWithTeleVerification() throws Exception {
    User user = userDAO.find(3L);
    asRoot();
    MockTelephoneVerificationService.enableFlag = true;
    String view = controller.edit(null, user.getUuid(),
        getRequestTemplate(HttpMethod.GET, "/users/" + user.getUuid() + "/myprofile"), map);
    UserForm form = (UserForm) map.get("user");
    Assert.assertNotNull(form);
    Assert.assertNotNull(form.getUser());
    Assert.assertEquals(user.getUsername(), form.getUser().getUsername());
    Assert.assertEquals("users.edit.myprofile", view);
  }

  @Test
  public void testViewAlertsDeliveryOptions() {
    String response = controller.viewAlertsDeliveryOptions(map);
    Assert.assertEquals(true, map.containsAttribute("alertsPrefs"));
    Assert.assertEquals("users.alerts.delivery_opts", response);
  }

  @Test
  public void testEditUserWithBlaclistedEmail() throws Exception {
    Configuration configuration = configurationService
        .locateConfigurationByName("com.citrix.cpbm.accountManagement.onboarding.emailDomain.blacklist");
    configuration.setValue("test.com");
    configurationService.update(configuration);
    asRoot();
    User user = userDAO.find(3L);
    UserForm form = new UserForm((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    user.setFirstName("Updated");
    user.setEmail("subodh@test.com");
    BindingResult result = validate(form);
    String view = controller.edit(user.getParam(), form, result,
        getRequestTemplate(HttpMethod.POST, "/users/3/myprofile"), map, status);
    Assert.assertEquals("users.edit.myprofile", view);

    User found = userDAO.find(user.getId());
    Assert.assertEquals(user.getFirstName(), found.getFirstName());

  }

  @Test
  public void testEditUserWithAlertPref() throws Exception {
    User user = userDAO.find(3L);
    asRoot();
    String emailAddress = "sk@test.com";

    UserAlertPreferences userAlertPreferences = userAlertPreferencesService.createUserAlertPreference(user,
        emailAddress, AlertType.USER_ALERT_EMAIL);
    userAlertPreferencesService.verifyUserAlertPreference(userAlertPreferences);
    String view = controller.edit(null, user.getUuid(),
        getRequestTemplate(HttpMethod.GET, "/users/" + user.getUuid() + "/myprofile"), map);
    UserForm form = (UserForm) map.get("user");
    Assert.assertNotNull(form);
    Assert.assertNotNull(form.getUser());
    Assert.assertEquals(user.getUsername(), form.getUser().getUsername());
    Assert.assertEquals("users.edit.myprofile", view);
  }

  @Test
  public void testGenerateGravatarUrlWithIntranetOnlyModeDisabled() throws Exception {
    com.vmops.model.Configuration isIntranetModeEnabled = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_use_intranet_only);
    isIntranetModeEnabled.setValue("false");
    request = new MockHttpServletRequest();
    request.setSession(session);
    controller.edit("", getRootUser().getParam(), request, map);
    Assert.assertTrue(map.containsKey("gravatarUrl"));
    Assert.assertTrue(map.get("gravatarUrl").toString().contains("gravatar.com"));
    Assert.assertFalse((Boolean) map.get("doNotShowGravatarLink"));
  }

  @Test
  public void testGenerateGravatarUrlWithIntranetOnlyModeEnabled() throws Exception {
    com.vmops.model.Configuration isIntranetModeEnabled = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_use_intranet_only);
    isIntranetModeEnabled.setValue("true");
    request = new MockHttpServletRequest();
    request.setSession(session);
    controller.edit("", getRootUser().getParam(), request, map);
    Assert.assertTrue(map.containsKey("gravatarUrl"));
    Assert.assertFalse(map.get("gravatarUrl").toString().contains("gravatar.com"));
    Assert.assertTrue(map.get("gravatarUrl").toString().contains("portal/images"));
    Assert.assertTrue((Boolean) map.get("doNotShowGravatarLink"));
  }

  /**
   * @Desc Test to create Normal User By Root
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateNormalUserAsRoot() throws Exception {
    logger.info("Entering testCreateNormalUserAsRoot test");
    int beforeCount = userDAO.count();
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("User");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("User", obtainedUser.getProfile().getName());
    User userFromDB = userDAO.find(obtainedUser.getId());
    PortalEvent event = eventListener.getEvents().get(0);
    Assert.assertEquals(userFromDB, event.getSource());
    Assert.assertTrue(event.getPayload() instanceof UserCreation);
    logger.info("Exiting testCreateNormalUserAsRoot test");
  }

  /**
   * @Desc Test to create Master User By Root
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateMasterUserAsRoot() throws Exception {
    logger.info("Entering testCreateMasterUserAsRoot test");
    int beforeCount = userDAO.count();
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Master User");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("Master User", obtainedUser.getProfile().getName());
    User userFromDB = userDAO.find(obtainedUser.getId());
    PortalEvent event = eventListener.getEvents().get(0);
    Assert.assertEquals(userFromDB, event.getSource());
    Assert.assertTrue(event.getPayload() instanceof UserCreation);
    logger.info("Exiting testCreateMasterUserAsRoot test");
  }

  /**
   * @Desc Test to create Normal User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateNormalUserAsTenant() throws Exception {
    logger.info("Entering testCreateNormalUserAsTenant test");
    int beforeCount = userDAO.count();
    User user = userDAO.find(3L);
    asUser(user);
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("User");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("User", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreateNormalUserAsTenant test");
  }

  /**
   * @Desc Test to create Power User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreatePowerUserAsTenant() throws Exception {
    logger.info("Entering testCreatePowerUserAsTenant test");
    int beforeCount = userDAO.count();
    User user = userDAO.find(3L);
    asUser(user);
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Power User");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("Power User", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreatePowerUserAsTenant test");
  }

  /**
   * @Desc Test to create Billing Admin User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateBillingAdminUserAsTenant() throws Exception {
    logger.info("Entering testCreateBillingAdminUserAsTenant test");
    int beforeCount = userDAO.count();
    User user = userDAO.find(3L);
    asUser(user);
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Billing Admin");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("Billing Admin", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreateBillingAdminUserAsTenant test");
  }

  /**
   * @Desc Test to create Ops Admin User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateOpsAdminUserAsRoot() throws Exception {
    logger.info("Entering testCreateOpsAdminUserAsRoot test");
    int beforeCount = userDAO.count();
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Ops Admin");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("Ops Admin", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreateOpsAdminUserAsRoot test");
  }

  /**
   * @Desc Test to create Sales Support User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateSalesSupportUserAsRoot() throws Exception {
    logger.info("Entering testCreateSalesSupportUserAsRoot test");
    int beforeCount = userDAO.count();
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Sales Support");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("Sales Support", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreateSalesSupportUserAsRoot test");
  }

  /**
   * @Desc Test to create Help Desk User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateHelpDeskUserAsRoot() throws Exception {
    logger.info("Entering testCreateHelpDeskUserAsRoot test");
    int beforeCount = userDAO.count();
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Sales Support");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("Sales Support", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreateHelpDeskUserAsRoot test");
  }

  /**
   * @Desc Test to create Product Manager User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateProductManagerUserAsRoot() throws Exception {
    logger.info("Entering testCreateProductManagerUserAsRoot test");
    int beforeCount = userDAO.count();
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Product Manager");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("Product Manager", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreateProductManagerUserAsRoot test");
  }

  /**
   * @Desc Test to create Finance Admin User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateFinanceAdminUserAsRoot() throws Exception {
    logger.info("Entering testCreateFinanceAdminUserAsRoot test");
    int beforeCount = userDAO.count();
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Finance Admin");
    Assert.assertNotNull(obtainedUser);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("Finance Admin", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreateFinanceAdminUserAsRoot test");
  }

  /**
   * @Desc Test to activate newly created Normal User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testActivateNormalUser() throws Exception {
    logger.info("Entering testActivateNormalUser test");
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("User");
    Assert.assertNotNull(obtainedUser);
    Long userId = obtainedUser.getId();
    User user = userDAO.find(userId);
    Assert.assertEquals(false, user.isEnabled());
    user.setEmailVerified(true);
    userDAO.merge(user);
    User activatedUser = controller.activateUser(user.getUuid(), map);
    Assert.assertEquals(true, activatedUser.isEnabled());
    PortalEvent event = eventListener.getEvents().get(1);
    Assert.assertEquals(user, event.getSource());
    Assert.assertTrue(event.getPayload() instanceof UserActivateEmail);
    logger.info("Exiting testActivateNormalUser test");
  }

  /**
   * @Desc Test to activate newly created Service Provider User
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testActivateServiceProviderUser() throws Exception {
    logger.info("Entering testActivateServiceProviderUser test");
    com.citrix.cpbm.access.User obtainedUser = createUserByProfile("Ops Admin");
    Assert.assertNotNull(obtainedUser);
    Long userId = obtainedUser.getId();
    User user = userDAO.find(userId);
    Assert.assertEquals(false, user.isEnabled());
    user.setEmailVerified(true);
    userDAO.merge(user);
    User activatedUser = controller.activateUser(user.getUuid(), map);
    Assert.assertEquals(true, activatedUser.isEnabled());
    PortalEvent event = eventListener.getEvents().get(1);
    Assert.assertEquals(user, event.getSource());
    Assert.assertTrue(event.getPayload() instanceof UserActivateEmail);
    logger.info("Exiting testActivateServiceProviderUser test");
  }

  /**
   * @Desc Test to deactivate a user By Tenant
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testDeactivateUserByTenant() throws Exception {
    logger.info("Entering testDeactivateUserByTenant test");
    UserHandle handle = userHandleDAO.find(3L);
    handle.setServiceInstanceUuid("4847df70-63bb-4273-a8db-30662b32d098");
    userHandleDAO.merge(handle);
    User masterUser = userDAO.find(21L);
    asUser(masterUser);
    List<User> userList = userService.getUsersInTenantByProfile(masterUser.getTenant(), 1, 10,
        profileDAO.findByName("User"));
    User user = userList.get(0);
    // deactivating the user
    User deactivatedUser = controller.deactivateUser(user.getUuid(), map);
    Assert.assertEquals(false, deactivatedUser.isEnabled());
    List<PortalEvent> eventList = eventListener.getEvents();
    Assert.assertEquals(user, eventList.get(0).getSource());
    Assert.assertTrue(eventList.get(0).getPayload() instanceof UserDeactivateEmail);
    logger.info("Entering testDeactivateUserByTenant test");
  }

  /**
   * @Desc Test to create Normal User with Custom Email template
   * @author vinayv
   * @throws Exception
   */
  @Test
  public void testCreateNormalUserWithCustomEmailTemplate() throws Exception {
    logger.info("Entering testCreateNormalUserWithCustomEmailTemplate test");
    int beforeCount = userDAO.count();
    User user = userDAO.find(3L);
    asUser(user);
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("test@test.com");
    newUser.setUsername("testuser");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName("User");
    form.setUserProfile(profile.getId());
    request.addParameter("submitButtonEmail", "CustomeEmail");
    BindingResult bindingResult = validate(form);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, request, new MockHttpSession());
    Assert.assertEquals("users.newuser.customemail", view);
    UserForm obtainedForm = (UserForm) map.get("user");
    Assert.assertEquals(form.getUser().getUsername(), obtainedForm.getUser().getUsername());
    com.citrix.cpbm.access.User obtainedUser = obtainedForm.getUser();
    Assert.assertNotNull(obtainedUser);
    obtainedForm.setCustomEmailSubject("customEmailSubject");
    obtainedForm.setEmailText("customEmailText");
    bindingResult = validate(obtainedForm);
    view = controller.createStepThree(obtainedForm, bindingResult, proxyTenant, map, request);
    Assert.assertEquals("users.newuserregistration.finish", view);
    int afterCount = userDAO.count();
    Assert.assertEquals(beforeCount + 1, afterCount);
    Assert.assertEquals("User", obtainedUser.getProfile().getName());
    logger.info("Exiting testCreateNormalUserWithCustomEmailTemplate test");
  }

  /**
   * @Desc Private method to create a user
   * @author vinayv
   * @param profileName
   * @return user object
   * @throws Exception
   */
  private com.citrix.cpbm.access.User createUserByProfile(String profileName) throws Exception {
    UserForm form = new UserForm();
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));
    com.citrix.cpbm.access.User newUser = form.getUser();
    newUser.setEmail("test@test.com");
    newUser.setUsername("testuser");
    newUser.setFirstName("firstName");
    newUser.setLastName("lastName");
    Profile profile = profileDAO.findByName(profileName);
    form.setUserProfile(profile.getId());
    request.addParameter("submitButtonEmail", "Finish");
    BindingResult bindingResult = validate(form);
    com.citrix.cpbm.access.Tenant proxyTenant = (com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(controller
        .getTenant());
    String view = controller.createUserStepTwo(form, bindingResult, proxyTenant, map, request, new MockHttpSession());
    Assert.assertEquals("users.newuserregistration.finish", view);
    UserForm obtainedForm = (UserForm) map.get("user");
    Assert.assertEquals(form.getUser().getUsername(), obtainedForm.getUser().getUsername());

    return obtainedForm.getUser();
  }
}
