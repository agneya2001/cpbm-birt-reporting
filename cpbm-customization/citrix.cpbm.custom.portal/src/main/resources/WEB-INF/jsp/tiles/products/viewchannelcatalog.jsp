<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="<%=request.getContextPath()%>/resources/all.js"></script>
<link rel="stylesheet" href="<%=request.getContextPath() %>/css/bootstrap.min.css" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath() %>/js/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/app.js"></script>
<link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/resources/all.css"/>
<link rel="stylesheet" type="text/css" media="all" href="<%=request.getContextPath()%>/css/main.css"/>
<script type="text/javascript" src="<%=request.getContextPath()%>/csrf_js_servlet"></script>
<script type="text/javascript">
var g_dictionary = {  
  dialogCancel: '<spring:message javaScriptEscape="true" code="label.cancel"/>',
  youCanNotSubscribeUntilYouAcceptTheTermsAndConditions: '<spring:message javaScriptEscape="true" code="you.can.not.subscribe.until.you.accept.the.terms.and.conditions"/>',
  decPoint: '<fmt:formatNumber var="str" value="0.1"/><c:out value="${fn:substring(str,1,2)}"/>',
  dialogClose: '<spring:message javaScriptEscape="true" code="label.close"/>'
}
</script >
</head>
<body>
<div id="main">
  <div id="maincontent_container">
    <div class="maincontent popup_window">
      <div class="secondlevel_menupanel currency_dropdown" >
      <div id="currency_selector" >
        <div class="catalog_currencybox" style="top: -8px;">
          <span id="selectedcurrencyflag" class="flagsbox"><img src='/portal/images/flags/<c:out value="${selectedCurrency.currencyCode}"></c:out>.gif' /></span>
          <span id="selectedcurrencytext" class="currencytext"><c:out value="${selectedCurrency.currencyCode}"></c:out></span>
          <input type="hidden" id="selectedCurrencySign" name="selectedCurrencySign" value="<c:out value="${selectedCurrency.sign}"/>"/>
          <span id="currencySelectorArrow" class="arrow"></span>
          <div id="catalog_currencybox_dropdown" class="catalog_currencybox_dropdown" style="display: none;margin:0px;">
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
      <input type="hidden" id="anonymousBrowsing" value="<c:out value="${anonymousBrowsing}"/>" />
        </div>
        <div class="clearboth"></div>
        
        <tiles:insertAttribute name="createsubscription" ignore="true" />
    </div>
  </div>
  <div id="footer" style="height:0px;"></div>
</div>

<div id="utilityrates_lightbox" class="utility_table" title='<spring:message code="label.view.utility.rates.dialog.title"/>' style="display:none;padding:10px;max-height:500px;">
    </div> 
</body>
</html>
