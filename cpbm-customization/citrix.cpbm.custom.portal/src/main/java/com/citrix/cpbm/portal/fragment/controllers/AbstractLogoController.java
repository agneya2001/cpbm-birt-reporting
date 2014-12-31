/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.citrix.cpbm.platform.admin.service.ConnectorConfigurationManager;
import com.citrix.cpbm.platform.util.CssdkConstants;
import com.vmops.model.Channel;
import com.vmops.model.Product;
import com.vmops.model.ProductBundle;
import com.vmops.model.Service;
import com.vmops.model.ServiceImage;
import com.vmops.model.ServiceInstance;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.portal.config.Configuration;
import com.vmops.portal.config.Configuration.Names;
import com.vmops.service.ChannelService;
import com.vmops.service.ProductBundleService;
import com.vmops.service.ProductService;
import com.vmops.service.UserService;
import com.vmops.web.controllers.AbstractAuthenticatedController;

public abstract class AbstractLogoController extends AbstractAuthenticatedController {

  @Autowired
  private ProductService productService;

  @Autowired
  private ProductBundleService productBundleService;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private ConnectorConfigurationManager connectorConfigurationManagerService;

  @RequestMapping(value = ("/tenant/{tenantParam}/favicon"), method = RequestMethod.GET)
  public void getTenantFavicon(@PathVariable String tenantParam, ModelMap map, HttpServletResponse response) {
    Tenant tenant = tenantService.get(tenantParam);
    logoResponse(tenant.getFaviconPath(), config.getDefaultFavicon(), response);
  }

  @RequestMapping(value = ("/tenant/{tenantParam}"), method = RequestMethod.GET)
  public void getTenantLogo(@PathVariable String tenantParam, ModelMap map, HttpServletResponse response) {
    Tenant tenant = tenantService.get(tenantParam);
    logoResponse(tenant.getImagePath(), config.getDefaultTenantLogo(), response);
  }

  @RequestMapping(value = ("/user/{userParam}"), method = RequestMethod.GET)
  public void getUserLogo(@PathVariable String userParam, ModelMap map, HttpServletResponse response) {
    User user = userService.get(userParam);
    logoResponse(user.getImagePath(), "", response);
  }

  @RequestMapping(value = ("/serviceInstance/{serviceInstanceId}"), method = RequestMethod.GET)
  public void getServiceInstanceLogo(@PathVariable String serviceInstanceId, ModelMap map, HttpServletResponse response) {
    ServiceInstance serviceInstance = connectorConfigurationManagerService.getInstance(serviceInstanceId);
    logoResponse(serviceInstance.getImagePath(), "", response);
  }

  @RequestMapping(value = ("/product/{productId}"), method = RequestMethod.GET)
  public void getProductLogo(@PathVariable String productId, ModelMap map, HttpServletResponse response) {
    Product product = productService.locateProductById(productId);
    logoResponse(product.getImagePath(), "", response);
  }

  @RequestMapping(value = ("/productBundles/{bundleId}"), method = RequestMethod.GET)
  public void getProductBundlesLogo(@PathVariable String bundleId, ModelMap map, HttpServletResponse response) {
    ProductBundle bundle = productBundleService.locateProductBundleById(bundleId);
    logoResponse(bundle.getImagePath(), "", response);
  }

  @RequestMapping(value = ("/connector/{serviceId}/{type}"), method = RequestMethod.GET)
  public void getServiceLogo(@PathVariable String serviceId, @PathVariable String type, ModelMap map,
      HttpServletResponse response) {
    Service service = connectorConfigurationManagerService.getService(serviceId);
    List<ServiceImage> images = new ArrayList<ServiceImage>(service.getImages());
    for (ServiceImage serviceImage : images) {
      if (serviceImage.getImagetype().equals(type)) {
        String cssdkFilesDirectory = FilenameUtils.concat(
            config.getValue(Names.com_citrix_cpbm_portal_settings_services_datapath), service.getServiceName() + "_"
                + service.getVendorVersion());
        logoResponse(FilenameUtils.concat(CssdkConstants.IMAGES_DIRECTORY, serviceImage.getImagepath()), "", response,
            cssdkFilesDirectory);
        return;
      }
    }
  }

  private void logoResponse(String imagePath, String defaultImagePath, HttpServletResponse response) {
    logoResponse(imagePath, defaultImagePath, response, null);
  }

