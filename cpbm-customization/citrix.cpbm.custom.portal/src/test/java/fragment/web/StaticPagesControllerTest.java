/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import web.WebTestsBase;
import web.support.DispatcherTestServlet;

import com.citrix.cpbm.portal.fragment.controllers.StaticPagesController;
import com.vmops.service.UserService;

public class StaticPagesControllerTest extends WebTestsBase {

  private ModelMap map;

  @Before
  public void init() throws Exception {
    map = new ModelMap();

  }

  @Autowired
  private StaticPagesController controller;

  @Autowired
  private UserService service;

  @Test
  public void testLandingRouting() throws Exception {
    logger.debug("Testing routing....");
    DispatcherTestServlet servlet = this.getServletInstance();
    Method expected = locateMethod(controller.getClass(), "handle", new Class[] {
      HttpServletRequest.class
    });

    String[] valid = new String[] {
        "/errors/notfound", "/errors/error", "/errors/something"
    };
    for (String uri : valid) {
      Method handler = servlet.recognize(getRequestTemplate(HttpMethod.GET, uri));
      Assert.assertEquals(expected, handler);
    }
  }

  @Test
  public void testErrorPages() {
    MockHttpServletRequest request = getRequestTemplate(HttpMethod.GET, "/errors/error");
    request.addHeader("Referer", "REFERER_MARKER");
    ModelAndView mav = controller.handle(request);
    ModelAndViewAssert.assertViewName(mav, null);
    ModelAndViewAssert.assertModelAttributeValue(mav, "ref", "REFERER_MARKER");
    ModelAndViewAssert.assertModelAttributeValue(mav, "uri", "/errors/error");
  }

  @Test
  public void testhelp() {
    String help = controller.help(map);
    Assert.assertNotNull(map.get("tenant"));
    Assert.assertEquals(help, new String("main.help"));
  }

  @Test
  public void testfaqs() {
    String faqs = controller.faqs(map);
    Assert.assertNotNull(map.get("tenant"));
    Assert.assertEquals(faqs, new String("main.faqs"));
  }

  @Test
  public void testcontactUs() {
    String contactus = controller.contactUs(map);
    Assert.assertNotNull(map.get("tenant"));
    Assert.assertEquals(contactus, new String("main.contactus"));
  }

  @Test
  public void testtermsAndConditions() {
    String termsAndConditions = controller.termsAndConditions(map);
    Assert.assertNotNull(map.get("tenant"));
    Assert.assertEquals(termsAndConditions, new String("main.termsAndConditions"));
  }
}
