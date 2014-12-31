<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script type="text/javascript">
  var paymentsPage = true;
  if (typeof i18n === 'undefined') {
    var i18n = {};
  }
  if (typeof i18n.errors === 'undefined') {
    i18n.errors = {};
  }
  if (typeof i18n.buttons === 'undefined') {
    i18n.buttons = {};
  }
  i18n.errors.billingHistory = {
    showDetails : '<spring:message javaScriptEscape="true" code="js.errors.billingHistory.showDetails"/>'
  };

  i18n.buttons.billingHistory = {
    viewDetails : '<spring:message javaScriptEscape="true" code="js.button.billingHistory.viewDetails"/>',
    hideDetails : '<spring:message javaScriptEscape="true" code="js.button.billingHistory.hideDetails"/>'
  };
  var dictionary = {
    chargeBackSuccess : '<spring:message javaScriptEscape="true" code="message.payments.chargeback.successful"/>',
    chargeBackFailure : '<spring:message javaScriptEscape="true" code="message.payments.chargeback.failure"/>',
    issuingChargeBack : '<spring:message javaScriptEscape="true" code="message.payments.chargeback.issuing"/>',
    paymentSuccessMessage:'<spring:message javaScriptEscape="true" code="message.invoices.payment.successful"/>',
    paymentFailureMessage:'<spring:message javaScriptEscape="true" code="message.invoices.payment.failed"/>',
    processingPayment: '<spring:message javaScriptEscape="true" code="message.invoices.make.payment"/>',
    cancelCredit: '<spring:message javaScriptEscape="true" code="label.billing.history.cancel.credit"/>',
    cancellingCredit: '<spring:message javaScriptEscape="true" code="label.billing.history.cancelling.credit"/>',
    cancelCreditSuccess: '<spring:message javaScriptEscape="true" code="js.errors.tenants.cancelCredit.success"/>',
    cancelCreditFailure: '<spring:message javaScriptEscape="true" code="js.errors.tenants.cancelCredit.failure"/>',
    makePayment: '<spring:message javaScriptEscape="true" code="label.billing.history.make.payment"/>',
    makingPayment: '<spring:message javaScriptEscape="true" code="label.billing.history.making.payment"/>',
    recordPayment: '<spring:message javaScriptEscape="true" code="label.billing.history.record.payment"/>',
    recordingPayment: '<spring:message javaScriptEscape="true" code="label.billing.history.recording.payment"/>',
    cancelPaymentConfirm: '<spring:message javaScriptEscape="true" code="message.billing.history.issue.chargeback"/>',
    cancelPaymentTitle: '<spring:message javaScriptEscape="true" code="label.billing.history.cancel.payment"/>'
  };
</script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/paymentHistory.js"></script>
<script type="text/javascript">
	var billingUrl = "<%=request.getContextPath() %>/portal/billing/";
</script>

