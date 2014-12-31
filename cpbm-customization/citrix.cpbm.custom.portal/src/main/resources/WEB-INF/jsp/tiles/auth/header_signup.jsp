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

   	<div class="login_maintabsarea">
       	<div class="login_maintabsbox">
            <div class="login_tabs on"><spring:message code="page.title.signup"/></div>
           <a class="login_tabs off" href="<%= request.getContextPath() %>/portal/login"><spring:message code="page.title.login"/></a>
         </div>
    </div>

        
