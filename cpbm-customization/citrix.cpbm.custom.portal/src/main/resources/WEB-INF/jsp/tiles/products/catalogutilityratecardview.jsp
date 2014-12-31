<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="main_width" value="100%" />
<c:set var="title_width" value="14%" />
<c:set var="currency_width" value="14%" />
<c:if test="${noDialog}">
  <c:set var="main_width" value="608px" />
  <c:set var="title_width" value="20%" />
  <c:set var="currency_width" value="15%" />
</c:if>

<div class="widget_grid inline subheader" style="width: <c:out value="${main_width}"/> ;">
     <div class="widget_grid_cell" style="width:<c:out value="${title_width}"/>">
         <span class="subheader"><spring:message code="label.name"/></span>
     </div>

     <c:set var="currencyCount" value="0" />
     <c:forEach var="currency" items="${currencies}">

        <c:if test="${(currencyCount < 4 and noDialog) or !noDialog}">
            <div class="widget_grid_cell" style="width:<c:out value="${currency_width}"/>">
                <div class="widget_flagbox" style="float:left; padding:0; margin:5px 0 0 10px;">
                    <div class="widget_currencyflag">
                        <img src="../../images/flags/<c:out value="${currency.currencyCode}"/>.gif" alt="" />
                    </div>
                </div>
                <span class="subheader" style="margin-left: 2px;"><c:out value="${currency.currencyCode}"/></span>
            </div>
        </c:if>

        <c:set var="currencyCount" value="${currencyCount+1}" />
     </c:forEach>

     <c:if test="${(fn:length(currencies) > 4 and noDialog)}">
         <div class="widget_grid_cell" style="width:4%;">
             <a class="moretabbutton" onclick="getFullListingOfCharges();" href="#" title="<spring:message code='label.show.more'/>">
             </a>
         </div>
     </c:if>
</div>

<div class="widgetgrid_wrapper plangrid_lightbox" style="overflow-x: hidden; overflow-y: auto; width: <c:out value="${main_width}"/> ; height: auto;">
    <c:set var="productCount" value="0"/>

    <c:forEach items="${fullProductPricingMap}" var="channelPriceMap" varStatus="status">

        <c:set var="productCount" value="${productCount+1}"/>
        <c:choose>
            <c:when test="${status.index % 2 == 0}">
                <c:set var="rowClass" value="odd"/>
            </c:when>
            <c:otherwise>
                <c:set var="rowClass" value="even"/>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${productCount == noOfProducts}">
                <div class="<c:out value="widget_grid inline ${rowClass}"/>" style="border-width: 0px 0px 1px; width: <c:out value="${main_width}"/> ; ">
            </c:when>
            <c:otherwise>
                <div class="<c:out value="widget_grid inline ${rowClass}"/>" style="border-width: 0px 0px 0px; width: <c:out value="${main_width}"/> ; ">
            </c:otherwise>
        </c:choose>
             <div class="widget_grid_cell" style="width:<c:out value="${title_width}"/>">
                <span class="celltext ellipsis" style="font-weight:bold; width:60%; color:#0A79AC; margin:15px 0 0 5px;" title="<c:out value="${channelPriceMap.key.name}"/>"><c:out value="${channelPriceMap.key.name}"/></span>
            </div>

            <c:set var="currencyCount" value="0" />
            <c:forEach var="currency" items="${currencies}">
                <c:if test="${(currencyCount < 4 and noDialog) or !noDialog}">
                    <div class="widget_grid_cell" style="width:<c:out value="${currency_width}"/>">
                       <span class="celltext" style="margin-left: 10px;">
                          <c:set var="productPrice" value="${channelPriceMap.value[currency]['catalog'].price}" />
                          <c:if test="${empty productPrice }">
                            <fmt:formatNumber var="productPrice" pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="0" />
                          </c:if>
                          <c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${productPrice}"/>
                          <br/>
                          <span style="font-style:italic; color:#666; margin-left: -4px; margin:right:2px" title="<spring:message code='label.reference.price'/>">(
                          <c:choose>
                            <c:when test="${channelPriceMap.value[currency]['rpb'] != null}">
                              <c:out value="${currency.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${channelPriceMap.value[currency]['rpb'].price}"/>
                            </c:when>
                            <c:otherwise>
                              <spring:message code="ui.label.na"/>
                            </c:otherwise>
                          </c:choose>)</span>
                       </span>
                    </div>
                </c:if>
                <c:set var="currencyCount" value="${currencyCount+1}" />
            </c:forEach>
        </div>

    </c:forEach>
</div>