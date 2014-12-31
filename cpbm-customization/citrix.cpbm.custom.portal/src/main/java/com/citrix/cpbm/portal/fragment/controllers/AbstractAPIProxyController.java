/*
*  Copyright © 2013 Citrix Systems, Inc.
*  You may not use, copy, or modify this file except pursuant to a valid license agreement from
*  Citrix Systems, Inc.
*/
package com.citrix.cpbm.portal.fragment.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.citrix.cpbm.platform.admin.service.ConnectorManagementService;
import com.citrix.cpbm.platform.spi.APICall;
import com.citrix.cpbm.platform.spi.APIHandler;
import com.citrix.cpbm.platform.spi.APIResponseObject;
import com.citrix.cpbm.platform.spi.CloudConnector;
import com.vmops.model.APIHandle;
import com.vmops.model.ServiceInstance;
import com.vmops.portal.security.UnanimousAPIAccessDecisionManager;
import com.vmops.service.APIRegistryService;
import com.vmops.service.exceptions.CloudServiceApiException;
import com.vmops.service.exceptions.CloudServiceException;
import com.vmops.web.controllers.AbstractAuthenticatedController;

public class AbstractAPIProxyController extends AbstractAuthenticatedController {

  @Autowired
  private APIRegistryService apiRegistryService;

  @Autowired
  private ConnectorManagementService connectorManagementService;

  private Logger logger = Logger.getLogger(AbstractAPIProxyController.class);

  @RequestMapping(value = {
      "/{apiSuffix}", "/{apiSuffix}/", "/{apiSuffix}/**"
  })
  public void request(@PathVariable String apiSuffix, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
    String urlFragment = getUrlFragment(apiSuffix, httpRequest.getPathInfo());
    handleRequest(apiSuffix, urlFragment, httpRequest, httpResponse);
  }

  private String getUrlFragment(String apiSuffix, String path) {
    String splitter = apiSuffix + "/";
    String urlFragment = null;
    int index = path.indexOf(splitter);
    if (index != -1) {
      String subPath = path.substring(index + splitter.length());
      if (subPath.length() > 0) {
        urlFragment = subPath;
      }
    }
    return urlFragment;
  }

  private void handleRequest(String apiSuffix, String urlFragment, HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {
    APIHandle apiHandle = apiRegistryService.getAPIHandleByHandle(apiSuffix);
    ServiceInstance serviceInstance = apiHandle.getServiceInstance();
    CloudConnector cloudConnector = (CloudConnector) connectorManagementService.getServiceInstance(serviceInstance
        .getUuid());
    APIHandler apiHandler = cloudConnector.getAPIHandler(apiHandle.getName());
    UnanimousAPIAccessDecisionManager unanimousAPIAccessDecisionManager = new UnanimousAPIAccessDecisionManager();
    unanimousAPIAccessDecisionManager.setDecisionVoters(apiHandler.getAPIAccessDecisionVoters());
    APIResponseObject apiResponseObject;
    APICall apiCall = apiHandler.getAPICall(httpRequest, getCurrentUser());
    try {
      unanimousAPIAccessDecisionManager.decide(apiCall);
      apiResponseObject = apiHandler.execute(apiCall);
    } catch (AccessDeniedException ade) {
      CloudServiceApiException cloudServiceApiException = new CloudServiceApiException(401, ade.getMessage());
      apiResponseObject = apiHandler.getErrorResponse(apiCall, cloudServiceApiException, 401);
    } catch (CloudServiceApiException csae) {
      apiResponseObject = apiHandler.getErrorResponse(apiCall, csae, 500);
    } catch (Exception e) {
      if(e instanceof CloudServiceException){
        throw (CloudServiceException) e;
      }
      CloudServiceApiException cloudServiceApiException = new CloudServiceApiException("Internal Server Error", e);
      cloudServiceApiException.setErrorCode(500);
      apiResponseObject = apiHandler.getErrorResponse(apiCall, cloudServiceApiException, 500);
    }
    // Set response headers
    Map<String, String> headers = apiResponseObject.getHeaders();
    if (headers != null) {
      for (String key : headers.keySet()) {
        httpResponse.setHeader(key, headers.get(key));
      }
    }
    // Set Response status
    httpResponse.setStatus(apiResponseObject.getStatus());
    // Write API response content to stream
    OutputStream outputStream = null;
    try {
      outputStream = httpResponse.getOutputStream();
      String responseBody = apiResponseObject.getResponseBody();
      if (responseBody != null) {
        byte[] bytes = responseBody.getBytes();
        httpResponse.setContentLength(bytes.length);
        outputStream.write(bytes);
      }
    } catch (IOException e) {
      logger.error("Error: IO exception in writing to output stream: " + e.getMessage(), e);
    } finally {
      try {
        if (outputStream != null) {
          outputStream.flush();
          outputStream.close();
        }
      } catch (IOException e) {
        logger.error("Error: while flushing/closing: " + e.getMessage(), e);
      }
    }
  }
}
