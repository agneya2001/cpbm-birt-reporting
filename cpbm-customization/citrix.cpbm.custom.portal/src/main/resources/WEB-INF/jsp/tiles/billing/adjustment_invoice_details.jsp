<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="netInvoiceItem" value="null"></c:set>
<spring:message code="dateonly.format" var="date_only_format"/>
<c:set var="invoiceUuid" value="${invoice.uuid}"></c:set>
<c:set var="invoiceType" value="${invoice.type}"></c:set>
<c:set var="invoiceitems" value="${invoice.invoiceItems}"></c:set>
<fmt:formatDate var="invoiceStartDate" pattern="${date_only_format}" value="${invoice.serviceStartDate}" timeZone="${currentUser.timeZone}"/>
<fmt:formatDate var="invoiceEndDate" pattern="${date_only_format}" value="${invoice.serviceEndDate}" timeZone="${currentUser.timeZone}"/>

<spring:message code="label.invoice.details" var="invoiceDetailsString"/>
<spring:message code="dateonly.format" var="date_only_format"/>
<c:set var="invoiceDetailTitle" value="${invoiceDetailsString} (${invoiceStartDate} - ${invoiceEndDate})"></c:set>

    <div class="more_details_grid_lightbox" >
    
      <div class="mainbox">
           <div class="morecontentarea"> 
           <div class="db_gridbox_rows" style="border-bottom-color: #E1E1E1;border-bottom-style: solid;border-bottom-width: .5px">
                  <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles header more_details_grid"><spring:message code="ui.label.emptylist.billing.leftnav.subscription"/></div>
                  </div>
                  <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles header more_details_grid"><spring:message code="ui.campaigns.label.create.title"/></div>
                  </div>
                  <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles header more_details_grid"><spring:message code="label.usage.billing.subscription.charge.type"/></div>
                  </div>
                  <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles header more_details_grid"><spring:message code="label.invoice.details.quantity"/></div>
                  </div>
                  <div class="db_gridbox_columns" style="width:20%;">
                    <div class="db_gridbox_celltitles header more_details_grid" style="width:100%;text-align:center;margin-left: 0px;"><spring:message code="label.usage.billing.subscription.charge.amount"/></div>
                  </div> 
            </div>
          <div style="max-height:300px;float:left;overflow:auto;width:100%;">
             <c:forEach items="${invoiceitems}" var="invoiceitem" varStatus="invoiceitemLoopStatus">
                 <c:if test="${invoiceitem.chargeType eq 'CHARGE'}">
                 <div class="db_gridbox_rows" 
                    <c:if test="${invoiceitem.chargeType eq 'TOTAL_CHARGES' or invoiceitem.chargeType eq 'TOTAL_DISCOUNTS' or invoiceitem.chargeType eq 'TOTAL_TAX'}">
                      style="border-top-color: #E1E1E1;border-top-style: solid;border-top-width: .5px;border-bottom-color: #E1E1E1;border-bottom-style: solid;border-bottom-width: .5px"
                    </c:if>
                  >    
                        <div class="db_gridbox_columns" style="width:20%;">
                          <div class="db_gridbox_celltitles">
                            <c:if test="${invoiceitem.chargeType eq 'CHARGE'}">
                              <c:choose>
                                <c:when test="${not empty invoiceitem.subscription.handle.resourceName}">
                                  <c:out value="${invoiceitem.subscription.handle.resourceName}"></c:out>
                                </c:when>
                                <c:otherwise>
                                  <c:out value="${invoiceitem.subscription.uuid}"></c:out>
                                </c:otherwise>
                              </c:choose>
                            </c:if>
                          </div>
                        </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                          <div class="db_gridbox_celltitles">
                            <c:if test="${invoiceitem.chargeType ne 'TOTAL_CHARGES' and invoiceitem.chargeType ne 'TOTAL_DISCOUNTS' and invoiceitem.chargeType ne 'TOTAL_TAX'}"><c:out value="${invoiceitem.description}"></c:out></c:if>
                          </div>
                        </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                          <spring:message code="label.usage.billing.invoiceitem.chargetype.${invoiceitem.chargeType}" var="chargeTypeLabel"/>
                          <div class="db_gridbox_celltitles"><c:out value="${chargeTypeLabel}"></c:out></div>
                        </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                          <div class="db_gridbox_celltitles"> </div>
                        </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                         <div class="db_gridbox_celltitles" style="text-align: center; width: 90%; margin-left: 0px; margin-right: 5px;">
                           <c:out value="${invoice.tenant
                           .currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoiceitem.amount}" minFractionDigits="${minFractionDigits}" />
                         </div>
                       </div>
                  </div>
                  </c:if>
                  <c:if test="${invoiceitem.chargeType eq 'NET_TOTAL_CHARGES'}">
                    <c:set var="netInvoiceItem" value="${invoiceitem}"></c:set>
                  </c:if>
             </c:forEach>
            </div>
