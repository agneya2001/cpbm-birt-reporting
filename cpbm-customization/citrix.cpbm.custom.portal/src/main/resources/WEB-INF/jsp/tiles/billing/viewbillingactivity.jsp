<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="widget_actionbar">
</div>

<div class="top_notifications">
  <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
  <div id="action_result_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
</div>

<div class="widget_browser">
  
    <div class="widget_rightselectedheader">
        <span class="icon"></span>
        <p>
            <c:choose>
			    <c:when test="${empty invoices || invoices == null}">
			      <spring:message code="message.no.data.to.show"/>
			    </c:when>
			    <c:otherwise>
			      <c:forEach var="invoice" items="${invoices}" varStatus="status">
			            <c:choose>
			              <c:when test="${status.index == 0}">
			                  <c:set var="firstinvoice" value="${invoice}"/>
			              </c:when>
			           </c:choose>
			       </c:forEach>
			       <spring:message code="month.date.year.hour.minute.format" var="month_date"/>
                   <spring:message code="label.invoice.billing.period"/>: <fmt:formatDate pattern="${month_date}" value="${firstinvoice.accountStatement.billingPeriodStartDate}" timeZone="${currentUser.timeZone}"/>&nbsp;&nbsp;-&nbsp;&nbsp;<fmt:formatDate pattern="${month_date}" value="${firstinvoice.accountStatement.billingPeriodEndDate}" timeZone="${currentUser.timeZone}"/>              
			    </c:otherwise> 
			  </c:choose>
        </p>
    </div>
    <div class="widget_browsermaster fullscreen">
    <c:if test="${!empty invoices && fn:length(invoices) gt 0 }">
        <div class="widget_browser_contentarea">
            <div class="widget_browsergrid_wrapper fullpagegrid">
                    <div class="widget_grid inline header">
                        <div class="widget_grid_cell" style="width:15%;">
                            <span class="header">
                            <spring:message code="label.state"/>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:15%;">
                            <span class="header">
                            <spring:message code="label.invoice.type"/>
                          </span>
                        </div>
                        
                        <div class="widget_grid_cell" style="width:30%;">
                            <span class="header">
                            <spring:message code="subscription.bundle"/>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:35%;">
                            <span class="header">
                            <spring:message code="label.details"/>
                          </span>
                        </div>
                    </div>
                   
                    <c:forEach var="invoice" items="${invoices}" varStatus="status">
                    <c:choose>
                      <c:when test="${status.index % 2 == 0}">
                          <c:set var="rowClass" value="odd"/>
                      </c:when>
                      <c:otherwise>
                          <c:set var="rowClass" value="even"/>
                      </c:otherwise>
                      </c:choose> 
                    <div class="<c:out value="widget_grid fullpage ${rowClass} fixheight"/>" >
                        <div class="widget_grid_cell" style="width:15%;">
                            <span class="celltext">
                            <spring:message code="billing.status.${invoice.state}"/>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:15%;">
                            <span class="celltext">
                            <spring:message code="label.invoice.type.${invoice.type}" />
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:30%;">
                            <span class="celltext">
                                <a href="/portal/portal/billing/subscriptions?tenant=<c:out value="${effectiveTenant.param}"/>&id=<c:out value="${invoice.subscription.uuid}" />"><c:out value="${invoice.subscription.productBundle.name}" /></a>
                          </span>
                        </div>
                        <div class="widget_grid_cell" style="width:35%;">
                           <span class="descpwrapper" style="width:340px;">
                            <span class="descpwrapper desctext">
                              <strong><spring:message code="label.invoice.total.amount"/>: </strong>
                              <a class="invoiceDetailsLink" href="javascript:void(0);" style="cursor:default;" id="moreLink_<c:out value='${invoice.uuid}'/>" type="${invoice.type}">
                                  <c:out value="${invoice.tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" value="${invoice.amount}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" />
                              </a>
                            </span>
                            <span class="descpwrapper desctext">
                              <strong><spring:message code="label.invoice.amount.due"/>: </strong><c:out value="${invoice.tenant.currency.sign}" /><fmt:formatNumber pattern="${currencyFormat}" value="${invoice.amountDue}" maxFractionDigits="${minFractionDigits}" minFractionDigits="${minFractionDigits}" />
                            </span>

                          </span>
                        </div>
                        <c:set var="invoice" value="${invoice}" scope="request"></c:set>
                        <c:set var="currentUser" value="${currentUser}" scope="request"></c:set>
                        <jsp:include page="dialog_invoice_details.jsp"></jsp:include>
                    </div>
                    </c:forEach>
        </div>
    </div>
    </c:if>
  </div>
</div>
