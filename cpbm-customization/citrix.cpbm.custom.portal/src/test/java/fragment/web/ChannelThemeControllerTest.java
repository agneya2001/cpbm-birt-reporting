/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.DispatcherServlet;

import web.WebTestsBase;

import com.citrix.cpbm.portal.fragment.controllers.ChannelThemeController;
import com.vmops.model.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ConfigurationService;
import com.vmops.web.interceptors.PortalSessionThemeResolverImpl;
import com.vmops.web.interceptors.PortalThemeResolver;

public class ChannelThemeControllerTest extends WebTestsBase {

  private HttpServletResponse response;

  private HttpServletRequest request;

  private HttpSession httpSession;

  private PortalThemeResolver themeResolver;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ChannelThemeController channelThemeController;

  @Before
  public void init() throws Exception {
    request = EasyMock.createMock(HttpServletRequest.class);
    response = EasyMock.createMock(HttpServletResponse.class);
    httpSession = EasyMock.createMock(HttpSession.class);
    themeResolver = new PortalSessionThemeResolverImpl();
    ServletOutputStream os = EasyMock.createMock(ServletOutputStream.class);
    EasyMock.expect(request.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE)).andReturn(themeResolver)
        .anyTimes();
    EasyMock.expect(response.getOutputStream()).andReturn(os).anyTimes();
    EasyMock.expect(request.getSession(false)).andReturn(httpSession).anyTimes();
    EasyMock.expect(httpSession.getAttribute("PortalSessionThemeResolverImpl.PREVIEW_CHANNEL_CODE")).andReturn(null)
        .anyTimes();
    httpSession.setAttribute(EasyMock.anyObject(String.class), EasyMock.anyObject());
    response.setContentType(EasyMock.anyObject(String.class));
    response.setContentLength(EasyMock.anyInt());
    response.setStatus(EasyMock.anyInt());
    response.setHeader(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class));
    EasyMock.replay(request);
    EasyMock.replay(response);
    EasyMock.replay(httpSession);

    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources\\9fc7754c-6d46-11e0-a026-065287aed31a\\");

  }

  @Test
  public void testLocateResource() {
    Map<String, Object> globalConfiguration = new HashMap<String, Object>();
    globalConfiguration.put("hasProductCrudAuthority", true);
    channelThemeController.locateResource(globalConfiguration, "1", "poli", "jpg", response, request);
    channelThemeController.locateResource(globalConfiguration, "1", "poli", "css", response, request);
  }
}