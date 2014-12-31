/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.ui.ModelMap;

import web.WebTestsBase;
import web.support.DispatcherTestServlet;

import com.citrix.cpbm.portal.fragment.controllers.AuthenticationController;
import com.vmops.event.PasswordResetRequest;
import com.vmops.event.PortalEvent;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.model.UserAlertPreferences;
import com.vmops.model.UserAlertPreferences.AlertType;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ConfigurationService;
import com.vmops.service.UserAlertPreferencesService;
import com.vmops.service.exceptions.NoSuchUserException;
import com.vmops.service.exceptions.UserAuthorizationInvalidException;
import com.vmops.utils.CryptoUtils;
import com.vmops.web.controllers.AbstractBaseController;
import com.vmops.web.filters.CaptchaAuthenticationFilter;

public class AuthenticationControllerTest extends WebTestsBase {

  @Autowired
  private AuthenticationController controller;

  @Autowired
  private Configuration config;

  @Autowired
  private ConfigurationService configurationService;

  private ModelMap map;

  private MockHttpSession session;

  @Autowired
  private UserAlertPreferencesService userAlertPreferencesService;

  @BeforeClass
  public static void initMail() {
    setupMail();
  }

  @Before
  public void init() {
    map = new ModelMap();
    session = new MockHttpSession();
    asAnonymous();
  }

