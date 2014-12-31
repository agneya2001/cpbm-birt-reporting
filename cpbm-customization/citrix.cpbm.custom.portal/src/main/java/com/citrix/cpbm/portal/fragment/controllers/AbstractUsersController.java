/*
 * Copyright ¬© 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import com.citrix.cpbm.access.proxy.CustomProxy;
import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.admin.service.exceptions.ConnectorManagementServiceException;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.citrix.cpbm.platform.spi.View;
import com.citrix.cpbm.platform.spi.ViewResolver;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.vmops.internal.service.CustomFieldService;
import com.vmops.internal.service.EmailService.EmailTemplate;
import com.vmops.internal.service.EventService;
import com.vmops.internal.service.NotificationService;
import com.vmops.internal.service.PrivilegeService;
import com.vmops.internal.service.TelephoneVerificationService;
import com.vmops.model.AccountHolder;
import com.vmops.model.AuditLog;
import com.vmops.model.Country;
import com.vmops.model.Event.Category;
import com.vmops.model.Event.Scope;
import com.vmops.model.Event.Severity;
import com.vmops.model.Event.Source;
import com.vmops.model.Profile;
import com.vmops.model.Service;
import com.vmops.model.ServiceInstance;
import com.vmops.model.SpendAlertSubscription;
import com.vmops.model.Tenant;
import com.vmops.model.Tenant.State;
import com.vmops.model.User;
import com.vmops.model.UserAlertPreferences;
import com.vmops.model.UserAlertPreferences.AlertType;
import com.vmops.model.UserHandle;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.ProfileService;
import com.vmops.service.UserAlertPreferencesService;
import com.vmops.service.UserService;
import com.vmops.service.UserService.Handle;
import com.vmops.service.exceptions.BeanValidationException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.NoSuchUserException;
import com.vmops.utils.DateTimeUtils;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.CustomAlertForm;
import com.vmops.web.forms.UserAlertEmailForm;
import com.vmops.web.forms.UserForm;
import com.vmops.web.interceptors.UserContextInterceptor;
import com.vmops.web.validators.MyProfileValidator;
import com.vmops.web.validators.UserStep1Validator;

/**
 * Controller that handles all user pages.
 * 
 * @author vijay
 */
public abstract class AbstractUsersController extends AbstractAuthenticatedController {

  @Autowired
  private UserService userService;

  @Autowired
  private ProfileService profileService;

  // TODO: Move profile access to profile service
  @Autowired
  private PrivilegeService privilegeService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private EventService eventService;

  @Autowired
  private UserAlertPreferencesService userAlertPreferencesService;

// CHECKSTYLE:OFF
  @Autowired
  ConnectorManagementService connectorManagmentService;

// CHECKSTYLE:ON
  @Autowired
  private CustomFieldService customFieldService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private Configuration config;

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManager;

  /**
   * Logger.
   */
  private static Logger logger = Logger.getLogger(AbstractUsersController.class);

