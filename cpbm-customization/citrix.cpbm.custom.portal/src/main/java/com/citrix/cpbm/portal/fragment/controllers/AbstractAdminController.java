/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.citrix.cpbm.portal.forms.AjaxResponse;
import com.citrix.cpbm.portal.forms.AjaxResponse.Status;
import com.vmops.internal.service.EmailService;
import com.vmops.internal.service.EventService;
import com.vmops.model.AccountControlServiceConfigMetadata;
import com.vmops.model.AccountType;
import com.vmops.model.AccountType.SpendBreachAction;
import com.vmops.model.AccountTypeCreditExposure;
import com.vmops.model.BaseServiceConfigurationMetadata;
import com.vmops.model.ComponentType;
import com.vmops.model.Configuration;
import com.vmops.model.EmailTemplates;
import com.vmops.model.EmailTemplates.Category;
import com.vmops.model.JobStatus;
import com.vmops.model.ModuleType;
import com.vmops.model.Service;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceInstanceConfig;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.AccountTypeService;
import com.vmops.service.ChannelService;
import com.vmops.service.ConfigurationService;
import com.vmops.service.CurrencyValueService;
import com.vmops.service.EmailTemplateService;
import com.vmops.service.JobManagementService;
import com.vmops.service.exceptions.AjaxFormValidationException;
import com.vmops.service.exceptions.DuplicateValueForUniqueServiceConfigException;
import com.vmops.service.exceptions.HtmlNotSanitizedException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.ServiceException;
import com.vmops.utils.CryptoUtils;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Level3;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.AccountTypeForm;
import com.vmops.web.validators.AccountTypesValidator;
import com.vmops.web.validators.ValidationUtil;

public abstract class AbstractAdminController extends AbstractAuthenticatedController {

  /**
   * Configuration service
   */
// CHECKSTYLE:OFF
  @Autowired
  protected ConfigurationService configurationService;

  @Autowired
  protected JobManagementService jobManagementService;

  @Autowired
  protected AccountTypeService accountTypeService;

  @Autowired
  protected EmailTemplateService emailTemplateService;

  @Autowired
  protected CurrencyValueService currencyValueService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  protected EventService eventService;

  @Resource(name = "messageSource")
  protected MessageSource messageSource;

  @Autowired
  protected ConnectorConfigurationManager connectorConfigurationManager;

  @Autowired
  protected ConnectorManagementService connectorManagementService;

