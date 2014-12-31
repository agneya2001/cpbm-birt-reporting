<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${bundleRevision != null || isUtility}">

<!-- Any changes done to the layout of following table needs to be done in convertSubscription.jsp and fragmentbody.jsp as these files contain the layout of this table in the loading view -->

<div class="entitlements_table_container" id="entitlement_container">
  <div class="rc_info_bubble_header" style="border-bottom: 1px dotted #CCCCCC;">${bundleRevision.productBundle.name}</div>
  <div class="rc_info_bubble_desc">${bundleRevision.productBundle.description}</div>
  <div style="overflow: hidden;">
    <div style="max-height: 500px; overflow: auto; float: left; width: 100%;">
      <div class="entitlementlightbox_bundledetails entitlements_div fixed_header_table_wrapper" style="margin:0;width:100%;">
        <div class="utility_table entitlements_table fixed_header_table_inner entitlements_table_inner convert_subscription" id="entitlements_table_container">
          <table id="entitlements_table" class="fixed table table-condensed table-hover table-bordered entitlements_table" style="width:400px;margin-bottom:0px;">
            <col width="115">
            <col width="120">
            <col width="170">
            <thead class="table_header fixedHeader">
              <tr>
                <th><p><spring:message code="label.bundle.list.includes.title" /></p></th>
                <th><p><spring:message code="label.catalog.utility.card.table.header.product" /></p></th>
                <th><p style="width:160px;" class="ellipsis" title='<spring:message code="label.bundle.list.overages.title" />'><spring:message code="label.bundle.list.overages.title" /></p></th>
              </tr>
            </thead>
            <tbody id="totalentitlments" style="overflow-x: hidden;">
            
            <c:choose>
            <c:when test="${!isUtility && not empty bundleRevision.entitlements}">
            <c:forEach items="${bundleRevision.entitlements}" var="entitlement">

                      <spring:message code="${entitlement.product.uom}" var="uom" />
                      <fmt:formatNumber var="formattedPrice" pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${productChargesMap.get(entitlement.product.id).price}" />
                      <c:set var="usageCount" value="${entitlement.includedUnits}"></c:set>
                      <c:choose>
                        <c:when test="${usageCount == -1}">
                          <spring:message code='label.catalog.Unlimited.Usage' var="usage" />
                          <spring:message code='label.Not.Applicable' var="finalPrice" />
                        </c:when>
                        <c:otherwise>
                          <c:set var="usage" value="${entitlement.includedUnits} ${uom}"></c:set>
                          <c:set var="finalPrice" value="${currency.sign}${formattedPrice} / ${uom}"></c:set>
                        </c:otherwise>
                      </c:choose>

                      <tr class='hover_enabled'>
              <td><div class="ellipsis" title="${usage}">${usage}</div></td>
              <td><div class="ellipsis" title="${entitlement.product.name}">${entitlement.product.name}</div></td>
              <td><div style="max-width:160px;" class="ellipsis" title="${finalPrice}">${finalPrice}</div></td>
            </tr>
            </c:forEach>
            </c:when>
            <c:otherwise>
            <spring:message code="message.bundle.details.dialog.no.entitlements" var="no_entitlements_message"/>
            <c:if test="${isUtility}">
            <spring:message code="message.no.entitlements.for.utility" var="no_entitlements_message"/>
            </c:if>
            <tr><td colspan="3"><p class="alert alert-info empty_body">${no_entitlements_message}</p></td></tr>
            </c:otherwise>
            </c:choose>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>
</c:if>
