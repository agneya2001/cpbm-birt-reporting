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
         <c:if test="${ currentTenant.id != 1}">
          <sec:authorize access="hasAnyRole('ROLE_USER_CRUD','ROLE_ACCOUNT_USER_CRUD','ROLE_ACCOUNT_BILLING_ADMIN')">
            <spring:url value="/portal/users/listusersforaccount" var="users_list" htmlEscape="false">
              <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
            </spring:url>
            <a class="secondlevel_menutabs <c:out value="${AllUsers}"/>" href="<c:out value="${users_list}"/>"><spring:message code="page.level2.allusers"/></a>
           </sec:authorize>
           </c:if>
           
	 </div>
   <div class="clearboth"></div>

  <tiles:insertDefinition name="warnings"></tiles:insertDefinition> 

</div>
                     
