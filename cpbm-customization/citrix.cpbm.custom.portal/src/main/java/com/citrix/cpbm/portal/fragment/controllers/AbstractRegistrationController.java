/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.tanesha.recaptcha.ReCaptcha;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.admin.service.exceptions.ConnectorManagementServiceException;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.citrix.cpbm.portal.forms.UserRegistration;
import com.vmops.event.DeviceFraudDetectionEvent;
import com.vmops.event.PortalEvent;
import com.vmops.internal.service.DeviceFraudDetectionService;
import com.vmops.internal.service.DeviceFraudDetectionService.ReviewStatus;
import com.vmops.internal.service.EventService;
import com.vmops.internal.service.PrivilegeService;
import com.vmops.internal.service.TelephoneTypeVerificationService;
import com.vmops.internal.service.TelephoneVerificationService;
import com.vmops.model.AccountType;
import com.vmops.model.Address;
import com.vmops.model.CampaignPromotion;
import com.vmops.model.Channel;
import com.vmops.model.Country;
import com.vmops.model.CurrencyValue;
import com.vmops.model.DeviceFraudDetectionAudit;
import com.vmops.model.Event;
import com.vmops.model.Event.Category;
import com.vmops.model.Event.Scope;
import com.vmops.model.Event.Severity;
import com.vmops.model.Event.Source;
import com.vmops.model.IPtoCountry;
import com.vmops.model.ProductBundle;
import com.vmops.model.ProductBundleRevision;
import com.vmops.model.PromotionToken;
import com.vmops.model.RateCardCharge;
import com.vmops.model.ServiceResourceType;
import com.vmops.model.ServiceResourceType.ResourceConstraint;
import com.vmops.model.ServiceResourceTypeGeneratedUsage;
import com.vmops.model.Tenant;
import com.vmops.model.Tenant.State;
import com.vmops.model.TrialAccount;
import com.vmops.model.User;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.CountryService;
import com.vmops.service.CurrencyValueService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.PromotionService;
import com.vmops.service.RegistrationService;
import com.vmops.service.TenantService;
import com.vmops.service.UserService;
import com.vmops.service.exceptions.IPtoCountryException;
import com.vmops.service.exceptions.NoSuchUserException;
import com.vmops.service.exceptions.TelephoneVerificationServiceException;
import com.vmops.service.exceptions.TrialCodeInvalidException;
import com.vmops.service.exceptions.TrialMaxAccountReachedException;
import com.vmops.service.exceptions.UserAuthorizationInvalidException;
import com.vmops.web.controllers.AbstractBaseController;
import com.vmops.web.controllers.menu.Page;

/**
 * Controller that handles all user pages.
 * 
 * @author vijay
 */

public abstract class AbstractRegistrationController extends AbstractBaseController {

// CHECKSTYLE:OFF
  @Autowired
  protected UserService userService;

  @Autowired
  protected RegistrationService registrationService;

  @Autowired
  protected PrivilegeService privilegeService;

  @Autowired
  protected PromotionService promotionService;

  @Autowired
  protected Configuration config;

  @Autowired
  protected ChannelService channelService;

  @Autowired
  protected CountryService countryService;

  @Autowired
  protected TenantService tenantService;

// CHECKSTYLE:ON
  private final Logger logger = Logger
      .getLogger(com.citrix.cpbm.portal.fragment.controllers.AbstractRegistrationController.class);

// CHECKSTYLE:OFF
  @Autowired
  protected ConnectorManagementService connectorManagementService;

  @Autowired
  protected ProductBundleService productBundleService;

  @Autowired
  protected CurrencyValueService currencyValueService;

  @Autowired
  protected EventService eventService;

  @Autowired
  protected ConnectorConfigurationManager connectorConfigurationManager;

// CHECKSTYLE:ON
  /**
   * Returns the ReCaptcha service configured. Also sets this as model attribute "captcha".
   * 
   * @return the captchaService
   */
  @ModelAttribute("captcha")
  public final ReCaptcha getCaptchaService() {
    return captchaService;
  }

  @InitBinder
  public void initBinder(WebDataBinder dataBinder) {
    dataBinder.setDisallowedFields(new String[] {
      "id"
    });
  }

  /**
   * Registering a Trial User
   * 
   * @param campaign
   * @param channelName
   * @param model
   * @return String
   */
  @RequestMapping(value = "/trial", method = RequestMethod.GET)
  public String trialRegister(@RequestParam("campaign") final String campaign,
      @RequestParam(value = "channelName", required = false) String channelName, ModelMap model) {
    logger.debug("###Entering in trialRegister(model) method @GET");
    model.addAttribute("title", "page.order_now");

    CampaignPromotion campaignPromotion = null;
    try {
      campaignPromotion = promotionService.locateCampaignByCode(campaign);

    } catch (NoSuchUserException e) {
      logger.error(e.getMessage());
      return "trial.invalid";
    }
    PromotionToken token = promotionService.generatePromotionalToken(campaignPromotion);
    if (token == null) {
      logger.error("Tokens not available");
      return "trial.invalid";
    }
    UserRegistration registration = createDefaultRegistration();
    registration.setDisposition(null);
    registration.getTenant().setAccountType(null);
    registration.setTrialCode(token.getCode());
    model.addAttribute("registration", registration);
    model.addAttribute("tenant", registration.getTenant());
    // TODO: for now, we will just hardcoded the channel name? in the form
    // as a hidden param in the form, so we know which channel to create the tenant in.
    Channel channel;
    if (channelName == null) {
      channel = channelService.getDefaultServiceProviderChannel();
    } else {
      channel = registrationService.getChannelByName(channelName);
    }
    model.addAttribute("channelParam", channel.getParam());

    String homeUrl = channelService.getValue(Names.com_citrix_cpbm_portal_marketing_home_url, channel.getParam());
    if (homeUrl != null) {
      model.addAttribute("homeUrl", homeUrl);
    }

    logger.debug("###Exiting trialRegister(model) method @GET");
    return "register.userinfo";
  }

  /**
   * SignUp of Step 1
   * 
   * @param model
   * @param request
   * @return String
   */
  @RequestMapping(value = "/account_type", method = RequestMethod.GET)
  public String signupStep1(ModelMap model, final HttpServletRequest request) {
    logger.debug("###Entering in account types selection(model) method @GET");
    Channel channel = channelService.getDefaultServiceProviderChannel();
    IPtoCountry iPtoCountry = super.getGeoIpToCountry(request, channel.getParam());
    if (iPtoCountry.isBlackListed()) {
      logger.debug("Error in validation because of blacklisted country " + "Country Code :"
          + iPtoCountry.getCountryCode());
      throw new IPtoCountryException(messageSource.getMessage("message.country.notsupported.error", null,
          request.getLocale()));
    }
    model.addAttribute("page", Page.HOME);
    model.addAttribute(Page.HOME.getLevel1().name(), "on");
    model.addAttribute("title", "page.order_now");

    UserRegistration registration = createDefaultRegistration();
    List<AccountType> accountTypes = registrationService.getSelfRegistrationAccountTypes();
    if (accountTypes != null && accountTypes.size() > 0) {
      registration.setSelfRegistrationAccountTypes(accountTypes);
    } else {
      model.addAttribute("signupwarningmessage",
          messageSource.getMessage("ui.signup.warning.message", null, request.getLocale()));
    }

    registration.getTenant().setSourceChannel(channel);
    TelephoneVerificationService telephoneVerificationService = (TelephoneVerificationService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION);
    boolean isEnabled = telephoneVerificationService != null && telephoneVerificationService.isEnabled();
    registration.setPhoneVerificationEnabled(isEnabled);
    model.addAttribute("registration", registration);
    model.addAttribute("tenant", registration.getTenant());
    model.addAttribute("channelParam", channel.getParam());

    String homeUrl = channelService.getValue(Names.com_citrix_cpbm_portal_marketing_home_url, channel.getParam());
    if (homeUrl != null) {
      model.addAttribute("homeUrl", homeUrl);
    }

    logger.debug("###Exiting register(model) method @GET");

    return "register.account_type";
  }

