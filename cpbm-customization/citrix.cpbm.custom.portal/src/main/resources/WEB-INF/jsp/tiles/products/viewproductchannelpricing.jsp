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
<c:if test="${currenciesToDisplay <= 4 }">
    <c:set var="title_width" value="23%" />
    <c:set var="currency_width" value="17%" />
</c:if>

<div class="widget_details_actionbox">
    <ul class="widget_detail_actionpanel" style="float:left;">
    </ul>
</div>

<div class="widget_browsergrid_wrapper wsubaction" style="<c:if test="${currenciesToDisplay > 4 }">width: 772px; </c:if> overflow-x: hidden;overflow-y: auto;">
    <div class="widget_details_inlinegrid" >

        <div class="widget_grid inline header widget_navtitlebox">
            <span class="widget_gridicon currentcalendar " style="margin:7px 0 0 5px;">
            </span>
            <span  class="title" style="margin-left:5px;width: auto;color:#FFF;">
              <spring:message code="date.format" var="dateonly_format"/>
              <spring:message code="label.bundle.list.ratecard.effective.date"/>
              <c:choose>
                <c:when test="${date != null}">
                    <fmt:formatDate value="${date}" pattern="${dateonly_format}"/>
                </c:when>
                <c:otherwise>
                    <spring:message code="ui.label.plan.date.not.yet.set"/>
                </c:otherwise>
              </c:choose>
            </span>
        </div>

        <div class="widget_inline_chargesbox">

            <div class="widget_grid inline subheader">
                <div class="widget_grid_cell header borders" style="width: <c:out value="${title_width}"/>;">
                    <span class="subheader right" style="color:#FFF;"><spring:message code="label.name"/></span>
                </div>

                <c:forEach var="currency" items="${currencieslist}" varStatus="status" end="${currenciesToDisplay - 1}">
                     <div class="widget_grid_cell borders" style="width: <c:out value="${currency_width}"/>;">
                        <div class="widget_flagbox catbundles">
                            <div class="widget_currencyflag">
                                <img src="../../images/flags/<c:out value="${currency.currency.currencyCode}"/>.gif" alt="" />
                            </div>
                        </div>
                        <span class="subheader"><c:out value="${currency.currency.currencyCode}"/></span>
                    </div>
                </c:forEach>

                <c:if test="${fn:length(currencieslist) > currenciesToDisplay}">
                    <div class="widget_grid_cell" style="width:4%;">
                        <a class="moretabbutton" onclick="viewChannelPricing(this, true);" href="#" title="<spring:message code='label.show.more'/>"></a>
                    </div>
                </c:if>
            </div>

            <c:forEach items="${productChannelChargesMap}" var="channelMap" varStatus="status">
                <c:choose>
                    <c:when test="${status.index % 2 == 0}">
                        <c:set var="rowClass" value="odd"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="rowClass" value="even"/>
                    </c:otherwise>
                </c:choose>
                <div class="<c:out value="widget_grid inline ${rowClass}"/>">
                    <div class="widget_grid_cell subheader borders" style="height:27px; width:<c:out value="${title_width}"/>;" title="<c:out value="${channelMap.key.name}" />">
                        <span class="celltext right ellipsis" style="width: 80%;"><c:out value="${channelMap.key.name}" /></span>
                    </div>

                    <c:forEach var="currency" items="${currencieslist}" end="${currenciesToDisplay - 1 }">
                        <div class="widget_grid_cell borders" style="height:27px; width: <c:out value="${currency_width}"/>;">
                            <span class="celltext">
                                <c:forEach items="${channelMap.value}" var="charge" >
                                    <c:if test="${charge.currencyValue.currencyCode == currency.currencyCode}">
                                        <c:out value="${charge.currencyValue.sign}"/><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${charge.price}"/>
                                    </c:if>
                                </c:forEach>
                            </span>
                        </div>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>

    </div>
</div>