  @Autowired
  protected EmailService emailService;

// CHECKSTYLE:ON
  /**
   * * List all the configurations
   * 
   * @param module
   * @param component
   * @param map
   * @return
   */
  @RequestMapping(value = "/config/show_configuration", method = RequestMethod.GET)
  public String showConfigurations(@RequestParam(value = "module", required = false) String module,
      @RequestParam(value = "component", required = false) String component, ModelMap map) {

    logger.debug("### In showConfigurations()  start method...");

    String moduleName = ModuleType.AccountManagement.getModuleName();
    String labelCode = "";
    if (module == null || module.equals(Level3.ConfigAccountManagement.getName())) {
      moduleName = ModuleType.AccountManagement.getModuleName();
      labelCode = Level3.ConfigAccountManagement.getCode();
      setPage(map, Page.CONFIG_ACCOUNT_MANAGEMENT);
    } else if (module.equals(Level3.CRM.getName())) {
      moduleName = ModuleType.CRM.getModuleName();
      labelCode = Level3.CRM.getCode();
      setPage(map, Page.CONFIG_CRM);
    } else if (module.equals(Level3.Integration.getName())) {
      moduleName = ModuleType.Integration.getModuleName();
      labelCode = Level3.Integration.getCode();
      setPage(map, Page.CONFIG_INTEGRATION);
    } else if (module.equals(Level3.Portal.getName())) {
      moduleName = ModuleType.Portal.getModuleName();
      labelCode = Level3.Portal.getCode();
      setPage(map, Page.CONFIG_PORTAL);
    } else if (module.equals(Level3.Reports.getName())) {
      moduleName = ModuleType.Reports.getModuleName();
      labelCode = Level3.Reports.getCode();
      setPage(map, Page.CONFIG_REPORTS);
    } else if (module.equals(Level3.Server.getName())) {
      moduleName = ModuleType.Server.getModuleName();
      labelCode = Level3.Server.getCode();
      setPage(map, Page.CONFIG_SERVER);
    } else if (module.equals(Level3.TrialManagement.getName())) {
      moduleName = ModuleType.TrialManagement.getModuleName();
      labelCode = Level3.TrialManagement.getCode();
      setPage(map, Page.CONFIG_TRIAL_MANAGEMENT);
    }
    if (module == null) {
      module = "ConfigAccountManagement";
    }
    map.addAttribute("labelCode", labelCode);
    map.addAttribute("module", module);
    map.addAttribute("moduleName", moduleName);
    map.addAttribute("tenant", getCurrentUser().getTenant());

    if (component != null) {
      map.addAttribute("component", component);

      List<Configuration> configList = configurationService.getConfiguration(moduleName, component);
      List<Configuration> copiedConfigList = new ArrayList<Configuration>();
      for (Configuration config : configList) {
        Configuration copiedConfig = config.copyThisConfiguration();
        if (config.isCurrentlyEncrypted()) {
          copiedConfig.setValue(CryptoUtils.decrypt(config.getValue(), CryptoUtils.keyGenerationSeed));
        }
        copiedConfigList.add(copiedConfig);
      }

      map.addAttribute("configurationList", copiedConfigList);
      com.vmops.web.forms.ConfigurationForm configurationForm = new com.vmops.web.forms.ConfigurationForm(
          copiedConfigList);
      map.addAttribute("configurationForm", configurationForm);
      return "configuration.edit";
    }

    map.addAttribute("componentTypes", filterComponetTypes(moduleName));
    map.addAttribute("is_enabled_map", enabledServiceConfigMap(moduleName));

    logger.debug("### In showConfigurations()  end");
    return "config.list";
  }

  /**
   * Edit configuration parameter
   * 
   * @param id
   * @param value
   * @param map
   * @return
   */
  @RequestMapping(value = {
    "/config/edit"
  }, method = RequestMethod.POST)
  @ResponseBody
  public AjaxResponse editConfiguration(
      @ModelAttribute("configurationForm") com.vmops.web.forms.ConfigurationForm configurationForm,
      BindingResult result, ModelMap map, HttpServletResponse response, HttpServletRequest request) {
    logger.debug("### In editConfiguration()  start method...");
    setPage(map, Page.ADMIN);
    List<Configuration> configurations = configurationForm.getConfigurations();
    configurationService.validateAndUpdateConfiguration(configurations, result);
    if (result.hasErrors()) {
      throw new AjaxFormValidationException(result);
    }

    return new AjaxResponse(Status.SUCCESS, "");
  }

