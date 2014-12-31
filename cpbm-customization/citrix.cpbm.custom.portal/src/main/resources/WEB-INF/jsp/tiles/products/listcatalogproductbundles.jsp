<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:forEach var="productBundleRevision" items="${productBundleRevisions}" varStatus="status">
		<c:set var="index" value="${status.index + lastBundleNo}" scope="request"></c:set>
		<c:set var="productBundleRevision" value="${productBundleRevision}" scope="request"></c:set>
		<c:set var="entitlements" value="${productBundleRevision.entitlements}" scope="request"></c:set>
		<c:set var="currencies" value="${supportedCurrencies}" scope="request"></c:set>
		<c:set var="fullBundlePricingMap" value="${fullBundlePricingMap}" scope="request"></c:set>
		<c:set var="arechargestobeshown" value="${productBundleRevision.productBundle.rateCard}" scope="request"></c:set>
		<c:set var="actionstoshow" value="${actiontoshow}" scope="request"></c:set>
		<c:set var="toalloweditprices" value="${toalloweditprices}" scope="request"></c:set>
		<c:set var="noDialog" value="true" scope="request"></c:set>

		<jsp:include page="catalogproductbundleview.jsp"></jsp:include>

</c:forEach>
