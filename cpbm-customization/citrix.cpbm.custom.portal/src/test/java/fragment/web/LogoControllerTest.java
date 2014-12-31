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
import org.springframework.ui.ModelMap;

import web.WebTestsBase;

import com.citrix.cpbm.portal.fragment.controllers.LogoController;
import com.vmops.model.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ConfigurationService;

public class LogoControllerTest extends WebTestsBase {

  private ModelMap map;

  private HttpServletResponse response;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private LogoController logoController;

  @Before
  public void init() throws Exception {
    map = new ModelMap();
    response = new MockHttpServletResponse();
    Configuration configuration = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    configuration.setValue("src\\test\\resources\\9fc7754c-6d46-11e0-a026-065287aed31a\\");

    Configuration config = configurationService
        .locateConfigurationByName(Names.com_citrix_cpbm_portal_settings_services_datapath);
    config.setValue("src\\test\\resources\\9fc7754c-6d46-11e0-a026-065287aed31a\\");

  }

  @Test
  public void testGetTenantFavicon() {

    logoController.getTenantFavicon("a667305a-1345-46bc-83d4-a6f3adcf33aa", map, response);
    checkCorrectnessForNullPath();

    response = new MockHttpServletResponse();
    logoController.getTenantFavicon("dfc84388-d44d-4d8e-9d6a-a62c1c16b7e4", map, response);

    checkCorrectnessOfMethod(response);
  }

  @Test
  public void testGetTenantLogo() {

    logoController.getTenantLogo("a667305a-1345-46bc-83d4-a6f3adcf33aa", map, response);
    checkCorrectnessForNullPath();

    response = new MockHttpServletResponse();
    logoController.getTenantLogo("dfc84388-d44d-4d8e-9d6a-a62c1c16b7e4", map, response);
    checkCorrectnessOfMethod(response);
  }

  @Test
  public void testGetUserLogo() {

    logoController.getUserLogo("B3B26BBF-119C-4F42-9DD3-36B6478D35E8", map, response);
    checkCorrectnessForNullPath();

    response = new MockHttpServletResponse();
    logoController.getUserLogo("0e04370a-e407-490f-8256-40c4e8c4fb6b", map, response);
    checkCorrectnessOfMethod(response);
  }

  @Test
  public void testGetServiceInstanceLogo() {

    logoController.getServiceInstanceLogo("003fa8ee-fba3-467f-a517-ed806dae8a85", map, response);
    checkCorrectnessForNullPath();

    response = new MockHttpServletResponse();
    logoController.getServiceInstanceLogo("003fa8ee-fba3-467f-a517-fd806dae8a80", map, response);
    checkCorrectnessOfMethod(response);
  }

  @Test
  public void testGetProductLogo() {

    logoController.getProductLogo("2", map, response);
    checkCorrectnessForNullPath();

    response = new MockHttpServletResponse();
    logoController.getProductLogo("1", map, response);
    checkCorrectnessOfMethod(response);

  }

  @Test
  public void testGetProductBundlesLogo() {

    logoController.getProductBundlesLogo("3", map, response);
    checkCorrectnessForNullPath();

    response = new MockHttpServletResponse();
    logoController.getProductBundlesLogo("2", map, response);
    checkCorrectnessOfMethod(response);

  }

  @Test
  public void testGetServiceLogo() {

    response = new MockHttpServletResponse();
    logoController.getServiceLogo("b1c9fbb0-8dab-42dc-ae0a-ce13ec84a1e4", "logo", map, response);
    checkCorrectnessOfMethod(response);

  }

  @Test
  public void testGetChannelLogo() {

    response = new MockHttpServletResponse();
    logoController.getChannelLogo("1", "false", null, map, response);
    checkCorrectnessOfMethod(response);

    response = new MockHttpServletResponse();
    logoController.getChannelLogo("3", "true", null, map, response);
    checkCorrectnessOfMethod(response);
  }

  @Test
  public void testGetChannelFavicon() {

    response = new MockHttpServletResponse();
    logoController.getChannelFavicon("1", "false", map, response);
    checkCorrectnessOfMethod(response);

    response = new MockHttpServletResponse();
    logoController.getChannelFavicon("3", "true", map, response);
    checkCorrectnessOfMethod(response);
  }

  @Test
  public void testGetChannelImage() {

    logoController.getChannelImage("1", "", map, response);
    checkCorrectnessOfMethod(response);

  }

  private void checkCorrectnessOfMethod(HttpServletResponse response) {
    Assert.assertTrue(response.getContentType().contains("image/"));
    Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  private void checkCorrectnessForNullPath() {
    Assert.assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
  }
}