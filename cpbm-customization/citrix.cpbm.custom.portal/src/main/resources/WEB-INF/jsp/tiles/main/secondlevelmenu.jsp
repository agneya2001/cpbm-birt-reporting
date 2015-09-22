<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<input type="hidden" id="l3_tenant_param" value='<c:out value="${tenant.param}"/>'/>
<div class="secondlevel_withsubmenu">
    <div class="secondlevel_breadcrumb_panel">
    	<div class="secondlevel_breadcrumbbox_tenant">
        	<p title="${tenant.name}"><c:out value="${tenant.name}"/></p>
      </div>
      <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>
    </div>        
     <div class="secondlevel_menupanel">
   			<spring:url value="/portal/home" var="dashboard_path" htmlEscape="false">
             	<spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
              <spring:param name="secondLevel">true</spring:param>
         	</spring:url>
			<a class="secondlevel_menutabs <c:out value="${Dashboard}"/>"href="<c:out value="${dashboard_path}" escapeXml="false" />"><spring:message code="page.level2.dashboard"/></a>
      
      <c:if test="${(showUserProfile && currentTenant.id == 1 && (userHasCloudServiceAccount || tenant.state eq 'ACTIVE'))}">
        	<spring:url value="/portal/connector/csinstances" var="connectors_list">
            <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
          </spring:url>
   	  	  <a class="secondlevel_menutabs <c:out value="${Services}"/>"href="<c:out value="${connectors_list}"/>"><spring:message code="page.level2.services"/></a>
      </c:if>
         <c:if test="${(showUserProfile && currentTenant.id == 1)}">
          <sec:authorize access="hasAnyRole('ROLE_USER_CRUD','ROLE_ACCOUNT_USER_CRUD','ROLE_ACCOUNT_BILLING_ADMIN')">
            <spring:url value="/portal/users/listusersforaccount" var="users_list" htmlEscape="false">
              <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
            </spring:url>
            <a class="secondlevel_menutabs <c:out value="${AllUsers}"/>" href="<c:out value="${users_list}"/>"><spring:message code="page.level2.allusers"/></a>
           </sec:authorize>
           </c:if>
           <c:if test="${(showUserProfile && currentTenant.id == 1) || currentTenant.id != 1}">
           <sec:authorize access="hasAnyRole('ROLE_USER_CRUD','ROLE_ACCOUNT_USER_CRUD','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_ACCOUNT_CRUD','ROLE_ACCOUNT_MGMT')">
              <spring:url value="/portal/tenants/alerts" var="all_alerts_path" htmlEscape="false">
                <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
              </spring:url>
              <a class="secondlevel_menutabs <c:out value="${AllAlerts}"/>" href="<c:out value="${all_alerts_path}"/>"><spring:message code="page.level2.allalerts"/></a>
             </sec:authorize> 
           </c:if>
        	<spring:url value="/portal/tenants/notifications" var="all_notifications_path" htmlEscape="false">
        		<spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
      		</spring:url>		
			     <a class="secondlevel_menutabs <c:out value="${AllNotifications}"/>" href="<c:out value="${all_notifications_path}"/>"><spring:message code="page.level2.allnotifications"/></a>
             <spring:url value="/portal/tasks/" var="all_tasks_path" htmlEscape="false">
               <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
             </spring:url>   
             <a class="secondlevel_menutabs <c:out value="${AllTasks}"/>" href="<c:out value="${all_tasks_path}"/>"><spring:message code="page.level2.alltasks"/></a>
     <c:if test="${(showUserProfile && currentTenant.id == 1) || currentTenant.id != 1}">
           <c:set var="normalUser" value="Y"></c:set>
           <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_USER_CRUD','ROLE_ACCOUNT_BILLING_ADMIN','ROLE_FINANCE_CRUD')">
              <c:set var="normalUser" value="N"></c:set>
           </sec:authorize>
          
          <spring:url value="/portal/billing/usageBilling" var="all_usages_path" htmlEscape="false">
            <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
            <c:if test="${normalUser ne 'N' }">
              <spring:param name="useruuid"><c:out value="${currentUser.param}"/></spring:param>
            </c:if>
          </spring:url>   

          <c:if test="${!(showUserProfile && currentTenant.id == 1 && normalUser eq 'Y')}">
            <a class="secondlevel_menutabs <c:out value="${UsageandBilling}"/>" href="<c:out value="${all_usages_path}"/>"><spring:message code="page.level2.usageandbilling"/></a>
          </c:if>
     </c:if>  
      
      <c:if test="${currentTenant.id == 1 &&  tenant.id == 1}">
      <sec:authorize access="hasRole('ROLE_CONFIGURATION_CRUD')">
               <spring:url value="/portal/connector/cs" var="connectors_list" htmlEscape="false">
                 <spring:param name="tenant"><c:out value="${tenant.param}"/></spring:param>
                </spring:url>
               <a class="secondlevel_menutabs <c:out value="${Connectors}"/>" href="<c:out value="${connectors_list}"/>"><spring:message code="page.level2.allservices"/></a>
      </sec:authorize>
      </c:if>
      
	 </div>
     <div class="clearboth"></div>
      <sec:authorize access="hasAnyRole('ROLE_ACCOUNT_CRUD','ROLE_ACCOUNT_MGMT')">
        <c:if test="${showUserProfile}">
          
          <div class="info" style="margin-top:2px;">
            <spring:message code="account.view.for.admin.profile.message"/>
            <span id="back_to_all_accounts"><a href="/portal/portal/tenants/list" ><spring:message code="label.home.second.level.backtoallaccounts"/></a></span>
          </div>
          
        </c:if>
      </sec:authorize>     
      <c:if test="${Dashboard eq 'on' && (currentLocale ne pageContext.request.locale)}">
        
        <div class="info" style="margin-top:2px;">
          <spring:message code="message.view.for.locale.mismatch"/><a href="/portal/portal/users/${currentUser.param}/myprofile" ><spring:message code="page.level2.myprofile"/></a>
        </div>
      </c:if>
      <c:if test="${not empty actionUrlMap}">
       <c:forEach var="entry" items="${actionUrlMap}">
         <div class="clearboth"></div>
         <div class="info" style="margin-top:2px;">
           <spring:message code="message.view.pendingactions.items.${entry.key.type}"/> <a href="${entry.value}"><spring:message code="message.view.pendingactions.click"/></a>
         </div>
       </c:forEach>
      </c:if>
       
  <tiles:insertDefinition name="warnings"></tiles:insertDefinition>
      
</div>