/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.WebUtils;

import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.vmops.internal.service.EventService;
import com.vmops.internal.service.PrivilegeService;
import com.vmops.internal.service.TelephoneVerificationService;
import com.vmops.model.Channel;
import com.vmops.model.Event.Category;
import com.vmops.model.Event.Scope;
import com.vmops.model.Event.Severity;
import com.vmops.model.Event.Source;
import com.vmops.model.User;
import com.vmops.model.UserAlertPreferences;
import com.vmops.model.UserAlertPreferences.AlertType;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.TenantService;
import com.vmops.service.UserAlertPreferencesService;
import com.vmops.service.UserService;
import com.vmops.service.UserService.Handle;
import com.vmops.service.exceptions.NoSuchUserException;
import com.vmops.service.exceptions.TelephoneVerificationServiceException;
import com.vmops.utils.CryptoUtils;
import com.vmops.web.controllers.AbstractBaseController;
import com.vmops.web.filters.CaptchaAuthenticationFilter;
import com.vmops.web.filters.exceptions.CaptchaValidationException;
import com.vmops.web.filters.exceptions.FqdnNotFoundException;
import com.vmops.web.filters.exceptions.IpRangeValidationException;
import com.vmops.web.interceptors.PortalThemeResolver;

public abstract class AbstractAuthenticationController extends AbstractBaseController {

  /**
   * The parameter name that indicates login failed.
   */
  private static final String LOGIN_FAILED_PARAM = "login_failed";

  private static final String LOGOUT_PARAM = "logout";

  private static final String TIME_OUT = "timeout";

  private static final String VERIFY = "verify";

  private static final String CAS = "cas";

  @Autowired
  private TenantService tenantService;

  @Autowired
  private UserService userService;

  @Autowired
  private PrivilegeService privilegeService;

  @Autowired
  private EventService eventService;

// CHECKSTYLE:OFF
  @Autowired
  protected ConnectorManagementService connectorManagementService;

// CHECKSTYLE:ON
  @Autowired
  private UserAlertPreferencesService userAlertPreferencesService;

  @Autowired
  private ChannelService channelService;

  /**
   * Logger.
   */
  private static Logger logger = Logger.getLogger(AbstractAuthenticationController.class);

