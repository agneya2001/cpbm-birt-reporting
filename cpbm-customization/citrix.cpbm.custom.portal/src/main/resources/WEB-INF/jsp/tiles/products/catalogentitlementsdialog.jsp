<!-- Copyright 2014 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<div class="dialog_formcontent entitlementlightbox" >

  <div class="details_lightboxformbox" style="height:auto; max-height: 348px; width: 300px; border-bottom:none;">
<c:choose>
  <c:when test="${not empty  entitlements}">
    <ul id="totalentitlments">
    <c:forEach var="entitlement" items="${entitlements}">
      <li style="color: #000">
        <span class="text ellipsis" style="margin-top: 14px;">
          <strong>
      <c:choose>
        <c:when test="${entitlement.includedUnits == -1}">
            <spring:message code="label.bundle.list.entitlement.unlimited"/>
        </c:when>
        <c:otherwise>
            <c:out value="${entitlement.includedUnits}"/>
        </c:otherwise>
      </c:choose>
          </strong>
          &nbsp;<c:out value="${entitlement.product.uom}" />&nbsp;<spring:message code="label.of"/>&nbsp;
          <c:out value="${entitlement.product.name}" />
        </span>
      </li>
    </c:forEach>
    </ul>
  </c:when>
  <c:otherwise>
    <ul id="totalentitlments">
      <li style="color: #000; margin-top: 14px">
        <spring:message code="message.bundle.details.dialog.no.entitlements" />
      </li>
    </ul>
  </c:otherwise>
</c:choose>
  </div>