  /**
   * List Email Templates
   * 
   * @param channelId
   * @return
   */
  @RequestMapping(value = "/email_templates", method = RequestMethod.GET)
  public String listEmailTemplates(@RequestParam(value = "filterby", required = false) String filterBy,
      @RequestParam(value = "currentpage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size,
      @RequestParam(value = "userlang", required = false, defaultValue = "en_US") String locale,
      @RequestParam(value = "channelId", required = false) Long channelId, HttpServletRequest request, ModelMap map) {
    logger.debug("### In listEmailTemplates()  start method...");
    setPage(map, Page.ADMIN_EMAIL_TEMPLATES);
    int defaultPerPage = getDefaultPageSize();
    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    int sizeInt = 0;
    if (size == null || size.equals("")) {
      sizeInt = emailTemplateService.getAllTemplatesByLocale(filterBy, 0, 0, locale, channelId).size();
    } else {
      sizeInt = Integer.parseInt(size);
    }
    List<EmailTemplates> emailTemplates = emailTemplateService.getAllTemplatesByLocale(filterBy, currentPageValue,
        defaultPerPage, locale, channelId);
    setPaginationValues(map, defaultPerPage, currentPageValue, sizeInt, null);
    map.addAttribute("templates", emailTemplates);
    Map<String, String> filtersMap = new LinkedHashMap<String, String>();
    for (Category category : Category.values()) {
      filtersMap.put("" + category.ordinal(), messageSource.getMessage(
          ("ui.email.templates.type." + category.getName()).toLowerCase(), null, getSessionLocale(request)));
    }
    map.addAttribute("filtersMap", filtersMap);
    map.addAttribute("supportedLocaleList", this.getLocaleDisplayName(listSupportedLocales()));
    map.addAttribute("channels", channelService.getChannels(null, null, null));
    if (filterBy == null) {
      filterBy = "0";
    }
    map.addAttribute("filterBy", filterBy);
    map.addAttribute("selectedLanguage", locale);
    map.addAttribute("selectedChannel", channelId);
    logger.debug("### In editEmailTemplates()  end");
    return "email.templates";

  }

  /**
   * Send Test Mail
   * 
   * @param channelId
   * @return
   */
  @RequestMapping(value = "/email_template/send", method = RequestMethod.POST)
  @ResponseBody
  public String sendEmailTemplate(@RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "emailid", required = true) String emailId, HttpServletResponse response,
      @RequestParam(value = "userlang", required = false, defaultValue = "en_US") String locale,
      @RequestParam(value = "channelId", required = false) String channelId, HttpServletRequest request, ModelMap map) {
    logger.debug("### In sendEmailTemplate()  start method...");

    // firing an event through service layer
    emailTemplateService.sendTestEmail(name, emailId, getCurrentUser(), locale, channelId);
    logger.debug("### In sendEmailTemplate()  end");
    return "success";

  }

