<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <div class="db_gridbox_rows detailsodd">
              <div class="db_gridbox_columns" style="width:20%;">
                  <div class="db_gridbox_celltitles details"><strong><spring:message code="ui.products.label.create.description"/></strong></div>
              </div>
              <div class="db_gridbox_columns" style="width:75%;">
                <div class="db_gridbox_celltitles details">
                <c:out value="${rateCard.description}"></c:out>
                </div>
              </div>
            </div>
              <div class="db_gridbox_rows detailsheader">
                <div class="db_gridbox_columns" style="width:20%;">
                   <div class="db_gridbox_celltitles header"><spring:message code="label.bundle.edit.urc.product"/></div>
                </div>
                  <div class="db_gridbox_columns" style="width:10%;">
                   <div class="db_gridbox_celltitles header"><spring:message code="label.bundle.edit.urc.uom"/></div>
                </div>
                <c:forEach var="supportedCurrency" items="${catalog.supportedCurrenciesByOrder}" end="3">
                  <div class="db_gridbox_columns " style="width:17%;">
                   <div class="db_gridbox_celltitles header">
                   <spring:message code="label.price"/>&nbsp;(<c:out value="${supportedCurrency.currency.currencyCode}"></c:out>)
                   </div>
                </div>
                </c:forEach> 
              </div>
              
              <!-- End of Header -->
              <div style="width:100%;"  class="main_gridlistbox">
              <!-- Data -->
               <c:forEach var="chargesMap" items="${rateCardChargesMap}" varStatus="rateCardstatus">               
                  <c:choose>
            <c:when test="${rateCardstatus.index % 2 == 0}">
              <c:set var="rowClass" value="odd"/>
            </c:when>
            <c:otherwise>
                <c:set var="rowClass" value="even"/>
            </c:otherwise>
          </c:choose>               
                   <div class="<c:out value="db_gridbox_rows ${selected}  ${rowClass}"/>">
                        <div class="db_gridbox_columns" style="width:20%;">
                            <div class="db_gridbox_celltitles">
                              <c:out value="${chargesMap.key.product.name}"/></div>
                          </div>
                          <div class="db_gridbox_columns" style="width:10%;">
                            <div class="db_gridbox_celltitles">
                              <spring:message code="${chargesMap.key.product.uom}"/></div>
                          </div>
                          <c:forEach end="3" items="${chargesMap.value}" var="charge">
                             <div class="db_gridbox_columns" style="width:17%;">
                            <div class="db_gridbox_celltitles"><fmt:formatNumber pattern="${currencyFormat}"  minFractionDigits="${minFractionDigits}" value="${charge.price}"  />
                            </div>
                            </div>
                          </c:forEach>                           
                    </div>               
               </c:forEach>
               </div>
 