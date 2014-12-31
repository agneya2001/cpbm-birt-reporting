<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/tiles/register/js_messages.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.selectboxes.min.js"></script>
<c:set var="countries" scope="request" value="${registration.countryList}"/>
<jsp:include page="/WEB-INF/jsp/tiles/shared/country_states.jsp"></jsp:include>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/jquery.stateselect.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/register.js"></script>
