<!-- Copyright 2013 Citrix Systems, Inc. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div>
    <ul class="service_health_list">
    <c:forEach items="${healthStatusMapForServiceInstances}" var="healthMap" varStatus="status">
        <li id="cloudService_<c:out value="${healthMap.id}" />" onclick="view_service_health(this)" title=<spring:message code="${healthMap.tooltip}"/>>
            <span class="icon servicehealth <c:out value="${healthMap.status}" />"></span><span class="servicename ellipsis"><a title="${healthMap.name}" href="javascript:void(0);"><c:out value="${healthMap.name}" /></a></span>
        </li>
    </c:forEach>
    </ul>
</div>
