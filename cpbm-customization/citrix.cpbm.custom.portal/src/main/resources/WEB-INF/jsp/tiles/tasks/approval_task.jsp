<!-- Copyright 2013 Citrix Systems, Inc. Licensed under the BSD 2 license. See LICENSE for more details. -->
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:url var="actionURL" value="/portal/tasks/approval-task"></spring:url>

<div class="dialog_formcontent wizard">
 <div class="j_actionForm">
  <div class="widgetwizard_contentarea sixstepswizard" style="height:auto;">
    <div class="widgetwizard_boxes fullheight sixstepswizard" style="height:auto;">
      <c:out value="${taskDetails}" escapeXml="false"></c:out>
      <div class="widgetwizard_reviewbox sixstepswizard ">
		<c:if test="${task.state != 'SUCCESS' && task.state != 'FAILURE'}">
          <form:form action="${actionURL}" method="post" id="approval_task_form" commandName="task">
            <input type="hidden" value="${task.uuid}" id="approval_task_uuid" />
            <ul>
              <li class="subselection">
                <span class="label sublabel"><spring:message code="ui.accounts.all.pending.changes.memo" /></span>
                <div class="nonmandatory_wrapper">
                	<form:textarea path="memo" class="commonboxes_formbox_withouttextbox equalwidth"  maxlength="1024"></form:textarea>
                </div>
                <div id="memo_errormsg" class="dialog_formcontent_errormsg" style="width:auto;"></div>
              </li>
            </ul>
          </form:form>
        </c:if>
     </div>
    </div>
	<div id="spinning_wheel_rhs" style="display: none;">
		<div class="widget_blackoverlay widget_rightpanel"
			style="height: 100%"></div>
		<div class="widget_loadingbox tasks">
			<div class="widget_loaderbox">
				<span class="bigloader"></span>
			</div>
			<div class="widget_loadertext">
				<p id="in_process_text">
					<spring:message code="label.loading" />
				</p>
			</div>
		</div>
	</div>
	</div>
  <div id="buttons" class="widgetwizard_nextprevpanel sixstepswizard">
    <c:if test="${task.state != 'SUCCESS' && task.state != 'FAILURE'}">
      <input type="button" id="approval_task_success" name="<spring:message code="ui.accounts.all.pending.changes.approve"/>" value="<spring:message code="ui.accounts.all.pending.changes.approve"/>"
        class="widgetwizard_nextprevpanel submitbutton">
      <input type="button" id="approval_task_failure" name="<spring:message code="ui.accounts.all.pending.changes.reject"/>" value="<spring:message code="ui.accounts.all.pending.changes.reject"/>" 
        class="widgetwizard_nextprevpanel submitbutton">
    </c:if>
    <a class="cancel close_enable_service_wizard" href="javascript:void(0);" id="approval_task_close"><spring:message code="label.cancel"/></a>
  </div>
  </div>
		<c:choose>
            <c:when test="${task.businessTransaction.type == 'tenantStateChange' }">
            	<c:set var="initialState">
            		<spring:message code="tenant.state.${fn:toLowerCase(task.businessTransaction.tenantInitialState)}"/>
            	</c:set>
            	<c:set var="targetState">
            		<spring:message code="tenant.state.${fn:toLowerCase(task.businessTransaction.tenantTargetState)}"/>
            	</c:set>
	            <c:set var="transactionMessage">
					<spring:message
						code="ui.task.businesstransaction.${task.businessTransaction.type}"
						arguments="${initialState},${targetState},${task.tenant.name}"></spring:message>
				</c:set>
            </c:when>
            <c:when test="${task.businessTransaction.type == 'tenantAccountTypeConversion' }">
            	<c:set var="initialAT">
            		<spring:message code="page.level2.${fn:toLowerCase(task.businessTransaction.accountTypeInitial.name)}"/>
            	</c:set>
            	<c:set var="targetAT">
            		<spring:message code="page.level2.${fn:toLowerCase(task.businessTransaction.accountType.name)}"/>
            	</c:set>
				<c:set var="transactionMessage">
					<spring:message
						code="ui.task.businesstransaction.${task.businessTransaction.type}"
						arguments="${initialAT},${targetAT},${task.tenant.name}"></spring:message>
				</c:set>           
            </c:when>
            <c:when test="${task.businessTransaction.type == 'paymentInfoChange' }">
				<c:set var="transactionMessage">
					<spring:message
						code="ui.task.businesstransaction.${task.businessTransaction.type}"
						arguments="null,null,${task.tenant.name}"></spring:message>
				</c:set>
            </c:when>
            <c:when test="${task.businessTransaction.type == 'subscriptionActivation' }">
            	<c:set var="subscriptionInitialState">
            		<spring:message code="subscription.state.${fn:toLowerCase(task.businessTransaction.subscriptionInitialState)}"/>
            	</c:set>
            	<c:set var="subscriptionTargetState">
            		<spring:message code="subscription.state.${fn:toLowerCase(task.businessTransaction.subscriptionTargetState)}"/>
            	</c:set>
            	<c:set var="transactionMessage">
					<spring:message code="ui.task.businesstransaction.${task.businessTransaction.type}"
						arguments="${subscriptionInitialState},${subscriptionTargetState},${task.businessTransaction.subscription.user.lastName}"></spring:message>
				</c:set>
            </c:when>
            <c:when test="${task.businessTransaction.type == 'cloudServiceActivation' }">
            	<c:set var="transactionMessage">
					<spring:message code="ui.task.businesstransaction.${task.businessTransaction.type}"
						arguments="null,null,${task.businessTransaction.serviceInstance.name}"></spring:message>
				</c:set>
            </c:when>
            <c:otherwise>
            	<c:set var="transactionMessage">
					<spring:message code="ui.task.businesstransaction.${task.businessTransaction.type}"
						arguments="null,null,null"></spring:message>
				</c:set>
            </c:otherwise>
		</c:choose>   
 <div class="j_success" style="display:none">
	 <div class="widgetwizard_contentarea sixstepswizard">
	    <div class="widgetwizard_boxes fullheight sixstepswizard">
	        <c:set var="taskMessage">
					<spring:message code="ui.widget.task.type.${task.type}.name"/>
			</c:set>
			<div class="widgetwizard_successbox tasks">
				<div class="widget_resulticon success"></div>
				<p class="hidden j_approved"><spring:message code="ui.widget.task.approved" arguments="${taskMessage},${transactionMessage}" htmlEscape="false"/></p>
				<p class="hidden j_rejected"><spring:message code="ui.widget.task.rejected" arguments="${taskMessage},${transactionMessage}" htmlEscape="false"/></p>
			</div>      
	    </div>
		</div>
	  <div id="okbuttons" class="widgetwizard_nextprevpanel sixstepswizard">
	    <input type="button" id="okbutton" name="<spring:message code="label.ok"/>" value="<spring:message code="label.ok"/>"  class="widgetwizard_nextprevpanel submitbutton">
	  </div>
  </div> 
</div>
