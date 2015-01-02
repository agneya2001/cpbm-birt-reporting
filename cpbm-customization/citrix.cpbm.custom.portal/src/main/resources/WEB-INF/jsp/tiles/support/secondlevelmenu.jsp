<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<tiles:insertDefinition name="support.tickets.js_messages"/>

<div class="secondlevel_withsubmenu">
  <div class="secondlevel_breadcrumb_panel">
    <div class="secondlevel_breadcrumbbox_tenant">
      <p title="${tenant.name}"><c:out value="${tenant.name}"/></p>
    </div>
    <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>
  </div>        
  <div class="secondlevel_menupanel">
  <c:choose>
  <c:when test="${isAdmin && tenant.id == 1 && (showHealthTab == null || showHealthTab) }">
  <c:if test="${!empty top_nav_cs_instances}">
    <a  class="secondlevel_menutabs <c:out value="${Health}"/>" href="<%=request.getContextPath() %>/portal/health">
  		<spring:message code="page.level2.systemhealth"/>
  	</a>
  </c:if>
	<c:if test="${ticketCapabilities == 'CRUD'}">
    <sec:authorize access="hasRole('ROLE_TICKET_MANAGEMENT')">
      <spring:url value="/portal/support/tickets" var="tickets_path" htmlEscape="false">
        <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
      </spring:url>   
      <a class="secondlevel_menutabs <c:out value="${Tickets}"/>" href="<c:out value="${tickets_path}"/>">
        <spring:message code="page.level2.tickets"/>
      </a> 
    </sec:authorize>
  </c:if>
  </c:when>
  <c:otherwise>
  <c:if test="${!empty top_nav_cs_instances && (showHealthTab == null || showHealthTab)}">
    <a class="secondlevel_menutabs <c:out value="${Health}"/>" href="<%=request.getContextPath() %>/portal/health">
        <spring:message code="page.level2.health"/>
    </a>
    </c:if>
    <c:if test="${isTicketServiceEnabled}">
          <sec:authorize access="hasAnyRole('ROLE_TICKET_MANAGEMENT')">
            <c:set var="showTicketsTab" value="true" />
            <c:if test="${ticketCapabilities == 'C'}">
                 <c:set var="showTicketsTab" value="false" />
            </c:if>
          </sec:authorize>
          <sec:authorize
            access="hasAnyRole('ROLE_USER_TICKET_MANAGEMENT', 'ROLE_SPL_USER_TICKET_MANAGEMENT', 'ROLE_TENANT_TICKET_MANAGEMENT')">
            <c:if test="${(ticketServiceInstance && ticketCapabilities == 'CRUD') || (ticketCapabilities == 'C') }">
              <c:set var="showTicketsTab" value="true" />
            </c:if>
          </sec:authorize>
     </c:if>
    <c:if test="${showTicketsTab}">
  		<spring:url value="/portal/support/tickets" var="tickets_path" htmlEscape="false">
    		<spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
    	</spring:url>   
    	<a class="secondlevel_menutabs <c:out value="${Tickets}"/>" href="<c:out value="${tickets_path}"/>">
      		<spring:message code="page.level2.tickets"/>
    	</a>
 	</c:if>
  </c:otherwise>
</c:choose>        

  <a  class="secondlevel_menutabs <c:out value="${SupportIaas}" />" href="<%=request.getContextPath() %>/portal/support/iaas_reports">
         <spring:message code="page.level2.iaas.reports"/>
         <span></span>
  </a>
    
  </div>
  <div class="clearboth"></div>
  <sec:authorize access="hasRole('ROLE_TICKET_MANAGEMENT')">
        <c:if test="${showUserProfile}">
          <div class="info" style="margin-top:2px;">
            <spring:message code="account.view.for.admin.profile.message"/>
            <span id="back_to_all_accounts"><a href="/portal/portal/tenants/list" ><spring:message code="label.home.second.level.backtoallaccounts"/></a></span>
          </div>
        </c:if>
      </sec:authorize>     
  <tiles:insertDefinition name="warnings"></tiles:insertDefinition> 
  
</div>  
