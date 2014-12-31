/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tiles.definition.NoSuchDefinitionException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ThemeResolver;

import web.WebTestsBase;

import com.citrix.cpbm.platform.exception.CloudServiceExceptionWrapper;
import com.citrix.cpbm.portal.fragment.controllers.RegistrationController;
import com.vmops.service.exceptions.AjaxFormValidationException;
import com.vmops.service.exceptions.CloudServiceException;
import com.vmops.service.exceptions.CreditCardFraudCheckException;
import com.vmops.service.exceptions.CurrencyPrecisionException;
import com.vmops.service.exceptions.IPtoCountryException;
import com.vmops.service.exceptions.InvalidAjaxRequestException;
import com.vmops.service.exceptions.NoSuchTenantException;
import com.vmops.service.exceptions.NoSuchUserException;
import com.vmops.service.exceptions.SubscriptionServiceException;
import com.vmops.service.exceptions.UserAuthorizationInvalidException;
import com.vmops.service.exceptions.api.ApiException;
import com.vmops.service.exceptions.api.ChangeTenantStateApiException;
import com.vmops.service.exceptions.api.FetchUserApiException;
import com.vmops.service.exceptions.api.ListProductBundlesApiException;
import com.vmops.service.exceptions.api.ListSubscriptionsApiException;
import com.vmops.service.exceptions.api.ListTenantsApiException;
import com.vmops.service.exceptions.api.RegisterTenantApiException;
import com.vmops.service.exceptions.api.SubscribeToProductBundleApiException;
import com.vmops.service.exceptions.api.UpdateTenantApiException;
import com.vmops.service.exceptions.api.UpdateUserApiException;
import com.vmops.service.exceptions.api.VerifyUserExistenceApiException;
import com.vmops.web.interceptors.PortalSessionThemeResolverImpl;

public class AbstractBaseControllerTest extends WebTestsBase {

  @Autowired
  private RegistrationController controller;

  @BeforeClass
  public static void initMail() {
    setupMail();
  }

  @Test
  public void testHandlePageNotFound() {
    String viewName = "errors/notfound";

    ModelAndView mav = controller.handlePageNotFound(new IllegalArgumentException(), new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, viewName);

    mav = controller.handlePageNotFound(new NoSuchUserException(), new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, viewName);

    mav = controller.handlePageNotFound(new NoSuchTenantException(), new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, viewName);

    mav = controller.handlePageNotFound(new NoSuchDefinitionException(), new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, viewName);

    mav = controller.handlePageNotFound(new UserAuthorizationInvalidException(), new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, viewName);
  }

  @Test
  public void testHandleApiException() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    ModelAndView mav = controller.handleApiException(new ApiException(), request);
    Assert.assertNotNull(mav.getModelMap());

    mav = controller.handleApiException(new CloudServiceExceptionWrapper(), request, new MockHttpServletResponse());
    Assert.assertNotNull(mav.getModelMap());

    mav = controller.handleApiException(new UpdateUserApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("EditUserResponse"));

    mav = controller.handleApiException(new UpdateTenantApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("EditTenantResponse"));

    mav = controller.handleApiException(new ChangeTenantStateApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("EditTenantStateResponse"));

    mav = controller.handleApiException(new VerifyUserExistenceApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("VerifyUserExistenceResponse"));

    mav = controller.handleApiException(new ListProductBundlesApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("ListProductBundlesResponse"));

    mav = controller.handleApiException(new SubscribeToProductBundleApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("SubscribeToProductBundleResponse"));

    mav = controller.handleApiException(new ListTenantsApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("ListTenantsResponse"));

    mav = controller.handleApiException(new FetchUserApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("FetchUserResponse"));

    mav = controller.handleApiException(new RegisterTenantApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("RegisterAccountResponse"));

