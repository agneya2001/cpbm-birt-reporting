<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
  <!-- Home Page Gravtars -->
<c:forEach items="${usersForGravatar}" var="user" varStatus="status">
  <div class="db_users_profilepic">
  <c:catch var="signalException">
    <a style="text-decoration: none;"
      href="<%=request.getContextPath() %>/portal/users/<c:out value="${user.param}"></c:out>/myprofile"> 
      <img src=<c:out value="${user.gravatarUrl}"></c:out> 
        title="<c:out value="${user.firstName}"></c:out> <c:out value="${user.lastName}"></c:out>, <spring:message code="profileName.${fn:replace(user.profile.name, ' ', '')}"/>"
      width="34 px" height="34 px" border="0"></img>
    </a>
    </c:catch>
  </div>
</c:forEach>