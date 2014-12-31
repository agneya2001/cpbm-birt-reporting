/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package com.citrix.cpbm.portal.fragment.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citrix.cpbm.platform.model.SsoObject;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.citrix.cpbm.platform.spi.SsoHandler;
import com.citrix.cpbm.platform.spi.View;
import com.citrix.cpbm.platform.spi.ViewResolver;
import com.vmops.model.Tenant;
import com.vmops.model.User;
import com.vmops.service.exceptions.CloudServiceException;
import com.vmops.web.controllers.AbstractAuthenticatedController;
import com.vmops.web.interceptors.UserContextInterceptor;

public abstract class AbstractManageResourceController extends AbstractAuthenticatedController {

  private Logger logger = Logger.getLogger(AbstractManageResourceController.class);

  @RequestMapping(value = "/get_resource_views", method = RequestMethod.GET)
  @ResponseBody
  public List<View> getResourceViews(@ModelAttribute("currentTenant") Tenant currentTenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID, ModelMap map,
      HttpServletRequest request) {
    ViewResolver viewResolver = ((CloudConnector) connectorManagementService.getServiceInstance(serviceInstanceUUID))
        .getViewResolver();
    User user = getCurrentUser();
    if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
      if (userService.getUserHandleByServiceInstanceUuid(user.getUuid(), serviceInstanceUUID) != null) {
        user = tenantService.get(tenantParam).getOwner();
      } else {
        throw new CloudServiceException(messageSource.getMessage("message.no.cloud.account", new String[] {
          user.getUsername()
        }, getSessionLocale(request)));
      }
    }
    List<View> resourceViewList = viewResolver.listResourceViews(user);
    map.addAttribute("resourceViews", resourceViewList);
    return resourceViewList;
  }

  @RequestMapping(value = "/get_sso_cmd_string", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> getSSOCmdString(@ModelAttribute("currentTenant") Tenant tenant,
      @RequestParam(value = "tenant", required = false) String tenantParam,
      @RequestParam(value = "serviceInstanceUUID", required = true) String serviceInstanceUUID, ModelMap map,
      HttpServletRequest request, HttpServletResponse response) {

    logger.debug("### In getSSOCmdString  start method (POST)...");
    SsoObject ssoObject = new SsoObject();
    Tenant userTenant = (Tenant) request.getAttribute(UserContextInterceptor.EFFECTIVE_TENANT_KEY);
    Map<String, String> responseMap = new HashMap<String, String>();
    if (userTenant.getState() == Tenant.State.NEW) {
      responseMap.put("status", "fail");
      if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
        responseMap.put("error_message",
            messageSource.getMessage("message.user.no.billing", null, getSessionLocale(request)));
        responseMap.put("url", "/portal/portal/home");
      } else {
        responseMap.put("url", "/portal/portal/tenants/editcurrent");
      }
    } else {
      // if user logged in as root and try to access cloud resources under specific account then using the specific
      // account and account owner.
      SsoHandler ssoHandler = ((CloudConnector) connectorManagementService.getServiceInstance(serviceInstanceUUID))
          .getSSOHandler();
      if (ssoHandler != null) {
        if ((Boolean) request.getAttribute("isSurrogatedTenant")) {
          logger.trace("###SPI Calling SsoHandler.handleLogin(User) for user:" + userTenant.getOwner());
          ssoObject = ssoHandler.handleLogin(userTenant.getOwner());
          logger.trace("###SPI Called SsoHandler.handleLogin(User) for user:" + userTenant.getOwner());
        } else {
          logger.trace("###SPI Calling SsoHandler.handleLogin(User) for user:" + getCurrentUser());
          ssoObject = ssoHandler.handleLogin(getCurrentUser());
          logger.trace("###SPI Calling SsoHandler.handleLogin(User) for user:" + getCurrentUser());
        }
        if (ssoObject != null && ssoObject.getCookies().size() > 0) {
          for (Cookie cookie : ssoObject.getCookies()) {
            response.addCookie(cookie);
            logger.debug("Sending cookies:" + cookie.getName() + " domain:" + cookie.getDomain());
          }
        }
        responseMap.put("callback", ssoObject.getCallBack());
        responseMap.put("status", "success");
        responseMap.put("cmdString", ssoObject.getSsoString());
      } else {
        // if a connector is not implementing a ssoHandler, return success
        responseMap.put("status", "success");
      }
    }
    logger.debug("### getSSOCmdString method ending...(POST)");
    return responseMap;
  }

}