    mav = controller.handleApiException(new ListSubscriptionsApiException(), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("ListSubscriptionsResponse"));
  }

  @Test
  public void testHandleCurrencyPrecisionException() {
    ModelAndView mav = controller.handleCurrencyPrecisionException(new CurrencyPrecisionException(),
        new MockHttpServletRequest(), new MockHttpServletResponse());
    Assert.assertNotNull(mav);
  }

  @Test
  public void testHandleMissingServletRequestParameterException() {
    ModelAndView mav = controller.handleMissingServletRequestParameterException(
        new MissingServletRequestParameterException("tenantParam", "String"), new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("editTenantState");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("EditTenantStateResponse"));

    request.setRequestURI("editTenant");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("EditTenantResponse"));

    request.setRequestURI("/tenants/fetch");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("FetchTenantResponse"));

    request.setRequestURI("/tenants");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());

    request.setRequestURI("registerAccount");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("RegisterAccountResponse"));

    request.setRequestURI("editUser");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("EditUserResponse"));

    request.setRequestURI("/users/fetch");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("FetchUserResponse"));

    request.setRequestURI("/users/search");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());

    request.setRequestURI("/verifyUserExistence");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("VerifyUserExistenceResponse"));

    request.setRequestURI("/productBundles/subscribe");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("SubscribeToProductBundleResponse"));

    request.setRequestURI("productBundles");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("ListProductBundlesResponse"));

    request.setRequestURI("/subscriptions");
    mav = controller.handleMissingServletRequestParameterException(new MissingServletRequestParameterException(
        "tenantParam", "String"), request);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertNotNull(mav.getModelMap().get("ListSubscriptionsResponse"));

  }

  @Test
  public void testHandleAjaxFormValidationException() {
    FieldError error = new FieldError("tenant", "teannt", "Invalid Tenant");
    List<ObjectError> lstError = new ArrayList<ObjectError>();
    lstError.add(error);
    BindingResult result = EasyMock.createMock(BindingResult.class);
    EasyMock.expect(result.getAllErrors()).andReturn(lstError).anyTimes();
    EasyMock.replay(result);
    Errors errors = new BindException(result);

    MockHttpServletResponse response = new MockHttpServletResponse();
    ModelAndView mav = controller.handleAjaxFormValidationException(new AjaxFormValidationException(errors),
        new MockHttpServletRequest(), response);
    Assert.assertNotNull(mav.getModelMap());
    Assert.assertEquals(420, response.getStatus());
    // ModelAndViewAssert.assertViewName(mav, expectedName);
  }

  @Test
  public void testHandleAccessDeniedException() {
    AccessDeniedException ex = new AccessDeniedException("test");
    ModelAndView mav = controller.handleAccessDeniedException(ex, new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, "errors/notauthorized");
  }

  @Test
  public void testHandleInvalidAjaxRequestException() {
    InvalidAjaxRequestException ex = new InvalidAjaxRequestException("test");
    ModelAndView mav = controller.handleInvalidAjaxRequestException(ex, new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, "errors.messagepage");
    ModelAndViewAssert.assertModelAttributeAvailable(mav, "errorMessage");
    ModelAndViewAssert.assertModelAttributeValue(mav, "errorMessage", ex.getMessage());

    ModelAndView mav1 = controller.handleInvalidAjaxRequestException(new InvalidAjaxRequestException(
        new CreditCardFraudCheckException("exception", new Exception(), "10")), new MockHttpServletRequest());

    Assert.assertNotNull(mav1);
  }

  @Test
  public void testHandleCloudServiceException() {
    ModelAndView mav = controller
        .handleCloudServiceException(new CloudServiceException(), new MockHttpServletRequest());
    Assert.assertNotNull(mav);

    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    EasyMock.expect(request.getHeader("X-Requested-With")).andReturn("XMLHttpRequest").anyTimes();
    EasyMock.replay(request);
    mav = controller.handleCloudServiceException(new CloudServiceException(), request);
    Assert.assertNotNull(mav);

  }

  @Test
  public void testHandleSubscriptionServiceException() {
    ModelAndView mav = controller.handleSubscriptionServiceException(new SubscriptionServiceException(),
        new MockHttpServletRequest(), new MockHttpServletResponse());
    Assert.assertNotNull(mav);

    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    EasyMock.expect(request.getHeader("X-Requested-With")).andReturn("XMLHttpRequest").anyTimes();
    EasyMock.expect(request.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE)).andReturn(null).anyTimes();
    EasyMock.replay(request);
    mav = controller.handleSubscriptionServiceException(new SubscriptionServiceException(), request,
        new MockHttpServletResponse());
    Assert.assertNotNull(mav);
  }

  @Test
  public void testHandleGenericException() {
    Exception ex = new Exception("generic");
    MockHttpServletRequest request = new MockHttpServletRequest();
    ModelAndView mav = controller.handleGenericException(ex, request);
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, "errors/error");
    ModelAndViewAssert.assertModelAttributeAvailable(mav, "errorMessage");
    ModelAndViewAssert.assertModelAttributeValue(mav, "errorMessage",
        messageSource.getMessage("ui.error.page.server.error.message", null, null));

    ex = new NullPointerException("NPE generic");
    mav = controller.handleGenericException(ex, request);
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, "errors/error");
    ModelAndViewAssert.assertModelAttributeAvailable(mav, "errorMessage");
    ModelAndViewAssert.assertModelAttributeValue(mav, "errorMessage",
        messageSource.getMessage("ui.error.page.server.error.message", null, null));

    request.addHeader("X-Requested-With", "XMLHttpRequest");
    mav = controller.handleGenericException(ex, request);
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, "errors.messagepage");
    ModelAndViewAssert.assertModelAttributeAvailable(mav, "errorMessage");
    ModelAndViewAssert.assertModelAttributeValue(mav, "errorMessage",
        messageSource.getMessage("ui.error.page.server.error.message", null, null));

    ex = new NullPointerException("NPE generic");
    mav = controller.handleGenericException(ex, request);
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, "errors.messagepage");
    ModelAndViewAssert.assertModelAttributeAvailable(mav, "errorMessage");
    ModelAndViewAssert.assertModelAttributeValue(mav, "errorMessage",
        messageSource.getMessage("ui.error.page.server.error.message", null, null));

  }

  @Test
  public void testHandleServiceException() {
    IPtoCountryException ex = new IPtoCountryException("IPtoCountryException");
    ModelAndView mav = controller.handleServiceException(ex, new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, "errors/serviceerror");
    ModelAndViewAssert.assertModelAttributeAvailable(mav, "serviceerror");
    ModelAndViewAssert.assertModelAttributeValue(mav, "serviceerror", ex.getMessage());

    CreditCardFraudCheckException cex = new CreditCardFraudCheckException("CreditCardFraudCheckException");
    mav = controller.handleServiceException(cex, new MockHttpServletRequest());
    Assert.assertNotNull(mav.getModelMap());
    ModelAndViewAssert.assertViewName(mav, "errors/serviceerror");
    ModelAndViewAssert.assertModelAttributeAvailable(mav, "serviceerror");
    ModelAndViewAssert.assertModelAttributeValue(mav, "serviceerror", cex.getMessage());

  }

  @Test
  public void testListSupportedLocales() {
    List<Locale> lstLocale = controller.listSupportedLocales();
    Assert.assertNotNull(lstLocale);
  }

  @Test
  public void testGetLocaleDisplayName() {
    List<Locale> lstLocale = controller.listSupportedLocales();
    Map<Locale, String> displayMap = controller.getLocaleDisplayName(lstLocale);
    Assert.assertNotNull(displayMap);

    String displayName = controller.getLocaleDisplayName(lstLocale.get(0));
    Assert.assertNotNull(displayName);
  }

  @Test
  public void testGetpreviewModeSettings() {
    HttpServletRequest request = prepareHttpRequestForChannelBrandingMock();
    Map<String, Object> returnMap = controller.getpreviewModeSettings(request);
    Assert.assertNotNull(returnMap);
    Assert.assertEquals(returnMap.get("previewChannelName"), "Channel-15");

  }

  @Test
  public void testGetGlobalConfigurations() {
    HttpServletRequest request = prepareHttpRequestForChannelBrandingMock();
    Map<String, Object> returnMap = controller.getGlobalConfigurations(request);
    Assert.assertNotNull(returnMap);
  }

  private HttpServletRequest prepareHttpRequestForChannelBrandingMock() {
    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    HttpSession httpSession = EasyMock.createMock(HttpSession.class);
    ThemeResolver themeResolver = new PortalSessionThemeResolverImpl();
    EasyMock.expect(request.getAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE)).andReturn(themeResolver)
        .anyTimes();
    EasyMock.expect(httpSession.getAttribute("PortalSessionThemeResolverImpl.FQDN_NAME")).andReturn("chn1-15")
        .anyTimes();
    EasyMock.expect(httpSession.getAttribute("PortalSessionThemeResolverImpl.PREVIEW_CHANNEL_CODE"))
        .andReturn("Channel-15").anyTimes();
    EasyMock.expect(httpSession.getAttribute("PortalSessionThemeResolverImpl.DELETED_FQDN_NAME")).andReturn(null)
        .anyTimes();

    EasyMock.expect(request.getSession(false)).andReturn(httpSession).anyTimes();
    EasyMock.replay(request);
    EasyMock.replay(httpSession);
    return request;
  }
}
