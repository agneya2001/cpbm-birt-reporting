<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="netInvoiceItem" value="null"></c:set>
<spring:message code="dateonly.format" var="date_only_format"/>
<c:set var="invoiceUuid" value="${invoice.uuid}"></c:set>
<c:set var="invoiceType" value="${invoice.type}"></c:set>
<c:set var="invoiceitems" value="${invoice.invoiceItems}"></c:set>
<fmt:formatDate var="invoiceStartDate" pattern="${date_only_format}" value="${invoice.serviceStartDate}" timeZone="${currentUser.timeZone}"/>
<fmt:formatDate var="invoiceEndDate" pattern="${date_only_format}" value="${invoice.serviceEndDate}" timeZone="${currentUser.timeZone}"/>

<spring:message code="label.invoice.details" var="invoiceDetailsString"/>
<c:set var="invoiceDetailTitle" value="${invoiceDetailsString} (${invoiceStartDate} - ${invoiceEndDate})"></c:set>
<div  id="dialog_invoice_details_<c:out value='${invoiceUuid}'/>" title="${invoiceDetailTitle}" style="display: none; overflow: hidden;" class="dialog_invoice_details">
   <c:choose>
   <c:when test="${invoiceType eq 'CloseOut'}">
     <jsp:include page="closeout_invoice_details.jsp"></jsp:include>
   </c:when>
   <c:when test="${invoiceType eq 'Adjustment'}">
      <jsp:include page="adjustment_invoice_details.jsp"></jsp:include>
   </c:when>
   <c:otherwise>
        <jsp:include page="other_invoice_details.jsp"></jsp:include>
   </c:otherwise>
   </c:choose>
</div>