  @RequestMapping({
      "/login", "/portal", "/"
  })
  public String login(HttpServletRequest request, ModelMap map, HttpSession session) {
    logger.debug("###Entering in login(req,map,session) method");

    boolean loginFailed = request.getParameter(LOGIN_FAILED_PARAM) != null;

    if (!loginFailed && request.getUserPrincipal() != null) {
      map.clear();
      return "redirect:/portal/home";
    }

    if (session.getAttribute("email_verified") != null) {
      map.addAttribute("email_verified", session.getAttribute("email_verified"));
      session.removeAttribute("email_verified");
    }
    String showSuffixControl = "false";
    String suffixControlType = "textbox";
    List<String> suffixList = null;
    if (config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed).equals("true")) {
      showSuffixControl = "true";
      if (config.getValue(Names.com_citrix_cpbm_login_screen_tenant_suffix_dropdown_enabled).equals("true")) {
        suffixControlType = "dropdown";
        suffixList = tenantService.getSuffixList();
      }
    }
    map.addAttribute("showSuffixControl", showSuffixControl);
    map.addAttribute("suffixControlType", suffixControlType);
    map.addAttribute("suffixList", suffixList);
    if (config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled)
        && config.getValue(Names.com_citrix_cpbm_directory_mode).equals("pull")) {
      map.addAttribute("directoryServiceAuthenticationEnabled", "true");
    }
    if (config.getValue(Names.com_citrix_cpbm_public_catalog_display).equals("true")
        && channelService.getDefaultServiceProviderChannel() != null) {
      map.addAttribute("showAnonymousCatalogBrowsing", "true");
    }
    map.addAttribute("showLanguageSelection", "true");
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("selected_language", request.getParameter("lang"));
    String redirect = null;
    boolean loggedOut = request.getParameter(LOGOUT_PARAM) != null;
    final Throwable ex = (Throwable) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

    // capture previous CAPTCHA position
    Boolean captchaRequiredSessionObj = (Boolean) session.getAttribute(CaptchaAuthenticationFilter.CAPTCHA_REQUIRED);

    // Get last user
    String username = (String) session
        .getAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY);

    // this as spring does a text-escape when it saves this attribute
    final String uUsername = HtmlUtils.htmlUnescape(username);

    final Locale locale = getSessionLocale(request);

    if (loginFailed) {
      String error = " " + messageSource.getMessage("error.auth.username.password.invalid", null, locale);

      try {
        User user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

          @Override
          public User run() {
            User user = userService.getUserByParam("username", uUsername, false);

            // All user writes here.
            // Every time there is a login failure but not invalid CAPTCHA or unrecognised FQDN,
            // we update failed login attempts for the user
            if (!(ex instanceof CaptchaValidationException) && !(ex instanceof LockedException)
                && !(ex instanceof IpRangeValidationException) && !(ex instanceof FqdnNotFoundException)) {
              user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            }

            int attempts = user.getFailedLoginAttempts();

            // Also locking the root user and quite easily too. Clearly this
// needs an eye!
            if (attempts >= config.getIntValue(Names.com_citrix_cpbm_accountManagement_security_logins_lockThreshold)) {
              user.setEnabled(false);
            }

            return user;
          }
        });

        int attempts = user.getFailedLoginAttempts();
        if (attempts >= config.getIntValue(Names.com_citrix_cpbm_accountManagement_security_logins_captchaThreshold)) {
          session.setAttribute(CaptchaAuthenticationFilter.CAPTCHA_REQUIRED, true);
        }
      } catch (NoSuchUserException e) {
      }

      captchaRequiredSessionObj = (Boolean) session.getAttribute(CaptchaAuthenticationFilter.CAPTCHA_REQUIRED);

      map.addAttribute("loginFailed", loginFailed);
      String lastUsername = uUsername;

      if (config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed).equals("true")) {
        if (!lastUsername.equals("root") && !lastUsername.equals("")) {
          lastUsername = lastUsername.substring(0, lastUsername.lastIndexOf('@'));
        }
      }
      map.addAttribute("lastUser", lastUsername);

      // Compose error string
      if (ex instanceof DisabledException) {
        error = " " + messageSource.getMessage("error.auth.username.password.invalid", null, locale);
      } else if (ex instanceof CaptchaValidationException) {
        error = " " + messageSource.getMessage("error.auth.captcha.invalid", null, locale);
      } else if (ex instanceof FqdnNotFoundException) {
        error = " " + messageSource.getMessage("error.auth.username.password.invalid", null, locale);
      } else if (ex instanceof IpRangeValidationException) {
        error = " " + messageSource.getMessage("error.auth.username.password.invalid", null, locale);
      } else if (ex instanceof LockedException) {
        error = " " + messageSource.getMessage("error.auth.username.password.invalid", null, locale);
      } else if (ex instanceof BadCredentialsException) {
        if (ex.getMessage() != null && ex.getMessage().length() > 0) {
          error = " " + messageSource.getMessage("error.auth.username.password.invalid", null, locale);
        }
      } else if (ex instanceof AuthenticationException) {
        error = " " + messageSource.getMessage("error.auth.username.password.invalid", null, locale);
      } else {
        logger.error("Error occurred in authentication", ex);
        error = " " + messageSource.getMessage("error.auth.unknown", null, locale);
      }

      if (captchaRequiredSessionObj != null && captchaRequiredSessionObj && !(ex instanceof CaptchaValidationException)
          && !(ex instanceof LockedException)) {
        error += " " + messageSource.getMessage("error.auth.account.may.locked", null, locale);
      }
      map.addAttribute("error", error);
    }

    map.addAttribute("showSignupLink", config.getValue(Names.com_citrix_cpbm_portal_settings_allow_signup));
    PortalThemeResolver portalThemeResolver = getChannelThemeResolver(request);
    if (portalThemeResolver != null) {
      String channelFqdn = portalThemeResolver.getChannelFqdn(request);
      if (StringUtils.isNotBlank(channelFqdn)) {
        Channel channel = channelService.findByFqdnPrefix(channelFqdn);
        if (channel != null && !channel.getUuid().equals(channelService.getDefaultServiceProviderChannel().getUuid())) {
          map.addAttribute("showSignupLink", channel.getChannelBrandingConfigurations().getSignupAllowed());
        }
      }
    }

    if (loggedOut) {
      map.addAttribute("logout", loggedOut);
    }

    // This could come from session or from user
    if (captchaRequiredSessionObj != null && captchaRequiredSessionObj.booleanValue()
        && !Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_use_intranet_only))) {
      map.addAttribute("showCaptcha", true);
      map.addAttribute("recaptchaPublicKey", config.getRecaptchaPublicKey());
    }

    map.addAttribute(TIME_OUT, request.getParameter(TIME_OUT) != null);
    map.addAttribute(VERIFY, request.getParameter(VERIFY) != null);
    logger.debug("###Exiting login(req,map,session) method");

    if (config.getAuthenticationService().compareToIgnoreCase(CAS) == 0) {
      try {

        redirect = getCasRedirectUrl(request, config.getCasLoginUrl());
      } catch (UnsupportedEncodingException e) {
        logger.error("Exception encoding: " + redirect, e);
      }
      if (redirect == null) {
        throw new InternalError("CAS authentication required, but login url not set");
      }
    }
    return redirect == null ? "auth.login" : "redirect:" + redirect;
  }

  @RequestMapping(value = {
    "/exitpreviewmode"
  }, method = RequestMethod.GET)
  public void exitPreviewMode(HttpServletRequest request, ModelMap map, HttpSession session) {
    logger.debug("###Entering in exitPreviewMode(req, map, session) method");
    getChannelThemeResolver(request).setPreviewChannelCode(request, null);
  }

  @RequestMapping(value = {
      "/{userParam}/loggedout", "{userParam}/j_spring_security_logout"
  })
  public String loggedout(@PathVariable String userParam, ModelMap map, HttpSession session,
      HttpServletResponse response, HttpServletRequest request) {
    logger.debug("###Entering in loggedout(response) method");
    String showSuffixControl = "false";
    String suffixControlType = "textbox";
    List<String> suffixList = null;
    if (config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed).equals("true")) {
      showSuffixControl = "true";
      if (config.getValue(Names.com_citrix_cpbm_login_screen_tenant_suffix_dropdown_enabled).equals("true")) {
        suffixControlType = "dropdown";
        suffixList = tenantService.getSuffixList();
      }
    }
    map.addAttribute("showSuffixControl", showSuffixControl);
    map.addAttribute("suffixControlType", suffixControlType);
    map.addAttribute("suffixList", suffixList);
    if (config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled)
        && config.getValue(Names.com_citrix_cpbm_directory_mode).equals("pull")) {
      map.addAttribute("directoryServiceAuthenticationEnabled", "true");
    }
    if (config.getValue(Names.com_citrix_cpbm_public_catalog_display).equals("true")
        && channelService.getDefaultServiceProviderChannel() != null) {
      map.addAttribute("showAnonymousCatalogBrowsing", "true");
    }
    map.addAttribute("showLanguageSelection", "true");
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("logout", true);
    String redirect = null;
    Enumeration<String> en = session.getAttributeNames();
    while (en.hasMoreElements()) {
      String attr = en.nextElement();
      session.removeAttribute(attr);
    }
    Cookie cookie = new Cookie("JforumSSO", "");
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
    if (request.getRequestedSessionId() != null && request.isRequestedSessionIdValid()) {
      // create logout notification begins
      User user = userService.get(userParam);
      String message = "logged.out";
      String messageArgs = user.getUsername();
      eventService.createEvent(new Date(), user, message, messageArgs, Source.PORTAL, Scope.USER, Category.ACCOUNT,
          Severity.INFORMATION, true);
    }
    session.invalidate();
    if (config.getAuthenticationService().compareToIgnoreCase(CAS) == 0) {
      try {
        redirect = getCasRedirectUrl(request, config.getCasLogoutUrl());
      } catch (UnsupportedEncodingException e) {
        logger.error("Exception encoding: " + redirect, e);
      }
      if (redirect == null) {
        throw new InternalError("CAS authentication required, but login url not set");
      }
    }

    SecurityContextHolder.getContext().setAuthentication(null);
    // ends
    logger.debug("###Exiting loggedout(response) method");
    return redirect == null ? "redirect:/j_spring_security_logout" : "redirect:" + redirect;
  }

  @RequestMapping(value = "/reset_password", method = RequestMethod.GET)
  public String requestReset(ModelMap map) {
    String showSuffixControl = "false";
    if (config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed).equals("true")) {
      showSuffixControl = "true";
      map.addAttribute("useSmallCss", "true");
    }
    map.addAttribute("showSuffixControl", showSuffixControl);
    return "auth.request_reset";
  }

  /**
   * @param userName
   * @param pickValFromReset
   * @param request
   * @return
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  @RequestMapping(value = "/request_call_by_user", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> requestCall(@RequestParam(value = "userName", required = false) final String userName,
      @RequestParam(value = "pickValFromReset", required = false) final String pickValFromReset,
      HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException {
    Map<String, String> returnResponse = new HashMap<String, String>();
    String generatedPhoneVerificationPin = request.getSession().getAttribute("phoneVerificationPin").toString();
    final String userNameLoc;

    if (pickValFromReset != null && pickValFromReset.equals("pick")) {
      userNameLoc = (String) request.getSession().getAttribute(RESET_USER_KEY);
    } else if (userName != null) {
      userNameLoc = userName;
    } else {
      returnResponse.put("result", "failed");
      returnResponse.put("message",
          messageSource.getMessage("js.errors.register.callFailed", null, request.getLocale()));
      return returnResponse;
    }

    request.getSession().setAttribute("userName", userNameLoc);
    User user = null;
    try {
      user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

        @Override
        public User run() {
          return userService.getUserByParam("username", userNameLoc, false);
        }
      });
    } catch (NoSuchUserException e) {
    }

    if (user == null) {
      returnResponse.put("result", "failed");
      returnResponse.put("message",
          messageSource.getMessage("js.errors.register.callFailed", null, request.getLocale()));
      return returnResponse;
    }

    try {
      String refId = ((TelephoneVerificationService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION)).requestCall(user.getCountryCode(),
          user.getPhoneWithoutIsdCode(), generatedPhoneVerificationPin);
      if (refId != null) {
        returnResponse.put("result", "success");
        returnResponse.put("message",
            messageSource.getMessage("js.errors.register.callRequested", null, request.getLocale()));
      } else {
        returnResponse.put("result", "failed");
        returnResponse.put("message",
            messageSource.getMessage("js.errors.register.callFailed", null, request.getLocale()));
      }
    } catch (TelephoneVerificationServiceException e) {
      returnResponse.put("result", "failed");
      returnResponse.put("message",
          messageSource.getMessage("js.errors.register.callFailed", null, request.getLocale()));
    }

    return returnResponse;
  }

  /**
   * @param userName
   * @param pickValFromReset
   * @param request
   * @return
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  @RequestMapping(value = "/request_sms_by_user", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> requestSMS(@RequestParam(value = "userName", required = false) final String userName,
      @RequestParam(value = "pickValFromReset", required = false) final String pickValFromReset,
      HttpServletRequest request) throws JsonGenerationException, JsonMappingException, IOException {
    String generatedPhoneVerificationPin = request.getSession().getAttribute("phoneVerificationPin").toString();
    final String userNameLoc;
    Map<String, String> returnResponse = new HashMap<String, String>();

    if (pickValFromReset != null && pickValFromReset.equals("pick")) {
      userNameLoc = (String) request.getSession().getAttribute(RESET_USER_KEY);
    } else if (userName != null) {
      userNameLoc = userName;
    } else {
      returnResponse.put("result", "failed");
      returnResponse.put("message",
          messageSource.getMessage("js.errors.register.textMessageFailed", null, request.getLocale()));
      return returnResponse;
    }

    request.getSession().setAttribute("userName", userNameLoc);
    User user = null;
    try {
      user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

        @Override
        public User run() {
          return userService.getUserByParam("username", userNameLoc, false);
        }
      });
    } catch (NoSuchUserException e) {
    }

    if (user == null) {
      // Returning success for security
      returnResponse.put("result", "success");
// reason.
      returnResponse.put("message",
          messageSource.getMessage("js.errors.register.textMessageRequested", null, request.getLocale()));
      return returnResponse;
    }

    try {
      String refId = ((TelephoneVerificationService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION)).requestSMS(user.getCountryCode(),
          user.getPhoneWithoutIsdCode(), generatedPhoneVerificationPin);
      if (refId != null) {
        returnResponse.put("result", "success");
        returnResponse.put("message",
            messageSource.getMessage("js.errors.register.textMessageRequested", null, request.getLocale()));
      } else {
        returnResponse.put("result", "failed");
        returnResponse.put("message",
            messageSource.getMessage("js.errors.register.textMessageFailed", null, request.getLocale()));
      }
    } catch (TelephoneVerificationServiceException e) {
      returnResponse.put("result", "failed");
      returnResponse.put("message",
          messageSource.getMessage("js.errors.register.textMessageFailed", null, request.getLocale()));
    }

    return returnResponse;
  }

  @RequestMapping(value = "/phoneverification/verifyPINForUnlock", method = RequestMethod.GET)
  @ResponseBody
  public String verifyPhoneVerificationPINForUnlock(@RequestParam(value = "PIN", required = true) String pin,
      HttpServletRequest request) {

    String generatedPhoneVerificationPin = (String) request.getSession().getAttribute("phoneVerificationPin");

    if (pin != null && pin.equals(generatedPhoneVerificationPin)) {
      return "success";
    }
    return "failed";
  }

  @RequestMapping(value = "/reset_password", method = RequestMethod.POST, params = "username")
  public String requestReset(@RequestParam(value = "username", required = true) final String username,
      HttpServletRequest request, ModelMap map) {
    logger.debug("###Entering in requestReset(username,request) method @POST");
    String showSuffixControl = "false";
    if (config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed).equals("true")) {
      showSuffixControl = "true";
      map.addAttribute("useSmallCss", "true");
    }
    map.addAttribute("showSuffixControl", showSuffixControl);
    try {
      User user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

        @Override
        public User run() {
          return userService.getUserByParam("username", username, false);
        }
      });
      if (user.isEmailVerified()) {
        Channel usersChannel = user.getTenant().getSourceChannel();
        if (usersChannel != null) {
          Channel fqdnBasedChannel = validateChannelCodeAndFqdn(request, usersChannel.getCode(), false);
          if (usersChannel.equals(fqdnBasedChannel)) {
            userService.sendResetPasswordMail(user, "reset.password", user.getUsername());
          }
        } else {
          userService.sendResetPasswordMail(user, "reset.password", user.getUsername());
        }
      }
    } catch (NoSuchUserException ex) {
      // no indication to user that this failed.
      return "auth.request_reset_success";
    }
    logger.debug("###Exiting requestReset(username,request) method @POST");
    return "auth.request_reset_success";
  }

  @RequestMapping(value = "/reset_password", method = RequestMethod.GET, params = "a")
  public String reset(@RequestParam(value = "a", required = true) final String auth,
      @RequestParam(value = "t", required = true) final long ts,
      @RequestParam(value = "i", required = true) final String userParam, HttpSession session, ModelMap map) {
    logger.debug("###Entering in reset(auth,ts,userId,session) method @GET");

    Random rnd = new Random();
    int n = 99999 - 1000;
    session.setAttribute("phoneVerificationPin", "" + rnd.nextInt(n));

    User user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

      @Override
      public User run() {
        User user = userService.get(userParam);
        userService.verifyAuthorization(user, auth, ts);
        return user;
      }
    });

    // Whether telesign is enabled and the user is master user?
    boolean isTelesignVerificationEnable = false;
    if ((TelephoneVerificationService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION) != null) {
      isTelesignVerificationEnable = ((TelephoneVerificationService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION)).isEnabled()
          && user.equals(user.getTenant().getOwner());
    }

    map.addAttribute("isTelesignVerificationEnable", isTelesignVerificationEnable);
    session.setAttribute(RESET_USER_KEY, user.getUsername());
    logger.debug("###Exiting in reset(auth,ts,userId,session) method @GET");
    return "auth.reset";
  }

  @RequestMapping(value = "/reset_password", method = RequestMethod.POST, params = "password")
  public String reset(@RequestParam(value = "password", required = true) final String password, HttpSession session,
      HttpServletRequest request) {
    logger.debug("###Entering in reset(password,session) method @POST");
    final String username = (String) session.getAttribute(RESET_USER_KEY);
    final int maxFailCount = config.getIntValue(Names.com_citrix_cpbm_accountManagement_security_logins_lockThreshold);
    User user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

      @Override
      public User run() {
        User user = userService.getUserByParam("username", username, false);
        return user;
      }
    });
    Channel usersChannel = user.getTenant().getSourceChannel();
    if (usersChannel != null) {
      Channel fqdnBasedChannel = validateChannelCodeAndFqdn(request, usersChannel.getCode(), false);
      if (usersChannel.equals(fqdnBasedChannel)) {
        resetUserPassword(password, session, maxFailCount, user);
      }
    } else {
      resetUserPassword(password, session, maxFailCount, user);
    }
    session.removeAttribute(RESET_USER_KEY);
    logger.debug("###Exiting in reset(password,session) method @POST");
    return "redirect:/portal/login";
  }

  @RequestMapping(value = "/verify_additional_email", method = RequestMethod.GET)
  public String verifyAdditionalEmail(@RequestParam(value = "a", required = true) final String auth,
      @RequestParam(value = "i", required = true) final String userParam,
      @RequestParam(value = "pi", required = true) final String cryptedEmail, final HttpServletRequest request,
      ModelMap map, HttpSession session) {
    logger.debug("###Entering in verifyAlertEmail(map) method @GET");
    User user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

      @Override
      public User run() {
        User user = userService.get(userParam);
        return user;
      }
    });

    Channel usersChannel = user.getTenant().getSourceChannel();
    if (usersChannel != null) {
      Channel fqdnBasedChannel = validateChannelCodeAndFqdn(request, usersChannel.getCode(), false);
      if (usersChannel.equals(fqdnBasedChannel)) {
        verifyAdditionalEmail(auth, cryptedEmail, user);
      }
    } else {
      verifyAdditionalEmail(auth, cryptedEmail, user);
    }
    // No need for map to propagate
    map.clear();
    session.setAttribute("email_verified", "Y");
    logger.debug("##Exiting verifyAlertEmail(map) method @GET");
    return "redirect:/portal/home";
  }

  @RequestMapping(value = "getGoogleAnalytics", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, String> getGoogleAnalytics() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("enabled", config.getValue(Names.com_citrix_cpbm_portal_integrations_analytics));
    map.put("account", config.getValue(Names.integration_analytics_google_analytics_account));
    map.put("domain", config.getValue(Names.integration_analytics_google_analytics_domain));
    return map;
  }

  private String getCasRedirectUrl(HttpServletRequest request, String casTargetUrl) throws UnsupportedEncodingException {
    ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
    String channelFqdn = null;
    String deletedFqdn = null;
    String casServiceUrl = config.getCasServiceUrl();
    casServiceUrl = StringUtils.replace(casServiceUrl, "*", "");
    String casFinalUrl = casTargetUrl;
    String previewChannelCode = null;
    if (themeResolver != null) {
      PortalThemeResolver portalThemeResolver = (PortalThemeResolver) themeResolver;
      channelFqdn = portalThemeResolver.getChannelFqdn(request);
      previewChannelCode = portalThemeResolver.getPreviewChannelCode(request);
      deletedFqdn = portalThemeResolver.getDeletedChannelFqdn(request);
      if (StringUtils.isNotBlank(casTargetUrl) && StringUtils.isNotBlank(channelFqdn)) {
        String publicHost = config.getPublicHost();
        int index = casTargetUrl.indexOf(publicHost);
        if (index > 0 && StringUtils.isBlank(previewChannelCode)) {
          channelFqdn = channelFqdn + "." + publicHost;
          casFinalUrl = StringUtils.replace(casTargetUrl, publicHost, channelFqdn);
        }
        index = casServiceUrl.indexOf(publicHost);
        if (index > 0 && StringUtils.isBlank(previewChannelCode)) {
          casServiceUrl = StringUtils.replace(casServiceUrl, publicHost, channelFqdn);
        }
      } else if (StringUtils.isNotBlank(casTargetUrl) && StringUtils.isNotBlank(deletedFqdn)) {
        String publicHost = config.getPublicHost();
        int index = casTargetUrl.indexOf(publicHost);
        if (index > 0 && StringUtils.isBlank(previewChannelCode)) {
          deletedFqdn = deletedFqdn + "." + publicHost;
          casFinalUrl = StringUtils.replace(casTargetUrl, publicHost, deletedFqdn);
        }
        index = casServiceUrl.indexOf(publicHost);
        if (index > 0 && StringUtils.isBlank(previewChannelCode)) {
          casServiceUrl = StringUtils.replace(casServiceUrl, publicHost, deletedFqdn);
        }
      }
    }

    if (StringUtils.isNotBlank(casFinalUrl)) {
      if (StringUtils.isNotBlank(previewChannelCode)) {
        casFinalUrl = casFinalUrl + "?previewchannelcode=" + previewChannelCode + "&" + "service="
            + URLEncoder.encode(casServiceUrl, "UTF-8");
      } else {
        casFinalUrl = casFinalUrl + "?service=" + URLEncoder.encode(casServiceUrl, "UTF-8");
      }
    }
    String lang = null;
    if (WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME) != null) {
      lang = ((Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME))
          .getLanguage();
      casFinalUrl = casFinalUrl + "&lang=" + lang;
      logger.debug("language :- " + lang);
    }

    /*
     * String redirect = StringUtils.isBlank(casFinalUrl) ? null : casFinalUrl + "service=" +
     * URLEncoder.encode(casServiceUrl, "UTF-8");
     */
    return casFinalUrl;
  }

  private void resetUserPassword(final String password, HttpSession session, final int maxFailCount, User user) {
    boolean isSystemRoot = userService.getSystemUser(Handle.ROOT).getUuid().equals(user.getUuid());
    if (isSystemRoot || !config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled)) {
      user.setClearPassword(password);
    } else if (config.getValue(Names.com_citrix_cpbm_directory_mode).equals("push")) {
      userService.updateUserPassword(password, user.getUuid());
    }
    if (!user.isEnabled() || user.getFailedLoginAttempts() >= maxFailCount) {
      user.setFailedLoginAttempts(0);
      user.setEnabled(true);
    }
    if (user.getFailedLoginAttempts() >= config
        .getIntValue(Names.com_citrix_cpbm_accountManagement_security_logins_captchaThreshold)) {
      session.setAttribute(CaptchaAuthenticationFilter.CAPTCHA_REQUIRED, true);
    }
  }

  private void verifyAdditionalEmail(final String auth, final String cryptedEmail, final User user) {

    privilegeService.runAsPortal(new PrivilegedAction<Void>() {

      @Override
      public Void run() {
        userService.verifyAuthorization(user, auth, 0);
        String emailAdd = CryptoUtils.decrypt(cryptedEmail, CryptoUtils.keyGenerationSeed);
        UserAlertPreferences userAlertPreferences = userAlertPreferencesService.locateUserAlertPreference(user,
            emailAdd);

        if (userAlertPreferences.getAlertType() == AlertType.USER_EMAIL) {
          user.setEmail(userAlertPreferences.getEmailAddress());
          userService.save(user);
        } else {
          userAlertPreferences.setEmailVerified(true);
          userAlertPreferencesService.save(userAlertPreferences);
        }
        return null;
      }
    });

  }
}