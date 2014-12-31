/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import web.WebTestsBase;

import com.citrix.cpbm.portal.fragment.controllers.ServiceProviderLogoController;
import com.vmops.model.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ConfigurationService;

public class ServiceProviderLogoControllerTest extends WebTestsBase {

  private HttpServletResponse response;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ServiceProviderLogoController serviceProviderLogoController;

  @Before
  public void init() throws Exception {
    response = new MockHttpServletResponse();
    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources\\9fc7754c-6d46-11e0-a026-065287aed31a\\");

  }

  @Test
  public void testGetTenantLogo() {
    serviceProviderLogoController.getTenantLogo(response);
    Assert.assertTrue(response.getContentType().contains("image/"));
    Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  public void testGetFavicon() {
    serviceProviderLogoController.getFavicon(response);
    Assert.assertTrue(response.getContentType().contains("image/"));
    Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }
}