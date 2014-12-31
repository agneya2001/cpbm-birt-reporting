/*
 * Copyright © 2014 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import web.WebTestsBase;

import com.citrix.cpbm.portal.fragment.controllers.HomeController;
import com.vmops.model.User;
import com.vmops.service.ConfigurationService;
import com.vmops.service.SupportService;
import com.vmops.web.interceptors.UserContextInterceptor;

/**
 * @author manish
 */
public class UserContextInterceptorTest extends WebTestsBase {

  private HttpServletRequest request;

  private HttpServletResponse response;

  private ModelAndView mav;

  @Autowired
  private ApplicationContext context;

  @Autowired
  private HomeController handler;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private SupportService supportService;

  private final UserContextInterceptor uci = new UserContextInterceptor();

  @Before
  public void init() throws Exception {
    mav = new ModelAndView();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    context.getAutowireCapableBeanFactory().autowireBean(uci);
    ((MockHttpServletRequest) request).setServletPath("/portal");
    ((MockHttpServletRequest) request).setPathInfo("/home");
    uci.setSuccessEndpointURL("/portal/home");
  }

  @Test
  public void testPreHandle() throws Exception {
    User user = userService.getUserByParam("id", 3, false);
    Assert.assertTrue(user.isEnabled());
    asUser(user);
    Assert.assertTrue(uci.preHandle(request, response, handler));
  }

  @Test
  public void testDoFilter() {
    asRoot();
    ((MockHttpServletRequest) request).setParameter(UserContextInterceptor.EFFECTIVE_TENANT_KEY,
        "CF319413-5DD7-4040-81FE-E2B1BBCF57F6");
    ((MockHttpServletRequest) request).setParameter(UserContextInterceptor.TENANT_PARAM,
        "CF319413-5DD7-4040-81FE-E2B1BBCF57F6");
    uci.doFilter(request, response, handler);
    Assert.assertTrue(request.getAttribute("isAdmin") != null);
    Assert.assertTrue((Boolean) request.getAttribute("isAdmin"));
    Assert.assertTrue(request.getAttribute("isSurrogatedTenant") != null);
    Assert.assertTrue((Boolean) request.getAttribute("isSurrogatedTenant"));
    Assert.assertEquals(tenantService.get("CF319413-5DD7-4040-81FE-E2B1BBCF57F6"),
        request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY));
  }

  @Test
  public void testPostHandle() throws Exception {
    uci.postHandle(request, response, handler, mav);
    Assert.assertEquals(supportService.getTicketCapability().name(),
        mav.getModel().get(UserContextInterceptor.TICKET_CAPABILITIES));
    // Need to write more asserts
  }

}
