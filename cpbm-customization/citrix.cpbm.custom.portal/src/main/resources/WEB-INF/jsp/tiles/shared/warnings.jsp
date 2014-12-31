<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:url value="/portal/billing/paymenthistory" var="pay_now_path" htmlEscape="false">
    <spring:param name="tenant"><c:out value="${effectiveTenant.param}"/></spring:param>
</spring:url>   

<c:if test="${showDelinquent}">
  <div class="clearboth"></div>
  <div class ="common_messagebox error" style="width:99%; margin-bottom:0px; padding:5px;">
    <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
      <c:choose>
      <c:when test="${effectiveTenant.state=='TERMINATED'}">
            <spring:message code="message.tenant.account.state.terminated"/>
      </c:when>
      <c:when test="${stateChangeCause == 'PAYMENT_FAILURE'}">
        <spring:message code="message.tenant.delinquent.admin.part1"/>
        <a href="<c:out value="${pay_now_path}"/>">
          <spring:message code="message.tenant.delinquent.admin.part2"/>
        </a>
        <spring:message code="message.tenant.delinquent.admin.part3"/>
      </c:when>
      <c:otherwise>
            <spring:message code="message.tenant.delinquent.admin.contact.support"/>
      </c:otherwise>
      </c:choose>
    </sec:authorize>
    <sec:authorize access="!hasAnyRole('ROLE_ACCOUNT_BILLING_ADMIN', 'ROLE_FINANCE_CRUD')">
      <c:choose>
          <c:when test="${effectiveTenant.state=='TERMINATED'}">
            <spring:message code="message.tenant.account.state.terminated"/>
          </c:when>
          <c:otherwise>
            <spring:message code="message.tenant.delinquent.user" arguments="${effectiveTenant.owner.firstName}, ${effectiveTenant.owner.lastName}" />
          </c:otherwise>
        </c:choose>
    </sec:authorize>
  </div>
</c:if>

<c:if test="${effectiveTenant.state == 'NEW'}">
  <div class="clearboth"></div>
  <div class ="common_messagebox error" style="width:99%; margin-bottom:0px; padding:5px;">    
      <spring:message code="message.tenant.account.state.new">
      </spring:message>
  </div>
</c:if>

<c:if test="${not empty activeTenantScopeWorkflowId}">
  <div class="clearboth"></div>
  <div class ="common_messagebox error" style="width:99%; margin-bottom:0px; padding:5px;">    
      <spring:message code="message.tenant.active.workflow">
      </spring:message>
      <a href="javascript:void(0);" class="workflowDetailsPopup" id="workflowdetails${activeTenantScopeWorkflowId}">
        <spring:message code="message.view.workflow.details.click" />
      </a>
      <div class="workflow_details_popup" title="<c:out value="${title.workflow.details.popup.name}"/>" style="display: none"> </div>
  </div>
</c:if>


<c:if test="${effectiveTenant.id != 1 && showWarningOfServiceInstanceNotEnabled}">
	<sec:authorize access="hasRole('ROLE_ACCOUNT_ADMIN')">
		<c:if test="${!userHasCloudServiceAccount && effectiveTenant.state == 'ACTIVE'}">
		  <div class="clearboth"></div>
		  <div class ="common_messagebox error" style="width:99%; margin-bottom:0px; padding:5px;">   
		    <spring:message code="service.no.instance.warning" htmlEscape="false"
		       arguments="${tenant.param}">
		    </spring:message> 
		  </div>
		</c:if>
	</sec:authorize>
</c:if>