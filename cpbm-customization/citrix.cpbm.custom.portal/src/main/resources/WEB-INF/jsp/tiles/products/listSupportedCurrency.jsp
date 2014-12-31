<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
  
<div class="widget_details_inlinegrid" style="width:600px">
  <div class="widget_grid inline header widget_navtitlebox" style="width: 100%">
    <span  class="title" style="margin-left:5px;width: auto;color:#FFF;"><spring:message code="ui.campaigns.label.create.discount.amount"></spring:message> 
    </span>
  </div>
  <div class="widget_inline_chargesbox" style="width: 100%">
    <div class="widget_grid inline subheader">
      <c:forEach var="supportedCurrency" items="${supportedCurrenciesForChannel}" varStatus="status">
        <div class="widget_grid_cell" style="width: 16%;">
          <div class="widget_flagbox" style="float:left;padding:0;margin:5px 0 0 5px;">
            <div class="widget_currencyflag">
              <img src="../../images/flags/<c:out value="${supportedCurrency.currencyCode}"/>.gif" alt="" />
            </div>
          </div>
          <span class="subheader"><c:out value="${supportedCurrency.currencyCode}"/>&nbsp;(&nbsp;<c:out value="${supportedCurrency.sign}" />&nbsp;)</span>
        </div>
      </c:forEach>
    </div>
    <div class="widget_grid inline odd" style="border:none;">  
      <c:forEach items="${supportedCurrenciesForChannel}" var="supportedCurrency">          
        <div class="widget_grid_cell" style="width: 16%;">
          <div class="mandatory_wrapper" style="margin:5px 0 0 0;">
            <input class="text priceRequired j_pricerequired" style="width:70%;margin:5px 5px 5px 5px;" id="discountAmountMap[<c:out value='${supportedCurrency.currencyCode}'/>]" name="discountAmountMap[<c:out value='${supportedCurrency.currencyCode}'/>]" tabindex="6" />
          </div>
        </div> 
      </c:forEach>  
    </div>
    <div class="main_addnew_formbox_errormsg" id="discountAmountMapError" style="display:none">
      <spring:message code="js.errors.campaigns.amountoff.required"/>
    </div> 
  </div>
</div>