  /**
   * Find which all services are enable for given user
   * 
   * @param userParam
   * @param map
   * @return
   */
  @RequestMapping(value = "/{userParam}", method = RequestMethod.GET)
  public String show(@PathVariable String userParam, ModelMap map) {
    User user = userService.get(userParam);
    customFieldService.populateCustomFields(user);
    customFieldService.populateCustomFields(user.getTenant());
    map.addAttribute("user", user);
    map.addAttribute("userLocale", this.getLocaleDisplayName(user.getLocale()));
    if (!isAllowedToAddUser(user.getTenant())) {
      map.addAttribute("isUsersMaxReached", "Y");
    }

    Map<ServiceInstance, Boolean> registrationStatus = userService.getTenantEnabledCloudServiceInstanceStatusMap(user);
    map.put("showEnableServiceLink", Boolean.FALSE);

    Map<String, UserHandle> mapOfInstanceVsHandle = new HashMap<String, UserHandle>();
    for (ServiceInstance si : registrationStatus.keySet()) {
      UserHandle latestHandle = userService.getLatestUserHandle(user.getUuid(), si.getUuid());
      if (latestHandle != null) {
        mapOfInstanceVsHandle.put(si.getUuid(), latestHandle);
      }

      if (latestHandle == null
          || !(latestHandle.getState().equals(com.vmops.model.UserHandle.State.ACTIVE) || latestHandle.getState()
              .equals(com.vmops.model.UserHandle.State.PROVISIONING))) {
        if (CollectionUtils.isNotEmpty(user.getAuthorities(si.getService()))) {
          map.put("showEnableServiceLink", Boolean.TRUE);
        }
      }
    }

    map.put("mapOfInstanceVsHandle", mapOfInstanceVsHandle);
    map.put("serviceRegistrationStatus", registrationStatus);
    return "user.show";
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(value = {
      "", "/listusersforaccount"
  }, method = RequestMethod.GET)
  public String listUsersForAccount(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "showAll", required = false, defaultValue = "false") boolean showAll,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpSession session,
      @RequestParam(value = "user", required = false) String userParam,
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "perPage", required = false, defaultValue = "14") int perPage,
      @RequestParam(value = "showAddNewUserWizard", required = false) String showAddNewUserWizard,
      HttpServletRequest request) {

    logger.debug("###Entering in list(tenant,showAll,tenantId,map) method @GET");

    map.addAttribute("defaultPageSize", getDefaultPageSize());
    if (showAddNewUserWizard != null) {
      map.addAttribute("showAddNewUserWizard", showAddNewUserWizard);
    }
    session.setAttribute("currentPerPagesize", perPage);
    int totalUsers = 0;

    map.addAttribute("isShowingAll", showAll);
    List<User> results;
    String view = "users.list_with_admin_menu";
    if (isAdmin() && showAll) {
      results = userService.list(page, perPage, null, null, false, null, null, null);
      totalUsers = userService.count(null, null, null, null);
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(getCurrentUser()));
    } else if (isAdmin() && (Boolean) request.getAttribute("isSurrogatedTenant")) {
      tenant = tenantService.get(tenantParam);
      results = userService.list(page, perPage, null, null, false, null, tenant.getId().toString(), null);
      totalUsers = userService.count(null, null, tenant.getId().toString(), null);
      map.addAttribute("showUserProfile", true);
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(tenant.getOwner()));
      setPage(map, Page.CRM_ALL_USERS);
      view = "users.list_with_user_menu";
    } else {
      User currentUser = this.getCurrentUser();
      if (!userService.hasAuthority(currentUser, "ROLE_ACCOUNT_CRUD")) {
        view = "users.nonroot.list_with_user_menu";
        setPage(map, Page.USERS_ALL_USERS);
      }
      if (userParam != null) {
        results = new ArrayList<User>();
        results.add(userService.get(userParam));
        totalUsers = 1;
      } else {
        results = userService.list(page, perPage, null, null, false, null, tenant.getId().toString(), null);
        totalUsers = userService.count(null, null, tenant.getId().toString(), null);
      }
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(currentUser));
      setPage(map, Page.ADMIN_ALL_USERS);
    }
    if (results != null && results.size() > 0) {
      customFieldService.populateCustomFields(results.get(0));
      customFieldService.populateCustomFields(results.get(0).getTenant());
    }

    map.addAttribute("tenant", tenant);
    map.addAttribute("users", results);
    map.addAttribute("size", results != null ? results.size() : 0);

    map.addAttribute("selectedUser", userParam);
    // get session data having any error messages
    String errormsg = (String) session.getAttribute("errormsg");
    session.removeAttribute("errormsg");
    if (errormsg != null && errormsg.equals("true")) {
      List<String> globalErrors = (List<String>) session.getAttribute("globalErrors");
      if (globalErrors != null) {
        map.addAttribute("globalErrors", globalErrors);
      }
      List<String> errorMsgList = (List<String>) session.getAttribute("errorMsgList");
      if (errorMsgList != null) {
        map.addAttribute("errorMsgList", errorMsgList);
      }
      map.addAttribute("errormsg", errormsg);

      session.removeAttribute("errorMsgList");
      session.removeAttribute("globalErrors");
    }
    // check user limit
    if (!isAllowedToAddUser(tenant)) {
      map.addAttribute("isUsersMaxReached", "Y");
    }

    map.addAttribute("enable_next", "False");

    if (totalUsers - page * perPage > 0) {
      map.addAttribute("enable_next", "True");
    } else {
      map.addAttribute("enable_next", "False");
    }
    map.addAttribute("current_page", page);

    logger.debug("###Exiting list(req,map,session) method");
    return view;
  }

  /**
   * Method for User Creation Step1
   * 
   * @param tenant
   * @param tenantParam
   * @param map
   * @param session
   * @param request
   * @return String
   */
  @RequestMapping(value = "/new/step1", method = RequestMethod.GET)
  public String createStepOne(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpSession session,
      HttpServletRequest request) {
    logger.debug("Entering user create: createStepOne ");
    UserForm user = new UserForm();
    user.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));

    Tenant userTenant = tenant;
    if (isAdmin() && (Boolean) request.getAttribute("isSurrogatedTenant")) {
      userTenant = tenantService.get(tenantParam);
      setPage(map, Page.CRM_USER_ADD);
      map.addAttribute("showUserProfile", true);
    } else if (isAdmin() && !(Boolean) request.getAttribute("isSurrogatedTenant")) {
      userTenant = tenantService.get(tenantParam);
      setPage(map, Page.ADMIN_ALL_USERS);
      map.addAttribute("showUserProfile", true);
    } else {
      setPage(map, Page.USER_ADD);
    }
    map.addAttribute("userTenant", CustomProxy.newInstance(userTenant));
    map.addAttribute("tenant", CustomProxy.newInstance(userTenant));

    // verify for max users. if not allowed then move to not authorize page
    if (!isAllowedToAddUser(userTenant)) {
      String msg = messageSource.getMessage("max.users.reached.popup.message", null, null,
          LocaleUtils.toLocale(channelService.getDefaultLocale(userTenant.getSourceChannel())));
      throw new InvalidAjaxRequestException(msg);
    }
    // defaults to user.
    user.setUserProfile(privilegeService.getUserProfile().getId());
    List<Profile> valideProfiles = profileService.listValidProfiles(userTenant, null);
    user.setValidProfiles(valideProfiles);
    logger.debug("Exiting user create: createStepOne ");

    map.addAttribute("displayChannel", false);
    map.addAttribute("channels", channelService.getChannels(null, null, null));
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("defaultLocale", channelService.getDefaultLocale(userTenant.getSourceChannel()));

    user.setTimeZone(channelService.getDefaultTimeZone(userTenant.getSourceChannel()));
    map.addAttribute("user", user);

    session.setAttribute("currentStep", "newUserCreationStep1");
    String showImportFromDirectoryServiceButton = "false";
    if (config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled)
        && config.getValue(Names.com_citrix_cpbm_directory_mode).equals("pull")) {
      showImportFromDirectoryServiceButton = "true";
    }
    map.addAttribute("showImportAdButton", showImportFromDirectoryServiceButton);
    return "users.new.step1";

  }

  /**
   * @param username
   * @return
   */
  private boolean isValidUserName(final String username) {
    try {
      privilegeService.runAsPortal(new PrivilegedAction<Void>() {

        @Override
        public Void run() {
          userService.getUserByParam("username", username, false);
          return null;
        }
      });
    } catch (NoSuchUserException ex) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
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
    User loggedInUser = getCurrentUser();
    final String loggedInTenantSuffix = loggedInUser.getTenant().getUsernameSuffix();

    String exists = Boolean.FALSE.toString();
    try {
      privilegeService.runAsPortal(new PrivilegedAction<Void>() {

        @Override
        public Void run() {
          if (config.getValue(Names.com_citrix_cpbm_username_duplicate_allowed).equals("true")) {
            userService.getUserByParam("username", username + "@" + loggedInTenantSuffix, true);
            logger.debug("In validateUsername() method end");
          } else {
            userService.getUserByParam("username", username, true);
            logger.debug("In validateUsername() method end");
          }
          return null;
        }
      });
    } catch (NoSuchUserException ex) {
      logger.debug(username + ": not exits in users table");
      return Boolean.TRUE.toString();
    }
    return exists;
  }

  /**
   * @author Amitpals This method combines the functionality of step2 and step3 of previous wizard for user creation
   *         which gets call from Finish Button of User Creation at step1
   * @param form
   * @param result
   * @param tenant
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @param request
   * @return
   */
  @RequestMapping(value = "/new/step1", method = RequestMethod.POST)
  public String createUserStepTwo(@ModelAttribute("user") UserForm form, BindingResult result,
      @ModelAttribute("userTenant") com.citrix.cpbm.access.Tenant tenant, ModelMap map, HttpServletRequest request,
      HttpSession session) {
    logger.debug("### Entering user create : createUserStepTwo");
    boolean validationError = false;
    session.setAttribute("currentStep", "newUserCreationStep1");
    // This attribute decides(createnewuser.jsp) the stepNumber to be shown.
    // set it default to step1 in case of any error in form submission step one be shown
    UserStep1Validator validator = new UserStep1Validator();
    validator.validate(form, result);
    if (result.hasErrors()) {
      validationError = true;
    } else if (!isValidUserName(form.getUser().getUsername())) {
      // if the values entered are OK check lastly check username in DB
      result.rejectValue("user.username", "js.errors.register.user.usernameRemote");
      validationError = true;
    }

    if (validationError) {
      // Attributes to populate the drop downs
      map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
      map.addAttribute("defaultLocale", channelService.getDefaultLocale(tenant.getSourceChannel()));
      return "users.new.step1";
    }

    String email = form.getUser().getEmail();
    map.addAttribute("userTenant", tenant);
    map.addAttribute("tenant", tenant);

    String channelparam = null;
    if (tenant.getSourceChannel() != null) {
      channelparam = tenant.getSourceChannel().getParam();
    }
    if (isEmailBlacklisted(email.toLowerCase(), channelparam)) {
      map.addAttribute("signuperror", "emaildomainblacklisted");
      if (isAdmin() && isSurrogatedTenant(getCurrentUser().getTenant(), tenant.getParam())) {
        setPage(map, Page.CRM_USER_ADD);
        map.addAttribute("showUserProfile", true);
      } else if (isAdmin() && !isSurrogatedTenant(getCurrentUser().getTenant(), tenant.getParam())) {
        setPage(map, Page.ADMIN_ALL_USERS);
        map.addAttribute("showUserProfile", true);
      } else {
        setPage(map, Page.USER_ADD);
      }
      // verify for max users. if not allowed then move to not authorize page
      if (!isAllowedToAddUser(tenant.getObject())) {
        String msg = messageSource.getMessage("max.users.reached.popup.message", null, null,
            LocaleUtils.toLocale(channelService.getDefaultLocale(tenant.getSourceChannel())));
        throw new InvalidAjaxRequestException(msg);
      }
      map.addAttribute("displayChannel", false);
      map.addAttribute("channels", channelService.getChannels(null, null, null));
      map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
      map.addAttribute("defaultLocale", channelService.getDefaultLocale(tenant.getSourceChannel()));
      return "users.new.step1";

    }
    String channelParam = form.getChannelParam();
    User currentUser = getCurrentUser();
    boolean isSystemRoot = currentUser.equals(userService.getSystemUser(Handle.ROOT));
    if (isSystemRoot && channelParam != null) {
      form.getUser().setSourceChannel(channelService.getChannel(channelParam));
    }
    if (isAdmin() && tenant.getParam().equals(getTenant().getParam())) {
      setPage(map, Page.ADMIN_ALL_USERS);
    } else if (tenant.getParam().equals(getTenant().getParam())) {
      setPage(map, Page.USER_ADD);
    } else {
      setPage(map, Page.CRM_USER_ADD);
      map.addAttribute("showUserProfile", true);
    }

    map.addAttribute("user", form);
    // step3 merged
    // "Finish" button pressed
    if (!request.getParameter("submitButtonEmail").equals("CustomeEmail")) {
      // Check the flow once more
      logger.debug("### User creation : request Finish");
      com.citrix.cpbm.access.User user = form.getUser();
      if (form.getUserLocale() == null) {
        user.setLocale(LocaleUtils.toLocale(channelService.getDefaultLocale(tenant.getSourceChannel())));
      } else {
        user.setLocale(form.getUserLocale());
      }
      if (StringUtils.isNotEmpty(form.getTimeZone())) {
        user.setTimeZone(form.getTimeZone());
      } else {
        user.setTimeZone(channelService.getDefaultTimeZone(tenant.getSourceChannel()));
      }
      user.getObject().setCreatedBy(getCurrentUser());
      if (form.getUserProfile() != null) {
        user.setProfile(profileService.getProfile(form.getUserProfile()));
      }
      userService.createUserInTenant(user, tenant, result);
      if (result.hasErrors()) {
        map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
        map.addAttribute("defaultLocale", channelService.getDefaultLocale(tenant.getSourceChannel()));
        return "users.new.step1";
      }
      map.addAttribute("tenant", tenant);
      String message = "create.user";
      String messageArgs = user.getUsername() + "," + tenant.getName();
      eventService.createEvent(new Date(), tenant.getObject(), message, messageArgs, Source.PORTAL, Scope.ACCOUNT,
          Category.ACCOUNT, Severity.INFORMATION, true);
      // User successfully created show next step
      session.setAttribute("currentStep", "newUserCreationStep3");
      setNewUserPageCount(session, tenant.getObject());
      return "users.newuserregistration.finish";

    } else {
      setNewUserPageCount(session, tenant.getObject());
      Map<String, Object> model = new HashMap<String, Object>();
      String userLocale;
      model.put("user", form.getUser());
      model.put("custom", true);
      if (form.getUserLocale() == null) {
        userLocale = channelService.getDefaultLocale(tenant.getSourceChannel());
      } else {
        userLocale = form.getUserLocale().toString();
      }
      long channelID = tenant.getSourceChannel() != null ? tenant.getSourceChannel().getId() : channelService
          .getDefaultServiceProviderChannel().getId();
      String emailText = userService.getEmailTemplateAsStringByLocale(EmailTemplate.VERIFY_EMAIL, model, userLocale,
          channelID);
      map.addAttribute("emailText", emailText);
      logger.debug("### User creation : Customize Welcome EMail");
      // User successfully created show next step
      session.setAttribute("currentStep", "newUserCreationStep2");
      return "users.newuser.customemail";
    }
  }

  private void setNewUserPageCount(HttpSession session, final Tenant tenant) {
    int totalUsers = userService.count(null, null, tenant.getId().toString(), null);
    logger.debug("Toatal Users= " + totalUsers);
    Integer defaultPageSize = (Integer) session.getAttribute("currentPerPagesize");
    int defaultPerPage = 0;
    if (defaultPageSize == null) {
      defaultPerPage = getDefaultPageSize();
    } else {
      defaultPerPage = defaultPageSize;
    }
    int newUserPage = (totalUsers + 1) / defaultPerPage;
    if (totalUsers % defaultPerPage == 0) {
      newUserPage = totalUsers / defaultPerPage;
    } else {
      newUserPage = totalUsers / defaultPerPage + 1;
    }
    session.setAttribute("newUsersPageCount", newUserPage);
  }

  /**
   * Called from Click on finish Button of Custom Email message page.
   * 
   * @param form {@link UserForm}
   * @param result {@link BindingResult}
   * @param tenant {@link Tenant}
   * @param map {@link ModelMap}
   * @param request {@link HttpServletRequest}
   * @return view
   */

  @RequestMapping(value = "/new/step2", method = RequestMethod.POST)
  public String createStepThree(@ModelAttribute("user") UserForm form, BindingResult result,
      @ModelAttribute("userTenant") com.citrix.cpbm.access.Tenant tenant, ModelMap map, HttpServletRequest request) {
    logger.debug("### Entering user create : createStepThree");
    if (isAdmin() && tenant.getParam().equals(getTenant().getParam())) {
      setPage(map, Page.ADMIN_ALL_USERS);
    } else if (tenant.getParam().equals(getTenant().getParam())) {
      setPage(map, Page.USER_ADD);
    } else {
      setPage(map, Page.CRM_USER_ADD);
      map.addAttribute("showUserProfile", true);
    }
    com.citrix.cpbm.access.Tenant proxyTenant = tenant;
    map.addAttribute("user", form);
    map.addAttribute("userTenant", proxyTenant);
    map.addAttribute("tenant", proxyTenant);
    Map<String, String> customEmailInfo = null;
    if (form.getCustomEmailSubject() != null || form.getEmailText() != null) {
      logger.debug("### User creation : User creation with Custom Email");
      customEmailInfo = new HashMap<String, String>();
      customEmailInfo.put("customSubject", form.getCustomEmailSubject());
      customEmailInfo.put("customEmailText", form.getEmailText());
    }
    com.citrix.cpbm.access.User user = form.getUser();
    user.setLocale(form.getUserLocale());
    if (StringUtils.isNotEmpty(form.getTimeZone())) {
      user.setTimeZone(form.getTimeZone());
    } else {
      user.setTimeZone(channelService.getDefaultTimeZone(proxyTenant.getSourceChannel()));
    }
    user.getObject().setCreatedBy(getCurrentUser());
    if (form.getUserProfile() != null) {
      user.setProfile(profileService.getProfile(form.getUserProfile()));
    }

    userService.createUserInTenant(user, proxyTenant, customEmailInfo, result);
    logger.debug("### Exiting user create : createStepThree");

    HttpSession session = request.getSession();
    // User successfully created show next step
    session.setAttribute("currentStep", "newUserCreationStep3");
    return "users.newuserregistration.finish";

  }

  /**
   * Edit Myprofile
   * 
   * @param currentTab Selected Tab
   * @param userParam User Param
   * @param request {@link HttpServletRequest}
   * @param map {@link ModelMap}
   * @return View
   */
  @RequestMapping(value = {
    "/{userParam}/myprofile"
  }, method = RequestMethod.GET)
  public String edit(@RequestParam(value = "activeTab", required = false) String currentTab,
      @PathVariable String userParam, HttpServletRequest request, ModelMap map) {
    logger.debug("###Entering in edit(userId,map) method @GET");

    User user = userService.get(userParam);
    com.citrix.cpbm.access.User userProxy = (com.citrix.cpbm.access.User) CustomProxy.newInstance(user);
    User logedInUser = getCurrentUser();
    if (isAdmin() && isSurrogatedTenant(getCurrentUser().getTenant(), user.getTenant().getParam())) {
      if (userService.hasAuthority(logedInUser, "ROLE_USER_CRUD")) {
        map.addAttribute("showUserProfile", true);
      } else {
        throw new AccessDeniedException("Not Authorized");
      }
    }

    customFieldService.populateCustomFields(user);
    customFieldService.populateCustomFields(user.getTenant());
    UserForm form = new UserForm((com.citrix.cpbm.access.User) CustomProxy.newInstance(user));
    form.setCountryList(countryService.getCountries(null, null, null, null, null, null, null));

    TelephoneVerificationService teleVerificationService = (TelephoneVerificationService) connectorManagementService
        .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION);

    if (teleVerificationService != null && teleVerificationService.isEnabled()) {
      form.setPhoneVerificationEnabled(true);
      Random rnd = new Random();
      int n = 99999 - 1000;
      form.setGeneratedPhoneVerificationPin("" + rnd.nextInt(n));
      request.getSession().setAttribute("phoneVerificationPin", "" + rnd.nextInt(n));
    }

    // Since we have no way to get the country from country code so in case of isd code 1 we are hardcoding it to "US".
    Country country = getCountry(userProxy);

    if (form.getCountryCode() == null || form.getCountryCode().equals("")) {
      form.setCountryCode(country.getIsdCode());
    }
    map.addAttribute("country_code_XX", country.getCountryCode2());

    // add cloud servcies and cloud for left menu
    List<Service> services = connectorConfigurationManager.getAllServicesByType(CssdkConstants.CLOUD);
    map.addAttribute("services", services);
    map.addAttribute("categories", connectorConfigurationManager.getAllCategories(CssdkConstants.CLOUD));

    // Phone number is mandatory for the owner of the tenant.
    if (user.getTenant().getOwner().equals(user)) {
      map.addAttribute("PhoneNumberMandatory", true);
    } else {
      map.addAttribute("PhoneNumberMandatory", false);
    }

    try {
      form.setUserClone((com.citrix.cpbm.access.User) CustomProxy.newInstance((User) user.clone()));
    } catch (CloneNotSupportedException e) {
      logger.debug("Cloning of user failed", e);
    }

    List<AuditLog> auditLogs = auditLogService.getLatestLoginAuditLog(user.getId());
    if (CollectionUtils.isNotEmpty(auditLogs)) {
      map.addAttribute("lastLogin", auditLogs.get(0).getCreationDate());
    }
    map.addAttribute("logins", auditLogService.getLoginCount(user.getId()));

    List<Country> filteredCountryList = getFilteredCountryList(form.getCountryList(), user.getTenant()
        .getSourceChannel() != null ? user.getTenant().getSourceChannel().getParam() : null);

    map.addAttribute("filteredCountryList", filteredCountryList);

    // create the gravatar url
    String gravatarUrl = generateGravatarUrl(user.getEmail());
    map.addAttribute("isprofilepage", "true");
    map.addAttribute("isUserRoot", userIsRoot(user));
    map.addAttribute("gravatarUrl", gravatarUrl);
    map.addAttribute("user", form);
    map.addAttribute("tenant", CustomProxy.newInstance(user.getTenant()));
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("userLocale", this.getLocaleDisplayName(userProxy.getLocale()));
    map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));
    String doNotShowPasswordEditLink = "false";
    if ((config.getBooleanValue(Configuration.Names.com_citrix_cpbm_portal_directory_service_enabled) && config
        .getValue(Names.com_citrix_cpbm_directory_mode).equals("pull")) || !user.isEmailVerified()) {
      doNotShowPasswordEditLink = "true";
    }
    map.addAttribute("doNotShowPasswordEditLink", doNotShowPasswordEditLink);
    map.addAttribute("doNotShowGravatarLink", Boolean.valueOf(config.getValue(Names.com_citrix_cpbm_use_intranet_only)));

    List<UserAlertPreferences> alertsPrefs = userAlertPreferencesService.listAllUserAlertPreferences(user);
    if (CollectionUtils.isNotEmpty(alertsPrefs)) {
      map.addAttribute("alertsPrefs", alertsPrefs);
      map.addAttribute("alertsPrefsSize", alertsPrefs.size());
      List<UserAlertPreferences> verifiedAlertPrefs = getVerifiedEmailPreferences(alertsPrefs);
      if (CollectionUtils.isNotEmpty(verifiedAlertPrefs)) {
        map.addAttribute("showChangePrimaryEmailLink", "true");
      }
    }
    map.addAttribute("addAlertEmailLimit",
        config.getIntValue(Names.com_citrix_cpbm_accountManagement_resourceLimits_registered_emailAddresses));

    map.addAttribute("activeTab", currentTab);
    map.addAttribute("userAlertEmailForm", new UserAlertEmailForm(user, AlertType.USER_ALERT_EMAIL));
    prepareServiceViewForUser(map, user);
    @SuppressWarnings("unchecked")
    Map<ServiceInstance, Boolean> serviceInstanceMap = (Map<ServiceInstance, Boolean>) map.get("serviceInstanceMap");

    List<ServiceInstance> serviceInstanceList = userService.getCloudServiceInstance(user, null);
    serviceInstanceMap.keySet().retainAll(serviceInstanceList);
    map.addAttribute("countPerCategory",
        connectorConfigurationManager.getInstanceCountPerCategoryMap(serviceInstanceMap));

    if ("notPref".equals(currentTab)) {
      setPage(map, Page.USER_NOTIFICATION_PREFERENCES);
    } else if ("emailAddresses".equals(currentTab)) {
      setPage(map, Page.USER_EMAIL_ADDRESSES);
    } else {
      setPage(map, Page.USER_PERSONAL_PROFILE);
    }
    logger.debug("###Exiting edit(userId,map) method @GET");
    return "users.edit.myprofile";
  }

  private List<UserAlertPreferences> getVerifiedEmailPreferences(List<UserAlertPreferences> alertsPrefs) {
    List<UserAlertPreferences> verifiedAlertPrefs = new ArrayList<UserAlertPreferences>();
    UserAlertPreferences alertPref = null;
    Iterator<UserAlertPreferences> alertPrefIt = alertsPrefs.iterator();
    while (alertPrefIt.hasNext()) {
      alertPref = alertPrefIt.next();
      if (alertPref.isEmailVerified()) {
        verifiedAlertPrefs.add(alertPref);
      }
    }
    return verifiedAlertPrefs;
  }

  private Country getCountry(com.citrix.cpbm.access.User userProxy) {
    Country country = null;
    String isdCode = userProxy.getCountryCode();
    if (isdCode == null) {
      country = countryService.locateCountryByCode(userProxy.getObject().getTenant().getAddress().getCountry());
    } else {
      if (isdCode.trim().equals("1")) {
        country = countryService.locateCountryByCode("US");
      } else {
        List<Country> countryList = countryService.locateCountryByIsdCode(isdCode);
        if (countryList.size() > 0) {
          country = countryList.get(0);
        } else {
          country = countryService.locateCountryByCode("US");
        }
      }
    }
    return country;
  }

  /**
   * Validates Email.
   * 
   * @param userParam User Param
   * @param email Email Id
   * @param request {@link HttpServletRequest}
   * @param map {@link ModelMap}
   * @return true if email is not blacklisted otherwise false.
   */
  @RequestMapping(value = "/{userParam}/validateemail", method = RequestMethod.GET)
  @ResponseBody
  public String validateemail(@PathVariable String userParam,
      @RequestParam(value = "email", required = false) String email, HttpServletRequest request, ModelMap map) {
    User user = userService.get(userParam);
    String channelParam = user.getTenant().getCatalog().getChannel().getParam();
    if (email != null && email.length() > 0 && isEmailBlacklisted(email.toLowerCase(), channelParam)) {
      return "false";
    }
    return "true";
  }

  /**
   * @param form
   * @param result
   * @param map
   * @param status
   * @return
   */
  @RequestMapping(value = {
    "/{userParam}/myprofile"
  }, method = RequestMethod.POST)
  public String edit(@PathVariable String userParam, @Valid @ModelAttribute("user") UserForm form,
      BindingResult result, HttpServletRequest request, ModelMap map, SessionStatus status) {
    logger.debug("###Entering in edit(form,result,map,status) method @POST");
    com.citrix.cpbm.access.User user = form.getUser();

    MyProfileValidator validator = new MyProfileValidator();
    validator.validate(form, result);
    if (result.hasErrors()
        && !(result.getErrorCount() == 1 && result.getAllErrors().get(0).getCode().equals("Size") && form.getUser()
            .getUsername().equals("root"))) {
      displayErrors(result);
      // TODO to return the edit page with errors
      return "redirect:/portal/users/" + userParam + "/myprofile";
    }
    User logedInUser = this.getCurrentUser();
    String channelparam = null;
    if (user.getTenant().getSourceChannel() != null) {
      channelparam = user.getTenant().getSourceChannel().getParam();
    }
    if (isEmailBlacklisted(user.getEmail().toLowerCase(), channelparam)) {
      logger.info("Email Id : " + form.getUser().getEmail()
          + " rejected because it is not on the whitelist or part of the blacklist. Kindly contact support");
      result.rejectValue("user.email", "signup.emaildomain.blacklist.error");
      return edit(null, user.getObject().getUuid(), request, map);
    }
    // password reset
    if (form.getClearPassword() != null && !form.getClearPassword().isEmpty()) {
      if (form.getOldPassword() == null) {
        result.addError(new FieldError(result.getObjectName(), "oldPassword", null, false, new String[] {
          "errors.password.required"
        }, null, null));
      } else if (!user.getObject().authenticate(form.getOldPassword())) {
        result.addError(new FieldError(result.getObjectName(), "oldPassword", null, false, new String[] {
          "errors.password.invalid"
        }, null, null));
      } else {
        user.getObject().setClearPassword(form.getClearPassword());
      }
    }
    com.citrix.cpbm.access.User userClone = form.getUserClone();
    // TODO need to do Validation(Once fix this then remove @Ignore annotation against testUpdateUserFail from Test
    // Suit.

    // removing all characters from phone number
    form.setPhone(form.getPhone().replaceAll(PHONE_NUMBER_REGEX, ""));
    String oldPhone = userClone.getPhoneWithoutIsdCode() != null ? userClone.getPhoneWithoutIsdCode() : "";
    boolean phoneVerificationEnabled = false;
    if (!form.getPhone().equals(oldPhone.replaceAll(PHONE_NUMBER_REGEX, ""))
        || !form.getCountryCode().toString().equals(user.getCountryCode())) {

      if (connectorManagementService.getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION) != null
          && ((TelephoneVerificationService) connectorManagementService
              .getOssServiceInstancebycategory(ConnectorType.PHONE_VERIFICATION)).isEnabled()) {
        phoneVerificationEnabled = true;
      }

      if (phoneVerificationEnabled && !userService.hasAuthority(logedInUser, "ROLE_ACCOUNT_CRUD")) {
        String generatedPhoneVerificationPin = (String) request.getSession().getAttribute("phoneVerificationPin");
        String actualPhoneNumber = (String) request.getSession().getAttribute("phoneNumber");
        if (form.getUserEnteredPhoneVerificationPin() == null
            || !form.getUserEnteredPhoneVerificationPin().equals(generatedPhoneVerificationPin)
            || !areDigitsInPhoneNosEqual(form.getPhone(), actualPhoneNumber)) {
          map.addAttribute("userEditError", "phoneVerfication.error");
          result.rejectValue("phone", "phoneVerfication.error");
          parseResult(result, map);
          return edit(null, user.getObject().getUuid(), request, map);
        }
      }
    }
    String phoneNo = form.getCountryCode().replaceAll(PHONE_NUMBER_REGEX, "") + COUNTRY_CODE_TO_PHONE_NUMBER_SEPERATOR
        + form.getPhone().replaceAll(PHONE_NUMBER_REGEX, "");
    // Set the phone number
    if (!phoneVerificationEnabled && StringUtils.isEmpty(form.getPhone())) {
      user.setPhone(null);
    } else {
      user.setPhone(phoneNo);
    }

    if ((user.getObject().getTenant().getState() == State.ACTIVE
        || user.getObject().getTenant().getState() == State.LOCKED || user.getObject().getTenant().getState() == State.SUSPENDED)
        && !user.getEmail().equals(userClone.getEmail())) {
      userAlertPreferencesService.createUserAlertPreference(user.getObject(), user.getEmail(), AlertType.USER_EMAIL);
      // set email so that it wont be updated in users table and other places
      user.setEmail(userClone.getEmail());
      user.setEmailVerified(true);
    }
    try {
      userService.update(user, result);
    } catch(BeanValidationException e) {
      if (result.hasErrors()
          && !(result.getErrorCount() == 1 && result.getAllErrors().get(0).getCode().equals("Size") && form.getUser()
              .getUsername().equals("root"))) {
        displayErrors(result);
        // TODO to return the edit page with errors
        return edit(null, userParam, request, map);
      }
    }
    form.setUser(user);
    map.addAttribute("user", form);
    setPage(map, Page.USER_PERSONAL_PROFILE);
    status.setComplete();
    logger.debug("###Exiting edit(form,result,map,status) method @POST");
    map.clear();
    return "redirect:/portal/users/" + user.getObject().getParam() + "/myprofile";
  }

  /**
   * This method is invoked when we try to change the password from the MyProfile Page
   * 
   * @param form
   * @param result
   * @param request
   * @param map
   * @param status
   * @param userParam
   * @return String - success/failure
   */

  @RequestMapping(value = {
    "/changePassword"
  }, method = RequestMethod.POST)
  @ResponseBody
  public String changePassword(@Valid @ModelAttribute("user") UserForm form, BindingResult result,
      HttpServletRequest request, ModelMap map, SessionStatus status, @RequestParam("userParam") String userParam) {
    com.citrix.cpbm.access.User user = form.getUser();
    boolean isSystemRoot = userService.getSystemUser(Handle.ROOT).getUuid().equals(userParam);
    if (isSystemRoot || !config.getBooleanValue(Names.com_citrix_cpbm_portal_directory_service_enabled)) {
      if (user.getObject().getOldPassword().equals(userService.get(userParam).getPassword())) {
        user.setPassword(form.getUser().getPassword());
        user = userService.save(user, result);
        form.setUser(user);
        return "success";
      }
      return "failure";
    } else if (config.getValue(Names.com_citrix_cpbm_directory_mode).equals("push")) {
      if (userService.authenticateUserInDirectoryService(user.getUsername(), user.getObject().getPlainOldPassword())) {
        User ldapUser = userService.get(userParam);
        userService.updateUserPassword(form.getUser().getObject().getLdapPassword(), ldapUser.getUuid());
        return "success";
      }
      return "failure";
    }
    return "failure";
  }

  /**
   * @param tenant
   * @param term
   * @param tenantParam
   * @param map
   * @return
   */
  @RequestMapping("/search")
  public String search(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "term", required = true) String term,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map) {
    if (isAdmin() && tenantParam != null) {
      tenant = tenantService.get(tenantParam);
    }
    map.addAttribute("users", userService.list(0, 0, term, null, true, null, tenant.getId().toString(), null));
    return "users.search_results";
  }

  /**
   * @param tenant
   * @param tenantParam
   * @param manage
   * @param serviceInstanceUUID
   * @param map
   * @return
   */
  @RequestMapping(value = "/cloud_login", method = RequestMethod.GET)
  @ResponseBody
  public String login(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "manage", required = false) String manage,
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID, ModelMap map) {
    logger.debug("###Entering in login(tenantId,map) method @GET");
    String url = null;
    Tenant userTenant = tenant;

    if (isAdmin() && tenantParam != null) {
      userTenant = tenantService.get(tenantParam);
    }
    url = getConsoleURL(userTenant, map, serviceInstanceUUID);
    if (url == null) {
      throw new NoSuchUserException("Invalid user for params");
    }
    if (manage != null && manage.length() > 0) {
      url = url + "&lp=" + manage;
    }
    logger.debug("###Exiting login(tenantId,map) method @GET");
    map.clear();
    return url;
  }

  /**
   * Private Method to get Console URL for the Tenant.
   * 
   * @param tenantId
   * @param map
   * @return String
   */
  private String getConsoleURL(Tenant tenant, ModelMap map, String serviceInstanceUUID) {
    String returnVal = null;
    // if user has CMgmt role, then login as user
    User user = getCurrentUser();
    View view = null;
    CloudConnector cloudConnector = (CloudConnector) connectorManagementService.getServiceInstance(serviceInstanceUUID);
    if (userService.getUserHandleByServiceInstanceUuid(user.getUuid(), serviceInstanceUUID) == null
        && userService.getUserHandleByServiceInstanceUuid(user.getTenant().getOwner().getUuid(), serviceInstanceUUID) != null) {
      view = cloudConnector.getViewResolver().getConsoleView(user.getTenant().getOwner());
    } else {
      view = cloudConnector.getViewResolver().getConsoleView(user);
    }
    returnVal = view != null ? view.getURL() : null;
    return returnVal;
  }

  /**
   * list the alerts
   * 
   * @param map
   * @return String
   */
  @RequestMapping(value = "/alert_prefs", method = RequestMethod.GET)
  public String viewAlertsDeliveryOptions(ModelMap map) {
    logger.debug("###Entering in viewAlertsDeliveryOptions(map) method @GET");
    setPage(map, Page.DASHBOARD_ALL_ALERTS);
    User user = getCurrentUser();
    map.addAttribute("user", user);

    List<UserAlertPreferences> alertsPrefs = new ArrayList<UserAlertPreferences>();
    try {
      alertsPrefs = userAlertPreferencesService.listUserAlertPreferences(user);
      map.addAttribute("alertsPrefs", alertsPrefs);
      map.addAttribute("alertsPrefsSize", alertsPrefs.size());
      map.addAttribute("addAlertEmailLimit",
          config.getIntValue(Names.com_citrix_cpbm_accountManagement_resourceLimits_registered_emailAddresses));
    } catch (Exception e) {
      logger.error(e);
    }
    logger.debug("###Exiting viewAlertsDeliveryOptions(map) method @GET");
    return "users.alerts.delivery_opts";
  }

  /**
   * list the alerts
   * 
   * @param map
   * @return String
   */
  @RequestMapping(value = "/alert_prefs", method = RequestMethod.POST)
  @ResponseBody
  public String saveAlertsDeliveryOptions(@RequestParam(value = "newEmail", required = false) String emailAddress,
      ModelMap map) {
    logger.debug("###Entering in viewAlertsDeliveryOptions(map) method @GET");

    User user = getCurrentUser();
    map.addAttribute("user", user);
    try {
      userAlertPreferencesService.createUserAlertPreference(user, emailAddress, AlertType.USER_ALERT_EMAIL);
    } catch (Exception e) {
      logger.debug("###Exiting viewAlertsDeliveryOptions(map) method @POST - failure");
      return "failure";
    }

    logger.debug("###Exiting viewAlertsDeliveryOptions(map) method @POST - success");
    return "success";
  }

  /**
   * edit timezone and preferred language
   * 
   * @param map
   * @return <string,string> Map
   */
  @RequestMapping(value = "/edit_prefs", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> editPrefs(@ModelAttribute("user") UserForm form, BindingResult result,
      @RequestParam(value = "timeZone", required = false) String timeZone,
      @RequestParam(value = "locale", required = false) String locale, ModelMap map, HttpServletRequest request) {
    logger.debug("####Entering in editPrefs() method @POST with TimeZone: " + timeZone);
    Map<String, String> returnMap = new HashMap<String, String>();
    com.citrix.cpbm.access.User user = form.getUser();
    // audit logs processing
    List<AuditLog> auditLogs = auditLogService.getLatestLoginAuditLog(user.getId());
    Date lastLogin = null;
    if (CollectionUtils.isNotEmpty(auditLogs)) {
      lastLogin = auditLogs.get(0).getCreationDate();
    }
    boolean localeChange = false;
    String oldLocale = user.getLocale() != null ? user.getLocale().toString() : null;
    try {
      if (StringUtils.isNotEmpty(timeZone)) {
        user.setTimeZone(timeZone);
      } else {
        user.setTimeZone(channelService.getDefaultTimeZone(user.getTenant().getSourceChannel()));
      }
      if (locale != null && !locale.equals("") && !locale.equals(oldLocale)) {
        user.setLocale(LocaleUtils.toLocale(locale));
        localeChange = true;
      }
      if (locale == null) {
        user.setLocale(null);
      }
      userService.update(user, result);
      User savedUser = userService.getUserByParam("username", user.getUsername(), false);
      form.setUser((com.citrix.cpbm.access.User) CustomProxy.newInstance(savedUser));
      map.addAttribute("user", form);
    } catch (Exception e) {
      logger.error("###Exiting editPrefs(String,map) method @POST - failure", e);
      returnMap.put("failure", "failure");
      localeChange = false;
      returnMap.put("localeChange", "false");
      return returnMap;
    }
    // Changing the locale value in session to get effective across the portal . No need to login and logout.
    if (localeChange && user.getObject().getParam().equals(getCurrentUser().getParam())) {
      WebUtils.setSessionAttribute(request, SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, user.getLocale());
      returnMap.put("localeChange", "true");
    }
    map.addAttribute("userLocale", this.getLocaleDisplayName(user.getLocale()));
    returnMap.put("timeZone", user.getTimeZone());
    returnMap.put("locale", this.getLocaleDisplayName(user.getLocale()));
    if (timeZone != null) {
      returnMap.put("lastLogin", ""
          + (lastLogin != null ? lastLogin.getTime() + TimeZone.getTimeZone(timeZone).getOffset(lastLogin.getTime())
              : ""));
    } else {
      returnMap.put("lastLogin", "" + (lastLogin != null ? lastLogin.getTime() : ""));
    }
    logger.debug("###Exiting editPrefs(String, map) method @POST - success");
    return returnMap;

  }

  /**
   * Verify if password is correct for the current user
   * 
   * @param map
   * @return
   */
  @RequestMapping(value = "/verify_password", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> verifyPassword(@RequestParam(value = "password", required = true) final String password,
      HttpServletRequest request) {

    Map<String, Object> mapresult = new HashMap<String, Object>();
    logger.debug("###Entering in verifyPassword(password,session) method @POST");
    User user = getCurrentUser();
    boolean isSystemRoot = user.equals(userService.getSystemUser(Handle.ROOT));
    List<Map<String, String>> credentialList = new ArrayList<Map<String, String>>();
    try {
      credentialList = connectorConfigurationManager.getApiCredentials(user, getTenant(), password,
          getSessionLocale(request), false);
    } catch (Exception e) {
      logger.error(e);
    }
    HashMap<String, String> bssApiCreds = new HashMap<String, String>();
    boolean authenticated = false;
    if ("cas".compareToIgnoreCase(config.getAuthenticationService()) == 0) {
      authenticated = user.authenticate(password);
      if (!isSystemRoot && config.getBooleanValue(Names.com_citrix_cpbm_portal_directory_service_enabled)
          && !authenticated) {
        authenticated = userService.authenticateUserInDirectoryService(user.getUsername(), password);
      }
    } else {
      if (!isSystemRoot && config.getBooleanValue(Names.com_citrix_cpbm_portal_directory_service_enabled)) {
        authenticated = userService.authenticateUserInDirectoryService(user.getUsername(), password);
      } else {
        authenticated = user.authenticate(password);
      }
    }
    if (authenticated) {
      String protocol = config.getValue(Names.com_citrix_cpbm_portal_settings_public_protocol);
      String host = config.getValue(Names.com_citrix_cpbm_portal_settings_public_host);
      String port = config.getValue(Names.com_citrix_cpbm_portal_settings_public_port);

      String bssApiEndpointURL = null;
      if (("http".equalsIgnoreCase(protocol) && "80".equals(port))
          || ("https".equalsIgnoreCase(protocol) && "443".equals(port))) {
        bssApiEndpointURL = protocol + "://" + host + "/portal/api";
      } else {
        bssApiEndpointURL = protocol + "://" + host + ":" + port + "/portal/api";
      }

      bssApiCreds.put("bssApiEndpointURL", bssApiEndpointURL);
      bssApiCreds.put("apiKey", user.getApiKey());
      bssApiCreds.put("secretKey", user.getSecretKey());
      mapresult.put("bssApiCredentials", bssApiCreds);
    }
    mapresult.put("userCredentialList", credentialList);
    mapresult.put("success", authenticated);

    logger.debug("###Exiting verifyPassword(password,map) method @POST returning  for " + user.getName());
    return mapresult;
  }

  @RequestMapping(value = "/generate_api_key", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> generateApiKey(HttpServletRequest request) {
    Map<String, Object> mapresult = new HashMap<String, Object>();
    logger.debug("###Entering in generateApiKey() method @POST");
    User user = getCurrentUser();
    userService.generateApiKeyAndSecretKey(user);
    logger.debug("###Exiting generateApiKey() method @POST returning  for " + user.getName());
    user = userService.getUserByParam("id", user.getId(), false);
    HashMap<String, String> bssApiCreds = new HashMap<String, String>();
    bssApiCreds.put("apiKey", user.getApiKey());
    bssApiCreds.put("secretKey", user.getSecretKey());
    mapresult.put("bssApiCredentials", bssApiCreds);
    mapresult.put("success", true);
    return mapresult;
  }

  /**
   * creates subscriptions
   * 
   * @param form
   * @param map
   * @return string
   */
  @RequestMapping(value = "/subscribe/new", method = RequestMethod.POST)
  public String createSpendAlertSubscription(@ModelAttribute("subscriptionForm") CustomAlertForm form, ModelMap map) {
    logger.debug("###Entering in createSubscription(form,result,map) method @POST");
    setPage(map, Page.HOME);
    User user = getCurrentUser();
    // Spend Limit money value type
    int subscriptionType = 1;
    SpendAlertSubscription subscription = new SpendAlertSubscription();
    subscription.setSubscriptionType(subscriptionType);
    AccountHolder accountHolder = null;
    subscription.setAccountHolder(accountHolder);
    subscription.setUser(user);
    // add new subscription
    notificationService.saveSubscription(subscription);

    logger.debug("###Exiting createSubscription(form,result,map) method @POST");
    return "redirect:/portal/users/subscribe";
  }

  /**
   * deletes subscription
   * 
   * @param subscriptionId
   * @param map
   * @return
   */
  @RequestMapping(value = "/{subscriptionId}/subscribe/delete", method = RequestMethod.POST)
  public String removeSpendAlertSubscription(@PathVariable String subscriptionId, ModelMap map) {
    setPage(map, Page.HOME);
    SpendAlertSubscription subscription = notificationService.getSpendAlertSubscription(new Long(subscriptionId));
    // removes subscription
    notificationService.removeSubscription(subscription);

    return "redirect:/portal/users/subscribe";
  }

  /**
   * Method is used in case of Requesting Password reset.
   * 
   * @param form
   * @param request
   * @param map
   */
  @RequestMapping(value = "/{userParam}/reset_password", method = RequestMethod.POST)
  public void requestReset(@ModelAttribute("user") UserForm form, HttpServletRequest request, ModelMap map) {
    logger.debug("###Entering in requestReset(username,request) method @POST");
    try {
      com.citrix.cpbm.access.User user = form.getUser();
      if (user.isEmailVerified()) {
        userService.sendResetPasswordMail(user.getObject(), "reset.password", user.getUsername());
      }
    } catch (NoSuchUserException ex) {
      logger.debug(ex.fillInStackTrace());
    }
    logger.debug("###Exiting requestReset(username,request) method @POST");
  }

  /**
   * Method used for deleting.
   * 
   * @param userParam
   * @param map
   * @return null
   * @throws NoSuchUserException
   * @throws ConnectorManagementServiceException
   */
  @RequestMapping(value = "/{userParam}/delete", method = RequestMethod.GET)
  @ResponseBody
  public String delete(@PathVariable String userParam, ModelMap map, HttpSession session) throws NoSuchUserException {
    User user = userService.get(userParam);
    Tenant tenant = user.getTenant();
    if (tenant.getState().equals(State.ACTIVE)) {
      String userNameWithoutId = user.getUsername();
      userService.deleteUser(user, getCurrentUser());
      map.clear();
      String message = "delete.user";
      String messageArgs = userNameWithoutId + "," + tenant.getName();
      eventService.createEvent(new Date(), tenant, message, messageArgs, Source.PORTAL, Scope.ACCOUNT,
          Category.ACCOUNT, Severity.INFORMATION, true);
      clearActiveSessionForUser(userNameWithoutId, session.getId());
    }
    return null;
  }

  /**
   * Gives the view for deactivating the user
   * 
   * @param userParam
   * @param action
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @return
   */
  @RequestMapping(value = "/{userParam}/deactivate", method = RequestMethod.GET)
  public String deactivate(@PathVariable String userParam,
      @RequestParam(value = "action", required = false) String action, ModelMap map) {
    User user = userService.get(userParam);
    map.addAttribute("user", user);
    String view = "redirect:/portal/users/" + userParam + "/myprofile";
    if (action != null && action.equals("ajax")) {
      String profileName = user.getProfile().getName();
      map.addAttribute("profileName", profileName);
      view = "user.show";
    }
    return view;
  }

  /**
   * Deactivates the given user
   * 
   * @param userParam
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @return
   */
  @RequestMapping(value = "/{userParam}/deactivate_user", method = RequestMethod.POST)
  @ResponseBody
  public User deactivateUser(@PathVariable String userParam, ModelMap map) {
    User user = userService.get(userParam);
    userService.disableAndSendDeactivationMail(user);
    map.addAttribute("user", user);
    return user;
  }

  /**
   * Gives the view for activating the user
   * 
   * @param userParam
   * @param action
   * @param map which is to be filled by method. Contains all the UI accessible variables as an attribute
   * @return
   */
  @RequestMapping(value = "/{userParam}/activate", method = RequestMethod.GET)
  public String activate(@PathVariable String userParam,
      @RequestParam(value = "action", required = false) String action, ModelMap map) {
    User user = userService.get(userParam);
    map.addAttribute("user", user);
    String view = "redirect:/portal/users/" + userParam + "/myprofile";
    if (action != null && action.equals("ajax")) {
      String profileName = user.getProfile().getName();
      map.addAttribute("profileName", profileName);
      view = "user.show";
    }
    return view;

  }

  /**
   * Activates the given user
   * 
   * @param userParam
   * @param map
   * @return
   */
  @RequestMapping(value = "/{userParam}/activate_user", method = RequestMethod.POST)
  @ResponseBody
  public User activateUser(@PathVariable String userParam, ModelMap map) {
    User user = userService.get(userParam);
    userService.enableAndSendActivationMail(user);
    map.addAttribute("user", user);
    return user;
  }

  /**
   * To resend user verification Email.
   * 
   * @param userParam
   * @param action
   * @param map
   * @return String
   */
  @RequestMapping(value = "/{userParam}/resendverification", method = RequestMethod.GET)
  public String resendVerificationEmail(@PathVariable String userParam,
      @RequestParam(value = "action", required = false) String action, ModelMap map) {
    User user = userService.get(userParam);
    userService.resendVerificationEmail(user);

    map.addAttribute("user", user);
    String view = "redirect:/portal/users/" + userParam + "/myprofile";
    if (action != null && action.equals("ajax")) {
      String profileName = user.getProfile().getName();
      map.addAttribute("profileName", profileName);
      view = "user.show";
    }
    return view;
  }

  /**
   * Validates for max custom spend alerts
   * 
   * @param id
   * @return String
   */
  @RequestMapping(value = "/subscribe/verify_max_subscriptions")
  @ResponseBody
  public String verifyMaxSubscription(@RequestParam(value = "projectMembershipId", required = false) final String id,
      @RequestParam(value = "tenantId", required = false) String tenantId) {
    logger.debug("In verifyMaxSubscription() method start and projectMembershipId is : " + id);
    Boolean result = notificationService.verifyMaxSubscription(id, tenantId, getCurrentUser());
    return result.toString();
  }

  private void parseResult(BindingResult result, ModelMap map) {

    if (result.getFieldErrors().size() > 0) {
      List<String> errorMsgList = new ArrayList<String>();
      for (FieldError fieldError : result.getFieldErrors()) {
        String fieldName = fieldError.getField();
        if (fieldName.contains(".")) {
          fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
        }
        errorMsgList.add(fieldName + " field value '" + fieldError.getRejectedValue() + "' is not valid.");
      }

      map.addAttribute("errorMsgList", errorMsgList);
      map.addAttribute("errormsg", "true");
    }
    if (result.hasGlobalErrors()) {
      List<ObjectError> errorList = result.getGlobalErrors();
      if (errorList.size() > 0) {
        List<String> globalErrors = new ArrayList<String>();
        for (ObjectError error : errorList) {
          globalErrors.add(error.getCode());

        }
        map.addAttribute("globalErrors", globalErrors);
        map.addAttribute("errormsg", "true");
      }

    }
  }

  private boolean isAllowedToAddUser(Tenant tenant) {
    int userLimit;
    if (tenant.getMaxUsers() != null) {
      userLimit = tenant.getMaxUsers().intValue();
    } else {
      userLimit = tenant.getAccountType().getMaxUsers().intValue();
    }

    List<User> usersUnderTenant = userService.list(0, 0, null, null, false, null, tenant.getId().toString(), null);
    int noOfUsers = usersUnderTenant.size();
    if (userLimit >= 0 && noOfUsers >= userLimit) {
      return false;
    }
    return true;
  }

  class DateCompare implements Comparator<Date> {

    @Override
    public int compare(Date arg0, Date arg1) {
      return arg0.compareTo(arg1);
    }

  }

  /**
   * Retrieve the User time zone offset
   * 
   * @param currentTenant
   * @param tenantParam
   * @param request
   * @return
   */
  @RequestMapping(value = "/user_timezone_offset", method = RequestMethod.GET)
  @ResponseBody
  public String getUserTimezoneOffset(@ModelAttribute("currentTenant") Tenant currentTenant, HttpServletRequest request) {
    logger.debug("### getUserTimezoneOffset method starting...(GET)");
    String userTimeZoneOffset = "0";
    Tenant effectiveTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);

    User user = getCurrentUser();
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      user = effectiveTenant.getOwner();
    }
    if (user.getTimeZone() != null) {
      userTimeZoneOffset = DateTimeUtils.getOffset(user.getTimeZone());
    } else {
      userTimeZoneOffset = DateTimeUtils.getOffset(Calendar.getInstance().getTimeZone().getID());
    }

    logger.debug("### getUserTimezoneOffset method ending...(GET)");
    return userTimeZoneOffset;
  }

  /**
   * Retrieves country ISD code
   * 
   * @param countyCode
   * @return
   */
  @RequestMapping(value = "/ISD_code_by_country_code", method = RequestMethod.GET)
  @ResponseBody
  public String getISDCodeByCounty(@RequestParam(value = "countyCode", required = true) String countyCode) {
    Country country = countryService.locateCountryByCode(countyCode);
    return country.getIsdCode();
  }

  private boolean areDigitsInPhoneNosEqual(String phoneNo1, String phoneNo2) {
    if (phoneNo1.replaceAll(PHONE_NUMBER_REGEX, "").equals(phoneNo2.replaceAll(PHONE_NUMBER_REGEX, ""))) {
      return true;
    }
    return false;
  }

  /**
   * @param userName
   * @return
   */
  @RequestMapping(value = {
    "importfromad.json"
  }, method = RequestMethod.GET)
  @ResponseBody
  public Map<String, String> getAdUserInfo(@RequestParam(value = "username", required = true) String userName) {
    logger.debug("### getAdUserInfo method starting...(GET)");
    HashMap<String, String> adUserInfo = null;
    try {
      adUserInfo = userService.locateUserInAd(userName, true);
    } catch (NoSuchUserException e) {
      throw new InvalidAjaxRequestException(e.getMessage());
    }
    logger.debug("### getAdUserInfo method ending...(GET)");
    return adUserInfo;
  }

  /**
   * Retrieves the view Object for user settings
   * 
   * @param currentTenant
   * @param instanceUuid
   * @return
   */
  @RequestMapping(value = "/resolve_view_for_Settings", method = RequestMethod.GET)
  @ResponseBody
  public View resolveViewForSettingFromServiceInstance(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "instanceUuid", required = true) String instanceUuid) {
    View returnView = null;
    logger.debug("Enter.. resolveViewForSettingFromServiceInstance::" + instanceUuid);
    CloudConnector connector = (CloudConnector) connectorManagementService.getServiceInstance(instanceUuid);
    if (connector != null) {
      logger.debug("Found Connector for " + instanceUuid);
      User user = getCurrentUser();
      ViewResolver viewResolver = connector.getViewResolver();
      if (viewResolver != null) {
        returnView = viewResolver.resolveUserSettingsView(user);
      }
    } else {
      logger.error("Error: not Connector found for " + instanceUuid);
    }
    logger.debug("Exit.. resolveViewForSettingFromServiceInstance:::" + returnView);
    return returnView;
  }

  /**
   * Retrieves the view Object for Account settings
   * 
   * @param tenantParam
   * @param instanceUuid
   * @return
   */
  @RequestMapping(value = "/resolve_view_for_account_settings", method = RequestMethod.GET)
  @ResponseBody
  public View resolveViewForAccountSettingFromServiceInstance(
      @RequestParam(value = "tenantParam", required = true) String tenantParam,
      @RequestParam(value = "instanceUuid", required = true) String instanceUuid) {
    return resolveViewForAccountSettingsFromServiceInstance(tenantParam, instanceUuid);
  }

  @ResponseBody
  @RequestMapping(value = "/enable_services", method = RequestMethod.POST)
  public Map<String, String> enableServices(@RequestParam(value = "tenantparam", required = true) String tenantParam,
      @RequestParam(value = "userparam", required = true) String userParam,
      @RequestParam(value = "instances", required = true) String instances) {

    Map<String, String> responseMap = new HashMap<String, String>();
    User user = userService.get(userParam);

    for (ServiceInstance serviceInstance : tenantService.getEnabledCSInstances(user.getTenant())) {
      if (StringUtils.contains(instances, serviceInstance.getUuid())) {
        userService.enableServices(user, serviceInstance);
      }
    }
    return responseMap;
  }

  @ResponseBody
  @RequestMapping(value = "/enabled_services", method = RequestMethod.POST)
  public Map<String, Object> getServiceProvisioningStatus(
      @RequestParam(value = "tenantparam", required = true) String tenantParam,
      @RequestParam(value = "userparam", required = true) String userParam,
      @RequestParam(value = "instances", required = true) String instances) {

    Map<String, Object> responseMap = new HashMap<String, Object>();
    User user = userService.get(userParam);
    for (ServiceInstance serviceInstance : tenantService.getEnabledCSInstances(user.getTenant())) {

      if (StringUtils.contains(instances, serviceInstance.getUuid())) {
        UserHandle handle = userService.getLatestUserHandle(user.getUuid(), serviceInstance.getUuid());
        Map<String, Object> handleObject = new HashMap<String, Object>();
        String state = handle != null ? handle.getState().name() : com.vmops.model.UserHandle.State.PROVISIONING.name();
        handleObject.put("state", state);
        if (handle != null && !handle.getState().equals(com.vmops.model.UserHandle.State.ACTIVE)) {
          handleObject.put("data", handle.getData());
        }
        responseMap.put(serviceInstance.getUuid(), handleObject);
      }
    }
    return responseMap;
  }

}
