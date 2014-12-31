<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>

<style>
.ui-datepicker-trigger {
    margin-left : 5px;
    margin-top : 0px;
}
</style>
<div class="dialog_formcontent wizard" style="height:auto">
  <div class="details_lightboxtitlebox" >
  </div>
    <div class="details_lightboxformbox" style="border-bottom: none;">
    <spring:url value="/portal/productBundles/plancharges" var="plan_charges_path" htmlEscape="false" /> 
    <form:form commandName="rateCardForm" id="rateCardForm" cssClass="formPanel ajaxForm"  action="${plan_charges_path}"  >    
    <ul> 
        <li style="margin:10px 0 0 10px;">
        <span style="margin-right:50px;font-weight: bold;" ><spring:message code="label.start.date"/></span>
          <spring:message code="dateonly.format" var="dateonly_format"/>
          <span ><fmt:formatDate  value="${rateCardForm.startDate}" pattern="${dateonly_format}" /></span>
        </li> 
     </ul>
     <div class="widget_details_inlinegrid" style="width:882px;" >
      <div class="widget_grid inline subheader"  style="width:882px;">
              <div class="widget_grid_cell  plan_bundle_charges_row_bundle_cell">
                  <span class="subheader"><spring:message code="label.bundle.name"/></span>
              </div> 
              <div class="plan_bundle_charges_row" >
               <div class="widget_grid_cell" style="width:155px;">
                  <span class="subheader"><spring:message code="charge.type"></spring:message> </span>
              </div>              
              <c:forEach var="currency" items="${currencieslist}" varStatus="status">
                <div class="widget_grid_cell" style="width:100px;">
                 <div class="widget_flagbox" style="float:left;padding:0;margin:5px 0 0 5px;">
                  <div class="widget_currencyflag">
                      <img alt="" src="../../images/flags/<c:out value="${currency.flag}" />">
                  </div>
                  </div>
                  <span class="subheader"><c:out value="${currency.currencyCode}"/>&nbsp;(&nbsp;<c:out value="${currency.sign}" />&nbsp;)</span>
                </div>
              </c:forEach>
              </div>
         </div>
         	<div class="widgetgrid_wrapper plangrid_lightbox">
          <c:forEach items="${rateCardForm.rateCardChargesList}" var="rateCardChargesForm" varStatus="rateCardStatus">
            <c:choose>
              <c:when test="${rateCardStatus.index % 2 == 0}">
                <c:set var="rowClass" value="odddark"/>
              </c:when>
              <c:otherwise>
                  <c:set var="rowClass" value="even"/>
              </c:otherwise>
            </c:choose>
          <c:set var="bundleTypeClass" value="computebundles"></c:set>
        
            <div class="<c:out value="widget_grid inline ${rowClass}"/>" style="width:880px;">
                <div class="widget_grid_cell plan_bundle_charges_row_bundle_cell" title="<c:out value="${rateCardChargesForm.bundle.name}"/>">
                 <span class='navicon <c:out value="${bundleTypeClass}"/>'
          id="nav_icon" style="height: 19px;"></span> <span class="celltext ellipsis" style="font-weight:bold;width:80px; color:#0A79AC; margin:15px 0 0 5px;"><c:out value="${rateCardChargesForm.bundle.name}"/></span>
                </div>
                
                <div class="plan_bundle_charges_row" style="height:30px;float:left; ">
                <div class="widget_grid_cell" style="width:155px;">
                  <span class="celltext"><strong><spring:message code="label.one.time"></spring:message> </strong></span>
              </div>
                   <c:forEach items="${rateCardChargesForm.nonRecurringRateCardChargesFormList}" var="nonrecurringCharge" varStatus="nonRecurringStatus">
                   <c:forEach var="currency" items="${currencieslist}">
                    <div class="widget_grid_cell" style="width:100px;">
                      <c:forEach items="${nonrecurringCharge.charges}" var="charge" varStatus="currencyIndex">
                          <c:if test="${currency.currencyCode == charge.currencyValue.currencyCode && charge != null && charge.price != null}">
                             <c:set var="found" value="1"/>
                            <div class="mandatory_wrapper" style="margin:0;">
                               <input style="width:60px;"   value='<c:out value="${charge.price }" />'
                                class="text priceRequired j_pricerequired" name="rateCardChargesList[<c:out value='${rateCardStatus.index}' />].nonRecurringRateCardChargesFormList[<c:out value='${nonRecurringStatus.index}' />].charges[<c:out value='${currencyIndex.index}' />].price" >
                            </div>
                           </c:if>
                       </c:forEach>                     
                     </div>
                  </c:forEach>
                </c:forEach>
                </div>
                <c:choose>
                  <c:when test="${rateCardChargesForm.bundle.rateCard.chargeType.frequencyInMonths != 0 }">
                     <div class="plan_bundle_charges_row" style="height:30px;float:left;">
                 <div class="widget_grid_cell" style="width:155px;">
                  <span class="celltext"><strong><spring:message code="label.recurring"></spring:message>&nbsp;:&nbsp; <spring:message code="charge.type.${rateCardChargesForm.bundle.rateCard.chargeType.name}"/></strong></span>
                  </div> 
                   <c:forEach items="${rateCardChargesForm.recurringRateCardChargesFormList}" var="recurringCharge" varStatus="recurringStatus">
                  <c:forEach var="currency" items="${currencieslist}">
                    <div class="widget_grid_cell" style="width:100px;">
                      <c:forEach items="${recurringCharge.charges}" var="charge" varStatus="currencyIndex">
                          <c:if test="${currency.currencyCode == charge.currencyValue.currencyCode && charge != null && charge.price != null}">
                             <c:set var="found" value="1"/>
                            <div class="mandatory_wrapper" style="margin:0;">
                               <input style="width:60px;"  value='<c:out value="${charge.price }" />'
                                class="text priceRequired j_pricerequired"
                                name="rateCardChargesList[<c:out value='${rateCardStatus.index}' />].recurringRateCardChargesFormList[<c:out value='${recurringStatus.index}' />].charges[<c:out value='${currencyIndex.index}' />].price" >
                            </div>
                           </c:if>
                       </c:forEach>                     
                     </div>
                  </c:forEach>
                </c:forEach>
                </div>
                  </c:when>
                  <c:otherwise>
                      <div class="plan_bundle_charges_row" style="height:30px;float:left;">
                 <div class="widget_grid_cell" style="width:130px;">
                  <span class="celltext"><strong><spring:message code="label.recurring"></spring:message>&nbsp;:&nbsp;<spring:message code="ui.label.na" /></strong></span>
                  </div> 
                  <c:forEach var="currency" items="${currencieslist}">
                    <div class="widget_grid_cell" style="width:100px;"></div>
                  </c:forEach>
                </div>
                  </c:otherwise>
                </c:choose>
             	
            </div>
          </c:forEach>
          </div>
    </div>
</form:form>
 <div class="common_messagebox error plangrid_lightbox"  style="width:882px;margin:0 0 0 10px; padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
        <span class="erroricon"></span>
        <p id="priceRequiredError" style="margin-top:7px;"></p>
    </div> 
</div>  

</div>
              
