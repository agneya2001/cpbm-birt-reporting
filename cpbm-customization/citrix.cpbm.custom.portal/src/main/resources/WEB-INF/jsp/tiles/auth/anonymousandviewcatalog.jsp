<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<div id="currency_selector">
  <div class="catalog_currencybox" style="top: -8px;">
    <span id="selectedcurrencyflag" class="flagsbox"><img src='/portal/images/flags/<c:out value="${selectedCurrency.currencyCode}"></c:out>.gif' /></span>
    <span id="selectedcurrencytext" class="currencytext"><c:out value="${selectedCurrency.currencyCode}"></c:out></span>
    <input type="hidden" id="selectedCurrencySign" name="selectedCurrencySign" value="<c:out value="${selectedCurrency.sign}"/>"/>
    <span id="currencySelectorArrow" class="arrow"></span>
    <div id="catalog_currencybox_dropdown" class="catalog_currencybox_dropdown" style="display: None;margin-top: -2px;margin-left: -1px">
      <ul style="margin:0px;">
          <c:forEach items="${currencies}" var="currency" varStatus="status">
            <li class="currencyLi" id='<c:out value="${currency.currencyCode}"></c:out>' sign='<c:out value="${currency.sign}"></c:out>'><span class="flagsbox"><img src='/portal/images/flags/<c:out value="${currency.currencyCode}"></c:out>.gif' /></span>
                <span class="currencytext"><c:out value="${currency.currencyCode}"></c:out></span>
            </li>
            </c:forEach>
        </ul>
    </div>
   </div>
</div>
<spring:message code="revision.date.format" var="revision_format"/>
<input type="hidden" id="viewChannelCatalog" value="<c:out value="${viewChannelCatalog}"/>" />
<input type="hidden" id="historyDateFormat" value="<c:out value="${revision_format}"/>" />
<input type="hidden" id="revision" value="<c:out value="${revision}"/>" />
<input type="hidden" id="revisionDate" value="<c:out value="${revisionDate}"/>" />
<input type="hidden" id="dateFormat" value="<c:out value="${dateFormat}"/>" />
<input type="hidden" id="channelId" value="<c:out value="${channel.id}"/>" />
<input type="hidden" id="channelCode" value="<c:out value="${channel.code}"/>" />
<input type="hidden" id="anonymousBrowsing" value="<c:out value="${anonymousBrowsing}"/>" />
  <tiles:insertAttribute name="createsubscription" ignore="true" />