  /**
   * View Email Template
   * 
   * @param channelId
   * @return
   */
  @RequestMapping(value = "/email_template/view", method = RequestMethod.GET)
  public String viewEmailTemplate(@RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "userlang", required = false, defaultValue = "en_US") String locale,
      @RequestParam(value = "channelId", required = false) Long channelId, HttpServletRequest request, ModelMap map) {
    logger.debug("### In viewEmailTemplate()  start method...");

    String emailText = null;
    EmailTemplates emailTemplate = emailTemplateService.getEmailTemplateByLocale(name, locale, channelId);
    Map<String, Object> model = new HashMap<String, Object>();
    map.addAttribute("templateName", name);
    map.addAttribute("resetToDefaultAvailable", emailTemplate.getChannel() == null ? false : true);
    map.addAttribute("template", emailTemplate);
    map.addAttribute("EmailId", getCurrentUser().getEmail());
    map.addAttribute("selectedLanguage", locale);
    map.addAttribute("selectedChannel", channelId);

    try {
      emailText = emailTemplateService.getEmailTemplateAsStringByLocale(name, model, locale, channelId);
      map.addAttribute("parseError", false);
    } catch (Exception e) {
      emailText = emailTemplate.getTemplateText();
      map.addAttribute("parseError", true);
    }

    map.addAttribute("emailText", emailText);

    logger.debug("### In viewEmailTemplate()  end");
    return "emailtemplate.view";

  }

  /**
   * Edit Email Template
   * 
   * @param channelId
   * @return
   */
  @RequestMapping(value = "/email_template/edit", method = RequestMethod.GET)
  public String editEmailTemplate(@RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "userlang", required = false, defaultValue = "en_US") String locale,
      @RequestParam(value = "channelId", required = false) Long channelId, ModelMap map) {
    logger.debug("### In editEmailTemplate()  start method...");
    EmailTemplates emailTemplate = emailTemplateService.getEmailTemplateByLocale(name, locale, channelId);
    map.addAttribute("templateName", name);
    map.addAttribute("emailText", emailTemplate.getTemplateText());
    logger.debug("### In editEmailTemplate()  end");
    return "emailtemplate.edit";
  }

  /**
   * Update Email Template
   * 
   * @param channelId
   * @return
   */
  @RequestMapping(value = "/email_template/update", method = RequestMethod.POST)
  public ModelMap updateEmailTemplate(@RequestParam(value = "name", required = true) String name,
      @RequestParam(value = "userlang", required = false, defaultValue = "en_US") String userLang,
      @RequestParam(value = "updatedtext", required = true) String emailText,
      @RequestParam(value = "channelId", required = false) Long channelId, HttpServletRequest request, ModelMap map) {
    logger.debug("### In updateEmailTemplate()  start method...");
    try {
      emailTemplateService.updateTemplate(name, emailText, getSessionLocale(request), userLang, channelId);
    } catch (HtmlNotSanitizedException e) {
      logger.error("### HTML is not safe. Aborting SAVE!");
      throw e;
    }
    EmailTemplates emailTemplate = emailTemplateService.getEmailTemplateByLocale(name, userLang, channelId);
    map.addAttribute("templateName", emailTemplate.getTemplateName());
    Map<String, Object> model = new HashMap<String, Object>();
    try {
      // checking whether the updated email's text is parsable or not.
      map.addAttribute("emailText",
          emailTemplateService.getEmailTemplateAsStringByLocale(name, model, userLang, channelId));
      map.addAttribute("parseError", false);
    } catch (Exception e) {
      map.addAttribute("emailText", emailText);
      map.addAttribute("parseError", true);
    }
    map.addAttribute("lastUpdatedAt", emailTemplate.getLastUpdatedAt().getTime());
    logger.debug("### In updateEmailTemplate()  end");
    return map;
  }

  @RequestMapping(value = "/email_template/{emailTemplateId}/reset_to_default", method = RequestMethod.POST)
  @ResponseBody
  public String reseToDefault(@PathVariable String emailTemplateId) {
    emailTemplateService.resetToDefaultEmailTemplate(emailTemplateId);
    return "success";
  }

  @RequestMapping(value = "/batch/status", method = RequestMethod.GET)
  public String showBatchStatus(
      @RequestParam(value = "currentpage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size, ModelMap map) {
    logger.debug("### In showBatchStatus()  start method...");
    setPage(map, Page.ADMIN_BATCH_JOBS);

    int currentPageValue = Integer.parseInt(currentPage);
    int perPageValue = getDefaultPageSize();
    int sizeInt = 0;

    if (size == null || size.equals("")) {
      sizeInt = jobManagementService.getJobStatus(0, 0).size();

    } else {
      sizeInt = Integer.parseInt(size);
    }

    List<JobStatus> batchList = jobManagementService.getJobStatus(currentPageValue, perPageValue);
    map.addAttribute("batchList", batchList);

    setPaginationValues(map, perPageValue, currentPageValue, sizeInt, null);

    logger.debug("### In showBatchStatus()  end");
    return "batch.list";
  }

  @RequestMapping(value = ("/view_batch_job"), method = RequestMethod.GET)
  public String viewBatchJob(@RequestParam(value = "id", required = true) String id, ModelMap map) {
    logger.debug("### viewBatchJob method starting...(GET)");
    JobStatus jobStatus = jobManagementService.locateJobById(id);
    map.addAttribute("jobStatus", jobStatus);
    logger.debug("### viewBatchJob method end");
    return "batch.view";
  }

  @RequestMapping(value = "/account_types/list", method = RequestMethod.GET)
  public String listAccountTypes(ModelMap map) {
    logger.debug("### In listAccountTypes()  start method...");
    setPage(map, Page.ADMIN_ACCOUNT_TYPES);
    List<AccountType> accountTypesList = accountTypeService.getAccountTypes();
    map.addAttribute("accountTypesList", accountTypesList);
    logger.debug("### In listAccountTypes()  end");
    return "accounttypes.list";
  }

  @RequestMapping(value = ("/view_account_type"), method = RequestMethod.GET)
  public String viewAccounttype(@RequestParam(value = "id", required = true) String id,
      @RequestParam(value = "tab", defaultValue = "1") String tab, ModelMap map) {
    logger.debug("### viewAccounttype method starting...(GET)");
    AccountType accountType = accountTypeService.locateAccountTypeById(id);
    accountTypeService.getAccountTypeCreditExposureList(accountType, getCurrentUser());
    map.addAttribute("tab", tab);
    map.addAttribute("accounttype", accountType);

    List<ServiceInstance> cloudTypeServiceInstances = connectorManagementService.getCloudTypeServiceInstances();
    Map<String, List<ServiceInstanceConfig>> mapOfControlsPerInstance = new LinkedHashMap<String, List<ServiceInstanceConfig>>();
    Map<String, Integer> mapOfControlMetaDataPerInstance = new LinkedHashMap<String, Integer>();
    Map<String, List<AccountControlServiceConfigMetadata>> initialMapOfControlsPerInstance = new HashMap<String, List<AccountControlServiceConfigMetadata>>();

    for (ServiceInstance serviceInstance : cloudTypeServiceInstances) {
      List<ServiceInstanceConfig> instanceConfigurationList = connectorConfigurationManager.getInstanceConfiguration(
          serviceInstance, accountType);
      Collections.sort(instanceConfigurationList);

      // Initially instance would not have any service control to show. In that case just retrieve the name of controls
      // from service, to keep UI proper
      // For all instances service controls will be same. Hence no need to save name of service controls per instance
      if (instanceConfigurationList.isEmpty()) {
        Set<AccountControlServiceConfigMetadata> properties = serviceInstance.getService()
            .getAccountControlServiceConfigMetadata();
        List<AccountControlServiceConfigMetadata> sortedProperties = new ArrayList<AccountControlServiceConfigMetadata>(
            properties);
        Collections.sort(sortedProperties);
        initialMapOfControlsPerInstance.put(serviceInstance.getUuid(), sortedProperties);
      }

      mapOfControlsPerInstance.put(serviceInstance.getUuid(), instanceConfigurationList);
      mapOfControlMetaDataPerInstance.put(serviceInstance.getUuid(), serviceInstance.getService()
          .getAccountControlServiceConfigMetadata() != null ? serviceInstance.getService()
          .getAccountControlServiceConfigMetadata().size() : 0);
    }

    map.addAttribute("mapOfControlsPerInstance", mapOfControlsPerInstance);
    map.addAttribute("mapOfControlMetaDataPerInstance", mapOfControlMetaDataPerInstance);
    map.addAttribute("initialMapForView", initialMapOfControlsPerInstance);
    map.addAttribute("instances", cloudTypeServiceInstances);
    map.addAttribute("services", this.getServices(cloudTypeServiceInstances));

    logger.debug("### viewAccounttype method end");
    return "accounttype.view";
  }

  @RequestMapping(value = "/edit_account_type", method = RequestMethod.GET)
  public String editAccountType(@RequestParam(value = "id", required = true) String id,
      @RequestParam(value = "mode", defaultValue = "1") String mode, ModelMap map) {
    logger.debug("### editAccountType method starting...(GET)");
    AccountType accountType = accountTypeService.locateAccountTypeById(id);
    AccountTypeForm accountTypeForm = new AccountTypeForm(accountType);
    map.addAttribute("accountTypeForm", accountTypeForm);
    List<String> spendBreachActions = new ArrayList<String>();
    for (SpendBreachAction sba : SpendBreachAction.values()) {
      if (!("8".equals(accountType.getPaymentModes().toString()) && sba.equals(SpendBreachAction.AUTO_PAY))) {
        spendBreachActions.add(sba.toString());
      }
    }
    map.addAttribute("creditBreachActions", spendBreachActions);
    map.addAttribute("mode", mode);
    logger.debug("### editAccountType method end");
    return "accounttype.edit";
  }

  /**
   * Method to add/edit control values from AccountTypes Tab for each account
   * 
   * @param id ServiceInstance Id
   * @param accountTypeId
   * @param map
   * @return
   */
  @RequestMapping(value = "/add_account_type_controls", method = RequestMethod.GET)
  public String addAccountTypeControls(@RequestParam(value = "id", defaultValue = "1") String id,
      @RequestParam(value = "accounttype", defaultValue = "1") String accountTypeId, ModelMap map) {

    logger.debug("### addAccountTypeControls method starting...(GET)");

    ServiceInstance instance = connectorConfigurationManager.getInstance(id);
    AccountType accountType = accountTypeService.locateAccountTypeById(accountTypeId);
    List<ServiceInstanceConfig> instanceProperties = connectorConfigurationManager.getInstanceConfiguration(instance,
        accountType);
    if (CollectionUtils.isEmpty(instanceProperties)) {
      Set<AccountControlServiceConfigMetadata> properties = instance.getService()
          .getAccountControlServiceConfigMetadata();
      List<BaseServiceConfigurationMetadata> sortedProperties = new ArrayList<BaseServiceConfigurationMetadata>(
          properties);
      Collections.sort(sortedProperties);
      map.addAttribute("account_control_add_properties", sortedProperties);
    } else {
      Collections.sort(instanceProperties);
      map.addAttribute("account_control_edit_properties", instanceProperties);
    }
    map.addAttribute("accountTypeId", accountTypeId);
    map.addAttribute("instance", instance);
    map.addAttribute("service", instance.getService());

    logger.debug("### addAccountType method end");
    return "accounttypecontrols.edit";
  }

  @RequestMapping(value = "/save_account_type_controls", method = RequestMethod.POST)
  public Map<String, String> persistAccountTypeControls(@RequestParam(value = "id") String id,
      @RequestParam(value = "accounttypeid") String accountTypeId, @RequestParam(value = "action") String action,
      @RequestParam("configproperties") final String configProperties, HttpServletRequest request) {

    boolean add = true;
    ServiceInstance instance = connectorConfigurationManager.getInstance(id);
    Service service = instance.getService();
    AccountType accountType = accountTypeService.locateAccountTypeById(accountTypeId);
    Map<String, String> map = new HashMap<String, String>();
    Map<String, ServiceInstanceConfig> mapOfFieldNameVsInstanceConfig = new HashMap<String, ServiceInstanceConfig>();
    Map<String, AccountControlServiceConfigMetadata> mapOfServiceMetaData = new HashMap<String, AccountControlServiceConfigMetadata>();

    try {

      if ("update".equals(action)) {
        add = false;
        List<ServiceInstanceConfig> instanceConfigList = connectorConfigurationManager.getInstanceConfiguration(
            instance, accountType);
        for (ServiceInstanceConfig instanceConfig : instanceConfigList) {
          // Get only AccountControl Config
          mapOfFieldNameVsInstanceConfig.put(instanceConfig.getName(), instanceConfig);
        }
      }

      Set<AccountControlServiceConfigMetadata> properties = service.getAccountControlServiceConfigMetadata();
      for (AccountControlServiceConfigMetadata serviceConfigurationMetadata : properties) {
        mapOfServiceMetaData.put(serviceConfigurationMetadata.getName(), serviceConfigurationMetadata);
      }

      JSONArray jsonArray = new JSONArray(configProperties);
      for (int index = 0; index < jsonArray.length(); index++) {
        JSONObject jsonObj = jsonArray.getJSONObject(index);
        String fieldName = jsonObj.get("name").toString();
        String fieldValue = jsonObj.get("value").toString();

        AccountControlServiceConfigMetadata configurationMetadata = mapOfServiceMetaData.get(fieldName);
        map.put("validationResult", CssdkConstants.SUCCESS);
        String validationJson = configurationMetadata.getValidation();

        String validationResult = ValidationUtil.valid(validationJson, fieldName, fieldValue, messageSource,
            getSessionLocale(request));
        if (!CssdkConstants.SUCCESS.equals(validationResult)) {
          map.put("validationResult", validationResult);
          return map;
        }

        if (add) {
          ServiceInstanceConfig instanceConfiguration = new ServiceInstanceConfig();
          instanceConfiguration.setService(service);
          instanceConfiguration.setServiceConfigMetadata(configurationMetadata);
          instanceConfiguration.setServiceInstance(instance);
          instanceConfiguration.setName(fieldName);
          instanceConfiguration.setValue(fieldValue);
          instanceConfiguration.setServiceInstanceConfigurer(accountType);
          instance.getServiceInstanceConfig().add(instanceConfiguration);
        } else {
          mapOfFieldNameVsInstanceConfig.get(fieldName).setValue(fieldValue);
        }
      }

      try {
        connectorManagementService.addUpdateAccountControlsForInstance(accountType, instance, add, map);
      } catch (DuplicateValueForUniqueServiceConfigException e) {
        map.put("result", CssdkConstants.FAILURE);
        map.put("message", e.getErrorMessage());
        return map;
      }

      map.put("result", CssdkConstants.SUCCESS);
    } catch (Exception e) {
      logger.error("Exception..", e);
      map.put("result", CssdkConstants.FAILURE);
      map.put("message", this.messageSource.getMessage("connector.instance.add.failure", null, null) + e.getMessage());
    }

    return map;
  }

  @RequestMapping(value = ("/edit_account_type"), method = RequestMethod.POST)
  @ResponseBody
  public AccountType edit(@ModelAttribute("accountTypeForm") AccountTypeForm accountTypeForm, BindingResult result,
      ModelMap map) {
    logger.debug("### edit method starting...(POST)");
    AccountTypesValidator validator = new AccountTypesValidator();
    validator.validate(accountTypeForm, result);
    if (result.hasErrors()) {
      String message = messageSource.getMessage("js.errors.accounttype.valueerror", null, null, getCurrentUser()
          .getLocale());
      throw new InvalidAjaxRequestException(message);
    }
    AccountType accountType = accountTypeForm.getAccountType();

    try {
      accountTypeService.validateUserLimit(accountType);
      accountTypeService.update(accountType);
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }
    return accountType;
  }

  /**
   * Edit account type credit exposure limits for multiple currencies(GET).
   * 
   * @param accountTypeId string
   * @param map ModelMap
   * @return String
   */
  @RequestMapping(value = "/edit_initial_deposit", method = RequestMethod.GET)
  public String editInitialDeposit(@RequestParam(value = "id", required = true) String accountTypeId, ModelMap map) {
    logger.debug("### editInitialDeposit method starting...(GET)");
    AccountType accountType = accountTypeService.locateAccountTypeById(accountTypeId);
    AccountTypeForm accountTypeForm = new AccountTypeForm(accountType);
    map.addAttribute("accountTypeForm", accountTypeForm);
    logger.debug("### editInitialDeposit GET method end");
    return "initialdeposit.edit";
  }

  /**
   * Edit account type credit exposure limits for multiple currencies(Post).
   * 
   * @param accountTypeForm AccountTypeForm
   * @param result BindingResult
   * @param map ModelMap
   * @return String
   */

  @RequestMapping(value = ("/edit_initial_deposit"), method = RequestMethod.POST)
  @ResponseBody
  public String editInitialDeposit(@ModelAttribute("accountTypeForm") AccountTypeForm accountTypeForm,
      BindingResult result, ModelMap map) {
    logger.debug("### editInitialDeposit method starting...(POST)");
    // Doing server side validation.
    if (accountTypeForm.getAccountType().getAccountTypeCreditExposureList() != null
        && accountTypeForm.getAccountType().getAccountTypeCreditExposureList().size() > 0) {
      for (AccountTypeCreditExposure creditExposure : accountTypeForm.getAccountType()
          .getAccountTypeCreditExposureList()) {
        if (creditExposure.getInitialDeposit() == null || "".equals(creditExposure.getInitialDeposit())) {
          continue;
        }
        creditExposure.setInitialDeposit(creditExposure.getInitialDeposit().setScale(
            Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
            RoundingMode.HALF_UP));
        AccountTypesValidator validator = new AccountTypesValidator();
        if (!validator.allowFloat(creditExposure.getInitialDeposit().toString())) {
          throw new AjaxFormValidationException(result);
        }
      }
    }
    AccountType accountType = accountTypeForm.getAccountType();
    try {
      accountTypeService.update(accountType);
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }
    logger.debug("### editInitialDeposit POST method end");
    return "success";
  }

  /**
   * Edit account type credit exposure limits for multiple currencies(GET).
   * 
   * @param accountTypeId string
   * @param map ModelMap
   * @return String
   */
  @RequestMapping(value = "/edit_credit_exposure", method = RequestMethod.GET)
  public String editCreditExposure(@RequestParam(value = "id", required = true) String accountTypeId, ModelMap map) {
    logger.debug("### editCreditExposure method starting...(GET)");
    AccountType accountType = accountTypeService.locateAccountTypeById(accountTypeId);
    AccountTypeForm accountTypeForm = new AccountTypeForm(accountType);
    map.addAttribute("accountTypeForm", accountTypeForm);
    logger.debug("### editCreditExposure method end");
    return "creditexposure.edit";
  }

  /**
   * Edit account type credit exposure limits for multiple currencies(Post).
   * 
   * @param accountTypeForm AccountTypeForm
   * @param result BindingResult
   * @param map ModelMap
   * @return String
   */

  @RequestMapping(value = ("/edit_credit_exposure"), method = RequestMethod.POST)
  @ResponseBody
  public String editcreditexposure(@ModelAttribute("accountTypeForm") AccountTypeForm accountTypeForm,
      BindingResult result, ModelMap map) {
    logger.debug("### editcreditexposure method starting...(POST)");
    // Doing server side validation.
    if (accountTypeForm.getAccountType().getAccountTypeCreditExposureList() != null
        && accountTypeForm.getAccountType().getAccountTypeCreditExposureList().size() > 0) {
      for (AccountTypeCreditExposure creditExposure : accountTypeForm.getAccountType()
          .getAccountTypeCreditExposureList()) {
        if (creditExposure.getCreditExposureLimit() == null || "".equals(creditExposure.getCreditExposureLimit())) {
          continue;
        }
        creditExposure.setCreditExposureLimit(creditExposure.getCreditExposureLimit().setScale(
            Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
            RoundingMode.HALF_UP));
        AccountTypesValidator validator = new AccountTypesValidator();
        if (!validator.allowFloat(creditExposure.getCreditExposureLimit().toString())
            || !validator.allowNegative(creditExposure.getCreditExposureLimit().toString())) {
          throw new AjaxFormValidationException(result);
        }
      }
    }
    AccountType accountType = accountTypeForm.getAccountType();
    try {
      accountTypeService.update(accountType);
    } catch (ServiceException ex) {
      throw new InvalidAjaxRequestException(ex.getMessage());
    }
    return "success";
  }

  /**
   * Filters component types based on module
   * 
   * @param moduleName
   * @return
   */
  private Set<ComponentType> filterComponetTypes(String moduleName) {
    List<Configuration> configurationList = configurationService.getConfiguration(moduleName,
        ComponentType.ALL.getComponentName());
    Set<String> exclusionList = new HashSet<String>();
    if ("Integration".equals(moduleName)) {
      exclusionList.add(ComponentType.DEVICE_IDENTITY.getComponentName());
    }
    Set<ComponentType> componentTypes = new LinkedHashSet<ComponentType>();
    for (Configuration config : configurationList) {
      for (ComponentType ctype : ComponentType.values()) {
        if (config.getComponent().equals(ctype.getComponentName()) && !exclusionList.contains(ctype.getComponentName())) {
          componentTypes.add(ctype);
          continue;
        }
      }
    }
    return componentTypes;
  }

  private Map<String, Object> enabledServiceConfigMap(String moduleName) {
    logger.debug("### getting the enabled settings for configs");
    List<Configuration> configurationList = configurationService.getConfiguration(moduleName,
        ComponentType.ALL.getComponentName());
    HashMap<String, Object> map = new HashMap<String, Object>();

    for (Configuration config : configurationList) {
      String enableServiceConfig = (moduleName + "." + config.getComponent().replace(" ", "")).toLowerCase()
          + ".enabled";
      if (config.getName().equals(enableServiceConfig)) {
        map.put(enableServiceConfig, config.copyThisConfiguration());
      }
    }
    logger.debug("### Exiting getting the enabled settings for configs");
    return map;
  }

  private Set<Service> getServices(List<ServiceInstance> instances) {
    Set<Service> services = new HashSet<Service>();
    for (ServiceInstance instance : instances) {
      services.add(instance.getService());
    }
    return services;
  }
}
