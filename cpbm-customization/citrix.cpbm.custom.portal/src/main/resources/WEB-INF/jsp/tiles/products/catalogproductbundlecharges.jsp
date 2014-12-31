<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="title_width" value="20%" />
<c:set var="currency_width" value="13%" />
<c:if test="${noDialog}">
  <c:set var="title_width" value="23%" />
  <c:set var="currency_width" value="17%" />
</c:if>

<div class="widget_inline_chargesbox" style="overflow:hidden;">
    <div class="widget_grid inline subheader">
        <div class="widget_grid_cell header borders" style="width:<c:out value="${title_width}"/>;">
            <span class="subheader right" style="color:#FFF;"><spring:message code="label.charges"/></span>
        </div>

        <c:set var="currencyCount" value="0" />
        <c:forEach var="currency" items="${currencies}">
            <c:if test="${(currencyCount < 4 and noDialog) or !noDialog}">
                <div class="widget_grid_cell borders" style="width:<c:out value="${currency_width}"/>;">
                  <div class="widget_flagbox catbundles">
                      <div class="widget_currencyflag">
                      <img src="../../images/flags/<c:out value="${currency.currency.currencyCode}"/>.gif" alt="" />
                      </div>
                  </div>
              <span class="subheader"><c:out value="${currency.currency.currencyCode}"/></span>
           </div>
           </c:if>
           <c:set var="currencyCount" value="${currencyCount+1}" />
       </c:forEach>

       <c:if test="${(fn:length(currencies) > 4 and noDialog)}">
         <div class="widget_grid_cell" style="width:4%;">
           <a class="moretabbutton" onclick="getFullListingOfCharges(this, <c:out value="${productBundleRevision.productBundle.id}"/>);" href="javascript:void(0);" title="<spring:message code='label.show.more'/>">
           </a>
         </div>
       </c:if>

    </div>
    <div class="widget_grid inline even">
      <div class="widget_grid_cell subheader borders" style="width:<c:out value="${title_width}"/>; padding-bottom: 12px;">
          <span class="celltext right" style="padding-top: 8px;"><spring:message code="label.one.time"/></span>
      </div>

      <c:set var="currencyCount" value="0" />
      <c:forEach var="currency" items="${currencies}">
        <c:if test="${(currencyCount < 4 and noDialog) or !noDialog}">
            <div class="widget_grid_cell borders" style="width:<c:out value="${currency_width}"/>;">
              <span class="celltext" style="padding-bottom: 4px; padding-top: 2px;">
                <c:if test="${fullBundlePricingMap[productBundleRevision][currency]['catalog-onetime'] != null}">
                         <c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${fullBundlePricingMap[productBundleRevision][currency]['catalog-onetime'].price}"/>
                         <br/>
                         <span style="font-style:italic; color:#666; margin-left: -4px; margin:right:2px" title="<spring:message code='label.reference.price'/>">(<c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${fullBundlePricingMap[productBundleRevision][currency]['rpb-onetime'].price}"/>)</span>
                         
                </c:if>
              </span>
            </div>
        </c:if>
        <c:set var="currencyCount" value="${currencyCount+1}" />
      </c:forEach>

    </div>
    <div class="widget_grid inline odd">
      <div class="widget_grid_cell subheader borders" style="width:<c:out value="${title_width}"/>; padding-bottom: 12px;">
          <span class="celltext right"  style="padding-top: 8px;"><spring:message code="label.recurring"/>: <spring:message code="charge.type.${productBundleRevision.productBundle.rateCard.chargeType.name}"/></span>
        </div>
        <c:set var="currencyCount" value="0" />
        <c:forEach var="currency" items="${currencies}">
          <c:if test="${(currencyCount < 4 and noDialog) or !noDialog}">
            <div class="widget_grid_cell borders" style="width:<c:out value="${currency_width}"/>;">
                <c:choose>
                  <c:when test="${productBundleRevision.productBundle.rateCard.chargeType.name == 'NONE'}">
                    <span class="celltext" style="padding-bottom: 4px; padding-top: 8px;"><spring:message code="ui.label.na"/></span>
                  </c:when>
                  <c:otherwise>
                     <c:if test="${fullBundlePricingMap[productBundleRevision][currency]['catalog-recurring'] != null}">
                        <span class="celltext" style="padding-bottom: 4px; padding-top: 2px;">
	                        <c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${fullBundlePricingMap[productBundleRevision][currency]['catalog-recurring'].price}"/>
	                        <br/>
                          <span style="font-style:italic; color:#666; margin-left: -4px; margin:right:2px" title="<spring:message code='label.reference.price'/>">(<c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${fullBundlePricingMap[productBundleRevision][currency]['rpb-recurring'].price}"/>)</span>
	                      </span>
                     </c:if>
                 </c:otherwise>
                </c:choose>
            </div>
          </c:if>
          <c:set var="currencyCount" value="${currencyCount+1}" />
        </c:forEach>
    </div>
</div>