<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script type="text/javascript">
var productsUrl = "<%=request.getContextPath() %>/portal/products/";
$("#dialog_edit_product_default_price").empty();
$("#dialog_plan_product_charges").empty();
</script>

<div class="widget_browsergrid_wrapper wsubaction"  style="overflow-x: hidden;overflow-y: auto;">

  <div id="product_row_template" class="widget_details_inlinegrid j_product_row_template" >
	  <div class="widget_grid inline header widget_navtitlebox">
	   <span class="widget_gridicon plancalendar " style="margin:7px 0 0 5px;"> </span>
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

	  <c:choose>
	    <c:when test="${fn:length(productChargesList) == 0}">
		    <div class='widget_grid inline'>
			    <div class="infomessage">
				    <spring:message code="dateonly.short.format" var="dateonly_format"/>
					    <fmt:formatDate var="displayDate" value="${date}" pattern="${dateonly_format}"/>
					    <spring:message code="ui.product.planned.pricing.message" htmlEscape="false" arguments="${displayDate}">
				    </spring:message>
			    </div>
		    </div>
	    </c:when>
	    <c:otherwise>
	     <c:forEach items="${productChargesList}" var="productPrice" varStatus="priceStatus">
	       <c:choose>
			     <c:when test="${status.index % 2 == 0}">
			         <c:set var="rowClass" value="odd"/>
			     </c:when>
			     <c:otherwise>
			         <c:set var="rowClass" value="even"/>
			     </c:otherwise>
	       </c:choose>
	       <div class='widget_grid inline<c:out  value="${rowClass}" />' id="<c:out value="${productPrice.currencyValue.currencyCode}" />">
		        <div class="widget_grid_labels">
		            <span> <spring:message code="currency.longname.${productPrice.currencyValue.currencyCode}"></spring:message></span>
		        </div>
		        <div class="widget_grid_description">
		          <span id="price">
		            <div style="float:left;"><c:out value="${productPrice.currencyValue.sign}"/></div>
		            <div style="float:left;"><fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${productPrice.price}"/></div>
		          </span>
		        </div>
		        <div class="widget_flagbox">
	            <div class="widget_currencyflag">
	                <img alt="" src="../../images/flags/<c:out value="${productPrice.currencyValue.flag}" />">
	            </div>
		        </div>
	       </div>
	      </c:forEach>
	    </c:otherwise>
	  </c:choose>

  </div>
</div>
