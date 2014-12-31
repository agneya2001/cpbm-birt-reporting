<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script>
	$(".widget_checkbox.new").off('click');
	$(".widget_checkbox").on('click', function() {
	  if($(this).find("span").attr("class") == "unchecked") {
	       $(this).find("span").removeClass('unchecked').addClass('checked');
	     } else {
	       operation = "remove";
	       $(this).find("span").removeClass('checked').addClass('unchecked');
	     }
	});
</script>
<script type="text/javascript">
  var currPrecision="<c:out value="${currencyFractionalDigitsLimit}"/>";
</script>               

<div class="widget_browsergrid_wrapper details" id="currencies_add" style="width:auto;">
  <div id="currency_row_container" style="margin-left: 15px; margin-right: 20px; margin-top: 20px;">
      <c:forEach var="currency" items="${availableCurrencies}" varStatus="newstatus">
        <c:choose>
          <c:when test="${(status.index + newstatus.index) % 2 == 0}">
            <c:set var="rowClass" value="odd"/>
          </c:when>
          <c:otherwise>
              <c:set var="rowClass" value="even"/>
          </c:otherwise>
        </c:choose>
        <div class="<c:out value="widget_grid details ${rowClass}"/>">
            <div class="widget_checkbox widget_checkbox_wide"
                currCode="<c:out value="${currency.currencyCode}"/>"
                currSign="<c:out value="${currency.sign}"/>"
                currName="<spring:message javaScriptEscape="true" code="currency.longname.${currency.currencyCode}"/>">
              <span class="unchecked"></span> 
            </div>
            <div class="widget_grid_description" style="margin:0;">
              <span><strong><c:out value="${currency.sign}"/> - <spring:message code="currency.longname.${currency.currencyCode}"/></strong></span>
            </div>
            <div class="widget_flagbox">
              <div class="widget_currencyflag">
                  <img src="../../images/flags/<c:out value="${currency.currencyCode}"/>.gif" alt="" />
              </div>
            </div>
        </div>
      </c:forEach>
  </div>
</div>