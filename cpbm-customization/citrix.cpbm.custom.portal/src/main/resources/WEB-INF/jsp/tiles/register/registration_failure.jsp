<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="login_headerarea">
  <div class="login_headerarea_left">
        <%@ include file="../shared/channel_logo.jsp" %>
      </div>     
  </div>
  <div class="login_maincontentarea">
    <div class="login_maincontentarea_titlepanel">
      <h1><spring:message code="label.registration.failure"/></h1>
    </div> 
    <div class="login_errorbox">                
      <p><spring:message code="message.registration.failure" arguments="${globalConfiguration['serviceProviderSupportEmailAddress']}"/></p> 
      <div class="clearboth"></div>
      <br/>
      <a href="<%= request.getContextPath() %>/portal/"><spring:message code="message.reset.password.login"/></a>
      <br/>
    </div>
</div>  

