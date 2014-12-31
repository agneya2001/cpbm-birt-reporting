<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="title_width" value="20%" />
<c:set var="currency_width" value="13%" />
<c:if test="${currenciesToDisplay <= 4 }">
    <c:set var="title_width" value="23%" />
    <c:set var="currency_width" value="17%" />
</c:if>

<div class="widget_inline_chargesbox">
    <div class="widget_grid inline subheader">
        <div class="widget_grid_cell header borders" style="width: <c:out value="${title_width}"/>;">
            <span class="subheader right" style="color:#FFF;"><spring:message code="label.charges"/></span>
        </div>

        <c:forEach var="currency" items="${currencieslist}" varStatus="status" end="${currenciesToDisplay - 1 }">
            <div class="widget_grid_cell borders" style="width: <c:out value="${currency_width}"/>;" >
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
              <a class="moretabbutton" onclick="viewBundlePricing(this, true);" href="#" title="<spring:message code='label.show.more'/>"></a>
            </div>
         </c:if>
      </div>

      <div class="widget_grid inline even">
        <div class="widget_grid_cell subheader borders" style="width: <c:out value="${title_width}"/>;">
            <span class="celltext right"><spring:message code="label.one.time"/></span>
        </div>

        <c:forEach var="currency" items="${currencieslist}" end="${currenciesToDisplay - 1 }">
            <div class="widget_grid_cell borders" style="width: <c:out value="${currency_width}"/>;">
                <span class="celltext" >
                    <c:forEach var="rateCardComponentChargesForm" items="${rateCardChargesForm.nonRecurringRateCardChargesFormList}">
                        <c:forEach var="charge" items="${rateCardComponentChargesForm.charges}" >
                            <c:if test="${charge.currencyValue.currencyCode == currency.currencyCode}">
                                <c:out value="${charge.currencyValue.sign}"/><fmt:formatNumber pattern="${currencyFormat}"  value="${charge.price}" minFractionDigits="${minFractionDigits}" />
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                </span>
            </div>
        </c:forEach>
      </div>

    <c:choose>
        <c:when test="${rateCardChargesForm.bundle.rateCard.chargeType.frequencyInMonths != 0 }">
            <div class="widget_grid inline odd">
                <div class="widget_grid_cell subheader borders" style="width: <c:out value="${title_width}"/>;">
                    <span class="celltext right" style="margin-right: 6px;"><spring:message code="label.recurring"/>:&nbsp;<spring:message code="charge.type.${rateCardChargesForm.bundle.rateCard.chargeType.name}"/></span>
                </div>

            <c:forEach var="currency" items="${currencieslist}" end="${currenciesToDisplay - 1 }">
                <div class="widget_grid_cell borders" style="width: <c:out value="${currency_width}"/>;">
                    <span class="celltext">
                        <c:forEach var="rateCardComponentChargesForm" items="${rateCardChargesForm.recurringRateCardChargesFormList}" >
                            <c:forEach var="charge" items="${rateCardComponentChargesForm.charges}" >
                                <c:if test="${charge.currencyValue.currencyCode == currency.currencyCode}">
                                    <c:out value="${charge.currencyValue.sign}"/><fmt:formatNumber pattern="${currencyFormat}"  value="${charge.price}" minFractionDigits="${minFractionDigits}" />
                                </c:if>
                            </c:forEach>
                        </c:forEach>
                    </span>
                </div>
            </c:forEach>
          </div>
        </c:when>
        <c:otherwise>
            <div class="widget_grid inline odd">
                <div class="widget_grid_cell subheader borders" style="width: <c:out value="${title_width}"/>;">
                    <span class="celltext right"><spring:message code="label.recurring"></spring:message>:&nbsp;<spring:message code="ui.label.na" /></span>
                </div> 
                <c:forEach var="currency" items="${currencieslist}">
                    <div class="widget_grid_cell borders" ></div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
 