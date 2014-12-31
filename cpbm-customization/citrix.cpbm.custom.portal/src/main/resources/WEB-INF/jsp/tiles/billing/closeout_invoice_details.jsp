<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:message code="dateonly.format" var="date_only_format"/>
<c:set var="invoiceUuid" value="${invoice.uuid}"></c:set>
<c:set var="invoiceType" value="${invoice.type}"></c:set>
<c:set var="invoiceitems" value="${invoice.invoiceItems}"></c:set>
<c:set var="invoiceitemsForSummary" value="${invoice.invoiceItemsSortedForSummaryItems}"></c:set>
<fmt:formatDate var="invoiceStartDate" pattern="${date_only_format}" value="${invoice.serviceStartDate}" timeZone="${currentUser.timeZone}"/>
<fmt:formatDate var="invoiceEndDate" pattern="${date_only_format}" value="${invoice.serviceEndDate}" timeZone="${currentUser.timeZone}"/>

<spring:message code="label.invoice.details" var="invoiceDetailsString"/>
<spring:message code="dateonly.format" var="date_only_format"/>
<c:set var="invoiceDetailTitle" value="${invoiceDetailsString} (${invoiceStartDate} - ${invoiceEndDate})"></c:set>
<div class="more_details_grid_lightbox_closeout" >
  <div class="mainbox">
    <div class="morecontentarea"> 
      <div class="db_gridbox_rows" >    
        <div class="db_gridbox_columns " style="width:19%;">
          <div class="db_gridbox_celltitles header more_details_grid"><spring:message code="label.bundle.edit.urc.product"/></div>
        </div>
        <div class="db_gridbox_columns" style="width:19%;">
          <div class="db_gridbox_celltitles header more_details_grid"><spring:message code="ui.campaigns.label.create.title"/></div>
        </div>
        <div class="db_gridbox_columns" style="width:14%;">
          <div class="db_gridbox_celltitles header more_details_grid" style="width:100%;text-align:right;margin-left: 0px;"><spring:message code="label.invoice.details.quantity"/></div>
        </div>
        <div class="db_gridbox_columns" style="width:14%;">
          <div class="db_gridbox_celltitles header more_details_grid" style="width:100%;text-align:right;margin-left: 0px;"><spring:message code="label.invoice.details.chargable.units"/></div>
        </div>
        <div class="db_gridbox_columns" style="width:10%;">
          <div class="db_gridbox_celltitles header more_details_grid"  style="width:100%;text-align:right;margin-left: 0px;"><spring:message code="usage.billing.view.subscription.unit.price.title"/></div>
        </div>
        <div class="db_gridbox_columns" style="width:14%;">
          <div class="db_gridbox_celltitles header more_details_grid" style="width:100%;text-align:center;margin-left: 0px;"><spring:message code="label.invoice.details.unit"/></div>
        </div>

        <div class="db_gridbox_columns" style="width:10%;">
          <div class="db_gridbox_celltitles header more_details_grid"  style="width:100%;text-align:center;margin-left: 0px;"><spring:message code="label.usage.billing.subscription.charge.amount"/></div>
        </div>
       </div>
     <c:set var="productServiceInstance" value=""></c:set>
     <div style="max-height:300px;float:left;overflow:auto;width:100%;">
     <c:forEach items="${invoiceitems}" var="invoiceitem" varStatus="invoiceitemLoopStatus">
         <c:if test="${invoiceitem.chargeType eq 'CHARGE'}">
         
         <c:if test="${invoiceitem.product.serviceInstance.uuid!= productServiceInstance}">
            <c:set var="productServiceInstance" value="${invoiceitem.product.serviceInstance.uuid}"></c:set>
            <div class="db_gridbox_rows" style="background-color:#C2E6F8">
              <span style="margin: 5px 0 0 10px;float:left;font-weight:bold;">
                <c:out value="${invoiceitem.product.serviceInstance.category}"/>:&nbsp;<c:out value="${invoiceitem.product.serviceInstance.name}"/>
              </span>
            </div>
          </c:if>
           <div class="db_gridbox_rows">    
                <div class="db_gridbox_columns" style="width:19%;">
                  <div class="db_gridbox_celltitles ellipsis" style="width:90%;" title="${invoiceitem.product.name}">
                  
                    <c:out value="${invoiceitem.product.name}"></c:out>
                  </div>
                </div>
                <div class="db_gridbox_columns" style="width:19%;">
                 <div class="db_gridbox_celltitles ellipsis" style="width:90%;" title="<c:out value="${invoiceitem.product.description}"></c:out>">
                   <c:out value="${invoiceitem.product.description}"></c:out>
                  </div>
                  
                </div>
                <div class="db_gridbox_columns" style="width:14%;">
                  <div class="db_gridbox_celltitles" style="text-align: right; width: 100%; margin-left: 0px; margin-right: 0px;">
                      <c:choose>
                        <c:when test="${invoiceitem.product.discrete}">
                          <fmt:formatNumber maxFractionDigits="0" value="${invoiceitem.quantity}"/>
                        </c:when>
                        <c:when test="${invoiceitem.quantity.unscaledValue() == 0}">
                           0.0000000000
                        </c:when>
                        <c:otherwise>
                          <c:out value="${invoiceitem.quantity}" />
                        </c:otherwise>
                      </c:choose>
                  </div>
                </div>
                <div class="db_gridbox_columns" style="width:14%;">
                  <div class="db_gridbox_celltitles"style="text-align: right; width: 100%; margin-left: 0px; margin-right: 0px;">
                      <c:choose>
                        <c:when test="${invoiceitem.product.discrete}">
                          <fmt:formatNumber maxFractionDigits="0" value="${invoiceitem.chargeableUsage}"/>
                        </c:when>
                        <c:when test="${invoiceitem.chargeableUsage.unscaledValue() == 0}">
                          0.0000000000
                        </c:when>
                        <c:otherwise>
                          <c:out value="${invoiceitem.chargeableUsage}" />
                        </c:otherwise>
                      </c:choose>
                  </div>
                </div>
                <div class="db_gridbox_columns" style="width:10%;">
                  <div class="db_gridbox_celltitles" style="text-align: right; width: 90%; margin-left: 0px; margin-right: 5px;">
                    <c:if test="${invoiceitem.unitPrice ne null}">
                      <c:out value="${invoice.tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoiceitem.unitPrice}" minFractionDigits="${currencyFractionalDigitsLimit}" />
                    </c:if>
                  </div>
                </div>
                <div class="db_gridbox_columns" style="width:14%;">
                  <div class="db_gridbox_celltitles"  style="text-align: center; width: 100%; margin-left: 0px; margin-right: 5px;"><c:out value="${invoiceitem.product.uom}" /></div>
                </div>
                <div class="db_gridbox_columns" style="width:10%;">
                 <div class="db_gridbox_celltitles" style="text-align: right; width: 90%; margin-left: 0px; margin-right: 5px;">
                   <c:out value="${invoice.tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoiceitem.amount}" minFractionDigits="${currencyFractionalDigitsLimit}" />
                 </div>
                </div>
          </div>
         
          </c:if>
        <c:if test="${invoiceitem.chargeType eq 'NET_TOTAL_CHARGES'}">
          <c:set var="netInvoiceItem" value="${invoiceitem}"></c:set>
        </c:if>
      </c:forEach>
       </div>

       
      <c:forEach items="${invoiceitemsForSummary}" var="invoiceitem" varStatus="invoiceitemLoopStatus">
        <c:if test="${invoiceitem.chargeType ne 'CHARGE' and invoiceitem.chargeType ne 'NET_TOTAL_CHARGES'}">
           <div class="db_gridbox_rows"  <c:if test="${invoiceitem.chargeType eq 'TOTAL_CHARGES' or invoiceitem.chargeType eq 'TOTAL_DISCOUNTS' or invoiceitem.chargeType eq 'TOTAL_TAX'}">
                      style="border-top-color: #E1E1E1;border-top-style: solid;border-top-width: .5px;border-bottom-color: #E1E1E1;border-bottom-style: solid;border-bottom-width: .5px"
                    </c:if>>    
                <div class="db_gridbox_columns" style="width:19%;">
                  <div class="db_gridbox_celltitles"></div>
                </div>
                <div class="db_gridbox_columns" style="width:37%;">
                 <div style="width: 100%;" class="db_gridbox_celltitles ellipsis">
                    <c:if test="${invoiceitem.chargeType ne 'TOTAL_CHARGES' and invoiceitem.chargeType ne 'TOTAL_DISCOUNTS' and invoiceitem.chargeType ne 'TOTAL_TAX'}">
                      <div class="db_gridbox_celltitles ellipsis" style="margin-top: 0px; margin-left: 2px;" title="<c:out value="${invoiceitem.description}"></c:out>"><c:out value="${invoiceitem.description}"></c:out></div>
                    </c:if>
                  </div>
                </div>
                <div class="db_gridbox_columns" style="width:24%;">
                 <div style="width: 100%;" class="db_gridbox_celltitles ellipsis">
                 <spring:message code="label.usage.billing.invoiceitem.chargetype.${invoiceitem.chargeType}" var="chargeTypeLabel"/>
                      <div class="db_gridbox_celltitles ellipsis" style="max-width:75%; margin-top: 0px; margin-left: 2px;" title="<c:out value="${chargeTypeLabel}"></c:out>"><c:out value="${chargeTypeLabel}"></c:out></div>
                  </div>
                </div>
                <div class="db_gridbox_columns" style="width:20%;">
                   <c:choose>
                   <c:when test="${invoiceitem.chargeType ne 'TOTAL_CHARGES' and invoiceitem.chargeType ne 'TOTAL_DISCOUNTS' and invoiceitem.chargeType ne 'TOTAL_TAX'}">
                   <div class="db_gridbox_celltitles" style="text-align: right; width: 95%; margin-left: 0px; margin-right: 0px;">
                    <c:out value="${invoice.tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoiceitem.amount}" minFractionDigits="${currencyFractionalDigitsLimit}" />
                    </c:when>
                    <c:otherwise>
                   <div class="db_gridbox_celltitles" style="text-align: right; width: 95%; margin-left: 0px; margin-right: 0px;">
                     <c:out value="${invoice.tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoiceitem.amount}" minFractionDigits="${minFractionDigits}" />
                    </c:otherwise>
                    </c:choose>
                   </div>
                </div>
          </div>
        </c:if>
      </c:forEach>
    </div> 
  </div>
</div>
<c:if test="${netInvoiceItem ne 'null'}">
<div class="db_gridbox_columns" style="width:60%;">
  <div class="db_gridbox_celltitles header more_details_grid"></div>
</div>
<div class="db_gridbox_columns" style="width:20%;">
  <div class="db_gridbox_celltitles header more_details_grid" style="width: 100%; margin-left: 0px;"><b><spring:message code="label.usage.billing.invoiceitem.chargetype.NET_TOTAL_CHARGES"/></b></div>
</div>
<div class="db_gridbox_columns" style="width:20%;">
  <div class="db_gridbox_celltitles header" style="text-align: right; width: 90%; margin-left: 0px; margin-right: 0px;"><b><c:out value="${invoice.tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${netInvoiceItem.amount}" minFractionDigits="${minFractionDigits}" /></b></div>
</div>
</c:if>