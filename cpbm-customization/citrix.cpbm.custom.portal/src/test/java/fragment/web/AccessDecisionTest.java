/*
 * Copyright © 2013 Citrix Systems, Inc. You may not use, copy, or modify this file except pursuant to a valid license
 * agreement from Citrix Systems, Inc.
 */
package fragment.web;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import web.WebTestsBase;

import com.vmops.model.User;

public class AccessDecisionTest extends WebTestsBase {

  private AccessDecisionManager manager;

  @Autowired
  private FilterSecurityInterceptor interceptor;

  private MockHttpServletResponse response = new MockHttpServletResponse();

  private MockHttpServletRequest request;

  @Before
  public void init() {
    request = createRequest(null);
    manager = interceptor.getAccessDecisionManager();
  }

  private static final FilterChain DUMMY_CHAIN = new FilterChain() {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
      throw new UnsupportedOperationException();
    }
  };

  @Test
  public void testAnonymousUrls() throws Exception {
    Authentication auth = createAnonymousToken();
    Object[][] anonymousAccessValid = {
        {
            HttpMethod.GET, "/portal/portal/"
        }, {
            HttpMethod.GET, "/portal/portal"
        }, {
            HttpMethod.GET, "/portal/portal/login"
        }, {
            HttpMethod.POST, "/portal/portal/login"
        }, {
            HttpMethod.GET, "/portal/portal/getGoogleAnalytics"
        }, {
            HttpMethod.GET, "/portal/portal/register"
        }, {
            HttpMethod.POST, "/portal/portal/register"
        }, {
            HttpMethod.GET, "/portal/portal/validate_username"
        }, {
            HttpMethod.GET, "/portal/portal/loggedout"
        }, {
            HttpMethod.GET, "/portal/portal/reset_password"
        }, {
            HttpMethod.POST, "/portal/portal/reset_password"
        }, {
            HttpMethod.GET, "/portal/portal/verify_email"
        }, {
            HttpMethod.GET, "/portal/portal/verify_user"
        }
    };
    Object[][] anonymousAccessInvalid = {
        {
            HttpMethod.GET, "/portal/portal/home"
        }, {
            HttpMethod.GET, "/portal/portal/profile"
        }, {
            HttpMethod.GET, "/portal/portal/profile/edit"
        }, {
            HttpMethod.POST, "/portal/portal/profile"
        }, {
            HttpMethod.GET, "/portal/portal/users"
        }, {
            HttpMethod.POST, "/portal/portal/users"
        }, {
            HttpMethod.GET, "/portal/portal/users/new"
        }, {
            HttpMethod.GET, "/portal/portal/users/1"
        }, {
            HttpMethod.PUT, "/portal/portal/users/1"
        }, {
            HttpMethod.GET, "/portal/portal/tenants"
        }, {
            HttpMethod.POST, "/portal/portal/tenants"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/new"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/1"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/1/edit"
        }, {
            HttpMethod.PUT, "/portal/portal/tenants/1"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/1/"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/approval-task/1"
        }, {
            HttpMethod.POST, "/portal/portal/tasks/approval-task"
        }, {
            HttpMethod.POST, "/portal/portal/acceptCookies"
        }
    };
    verify(auth, anonymousAccessValid, anonymousAccessInvalid);
  }

  @Test
  public void testAuthenticatedUrls() {
    Object[][] authenticatedAccessValid = {
        {
            HttpMethod.GET, "/portal/portal/"
        }, {
            HttpMethod.GET, "/portal/portal/home"
        }, {
            HttpMethod.GET, "/portal/portal/profile"
        }, {
            HttpMethod.GET, "/portal/portal/profile/edit"
        }, {
            HttpMethod.POST, "/portal/portal/profile"
        }, {
            HttpMethod.POST, "/portal/portal/acceptCookies"
        }, {
            HttpMethod.GET, "/portal/portal/users"
        }, {
            HttpMethod.GET, "/portal/portal/users/new"
        }, {
            HttpMethod.POST, "/portal/portal/users"
        }, {
            HttpMethod.GET, "/portal/portal/users/1"
        }, {
            HttpMethod.PUT, "/portal/portal/users/1"
        }, {
            HttpMethod.GET, "/portal/portal/tenants"
        }, {
            HttpMethod.POST, "/portal/portal/tenants"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/new"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/1"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/1/edit"
        }, {
            HttpMethod.PUT, "/portal/portal/tenants/1"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/1/"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/approval-task/1"
        }, {
            HttpMethod.POST, "/portal/portal/tasks/approval-task"
        }
    };

    User user = getRootUser();
    Authentication auth = createAuthenticationToken(user);
    verify(auth, authenticatedAccessValid, null);
  }

  @Test
  public void testOwnerUrls() {
    Object[][] valid = {
        {
            HttpMethod.GET, "/portal/portal/"
        }, {
            HttpMethod.GET, "/portal/portal/home"
        }, {
            HttpMethod.GET, "/portal/portal/profile"
        }, {
            HttpMethod.GET, "/portal/portal/profile/edit"
        }, {
            HttpMethod.POST, "/portal/portal/profile"
        }, {
            HttpMethod.POST, "/portal/portal/acceptCookies"
        }, {
            HttpMethod.GET, "/portal/portal/users"
        }, {
            HttpMethod.GET, "/portal/portal/users/new"
        }, {
            HttpMethod.POST, "/portal/portal/users"
        }, {
            HttpMethod.GET, "/portal/portal/users/1/myprofile"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/1/edit"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/1/"
        }, {
            HttpMethod.GET, "/portal/portal/tasks/approval-task/1"
        }, {
            HttpMethod.POST, "/portal/portal/tasks/approval-task"
        }
    };

    Object[][] invalid = {
        {
            HttpMethod.GET, "/portal/portal/tenants"
        }, {
            HttpMethod.POST, "/portal/portal/tenants"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/new"
        },
    };

    User user = getDefaultTenant().getOwner();
    Authentication auth = createAuthenticationToken(user);
    verify(auth, valid, invalid);
  }

  @Test
  public void testUserUrls() {
    Object[][] valid = {
        {
            HttpMethod.GET, "/portal/portal/"
        }, {
            HttpMethod.GET, "/portal/portal/home"
        }, {
            HttpMethod.GET, "/portal/portal/profile"
        }, {
            HttpMethod.GET, "/portal/portal/profile/edit"
        }, {
            HttpMethod.POST, "/portal/portal/profile"
        }, {
            HttpMethod.POST, "/portal/portal/acceptCookies"
        }
    };

    Object[][] invalid = {
        {
            HttpMethod.GET, "/portal/portal/users/1"
        }, {
            HttpMethod.PUT, "/portal/portal/users/1"
        }, {
            HttpMethod.GET, "/portal/portal/users"
        }, {
            HttpMethod.GET, "/portal/portal/users/new"
        }, {
            HttpMethod.POST, "/portal/portal/users"
        }, {
            HttpMethod.GET, "/portal/portal/tenants"
        }, {
            HttpMethod.POST, "/portal/portal/tenants"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/new"
        }, {
            HttpMethod.GET, "/portal/portal/tenants/1/edit"
        }, {
            HttpMethod.PUT, "/portal/portal/tenants/1"
        }
    };

    User user = createTestUserInTenant(getDefaultTenant());
    Authentication auth = createAuthenticationToken(user);
    verify(auth, valid, invalid);
  }

  private void verify(Authentication auth, Object[][] valid, Object[][] invalid) {
    if (valid != null) {
      for (Object[] testcase : valid) {
        verifyAccess(auth, (HttpMethod) testcase[0], (String) testcase[1], true);
      }
    }
    if (invalid != null) {
      for (Object[] testcase : invalid) {
        verifyAccess(auth, (HttpMethod) testcase[0], (String) testcase[1], false);
      }
    }
  }

  private void verifyAccess(Authentication auth, HttpMethod method, String uri, boolean valid) {
    request.setMethod(method.name());
    request.setRequestURI(uri);
    FilterInvocation invocation = new FilterInvocation(request, response, DUMMY_CHAIN);
    Collection<ConfigAttribute> attrs = interceptor.getSecurityMetadataSource().getAttributes(invocation);
    try {
      manager.decide(auth, invocation, attrs);
      if (!valid) {
        Assert.fail("Access granted for " + uri + " [" + method.name() + "] for user " + auth.getName());
      }
    } catch (AccessDeniedException ex) {
      if (valid) {
        Assert.fail("Access denied for " + uri + " [" + method.name() + "] for " + auth.getName());
      }
    } catch (InsufficientAuthenticationException ex) {
      Assert.fail("Insufficient authentication for " + uri + " [" + method.name() + "] for " + auth.getName());
    }
  }

  private MockHttpServletRequest createRequest(String uri) {
    MockHttpServletRequest mockRequest = getRequestTemplate(HttpMethod.GET, uri);
    mockRequest.setContextPath("/portal");
    mockRequest.setServletPath(null);
    return mockRequest;
  }
}