<div class="widget_box">
	<div class="widget_leftpanel">
		<div class="widget_titlebar">
			<h2 id="list_titlebar">
				<span id="list_all"><spring:message code="label.list.all" /> </span>
			</h2>
			<div class="widget_actionarea" id="top_actions">
				<div class="widget_moreactions action_menu_container" title="<spring:message code='manage'/>">
					<div class="widget_actionpopover" id="action_menu" style="display: none;">
						<div class="widget_actionpopover_top"></div>
						<div class="widget_actionpopover_mid">
							<ul class="widget_actionpoplist">
								<c:set var="noAvailableOptions" value="true"/>
								<sec:authorize access="hasRole('ROLE_FINANCE_CRUD')">
									<c:if test="${effectiveTenant.accountType.paymentModes eq 8}">
										<li id="recordPayment" class="recordPayment" onclick="recordPayment()">
											<spring:message code="label.billing.history.record.payment" />
										</li>
										<c:set var="noAvailableOptions" value="false"/>
									</c:if>
								</sec:authorize>
								<sec:authorize access="hasRole('ROLE_ACCOUNT_BILLING_ADMIN')">
									<c:if test="${effectiveTenant.accountType.paymentModes eq 2}">
										<li id="makePayment" class="makePayment" onclick="makePayment()">
											<spring:message code="label.billing.history.make.payment"/>
										</li>
										<c:set var="noAvailableOptions" value="false"/>
									</c:if>
								</sec:authorize>
								<c:if test="${noAvailableOptions}">
									<li id="no_actions_available_volume" title='<spring:message code="label.no.actions.available"/>'>
										<spring:message code="label.no.actions.available" />
									</li>
								</c:if>
							</ul>
						</div>
						<div class="widget_actionpopover_bot"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="widget_searchpanel">
			<div id="search_panel" style="margin: 8px 0 0 13px; color: #FFFFFF;"></div>
		</div>
		<div class="widget_navigation">
			<ul class="widget_navigationlist" id="grid_row_container">
				<c:choose>
					<c:when test="${(empty salesLedgerRecords || salesLedgerRecords == null) && (empty initialDeposit || initialDeposit == null)}">
						<li class="widget_navigationlist nonlist" id="non_list"><span class="navicon payments"></span>
							<div class="widget_navtitlebox">
								<span class="newlist"><spring:message code="message.no.payments.available"/></span>
							</div>
						</li>
					</c:when>
					<c:otherwise>
						<c:forEach var="salesLedgerRecord" items="${salesLedgerRecords}" varStatus="status">
							<c:choose>
								<c:when test="${status.index == 0}">
									<c:set var="selected" value="selected active" />
								</c:when>
								<c:otherwise>
									<c:set var="selected" value="" />
								</c:otherwise>
							</c:choose>

							<li class='<c:out value="widget_navigationlist ${selected} invoices"/>' id="<c:out value='row${salesLedgerRecord.param}'/>"
								onclick="viewPaymentDetails(this)" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
								
								<c:if test="${salesLedgerRecord.type eq 'AUTO' || salesLedgerRecord.type eq 'NOTIONAL' || salesLedgerRecord.type eq 'MANUAL' || salesLedgerRecord.type eq 'RECORD' || salesLedgerRecord.type eq 'ADJUSTMENT'}">
									<span class="navicon payments" id="nav_icon"></span>
								</c:if>
								<c:if test="${salesLedgerRecord.type eq 'SERVICE_CREDIT'}">
									<span class="navicon credit" id="nav_icon"></span>
								</c:if>
								<c:if test="${salesLedgerRecord.type eq 'INVOICE' && salesLedgerRecord.invoice.type.name eq 'DebitNote'}">
									<span class="navicon credit_reverse" id="nav_icon"></span>
								</c:if> 
								
								<div class="widget_navtitlebox">
									<span class="title"> 
										<spring:message code="dateonly.format" var="dateonly_format" /> 
										<fmt:formatDate value="${salesLedgerRecord.createdAt}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}" />
										<c:set var="uuid" value="${salesLedgerRecord.uuid}"/>
									 	&nbsp;(<c:out value="${fn:substring(uuid, 0, 8)}" />...)
									</span> 
									<span class="subtitle"> 
										<%-- <c:if test="${salesLedgerRecord.type eq 'AUTO' || salesLedgerRecord.type eq 'NOTIONAL' || salesLedgerRecord.type eq 'MANUAL' || salesLedgerRecord.type eq 'RECORD'}"> --%> 
											<c:out value="${tenant.currency.sign}" />
											<fmt:formatNumber pattern="${currencyFormat}" value="${salesLedgerRecord.transactionAmount}" minFractionDigits="${minFractionDigits}" />
										<%-- </c:if>  --%>
									</span>
								</div> 
								
								<!--Info popover starts here-->
								<div class="widget_info_popover" id="info_bubble" style="display: none">
									<div class="popover_wrapper">
										<div class="popover_shadow"></div>
										<div class="popover_contents">
											<div class="raw_contents">
												<div class="raw_content_row">
													<div class="raw_contents_title">
														<span><spring:message code="billing_history.list.header.date" />:</span>
													</div>
													<div class="raw_contents_value">
														<span> 
															<spring:message code="dateonly.format" var="dateonly_format" /> 
															<fmt:formatDate value="${salesLedgerRecord.createdAt}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}" />
														</span>
													</div>
												</div>
											     <div class="raw_content_row">
													<div class="raw_contents_title">
														<span><spring:message code="label.billing.history.billingUUId.payment"/>:</span>
													</div>
													<div class="raw_contents_value">
														<span> 
															<c:out value="${salesLedgerRecord.uuid}" />
														</span>
													</div>
												</div>
											
												<div class="raw_content_row">
													<div class="raw_contents_title">
														<span><spring:message code="label.billing.history.billingId.payment" />:</span>
													</div>
													<div class="raw_contents_value">
														<span> 
															<c:out value="${salesLedgerRecord.paymentTransaction.transactionId}" />
														</span>
													</div>
												</div>
												<div class="raw_content_row">
													<div class="raw_contents_title">
														<span><spring:message code="label.state" />:</span>
													</div>
													<div class="raw_contents_value">
														<span> 
															<c:out value="${salesLedgerRecord.paymentTransaction.state}" />
														</span>
													</div>
												</div>
												<div class="raw_content_row">
													<div class="raw_contents_title">
														<span><spring:message code="billing_history.list.header.billed" />:</span>
													</div>
													<div class="raw_contents_value">
														<span>
														 	<c:if test="${salesLedgerRecord.type eq 'AUTO' || salesLedgerRecord.type eq 'NOTIONAL' || salesLedgerRecord.type eq 'MANUAL' || salesLedgerRecord.type eq 'RECORD'}"> 
																<c:out value="${tenant.currency.sign}" />
																<fmt:formatNumber pattern="${currencyFormat}" value="${salesLedgerRecord.transactionAmount}" minFractionDigits="${minFractionDigits}" />
															</c:if>
														</span>
													</div>
												</div>
												<div class="raw_content_row">
													<div class="raw_contents_title">
														<span><spring:message code="label.billing.history.description" />:</span>
													</div>
													<div class="raw_contents_value">
														<span> <c:out value="${salesLedgerRecord.memo}" /></span>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div> <!--Info popover ends here-->
								
							</li>
						</c:forEach>
            <c:if test="${not empty initialDeposit && initialDeposit != null}">
              <c:choose>
                <c:when test="${(empty salesLedgerRecords || salesLedgerRecords == null)}">
                  <c:set var="selected" value="selected active" />
                </c:when>
                <c:otherwise>
                  <c:set var="selected" value="" />
                </c:otherwise>
              </c:choose>

              <li class='<c:out value="widget_navigationlist ${selected} invoices js_initialDeposit"/>' id="<c:out value='row${initialDeposit.id}'/>"
                onclick="viewPaymentDetails(this)" onmouseover="showInfoBubble(this)" onmouseout="hideInfoBubble(this)">
                <span class="navicon payments" id="nav_icon"></span>
                <div class="widget_navtitlebox">
                  <span class="title"> 
                    <spring:message code="dateonly.format" var="dateonly_format" /> 
                    <fmt:formatDate value="${initialDeposit.receivedOn}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}" />
                  </span> 
                  <span class="subtitle"> 
                      <c:out value="${tenant.currency.sign}" />
                      <fmt:formatNumber pattern="${currencyFormat}" value="${initialDeposit.amount}" minFractionDigits="${minFractionDigits}" />
                  </span>
                </div> 
                
                <!--Info popover starts here-->
                <div class="widget_info_popover" id="info_bubble" style="display: none">
                  <div class="popover_wrapper">
                    <div class="popover_shadow"></div>
                    <div class="popover_contents">
                      <div class="raw_contents">
                        <div class="raw_content_row">
                          <div class="raw_contents_title">
                            <span><spring:message code="billing_history.list.header.date" />:</span>
                          </div>
                          <div class="raw_contents_value">
                            <span> 
                              <spring:message code="dateonly.format" var="dateonly_format" /> 
                              <fmt:formatDate value="${initialDeposit.receivedOn}" pattern="${dateonly_format}" timeZone="${currentUser.timeZone}" />
                            </span>
                          </div>
                        </div>
                        <div class="raw_content_row">
                          <div class="raw_contents_title">
                            <span><spring:message code="label.state" />:</span>
                          </div>
                          <div class="raw_contents_value">
                            <span> 
                              <spring:message code="message.paymentstate.completed" />
                            </span>
                          </div>
                        </div>
                        <div class="raw_content_row">
                          <div class="raw_contents_title">
                            <span><spring:message code="billing_history.list.header.billed" />:</span>
                          </div>
                          <div class="raw_contents_value">
                            <span>
                                <c:out value="${tenant.currency.sign}" />
                                <fmt:formatNumber pattern="${currencyFormat}" value="${initialDeposit.amount}" minFractionDigits="${minFractionDigits}" />
                            </span>
                          </div>
                        </div>
                        <div class="raw_content_row">
                          <div class="raw_contents_title">
                            <span><spring:message code="label.billing.history.description" />:</span>
                          </div>
                          <div class="raw_contents_value">
                            <span><spring:message code="label.billing.leftnav.record.deposit" /></span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div> <!--Info popover ends here-->
                
              </li>
            </c:if>
					</c:otherwise>
				</c:choose>
			</ul>
		</div>
		<div class="widget_panelnext">
			<div class="widget_navnextbox">
				<c:choose>
					<c:when test="${current_page <= 1}">
						<a class="widget_navnext_buttons prev nonactive" href="javascript:void(0);" id="click_previous"><spring:message
								code="label.previous.short" /></a>
					</c:when>
					<c:otherwise>
						<a class="widget_navnext_buttons prev" href="javascript:void(0);" id="click_previous" onclick="previousClick()"><spring:message
								code="label.previous.short" /></a>
					</c:otherwise>
				</c:choose>

				<c:choose>
					<c:when test="${enable_next == 'True'}">
						<a class="widget_navnext_buttons next" href="javascript:void(0);" id="click_next" onclick="nextClick()"><spring:message
								code="label.next" /></a>
					</c:when>
					<c:otherwise>
						<a class="widget_navnext_buttons next nonactive" href="javascript:void(0);" id="click_next"><spring:message
								code="label.next" /></a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
	<div class="widget_rightpanel" id="viewBillingActivityDiv">
		<c:if test="${empty salesLedgerRecords || salesLedgerRecords == null}">
			<jsp:include page="/WEB-INF/jsp/tiles/billing/viewPaymentActivity.jsp"></jsp:include>
		</c:if>
	</div>
	<div class="widget_rightpanel" id="recordOrMakePaymentFormDiv" title='<spring:message code="label.billing.history.record.payment"/>' style="display:none">
		<jsp:include page="/WEB-INF/jsp/tiles/billing/recordOrMakePayment.jsp"></jsp:include>
	</div>
	<div class="widget_rightpanel" id="issueChargeBackDiv" title='<spring:message code="label.billing.history.issue.charge.back"/>' style="display: none">
		<div class="dialog_formcontent wizard">
			<ol>
				<li style="margin: 10px 0 0 10px;">
					<label style="color: #111; font-weight: bold; width: 380px; margin: 4px 0 0 8px;"> <spring:message code="message.billing.history.issue.chargeback" /></label>
				</li>
			</ol>
		</div>
	</div>
	<div class="widget_rightpanel" id="cancelCreditDiv" title='<spring:message code="label.billing.history.cancel.credit"/>' style="display: none">
		<div class="dialog_formcontent wizard">
			<ol>
				<li style="margin: 10px 0 0 10px;">
					<label style="color: #111; font-weight: bold; width: 380px; margin: 4px 0 0 8px;"> <spring:message code="message.billing.history.reverse.credits" /></label>
				</li>
			</ol>
		</div>
	</div>
<input type="hidden" id="tenantParam" value="<c:out value="${tenant.param}"/>"/>
<input type="hidden" id="current_page"  value="<c:out value="${current_page}"/>"/>

