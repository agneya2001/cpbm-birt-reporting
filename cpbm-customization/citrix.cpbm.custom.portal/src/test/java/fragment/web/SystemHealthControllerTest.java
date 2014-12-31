/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
/**
 * 
 */
package fragment.web;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import web.WebTestsBase;
import web.support.DispatcherTestServlet;

import com.citrix.cpbm.portal.fragment.controllers.SystemHealthController;
import com.vmops.model.Health;
import com.vmops.model.ServiceInstance;
import com.vmops.model.ServiceNotification;
import com.vmops.model.ServiceNotification.Type;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.persistence.ServiceNotificationDAO;
import com.vmops.utils.DateTimeUtils;
import com.vmops.web.controllers.AbstractBaseController;
import com.vmops.web.controllers.menu.Page;
import com.vmops.web.forms.ServiceNotificationForm;

/**
 * @author vijay
 */
public class SystemHealthControllerTest extends WebTestsBase {

  @Autowired
  private SystemHealthController controller;

  @Autowired
  private ServiceNotificationDAO serviceNotificationDAO;

  @Autowired
  private ServiceInstanceDao serviceInstanceDao;

  private ServiceInstance defaultServiceInstance;

  private ModelMap map;

  private Calendar calendar;

  private MockHttpServletRequest request;

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    calendar = Calendar.getInstance();
    defaultServiceInstance = serviceInstanceDao.find(1L);
    request = new MockHttpServletRequest();

  }

  @Test
  public void testRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class<? extends SystemHealthController> controllerClass = controller.getClass();

    Method expected = locateMethod(controllerClass, "health", new Class[] {
        String.class, int.class, ModelMap.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/health"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "showStatusDetails", new Class[] {
        String.class, String.class, String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/health/show_status_details"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "getHealthStatusForServiceInstance", new Class[] {
        String.class, HttpServletRequest.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/health/get_health_status_for_service_instance"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "showAddStatus", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/health/add_status"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "showAddSchedMaintenance", new Class[] {
        String.class, String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/health/add_scheduled_maintenance"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "addStatus", new Class[] {
        ServiceNotificationForm.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/health/add_status"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "saveMaintenanceSchedule", new Class[] {
        ServiceNotificationForm.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/health/save_maintenance_schedule"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "updateMaintenanceSchedule", new Class[] {
        ServiceNotificationForm.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/health/update_maintenance_schedule"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "editStatusDetails", new Class[] {
        String.class, ModelMap.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/health/edit_status_details"));
    Assert.assertEquals(expected, handler);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testHealth() throws Exception {
    calendar.add(Calendar.HOUR_OF_DAY, -1);
    Date start = calendar.getTime();
    calendar.add(Calendar.HOUR_OF_DAY, 5);
    Date end = calendar.getTime();
    ServiceNotification notification = new ServiceNotification(getRootUser(), start, end, "planned",
        "planned down time", defaultServiceInstance);
    serviceNotificationDAO.save(notification);

    for (int i = 0; i < 3; i++) {
      calendar.add(Calendar.DAY_OF_MONTH, random.nextInt(5) + 2);
      start = calendar.getTime();
      calendar.add(Calendar.HOUR_OF_DAY, random.nextInt(5) + 1);
      end = calendar.getTime();
      notification = new ServiceNotification(getRootUser(), start, end, "planned", "planned down time",
          defaultServiceInstance);
      serviceNotificationDAO.save(notification);
    }
    serviceNotificationDAO.flush();

    ServiceInstance instance = serviceInstanceDao.find(1L);
    String view = controller.health(instance.getUuid(), 0, map);
    Assert.assertEquals("system.health", view);

    Object o = map.get("dateStatus");
    Assert.assertTrue(o instanceof Map);
    Map<Date, Health> dates = (Map<Date, Health>) o;
    Assert.assertEquals(AbstractBaseController.getDefaultPageSize().intValue(), dates.keySet().size());
    for (Date date : dates.keySet()) {
      Assert.assertTrue(DateUtils.isSameDay(new Date(), date) || date.before(new Date()));
    }

    o = map.get("dateStatusHistory");
    Assert.assertTrue(o instanceof Map);
    Map<Date, List<ServiceNotification>> datesStatusHistory = (Map<Date, List<ServiceNotification>>) o;
    Assert.assertEquals(AbstractBaseController.getDefaultPageSize().intValue(), datesStatusHistory.keySet().size());
    for (Date date : datesStatusHistory.keySet()) {
      Assert.assertTrue(DateUtils.isSameDay(new Date(), date) || date.before(new Date()));
    }

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSystemStatus() throws Exception {

    controller.getCurrentUser().setTimeZone("GMT");
    ServiceInstance instance = serviceInstanceDao.find(1L);

    controller.showStatusDetails("07082011", instance.getUuid(), "ddMMyyyy", map);
    Object o = map.get("health");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof Health);
    Health health = (Health) o;
    Assert.assertEquals(Health.NORMAL, health);
    o = map.get("notifications");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof List<?>);
    List<ServiceNotification> notifications = (List<ServiceNotification>) o;
    Assert.assertEquals(0, notifications.size());

    controller.showStatusDetails("08052013", instance.getUuid(), "ddMMyyyy", map);
    o = map.get("health");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof Health);
    health = (Health) o;
    Assert.assertEquals(Health.ISSUE, health);
    o = map.get("notifications");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof List<?>);
    notifications = (List<ServiceNotification>) o;
    Assert.assertEquals(3, notifications.size());
    Assert.assertEquals(Type.ISSUE, notifications.get(0).getNotificationType());

    controller.showStatusDetails("11052013", instance.getUuid(), "ddMMyyyy", map);
    o = map.get("health");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof Health);
    health = (Health) o;
    Assert.assertEquals(Health.DOWN, health);
    o = map.get("notifications");
    Assert.assertNotNull(o);
    Assert.assertTrue(o instanceof List<?>);
    notifications = (List<ServiceNotification>) o;
    Assert.assertEquals(2, notifications.size());
    Assert.assertEquals(Type.DISRUPTION, notifications.get(0).getNotificationType());

    // controller.getCurrentUser().setTimeZone("GMT-08:00");
    // controller.showStatusDetails("08052013", instance.getUuid(),
    // "ddMMyyyy", map);
    // o = map.get("health");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof Health);
    // health = (Health) o;
    // Assert.assertEquals(Health.ISSUE, health);
    // o = map.get("notifications");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof List<?>);
    // notifications = (List<ServiceNotification>) o;
    // Assert.assertEquals(2, notifications.size());
    // Assert.assertEquals(Type.ISSUE,
    // notifications.get(0).getNotificationType());
    //
    // controller.showStatusDetails("09082011", instance.getUuid(),
    // "ddMMyyyy", map);
    // o = map.get("health");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof Health);
    // health = (Health) o;
    // Assert.assertEquals(Health.DOWN, health);
    // o = map.get("notifications");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof List<?>);
    // notifications = (List<ServiceNotification>) o;
    // Assert.assertEquals(1, notifications.size());
    // Assert.assertEquals(Type.DISRUPTION,
    // notifications.get(0).getNotificationType());
    //
    // controller.showStatusDetails("10082011", instance.getUuid(),
    // "ddMMyyyy", map);
    // o = map.get("health");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof Health);
    // health = (Health) o;
    // Assert.assertEquals(Health.ISSUE, health);
    // o = map.get("notifications");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof List<?>);
    // notifications = (List<ServiceNotification>) o;
    // Assert.assertEquals(2, notifications.size());
    // Assert.assertEquals(Type.ISSUE,
    // notifications.get(0).getNotificationType());
    //
    // controller.getCurrentUser().setTimeZone("GMT+06:00");
    // controller.showStatusDetails("08082011", instance.getUuid(),
    // "ddMMyyyy", map);
    // o = map.get("health");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof Health);
    // health = (Health) o;
    // Assert.assertEquals(Health.ISSUE, health);
    // o = map.get("notifications");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof List<?>);
    // notifications = (List<ServiceNotification>) o;
    // Assert.assertEquals(3, notifications.size());
    // Assert.assertEquals(Type.ISSUE,
    // notifications.get(0).getNotificationType());
    //
    // controller.showStatusDetails("09082011", instance.getUuid(),
    // "ddMMyyyy", map);
    // o = map.get("health");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof Health);
    // health = (Health) o;
    // Assert.assertEquals(Health.ISSUE, health);
    // o = map.get("notifications");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof List<?>);
    // notifications = (List<ServiceNotification>) o;
    // Assert.assertEquals(0, notifications.size());
    //
    // controller.showStatusDetails("10082011", instance.getUuid(),
    // "ddMMyyyy", map);
    // o = map.get("health");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof Health);
    // health = (Health) o;
    // Assert.assertEquals(Health.NORMAL, health);
    // o = map.get("notifications");
    // Assert.assertNotNull(o);
    // Assert.assertTrue(o instanceof List<?>);
    // notifications = (List<ServiceNotification>) o;
    // Assert.assertEquals(2, notifications.size());
    // Assert.assertEquals(Type.RESOLUTION,
    // notifications.get(0).getNotificationType());
  }

  @Test
  public void testSaveMaintenanceSchedule() throws Exception {
    calendar.add(Calendar.HOUR_OF_DAY, -1);
    Date start = calendar.getTime();
    calendar.add(Calendar.HOUR_OF_DAY, 5);
    Date end = calendar.getTime();
    ServiceNotification notification = new ServiceNotification(getRootUser(), start, end, "planned",
        "planned down time", defaultServiceInstance);
    ServiceNotificationForm form = new ServiceNotificationForm(notification);
    form.setServiceInstanceUUID(defaultServiceInstance.getUuid());
    controller.saveMaintenanceSchedule(form, map);
    Assert.assertTrue(map.containsKey("item"));
  }

  @Test
  public void testshowAddStatus() {
    String showStatus = controller.showAddStatus(defaultServiceInstance.getUuid(), map);
    Assert.assertEquals(new String("system.health.addStatus"), showStatus);
  }

  @Test
  public void testshowAddStatusWithInstanceNull() {
    String showStatus = controller.showAddStatus(null, map);
    Assert.assertEquals(new String("system.health.addStatus"), showStatus);
  }

  @Test
  public void testshowAddStatusAsMasterUser() {

    User user = userDAO.find(3L);
    asUser(user);

    String showStatus = controller.health(defaultServiceInstance.getUuid(), 1, map);
    Assert.assertEquals(new String("system.health"), showStatus);
  }

  @Test
  public void testshowAddSchedMaintenance() {
    String showMaintenace = controller.showAddSchedMaintenance(null, null, map);
    Assert.assertEquals(showMaintenace, new String("system.health.addSchedMaintenance"));
    Assert.assertEquals(map.get("newSchedule"), true);
    ServiceNotificationForm serviceNotificationForm = ((ServiceNotificationForm) map.get("serviceNotificationForm"));
    Assert.assertNotNull(serviceNotificationForm.getServiceNotification());
    Assert.assertEquals(serviceNotificationForm.getServiceNotification().getNotificationType(), Type.MAINTENANCE);

    map.clear();
    showMaintenace = controller.showAddSchedMaintenance("1", null, map);
    Assert.assertEquals(map.get("newSchedule"), false);

  }

  @Test
  public void testaddStatus() {
    request = new MockHttpServletRequest();
    ServiceNotification notification = serviceNotificationDAO.find(1L);
    ServiceNotificationForm form = new ServiceNotificationForm(notification);
    form.setDateFormat("ddMMyyyy");
    form.setServiceInstanceUUID(defaultServiceInstance.getUuid());
    HashMap<String, String> controllerMap = (HashMap<String, String>) controller.addStatus(form, request);
    Assert.assertEquals(controllerMap.get("description"), notification.getDescription().toString());
    Assert.assertEquals(controllerMap.get("notificationType"), notification.getNotificationType().toString());
    Assert.assertNotNull(controllerMap.get("status"));
    Assert.assertNotNull(controllerMap.get("recordedOn"));
    Assert.assertNotNull(controllerMap.get("status"));

    controllerMap.clear();
    notification.setNotificationType(Type.ISSUE);
    serviceNotificationDAO.save(notification);
    controllerMap = (HashMap<String, String>) controller.addStatus(form, request);
    Assert.assertEquals(controllerMap.get("notificationType"), notification.getNotificationType().toString());

    controllerMap.clear();
    notification.setNotificationType(Type.RESOLUTION);
    serviceNotificationDAO.save(notification);
    controllerMap = (HashMap<String, String>) controller.addStatus(form, request);
    Assert.assertEquals(controllerMap.get("notificationType"), notification.getNotificationType().toString());

  }

  @Test
  public void testupdateMaintenanceSchedule() {
    ServiceNotification notification = serviceNotificationDAO.find(1L);
    ServiceNotificationForm form = new ServiceNotificationForm(notification);
    String update = controller.updateMaintenanceSchedule(form, map);
    Assert.assertEquals(update, new String("system.health.maintenanceView"));
    Assert.assertEquals(map.get("item"), notification);
    Assert.assertNotNull(notification.getPlannedStart());
    Assert.assertNotNull(notification.getPlannedEnd());

  }

  @Test
  public void testeditStatusDetails() {
    String edit = controller.editStatusDetails("1", map);
    Assert.assertEquals(edit, new String("system.health.editStatusDetails"));
    ServiceNotificationForm serviceNotificationForm = ((ServiceNotificationForm) map.get("serviceNotificationForm"));
    Assert.assertNotNull(serviceNotificationForm);
    Assert.assertEquals(map.get("serviceNotification"), serviceNotificationForm.getServiceNotification());
  }

  @Test
  public void testdeleteServiceNotification() {
    int beforeNotificationCount = serviceNotificationDAO.count();
    String delete = controller.deleteServiceNotification("1", map);
    Assert.assertEquals(delete, new String("success"));
    int afterNotificationCount = serviceNotificationDAO.count();
    Assert.assertEquals(afterNotificationCount, beforeNotificationCount - 1);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testhealthmaintainance() {
    String healthMaintainance = controller.healthmaintainance(defaultServiceInstance.getUuid(), "1", null, map);
    Assert.assertEquals(healthMaintainance, new String("system.health.maintenance"));
    Assert.assertNotNull(map.get("today"));
    Assert.assertEquals(new Date().toString(), ((Date) map.get("today")).toString());
    Assert.assertNotNull(map.get("tenant"));
    Tenant tenant = (Tenant) map.get("tenant");
    Assert.assertEquals(getSystemTenant(), tenant);
    Assert.assertNotNull(map.get("maintenance"));
    List<ServiceNotification> serviceNotificationList = (List<ServiceNotification>) map.get("maintenance");
    Assert.assertEquals(1, serviceNotificationList.size());
  }

  @Test
  public void testHealthStatusForServiceInstance() {
    String healthMaintainance = controller.getHealthStatusForServiceInstance(defaultServiceInstance.getUuid(), request,
        map);
    Assert.assertNotNull(healthMaintainance);
    Assert.assertEquals(new String("service.health.chart"), healthMaintainance);
    Assert.assertEquals(Health.NORMAL.toString(), map.get("status"));
  }

  @Test
  public void testHealthStatusForServiceInstanceAsMasterUser() {

    User user = userDAO.find(3L);
    asUser(user);

    String healthMaintainance = controller.getHealthStatusForServiceInstance(defaultServiceInstance.getUuid(), request,
        map);
    Assert.assertNotNull(healthMaintainance);
    Assert.assertEquals(new String("service.health.chart"), healthMaintainance);
    Assert.assertEquals(Health.NORMAL.toString(), map.get("status"));
  }

  @Test
  public void testHealthStatusForServiceInstanceAsNormalUser() {

    User user = userDAO.find(22L);
    asUser(user);

    String healthMaintainance = controller.getHealthStatusForServiceInstance(defaultServiceInstance.getUuid(), request,
        map);
    Assert.assertNotNull(healthMaintainance);
    Assert.assertEquals(new String("service.health.chart"), healthMaintainance);
    Assert.assertEquals(Health.NORMAL.toString(), map.get("status"));
  }

  @Test
  public void testHealthStatusForServiceInstanceAsNull() {
    String healthMaintainance = controller.getHealthStatusForServiceInstance(null, request, map);
    Assert.assertNotNull(healthMaintainance);
    Assert.assertEquals(new String("service.health.chart"), healthMaintainance);
    Assert.assertEquals(Health.DOWN.toString(), map.get("status"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testmaintenanceView() {
    ServiceNotification notification = serviceNotificationDAO.find(1L);
    String healthMaintainance = controller.maintenanceView(defaultServiceInstance.getUuid(), "1", map);
    Assert.assertNotNull(healthMaintainance);
    Assert.assertEquals(new String("system.health.maintenanceView"), healthMaintainance);
    Assert.assertEquals(Page.SUPPORT_HEALTH, map.get("page"));
    List<ServiceNotification> list = (List<ServiceNotification>) map.get("notifications");
    Assert.assertEquals(13, list.size());
    ServiceNotification obtainedNotification = (ServiceNotification) map.get("item");
    Assert.assertEquals(notification, obtainedNotification);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testmaintenanceViewWithServiceInstanceAsNull() {
    ServiceNotification notification = serviceNotificationDAO.find(1L);
    String healthMaintainance = controller.maintenanceView(null, "1", map);
    Assert.assertNotNull(healthMaintainance);
    Assert.assertEquals(new String("system.health.maintenanceView"), healthMaintainance);
    Assert.assertEquals(Page.SUPPORT_HEALTH, map.get("page"));
    List<ServiceNotification> list = (List<ServiceNotification>) map.get("notifications");
    Assert.assertEquals(serviceNotificationDAO.count(), list.size());
    ServiceNotification obtainedNotification = (ServiceNotification) map.get("item");
    Assert.assertEquals(notification, obtainedNotification);
  }

  @Test
  public void testMaintenanceSchedule() throws Exception {
    Date startDate = new Date();
    Date endDate = DateUtils.addDays(startDate, 1);
    ServiceNotification notification = new ServiceNotification(getRootUser(), startDate, endDate, "planned",
        "planned down time", defaultServiceInstance);
    ServiceNotificationForm form = new ServiceNotificationForm(notification);
    form.setServiceInstanceUUID(defaultServiceInstance.getUuid());
    controller.saveMaintenanceSchedule(form, map);
    ServiceNotification serviceNotification = (ServiceNotification) map.get("item");
    Assert.assertEquals(serviceNotification.getSubject(), new String("planned"));
    Assert.assertEquals(serviceNotification.getDescription(), new String("planned down time"));
    Date start = serviceNotification.getPlannedStart();
    Date end = serviceNotification.getPlannedEnd();

    Assert.assertEquals(start,
        DateTimeUtils.getDateInSystemTZForRecording(startDate, DateTimeUtils.getTimeZone(getRootUser())));
    Assert.assertEquals(end,
        DateTimeUtils.getDateInSystemTZForRecording(endDate, DateTimeUtils.getTimeZone(getRootUser())));

  }

}
