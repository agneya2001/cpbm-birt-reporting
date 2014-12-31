/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.admin.service.utils.BootstrapServiceXmlBean.Roles;
import com.citrix.cpbm.platform.admin.service.utils.BootstrapServiceXmlBean.Roles.Role;
import com.citrix.cpbm.platform.admin.service.utils.ServiceResponse;
import com.citrix.cpbm.platform.admin.service.utils.ServiceResponse.Status;
import com.citrix.cpbm.platform.spi.BaseConnector;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory;
import com.citrix.cpbm.platform.spi.CloudConnectorFactory.ConnectorType;
import com.citrix.cpbm.platform.spi.View;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.vmops.model.AccountConfigurationServiceConfigMetadata;
import com.vmops.model.BaseServiceConfigurationMetadata;
import com.vmops.model.Category;
import com.vmops.model.CurrencyValue;
import com.vmops.model.JsonBean;
import com.vmops.model.MediationRule;
import com.vmops.model.Product;
import com.vmops.model.ProductCharge;
import com.vmops.model.Profile;
import com.vmops.model.Revision;
import com.vmops.model.SecurityContextScope;
import com.vmops.model.SecurityContextScope.Scope;
import com.vmops.model.Service;
import com.vmops.model.ServiceConfigurationMetadata;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceInstanceConfig;
import com.vmops.model.ServiceInstanceTenantConfiguration;
import com.vmops.model.ServiceUsageType;
import com.vmops.model.ServiceUsageTypeUomScale;
import com.vmops.model.Tenant;
import com.vmops.model.TenantHandle;
import com.vmops.model.User;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.CurrencyValueService;
import com.vmops.service.ProfileService;
import com.vmops.service.TenantService;
import com.vmops.service.UserService.Handle;
import com.vmops.utils.JSONUtils;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.ServiceInstanceLogoForm;
import com.vmops.web.validators.ServiceInstanceLogoFormValidator;

public abstract class AbstractConnectorController extends AbstractAuthenticatedController {

// CHECKSTYLE:OFF
  @Autowired
  protected ConnectorManagementService connectorManagementService;

  @Autowired
  protected ConnectorConfigurationManager connectorConfigurationManager;

  @Autowired
  protected ProfileService profileService;

  @Autowired
  protected CurrencyValueService currencyValueService;

// CHECKSTYLE:ON

  private static String tncString = "";

