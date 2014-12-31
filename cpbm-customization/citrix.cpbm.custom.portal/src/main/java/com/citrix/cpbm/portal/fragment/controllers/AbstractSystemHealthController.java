/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.vmops.model.Health;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceNotification;
import com.vmops.model.ServiceNotification.Type;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.service.SystemHealthService;
import com.vmops.service.TenantService;
import com.vmops.utils.DateTimeUtils;
import com.vmops.utils.DateUtils;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.ServiceNotificationForm;
import com.vmops.web.interceptors.UserContextInterceptor;

/**
 * Controller for System Health related requests.
 * 
 * @author vaibhav
 */

public abstract class AbstractSystemHealthController extends AbstractAuthenticatedController {

  @Autowired
  private SystemHealthService healthService;

// CHECKSTYLE:OFF
  @Autowired
  protected ConnectorConfigurationManager connectorConfigurationManager;

// CHECKSTYLE:ON

  private Logger logger = Logger.getLogger("com.vmops.web.controllers.AbstractSystemHealthController");

  @RequestMapping(value = {
      "", "/"
  }, method = RequestMethod.GET)
  public String health(@RequestParam(value = "serviceinstanceuuid", required = false) String serviceInstanceUUID,
      @RequestParam(value = "page", required = false, defaultValue = "1") int page, ModelMap map) {
    logger.debug("###Entering in health(map) method @GET");
    int perPage = getDefaultPageSize();
    setPage(map, Page.SUPPORT_HEALTH);
    // Fetching category list and prepending it with All category
    User currentUser = getCurrentUser();
    List<String> serviceCategoryList = userService.getAllAccessibleCloudServiceCategories(currentUser);
    map.addAttribute("serviceCategoryList", serviceCategoryList);
    List<ServiceNotification> list = null;
    List<ServiceInstance> cloudTypeServiceInstances = getCloudTypeServiceInstances(currentUser);
    ServiceInstance serviceInstance = null;
    if (cloudTypeServiceInstances != null && cloudTypeServiceInstances.size() > 0) {
      if (serviceInstanceUUID == null || serviceInstanceUUID.equals("")) {
        serviceInstanceUUID = cloudTypeServiceInstances.get(0).getUuid();
      }
      serviceInstance = connectorConfigurationManager.getInstanceByUUID(serviceInstanceUUID);
      map.addAttribute("selectedCloudServiceInstance", serviceInstanceUUID);
      map.addAttribute("selectedCategory", serviceInstance.getService().getCategory());
      Map<Date, Health> dateStatus = healthService.getStatus(serviceInstance, currentUser, page, perPage);
      map.addAttribute("dateStatus", dateStatus);
      map.addAttribute("dateStatusLen", dateStatus.entrySet().size());
      map.addAttribute("dateStatusHistory", healthService.getHistoryList(serviceInstance, currentUser, page, perPage));
      map.addAttribute("notifications", list);
      map.addAttribute("today", new Date());
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(currentUser));
      int totalNumberOfDays = healthService.getNotificationDays(serviceInstance, currentUser);
      setPaginationValues(map, perPage, page, totalNumberOfDays, null);
    }
    map.addAttribute("tenant", getTenant());
    logger.debug("###Exiting health(map) method @GET");
    return "system.health";
  }

  @RequestMapping(value = "/health_maintainance", method = RequestMethod.GET)
  public String healthmaintainance(
      @RequestParam(value = "serviceinstanceuuid", required = false) String serviceInstanceUUID,
      @RequestParam(value = "currentpage", required = false, defaultValue = "1") String currentPage,
      @RequestParam(value = "size", required = false) String size, ModelMap map) {
    logger.debug("###Entering in healthmaintainance(map) method @GET");
    setPage(map, Page.SUPPORT_HEALTH);
    int currentPageValue = currentPage != null ? Integer.parseInt(currentPage) : 1;
    int perPageValue = getDefaultPageSize();
    int sizeInt = 0;

    User currentUser = getCurrentUser();

    // Fetching category list and prepending it with All category
    List<String> serviceCategoryList = userService.getAllAccessibleCloudServiceCategories(currentUser);
    map.addAttribute("serviceCategoryList", serviceCategoryList);
    List<ServiceInstance> cloudTypeServiceInstances = getCloudTypeServiceInstances(currentUser);
    ServiceInstance serviceInstance = null;
    if (cloudTypeServiceInstances != null && cloudTypeServiceInstances.size() > 0) {
      if (serviceInstanceUUID == null || serviceInstanceUUID.equals("")) {
        serviceInstanceUUID = cloudTypeServiceInstances.get(0).getUuid();
      }
      serviceInstance = connectorConfigurationManager.getInstanceByUUID(serviceInstanceUUID);
      map.addAttribute("selectedCloudServiceInstance", serviceInstanceUUID);
      map.addAttribute("selectedCategory", serviceInstance.getService().getCategory());
      List<ServiceNotification> maintenance = new ArrayList<ServiceNotification>();
      try {
        if (size == null || size.equals("")) {
          sizeInt = healthService.listPlannedNotifications(serviceInstance, 0, 0).size();
        } else {
          sizeInt = Integer.parseInt(size);
        }
        maintenance = healthService.listPlannedNotifications(serviceInstance, currentPageValue, perPageValue);
        map.addAttribute("maintenance", maintenance);
        map.addAttribute("size", maintenance.size());
      } catch (Exception e) {
        logger.error("Error while retrieving notifications: " + e);
      }
      map.addAttribute("today", new Date());
      map.addAttribute("tenant", getTenant());
      map.addAttribute("userHasCloudServiceAccount", userService.isUserHasAnyActiveCloudService(currentUser));
      setPaginationValues(map, perPageValue, currentPageValue, sizeInt, null);
    }
    logger.debug("###Exiting healthmaintainance(map) method @GET");
    return "system.health.maintenance";
  }

  @RequestMapping(value = ("/show_status_details"), method = RequestMethod.GET)
  public String showStatusDetails(@RequestParam(value = "date", required = true) String date,
      @RequestParam(value = "serviceinstanceuuid", required = true) String serviceInstanceUUID,
      @RequestParam(value = "dateformat", required = true) String dateFormat, ModelMap map) {
    logger.debug("### showStatusDetails method starting...(GET)");
    User currentUser = getCurrentUser();
    Calendar zeroHourOfTheTargetDate = Calendar
        .getInstance(TimeZone.getTimeZone(DateTimeUtils.getTimeZone(currentUser)));
    zeroHourOfTheTargetDate.clear();
    // IMPORTANT!!! don't zero out after setting the time zone, won't work.

    SimpleDateFormat sd = new SimpleDateFormat(dateFormat);
    sd.setTimeZone(TimeZone.getTimeZone(DateTimeUtils.getTimeZone(currentUser)));
    try {
      zeroHourOfTheTargetDate.setTime(sd.parse(date));
    } catch (ParseException e) {
      logger.error("Error found ...", e);
    }
    ServiceInstance serviceInstance = connectorConfigurationManager.getInstanceByUUID(serviceInstanceUUID);
    map.addAttribute("health", healthService.getSystemHealth(zeroHourOfTheTargetDate.getTime(), serviceInstance));
    map.addAttribute("notifications", healthService.listNotifications(zeroHourOfTheTargetDate.getTime(),
        DateTimeUtils.getTimeZone(currentUser), 0, 0, serviceInstance));
    map.addAttribute("date", zeroHourOfTheTargetDate.getTime());
    // TODO:FIX this. List service Insatnces if required
    logger.debug("### showStatusDetails method end");
    return "system.health.statusDetails";
  }

  @RequestMapping(value = ("/get_health_status_for_service_instance"), method = RequestMethod.GET)
  public String getHealthStatusForServiceInstance(
      @RequestParam(value = "serviceinstanceuuid", required = false) String serviceInstanceUUID,
      HttpServletRequest request, ModelMap map) {
    ServiceInstance serviceInstance = null;
    try {
      serviceInstance = connectorConfigurationManager.getInstance(serviceInstanceUUID);
    } catch (Exception e) {
      logger.error("Error in getting service Instance in getHealthStatusForServiceInstance method", e);
    }
    Health health = healthService.getSystemHealthStatus(new Date(), serviceInstance);
    ServiceNotification latestNotification = healthService.getLatestNotification(serviceInstance);
    String message = messageSource.getMessage(health.getDescription(), null, getSessionLocale(request));
    map.addAttribute("message", message);
    map.addAttribute("status", health.toString());
    map.addAttribute("latestNotification", latestNotification);

    return "service.health.chart";
  }

  @RequestMapping(value = ("/get_health_status_for_service_instances"), method = RequestMethod.GET)
  public String getHealthStatusForAllServiceInstances(
      @RequestParam(value = "tenant", required = false) String tenantParam, HttpServletRequest request, ModelMap map) {
    User user = getCurrentUser(true);
    Tenant tenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      user = tenant.getOwner();
    }
    List<Map<String, String>> serviceInstanceValues = new ArrayList<Map<String, String>>();
    List<ServiceInstance> serviceInstances = userService.getCloudServiceInstance(user, null);
    for (ServiceInstance serviceInstance : serviceInstances) {
      Map<String, String> currentInstanceValues = new HashMap<String, String>();
      Health health = healthService.getSystemHealthStatus(new Date(), serviceInstance);
      ServiceNotification latestNotification = healthService.getLatestNotification(serviceInstance);
      currentInstanceValues.put("name", serviceInstance.getName());
      currentInstanceValues.put("id", serviceInstance.getUuid());
      currentInstanceValues.put("status", health.toString());
      currentInstanceValues.put("tooltip", "system.health." + health.toString());
      if (latestNotification != null) {
        currentInstanceValues.put("latestNotification", latestNotification.getDescription());
      }
      serviceInstanceValues.add(currentInstanceValues);

    }
    map.addAttribute("healthStatusMapForServiceInstances", serviceInstanceValues);

    return "service.health.list";
  }

  @RequestMapping(value = "/add_status", method = RequestMethod.GET)
  public String showAddStatus(
      @RequestParam(value = "serviceinstanceuuid", required = false) String serviceInstanceUUID, ModelMap map) {

    logger.debug("###Entering in addStatus() method @GET ");
    ServiceNotificationForm form = new ServiceNotificationForm(new ServiceNotification());
    form.setDateFormat("dd MMM yyyy");
    map.addAttribute("serviceNotificationForm", form);
    if (serviceInstanceUUID == null) {
      List<ServiceInstance> cloudTypeServiceInstances = connectorManagementService.getCloudTypeServiceInstances();
      map.addAttribute("cloudTypeServiceInstances", cloudTypeServiceInstances);
    } else {
      map.addAttribute("selectedServiceInstanceUUID", serviceInstanceUUID);
    }
    logger.debug("###Leaving addStatus() method @GET ");
    return "system.health.addStatus";
  }

  @RequestMapping(value = "/add_scheduled_maintenance", method = RequestMethod.GET)
  public String showAddSchedMaintenance(@RequestParam(value = "id", required = false) String id,
      @RequestParam(value = "serviceinstanceuuid", required = false) String serviceInstanceUUID, ModelMap map) {
    logger.debug("###Entering in showAddSchedMaintenance() method @GET ");
    ServiceNotification notification;
    if (id != null) {
      notification = healthService.locateServiceNotificationById(id);
      map.addAttribute("newSchedule", false);
      serviceInstanceUUID = notification.getServiceInstance().getUuid();
    } else {
      // new schedule
      notification = new ServiceNotification();
      map.addAttribute("newSchedule", true);
    }
    ServiceNotificationForm serviceNotificationForm = new ServiceNotificationForm(notification);
    serviceNotificationForm.getServiceNotification().setNotificationType(Type.MAINTENANCE);
    serviceNotificationForm.setNotificationId(notification.getId() + "");

    map.addAttribute("serviceNotificationForm", serviceNotificationForm);
    if (serviceInstanceUUID == null) {
      List<ServiceInstance> cloudTypeServiceInstances = connectorManagementService.getCloudTypeServiceInstances();
      map.addAttribute("cloudTypeServiceInstances", cloudTypeServiceInstances);
    } else {
      map.addAttribute("selectedServiceInstanceUUID", serviceInstanceUUID);
    }
    map.addAttribute("date_format", messageSource.getMessage("maintenance.date.format", null, getCurrentUser().getLocale()));
    logger.debug("###Leaving showAddSchedMaintenance() method @GET ");
    return "system.health.addSchedMaintenance";
  }

  @ResponseBody
  @RequestMapping(value = "/add_status", method = RequestMethod.POST)
  public Map<String, String> addStatus(@ModelAttribute("serviceNotificationForm") ServiceNotificationForm form,
      HttpServletRequest request) {
    logger.debug("###Entering in addStatus() method @POST");
    HashMap<String, String> map = new HashMap<String, String>();
    ServiceNotification notification = form.getServiceNotification();
    Health status = Health.NORMAL;

    String serviceInstanceUUID = form.getServiceInstanceUUID();

    ServiceInstance serviceInstance = null;
    try {
      serviceInstance = connectorConfigurationManager.getInstance(serviceInstanceUUID);
    } catch (Exception e) {
      logger.error("Error in getting instance from service and instance uuids..", e);
    }
    notification.setServiceInstance(serviceInstance);

    switch (notification.getNotificationType()) {
      case DISRUPTION:
        notification = healthService.recordDisruption(notification.getSubject(), notification.getDescription(),
            serviceInstance);
        status = Health.DOWN;
        break;
      case ISSUE:
        notification = healthService.recordIssue(notification.getSubject(), notification.getDescription(),
            serviceInstance);
        status = Health.ISSUE;
        break;
      case RESOLUTION:
        notification = healthService.recordResolution(notification.getSubject(), notification.getDescription(),
            serviceInstance);
        status = Health.NORMAL;
        break;
      default:
        notification = null;
    }
    if (notification != null) {
      map.put("id", notification.getId().toString());
      map.put("description", notification.getDescription().toString());
      User currentuser = getCurrentUser();
      map.put(
          "recordedOn",
          DateTimeUtils.getDateStringInLocal(notification.getRecordedOn(), DateTimeUtils.getTimeZone(currentuser),
              form.getDateFormat(), currentuser));
      map.put("notificationType", notification.getNotificationType().toString());

      String statusDesc = messageSource.getMessage(status.getDescription(), null, getSessionLocale(request));
      map.put("status", statusDesc);
    }
    logger.debug("###Exiting addStatus() method @POST");

    return map;
  }

  @RequestMapping(value = "/save_maintenance_schedule", method = RequestMethod.POST)
  public String saveMaintenanceSchedule(@ModelAttribute("serviceNotificationForm") ServiceNotificationForm form,
      ModelMap map) {
    logger.debug("###Entering in saveMaintenanceSchedule() method @POST");
    User currentUser = getCurrentUser();
    ServiceNotification notification = form.getServiceNotification();
    String serviceInstanceUUID = form.getServiceInstanceUUID();

    ServiceInstance serviceInstance = null;
    try {
      serviceInstance = connectorConfigurationManager.getInstance(serviceInstanceUUID);
    } catch (Exception e) {
      logger.error("Error in getting instance from service and instance uuids..", e);
    }
    notification.setServiceInstance(serviceInstance);
    String error = null;
    try {
      Date startDate = notification.getPlannedStart();
      if (StringUtils.isNotBlank(form.getStartDateString()) && StringUtils.isNotBlank(form.getDateFormat())) {
        startDate = DateUtils.getDateFormatter(form.getDateFormat()).parse(form.getStartDateString());
      }
      Date endDate = notification.getPlannedEnd();
      if (StringUtils.isNotBlank(form.getEndDateString()) && StringUtils.isNotBlank(form.getDateFormat())) {
        endDate = DateUtils.getDateFormatter(form.getDateFormat()).parse(form.getEndDateString());
      }
      notification = healthService.recordPlannedMaintenance(
          notification.getSubject(),
          notification.getDescription(),
          notification.getServiceInstance(),
          DateTimeUtils.getDateInSystemTZForRecording(startDate,
              DateTimeUtils.getTimeZone(currentUser)),
          DateTimeUtils.getDateInSystemTZForRecording(endDate,
              DateTimeUtils.getTimeZone(currentUser)));
    } catch (IllegalArgumentException ex) {
      logger.error("Error while recording Scheduled Maintenance", ex);
      error = ex.getMessage();
    } catch (ParseException ex) {
      logger.error("Error while recording Scheduled Maintenance", ex);
      error = ex.getMessage();
    }
    if (error != null) {
      map.addAttribute("error", error);
    } else {
      map.addAttribute("item", notification);
    }
    logger.debug("###Exiting saveMaintenanceSchedule() method @POST");
    return "system.health.maintenanceView";
  }

  @RequestMapping(value = "/update_maintenance_schedule", method = RequestMethod.POST)
  public String updateMaintenanceSchedule(@ModelAttribute("serviceNotificationForm") ServiceNotificationForm form,
      ModelMap map) {
    logger.debug("###Entering in updateMaintenanceSchedule() method @POST");
    User currentUser = getCurrentUser();
    ServiceNotification notification = form.getServiceNotification();
    String error = null;
    try {
      Date startDate = notification.getPlannedStart();
      if (StringUtils.isNotBlank(form.getStartDateString()) && StringUtils.isNotBlank(form.getDateFormat())) {
        startDate = DateUtils.getDateFormatter(form.getDateFormat()).parse(form.getStartDateString());
      }
      Date endDate = notification.getPlannedEnd();
      if (StringUtils.isNotBlank(form.getEndDateString()) && StringUtils.isNotBlank(form.getDateFormat())) {
        endDate = DateUtils.getDateFormatter(form.getDateFormat()).parse(form.getEndDateString());
      }
      notification.setPlannedStart(DateTimeUtils.getDateInSystemTZForRecording(startDate,
          DateTimeUtils.getTimeZone(currentUser)));
      notification.setPlannedEnd(DateTimeUtils.getDateInSystemTZForRecording(endDate,
          DateTimeUtils.getTimeZone(currentUser)));
      healthService.updateServiceNotification(notification);
    } catch (ParseException e) {
      logger.error("Error while updating Scheduled Maintenance", e);
      error = e.getMessage();
    }
    if (error != null) {
      map.addAttribute("error", error);
    } else {
      map.addAttribute("item", notification);
    }
    logger.debug("###Exiting updateMaintenanceSchedule() method @POST");
    return "system.health.maintenanceView";
  }

  @RequestMapping(value = "/maintenance_view", method = RequestMethod.GET)
  public String maintenanceView(
      @RequestParam(value = "serviceinstanceuuid", required = false) String serviceInstanceUUID,
      @RequestParam(value = "id", required = false) String notificationId, ModelMap map) {
    logger.debug("###Entering in healthmaintainance View");
    setPage(map, Page.SUPPORT_HEALTH);
    Long notificationId2 = Long.valueOf(notificationId);
    List<ServiceNotification> list = null;
    List<ServiceInstance> cloudTypeServiceInstances = connectorManagementService.getCloudTypeServiceInstances();
    ServiceInstance serviceInstance = null;
    if (cloudTypeServiceInstances != null && cloudTypeServiceInstances.size() > 0) {
      if (serviceInstanceUUID != null && !serviceInstanceUUID.equals("")) {
        serviceInstance = connectorConfigurationManager.getInstanceByUUID(serviceInstanceUUID);
      }
      list = healthService.listNotificationsByServiceInstance(serviceInstance, 0, 0);
      map.addAttribute("cloudTypeServiceInstances", cloudTypeServiceInstances);
    }
    list = healthService.listNotificationsByServiceInstance(serviceInstance, 0, 0);
    map.addAttribute("notifications", list);
    map.addAttribute("today", new Date());
    map.addAttribute("item", healthService.getPlannedNotification(notificationId2));
    map.addAttribute("tenant", getTenant());
    logger.debug("###Exiting healthmaintainance View");
    return "system.health.maintenanceView";
  }

  @RequestMapping(value = ("/edit_status_details"), method = RequestMethod.GET)
  public String editStatusDetails(@RequestParam(value = "id", required = true) String id, ModelMap map) {
    logger.debug("### editStatusDetails method starting...(GET)");
    ServiceNotification notification = healthService.locateServiceNotificationById(id);
    ServiceNotificationForm serviceNotificationForm = new ServiceNotificationForm(notification);
    serviceNotificationForm.setNotificationId(notification.getId() + "");
    map.addAttribute("serviceNotificationForm", serviceNotificationForm);
    map.addAttribute("serviceNotification", notification);
    logger.debug("### editStatusDetails method end");
    return "system.health.editStatusDetails";
  }

  @RequestMapping(value = {
    "/{id}/remove_status"
  }, method = RequestMethod.GET)
  @ResponseBody
  public String deleteServiceNotification(@PathVariable String id, ModelMap map) {
    // TODO: Needs to be redone based on service instance.
    logger.debug("### In deleteServiceNotification()  start method...");
    ServiceNotification serviceNotification = healthService.locateServiceNotificationById(id);
    healthService.deleteServiceNotification(serviceNotification);
    logger.debug("###Exiting deleteServiceNotification() method @POST");
    return "success";
  }

  private List<ServiceInstance> getCloudTypeServiceInstances(User currentUser) {
    if (currentUser.getTenant().getAccountId().equals(TenantService.SYSTEM_TENANT)) {
      return connectorManagementService.getCloudTypeServiceInstances();
    } else {
      return userService.getCloudServiceInstance(currentUser, null);
    }
  }

}
