<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="lightbox_height" value="96" />
<c:if test="${productBundleRevision.productBundle.rateCard.chargeType.name == 'NONE'}">
  <c:set var="lightbox_height" value="90" />
</c:if>
<div class="dialog_formcontent wizard" style="height:auto;">
  <div class="details_lightboxtitlebox" >
  </div>

	<div class="details_lightboxformbox" style="border-bottom: none;" >

	   <div class="widget_details_inlinegrid" style="width:830px;">

	     <div class="widget_grid inline subheader" style="width:830px;">
	       <div class="widget_grid_cell" style="width:150px;">
	           <span class="subheader right"><spring:message code="label.charges"/></span>
	       </div>
	       <c:forEach var="currency" items="${supportedCurrencies}" varStatus="status">
	           <div class="widget_grid_cell" style="width: 108px;">
	             <div class="widget_flagbox" style="float:left; padding:0; margin:5px 0 0 10px;">
	                 <div class="widget_currencyflag">
	                   <img src="../../images/flags/<c:out value="${currency.currencyCode}"/>.gif" alt="" />
	                 </div>
	             </div>
	             <span class="subheader" style="margin-left: 2px;"><c:out value="${currency.currencyCode}"/>&nbsp;(<c:out value="${currency.sign}" />)</span>
	          </div>
	      </c:forEach>
	    </div>

    <div class="widgetgrid_wrapper plangrid_lightbox" style="width:830px; height:<c:out value="${lightbox_height}"/>px; overflow-x: hidden; overflow-y: auto;">

	    <div class="widget_grid inline even" style="width:830px;">
	     <div class="widget_grid_cell" style="width: 150px;">
	         <span class="celltext ellipsis right"><spring:message code="label.one.time"/></span>
	     </div>
	     <c:forEach var="currency" items="${supportedCurrencies}">
	       <div class="widget_grid_cell" style="width:108px;">
	        <span class="celltext">
          
	           <c:if test="${fullBundlePricingMap[productBundleRevision][currency]['catalog-onetime'] != null}">
	               <div class="mandatory_wrapper" style="margin:0px 0px 0px -5px;">
                     <input style="height:auto; margin: 0px 0px 0px 10px; width: 80px; height: 16px;" id="currencyValsWeNeed<c:out value='${currency.currencyCode}${productBundleRevision.productBundle.id}'/>"
                            class="text priceRequired j_pricerequired"
                            value='<fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${currencyFractionalDigitsLimit}" value="${fullBundlePricingMap[productBundleRevision][currency]['catalog-onetime'].price}"/>'
                            previousvalue='<c:out value="${fullBundlePricingMap[productBundleRevision][currency]['catalog-onetime'].price}" />'
                            currencyId='<c:out value="${currency.id}" />'
                            currencycode='<c:out value="${currency.currencyCode}" />'
                            isRecurring="0" />
                     <span style="height:auto; margin: 0px 0px 0px 10px; width: 80px; height: 16px; font-style:italic; color:#666;" title="<spring:message code='label.reference.price'/>">(<fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${currencyFractionalDigitsLimit}" value="${fullBundlePricingMap[productBundleRevision][currency]['rpb-onetime'].price}"/>)</span>
                 </div>
	           </c:if>
	        </span>
	       </div>
	     </c:forEach>
	    </div>

	    <div class="widget_grid inline odd" style="width:830px;">
	      <div class="widget_grid_cell" style="width: 150px;">
	         <span class="celltext right"><spring:message code="label.recurring"/>: <spring:message code="charge.type.${productBundleRevision.productBundle.rateCard.chargeType.name}"/></span>
	       </div>
	       <c:forEach var="currency" items="${supportedCurrencies}">
	         <div class="widget_grid_cell" style="width:108px;">
	           <span class="celltext">
	                <c:if test="${fullBundlePricingMap[productBundleRevision][currency]['catalog-recurring'] != null}">
	                   <div class="mandatory_wrapper" style="margin:0px 0px 0px -5px;">
	                     <input style="height:auto; margin: 0px 0px 0px 10px; width: 80px; height: 16px;" id="currencyValsWeNeed<c:out value='${currency.currencyCode}${productBundleRevision.productBundle.id} }'/>"
	                            class="text priceRequired j_pricerequired"
                              value='<fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${currencyFractionalDigitsLimit}" value="${fullBundlePricingMap[productBundleRevision][currency]['catalog-recurring'].price}"/>'
                              previousvalue='<c:out value="${fullBundlePricingMap[productBundleRevision][currency]['catalog-recurring'].price}" />'
	                            currencyId='<c:out value="${currency.id}" />'
	                            currencycode='<c:out value="${currency.currencyCode}" />'
	                            isRecurring="1" />
	                     <span style="height:auto; margin: 0px 0px 0px 10px; width: 80px; height: 16px; font-style:italic; color:#666;" title="<spring:message code='label.reference.price'/>">(<fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${currencyFractionalDigitsLimit}" value="${fullBundlePricingMap[productBundleRevision][currency]['rpb-recurring'].price}"/>)</span>
	                   </div>
	                </c:if>
	           </span>
	         </div>
	       </c:forEach>

      </div>
    </div>

	  </div>

		<div id="error_div" class="common_messagebox error" style="width:830px; margin:0 0 0 10px; padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
		    <span class="erroricon"></span>
		     <p id="priceError" style="margin-top:7px;"></p>
		</div>

  </div>
</div>
