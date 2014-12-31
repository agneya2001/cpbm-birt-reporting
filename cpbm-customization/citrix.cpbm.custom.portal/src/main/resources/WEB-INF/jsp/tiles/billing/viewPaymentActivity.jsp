<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="widget_actionbar">
	<div id="top_actions" class="widget_actionarea">
		<div style="display: none" id="spinning_wheel">
			<div class="maindetails_footer_loadingpanel"></div>
			<div class="maindetails_footer_loadingbox first">
				<div class="maindetails_footer_loadingicon"></div>
				<p id="in_process_text"></p>
			</div>
		</div>
	</div>
</div>
<div class="top_notifications">
  <div id="top_message_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg"></p>
  </div>
  <div id="action_result_panel" class="common_messagebox widget" style="display:none;">
    <button type="button" class="close js_close_parent" >&times;</button>
    <span id="status_icon"></span><p id="msg">${errorMsg}</p>
  </div>
</div>

<div class="widget_browser">
	<div class="widget_browsermaster">
		<div class="widget_browser_contentarea">
      <div class="widget_browsergrid_wrapper master">

        <!-- Logic for evaluating payment amount for slr and initial deposit is different. Precomputing them here to be used below -->
        <c:choose>
          <c:when test="${isInitialDeposit}">
            <spring:message code='dateonly.format' var="dateFormat" />
            <c:set var="paymentDate" value="${initialDeposit.receivedOn}"></c:set>
            <c:set var="paymentAmount" value="${initialDeposit.amount}"></c:set>
          </c:when>
          <c:otherwise>
            <spring:message code="date.format" var="dateFormat" />
            <c:set var="paymentDate" value="${salesLedgerRecord.createdAt}"></c:set>
            <c:set var="paymentAmount" value="${salesLedgerRecord.transactionAmount}"></c:set>
          </c:otherwise>
        </c:choose>

        <div class="widget_grid master">
          <div class="widget_grid_labels">
            <span><spring:message code="billing_history.list.header.date" /></span>
          </div>
          <div class="widget_grid_description">
            <span> <fmt:formatDate value="${paymentDate}" pattern="${dateFormat}" timeZone="${currentUser.timeZone}" />
            </span>
          </div>
        </div>
        <c:if test="${!isInitialDeposit}">
          <div class="widget_grid master">
            <div class="widget_grid_labels">
              <span><spring:message code="label.billing.history.billingUUId.payment" /></span>
            </div>
            <div class="widget_grid_description">
              <span class="uuid"> <c:out value="${salesLedgerRecord.uuid}" /></span>
            </div>
          </div>
        </c:if>
        <div class="widget_grid master">
          <div class="widget_grid_labels">
            <span><spring:message code="billing_history.list.header.amount" /></span>
          </div>
          <div class="widget_grid_description">
            <span> <c:out value="${tenant.currency.sign}" /> <fmt:formatNumber pattern="${currencyFormat}" value="${paymentAmount}"
                minFractionDigits="${minFractionDigits}" />
            </span>
          </div>
        </div>
      </div>
      <div class="widget_masterbigicons payments"></div>
		</div>
	</div>

	<div class="widget_browser_contentarea">
		<ul class="widgets_detailstab">
			<li class="widgets_detailstab active" id="details_tab"><spring:message code="label.details" /></li>
		</ul>
		<div class="widget_details_actionbox">
			<ul class="widget_detail_actionpanel">
				<sec:authorize access="hasRole('ROLE_FINANCE_CRUD')">
          <!-- None of the following actions need to be shown for initial deposit entry -->
					<c:if test="${!isInitialDeposit && (salesLedgerRecord.cancellationReferenceId eq null)}">
						<c:if test="${salesLedgerRecord.type eq 'AUTO' ||salesLedgerRecord.type eq 'MANUAL'}"> 
							<li class="widget_detail_actionpanel cancelCreditOrPayment">
								<a href="#" id="<c:out value="cancelCreditOrPayment${salesLedgerRecord.param}"/>" onclick="chargeBack(this);"><spring:message code="label.billing.history.issue.charge.back" /></a>
							</li>
						</c:if>
						<c:if test="${salesLedgerRecord.type eq 'NOTIONAL' || salesLedgerRecord.type eq 'RECORD'}"> 
							<li class="widget_detail_actionpanel cancelCreditOrPayment">
								<a href="#" id="<c:out value="cancelCreditOrPayment${salesLedgerRecord.param}"/>" onclick="cancelRecordedPayment(this);"><spring:message code="label.billing.history.cancel.payment" /></a>
							</li>
						</c:if>
						<c:if test="${salesLedgerRecord.type eq 'SERVICE_CREDIT'}">
							<li class="widget_detail_actionpanel cancelCreditOrPayment">
								<a href="#" id="<c:out value="cancelCreditOrPayment${salesLedgerRecord.param}"/>" onclick="cancelCredit(this);"><spring:message code="label.billing.history.cancel.credit" /></a>
							</li>
						</c:if>
					</c:if>
				</sec:authorize>
			</ul>
		</div>

    <div class="widget_browsergrid_wrapper details">
      <div class="widget_grid details">
        <div class="widget_grid_labels">
          <span><spring:message code="label.billing.history.description" /></span>
        </div>
        <div class="widget_grid_description">
          <!-- For intial deposit hard code the description but for slr take it from memo -->
          <spring:message code="label.billing.leftnav.record.deposit" var="paymentDesc"/>
          <c:if test="${!isInitialDeposit}">
            <c:set var="paymentDesc" value="${salesLedgerRecord.memo}"></c:set>
          </c:if>
          <span>${paymentDesc}</span>
        </div>
      </div>
      <sec:authorize access="hasRole('ROLE_FINANCE_CRUD')">
        <c:if test="${not empty salesLedgerRecord}">
          <div class="widget_grid details">
            <div class="widget_grid_labels">
              <span><spring:message code="label.type" /></span>
            </div>
            <div class="widget_grid_description">
              <span><spring:message code="salesledgerrecord.type.${salesLedgerRecord.type}" /></span>
            </div>
          </div>
        </c:if>
      </sec:authorize>
      <c:if test="${isInitialDeposit or salesLedgerRecord != null and salesLedgerRecord.paymentTransaction.state !=null and (not empty salesLedgerRecord.paymentTransaction.state)}">
        <div class="widget_grid details">
          <div class="widget_grid_labels">
            <span><spring:message code="label.state" /></span>
          </div>
          <div class="widget_grid_description">
            <spring:message code="message.paymentstate.completed" var="paymentState" />
            <c:if test="${!isInitialDeposit}">
              <spring:message code="message.paymentstate.${fn:toLowerCase(salesLedgerRecord.paymentTransaction.state)}" var="paymentState" />
            </c:if>
            <span>${paymentState}</span>
          </div>
        </div>
      </c:if>
      <c:if test="${salesLedgerRecord != null and salesLedgerRecord.paymentTransaction.transactionId !=null and (not empty salesLedgerRecord.paymentTransaction.transactionId)}">
        <div class="widget_grid details">
          <div class="widget_grid_labels">
            <span><spring:message code="label.billing.history.billingId.payment" /></span>
          </div>
          <div class="widget_grid_description">
            <span>${salesLedgerRecord.paymentTransaction.transactionId}</span>
          </div>
        </div>
      </c:if>
    </div>
  </div>
</div>
 
<div id="updateBillingActivityDiv" style="display:none"> <!-- UNUSED REMOVE ? -->
</div>

<input id="invoice_id" type="hidden" value='<c:out value="${salesLedgerRecord.paymentTransaction.transactionId}" />'/>
