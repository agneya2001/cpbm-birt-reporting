/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import web.WebTestsBaseWithMockConnectors;
import web.support.DispatcherTestServlet;

import com.citrix.cpbm.platform.model.SsoObject;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.SsoHandler;
import com.citrix.cpbm.platform.spi.View;
import com.citrix.cpbm.platform.spi.View.ViewMode;
import com.citrix.cpbm.platform.spi.ViewResolver;
import com.citrix.cpbm.portal.fragment.controllers.AbstractManageResourceController;
import com.citrix.cpbm.portal.fragment.controllers.ConnectorController;
import com.vmops.model.ServiceInstance;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.persistence.ServiceDAO;
import com.vmops.persistence.ServiceInstanceDao;
import com.vmops.persistence.TenantDAO;
import common.MockCloudInstance;

public class AbstractManageResourceControllerTest extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  private MockHttpServletResponse response;

  private MockHttpServletRequest request;

  @Autowired
  private AbstractManageResourceController controller;

  @Autowired
  private TenantDAO tenantdao;

  @Autowired
  private ServiceInstanceDao serviceInstanceDao;

  @Autowired
  private ServiceDAO servicedao;

  @Autowired
  private ConnectorController connectorController;

  private MockCloudInstance mockCloudInstance = null;

  public void setSSOHandler() {
    mockCloudInstance = getMockCloudInstance();
    CloudConnector connector = mockCloudInstance.getCloudConnector();
    SsoHandler ssoHandler = mockCloudInstance.getSsoHandler();
    EasyMock.expect(ssoHandler.handleLogin(EasyMock.anyObject(User.class))).andAnswer(new IAnswer<SsoObject>() {

      @Override
      public SsoObject answer() throws Exception {
        SsoObject ssoObject = new SsoObject();
        String name = ((User) EasyMock.getCurrentArguments()[0]).getName();
        ssoObject.setSsoString(name);
        List<Cookie> cookies = new ArrayList<Cookie>();
        Cookie c = new Cookie("Test", "Test");
        cookies.add(c);
        ssoObject.setCookies(cookies);
        return ssoObject;
      }

    }).anyTimes();
    EasyMock.replay(ssoHandler);
    EasyMock.replay(connector);
  }

  public void setlistResourceViews() {
    mockCloudInstance = getMockCloudInstance();
    CloudConnector connector = mockCloudInstance.getCloudConnector();
    ViewResolver viewresolver = mockCloudInstance.getViewResolver();
    EasyMock.expect(viewresolver.listResourceViews(EasyMock.anyObject(User.class)))
        .andAnswer(new IAnswer<List<View>>() {

          @Override
          public List<View> answer() throws Exception {
            List<View> viewList = new ArrayList<View>();
            String name = ((User) EasyMock.getCurrentArguments()[0]).getName();
            View v = new View(name, "TestURL", ViewMode.WINDOW);
            viewList.add(v);
            return viewList;
          }

        }).anyTimes();
    EasyMock.replay(viewresolver);
    EasyMock.replay(connector);
  }

