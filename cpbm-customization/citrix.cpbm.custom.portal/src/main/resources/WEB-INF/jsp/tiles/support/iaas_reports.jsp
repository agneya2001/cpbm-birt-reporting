<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/report.js"></script>
<jsp:include page="js_messages.jsp"></jsp:include>
<div class="maintitlebox">
        <h1><c:out value="${report.title}"/> </h1>
</div>
<div class="clearboth"></div>
<sec:authentication property="principal.authorities" var="userAuths"/>
<sec:authentication property="principal.username" var="username"/>
<sec:authentication property="principal.profile" var="profile"/>

<iframe src="/birt/index.jsp?username=${username}&userauth=${userAuths}&profile=${profile}" height=800px width=100% frameborder=0>
</iframe>

<div class="clearboth"></div>
