<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<fmt:setLocale value="${pageContext.request.locale}" scope="request"/>
<c:choose>
  <c:when test="${currentUser != null}">
    <c:set var="home_path" value="/home"/>
  </c:when>
  <c:otherwise>
    <c:set var="home_path" value="/"/>
  </c:otherwise>
</c:choose>

   	<div class="login_maintabsarea" <c:if test="${directoryServiceAuthenticationEnabled || showSuffixControl}">  style="display: None" </c:if>>
       	<div class="login_maintabsbox">
            <c:if test="${showSignupLink}">
              <a id="signup_tab" style="display: None" class="login_tabs off" href="<%= request.getContextPath() %>/portal/account_type"><spring:message code="page.title.signup"/></a>
            </c:if>
            
            <div class="login_tabs on"><spring:message code="page.title.login"/></div>
         </div>
    </div>

        
