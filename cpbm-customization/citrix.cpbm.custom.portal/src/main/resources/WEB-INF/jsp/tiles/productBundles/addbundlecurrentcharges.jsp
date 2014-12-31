<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/commonBundles.js"></script>
<div class="dialog_formcontent">

    <div class="details_lightboxformbox" style="border-bottom: none;">
   <spring:url value="/portal/productBundles/addbundlecurrentcharges" var="add_bundle_current_charges_path" htmlEscape="false" />
    <form:form commandName="rateCardChargesForm" id="rateCardChargesForm" cssClass="formPanel ajaxForm"  action="${add_bundle_current_charges_path}"  >  
     <c:set var="noofcurrencies" value="${200+(currencieslistsize*100)}"></c:set>
     <div class="widget_details_inlinegrid" style="<c:out value="width:${noofcurrencies}px"/>">        
            <div class="widget_grid inline subheader">
              <div class="widget_grid_cell" style="width:150px;">
                  <span class="subheader"><spring:message code="charge.type"></spring:message> </span>
              </div>            
               <c:forEach items="${currencyValueList}" var="currency">
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
          
          
           <div class="widget_grid inline odd" >  
              <div class="widget_grid_cell" style="width:150px;">
                  <span class="celltext"><strong><spring:message code="label.one.time"></spring:message> </strong></span>
              </div>
           <c:forEach items="${rateCardChargesForm.nonRecurringRateCardChargesFormList}" var="rateCardComponentChargesForm" varStatus="nonRecurringstatus">				   
				    <c:forEach items="${rateCardComponentChargesForm.charges}" var="charge" varStatus="nonrecurringchargesstatus">
				      <div class="widget_grid_cell" style="width:100px;">
				       <div class="mandatory_wrapper" style="margin:0;">
				       <input class="text priceRequired j_pricerequired"
				        value='<c:out value="${charge.price }" />'
				       style="width:70%;"type="text" 
				       name="nonRecurringRateCardChargesFormList[<c:out value='${nonRecurringstatus.index}' />].charges[<c:out value='${nonrecurringchargesstatus.index}' />].price" />
				       </div>
				    </div> 
				    </c:forEach>				    
				    </c:forEach>  
				</div>
				 <c:choose>
         <c:when test="${rateCardChargesForm.bundle.rateCard.chargeType.frequencyInMonths != 0 }">
                  
          <div class="widget_grid inline odd" > 
         <div class="widget_grid_cell" style="width:150px;">
                <span class="celltext"><strong><spring:message code="label.recurring"></spring:message>&nbsp;:&nbsp; <spring:message code="charge.type.${rateCardChargesForm.bundle.rateCard.chargeType.name}"/> </strong></span>
            </div> 
         <c:forEach items="${rateCardChargesForm.recurringRateCardChargesFormList}" var="recurringrateCardComponentChargesForm" varStatus="recurringstatus">
         
          <c:forEach items="${recurringrateCardComponentChargesForm.charges}" var="charge" varStatus="recurringchargesstatus">
            <div class="widget_grid_cell" style="width:100px;">
             <div class="mandatory_wrapper" style="margin:0;">
             <input class="text priceRequired j_pricerequired"  
             value='<c:out value="${charge.price }" />' 
             style="width:70%;" type="text" name="recurringRateCardChargesFormList[<c:out value='${recurringstatus.index}' />].charges[<c:out value='${recurringchargesstatus.index}' />].price" />
             </div>
          </div> 
          </c:forEach>            
          </c:forEach>  
            </div> 
          </c:when>
           <c:otherwise>
          <div class="widget_grid inline odd" > 
         <div class="widget_grid_cell" style="width:150px;">
          <span class="celltext"><strong><spring:message code="label.recurring"></spring:message>&nbsp;:&nbsp;N/A</strong></span>
          </div> 
          <c:forEach var="currency" items="${currencieslist}">
            <div class="widget_grid_cell" style="width:100px;"></div>
          </c:forEach>
        </div>
      </c:otherwise>
      </c:choose>
        </div>
</form:form>
 <div class="common_messagebox error" style="<c:out value="width:${noofcurrencies}px;"/> margin:0 0 0 10px; padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
        <span class="erroricon"></span>
        <p id="priceRequiredError" style="margin-top:7px;"></p>
    </div> 
</div> 
</div>
              