// private SsoObject getSSOObject(SsoObject ssoObject, User user) {
// ssoObject.setSsoString(user.getName());
// return ssoObject;
// }

  @Test
  public void testRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Class<? extends AbstractManageResourceController> controllerClass = controller.getClass();
    Method expected = locateMethod(controllerClass, "getSSOCmdString", new Class[] {
        Tenant.class, String.class, String.class, ModelMap.class, HttpServletRequest.class, HttpServletResponse.class
    });
    Method handler = servlet.recognize(getRequestTemplate(HttpMethod.POST, "/manage_resource/get_sso_cmd_string"));
    Assert.assertEquals(expected, handler);

    expected = locateMethod(controllerClass, "getResourceViews", new Class[] {
        Tenant.class, String.class, String.class, ModelMap.class, HttpServletRequest.class
    });
    handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, "/manage_resource/get_resource_views"));
    Assert.assertEquals(expected, handler);
  }

  /**
   * @author Abhaik
   * @description : Test to get the SSO Cmd for an Active Tenant through Service Provider User
   */
  @Test
  public void testSSOFromSPUserForActiveTenant() {

    setSSOHandler();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    map = new ModelMap();
    Tenant systemTenant = tenantService.getSystemTenant();
    Tenant tenant = tenantdao.find(2L);
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("effectiveTenant", tenant);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(systemTenant, tenant.getParam()));

    Map<String, String> resultMap = controller.getSSOCmdString(systemTenant, tenant.getParam(), instance.getUuid(),
        map, request, response);
    Assert.assertNotNull(resultMap);

    String status = resultMap.get("status");
    Assert.assertEquals("success", status);

    String cmdString = resultMap.get("cmdString");
    Assert.assertNotNull(cmdString);
    Assert.assertEquals(cmdString, tenant.getOwner().getName());

  }

  /**
   * @author Abhaik
   * @description : Test to get the SSO Cmd for a Service Provider User through Service Provider User only
   */
  @Test
  public void testSSOFromSPUserForSelf() {

    setSSOHandler();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    map = new ModelMap();
    Tenant systemTenant = tenantService.getSystemTenant();
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("effectiveTenant", systemTenant);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(systemTenant, systemTenant.getParam()));

    Map<String, String> resultMap = controller.getSSOCmdString(systemTenant, systemTenant.getParam(),
        instance.getUuid(), map, request, response);
    Assert.assertNotNull(resultMap);

    String status = resultMap.get("status");
    Assert.assertEquals("success", status);

    String cmdString = resultMap.get("cmdString");
    Assert.assertNotNull(cmdString);
    Assert.assertEquals(cmdString, systemTenant.getOwner().getName());

  }

  /**
   * @author Abhaik
   * @description : Test to get the SSO Cmd for an Active Tenant having Null SSO Handler through Service Provider User
   */
  @Test
  public void testSSOFromSPUserForSsoHandlerIsNull() {

    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    map = new ModelMap();
    Tenant systemTenant = tenantService.getSystemTenant();
    Tenant tenant = tenantdao.find(2L);
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("effectiveTenant", tenant);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(systemTenant, tenant.getParam()));

    Map<String, String> resultMap = controller.getSSOCmdString(systemTenant, tenant.getParam(), instance.getUuid(),
        map, request, response);
    Assert.assertNotNull(resultMap);

    String status = resultMap.get("status");
    Assert.assertEquals("success", status);

    String cmdString = resultMap.get("cmdString");
    Assert.assertNull(cmdString);

  }

