<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="secondlevel_withsubmenu">
    <div class="secondlevel_breadcrumb_panel">
      <div class="secondlevel_breadcrumbbox_tenant">
          <p title="${tenant.name}"><c:out value="${tenant.name}"/></p>
      </div>
      <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>
    </div>
     <div class="secondlevel_menupanel">
        <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_CRUD','ROLE_ACCOUNT_MGMT')">
          <spring:url value="/portal/tenants/list?accountType=" var="tenants_list" htmlEscape="false">
            <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
          </spring:url>
          <a class="secondlevel_menutabs <c:out value="${AllAccounts}"/>" href="<c:out value="${tenants_list}"/>"><spring:message code="page.level2.allaccounts"/></a>
          <c:forEach items="${accountTypes}" var="choice" varStatus="status">
            <c:if test="${choice.id != 1}">
            <spring:url value="/portal/tenants/list" var="tenants_list" htmlEscape="false">
              <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
              <spring:param name="accountType"><c:out value="${choice.id}"/></spring:param>
            </spring:url>
            
           
            <a class="secondlevel_menutabs
              <c:if test="${selectedTab eq choice.name}">
              <c:out value="${on}"/>
              </c:if>
              " href="<c:out value="${tenants_list}"/>"><spring:message code="page.level2.${choice.nameLower}"/></a>
            </c:if>
          </c:forEach>
        </sec:authorize>
   </div>
   <div class="clearboth"></div>
</div>