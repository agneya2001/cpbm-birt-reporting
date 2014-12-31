<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%-- TODO: cloudAccount is now obsolete; Need to be refactored.
<script type="text/javascript">
  cloudStackDomainid = '<c:out value="${effectiveTenant.owner.cloudAccount.cloudStackDomainId}"/>';
  <c:choose>
    <c:when test="${isSurrogatedTenant}">
      cloudStackAccount = '<c:out value="${effectiveTenant.owner.cloudAccount.accountName}"/>'; 
      cloudStackRole = '2'; 
    </c:when>
    <c:otherwise>
      cloudStackAccount = '<c:out value="${currentUser.cloudAccount.accountName}"/>';
      <c:choose>
        <c:when test="${currentUser == effectiveTenant.owner}">
          cloudStackRole = '2'; 
        </c:when>
        <c:otherwise>
          cloudStackRole = '0';
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</script>--%>

<div class="secondlevel_withsubmenu">
    <div class="secondlevel_breadcrumb_panel">
      <div class="secondlevel_breadcrumbbox_tenant">
          <p title="${tenant.name}"><c:out value="${tenant.name}"/></p>
      </div>
      <div class="doc_help_link"><i class="fa fa-question-circle"></i></div>
    </div>        
     <div class="secondlevel_menupanel">
          <spring:url value="/portal/users/" var="users_path" htmlEscape="false"></spring:url>
          <a id="profile" class="secondlevel_menutabs auto_width <c:out value="${Profile}"/>" href="<c:out value="${users_path}" escapeXml="false" /><c:out value="${user.user.param}" escapeXml="false" />/myprofile"><spring:message code="page.level2.profile"/></a>
          <sec:authorize  access="hasAnyRole('ROLE_USER')">
            <c:if test="${currentUser.param eq user.user.param}">
              <a id="apiCredentials" class="secondlevel_menutabs auto_width <c:out value="${APICredentials}"/>" ><span class="loginicon"></span><spring:message code="page.level2.apicredentials"/></a>
            </c:if>
          </sec:authorize>
          <a id="notPref" class="secondlevel_menutabs auto_width <c:out value="${NotifictaionPrefrences}" />" ><spring:message code="page.level2.notificationpreferences"/></a>
          <a id="emailAddresses" class="secondlevel_menutabs auto_width <c:out value="${EMailAddresses}" />" ><spring:message code="page.level2.emailaddresses"/></a>
          
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

</div>