//
  /**
   * @author Abhaik
   * @description : Test to get the SSO Cmd for an Active Tenant through Service Provider User by setting cookies
   */
  @Test
  public void testSSOFromSPUserForSetCookie() {

    setSSOHandler();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    map = new ModelMap();
    Tenant systemTenant = tenantService.getSystemTenant();
    Tenant tenant = tenantdao.find(2L);
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("effectiveTenant", tenant);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(systemTenant, tenant.getParam()));

    Map<String, String> resultMap = controller.getSSOCmdString(systemTenant, tenant.getParam(), instance.getUuid(),
        map, request, response);
    Assert.assertNotNull(resultMap);

    String status = resultMap.get("status");
    Assert.assertEquals("success", status);

    String cmdString = resultMap.get("cmdString");
    Assert.assertNotNull(cmdString);

    Cookie obtCookie = response.getCookie("Test");
    Assert.assertEquals("Test", obtCookie.getValue());

  }

  /**
   * @author Abhaik
   * @description : Test to get the SSO Cmd for a New Tenant through Service Provider User
   */

  @Test
  public void testSSOFromSPUserForNewTenant() {

    setSSOHandler();
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    map = new ModelMap();
    Tenant systemTenant = tenantService.getSystemTenant();
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("effectiveTenant", tenant);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(systemTenant, tenant.getParam()));

    Map<String, String> resultMap = controller.getSSOCmdString(systemTenant, tenant.getParam(), instance.getUuid(),
        map, request, response);
    Assert.assertNotNull(resultMap);

    String status = resultMap.get("status");
    Assert.assertEquals("fail", status);

    String cmdString = resultMap.get("cmdString");
    Assert.assertNull("Tenant is New State", cmdString);

    String errorMessage = resultMap.get("error_message");
    Assert.assertEquals(
        messageSource.getMessage("message.user.no.billing", null, controller.getSessionLocale(request)), errorMessage);

    String url = resultMap.get("url");
    Assert.assertEquals("/portal/portal/home", url);

  }

  /**
   * @author Abhaik
   * @description : Test to get the SSO Cmd for a New Tenant through the same Tenant only
   */
  @Test
  public void testSSOFromSelfForNewTenant() {

    setSSOHandler();
    Tenant tenant = createTestTenant(accountTypeDAO.getDefaultRegistrationAccountType());
    User user = tenant.getOwner();
    asUser(user);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    map = new ModelMap();
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("effectiveTenant", tenant);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(tenant, tenant.getParam()));

    Map<String, String> resultMap = controller.getSSOCmdString(tenant, tenant.getParam(), instance.getUuid(), map,
        request, response);
    Assert.assertNotNull(resultMap);

    String status = resultMap.get("status");
    Assert.assertEquals("fail", status);

    String cmdString = resultMap.get("cmdString");
    Assert.assertNull("Tenant is New State", cmdString);

    String url = resultMap.get("url");
    Assert.assertEquals("/portal/portal/tenants/editcurrent", url);

  }

  /**
   * @author Abhaik
   * @description : Test to get the SSO Cmd for an Active Tenant through the same Tenant only
   */
  @Test
  public void testSSOFromTenantForSelf() {

    setSSOHandler();
    User user = userDAO.find(3L);
    asUser(user);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    map = new ModelMap();
    Tenant tenant = tenantdao.find(2L);
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("effectiveTenant", tenant);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(tenant, tenant.getParam()));

    Map<String, String> resultMap = controller.getSSOCmdString(tenant, tenant.getParam(), instance.getUuid(), map,
        request, response);
    Assert.assertNotNull(resultMap);

    String status = resultMap.get("status");
    Assert.assertEquals("success", status);

    String cmdString = resultMap.get("cmdString");
    Assert.assertNotNull(cmdString);
    Assert.assertEquals(cmdString, tenant.getOwner().getName());

  }

  /**
   * @author Abhaik
   * @description : Test to get the SSO Cmd for an Active Tenant through other Tenant
   */
  @Test
  public void testSSOFromTenantForOther() {

    setSSOHandler();
    User user = userDAO.find(3L);
    asUser(user);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    map = new ModelMap();
    Tenant tenant = tenantdao.find(2L);
    Tenant otherTenant = tenantdao.find(10L);
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("effectiveTenant", tenant);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(tenant, otherTenant.getParam()));

    Map<String, String> resultMap = controller.getSSOCmdString(tenant, otherTenant.getParam(), instance.getUuid(), map,
        request, response);
    Assert.assertNotNull(resultMap);

    String status = resultMap.get("status");
    Assert.assertEquals("success", status);

    String cmdString = resultMap.get("cmdString");
    Assert.assertEquals(cmdString, tenant.getOwner().getName());

  }

  /**
   * @author Abhaik
   * @description : Test to get the Resource Views for a Tenant through Service Provider User
   */
  @Test
  public void testResourceViewsFromSPUserForTenant() {

    setlistResourceViews();
    request = new MockHttpServletRequest();
    map = new ModelMap();
    Tenant systemTenant = tenantService.getSystemTenant();
    Tenant tenant = tenantdao.find(2L);
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(systemTenant, tenant.getParam()));

    List<View> resultListView = controller.getResourceViews(systemTenant, tenant.getParam(), instance.getUuid(), map,
        request);
    Assert.assertNotNull(resultListView);

    View resultview = resultListView.get(0);
    Assert.assertEquals(tenant.getOwner().getName(), resultview.getName());
    Assert.assertEquals("TestURL", resultview.getURL());
    Assert.assertEquals(ViewMode.WINDOW, resultview.getMode());

  }

  /**
   * @author Abhaik
   * @description : Test to get the Resource Views for a Service Provider User through same Service Provider User
   */
  @Test
  public void testResourceViewsFromSPUserForSelf() {

    setlistResourceViews();
    request = new MockHttpServletRequest();
    map = new ModelMap();
    Tenant systemTenant = tenantService.getSystemTenant();
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(systemTenant, systemTenant.getParam()));

    List<View> resultListView = controller.getResourceViews(systemTenant, systemTenant.getParam(), instance.getUuid(),
        map, request);
    Assert.assertNotNull(resultListView);

    View resultview = resultListView.get(0);
    Assert.assertEquals(systemTenant.getOwner().getName(), resultview.getName());
    Assert.assertEquals("TestURL", resultview.getURL());
    Assert.assertEquals(ViewMode.WINDOW, resultview.getMode());

  }

  /**
   * @author Abhaik
   * @description : Test to get the Resource Views for a Tenant through same Tenant
   */
  @Test
  public void testResourceViewsFromTenantForSelf() {

    User user = userDAO.find(3L);
    asUser(user);
    setlistResourceViews();
    request = new MockHttpServletRequest();
    map = new ModelMap();
    Tenant tenant = tenantdao.find(2L);
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(tenant, tenant.getParam()));

    List<View> resultListView = controller
        .getResourceViews(tenant, tenant.getParam(), instance.getUuid(), map, request);
    Assert.assertNotNull(resultListView);

    View resultview = resultListView.get(0);
    Assert.assertEquals(tenant.getOwner().getName(), resultview.getName());
    Assert.assertEquals("TestURL", resultview.getURL());
    Assert.assertEquals(ViewMode.WINDOW, resultview.getMode());

  }

  /**
   * @author Abhaik
   * @description : Test to get the Resource Views for a Tenant through other Tenant
   */
  @Test
  public void testResourceViewsFromTenantForOther() {

    User user = userDAO.find(3L);
    asUser(user);
    setlistResourceViews();
    request = new MockHttpServletRequest();
    map = new ModelMap();
    Tenant tenant = tenantdao.find(2L);
    Tenant othertenant = tenantdao.find(10L);
    ServiceInstance instance = serviceInstanceDao.find(1L);
    request.setAttribute("isSurrogatedTenant", isSurrogatedTenant(tenant, othertenant.getParam()));

    List<View> resultListView = controller.getResourceViews(tenant, othertenant.getParam(), instance.getUuid(), map,
        request);
    Assert.assertNotNull(resultListView);

    View resultview = resultListView.get(0);
    Assert.assertEquals(tenant.getOwner().getName(), resultview.getName());
    Assert.assertEquals("TestURL", resultview.getURL());
    Assert.assertEquals(ViewMode.WINDOW, resultview.getMode());

  }

  /**
   * @author vinayv
   * @description : Test to get the Resource Views for a Tenant for Service Instance which was not enabled
   */
  @Test
  public void testResourceViewsFromSPUserForTenantWithNoCloudAccount() {
    try {
      request = new MockHttpServletRequest();
      User user = userDAO.find(3L);
      asUser(user);
      setlistResourceViews();
      map = new ModelMap();
      Tenant tenant = tenantdao.find(2L);
      ServiceInstance instance = serviceInstanceDao.find(11L);
      request.setAttribute("isSurrogatedTenant", Boolean.TRUE);
      controller.getResourceViews(tenant, tenant.getParam(), instance.getUuid(), map, request);
    } catch (Exception e) {
      Assert.assertEquals("message.no.cloud.account", e.getMessage());
    }
  }

}
