<!-- Copyright 2014 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
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
    <h2 style="width:auto;"><spring:message code="message.error.download.file"/></h2>
    <br /> <br />
    <h3 style="float:left;"><a href="javascript:history.back()"><spring:message code="ui.home.page.title.go.to.channels"/></a></h3>
  </div>
</div>
