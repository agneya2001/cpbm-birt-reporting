<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<div class="secondlevel_withsubmenu">
    <div class="secondlevel_breadcrumb_panel">
      <div class="secondlevel_breadcrumbbox_tenant">
          <p title="${tenant.name}"><c:out value="${tenant.name}"/></p>
      </div>
      <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>
    </div>        
     <div class="secondlevel_menupanel">
          
          <spring:url value="/portal/tenants/editcurrent" var="tenants_path" htmlEscape="false"></spring:url>
          <sec:authorize  access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_CRUD')">
            <a id="general" class="secondlevel_menutabs <c:out value="${General}"/>" href="<c:out value="${tenants_path}" escapeXml="false" />"><spring:message code="page.level2.general"/></a>
          </sec:authorize>
          <sec:authorize  access="hasAnyRole('ROLE_ACCOUNT_ADMIN','ROLE_ACCOUNT_CRUD')">
            <a id="contact" class="secondlevel_menutabs <c:out value="${Contact}"/>" ><spring:message code="page.level2.contact"/></a>
          </sec:authorize>
           <sec:authorize  access="hasAnyRole('ROLE_USER')">
            <c:if test="${currentUser.param eq tenant.owner.param}">
              <a id="apiCredentials" class="secondlevel_menutabs <c:out value="${APICredentials}"/>" ><span class="loginicon"></span><spring:message code="page.level2.apicredentials"/></a>
            </c:if>
          </sec:authorize>
          <c:if test="${tenant.accountType.name != 'SYSTEM' && tenantForm.tenant.accountType.name != 'Trial'}">
          	<a id="billingDetails" class="secondlevel_menutabs <c:out value="${BillingDetails}"/>" ><spring:message code="page.level2.billingdetails"/></a>
          </c:if>
		                   
	      <sec:authorize access="hasRole('ROLE_ACCOUNT_ADMIN')">
	        <c:if test="${tenant.accountId != '00000000'}">
	        	<a id="history" class="secondlevel_menutabs <c:out value="${History}"/>"><spring:message code="ui.label.tenant.view.stateChanges"/></a>
	        </c:if>
	      </sec:authorize> 
    </div>
    <div class="clearboth"></div>

</div>