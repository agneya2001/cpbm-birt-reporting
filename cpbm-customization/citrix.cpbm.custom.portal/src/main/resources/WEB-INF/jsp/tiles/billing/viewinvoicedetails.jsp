<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="widget_grid details header"  style="display:block;">
  <div class="widget_grid_cell" style="width:30%;">
     <span class="header"><spring:message code="label.invoice.details.charge"/></span>
  </div>
  <div class="widget_grid_cell" style="width:15%;">
     <span class="header"><spring:message code="label.invoice.details.amount"/></span>
  </div> 
  <div class="widget_grid_cell" style="width:15%;">
     <span class="header"><spring:message code="label.invoice.details.quantity"/></span>
  </div> 
  <div class="widget_grid_cell" style="width:20%;">
     <span class="header"><spring:message code="label.invoice.details.service.start.date"/></span>
  </div>
  <div class="widget_grid_cell" >
     <span class="header"><spring:message code="label.invoice.details.service.end.date"/></span>
  </div> 
</div>

<c:forEach var="invoiceItem" items="${invoice.invoiceItemRecords}" varStatus="status">
  <c:choose>
    <c:when test="${status.index % 2 == 0}">
      <c:set var="rowClass" value="odd" />
    </c:when>
    <c:otherwise>
      <c:set var="rowClass" value="even" />
    </c:otherwise>
  </c:choose>

  <div class="<c:out value="widget_grid details ${rowClass}"/>">
  <spring:message code="dateonly.format" var="dateonly_format"/>	
    <div class="widget_grid_cell" style="width: 30%;">
      <span class="list ellipsis" title = "<c:out value="${invoiceItem.charge}" />" style = "width:85%"> <c:out value="${invoiceItem.charge}" /></span>
    </div>
    <div class="widget_grid_cell" style="width: 15%;">
      <span class="list">
       <c:out value="${invoice.tenant.currency.sign}" />
      	<fmt:formatNumber pattern="${currencyFormat}"  minFractionDigits="${minFractionDigits}" value="${invoiceItem.chargeAmount}"  />
      </span>
    </div>
    <div class="widget_grid_cell" style="width: 15%;">
      <span class="list"><c:out value="${invoiceItem.quantity}" /></span>
    </div>
    <div class="widget_grid_cell" style="width: 20%;">
      <span class="list">
      <fmt:formatDate value="${invoiceItem.serviceStartDate}" pattern="${dateonly_format}" />
      </span>
    </div>
    <div class="widget_grid_cell" >
      <span class="list">
      <fmt:formatDate value="${invoiceItem.serviceEndDate}" pattern="${dateonly_format}" />
      </span>
    </div>
  </div>
</c:forEach>