  /**
   * For the Signup.
   * 
   * @param map
   * @param globalConfiguration
   * @param promoCode
   * @param channelCode
   * @param sessionStatus
   * @param request
   * @param registration
   * @param result
   * @param channelParam
   * @return String
   * @throws IPtoCountryException
   */
  @RequestMapping(value = "/signup", method = RequestMethod.GET)
  public String signup(final ModelMap map,
      @ModelAttribute("globalConfiguration") Map<String, Object> globalConfiguration,
      @RequestParam(value = "promocode", required = false) final String promoCode,
      @RequestParam(value = "channelcode", required = false) final String channelCode,
      @RequestParam(value = "accountTypeId", required = true) String accountTypeId, SessionStatus sessionStatus,
      HttpServletRequest request) throws IPtoCountryException {
    logger.debug("###Entering in signup(model) method @GET");

    Channel channel = validateChannelCodeAndFqdn(request, channelCode, true);
    if (channel == null) {
      return "errors/notfound";
    }
    String channelParam = channel.getUuid();
    boolean channelSignupAllowed = Boolean.valueOf(channelService.getValue(
        Names.com_citrix_cpbm_portal_settings_allow_signup, channelParam));
    boolean globalSignupAllowed = Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_portal_settings_allow_signup));
    Channel defaultChannel = channelService.getDefaultServiceProviderChannel();

    if ((!channelSignupAllowed && !channel.equals(defaultChannel))
        || (channel.equals(defaultChannel) && !globalSignupAllowed)) {
      return "register.fail";
    }
    map.addAttribute("channelParam", channelParam);
    AccountType accountType = null;
    try {
      accountType = registrationService.getAccountTypeById(accountTypeId);
    } catch (HibernateObjectRetrievalFailureException e) {
      throw new IllegalArgumentException("Accounttype with id " + accountTypeId + "does not exist.");
    }

    List<AccountType> accountTypes = registrationService.getSelfRegistrationAccountTypes();
    if (accountTypes == null || accountTypes.size() == 0 || !(accountTypes.contains(accountType))) {
      map.addAttribute("signupwarningmessage",
          messageSource.getMessage("ui.signup.warning.message", null, request.getLocale()));
    }

    UserRegistration registration = createDefaultRegistration();
    channel = channelService.getDefaultServiceProviderChannel();
    registration.getTenant().setSourceChannel(channel);
    registration.setAccountTypeId(accountTypeId);
    TelephoneVerificationService telephoneVerificationService = (TelephoneVerificationService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION);
    boolean isEnabled = telephoneVerificationService != null && telephoneVerificationService.isEnabled();
    registration.setPhoneVerificationEnabled(isEnabled);

    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));

    return showSignup(registration, map, request, accountType, promoCode, channelCode, globalConfiguration);
  }

  /**
   * This method Shows Signup.
   * 
   * @param registration
   * @param map
   * @param channelParam
   * @param request
   * @param accountType
   * @param promoCode
   * @param channelCode
   * @return String
   */
  private String showSignup(final UserRegistration registration, final ModelMap map, HttpServletRequest request,
      AccountType accountType, String promoCode, String channelCode, Map<String, Object> globalConfiguration) {
    map.addAttribute("page", Page.HOME);
    map.addAttribute(Page.HOME.getLevel1().name(), "on");

    // compatibility for channel and promotion would happen at register.post
    // if promotion code is passed, we select a appropriate channel to pass
    // but do not fail here in case we do not find such a channel.
    Channel channel = null;
    String channelParam = null;
    if (StringUtils.isNotBlank(promoCode) && StringUtils.isNotBlank(channelCode)) {
      channel = channelService.locateByChannelCode(channelCode);

    } else if (StringUtils.isNotBlank(promoCode)) {
      channel = promotionService.findAptChannel(promoCode);
    } else if (StringUtils.isNotBlank(channelCode) && !accountType.isTrial()) {
      channel = channelService.locateByChannelCode(channelCode);
    }
    if (channel == null && globalConfiguration != null && globalConfiguration.get("channelid") != null) {
      channel = channelService.getChannelById(globalConfiguration.get("channelid").toString());
    }
    if (channel != null) {
      channelParam = channel.getParam();
      map.addAttribute("defaultLocale", channelService.getDefaultLocale(channel));
    } else {
      map.addAttribute("defaultLocale", channelService.getDefaultLocale(null));
    }

    Locale currentLocale = (Locale) WebUtils.getSessionAttribute(request,
        SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
    map.addAttribute("currentLocale", currentLocale);
    registration.getTenant().setAccountType(accountType);
    registration.setCurrencyValueList(channelService.listCurrencies(channelParam));
    map.addAttribute("registration", registration);
    map.addAttribute("tenant", registration.getTenant());
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));

    addFraudProfilingHostToSession(map);

    map.addAttribute("promoCode", promoCode);

    boolean trialAcountSelected = registrationService.getTrialAccountType().equals(accountType);
    map.addAttribute("trialAcountSelected", trialAcountSelected);
    map.addAttribute("isTrialSignup", trialAcountSelected);
    String homeUrl = channelService.getValue(Names.com_citrix_cpbm_portal_marketing_home_url, channelParam);
    if (homeUrl != null) {
      map.addAttribute("homeUrl", homeUrl);
    }

    return "register.userinfo";
  }

  /**
   * @author amitpals: Call Flow- User Clicks on the link Emailed to him post registration
   * @param model
   * @param request
   * @return :- Jsp Prompting user to enter password
   */
  // verifyUser mapping is to have upgrade compatibility for email verifyUser
  @RequestMapping(value = {
      "/verify_user", "/verifyUser"
  }, method = RequestMethod.GET)
  public String updatePasswordAndVerifyEmail(@RequestParam(value = "a", required = true) final String auth,
      @RequestParam(value = "i", required = true) final String userParam, final HttpServletRequest request,
      ModelMap model, HttpSession session) {
    logger.debug("###Entering in checkPassword(auth,userId,request) method @GET");
    String redirect = null;
    redirect = "/portal/login";
    User user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

      @Override
      public User run() {
        User user = userService.get(userParam);
        Locale locale = user.getLocale();
        if (locale != null) {
          WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        }

        return user;
      }

    });
    Channel usersChannel = user.getTenant().getSourceChannel();
    if (usersChannel != null) {
      Channel fqdnBasedChannel = validateChannelCodeAndFqdn(request, usersChannel.getCode(), false);
      if (!usersChannel.equals(fqdnBasedChannel)) {
        return "redirect:" + redirect;
      }
    }
    // needed in verifyemail
    session.setAttribute("regAuth", auth);
    session.setAttribute("regParam", userParam);

    if (user.getPassword() == null
        && (!config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled) || config
            .getValue(Names.com_citrix_cpbm_directory_mode).equals("push"))) {
      return "register.setpassword";
    }
    if (user.isEmailVerified()) {
      return "redirect:" + redirect;
    } else {
      return "redirect:" + "/portal/verify_email";
    }

  }

  /**
   * For setting up the password
   * 
   * @param password
   * @param session
   * @return String
   */
  @RequestMapping(value = "/setpassword", method = RequestMethod.POST)
  public String setPassword(@RequestParam(value = "password", required = true) final String password,
      HttpSession session) {
    {
      logger
          .debug("###Entering setPassword (@RequestParam(value = password, required = true) final String password, HttpSession session,HttpServletRequest request) ");
      StringBuffer redirect = new StringBuffer();
      redirect.append("redirect:/portal/verify_email");
      final String userParam = (String) session.getAttribute("regParam");

      User user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

        @Override
        public User run() {
          User user = userService.get(userParam);
          if (!config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled)) {
            user.setClearPassword(password);
          } else if (config.getValue(Names.com_citrix_cpbm_directory_mode).equals("push")) {
            user.setClearLdapPassword(password);
          }
          return user;
        }
      });
      userService.update(user, false);
      logger.debug(" AbstractRegistration  After Updating the user in DB password " + user.getPassword());
      return redirect.toString();
    }

  }

  /**
   * Get method for Back.
   * 
   * @param registration
   * @param model
   * @return String
   */
  @RequestMapping(value = "/back", method = RequestMethod.GET)
  public String back(@ModelAttribute("registration") final UserRegistration registration, ModelMap model) {
    logger.debug("###Entering in back(model) method @GET");
    model.addAttribute("page", Page.HOME);
    model.addAttribute(Page.HOME.getLevel1().name(), "on");
    model.addAttribute("title", "page.order_now");
    model.addAttribute("registration", registration);
    model.addAttribute("tenant", registration.getTenant());
    String homeUrl = config.getValue(Names.com_citrix_cpbm_portal_marketing_home_url);
    if (homeUrl != null) {
      model.addAttribute("homeUrl", homeUrl);
    }
    logger.debug("###Exiting back(model) method @GET");
    return "register.userinfo";
  }

  /**
   * Method to get user info.
   * 
   * @param registration
   * @param result
   * @param map
   * @param channelParam
   * @param sessionStatus
   * @param request
   * @return String
   * @throws IPtoCountryException
   */
  @RequestMapping(value = "/user_info", method = RequestMethod.POST)
  public String userInfo(@Valid @ModelAttribute("registration") final UserRegistration registration,
      BindingResult result, final ModelMap map, @ModelAttribute("channelParam") final String channelParam,
      SessionStatus sessionStatus, HttpServletRequest request) throws IPtoCountryException {
    logger.debug("###Entering in userInfo(model) method @POST");
    String email = registration.getUser().getEmail();
    registration.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));

    if (isEmailBlacklisted(email.toLowerCase(), channelParam)) {
      map.addAttribute("signuperror", "emaildomainblacklisted");
      map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
      map.addAttribute("defaultLocale", channelService.getDefaultLocale(registration.getTenant().getSourceChannel()));
      addFraudProfilingHostToSession(map);
      return "register.userinfo";
    }
    beanValidatorService.validate(registration.getUser(), result);
    if (result.hasErrors()) {
      return "register.userinfo";
    }
    IPtoCountry iPtoCountry = super.getGeoIpToCountry(request, channelParam);
    map.addAttribute("page", Page.HOME);
    map.addAttribute(Page.HOME.getLevel1().name(), "on");

    map.addAttribute("registration", registration);
    map.addAttribute("tenant", registration.getTenant());
    String homeUrl = channelService.getValue(Names.com_citrix_cpbm_portal_marketing_home_url, channelParam);
    if (homeUrl != null) {
      map.addAttribute("homeUrl", homeUrl);
    }

    map.addAttribute("tnc", getTermsAndConditions());
    if (!Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_use_intranet_only))) {
      map.addAttribute("showCaptcha", true);
      map.addAttribute("recaptchaPublicKey", config.getRecaptchaPublicKey());
    } else {
      logger.debug("Not displaying captcha because intranet only mode is enabled");
    }
    List<Country> filteredCountryList = getFilteredCountryList(registration.getCountryList(), channelParam);
    map.addAttribute("filteredCountryList", filteredCountryList);
    map.addAttribute("ipToCountryCode", iPtoCountry.getCountryCode());
    map.addAttribute("allowSecondaryCheckBox", registration.getTenant().getAccountType().isEnableSecondaryAddress());
    logger.debug("###Exiting userInfo(model) method @POST");
    return "register.moreuserinfo";
  }

  /**
   * Method for Phone Verification
   * 
   * @param registration
   * @param result
   * @param captchaChallenge
   * @param captchaResponse
   * @param map
   * @param channelParam
   * @param sessionStatus
   * @param request
   * @return String
   */
  @RequestMapping(value = "/phone_verification", method = RequestMethod.POST)
  public String phoneverification(@Valid @ModelAttribute("registration") final UserRegistration registration,
      BindingResult result,
      @RequestParam(value = "recaptcha_challenge_field", required = false) String captchaChallenge,
      @RequestParam(value = "recaptcha_response_field", required = false) String captchaResponse, final ModelMap map,
      @ModelAttribute("channelParam") final String channelParam, SessionStatus sessionStatus, HttpServletRequest request) {

    if (!Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_use_intranet_only))) {
      try {
        verifyCaptcha(captchaChallenge, captchaResponse, getRemoteUserIp(request), captchaService);
      } catch (CaptchaFailureException ex) {
        IPtoCountry iPtoCountry = super.getGeoIpToCountry(request, channelParam);
        List<Country> filteredCountryList = getFilteredCountryList(registration.getCountryList(), channelParam);
        map.addAttribute("filteredCountryList", filteredCountryList);
        if (registration.getUser().getAddress().getCountry().length() > 0) {
          map.addAttribute("ipToCountryCode", registration.getUser().getAddress().getCountry());
        } else {
          map.addAttribute("ipToCountryCode", iPtoCountry.getCountryCode());
        }
        map.addAttribute("registrationError", "captcha.error");
        map.addAttribute("recaptchaPublicKey", config.getRecaptchaPublicKey());
        map.addAttribute("allowSecondaryCheckBox", registration.getTenant().getAccountType().isEnableSecondaryAddress());
        result.reject("errors.registration.captcha", null, null);
        map.addAttribute("showCaptcha", true);
        return "register.moreuserinfo";
      }
    } else {
      logger.debug("No captcha verification required because intranet only mode is enabled");
    }
    Country country = countryService.locateCountryByCode(registration.getUser().getAddress().getCountry());
    registration.setCountryName(country.getName());
    registration.setCountryCode(country.getIsdCode());
    Random rnd = new Random();
    int n = 99999 - 1000;
    request.getSession().setAttribute("phoneVerificationPin", "" + rnd.nextInt(n));
    map.addAttribute("registration", registration);
    return "register.phoneverification";
  }

  @RequestMapping(value = {
    "getSupportedLanguages"
  }, method = RequestMethod.GET)
  @ResponseBody
  public Map<Locale, String> listAnonymousProductBundles() {
    return this.getLocaleDisplayName(listSupportedLocales());
  }

  // This method should be used from external systems to set portal language
  @RequestMapping(value = {
    "setLocale"
  }, method = RequestMethod.GET)
  public String setLocaleForAnonymous(@RequestParam(value = "lang", required = true) String lang) {
    return "blankview";
  }

  @RequestMapping(value = {
    "isShowAnnonymousCatalog"
  }, method = RequestMethod.GET)
  @ResponseBody
  public boolean isShowAnnonymousCatalog() {
    return config.getValue(Names.com_citrix_cpbm_public_catalog_display).equals("true")
        && channelService.getDefaultServiceProviderChannel() != null;
  }

  @RequestMapping(value = {
    "isSystemActive"
  }, method = RequestMethod.GET)
  @ResponseBody
  public boolean isSystemActive() {
    return channelService.getDefaultServiceProviderChannel() != null;
  }

  @RequestMapping(value = {
    "getLoginPageUIRelatedConfigs"
  }, method = RequestMethod.GET)
  @ResponseBody
  public Map<String, Object> getLoginPageUIRelatedConfigs() {
    HashMap<String, Object> loginUIConfigs = new HashMap<String, Object>();
    String isDirectoryServiceAuthenticationON = "false";
    if (config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled)
        && config.getValue(Names.com_citrix_cpbm_directory_mode).equals("pull")) {
      isDirectoryServiceAuthenticationON = "true";
    }
    loginUIConfigs.put("isDirectoryServiceAuthenticationON", isDirectoryServiceAuthenticationON);
    String showSuffix = config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed);
    loginUIConfigs.put("showSuffix", showSuffix);
    String showSuffixDropBox = config.getValue(Names.com_citrix_cpbm_login_screen_tenant_suffix_dropdown_enabled);
    loginUIConfigs.put("showSuffixDropBox", showSuffixDropBox);

    List<String> suffixList = new ArrayList<String>();
    if (showSuffix.equals("true") && showSuffixDropBox.equals("true")) {
      suffixList = tenantService.getSuffixList();
    }
    loginUIConfigs.put("suffixList", suffixList);
    return loginUIConfigs;
  }

  /**
   * This method returns list of Anonymous Product Bundles
   * 
   * @param channelParam
   * @param currencyCode
   * @param isServiceBundle
   * @param category
   * @return List<ProductBundleRevision>
   */
  @RequestMapping(value = {
    "listAnonymous.json"
  }, method = RequestMethod.GET)
  @ResponseBody
  public List<ProductBundleRevision> listAnonymousProductBundles(
      @RequestParam(value = "channelParam", required = true) String channelParam,
      @RequestParam(value = "currencyCode", required = true) String currencyCode,
      @RequestParam(value = "isServiceBundle", required = true) Boolean isServiceBundle,
      @RequestParam(value = "category", required = false) String category) {
    logger.debug("### listAnonymousProductBundles method starting...");
    List<ProductBundleRevision> productBundles = new ArrayList<ProductBundleRevision>();
    // Get Channel and then catalog.
    Channel channel = channelService.getChannel(channelParam);
    // Get currency.
    // return only those product bundles belonging to active catalogs
    productBundles = channelService.getCurrentChannelRevision(channel, false).getProductBundleRevisions();
    return getSortedProductBundles(productBundles, isServiceBundle, category, currencyCode);
  }

  /**
   * Gives the Sorted Product Bundles
   * 
   * @param productBundles
   * @param isServiceBundle
   * @param category
   * @param currencyCode
   * @return List<ProductBundleRevision>
   */
  private List<ProductBundleRevision> getSortedProductBundles(List<ProductBundleRevision> productBundles,
      Boolean isServiceBundle, String category, String currencyCode) {
    List<ProductBundleRevision> retBundles = new ArrayList<ProductBundleRevision>();
    for (ProductBundleRevision productBundle : productBundles) {
      if (ResourceConstraint.ACCOUNT.equals(productBundle.getProductBundle().getBusinessConstraint())
          || productBundle.getProductBundle().getRemoved() != null || !productBundle.getProductBundle().getPublish()) {
        continue;
      }
      if (isServiceBundle && productBundle.getProductBundle().getResourceType() != null) {
        continue;
      }
      if (!isServiceBundle && productBundle.getProductBundle().getResourceType() == null) {
        continue;
      }
      if (StringUtils.isNotBlank(category)
          && !category.equalsIgnoreCase(productBundle.getProductBundle().getServiceInstanceId().getService()
              .getCategory())) {
        continue;
      }

      retBundles.add(productBundle);
    }

    List<ProductBundleRevision> filteredBundleRevisions = new ArrayList<ProductBundleRevision>();
    for (ProductBundleRevision productBundleRevision : retBundles) {
      ProductBundleRevision productBundleRevisionForTenantCurrency = getProductBundleRevisionForTenantCurrency(
          currencyCode, productBundleRevision);
      filteredBundleRevisions.add(productBundleRevisionForTenantCurrency);
    }

    List<ProductBundleRevision> sortedProductBundles = new ArrayList<ProductBundleRevision>(filteredBundleRevisions);
    Collections.sort(sortedProductBundles, new Comparator<ProductBundleRevision>() {

      @Override
      public int compare(ProductBundleRevision o1, ProductBundleRevision o2) {
        return new Long(o1.getProductBundle().getSortOrder()).compareTo(new Long(o2.getProductBundle().getSortOrder()));
      }
    });
    logger.debug("### listProductBundles method ending...");
    return sortedProductBundles;
  }

  /**
   * This method gets Product Bundle Revision For TenantCurrency
   * 
   * @param currencyCode
   * @param productBundleRevision
   * @return ProductBundleRevision
   */
  private ProductBundleRevision getProductBundleRevisionForTenantCurrency(String currencyCode,
      ProductBundleRevision productBundleRevision) {
    // Doing this so that the cached reference is not touched.
    ProductBundleRevision productBundleRevisionForTenantCurrency = new ProductBundleRevision();
    productBundleRevisionForTenantCurrency.setEntitlements(productBundleRevision.getEntitlements());
    productBundleRevisionForTenantCurrency.setProductBundle(productBundleRevision.getProductBundle());
    productBundleRevisionForTenantCurrency.setProvisioningConstraints(productBundleRevision
        .getProvisioningConstraints());
    productBundleRevisionForTenantCurrency.setRevision(productBundleRevision.getRevision());
    List<RateCardCharge> rateCardCharges = new ArrayList<RateCardCharge>();
    for (RateCardCharge charge : productBundleRevision.getRateCardCharges()) {
      if (charge.getCurrencyValue().getCurrencyCode().equals(currencyCode)) {
        BigDecimal price = charge.getPrice().setScale(
            charge.getCurrencyValue().getCurrency().getDefaultFractionDigits(), RoundingMode.HALF_UP);
        charge.setPrice(price);
        rateCardCharges.add(charge);
      }
    }
    productBundleRevisionForTenantCurrency.setRateCardCharges(rateCardCharges);
    return productBundleRevisionForTenantCurrency;
  }

  /**
   * This method is used for Register.
   * 
   * @param registration
   * @param result
   * @param captchaChallenge
   * @param captchaResponse
   * @param map
   * @param channelParam
   * @param sessionStatus
   * @param request
   * @return String
   */
  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public String register(@Valid @ModelAttribute("registration") final UserRegistration registration,
      final BindingResult result,
      @RequestParam(value = "recaptcha_challenge_field", required = false) String captchaChallenge,
      @RequestParam(value = "recaptcha_response_field", required = false) String captchaResponse, final ModelMap map,
      @ModelAttribute("channelParam") final String channelParam, SessionStatus sessionStatus, HttpServletRequest request) {
    logger.debug("###Entering in register( method @POST");
    map.addAttribute("page", Page.HOME);
    map.addAttribute("registration", registration);
    addFraudProfilingHostToSession(map);
    IPtoCountry iPtoCountry = super.getGeoIpToCountry(request, channelParam);
    List<Country> filteredCountryList = getFilteredCountryList(registration.getCountryList(), channelParam);
    map.addAttribute("filteredCountryList", filteredCountryList);
    if (registration.getUser().getAddress().getCountry().length() > 0) {
      map.addAttribute("ipToCountryCode", registration.getUser().getAddress().getCountry());
    } else {
      map.addAttribute("ipToCountryCode", iPtoCountry.getCountryCode());
    }
    map.addAttribute(Page.HOME.getLevel1().name(), "on");
    map.addAttribute("allowSecondaryCheckBox", registration.getTenant().getAccountType().isEnableSecondaryAddress());
    if (!registration.getPhoneVerificationEnabled()
        && !Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_use_intranet_only))) {
      try {
        verifyCaptcha(captchaChallenge, captchaResponse, getRemoteUserIp(request), captchaService);
      } catch (CaptchaFailureException ex) {
        map.addAttribute("registrationError", "captcha.error");
        map.addAttribute("recaptchaPublicKey", config.getRecaptchaPublicKey());
        map.addAttribute("showCaptcha", true);
        result.reject("errors.registration.captcha", null, null);
        map.addAttribute("allowSecondaryCheckBox", registration.getTenant().getAccountType().isEnableSecondaryAddress());
        return "register.moreuserinfo";
      }
    }

    /* TODO: Add custom annotation in bean validation and move the sanitization there */
    // HTML sanitization of form fields.. please add fields to the list below

    Locale currentLocale = null;
    String currentLocaleString = null;

    if (map.get("currentLocale") != null) {
      currentLocaleString = map.get("currentLocale").toString();
    }

    if (StringUtils.isNotBlank(currentLocaleString)) {
      try {
        currentLocale = LocaleUtils.toLocale(currentLocaleString);
      } catch (Exception e) {
        logger.error("Invalid currentLocale in map", e);
      }
    }

    Address validationAddress = registration.getUser().getAddress();
    Address secondaryAddress = registration.getSecondaryAddress();

    com.citrix.cpbm.access.Tenant validationTenant = registration.getTenant();

    List<HtmlSanitizationField> fieldsToBeCheckedForSafety = new ArrayList<AbstractBaseController.HtmlSanitizationField>();

    fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("trialCode", registration.getTrialCode(),
        "label.field.deemed.unsafe", new Object[] {
          messageSource.getMessage("label.userInfo.promo.code", null, currentLocale)
        }));

    fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("state", validationAddress.getState(),
        "label.field.deemed.unsafe", new Object[] {
          messageSource.getMessage("label.moreUserInfo.state", null, currentLocale)
        }));

    fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("street1", validationAddress.getStreet1(),
        "label.field.deemed.unsafe", new Object[] {
          messageSource.getMessage("label.moreUserInfo.address1", null, currentLocale)
        }));

    fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("street2", validationAddress.getStreet2(),
        "label.field.deemed.unsafe", new Object[] {
          messageSource.getMessage("label.moreUserInfo.address2", null, currentLocale)
        }));

    fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("city", validationAddress.getCity(),
        "label.field.deemed.unsafe", new Object[] {
          messageSource.getMessage("label.moreUserInfo.city", null, currentLocale)
        }));

    fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("companyName", validationTenant.getName(),
        "label.field.deemed.unsafe", new Object[] {
          messageSource.getMessage("label.moreUserInfo.companyName", null, currentLocale)
        }));

    if (secondaryAddress != null) {

      fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("secondaryAddress.state", secondaryAddress.getState(),
          "label.field.deemed.unsafe", new Object[] {
            messageSource.getMessage("label.moreUserInfo.state", null, currentLocale)
          }));

      fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("secondaryAddress.street1", secondaryAddress
          .getStreet1(), "label.field.deemed.unsafe", new Object[] {
        messageSource.getMessage("label.moreUserInfo.address1", null, currentLocale)
      }));

      fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("secondaryAddress.street2", secondaryAddress
          .getStreet2(), "label.field.deemed.unsafe", new Object[] {
        messageSource.getMessage("label.moreUserInfo.address2", null, currentLocale)
      }));

      fieldsToBeCheckedForSafety.add(new HtmlSanitizationField("secondaryAddress.city", secondaryAddress.getCity(),
          "label.field.deemed.unsafe", new Object[] {
            messageSource.getMessage("label.moreUserInfo.city", null, currentLocale)
          }));

    }

    validateRegistrationIsHtmlSafe(fieldsToBeCheckedForSafety, result, currentLocale, false);

    if (result.hasErrors()) {
      displayErrors(result);
      parseResult(result, map);
      registration.setCurrency(config.getValue(Names.com_citrix_cpbm_portal_settings_default_currency));
      registration.setAllowSecondary(false);
      registration.setSecondaryAddress(new Address());
      logger.debug("###Exiting register(registration,result,captchaChallenge,,captchaResponse,"
          + "map,sessionStatus,request) method @POST");
      map.addAttribute("allowSecondaryCheckBox", registration.getTenant().getAccountType().isEnableSecondaryAddress());
      if (!Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_use_intranet_only))) {
        map.addAttribute("recaptchaPublicKey", config.getRecaptchaPublicKey());
        map.addAttribute("showCaptcha", true);
      }
      return "register.moreuserinfo";
    }

    registration.getUser().setPhone(registration.getUser().getPhone().replaceAll(PHONE_NUMBER_REGEX, ""));

    TelephoneVerificationService telephoneVerificationService = (TelephoneVerificationService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION);
    if (telephoneVerificationService != null && telephoneVerificationService.isEnabled()) {
      String generatedPhoneVerificationPin = request.getSession().getAttribute("phoneVerificationPin").toString();
      String actualPhoneNumber = request.getSession().getAttribute("phoneNumber").toString();
      if (!registration.getUserEnteredPhoneVerificationPin().equals(generatedPhoneVerificationPin)
          || !areDigitsInPhoneNosEqual(registration.getUser().getPhone(), actualPhoneNumber)) {
        map.addAttribute("registrationError", "phoneVerfication.error");
        result.reject("errors.registration.user.phone", null, null);
        return "register.phoneverification";
      }
    }
    if (result.hasErrors()) {
      displayErrors(result);
      parseResult(result, map);
      return "register.userinfo";
    }

    // Device intelligence and fraud detection
    ReviewStatus fraudStatus = null;
    DeviceFraudDetectionAudit log = null;
    DeviceFraudDetectionService deviceFraudDetectionService = (DeviceFraudDetectionService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.DEVICE_FRAUD_CONTROL);
    if (deviceFraudDetectionService != null && deviceFraudDetectionService.isEnabled()) {
      fraudStatus = assessAccountCreationRisk(registration, request);

      if (fraudStatus == ReviewStatus.FAIL) {
        return "register.fail";
      }

      log = ((DeviceFraudDetectionService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.DEVICE_FRAUD_CONTROL)).getLastLog(
          request.getSession().getId(), registration.getUser().getUsername());

      String message = "device.fraud";
      Tenant tenant = tenantService.getSystemTenant();
      String messageArguments = registration.getUser().getUsername();
      Event event = null;

      switch (fraudStatus) {
        case REJECT:
          context.publishEvent(new PortalEvent("Device Fraud Detection Event", actorService.getActor(),
              new DeviceFraudDetectionEvent(log, registration.getUser().getUsername(), registration.getUser()
                  .getEmail(), registration.getUser().getPhone(), registration.getUser().getFirstName(), registration
                  .getUser().getLastName())));
          event = new Event(new Date(), message, messageArguments, tenant, Source.PORTAL, Scope.ACCOUNT,
              Category.ACCOUNT, Severity.CRITICAL, true);
          eventService.createEvent(event, false);
          return "register.fail";

        case REVIEW:
          // account to be manual activated
          registration.getTenant().setIsManualActivation(true);

          context.publishEvent(new PortalEvent("Device Fraud Detection Event", actorService.getActor(),
              new DeviceFraudDetectionEvent(log, registration.getUser().getUsername(), registration.getUser()
                  .getEmail(), registration.getUser().getPhone(), registration.getUser().getFirstName(), registration
                  .getUser().getLastName())));
          event = new Event(new Date(), message, messageArguments, tenant, Source.PORTAL, Scope.ACCOUNT,
              Category.ACCOUNT, Severity.ALERT, true);
          eventService.createEvent(event, false);

          // Model should know fraud has been detected
          map.put("deviceFraudDetected", true);
        default:
          break;
      }

    }
    String channelUuid = channelParam;
    if (StringUtils.isBlank(channelUuid)) {
      channelUuid = channelService.getDefaultServiceProviderChannel().getUuid();
    }
    // This checks whether the trial code is valid or not
    map.addAttribute("supportEmail",
        channelService.getValue(Names.com_citrix_cpbm_portal_addressbook_helpDeskEmail, channelUuid));
    map.addAttribute("supportPhone",
        channelService.getValue(Names.com_citrix_cpbm_portal_settings_helpdesk_phone, channelUuid));

    // post processing for trial code
    if (!StringUtils.isEmpty(registration.getTrialCode())) {
      String promoCode = registration.getTrialCode();
      String channelCode = channelService.getChannel(channelParam).getCode();

      if (!promotionService.isValidPromotion(promoCode, channelCode)) {
        logger.debug("Invalid promo code " + promoCode + " for channel code " + channelCode);
        return "register.fail";
      }

      // preempt trial account type creation if NOT supported [TA10428]
      CampaignPromotion cp = promotionService.locatePromotionByToken(promoCode);
      AccountType requestedAccountType = registrationService.getAccountTypeById(registration.getAccountTypeId());

      if (requestedAccountType.equals(registrationService.getTrialAccountType()) && !cp.isTrial()) {
        logger.debug("Invalid promo code " + promoCode + " for account type " + requestedAccountType);

        return "register.fail";
      }
    }

    registration.getTenant().setAddress(registration.getUser().getAddress());
    if (!registration.isAllowSecondary()) {
      registration.getTenant().setSecondaryAddress(null);
    } else {
      registration.getTenant().setSecondaryAddress(registration.getSecondaryAddress());
    }
    registration.getTenant().setSyncBillingAddress(true);
    // to store error messages
    List<String> errorMsgList = new ArrayList<String>();
    try {

      final com.citrix.cpbm.access.User owner = registration.getUser();
      if (registration.getCountryCode() == null || registration.getCountryCode().equals("")) {
        Country country = countryService.locateCountryByCode(registration.getUser().getAddress().getCountry());
        registration.setCountryName(country.getName());
        registration.setCountryCode(country.getIsdCode());
      }
      String phoneNo = registration.getCountryCode().replaceAll(PHONE_NUMBER_REGEX, "")
          + COUNTRY_CODE_TO_PHONE_NUMBER_SEPERATOR + owner.getPhone().replaceAll(PHONE_NUMBER_REGEX, "");
      // Set the phone number
      owner.setPhone(phoneNo);

      owner.setLocale(registration.getUser().getLocale());
      registration.getTenant().setRemoteAddress(getRemoteUserIp(request));

      // set currency
      for (CurrencyValue cv : registration.getCurrencyValueList()) {
        if (cv.getCurrencyCode().equals(registration.getCurrency())) {
          registration.getTenant().setCurrency(cv);
          break;
        }
      }

      privilegeService.runAsPortal(new PrivilegedAction<Void>() {

        @Override
        public Void run() {
          AccountType requestedAccountType = registrationService.getAccountTypeById(registration.getAccountTypeId());
          if (requestedAccountType.equals(registrationService.getTrialAccountType())) {
            TrialAccount account = null;
            try {
              account = registrationService.registerTrialAccount(registration.getTrialCode(), registration.getTenant(),
                  owner, channelParam, result);
            } catch (TrialCodeInvalidException e) {
              logger.debug("Invalid Trial Code", e);
            } catch (ConnectorManagementServiceException e) {
              logger.debug("Cannot find service instance", e);
            }
            map.addAttribute("trial", account);
            return null;
          } else {
            try {
              registrationService.registerTenant(registration.getTenant(), owner, channelParam,
                  registration.getTrialCode(), result);
            } catch (ConnectorManagementServiceException e) {
              logger.debug("Cannot find service instance", e);
            }
          }
          if (!result.hasErrors()) {
            Tenant t = tenantService.get(registration.getTenant().getUuid());
            t.setAccountType(registrationService.getAccountTypeById(registration.getAccountTypeId()));
            t.getTenantExtraInformation().setPaymentMode(requestedAccountType.getSupportedPaymentModes().get(0));
            tenantService.save(t);
            registration.setTenant((com.citrix.cpbm.access.Tenant) CustomProxy.newInstance(t));
          }
          return null;

        }
      });
      if (deviceFraudDetectionService != null && deviceFraudDetectionService.isEnabled()) {
        log.setUserId(registration.getTenant().getOwner().getId());
      }

      map.addAttribute("tenant", registration.getTenant());
      String homeUrl = channelService.getValue(Names.com_citrix_cpbm_portal_marketing_home_url, channelParam);
      if (homeUrl != null) {
        map.addAttribute("homeUrl", homeUrl);
      }
    } catch (DataAccessException ex) {
      logger.error(ex);
      result.reject("errors.registration", new Object[] {
        ex.getMessage()
      }, null);
      errorMsgList.add("You must accept the terms and conditions to use this service");
    } catch (TrialCodeInvalidException ex) {
      result.reject("errors.tenant.registration");
      map.addAttribute("trialCode", "errors.tenant.registration");
      logger.debug("registrationError " + ex.getMessage());
    } catch (TrialMaxAccountReachedException ex) {
      result.reject("errors.tenant.registration");
      map.addAttribute("trialCode", "errors.tenant.registration");
      logger.debug("registrationError " + ex.getMessage());
    } catch (Exception ex) {
      logger.error("registrationError ", ex);
      return "redirect:/portal/errors/error";
    }
    if (result.hasErrors()) {
      displayErrors(result);
      parseResult(result, map);
      registration.reset();
      registration.setCurrency(config.getValue(Names.com_citrix_cpbm_portal_settings_default_currency));
      if (errorMsgList.size() > 0) {
        map.addAttribute("errorMsgList", errorMsgList);
        map.addAttribute("errormsg", true);
      }
      logger.debug("###Exiting register(registration,result,captchaChallenge,,captchaResponse,"
          + "map,sessionStatus,request) method @POST");
      map.addAttribute("allowSecondaryCheckBox", registration.getTenant().getAccountType().isEnableSecondaryAddress());
      if (!Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_use_intranet_only))) {
        map.addAttribute("recaptchaPublicKey", config.getRecaptchaPublicKey());
        map.addAttribute("showCaptcha", true);
      }
      return "register.moreuserinfo";
    } else {
      // clean up parameters in session.
      sessionStatus.setComplete();
      logger.debug("###Exiting register(registration,result,captchaChallenge,,captchaResponse,"
          + "map,sessionStatus,request) method @POST");
      map.addAttribute("user", registration.getUser());
      return "register.registration_success";
    }
  }

  /**
   * This method used for validation of Username
   * 
   * @param username
   * @return String
   */
  @RequestMapping(value = "/validate_username")
  @ResponseBody
  public String validateUsername(@RequestParam("user.username") final String username) {
    logger.debug("In validateUsername() method start and username is : " + username);
    // validate with portal users table
    try {
      privilegeService.runAsPortal(new PrivilegedAction<Void>() {

        @Override
        public Void run() {
          userService.getUserByParam("username", username, true);
          return null;
        }
      });
    } catch (NoSuchUserException ex) {
      logger.debug(username + ": not exits in users table");
      return Boolean.TRUE.toString();
    }
    logger.debug("In validateUsername() method end");
    return Boolean.FALSE.toString();
  }

  /**
   * For validation of Suffix.
   * 
   * @param suffix
   * @return String
   */
  @RequestMapping(value = "/validate_suffix")
  @ResponseBody
  public String validateSuffix(@RequestParam("tenant.usernameSuffix") final String suffix) {
    logger.debug("In validateSuffix() method and suffix is : " + suffix);
    List<Tenant> tList = tenantService.list(0, 0, null, null, false, null, null, null, null, suffix, null, null, null);
    boolean exists = true;
    if (tList.size() == 0) {
      exists = false;
    }
    return new Boolean(!exists).toString();
  }

  /**
   * For validation of Email.
   * 
   * @param email
   * @return String
   */
  @RequestMapping(value = "/validate_email_domain")
  @ResponseBody
  public String validateMailDomain(@RequestParam("user.email") final String email,
      @RequestParam("channelParam") final String channelparam) {
    logger.debug("In validateMailDomain() method and email is : " + email);
    return new Boolean(!isEmailBlacklisted(email, channelparam)).toString();
  }

  /**
   * @author amitpals
   * @param request
   * @param map
   * @param session
   * @return String
   */
  @RequestMapping(value = "/verify_email", method = RequestMethod.GET)
  public String verifyEmail(final HttpServletRequest request, ModelMap map, final HttpSession session) {
    logger.debug("###Entering in verifyEmail(Req,model,session) method @GET");
    String redirect = null;
    redirect = "/?verify";
    final String auth = (String) session.getAttribute("regAuth");
    final String userParam = (String) session.getAttribute("regParam");

    User user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

      @Override
      public User run() {
        try {
          return userService.get(userParam);
        } catch (NoSuchUserException e) {
          throw new UserAuthorizationInvalidException(e);

        }
      }
    });

    if (!config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled)) {
      if (user.getPassword() == null) {
        logger.debug("AbstractRegistrationCont userpassword is null returning to register.setpassword");
        return "register.setpassword";
      }
    }
    if (user.isEmailVerified()) {
      logger.debug("verifyEmail: email already verified forwarding to Login page");
      return "redirect:" + redirect;
    }
    logger.debug("verifyEmail: email not yet verified verifying...");
    user = privilegeService.runAsPortal(new PrivilegedAction<User>() {

      @Override
      public User run() {
        User user = userService.get(userParam);
        userService.verifyAuthorization(user.getId(), auth, 0);
        logger.debug("verifyEmail: user enabled " + user.isEnabled());

        if (!user.isEnabled() && user.getTenant().getState() != State.TERMINATED) {

          logger.debug("verifyEmail: Activating user...");
          userService.activate(user);
          try {
            String message = "email.verified";
            String messageArguments = user.getUsername();
            Event alert = new Event(new Date(), message, messageArguments, user, Source.PORTAL, Scope.USER,
                Category.ACCOUNT, Severity.INFORMATION, true);
            eventService.createEvent(alert, false);
          } catch (Exception e) {
            logger.debug(e);
          }
        }
        return user;
      }
    });
    logger.debug("###Exiting verifyEmail(Req,model,session) method @GET  redirecting to " + redirect);
    return "redirect:" + redirect;
  }

  /**
   * Private method to create Default Registration
   * 
   * @return UserRegistration
   */
  private UserRegistration createDefaultRegistration() {
    UserRegistration registration = new UserRegistration();
    registration.setCurrency(config.getValue(Names.com_citrix_cpbm_portal_settings_default_currency));
    return registration;
  }

  /**
   * Protected method to Parse Result.
   * 
   * @param result
   * @param map
   */
  protected void parseResult(BindingResult result, ModelMap map) {
    if (result.hasGlobalErrors()) {
      List<String> errorMsgList = new ArrayList<String>();
      for (ObjectError objectError : result.getGlobalErrors()) {
        errorMsgList.add(messageSource.getMessage(objectError.getCode(), objectError.getArguments(), null));
      }

      map.addAttribute("errorMsgList", errorMsgList);
      map.addAttribute("errormsg", true);
    }
  }

  /**
   * Private method to get Terms and Conditions.
   * 
   * @return
   */
  private String getTermsAndConditions() {
    try {
      File file = new File(config.getTncFileLocation());
      BufferedReader br = new BufferedReader(new FileReader(file));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
      }
      br.close();
      return sb.toString();
    } catch (Exception e) {
      logger.debug("Failed to read Terms and conditions file" + e.getMessage());
      return "";
    }
  }

  /**
   * Method for request Call
   * 
   * @param phoneNumber
   * @param countryCode
   * @param request
   * @return Map of String
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  @RequestMapping(value = "/request_call", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> requestCall(@RequestParam(value = "phoneNumber", required = true) String phoneNumber,
      @RequestParam(value = "countryCode", required = true) String countryCode, HttpServletRequest request)
      throws JsonGenerationException, JsonMappingException, IOException {
    Map<String, String> returnResponse = new HashMap<String, String>();
    Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
    try {
      Map<String, String> phoneTypeResp = ((TelephoneTypeVerificationService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION)).verifyPhoneType(countryCode, phoneNumber,
          locale);
      if (!Boolean.valueOf(phoneTypeResp.get("result").toString())) {
        returnResponse.put("result", "failed");
        returnResponse.put("message", phoneTypeResp.get("message"));
        return returnResponse;
      }
    } catch (TelephoneVerificationServiceException e) {
      returnResponse.put("result", "failed");
      returnResponse.put("message",
          messageSource.getMessage("js.errors.register.phone.type.validation.failure", null, locale));
      return returnResponse;
    }
    String generatedPhoneVerificationPin = request.getSession().getAttribute("phoneVerificationPin").toString();
    request.getSession().setAttribute("phoneNumber", phoneNumber);

    try {
      String refId = ((TelephoneVerificationService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION)).requestCall(countryCode, phoneNumber,
          generatedPhoneVerificationPin);
      if (refId != null) {
        returnResponse.put("result", "success");
        returnResponse.put("message", messageSource.getMessage("js.errors.register.callRequested", null, locale));
      } else {
        returnResponse.put("result", "failed");
        returnResponse.put("message", messageSource.getMessage("js.errors.register.callFailed", null, locale));
      }
    } catch (TelephoneVerificationServiceException e) {
      returnResponse.put("result", "failed");
      returnResponse.put("message", messageSource.getMessage("js.errors.register.callFailed", null, locale));
    }
    return returnResponse;
  }

  /**
   * Method for Request of SMS
   * 
   * @param phoneNumber
   * @param countryCode
   * @param request
   * @return Map of String
   * @throws JsonGenerationException
   * @throws JsonMappingException
   * @throws IOException
   */
  @RequestMapping(value = "/request_sms", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> requestSMS(@RequestParam(value = "phoneNumber", required = true) String phoneNumber,
      @RequestParam(value = "countryCode", required = true) String countryCode, HttpServletRequest request)
      throws JsonGenerationException, JsonMappingException, IOException {
    Map<String, String> returnResponse = new HashMap<String, String>();
    Locale locale = (Locale) WebUtils.getSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
    try {
      Map<String, String> phoneTypeResp = ((TelephoneTypeVerificationService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION)).verifyPhoneType(countryCode, phoneNumber,
          locale);
      if (!Boolean.valueOf(phoneTypeResp.get("result").toString())) {
        returnResponse.put("result", "failed");
        returnResponse.put("message", phoneTypeResp.get("message"));
        return returnResponse;
      }
    } catch (TelephoneVerificationServiceException e) {
      returnResponse.put("result", "failed");
      returnResponse.put("message",
          messageSource.getMessage("js.errors.register.phone.type.validation.failure", null, locale));
      return returnResponse;
    }
    String generatedPhoneVerificationPin = request.getSession().getAttribute("phoneVerificationPin").toString();
    request.getSession().setAttribute("phoneNumber", phoneNumber);

    try {
      String refId = ((TelephoneVerificationService) connectorManagementService
          .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION)).requestSMS(countryCode, phoneNumber,
          generatedPhoneVerificationPin);
      if (refId != null) {
        returnResponse.put("result", "success");
        returnResponse
            .put("message", messageSource.getMessage("js.errors.register.textMessageRequested", null, locale));
      } else {
        returnResponse.put("result", "failed");
        returnResponse.put("message", messageSource.getMessage("js.errors.register.textMessageFailed", null, locale));
      }
    } catch (TelephoneVerificationServiceException e) {
      returnResponse.put("result", "failed");
      returnResponse.put("message", messageSource.getMessage("js.errors.register.textMessageFailed", null, locale));
    }
    return returnResponse;
  }

  /**
   * Method for verification of Phone verification Pin.
   * 
   * @param pin
   * @param phoneNumber
   * @param request
   * @return String
   */
  @RequestMapping(value = "/phoneverification/verify_pin", method = RequestMethod.POST)
  @ResponseBody
  public String verifyPhoneVerificationPIN(@RequestParam(value = "PIN", required = true) String pin,
      @RequestParam(value = "phoneNumber", required = true) String phoneNumber, HttpServletRequest request) {
    String actualPhoneNumber = (String) request.getSession().getAttribute("phoneNumber");
    String generatedPhoneVerificationPin = (String) request.getSession().getAttribute("phoneVerificationPin");
    if (pin.equals(generatedPhoneVerificationPin) && areDigitsInPhoneNosEqual(actualPhoneNumber, phoneNumber)) {
      return "success";
    }
    return "failed";
  }

  protected boolean areDigitsInPhoneNosEqual(String phoneNo1, String phoneNo2) {
    if (phoneNo1.replaceAll("[^\\d]", "").equals(phoneNo2.replaceAll("[^\\d]", ""))) {
      return true;
    }
    return false;
  }

  /**
   * @param registration
   * @param request
   * @return ReviewStatus
   */
  private ReviewStatus assessAccountCreationRisk(final UserRegistration registration, HttpServletRequest request)
  /*
   * throws HttpException, IOException, UnsupportedEncodingException, InterruptedException, ExecutionException,
   * ServiceException
   */{
    Map<String, String> accountIdentifiers = new HashMap<String, String>();
    accountIdentifiers.put("service_type", "session-policy");
    accountIdentifiers.put("account_login", registration.getUser().getUsername());
    accountIdentifiers.put("account_email", registration.getUser().getEmail());
    accountIdentifiers.put("account_telephone", registration.getUser().getPhone());
    accountIdentifiers.put("account_name", registration.getUser().getFirstName() + " "
        + registration.getUser().getLastName());
    // Put address
    final Address address = registration.getBillingAddress();
    accountIdentifiers.put("account_address_street1", address.getStreet1());
    accountIdentifiers.put("account_address_street2", address.getStreet2());
    accountIdentifiers.put("account_address_city", address.getCity());
    accountIdentifiers.put("account_address_state", address.getState());
    accountIdentifiers.put("account_address_country", address.getCountry());
    accountIdentifiers.put("account_address_zip", address.getPostalCode());
    accountIdentifiers.put("local_attrib_2", registration.getTenant().getName());
    accountIdentifiers.put("event_type", "ACCOUNT_CREATION");

    // Call Device control
    return ((DeviceFraudDetectionService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.DEVICE_FRAUD_CONTROL)).assessAccountCreationRisk(request
        .getSession().getId(), accountIdentifiers);
  }

  /**
   * Private method to add Fraud Profiling Host To Session
   * 
   * @param map
   */
  private void addFraudProfilingHostToSession(ModelMap map) {

    // No need to pass profiling API host data unless needed
    DeviceFraudDetectionService deviceFraudDetectionService = (DeviceFraudDetectionService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.DEVICE_FRAUD_CONTROL);
    if (deviceFraudDetectionService == null || !deviceFraudDetectionService.isEnabled()) {
      map.remove("ThreatMetrixEnabled");
      return;
    }

    map.addAttribute("ThreatMetrixEnabled", "True");

    ServletRequestAttributes requestAttrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

    // Add Device profiling parameters
    requestAttrs
        .getRequest()
        .getSession()
        .setAttribute(
            "fraudProfilingHost",
            deviceFraudDetectionService.getServiceInstanceConfiguration().getInstanceProperties().get("profilinghost")
                .getValue());

    requestAttrs
        .getRequest()
        .getSession()
        .setAttribute(
            "fraudOrgid",
            deviceFraudDetectionService.getServiceInstanceConfiguration().getInstanceProperties().get("orgid")
                .getValue());
  }

  /**
   * Method for utility Rate LightBox
   * 
   * @param channelParam
   * @param currencyCode
   * @param serviceInstanceUuid
   * @param productBundleCode
   * @param map
   * @return String
   */
  @RequestMapping(value = "/utilityrates_lightbox", method = RequestMethod.GET)
  public String utilityratesLightbox(@RequestParam(value = "channelParam", required = false) String channelParam,
      @RequestParam(value = "currencyCode", required = false) String currencyCode,
      @RequestParam(value = "serviceInstanceUuid", required = false) String serviceInstanceUuid,
      @RequestParam(value = "productBundleCode", required = false) String productBundleCode, ModelMap map) {
    logger.debug("### utilityrates_lightbox method starting...(GET)");
    Channel channel = channelService.getChannel(channelParam);
    CurrencyValue currency = currencyValueService.locateBYCurrencyCode(currencyCode);
    if (channel == null) {
      channel = channelService.getDefaultServiceProviderChannel();
    }
    ProductBundle productBundle = null;
    List<ServiceResourceTypeGeneratedUsage> generatedUsageListForServiceResourceType = null;
    if (StringUtils.isNotBlank(productBundleCode)) {
      productBundle = productBundleService.findProductBundleByCode(productBundleCode);
    }
    ServiceResourceType serviceResourceType = null;
    if (productBundle != null) {
      serviceResourceType = productBundle.getResourceType();
    }
    if (serviceResourceType != null) {
      generatedUsageListForServiceResourceType = serviceResourceType.getServiceResourceGenerate();
    }
    map.addAttribute("generatedUsageListForServiceResourceType", generatedUsageListForServiceResourceType);
    Map<Object, Object> retMap = productService.getUtilityChargesMap(channel, currency, serviceInstanceUuid, null,
        null, null);
    map.addAttribute("retMap", retMap);
    map.addAttribute("currency", currency);
    map.addAttribute("startDate", productService.getCurrentRevision(channel.getCatalog()).getStartDate());
    logger.debug("### utilityrates_lightbox method ending...(GET)");
    return "catalog.utilityrates.lightbox";
  }

}