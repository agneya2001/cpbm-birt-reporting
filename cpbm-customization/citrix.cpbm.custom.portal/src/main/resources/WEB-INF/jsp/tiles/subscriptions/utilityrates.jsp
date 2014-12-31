<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
  <!-- Utility Charges -->
<div class="utilityrateslist_container" style="overflow:auto;">
<spring:message code="dateonly.format" var="date_format"/>  
<spring:message code="ui.label.report.generate" var="generate"/>
<spring:message code="label.catalog.utilityrate.charges.for" var="chargesfor"/>
<spring:message code="label.catalog.utilityrate.all.charges" var="allcharges"/>
 <h2 style="margin-left: 10px;"><spring:message code="label.catalog.utilityrate.title"/></h2>   
 <h3 style="margin-right: 10px; width: 260px; margin-left: 350px;"><spring:message code="label.catalog.utilityrate.effective.date"/>     
  <fmt:formatDate value="${startDate}" pattern="${date_format}" timeZone="${currentUser.timeZone}"/></h3>
  <div class="utilityrateslist_box">
    <c:if test="${topReturnMap != null}">
      <h3 style="margin-left: 10px; margin-right: 530px; margin-bottom: 5px; width: 200px;"><c:out value="${chargesfor}"></c:out><c:out value="${resourceTypeName}"></c:out></h3>
      <c:set var="mapToDisplay" value="${topReturnMap}" scope="request"></c:set>
      <c:set var="showGenerated" value="false" scope="request"></c:set>
      <jsp:include page="utilityratediv.jsp"></jsp:include>
    </c:if>
    <c:if test="${retMap != null}">
      <h3 style="margin-left: 10px; margin-right: 530px; margin-bottom: 5px; width: 200px;"><c:out value="${allcharges}"></c:out></h3>
      <c:set var="mapToDisplay" value="${retMap}" scope="request"></c:set>
      <c:set var="showGenerated" value="true" scope="request"></c:set>
      <jsp:include page="utilityratediv.jsp"></jsp:include>
    </c:if>
    </div>
 </div>  
             