<c:forEach items="${invoiceitems}" var="invoiceitem" varStatus="invoiceitemLoopStatus">
                 <c:if test="${invoiceitem.chargeType eq 'TOTAL_CHARGES' or invoiceitem.chargeType eq 'TOTAL_DISCOUNTS' or invoiceitem.chargeType eq 'TOTAL_TAX'}">
                 <div class="db_gridbox_rows" 
                    <c:if test="${invoiceitem.chargeType eq 'TOTAL_CHARGES' or invoiceitem.chargeType eq 'TOTAL_DISCOUNTS' or invoiceitem.chargeType eq 'TOTAL_TAX'}">
                      style="border-top-color: #E1E1E1;border-top-style: solid;border-top-width: .5px;border-bottom-color: #E1E1E1;border-bottom-style: solid;border-bottom-width: .5px"
                    </c:if>
                  >    
                        <div class="db_gridbox_columns" style="width:20%;">
                          <div class="db_gridbox_celltitles">
                            <c:if test="${invoiceitem.chargeType eq 'CHARGE'}">
                              <c:choose>
                                <c:when test="${not empty invoiceitem.subscription.handle.resourceName}">
                                  <c:out value="${invoiceitem.subscription.handle.resourceName}"></c:out>
                                </c:when>
                                <c:otherwise>
                                  <c:out value="${invoiceitem.subscription.uuid}"></c:out>
                                </c:otherwise>
                              </c:choose>
                            </c:if>
                          </div>
                        </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                          <div class="db_gridbox_celltitles">
                            <c:if test="${invoiceitem.chargeType ne 'TOTAL_CHARGES' and invoiceitem.chargeType ne 'TOTAL_DISCOUNTS' and invoiceitem.chargeType ne 'TOTAL_TAX'}"><c:out value="${invoiceitem.description}"></c:out></c:if>
                          </div>
                        </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                          <spring:message code="label.usage.billing.invoiceitem.chargetype.${invoiceitem.chargeType}" var="chargeTypeLabel"/>
                          <div class="db_gridbox_celltitles"><c:out value="${chargeTypeLabel}"></c:out></div>
                        </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                          <div class="db_gridbox_celltitles"> </div>
                        </div>
                        <div class="db_gridbox_columns" style="width:20%;">
                         <div class="db_gridbox_celltitles" style="text-align: center; width: 90%; margin-left: 0px; margin-right: 5px;">
                           <c:out value="${invoice.tenant
                           .currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${invoiceitem.amount}" minFractionDigits="${minFractionDigits}" />
                         </div>
                       </div>
                  </div>
                  </c:if>
                  <c:if test="${invoiceitem.chargeType eq 'NET_TOTAL_CHARGES'}">
                    <c:set var="netInvoiceItem" value="${invoiceitem}"></c:set>
                  </c:if>
             </c:forEach>

         </div> 
        
      </div>
    </div>
        <c:if test="${netInvoiceItem ne 'null'}">
       <div class="db_gridbox_columns" style="width:60%;">
                      <div class="db_gridbox_celltitles header more_details_grid"></div>
        </div>
        <div class="db_gridbox_columns" style="width:18%;">
                      <div class="db_gridbox_celltitles header more_details_grid"><b><spring:message code="label.usage.billing.invoiceitem.chargetype.NET_TOTAL_CHARGES"/></b></div>
        </div>
        <div class="db_gridbox_columns" style="width:22%;">
                      <div class="db_gridbox_celltitles header" style="text-align: center; width: 90%; margin-left: 0px; margin-right: 5px;"><b><c:out value="${invoice.tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}"  value="${netInvoiceItem.amount}" minFractionDigits="${minFractionDigits}" /></b></div>
        </div>
     </c:if>