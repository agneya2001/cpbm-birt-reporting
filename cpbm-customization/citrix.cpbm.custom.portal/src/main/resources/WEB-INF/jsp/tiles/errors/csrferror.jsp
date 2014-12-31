<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set var="home_path" value="/home"/>
<div class="error_box" style="height: 200px">
  <div class="login_headerarea">
    <div class="login_headerarea_left">
      <%@ include file="../shared/channel_logo.jsp"%>
    </div>
  </div>

 <div class="error_maincontentarea">
    <div class="error_maincontentarea_titlepanel">
      <h1><spring:message code="ui.error.csrf.exploit.detected" /></h1>
    </div>
    <div class="login_errorbox">
      <div class="clearboth"></div>
      <br /> <a href="<%= request.getContextPath() %>/portal<c:out value="${home_path}"/>"><spring:message code="ui.error.page.support.return.home" /></a> <br />
    </div>
  </div>
</div>
