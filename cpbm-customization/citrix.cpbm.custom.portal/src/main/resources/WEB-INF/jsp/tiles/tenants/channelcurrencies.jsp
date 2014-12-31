<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<!-- Adding this page to support i18 for currency names. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
 <option value=""><spring:message code="label.choose"></spring:message> </option>
<c:forEach var="currency" items="${account.currencyValueList}" varStatus="status">
<option value="<c:out value="${currency.currencyCode}"></c:out>"><spring:message code="currency.longname.${currency.currencyCode}"></spring:message></option>
</c:forEach>
                                 