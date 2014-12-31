/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import web.WebTestsBaseWithMockConnectors;

import com.citrix.cpbm.platform.bootstrap.service.BootstrapActivator;
import com.citrix.cpbm.platform.spi.APIAccessDecisionVoter;
import com.citrix.cpbm.platform.spi.APICall;
import com.citrix.cpbm.platform.spi.APIHandler;
import com.citrix.cpbm.platform.spi.APIResponseObject;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.portal.fragment.controllers.APIProxyController;
import com.citrix.cpbm.portal.fragment.controllers.WorkflowController;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ConfigurationService;
import com.vmops.service.exceptions.CloudServiceApiException;
import com.vmops.web.controllers.menu.Page;
import common.MockCloudInstance;

/**
 * @author Manish
 */
public class AuthenticatedControllerTest extends WebTestsBaseWithMockConnectors {

  private ModelMap map;

  @Autowired
  private WorkflowController workFLowController;
  
  @Autowired
  private APIProxyController apiProxyController;

  @Autowired
  private ConfigurationService configurationService;

  private MockHttpServletRequest request;

  private MockHttpServletResponse response;

  private final BootstrapActivator bootstrapActivator = new BootstrapActivator();

  private APIHandler apiHandler;

  private APICall apiCall;

  private APIResponseObject apiResponseObject;
  @Before
  public void init() throws Exception {
    map = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  private void prepareAPIMock(boolean valid) {
    super.prepareMock(true, bootstrapActivator);
    MockCloudInstance mock = this.getMockCloudInstance();
    CloudConnector cloudConnector = mock.getCloudConnector();

    CloudServiceApiException cse = new CloudServiceApiException();


    EasyMock.reset(cloudConnector);
    apiHandler = EasyMock.createMock(APIHandler.class);
    apiCall = EasyMock.createMock(APICall.class);
    EasyMock.reset(apiHandler);
    EasyMock.reset(apiCall);

    EasyMock.expect(cloudConnector.getAPIHandler(EasyMock.anyObject(String.class))).andReturn(apiHandler).anyTimes();
    EasyMock
        .expect(apiHandler.getAPICall(EasyMock.anyObject(HttpServletRequest.class), EasyMock.anyObject(User.class)))
        .andReturn(apiCall).anyTimes();

    EasyMock.expect(apiHandler.getAPIAccessDecisionVoters()).andReturn(new ArrayList<APIAccessDecisionVoter>())
        .anyTimes();
    
    if (valid) {
      apiResponseObject = new APIResponseObject(200, new HashMap<String, String>(), "DONE!");
      EasyMock.expect(apiHandler.execute(apiCall)).andReturn(apiResponseObject).once();
    } else {
      apiResponseObject = new APIResponseObject(500, new HashMap<String, String>(), "FAILED!");
      EasyMock.expect(apiHandler.execute(apiCall)).andThrow(cse).once();
      EasyMock.expect(apiHandler.getErrorResponse(apiCall, cse, 500)).andReturn(apiResponseObject).once();
    }

    EasyMock.replay(apiHandler);
    EasyMock.replay(apiCall);
    EasyMock.replay(cloudConnector);

  }

  @Test
  public void testSetPageAsRoot() throws Exception {
    asRoot();
    Page page = Page.DASHBOARD;
    workFLowController.setPage(map, page);
    Assert.assertEquals(map.get("page"), page);
    Assert.assertEquals("on", map.get(page.getLevel1().name()));
    Assert.assertEquals("on", map.get(page.getLevel2().name()));
    Assert.assertTrue(map.get("top_nav_health_status") != null);
    Assert.assertTrue(map.get("top_nav_cs_instances") != null);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSetPageAsUserWithNoCloudAccount() throws Exception {
    asRoot();
    Tenant tenant = createTenantWithOwner();

    asUser(tenant.getOwner());

    Page page = Page.DASHBOARD;
    workFLowController.setPage(map, page);
    Assert.assertEquals(map.get("page"), page);
    Assert.assertEquals("on", map.get(page.getLevel1().name()));
    Assert.assertEquals("on", map.get(page.getLevel2().name()));
    Assert.assertTrue(map.get("top_nav_health_status").equals("NORMAL"));
    Assert.assertTrue(map.get("top_nav_cs_instances") != null);
    Assert.assertTrue(((List<Map<String, String>>) map.get("top_nav_cs_instances")).size() == 0);
  }
  
  @Test
  public void testRequest() {
    prepareAPIMock(true);
    String apiSuffix = "CCP-API";
    request.setPathInfo("/portal/CCP-API/test");
    apiProxyController.request(apiSuffix, request, response);
    // As request method does not return anything. Verifying here the call count
    EasyMock.verify(apiHandler);
    EasyMock.verify(apiCall);

    // Cloud Service API exception case
    prepareAPIMock(false);
    apiProxyController.request(apiSuffix, request, response);
    // As request method does not return anything. Verifying here the call count
    EasyMock.verify(apiHandler);
    EasyMock.verify(apiCall);
  }

  @Test
  public void testDisplayCookiesWarning(){
    Tenant tenant = createTenantWithOwner();

    asUser(tenant.getOwner());
    
    /**
     * Initially cookie warning is disabled hence dont show the warning
     */
    Assert.assertFalse(workFLowController.displayCookiesWarning());
    
    com.vmops.model.Configuration isCookieWarningEnabled = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_cookies_warning_enabled);
    isCookieWarningEnabled.setValue("true");
    configurationService.update(isCookieWarningEnabled);
    
    /**
     * After enabling the cookie warning, display the warning when user has not accepted it
     */
    Assert.assertTrue(workFLowController.displayCookiesWarning());
    
    tenant.getOwner().setAcceptedCookies(true);
    
    /**
     * Do not display the warning when user has already accepted it
     */
    Assert.assertFalse(workFLowController.displayCookiesWarning());
  }

}
