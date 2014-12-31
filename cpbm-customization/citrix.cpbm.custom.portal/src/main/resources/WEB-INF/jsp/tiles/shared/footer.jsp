<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="footer_linksbox">
  <p><a href="<%=request.getContextPath() %>/portal/health"><spring:message code="ui.footer.page.title.servicehealth"/></a> | <a href="<c:out value="${footerLinks.support}"/>"><spring:message code="ui.footer.page.title.support"/></a> | <a href="<c:out value="${footerLinks.blog}"/>"><spring:message code="ui.footer.page.title.blog"/></a> | <a href="<c:out value="${footerLinks.forum}"/>"><spring:message code="ui.footer.page.title.forum"/></a> | <a href="<c:out value="${footerLinks.contactUs}"/>"><spring:message code="ui.footer.page.title.contactus"/></a></p>
  <div class="clearboth"></div>
  <p><spring:message code="footer.copyright.message" htmlEscape="false" /> | <a href="<c:out value="${footerLinks.privacyPolicy}"/>"><spring:message code="ui.footer.page.title.privacypolicy"/></a> | <a href="<c:out value="${footerLinks.termsOfUse}"/>"><spring:message code="ui.footer.page.title.termsofuse"/></a></p>
</div>
<div style="text-align:right">
  <sec:authorize access="hasRole('ROLE_ACCOUNT_CRUD')">
  
    <a href="<%=request.getContextPath() %>/license/index.html" target="_blank"><c:out value="${globalConfiguration['version.id']}"></c:out> </a> (<c:out value="${globalConfiguration['build.id']}"></c:out>)
  
      
  </sec:authorize>
</div>
