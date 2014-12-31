<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="error_box" style="height:200px">
  <div class="login_headerarea">
    <div class="login_headerarea_left">
      <%@ include file="../shared/channel_logo.jsp" %>
    </div>
  </div>
  <div style="padding:20px;">
    <h3 style="width:auto;color:red;"><c:out value="${errormsg}"/></h3><br><h3><spring:message code="label.userlocked.support"/>
    <c:out value="${supportPhone}"/>,
    <a href="mailto:<c:out value="${supportEmail}"/>"><c:out value="${supportEmail}"/></a><spring:message code="label.userlocked.generic"/>
    <a href="<c:out value="${supportUrl}"/>"><spring:message code="label.userlocked.site"/></a>.</h3>    
    <br /> <br />
  </div>
</div>