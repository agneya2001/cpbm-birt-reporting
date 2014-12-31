<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" src="<%=request.getContextPath() %>/js/products.js"></script>
<script type="text/javascript">
   var productsUrl = "<%=request.getContextPath() %>/portal/products/";
  $("#dialog_view_planned_charges").empty();  
  $("#dialog_plan_charges").empty();  
</script>
              
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
    <spring:url value="/portal/products/editplannedcharges" var="edit_planned_charges_path" htmlEscape="false" /> 
    <form:form commandName="productForm" id="productRateCardForm" cssClass="formPanel ajaxForm"  action="${edit_planned_charges_path}">
    <ul>
    <li style="margin:10px 0 0 10px;">
          <span>
          <c:choose>
            <c:when test="${productForm.startDate != null}">
              <span style="margin-right:50px;font-weight: bold;" ><spring:message code="label.start.date"/></span>
              <spring:message code="dateonly.format" var="dateonly_format"/>
              <fmt:formatDate  value="${productForm.startDate}" pattern="${dateonly_format}" />
            </c:when>
            <c:otherwise>
              <span style="margin-right:50px;font-weight: bold;" ><spring:message code="ui.label.no.planned.date"/></span>
            </c:otherwise>
          </c:choose>
            
          </span>
        </li> 
        
     </ul> 
       <div class="widget_details_inlinegrid product_plan_charges_grid"    >
      <div class="widget_grid inline subheader product_plan_charges_grid"  >
              <div class="widget_grid_cell product_displayname_cell" >
                  <span class="subheader" ><spring:message code="ui.products.label.create.product.name"/></span>
              </div>  
              <c:forEach var="currency" items="${currencieslist}" varStatus="status">
                <div class="widget_grid_cell currency_cell" >
                 <div class="widget_flagbox" style="float:left;padding:0;margin:5px 0 0 5px;">
                  <div class="widget_currencyflag">
                      <img alt="" src="../../images/flags/<c:out value="${currency.flag}" />">
                  </div>
                  </div>
                  <span class="subheader"><c:out value="${currency.currencyCode}"/>&nbsp;(&nbsp;<c:out value="${currency.sign}" />&nbsp;)</span>
                </div>
              </c:forEach>
         </div>
        <div class="widgetgrid_wrapper plangrid_lightbox product_plan_charges_grid" >
          <c:forEach items="${productForm.productChargesFormList}" var="productChargesForm" varStatus="status">
            <c:choose>
              <c:when test="${status.index % 2 == 0}">
                <c:set var="rowClass" value="odddark"/>
              </c:when>
              <c:otherwise>
                  <c:set var="rowClass" value="even"/>
              </c:otherwise>
            </c:choose>
           <div class="<c:out value="widget_grid inline ${rowClass}"/> product_plan_charges_innergrid" >
                <div class="widget_grid_cell product_displayname_cell"  title="<c:out value="${productChargesForm.product.name}"/>">
                  <span class="celltext ellipsis" style="font-weight:bold;width:105px; color:#0A79AC; margin:15px 0 0 5px;"><c:out value="${productChargesForm.product.name}"/></span>
                </div>
                 <div class="widget_grid_cell currency_cell"  >               
                </div>
                <c:forEach items="${productChargesForm.charges}" var="productCharge" varStatus="priceStatus">  
                       
              <div class="widget_grid_cell currency_cell" >
              
                <div class="mandatory_wrapper" style="margin:5px 0 0 1px;">
                 <input style="width:60px;"  id="productChargesFormList<c:out value='${status.index}' />.charges<c:out value='${priceStatus.index}' />" 
                value='<c:out value="${productCharge.price }" />'
                  class="text priceRequired j_pricerequired" name="productChargesFormList[<c:out value='${status.index}' />].charges[<c:out value='${priceStatus.index}' />].price" >
                  </div>
                </div>
                </c:forEach>
            </div>
          </c:forEach>
          </div>
    </div>
</form:form>

<input type="hidden" id="planforcurrentchargesflag" value="false" />

<div class="dialog_common_messagebox alert-danger error product_plan_charges_grid" style="margin:0 0 0 10px;float:left; padding:0 0 5px 0; border:1px solid #CCCCCC; display:block;display:none;">
  <span class="erroricon"></span>
  <p id="priceRequiredError" style="margin-top:7px;"></p>
</div>
</div>
</div>