  private void logoResponse(String imagePath, String defaultImagePath, HttpServletResponse response,
      String cssdkFilesDirectory) {
    FileInputStream fileinputstream = null;
    String rootImageDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    if (StringUtils.isNotBlank(cssdkFilesDirectory)) {
      rootImageDir = cssdkFilesDirectory;
    }
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
        logger.debug("###File not found in retrieving logo " + imagePath);
      } catch (IOException e) {
        logger.debug("###IO Error in retrieving logo");
      }
    }
    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    response.setHeader("Location", defaultImagePath);
  }

  @RequestMapping(value = ("/channel/{channelId}"), method = RequestMethod.GET)
  public void getChannelLogo(@PathVariable String channelId,
      @RequestParam(value = "unpublished", required = false) String unpublished, String imageName, ModelMap map,
      HttpServletResponse response) {

    String defaultImagePath = "";
    if (!StringUtils.equals((String) map.get("imageName"), "header")) {
      Channel channel = channelService.getChannelById(channelId);

      String imagePath = channel.getChannelBrandingConfigurations().getLogoImageFileName();
      if (StringUtils.isNotBlank(unpublished) && unpublished.equals("true")) {
        imagePath = channel.getUnPublishedChannelBrandingConfigurations().getLogoImageFileName();
      }
      FileInputStream fileinputstream = null;
      String channelBrandingFilesDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
      if (StringUtils.isNotBlank(channelBrandingFilesDir)) {
        try {
          if (StringUtils.isNotBlank(imagePath)) {
            String absoluteImagePath = FilenameUtils.concat(channelBrandingFilesDir,
                Configuration.CHANNEL_BRANDING_FILES);
            if (StringUtils.isNotBlank(unpublished) && unpublished.equals("true")) {
              absoluteImagePath = FilenameUtils.concat(absoluteImagePath,
                  com.vmops.portal.config.Configuration.UNPUBLISHED_CHANNEL_BRANDING_FILES);
            }
            absoluteImagePath = FilenameUtils.concat(absoluteImagePath, channel.getId().toString());
            absoluteImagePath = FilenameUtils.concat(absoluteImagePath, imagePath);
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
          } else {
            Tenant tenant = tenantService.getSystemTenant();
            imagePath = tenant.getImagePath();
            defaultImagePath = config.getDefaultTenantLogo();
            if (StringUtils.isBlank(defaultImagePath)) {
              imagePath = config.getDefaultTenantLogo();
            }
            logoResponse(imagePath, defaultImagePath, response);
          }
        } catch (FileNotFoundException e) {
          logger.debug("###File not found in retrieving logo " + imagePath);
        } catch (IOException e) {
          logger.debug("###IO Error in retrieving logo");
        }
      }
    } else {
      defaultImagePath = config.getDefaultHeaderImage();
    }

    if (StringUtils.isNotBlank(defaultImagePath)) {
      response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
      response.setHeader("Location", defaultImagePath);
    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  @RequestMapping(value = ("/channel/{channelId}/favicon"), method = RequestMethod.GET)
  public void getChannelFavicon(@PathVariable String channelId,
      @RequestParam(value = "unpublished", required = false) String unpublished, ModelMap map,
      HttpServletResponse response) {
    Channel channel = channelService.getChannelById(channelId);
    String channelBrandingFilesDir = config.getValue(Names.com_citrix_cpbm_portal_settings_images_uploadPath);
    String absoluteImagePath = FilenameUtils.concat(channelBrandingFilesDir, Configuration.CHANNEL_BRANDING_FILES);
    if (StringUtils.isNotBlank(unpublished) && unpublished.equals("true")) {
      absoluteImagePath = FilenameUtils.concat(absoluteImagePath, Configuration.UNPUBLISHED_CHANNEL_BRANDING_FILES);
    }
    absoluteImagePath = FilenameUtils.concat(absoluteImagePath, channel.getId().toString());
    String imagePath = channel.getChannelBrandingConfigurations().getFavIconImageFileName();
    if (StringUtils.isNotBlank(unpublished) && unpublished.equals("true")) {
      imagePath = channel.getUnPublishedChannelBrandingConfigurations().getFavIconImageFileName();
    }
    if (StringUtils.isBlank(imagePath)) {
      Tenant tenant = tenantService.getSystemTenant();
      imagePath = tenant.getFaviconPath();
      if (StringUtils.isBlank(imagePath)) {
        imagePath = config.getDefaultFavicon();

      }
      absoluteImagePath = null;
    }
    logoResponse(imagePath, config.getDefaultFavicon(), response, absoluteImagePath);
  }

  @RequestMapping(value = ("/channel/{channelId}/{imageName}"), method = RequestMethod.GET)
  public void getChannelImage(@PathVariable String channelId, @PathVariable String imageName, ModelMap map,
      HttpServletResponse response) {
    map.addAttribute("imageName", imageName);
    getChannelLogo(channelId, null, imageName, map, response);
  }

}
