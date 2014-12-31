<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript">
  var productsUrl = "<%=request.getContextPath() %>/portal/products/";
  $("#dialog_edit_planned_charges").empty();  
  $("#dialog_plan_charges").empty();  
</script>
<div class="dialog_formcontent wizard" style="height:auto">
  <div class="details_lightboxtitlebox" >
  </div>
    <div class="details_lightboxformbox" style="border-bottom: none;">
    <ul> 
    
       <li style="margin:10px 0 0 10px;">
          <span style="margin-right:50px;font-weight: bold;" ><spring:message code="label.start.date"/></span>
          <spring:message code="dateonly.format" var="dateonly_format"/>
          <span >
            <c:choose>
              <c:when test="${date != null}">
                  <fmt:formatDate value="${date}" pattern="${dateonly_format}"/>
              </c:when>
              <c:otherwise>
                  <spring:message code="ui.label.plan.date.not.yet.set"/>
              </c:otherwise>
            </c:choose>
          </span>
        </li> 
     </ul> 
     <div class="widget_details_inlinegrid product_plan_charges_grid"   >
      <div class="widget_grid inline subheader product_plan_charges_grid"  >
              <div class="widget_grid_cell product_displayname_cell" >
                  <span class="subheader" ><spring:message code="ui.products.label.create.product.name"/></span>
              </div> 
              <div class="widget_grid_cell currency_cell" >
                  <span class="subheader" ><spring:message code="units"/></span>
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
          <c:forEach items="${plannedCharges}" var="plannedChargesMap" varStatus="status">
            <c:choose>
              <c:when test="${status.index % 2 == 0}">
                <c:set var="rowClass" value="odd"/>
              </c:when>
              <c:otherwise>
                  <c:set var="rowClass" value="even"/>
              </c:otherwise>
            </c:choose>
           <div class="<c:out value="widget_grid inline ${rowClass}"/> product_plan_charges_innergrid" >
                <div class="widget_grid_cell product_displayname_cell"  title="<c:out value="${plannedChargesMap.key.name}"/>">
                  <span class="celltext ellipsis" style="font-weight:bold;width:105px; color:#0A79AC; margin:15px 0 0 5px;"><c:out value="${plannedChargesMap.key.name}"/></span>
                </div>
                <div class="widget_grid_cell currency_cell"  >               
                </div>
                <c:forEach items="${plannedChargesMap.value}" var="productCharge" varStatus="priceStatus">  
                       
              <div class="widget_grid_cell currency_cell" >
              <span class="celltext">  <fmt:formatNumber pattern="${currencyFormat}" minFractionDigits="${minFractionDigits}" value="${productCharge.price}"/></span>
                </div>
                </c:forEach>
            </div>
          </c:forEach>
          </div>
    </div>
</div>  
</div>
              
