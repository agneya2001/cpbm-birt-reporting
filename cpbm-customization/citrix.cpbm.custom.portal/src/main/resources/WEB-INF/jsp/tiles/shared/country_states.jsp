<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<script language="javascript">
if( typeof country_states === 'undefined' ) {
  var country_states = {};
  <c:forEach var="country" items="${countries}">
  	<c:if test="${not empty country.stateCodes}">
  		country_states["<c:out value='${country.countryCode2}'/>"] = {
        '':"<spring:message code='label.choose'/>",
		<c:forEach var="state" items="${country.stateCodes}">
			'<c:out value="${state}"/>':"<spring:message code='${country.countryCode2}.${state}'/>",
		</c:forEach>
  		};
	</c:if>
  </c:forEach>
}
</script>