  @PostConstruct
  public void getTermsAndConditions() {
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
      tncString = sb.toString();
    } catch (Exception e) {
      logger.debug("Failed to read Terms and conditions file" + e.getMessage());
    }
  }

  @RequestMapping(value = "/cs", method = RequestMethod.GET)
  public String showCloudServices(@RequestParam(value = "id", required = false) String serviceUuid,
      @RequestParam(value = "instanceId", required = false) String instanceUuId,
      @RequestParam(value = "action", required = false) String action, ModelMap map) {
    return getView(serviceUuid, instanceUuId, action, map, false);
  }

  @RequestMapping(value = "/csinstances", method = RequestMethod.GET)
  public String showCloudServices(@RequestParam(value = "tenant", required = false) String tenantUuid,
      @RequestParam(value = "showIframe", required = false) String showIframe,
      @RequestParam(value = "serviceInstanceUUID", required = false) String serviceInstanceUUID, ModelMap map,
      HttpServletRequest request) {
    Tenant tenant = tenantService.get(tenantUuid);
    if (tenant.getAccountId().equals(TenantService.SYSTEM_TENANT)) {
      return "redirect:/portal/home";
    }
    map.addAttribute("tenant", tenant);
    String uiView = null;
    User user = getCurrentUser();
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      setPage(map, Page.CRM_SERVICES);
      map.addAttribute("showUserProfile", true);
      uiView = "company_setup.connector_cs_admin";
      map.addAttribute("spUserHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));
      user = tenant.getOwner();
    } else {
      setPage(map, Page.COMPANY_CONNECTORS_CS);
      map.addAttribute("showUserProfile", false);
      uiView = "company_setup.connector_cs";
    }
    map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(user));
    boolean iframeView = false;
    if (showIframe != null && showIframe.equals("true") && serviceInstanceUUID != null
        && !serviceInstanceUUID.equals("")
        && connectorConfigurationManager.getInstanceByUUID(serviceInstanceUUID) != null) {
      iframeView = true;
      map.addAttribute("serviceInstanceUUID", serviceInstanceUUID);
      Map<ServiceInstance, Map<String, String>> serviceInstanceWfMap = new HashMap<ServiceInstance, Map<String, String>>();
      addWorkflowDetails(serviceInstanceWfMap, tenant, serviceInstanceUUID);
      map.addAttribute("serviceInstanceWfMap", serviceInstanceWfMap);
    } else {
      map.addAttribute("categories", connectorConfigurationManager.getAllCategories(CssdkConstants.CLOUD));
    }
    map.addAttribute("iframe_view", iframeView);

    prepareServiceViewForUser(map, user);
    List<Service> services = connectorConfigurationManager.getAllServicesByType(CssdkConstants.CLOUD);
    map.addAttribute("services", services);

    map.addAttribute("effectiveUser", user);
    @SuppressWarnings("unchecked")
    Map<ServiceInstance, Boolean> serviceInstanceMap = (Map<ServiceInstance, Boolean>) map.get("serviceInstanceMap");
    if ((Boolean) request.getAttribute("isSurrogatedTenant")
        || (!userService.hasAnyAuthority(user, "ROLE_ACCOUNT_CRUD", "ROLE_ACCOUNT_USER_CRUD"))) {
      filterServiceInstances(serviceInstanceMap, getCurrentUser());
    }
    map.addAttribute("countPerCategory",
        connectorConfigurationManager.getInstanceCountPerCategoryMap(serviceInstanceMap));
    map.addAttribute("serviceInstanceViewMap", getViewMap(tenant, serviceInstanceMap));

    Map<String, Boolean> serviceInstanceProvisioningMap = new HashMap<String, Boolean>();
    for (Map.Entry<ServiceInstance, Boolean> entry : serviceInstanceMap.entrySet()) {
      ServiceInstanceTenantConfiguration sitc = tenantService.getServiceInstanceTenantConfiguration(user.getTenant(),
          entry.getKey().getUuid());
      if (sitc != null) {
        serviceInstanceProvisioningMap.put(entry.getKey().getUuid(), sitc.getAutoProvision());
      }
    }
    map.addAttribute("serviceInstanceProvisioningMap", serviceInstanceProvisioningMap);

    Map<String, Boolean> enableServiceSupportedMap = new HashMap<String, Boolean>();
    if (services != null) {
      for (Service service : services) {
        boolean enableServiceForUserIsSupported = connectorManagementService.hasTenantScopedRole(service);
        enableServiceSupportedMap.put(service.getUuid(), enableServiceForUserIsSupported);
      }
      map.addAttribute("enableServiceSupportedMap", enableServiceSupportedMap);
    }

    boolean payAsYouGoMode = config.getBooleanValue(Names.com_citrix_cpbm_catalog_payAsYouGoMode);
    map.addAttribute("payAsYouGoMode", payAsYouGoMode);

    return uiView;
  }

  @RequestMapping(value = "/oss", method = RequestMethod.GET)
  public String showOSSServices(@RequestParam(value = "id", required = false) String serviceUuid,
      @RequestParam(value = "instanceId", required = false) String instanceUuId,
      @RequestParam(value = "action", required = false) String action, ModelMap map) {
    return getView(serviceUuid, instanceUuId, action, map, true);
  }

  private String getView(String serviceUuid, String instanceUuId, String action, ModelMap map, boolean isOss) {

    logger.debug("### Inside view.");
    String view = "";
    map.addAttribute("tenant", getCurrentUser().getTenant());
    setPage(map, isOss ? Page.HOME_CONNECTORS_OSS : Page.HOME_CONNECTORS_CS);
    prepareServiceViewForUser(map, getCurrentUser());

    if (StringUtils.isNotBlank(serviceUuid)) {
      Service service = connectorConfigurationManager.getService(serviceUuid);
      map.addAttribute("service", service);
      if ("view".equals(action)) {
        map.addAttribute("viewServiceDetails", true);
        view = isOss ? "main.home_oss.instance.edit" : "main.home_service.details";
      } else {
        List<BaseServiceConfigurationMetadata> propertiesList = new ArrayList<BaseServiceConfigurationMetadata>(
            service.getServiceConfigurationMetadata());
        Collections.sort(propertiesList);
        map.addAttribute("service_config_properties", propertiesList);
        setQuickProductProperties(map);
        view = isOss ? "main.home_oss.instance.edit" : "main.home_cs.instance.add";
      }
    } else if (StringUtils.isNotBlank(instanceUuId)) {
      ServiceInstance instance = connectorConfigurationManager.getInstanceByUUID(instanceUuId);
      List<ServiceInstanceConfig> instanceConfiguration = new ArrayList<ServiceInstanceConfig>(
          connectorConfigurationManager.getInstanceConfiguration(instance, instance.getService()));
      Collections.sort(instanceConfiguration, new Comparator<ServiceInstanceConfig>() {

        @Override
        public int compare(ServiceInstanceConfig o1, ServiceInstanceConfig o2) {
          return o1.getServiceConfigMetadata().getPropertyOrder() - o2.getServiceConfigMetadata().getPropertyOrder();
        }
      });
      map.addAttribute("instance", instance);
      map.addAttribute("service", instance.getService());
      map.addAttribute("instance_properties", instanceConfiguration);
      setQuickProductProperties(map);
      List<ServiceUsageType> usageTypeListWithNoProduct = connectorConfigurationManager
          .getUsageTypesWithNoProduct(instance);
      map.addAttribute("serviceUsageTypes", usageTypeListWithNoProduct);
      view = isOss ? "main.home_oss.instance.edit" : "main.home_cs.instance.edit";
    } else {
      List<Service> services = connectorConfigurationManager.getAllServicesByType(isOss ? CssdkConstants.OSS
          : CssdkConstants.CLOUD);
      map.addAttribute("services", services);
      map.addAttribute("categories",
          connectorConfigurationManager.getAllCategories(isOss ? CssdkConstants.OSS : CssdkConstants.CLOUD));
      map.addAttribute("countPerCategory", connectorConfigurationManager.getServiceCountPerCategory(services));
      if (isOss) {
        // get from RegistrationController
        boolean isSystemActive = false;
        map.addAttribute("isSystemActive", isSystemActive);
      }
      view = isOss ? "main.home_connector_oss" : "main.home_connector_cs_admin";
    }
    map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(getCurrentUser()));
    logger.debug("### Exiting view.");
    return view;
  }

  @RequestMapping(value = {
    "/create_instance"
  }, method = RequestMethod.POST)
  public @ResponseBody
  ModelMap saveInstance(@RequestParam("id") final String id, @RequestParam("action") final String action,
      @RequestParam("configProperties") final String configProperties,
      @RequestParam(value = "quickProducts", required = false) final String quickProducts, ModelMap map,
      HttpServletRequest request) {

    ServiceResponse response = new ServiceResponse();
    try {
      boolean update = "update".equals(action);

      Map<String, String> validation = new HashMap<String, String>();
      ServiceInstance instance = createServiceInstance(id, configProperties, validation, update,
          getSessionLocale(request));
      if (!CssdkConstants.SUCCESS.equals(validation.get("validationResult"))) {
        map.put("validationResult", validation.get("validationResult"));
        return map;
      }
      map.put("validationResult", validation.get("validationResult"));

      List<HashMap<String, Object>> quickProductList = getQuickProductList(quickProducts, instance);

      if (update) {
        connectorManagementService.updateInstance(instance, response, quickProductList);
      } else {
        connectorManagementService.addInstance(instance, response, quickProductList);
      }

      if (response.getStatus().equals(Status.SUCCESS)) {
        map.addAttribute("instanceid", instance.getUuid());
        map.addAttribute("result", CssdkConstants.SUCCESS);
        map.addAttribute(
            "message",
            update ? this.messageSource
                .getMessage("connector.instance.update.success", null, getSessionLocale(request)) : this.messageSource
                .getMessage("connector.instance.add.success", null, getSessionLocale(request)));
      } else {
        map.addAttribute("result", CssdkConstants.FAILURE);
        map.addAttribute("message", response.getMessage());
      }
    } catch (Exception e) {
      logger.error("Exception..", e);
      if (response.getStatus() != null && response.getStatus().equals(Status.FAILURE)) {
        map.addAttribute("message", response.getMessage());
      } else {
        map.addAttribute("message",
            this.messageSource.getMessage("connector.instance.add.failure", null, getSessionLocale(request)));
      }
      map.addAttribute("result", CssdkConstants.FAILURE);
    }
    return map;
  }

  @RequestMapping(value = {
    "/view_instances"
  }, method = RequestMethod.GET)
  public @ResponseBody
  ModelMap viewInstance(@RequestParam("id") final String id, ModelMap map) {
    List<ServiceInstance> instances = connectorConfigurationManager.getAllInstances(id);
    Map<String, Boolean> isAliveMap = new HashMap<String, Boolean>();
    map.addAttribute("instances", instances);
    map.addAttribute("isAliveMap", getConnectorStatusMap(instances, isAliveMap));
    return map;
  }

  @RequestMapping(value = {
    "/enable_service"
  }, method = RequestMethod.GET)
  public String enableService(@RequestParam("id") final String id, ModelMap map, HttpServletRequest request) {
    Service service = connectorConfigurationManager.getService(id);
    map.addAttribute("uuid", id);
    map.addAttribute("tnc", connectorConfigurationManager.getTermsAndConditions(service, getSessionLocale(request)));

    if (service.getType().equals(ConnectorType.CLOUD.toString())) {
      // TODO this may work now but this needs to be rewritten so that
      // actual program logic, including partitioning of roles into
      // scopes, resides here
      Collection<Profile> globalProfiles = profileService.listAllProfilesOfClass(Scope.GLOBAL);
      Collection<Profile> tenantProfiles = profileService.listAllProfilesOfClass(Scope.TENANT);

      map.addAttribute("subScopeMap", getSubScopeMap());
      map.addAttribute("opsProfiles", globalProfiles);
      map.addAttribute("nonOpsProfiles", tenantProfiles);
      Map<String, List<Role>> roleMap = connectorConfigurationManager.getTenantAndGlobalRoles(service);
      map.put("globalRoles", roleMap.get("GLOBAL_ROLES"));
      map.put("tenantRoles", roleMap.get("TENANT_ROLES"));

      map.addAttribute("cloudService", true);
      map.addAttribute("serviceName", service.getServiceName());
    } else {
      map.addAttribute("cloudService", false);
    }

    return "main.home_connector_enable";
  }

  @RequestMapping(value = {
    "/enable_service"
  }, method = RequestMethod.POST)
  public @ResponseBody
  String enableService(@RequestParam("id") final String id,
      @RequestParam("profiledetails") final String profiledetails, ModelMap map) {

    try {
      Service service = connectorConfigurationManager.getService(id);
      if (StringUtils.isNotEmpty(profiledetails)) {
        Map<String, List<String>> profileRoleMapping = getProfileRoleMapping(profiledetails);
        Roles cloudServiceRoles = connectorConfigurationManager.getRoles(service);
        if (cloudServiceRoles != null) {
          connectorConfigurationManager.persistCloudServiceRoles(cloudServiceRoles, profileRoleMapping);
        }
        // Refreshing object as it gets stale in persistRoles
        service = connectorConfigurationManager.getService(id);
      }
      service.setTermsAndConditionsAccepted(true);
      connectorManagementService.saveServiceType(service);
    } catch (Exception e) {
      logger.error("Exception..", e);
      return "failure";
    }
    return "success";
  }

  @RequestMapping(value = {
    "/status"
  }, method = RequestMethod.GET)
  public @ResponseBody
  boolean isAlive(@RequestParam("id") final String instanceUUID, ModelMap map) {
    BaseConnector connector = null;
    ServiceInstance si = connectorConfigurationManager.getInstance(instanceUUID);
    if (si.getService().getType().equals(CloudConnectorFactory.ConnectorType.CLOUD.toString())) {
      connector = connectorManagementService.getServiceInstance(instanceUUID);
    } else {
      connector = connectorManagementService.getOssServiceInstancebycategory(ConnectorType.valueOf(si.getService()
          .getCategory()));
    }
    return connector != null ? connector.getStatus() : false;
  }

  @RequestMapping(value = {
    "/enable"
  }, method = RequestMethod.POST)
  public @ResponseBody
  ModelMap enable(@RequestParam("id") final String id, @RequestParam("enable") final boolean enable, ModelMap map,
      HttpServletRequest request) {

    Service service = connectorConfigurationManager.getService(id);
    List<Service> services = connectorConfigurationManager.getAllEnabledServices(service.getCategory());
    map.addAttribute("result", "failure");
    if (enable && CollectionUtils.isNotEmpty(services)) {
      map.addAttribute("message",
          this.messageSource.getMessage("oss.instance.already.enabled", null, getSessionLocale(request)));
      return map;
    }
    // get from RegistrationController
    boolean isSystemActive = false;
    if (isSystemActive) {
      map.addAttribute("message",
          this.messageSource.getMessage("oss.enable.disable.freezed", null, getSessionLocale(request)));
      return map;
    }

    if (CollectionUtils.isEmpty(service.getServiceInstances())) {
      map.addAttribute("message",
          this.messageSource.getMessage("oss.service.not.configured", null, getSessionLocale(request)));
      return map;
    }

    // First manipulate the Map
    connectorManagementService.enableDisableOSS(service.getCategory(), enable);

    // then, update the DB.
    service.setEnabled(enable);
    connectorManagementService.saveServiceType(service);

    map.addAttribute("enabled", enable);
    map.addAttribute("result", "success");
    if (enable) {
      map.addAttribute("message",
          this.messageSource.getMessage("oss.service.enable.success", null, getSessionLocale(request)));
    } else {
      map.addAttribute("message",
          this.messageSource.getMessage("oss.service.disable.success", null, getSessionLocale(request)));
    }
    return map;
  }

  @RequestMapping(value = "/service_instance_list", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, List<Map<String, String>>> getServiceInstanceList(
      @ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "viewCatalog", required = false, defaultValue = "false") Boolean viewCatalog,
      @RequestParam(value = "category", required = false) String category, HttpServletRequest request) {

    logger.debug("###Entering in getServiceInstanceList GET");
    User user = getCurrentUser();
    if (user == null || viewCatalog && user.getTenant().getAccountId().equals(TenantService.SYSTEM_TENANT)) {
      user = userService.getSystemUser(Handle.PORTAL);
    }
    List<ServiceInstance> serviceProviderCloudTypeServiceInstances = new ArrayList<ServiceInstance>();
    boolean isSurrogatedTenant = (Boolean) request.getAttribute("isSurrogatedTenant");
    if (isSurrogatedTenant) {
      serviceProviderCloudTypeServiceInstances = userService.getCloudServiceInstance(user, category);
      user = tenantService.get(tenantParam).getOwner();
    }
    List<ServiceInstance> cloudTypeServiceInstances = userService.getCloudServiceInstance(user, category);
    if (isSurrogatedTenant) {
      cloudTypeServiceInstances.retainAll(serviceProviderCloudTypeServiceInstances);
    }

    List<Map<String, String>> serviceInstanceValues = new ArrayList<Map<String, String>>();
    for (ServiceInstance currentInstance : cloudTypeServiceInstances) {
      Map<String, String> currentInstanceValues = new HashMap<String, String>();
      currentInstanceValues.put("uuid", currentInstance.getUuid());
      currentInstanceValues.put("name", currentInstance.getName());
      serviceInstanceValues.add(currentInstanceValues);
    }
    Map<String, List<Map<String, String>>> service_instance_list = new HashMap<String, List<Map<String, String>>>();
    service_instance_list.put("ServiceInstances", serviceInstanceValues);
    return service_instance_list;
  }

  @RequestMapping(value = "/account_config_params", method = RequestMethod.GET)
  public String fetchAccountConfigurationsParams(
      @RequestParam(value = "serviceInstanceUUID", required = false) String serviceInstanceUUID,
      @RequestParam(value = "tenant", required = false) String tenantParam, ModelMap map, HttpServletRequest request) {

    map.addAttribute("serviceInstanceUUID", serviceInstanceUUID);

    ServiceInstance serviceinstance = connectorConfigurationManager.getInstance(serviceInstanceUUID);
    map.addAttribute("uuid", serviceinstance.getService().getUuid());
    map.addAttribute("tnc", tncString);

    Service service = serviceinstance.getService();

    StringBuilder propString = new StringBuilder();
    Set<AccountConfigurationServiceConfigMetadata> accountConfigurationServiceConfigMetadataList = service
        .getAccountConfigServiceConfigMetadata();
    for (AccountConfigurationServiceConfigMetadata accountConfigurationServiceConfigMetadata : accountConfigurationServiceConfigMetadataList) {
      if (!"EDITOR".equals(accountConfigurationServiceConfigMetadata.getName())) {
        propString.append(accountConfigurationServiceConfigMetadata.getName()).append(",");
      }
    }
    String tilesEditor = connectorConfigurationManager.getJspPath(service);
    if (!StringUtils.isNotBlank(tilesEditor)) {
      map.addAttribute("accountConfigEditor", null);
    } else {
      map.addAttribute("accountConfigEditor", tilesEditor);
    }
    boolean isEnableServiceForUserIsSupported = connectorManagementService.hasTenantScopedRole(serviceinstance
        .getService());
    // Add Service Instance handle
    TenantHandle latestTenantHandle = tenantService.getLatestTenantHandle(tenantParam, serviceInstanceUUID);
    map.addAttribute("latestTenantHandle", latestTenantHandle);
    Tenant tenant = tenantService.get(tenantParam);
    ServiceInstanceTenantConfiguration serviceInstanceTenantConfiguration = tenantService
        .getServiceInstanceTenantConfiguration(tenant, serviceinstance.getUuid());
    if (serviceInstanceTenantConfiguration != null) {
      map.addAttribute("accountConfigurationData", serviceInstanceTenantConfiguration.getData());
    }
    map.addAttribute("enableServiceForAllUsers", isEnableServiceForUserIsSupported);
    map.addAttribute("service_account_config_properties", accountConfigurationServiceConfigMetadataList);
    map.addAttribute("service_account_config_properties_list", propString.toString());
    map.addAttribute("service", service);
    map.addAttribute("serviceName", service.getServiceName());
    map.addAttribute("tenant", tenant);
    List<String> steps = new ArrayList<String>();
    steps.add(messageSource.getMessage("enable.service.dialog.tnc.title", null, getSessionLocale(request)));
    if (CollectionUtils.isNotEmpty(accountConfigurationServiceConfigMetadataList)) {
      steps.add(messageSource.getMessage("enable.service.dialog.configure.account.title", null,
          getSessionLocale(request)));
    }
    if (isEnableServiceForUserIsSupported) {
      steps.add(messageSource.getMessage("label.service.users.enable", null, getSessionLocale(request)));
    }
    steps.add(messageSource.getMessage("label.enable.service.review", null, getSessionLocale(request)));
    steps.add(messageSource.getMessage("label.enable.service.finish", null, getSessionLocale(request)));
    map.addAttribute("steps", steps);
    return "enable.service";
  }

  @RequestMapping(value = "/has_service_configuration", method = RequestMethod.GET)
  @ResponseBody
  public Boolean hasServiceConfiguration(@RequestParam(value = "id", required = false) String serviceID,
      @RequestParam(value = "instanceId", required = false) String instanceId) {

    Service service = null;
    if (StringUtils.isNotBlank(serviceID)) {
      service = connectorConfigurationManager.getService(serviceID);
    } else if (StringUtils.isNotBlank(instanceId)) {
      ServiceInstance serviceinstance = connectorConfigurationManager.getInstance(instanceId);
      service = serviceinstance.getService();
    }

    if (service != null && CollectionUtils.isNotEmpty(service.getServiceConfigurationMetadata())) {
      return true;
    }
    return false;
  }

  @RequestMapping(value = {
    "/load_packaged_jsp"
  }, method = RequestMethod.GET)
  @ResponseBody
  public void loadPackagedJspInConnector(
      @RequestParam(value = "serviceInstanceUUID", required = false) String serviceInstanceUUID,
      HttpServletResponse response) {
    ServiceInstance serviceinstance = connectorConfigurationManager.getInstance(serviceInstanceUUID);
    if (serviceinstance != null) {
      loadConnectorJsp(serviceinstance.getService(), response);
    }
  }

  @RequestMapping(value = "/upload_logo", method = RequestMethod.GET)
  public String uploadServiceInstanceLogo(@RequestParam(value = "Id", required = true) String id, ModelMap map) {
    logger.debug("### upload service instance logo method starting...(GET)");

    ServiceInstanceLogoForm instanceLogoForm = new ServiceInstanceLogoForm(
        connectorConfigurationManager.getInstance(id));
    map.addAttribute("serviceInstanceLogoForm", instanceLogoForm);

    logger.debug("### upload service instance logo method end...(GET)");
    return "service.instance.editlogo";
  }

  @RequestMapping(value = "/upload_logo", method = RequestMethod.POST)
  public ResponseEntity<String> uploadServiceInstanceLogo(
      @ModelAttribute("serviceInstanceLogoForm") ServiceInstanceLogoForm form, BindingResult result,
      HttpServletRequest request, ModelMap map) {

    logger.debug("### upload service instance logo method starting...(POST)");
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Content-Type", "text/html; charset=utf-8");

    Locale locale = getCurrentUser().getLocale();
    String fileSize = checkFileUploadMaxSizeException(request);
    if (fileSize != null) {
      result.rejectValue("logo", "error.image.max.upload.size.exceeded");
      return new ResponseEntity<String>(messageSource.getMessage(result.getFieldError("logo").getCode(), new Object[] {
        fileSize
      }, locale), responseHeaders, HttpStatus.OK);

    }
    String rootImageDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    if (StringUtils.isNotBlank(rootImageDir)) {
      ServiceInstance serviceInstance = form.getServiceInstance();
      ServiceInstanceLogoFormValidator validator = new ServiceInstanceLogoFormValidator();
      validator.validate(form, result);
      if (result.hasErrors()) {
        return new ResponseEntity<String>(
            messageSource.getMessage(result.getFieldError("logo").getCode(), null, locale), responseHeaders,
            HttpStatus.OK);

      } else {
        setImagePath(rootImageDir, serviceInstance, form.getLogo());
      }

      return new ResponseEntity<String>("success", responseHeaders, HttpStatus.OK);
    } else {
      result.rejectValue("logo", "error.custom.image.upload.dir");
      return new ResponseEntity<String>(messageSource.getMessage(result.getFieldError("logo").getCode(), null, locale),
          responseHeaders, HttpStatus.OK);

    }
  }

  @RequestMapping(value = "/getHandleState", method = RequestMethod.GET)
  @ResponseBody
  public String getHandleState(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = true) String tenantParam,
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID) {
    TenantHandle tenantHandle = tenantService.getLatestTenantHandle(tenantParam, serviceInstanceUUID);
    if (tenantHandle != null) {
      return tenantHandle.getState().name();
    }
    return null;
  }

  private void setImagePath(String rootImageDir, ServiceInstance serviceInstance, MultipartFile logoFile) {
    String serviceInstanceDir = "serviceInstance";
    File file = new File(FilenameUtils.concat(rootImageDir, serviceInstanceDir));
    if (!file.exists()) {
      file.mkdir();
    }
    String serviceInstanceAbsoluteDir = FilenameUtils.concat(rootImageDir, serviceInstanceDir);
    String relativeImageDir = FilenameUtils.concat(serviceInstanceDir, serviceInstance.getId().toString());
    File file1 = new File(FilenameUtils.concat(serviceInstanceAbsoluteDir, serviceInstance.getId().toString()));
    if (!file1.exists()) {
      file1.mkdir();
    }

    try {
      if (StringUtils.isNotBlank(logoFile.getOriginalFilename())) {
        String logoFileRelativePath = writeMultiPartFileToLocalFile(rootImageDir, relativeImageDir, logoFile);
        serviceInstance.setImagePath(logoFileRelativePath);
      }
      connectorConfigurationManager.updateServiceInstance(serviceInstance);
    } catch (IOException e) {
      logger.debug("###IO Exception in writing custom image file");
    }
  }

  private Map<ServiceInstance, Boolean> getViewMap(Tenant tenant, Map<ServiceInstance, Boolean> serviceInstanceMap) {

    Map<ServiceInstance, Boolean> serviceInstanceViewMap = new HashMap<ServiceInstance, Boolean>();
    for (Map.Entry<ServiceInstance, Boolean> entry : serviceInstanceMap.entrySet()) {
      serviceInstanceViewMap.put(entry.getKey(), false);
      try {
        if (entry.getValue()) {
          View view = resolveViewForAccountSettingsFromServiceInstance(tenant.getUuid(), entry.getKey().getUuid());
          if (view != null) {
            serviceInstanceViewMap.put(entry.getKey(), true);
          }
        }
      } catch (Exception e) {
        logger.error("Error in resolving the view for service instance : " + entry.getKey().getUuid(), e);
      }
    }
    return serviceInstanceViewMap;
  }

  private Map<String, Boolean> getSubScopeMap() {
    Map<String, Boolean> subScopeMap = new HashMap<String, Boolean>();
    Scope scopes[] = SecurityContextScope.Scope.values();
    for (Scope scope : scopes) {
      Collection<Scope> subScopes = SecurityContextScope.getSubScopes(scope);
      for (Scope scope2 : subScopes) {
        subScopeMap.put(scope.name() + "." + scope2.getName(), true);
      }
    }
    return subScopeMap;
  }

  private List<HashMap<String, Object>> getQuickProductList(String quickProducts, ServiceInstance instance)
      throws JSONException {

    List<HashMap<String, Object>> quickProductList = new ArrayList<HashMap<String, Object>>();
    if (instance.getService().getType().equals(CssdkConstants.CLOUD) && StringUtils.isNotEmpty(quickProducts)) {
      JSONArray productJsonArray = new JSONArray(quickProducts);
      List<Revision> allRPBRevisions = productService.getAllRevisions(null);
      // Size of the list should never be 0, as there always is a revision with
      // null start date, if not any other
      Revision productRevision = allRPBRevisions.get(allRPBRevisions.size() - 1);

      for (int index = 0; index < productJsonArray.length(); index++) {
        JSONObject jsonObj = productJsonArray.getJSONObject(index);
        HashMap<String, Object> productMap = new HashMap<String, Object>();

        String productName = jsonObj.get("name").toString();
        String code = jsonObj.get("code").toString();
        String scale = jsonObj.get("scale").toString();
        String uom = jsonObj.get("uom").toString();
        String usageTypeId = jsonObj.get("usageTypeId").toString();

        JSONArray priceList = new JSONArray(jsonObj.getString("price"));

        ServiceUsageType serviceUsageType = connectorConfigurationManager.getServiceUsageTypeById(Long
            .parseLong(usageTypeId));
        Category category = productService.getCategory(jsonObj.getLong("category"));

        Product product = new Product();
        product.setName(productName);
        product.setCode(code);
        product.setUom(uom);
        product.setCategory(category);

        productMap.put("product", product);

        MediationRule mediationRule = createMediationRule(product, serviceUsageType, instance, null,
            BigDecimal.valueOf(Double.parseDouble(scale)), "", productRevision);

        productMap.put("mediationRule", mediationRule);

        List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
        for (int priceIndex = 0; priceIndex < priceList.length(); priceIndex++) {
          JSONObject price = priceList.getJSONObject(priceIndex);
          ProductCharge productCharge = new ProductCharge();
          productCharge.setPrice(new BigDecimal(price.getString("currencyVal")).setScale(4, BigDecimal.ROUND_HALF_UP));
          CurrencyValue cv = currencyValueService.locateBYCurrencyCode(price.getString("currencyCode"));
          productCharge.setCurrencyValue(cv);
          productCharge.setRevision(productRevision);
          productCharges.add(productCharge);
        }

        productMap.put("productCharges", productCharges);
        quickProductList.add(productMap);
      }
    }
    return quickProductList;
  }

  private MediationRule createMediationRule(Product product, ServiceUsageType serviceUsageType,
      ServiceInstance serviceInstance, String operator, BigDecimal conversionFactor, String comments, Revision revision) {
    MediationRule mediationRule = new MediationRule();
    mediationRule.setServiceUsageType(serviceUsageType);
    if (operator != null && operator.length() > 0 && operator.equalsIgnoreCase("exclude")) {
      mediationRule.setOperator(com.vmops.model.MediationRule.Operator.EXCLUDE);
    } else {
      mediationRule.setOperator(com.vmops.model.MediationRule.Operator.COMBINE);
    }

    String scaleName = product.getUom();
    ServiceUsageTypeUomScale targetScale = null;
    boolean isMonthly = false;

    for (ServiceUsageTypeUomScale scale : serviceUsageType.getServiceUsageTypeUom().getServiceUsageTypeUomScale()) {
      if (scale.getName().equals(scaleName)) {
        targetScale = scale;
        break;
      }
    }

    if (targetScale != null) {
      isMonthly = targetScale.isMonthly();
    }

    mediationRule.setMonthly(isMonthly);
    mediationRule.setConversionFactor(conversionFactor);
    mediationRule.setServiceInstance(serviceInstance);
    mediationRule.setComments(comments);
    mediationRule.setProduct(product);
    mediationRule.setCreatedBy(actorService.getActor());
    mediationRule.setRevision(revision);
    return mediationRule;
  }

  private Map<String, Boolean> getConnectorStatusMap(List<ServiceInstance> instances, Map<String, Boolean> isAliveMap) {

    BaseConnector connector = null;
    for (ServiceInstance si : instances) {
      try {
        if (si.getService().getType().equals(CloudConnectorFactory.ConnectorType.CLOUD.toString())) {
          connector = connectorManagementService.getServiceInstance(si.getUuid());
        } else {
          connector = connectorManagementService.getOssServiceInstancebycategory(ConnectorType.valueOf(si.getService()
              .getCategory()));
        }
        isAliveMap.put(si.getUuid(), connector != null ? connector.getStatus() : false);
      } catch (Exception e) {
        logger.error("Exception..", e);
      }
    }
    return isAliveMap;
  }

  private ServiceInstance createServiceInstance(String id, String configProperties, Map<String, String> map,
      boolean update, Locale locale) throws Exception {

    Map<String, ServiceInstanceConfig> mapOfFieldNameVsInstanceConfig = new HashMap<String, ServiceInstanceConfig>();
    Set<ServiceInstanceConfig> instanceConfigurationList = new HashSet<ServiceInstanceConfig>();

    ServiceInstance instance = null;
    Service service = null;

    if (update) {
      instance = connectorConfigurationManager.getInstance(id);
      service = instance.getService();
      Set<ServiceInstanceConfig> instanceConfig = instance.getServiceInstanceConfig();
      for (ServiceInstanceConfig serviceInstanceConfig : instanceConfig) {
        mapOfFieldNameVsInstanceConfig.put(serviceInstanceConfig.getName(), serviceInstanceConfig);
      }
    } else {
      instance = new ServiceInstance();
      service = connectorConfigurationManager.getService(id);
      instance.setServiceInstanceConfig(instanceConfigurationList);
      instance.setService(service);
    }
    String validationResult = populateInstanceProperties(instance, instanceConfigurationList, configProperties,
        mapOfFieldNameVsInstanceConfig, update, locale);
    map.put("validationResult", validationResult);

    return instance;
  }

  private String populateInstanceProperties(ServiceInstance instance,
      Set<ServiceInstanceConfig> instanceConfigurationList, String configProperties,
      Map<String, ServiceInstanceConfig> mapOfFieldNameVsInstanceConfig, boolean update, Locale locale)
      throws Exception {

    Service service = instance.getService();

    Map<String, ServiceConfigurationMetadata> mapOfServiceMetaData = new HashMap<String, ServiceConfigurationMetadata>();
    Set<ServiceConfigurationMetadata> properties = service.getServiceConfigurationMetadata();
    for (ServiceConfigurationMetadata serviceConfigurationMetadata : properties) {
      mapOfServiceMetaData.put(serviceConfigurationMetadata.getName(), serviceConfigurationMetadata);
    }

    JSONArray jsonArray = new JSONArray(configProperties);
    for (int index = 0; index < jsonArray.length(); index++) {
      JSONObject jsonObj = jsonArray.getJSONObject(index);
      String fieldName = jsonObj.get("name").toString();
      String fieldValue = StringUtils.trim(jsonObj.get("value").toString());

      if ("instancename".equals(fieldName)) {
        instance.setName(fieldValue);
        continue;
      } else if ("instancedescription".equals(fieldName)) {
        instance.setDescription(fieldValue);
        continue;
      } else if ("instancecode".equals(fieldName)) {
        instance.setCode(fieldValue);
        continue;
      }

      ServiceConfigurationMetadata configurationMetadata = mapOfServiceMetaData.get(fieldName);
      String validationJson = configurationMetadata.getValidation();

      String validationResult = valid(validationJson, fieldName, fieldValue, locale);
      if (!CssdkConstants.SUCCESS.equals(validationResult)) {
        return validationResult;
      }

      if (update) {
        mapOfFieldNameVsInstanceConfig.get(fieldName).setValue(fieldValue);
      } else {
        ServiceInstanceConfig instanceConfiguration = new ServiceInstanceConfig();
        instanceConfiguration.setService(service);
        instanceConfiguration.setServiceConfigMetadata(configurationMetadata);
        instanceConfiguration.setServiceInstance(instance);
        instanceConfiguration.setName(fieldName);
        instanceConfiguration.setValue(fieldValue);
        instanceConfiguration.setServiceInstanceConfigurer(service);
        instanceConfigurationList.add(instanceConfiguration);
      }
    }

    if (service.getType().equals(CssdkConstants.OSS)) {
      instance.setName(service.getCategory());
      instance.setDescription("Instance for " + service.getCategory() + " Category");
      instance.setCode(instance.getName() + "." + service.getUuid().substring(0, 8));
    }
    return CssdkConstants.SUCCESS;
  }

  private String valid(String validationJson, String fieldName, String fieldValue, Locale locale) throws Exception {
    if (StringUtils.isNotBlank(validationJson)) {
      JsonBean jsonObject = JSONUtils.fromJSONString(validationJson, JsonBean.class);
      PropertyDescriptor pd[] = BeanUtils.getPropertyDescriptors(JsonBean.class);
      for (PropertyDescriptor propertyDescriptor : pd) {
        String propertyname = propertyDescriptor.getName();
        if (!"class".equals(propertyname)) {
          Object propertyvalue = PropertyUtils.getProperty(jsonObject, propertyname);
          if (propertyvalue instanceof Boolean && (Boolean) propertyvalue || !(propertyvalue instanceof Boolean)) {
            String validationResult = jsonObject.validate(fieldName, fieldValue, messageSource, locale);
            if (!validationResult.equals(CssdkConstants.SUCCESS)) {
              return validationResult;
            }
          }
        }
      }
    }
    return CssdkConstants.SUCCESS;
  }

  private void setQuickProductProperties(ModelMap map) {
    List<CurrencyValue> activeCurrencies = currencyValueService.listActiveCurrencies();
    map.addAttribute("activeCurrencies", activeCurrencies);
    Revision currentRevision = productService.getCurrentRevision(null);
    List<ProductCharge> productCharges = getProductCharges(activeCurrencies, currentRevision);
    map.addAttribute("productCharges", productCharges);
    List<Category> categories = productService.getAllCategories();
    map.addAttribute("categories", categories);
  }

  private List<ProductCharge> getProductCharges(List<CurrencyValue> activeCurrencies, Revision currentRevision) {
    List<ProductCharge> productCharges = new ArrayList<ProductCharge>();
    for (CurrencyValue cv : activeCurrencies) {
      ProductCharge newCharge = new ProductCharge();
      newCharge.setPrice(BigDecimal.ZERO.setScale(
          Integer.parseInt(config.getValue(Names.com_citrix_cpbm_portal_appearance_currency_precision)),
          BigDecimal.ROUND_HALF_UP));
      newCharge.setCurrencyValue(cv);
      newCharge.setRevision(currentRevision);
      productCharges.add(newCharge);
    }
    return productCharges;
  }

  private Map<String, List<String>> getProfileRoleMapping(String profiledetails) throws JSONException {
    Map<String, List<String>> profileRoleMapping = new LinkedHashMap<String, List<String>>();
    JSONArray jsonArray = new JSONArray(profiledetails);
    for (int index = 0; index < jsonArray.length(); index++) {
      List<String> roleList = new ArrayList<String>();
      JSONObject jsonObj = jsonArray.getJSONObject(index);
      String profileid = jsonObj.get("profileid").toString();
      String roles = jsonObj.get("roles").toString();
      JSONArray rolesArray = new JSONArray(roles);
      for (int i = 0; i < rolesArray.length(); i++) {
        String role = rolesArray.getString(i);
        roleList.add(role);
      }
      profileRoleMapping.put(profileid, roleList);
    }
    return profileRoleMapping;
  }

  private void loadConnectorJsp(Service service, HttpServletResponse response) {

    FileInputStream fileinputstream = null;
    String jspProvidedByService = connectorConfigurationManager.getJspPath(service);

    try {
      if (StringUtils.isNotBlank(jspProvidedByService)) {
        String cssdkFilesDirectory = FilenameUtils.concat(
            config.getValue(Names.com_citrix_cpbm_portal_settings_services_datapath), service.getServiceName() + "_"
                + service.getVendorVersion());
        String jspPath = cssdkFilesDirectory + "/" + CssdkConstants.JSP_DIRECTORY + "/" + jspProvidedByService;
        fileinputstream = new FileInputStream(jspPath);
        if (fileinputstream != null) {
          int numberBytes = fileinputstream.available();
          byte bytearray[] = new byte[numberBytes];
          fileinputstream.read(bytearray);
          response.setContentType("text/html");
          OutputStream outputStream = response.getOutputStream();
          response.setContentLength(numberBytes);
          outputStream.write(bytearray);
          outputStream.flush();
          outputStream.close();
          fileinputstream.close();
          return;
        }
      }
    } catch (FileNotFoundException e) {
      logger.error("FileNot Found...", e);
    } catch (IOException e) {
      logger.error("IOException Found...", e);
    }
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
  }

  private void filterServiceInstances(Map<ServiceInstance, Boolean> serviceInstanceMap, User user) {
    List<ServiceInstance> serviceInstanceList = userService.getCloudServiceInstance(user, null);
    serviceInstanceMap.keySet().retainAll(serviceInstanceList);
  }
}
