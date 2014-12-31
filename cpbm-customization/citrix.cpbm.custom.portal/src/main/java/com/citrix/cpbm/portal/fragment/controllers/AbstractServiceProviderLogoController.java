/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmops.model.Tenant;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.TenantService;
import com.vmops.web.controllers.AbstractBaseController;

public abstract class AbstractServiceProviderLogoController extends AbstractBaseController {

// CHECKSTYLE:OFF
  @Autowired
  protected TenantService tenantService;

// CHECKSTYLE:ON
  private Logger logger = Logger
      .getLogger(com.citrix.cpbm.portal.fragment.controllers.AbstractServiceProviderLogoController.class);

  @RequestMapping(value = ("/spfavicon"), method = RequestMethod.GET)
  public void getFavicon(HttpServletResponse response) {
    Tenant tenant = tenantService.getSystemTenant();
    logoResponse(tenant.getFaviconPath(), config.getDefaultFavicon(), response);
  }

  @RequestMapping(value = ("/splogo"), method = RequestMethod.GET)
  public void getTenantLogo(HttpServletResponse response) {
    Tenant tenant = tenantService.getSystemTenant();
    logoResponse(tenant.getImagePath(), config.getDefaultTenantLogo(), response);
  }

  private void logoResponse(String imagePath, String defaultImagePath, HttpServletResponse response) {
    FileInputStream fileinputstream = null;
    String rootImageDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    if (rootImageDir != null && !rootImageDir.trim().equals("")) {
      try {
        if (imagePath != null && !imagePath.trim().equals("")) {
          String absoluteImagePath = FilenameUtils.concat(rootImageDir, imagePath);
          fileinputstream = new FileInputStream(absoluteImagePath);
          if (fileinputstream != null) {
            int numberBytes = fileinputstream.available();
            byte bytearray[] = new byte[numberBytes];
            fileinputstream.read(bytearray);
            response.setContentType("image/" + FilenameUtils.getExtension(imagePath));
            // TODO:Set Cache headers for browser to force browser to cache to reduce load
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
        logger.debug("###File not found in retrieving logo");
      } catch (IOException e) {
        logger.debug("###IO Error in retrieving logo");
      }
    }
    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    response.setHeader("Location", defaultImagePath);
  }
}