  @Test
  public void testLandingRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = getServletInstance();
    Method expected = locateMethod(controller.getClass(), "login", new Class[] {
        HttpServletRequest.class, ModelMap.class, HttpSession.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/login"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controller.getClass(), "loggedout", new Class[] {
        java.lang.String.class, ModelMap.class, HttpSession.class, HttpServletResponse.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/userParam/loggedout"));
    Assert.assertEquals(expected, handler);

    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/reset_password");

    expected = locateMethod(controller.getClass(), "requestReset", new Class[] {
      ModelMap.class,
    });
    handler = servlet.recognize(request);
    Assert.assertEquals(expected, handler);

    request.removeAllParameters();
    request.addParameter("username", "value");
    request.setMethod(HttpMethod.POST.name());
    expected = locateMethod(controller.getClass(), "requestReset", new Class[] {
        String.class, HttpServletRequest.class, ModelMap.class
    });
    handler = servlet.recognize(request);
    Assert.assertEquals(expected, handler);

    request.removeAllParameters();
    request.setMethod(HttpMethod.GET.name());
    request.addParameter("a", "value");
    request.addParameter("t", "0");
    request.addParameter("i", "value");
    expected = locateMethod(controller.getClass(), "reset", new Class[] {
        String.class, Long.TYPE, String.class, HttpSession.class, ModelMap.class
    });
    handler = servlet.recognize(request);
    Assert.assertEquals(expected, handler);

    request.removeAllParameters();
    request.addParameter("password", "password");
    request.setMethod(HttpMethod.POST.name());
    expected = locateMethod(controller.getClass(), "reset", new Class[] {
        String.class, HttpSession.class, HttpServletRequest.class
    });
    handler = servlet.recognize(request);
    Assert.assertEquals(expected, handler);

    request.removeAllParameters();
    expected = locateMethod(controller.getClass(), "requestCall", new Class[] {
        String.class, String.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/request_call_by_user"));
    Assert.assertEquals(expected, handler);

    request.removeAllParameters();
    expected = locateMethod(controller.getClass(), "requestSMS", new Class[] {
        String.class, String.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/request_sms_by_user"));
    Assert.assertEquals(expected, handler);

    request.removeAllParameters();
    expected = locateMethod(controller.getClass(), "verifyAdditionalEmail", new Class[] {
        String.class, String.class, String.class, HttpServletRequest.class, ModelMap.class, HttpSession.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/verify_additional_email"));
    Assert.assertEquals(expected, handler);

    request.removeAllParameters();
    expected = locateMethod(controller.getClass(), "getGoogleAnalytics", new Class[] {});
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/getGoogleAnalytics"));
    Assert.assertEquals(expected, handler);

  }

  @Test
  public void testLogin() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    String actualResult = controller.login(request, map, new MockHttpSession());
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLoginUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          actualResult);
    } else {
      Assert.assertEquals("auth.login", actualResult);
    }
  }

  @Test
  public void testLoginFailed() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.setParameter("login_failed", "");
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY, "someuser");
    mockSession.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, new DisabledException("Account is disabled"));
    String actualResult = controller.login(request, map, mockSession);
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLoginUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          actualResult);
    } else {
      Assert.assertEquals("auth.login", actualResult);
    }
    Assert.assertTrue((Boolean) map.get("loginFailed"));
    Assert.assertEquals("someuser", map.get("lastUser"));
    Assert.assertNotNull(map.get("error"));
  }

  @Test
  public void testLoginFailedDisabled() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.setParameter("login_failed", "");
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY, "someuser");
    mockSession.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, new DisabledException("Account is disabled"));
    String actualResult = controller.login(request, map, mockSession);
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLoginUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          actualResult);
    } else {
      Assert.assertEquals("auth.login", actualResult);
    }
    String message = (String) map.get("error");
    Assert.assertNotNull(message);
    Assert.assertThat(message, JUnitMatchers.containsString("Username or password incorrect."));
  }

  @Test
  public void testLoginFailedLocked() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.setParameter("login_failed", "");
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY, "someuser");
    mockSession.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, new LockedException("Account is locked"));
    String actualResult = controller.login(request, map, mockSession);
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLoginUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          actualResult);
    } else {
      Assert.assertEquals("auth.login", actualResult);
    }
    String message = (String) map.get("error");
    Assert.assertNotNull(message);
    Assert.assertThat(message, JUnitMatchers.containsString("Username or password incorrect."));
  }

  @Test
  public void testLoginFailedAuth() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.setParameter("login_failed", "");
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY, "someuser");
    mockSession.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, new BadCredentialsException("Bad creds"));
    String actualResult = controller.login(request, map, mockSession);
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLoginUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          actualResult);
    } else {
      Assert.assertEquals("auth.login", actualResult);
    }
    String message = (String) map.get("error");
    Assert.assertNotNull(message);
    Assert.assertThat(message, JUnitMatchers.containsString("Username or password incorrect."));
  }

  @Test
  public void testLoginFailedOther() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.setParameter("login_failed", "");
    MockHttpSession mockSession = new MockHttpSession();
    mockSession.setAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY, "someuser");
    mockSession.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, new Exception());
    String actualResult = controller.login(request, map, mockSession);
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLoginUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          actualResult);
    } else {
      Assert.assertEquals("auth.login", actualResult);
    }
    String message = (String) map.get("error");
    Assert.assertNotNull(message);
    Assert.assertThat(message, JUnitMatchers.containsString("Unknown error"));
  }

  @Test
  public void testLoginAfterLogout() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.setParameter("logout", "");
    String actualResult = controller.login(request, map, new MockHttpSession());
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLoginUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          actualResult);
    } else {
      Assert.assertEquals("auth.login", actualResult);
    }
    boolean message = (Boolean) map.get("logout");
    Assert.assertTrue(message);
  }

  @Test
  public void testLoggedOut() throws Exception {
    asRoot();
    User user = getSystemTenant().getOwner();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();
    String view = controller.loggedout(user.getUuid(), map, session, response, request);
    Cookie cookie = response.getCookie("JforumSSO");
    Assert.assertEquals(cookie.getValue(), "");
    Assert.assertTrue(session.isInvalid());
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLogoutUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          view);
    } else {
      Assert.assertEquals("redirect:/j_spring_security_logout", view);
    }

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testLoginWithSuffixList() throws Exception {
    asRoot();
    Tenant defaultTenant = getDefaultTenant();
    defaultTenant.setUsernameSuffix("test");
    tenantService.save(defaultTenant);

    com.vmops.model.Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_username_duplicate_allowed);
    configuration.setValue("true");
    configurationService.update(configuration);

    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    String actualResult = controller.login(request, map, new MockHttpSession());
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLoginUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          actualResult);
    } else {
      Assert.assertEquals("auth.login", actualResult);
    }

    List<String> suffixList = (List<String>) map.get("suffixList");
    Assert.assertEquals(1, suffixList.size());

    defaultTenant = getDefaultTenant();
    defaultTenant.setUsernameSuffix(null);
    tenantService.save(defaultTenant);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testLoggedOutWithSuffix() throws Exception {
    asRoot();
    Tenant defaultTenant = getDefaultTenant();
    defaultTenant.setUsernameSuffix("test");
    tenantService.save(defaultTenant);

    com.vmops.model.Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_username_duplicate_allowed);
    configuration.setValue("true");
    configurationService.update(configuration);

    User user = getSystemTenant().getOwner();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();
    String view = controller.loggedout(user.getUuid(), map, session, response, request);
    Cookie cookie = response.getCookie("JforumSSO");
    Assert.assertEquals(cookie.getValue(), "");
    if (config.getAuthenticationService().compareToIgnoreCase("cas") == 0) {
      Assert.assertEquals(
          "redirect:" + config.getCasLogoutUrl() + "?service=" + URLEncoder.encode(config.getCasServiceUrl(), "UTF-8"),
          view);
    } else {
      Assert.assertEquals("redirect:/j_spring_security_logout", view);
    }

    List<String> suffixList = (List<String>) map.get("suffixList");
    Assert.assertEquals(1, suffixList.size());

    defaultTenant = getDefaultTenant();
    defaultTenant.setUsernameSuffix(null);
    tenantService.save(defaultTenant);

  }

  @Test
  public void testPasswordResetRequest() throws Exception {
    String view = controller.requestReset(map);
    Assert.assertEquals("auth.request_reset", view);
  }

  @Test
  public void testPasswordResetRequestSubmit() throws Exception {
    User user = userDAO.find(2L);
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/portal/reset_password");
    request.setServletPath("/portal");
    request.setContextPath("/portal");
    request.setLocalPort(8080);
    String view = controller.requestReset(user.getUsername(), request, new ModelMap());
    Assert.assertEquals("auth.request_reset_success", view);
    Assert.assertEquals(1, eventListener.getEvents().size());
    PortalEvent event = eventListener.getEvents().get(0);
    Assert.assertTrue(event.getPayload() instanceof PasswordResetRequest);
    Assert.assertEquals(user.getUsername(), ((PasswordResetRequest) event.getPayload()).getUsername());
    Assert.assertEquals(user, event.getSource());
  }

  @Test
  public void testPasswordResetRequestSubmitUnverifiedUser() throws Exception {
    asRoot();
    User user = createTestUserInTenant(getDefaultTenant());
    eventListener.clear();
    asAnonymous();
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/portal/reset_password");
    request.setServletPath("/portal");
    request.setContextPath("/portal");
    request.setLocalPort(8080);
    String view = controller.requestReset(user.getUsername(), request, new ModelMap());
    Assert.assertEquals("auth.request_reset_success", view);
    Assert.assertEquals(0, eventListener.getEvents().size());
  }

  @Test
  public void testReset() {
    MockHttpSession mockSession = new MockHttpSession();
    User user = userDAO.find(3L);
    long genTime = System.currentTimeMillis();
    String auth = user.getAuthorization(genTime);
    String view = controller.reset(auth, genTime, user.getParam(), mockSession, map);
    Assert.assertEquals("auth.reset", view);
    Assert.assertNotNull(mockSession.getAttribute(AbstractBaseController.RESET_USER_KEY));
    Assert.assertEquals(user.getUsername(), mockSession.getAttribute(AbstractBaseController.RESET_USER_KEY));
  }

  @Test(expected = UserAuthorizationInvalidException.class)
  public void testBadReset() {
    MockHttpSession mockSession = new MockHttpSession();
    User user = userDAO.find(3L);
    long genTime = System.currentTimeMillis();
    String auth = user.getAuthorization(genTime);
    String view = controller.reset(auth, genTime - 100, user.getParam(), mockSession, map);
    Assert.assertEquals("auth.reset", view);
    Assert.assertNotNull(mockSession.getAttribute(AbstractBaseController.RESET_USER_KEY));
    Assert.assertEquals(user.getUsername(), mockSession.getAttribute(AbstractBaseController.RESET_USER_KEY));
  }

  @Test
  public void testResetPassword() {
    MockHttpSession mockSession = new MockHttpSession();
    HttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    User user = userDAO.find(3L);
    mockSession.setAttribute(AbstractBaseController.RESET_USER_KEY, user.getUsername());
    String newpass = "newPassw0rd";
    String view = controller.reset(newpass, mockSession, mockHttpServletRequest);
    Assert.assertEquals("redirect:/portal/login", view);
    userDAO.flush();
    userDAO.refresh(user);
    Assert.assertTrue(user.authenticate(newpass));
  }

  @Test(expected = NoSuchUserException.class)
  public void testBadResetPassword() {
    MockHttpSession mockSession = new MockHttpSession();
    String newpass = "newPassw0rd";
    HttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    controller.reset(newpass, mockSession, mockHttpServletRequest);
  }

  @Test
  public void testVerifyAdditionalEmail() {
    asRoot();
    User user = createTestUserInTenant(getDefaultTenant());
    UserAlertPreferences uap = userAlertPreferencesService.createUserAlertPreference(user, "test@test123.com",
        AlertType.USER_ALERT_EMAIL);
    controller.verifyAdditionalEmail(user.getAuthorization(0), user.getParam(),
        CryptoUtils.encrypt(uap.getEmailAddress(), CryptoUtils.keyGenerationSeed),
        getRequestTemplate(HttpMethod.GET, "/verify_additional_email"), map, session);
    UserAlertPreferences uapSaved = userAlertPreferencesService.locateUserAlertPreference(user, uap.getEmailAddress());
    Assert.assertTrue(uapSaved.isEmailVerified());
  }

  @Test
  public void testVerifyAdditionalEmailWithoutLogin() {
    asRoot();
    User user = createTestUserInTenant(getDefaultTenant());
    UserAlertPreferences uap = userAlertPreferencesService.createUserAlertPreference(user, "test@test123.com",
        AlertType.USER_ALERT_EMAIL);
    asAnonymous();
    controller.verifyAdditionalEmail(user.getAuthorization(0), user.getParam(),
        CryptoUtils.encrypt(uap.getEmailAddress(), CryptoUtils.keyGenerationSeed),
        getRequestTemplate(HttpMethod.GET, "/verify_additional_email"), map, session);
    UserAlertPreferences uapSaved = userAlertPreferencesService.locateUserAlertPreference(user, uap.getEmailAddress());
    Assert.assertTrue(uapSaved.isEmailVerified());
  }

  @Test(expected = UserAuthorizationInvalidException.class)
  public void testVerifyAdditionalEmailInvalidAuthorization() {
    asRoot();
    User user = createTestUserInTenant(getDefaultTenant());
    UserAlertPreferences uap = userAlertPreferencesService.createUserAlertPreference(user, "test@test1234.com",
        AlertType.USER_ALERT_EMAIL);
    controller.verifyAdditionalEmail(user.getAuthorization(1), user.getParam(),
        CryptoUtils.encrypt(uap.getEmailAddress(), CryptoUtils.keyGenerationSeed),
        getRequestTemplate(HttpMethod.GET, "/verify_additional_email"), map, session);
    userAlertPreferencesService.locateUserAlertPreference(user, uap.getEmailAddress());
  }

  @Test
  public void testRequestCall() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.getSession().setAttribute("phoneVerificationPin", "5555");
    asRoot();
    User user = createTestUserInTenant(getDefaultTenant());
    Map<String, String> actualResult = controller.requestCall(user.getUsername(), null, request);
    Assert.assertEquals("You will receive a call shortly", actualResult.get("message"));
    Assert.assertEquals("success", actualResult.get("result"));
    actualResult = controller.requestCall(user.getUsername(), "pick", request);
    Assert.assertEquals("Failed to make call. Please try later.", actualResult.get("message"));
    Assert.assertEquals("failed", actualResult.get("result"));
    request.getSession().setAttribute("phoneVerificationPin", "PINS");
    user.setCountryCode("480");
    actualResult = controller.requestCall(user.getUsername(), null, request);
    Assert.assertEquals("Failed to make call. Please try later.", actualResult.get("message"));
    Assert.assertEquals("failed", actualResult.get("result"));
    actualResult = controller.requestCall(null, "pickl3", request);
    Assert.assertEquals("Failed to make call. Please try later.", actualResult.get("message"));
    Assert.assertEquals("failed", actualResult.get("result"));
    actualResult = controller.requestCall("pick", "pickl3", request);
    Assert.assertEquals("Failed to make call. Please try later.", actualResult.get("message"));
    Assert.assertEquals("failed", actualResult.get("result"));
  }

  @Test
  public void tesRequestSMS() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.getSession().setAttribute("phoneVerificationPin", "7896");
    asRoot();
    User user = createTestUserInTenant(getDefaultTenant());
    Map<String, String> actualResult = controller.requestSMS(user.getUsername(), null, request);
    Assert.assertEquals("You will receive a text message shortly", actualResult.get("message"));
    Assert.assertEquals("success", actualResult.get("result"));
    request.getSession().setAttribute("vmops__reset_password__key", user.getUsername());
    actualResult = controller.requestSMS(user.getUsername(), "pick", request);
    Assert.assertEquals("You will receive a text message shortly", actualResult.get("message"));
    Assert.assertEquals("success", actualResult.get("result"));
    user.setCountryCode("480");
    actualResult = controller.requestSMS(user.getUsername(), null, request);
    Assert.assertEquals("Failed to send text message. Please try later.", actualResult.get("message"));
    Assert.assertEquals("failed", actualResult.get("result"));
    actualResult = controller.requestSMS(null, "pickl3", request);
    Assert.assertEquals("Failed to send text message. Please try later.", actualResult.get("message"));
    Assert.assertEquals("failed", actualResult.get("result"));
    actualResult = controller.requestSMS("picky", "pickl3", request);
    Assert.assertEquals("You will receive a text message shortly", actualResult.get("message"));
    Assert.assertEquals("success", actualResult.get("result"));
  }

  @Test
  public void testVerifyPhoneVerificationPINForUnlock() throws Exception {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.getSession().setAttribute("phoneVerificationPin", "496");
    asRoot();
    String actualResult = controller.verifyPhoneVerificationPINForUnlock("PIN", request);
    Assert.assertEquals("failed", actualResult);
    request.getSession().setAttribute("phoneVerificationPin", "PIN");
    actualResult = controller.verifyPhoneVerificationPINForUnlock("PIN", request);
    Assert.assertEquals("success", actualResult);
  }

  @Test
  public void testLoginWithIntranetOnlyModeDisabled() throws Exception {
    com.vmops.model.Configuration isIntranetModeEnabled = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_use_intranet_only);
    isIntranetModeEnabled.setValue("false");
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.getSession().setAttribute(CaptchaAuthenticationFilter.CAPTCHA_REQUIRED, true);
    controller.login(request, map, request.getSession());
    Assert.assertTrue(map.containsKey("showCaptcha"));
    Assert.assertTrue(Boolean.valueOf(map.get("showCaptcha").toString()));
    Assert.assertTrue(map.containsKey("recaptchaPublicKey"));

  }

  @Test
  public void testLoginWithIntranetOnlyModeEnabled() throws Exception {
    configurationService.clearConfigurationCache(true, "");
    com.vmops.model.Configuration isIntranetModeEnabled = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_use_intranet_only);
    isIntranetModeEnabled.setValue("true");
    configurationService.update(isIntranetModeEnabled);
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/login");
    request.getSession().setAttribute(CaptchaAuthenticationFilter.CAPTCHA_REQUIRED, true);
    controller.login(request, map, request.getSession());
    Assert.assertFalse(map.containsKey("showCaptcha"));
    Assert.assertFalse(map.containsKey("recaptchaPublicKey"));

  }

  @Test
  public void testGetGoogleAnalytics() {
    Map<String, String> ga = controller.getGoogleAnalytics();
    Assert.assertFalse(ga.isEmpty());
    Assert.assertTrue(ga.containsKey("enabled"));
    Assert.assertTrue(ga.containsKey("account"));
    Assert.assertTrue(ga.containsKey("domain"));
  }

}